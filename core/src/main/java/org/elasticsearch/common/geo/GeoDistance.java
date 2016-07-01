begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
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
name|util
operator|.
name|Bits
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
name|util
operator|.
name|SloppyMath
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
name|index
operator|.
name|fielddata
operator|.
name|FieldData
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
name|GeoPointValues
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
name|MultiGeoPointValues
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
name|NumericDoubleValues
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
name|SortedNumericDoubleValues
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
name|SortingNumericDoubleValues
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

begin_comment
comment|/**  * Geo distance calculation.  */
end_comment

begin_enum
DECL|enum|GeoDistance
specifier|public
enum|enum
name|GeoDistance
implements|implements
name|Writeable
block|{
comment|/**      * Calculates distance as points on a plane. Faster, but less accurate than {@link #ARC}.      */
DECL|enum constant|PLANE
name|PLANE
block|{
annotation|@
name|Override
specifier|public
name|double
name|calculate
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
name|double
name|px
init|=
name|targetLongitude
operator|-
name|sourceLongitude
decl_stmt|;
name|double
name|py
init|=
name|targetLatitude
operator|-
name|sourceLatitude
decl_stmt|;
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|px
operator|*
name|px
operator|+
name|py
operator|*
name|py
argument_list|)
operator|*
name|unit
operator|.
name|getDistancePerDegree
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|normalize
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
return|;
block|}
annotation|@
name|Override
specifier|public
name|FixedSourceDistance
name|fixedSourceDistance
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
return|return
operator|new
name|PlaneFixedSourceDistance
argument_list|(
name|sourceLatitude
argument_list|,
name|sourceLongitude
argument_list|,
name|unit
argument_list|)
return|;
block|}
block|}
block|,
comment|/**      * Calculates distance factor.      */
DECL|enum constant|FACTOR
name|FACTOR
block|{
annotation|@
name|Override
specifier|public
name|double
name|calculate
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
name|double
name|longitudeDifference
init|=
name|targetLongitude
operator|-
name|sourceLongitude
decl_stmt|;
name|double
name|a
init|=
name|Math
operator|.
name|toRadians
argument_list|(
literal|90D
operator|-
name|sourceLatitude
argument_list|)
decl_stmt|;
name|double
name|c
init|=
name|Math
operator|.
name|toRadians
argument_list|(
literal|90D
operator|-
name|targetLatitude
argument_list|)
decl_stmt|;
return|return
operator|(
name|Math
operator|.
name|cos
argument_list|(
name|a
argument_list|)
operator|*
name|Math
operator|.
name|cos
argument_list|(
name|c
argument_list|)
operator|)
operator|+
operator|(
name|Math
operator|.
name|sin
argument_list|(
name|a
argument_list|)
operator|*
name|Math
operator|.
name|sin
argument_list|(
name|c
argument_list|)
operator|*
name|Math
operator|.
name|cos
argument_list|(
name|Math
operator|.
name|toRadians
argument_list|(
name|longitudeDifference
argument_list|)
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|normalize
parameter_list|(
name|double
name|distance
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
return|return
name|Math
operator|.
name|cos
argument_list|(
name|distance
operator|/
name|unit
operator|.
name|getEarthRadius
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FixedSourceDistance
name|fixedSourceDistance
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
return|return
operator|new
name|FactorFixedSourceDistance
argument_list|(
name|sourceLatitude
argument_list|,
name|sourceLongitude
argument_list|,
name|unit
argument_list|)
return|;
block|}
block|}
block|,
comment|/**      * Calculates distance as points on a globe.      */
DECL|enum constant|ARC
name|ARC
block|{
annotation|@
name|Override
specifier|public
name|double
name|calculate
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
name|double
name|result
init|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|sourceLatitude
argument_list|,
name|sourceLongitude
argument_list|,
name|targetLatitude
argument_list|,
name|targetLongitude
argument_list|)
decl_stmt|;
return|return
name|unit
operator|.
name|fromMeters
argument_list|(
name|result
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|normalize
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
return|;
block|}
annotation|@
name|Override
specifier|public
name|FixedSourceDistance
name|fixedSourceDistance
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
return|return
operator|new
name|ArcFixedSourceDistance
argument_list|(
name|sourceLatitude
argument_list|,
name|sourceLongitude
argument_list|,
name|unit
argument_list|)
return|;
block|}
block|}
block|,
comment|/**      * Calculates distance as points on a globe in a sloppy way. Close to the pole areas the accuracy      * of this function decreases.      */
DECL|enum constant|Deprecated
annotation|@
name|Deprecated
DECL|enum constant|SLOPPY_ARC
name|SLOPPY_ARC
block|{
annotation|@
name|Override
specifier|public
name|double
name|normalize
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
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|calculate
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
return|return
name|unit
operator|.
name|fromMeters
argument_list|(
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|sourceLatitude
argument_list|,
name|sourceLongitude
argument_list|,
name|targetLatitude
argument_list|,
name|targetLongitude
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FixedSourceDistance
name|fixedSourceDistance
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
return|return
operator|new
name|SloppyArcFixedSourceDistance
argument_list|(
name|sourceLatitude
argument_list|,
name|sourceLongitude
argument_list|,
name|unit
argument_list|)
return|;
block|}
block|}
block|;
DECL|method|readFromStream
specifier|public
specifier|static
name|GeoDistance
name|readFromStream
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ord
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
operator|||
name|ord
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown GeoDistance ordinal ["
operator|+
name|ord
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|GeoDistance
operator|.
name|values
argument_list|()
index|[
name|ord
index|]
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
name|writeVInt
argument_list|(
name|this
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Default {@link GeoDistance} function. This method should be used, If no specific function has been selected.      * This is an alias for<code>SLOPPY_ARC</code>      */
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|GeoDistance
name|DEFAULT
init|=
name|SLOPPY_ARC
decl_stmt|;
DECL|method|normalize
specifier|public
specifier|abstract
name|double
name|normalize
parameter_list|(
name|double
name|distance
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
function_decl|;
DECL|method|calculate
specifier|public
specifier|abstract
name|double
name|calculate
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
function_decl|;
DECL|method|fixedSourceDistance
specifier|public
specifier|abstract
name|FixedSourceDistance
name|fixedSourceDistance
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
function_decl|;
DECL|field|MIN_LAT
specifier|private
specifier|static
specifier|final
name|double
name|MIN_LAT
init|=
name|Math
operator|.
name|toRadians
argument_list|(
operator|-
literal|90d
argument_list|)
decl_stmt|;
comment|// -PI/2
DECL|field|MAX_LAT
specifier|private
specifier|static
specifier|final
name|double
name|MAX_LAT
init|=
name|Math
operator|.
name|toRadians
argument_list|(
literal|90d
argument_list|)
decl_stmt|;
comment|//  PI/2
DECL|field|MIN_LON
specifier|private
specifier|static
specifier|final
name|double
name|MIN_LON
init|=
name|Math
operator|.
name|toRadians
argument_list|(
operator|-
literal|180d
argument_list|)
decl_stmt|;
comment|// -PI
DECL|field|MAX_LON
specifier|private
specifier|static
specifier|final
name|double
name|MAX_LON
init|=
name|Math
operator|.
name|toRadians
argument_list|(
literal|180d
argument_list|)
decl_stmt|;
comment|//  PI
DECL|method|distanceBoundingCheck
specifier|public
specifier|static
name|DistanceBoundingCheck
name|distanceBoundingCheck
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|double
name|distance
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
comment|// angular distance in radians on a great circle
comment|// assume worst-case: use the minor axis
name|double
name|radDist
init|=
name|unit
operator|.
name|toMeters
argument_list|(
name|distance
argument_list|)
operator|/
name|GeoUtils
operator|.
name|EARTH_SEMI_MINOR_AXIS
decl_stmt|;
name|double
name|radLat
init|=
name|Math
operator|.
name|toRadians
argument_list|(
name|sourceLatitude
argument_list|)
decl_stmt|;
name|double
name|radLon
init|=
name|Math
operator|.
name|toRadians
argument_list|(
name|sourceLongitude
argument_list|)
decl_stmt|;
name|double
name|minLat
init|=
name|radLat
operator|-
name|radDist
decl_stmt|;
name|double
name|maxLat
init|=
name|radLat
operator|+
name|radDist
decl_stmt|;
name|double
name|minLon
operator|,
name|maxLon
expr_stmt|;
if|if
condition|(
name|minLat
operator|>
name|MIN_LAT
operator|&&
name|maxLat
operator|<
name|MAX_LAT
condition|)
block|{
name|double
name|deltaLon
init|=
name|Math
operator|.
name|asin
argument_list|(
name|Math
operator|.
name|sin
argument_list|(
name|radDist
argument_list|)
operator|/
name|Math
operator|.
name|cos
argument_list|(
name|radLat
argument_list|)
argument_list|)
decl_stmt|;
name|minLon
operator|=
name|radLon
operator|-
name|deltaLon
expr_stmt|;
if|if
condition|(
name|minLon
operator|<
name|MIN_LON
condition|)
name|minLon
operator|+=
literal|2d
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
name|maxLon
operator|=
name|radLon
operator|+
name|deltaLon
expr_stmt|;
if|if
condition|(
name|maxLon
operator|>
name|MAX_LON
condition|)
name|maxLon
operator|-=
literal|2d
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
block|}
else|else
block|{
comment|// a pole is within the distance
name|minLat
operator|=
name|Math
operator|.
name|max
argument_list|(
name|minLat
argument_list|,
name|MIN_LAT
argument_list|)
expr_stmt|;
name|maxLat
operator|=
name|Math
operator|.
name|min
argument_list|(
name|maxLat
argument_list|,
name|MAX_LAT
argument_list|)
expr_stmt|;
name|minLon
operator|=
name|MIN_LON
expr_stmt|;
name|maxLon
operator|=
name|MAX_LON
expr_stmt|;
block|}
name|GeoPoint
name|topLeft
init|=
operator|new
name|GeoPoint
argument_list|(
name|Math
operator|.
name|toDegrees
argument_list|(
name|maxLat
argument_list|)
argument_list|,
name|Math
operator|.
name|toDegrees
argument_list|(
name|minLon
argument_list|)
argument_list|)
decl_stmt|;
name|GeoPoint
name|bottomRight
init|=
operator|new
name|GeoPoint
argument_list|(
name|Math
operator|.
name|toDegrees
argument_list|(
name|minLat
argument_list|)
argument_list|,
name|Math
operator|.
name|toDegrees
argument_list|(
name|maxLon
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|minLon
operator|>
name|maxLon
condition|)
block|{
return|return
operator|new
name|Meridian180DistanceBoundingCheck
argument_list|(
name|topLeft
argument_list|,
name|bottomRight
argument_list|)
return|;
block|}
return|return
operator|new
name|SimpleDistanceBoundingCheck
argument_list|(
name|topLeft
argument_list|,
name|bottomRight
argument_list|)
return|;
block|}
comment|/**      * Get a {@link GeoDistance} according to a given name. Valid values are      *      *<ul>      *<li><b>plane</b> for<code>GeoDistance.PLANE</code></li>      *<li><b>sloppy_arc</b> for<code>GeoDistance.SLOPPY_ARC</code></li>      *<li><b>factor</b> for<code>GeoDistance.FACTOR</code></li>      *<li><b>arc</b> for<code>GeoDistance.ARC</code></li>      *</ul>      *      * @param name name of the {@link GeoDistance}      * @return a {@link GeoDistance}      */
DECL|method|fromString
specifier|public
specifier|static
name|GeoDistance
name|fromString
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|name
operator|=
name|name
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"plane"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|PLANE
return|;
block|}
elseif|else
if|if
condition|(
literal|"arc"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|ARC
return|;
block|}
elseif|else
if|if
condition|(
literal|"sloppy_arc"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|SLOPPY_ARC
return|;
block|}
elseif|else
if|if
condition|(
literal|"factor"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|FACTOR
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No geo distance for ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|interface|FixedSourceDistance
specifier|public
specifier|static
interface|interface
name|FixedSourceDistance
block|{
DECL|method|calculate
name|double
name|calculate
parameter_list|(
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|)
function_decl|;
block|}
DECL|interface|DistanceBoundingCheck
specifier|public
specifier|static
interface|interface
name|DistanceBoundingCheck
block|{
DECL|method|isWithin
name|boolean
name|isWithin
parameter_list|(
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|)
function_decl|;
DECL|method|topLeft
name|GeoPoint
name|topLeft
parameter_list|()
function_decl|;
DECL|method|bottomRight
name|GeoPoint
name|bottomRight
parameter_list|()
function_decl|;
block|}
DECL|field|ALWAYS_INSTANCE
specifier|public
specifier|static
specifier|final
name|AlwaysDistanceBoundingCheck
name|ALWAYS_INSTANCE
init|=
operator|new
name|AlwaysDistanceBoundingCheck
argument_list|()
decl_stmt|;
DECL|class|AlwaysDistanceBoundingCheck
specifier|private
specifier|static
class|class
name|AlwaysDistanceBoundingCheck
implements|implements
name|DistanceBoundingCheck
block|{
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|topLeft
specifier|public
name|GeoPoint
name|topLeft
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|bottomRight
specifier|public
name|GeoPoint
name|bottomRight
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|Meridian180DistanceBoundingCheck
specifier|public
specifier|static
class|class
name|Meridian180DistanceBoundingCheck
implements|implements
name|DistanceBoundingCheck
block|{
DECL|field|topLeft
specifier|private
specifier|final
name|GeoPoint
name|topLeft
decl_stmt|;
DECL|field|bottomRight
specifier|private
specifier|final
name|GeoPoint
name|bottomRight
decl_stmt|;
DECL|method|Meridian180DistanceBoundingCheck
specifier|public
name|Meridian180DistanceBoundingCheck
parameter_list|(
name|GeoPoint
name|topLeft
parameter_list|,
name|GeoPoint
name|bottomRight
parameter_list|)
block|{
name|this
operator|.
name|topLeft
operator|=
name|topLeft
expr_stmt|;
name|this
operator|.
name|bottomRight
operator|=
name|bottomRight
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|)
block|{
return|return
operator|(
name|targetLatitude
operator|>=
name|bottomRight
operator|.
name|lat
argument_list|()
operator|&&
name|targetLatitude
operator|<=
name|topLeft
operator|.
name|lat
argument_list|()
operator|)
operator|&&
operator|(
name|targetLongitude
operator|>=
name|topLeft
operator|.
name|lon
argument_list|()
operator|||
name|targetLongitude
operator|<=
name|bottomRight
operator|.
name|lon
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|topLeft
specifier|public
name|GeoPoint
name|topLeft
parameter_list|()
block|{
return|return
name|topLeft
return|;
block|}
annotation|@
name|Override
DECL|method|bottomRight
specifier|public
name|GeoPoint
name|bottomRight
parameter_list|()
block|{
return|return
name|bottomRight
return|;
block|}
block|}
DECL|class|SimpleDistanceBoundingCheck
specifier|public
specifier|static
class|class
name|SimpleDistanceBoundingCheck
implements|implements
name|DistanceBoundingCheck
block|{
DECL|field|topLeft
specifier|private
specifier|final
name|GeoPoint
name|topLeft
decl_stmt|;
DECL|field|bottomRight
specifier|private
specifier|final
name|GeoPoint
name|bottomRight
decl_stmt|;
DECL|method|SimpleDistanceBoundingCheck
specifier|public
name|SimpleDistanceBoundingCheck
parameter_list|(
name|GeoPoint
name|topLeft
parameter_list|,
name|GeoPoint
name|bottomRight
parameter_list|)
block|{
name|this
operator|.
name|topLeft
operator|=
name|topLeft
expr_stmt|;
name|this
operator|.
name|bottomRight
operator|=
name|bottomRight
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|)
block|{
return|return
operator|(
name|targetLatitude
operator|>=
name|bottomRight
operator|.
name|lat
argument_list|()
operator|&&
name|targetLatitude
operator|<=
name|topLeft
operator|.
name|lat
argument_list|()
operator|)
operator|&&
operator|(
name|targetLongitude
operator|>=
name|topLeft
operator|.
name|lon
argument_list|()
operator|&&
name|targetLongitude
operator|<=
name|bottomRight
operator|.
name|lon
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|topLeft
specifier|public
name|GeoPoint
name|topLeft
parameter_list|()
block|{
return|return
name|topLeft
return|;
block|}
annotation|@
name|Override
DECL|method|bottomRight
specifier|public
name|GeoPoint
name|bottomRight
parameter_list|()
block|{
return|return
name|bottomRight
return|;
block|}
block|}
DECL|class|PlaneFixedSourceDistance
specifier|public
specifier|static
class|class
name|PlaneFixedSourceDistance
implements|implements
name|FixedSourceDistance
block|{
DECL|field|sourceLatitude
specifier|private
specifier|final
name|double
name|sourceLatitude
decl_stmt|;
DECL|field|sourceLongitude
specifier|private
specifier|final
name|double
name|sourceLongitude
decl_stmt|;
DECL|field|distancePerDegree
specifier|private
specifier|final
name|double
name|distancePerDegree
decl_stmt|;
DECL|method|PlaneFixedSourceDistance
specifier|public
name|PlaneFixedSourceDistance
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|sourceLatitude
operator|=
name|sourceLatitude
expr_stmt|;
name|this
operator|.
name|sourceLongitude
operator|=
name|sourceLongitude
expr_stmt|;
name|this
operator|.
name|distancePerDegree
operator|=
name|unit
operator|.
name|getDistancePerDegree
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|calculate
specifier|public
name|double
name|calculate
parameter_list|(
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|)
block|{
name|double
name|px
init|=
name|targetLongitude
operator|-
name|sourceLongitude
decl_stmt|;
name|double
name|py
init|=
name|targetLatitude
operator|-
name|sourceLatitude
decl_stmt|;
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|px
operator|*
name|px
operator|+
name|py
operator|*
name|py
argument_list|)
operator|*
name|distancePerDegree
return|;
block|}
block|}
DECL|class|FactorFixedSourceDistance
specifier|public
specifier|static
class|class
name|FactorFixedSourceDistance
implements|implements
name|FixedSourceDistance
block|{
DECL|field|sourceLongitude
specifier|private
specifier|final
name|double
name|sourceLongitude
decl_stmt|;
DECL|field|a
specifier|private
specifier|final
name|double
name|a
decl_stmt|;
DECL|field|sinA
specifier|private
specifier|final
name|double
name|sinA
decl_stmt|;
DECL|field|cosA
specifier|private
specifier|final
name|double
name|cosA
decl_stmt|;
DECL|method|FactorFixedSourceDistance
specifier|public
name|FactorFixedSourceDistance
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|sourceLongitude
operator|=
name|sourceLongitude
expr_stmt|;
name|this
operator|.
name|a
operator|=
name|Math
operator|.
name|toRadians
argument_list|(
literal|90D
operator|-
name|sourceLatitude
argument_list|)
expr_stmt|;
name|this
operator|.
name|sinA
operator|=
name|Math
operator|.
name|sin
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|this
operator|.
name|cosA
operator|=
name|Math
operator|.
name|cos
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|calculate
specifier|public
name|double
name|calculate
parameter_list|(
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|)
block|{
name|double
name|longitudeDifference
init|=
name|targetLongitude
operator|-
name|sourceLongitude
decl_stmt|;
name|double
name|c
init|=
name|Math
operator|.
name|toRadians
argument_list|(
literal|90D
operator|-
name|targetLatitude
argument_list|)
decl_stmt|;
return|return
operator|(
name|cosA
operator|*
name|Math
operator|.
name|cos
argument_list|(
name|c
argument_list|)
operator|)
operator|+
operator|(
name|sinA
operator|*
name|Math
operator|.
name|sin
argument_list|(
name|c
argument_list|)
operator|*
name|Math
operator|.
name|cos
argument_list|(
name|Math
operator|.
name|toRadians
argument_list|(
name|longitudeDifference
argument_list|)
argument_list|)
operator|)
return|;
block|}
block|}
comment|/**      * Basic implementation of {@link FixedSourceDistance}. This class keeps the basic parameters for a distance      * functions based on a fixed source. Namely latitude, longitude and unit.      */
DECL|class|FixedSourceDistanceBase
specifier|public
specifier|abstract
specifier|static
class|class
name|FixedSourceDistanceBase
implements|implements
name|FixedSourceDistance
block|{
DECL|field|sourceLatitude
specifier|protected
specifier|final
name|double
name|sourceLatitude
decl_stmt|;
DECL|field|sourceLongitude
specifier|protected
specifier|final
name|double
name|sourceLongitude
decl_stmt|;
DECL|field|unit
specifier|protected
specifier|final
name|DistanceUnit
name|unit
decl_stmt|;
DECL|method|FixedSourceDistanceBase
specifier|public
name|FixedSourceDistanceBase
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|sourceLatitude
operator|=
name|sourceLatitude
expr_stmt|;
name|this
operator|.
name|sourceLongitude
operator|=
name|sourceLongitude
expr_stmt|;
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
block|}
block|}
DECL|class|ArcFixedSourceDistance
specifier|public
specifier|static
class|class
name|ArcFixedSourceDistance
extends|extends
name|FixedSourceDistanceBase
block|{
DECL|method|ArcFixedSourceDistance
specifier|public
name|ArcFixedSourceDistance
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
name|super
argument_list|(
name|sourceLatitude
argument_list|,
name|sourceLongitude
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|calculate
specifier|public
name|double
name|calculate
parameter_list|(
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|)
block|{
return|return
name|ARC
operator|.
name|calculate
argument_list|(
name|sourceLatitude
argument_list|,
name|sourceLongitude
argument_list|,
name|targetLatitude
argument_list|,
name|targetLongitude
argument_list|,
name|unit
argument_list|)
return|;
block|}
block|}
DECL|class|SloppyArcFixedSourceDistance
specifier|public
specifier|static
class|class
name|SloppyArcFixedSourceDistance
extends|extends
name|FixedSourceDistanceBase
block|{
DECL|method|SloppyArcFixedSourceDistance
specifier|public
name|SloppyArcFixedSourceDistance
parameter_list|(
name|double
name|sourceLatitude
parameter_list|,
name|double
name|sourceLongitude
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
name|super
argument_list|(
name|sourceLatitude
argument_list|,
name|sourceLongitude
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|calculate
specifier|public
name|double
name|calculate
parameter_list|(
name|double
name|targetLatitude
parameter_list|,
name|double
name|targetLongitude
parameter_list|)
block|{
return|return
name|SLOPPY_ARC
operator|.
name|calculate
argument_list|(
name|sourceLatitude
argument_list|,
name|sourceLongitude
argument_list|,
name|targetLatitude
argument_list|,
name|targetLongitude
argument_list|,
name|unit
argument_list|)
return|;
block|}
block|}
comment|/**      * Return a {@link SortedNumericDoubleValues} instance that returns the distances to a list of geo-points for each document.      */
DECL|method|distanceValues
specifier|public
specifier|static
name|SortedNumericDoubleValues
name|distanceValues
parameter_list|(
specifier|final
name|MultiGeoPointValues
name|geoPointValues
parameter_list|,
specifier|final
name|FixedSourceDistance
modifier|...
name|distances
parameter_list|)
block|{
specifier|final
name|GeoPointValues
name|singleValues
init|=
name|FieldData
operator|.
name|unwrapSingleton
argument_list|(
name|geoPointValues
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleValues
operator|!=
literal|null
operator|&&
name|distances
operator|.
name|length
operator|==
literal|1
condition|)
block|{
specifier|final
name|Bits
name|docsWithField
init|=
name|FieldData
operator|.
name|unwrapSingletonBits
argument_list|(
name|geoPointValues
argument_list|)
decl_stmt|;
return|return
name|FieldData
operator|.
name|singleton
argument_list|(
operator|new
name|NumericDoubleValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|double
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|docsWithField
operator|!=
literal|null
operator|&&
operator|!
name|docsWithField
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
return|return
literal|0d
return|;
block|}
specifier|final
name|GeoPoint
name|point
init|=
name|singleValues
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
return|return
name|distances
index|[
literal|0
index|]
operator|.
name|calculate
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
return|;
block|}
block|}
argument_list|,
name|docsWithField
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SortingNumericDoubleValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|geoPointValues
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|resize
argument_list|(
name|geoPointValues
operator|.
name|count
argument_list|()
operator|*
name|distances
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|valueCounter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FixedSourceDistance
name|distance
range|:
name|distances
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|geoPointValues
operator|.
name|count
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|GeoPoint
name|point
init|=
name|geoPointValues
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|values
index|[
name|valueCounter
index|]
operator|=
name|distance
operator|.
name|calculate
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
name|valueCounter
operator|++
expr_stmt|;
block|}
block|}
name|sort
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
block|}
end_enum

end_unit

