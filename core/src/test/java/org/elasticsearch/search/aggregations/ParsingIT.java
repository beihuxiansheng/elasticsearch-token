begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
package|;
end_package

begin_comment
comment|// NORELEASE move these tests to unit tests when aggs refactoring is done
end_comment

begin_comment
comment|//    @Test(expected=SearchPhaseExecutionException.class)
end_comment

begin_comment
comment|//    public void testTwoTypes() throws Exception {
end_comment

begin_comment
comment|//        createIndex("idx");
end_comment

begin_comment
comment|//        ensureGreen();
end_comment

begin_comment
comment|//        client().prepareSearch("idx").setAggregations(JsonXContent.contentBuilder()
end_comment

begin_comment
comment|//            .startObject()
end_comment

begin_comment
comment|//                .startObject("in_stock")
end_comment

begin_comment
comment|//                    .startObject("filter")
end_comment

begin_comment
comment|//                        .startObject("range")
end_comment

begin_comment
comment|//                            .startObject("stock")
end_comment

begin_comment
comment|//                                .field("gt", 0)
end_comment

begin_comment
comment|//                            .endObject()
end_comment

begin_comment
comment|//                        .endObject()
end_comment

begin_comment
comment|//                    .endObject()
end_comment

begin_comment
comment|//                    .startObject("terms")
end_comment

begin_comment
comment|//                        .field("field", "stock")
end_comment

begin_comment
comment|//                    .endObject()
end_comment

begin_comment
comment|//                .endObject()
end_comment

begin_comment
comment|//            .endObject()).execute().actionGet();
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    @Test(expected=SearchPhaseExecutionException.class)
end_comment

begin_comment
comment|//    public void testTwoAggs() throws Exception {
end_comment

begin_comment
comment|//        createIndex("idx");
end_comment

begin_comment
comment|//        ensureGreen();
end_comment

begin_comment
comment|//        client().prepareSearch("idx").setAggregations(JsonXContent.contentBuilder()
end_comment

begin_comment
comment|//            .startObject()
end_comment

begin_comment
comment|//                .startObject("by_date")
end_comment

begin_comment
comment|//                    .startObject("date_histogram")
end_comment

begin_comment
comment|//                        .field("field", "timestamp")
end_comment

begin_comment
comment|//                        .field("interval", "month")
end_comment

begin_comment
comment|//                    .endObject()
end_comment

begin_comment
comment|//                    .startObject("aggs")
end_comment

begin_comment
comment|//                        .startObject("tag_count")
end_comment

begin_comment
comment|//                            .startObject("cardinality")
end_comment

begin_comment
comment|//                                .field("field", "tag")
end_comment

begin_comment
comment|//                            .endObject()
end_comment

begin_comment
comment|//                        .endObject()
end_comment

begin_comment
comment|//                    .endObject()
end_comment

begin_comment
comment|//                    .startObject("aggs") // 2nd "aggs": illegal
end_comment

begin_comment
comment|//                        .startObject("tag_count2")
end_comment

begin_comment
comment|//                            .startObject("cardinality")
end_comment

begin_comment
comment|//                                .field("field", "tag")
end_comment

begin_comment
comment|//                            .endObject()
end_comment

begin_comment
comment|//                        .endObject()
end_comment

begin_comment
comment|//                    .endObject()
end_comment

begin_comment
comment|//            .endObject()).execute().actionGet();
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    @Test(expected=SearchPhaseExecutionException.class)
end_comment

begin_comment
comment|//    public void testInvalidAggregationName() throws Exception {
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//        Matcher matcher = Pattern.compile("[^\\[\\]>]+").matcher("");
end_comment

begin_comment
comment|//        String name;
end_comment

begin_comment
comment|//        SecureRandom rand = new SecureRandom();
end_comment

begin_comment
comment|//        int len = randomIntBetween(1, 5);
end_comment

begin_comment
comment|//        char[] word = new char[len];
end_comment

begin_comment
comment|//        while(true) {
end_comment

begin_comment
comment|//            for (int i = 0; i< word.length; i++) {
end_comment

begin_comment
comment|//                word[i] = (char) rand.nextInt(127);
end_comment

begin_comment
comment|//            }
end_comment

begin_comment
comment|//            name = String.valueOf(word);
end_comment

begin_comment
comment|//            if (!matcher.reset(name).matches()) {
end_comment

begin_comment
comment|//                break;
end_comment

begin_comment
comment|//            }
end_comment

begin_comment
comment|//        }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//        createIndex("idx");
end_comment

begin_comment
comment|//        ensureGreen();
end_comment

begin_comment
comment|//        client().prepareSearch("idx").setAggregations(JsonXContent.contentBuilder()
end_comment

