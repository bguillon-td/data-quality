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
package org.talend.dataquality.statistics.hierarchy.result;

import org.talend.daikon.avro.visitor.path.TraversalPath;
import org.talend.dataquality.common.inference.Analyzers;

public class MapAnalyzerResult extends ContainerAnalyzerResult {

    private final Analyzers.Result sizeResult;

    private final Analyzers.Result keysResult;

    public MapAnalyzerResult(TraversalPath path, Analyzers.Result result, Analyzers.Result sizeResult,
            Analyzers.Result keysResult) {
        super(path, result);
        this.sizeResult = sizeResult;
        this.keysResult = keysResult;
    }

    public <T> T getSizeResult(Class<T> resultType) {
        return this.sizeResult.get(resultType);
    }

    public <T> T getKeysResult(Class<T> resultType) {
        return this.keysResult.get(resultType);
    }

    @Override
    protected String getChildElementName(TraversalPath.TraversalPathElement element) {
        if (element instanceof TraversalPath.MapEntryPathElement) {
            TraversalPath.MapEntryPathElement mapElement = (TraversalPath.MapEntryPathElement) element;
            return mapElement.getKey();
        }
        return super.getChildElementName(element);
    }
}
