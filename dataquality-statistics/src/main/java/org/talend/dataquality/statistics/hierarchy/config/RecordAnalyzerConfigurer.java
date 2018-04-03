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
package org.talend.dataquality.statistics.hierarchy.config;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordAnalyzerConfigurer {

    private final Map<Schema.Type, AnalyzerSupplier> typesConfiguration = new HashMap<>();

    private final AnalyzerSupplier fallbackAnalyzerProvider;

    private final Map<Schema.Type, List<ValueConverter>> valueConverters = new HashMap<>();

    private AnalyzerSupplier mapKeysAnalyzerSupplier;

    private AnalyzerSupplier mapSizeAnalyzerSupplier;

    private AnalyzerSupplier arraySizeAnalyzerSupplier;

    private RecordAnalyzerConfigurer(AnalyzerSupplier fallbackAnalyzerProvider) {

        this.fallbackAnalyzerProvider = fallbackAnalyzerProvider;
    }

    public static RecordAnalyzerConfigurer create(AnalyzerSupplier defaultAnalyzerSupplier) {
        return new RecordAnalyzerConfigurer(defaultAnalyzerSupplier);
    }

    public static RecordAnalyzerConfigurer fromDefault() {
        return DefaultConfiguration.startDefaultConfigurer();
    }

    static RecordAnalyzerConfigurer create() {
        return new RecordAnalyzerConfigurer(null);
    }

    public RecordAnalyzerConfiguration build() {
        return new RecordAnalyzerConfigurationImpl();
    }

    public RecordAnalyzerConfigurer intAnalyzer(AnalyzerSupplier analyzerSupplier) {
        return this.addToTypeConfiguration(Schema.Type.INT, analyzerSupplier);
    }

    public RecordAnalyzerConfigurer longAnalyzer(AnalyzerSupplier analyzerSupplier) {
        return this.addToTypeConfiguration(Schema.Type.LONG, analyzerSupplier);
    }

    public RecordAnalyzerConfigurer stringAnalyzer(AnalyzerSupplier analyzerSupplier) {
        return this.addToTypeConfiguration(Schema.Type.STRING, analyzerSupplier);
    }

    public RecordAnalyzerConfigurer floatAnalyzer(AnalyzerSupplier analyzerSupplier) {
        return this.addToTypeConfiguration(Schema.Type.FLOAT, analyzerSupplier);
    }

    public RecordAnalyzerConfigurer doubleAnalyzer(AnalyzerSupplier analyzerSupplier) {
        return this.addToTypeConfiguration(Schema.Type.DOUBLE, analyzerSupplier);
    }

    public RecordAnalyzerConfigurer booleanAnalyzer(AnalyzerSupplier analyzerSupplier) {
        return this.addToTypeConfiguration(Schema.Type.BOOLEAN, analyzerSupplier);
    }

    public RecordAnalyzerConfigurer fixedAnalyzer(AnalyzerSupplier analyzerSupplier) {
        return this.addToTypeConfiguration(Schema.Type.FIXED, analyzerSupplier);
    }

    public RecordAnalyzerConfigurer bytesAnalyzer(AnalyzerSupplier analyzerSupplier) {
        return this.addToTypeConfiguration(Schema.Type.BYTES, analyzerSupplier);
    }

    public RecordAnalyzerConfigurer recordAnalyzer(AnalyzerSupplier analyzerSupplier) {
        return this.addToTypeConfiguration(Schema.Type.RECORD, analyzerSupplier);
    }

    public RecordAnalyzerConfigurer arrayAnalyzer(AnalyzerSupplier analyzerSupplier) {
        return this.addToTypeConfiguration(Schema.Type.ARRAY, analyzerSupplier);
    }

    public RecordAnalyzerConfigurer mapAnalyzer(AnalyzerSupplier analyzerSupplier) {
        return this.addToTypeConfiguration(Schema.Type.MAP, analyzerSupplier);
    }

    public RecordAnalyzerConfigurer mapKeysAnalyzer(AnalyzerSupplier analyzerSupplier) {
        this.mapKeysAnalyzerSupplier = analyzerSupplier;
        return this;
    }

    public RecordAnalyzerConfigurer arraySizeAnalyzer(AnalyzerSupplier analyzerSupplier) {
        this.arraySizeAnalyzerSupplier = analyzerSupplier;
        return this;
    }

    public RecordAnalyzerConfigurer mapSizeAnalyzer(AnalyzerSupplier analyzerSupplier) {
        this.mapSizeAnalyzerSupplier = analyzerSupplier;
        return this;
    }

    public RecordAnalyzerConfigurer bytesConverter(ValueConverter<ByteBuffer> converter) {
        return this.addValueConverter(Schema.Type.BYTES, converter);
    }

    public RecordAnalyzerConfigurer fixedConverter(ValueConverter<GenericData.Fixed> converter) {
        return this.addValueConverter(Schema.Type.FIXED, converter);
    }

    private RecordAnalyzerConfigurer addToTypeConfiguration(Schema.Type type, AnalyzerSupplier supplier) {
        this.typesConfiguration.put(type, supplier);
        return this;
    }

    private RecordAnalyzerConfigurer addValueConverter(Schema.Type type, ValueConverter converter) {
        this.valueConverters.computeIfAbsent(type, t -> new ArrayList<>()).add(converter);
        return this;
    }

    class RecordAnalyzerConfigurationImpl implements RecordAnalyzerConfiguration {

        @Override
        public ValueConverter<ByteBuffer> getBytesConverter() {
            // TODO
            return null;
        }

        @Override
        public Analyzer<Analyzers.Result> createAnalyzer(Schema schema) {
            AnalyzerSupplier suppliers = getAnalyzerSupplier(schema.getType());
            return suppliers.get();
        }

        @Override
        public Analyzer<Analyzers.Result> createArraySizeAnalyzer() {
            return RecordAnalyzerConfigurer.this.arraySizeAnalyzerSupplier != null
                    ? RecordAnalyzerConfigurer.this.arraySizeAnalyzerSupplier.get()
                    : this.getAnalyzerSupplier(Schema.Type.INT).get();
        }

        @Override
        public Analyzer<Analyzers.Result> createMapSizeAnalyzer() {
            return RecordAnalyzerConfigurer.this.mapSizeAnalyzerSupplier != null
                    ? RecordAnalyzerConfigurer.this.mapSizeAnalyzerSupplier.get()
                    : this.getAnalyzerSupplier(Schema.Type.INT).get();
        }

        @Override
        public Analyzer<Analyzers.Result> createMapKeysAnalyzer() {
            return RecordAnalyzerConfigurer.this.mapKeysAnalyzerSupplier != null
                    ? RecordAnalyzerConfigurer.this.mapKeysAnalyzerSupplier.get()
                    : this.getAnalyzerSupplier(Schema.Type.STRING).get();
        }

        private AnalyzerSupplier getAnalyzerSupplier(Schema.Type type) {
            AnalyzerSupplier analyzerSuppliers = RecordAnalyzerConfigurer.this.typesConfiguration.get(type);
            if (analyzerSuppliers == null) {
                return this.getFallBackAnalyzer(type);
            }
            return analyzerSuppliers;
        }

        private AnalyzerSupplier getFallBackAnalyzer(Schema.Type type) {
            return RecordAnalyzerConfigurer.this.fallbackAnalyzerProvider;
        }
    }

}
