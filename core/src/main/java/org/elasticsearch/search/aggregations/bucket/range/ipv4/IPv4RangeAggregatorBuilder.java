begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range.ipv4
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
name|ipv4
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
name|ParseFieldMatcher
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
name|network
operator|.
name|Cidrs
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
name|AggregatorFactories
operator|.
name|Builder
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
name|AbstractRangeBuilder
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
name|RangeAggregator
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
name|AggregationContext
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
name|ValuesSourceConfig
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_class
DECL|class|IPv4RangeAggregatorBuilder
specifier|public
class|class
name|IPv4RangeAggregatorBuilder
extends|extends
name|AbstractRangeBuilder
argument_list|<
name|IPv4RangeAggregatorBuilder
argument_list|,
name|IPv4RangeAggregatorBuilder
operator|.
name|Range
argument_list|>
block|{
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|IPv4RangeAggregatorBuilder
name|PROTOTYPE
init|=
operator|new
name|IPv4RangeAggregatorBuilder
argument_list|(
literal|""
argument_list|)
decl_stmt|;
DECL|method|IPv4RangeAggregatorBuilder
specifier|public
name|IPv4RangeAggregatorBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalIPv4Range
operator|.
name|FACTORY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|InternalIPv4Range
operator|.
name|TYPE
operator|.
name|name
argument_list|()
return|;
block|}
comment|/**      * Add a new range to this aggregation.      *      * @param key      *            the key to use for this range in the response      * @param from      *            the lower bound on the distances, inclusive      * @param to      *            the upper bound on the distances, exclusive      */
DECL|method|addRange
specifier|public
name|IPv4RangeAggregatorBuilder
name|addRange
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addMaskRange(String, String)} but uses the mask itself as      * a key.      */
DECL|method|addMaskRange
specifier|public
name|IPv4RangeAggregatorBuilder
name|addMaskRange
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|mask
parameter_list|)
block|{
return|return
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|mask
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Same as {@link #addMaskRange(String, String)} but uses the mask itself as      * a key.      */
DECL|method|addMaskRange
specifier|public
name|IPv4RangeAggregatorBuilder
name|addMaskRange
parameter_list|(
name|String
name|mask
parameter_list|)
block|{
return|return
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|mask
argument_list|,
name|mask
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Same as {@link #addRange(String, String, String)} but the key will be      * automatically generated.      */
DECL|method|addRange
specifier|public
name|IPv4RangeAggregatorBuilder
name|addRange
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
block|{
return|return
name|addRange
argument_list|(
literal|null
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
return|;
block|}
comment|/**      * Same as {@link #addRange(String, String, String)} but there will be no      * lower bound.      */
DECL|method|addUnboundedTo
specifier|public
name|IPv4RangeAggregatorBuilder
name|addUnboundedTo
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|to
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
literal|null
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addUnboundedTo(String, String)} but the key will be      * generated automatically.      */
DECL|method|addUnboundedTo
specifier|public
name|IPv4RangeAggregatorBuilder
name|addUnboundedTo
parameter_list|(
name|String
name|to
parameter_list|)
block|{
return|return
name|addUnboundedTo
argument_list|(
literal|null
argument_list|,
name|to
argument_list|)
return|;
block|}
comment|/**      * Same as {@link #addRange(String, String, String)} but there will be no      * upper bound.      */
DECL|method|addUnboundedFrom
specifier|public
name|IPv4RangeAggregatorBuilder
name|addUnboundedFrom
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|from
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addUnboundedFrom(String, String)} but the key will be      * generated automatically.      */
DECL|method|addUnboundedFrom
specifier|public
name|IPv4RangeAggregatorBuilder
name|addUnboundedFrom
parameter_list|(
name|String
name|from
parameter_list|)
block|{
return|return
name|addUnboundedFrom
argument_list|(
literal|null
argument_list|,
name|from
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|innerBuild
specifier|protected
name|Ipv4RangeAggregatorFactory
name|innerBuild
parameter_list|(
name|AggregationContext
name|context
parameter_list|,
name|ValuesSourceConfig
argument_list|<
name|Numeric
argument_list|>
name|config
parameter_list|,
name|AggregatorFactory
argument_list|<
name|?
argument_list|>
name|parent
parameter_list|,
name|Builder
name|subFactoriesBuilder
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Ipv4RangeAggregatorFactory
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|config
argument_list|,
name|ranges
argument_list|,
name|keyed
argument_list|,
name|rangeFactory
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|subFactoriesBuilder
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createFactoryFromStream
specifier|protected
name|IPv4RangeAggregatorBuilder
name|createFactoryFromStream
parameter_list|(
name|String
name|name
parameter_list|,
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|IPv4RangeAggregatorBuilder
name|factory
init|=
operator|new
name|IPv4RangeAggregatorBuilder
argument_list|(
name|name
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|factory
operator|.
name|addRange
argument_list|(
name|Range
operator|.
name|PROTOTYPE
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|factory
return|;
block|}
DECL|class|Range
specifier|public
specifier|static
class|class
name|Range
extends|extends
name|RangeAggregator
operator|.
name|Range
block|{
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|Range
name|PROTOTYPE
init|=
operator|new
name|Range
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
DECL|field|MASK_FIELD
specifier|static
specifier|final
name|ParseField
name|MASK_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"mask"
argument_list|)
decl_stmt|;
DECL|field|cidr
specifier|private
specifier|final
name|String
name|cidr
decl_stmt|;
DECL|method|Range
specifier|public
name|Range
parameter_list|(
name|String
name|key
parameter_list|,
name|Double
name|from
parameter_list|,
name|Double
name|to
parameter_list|)
block|{
name|this
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
literal|null
argument_list|,
name|to
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|Range
specifier|public
name|Range
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
block|{
name|this
argument_list|(
name|key
argument_list|,
literal|null
argument_list|,
name|from
argument_list|,
literal|null
argument_list|,
name|to
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|Range
specifier|public
name|Range
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|cidr
parameter_list|)
block|{
name|this
argument_list|(
name|key
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|cidr
argument_list|)
expr_stmt|;
block|}
DECL|method|Range
specifier|private
name|Range
parameter_list|(
name|String
name|key
parameter_list|,
name|Double
name|from
parameter_list|,
name|String
name|fromAsStr
parameter_list|,
name|Double
name|to
parameter_list|,
name|String
name|toAsStr
parameter_list|,
name|String
name|cidr
parameter_list|)
block|{
name|super
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|fromAsStr
argument_list|,
name|to
argument_list|,
name|toAsStr
argument_list|)
expr_stmt|;
name|this
operator|.
name|cidr
operator|=
name|cidr
expr_stmt|;
block|}
DECL|method|mask
specifier|public
name|String
name|mask
parameter_list|()
block|{
return|return
name|cidr
return|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|Range
name|process
parameter_list|(
name|ValueParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
assert|assert
name|parser
operator|!=
literal|null
assert|;
name|Double
name|from
init|=
name|this
operator|.
name|from
decl_stmt|;
name|Double
name|to
init|=
name|this
operator|.
name|to
decl_stmt|;
name|String
name|key
init|=
name|this
operator|.
name|key
decl_stmt|;
if|if
condition|(
name|fromAsStr
operator|!=
literal|null
condition|)
block|{
name|from
operator|=
name|parser
operator|.
name|parseDouble
argument_list|(
name|fromAsStr
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|toAsStr
operator|!=
literal|null
condition|)
block|{
name|to
operator|=
name|parser
operator|.
name|parseDouble
argument_list|(
name|toAsStr
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cidr
operator|!=
literal|null
condition|)
block|{
name|long
index|[]
name|fromTo
init|=
name|Cidrs
operator|.
name|cidrMaskToMinMax
argument_list|(
name|cidr
argument_list|)
decl_stmt|;
name|from
operator|=
name|fromTo
index|[
literal|0
index|]
operator|==
literal|0
condition|?
name|Double
operator|.
name|NEGATIVE_INFINITY
else|:
name|fromTo
index|[
literal|0
index|]
expr_stmt|;
name|to
operator|=
name|fromTo
index|[
literal|1
index|]
operator|==
name|InternalIPv4Range
operator|.
name|MAX_IP
condition|?
name|Double
operator|.
name|POSITIVE_INFINITY
else|:
name|fromTo
index|[
literal|1
index|]
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|key
operator|==
literal|null
condition|)
block|{
name|key
operator|=
name|cidr
expr_stmt|;
block|}
block|}
return|return
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|Range
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
throws|throws
name|IOException
block|{
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
name|double
name|from
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|String
name|fromAsStr
init|=
literal|null
decl_stmt|;
name|double
name|to
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|String
name|toAsStr
init|=
literal|null
decl_stmt|;
name|String
name|key
init|=
literal|null
decl_stmt|;
name|String
name|cidr
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
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|FROM_FIELD
argument_list|)
condition|)
block|{
name|from
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|TO_FIELD
argument_list|)
condition|)
block|{
name|to
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
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
name|VALUE_STRING
condition|)
block|{
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|FROM_FIELD
argument_list|)
condition|)
block|{
name|fromAsStr
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|TO_FIELD
argument_list|)
condition|)
block|{
name|toAsStr
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|KEY_FIELD
argument_list|)
condition|)
block|{
name|key
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|MASK_FIELD
argument_list|)
condition|)
block|{
name|cidr
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|fromAsStr
argument_list|,
name|to
argument_list|,
name|toAsStr
argument_list|,
name|cidr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
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
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|KEY_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cidr
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|MASK_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|cidr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|Double
operator|.
name|isFinite
argument_list|(
name|from
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|FROM_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|from
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Double
operator|.
name|isFinite
argument_list|(
name|to
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|TO_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fromAsStr
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|FROM_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|fromAsStr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|toAsStr
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|TO_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|toAsStr
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|Range
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|key
init|=
name|in
operator|.
name|readOptionalString
argument_list|()
decl_stmt|;
name|String
name|fromAsStr
init|=
name|in
operator|.
name|readOptionalString
argument_list|()
decl_stmt|;
name|String
name|toAsStr
init|=
name|in
operator|.
name|readOptionalString
argument_list|()
decl_stmt|;
name|double
name|from
init|=
name|in
operator|.
name|readDouble
argument_list|()
decl_stmt|;
name|double
name|to
init|=
name|in
operator|.
name|readDouble
argument_list|()
decl_stmt|;
name|String
name|mask
init|=
name|in
operator|.
name|readOptionalString
argument_list|()
decl_stmt|;
return|return
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|fromAsStr
argument_list|,
name|to
argument_list|,
name|toAsStr
argument_list|,
name|mask
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeOptionalString
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|fromAsStr
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|toAsStr
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|to
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|cidr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|super
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cidr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|cidr
argument_list|,
operator|(
operator|(
name|Range
operator|)
name|obj
operator|)
operator|.
name|cidr
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
