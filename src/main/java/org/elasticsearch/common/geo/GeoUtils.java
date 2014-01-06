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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|GeohashPrefixTree
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|QuadPrefixTree
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|GeoUtils
specifier|public
class|class
name|GeoUtils
block|{
comment|/** Earth ellipsoid major axis defined by WGS 84 in meters */
DECL|field|EARTH_SEMI_MAJOR_AXIS
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_SEMI_MAJOR_AXIS
init|=
literal|6378137.0
decl_stmt|;
comment|// meters (WGS 84)
comment|/** Earth ellipsoid minor axis defined by WGS 84 in meters */
DECL|field|EARTH_SEMI_MINOR_AXIS
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_SEMI_MINOR_AXIS
init|=
literal|6356752.314245
decl_stmt|;
comment|// meters (WGS 84)
comment|/** Earth mean radius defined by WGS 84 in meters */
DECL|field|EARTH_MEAN_RADIUS
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_MEAN_RADIUS
init|=
literal|6371008.7714D
decl_stmt|;
comment|// meters (WGS 84)
comment|/** Earth axis ratio defined by WGS 84 (0.996647189335) */
DECL|field|EARTH_AXIS_RATIO
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_AXIS_RATIO
init|=
name|EARTH_SEMI_MINOR_AXIS
operator|/
name|EARTH_SEMI_MAJOR_AXIS
decl_stmt|;
comment|/** Earth ellipsoid equator length in meters */
DECL|field|EARTH_EQUATOR
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_EQUATOR
init|=
literal|2
operator|*
name|Math
operator|.
name|PI
operator|*
name|EARTH_SEMI_MAJOR_AXIS
decl_stmt|;
comment|/** Earth ellipsoid polar distance in meters */
DECL|field|EARTH_POLAR_DISTANCE
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_POLAR_DISTANCE
init|=
name|Math
operator|.
name|PI
operator|*
name|EARTH_SEMI_MINOR_AXIS
decl_stmt|;
comment|/**      * Calculate the width (in meters) of geohash cells at a specific level       * @param level geohash level must be greater or equal to zero       * @return the width of cells at level in meters        */
DECL|method|geoHashCellWidth
specifier|public
specifier|static
name|double
name|geoHashCellWidth
parameter_list|(
name|int
name|level
parameter_list|)
block|{
assert|assert
name|level
operator|>=
literal|0
assert|;
comment|// Geohash cells are split into 32 cells at each level. the grid
comment|// alternates at each level between a 8x4 and a 4x8 grid
return|return
name|EARTH_EQUATOR
operator|/
operator|(
literal|1L
operator|<<
operator|(
operator|(
operator|(
operator|(
name|level
operator|+
literal|1
operator|)
operator|/
literal|2
operator|)
operator|*
literal|3
operator|)
operator|+
operator|(
operator|(
name|level
operator|/
literal|2
operator|)
operator|*
literal|2
operator|)
operator|)
operator|)
return|;
block|}
comment|/**      * Calculate the width (in meters) of quadtree cells at a specific level       * @param level quadtree level must be greater or equal to zero       * @return the width of cells at level in meters        */
DECL|method|quadTreeCellWidth
specifier|public
specifier|static
name|double
name|quadTreeCellWidth
parameter_list|(
name|int
name|level
parameter_list|)
block|{
assert|assert
name|level
operator|>=
literal|0
assert|;
return|return
name|EARTH_EQUATOR
operator|/
operator|(
literal|1L
operator|<<
name|level
operator|)
return|;
block|}
comment|/**      * Calculate the height (in meters) of geohash cells at a specific level       * @param level geohash level must be greater or equal to zero       * @return the height of cells at level in meters        */
DECL|method|geoHashCellHeight
specifier|public
specifier|static
name|double
name|geoHashCellHeight
parameter_list|(
name|int
name|level
parameter_list|)
block|{
assert|assert
name|level
operator|>=
literal|0
assert|;
comment|// Geohash cells are split into 32 cells at each level. the grid
comment|// alternates at each level between a 8x4 and a 4x8 grid
return|return
name|EARTH_POLAR_DISTANCE
operator|/
operator|(
literal|1L
operator|<<
operator|(
operator|(
operator|(
operator|(
name|level
operator|+
literal|1
operator|)
operator|/
literal|2
operator|)
operator|*
literal|2
operator|)
operator|+
operator|(
operator|(
name|level
operator|/
literal|2
operator|)
operator|*
literal|3
operator|)
operator|)
operator|)
return|;
block|}
comment|/**      * Calculate the height (in meters) of quadtree cells at a specific level       * @param level quadtree level must be greater or equal to zero       * @return the height of cells at level in meters        */
DECL|method|quadTreeCellHeight
specifier|public
specifier|static
name|double
name|quadTreeCellHeight
parameter_list|(
name|int
name|level
parameter_list|)
block|{
assert|assert
name|level
operator|>=
literal|0
assert|;
return|return
name|EARTH_POLAR_DISTANCE
operator|/
operator|(
literal|1L
operator|<<
name|level
operator|)
return|;
block|}
comment|/**      * Calculate the size (in meters) of geohash cells at a specific level       * @param level geohash level must be greater or equal to zero       * @return the size of cells at level in meters        */
DECL|method|geoHashCellSize
specifier|public
specifier|static
name|double
name|geoHashCellSize
parameter_list|(
name|int
name|level
parameter_list|)
block|{
assert|assert
name|level
operator|>=
literal|0
assert|;
specifier|final
name|double
name|w
init|=
name|geoHashCellWidth
argument_list|(
name|level
argument_list|)
decl_stmt|;
specifier|final
name|double
name|h
init|=
name|geoHashCellHeight
argument_list|(
name|level
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|w
operator|*
name|w
operator|+
name|h
operator|*
name|h
argument_list|)
return|;
block|}
comment|/**      * Calculate the size (in meters) of quadtree cells at a specific level       * @param level quadtree level must be greater or equal to zero       * @return the size of cells at level in meters        */
DECL|method|quadTreeCellSize
specifier|public
specifier|static
name|double
name|quadTreeCellSize
parameter_list|(
name|int
name|level
parameter_list|)
block|{
assert|assert
name|level
operator|>=
literal|0
assert|;
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|EARTH_POLAR_DISTANCE
operator|*
name|EARTH_POLAR_DISTANCE
operator|+
name|EARTH_EQUATOR
operator|*
name|EARTH_EQUATOR
argument_list|)
operator|/
operator|(
literal|1L
operator|<<
name|level
operator|)
return|;
block|}
comment|/**      * Calculate the number of levels needed for a specific precision. Quadtree      * cells will not exceed the specified size (diagonal) of the precision.      * @param meters Maximum size of cells in meters (must greater than zero)      * @return levels need to achieve precision        */
DECL|method|quadTreeLevelsForPrecision
specifier|public
specifier|static
name|int
name|quadTreeLevelsForPrecision
parameter_list|(
name|double
name|meters
parameter_list|)
block|{
assert|assert
name|meters
operator|>=
literal|0
assert|;
if|if
condition|(
name|meters
operator|==
literal|0
condition|)
block|{
return|return
name|QuadPrefixTree
operator|.
name|MAX_LEVELS_POSSIBLE
return|;
block|}
else|else
block|{
specifier|final
name|double
name|ratio
init|=
literal|1
operator|+
operator|(
name|EARTH_POLAR_DISTANCE
operator|/
name|EARTH_EQUATOR
operator|)
decl_stmt|;
comment|// cell ratio
specifier|final
name|double
name|width
init|=
name|Math
operator|.
name|sqrt
argument_list|(
operator|(
name|meters
operator|*
name|meters
operator|)
operator|/
operator|(
name|ratio
operator|*
name|ratio
operator|)
argument_list|)
decl_stmt|;
comment|// convert to cell width
specifier|final
name|long
name|part
init|=
name|Math
operator|.
name|round
argument_list|(
name|Math
operator|.
name|ceil
argument_list|(
name|EARTH_EQUATOR
operator|/
name|width
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|level
init|=
name|Long
operator|.
name|SIZE
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|part
argument_list|)
operator|-
literal|1
decl_stmt|;
comment|// (log_2)
return|return
operator|(
name|part
operator|<=
operator|(
literal|1l
operator|<<
name|level
operator|)
operator|)
condition|?
name|level
else|:
operator|(
name|level
operator|+
literal|1
operator|)
return|;
comment|// adjust level
block|}
block|}
comment|/**      * Calculate the number of levels needed for a specific precision. QuadTree      * cells will not exceed the specified size (diagonal) of the precision.      * @param distance Maximum size of cells as unit string (must greater or equal to zero)      * @return levels need to achieve precision        */
DECL|method|quadTreeLevelsForPrecision
specifier|public
specifier|static
name|int
name|quadTreeLevelsForPrecision
parameter_list|(
name|String
name|distance
parameter_list|)
block|{
return|return
name|quadTreeLevelsForPrecision
argument_list|(
name|DistanceUnit
operator|.
name|parse
argument_list|(
name|distance
argument_list|,
name|DistanceUnit
operator|.
name|METERS
argument_list|,
name|DistanceUnit
operator|.
name|METERS
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Calculate the number of levels needed for a specific precision. GeoHash      * cells will not exceed the specified size (diagonal) of the precision.      * @param meters Maximum size of cells in meters (must greater or equal to zero)      * @return levels need to achieve precision        */
DECL|method|geoHashLevelsForPrecision
specifier|public
specifier|static
name|int
name|geoHashLevelsForPrecision
parameter_list|(
name|double
name|meters
parameter_list|)
block|{
assert|assert
name|meters
operator|>=
literal|0
assert|;
if|if
condition|(
name|meters
operator|==
literal|0
condition|)
block|{
return|return
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
return|;
block|}
else|else
block|{
specifier|final
name|double
name|ratio
init|=
literal|1
operator|+
operator|(
name|EARTH_POLAR_DISTANCE
operator|/
name|EARTH_EQUATOR
operator|)
decl_stmt|;
comment|// cell ratio
specifier|final
name|double
name|width
init|=
name|Math
operator|.
name|sqrt
argument_list|(
operator|(
name|meters
operator|*
name|meters
operator|)
operator|/
operator|(
name|ratio
operator|*
name|ratio
operator|)
argument_list|)
decl_stmt|;
comment|// convert to cell width
specifier|final
name|double
name|part
init|=
name|Math
operator|.
name|ceil
argument_list|(
name|EARTH_EQUATOR
operator|/
name|width
argument_list|)
decl_stmt|;
if|if
condition|(
name|part
operator|==
literal|1
condition|)
return|return
literal|1
return|;
specifier|final
name|int
name|bits
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|round
argument_list|(
name|Math
operator|.
name|ceil
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|part
argument_list|)
operator|/
name|Math
operator|.
name|log
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|full
init|=
name|bits
operator|/
literal|5
decl_stmt|;
comment|// number of 5 bit subdivisions
specifier|final
name|int
name|left
init|=
name|bits
operator|-
name|full
operator|*
literal|5
decl_stmt|;
comment|// bit representing the last level
specifier|final
name|int
name|even
init|=
name|full
operator|+
operator|(
name|left
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
comment|// number of even levels
specifier|final
name|int
name|odd
init|=
name|full
operator|+
operator|(
name|left
operator|>
literal|3
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
comment|// number of odd levels
return|return
name|even
operator|+
name|odd
return|;
block|}
block|}
comment|/**      * Calculate the number of levels needed for a specific precision. GeoHash      * cells will not exceed the specified size (diagonal) of the precision.      * @param distance Maximum size of cells as unit string (must greater or equal to zero)      * @return levels need to achieve precision        */
DECL|method|geoHashLevelsForPrecision
specifier|public
specifier|static
name|int
name|geoHashLevelsForPrecision
parameter_list|(
name|String
name|distance
parameter_list|)
block|{
return|return
name|geoHashLevelsForPrecision
argument_list|(
name|DistanceUnit
operator|.
name|parse
argument_list|(
name|distance
argument_list|,
name|DistanceUnit
operator|.
name|METERS
argument_list|,
name|DistanceUnit
operator|.
name|METERS
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Normalize longitude to lie within the -180 (exclusive) to 180 (inclusive) range.      *      * @param lon Longitude to normalize      * @return The normalized longitude.      */
DECL|method|normalizeLon
specifier|public
specifier|static
name|double
name|normalizeLon
parameter_list|(
name|double
name|lon
parameter_list|)
block|{
return|return
name|centeredModulus
argument_list|(
name|lon
argument_list|,
literal|360
argument_list|)
return|;
block|}
comment|/**      * Normalize latitude to lie within the -90 to 90 (both inclusive) range.      *<p/>      * Note: You should not normalize longitude and latitude separately,      * because when normalizing latitude it may be necessary to      * add a shift of 180&deg; in the longitude.      * For this purpose, you should call the      * {@link #normalizePoint(GeoPoint)} function.      *      * @param lat Latitude to normalize      * @return The normalized latitude.      * @see #normalizePoint(GeoPoint)      */
DECL|method|normalizeLat
specifier|public
specifier|static
name|double
name|normalizeLat
parameter_list|(
name|double
name|lat
parameter_list|)
block|{
name|lat
operator|=
name|centeredModulus
argument_list|(
name|lat
argument_list|,
literal|360
argument_list|)
expr_stmt|;
if|if
condition|(
name|lat
operator|<
operator|-
literal|90
condition|)
block|{
name|lat
operator|=
operator|-
literal|180
operator|-
name|lat
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lat
operator|>
literal|90
condition|)
block|{
name|lat
operator|=
literal|180
operator|-
name|lat
expr_stmt|;
block|}
return|return
name|lat
return|;
block|}
comment|/**      * Normalize the geo {@code Point} for its coordinates to lie within their      * respective normalized ranges.      *<p/>      * Note: A shift of 180&deg; is applied in the longitude if necessary,      * in order to normalize properly the latitude.      *      * @param point The point to normalize in-place.      */
DECL|method|normalizePoint
specifier|public
specifier|static
name|void
name|normalizePoint
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
block|{
name|normalizePoint
argument_list|(
name|point
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Normalize the geo {@code Point} for the given coordinates to lie within      * their respective normalized ranges.      *<p/>      * You can control which coordinate gets normalized with the two flags.      *<p/>      * Note: A shift of 180&deg; is applied in the longitude if necessary,      * in order to normalize properly the latitude.      * If normalizing latitude but not longitude, it is assumed that      * the longitude is in the form x+k*360, with x in ]-180;180],      * and k is meaningful to the application.      * Therefore x will be adjusted while keeping k preserved.      *      * @param point   The point to normalize in-place.      * @param normLat Whether to normalize latitude or leave it as is.      * @param normLon Whether to normalize longitude.      */
DECL|method|normalizePoint
specifier|public
specifier|static
name|void
name|normalizePoint
parameter_list|(
name|GeoPoint
name|point
parameter_list|,
name|boolean
name|normLat
parameter_list|,
name|boolean
name|normLon
parameter_list|)
block|{
name|double
name|lat
init|=
name|point
operator|.
name|lat
argument_list|()
decl_stmt|;
name|double
name|lon
init|=
name|point
operator|.
name|lon
argument_list|()
decl_stmt|;
name|normLat
operator|=
name|normLat
operator|&&
operator|(
name|lat
operator|>
literal|90
operator|||
name|lat
operator|<=
operator|-
literal|90
operator|)
expr_stmt|;
name|normLon
operator|=
name|normLon
operator|&&
operator|(
name|lon
operator|>
literal|180
operator|||
name|lon
operator|<=
operator|-
literal|180
operator|)
expr_stmt|;
if|if
condition|(
name|normLat
condition|)
block|{
name|lat
operator|=
name|centeredModulus
argument_list|(
name|lat
argument_list|,
literal|360
argument_list|)
expr_stmt|;
name|boolean
name|shift
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|lat
operator|<
operator|-
literal|90
condition|)
block|{
name|lat
operator|=
operator|-
literal|180
operator|-
name|lat
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lat
operator|>
literal|90
condition|)
block|{
name|lat
operator|=
literal|180
operator|-
name|lat
expr_stmt|;
block|}
else|else
block|{
comment|// No need to shift the longitude, and the latitude is normalized
name|shift
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|shift
condition|)
block|{
if|if
condition|(
name|normLon
condition|)
block|{
name|lon
operator|+=
literal|180
expr_stmt|;
block|}
else|else
block|{
comment|// Longitude won't be normalized,
comment|// keep it in the form x+k*360 (with x in ]-180;180])
comment|// by only changing x, assuming k is meaningful for the user application.
name|lon
operator|+=
name|normalizeLon
argument_list|(
name|lon
argument_list|)
operator|>
literal|0
condition|?
operator|-
literal|180
else|:
literal|180
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|normLon
condition|)
block|{
name|lon
operator|=
name|centeredModulus
argument_list|(
name|lon
argument_list|,
literal|360
argument_list|)
expr_stmt|;
block|}
name|point
operator|.
name|reset
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
expr_stmt|;
block|}
DECL|method|centeredModulus
specifier|private
specifier|static
name|double
name|centeredModulus
parameter_list|(
name|double
name|dividend
parameter_list|,
name|double
name|divisor
parameter_list|)
block|{
name|double
name|rtn
init|=
name|dividend
operator|%
name|divisor
decl_stmt|;
if|if
condition|(
name|rtn
operator|<=
literal|0
condition|)
block|{
name|rtn
operator|+=
name|divisor
expr_stmt|;
block|}
if|if
condition|(
name|rtn
operator|>
name|divisor
operator|/
literal|2
condition|)
block|{
name|rtn
operator|-=
name|divisor
expr_stmt|;
block|}
return|return
name|rtn
return|;
block|}
block|}
end_class

end_unit

