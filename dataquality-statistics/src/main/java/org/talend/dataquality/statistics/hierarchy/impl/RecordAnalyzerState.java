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

class RecordAnalyzerState extends ContainerAnalyzerState {

    RecordAnalyzerState(Analyzer<Analyzers.Result> analyzer, TraversalPath path, AnalyzersFactory provider) {
        super(analyzer, path, provider);
    }

    <R> R accept(StateVisitor<R> visitor) {
        return visitor.visitRecordState(this);
    }
}
