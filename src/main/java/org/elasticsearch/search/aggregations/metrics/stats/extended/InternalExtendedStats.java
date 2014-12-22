begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.stats.extended
package|package
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
name|stats
operator|.
name|extended
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
name|inject
operator|.
name|internal
operator|.
name|Nullable
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
name|StreamInput
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
name|StreamOutput
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentBuilderString
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
name|AggregationStreams
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
name|InternalAggregation
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
name|stats
operator|.
name|InternalStats
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
name|support
operator|.
name|format
operator|.
name|ValueFormatter
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/** * */
end_comment

begin_class
DECL|class|InternalExtendedStats
specifier|public
class|class
name|InternalExtendedStats
extends|extends
name|InternalStats
implements|implements
name|ExtendedStats
block|{
DECL|field|TYPE
specifier|public
specifier|final
specifier|static
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
literal|"extended_stats"
argument_list|,
literal|"estats"
argument_list|)
decl_stmt|;
DECL|field|STREAM
specifier|public
specifier|final
specifier|static
name|AggregationStreams
operator|.
name|Stream
name|STREAM
init|=
operator|new
name|AggregationStreams
operator|.
name|Stream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InternalExtendedStats
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalExtendedStats
name|result
init|=
operator|new
name|InternalExtendedStats
argument_list|()
decl_stmt|;
name|result
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
decl_stmt|;
DECL|method|registerStreams
specifier|public
specifier|static
name|void
name|registerStreams
parameter_list|()
block|{
name|AggregationStreams
operator|.
name|registerStream
argument_list|(
name|STREAM
argument_list|,
name|TYPE
operator|.
name|stream
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|enum|Metrics
enum|enum
name|Metrics
block|{
DECL|enum constant|count
DECL|enum constant|sum
DECL|enum constant|min
DECL|enum constant|max
DECL|enum constant|avg
DECL|enum constant|sum_of_squares
DECL|enum constant|variance
DECL|enum constant|std_deviation
name|count
block|,
name|sum
block|,
name|min
block|,
name|max
block|,
name|avg
block|,
name|sum_of_squares
block|,
name|variance
block|,
name|std_deviation
block|;
DECL|method|resolve
specifier|public
specifier|static
name|Metrics
name|resolve
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|Metrics
operator|.
name|valueOf
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
DECL|field|sumOfSqrs
specifier|private
name|double
name|sumOfSqrs
decl_stmt|;
DECL|method|InternalExtendedStats
name|InternalExtendedStats
parameter_list|()
block|{}
comment|// for serialization
DECL|method|InternalExtendedStats
specifier|public
name|InternalExtendedStats
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|count
parameter_list|,
name|double
name|sum
parameter_list|,
name|double
name|min
parameter_list|,
name|double
name|max
parameter_list|,
name|double
name|sumOfSqrs
parameter_list|,
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|count
argument_list|,
name|sum
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|formatter
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|this
operator|.
name|sumOfSqrs
operator|=
name|sumOfSqrs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|double
name|value
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
literal|"sum_of_squares"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|sumOfSqrs
return|;
block|}
if|if
condition|(
literal|"variance"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|getVariance
argument_list|()
return|;
block|}
if|if
condition|(
literal|"std_deviation"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|getStdDeviation
argument_list|()
return|;
block|}
return|return
name|super
operator|.
name|value
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSumOfSquares
specifier|public
name|double
name|getSumOfSquares
parameter_list|()
block|{
return|return
name|sumOfSqrs
return|;
block|}
annotation|@
name|Override
DECL|method|getVariance
specifier|public
name|double
name|getVariance
parameter_list|()
block|{
return|return
operator|(
name|sumOfSqrs
operator|-
operator|(
operator|(
name|sum
operator|*
name|sum
operator|)
operator|/
name|count
operator|)
operator|)
operator|/
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|getStdDeviation
specifier|public
name|double
name|getStdDeviation
parameter_list|()
block|{
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|getVariance
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSumOfSquaresAsString
specifier|public
name|String
name|getSumOfSquaresAsString
parameter_list|()
block|{
return|return
name|valueAsString
argument_list|(
name|Metrics
operator|.
name|sum_of_squares
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getVarianceAsString
specifier|public
name|String
name|getVarianceAsString
parameter_list|()
block|{
return|return
name|valueAsString
argument_list|(
name|Metrics
operator|.
name|variance
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getStdDeviationAsString
specifier|public
name|String
name|getStdDeviationAsString
parameter_list|()
block|{
return|return
name|valueAsString
argument_list|(
name|Metrics
operator|.
name|std_deviation
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|reduce
specifier|public
name|InternalExtendedStats
name|reduce
parameter_list|(
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
name|double
name|sumOfSqrs
init|=
literal|0
decl_stmt|;
for|for
control|(
name|InternalAggregation
name|aggregation
range|:
name|reduceContext
operator|.
name|aggregations
argument_list|()
control|)
block|{
name|InternalExtendedStats
name|stats
init|=
operator|(
name|InternalExtendedStats
operator|)
name|aggregation
decl_stmt|;
name|sumOfSqrs
operator|+=
name|stats
operator|.
name|getSumOfSquares
argument_list|()
expr_stmt|;
block|}
specifier|final
name|InternalStats
name|stats
init|=
name|super
operator|.
name|reduce
argument_list|(
name|reduceContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|InternalExtendedStats
argument_list|(
name|name
argument_list|,
name|stats
operator|.
name|getCount
argument_list|()
argument_list|,
name|stats
operator|.
name|getSum
argument_list|()
argument_list|,
name|stats
operator|.
name|getMin
argument_list|()
argument_list|,
name|stats
operator|.
name|getMax
argument_list|()
argument_list|,
name|sumOfSqrs
argument_list|,
name|valueFormatter
argument_list|,
name|getMetaData
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readOtherStatsFrom
specifier|public
name|void
name|readOtherStatsFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|sumOfSqrs
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeOtherStatsTo
specifier|protected
name|void
name|writeOtherStatsTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeDouble
argument_list|(
name|sumOfSqrs
argument_list|)
expr_stmt|;
block|}
DECL|class|Fields
specifier|static
class|class
name|Fields
block|{
DECL|field|SUM_OF_SQRS
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|SUM_OF_SQRS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"sum_of_squares"
argument_list|)
decl_stmt|;
DECL|field|SUM_OF_SQRS_AS_STRING
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|SUM_OF_SQRS_AS_STRING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"sum_of_squares_as_string"
argument_list|)
decl_stmt|;
DECL|field|VARIANCE
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|VARIANCE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"variance"
argument_list|)
decl_stmt|;
DECL|field|VARIANCE_AS_STRING
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|VARIANCE_AS_STRING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"variance_as_string"
argument_list|)
decl_stmt|;
DECL|field|STD_DEVIATION
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|STD_DEVIATION
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"std_deviation"
argument_list|)
decl_stmt|;
DECL|field|STD_DEVIATION_AS_STRING
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|STD_DEVIATION_AS_STRING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"std_deviation_as_string"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|otherStatsToXCotent
specifier|protected
name|XContentBuilder
name|otherStatsToXCotent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SUM_OF_SQRS
argument_list|,
name|count
operator|!=
literal|0
condition|?
name|sumOfSqrs
else|:
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|VARIANCE
argument_list|,
name|count
operator|!=
literal|0
condition|?
name|getVariance
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STD_DEVIATION
argument_list|,
name|count
operator|!=
literal|0
condition|?
name|getStdDeviation
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|!=
literal|0
operator|&&
name|valueFormatter
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SUM_OF_SQRS_AS_STRING
argument_list|,
name|valueFormatter
operator|.
name|format
argument_list|(
name|sumOfSqrs
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|VARIANCE_AS_STRING
argument_list|,
name|valueFormatter
operator|.
name|format
argument_list|(
name|getVariance
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STD_DEVIATION_AS_STRING
argument_list|,
name|valueFormatter
operator|.
name|format
argument_list|(
name|getStdDeviation
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

