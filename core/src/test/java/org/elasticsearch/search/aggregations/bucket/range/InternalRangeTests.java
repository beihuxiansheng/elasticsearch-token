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
DECL|class|InternalRangeTests
specifier|public
class|class
name|InternalRangeTests
extends|extends
name|InternalRangeTestCase
argument_list|<
name|InternalRange
argument_list|>
block|{
DECL|field|format
specifier|private
name|DocValueFormat
name|format
decl_stmt|;
DECL|field|ranges
specifier|private
name|List
argument_list|<
name|Tuple
argument_list|<
name|Double
argument_list|,
name|Double
argument_list|>
argument_list|>
name|ranges
decl_stmt|;
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
name|format
operator|=
name|randomNumericDocValueFormat
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Tuple
argument_list|<
name|Double
argument_list|,
name|Double
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
name|rarely
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
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rarely
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
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|,
name|randomDouble
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rarely
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
name|randomDouble
argument_list|()
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|interval
init|=
name|randomFrom
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|,
literal|25
argument_list|,
literal|50
argument_list|,
literal|100
argument_list|)
decl_stmt|;
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
specifier|final
name|double
name|max
init|=
operator|(
name|double
operator|)
name|numRanges
operator|*
name|interval
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|numRanges
operator|-
name|listOfRanges
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|;
name|i
operator|++
control|)
block|{
name|double
name|from
init|=
name|i
operator|*
name|interval
decl_stmt|;
name|double
name|to
init|=
name|from
operator|+
name|interval
decl_stmt|;
name|Tuple
argument_list|<
name|Double
argument_list|,
name|Double
argument_list|>
name|range
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|range
operator|=
name|Tuple
operator|.
name|tuple
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Add some overlapping range
name|range
operator|=
name|Tuple
operator|.
name|tuple
argument_list|(
name|randomFrom
argument_list|(
literal|0.0
argument_list|,
name|max
operator|/
literal|3
argument_list|)
argument_list|,
name|randomFrom
argument_list|(
name|max
argument_list|,
name|max
operator|/
literal|2
argument_list|,
name|max
operator|/
literal|3
operator|*
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|listOfRanges
operator|.
name|add
argument_list|(
name|range
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
name|InternalRange
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
specifier|final
name|List
argument_list|<
name|InternalRange
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
name|Tuple
argument_list|<
name|Double
argument_list|,
name|Double
argument_list|>
name|range
init|=
name|ranges
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|docCount
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|double
name|from
init|=
name|range
operator|.
name|v1
argument_list|()
decl_stmt|;
name|double
name|to
init|=
name|range
operator|.
name|v2
argument_list|()
decl_stmt|;
name|buckets
operator|.
name|add
argument_list|(
operator|new
name|InternalRange
operator|.
name|Bucket
argument_list|(
literal|"range_"
operator|+
name|i
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|docCount
argument_list|,
name|aggregations
argument_list|,
name|keyed
argument_list|,
name|format
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InternalRange
argument_list|<>
argument_list|(
name|name
argument_list|,
name|buckets
argument_list|,
name|format
argument_list|,
name|keyed
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
name|InternalRange
argument_list|>
name|instanceReader
parameter_list|()
block|{
return|return
name|InternalRange
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
name|ParsedRange
operator|.
name|class
return|;
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
name|InternalRange
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
name|ParsedRange
operator|.
name|ParsedBucket
operator|.
name|class
return|;
block|}
block|}
end_class

end_unit

