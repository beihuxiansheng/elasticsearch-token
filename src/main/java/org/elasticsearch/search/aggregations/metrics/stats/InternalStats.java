begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.stats
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
name|InternalNumericMetricsAggregation
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
name|ValueFormatterStreams
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
DECL|class|InternalStats
specifier|public
class|class
name|InternalStats
extends|extends
name|InternalNumericMetricsAggregation
operator|.
name|MultiValue
implements|implements
name|Stats
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
literal|"stats"
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
name|InternalStats
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalStats
name|result
init|=
operator|new
name|InternalStats
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
name|count
block|,
name|sum
block|,
name|min
block|,
name|max
block|,
name|avg
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
DECL|field|count
specifier|protected
name|long
name|count
decl_stmt|;
DECL|field|min
specifier|protected
name|double
name|min
decl_stmt|;
DECL|field|max
specifier|protected
name|double
name|max
decl_stmt|;
DECL|field|sum
specifier|protected
name|double
name|sum
decl_stmt|;
DECL|method|InternalStats
specifier|protected
name|InternalStats
parameter_list|()
block|{}
comment|// for serialization
DECL|method|InternalStats
specifier|public
name|InternalStats
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
name|metaData
argument_list|)
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|sum
operator|=
name|sum
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|getMin
specifier|public
name|double
name|getMin
parameter_list|()
block|{
return|return
name|min
return|;
block|}
annotation|@
name|Override
DECL|method|getMax
specifier|public
name|double
name|getMax
parameter_list|()
block|{
return|return
name|max
return|;
block|}
annotation|@
name|Override
DECL|method|getAvg
specifier|public
name|double
name|getAvg
parameter_list|()
block|{
return|return
name|sum
operator|/
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|getSum
specifier|public
name|double
name|getSum
parameter_list|()
block|{
return|return
name|sum
return|;
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
name|Metrics
name|metrics
init|=
name|Metrics
operator|.
name|valueOf
argument_list|(
name|name
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|metrics
condition|)
block|{
case|case
name|min
case|:
return|return
name|this
operator|.
name|min
return|;
case|case
name|max
case|:
return|return
name|this
operator|.
name|max
return|;
case|case
name|avg
case|:
return|return
name|this
operator|.
name|getAvg
argument_list|()
return|;
case|case
name|count
case|:
return|return
name|this
operator|.
name|count
return|;
case|case
name|sum
case|:
return|return
name|this
operator|.
name|sum
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown value ["
operator|+
name|name
operator|+
literal|"] in common stats aggregation"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|reduce
specifier|public
name|InternalStats
name|reduce
parameter_list|(
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
name|double
name|min
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|max
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|double
name|sum
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
name|InternalStats
name|stats
init|=
operator|(
name|InternalStats
operator|)
name|aggregation
decl_stmt|;
name|count
operator|+=
name|stats
operator|.
name|getCount
argument_list|()
expr_stmt|;
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|min
argument_list|,
name|stats
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|stats
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|sum
operator|+=
name|stats
operator|.
name|getSum
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|InternalStats
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
name|getMetaData
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|void
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|valueFormatter
operator|=
name|ValueFormatterStreams
operator|.
name|readOptional
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|count
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|min
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|max
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|sum
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|readOtherStatsFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
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
block|{     }
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|ValueFormatterStreams
operator|.
name|writeOptional
argument_list|(
name|valueFormatter
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|min
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|max
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|sum
argument_list|)
expr_stmt|;
name|writeOtherStatsTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
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
block|{     }
DECL|class|Fields
specifier|static
class|class
name|Fields
block|{
DECL|field|COUNT
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|COUNT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"count"
argument_list|)
decl_stmt|;
DECL|field|MIN
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|MIN
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"min"
argument_list|)
decl_stmt|;
DECL|field|MIN_AS_STRING
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|MIN_AS_STRING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"min_as_string"
argument_list|)
decl_stmt|;
DECL|field|MAX
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|MAX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"max"
argument_list|)
decl_stmt|;
DECL|field|MAX_AS_STRING
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|MAX_AS_STRING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"max_as_string"
argument_list|)
decl_stmt|;
DECL|field|AVG
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|AVG
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"avg"
argument_list|)
decl_stmt|;
DECL|field|AVG_AS_STRING
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|AVG_AS_STRING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"avg_as_string"
argument_list|)
decl_stmt|;
DECL|field|SUM
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|SUM
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"sum"
argument_list|)
decl_stmt|;
DECL|field|SUM_AS_STRING
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|SUM_AS_STRING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"sum_as_string"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|public
name|XContentBuilder
name|doXContentBody
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
name|COUNT
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MIN
argument_list|,
name|count
operator|!=
literal|0
condition|?
name|min
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
name|MAX
argument_list|,
name|count
operator|!=
literal|0
condition|?
name|max
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
name|AVG
argument_list|,
name|count
operator|!=
literal|0
condition|?
name|getAvg
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
name|SUM
argument_list|,
name|count
operator|!=
literal|0
condition|?
name|sum
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
name|MIN_AS_STRING
argument_list|,
name|valueFormatter
operator|.
name|format
argument_list|(
name|min
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MAX_AS_STRING
argument_list|,
name|valueFormatter
operator|.
name|format
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|AVG_AS_STRING
argument_list|,
name|valueFormatter
operator|.
name|format
argument_list|(
name|getAvg
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
name|SUM_AS_STRING
argument_list|,
name|valueFormatter
operator|.
name|format
argument_list|(
name|sum
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|otherStatsToXCotent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
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
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

