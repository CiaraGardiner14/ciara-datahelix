package com.scottlogic.deg.generator.generation;

import com.google.inject.Inject;
import com.scottlogic.deg.generator.DataBagValueSource;
import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.config.detail.DataGenerationType;
import com.scottlogic.deg.generator.generation.databags.DataBag;
import com.scottlogic.deg.generator.generation.databags.DataBagValue;
import com.scottlogic.deg.generator.generation.fieldvaluesources.CombiningFieldValueSource;
import com.scottlogic.deg.generator.generation.fieldvaluesources.FieldValueSource;
import com.scottlogic.deg.generator.utils.JavaUtilRandomNumberGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FieldSpecValueGenerator {
    private final DataGenerationType dataType;
    private final FieldValueSourceEvaluator sourceFactory;
    private final JavaUtilRandomNumberGenerator randomNumberGenerator;

    @Inject
    public FieldSpecValueGenerator(DataGenerationType dataGenerationType, FieldValueSourceEvaluator sourceEvaluator, JavaUtilRandomNumberGenerator randomNumberGenerator) {
        this.dataType = dataGenerationType;
        this.sourceFactory = sourceEvaluator;
        this.randomNumberGenerator = randomNumberGenerator;
    }

    public Stream<DataBag> generate(Field field, Set<FieldSpec> specs) {
        List<FieldValueSource> fieldValueSources = specs.stream()
            .map(sourceFactory::getFieldValueSources)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());

        return createValuesFromSources(field, specs.stream().findFirst().orElse(FieldSpec.Empty), fieldValueSources);
    }

    public Stream<DataBag> generate(Field field, FieldSpec spec) {
        List<FieldValueSource> fieldValueSources = sourceFactory.getFieldValueSources(spec);

        return createValuesFromSources(field, spec, fieldValueSources);
    }

    @NotNull
    private Stream<DataBag> createValuesFromSources(Field field, FieldSpec spec, List<FieldValueSource> fieldValueSources) {
        FieldValueSource combinedFieldValueSource = new CombiningFieldValueSource(fieldValueSources);

        Iterable<Object> iterable =  getDataValues(combinedFieldValueSource);

        return StreamSupport.stream(iterable.spliterator(), false)
            .map(value -> {
                DataBagValue dataBagValue = new DataBagValue(
                    value,
                    spec.getFormatRestrictions() != null
                        ? spec.getFormatRestrictions().formatString
                        : null,
                    new DataBagValueSource(spec.getFieldSpecSource()));

                return DataBag.startBuilding()
                    .set(
                        field,
                        dataBagValue)
                    .build();
            });
    }

    private Iterable<Object> getDataValues(FieldValueSource source) {
        switch (dataType) {
            case FULL_SEQUENTIAL:
            default:
                return source.generateAllValues();
            case INTERESTING:
                return source.generateInterestingValues();
            case RANDOM:
                return source.generateRandomValues(randomNumberGenerator);
        }
    }
}

