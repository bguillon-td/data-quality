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

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerAnalyzerResult extends FieldAnalyzerResult {

    private final List<FieldAnalyzerResult> children = new ArrayList<>();

    public ContainerAnalyzerResult(TraversalPath path, Analyzers.Result result) {
        super(path, result);
    }

    public void add(FieldAnalyzerResult child) {
        this.children.add(child);
    }

    public FieldAnalyzerResult getChild(String name) {
        return children.stream().filter(c -> childMatchesName(c.getPath().last(), name)).findFirst().orElse(null);
    }

    public <T extends FieldAnalyzerResult> T getChild(String name, Class<T> clazz) {
        return clazz.cast(this.getChild(name));
    }

    private boolean childMatchesName(TraversalPath.TraversalPathElement element, String name) {
        String lastElementName = element.getName();
        return getChildElementName(element).equals(name);
    }

    protected String getChildElementName(TraversalPath.TraversalPathElement element) {
        return element.getName();
    }

}
