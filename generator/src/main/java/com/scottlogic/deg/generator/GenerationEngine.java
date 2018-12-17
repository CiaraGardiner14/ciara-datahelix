package com.scottlogic.deg.generator;

import com.scottlogic.deg.generator.constraints.Constraint;
import com.scottlogic.deg.generator.constraints.grammatical.AndConstraint;
import com.scottlogic.deg.generator.constraints.grammatical.ViolateConstraint;
import com.scottlogic.deg.generator.generation.DataGenerator;
import com.scottlogic.deg.generator.generation.GenerationConfig;
import com.scottlogic.deg.generator.inputs.InvalidProfileException;
import com.scottlogic.deg.generator.outputs.GeneratedObject;
import com.scottlogic.deg.generator.outputs.TestCaseDataSet;
import com.scottlogic.deg.generator.outputs.TestCaseGenerationResult;
import com.scottlogic.deg.generator.outputs.targets.OutputTarget;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerationEngine {
    private final OutputTarget outputter;
    private final DataGenerator dataGenerator;

    public GenerationEngine(OutputTarget outputter, DataGenerator dataGenerator) {
        this.outputter = outputter;
        this.dataGenerator = dataGenerator;
    }

    public void generateDataSet(Profile profile, GenerationConfig config) throws IOException {
        final Stream<GeneratedObject> generatedDataItems = generate(profile, config);

        this.outputter.outputDataset(generatedDataItems, profile.fields);
    }

    public void generateTestCases(Profile profile, GenerationConfig config) throws IOException, InvalidProfileException {
        final TestCaseDataSet validCase = new TestCaseDataSet("", generate(profile, config));

        System.out.println("Valid cases generated, starting violation generation...");

        final List<TestCaseDataSet> violatingCases = profile.rules.stream()
            .map(rule ->
            {
                Collection<Rule> violatedRule = profile.rules.stream()
                    .map(r -> r == rule
                        ? violateRule(rule)
                        : r)
                    .collect(Collectors.toList());

                Profile violatingProfile = new Profile(profile.fields, violatedRule);

                return new TestCaseDataSet(
                    rule.description,
                    generate(
                        violatingProfile,
                        config));
            })
            .collect(Collectors.toList());


        final TestCaseGenerationResult generationResult = new TestCaseGenerationResult(
            profile,
            Stream.concat(
                Stream.of(validCase),
                violatingCases.stream())
                .collect(Collectors.toList()));

        this.outputter.outputTestCases(generationResult);
    }

    private Stream<GeneratedObject> generate(Profile profile, GenerationConfig config) {
        return this.dataGenerator.generateData(
            profile,
            config);
    }

    private Rule violateRule(Rule rule) {
        Constraint violateConstraint =
            rule.constraints.size() == 1
                ? new ViolateConstraint(
                rule.constraints.iterator().next())
                : new ViolateConstraint(
                new AndConstraint(
                    rule.constraints));

        return new Rule(rule.description, Collections.singleton(violateConstraint));
    }
}
