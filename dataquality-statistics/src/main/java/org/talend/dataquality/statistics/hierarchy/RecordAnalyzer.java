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
package org.talend.dataquality.statistics.hierarchy;

import org.apache.avro.generic.IndexedRecord;
import org.talend.dataquality.statistics.hierarchy.config.AnalyzersFactory;
import org.talend.dataquality.statistics.hierarchy.config.DefaultAnalyzersFactory;
import org.talend.dataquality.statistics.hierarchy.config.RecordAnalyzerConfiguration;
import org.talend.dataquality.statistics.hierarchy.config.RecordAnalyzerConfigurer;
import org.talend.dataquality.statistics.hierarchy.impl.RecordAnalyzerImpl;
import org.talend.dataquality.statistics.hierarchy.result.RecordAnalyzerResult;

import java.util.stream.Stream;

/**
 * The main entry-point of the API.
 *
 * This analyzer works on a stream of Avro {@link IndexedRecord}s.
 *
 * It will visit each IndexedRecord in deep and execute a configurable set
 * of {@link org.talend.dataquality.common.inference.Analyzer}s on each field it visit.
 *
 * The analyzer keeps an internal state for each distinct traversal path. This state is updated for each analyzed record
 * so that at the end of the analysis an aggregated result is provided.
 *
 * <pre>
 *     {@code
 *     RecordAnalyzer analyzer = RecordAnalyzer.create();
 *     Stream<IndexedRecord> records = ...;
 *     analyzer.analyze(records);
 *     ContainerAnalyzerResult result = analyzer.getResult();
 *     }
 * </pre>
 *
 * The {@link #create()} and {@link #create(RecordAnalyzerConfiguration)} static methods of this interface can be used
 * to create an implementation without bothering with implementation details.
 */
public interface RecordAnalyzer {

    /**
     * @return a new instance of RecordAnalyzer configured with {@link DefaultAnalyzersFactory}
     */
    static RecordAnalyzer create() {
        return create(RecordAnalyzerConfigurer.fromDefault().build());
    }

    /**
     * @param configuration configures the analysis
     * @return a new instance of RecordAnalyzer configured with the provided analyzersFactory
     */
    static RecordAnalyzer create(RecordAnalyzerConfiguration configuration) {
        return new RecordAnalyzerImpl(configuration);
    }

    /**
     * Analyzes the provided record.
     *
     * Can be called several times sequentially to analyze many records.
     *
     * @param record the record to analyze
     */
    void analyze(IndexedRecord record);

    /**
     * Analyzes and consumes a stream of records
     *
     * @param records the stream of records
     */
    default void analyze(Stream<IndexedRecord> records) {
        records.forEach(this::analyze);
    }

    /**
     * @return The aggregated result of all the analysis performed through {@link #analyze(IndexedRecord)}
     */
    RecordAnalyzerResult getResult();

}
