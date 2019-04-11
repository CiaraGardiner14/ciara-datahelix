package com.scottlogic.deg.generator.restrictions;

import com.scottlogic.deg.generator.constraints.atomic.StandardConstraintTypes;
import com.scottlogic.deg.generator.generation.IsinStringGenerator;
import com.scottlogic.deg.generator.generation.StringGenerator;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.nullValue;

class StringRestrictionsTests {
    @Test
    void createGenerator_firstCall_shouldCreateAGenerator() {
        StringRestrictions restrictions = ofLength(10, false, false);

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator, not(nullValue()));
    }

    @Test
    void createGenerator_secondCall_shouldReturnSameGenerator() {
        StringRestrictions restrictions = ofLength(10, false, false);
        StringGenerator firstGenerator = restrictions.createGenerator();

        StringGenerator secondGenerator = restrictions.createGenerator();

        Assert.assertThat(secondGenerator, sameInstance(firstGenerator));
    }

    @Test
    void createGenerator_withMaxLengthConstraint_shouldCreateStringsToMaxLength() {
        StringRestrictions restrictions = maxLength(9, false);

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{0,9}$/"));
    }

    @Test
    void createGenerator_withMinLengthConstraint_shouldCreateStringsFromMinLengthToDefaultLength() {
        StringRestrictions restrictions = minLength(11, false)
            .intersect(maxLength(255, true));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{11,255}$/"));
    }

    @Test
    void createGenerator_withOfLengthConstraint_shouldCreateStringsOfLength() {
        StringRestrictions restrictions = ofLength(10, false, false);

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{10}$/"));
    }

    @Test
    void createGenerator_withMinAndNonContradictingMaxLengthConstraint_shouldCreateStringsBetweenLengths() {
        StringRestrictions restrictions =
            minLength(6, false)
                .intersect(maxLength(9, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{6,9}$/"));
    }

    @Test
    void createGenerator_withMinAndContradictingMaxLengthConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions =
            minLength(11, false)
                .intersect(maxLength(4, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withMinAndNonContradictingOfLengthConstraint_shouldCreateStringsOfLength() {
        StringRestrictions restrictions =
            minLength(5, false)
                .intersect(ofLength(10, false, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{10}$/"));
    }

    @Test
    void createGenerator_withMinAndContradictingOfLengthConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions =
            minLength(10, false)
                .intersect(ofLength(5, false, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withMaxAndNonContradictingOfLengthConstraint_shouldCreateStringsOfLength() {
        StringRestrictions restrictions =
            maxLength(10, false)
                .intersect(ofLength(5, false, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{5}$/"));
    }

    @Test
    void createGenerator_withMaxAndContradictingOfLengthConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions =
            maxLength(5, false)
                .intersect(ofLength(10, false, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withMinMaxAndNonContradictingOfLengthConstraint_shouldCreateStringsOfLength() {
        StringRestrictions restrictions =
            minLength(5, false)
                .intersect(maxLength(10, false))
                .intersect(ofLength(7, false, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{7}$/"));
    }

    @Test
    void createGenerator_with2MinLengthConstraints_shouldCreateStringsOfLongerThatGreatestMin() {
        StringRestrictions restrictions =
            minLength(5, false)
                .intersect(minLength(11, false))
                .intersect(maxLength(255, true));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{11,255}$/"));
    }

    @Test
    void createGenerator_with2MaxLengthConstraints_shouldCreateStringsOfShortestThatLowestMax() {
        StringRestrictions restrictions =
            maxLength(4, false)
                .intersect(maxLength(10, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{0,4}$/"));
    }

    @Test
    void createGenerator_with2OfLengthConstraints_shouldCreateNoStrings() {
        StringRestrictions restrictions =
            ofLength(5, false, false)
                .intersect(ofLength(10, false, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withOnlyAMatchingRegexConstraint_shouldCreateStringsMatchingRegex() {
        StringRestrictions restrictions = matchingRegex("[a-z]{0,9}", false)
            .intersect(maxLength(255, true));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{0,255}$/ ∩ /[a-z]{0,9}/"));
    }

    @Test
    void createGenerator_withNonContradictingMinLengthAndMatchingRegexConstraint_shouldCreateStringsMatchingRegexAndLongerThanMinLength() {
        StringRestrictions restrictions = matchingRegex("[a-z]{0,9}", false)
            .intersect(minLength(6, false))
            .intersect(maxLength(255, true));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{6,255}$/ ∩ /[a-z]{0,9}/"));
    }

    @Test
    void createGenerator_withContradictingMinLengthAndMatchingRegexConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions = matchingRegex("[a-z]{0,9}", false)
            .intersect(minLength(100, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withNonContradictingMaxLengthAndMatchingRegexConstraint_shouldCreateStringsMatchingRegexAndShorterThanMinLength() {
        StringRestrictions restrictions = matchingRegex("[a-z]{0,9}", false)
            .intersect(maxLength(4, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{0,4}$/ ∩ /[a-z]{0,9}/"));
    }

    @Test
    void createGenerator_withContradictingMaxLengthAndMatchingRegexConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions = matchingRegex("[a-z]{5,9}", false)
            .intersect(maxLength(2, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withNonContradictingOfLengthAndMatchingRegexConstraint_shouldCreateStringsMatchingRegexAndOfPrescribedLength() {
        StringRestrictions restrictions = matchingRegex("[a-z]{0,9}", false)
            .intersect(ofLength(5, false, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{5}$/ ∩ /[a-z]{0,9}/"));
    }

    @Test
    void createGenerator_withContradictingOfLengthAndMatchingRegexConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions = matchingRegex("[a-z]{0,9}", false)
            .intersect(ofLength(100, false, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withMinAndMaxLengthAndMatchingRegexConstraint_shouldCreateStringsMatchingRegexAndBetweenLengths() {
        StringRestrictions restrictions = matchingRegex("[a-z]{0,9}", false)
            .intersect(minLength(3, false))
            .intersect(maxLength(7, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{3,7}$/ ∩ /[a-z]{0,9}/"));
    }

    @Test
    void createGenerator_withOnlyAContainingRegexConstraint_shouldCreateStringsContainingRegex() {
        StringRestrictions restrictions = containsRegex("[a-z]{0,9}", false)
            .intersect(maxLength(255, true));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{0,255}$/ ∩ */[a-z]{0,9}/*"));
    }

    @Test
    void createGenerator_withNonContradictingMinLengthAndContainingRegexConstraint_shouldCreateStringsContainingRegexAndLongerThanMinLength() {
        StringRestrictions restrictions = containsRegex("[a-z]{0,9}", false)
            .intersect(minLength(6, false))
            .intersect(maxLength(255, true));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{6,255}$/ ∩ */[a-z]{0,9}/*"));
    }

    @Test
    void createGenerator_withContradictingMinLengthAndContainingRegexConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions = containsRegex("[a-z]{0,9}", false)
            .intersect(minLength(100, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withNonContradictingMaxLengthAndContainingRegexConstraint_shouldCreateStringsContainingRegexAndShorterThanMinLength() {
        StringRestrictions restrictions = containsRegex("[a-z]{0,9}", false)
            .intersect(maxLength(4, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{0,4}$/ ∩ */[a-z]{0,9}/*"));
    }

    @Test
    void createGenerator_withContradictingMaxLengthAndContainingRegexConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions = containsRegex("[a-z]{5,9}", false)
            .intersect(maxLength(2, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withNonContradictingOfLengthAndContainingRegexConstraint_shouldCreateStringsContainingRegexAndOfPrescribedLength() {
        StringRestrictions restrictions = containsRegex("[a-z]{0,9}", false)
            .intersect(ofLength(5, false, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{5}$/ ∩ */[a-z]{0,9}/*"));
    }

    @Test
    void createGenerator_withContradictingOfLengthAndContainingRegexConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions = containsRegex("[a-z]{0,9}", false)
            .intersect(ofLength(100, false, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withMinAndMaxLengthAndContainingRegexConstraint_shouldCreateStringsContainingRegexAndBetweenLengths() {
        StringRestrictions restrictions = containsRegex("[a-z]{0,9}", false)
            .intersect(minLength(3, false))
            .intersect(maxLength(7, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{3,7}$/ ∩ */[a-z]{0,9}/*"));
    }

    @Test
    void createGenerator_withOnlyAMatchingStandardConstraint_shouldCreateSomeStrings() {
        StringRestrictions restrictions = aValid(StandardConstraintTypes.ISIN, false);

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator, instanceOf(IsinStringGenerator.class));
    }

    @Test
    void createGenerator_withMinLengthAndMatchingStandardConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions = aValid(StandardConstraintTypes.ISIN, false)
            .intersect(minLength(1, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withMaxLengthAndMatchingStandardConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions = aValid(StandardConstraintTypes.ISIN, false)
            .intersect(maxLength(100, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withOfLengthAndMatchingStandardConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions = aValid(StandardConstraintTypes.ISIN, false)
            .intersect(ofLength(12, false, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withMatchingRegexAndMatchingStandardConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions = aValid(StandardConstraintTypes.ISIN, false)
            .intersect(matchingRegex("[a-zA-Z0-9]{12}", false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withContainingRegexAndMatchingStandardConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions = aValid(StandardConstraintTypes.ISIN, false)
            .intersect(containsRegex("[a-zA-Z0-9]{12}", false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withNegatedMaxLengthConstraint_shouldCreateStringsFromLength() {
        StringRestrictions restrictions = minLength(10, false)
            .intersect(maxLength(255, true));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{10,255}$/"));
    }

    @Test
    void createGenerator_withNegatedMinLengthConstraint_shouldCreateStringsUpToLength() {
        StringRestrictions restrictions = maxLength(10, false);

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{0,10}$/"));
    }

    @Test
    void createGenerator_withNegatedOfLengthConstraint_shouldCreateStringsShorterThanAndLongerThanExcludedLength() {
        StringRestrictions restrictions = ofLength(10, true, false)
            .intersect(maxLength(255, true));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^(.{0,9}|.{11,255})$/"));
    }

    @Test
    void createGenerator_withNegatedMinAndNonContradictingMaxLengthConstraint_shouldCreateStringsBetweenLengths() {
        StringRestrictions restrictions =
            maxLength(5, false)
                .intersect(maxLength(10, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{0,5}$/"));
    }

    @Test
    void createGenerator_withNegatedMinAndContradictingMaxLengthConstraint_shouldCreateShorterThanLowestLength() {
        StringRestrictions restrictions =
            maxLength(10, false)
                .intersect(maxLength(4, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{0,4}$/"));
    }

    @Test
    void createGenerator_withNegatedMinAndNonContradictingOfLengthConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions =
            maxLength(5, false)
                .intersect(ofLength(10, false, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withNegatedMinAndContradictingOfLengthConstraint_shouldCreateStringsOfLength() {
        StringRestrictions restrictions =
            maxLength(10, false)
                .intersect(ofLength(5, false, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{5}$/"));
    }

    @Test
    void createGenerator_withNegatedMaxAndNonContradictingOfLengthConstraint_shouldCreateNoStrings() {
        StringRestrictions restrictions =
            minLength(10, false)
                .intersect(ofLength(5, false, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withNegatedMaxAndContradictingOfLengthConstraint_shouldCreateStringsShorterThanMaximumLength() {
        StringRestrictions restrictions =
            maxLength(4, false)
                .intersect(ofLength(10, true, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{0,4}$/"));
    }

    @Test
    void createGenerator_withNegatedMinMaxAndNonContradictingOfLengthConstraint_should() {
        StringRestrictions restrictions =
            maxLength(5, false)
                .intersect(minLength(10, false))
                .intersect(ofLength(7, true, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withNegated2MinLengthConstraints_shouldCreateStringsUptoShortestLength() {
        StringRestrictions restrictions =
            maxLength(5, false)
                .intersect(maxLength(10, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{0,5}$/"));
    }

    @Test
    void createGenerator_withNegated2MaxLengthConstraints_shouldCreateStringsFromShortestLengthToDefaultMax() {
        StringRestrictions restrictions =
            minLength(5, false)
                .intersect(minLength(10, false))
                .intersect(maxLength(255, true));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{10,255}$/"));
    }

    @Test
    void createGenerator_with2OfDifferentLengthConstraintsWhereOneIsNegated_shouldCreateStringsOfNonNegatedLength() {
        StringRestrictions restrictions =
            ofLength(5, false, false)
                .intersect(ofLength(10, true, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{5}$/"));
    }

    @Test
    void createGenerator_with2OfLengthConstraintsWhereOneIsNegated_should() {
        StringRestrictions restrictions =
            ofLength(5, false, false)
                .intersect(ofLength(5, true, false));

        StringGenerator generator = restrictions.createGenerator();

        assertGeneratorCannotGenerateAnyStrings(generator);
    }

    @Test
    void createGenerator_withNotOfLengthSameAsMaxLength_shouldPermitStringsUpToMaxLengthLess1() {
        StringRestrictions restrictions =
            maxLength(5, false)
                .intersect(ofLength(4, true, false));

        StringGenerator generator = restrictions.createGenerator();

        Assert.assertThat(generator.toString(), equalTo("/^.{0,3}$/"));
    }

    private static StringRestrictions ofLength(int length, boolean negate, boolean soft){
        return negate
            ? TextualRestrictions.withoutLength(length)
            : TextualRestrictions.withLength(length, soft);
    }

    private static StringRestrictions maxLength(int length, boolean soft){
        return TextualRestrictions.withMaxLength(length, soft);
    }

    private static StringRestrictions minLength(int length, boolean soft){
        return TextualRestrictions.withMinLength(length, soft);
    }

    private static StringRestrictions matchingRegex(String regex, @SuppressWarnings("SameParameterValue") boolean negate){
        return TextualRestrictions.withStringMatching(Pattern.compile(regex), negate);
    }

    private static StringRestrictions containsRegex(String regex, @SuppressWarnings("SameParameterValue") boolean negate){
        return TextualRestrictions.withStringContaining(Pattern.compile(regex), negate);
    }

    private static StringRestrictions aValid(@SuppressWarnings("SameParameterValue") StandardConstraintTypes type, @SuppressWarnings("SameParameterValue") boolean negate){
        return new MatchesStandardStringRestrictions(type, negate);
    }

    private static void assertGeneratorCannotGenerateAnyStrings(StringGenerator generator){
        Iterator<String> stringValueIterator = generator.generateAllValues().iterator();
        Assert.assertThat(stringValueIterator.hasNext(), is(false));
    }
}