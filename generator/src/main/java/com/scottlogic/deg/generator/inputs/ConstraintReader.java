package com.scottlogic.deg.generator.inputs;

import com.scottlogic.deg.generator.DataTypeImports;
import com.scottlogic.deg.generator.ProfileFields;
import com.scottlogic.deg.generator.constraints.Constraint;
import com.scottlogic.deg.schemas.v0_1.ConstraintDTO;

import java.util.Set;

@FunctionalInterface
public interface ConstraintReader {
    Constraint apply(
        ConstraintDTO dto,
        ProfileFields fields,
        Set<RuleInformation> rules, DataTypeImports imports)
        throws InvalidProfileException;
}