begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|geo
operator|.
name|GeoUtils
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
name|inject
operator|.
name|Inject
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
name|index
operator|.
name|mapper
operator|.
name|geo
operator|.
name|GeoPointFieldMapper
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
comment|/**  *<pre>  * {  *     "name.lat" : 1.1,  *     "name.lon" : 1.2,  * }  *</pre>  */
end_comment

begin_class
DECL|class|GeoDistanceRangeQueryParser
specifier|public
class|class
name|GeoDistanceRangeQueryParser
extends|extends
name|BaseQueryParser
argument_list|<
name|GeoDistanceRangeQueryBuilder
argument_list|>
block|{
DECL|field|FROM_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|FROM_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"from"
argument_list|)
decl_stmt|;
DECL|field|TO_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|TO_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"to"
argument_list|)
decl_stmt|;
DECL|field|INCLUDE_LOWER_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|INCLUDE_LOWER_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"include_lower"
argument_list|)
decl_stmt|;
DECL|field|INCLUDE_UPPER_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|INCLUDE_UPPER_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"include_upper"
argument_list|)
decl_stmt|;
DECL|field|GT_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|GT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"gt"
argument_list|)
decl_stmt|;
DECL|field|GTE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|GTE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"gte"
argument_list|,
literal|"ge"
argument_list|)
decl_stmt|;
DECL|field|LT_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|LT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"lt"
argument_list|)
decl_stmt|;
DECL|field|LTE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|LTE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"lte"
argument_list|,
literal|"le"
argument_list|)
decl_stmt|;
DECL|field|UNIT_FIELD
specifier|public
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
specifier|public
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
DECL|field|NAME_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|NAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"_name"
argument_list|)
decl_stmt|;
DECL|field|BOOST_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|BOOST_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"boost"
argument_list|)
decl_stmt|;
DECL|field|OPTIMIZE_BBOX_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|OPTIMIZE_BBOX_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"optimize_bbox"
argument_list|)
decl_stmt|;
DECL|field|COERCE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|COERCE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"coerce"
argument_list|,
literal|"normalize"
argument_list|)
decl_stmt|;
DECL|field|IGNORE_MALFORMED_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|IGNORE_MALFORMED_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"ignore_malformed"
argument_list|)
decl_stmt|;
annotation|@
name|Inject
DECL|method|GeoDistanceRangeQueryParser
specifier|public
name|GeoDistanceRangeQueryParser
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|names
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|GeoDistanceRangeQueryBuilder
operator|.
name|NAME
block|,
literal|"geoDistanceRange"
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|GeoDistanceRangeQueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|GeoDistanceRangeQueryBuilder
operator|.
name|PROTOTYPE
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|GeoDistanceRangeQueryBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|Float
name|boost
init|=
literal|null
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|GeoPoint
name|point
init|=
literal|null
decl_stmt|;
name|String
name|geohash
init|=
literal|null
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|Object
name|vFrom
init|=
literal|null
decl_stmt|;
name|Object
name|vTo
init|=
literal|null
decl_stmt|;
name|Boolean
name|includeLower
init|=
literal|null
decl_stmt|;
name|Boolean
name|includeUpper
init|=
literal|null
decl_stmt|;
name|DistanceUnit
name|unit
init|=
literal|null
decl_stmt|;
name|GeoDistance
name|geoDistance
init|=
literal|null
decl_stmt|;
name|String
name|optimizeBbox
init|=
literal|null
decl_stmt|;
name|Boolean
name|coerce
init|=
literal|null
decl_stmt|;
name|Boolean
name|ignoreMalformed
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
name|parseContext
operator|.
name|isDeprecatedSetting
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// skip
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
name|point
operator|==
literal|null
condition|)
block|{
name|point
operator|=
operator|new
name|GeoPoint
argument_list|()
expr_stmt|;
block|}
name|GeoUtils
operator|.
name|parseGeoPoint
argument_list|(
name|parser
argument_list|,
name|point
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|currentFieldName
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
name|START_OBJECT
condition|)
block|{
comment|// the json in the format of -> field : { lat : 30, lon : 12 }
name|fieldName
operator|=
name|currentFieldName
expr_stmt|;
if|if
condition|(
name|point
operator|==
literal|null
condition|)
block|{
name|point
operator|=
operator|new
name|GeoPoint
argument_list|()
expr_stmt|;
block|}
name|GeoUtils
operator|.
name|parseGeoPoint
argument_list|(
name|parser
argument_list|,
name|point
argument_list|)
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
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|FROM_FIELD
argument_list|)
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
name|VALUE_NULL
condition|)
block|{                     }
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
name|vFrom
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
comment|// a String
block|}
else|else
block|{
name|vFrom
operator|=
name|parser
operator|.
name|numberValue
argument_list|()
expr_stmt|;
comment|// a Number
block|}
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|TO_FIELD
argument_list|)
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
name|VALUE_NULL
condition|)
block|{                     }
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
name|vTo
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
comment|// a String
block|}
else|else
block|{
name|vTo
operator|=
name|parser
operator|.
name|numberValue
argument_list|()
expr_stmt|;
comment|// a Number
block|}
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|INCLUDE_LOWER_FIELD
argument_list|)
condition|)
block|{
name|includeLower
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|INCLUDE_UPPER_FIELD
argument_list|)
condition|)
block|{
name|includeUpper
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|GT_FIELD
argument_list|)
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
name|VALUE_NULL
condition|)
block|{                     }
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
name|vFrom
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
comment|// a String
block|}
else|else
block|{
name|vFrom
operator|=
name|parser
operator|.
name|numberValue
argument_list|()
expr_stmt|;
comment|// a Number
block|}
name|includeLower
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|GTE_FIELD
argument_list|)
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
name|VALUE_NULL
condition|)
block|{                     }
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
name|vFrom
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
comment|// a String
block|}
else|else
block|{
name|vFrom
operator|=
name|parser
operator|.
name|numberValue
argument_list|()
expr_stmt|;
comment|// a Number
block|}
name|includeLower
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|LT_FIELD
argument_list|)
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
name|VALUE_NULL
condition|)
block|{                     }
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
name|vTo
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
comment|// a String
block|}
else|else
block|{
name|vTo
operator|=
name|parser
operator|.
name|numberValue
argument_list|()
expr_stmt|;
comment|// a Number
block|}
name|includeUpper
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|LTE_FIELD
argument_list|)
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
name|VALUE_NULL
condition|)
block|{                     }
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
name|vTo
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
comment|// a String
block|}
else|else
block|{
name|vTo
operator|=
name|parser
operator|.
name|numberValue
argument_list|()
expr_stmt|;
comment|// a Number
block|}
name|includeUpper
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|UNIT_FIELD
argument_list|)
condition|)
block|{
name|unit
operator|=
name|DistanceUnit
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|DISTANCE_TYPE_FIELD
argument_list|)
condition|)
block|{
name|geoDistance
operator|=
name|GeoDistance
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentFieldName
operator|.
name|endsWith
argument_list|(
name|GeoPointFieldMapper
operator|.
name|Names
operator|.
name|LAT_SUFFIX
argument_list|)
condition|)
block|{
if|if
condition|(
name|point
operator|==
literal|null
condition|)
block|{
name|point
operator|=
operator|new
name|GeoPoint
argument_list|()
expr_stmt|;
block|}
name|point
operator|.
name|resetLat
argument_list|(
name|parser
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|currentFieldName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|currentFieldName
operator|.
name|length
argument_list|()
operator|-
name|GeoPointFieldMapper
operator|.
name|Names
operator|.
name|LAT_SUFFIX
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentFieldName
operator|.
name|endsWith
argument_list|(
name|GeoPointFieldMapper
operator|.
name|Names
operator|.
name|LON_SUFFIX
argument_list|)
condition|)
block|{
if|if
condition|(
name|point
operator|==
literal|null
condition|)
block|{
name|point
operator|=
operator|new
name|GeoPoint
argument_list|()
expr_stmt|;
block|}
name|point
operator|.
name|resetLon
argument_list|(
name|parser
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|currentFieldName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|currentFieldName
operator|.
name|length
argument_list|()
operator|-
name|GeoPointFieldMapper
operator|.
name|Names
operator|.
name|LON_SUFFIX
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentFieldName
operator|.
name|endsWith
argument_list|(
name|GeoPointFieldMapper
operator|.
name|Names
operator|.
name|GEOHASH_SUFFIX
argument_list|)
condition|)
block|{
name|geohash
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
name|fieldName
operator|=
name|currentFieldName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|currentFieldName
operator|.
name|length
argument_list|()
operator|-
name|GeoPointFieldMapper
operator|.
name|Names
operator|.
name|GEOHASH_SUFFIX
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|NAME_FIELD
argument_list|)
condition|)
block|{
name|queryName
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
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|BOOST_FIELD
argument_list|)
condition|)
block|{
name|boost
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|OPTIMIZE_BBOX_FIELD
argument_list|)
condition|)
block|{
name|optimizeBbox
operator|=
name|parser
operator|.
name|textOrNull
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|COERCE_FIELD
argument_list|)
condition|)
block|{
name|coerce
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|IGNORE_MALFORMED_FIELD
argument_list|)
condition|)
block|{
name|ignoreMalformed
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|point
operator|==
literal|null
condition|)
block|{
name|point
operator|=
operator|new
name|GeoPoint
argument_list|()
expr_stmt|;
block|}
name|point
operator|.
name|resetFromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|currentFieldName
expr_stmt|;
block|}
block|}
block|}
name|GeoDistanceRangeQueryBuilder
name|queryBuilder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|point
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|point
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|geohash
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|geohash
argument_list|(
name|geohash
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|vFrom
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|from
argument_list|(
name|vFrom
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|vTo
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|to
argument_list|(
name|vTo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeUpper
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|includeUpper
argument_list|(
name|includeUpper
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeLower
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|includeLower
argument_list|(
name|includeLower
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|unit
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|unit
argument_list|(
name|unit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|geoDistance
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|geoDistance
argument_list|(
name|geoDistance
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|optimizeBbox
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|optimizeBbox
argument_list|(
name|optimizeBbox
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|coerce
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|coerce
argument_list|(
name|coerce
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ignoreMalformed
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|ignoreMalformed
argument_list|(
name|ignoreMalformed
argument_list|)
expr_stmt|;
block|}
return|return
name|queryBuilder
return|;
block|}
block|}
end_class

end_unit

