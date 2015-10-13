begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.histogram
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
name|histogram
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
name|ParseField
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
name|ParsingException
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
name|rounding
operator|.
name|Rounding
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
name|unit
operator|.
name|TimeValue
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
name|AggregatorFactory
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
name|ValueType
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
name|ValuesSource
operator|.
name|Numeric
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
name|ValuesSourceAggregatorFactory
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
name|ValuesSourceType
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
comment|/**  *  */
end_comment

begin_class
DECL|class|DateHistogramParser
specifier|public
class|class
name|DateHistogramParser
extends|extends
name|HistogramParser
block|{
DECL|method|DateHistogramParser
specifier|public
name|DateHistogramParser
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|InternalDateHistogram
operator|.
name|TYPE
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parseStringInterval
specifier|protected
name|Object
name|parseStringInterval
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
operator|new
name|DateHistogramInterval
argument_list|(
name|text
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createFactory
specifier|protected
name|ValuesSourceAggregatorFactory
argument_list|<
name|Numeric
argument_list|>
name|createFactory
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|ValuesSourceType
name|valuesSourceType
parameter_list|,
name|ValueType
name|targetValueType
parameter_list|,
name|Map
argument_list|<
name|ParseField
argument_list|,
name|Object
argument_list|>
name|otherOptions
parameter_list|)
block|{
name|HistogramAggregator
operator|.
name|DateHistogramFactory
name|factory
init|=
operator|new
name|HistogramAggregator
operator|.
name|DateHistogramFactory
argument_list|(
name|aggregationName
argument_list|)
decl_stmt|;
name|Object
name|interval
init|=
name|otherOptions
operator|.
name|get
argument_list|(
name|Rounding
operator|.
name|Interval
operator|.
name|INTERVAL_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|interval
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
literal|null
argument_list|,
literal|"Missing required field [interval] for histogram aggregation ["
operator|+
name|aggregationName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|interval
operator|instanceof
name|Long
condition|)
block|{
name|factory
operator|.
name|interval
argument_list|(
operator|(
name|Long
operator|)
name|interval
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|interval
operator|instanceof
name|DateHistogramInterval
condition|)
block|{
name|factory
operator|.
name|dateHistogramInterval
argument_list|(
operator|(
name|DateHistogramInterval
operator|)
name|interval
argument_list|)
expr_stmt|;
block|}
name|Long
name|offset
init|=
operator|(
name|Long
operator|)
name|otherOptions
operator|.
name|get
argument_list|(
name|Rounding
operator|.
name|OffsetRounding
operator|.
name|OFFSET_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|offset
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|offset
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
name|ExtendedBounds
name|extendedBounds
init|=
operator|(
name|ExtendedBounds
operator|)
name|otherOptions
operator|.
name|get
argument_list|(
name|ExtendedBounds
operator|.
name|EXTENDED_BOUNDS_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|extendedBounds
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|extendedBounds
argument_list|(
name|extendedBounds
argument_list|)
expr_stmt|;
block|}
name|Boolean
name|keyed
init|=
operator|(
name|Boolean
operator|)
name|otherOptions
operator|.
name|get
argument_list|(
name|HistogramAggregator
operator|.
name|KEYED_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyed
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|keyed
argument_list|(
name|keyed
argument_list|)
expr_stmt|;
block|}
name|Long
name|minDocCount
init|=
operator|(
name|Long
operator|)
name|otherOptions
operator|.
name|get
argument_list|(
name|HistogramAggregator
operator|.
name|MIN_DOC_COUNT_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|minDocCount
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|minDocCount
argument_list|(
name|minDocCount
argument_list|)
expr_stmt|;
block|}
name|InternalOrder
name|order
init|=
operator|(
name|InternalOrder
operator|)
name|otherOptions
operator|.
name|get
argument_list|(
name|HistogramAggregator
operator|.
name|ORDER_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|order
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|order
argument_list|(
name|order
argument_list|)
expr_stmt|;
block|}
return|return
name|factory
return|;
block|}
DECL|method|resolveOrder
specifier|static
name|InternalOrder
name|resolveOrder
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|asc
parameter_list|)
block|{
if|if
condition|(
literal|"_key"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
operator|||
literal|"_time"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
call|(
name|InternalOrder
call|)
argument_list|(
name|asc
condition|?
name|InternalOrder
operator|.
name|KEY_ASC
else|:
name|InternalOrder
operator|.
name|KEY_DESC
argument_list|)
return|;
block|}
if|if
condition|(
literal|"_count"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
call|(
name|InternalOrder
call|)
argument_list|(
name|asc
condition|?
name|InternalOrder
operator|.
name|COUNT_ASC
else|:
name|InternalOrder
operator|.
name|COUNT_DESC
argument_list|)
return|;
block|}
return|return
operator|new
name|InternalOrder
operator|.
name|Aggregation
argument_list|(
name|key
argument_list|,
name|asc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseStringOffset
specifier|protected
name|long
name|parseStringOffset
parameter_list|(
name|String
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|offset
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'-'
condition|)
block|{
return|return
operator|-
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|offset
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|null
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".parseOffset"
argument_list|)
operator|.
name|millis
argument_list|()
return|;
block|}
name|int
name|beginIndex
init|=
name|offset
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'+'
condition|?
literal|1
else|:
literal|0
decl_stmt|;
return|return
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|offset
operator|.
name|substring
argument_list|(
name|beginIndex
argument_list|)
argument_list|,
literal|null
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".parseOffset"
argument_list|)
operator|.
name|millis
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFactoryPrototype
specifier|public
name|AggregatorFactory
name|getFactoryPrototype
parameter_list|()
block|{
return|return
name|HistogramAggregator
operator|.
name|DateHistogramFactory
operator|.
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit

