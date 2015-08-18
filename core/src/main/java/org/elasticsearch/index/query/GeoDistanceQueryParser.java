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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|GeoHashUtils
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
name|fielddata
operator|.
name|IndexGeoPointFieldData
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
name|MappedFieldType
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|geo
operator|.
name|GeoDistanceRangeQuery
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
DECL|class|GeoDistanceQueryParser
specifier|public
class|class
name|GeoDistanceQueryParser
extends|extends
name|BaseQueryParserTemp
block|{
annotation|@
name|Inject
DECL|method|GeoDistanceQueryParser
specifier|public
name|GeoDistanceQueryParser
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
name|GeoDistanceQueryBuilder
operator|.
name|NAME
block|,
literal|"geoDistance"
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|QueryParseContext
name|parseContext
init|=
name|context
operator|.
name|parseContext
argument_list|()
decl_stmt|;
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
name|float
name|boost
init|=
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
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
operator|new
name|GeoPoint
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|double
name|distance
init|=
literal|0
decl_stmt|;
name|Object
name|vDistance
init|=
literal|null
decl_stmt|;
name|DistanceUnit
name|unit
init|=
name|DistanceUnit
operator|.
name|DEFAULT
decl_stmt|;
name|GeoDistance
name|geoDistance
init|=
name|GeoDistance
operator|.
name|DEFAULT
decl_stmt|;
name|String
name|optimizeBbox
init|=
literal|"memory"
decl_stmt|;
specifier|final
name|boolean
name|indexCreatedBeforeV2_0
init|=
name|parseContext
operator|.
name|shardContext
argument_list|()
operator|.
name|indexVersionCreated
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_0_0
argument_list|)
decl_stmt|;
name|boolean
name|coerce
init|=
literal|false
decl_stmt|;
name|boolean
name|ignoreMalformed
init|=
literal|false
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
name|fieldName
operator|=
name|currentFieldName
expr_stmt|;
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
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
comment|// the json in the format of -> field : { lat : 30, lon : 12 }
name|String
name|currentName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|fieldName
operator|=
name|currentFieldName
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
name|currentName
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
name|currentName
operator|.
name|equals
argument_list|(
name|GeoPointFieldMapper
operator|.
name|Names
operator|.
name|LAT
argument_list|)
condition|)
block|{
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
block|}
elseif|else
if|if
condition|(
name|currentName
operator|.
name|equals
argument_list|(
name|GeoPointFieldMapper
operator|.
name|Names
operator|.
name|LON
argument_list|)
condition|)
block|{
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
block|}
elseif|else
if|if
condition|(
name|currentName
operator|.
name|equals
argument_list|(
name|GeoPointFieldMapper
operator|.
name|Names
operator|.
name|GEOHASH
argument_list|)
condition|)
block|{
name|GeoHashUtils
operator|.
name|decode
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|,
name|point
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[geo_distance] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
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
name|currentFieldName
operator|.
name|equals
argument_list|(
literal|"distance"
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
name|VALUE_STRING
condition|)
block|{
name|vDistance
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
name|vDistance
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
name|currentFieldName
operator|.
name|equals
argument_list|(
literal|"unit"
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
name|currentFieldName
operator|.
name|equals
argument_list|(
literal|"distance_type"
argument_list|)
operator|||
name|currentFieldName
operator|.
name|equals
argument_list|(
literal|"distanceType"
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
name|GeoHashUtils
operator|.
name|decode
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|,
name|point
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
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"optimize_bbox"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"optimizeBbox"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"coerce"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
operator|(
name|indexCreatedBeforeV2_0
operator|&&
literal|"normalize"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|)
condition|)
block|{
name|coerce
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|coerce
operator|==
literal|true
condition|)
block|{
name|ignoreMalformed
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"ignore_malformed"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|&&
name|coerce
operator|==
literal|false
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
comment|// validation was not available prior to 2.x, so to support bwc percolation queries we only ignore_malformed on 2.x created indexes
if|if
condition|(
operator|!
name|indexCreatedBeforeV2_0
operator|&&
operator|!
name|ignoreMalformed
condition|)
block|{
if|if
condition|(
name|point
operator|.
name|lat
argument_list|()
operator|>
literal|90.0
operator|||
name|point
operator|.
name|lat
argument_list|()
operator|<
operator|-
literal|90.0
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"illegal latitude value [{}] for [{}]"
argument_list|,
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|GeoDistanceQueryBuilder
operator|.
name|NAME
argument_list|)
throw|;
block|}
if|if
condition|(
name|point
operator|.
name|lon
argument_list|()
operator|>
literal|180.0
operator|||
name|point
operator|.
name|lon
argument_list|()
operator|<
operator|-
literal|180
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"illegal longitude value [{}] for [{}]"
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|GeoDistanceQueryBuilder
operator|.
name|NAME
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|coerce
condition|)
block|{
name|GeoUtils
operator|.
name|normalizePoint
argument_list|(
name|point
argument_list|,
name|coerce
argument_list|,
name|coerce
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|vDistance
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"geo_distance requires 'distance' to be specified"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|vDistance
operator|instanceof
name|Number
condition|)
block|{
name|distance
operator|=
name|DistanceUnit
operator|.
name|DEFAULT
operator|.
name|convert
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|vDistance
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|distance
operator|=
name|DistanceUnit
operator|.
name|parse
argument_list|(
operator|(
name|String
operator|)
name|vDistance
argument_list|,
name|unit
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
name|distance
operator|=
name|geoDistance
operator|.
name|normalize
argument_list|(
name|distance
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|MappedFieldType
name|fieldType
init|=
name|parseContext
operator|.
name|shardContext
argument_list|()
operator|.
name|fieldMapper
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"failed to find geo_point field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|fieldType
operator|instanceof
name|GeoPointFieldMapper
operator|.
name|GeoPointFieldType
operator|)
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"field ["
operator|+
name|fieldName
operator|+
literal|"] is not a geo_point field"
argument_list|)
throw|;
block|}
name|GeoPointFieldMapper
operator|.
name|GeoPointFieldType
name|geoFieldType
init|=
operator|(
operator|(
name|GeoPointFieldMapper
operator|.
name|GeoPointFieldType
operator|)
name|fieldType
operator|)
decl_stmt|;
name|IndexGeoPointFieldData
name|indexFieldData
init|=
name|context
operator|.
name|getForField
argument_list|(
name|fieldType
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|GeoDistanceRangeQuery
argument_list|(
name|point
argument_list|,
literal|null
argument_list|,
name|distance
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|geoDistance
argument_list|,
name|geoFieldType
argument_list|,
name|indexFieldData
argument_list|,
name|optimizeBbox
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|addNamedQuery
argument_list|(
name|queryName
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|GeoDistanceQueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|GeoDistanceQueryBuilder
operator|.
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit

