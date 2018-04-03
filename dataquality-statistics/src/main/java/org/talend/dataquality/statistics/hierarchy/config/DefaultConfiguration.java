// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.statistics.hierarchy.config;

import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.statistics.cardinality.CardinalityAnalyzer;
import org.talend.dataquality.statistics.frequency.pattern.CompositePatternFrequencyAnalyzer;
import org.talend.dataquality.statistics.numeric.quantile.QuantileAnalyzer;
import org.talend.dataquality.statistics.numeric.summary.SummaryAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeEnum;

abstract class DefaultConfiguration {

    static RecordAnalyzerConfigurer startDefaultConfigurer() {
        return RecordAnalyzerConfigurer.create()
                // integer support
                .intAnalyzer(createIntegerAnalyzer()).longAnalyzer(createIntegerAnalyzer())
                // decimal support
                .floatAnalyzer(createDecimalAnalyzer()).doubleAnalyzer(createDecimalAnalyzer())
                // string support
                .stringAnalyzer(createStringAnalyzer())
                // boolean support
                .booleanAnalyzer(createStringAnalyzer())
                // fixed support
                .fixedAnalyzer(() -> Analyzers.with(new CardinalityAnalyzer()))
                // bytes support
                .bytesAnalyzer(() -> Analyzers.with(new CardinalityAnalyzer()))
                // arrays support
                .arrayAnalyzer(() -> Analyzers.with(new CardinalityAnalyzer())).arraySizeAnalyzer(createIntegerAnalyzer())
                // maps support
                .mapAnalyzer(() -> Analyzers.with(new CardinalityAnalyzer())).mapSizeAnalyzer(createIntegerAnalyzer())
                .mapKeysAnalyzer(createStringAnalyzer())
                // records support
                .recordAnalyzer(createRecordAnalyzer());
    }

    private static AnalyzerSupplier createStringAnalyzer() {
        return () -> Analyzers.with(new CardinalityAnalyzer(), new CompositePatternFrequencyAnalyzer());
    }

    private static AnalyzerSupplier createIntegerAnalyzer() {
        return () -> Analyzers.with(new SummaryAnalyzer(new DataTypeEnum[] { DataTypeEnum.INTEGER }),
                new QuantileAnalyzer(new DataTypeEnum[] { DataTypeEnum.INTEGER }), new CardinalityAnalyzer());
    }

    private static AnalyzerSupplier createDecimalAnalyzer() {
        return () -> Analyzers.with(new SummaryAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE }),
                new QuantileAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE }), new CardinalityAnalyzer());
    }

    private static AnalyzerSupplier createRecordAnalyzer() {
        return () -> Analyzers.with(new CardinalityAnalyzer());
    }

}
