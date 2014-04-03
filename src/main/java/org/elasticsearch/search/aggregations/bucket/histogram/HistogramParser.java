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
name|xcontent
operator|.
name|XContentParser
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
name|SearchParseException
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
name|ValuesSourceParser
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
name|ValueParser
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
name|internal
operator|.
name|SearchContext
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

begin_comment
comment|/**  * Parses the histogram request  */
end_comment

begin_class
DECL|class|HistogramParser
specifier|public
class|class
name|HistogramParser
implements|implements
name|Aggregator
operator|.
name|Parser
block|{
DECL|field|EXTENDED_BOUNDS
specifier|static
specifier|final
name|ParseField
name|EXTENDED_BOUNDS
init|=
operator|new
name|ParseField
argument_list|(
literal|"extended_bounds"
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|InternalHistogram
operator|.
name|TYPE
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|AggregatorFactory
name|parse
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ValuesSourceParser
name|vsParser
init|=
name|ValuesSourceParser
operator|.
name|numeric
argument_list|(
name|aggregationName
argument_list|,
name|InternalHistogram
operator|.
name|TYPE
argument_list|,
name|context
argument_list|)
operator|.
name|requiresSortedValues
argument_list|(
literal|true
argument_list|)
operator|.
name|targetValueType
argument_list|(
name|ValueType
operator|.
name|NUMERIC
argument_list|)
operator|.
name|formattable
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|boolean
name|keyed
init|=
literal|false
decl_stmt|;
name|long
name|minDocCount
init|=
literal|1
decl_stmt|;
name|InternalOrder
name|order
init|=
operator|(
name|InternalOrder
operator|)
name|InternalOrder
operator|.
name|KEY_ASC
decl_stmt|;
name|long
name|interval
init|=
operator|-
literal|1
decl_stmt|;
name|ExtendedBounds
name|extendedBounds
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|vsParser
operator|.
name|token
argument_list|(
name|currentFieldName
argument_list|,
name|token
argument_list|,
name|parser
argument_list|)
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NUMBER
condition|)
block|{
if|if
condition|(
literal|"interval"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|interval
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"min_doc_count"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"minDocCount"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|minDocCount
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown key for a "
operator|+
name|token
operator|+
literal|" in aggregation ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_BOOLEAN
condition|)
block|{
if|if
condition|(
literal|"keyed"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|keyed
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown key for a "
operator|+
name|token
operator|+
literal|" in aggregation ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
literal|"order"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|String
name|dir
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
name|boolean
name|asc
init|=
literal|"asc"
operator|.
name|equals
argument_list|(
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|asc
operator|&&
operator|!
literal|"desc"
operator|.
name|equals
argument_list|(
name|dir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown order direction ["
operator|+
name|dir
operator|+
literal|"] in aggregation ["
operator|+
name|aggregationName
operator|+
literal|"]. Should be either [asc] or [desc]"
argument_list|)
throw|;
block|}
name|order
operator|=
name|resolveOrder
argument_list|(
name|currentFieldName
argument_list|,
name|asc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|EXTENDED_BOUNDS
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|extendedBounds
operator|=
operator|new
name|ExtendedBounds
argument_list|()
expr_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"min"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|extendedBounds
operator|.
name|min
operator|=
name|parser
operator|.
name|longValue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"max"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|extendedBounds
operator|.
name|max
operator|=
name|parser
operator|.
name|longValue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown extended_bounds key for a "
operator|+
name|token
operator|+
literal|" in aggregation ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown key for a "
operator|+
name|token
operator|+
literal|" in aggregation ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unexpected token "
operator|+
name|token
operator|+
literal|" in aggregation ["
operator|+
name|aggregationName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|interval
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Missing required field [interval] for histogram aggregation ["
operator|+
name|aggregationName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|Rounding
name|rounding
init|=
operator|new
name|Rounding
operator|.
name|Interval
argument_list|(
name|interval
argument_list|)
decl_stmt|;
if|if
condition|(
name|extendedBounds
operator|!=
literal|null
condition|)
block|{
comment|// with numeric histogram, we can process here and fail fast if the bounds are invalid
name|extendedBounds
operator|.
name|processAndValidate
argument_list|(
name|aggregationName
argument_list|,
name|context
argument_list|,
name|ValueParser
operator|.
name|RAW
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|HistogramAggregator
operator|.
name|Factory
argument_list|(
name|aggregationName
argument_list|,
name|vsParser
operator|.
name|config
argument_list|()
argument_list|,
name|rounding
argument_list|,
name|order
argument_list|,
name|keyed
argument_list|,
name|minDocCount
argument_list|,
name|extendedBounds
argument_list|,
name|InternalHistogram
operator|.
name|FACTORY
argument_list|)
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
block|}
end_class

end_unit

