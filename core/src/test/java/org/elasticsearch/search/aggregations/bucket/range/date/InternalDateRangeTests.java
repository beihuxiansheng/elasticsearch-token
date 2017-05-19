begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range.date
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
operator|.
name|date
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
name|bucket
operator|.
name|range
operator|.
name|InternalRangeTestCase
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
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTime
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTimeZone
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_class
DECL|class|InternalDateRangeTests
specifier|public
class|class
name|InternalDateRangeTests
extends|extends
name|InternalRangeTestCase
argument_list|<
name|InternalDateRange
argument_list|>
block|{
DECL|field|format
specifier|private
name|DocValueFormat
name|format
decl_stmt|;
DECL|field|dateRanges
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
name|dateRanges
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
name|Function
argument_list|<
name|DateTime
argument_list|,
name|DateTime
argument_list|>
name|interval
init|=
name|randomFrom
argument_list|(
name|dateTime
lambda|->
name|dateTime
operator|.
name|plusSeconds
argument_list|(
literal|1
argument_list|)
argument_list|,
name|dateTime
lambda|->
name|dateTime
operator|.
name|plusMinutes
argument_list|(
literal|1
argument_list|)
argument_list|,
name|dateTime
lambda|->
name|dateTime
operator|.
name|plusHours
argument_list|(
literal|1
argument_list|)
argument_list|,
name|dateTime
lambda|->
name|dateTime
operator|.
name|plusDays
argument_list|(
literal|1
argument_list|)
argument_list|,
name|dateTime
lambda|->
name|dateTime
operator|.
name|plusMonths
argument_list|(
literal|1
argument_list|)
argument_list|,
name|dateTime
lambda|->
name|dateTime
operator|.
name|plusYears
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numRanges
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
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
argument_list|(
name|numRanges
argument_list|)
decl_stmt|;
name|DateTime
name|date
init|=
operator|new
name|DateTime
argument_list|(
name|DateTimeZone
operator|.
name|UTC
argument_list|)
decl_stmt|;
name|double
name|start
init|=
name|date
operator|.
name|getMillis
argument_list|()
decl_stmt|;
name|double
name|end
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
name|numRanges
condition|;
name|i
operator|++
control|)
block|{
name|double
name|from
init|=
name|date
operator|.
name|getMillis
argument_list|()
decl_stmt|;
name|date
operator|=
name|interval
operator|.
name|apply
argument_list|(
name|date
argument_list|)
expr_stmt|;
name|double
name|to
init|=
name|date
operator|.
name|getMillis
argument_list|()
decl_stmt|;
name|listOfRanges
operator|.
name|add
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|to
operator|>
name|end
condition|)
block|{
name|end
operator|=
name|to
expr_stmt|;
block|}
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
specifier|final
name|int
name|randomOverlaps
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
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
name|randomOverlaps
condition|;
name|i
operator|++
control|)
block|{
name|listOfRanges
operator|.
name|add
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
name|start
argument_list|,
name|randomDoubleBetween
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|dateRanges
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
name|InternalDateRange
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
name|InternalDateRange
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
name|dateRanges
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
name|dateRanges
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
name|InternalDateRange
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
name|InternalDateRange
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
name|InternalDateRange
argument_list|>
name|instanceReader
parameter_list|()
block|{
return|return
name|InternalDateRange
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
name|ParsedDateRange
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
name|InternalDateRange
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
name|ParsedDateRange
operator|.
name|ParsedBucket
operator|.
name|class
return|;
block|}
block|}
end_class

end_unit

