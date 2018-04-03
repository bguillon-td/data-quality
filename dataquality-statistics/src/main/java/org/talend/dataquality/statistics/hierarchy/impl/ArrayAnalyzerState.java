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

import org.talend.daikon.avro.visitor.path.TraversalPath;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.statistics.cardinality.CardinalityAnalyzer;
import org.talend.dataquality.statistics.hierarchy.config.AnalyzersFactory;

class ArrayAnalyzerState extends ContainerAnalyzerState {

    private final Analyzer<Analyzers.Result> sizeAnalyzer;

    private final IndicesState indicesState;

    ArrayAnalyzerState(Analyzer<Analyzers.Result> analyzer, TraversalPath path, AnalyzersFactory provider) {
        super(analyzer, path, provider);
        this.sizeAnalyzer = provider.createArraySizeAnalyzer();
        this.sizeAnalyzer.init();
        this.indicesState = new IndicesState(Analyzers.with(new CardinalityAnalyzer()), path, provider);
    }

    ContainerAnalyzerState getIndicesState() {
        return indicesState;
    }

    @Override
    public <R> R accept(StateVisitor<R> visitor) {
        return visitor.visitArrayState(this);
    }

    boolean analyzeArraySize(long size) {
        return this.sizeAnalyzer.analyze(String.valueOf(size));
    }

    Analyzers.Result getArraySizeResult() {
        return this.getUniqueResult(this.sizeAnalyzer.getResult());
    }

    @Override
    public void end() {
        super.end();
        sizeAnalyzer.end();
    }

    private static class IndicesState extends ContainerAnalyzerState {

        IndicesState(Analyzer<Analyzers.Result> analyzer, TraversalPath path, AnalyzersFactory provider) {
            super(analyzer, path, provider);
            this.analyze("1");
        }
    }
}
