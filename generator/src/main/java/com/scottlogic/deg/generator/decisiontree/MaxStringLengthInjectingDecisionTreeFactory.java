package com.scottlogic.deg.generator.decisiontree;

import com.google.inject.Inject;
import com.scottlogic.deg.common.profile.Profile;
import com.scottlogic.deg.common.profile.Rule;
import com.scottlogic.deg.common.profile.constraints.Constraint;
import com.scottlogic.deg.common.profile.constraints.atomic.AtomicConstraint;
import com.scottlogic.deg.common.profile.constraints.atomic.IsStringShorterThanConstraint;
import com.scottlogic.deg.common.profile.constraints.grammatical.AndConstraint;
import com.scottlogic.deg.common.util.Defaults;
import com.scottlogic.deg.common.profile.RuleInformation;
import com.scottlogic.deg.generator.decisiontree.FieldSpecTree.ProfileFSConstraintNodeFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Decorator over a DecisionTreeFactory to inject a &lt;shorterThan X&gt; constraint at the root node for every field
 */
public class MaxStringLengthInjectingDecisionTreeFactory implements DecisionTreeFactory{
    private final int maxLength;
    private final DecisionTreeFactory underlyingFactory;

    @Inject
    public MaxStringLengthInjectingDecisionTreeFactory(ProfileFSConstraintNodeFactory underlyingFactory) {
        this(underlyingFactory, Defaults.MAX_STRING_LENGTH);
    }

    public MaxStringLengthInjectingDecisionTreeFactory(DecisionTreeFactory underlyingFactory, int maxLength) {
        this.underlyingFactory = underlyingFactory;
        this.maxLength = maxLength;
    }

    @Override
    public DecisionTree create(Profile profile) {
        List<Constraint> shorterThan = profile.getFields()
            .stream()
            .map(field -> new IsStringShorterThanConstraint(field, maxLength + 1, Collections.emptySet()))
            .collect(Collectors.toList());

        ArrayList<Rule> newRules = new ArrayList<>(Arrays.asList(new Rule(new RuleInformation("max Lengths"), shorterThan)));
        newRules.addAll(profile.getRules());

        return underlyingFactory.create(new Profile(profile.getFields(), newRules));
    }
}
