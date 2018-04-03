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

public class ArrayAnalyzerResult extends ContainerAnalyzerResult {

    private final Analyzers.Result sizeResult;

    public ArrayAnalyzerResult(TraversalPath path, Analyzers.Result result, Analyzers.Result sizeResult) {
        super(path, result);
        this.sizeResult = sizeResult;
    }

    public <T> T getSizeResult(Class<T> resultType) {
        return this.sizeResult.get(resultType);
    }

    @Override
    protected String getChildElementName(TraversalPath.TraversalPathElement element) {
        if (element instanceof TraversalPath.ArrayItemPathElement) {
            TraversalPath.ArrayItemPathElement arrayElement = (TraversalPath.ArrayItemPathElement) element;
            return String.valueOf(arrayElement.getIndex());
        }
        return super.getChildElementName(element);
    }
}
