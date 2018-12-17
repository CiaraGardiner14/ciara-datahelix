package com.scottlogic.deg.generator;

import com.scottlogic.deg.generator.analysis.FieldDependencyAnalyser;
import com.scottlogic.deg.generator.decisiontree.MostProlificConstraintOptimiser;
import com.scottlogic.deg.generator.decisiontree.NoopDecisionTreeOptimiser;
import com.scottlogic.deg.generator.decisiontree.ProfileDecisionTreeFactory;
import com.scottlogic.deg.generator.decisiontree.tree_partitioning.NoopTreePartitioner;
import com.scottlogic.deg.generator.decisiontree.tree_partitioning.RelatedFieldTreePartitioner;
import com.scottlogic.deg.generator.generation.DataGeneratorMonitor;
import com.scottlogic.deg.generator.generation.DecisionTreeDataGenerator;
import com.scottlogic.deg.generator.generation.GenerationConfig;
import com.scottlogic.deg.generator.generation.NoopDataGeneratorMonitor;
import com.scottlogic.deg.generator.inputs.InvalidProfileException;
import com.scottlogic.deg.generator.inputs.ProfileReader;
import com.scottlogic.deg.generator.outputs.dataset_writers.CsvDataSetWriter;
import com.scottlogic.deg.generator.outputs.targets.FileOutputTarget;
import com.scottlogic.deg.generator.walker.reductive.field_selection_strategy.FixFieldStrategy;
import com.scottlogic.deg.generator.walker.reductive.field_selection_strategy.HierarchicalDependencyFixFieldStrategy;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@CommandLine.Command(
    name = "generate",
    description = "Generates data using a profile file.",
    mixinStandardHelpOptions = true,
    version = "1.0")
public class Generate implements Runnable {
    public static final String defaultTreeWalkerType = "cartesian_product";

    @CommandLine.Parameters(index = "0", description = "The path of the profile json file.")
    private File profileFile;

    @CommandLine.Parameters(index = "1", description = "The path to write the generated data file to.")
    private Path outputPath;

    @CommandLine.Option(names = {"-t", "--t"},
        description = "Determines the type of data generation performed (FULL_SEQUENTIAL, INTERESTING, RANDOM).",
        defaultValue = "INTERESTING")
    private GenerationConfig.DataGenerationType generationType;

    @CommandLine.Option(names = {"-c", "--c"},
        description = "Determines the type of combination strategy used (pinning, exhaustive, minimal).",
        defaultValue = "PINNING")
    private GenerationConfig.CombinationStrategyType combinationType = GenerationConfig.CombinationStrategyType.PINNING;

    @CommandLine.Option(
            names = {"--no-optimise"},
            description = "Prevents tree optimisation",
            hidden = true)
    private boolean dontOptimise;

    @CommandLine.Option(
            names = {"--no-partition"},
            description = "Prevents tree partitioning",
            hidden = true)
    private boolean dontPartitionTrees;

    @CommandLine.Option(names = {"-w", "--w"},
        description = "Determines the tree walker that should be used.",
        defaultValue = defaultTreeWalkerType,
        hidden = true)
    private GenerationConfig.TreeWalkerType walkerType;

    @Override
    public void run() {
        GenerationConfig config = new GenerationConfig(
            generationType,
            walkerType,
            combinationType);

        try {
            final Profile profile = new ProfileReader().read(profileFile.toPath());
            FixFieldStrategy fixFieldStrategy = new HierarchicalDependencyFixFieldStrategy(profile, new FieldDependencyAnalyser());
            DataGeneratorMonitor monitor = new NoopDataGeneratorMonitor();
            new GenerationEngine(
                new FileOutputTarget(outputPath, new CsvDataSetWriter()),
                new DecisionTreeDataGenerator(
                    config,
                    dontPartitionTrees
                        ? new NoopTreePartitioner()
                        : new RelatedFieldTreePartitioner(),
                    dontOptimise
                        ? new NoopDecisionTreeOptimiser()
                        : new MostProlificConstraintOptimiser(),
                    monitor,
                    new ProfileDecisionTreeFactory(),
                    fixFieldStrategy))
                .generateDataSet(profile, config);
        } catch (IOException | InvalidProfileException e) {
            e.printStackTrace();
        }
    }
}
