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

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomStrings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchPhaseExecutionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESIntegTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
DECL|class|ParsingIT
specifier|public
class|class
name|ParsingIT
extends|extends
name|ESIntegTestCase
block|{
comment|// NORELEASE move these tests to unit tests when aggs refactoring is done
comment|//    @Test(expected=SearchPhaseExecutionException.class)
comment|//    public void testTwoTypes() throws Exception {
comment|//        createIndex("idx");
comment|//        ensureGreen();
comment|//        client().prepareSearch("idx").setAggregations(JsonXContent.contentBuilder()
comment|//            .startObject()
comment|//                .startObject("in_stock")
comment|//                    .startObject("filter")
comment|//                        .startObject("range")
comment|//                            .startObject("stock")
comment|//                                .field("gt", 0)
comment|//                            .endObject()
comment|//                        .endObject()
comment|//                    .endObject()
comment|//                    .startObject("terms")
comment|//                        .field("field", "stock")
comment|//                    .endObject()
comment|//                .endObject()
comment|//            .endObject()).execute().actionGet();
comment|//    }
comment|//
comment|//    @Test(expected=SearchPhaseExecutionException.class)
comment|//    public void testTwoAggs() throws Exception {
comment|//        createIndex("idx");
comment|//        ensureGreen();
comment|//        client().prepareSearch("idx").setAggregations(JsonXContent.contentBuilder()
comment|//            .startObject()
comment|//                .startObject("by_date")
comment|//                    .startObject("date_histogram")
comment|//                        .field("field", "timestamp")
comment|//                        .field("interval", "month")
comment|//                    .endObject()
comment|//                    .startObject("aggs")
comment|//                        .startObject("tag_count")
comment|//                            .startObject("cardinality")
comment|//                                .field("field", "tag")
comment|//                            .endObject()
comment|//                        .endObject()
comment|//                    .endObject()
comment|//                    .startObject("aggs") // 2nd "aggs": illegal
comment|//                        .startObject("tag_count2")
comment|//                            .startObject("cardinality")
comment|//                                .field("field", "tag")
comment|//                            .endObject()
comment|//                        .endObject()
comment|//                    .endObject()
comment|//            .endObject()).execute().actionGet();
comment|//    }
comment|//
comment|//    @Test(expected=SearchPhaseExecutionException.class)
comment|//    public void testInvalidAggregationName() throws Exception {
comment|//
comment|//        Matcher matcher = Pattern.compile("[^\\[\\]>]+").matcher("");
comment|//        String name;
comment|//        SecureRandom rand = new SecureRandom();
comment|//        int len = randomIntBetween(1, 5);
comment|//        char[] word = new char[len];
comment|//        while(true) {
comment|//            for (int i = 0; i< word.length; i++) {
comment|//                word[i] = (char) rand.nextInt(127);
comment|//            }
comment|//            name = String.valueOf(word);
comment|//            if (!matcher.reset(name).matches()) {
comment|//                break;
comment|//            }
comment|//        }
comment|//
comment|//        createIndex("idx");
comment|//        ensureGreen();
comment|//        client().prepareSearch("idx").setAggregations(JsonXContent.contentBuilder()
comment|//            .startObject()
comment|//                .startObject(name)
comment|//                    .startObject("filter")
comment|//                        .startObject("range")
comment|//                            .startObject("stock")
comment|//                                .field("gt", 0)
comment|//                            .endObject()
comment|//                        .endObject()
comment|//                    .endObject()
comment|//            .endObject()).execute().actionGet();
comment|//    }
comment|//
comment|//    @Test(expected=SearchPhaseExecutionException.class)
comment|//    public void testSameAggregationName() throws Exception {
comment|//        createIndex("idx");
comment|//        ensureGreen();
comment|//        final String name = RandomStrings.randomAsciiOfLength(getRandom(), 10);
comment|//        client().prepareSearch("idx").setAggregations(JsonXContent.contentBuilder()
comment|//            .startObject()
comment|//                .startObject(name)
comment|//                    .startObject("terms")
comment|//                        .field("field", "a")
comment|//                    .endObject()
comment|//                .endObject()
comment|//                .startObject(name)
comment|//                    .startObject("terms")
comment|//                        .field("field", "b")
comment|//                    .endObject()
comment|//                .endObject()
comment|//            .endObject()).execute().actionGet();
comment|//    }
comment|//
comment|//    @Test(expected=SearchPhaseExecutionException.class)
comment|//    public void testMissingName() throws Exception {
comment|//        createIndex("idx");
comment|//        ensureGreen();
comment|//        client().prepareSearch("idx").setAggregations(JsonXContent.contentBuilder()
comment|//            .startObject()
comment|//                .startObject("by_date")
comment|//                    .startObject("date_histogram")
comment|//                        .field("field", "timestamp")
comment|//                        .field("interval", "month")
comment|//                    .endObject()
comment|//                    .startObject("aggs")
comment|//                        // the aggregation name is missing
comment|//                        //.startObject("tag_count")
comment|//                            .startObject("cardinality")
comment|//                                .field("field", "tag")
comment|//                            .endObject()
comment|//                        //.endObject()
comment|//                    .endObject()
comment|//            .endObject()).execute().actionGet();
comment|//    }
comment|//
comment|//    @Test(expected=SearchPhaseExecutionException.class)
comment|//    public void testMissingType() throws Exception {
comment|//        createIndex("idx");
comment|//        ensureGreen();
comment|//        client().prepareSearch("idx").setAggregations(JsonXContent.contentBuilder()
comment|//            .startObject()
comment|//                .startObject("by_date")
comment|//                    .startObject("date_histogram")
comment|//                        .field("field", "timestamp")
comment|//                        .field("interval", "month")
comment|//                    .endObject()
comment|//                    .startObject("aggs")
comment|//                        .startObject("tag_count")
comment|//                            // the aggregation type is missing
comment|//                            //.startObject("cardinality")
comment|//                                .field("field", "tag")
comment|//                            //.endObject()
comment|//                        .endObject()
comment|//                    .endObject()
comment|//            .endObject()).execute().actionGet();
comment|//    }
block|}
end_class

end_unit

