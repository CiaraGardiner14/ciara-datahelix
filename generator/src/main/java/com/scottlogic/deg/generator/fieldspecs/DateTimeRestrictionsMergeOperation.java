package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.generator.constraints.atomic.IsOfTypeConstraint;
import com.scottlogic.deg.generator.restrictions.*;

import java.util.Optional;

public class DateTimeRestrictionsMergeOperation implements RestrictionMergeOperation {
    private static final DateTimeRestrictionsMerger dateTimeRestrictionsMerger = new DateTimeRestrictionsMerger();

    @Override
    public Optional<FieldSpec> applyMergeOperation(FieldSpec left, FieldSpec right, FieldSpec merged) {
        DateTimeRestrictions dateTimeRestrictions = dateTimeRestrictionsMerger.merge(
            left.getDateTimeRestrictions(), right.getDateTimeRestrictions());

        if (dateTimeRestrictions == null) {
            return Optional.of(merged.withDateTimeRestrictions(
                null,
                FieldSpecSource.Empty));
        }

        TypeRestrictions typeRestrictions = merged.getTypeRestrictions();
        if (!typeRestrictions.isTypeAllowed(IsOfTypeConstraint.Types.TEMPORAL)) {
            return Optional.empty();
        }

        return Optional.of(merged
            .withDateTimeRestrictions(
                dateTimeRestrictions,
                FieldSpecSource.fromFieldSpecs(left, right))
            .withTypeRestrictions(
                DataTypeRestrictions.createFromWhiteList(IsOfTypeConstraint.Types.TEMPORAL),
                FieldSpecSource.fromFieldSpecs(left, right)));
    }
}