begin_comment
comment|//            .startObject()
end_comment

begin_comment
comment|//                .startObject(name)
end_comment

begin_comment
comment|//                    .startObject("filter")
end_comment

begin_comment
comment|//                        .startObject("range")
end_comment

begin_comment
comment|//                            .startObject("stock")
end_comment

begin_comment
comment|//                                .field("gt", 0)
end_comment

begin_comment
comment|//                            .endObject()
end_comment

begin_comment
comment|//                        .endObject()
end_comment

begin_comment
comment|//                    .endObject()
end_comment

begin_comment
comment|//            .endObject()).execute().actionGet();
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    @Test(expected=SearchPhaseExecutionException.class)
end_comment

begin_comment
comment|//    public void testSameAggregationName() throws Exception {
end_comment

begin_comment
comment|//        createIndex("idx");
end_comment

begin_comment
comment|//        ensureGreen();
end_comment

begin_comment
comment|//        final String name = RandomStrings.randomAsciiOfLength(getRandom(), 10);
end_comment

begin_comment
comment|//        client().prepareSearch("idx").setAggregations(JsonXContent.contentBuilder()
end_comment

begin_comment
comment|//            .startObject()
end_comment

begin_comment
comment|//                .startObject(name)
end_comment

begin_comment
comment|//                    .startObject("terms")
end_comment

begin_comment
comment|//                        .field("field", "a")
end_comment

begin_comment
comment|//                    .endObject()
end_comment

begin_comment
comment|//                .endObject()
end_comment

begin_comment
comment|//                .startObject(name)
end_comment

begin_comment
comment|//                    .startObject("terms")
end_comment

begin_comment
comment|//                        .field("field", "b")
end_comment

begin_comment
comment|//                    .endObject()
end_comment

begin_comment
comment|//                .endObject()
end_comment

begin_comment
comment|//            .endObject()).execute().actionGet();
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    @Test(expected=SearchPhaseExecutionException.class)
end_comment

begin_comment
comment|//    public void testMissingName() throws Exception {
end_comment

begin_comment
comment|//        createIndex("idx");
end_comment

begin_comment
comment|//        ensureGreen();
end_comment

begin_comment
comment|//        client().prepareSearch("idx").setAggregations(JsonXContent.contentBuilder()
end_comment

begin_comment
comment|//            .startObject()
end_comment

begin_comment
comment|//                .startObject("by_date")
end_comment

begin_comment
comment|//                    .startObject("date_histogram")
end_comment

begin_comment
comment|//                        .field("field", "timestamp")
end_comment

begin_comment
comment|//                        .field("interval", "month")
end_comment

begin_comment
comment|//                    .endObject()
end_comment

begin_comment
comment|//                    .startObject("aggs")
end_comment

begin_comment
comment|//                        // the aggregation name is missing
end_comment

begin_comment
comment|//                        //.startObject("tag_count")
end_comment

begin_comment
comment|//                            .startObject("cardinality")
end_comment

begin_comment
comment|//                                .field("field", "tag")
end_comment

begin_comment
comment|//                            .endObject()
end_comment

begin_comment
comment|//                        //.endObject()
end_comment

begin_comment
comment|//                    .endObject()
end_comment

begin_comment
comment|//            .endObject()).execute().actionGet();
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    @Test(expected=SearchPhaseExecutionException.class)
end_comment

begin_comment
comment|//    public void testMissingType() throws Exception {
end_comment

begin_comment
comment|//        createIndex("idx");
end_comment

begin_comment
comment|//        ensureGreen();
end_comment

begin_comment
comment|//        client().prepareSearch("idx").setAggregations(JsonXContent.contentBuilder()
end_comment

begin_comment
comment|//            .startObject()
end_comment

begin_comment
comment|//                .startObject("by_date")
end_comment

begin_comment
comment|//                    .startObject("date_histogram")
end_comment

begin_comment
comment|//                        .field("field", "timestamp")
end_comment

begin_comment
comment|//                        .field("interval", "month")
end_comment

begin_comment
comment|//                    .endObject()
end_comment

begin_comment
comment|//                    .startObject("aggs")
end_comment

begin_comment
comment|//                        .startObject("tag_count")
end_comment

begin_comment
comment|//                            // the aggregation type is missing
end_comment

begin_comment
comment|//                            //.startObject("cardinality")
end_comment

begin_comment
comment|//                                .field("field", "tag")
end_comment

begin_comment
comment|//                            //.endObject()
end_comment

begin_comment
comment|//                        .endObject()
end_comment

begin_comment
comment|//                    .endObject()
end_comment

begin_comment
comment|//            .endObject()).execute().actionGet();
end_comment

begin_comment
comment|//    }
end_comment

end_unit

