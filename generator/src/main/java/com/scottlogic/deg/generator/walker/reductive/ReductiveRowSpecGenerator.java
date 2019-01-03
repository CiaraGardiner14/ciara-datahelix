package com.scottlogic.deg.generator.walker.reductive;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.constraints.atomic.AtomicConstraint;
import com.scottlogic.deg.generator.decisiontree.ConstraintNode;
import com.scottlogic.deg.generator.generation.ReductiveDataGeneratorMonitor;
import com.scottlogic.deg.generator.reducer.ConstraintReducer;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.fieldspecs.FieldSpecMerger;
import com.scottlogic.deg.generator.fieldspecs.RowSpec;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReductiveRowSpecGenerator {

    private final ConstraintReducer constraintReducer;
    private final FieldSpecMerger fieldSpecMerger;
    private final ReductiveDataGeneratorMonitor monitor;

    public ReductiveRowSpecGenerator(
        ConstraintReducer constraintReducer,
        FieldSpecMerger fieldSpecMerger,
        ReductiveDataGeneratorMonitor monitor) {
        this.fieldSpecMerger = fieldSpecMerger;
        this.constraintReducer = constraintReducer;
        this.monitor = monitor;
    }

    //produce a stream of RowSpecs for each value in the permitted set of values for the field fixed on the last iteration
    public Stream<RowSpec> createRowSpecsFromFixedValues(ReductiveState reductiveState, ConstraintNode constraintNode) {
        //create a row spec where every field is set to this.fixedFields & field=value
        if (reductiveState.getLastFixedField() == null) {
            throw new IllegalStateException("Field has not been fixed yet");
        }

        Map<Field, FieldSpec> fieldSpecsPerField = getFieldSpecsForAllFixedFields(reductiveState, constraintNode);

        if (fieldSpecsPerField.values().stream().anyMatch(fieldSpec -> fieldSpec == FieldSpec.Empty)){
            return Stream.empty();
        }

        RowSpec rowSpecWithAllValues= new RowSpec(reductiveState.getFields(), fieldSpecsPerField);

        this.monitor.rowSpecEmitted(
            reductiveState.getLastFixedField(),
            fieldSpecsPerField.get(reductiveState.getLastFixedField().field),
            rowSpecWithAllValues);
        return Stream.of(rowSpecWithAllValues);
    }

    private Map<Field, FieldSpec> getFieldSpecsForAllFixedFields(ReductiveState reductiveState, ConstraintNode constraintNode){
        Map<Field, List<AtomicConstraint>> fieldToConstraints = constraintNode.getAtomicConstraints()
            .stream()
            .collect(Collectors.groupingBy(AtomicConstraint::getField));

        return Stream.concat(reductiveState.getFixedFieldsExceptLast().values().stream(), Stream.of(reductiveState.getLastFixedField()))
            .collect(Collectors.toMap(
                ff -> ff.field,
                ff -> {
                    FieldSpec fieldSpec = createFieldSpec(ff, fieldToConstraints.get(ff.field));
                    return fieldSpec == null
                        ? FieldSpec.Empty
                        : fieldSpec;
                }
            ));
    }

    //create a FieldSpec for a given FixedField and the atomic constraints we know about this field
    private FieldSpec createFieldSpec(FixedField fixedField, Collection<AtomicConstraint> constraintsForField) {
        FieldSpec fixedFieldSpec = fixedField.getFieldSpecForCurrentValue();
        Optional<FieldSpec> constrainedFieldSpecOpt = this.constraintReducer.reduceConstraintsToFieldSpec(constraintsForField);

        if (!constrainedFieldSpecOpt.isPresent()){
            return null; //this shouldn't happen: caused by constraints for one of the fixed fields contradicting each other (issue in optimising and/or reducing) - see issue #250
        }

        return this.fieldSpecMerger
            .merge(fixedFieldSpec, constrainedFieldSpecOpt.get())
            .orElseThrow(() -> new UnsupportedOperationException("Contradiction? - " + fixedField.toString() + "\n" + constraintsForField.toString()));
    }

}
