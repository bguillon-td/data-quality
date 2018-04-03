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

import java.io.Serializable;

/**
 * Provides a way to choose the {@link Analyzer} to trigger on each
 * Avro type.
 *
 * Analyzers can be combined using the {@link Analyzers} implementation
 */
public interface AnalyzersFactory extends Serializable {

    /**
     * Creates the appropriate analyzer for a given schema
     * 
     * @param type the schema to consider
     * @return the analyzer
     */
    Analyzer<Analyzers.Result> createAnalyzer(Schema type);

    /**
     * @return a dedicated analyzers for arrays' size
     */
    Analyzer<Analyzers.Result> createArraySizeAnalyzer();

    /**
     * @return a dedicated analyzer for maps' size
     */
    Analyzer<Analyzers.Result> createMapSizeAnalyzer();

    /**
     * @return a dedicated analyzer for maps' keys (always strings in Avro)
     */
    Analyzer<Analyzers.Result> createMapKeysAnalyzer();

}
