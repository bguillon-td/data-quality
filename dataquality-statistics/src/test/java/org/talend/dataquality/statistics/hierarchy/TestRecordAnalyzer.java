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
package org.talend.dataquality.statistics.hierarchy;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.util.Utf8;
import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.statistics.cardinality.CardinalityStatistics;
import org.talend.dataquality.statistics.frequency.pattern.PatternFrequencyStatistics;
import org.talend.dataquality.statistics.hierarchy.result.*;
import org.talend.dataquality.statistics.numeric.quantile.QuantileStatistics;
import org.talend.dataquality.statistics.numeric.summary.SummaryStatistics;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestRecordAnalyzer {

    @Test
    public void testFlatRecords() {
        Schema schema = SchemaBuilder.record("test").fields().name("intValue").type().intType().noDefault().name("longValue")
                .type().longType().noDefault().endRecord();

        IndexedRecord record1 = new GenericRecordBuilder(schema).set("intValue", 2).set("longValue", 45L).build();

        IndexedRecord record2 = new GenericRecordBuilder(schema).set("intValue", 5).set("longValue", 45L).build();

        RecordAnalyzer analyzer = RecordAnalyzer.create();

        analyzer.analyze(record1);

        analyzer.analyze(record2);

        ContainerAnalyzerResult root = analyzer.getResult();
        Assert.assertNotNull(root);
        Assert.assertEquals(2, root.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, root.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(0, root.getResult(CardinalityStatistics.class).getDuplicateCount());

        FieldAnalyzerResult intValue = root.getChild("intValue");
        Assert.assertEquals(2, intValue.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, intValue.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(0, intValue.getResult(CardinalityStatistics.class).getDuplicateCount());

        FieldAnalyzerResult longValue = root.getChild("longValue");
        Assert.assertEquals(2, longValue.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, longValue.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(1, longValue.getResult(CardinalityStatistics.class).getDuplicateCount());
    }

    @Test
    public void testInnerRecord() {
        Schema main = SchemaBuilder.record("test").fields().name("intValue").type().intType().noDefault().name("longValue").type()
                .longType().noDefault().name("inner").type().record("inner").fields().name("innerIntValue").type().intType()
                .noDefault().name("innerLongValue").type().longType().noDefault().endRecord().noDefault().endRecord();

        IndexedRecord record1 = new GenericRecordBuilder(main).set("intValue", 2).set("longValue", 45L)
                .set("inner", new GenericRecordBuilder(main.getField("inner").schema()).set("innerIntValue", 133)
                        .set("innerLongValue", 153L).build())
                .build();

        IndexedRecord record2 = new GenericRecordBuilder(main).set("intValue", 5).set("longValue", 45L)
                .set("inner", new GenericRecordBuilder(main.getField("inner").schema()).set("innerIntValue", 266)
                        .set("innerLongValue", 153L).build())
                .build();

        RecordAnalyzer analyzer = RecordAnalyzer.create();

        analyzer.analyze(record1);

        analyzer.analyze(record2);

        ContainerAnalyzerResult root = analyzer.getResult();
        Assert.assertEquals("/", root.getPath().toString());
        Assert.assertEquals(2, root.getResult(CardinalityStatistics.class).getCount());

        // intValue : first level field
        FieldAnalyzerResult intValue = root.getChild("intValue");
        Assert.assertNotNull(intValue);
        Assert.assertEquals(2, intValue.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, intValue.getResult(CardinalityStatistics.class).getDistinctCount());

        // longValue: first level field
        FieldAnalyzerResult longValue = root.getChild("longValue");
        Assert.assertEquals(2, longValue.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, longValue.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(1, longValue.getResult(CardinalityStatistics.class).getDuplicateCount());

        // inner : first level field of type record
        ContainerAnalyzerResult inner = root.getChild("inner", ContainerAnalyzerResult.class);
        Assert.assertNotNull(inner);
        Assert.assertEquals(2, inner.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, inner.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(0, inner.getResult(CardinalityStatistics.class).getDuplicateCount());

        // innerIntValue: second level field
        FieldAnalyzerResult innerIntValue = inner.getChild("innerIntValue");
        Assert.assertNotNull(innerIntValue);
        Assert.assertEquals(2, innerIntValue.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, innerIntValue.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(0, innerIntValue.getResult(CardinalityStatistics.class).getDuplicateCount());

        // innerLongValue: second level field
        FieldAnalyzerResult innerLongValue = inner.getChild("innerLongValue");
        Assert.assertNotNull(innerLongValue);
        Assert.assertEquals(2, innerLongValue.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, innerLongValue.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(1, innerLongValue.getResult(CardinalityStatistics.class).getDuplicateCount());

    }

    @Test
    public void testArrayOfIntegers() {
        Schema schema = SchemaBuilder.record("test").fields().name("array").type().array().items().intType().noDefault()
                .endRecord();

        IndexedRecord record1 = new GenericRecordBuilder(schema).set("array", Arrays.asList(12, 14, 16)).build();

        IndexedRecord record2 = new GenericRecordBuilder(schema).set("array", Arrays.asList(2, 14)).build();

        RecordAnalyzer analyzer = RecordAnalyzer.create();

        analyzer.analyze(record1);

        analyzer.analyze(record2);

        // root statistics
        ContainerAnalyzerResult root = analyzer.getResult();
        Assert.assertEquals(2, root.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, root.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(0, root.getResult(CardinalityStatistics.class).getDuplicateCount());

        // array field statistics
        ArrayAnalyzerResult array = root.getChild("array", ArrayAnalyzerResult.class);
        Assert.assertEquals(5, array.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(4, array.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(1, array.getResult(CardinalityStatistics.class).getDuplicateCount());

        // array field size statistics
        Assert.assertEquals(2, array.getSizeResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, array.getSizeResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(2d, array.getSizeResult(SummaryStatistics.class).getMin(), 0); // min array size
        Assert.assertEquals(3d, array.getSizeResult(SummaryStatistics.class).getMax(), 0); // max array size
        Assert.assertEquals(2.5d, array.getSizeResult(SummaryStatistics.class).getMean(), 0); // avg array size

        // per index statistics
        FieldAnalyzerResult index0 = array.getChild("0", FieldAnalyzerResult.class);
        Assert.assertEquals(2, index0.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, index0.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(0, index0.getResult(CardinalityStatistics.class).getDuplicateCount());
        Assert.assertNotNull(index0.getResult(SummaryStatistics.class));
        Assert.assertNotNull(index0.getResult(QuantileStatistics.class));

        FieldAnalyzerResult index1 = array.getChild("1", FieldAnalyzerResult.class);
        Assert.assertEquals(2, index1.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, index1.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(1, index1.getResult(CardinalityStatistics.class).getDuplicateCount());
        Assert.assertNotNull(index1.getResult(SummaryStatistics.class));
        Assert.assertNotNull(index1.getResult(QuantileStatistics.class));

        FieldAnalyzerResult index2 = array.getChild("2", FieldAnalyzerResult.class);
        Assert.assertEquals(1, index2.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, index2.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(0, index2.getResult(CardinalityStatistics.class).getDuplicateCount());
        Assert.assertNotNull(index2.getResult(SummaryStatistics.class));
        Assert.assertNotNull(index2.getResult(QuantileStatistics.class));
    }

    @Test
    public void testArrayOfArrayOfString() {
        Schema schema = SchemaBuilder.record("test").fields().name("arrayOfArray").type().array().items().array().items()
                .stringType().noDefault().endRecord();

        IndexedRecord record1 = new GenericRecordBuilder(schema).set("arrayOfArray",
                Arrays.asList(Arrays.asList("ABC", "DEFGHI"), Arrays.asList("abc", "def"), Collections.singletonList("abc")))
                .build();

        IndexedRecord record2 = new GenericRecordBuilder(schema)
                .set("arrayOfArray", Arrays.asList(Collections.singletonList("ABC"), Arrays.asList("abc", "def"))).build();

        RecordAnalyzer analyzer = RecordAnalyzer.create();

        analyzer.analyze(record1);

        analyzer.analyze(record2);

        RecordAnalyzerResult root = analyzer.getResult();

        // first level array
        ArrayAnalyzerResult firstLevelArray = root.getChild("arrayOfArray", ArrayAnalyzerResult.class);

        // first level array - index 0 = ["ABC", "DEFGHI"], ["ABC"]
        ArrayAnalyzerResult arrayOfArray0 = firstLevelArray.getChild("0", ArrayAnalyzerResult.class);
        Assert.assertEquals(3, arrayOfArray0.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, arrayOfArray0.getSizeResult(SummaryStatistics.class).getMin(), 0);
        Assert.assertEquals(2, arrayOfArray0.getSizeResult(SummaryStatistics.class).getMax(), 0);

        // index 0, 0 = ABC, ABC
        FieldAnalyzerResult index00 = arrayOfArray0.getChild("0");
        Assert.assertEquals(2, index00.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, index00.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(2, index00.getResult(PatternFrequencyStatistics.class).getFrequency("AAA"));

        // index 0, 1 = DEFGHI
        FieldAnalyzerResult index01 = arrayOfArray0.getChild("0");
        Assert.assertEquals(2, index01.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, index01.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(2, index01.getResult(PatternFrequencyStatistics.class).getFrequency("AAA"));

        // first level array - index 1 = ["abc", "def"], ["abc", "def"]
        ArrayAnalyzerResult arrayOfArray1 = firstLevelArray.getChild("1", ArrayAnalyzerResult.class);
        Assert.assertEquals(4, arrayOfArray1.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, arrayOfArray1.getSizeResult(SummaryStatistics.class).getMin(), 0);
        Assert.assertEquals(2, arrayOfArray1.getSizeResult(SummaryStatistics.class).getMax(), 0);

        // first level array - index 2 = ["abc"]
        ArrayAnalyzerResult arrayOfArray2 = firstLevelArray.getChild("2", ArrayAnalyzerResult.class);
        Assert.assertEquals(1, arrayOfArray2.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, arrayOfArray2.getSizeResult(SummaryStatistics.class).getMin(), 0);
        Assert.assertEquals(1, arrayOfArray2.getSizeResult(SummaryStatistics.class).getMax(), 0);

        // second level array
        ArrayAnalyzerResult secondLevelArray = firstLevelArray.getChild("arrayOfArray", ArrayAnalyzerResult.class);
        Assert.assertEquals(8, secondLevelArray.getResult(CardinalityStatistics.class).getCount());

        // full content of the array of array
        FieldAnalyzerResult rawContent = secondLevelArray.getChild("arrayOfArray");
        Assert.assertEquals(2, rawContent.getResult(PatternFrequencyStatistics.class).getFrequency("AAA"));
        Assert.assertEquals(1, rawContent.getResult(PatternFrequencyStatistics.class).getFrequency("AAAAAA"));
        Assert.assertEquals(5, rawContent.getResult(PatternFrequencyStatistics.class).getFrequency("aaa"));
    }

    @Test
    public void testArrayOfRecords() {
        Schema innerRecordSchema = SchemaBuilder.record("inner").fields().name("innerString").type().stringType().noDefault()
                .name("innerInteger").type().intType().noDefault().endRecord();

        Schema schema = SchemaBuilder.record("test").fields().name("array").type().array().items().type(innerRecordSchema)
                .noDefault().endRecord();

        // array of 3 records
        IndexedRecord record1 = new GenericRecordBuilder(schema).set("array", Arrays.asList(
                new GenericRecordBuilder(innerRecordSchema).set("innerString", "innerField1").set("innerInteger", 1).build(),
                new GenericRecordBuilder(innerRecordSchema).set("innerString", "innerField2").set("innerInteger", 2).build(),
                new GenericRecordBuilder(innerRecordSchema).set("innerString", "innerField3").set("innerInteger", 3).build()))
                .build();

        // array of 2 records
        IndexedRecord record2 = new GenericRecordBuilder(schema).set("array", Arrays.asList(
                new GenericRecordBuilder(innerRecordSchema).set("innerString", "innerField4").set("innerInteger", 4).build(),
                new GenericRecordBuilder(innerRecordSchema).set("innerString", "innerField5").set("innerInteger", 5).build()))
                .build();

        // array of 1 record
        IndexedRecord record3 = new GenericRecordBuilder(schema).set("array", Collections.singletonList(
                new GenericRecordBuilder(innerRecordSchema).set("innerString", "innerField1").set("innerInteger", 1).build()))
                .build();

        RecordAnalyzer analyzer = RecordAnalyzer.create();

        analyzer.analyze(record1);
        analyzer.analyze(record2);
        analyzer.analyze(record3);

        RecordAnalyzerResult root = analyzer.getResult();

        // /array analysis
        ArrayAnalyzerResult array = root.getChild("array", ArrayAnalyzerResult.class);
        Assert.assertEquals(3, array.getSizeResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(3, array.getSizeResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(0, array.getSizeResult(CardinalityStatistics.class).getDuplicateCount());

        Assert.assertEquals(1, array.getSizeResult(SummaryStatistics.class).getMin(), 0);
        Assert.assertEquals(3, array.getSizeResult(SummaryStatistics.class).getMax(), 0);
        Assert.assertEquals(2, array.getSizeResult(SummaryStatistics.class).getMean(), 0);

        RecordAnalyzerResult inner = array.getChild("array", RecordAnalyzerResult.class);

        // /array/innerInteger analysis
        FieldAnalyzerResult innerInteger = inner.getChild("innerInteger", FieldAnalyzerResult.class);
        Assert.assertEquals(6, innerInteger.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(5, innerInteger.getResult(CardinalityStatistics.class).getDistinctCount()); // 1 is duplicate
        Assert.assertEquals(1, innerInteger.getResult(CardinalityStatistics.class).getDuplicateCount());

        // /array/innerString analysis
        FieldAnalyzerResult innerString = inner.getChild("innerString", FieldAnalyzerResult.class);
        Assert.assertEquals(6, innerString.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(5, innerString.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(1, innerString.getResult(CardinalityStatistics.class).getDuplicateCount());

        // /array[0] analysis
        RecordAnalyzerResult array0 = array.getChild("0", RecordAnalyzerResult.class);
        Assert.assertEquals(3, array0.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, array0.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(1, array0.getResult(CardinalityStatistics.class).getDuplicateCount());

        // /array[0]/innerInteger analysis
        FieldAnalyzerResult array0InnerInteger = array0.getChild("innerInteger");
        Assert.assertEquals(3, array0InnerInteger.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, array0InnerInteger.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(1, array0InnerInteger.getResult(CardinalityStatistics.class).getDuplicateCount());

        // /array[0]/stringInteger analysis
        FieldAnalyzerResult array0InnerString = array0.getChild("innerString");
        Assert.assertEquals(3, array0InnerString.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, array0InnerString.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(1, array0InnerString.getResult(CardinalityStatistics.class).getDuplicateCount());

        // /array[1] analysis
        RecordAnalyzerResult array1 = array.getChild("1", RecordAnalyzerResult.class);
        Assert.assertEquals(2, array1.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, array1.getResult(CardinalityStatistics.class).getDistinctCount());

        // /array[1]/innerInteger analysis
        FieldAnalyzerResult array1InnerInteger = array1.getChild("innerInteger");
        Assert.assertEquals(2, array1InnerInteger.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, array1InnerInteger.getResult(CardinalityStatistics.class).getDistinctCount());

        // /array[0]/stringInteger analysis
        FieldAnalyzerResult array1InnerString = array1.getChild("innerString");
        Assert.assertEquals(2, array1InnerString.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, array1InnerString.getResult(CardinalityStatistics.class).getDistinctCount());

        // /array[2] analysis
        RecordAnalyzerResult array2 = array.getChild("2", RecordAnalyzerResult.class);
        Assert.assertEquals(1, array2.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, array2.getResult(CardinalityStatistics.class).getDistinctCount());

        // /array[2]/innerInteger analysis
        FieldAnalyzerResult array2InnerInteger = array2.getChild("innerInteger");
        Assert.assertEquals(1, array2InnerInteger.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, array2InnerInteger.getResult(CardinalityStatistics.class).getDistinctCount());

        // /array[0]/stringInteger analysis
        FieldAnalyzerResult array2InnerString = array2.getChild("innerString");
        Assert.assertEquals(1, array2InnerString.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, array2InnerString.getResult(CardinalityStatistics.class).getDistinctCount());
    }

    @Test
    public void testArrayOfMapsOfStrings() {
        Schema schema = SchemaBuilder.record("test").fields().name("array").type().array().items().map().values().stringType()
                .noDefault().endRecord();

        Map<Utf8, String> map1 = new HashMap<>();
        map1.put(new Utf8("key1"), "value1");
        map1.put(new Utf8("key2"), "value2");
        map1.put(new Utf8("key3"), "value3");

        Map<Utf8, String> map2 = new HashMap<>();
        map2.put(new Utf8("key1"), "value4");
        map2.put(new Utf8("key2"), "value5");
        map2.put(new Utf8("key3"), "value6");

        Map<Utf8, String> map3 = new HashMap<>();
        map3.put(new Utf8("key1"), "value7");
        map3.put(new Utf8("key2"), "value8");
        map3.put(new Utf8("key3"), "value9");

        IndexedRecord record1 = new GenericRecordBuilder(schema).set("array", Arrays.asList(map1, map2)).build();

        IndexedRecord record2 = new GenericRecordBuilder(schema).set("array", Collections.singletonList(map3)).build();

        RecordAnalyzer analyzer = RecordAnalyzer.create();

        analyzer.analyze(record1);
        analyzer.analyze(record2);

        RecordAnalyzerResult root = analyzer.getResult();

        ArrayAnalyzerResult array = root.getChild("array", ArrayAnalyzerResult.class);
        Assert.assertEquals(2, array.getSizeResult(SummaryStatistics.class).getMax(), 0);
        Assert.assertEquals(1, array.getSizeResult(SummaryStatistics.class).getMin(), 0);
        Assert.assertEquals(1.5, array.getSizeResult(SummaryStatistics.class).getMean(), 0);
    }

    @Test
    public void testMapOfStrings() {
        Schema schema = SchemaBuilder.record("test").fields().name("map").type().map().values().stringType().noDefault()
                .endRecord();

        Map<Utf8, String> map1 = new HashMap<>();
        map1.put(new Utf8("key1"), "value1");
        map1.put(new Utf8("key2"), "value2");
        map1.put(new Utf8("key3"), "value3");

        IndexedRecord record1 = new GenericRecordBuilder(schema).set("map", map1).build();

        Map<Utf8, String> map2 = new HashMap<>();
        map2.put(new Utf8("key1"), "value4");
        map2.put(new Utf8("key5"), "value5");

        IndexedRecord record2 = new GenericRecordBuilder(schema).set("map", map2).build();

        Map<Utf8, String> map3 = new HashMap<>();
        map3.put(new Utf8("key1"), "value1");
        map3.put(new Utf8("key5"), "value5");
        map3.put(new Utf8("key6"), "value6");
        map3.put(new Utf8("superKey7"), "superValue7");

        IndexedRecord record3 = new GenericRecordBuilder(schema).set("map", map3).build();

        RecordAnalyzer analyzer = RecordAnalyzer.create();

        analyzer.analyze(record1);
        analyzer.analyze(record2);
        analyzer.analyze(record3);

        RecordAnalyzerResult root = analyzer.getResult();

        // map analysis
        MapAnalyzerResult map = root.getChild("map", MapAnalyzerResult.class);
        Assert.assertEquals(3, map.getSizeResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(3, map.getSizeResult(CardinalityStatistics.class).getDistinctCount());

        Assert.assertEquals(8, map.getResult(PatternFrequencyStatistics.class).getFrequency("aaaaa9"));
        Assert.assertEquals(1, map.getResult(PatternFrequencyStatistics.class).getFrequency("aaaaaAaaaa9"));

        // map size analysis
        Assert.assertEquals(4, map.getSizeResult(SummaryStatistics.class).getMax(), 0);
        Assert.assertEquals(2, map.getSizeResult(SummaryStatistics.class).getMin(), 0);
        Assert.assertEquals(3, map.getSizeResult(SummaryStatistics.class).getMean(), 0);

        // map keys analysis
        Assert.assertEquals(9, map.getKeysResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(6, map.getKeysResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(3, map.getKeysResult(CardinalityStatistics.class).getDuplicateCount());
        Assert.assertEquals(8, map.getKeysResult(PatternFrequencyStatistics.class).getFrequency("aaa9"));
        Assert.assertEquals(1, map.getKeysResult(PatternFrequencyStatistics.class).getFrequency("aaaaaAaa9"));

        // map values by key analysis
        FieldAnalyzerResult key1 = map.getChild("key1");
        Assert.assertEquals(3, key1.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(3, key1.getResult(PatternFrequencyStatistics.class).getFrequency("aaaaa9"));

        FieldAnalyzerResult key2 = map.getChild("key2");
        Assert.assertEquals(1, key2.getResult(CardinalityStatistics.class).getCount());

        FieldAnalyzerResult key3 = map.getChild("key3");
        Assert.assertEquals(1, key3.getResult(CardinalityStatistics.class).getCount());

        FieldAnalyzerResult key5 = map.getChild("key5");
        Assert.assertEquals(2, key5.getResult(CardinalityStatistics.class).getCount());
    }

    @Test
    public void testMapOfRecord() {
        Schema innerSchema = SchemaBuilder.record("inner").fields().name("innerInteger").type().intType().noDefault()
                .name("innerString").type().stringType().noDefault().endRecord();

        Schema schema = SchemaBuilder.record("test").fields().name("map").type().map().values().type(innerSchema).noDefault()
                .endRecord();

        Map<Utf8, IndexedRecord> map1 = new HashMap<>();
        map1.put(new Utf8("key1"),
                new GenericRecordBuilder(innerSchema).set("innerInteger", 1).set("innerString", "string1").build());
        map1.put(new Utf8("key2"),
                new GenericRecordBuilder(innerSchema).set("innerInteger", 2).set("innerString", "string2").build());

        IndexedRecord record1 = new GenericRecordBuilder(schema).set("map", map1).build();

        Map<Utf8, IndexedRecord> map2 = new HashMap<>();
        map2.put(new Utf8("key1"),
                new GenericRecordBuilder(innerSchema).set("innerInteger", 3).set("innerString", "string3").build());
        map2.put(new Utf8("key4"),
                new GenericRecordBuilder(innerSchema).set("innerInteger", 4).set("innerString", "string4").build());
        map2.put(new Utf8("key5"),
                new GenericRecordBuilder(innerSchema).set("innerInteger", 5).set("innerString", "string5").build());

        IndexedRecord record2 = new GenericRecordBuilder(schema).set("map", map2).build();

        RecordAnalyzer analyzer = RecordAnalyzer.create();

        analyzer.analyze(record1);
        analyzer.analyze(record2);

        RecordAnalyzerResult root = analyzer.getResult();

        // map analysis
        MapAnalyzerResult map = root.getChild("map", MapAnalyzerResult.class);
        Assert.assertEquals(2, map.getSizeResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, map.getSizeResult(CardinalityStatistics.class).getDistinctCount());

        // map size analysis
        Assert.assertEquals(3, map.getSizeResult(SummaryStatistics.class).getMax(), 0);
        Assert.assertEquals(2, map.getSizeResult(SummaryStatistics.class).getMin(), 0);
        Assert.assertEquals(2.5, map.getSizeResult(SummaryStatistics.class).getMean(), 0);

        // map keys analysis
        Assert.assertEquals(5, map.getKeysResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(4, map.getKeysResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(1, map.getKeysResult(CardinalityStatistics.class).getDuplicateCount());
        Assert.assertEquals(5, map.getKeysResult(PatternFrequencyStatistics.class).getFrequency("aaa9"));

        // map values by key analysis
        RecordAnalyzerResult key1 = map.getChild("key1", RecordAnalyzerResult.class);
        Assert.assertEquals(2, key1.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(2, key1.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(3, key1.getChild("innerInteger").getResult(SummaryStatistics.class).getMax(), 0);
        Assert.assertEquals(1, key1.getChild("innerInteger").getResult(SummaryStatistics.class).getMin(), 0);
        Assert.assertEquals(2, key1.getChild("innerInteger").getResult(SummaryStatistics.class).getMean(), 0);
        Assert.assertEquals(2, key1.getChild("innerString").getResult(PatternFrequencyStatistics.class).getFrequency("aaaaaa9"));

        RecordAnalyzerResult key2 = map.getChild("key2", RecordAnalyzerResult.class);
        Assert.assertEquals(1, key2.getResult(CardinalityStatistics.class).getCount());
        Assert.assertEquals(1, key2.getResult(CardinalityStatistics.class).getDistinctCount());
        Assert.assertEquals(2, key2.getChild("innerInteger").getResult(SummaryStatistics.class).getMax(), 0);
        Assert.assertEquals(2, key2.getChild("innerInteger").getResult(SummaryStatistics.class).getMin(), 0);
        Assert.assertEquals(2, key2.getChild("innerInteger").getResult(SummaryStatistics.class).getMean(), 0);
        Assert.assertEquals(1, key2.getChild("innerString").getResult(PatternFrequencyStatistics.class).getFrequency("aaaaaa9"));

    }
}
