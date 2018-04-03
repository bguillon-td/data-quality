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

import java.io.Serializable;
import java.util.List;

class FieldAnalyzerState implements Serializable {

    private final Analyzer<Analyzers.Result> analyzer;

    private final TraversalPath path;

    FieldAnalyzerState(Analyzer<Analyzers.Result> analyzer, TraversalPath path) {
        this.analyzer = analyzer;
        this.analyzer.init();
        this.path = path;
    }

    boolean analyze(String value) {
        return this.analyzer.analyze(value);
    }

    void end() {
        this.analyzer.end();
    }

    Analyzers.Result getResult() {
        return this.getUniqueResult(this.analyzer.getResult());
    }

    TraversalPath getPath() {
        return path;
    }

    <R> R accept(StateVisitor<R> visitor) {
        return visitor.visitFieldState(this);
    }

    protected Analyzers.Result getUniqueResult(List<Analyzers.Result> resultsList) {
        if (resultsList.size() != 1) {
            throw new IllegalStateException(String.format(
                    "Analyzer's result should always have exactly one result but got %d for path %s", resultsList.size(), path));
        }
        return resultsList.get(0);
    }

}
