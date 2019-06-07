package com.scottlogic.deg.generator.decisiontree.FieldSpecTree;

import com.google.inject.Inject;
import com.scottlogic.deg.common.ValidationException;
import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.Profile;
import com.scottlogic.deg.common.profile.constraints.Constraint;
import com.scottlogic.deg.common.profile.constraints.atomic.AtomicConstraint;
import com.scottlogic.deg.common.profile.constraints.grammatical.AndConstraint;
import com.scottlogic.deg.common.profile.constraints.grammatical.ConditionalConstraint;
import com.scottlogic.deg.common.profile.constraints.grammatical.NegatedGrammaticalConstraint;
import com.scottlogic.deg.common.profile.constraints.grammatical.OrConstraint;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.fieldspecs.RowSpecMerger;
import com.scottlogic.deg.generator.reducer.ConstraintReducer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProfileFSConstraintNodeFactory {
    private final ConstraintReducer constraintReducer;
    private final RowSpecMerger rowSpecMerger;

    @Inject
    public ProfileFSConstraintNodeFactory(ConstraintReducer constraintReducer, RowSpecMerger rowSpecMerger) {
        this.constraintReducer = constraintReducer;
        this.rowSpecMerger = rowSpecMerger;
    }

    public FSConstraintNode create(Profile profile){
        Set<Constraint> constraints = profile.getRules().stream()
            .flatMap(rule -> rule.constraints.stream())
            .collect(Collectors.toSet());

        return convertAndConstraint(new AndConstraint(constraints), new HashMap<>());
    }

    private FSConstraintNode convertAndConstraint(AndConstraint andConstraint, Map<Field, FieldSpec> parentFieldSpecs) {
        ConstraintNodeDetails constraintNodeDetails = getConstraintNodeDetails(andConstraint.subConstraints);

        Optional<Map<Field, FieldSpec>> localFieldSpecs = constraintReducer.reduceConstraintsToRowSpec(constraintNodeDetails.atomics);
        if (!localFieldSpecs.isPresent()){
            throw new ValidationException("AAAH, A CONTRADICTION, TODO DO HANDLE THIS");
        }

        Optional<Map<Field, FieldSpec>> combinedFieldSpecs = rowSpecMerger.merge(parentFieldSpecs, localFieldSpecs.get());

        if (!combinedFieldSpecs.isPresent()){
            throw new ValidationException("AAAH, A CONTRADICTION, TODO DO HANDLE THIS");
        }

        Collection<FSDecisionNode> decisions = new ArrayList<>();
        for (OrConstraint decision : constraintNodeDetails.decisions) {
            decisions.add(convertOrConstraint(decision, combinedFieldSpecs.get()));
        }

        return new FSConstraintNode(combinedFieldSpecs.get(), decisions);
    }

    private FSDecisionNode convertOrConstraint(OrConstraint decision, Map<Field, FieldSpec> fieldFieldSpecMap) {
       return new FSDecisionNode(
           decision.subConstraints.stream()
               .map(c -> convertAndConstraint(new AndConstraint(c), fieldFieldSpecMap)
               ).collect(Collectors.toList()));
    }

    private OrConstraint convertToOr(ConditionalConstraint conditionalConstraint){
        Constraint IF = conditionalConstraint.condition;
        Constraint THEN = conditionalConstraint.whenConditionIsTrue;
        Constraint ELSE = conditionalConstraint.whenConditionIsFalse;

        return new OrConstraint(
            new AndConstraint(IF, THEN),
            ELSE == null ? IF.negate() : new AndConstraint(IF.negate(), ELSE));
    }

    private ConstraintNodeDetails getConstraintNodeDetails(Collection<Constraint> subConstraints) {
        ConstraintNodeDetails acc = new ConstraintNodeDetails();

        for (Constraint constraint : subConstraints) {
            if (constraint instanceof AtomicConstraint) {
                acc.atomics.add((AtomicConstraint) constraint);
            }
            else if (constraint instanceof ConditionalConstraint){
                acc.decisions.add(convertToOr((ConditionalConstraint) constraint));
            }
            else if (constraint instanceof OrConstraint){
                acc.decisions.add((OrConstraint) constraint);
            }
            else if (constraint instanceof AndConstraint){
                ConstraintNodeDetails sub = getConstraintNodeDetails(((AndConstraint) constraint).subConstraints);
                acc.atomics.addAll(sub.atomics);
                acc.decisions.addAll(sub.decisions);
            }
            else if (constraint instanceof NegatedGrammaticalConstraint){
                ConstraintNodeDetails sub = getConstraintNodeDetails(Arrays.asList(
                    getNegatedConstraint(((NegatedGrammaticalConstraint) constraint).negatedConstraint)));
                acc.atomics.addAll(sub.atomics);
                acc.decisions.addAll(sub.decisions);
            }
            else {
                throw new ValidationException("unexpected constraint in profile");
            }
        }
        return acc;
    }

    private Constraint getNegatedConstraint(Constraint negatedConstraint) {
        // ¬AND(X, Y, Z) reduces to OR(¬X, ¬Y, ¬Z)
        if (negatedConstraint instanceof AndConstraint) {
            Collection<Constraint> subConstraints = ((AndConstraint) negatedConstraint).subConstraints;

            return new OrConstraint(negateEach(subConstraints));
        }
        // ¬OR(X, Y, Z) reduces to AND(¬X, ¬Y, ¬Z)
        else if (negatedConstraint instanceof OrConstraint) {
            Collection<Constraint> subConstraints = ((OrConstraint) negatedConstraint).subConstraints;

            return new AndConstraint(negateEach(subConstraints));
        }
        if (negatedConstraint instanceof ConditionalConstraint){
            return getNegatedConstraint(convertToOr((ConditionalConstraint) negatedConstraint));
        }
        if (negatedConstraint instanceof NegatedGrammaticalConstraint){
            return ((NegatedGrammaticalConstraint) negatedConstraint).negatedConstraint;
        }
        if (negatedConstraint instanceof AtomicConstraint) {
            return negatedConstraint.negate();
        }
        throw new ValidationException("unexpected constraint in profile");
    }

    private static Collection<Constraint> negateEach(Collection<Constraint> constraints) {
        return constraints.stream()
            .map(Constraint::negate)
            .collect(Collectors.toList());
    }

    private class ConstraintNodeDetails {
        Set<AtomicConstraint> atomics = new HashSet<>();
        Set<OrConstraint> decisions = new HashSet<>();
    }
}
