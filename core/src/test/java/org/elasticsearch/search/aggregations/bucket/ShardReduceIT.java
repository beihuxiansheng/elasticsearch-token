begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
operator|.
name|GeoHashUtils
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
name|index
operator|.
name|query
operator|.
name|QueryBuilders
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
name|Aggregator
operator|.
name|SubAggCollectionMode
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
name|filter
operator|.
name|Filter
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
name|geogrid
operator|.
name|GeoHashGrid
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
name|global
operator|.
name|Global
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
name|DateHistogramInterval
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
name|nested
operator|.
name|Nested
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
name|range
operator|.
name|Range
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
name|ESIntegTestCase
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
name|dateHistogram
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
name|dateRange
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
name|filter
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
name|geohashGrid
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
name|global
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
name|ipRange
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
name|search
operator|.
name|aggregations
operator|.
name|AggregationBuilders
operator|.
name|nested
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
name|range
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
name|terms
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

begin_comment
comment|/**  * Tests making sure that the reduce is propagated to all aggregations in the hierarchy when executing on a single shard  * These tests are based on the date histogram in combination of min_doc_count=0. In order for the date histogram to  * compute empty buckets, its {@code reduce()} method must be called. So by adding the date histogram under other buckets,  * we can make sure that the reduce is properly propagated by checking that empty buckets were created.  */
end_comment

begin_class
annotation|@
name|ESIntegTestCase
operator|.
name|SuiteScopeTestCase
DECL|class|ShardReduceIT
specifier|public
class|class
name|ShardReduceIT
extends|extends
name|ESIntegTestCase
block|{
DECL|method|indexDoc
specifier|private
name|IndexRequestBuilder
name|indexDoc
parameter_list|(
name|String
name|date
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|Exception
block|{
return|return
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
literal|"value"
argument_list|,
name|value
argument_list|)
operator|.
name|field
argument_list|(
literal|"ip"
argument_list|,
literal|"10.0.0."
operator|+
name|value
argument_list|)
operator|.
name|field
argument_list|(
literal|"location"
argument_list|,
name|GeoHashUtils
operator|.
name|stringEncode
argument_list|(
literal|5
argument_list|,
literal|52
argument_list|,
name|GeoHashUtils
operator|.
name|PRECISION
argument_list|)
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|,
name|date
argument_list|)
operator|.
name|field
argument_list|(
literal|"term-l"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"term-d"
argument_list|,
literal|1.5
argument_list|)
operator|.
name|field
argument_list|(
literal|"term-s"
argument_list|,
literal|"term"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"nested"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|,
name|date
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
return|;
block|}
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
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"nested"
argument_list|,
literal|"type=nested"
argument_list|,
literal|"ip"
argument_list|,
literal|"type=ip"
argument_list|,
literal|"location"
argument_list|,
literal|"type=geo_point"
argument_list|,
literal|"term-s"
argument_list|,
literal|"type=keyword"
argument_list|)
argument_list|)
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|indexDoc
argument_list|(
literal|"2014-01-01"
argument_list|,
literal|1
argument_list|)
argument_list|,
name|indexDoc
argument_list|(
literal|"2014-01-02"
argument_list|,
literal|2
argument_list|)
argument_list|,
name|indexDoc
argument_list|(
literal|"2014-01-04"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|()
expr_stmt|;
block|}
DECL|method|testGlobal
specifier|public
name|void
name|testGlobal
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|global
argument_list|(
literal|"global"
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|Global
name|global
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"global"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|global
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFilter
specifier|public
name|void
name|testFilter
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|filter
argument_list|(
literal|"filter"
argument_list|,
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|Filter
name|filter
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"filter"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|filter
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissing
specifier|public
name|void
name|testMissing
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|missing
argument_list|(
literal|"missing"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foobar"
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|Missing
name|missing
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"missing"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|missing
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGlobalWithFilterWithMissing
specifier|public
name|void
name|testGlobalWithFilterWithMissing
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|global
argument_list|(
literal|"global"
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|filter
argument_list|(
literal|"filter"
argument_list|,
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|missing
argument_list|(
literal|"missing"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foobar"
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
argument_list|)
argument_list|)
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
name|Global
name|global
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"global"
argument_list|)
decl_stmt|;
name|Filter
name|filter
init|=
name|global
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"filter"
argument_list|)
decl_stmt|;
name|Missing
name|missing
init|=
name|filter
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"missing"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|missing
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNested
specifier|public
name|void
name|testNested
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|nested
argument_list|(
literal|"nested"
argument_list|,
literal|"nested"
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"nested.date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|Nested
name|nested
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"nested"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|nested
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringTerms
specifier|public
name|void
name|testStringTerms
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|terms
argument_list|(
literal|"terms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"term-s"
argument_list|)
operator|.
name|collectMode
argument_list|(
name|randomFrom
argument_list|(
name|SubAggCollectionMode
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|Terms
name|terms
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"term"
argument_list|)
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongTerms
specifier|public
name|void
name|testLongTerms
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|terms
argument_list|(
literal|"terms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"term-l"
argument_list|)
operator|.
name|collectMode
argument_list|(
name|randomFrom
argument_list|(
name|SubAggCollectionMode
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|Terms
name|terms
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"1"
argument_list|)
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleTerms
specifier|public
name|void
name|testDoubleTerms
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|terms
argument_list|(
literal|"terms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"term-d"
argument_list|)
operator|.
name|collectMode
argument_list|(
name|randomFrom
argument_list|(
name|SubAggCollectionMode
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|Terms
name|terms
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"1.5"
argument_list|)
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRange
specifier|public
name|void
name|testRange
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|range
argument_list|(
literal|"range"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|)
operator|.
name|addRange
argument_list|(
literal|"r1"
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|Range
name|range
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"range"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDateRange
specifier|public
name|void
name|testDateRange
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|dateRange
argument_list|(
literal|"range"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|addRange
argument_list|(
literal|"r1"
argument_list|,
literal|"2014-01-01"
argument_list|,
literal|"2014-01-10"
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|Range
name|range
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"range"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIpRange
specifier|public
name|void
name|testIpRange
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|ipRange
argument_list|(
literal|"range"
argument_list|)
operator|.
name|field
argument_list|(
literal|"ip"
argument_list|)
operator|.
name|addRange
argument_list|(
literal|"r1"
argument_list|,
literal|"10.0.0.1"
argument_list|,
literal|"10.0.0.10"
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|Range
name|range
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"range"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|range
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testHistogram
specifier|public
name|void
name|testHistogram
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|histogram
argument_list|(
literal|"topHisto"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|)
operator|.
name|interval
argument_list|(
literal|5
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|topHisto
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"topHisto"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|topHisto
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDateHistogram
specifier|public
name|void
name|testDateHistogram
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"topHisto"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|MONTH
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|topHisto
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"topHisto"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|topHisto
operator|.
name|getBuckets
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGeoHashGrid
specifier|public
name|void
name|testGeoHashGrid
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
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|geohashGrid
argument_list|(
literal|"grid"
argument_list|)
operator|.
name|field
argument_list|(
literal|"location"
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|dateHistogramInterval
argument_list|(
name|DateHistogramInterval
operator|.
name|DAY
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
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
name|GeoHashGrid
name|grid
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"grid"
argument_list|)
decl_stmt|;
name|Histogram
name|histo
init|=
name|grid
operator|.
name|getBuckets
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
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
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

