package com.scottlogic.deg.common.profile.constraints.atomic;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.RuleInformation;


import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;

public class IsAfterOrEqualToConstantDateTimeConstraint implements AtomicConstraint {
    public final Field field;
    public final OffsetDateTime referenceValue;
    private final Set<RuleInformation> rules;

    public IsAfterOrEqualToConstantDateTimeConstraint(Field field, OffsetDateTime referenceValue, Set<RuleInformation> rules) {
        this.field = field;
        this.referenceValue = referenceValue;
        this.rules= rules;
    }

    @Override
    public String toDotLabel(){
        return String.format("%s >= %s", field.name, referenceValue);
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o instanceof ViolatedAtomicConstraint) {
            return o.equals(this);
        }
        if (o == null || getClass() != o.getClass()) return false;
        IsAfterOrEqualToConstantDateTimeConstraint constraint = (IsAfterOrEqualToConstantDateTimeConstraint) o;
        return Objects.equals(field, constraint.field) && Objects.equals(referenceValue, constraint.referenceValue);
    }

    @Override
    public int hashCode(){
        return Objects.hash(field, referenceValue);
    }

    @Override
    public String toString(){
        return String.format("`%s` >= %s", field.name, referenceValue);
    }


    @Override
    public Set<RuleInformation> getRules() {
        return rules;
    }

    @Override
    public AtomicConstraint withRules(Set<RuleInformation> rules) {
        return new IsAfterOrEqualToConstantDateTimeConstraint(this.field, this.referenceValue, rules);
    }
}
