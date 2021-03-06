begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|pipeline
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
name|ExtendedBounds
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
name|histogram
operator|.
name|Histogram
operator|.
name|Bucket
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
name|metrics
operator|.
name|sum
operator|.
name|Sum
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
name|ArrayList
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
name|rangeQuery
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
name|histogram
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
name|sum
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
name|pipeline
operator|.
name|PipelineAggregatorBuilders
operator|.
name|cumulativeSum
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
name|assertAcked
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
name|Matchers
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
name|core
operator|.
name|IsNull
operator|.
name|notNullValue
import|;
end_import

begin_class
annotation|@
name|ESIntegTestCase
operator|.
name|SuiteScopeTestCase
DECL|class|CumulativeSumIT
specifier|public
class|class
name|CumulativeSumIT
extends|extends
name|ESIntegTestCase
block|{
DECL|field|SINGLE_VALUED_FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SINGLE_VALUED_FIELD_NAME
init|=
literal|"l_value"
decl_stmt|;
DECL|field|numDocs
specifier|static
name|int
name|numDocs
decl_stmt|;
DECL|field|interval
specifier|static
name|int
name|interval
decl_stmt|;
DECL|field|minRandomValue
specifier|static
name|int
name|minRandomValue
decl_stmt|;
DECL|field|maxRandomValue
specifier|static
name|int
name|maxRandomValue
decl_stmt|;
DECL|field|numValueBuckets
specifier|static
name|int
name|numValueBuckets
decl_stmt|;
DECL|field|valueCounts
specifier|static
name|long
index|[]
name|valueCounts
decl_stmt|;
annotation|@
name|Override
DECL|method|setupSuiteScopeCluster
specifier|public
name|void
name|setupSuiteScopeCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"idx"
argument_list|)
expr_stmt|;
name|createIndex
argument_list|(
literal|"idx_unmapped"
argument_list|)
expr_stmt|;
name|numDocs
operator|=
name|randomIntBetween
argument_list|(
literal|6
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|interval
operator|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|minRandomValue
operator|=
literal|0
expr_stmt|;
name|maxRandomValue
operator|=
literal|20
expr_stmt|;
name|numValueBuckets
operator|=
operator|(
operator|(
name|maxRandomValue
operator|-
name|minRandomValue
operator|)
operator|/
name|interval
operator|)
operator|+
literal|1
expr_stmt|;
name|valueCounts
operator|=
operator|new
name|long
index|[
name|numValueBuckets
index|]
expr_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|builders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|int
name|fieldValue
init|=
name|randomIntBetween
argument_list|(
name|minRandomValue
argument_list|,
name|maxRandomValue
argument_list|)
decl_stmt|;
name|builders
operator|.
name|add
argument_list|(
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
name|SINGLE_VALUED_FIELD_NAME
argument_list|,
name|fieldValue
argument_list|)
operator|.
name|field
argument_list|(
literal|"tag"
argument_list|,
literal|"tag"
operator|+
operator|(
name|i
operator|%
name|interval
operator|)
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bucket
init|=
operator|(
name|fieldValue
operator|/
name|interval
operator|)
decl_stmt|;
comment|// + (fieldValue< 0 ? -1 : 0) - (minRandomValue / interval - 1);
name|valueCounts
index|[
name|bucket
index|]
operator|++
expr_stmt|;
block|}
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"empty_bucket_idx"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
name|SINGLE_VALUED_FIELD_NAME
argument_list|,
literal|"type=integer"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|builders
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"empty_bucket_idx"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|i
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
name|SINGLE_VALUED_FIELD_NAME
argument_list|,
name|i
operator|*
literal|2
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
block|}
DECL|method|testDocCount
specifier|public
name|void
name|testDocCount
parameter_list|()
throws|throws
name|Exception
block|{
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
name|histogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
name|SINGLE_VALUED_FIELD_NAME
argument_list|)
operator|.
name|interval
argument_list|(
name|interval
argument_list|)
operator|.
name|extendedBounds
argument_list|(
name|minRandomValue
argument_list|,
name|maxRandomValue
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|cumulativeSum
argument_list|(
literal|"cumulative_sum"
argument_list|,
literal|"_count"
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
name|assertSearchResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Histogram
name|histo
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"histo"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|histo
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|histo
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"histo"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|Bucket
argument_list|>
name|buckets
init|=
name|histo
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
name|numValueBuckets
argument_list|)
argument_list|)
expr_stmt|;
name|double
name|sum
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
name|numValueBuckets
condition|;
operator|++
name|i
control|)
block|{
name|Histogram
operator|.
name|Bucket
name|bucket
init|=
name|buckets
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|bucket
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|bucket
operator|.
name|getKey
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|i
operator|*
name|interval
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|valueCounts
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|sum
operator|+=
name|bucket
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|InternalSimpleValue
name|cumulativeSumValue
init|=
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"cumulative_sum"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cumulativeSumValue
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cumulativeSumValue
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"cumulative_sum"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cumulativeSumValue
operator|.
name|value
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|sum
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMetric
specifier|public
name|void
name|testMetric
parameter_list|()
throws|throws
name|Exception
block|{
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
name|histogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
name|SINGLE_VALUED_FIELD_NAME
argument_list|)
operator|.
name|interval
argument_list|(
name|interval
argument_list|)
operator|.
name|extendedBounds
argument_list|(
name|minRandomValue
argument_list|,
name|maxRandomValue
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|sum
argument_list|(
literal|"sum"
argument_list|)
operator|.
name|field
argument_list|(
name|SINGLE_VALUED_FIELD_NAME
argument_list|)
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|cumulativeSum
argument_list|(
literal|"cumulative_sum"
argument_list|,
literal|"sum"
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
name|assertSearchResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Histogram
name|histo
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"histo"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|histo
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|histo
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"histo"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|Bucket
argument_list|>
name|buckets
init|=
name|histo
operator|.
name|getBuckets
argument_list|()
decl_stmt|;
name|double
name|bucketSum
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
name|buckets
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|Bucket
name|bucket
init|=
name|buckets
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|bucket
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|bucket
operator|.
name|getKey
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|i
operator|*
name|interval
argument_list|)
argument_list|)
expr_stmt|;
name|Sum
name|sum
init|=
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"sum"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sum
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|bucketSum
operator|+=
name|sum
operator|.
name|value
argument_list|()
expr_stmt|;
name|InternalSimpleValue
name|sumBucketValue
init|=
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"cumulative_sum"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sumBucketValue
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sumBucketValue
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"cumulative_sum"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sumBucketValue
operator|.
name|value
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|bucketSum
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNoBuckets
specifier|public
name|void
name|testNoBuckets
parameter_list|()
throws|throws
name|Exception
block|{
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
name|setQuery
argument_list|(
name|rangeQuery
argument_list|(
name|SINGLE_VALUED_FIELD_NAME
argument_list|)
operator|.
name|lt
argument_list|(
name|minRandomValue
argument_list|)
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|histogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
name|SINGLE_VALUED_FIELD_NAME
argument_list|)
operator|.
name|interval
argument_list|(
name|interval
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|sum
argument_list|(
literal|"sum"
argument_list|)
operator|.
name|field
argument_list|(
name|SINGLE_VALUED_FIELD_NAME
argument_list|)
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|cumulativeSum
argument_list|(
literal|"cumulative_sum"
argument_list|,
literal|"sum"
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
name|assertSearchResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Histogram
name|histo
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"histo"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|histo
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|histo
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"histo"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|Bucket
argument_list|>
name|buckets
init|=
name|histo
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
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

