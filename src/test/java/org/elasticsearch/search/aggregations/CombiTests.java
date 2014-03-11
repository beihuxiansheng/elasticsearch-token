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
name|IntIntMap
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
name|IntIntOpenHashMap
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
name|histogram
operator|.
name|Histogram
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
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|terms
operator|.
name|Terms
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
name|hamcrest
operator|.
name|Matchers
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
name|Collection
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
name|*
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|IsNull
operator|.
name|notNullValue
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|CombiTests
specifier|public
class|class
name|CombiTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
comment|/**      * Making sure that if there are multiple aggregations, working on the same field, yet require different      * value source type, they can all still work. It used to fail as we used to cache the ValueSource by the      * field name. If the cached value source was of type "bytes" and another aggregation on the field required to see      * it as "numeric", it didn't work. Now we cache the Value Sources by a custom key (field name + ValueSource type)      * so there's no conflict there.      */
annotation|@
name|Test
DECL|method|multipleAggs_OnSameField_WithDifferentRequiredValueSourceType
specifier|public
name|void
name|multipleAggs_OnSameField_WithDifferentRequiredValueSourceType
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
name|IntIntOpenHashMap
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
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|terms
argument_list|(
literal|"values"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
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
name|Terms
name|terms
init|=
name|aggs
operator|.
name|get
argument_list|(
literal|"values"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
name|buckets
init|=
name|terms
operator|.
name|getBuckets
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|buckets
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Terms
operator|.
name|Bucket
name|bucket
range|:
name|buckets
control|)
block|{
name|values
operator|.
name|remove
argument_list|(
name|bucket
operator|.
name|getKeyAsNumber
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|values
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Some top aggs (eg. date_/histogram) that are executed on unmapped fields, will generate an estimate count of buckets - zero.      * when the sub aggregator is then created, it will take this estimation into account. This used to cause      * and an ArrayIndexOutOfBoundsException...      */
annotation|@
name|Test
DECL|method|subAggregationForTopAggregationOnUnmappedField
specifier|public
name|void
name|subAggregationForTopAggregationOnUnmappedField
parameter_list|()
throws|throws
name|Exception
block|{
name|prepareCreate
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"name"
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
name|startObject
argument_list|(
literal|"value"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"integer"
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
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|ensureSearchable
argument_list|(
literal|"idx"
argument_list|)
expr_stmt|;
name|SearchResponse
name|searchResponse
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
name|histogram
argument_list|(
literal|"values"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value1"
argument_list|)
operator|.
name|interval
argument_list|(
literal|1
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|terms
argument_list|(
literal|"names"
argument_list|)
operator|.
name|field
argument_list|(
literal|"name"
argument_list|)
argument_list|)
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
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|Histogram
name|values
init|=
name|searchResponse
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"values"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|values
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|values
operator|.
name|getBuckets
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

