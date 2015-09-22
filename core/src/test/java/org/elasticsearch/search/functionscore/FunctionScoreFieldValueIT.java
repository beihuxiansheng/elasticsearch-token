begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.functionscore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|functionscore
package|;
end_package

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
name|action
operator|.
name|search
operator|.
name|SearchResponse
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
name|bytes
operator|.
name|BytesArray
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
name|lucene
operator|.
name|search
operator|.
name|function
operator|.
name|FieldValueFactorFunction
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|functionscore
operator|.
name|ScoreFunctionBuilders
operator|.
name|fieldValueFactorFunction
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Tests for the {@code field_value_factor} function in a function_score query.  */
end_comment

begin_class
DECL|class|FunctionScoreFieldValueIT
specifier|public
class|class
name|FunctionScoreFieldValueIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Test
DECL|method|testFieldValueFactor
specifier|public
name|void
name|testFieldValueFactor
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type1"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"test"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|randomFrom
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"short"
block|,
literal|"float"
block|,
literal|"long"
block|,
literal|"integer"
block|,
literal|"double"
block|}
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"body"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"test"
argument_list|,
literal|5
argument_list|,
literal|"body"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"test"
argument_list|,
literal|17
argument_list|,
literal|"body"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"body"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
comment|// document 2 scores higher because 17> 5
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setExplain
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|setQuery
argument_list|(
name|functionScoreQuery
argument_list|(
name|simpleQueryStringQuery
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|fieldValueFactorFunction
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertOrderedSearchHits
argument_list|(
name|response
argument_list|,
literal|"2"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// try again, but this time explicitly use the do-nothing modifier
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setExplain
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|setQuery
argument_list|(
name|functionScoreQuery
argument_list|(
name|simpleQueryStringQuery
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|fieldValueFactorFunction
argument_list|(
literal|"test"
argument_list|)
operator|.
name|modifier
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|NONE
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertOrderedSearchHits
argument_list|(
name|response
argument_list|,
literal|"2"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// document 1 scores higher because 1/5> 1/17
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setExplain
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|setQuery
argument_list|(
name|functionScoreQuery
argument_list|(
name|simpleQueryStringQuery
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|fieldValueFactorFunction
argument_list|(
literal|"test"
argument_list|)
operator|.
name|modifier
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|RECIPROCAL
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertOrderedSearchHits
argument_list|(
name|response
argument_list|,
literal|"1"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
comment|// doc 3 doesn't have a "test" field, so an exception will be thrown
try|try
block|{
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setExplain
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|setQuery
argument_list|(
name|functionScoreQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|,
name|fieldValueFactorFunction
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertFailures
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SearchPhaseExecutionException
name|e
parameter_list|)
block|{
comment|// We are expecting an exception, because 3 has no field
block|}
comment|// doc 3 doesn't have a "test" field but we're defaulting it to 100 so it should be last
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setExplain
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|setQuery
argument_list|(
name|functionScoreQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|,
name|fieldValueFactorFunction
argument_list|(
literal|"test"
argument_list|)
operator|.
name|modifier
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|RECIPROCAL
argument_list|)
operator|.
name|missing
argument_list|(
literal|100
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertOrderedSearchHits
argument_list|(
name|response
argument_list|,
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
comment|// field is not mapped but we're defaulting it to 100 so all documents should have the same score
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setExplain
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|setQuery
argument_list|(
name|functionScoreQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|,
name|fieldValueFactorFunction
argument_list|(
literal|"notmapped"
argument_list|)
operator|.
name|modifier
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|RECIPROCAL
argument_list|)
operator|.
name|missing
argument_list|(
literal|100
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|score
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|2
argument_list|)
operator|.
name|score
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// n divided by 0 is infinity, which should provoke an exception.
try|try
block|{
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setExplain
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|setQuery
argument_list|(
name|functionScoreQuery
argument_list|(
name|simpleQueryStringQuery
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|fieldValueFactorFunction
argument_list|(
literal|"test"
argument_list|)
operator|.
name|modifier
argument_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|RECIPROCAL
argument_list|)
operator|.
name|factor
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertFailures
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SearchPhaseExecutionException
name|e
parameter_list|)
block|{
comment|// This is fine, the query will throw an exception if executed
comment|// locally, instead of just having failures
block|}
comment|//        // don't permit an array of factors
comment|//        try {
comment|//          String querySource = "{" +
comment|//            "\"query\": {" +
comment|//            "  \"function_score\": {" +
comment|//            "    \"query\": {" +
comment|//            "      \"match\": {\"name\": \"foo\"}" +
comment|//            "      }," +
comment|//            "      \"functions\": [" +
comment|//            "        {" +
comment|//            "          \"field_value_factor\": {" +
comment|//            "            \"field\": \"test\"," +
comment|//            "            \"factor\": [1.2,2]" +
comment|//            "          }" +
comment|//            "        }" +
comment|//            "      ]" +
comment|//            "    }" +
comment|//            "  }" +
comment|//            "}";
comment|//          response = client().prepareSearch("test")
comment|//          .setSource(new BytesArray(querySource))
comment|//                  .get();
comment|//          assertFailures(response);
comment|//        } catch (SearchPhaseExecutionException e) {
comment|//          // This is fine, the query will throw an exception if executed
comment|//          // locally, instead of just having failures
comment|//        } NOCOMMIT fix this
block|}
block|}
end_class

end_unit

