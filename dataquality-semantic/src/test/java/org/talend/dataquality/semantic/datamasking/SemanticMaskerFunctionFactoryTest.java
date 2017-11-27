// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.semantic.datamasking;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionType;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.semantic.GenerateFromRegex;

/**
 * Test class for SemanticMaskerFunctionFactory
 */
public class SemanticMaskerFunctionFactoryTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.semantic.datamasking.SemanticMaskerFunctionFactory#createMaskerFunctionForSemanticCategory(java.lang.String, java.lang.String, java.util.List)}
     * .
     */
    @Test
    public void testCreateMaskerFunctionForSemanticCategoryStringStringListOfString() {
        // normal case
        Function<String> generateFromRegexFunction = SemanticMaskerFunctionFactory
                .createMaskerFunctionForSemanticCategory("FR_POSTAL_CODE", "integer", null); //$NON-NLS-1$ //$NON-NLS-2$
        generateFromRegexFunction.setRandom(new Random(100L));
        Assert.assertTrue("The Function should be instance of GenerateFromRegex class", //$NON-NLS-1$
                generateFromRegexFunction instanceof GenerateFromRegex);
        String generateMaskedRow = generateFromRegexFunction.generateMaskedRow("any input string"); //$NON-NLS-1$
        Assert.assertEquals("The mask result should be 02779", "02779", generateMaskedRow); //$NON-NLS-1$//$NON-NLS-2$

        // when input data from name change to id

        generateFromRegexFunction = SemanticMaskerFunctionFactory
                .createMaskerFunctionForSemanticCategory("583edc44ec06957a34fa643c", "integer", null); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertFalse("The Function should not be instance of GenerateFromRegex class", //$NON-NLS-1$
                generateFromRegexFunction instanceof GenerateFromRegex);

        // category and dataType is not exist case
        try {
            generateFromRegexFunction = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory("aaaaa", "bigdata", //$NON-NLS-1$//$NON-NLS-2$
                    null);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("There should be a IllegalArgumentException", true); //$NON-NLS-1$
            return;
        }
        Assert.assertTrue("this case there should be a exception", false); //$NON-NLS-1$
    }

    @Test
    public void testGetFunctionByType() {
        Function function = SemanticMaskerFunctionFactory
                .getMaskerFunctionByFunctionName(FunctionType.BETWEEN_INDEXES_KEEP.name(), "string", null, "2,10");
        Assert.assertEquals(FunctionType.BETWEEN_INDEXES_KEEP.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.BETWEEN_INDEXES_REMOVE.name(),
                "string", null, "2,10");
        Assert.assertEquals(FunctionType.BETWEEN_INDEXES_REMOVE.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.BETWEEN_INDEXES_REPLACE.name(),
                "string", null, "2,10");
        Assert.assertEquals(FunctionType.BETWEEN_INDEXES_REPLACE.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.KEEP_YEAR.name(), "string", null,
                "2,10");
        Assert.assertNotEquals(FunctionType.KEEP_YEAR.getClazz(), function.getClass());
        Assert.assertEquals(org.talend.dataquality.datamasking.semantic.DateFunctionAdapter.class, function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.GENERATE_FROM_PATTERN.name(),
                "String", null, "2,10");
        Assert.assertEquals(FunctionType.GENERATE_FROM_PATTERN.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.REPLACE_ALL.name(), "String", null,
                "X");
        Assert.assertEquals(FunctionType.REPLACE_ALL.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.REPLACE_FIRST_CHARS.name(),
                "string", null, "10");
        Assert.assertEquals(FunctionType.REPLACE_FIRST_CHARS_STRING.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.REPLACE_LAST_CHARS.name(), "string",
                null, "5");
        Assert.assertEquals(FunctionType.REPLACE_LAST_CHARS_STRING.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.REPLACE_NUMERIC.name(), "integer",
                null, "5");
        Assert.assertEquals(FunctionType.REPLACE_NUMERIC_INT.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.KEEP_FIRST_AND_GENERATE.name(),
                "string", null, "2");
        Assert.assertEquals(FunctionType.KEEP_FIRST_AND_GENERATE_STRING.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.KEEP_LAST_AND_GENERATE.name(),
                "string", null, "2");
        Assert.assertEquals(FunctionType.KEEP_LAST_AND_GENERATE_STRING.getClazz(), function.getClass());
    }

}