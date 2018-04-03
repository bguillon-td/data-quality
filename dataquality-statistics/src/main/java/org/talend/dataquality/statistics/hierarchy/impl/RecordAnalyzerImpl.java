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

import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.visitor.path.TraversalPath;
import org.talend.daikon.avro.visitor.record.*;
import org.talend.dataquality.statistics.hierarchy.config.AnalyzersFactory;
import org.talend.dataquality.statistics.hierarchy.RecordAnalyzer;
import org.talend.dataquality.statistics.hierarchy.result.RecordAnalyzerResult;

import java.util.Arrays;
import java.util.Stack;
import java.util.function.Function;

public class RecordAnalyzerImpl implements RecordAnalyzer, RecordVisitor {

    private final AnalyzersFactory analyzersFactory;

    private final Stack<ContainerAnalyzerState> state = new Stack<>();

    private ContainerAnalyzerState root;

    public RecordAnalyzerImpl(AnalyzersFactory analyzersFactory) {
        this.analyzersFactory = analyzersFactory;
    }

    @Override
    public void analyze(IndexedRecord record) {
        VisitableRecord visitable = new VisitableRecord(record);
        visitable.accept(this);
    }

    @Override
    public RecordAnalyzerResult getResult() {
        ResultStateVisitor visitor = new ResultStateVisitor();
        return (RecordAnalyzerResult) this.root.accept(visitor);
    }

    @Override
    public void visit(VisitableInt field) {
        this.analyzeField(field, String::valueOf);
    }

    @Override
    public void visit(VisitableLong field) {
        this.analyzeField(field, String::valueOf);
    }

    @Override
    public void visit(VisitableString field) {
        this.analyzeField(field, x -> x);
    }

    @Override
    public void visit(VisitableBoolean field) {
        this.analyzeField(field, String::valueOf);
    }

    @Override
    public void visit(VisitableFloat field) {
        this.analyzeField(field, String::valueOf);
    }

    @Override
    public void visit(VisitableDouble field) {
        this.analyzeField(field, String::valueOf);
    }

    @Override
    public void visit(VisitableNull field) {
        this.analyzeField(field, x -> null);
    }

    @Override
    public void visit(VisitableFixed field) {
        this.analyzeField(field, f -> Arrays.toString(f.bytes()));
    }

    @Override
    public void visit(VisitableBytes field) {
        this.analyzeField(field, f -> Arrays.toString(f.array()));
    }

    @Override
    public void visit(VisitableRecord record) {
        if (root == null) {
            root = new RecordAnalyzerState(this.analyzersFactory.createAnalyzer(record.getValue().getSchema()), record.getPath(),
                    this.analyzersFactory);
        }
        final ContainerAnalyzerState nextState = this.getState(record.getPath(), ContainerAnalyzerState.class);
        nextState.analyze(String.valueOf(record.getValue()));
        this.state.push(nextState);

        // continue the visit
        record.getFields().forEachRemaining(f -> f.accept(this));

        this.state.pop();
    }

    @Override
    public void visit(VisitableArray array) {
        final ArrayAnalyzerState state = this.getState(array.getPath(), ArrayAnalyzerState.class);
        // analyze size
        state.analyzeArraySize(array.getValue().size());

        array.getValue().forEach(item -> state.analyze(String.valueOf(item)));

        this.state.push(state);

        // analyze content - this is part of the array's path itself
        array.getItems(VisitableArray.ArrayItemsPathType.NOT_INDEXED).forEachRemaining(i -> i.accept(this));

        this.state.push(state.getIndicesState());

        // analyze content by index - this is part of array's children
        array.getItems(VisitableArray.ArrayItemsPathType.INDEXED).forEachRemaining(i -> i.accept(this));

        this.state.pop();

        this.state.pop();
    }

    @Override
    public void visit(VisitableMap map) {
        final MapAnalyzerState state = this.getState(map.getPath(), MapAnalyzerState.class);
        // analyze size
        state.analyzeMapSize(map.getValue().size());

        // analyze keys
        map.getValue().keySet().forEach(k -> state.analyzeMapKey(k.toString()));

        // analyze values as text (map analysis)
        map.getValue().values().forEach(v -> state.analyze(String.valueOf(v)));

        this.state.push(state);

        // enter values
        map.getValues().forEachRemaining(v -> v.accept(this));

        this.state.pop();
    }

    private <T> boolean analyzeField(VisitableStructure<T> field, Function<T, String> toString) {
        final String stringValue = toString.apply(field.getValue());
        return this.getState(field.getPath(), FieldAnalyzerState.class).analyze(stringValue);
    }

    private <T extends FieldAnalyzerState> T getState(TraversalPath path, Class<T> expectedType) {
        if (state.isEmpty()) {
            return expectedType.cast(root);
        }
        return expectedType.cast(state.peek().get(path));
    }
}
