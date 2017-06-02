begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range
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
operator|.
name|range
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
name|util
operator|.
name|BytesRef
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
name|Tuple
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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
name|DocValueFormat
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
name|InternalAggregations
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
name|InternalMultiBucketAggregation
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
name|ParsedMultiBucketAggregation
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
name|PipelineAggregator
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_class
DECL|class|InternalBinaryRangeTests
specifier|public
class|class
name|InternalBinaryRangeTests
extends|extends
name|InternalRangeTestCase
argument_list|<
name|InternalBinaryRange
argument_list|>
block|{
DECL|field|ranges
specifier|private
name|List
argument_list|<
name|Tuple
argument_list|<
name|BytesRef
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|ranges
decl_stmt|;
annotation|@
name|Override
DECL|method|minNumberOfBuckets
specifier|protected
name|int
name|minNumberOfBuckets
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Tuple
argument_list|<
name|BytesRef
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|listOfRanges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|listOfRanges
operator|.
name|add
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|null
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|15
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|listOfRanges
operator|.
name|add
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|15
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|listOfRanges
operator|.
name|add
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|numRanges
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|randomNumberOfBuckets
argument_list|()
operator|-
name|listOfRanges
operator|.
name|size
argument_list|()
argument_list|)
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
name|numRanges
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
index|[]
name|values
init|=
operator|new
name|BytesRef
index|[
literal|2
index|]
decl_stmt|;
name|values
index|[
literal|0
index|]
operator|=
operator|new
name|BytesRef
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|15
argument_list|)
argument_list|)
expr_stmt|;
name|values
index|[
literal|1
index|]
operator|=
operator|new
name|BytesRef
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|15
argument_list|)
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|listOfRanges
operator|.
name|add
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
name|values
index|[
literal|0
index|]
argument_list|,
name|values
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|listOfRanges
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|ranges
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|listOfRanges
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createTestInstance
specifier|protected
name|InternalBinaryRange
name|createTestInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|,
name|boolean
name|keyed
parameter_list|)
block|{
name|DocValueFormat
name|format
init|=
name|DocValueFormat
operator|.
name|RAW
decl_stmt|;
name|List
argument_list|<
name|InternalBinaryRange
operator|.
name|Bucket
argument_list|>
name|buckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|nullKey
init|=
name|randomBoolean
argument_list|()
condition|?
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|ranges
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
else|:
operator|-
literal|1
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
name|ranges
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|docCount
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|String
name|key
init|=
operator|(
name|i
operator|==
name|nullKey
operator|)
condition|?
literal|null
else|:
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|buckets
operator|.
name|add
argument_list|(
operator|new
name|InternalBinaryRange
operator|.
name|Bucket
argument_list|(
name|format
argument_list|,
name|keyed
argument_list|,
name|key
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|v1
argument_list|()
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|v2
argument_list|()
argument_list|,
name|docCount
argument_list|,
name|aggregations
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InternalBinaryRange
argument_list|(
name|name
argument_list|,
name|format
argument_list|,
name|keyed
argument_list|,
name|buckets
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|instanceReader
specifier|protected
name|Writeable
operator|.
name|Reader
argument_list|<
name|InternalBinaryRange
argument_list|>
name|instanceReader
parameter_list|()
block|{
return|return
name|InternalBinaryRange
operator|::
operator|new
return|;
block|}
annotation|@
name|Override
DECL|method|implementationClass
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|ParsedMultiBucketAggregation
argument_list|>
name|implementationClass
parameter_list|()
block|{
return|return
name|ParsedBinaryRange
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|assertReduced
specifier|protected
name|void
name|assertReduced
parameter_list|(
name|InternalBinaryRange
name|reduced
parameter_list|,
name|List
argument_list|<
name|InternalBinaryRange
argument_list|>
name|inputs
parameter_list|)
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
for|for
control|(
name|InternalBinaryRange
name|input
range|:
name|inputs
control|)
block|{
name|assertEquals
argument_list|(
name|reduced
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|input
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Range
operator|.
name|Bucket
name|bucket
range|:
name|reduced
operator|.
name|getBuckets
argument_list|()
control|)
block|{
name|int
name|expectedCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|InternalBinaryRange
name|input
range|:
name|inputs
control|)
block|{
name|expectedCount
operator|+=
name|input
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
name|pos
argument_list|)
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedCount
argument_list|,
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|pos
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|internalRangeBucketClass
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|InternalMultiBucketAggregation
operator|.
name|InternalBucket
argument_list|>
name|internalRangeBucketClass
parameter_list|()
block|{
return|return
name|InternalBinaryRange
operator|.
name|Bucket
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|parsedRangeBucketClass
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|ParsedMultiBucketAggregation
operator|.
name|ParsedBucket
argument_list|>
name|parsedRangeBucketClass
parameter_list|()
block|{
return|return
name|ParsedBinaryRange
operator|.
name|ParsedBucket
operator|.
name|class
return|;
block|}
block|}
end_class

end_unit

