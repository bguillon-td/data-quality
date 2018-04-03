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

import org.apache.avro.Schema;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.statistics.cardinality.CardinalityAnalyzer;
import org.talend.dataquality.statistics.frequency.pattern.CompositePatternFrequencyAnalyzer;
import org.talend.dataquality.statistics.hierarchy.config.AnalyzersFactory;
import org.talend.dataquality.statistics.numeric.quantile.QuantileAnalyzer;
import org.talend.dataquality.statistics.numeric.summary.SummaryAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * Default implementation of {@link AnalyzersFactory} with relevant analyzers wired
 * for each type.
 */
public class DefaultAnalyzersFactory {

    // @Override
    // public Analyzer<Analyzers.Result> createAnalyzer(Schema type) {
    // switch (type.getType()) {
    // case RECORD:
    // return createRecordAnalyzer();
    // case ARRAY:
    // return createArrayAnalyzer();
    // case MAP:
    // return createMapAnalyzer(type);
    // case INT:
    // case LONG:
    // return createIntegerAnalyzer();
    // case STRING:
    // return createStringAnalyzer();
    // default:
    // throw new UnsupportedOperationException("Data type " + type.getType() + " is not supported yet");
    // }
    // }
    //
    // @Override
    // public Analyzer<Analyzers.Result> createArraySizeAnalyzer() {
    // return createIntegerAnalyzer();
    // }
    //
    // @Override
    // public Analyzer<Analyzers.Result> createMapSizeAnalyzer() {
    // return createIntegerAnalyzer();
    // }
    //
    // @Override
    // public Analyzer<Analyzers.Result> createMapKeysAnalyzer() {
    // return createStringAnalyzer();
    // }
    //
    // private Analyzer<Analyzers.Result> createRecordAnalyzer() {
    // return Analyzers.with(new CardinalityAnalyzer()); // for number of not null records
    // }
    //
    // private Analyzer<Analyzers.Result> createArrayAnalyzer() {
    // return Analyzers.with(new CardinalityAnalyzer()); // for number of not null records
    // }
    //
    // private Analyzer<Analyzers.Result> createMapAnalyzer(Schema mapSchema) {
    // Analyzers analyzer = (Analyzers) this.createAnalyzer(mapSchema.getValueType());
    // return analyzer.and((Analyzers) Analyzers.with(new CardinalityAnalyzer()));
    // }

    // private Analyzer<Analyzers.Result> createStringAnalyzer() {
    // return Analyzers.with(new CardinalityAnalyzer(), new CompositePatternFrequencyAnalyzer());
    // }

    // private Analyzer<Analyzers.Result> createIntegerAnalyzer() {
    // return Analyzers.with(
    // new SummaryAnalyzer(new DataTypeEnum[]{DataTypeEnum.INTEGER}),
    // new QuantileAnalyzer(new DataTypeEnum[]{DataTypeEnum.INTEGER}),
    // new CardinalityAnalyzer());
    // }
}
