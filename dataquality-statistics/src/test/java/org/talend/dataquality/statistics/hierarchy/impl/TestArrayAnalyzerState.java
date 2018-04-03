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

import org.apache.avro.SchemaBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.talend.daikon.avro.visitor.path.TraversalPath;
import org.talend.dataquality.statistics.hierarchy.config.RecordAnalyzerConfiguration;
import org.talend.dataquality.statistics.hierarchy.config.RecordAnalyzerConfigurer;

public class TestArrayAnalyzerState {

    @Test
    public void testIndexed() throws Exception {
        TraversalPath path = TraversalPath.create(
                SchemaBuilder.record("test").fields().name("array").type().array().items().stringType().noDefault().endRecord())
                .append("array");

        RecordAnalyzerConfiguration configuration = RecordAnalyzerConfigurer.fromDefault().build();

        ArrayAnalyzerState state = new ArrayAnalyzerState(null, path, configuration);

        FieldAnalyzerState index0 = state.get(path.appendArrayIndex(0));

        Assert.assertNotNull(index0);
    }

}
