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

import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.statistics.hierarchy.result.*;

class ResultStateVisitor implements StateVisitor<FieldAnalyzerResult> {

    @Override
    public FieldAnalyzerResult visitFieldState(FieldAnalyzerState state) {
        state.end();
        return new FieldAnalyzerResult(state.getPath(), state.getResult());
    }

    @Override
    public FieldAnalyzerResult visitRecordState(RecordAnalyzerState state) {
        state.end();
        ContainerAnalyzerResult result = new RecordAnalyzerResult(state.getPath(), state.getResult());
        this.doVisitContainerStateChildren(state, result);
        return result;
    }

    @Override
    public FieldAnalyzerResult visitMapState(MapAnalyzerState state) {
        state.end();
        MapAnalyzerResult result = new MapAnalyzerResult(state.getPath(), state.getResult(), state.getMapSizeResult(),
                state.getMapKeysResult());
        this.doVisitContainerStateChildren(state, result);
        return result;
    }

    @Override
    public FieldAnalyzerResult visitArrayState(ArrayAnalyzerState state) {
        state.end();
        Analyzers.Result results = state.getArraySizeResult();

        // TODO: visit indexed result

        ArrayAnalyzerResult result = new ArrayAnalyzerResult(state.getPath(), state.getResult(), results);
        this.doVisitContainerStateChildren(state, result);
        return result;
    }

    private void doVisitContainerStateChildren(ContainerAnalyzerState state, ContainerAnalyzerResult result) {
        state.forEach((k, s) -> {
            FieldAnalyzerResult analyzerResult = s.accept(this);
            result.add(analyzerResult);
        });
    }

}
