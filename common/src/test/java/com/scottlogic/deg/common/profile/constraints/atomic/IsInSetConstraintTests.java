package com.scottlogic.deg.common.profile.constraints.atomic;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.RuleInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

public class IsInSetConstraintTests {

    @Test
    public void testConstraintThrowsIfGivenEmptySet(){
        Field field1 = new Field("TestField");

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new IsInSetConstraint(field1, Collections.emptySet(), rules()));
    }

    @Test
    public void testConstraintThrowsIfGivenNullInASet(){
        Field field1 = new Field("TestField");

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new IsInSetConstraint(field1, Collections.singleton(null), rules()));
    }

    @Test
    public void testConstraintThrowsNothingIfGivenAValidSet(){
        Field field1 = new Field("TestField");
        Assertions.assertDoesNotThrow(
            () -> new IsInSetConstraint(field1, Collections.singleton("foo"), rules()));
    }

    private static Set<RuleInformation> rules(){
        return Collections.singleton(new RuleInformation());
    }
}
