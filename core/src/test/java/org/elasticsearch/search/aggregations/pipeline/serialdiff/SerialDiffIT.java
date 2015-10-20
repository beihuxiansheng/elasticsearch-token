begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline.serialdiff
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
operator|.
name|serialdiff
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
name|collect
operator|.
name|EvictingQueue
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
name|InternalHistogram
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
name|ValuesSourceMetricsAggregationBuilder
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
name|pipeline
operator|.
name|BucketHelpers
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
name|pipeline
operator|.
name|PipelineAggregationHelperTests
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
name|pipeline
operator|.
name|SimpleValue
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
name|hamcrest
operator|.
name|Matchers
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|avg
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
name|max
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
name|min
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
name|diff
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
name|closeTo
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
name|Matchers
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
name|nullValue
import|;
end_import

begin_class
annotation|@
name|ESIntegTestCase
operator|.
name|SuiteScopeTestCase
DECL|class|SerialDiffIT
specifier|public
class|class
name|SerialDiffIT
extends|extends
name|ESIntegTestCase
block|{
DECL|field|INTERVAL_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|INTERVAL_FIELD
init|=
literal|"l_value"
decl_stmt|;
DECL|field|VALUE_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|VALUE_FIELD
init|=
literal|"v_value"
decl_stmt|;
DECL|field|interval
specifier|static
name|int
name|interval
decl_stmt|;
DECL|field|numBuckets
specifier|static
name|int
name|numBuckets
decl_stmt|;
DECL|field|lag
specifier|static
name|int
name|lag
decl_stmt|;
DECL|field|gapPolicy
specifier|static
name|BucketHelpers
operator|.
name|GapPolicy
name|gapPolicy
decl_stmt|;
DECL|field|metric
specifier|static
name|ValuesSourceMetricsAggregationBuilder
name|metric
decl_stmt|;
DECL|field|mockHisto
specifier|static
name|List
argument_list|<
name|PipelineAggregationHelperTests
operator|.
name|MockBucket
argument_list|>
name|mockHisto
decl_stmt|;
DECL|field|testValues
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|ArrayList
argument_list|<
name|Double
argument_list|>
argument_list|>
name|testValues
decl_stmt|;
DECL|enum|MetricTarget
enum|enum
name|MetricTarget
block|{
DECL|enum constant|VALUE
DECL|enum constant|COUNT
name|VALUE
argument_list|(
literal|"value"
argument_list|)
block|,
name|COUNT
argument_list|(
literal|"count"
argument_list|)
block|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|MetricTarget
name|MetricTarget
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|name
operator|=
name|s
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
DECL|method|randomMetric
specifier|private
name|ValuesSourceMetricsAggregationBuilder
name|randomMetric
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|int
name|rand
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|rand
condition|)
block|{
case|case
literal|0
case|:
return|return
name|min
argument_list|(
name|name
argument_list|)
operator|.
name|field
argument_list|(
name|field
argument_list|)
return|;
case|case
literal|2
case|:
return|return
name|max
argument_list|(
name|name
argument_list|)
operator|.
name|field
argument_list|(
name|field
argument_list|)
return|;
case|case
literal|3
case|:
return|return
name|avg
argument_list|(
name|name
argument_list|)
operator|.
name|field
argument_list|(
name|field
argument_list|)
return|;
default|default:
return|return
name|avg
argument_list|(
name|name
argument_list|)
operator|.
name|field
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
DECL|method|assertValidIterators
specifier|private
name|void
name|assertValidIterators
parameter_list|(
name|Iterator
name|expectedBucketIter
parameter_list|,
name|Iterator
name|expectedCountsIter
parameter_list|,
name|Iterator
name|expectedValuesIter
parameter_list|)
block|{
if|if
condition|(
operator|!
name|expectedBucketIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"`expectedBucketIter` iterator ended before `actual` iterator, size mismatch"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|expectedCountsIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"`expectedCountsIter` iterator ended before `actual` iterator, size mismatch"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|expectedValuesIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"`expectedValuesIter` iterator ended before `actual` iterator, size mismatch"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertBucketContents
specifier|private
name|void
name|assertBucketContents
parameter_list|(
name|Histogram
operator|.
name|Bucket
name|actual
parameter_list|,
name|Double
name|expectedCount
parameter_list|,
name|Double
name|expectedValue
parameter_list|)
block|{
comment|// This is a gap bucket
name|SimpleValue
name|countDiff
init|=
name|actual
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"diff_counts"
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectedCount
operator|==
literal|null
condition|)
block|{
name|assertThat
argument_list|(
literal|"[_count] diff is not null"
argument_list|,
name|countDiff
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
literal|"[_count] diff is null"
argument_list|,
name|countDiff
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"[_count] diff does not match expected ["
operator|+
name|countDiff
operator|.
name|value
argument_list|()
operator|+
literal|" vs "
operator|+
name|expectedCount
operator|+
literal|"]"
argument_list|,
name|countDiff
operator|.
name|value
argument_list|()
argument_list|,
name|closeTo
argument_list|(
name|expectedCount
argument_list|,
literal|0.1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// This is a gap bucket
name|SimpleValue
name|valuesDiff
init|=
name|actual
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"diff_values"
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectedValue
operator|==
literal|null
condition|)
block|{
name|assertThat
argument_list|(
literal|"[value] diff is not null"
argument_list|,
name|valuesDiff
argument_list|,
name|Matchers
operator|.
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
literal|"[value] diff is null"
argument_list|,
name|valuesDiff
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"[value] diff does not match expected ["
operator|+
name|valuesDiff
operator|.
name|value
argument_list|()
operator|+
literal|" vs "
operator|+
name|expectedValue
operator|+
literal|"]"
argument_list|,
name|valuesDiff
operator|.
name|value
argument_list|()
argument_list|,
name|closeTo
argument_list|(
name|expectedValue
argument_list|,
literal|0.1
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|interval
operator|=
literal|5
expr_stmt|;
name|numBuckets
operator|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|80
argument_list|)
expr_stmt|;
name|lag
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|numBuckets
operator|/
literal|2
argument_list|)
expr_stmt|;
name|gapPolicy
operator|=
name|randomBoolean
argument_list|()
condition|?
name|BucketHelpers
operator|.
name|GapPolicy
operator|.
name|SKIP
else|:
name|BucketHelpers
operator|.
name|GapPolicy
operator|.
name|INSERT_ZEROS
expr_stmt|;
name|metric
operator|=
name|randomMetric
argument_list|(
literal|"the_metric"
argument_list|,
name|VALUE_FIELD
argument_list|)
expr_stmt|;
name|mockHisto
operator|=
name|PipelineAggregationHelperTests
operator|.
name|generateHistogram
argument_list|(
name|interval
argument_list|,
name|numBuckets
argument_list|,
name|randomDouble
argument_list|()
argument_list|,
name|randomDouble
argument_list|()
argument_list|)
expr_stmt|;
name|testValues
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|8
argument_list|)
expr_stmt|;
for|for
control|(
name|MetricTarget
name|target
range|:
name|MetricTarget
operator|.
name|values
argument_list|()
control|)
block|{
name|setupExpected
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|PipelineAggregationHelperTests
operator|.
name|MockBucket
name|mockBucket
range|:
name|mockHisto
control|)
block|{
for|for
control|(
name|double
name|value
range|:
name|mockBucket
operator|.
name|docValues
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
name|INTERVAL_FIELD
argument_list|,
name|mockBucket
operator|.
name|key
argument_list|)
operator|.
name|field
argument_list|(
name|VALUE_FIELD
argument_list|,
name|value
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
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
block|}
comment|/**      * @param target    The document field "target", e.g. _count or a field value      */
DECL|method|setupExpected
specifier|private
name|void
name|setupExpected
parameter_list|(
name|MetricTarget
name|target
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Double
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numBuckets
argument_list|)
decl_stmt|;
name|EvictingQueue
argument_list|<
name|Double
argument_list|>
name|lagWindow
init|=
operator|new
name|EvictingQueue
argument_list|<>
argument_list|(
name|lag
argument_list|)
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PipelineAggregationHelperTests
operator|.
name|MockBucket
name|mockBucket
range|:
name|mockHisto
control|)
block|{
name|Double
name|metricValue
decl_stmt|;
name|double
index|[]
name|docValues
init|=
name|mockBucket
operator|.
name|docValues
decl_stmt|;
comment|// Gaps only apply to metric values, not doc _counts
if|if
condition|(
name|mockBucket
operator|.
name|count
operator|==
literal|0
operator|&&
name|target
operator|.
name|equals
argument_list|(
name|MetricTarget
operator|.
name|VALUE
argument_list|)
condition|)
block|{
comment|// If there was a gap in doc counts and we are ignoring, just skip this bucket
if|if
condition|(
name|gapPolicy
operator|.
name|equals
argument_list|(
name|BucketHelpers
operator|.
name|GapPolicy
operator|.
name|SKIP
argument_list|)
condition|)
block|{
name|metricValue
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|gapPolicy
operator|.
name|equals
argument_list|(
name|BucketHelpers
operator|.
name|GapPolicy
operator|.
name|INSERT_ZEROS
argument_list|)
condition|)
block|{
comment|// otherwise insert a zero instead of the true value
name|metricValue
operator|=
literal|0.0
expr_stmt|;
block|}
else|else
block|{
name|metricValue
operator|=
name|PipelineAggregationHelperTests
operator|.
name|calculateMetric
argument_list|(
name|docValues
argument_list|,
name|metric
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// If this isn't a gap, or is a _count, just insert the value
name|metricValue
operator|=
name|target
operator|.
name|equals
argument_list|(
name|MetricTarget
operator|.
name|VALUE
argument_list|)
condition|?
name|PipelineAggregationHelperTests
operator|.
name|calculateMetric
argument_list|(
name|docValues
argument_list|,
name|metric
argument_list|)
else|:
name|mockBucket
operator|.
name|count
expr_stmt|;
block|}
name|counter
operator|+=
literal|1
expr_stmt|;
comment|// Still under the initial lag period, add nothing and move on
name|Double
name|lagValue
decl_stmt|;
if|if
condition|(
name|counter
operator|<=
name|lag
condition|)
block|{
name|lagValue
operator|=
name|Double
operator|.
name|NaN
expr_stmt|;
block|}
else|else
block|{
name|lagValue
operator|=
name|lagWindow
operator|.
name|peek
argument_list|()
expr_stmt|;
comment|// Peek here, because we rely on add'ing to always move the window
block|}
comment|// Normalize null's to NaN
if|if
condition|(
name|metricValue
operator|==
literal|null
condition|)
block|{
name|metricValue
operator|=
name|Double
operator|.
name|NaN
expr_stmt|;
block|}
comment|// Both have values, calculate diff and replace the "empty" bucket
if|if
condition|(
operator|!
name|Double
operator|.
name|isNaN
argument_list|(
name|metricValue
argument_list|)
operator|&&
operator|!
name|Double
operator|.
name|isNaN
argument_list|(
name|lagValue
argument_list|)
condition|)
block|{
name|double
name|diff
init|=
name|metricValue
operator|-
name|lagValue
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// The tests need null, even though the agg doesn't
block|}
name|lagWindow
operator|.
name|add
argument_list|(
name|metricValue
argument_list|)
expr_stmt|;
block|}
name|testValues
operator|.
name|put
argument_list|(
name|target
operator|.
name|toString
argument_list|()
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
DECL|method|testBasicDiff
specifier|public
name|void
name|testBasicDiff
parameter_list|()
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
name|setTypes
argument_list|(
literal|"type"
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
name|INTERVAL_FIELD
argument_list|)
operator|.
name|interval
argument_list|(
name|interval
argument_list|)
operator|.
name|extendedBounds
argument_list|(
literal|0L
argument_list|,
call|(
name|long
call|)
argument_list|(
name|interval
operator|*
operator|(
name|numBuckets
operator|-
literal|1
operator|)
argument_list|)
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|metric
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|diff
argument_list|(
literal|"diff_counts"
argument_list|)
operator|.
name|lag
argument_list|(
name|lag
argument_list|)
operator|.
name|gapPolicy
argument_list|(
name|gapPolicy
argument_list|)
operator|.
name|setBucketsPaths
argument_list|(
literal|"_count"
argument_list|)
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|diff
argument_list|(
literal|"diff_values"
argument_list|)
operator|.
name|lag
argument_list|(
name|lag
argument_list|)
operator|.
name|gapPolicy
argument_list|(
name|gapPolicy
argument_list|)
operator|.
name|setBucketsPaths
argument_list|(
literal|"the_metric"
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
name|InternalHistogram
argument_list|<
name|InternalHistogram
operator|.
name|Bucket
argument_list|>
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
name|InternalHistogram
operator|.
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
literal|"Size of buckets array is not correct."
argument_list|,
name|buckets
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|mockHisto
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Double
argument_list|>
name|expectedCounts
init|=
name|testValues
operator|.
name|get
argument_list|(
name|MetricTarget
operator|.
name|COUNT
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Double
argument_list|>
name|expectedValues
init|=
name|testValues
operator|.
name|get
argument_list|(
name|MetricTarget
operator|.
name|VALUE
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|Histogram
operator|.
name|Bucket
argument_list|>
name|actualIter
init|=
name|buckets
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|PipelineAggregationHelperTests
operator|.
name|MockBucket
argument_list|>
name|expectedBucketIter
init|=
name|mockHisto
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Double
argument_list|>
name|expectedCountsIter
init|=
name|expectedCounts
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Double
argument_list|>
name|expectedValuesIter
init|=
name|expectedValues
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|actualIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|assertValidIterators
argument_list|(
name|expectedBucketIter
argument_list|,
name|expectedCountsIter
argument_list|,
name|expectedValuesIter
argument_list|)
expr_stmt|;
name|Histogram
operator|.
name|Bucket
name|actual
init|=
name|actualIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|PipelineAggregationHelperTests
operator|.
name|MockBucket
name|expected
init|=
name|expectedBucketIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Double
name|expectedCount
init|=
name|expectedCountsIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Double
name|expectedValue
init|=
name|expectedValuesIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"keys do not match"
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|actual
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
name|expected
operator|.
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"doc counts do not match"
argument_list|,
name|actual
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|expected
operator|.
name|count
argument_list|)
argument_list|)
expr_stmt|;
name|assertBucketContents
argument_list|(
name|actual
argument_list|,
name|expectedCount
argument_list|,
name|expectedValue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidLagSize
specifier|public
name|void
name|testInvalidLagSize
parameter_list|()
block|{
try|try
block|{
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"type"
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
name|INTERVAL_FIELD
argument_list|)
operator|.
name|interval
argument_list|(
name|interval
argument_list|)
operator|.
name|extendedBounds
argument_list|(
literal|0L
argument_list|,
call|(
name|long
call|)
argument_list|(
name|interval
operator|*
operator|(
name|numBuckets
operator|-
literal|1
operator|)
argument_list|)
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|metric
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|diff
argument_list|(
literal|"diff_counts"
argument_list|)
operator|.
name|lag
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|gapPolicy
argument_list|(
name|gapPolicy
argument_list|)
operator|.
name|setBucketsPaths
argument_list|(
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
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SearchPhaseExecutionException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"all shards failed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

