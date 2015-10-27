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
name|hppc
operator|.
name|IntIntHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntIntMap
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
name|index
operator|.
name|IndexRequestBuilder
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
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|missing
operator|.
name|Missing
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|search
operator|.
name|aggregations
operator|.
name|AggregationBuilders
operator|.
name|missing
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
name|assertSearchResponse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MetaDataIT
specifier|public
class|class
name|MetaDataIT
extends|extends
name|ESIntegTestCase
block|{
comment|/**      * Making sure that if there are multiple aggregations, working on the same field, yet require different      * value source type, they can all still work. It used to fail as we used to cache the ValueSource by the      * field name. If the cached value source was of type "bytes" and another aggregation on the field required to see      * it as "numeric", it didn't work. Now we cache the Value Sources by a custom key (field name + ValueSource type)      * so there's no conflict there.      */
DECL|method|testMetaDataSetOnAggregationResult
specifier|public
name|void
name|testMetaDataSetOnAggregationResult
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"idx"
argument_list|)
expr_stmt|;
name|IndexRequestBuilder
index|[]
name|builders
init|=
operator|new
name|IndexRequestBuilder
index|[
name|randomInt
argument_list|(
literal|30
argument_list|)
index|]
decl_stmt|;
name|IntIntMap
name|values
init|=
operator|new
name|IntIntHashMap
argument_list|()
decl_stmt|;
name|long
name|missingValues
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|builders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
literal|"name_"
operator|+
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|missingValues
operator|++
expr_stmt|;
name|builders
index|[
name|i
index|]
operator|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|value
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|values
operator|.
name|put
argument_list|(
name|value
argument_list|,
name|values
operator|.
name|getOrDefault
argument_list|(
name|value
argument_list|,
literal|0
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|builders
index|[
name|i
index|]
operator|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
name|value
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|()
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nestedMetaData
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"nested"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|missingValueMetaData
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"numeric"
argument_list|,
literal|1.2
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"bool"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"complex"
argument_list|,
name|nestedMetaData
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|missing
argument_list|(
literal|"missing_values"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|)
operator|.
name|setMetaData
argument_list|(
name|missingValueMetaData
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Aggregations
name|aggs
init|=
name|response
operator|.
name|getAggregations
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|aggs
argument_list|)
expr_stmt|;
name|Missing
name|missing
init|=
name|aggs
operator|.
name|get
argument_list|(
literal|"missing_values"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|missing
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|missing
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|missingValues
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|returnedMetaData
init|=
name|missing
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|returnedMetaData
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|returnedMetaData
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|returnedMetaData
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.2
argument_list|,
name|returnedMetaData
operator|.
name|get
argument_list|(
literal|"numeric"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|returnedMetaData
operator|.
name|get
argument_list|(
literal|"bool"
argument_list|)
argument_list|)
expr_stmt|;
name|Object
name|nestedObject
init|=
name|returnedMetaData
operator|.
name|get
argument_list|(
literal|"complex"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|nestedObject
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nestedMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|nestedObject
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|nestedMap
operator|.
name|get
argument_list|(
literal|"nested"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

