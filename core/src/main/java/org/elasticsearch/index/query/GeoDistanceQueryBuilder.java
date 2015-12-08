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
name|GeoPointDistanceQuery
import|;
end_import

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
name|Strings
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
name|XContentBuilder
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
name|BaseGeoPointFieldMapper
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
name|GeoPointFieldMapperLegacy
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_comment
comment|/**  * Filter results of a query to include only those within a specific distance to some  * geo point.  * */
end_comment

begin_class
DECL|class|GeoDistanceQueryBuilder
specifier|public
class|class
name|GeoDistanceQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|GeoDistanceQueryBuilder
argument_list|>
block|{
comment|/** Name of the query in the query dsl. */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"geo_distance"
decl_stmt|;
comment|/** Default for latitude normalization (as of this writing true).*/
DECL|field|DEFAULT_NORMALIZE_LAT
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_NORMALIZE_LAT
init|=
literal|true
decl_stmt|;
comment|/** Default for longitude normalization (as of this writing true). */
DECL|field|DEFAULT_NORMALIZE_LON
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_NORMALIZE_LON
init|=
literal|true
decl_stmt|;
comment|/** Default for distance unit computation. */
DECL|field|DEFAULT_DISTANCE_UNIT
specifier|public
specifier|static
specifier|final
name|DistanceUnit
name|DEFAULT_DISTANCE_UNIT
init|=
name|DistanceUnit
operator|.
name|DEFAULT
decl_stmt|;
comment|/** Default for geo distance computation. */
DECL|field|DEFAULT_GEO_DISTANCE
specifier|public
specifier|static
specifier|final
name|GeoDistance
name|DEFAULT_GEO_DISTANCE
init|=
name|GeoDistance
operator|.
name|DEFAULT
decl_stmt|;
comment|/** Default for optimising query through pre computed bounding box query. */
DECL|field|DEFAULT_OPTIMIZE_BBOX
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_OPTIMIZE_BBOX
init|=
literal|"memory"
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
comment|/** Distance from center to cover. */
DECL|field|distance
specifier|private
name|double
name|distance
decl_stmt|;
comment|/** Point to use as center. */
DECL|field|center
specifier|private
name|GeoPoint
name|center
init|=
operator|new
name|GeoPoint
argument_list|(
name|Double
operator|.
name|NaN
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
decl_stmt|;
comment|/** Algorithm to use for distance computation. */
DECL|field|geoDistance
specifier|private
name|GeoDistance
name|geoDistance
init|=
name|DEFAULT_GEO_DISTANCE
decl_stmt|;
comment|/** Whether or not to use a bbox for pre-filtering. TODO change to enum? */
DECL|field|optimizeBbox
specifier|private
name|String
name|optimizeBbox
init|=
name|DEFAULT_OPTIMIZE_BBOX
decl_stmt|;
comment|/** How strict should geo coordinate validation be? */
DECL|field|validationMethod
specifier|private
name|GeoValidationMethod
name|validationMethod
init|=
name|GeoValidationMethod
operator|.
name|DEFAULT
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|GeoDistanceQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|GeoDistanceQueryBuilder
argument_list|(
literal|"_na_"
argument_list|)
decl_stmt|;
comment|/**      * Construct new GeoDistanceQueryBuilder.      * @param fieldName name of indexed geo field to operate distance computation on.      * */
DECL|method|GeoDistanceQueryBuilder
specifier|public
name|GeoDistanceQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fieldName must not be null or empty"
argument_list|)
throw|;
block|}
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
comment|/** Name of the field this query is operating on. */
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldName
return|;
block|}
comment|/** Sets the center point for the query.      * @param point the center of the query      **/
DECL|method|point
specifier|public
name|GeoDistanceQueryBuilder
name|point
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
block|{
if|if
condition|(
name|point
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"center point must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|center
operator|=
name|point
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the center point of the query.      * @param lat latitude of center      * @param lon longitude of center      * */
DECL|method|point
specifier|public
name|GeoDistanceQueryBuilder
name|point
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|this
operator|.
name|center
operator|=
operator|new
name|GeoPoint
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the center point of the distance query. */
DECL|method|point
specifier|public
name|GeoPoint
name|point
parameter_list|()
block|{
return|return
name|this
operator|.
name|center
return|;
block|}
comment|/** Sets the distance from the center using the default distance unit.*/
DECL|method|distance
specifier|public
name|GeoDistanceQueryBuilder
name|distance
parameter_list|(
name|String
name|distance
parameter_list|)
block|{
return|return
name|distance
argument_list|(
name|distance
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
comment|/** Sets the distance from the center for this query. */
DECL|method|distance
specifier|public
name|GeoDistanceQueryBuilder
name|distance
parameter_list|(
name|String
name|distance
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|distance
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"distance must not be null or empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|unit
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"distance unit must not be null"
argument_list|)
throw|;
block|}
name|double
name|newDistance
init|=
name|DistanceUnit
operator|.
name|parse
argument_list|(
name|distance
argument_list|,
name|unit
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|newDistance
operator|<=
literal|0.0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"distance must be greater than zero"
argument_list|)
throw|;
block|}
name|this
operator|.
name|distance
operator|=
name|newDistance
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Sets the distance from the center for this query. */
DECL|method|distance
specifier|public
name|GeoDistanceQueryBuilder
name|distance
parameter_list|(
name|double
name|distance
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
return|return
name|distance
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|distance
argument_list|)
argument_list|,
name|unit
argument_list|)
return|;
block|}
comment|/** Returns the distance configured as radius. */
DECL|method|distance
specifier|public
name|double
name|distance
parameter_list|()
block|{
return|return
name|distance
return|;
block|}
comment|/** Sets the center point for this query. */
DECL|method|geohash
specifier|public
name|GeoDistanceQueryBuilder
name|geohash
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|geohash
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"geohash must not be null or empty"
argument_list|)
throw|;
block|}
name|this
operator|.
name|center
operator|.
name|resetFromGeoHash
argument_list|(
name|geohash
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Which type of geo distance calculation method to use. */
DECL|method|geoDistance
specifier|public
name|GeoDistanceQueryBuilder
name|geoDistance
parameter_list|(
name|GeoDistance
name|geoDistance
parameter_list|)
block|{
if|if
condition|(
name|geoDistance
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"geoDistance must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|geoDistance
operator|=
name|geoDistance
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns geo distance calculation type to use. */
DECL|method|geoDistance
specifier|public
name|GeoDistance
name|geoDistance
parameter_list|()
block|{
return|return
name|this
operator|.
name|geoDistance
return|;
block|}
comment|/**      * Set this to memory or indexed if before running the distance      * calculation you want to limit the candidates to hits in the      * enclosing bounding box.      **/
DECL|method|optimizeBbox
specifier|public
name|GeoDistanceQueryBuilder
name|optimizeBbox
parameter_list|(
name|String
name|optimizeBbox
parameter_list|)
block|{
if|if
condition|(
name|optimizeBbox
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"optimizeBbox must not be null"
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|optimizeBbox
condition|)
block|{
case|case
literal|"none"
case|:
case|case
literal|"memory"
case|:
case|case
literal|"indexed"
case|:
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"optimizeBbox must be one of [none, memory, indexed]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|optimizeBbox
operator|=
name|optimizeBbox
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns whether or not to run a BoundingBox query prior to      * distance query for optimization purposes.*/
DECL|method|optimizeBbox
specifier|public
name|String
name|optimizeBbox
parameter_list|()
block|{
return|return
name|this
operator|.
name|optimizeBbox
return|;
block|}
comment|/** Set validaton method for geo coordinates. */
DECL|method|setValidationMethod
specifier|public
name|void
name|setValidationMethod
parameter_list|(
name|GeoValidationMethod
name|method
parameter_list|)
block|{
name|this
operator|.
name|validationMethod
operator|=
name|method
expr_stmt|;
block|}
comment|/** Returns validation method for geo coordinates. */
DECL|method|getValidationMethod
specifier|public
name|GeoValidationMethod
name|getValidationMethod
parameter_list|()
block|{
return|return
name|this
operator|.
name|validationMethod
return|;
block|}
annotation|@
name|Override
DECL|method|doToQuery
specifier|protected
name|Query
name|doToQuery
parameter_list|(
name|QueryShardContext
name|shardContext
parameter_list|)
throws|throws
name|IOException
block|{
name|MappedFieldType
name|fieldType
init|=
name|shardContext
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
name|QueryShardException
argument_list|(
name|shardContext
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
name|BaseGeoPointFieldMapper
operator|.
name|GeoPointFieldType
operator|)
condition|)
block|{
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|shardContext
argument_list|,
literal|"field ["
operator|+
name|fieldName
operator|+
literal|"] is not a geo_point field"
argument_list|)
throw|;
block|}
name|QueryValidationException
name|exception
init|=
name|checkLatLon
argument_list|(
name|shardContext
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
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|shardContext
argument_list|,
literal|"couldn't validate latitude/ longitude values"
argument_list|,
name|exception
argument_list|)
throw|;
block|}
if|if
condition|(
name|GeoValidationMethod
operator|.
name|isCoerce
argument_list|(
name|validationMethod
argument_list|)
condition|)
block|{
name|GeoUtils
operator|.
name|normalizePoint
argument_list|(
name|center
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|double
name|normDistance
init|=
name|geoDistance
operator|.
name|normalize
argument_list|(
name|this
operator|.
name|distance
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardContext
operator|.
name|indexVersionCreated
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_2_0
argument_list|)
condition|)
block|{
name|GeoPointFieldMapperLegacy
operator|.
name|GeoPointFieldType
name|geoFieldType
init|=
operator|(
operator|(
name|GeoPointFieldMapperLegacy
operator|.
name|GeoPointFieldType
operator|)
name|fieldType
operator|)
decl_stmt|;
name|IndexGeoPointFieldData
name|indexFieldData
init|=
name|shardContext
operator|.
name|getForField
argument_list|(
name|fieldType
argument_list|)
decl_stmt|;
return|return
operator|new
name|GeoDistanceRangeQuery
argument_list|(
name|center
argument_list|,
literal|null
argument_list|,
name|normDistance
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
return|;
block|}
name|normDistance
operator|=
name|GeoUtils
operator|.
name|maxRadialDistance
argument_list|(
name|center
argument_list|,
name|normDistance
argument_list|)
expr_stmt|;
return|return
operator|new
name|GeoPointDistanceQuery
argument_list|(
name|fieldType
operator|.
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|,
name|center
operator|.
name|lon
argument_list|()
argument_list|,
name|center
operator|.
name|lat
argument_list|()
argument_list|,
name|normDistance
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|protected
name|void
name|doXContent
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
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
name|fieldName
argument_list|)
operator|.
name|value
argument_list|(
name|center
operator|.
name|lon
argument_list|()
argument_list|)
operator|.
name|value
argument_list|(
name|center
operator|.
name|lat
argument_list|()
argument_list|)
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|GeoDistanceQueryParser
operator|.
name|DISTANCE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|distance
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|GeoDistanceQueryParser
operator|.
name|DISTANCE_TYPE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|geoDistance
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|GeoDistanceQueryParser
operator|.
name|OPTIMIZE_BBOX_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|optimizeBbox
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|GeoDistanceQueryParser
operator|.
name|VALIDATION_METHOD_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|validationMethod
argument_list|)
expr_stmt|;
name|printBoostAndQueryName
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|center
argument_list|,
name|geoDistance
argument_list|,
name|optimizeBbox
argument_list|,
name|distance
argument_list|,
name|validationMethod
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|GeoDistanceQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|,
name|other
operator|.
name|fieldName
argument_list|)
operator|&&
operator|(
name|distance
operator|==
name|other
operator|.
name|distance
operator|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|validationMethod
argument_list|,
name|other
operator|.
name|validationMethod
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|center
argument_list|,
name|other
operator|.
name|center
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|optimizeBbox
argument_list|,
name|other
operator|.
name|optimizeBbox
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|geoDistance
argument_list|,
name|other
operator|.
name|geoDistance
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|GeoDistanceQueryBuilder
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fieldName
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|GeoDistanceQueryBuilder
name|result
init|=
operator|new
name|GeoDistanceQueryBuilder
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|result
operator|.
name|distance
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|result
operator|.
name|validationMethod
operator|=
name|GeoValidationMethod
operator|.
name|readGeoValidationMethodFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|result
operator|.
name|center
operator|=
name|in
operator|.
name|readGeoPoint
argument_list|()
expr_stmt|;
name|result
operator|.
name|optimizeBbox
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|result
operator|.
name|geoDistance
operator|=
name|GeoDistance
operator|.
name|readGeoDistanceFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
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
name|out
operator|.
name|writeString
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|distance
argument_list|)
expr_stmt|;
name|validationMethod
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeGeoPoint
argument_list|(
name|center
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|optimizeBbox
argument_list|)
expr_stmt|;
name|geoDistance
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|checkLatLon
specifier|private
name|QueryValidationException
name|checkLatLon
parameter_list|(
name|boolean
name|indexCreatedBeforeV2_0
parameter_list|)
block|{
comment|// validation was not available prior to 2.x, so to support bwc percolation queries we only ignore_malformed on 2.x created indexes
if|if
condition|(
name|GeoValidationMethod
operator|.
name|isIgnoreMalformed
argument_list|(
name|validationMethod
argument_list|)
operator|||
name|indexCreatedBeforeV2_0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|QueryValidationException
name|validationException
init|=
literal|null
decl_stmt|;
comment|// For everything post 2.0, validate latitude and longitude unless validation was explicitly turned off
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLatitude
argument_list|(
name|center
operator|.
name|getLat
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"center point latitude is invalid: "
operator|+
name|center
operator|.
name|getLat
argument_list|()
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLongitude
argument_list|(
name|center
operator|.
name|getLon
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"center point longitude is invalid: "
operator|+
name|center
operator|.
name|getLon
argument_list|()
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
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
name|NAME
return|;
block|}
block|}
end_class

end_unit

