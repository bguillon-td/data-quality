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
import org.talend.dataquality.statistics.hierarchy.config.AnalyzersFactory;

class MapAnalyzerState extends ContainerAnalyzerState {

    private final Analyzer<Analyzers.Result> sizeAnalyzer;

    private final Analyzer<Analyzers.Result> keysAnalyzer;

    MapAnalyzerState(TraversalPath path, AnalyzersFactory provider) {
        super(provider.createAnalyzer(path.last().getSchema().getValueType()), path, provider);
        this.sizeAnalyzer = provider.createMapSizeAnalyzer();
        this.keysAnalyzer = provider.createMapKeysAnalyzer();
    }

    @Override
    public <R> R accept(StateVisitor<R> visitor) {
        return visitor.visitMapState(this);
    }

    boolean analyzeMapSize(long size) {
        return this.sizeAnalyzer.analyze(String.valueOf(size));
    }

    Analyzers.Result getMapSizeResult() {
        return this.getUniqueResult(this.sizeAnalyzer.getResult());
    }

    boolean analyzeMapKey(String key) {
        return this.keysAnalyzer.analyze(key);
    }

    Analyzers.Result getMapKeysResult() {
        return this.getUniqueResult(this.keysAnalyzer.getResult());
    }

    @Override
    public void end() {
        super.end();
        sizeAnalyzer.end();
        keysAnalyzer.end();
    }

}
