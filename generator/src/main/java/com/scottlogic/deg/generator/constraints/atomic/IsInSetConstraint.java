package com.scottlogic.deg.generator.constraints.atomic;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.inputs.validation.ProfileVisitor;
import com.scottlogic.deg.generator.inputs.validation.VisitableProfileElement;
import com.scottlogic.deg.generator.inputs.RuleInformation;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class IsInSetConstraint implements AtomicConstraint, VisitableProfileElement {
    public final Field field;
    public final Set<Object> legalValues;
    private final Set<RuleInformation> rules;

    public IsInSetConstraint(Field field, Set<Object> legalValues, Set<RuleInformation> rules) {
        this.field = field;
        this.legalValues = legalValues;
        this.rules = rules;

        if (legalValues.isEmpty()) {
            throw new IllegalArgumentException("Cannot create an IsInSetConstraint for field '" +
                field.name + "' with an empty set.");
        }
    }

    @Override
    public String toDotLabel() {
        final int limit = 3;

        if (legalValues.size() <= limit) {
            return String.format("%s in [%s]", field.name,
                legalValues.stream().map(x -> x.toString()).collect(Collectors.joining(", ")));
        }


        return String.format("%s in [%s, ...](%d values)",
            field.name,
            legalValues.stream().limit(limit).map(x -> x.toString()).collect(Collectors.joining(", ")),
            legalValues.size());
    }

    @Override
    public Field getField() {
        return field;
    }

    public String toString(){
        return String.format(
                "`%s` in %s",
                field.name,
                Objects.toString(legalValues));
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o instanceof ViolatedAtomicConstraint) {
            return o.equals(this);
        }
        if (o == null || getClass() != o.getClass()) return false;
        IsInSetConstraint constraint = (IsInSetConstraint) o;
        return Objects.equals(field, constraint.field) && Objects.equals(legalValues, constraint.legalValues);
    }

    @Override
    public int hashCode(){
        return Objects.hash(field, legalValues);
    }

    @Override
    public void accept(ProfileVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Set<RuleInformation> getRules() {
        return rules;
    }

    @Override
    public AtomicConstraint withRules(Set<RuleInformation> rules) {
        return new IsInSetConstraint(this.field, this.legalValues, rules);
    }
}
