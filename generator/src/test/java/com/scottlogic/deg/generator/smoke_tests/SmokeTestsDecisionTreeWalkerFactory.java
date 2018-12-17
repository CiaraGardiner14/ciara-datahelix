package com.scottlogic.deg.generator.smoke_tests;

import com.scottlogic.deg.generator.reducer.ConstraintReducer;
import com.scottlogic.deg.generator.restrictions.FieldSpecFactory;
import com.scottlogic.deg.generator.restrictions.FieldSpecMerger;
import com.scottlogic.deg.generator.restrictions.RowSpecMerger;
import com.scottlogic.deg.generator.walker.CartesianProductDecisionTreeWalker;
import com.scottlogic.deg.generator.walker.DecisionTreeWalker;
import com.scottlogic.deg.generator.walker.DecisionTreeWalkerFactory;

public class SmokeTestsDecisionTreeWalkerFactory implements DecisionTreeWalkerFactory {

    @Override
    public DecisionTreeWalker getDecisionTreeWalker() {
        FieldSpecMerger fieldSpecMerger = new FieldSpecMerger();
        RowSpecMerger rowSpecMerger = new RowSpecMerger(fieldSpecMerger);
        ConstraintReducer constraintReducer = new ConstraintReducer(
            new FieldSpecFactory(),
            fieldSpecMerger);

        return new CartesianProductDecisionTreeWalker(
            constraintReducer,
            rowSpecMerger);
    }
}
