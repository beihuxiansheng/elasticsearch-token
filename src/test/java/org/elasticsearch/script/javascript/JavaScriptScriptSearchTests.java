begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.javascript
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|javascript
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
name|SearchResponse
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
name|SearchType
import|;
end_import

begin_import
import|import
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
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
operator|.
name|SortOrder
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
name|ElasticsearchIntegrationTest
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
operator|.
name|searchRequest
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
name|FilterBuilders
operator|.
name|scriptFilter
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
name|search
operator|.
name|builder
operator|.
name|SearchSourceBuilder
operator|.
name|searchSource
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|JavaScriptScriptSearchTests
specifier|public
class|class
name|JavaScriptScriptSearchTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testJavaScriptFilter
specifier|public
name|void
name|testJavaScriptFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value beck"
argument_list|)
operator|.
name|field
argument_list|(
literal|"num1"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|index
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value beck"
argument_list|)
operator|.
name|field
argument_list|(
literal|"num1"
argument_list|,
literal|2.0f
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|index
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"3"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value beck"
argument_list|)
operator|.
name|field
argument_list|(
literal|"num1"
argument_list|,
literal|3.0f
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> running doc['num1'].value> 1"
argument_list|)
expr_stmt|;
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|filteredQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|,
name|scriptFilter
argument_list|(
literal|"doc['num1'].value> 1"
argument_list|)
operator|.
name|lang
argument_list|(
literal|"js"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"num1"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
operator|.
name|addScriptField
argument_list|(
literal|"sNum1"
argument_list|,
literal|"js"
argument_list|,
literal|"doc['num1'].value"
argument_list|,
literal|null
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
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
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Double
operator|)
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
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sNum1"
argument_list|)
operator|.
name|values
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Double
operator|)
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sNum1"
argument_list|)
operator|.
name|values
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|3.0
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> running doc['num1'].value> param1"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|filteredQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|,
name|scriptFilter
argument_list|(
literal|"doc['num1'].value> param1"
argument_list|)
operator|.
name|lang
argument_list|(
literal|"js"
argument_list|)
operator|.
name|addParam
argument_list|(
literal|"param1"
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"num1"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
operator|.
name|addScriptField
argument_list|(
literal|"sNum1"
argument_list|,
literal|"js"
argument_list|,
literal|"doc['num1'].value"
argument_list|,
literal|null
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
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
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Double
operator|)
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
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sNum1"
argument_list|)
operator|.
name|values
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|3.0
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> running doc['num1'].value> param1"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|filteredQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|,
name|scriptFilter
argument_list|(
literal|"doc['num1'].value> param1"
argument_list|)
operator|.
name|lang
argument_list|(
literal|"js"
argument_list|)
operator|.
name|addParam
argument_list|(
literal|"param1"
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addSort
argument_list|(
literal|"num1"
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
operator|.
name|addScriptField
argument_list|(
literal|"sNum1"
argument_list|,
literal|"js"
argument_list|,
literal|"doc['num1'].value"
argument_list|,
literal|null
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
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
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Double
operator|)
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
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sNum1"
argument_list|)
operator|.
name|values
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Double
operator|)
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sNum1"
argument_list|)
operator|.
name|values
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
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
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Double
operator|)
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
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"sNum1"
argument_list|)
operator|.
name|values
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|3.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testScriptFieldUsingSource
specifier|public
name|void
name|testScriptFieldUsingSource
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"obj1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"something"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"obj2"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"arr2"
argument_list|)
operator|.
name|value
argument_list|(
literal|"arr_value1"
argument_list|)
operator|.
name|value
argument_list|(
literal|"arr_value2"
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addScriptField
argument_list|(
literal|"s_obj1"
argument_list|,
literal|"js"
argument_list|,
literal|"_source.obj1"
argument_list|,
literal|null
argument_list|)
operator|.
name|addScriptField
argument_list|(
literal|"s_obj1_test"
argument_list|,
literal|"js"
argument_list|,
literal|"_source.obj1.test"
argument_list|,
literal|null
argument_list|)
operator|.
name|addScriptField
argument_list|(
literal|"s_obj2"
argument_list|,
literal|"js"
argument_list|,
literal|"_source.obj2"
argument_list|,
literal|null
argument_list|)
operator|.
name|addScriptField
argument_list|(
literal|"s_obj2_arr2"
argument_list|,
literal|"js"
argument_list|,
literal|"_source.obj2.arr2"
argument_list|,
literal|null
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sObj1
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
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
name|field
argument_list|(
literal|"s_obj1"
argument_list|)
operator|.
name|value
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|sObj1
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"something"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
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
name|field
argument_list|(
literal|"s_obj1_test"
argument_list|)
operator|.
name|value
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"something"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sObj2
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
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
name|field
argument_list|(
literal|"s_obj2"
argument_list|)
operator|.
name|value
argument_list|()
decl_stmt|;
name|List
name|sObj2Arr2
init|=
operator|(
name|List
operator|)
name|sObj2
operator|.
name|get
argument_list|(
literal|"arr2"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sObj2Arr2
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sObj2Arr2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"arr_value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sObj2Arr2
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"arr_value2"
argument_list|)
argument_list|)
expr_stmt|;
name|sObj2Arr2
operator|=
operator|(
name|List
operator|)
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
name|field
argument_list|(
literal|"s_obj2_arr2"
argument_list|)
operator|.
name|value
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|sObj2Arr2
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sObj2Arr2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"arr_value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sObj2Arr2
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"arr_value2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustomScriptBoost
specifier|public
name|void
name|testCustomScriptBoost
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value beck"
argument_list|)
operator|.
name|field
argument_list|(
literal|"num1"
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value beck"
argument_list|)
operator|.
name|field
argument_list|(
literal|"num1"
argument_list|,
literal|2.0f
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--- QUERY_THEN_FETCH"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> running doc['num1'].value"
argument_list|)
expr_stmt|;
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|()
operator|.
name|searchType
argument_list|(
name|SearchType
operator|.
name|QUERY_THEN_FETCH
argument_list|)
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|query
argument_list|(
name|functionScoreQuery
argument_list|(
name|termQuery
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|ScoreFunctionBuilders
operator|.
name|scriptFunction
argument_list|(
literal|"doc['num1'].value"
argument_list|)
operator|.
name|lang
argument_list|(
literal|"js"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"Failures "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|response
operator|.
name|getShardFailures
argument_list|()
argument_list|)
argument_list|,
name|response
operator|.
name|getShardFailures
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> Hit[0] {} Explanation {}"
argument_list|,
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
name|id
argument_list|()
argument_list|,
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
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> Hit[1] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
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
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> running -doc['num1'].value"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|()
operator|.
name|searchType
argument_list|(
name|SearchType
operator|.
name|QUERY_THEN_FETCH
argument_list|)
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|query
argument_list|(
name|functionScoreQuery
argument_list|(
name|termQuery
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|ScoreFunctionBuilders
operator|.
name|scriptFunction
argument_list|(
literal|"-doc['num1'].value"
argument_list|)
operator|.
name|lang
argument_list|(
literal|"js"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"Failures "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|response
operator|.
name|getShardFailures
argument_list|()
argument_list|)
argument_list|,
name|response
operator|.
name|getShardFailures
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> Hit[0] {} Explanation {}"
argument_list|,
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
name|id
argument_list|()
argument_list|,
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
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> Hit[1] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
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
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> running pow(doc['num1'].value, 2)"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|()
operator|.
name|searchType
argument_list|(
name|SearchType
operator|.
name|QUERY_THEN_FETCH
argument_list|)
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|query
argument_list|(
name|functionScoreQuery
argument_list|(
name|termQuery
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|ScoreFunctionBuilders
operator|.
name|scriptFunction
argument_list|(
literal|"Math.pow(doc['num1'].value, 2)"
argument_list|)
operator|.
name|lang
argument_list|(
literal|"js"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"Failures "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|response
operator|.
name|getShardFailures
argument_list|()
argument_list|)
argument_list|,
name|response
operator|.
name|getShardFailures
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> Hit[0] {} Explanation {}"
argument_list|,
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
name|id
argument_list|()
argument_list|,
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
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> Hit[1] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
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
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> running max(doc['num1'].value, 1)"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|()
operator|.
name|searchType
argument_list|(
name|SearchType
operator|.
name|QUERY_THEN_FETCH
argument_list|)
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|query
argument_list|(
name|functionScoreQuery
argument_list|(
name|termQuery
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|ScoreFunctionBuilders
operator|.
name|scriptFunction
argument_list|(
literal|"Math.max(doc['num1'].value, 1)"
argument_list|)
operator|.
name|lang
argument_list|(
literal|"js"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"Failures "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|response
operator|.
name|getShardFailures
argument_list|()
argument_list|)
argument_list|,
name|response
operator|.
name|getShardFailures
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> Hit[0] {} Explanation {}"
argument_list|,
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
name|id
argument_list|()
argument_list|,
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
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> Hit[1] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
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
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> running doc['num1'].value * _score"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|()
operator|.
name|searchType
argument_list|(
name|SearchType
operator|.
name|QUERY_THEN_FETCH
argument_list|)
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|query
argument_list|(
name|functionScoreQuery
argument_list|(
name|termQuery
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|ScoreFunctionBuilders
operator|.
name|scriptFunction
argument_list|(
literal|"doc['num1'].value * _score"
argument_list|)
operator|.
name|lang
argument_list|(
literal|"js"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"Failures "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|response
operator|.
name|getShardFailures
argument_list|()
argument_list|)
argument_list|,
name|response
operator|.
name|getShardFailures
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> Hit[0] {} Explanation {}"
argument_list|,
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
name|id
argument_list|()
argument_list|,
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
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> Hit[1] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
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
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> running param1 * param2 * _score"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|()
operator|.
name|searchType
argument_list|(
name|SearchType
operator|.
name|QUERY_THEN_FETCH
argument_list|)
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|query
argument_list|(
name|functionScoreQuery
argument_list|(
name|termQuery
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|ScoreFunctionBuilders
operator|.
name|scriptFunction
argument_list|(
literal|"param1 * param2 * _score"
argument_list|)
operator|.
name|param
argument_list|(
literal|"param1"
argument_list|,
literal|2
argument_list|)
operator|.
name|param
argument_list|(
literal|"param2"
argument_list|,
literal|2
argument_list|)
operator|.
name|lang
argument_list|(
literal|"js"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"Failures "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|response
operator|.
name|getShardFailures
argument_list|()
argument_list|)
argument_list|,
name|response
operator|.
name|getShardFailures
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> Hit[0] {} Explanation {}"
argument_list|,
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
name|id
argument_list|()
argument_list|,
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
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|" --> Hit[1] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|id
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

