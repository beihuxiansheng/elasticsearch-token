begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range.geodistance
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
name|geodistance
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
name|geo
operator|.
name|GeoDistance
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
name|geo
operator|.
name|GeoPoint
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
name|unit
operator|.
name|DistanceUnit
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
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
name|AbstractValuesSourceParser
operator|.
name|GeoPointValuesSourceParser
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
name|GeoPointParser
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
name|ArrayList
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|GeoDistanceParser
specifier|public
class|class
name|GeoDistanceParser
extends|extends
name|GeoPointValuesSourceParser
block|{
DECL|field|ORIGIN_FIELD
specifier|static
specifier|final
name|ParseField
name|ORIGIN_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"origin"
argument_list|,
literal|"center"
argument_list|,
literal|"point"
argument_list|,
literal|"por"
argument_list|)
decl_stmt|;
DECL|field|UNIT_FIELD
specifier|static
specifier|final
name|ParseField
name|UNIT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"unit"
argument_list|)
decl_stmt|;
DECL|field|DISTANCE_TYPE_FIELD
specifier|static
specifier|final
name|ParseField
name|DISTANCE_TYPE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"distance_type"
argument_list|)
decl_stmt|;
DECL|field|geoPointParser
specifier|private
name|GeoPointParser
name|geoPointParser
init|=
operator|new
name|GeoPointParser
argument_list|(
name|InternalGeoDistance
operator|.
name|TYPE
argument_list|,
name|ORIGIN_FIELD
argument_list|)
decl_stmt|;
DECL|method|GeoDistanceParser
specifier|public
name|GeoDistanceParser
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
name|super
argument_list|(
name|key
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
argument_list|,
name|from
operator|==
literal|null
condition|?
literal|0
else|:
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
comment|/**          * Read from a stream.          */
DECL|method|Range
specifier|public
name|Range
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
operator|.
name|readOptionalString
argument_list|()
argument_list|,
name|in
operator|.
name|readDouble
argument_list|()
argument_list|,
name|in
operator|.
name|readDouble
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
DECL|method|key
specifier|private
specifier|static
name|String
name|key
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
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
return|return
name|key
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
operator|(
name|from
operator|==
literal|null
operator|||
name|from
operator|==
literal|0
operator|)
condition|?
literal|"*"
else|:
name|from
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
operator|(
name|to
operator|==
literal|null
operator|||
name|Double
operator|.
name|isInfinite
argument_list|(
name|to
argument_list|)
operator|)
condition|?
literal|"*"
else|:
name|to
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createFactory
specifier|protected
name|GeoDistanceAggregatorBuilder
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
name|GeoPoint
name|origin
init|=
operator|(
name|GeoPoint
operator|)
name|otherOptions
operator|.
name|get
argument_list|(
name|ORIGIN_FIELD
argument_list|)
decl_stmt|;
name|GeoDistanceAggregatorBuilder
name|factory
init|=
operator|new
name|GeoDistanceAggregatorBuilder
argument_list|(
name|aggregationName
argument_list|,
name|origin
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Range
argument_list|>
name|ranges
init|=
operator|(
name|List
argument_list|<
name|Range
argument_list|>
operator|)
name|otherOptions
operator|.
name|get
argument_list|(
name|RangeAggregator
operator|.
name|RANGES_FIELD
argument_list|)
decl_stmt|;
for|for
control|(
name|Range
name|range
range|:
name|ranges
control|)
block|{
name|factory
operator|.
name|addRange
argument_list|(
name|range
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
name|RangeAggregator
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
name|DistanceUnit
name|unit
init|=
operator|(
name|DistanceUnit
operator|)
name|otherOptions
operator|.
name|get
argument_list|(
name|UNIT_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|unit
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|unit
argument_list|(
name|unit
argument_list|)
expr_stmt|;
block|}
name|GeoDistance
name|distanceType
init|=
operator|(
name|GeoDistance
operator|)
name|otherOptions
operator|.
name|get
argument_list|(
name|DISTANCE_TYPE_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|distanceType
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|distanceType
argument_list|(
name|distanceType
argument_list|)
expr_stmt|;
block|}
return|return
name|factory
return|;
block|}
annotation|@
name|Override
DECL|method|token
specifier|protected
name|boolean
name|token
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|String
name|currentFieldName
parameter_list|,
name|Token
name|token
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|,
name|Map
argument_list|<
name|ParseField
argument_list|,
name|Object
argument_list|>
name|otherOptions
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|geoPointParser
operator|.
name|token
argument_list|(
name|aggregationName
argument_list|,
name|currentFieldName
argument_list|,
name|token
argument_list|,
name|parser
argument_list|,
name|parseFieldMatcher
argument_list|,
name|otherOptions
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
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
name|UNIT_FIELD
argument_list|)
condition|)
block|{
name|DistanceUnit
name|unit
init|=
name|DistanceUnit
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|otherOptions
operator|.
name|put
argument_list|(
name|UNIT_FIELD
argument_list|,
name|unit
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
name|DISTANCE_TYPE_FIELD
argument_list|)
condition|)
block|{
name|GeoDistance
name|distanceType
init|=
name|GeoDistance
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|otherOptions
operator|.
name|put
argument_list|(
name|DISTANCE_TYPE_FIELD
argument_list|,
name|distanceType
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|RangeAggregator
operator|.
name|KEYED_FIELD
argument_list|)
condition|)
block|{
name|boolean
name|keyed
init|=
name|parser
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
name|otherOptions
operator|.
name|put
argument_list|(
name|RangeAggregator
operator|.
name|KEYED_FIELD
argument_list|,
name|keyed
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
name|START_ARRAY
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
name|RangeAggregator
operator|.
name|RANGES_FIELD
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|Range
argument_list|>
name|ranges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|END_ARRAY
condition|)
block|{
name|String
name|fromAsStr
init|=
literal|null
decl_stmt|;
name|String
name|toAsStr
init|=
literal|null
decl_stmt|;
name|double
name|from
init|=
literal|0.0
decl_stmt|;
name|double
name|to
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|String
name|key
init|=
literal|null
decl_stmt|;
name|String
name|toOrFromOrKey
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
name|toOrFromOrKey
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
name|toOrFromOrKey
argument_list|,
name|Range
operator|.
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
name|toOrFromOrKey
argument_list|,
name|Range
operator|.
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
name|toOrFromOrKey
argument_list|,
name|Range
operator|.
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
name|toOrFromOrKey
argument_list|,
name|Range
operator|.
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
name|toOrFromOrKey
argument_list|,
name|Range
operator|.
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
block|}
block|}
if|if
condition|(
name|fromAsStr
operator|!=
literal|null
operator|||
name|toAsStr
operator|!=
literal|null
condition|)
block|{
name|ranges
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|Double
operator|.
name|parseDouble
argument_list|(
name|fromAsStr
argument_list|)
argument_list|,
name|Double
operator|.
name|parseDouble
argument_list|(
name|toAsStr
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ranges
operator|.
name|add
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
block|}
block|}
name|otherOptions
operator|.
name|put
argument_list|(
name|RangeAggregator
operator|.
name|RANGES_FIELD
argument_list|,
name|ranges
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

