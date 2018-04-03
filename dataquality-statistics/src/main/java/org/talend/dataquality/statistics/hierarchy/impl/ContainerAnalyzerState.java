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
package org.talend.dataquality.statistics.hierarchy.impl;

import org.apache.avro.Schema;
import org.talend.daikon.avro.visitor.path.TraversalPath;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.statistics.hierarchy.config.AnalyzersFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

abstract class ContainerAnalyzerState extends FieldAnalyzerState {

    protected final Map<TraversalPath, FieldAnalyzerState> children = new HashMap<>();

    protected final AnalyzersFactory provider;

    ContainerAnalyzerState(Analyzer<Analyzers.Result> analyzer, TraversalPath path, AnalyzersFactory provider) {
        super(analyzer, path);
        this.provider = provider;
    }

    FieldAnalyzerState get(TraversalPath path) {
        return this.children.computeIfAbsent(path, this::createState);
    }

    <T extends FieldAnalyzerState> T get(TraversalPath path, Class<T> expectedType) {
        return expectedType.cast(this.get(path));
    }

    void forEach(BiConsumer<TraversalPath, FieldAnalyzerState> consumer) {
        this.children.forEach(consumer);
    }

    protected FieldAnalyzerState createState(TraversalPath path) {
        return createState(path, path.last().getSchema());
    }

    protected FieldAnalyzerState createState(TraversalPath path, Schema schema) {
        switch (schema.getType()) {
        case ARRAY:
            return new ArrayAnalyzerState(provider.createAnalyzer(schema), path, provider);
        case RECORD:
            return new RecordAnalyzerState(provider.createAnalyzer(schema), path, provider);
        case MAP:
            return new MapAnalyzerState(path, provider);
        default:
            return new FieldAnalyzerState(provider.createAnalyzer(schema), path);
        }
    }
}
