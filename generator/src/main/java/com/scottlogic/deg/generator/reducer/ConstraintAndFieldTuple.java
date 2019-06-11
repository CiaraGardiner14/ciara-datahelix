package com.scottlogic.deg.generator.reducer;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.constraints.atomic.AtomicConstraint;

class ConstraintAndFieldTuple {
    private final Field field;
    private final AtomicConstraint constraint;

    ConstraintAndFieldTuple(AtomicConstraint constraint, Field field) {
        this.constraint = constraint;
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public AtomicConstraint getConstraint() {
        return constraint;
    }
}
