package com.scottlogic.deg.generator.inputs.validation.validators;

import com.scottlogic.deg.generator.inputs.validation.*;
import com.scottlogic.deg.generator.inputs.validation.messages.*;
import com.scottlogic.deg.generator.restrictions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TemporalConstraintValidator implements ConstraintValidatorAlerts {

    public final ValidationType validationType = ValidationType.TEMPORAL;

    private List<ValidationAlert> alerts;
    private DateTimeRestrictions currentRestrictions;

    public TemporalConstraintValidator() {
        this.alerts = new ArrayList<>();
    }

    public void isAfter(String field, LocalDateTime referenceValue, boolean inclusive) {

        DateTimeRestrictions candidateRestrictions = new DateTimeRestrictions();
        candidateRestrictions.min = new DateTimeRestrictions.DateTimeLimit(
            referenceValue,
            inclusive);

        DateTimeRestrictionsMerger merger = new DateTimeRestrictionsMerger();

        MergeResult<DateTimeRestrictions> result = merger.merge(currentRestrictions, candidateRestrictions);

        if (result.successful) {
            currentRestrictions = result.restrictions;

            if (currentRestrictions.min != null
                && currentRestrictions.max != null
                && currentRestrictions.min.getLimit().compareTo(currentRestrictions.max.getLimit()) >0 ) {

                logInformation(field, new TemporalConstraintValidationMessages(
                    currentRestrictions.min == null ? null : currentRestrictions.min.getLimit(),
                    currentRestrictions.max == null ? null : currentRestrictions.max.getLimit(),
                    referenceValue));
            }
        } else {
            logError(field, new TemporalConstraintValidationMessages(
                currentRestrictions.min == null ? null : currentRestrictions.min.getLimit(),
                currentRestrictions.max == null ? null : currentRestrictions.max.getLimit(),
                referenceValue));
        }
    }

    public void isBefore(String field, LocalDateTime referenceValue, boolean inclusive) {

        DateTimeRestrictions candidateRestrictions = new DateTimeRestrictions();
        candidateRestrictions.max = new DateTimeRestrictions.DateTimeLimit(
            referenceValue,
            inclusive);

        DateTimeRestrictionsMerger merger = new DateTimeRestrictionsMerger();

        MergeResult<DateTimeRestrictions> result = merger.merge(currentRestrictions, candidateRestrictions);

        if (result.successful) {
            currentRestrictions = result.restrictions;

            if (currentRestrictions.min != null
                && currentRestrictions.max != null
                && currentRestrictions.min.getLimit().compareTo(currentRestrictions.max.getLimit()) >0 ) {

                logInformation(field, new TemporalConstraintValidationMessages(
                    currentRestrictions.min == null ? null : currentRestrictions.min.getLimit(),
                    currentRestrictions.max == null ? null : currentRestrictions.max.getLimit(),
                    referenceValue));
            }
        } else {
            logError(field, new TemporalConstraintValidationMessages(
                currentRestrictions.min == null ? null : currentRestrictions.min.getLimit(),
                currentRestrictions.max == null ? null : currentRestrictions.max.getLimit(),
                referenceValue));
        }
    }

    private void logError(String field, StandardValidationMessages message) {
        alerts.add(new ValidationAlert(
            Criticality.ERROR,
            message,
            validationType,
            field));
    }

    private void logInformation(String field, StandardValidationMessages message) {
        alerts.add(new ValidationAlert(
            Criticality.INFORMATION,
            message,
            validationType,
            field));
    }

    @Override
    public List<ValidationAlert> getAlerts() {
        return alerts;
    }
}
