package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.generator.restrictions.*;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Details a column's atomic constraints
 */
public class FieldSpec {
    public static final FieldSpec Empty = new FieldSpec(null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        FieldSpecSource.Empty);

    private final SetRestrictions setRestrictions;
    private final NumericRestrictions numericRestrictions;
    private final StringRestrictions stringRestrictions;
    private final NullRestrictions nullRestrictions;
    private final TypeRestrictions typeRestrictions;
    private final DateTimeRestrictions dateTimeRestrictions;
    private final FormatRestrictions formatRestrictions;
    private final GranularityRestrictions granularityRestrictions;
    private final FieldSpecSource source;

    public FieldSpec(
        SetRestrictions setRestrictions,
        NumericRestrictions numericRestrictions,
        StringRestrictions stringRestrictions,
        NullRestrictions nullRestrictions,
        TypeRestrictions typeRestrictions,
        DateTimeRestrictions dateTimeRestrictions,
        FormatRestrictions formatRestrictions,
        GranularityRestrictions granularityRestrictions,
        FieldSpecSource source) {
        this.setRestrictions = setRestrictions;
        this.numericRestrictions = numericRestrictions;
        this.stringRestrictions = stringRestrictions;
        this.nullRestrictions = nullRestrictions;
        this.typeRestrictions = typeRestrictions;
        this.dateTimeRestrictions = dateTimeRestrictions;
        this.formatRestrictions = formatRestrictions;
        this.granularityRestrictions = granularityRestrictions;
        this.source = source;
    }

    public SetRestrictions getSetRestrictions() {
        return setRestrictions;
    }

    public NumericRestrictions getNumericRestrictions() {
        return numericRestrictions;
    }

    public StringRestrictions getStringRestrictions() {
        return stringRestrictions;
    }

    public NullRestrictions getNullRestrictions() {
        return nullRestrictions;
    }

    public TypeRestrictions getTypeRestrictions() {
        return typeRestrictions;
    }

    public DateTimeRestrictions getDateTimeRestrictions() { return dateTimeRestrictions; }

    public GranularityRestrictions getGranularityRestrictions() { return granularityRestrictions; }

    public FieldSpec withSetRestrictions(SetRestrictions setRestrictions, FieldSpecSource source) {
        return new FieldSpec(
            setRestrictions,
            this.numericRestrictions,
            this.stringRestrictions,
            this.nullRestrictions,
            this.typeRestrictions,
            this.dateTimeRestrictions,
            this.formatRestrictions,
            this.granularityRestrictions,
            this.source.combine(source));
    }

    public FieldSpec withNumericRestrictions(NumericRestrictions numericRestrictions, FieldSpecSource source) {
        return new FieldSpec(
            this.setRestrictions,
            numericRestrictions,
            this.stringRestrictions,
            this.nullRestrictions,
            this.typeRestrictions,
            this.dateTimeRestrictions,
            this.formatRestrictions,
            this.granularityRestrictions,
            this.source.combine(source));
    }

    public FieldSpec withStringRestrictions(StringRestrictions stringRestrictions, FieldSpecSource source) {
        return new FieldSpec(
            this.setRestrictions,
            this.numericRestrictions,
            stringRestrictions,
            this.nullRestrictions,
            this.typeRestrictions,
            this.dateTimeRestrictions,
            this.formatRestrictions,
            this.granularityRestrictions,
            this.source.combine(source));
    }

    public FieldSpec withTypeRestrictions(TypeRestrictions typeRestrictions, FieldSpecSource source) {
        return new FieldSpec(
            this.setRestrictions,
            this.numericRestrictions,
            this.stringRestrictions,
            this.nullRestrictions,
            typeRestrictions,
            this.dateTimeRestrictions,
            this.formatRestrictions,
            this.granularityRestrictions,
            this.source.combine(source));
    }

    public FieldSpec withNullRestrictions(NullRestrictions nullRestrictions, FieldSpecSource source) {
        return new FieldSpec(
            this.setRestrictions,
            this.numericRestrictions,
            this.stringRestrictions,
            nullRestrictions,
            this.typeRestrictions,
            this.dateTimeRestrictions,
            this.formatRestrictions,
            this.granularityRestrictions,
            this.source.combine(source));
    }

    public FieldSpec withDateTimeRestrictions(DateTimeRestrictions dateTimeRestrictions, FieldSpecSource source) {
        return new FieldSpec(
            this.setRestrictions,
            this.numericRestrictions,
            this.stringRestrictions,
            this.nullRestrictions,
            this.typeRestrictions,
            dateTimeRestrictions,
            this.formatRestrictions,
            this.granularityRestrictions,
            this.source.combine(source));
    }

    public FieldSpec withGranularityRestrictions(GranularityRestrictions granularityRestrictions, FieldSpecSource source) {
        return new FieldSpec(
            this.setRestrictions,
            this.numericRestrictions,
            this.stringRestrictions,
            this.nullRestrictions,
            this.typeRestrictions,
            this.dateTimeRestrictions,
            this.formatRestrictions,
            granularityRestrictions,
            this.source.combine(source));
    }

    @Override
    public String toString() {
        return String.join(
            " & ",
            Stream.of(
                Objects.toString(setRestrictions, null),
                Objects.toString(numericRestrictions, null),
                Objects.toString(stringRestrictions, null),
                Objects.toString(nullRestrictions, null),
                Objects.toString(typeRestrictions, null),
                Objects.toString(dateTimeRestrictions, null),
                Objects.toString(granularityRestrictions, null))
            .filter(s -> s != null)
            .collect(Collectors.toList()));
    }

    public FormatRestrictions getFormatRestrictions() {
        return formatRestrictions;
    }

    public FieldSpec withFormatRestrictions(FormatRestrictions formatRestrictions, FieldSpecSource source) {
        return new FieldSpec(
            this.setRestrictions,
            this.numericRestrictions,
            this.stringRestrictions,
            this.nullRestrictions,
            this.typeRestrictions,
            this.dateTimeRestrictions,
            formatRestrictions,
            this.granularityRestrictions,
            this.source.combine(source));
    }

    public FieldSpecSource getFieldSpecSource() {
        return this.source;
    }
}

