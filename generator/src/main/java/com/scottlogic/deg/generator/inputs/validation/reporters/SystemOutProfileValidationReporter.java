package com.scottlogic.deg.generator.inputs.validation.reporters;

import com.scottlogic.deg.generator.inputs.validation.Criticality;
import com.scottlogic.deg.generator.inputs.validation.ValidationAlert;

import java.util.List;

public class SystemOutProfileValidationReporter implements  ProfileValidationReporter {

    @Override
    public void output(List<ValidationAlert> alerts) {
        if (alerts.size() == 0) return;

        boolean hasErrors = false;
        for (ValidationAlert alert : alerts) {

            if (alert.getCriticality().equals(Criticality.ERROR)) {
                hasErrors = true;
            }

            System.out.println(String.format("Field %s: %s during %s Validation: %s ",
                alert.getField().toString(),
                alert.getCriticality().toString(),
                alert.getValidationType().toString(),
                alert.getMessage().getVerboseMessage()));
        }

        if (hasErrors) {
            System.out.println("Encountered unrecoverable profile validation errors.");
            System.exit(1);
        }
    }
}
