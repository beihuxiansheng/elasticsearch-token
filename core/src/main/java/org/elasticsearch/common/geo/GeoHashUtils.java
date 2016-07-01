begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collection
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
name|geopoint
operator|.
name|document
operator|.
name|GeoPointField
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
name|BitUtil
import|;
end_import

begin_comment
comment|/**  * Utilities for converting to/from the GeoHash standard  *  * The geohash long format is represented as lon/lat (x/y) interleaved with the 4 least significant bits  * representing the level (1-12) [xyxy...xyxyllll]  *  * This differs from a morton encoded value which interleaves lat/lon (y/x).*  */
end_comment

begin_class
DECL|class|GeoHashUtils
specifier|public
class|class
name|GeoHashUtils
block|{
DECL|field|BASE_32
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|BASE_32
init|=
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
literal|'b'
block|,
literal|'c'
block|,
literal|'d'
block|,
literal|'e'
block|,
literal|'f'
block|,
literal|'g'
block|,
literal|'h'
block|,
literal|'j'
block|,
literal|'k'
block|,
literal|'m'
block|,
literal|'n'
block|,
literal|'p'
block|,
literal|'q'
block|,
literal|'r'
block|,
literal|'s'
block|,
literal|'t'
block|,
literal|'u'
block|,
literal|'v'
block|,
literal|'w'
block|,
literal|'x'
block|,
literal|'y'
block|,
literal|'z'
block|}
decl_stmt|;
DECL|field|BASE_32_STRING
specifier|private
specifier|static
specifier|final
name|String
name|BASE_32_STRING
init|=
operator|new
name|String
argument_list|(
name|BASE_32
argument_list|)
decl_stmt|;
comment|/** maximum precision for geohash strings */
DECL|field|PRECISION
specifier|public
specifier|static
specifier|final
name|int
name|PRECISION
init|=
literal|12
decl_stmt|;
DECL|field|MORTON_OFFSET
specifier|private
specifier|static
specifier|final
name|short
name|MORTON_OFFSET
init|=
operator|(
name|GeoPointField
operator|.
name|BITS
operator|<<
literal|1
operator|)
operator|-
operator|(
name|PRECISION
operator|*
literal|5
operator|)
decl_stmt|;
comment|// No instance:
DECL|method|GeoHashUtils
specifier|private
name|GeoHashUtils
parameter_list|()
block|{     }
comment|/**      * Encode lon/lat to the geohash based long format (lon/lat interleaved, 4 least significant bits = level)      */
DECL|method|longEncode
specifier|public
specifier|static
specifier|final
name|long
name|longEncode
parameter_list|(
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|int
name|level
parameter_list|)
block|{
comment|// shift to appropriate level
specifier|final
name|short
name|msf
init|=
call|(
name|short
call|)
argument_list|(
operator|(
operator|(
literal|12
operator|-
name|level
operator|)
operator|*
literal|5
operator|)
operator|+
name|MORTON_OFFSET
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|BitUtil
operator|.
name|flipFlop
argument_list|(
name|GeoPointField
operator|.
name|encodeLatLon
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
argument_list|)
operator|>>>
name|msf
operator|)
operator|<<
literal|4
operator|)
operator||
name|level
return|;
block|}
comment|/**      * Encode from geohash string to the geohash based long format (lon/lat interleaved, 4 least significant bits = level)      */
DECL|method|longEncode
specifier|public
specifier|static
specifier|final
name|long
name|longEncode
parameter_list|(
specifier|final
name|String
name|hash
parameter_list|)
block|{
name|int
name|level
init|=
name|hash
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
name|long
name|b
decl_stmt|;
name|long
name|l
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|char
name|c
range|:
name|hash
operator|.
name|toCharArray
argument_list|()
control|)
block|{
name|b
operator|=
call|(
name|long
call|)
argument_list|(
name|BASE_32_STRING
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator||=
operator|(
name|b
operator|<<
operator|(
name|level
operator|--
operator|*
literal|5
operator|)
operator|)
expr_stmt|;
block|}
return|return
operator|(
name|l
operator|<<
literal|4
operator|)
operator||
name|hash
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**      * Encode an existing geohash long to the provided precision      */
DECL|method|longEncode
specifier|public
specifier|static
name|long
name|longEncode
parameter_list|(
name|long
name|geohash
parameter_list|,
name|int
name|level
parameter_list|)
block|{
specifier|final
name|short
name|precision
init|=
call|(
name|short
call|)
argument_list|(
name|geohash
operator|&
literal|15
argument_list|)
decl_stmt|;
if|if
condition|(
name|precision
operator|==
name|level
condition|)
block|{
return|return
name|geohash
return|;
block|}
elseif|else
if|if
condition|(
name|precision
operator|>
name|level
condition|)
block|{
return|return
operator|(
operator|(
name|geohash
operator|>>>
operator|(
operator|(
operator|(
name|precision
operator|-
name|level
operator|)
operator|*
literal|5
operator|)
operator|+
literal|4
operator|)
operator|)
operator|<<
literal|4
operator|)
operator||
name|level
return|;
block|}
return|return
operator|(
operator|(
name|geohash
operator|>>>
literal|4
operator|)
operator|<<
operator|(
operator|(
operator|(
name|level
operator|-
name|precision
operator|)
operator|*
literal|5
operator|)
operator|+
literal|4
operator|)
operator||
name|level
operator|)
return|;
block|}
comment|/**      * Convert from a morton encoded long from a geohash encoded long      */
DECL|method|fromMorton
specifier|public
specifier|static
name|long
name|fromMorton
parameter_list|(
name|long
name|morton
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|long
name|mFlipped
init|=
name|BitUtil
operator|.
name|flipFlop
argument_list|(
name|morton
argument_list|)
decl_stmt|;
name|mFlipped
operator|>>>=
operator|(
operator|(
operator|(
name|GeoHashUtils
operator|.
name|PRECISION
operator|-
name|level
operator|)
operator|*
literal|5
operator|)
operator|+
name|MORTON_OFFSET
operator|)
expr_stmt|;
return|return
operator|(
name|mFlipped
operator|<<
literal|4
operator|)
operator||
name|level
return|;
block|}
comment|/**      * Encode to a geohash string from the geohash based long format      */
DECL|method|stringEncode
specifier|public
specifier|static
specifier|final
name|String
name|stringEncode
parameter_list|(
name|long
name|geoHashLong
parameter_list|)
block|{
name|int
name|level
init|=
operator|(
name|int
operator|)
name|geoHashLong
operator|&
literal|15
decl_stmt|;
name|geoHashLong
operator|>>>=
literal|4
expr_stmt|;
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|level
index|]
decl_stmt|;
do|do
block|{
name|chars
index|[
operator|--
name|level
index|]
operator|=
name|BASE_32
index|[
call|(
name|int
call|)
argument_list|(
name|geoHashLong
operator|&
literal|31L
argument_list|)
index|]
expr_stmt|;
name|geoHashLong
operator|>>>=
literal|5
expr_stmt|;
block|}
do|while
condition|(
name|level
operator|>
literal|0
condition|)
do|;
return|return
operator|new
name|String
argument_list|(
name|chars
argument_list|)
return|;
block|}
comment|/**      * Encode to a geohash string from full resolution longitude, latitude)      */
DECL|method|stringEncode
specifier|public
specifier|static
specifier|final
name|String
name|stringEncode
parameter_list|(
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|)
block|{
return|return
name|stringEncode
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|,
literal|12
argument_list|)
return|;
block|}
comment|/**      * Encode to a level specific geohash string from full resolution longitude, latitude      */
DECL|method|stringEncode
specifier|public
specifier|static
specifier|final
name|String
name|stringEncode
parameter_list|(
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|int
name|level
parameter_list|)
block|{
comment|// convert to geohashlong
specifier|final
name|long
name|ghLong
init|=
name|fromMorton
argument_list|(
name|GeoPointField
operator|.
name|encodeLatLon
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
argument_list|,
name|level
argument_list|)
decl_stmt|;
return|return
name|stringEncode
argument_list|(
name|ghLong
argument_list|)
return|;
block|}
comment|/**      * Encode to a full precision geohash string from a given morton encoded long value      */
DECL|method|stringEncodeFromMortonLong
specifier|public
specifier|static
specifier|final
name|String
name|stringEncodeFromMortonLong
parameter_list|(
specifier|final
name|long
name|hashedVal
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|stringEncode
argument_list|(
name|hashedVal
argument_list|,
name|PRECISION
argument_list|)
return|;
block|}
comment|/**      * Encode to a geohash string at a given level from a morton long      */
DECL|method|stringEncodeFromMortonLong
specifier|public
specifier|static
specifier|final
name|String
name|stringEncodeFromMortonLong
parameter_list|(
name|long
name|hashedVal
parameter_list|,
specifier|final
name|int
name|level
parameter_list|)
block|{
comment|// bit twiddle to geohash (since geohash is a swapped (lon/lat) encoding)
name|hashedVal
operator|=
name|BitUtil
operator|.
name|flipFlop
argument_list|(
name|hashedVal
argument_list|)
expr_stmt|;
name|StringBuilder
name|geoHash
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|short
name|precision
init|=
literal|0
decl_stmt|;
specifier|final
name|short
name|msf
init|=
operator|(
name|GeoPointField
operator|.
name|BITS
operator|<<
literal|1
operator|)
operator|-
literal|5
decl_stmt|;
name|long
name|mask
init|=
literal|31L
operator|<<
name|msf
decl_stmt|;
do|do
block|{
name|geoHash
operator|.
name|append
argument_list|(
name|BASE_32
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|mask
operator|&
name|hashedVal
operator|)
operator|>>>
operator|(
name|msf
operator|-
operator|(
name|precision
operator|*
literal|5
operator|)
operator|)
argument_list|)
index|]
argument_list|)
expr_stmt|;
comment|// next 5 bits
name|mask
operator|>>>=
literal|5
expr_stmt|;
block|}
do|while
condition|(
operator|++
name|precision
operator|<
name|level
condition|)
do|;
return|return
name|geoHash
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Encode to a morton long value from a given geohash string      */
DECL|method|mortonEncode
specifier|public
specifier|static
specifier|final
name|long
name|mortonEncode
parameter_list|(
specifier|final
name|String
name|hash
parameter_list|)
block|{
name|int
name|level
init|=
literal|11
decl_stmt|;
name|long
name|b
decl_stmt|;
name|long
name|l
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|char
name|c
range|:
name|hash
operator|.
name|toCharArray
argument_list|()
control|)
block|{
name|b
operator|=
call|(
name|long
call|)
argument_list|(
name|BASE_32_STRING
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator||=
operator|(
name|b
operator|<<
operator|(
operator|(
name|level
operator|--
operator|*
literal|5
operator|)
operator|+
name|MORTON_OFFSET
operator|)
operator|)
expr_stmt|;
block|}
return|return
name|BitUtil
operator|.
name|flipFlop
argument_list|(
name|l
argument_list|)
return|;
block|}
comment|/**      * Encode to a morton long value from a given geohash long value      */
DECL|method|mortonEncode
specifier|public
specifier|static
specifier|final
name|long
name|mortonEncode
parameter_list|(
specifier|final
name|long
name|geoHashLong
parameter_list|)
block|{
specifier|final
name|int
name|level
init|=
call|(
name|int
call|)
argument_list|(
name|geoHashLong
operator|&
literal|15
argument_list|)
decl_stmt|;
specifier|final
name|short
name|odd
init|=
call|(
name|short
call|)
argument_list|(
name|level
operator|&
literal|1
argument_list|)
decl_stmt|;
return|return
name|BitUtil
operator|.
name|flipFlop
argument_list|(
operator|(
operator|(
name|geoHashLong
operator|>>>
literal|4
operator|)
operator|<<
name|odd
operator|)
operator|<<
operator|(
operator|(
operator|(
literal|12
operator|-
name|level
operator|)
operator|*
literal|5
operator|)
operator|+
operator|(
name|MORTON_OFFSET
operator|-
name|odd
operator|)
operator|)
argument_list|)
return|;
block|}
DECL|method|encode
specifier|private
specifier|static
specifier|final
name|char
name|encode
parameter_list|(
name|int
name|x
parameter_list|,
name|int
name|y
parameter_list|)
block|{
return|return
name|BASE_32
index|[
operator|(
operator|(
name|x
operator|&
literal|1
operator|)
operator|+
operator|(
operator|(
name|y
operator|&
literal|1
operator|)
operator|*
literal|2
operator|)
operator|+
operator|(
operator|(
name|x
operator|&
literal|2
operator|)
operator|*
literal|2
operator|)
operator|+
operator|(
operator|(
name|y
operator|&
literal|2
operator|)
operator|*
literal|4
operator|)
operator|+
operator|(
operator|(
name|x
operator|&
literal|4
operator|)
operator|*
literal|4
operator|)
operator|)
operator|%
literal|32
index|]
return|;
block|}
comment|/**      * Calculate all neighbors of a given geohash cell.      *      * @param geohash Geohash of the defined cell      * @return geohashes of all neighbor cells      */
DECL|method|neighbors
specifier|public
specifier|static
name|Collection
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|neighbors
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
return|return
name|addNeighbors
argument_list|(
name|geohash
argument_list|,
name|geohash
operator|.
name|length
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|CharSequence
argument_list|>
argument_list|(
literal|8
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Calculate the geohash of a neighbor of a geohash      *      * @param geohash the geohash of a cell      * @param level   level of the geohash      * @param dx      delta of the first grid coordinate (must be -1, 0 or +1)      * @param dy      delta of the second grid coordinate (must be -1, 0 or +1)      * @return geohash of the defined cell      */
DECL|method|neighbor
specifier|public
specifier|static
specifier|final
name|String
name|neighbor
parameter_list|(
name|String
name|geohash
parameter_list|,
name|int
name|level
parameter_list|,
name|int
name|dx
parameter_list|,
name|int
name|dy
parameter_list|)
block|{
name|int
name|cell
init|=
name|BASE_32_STRING
operator|.
name|indexOf
argument_list|(
name|geohash
operator|.
name|charAt
argument_list|(
name|level
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|// Decoding the Geohash bit pattern to determine grid coordinates
name|int
name|x0
init|=
name|cell
operator|&
literal|1
decl_stmt|;
comment|// first bit of x
name|int
name|y0
init|=
name|cell
operator|&
literal|2
decl_stmt|;
comment|// first bit of y
name|int
name|x1
init|=
name|cell
operator|&
literal|4
decl_stmt|;
comment|// second bit of x
name|int
name|y1
init|=
name|cell
operator|&
literal|8
decl_stmt|;
comment|// second bit of y
name|int
name|x2
init|=
name|cell
operator|&
literal|16
decl_stmt|;
comment|// third bit of x
comment|// combine the bitpattern to grid coordinates.
comment|// note that the semantics of x and y are swapping
comment|// on each level
name|int
name|x
init|=
name|x0
operator|+
operator|(
name|x1
operator|/
literal|2
operator|)
operator|+
operator|(
name|x2
operator|/
literal|4
operator|)
decl_stmt|;
name|int
name|y
init|=
operator|(
name|y0
operator|/
literal|2
operator|)
operator|+
operator|(
name|y1
operator|/
literal|4
operator|)
decl_stmt|;
if|if
condition|(
name|level
operator|==
literal|1
condition|)
block|{
comment|// Root cells at north (namely "bcfguvyz") or at
comment|// south (namely "0145hjnp") do not have neighbors
comment|// in north/south direction
if|if
condition|(
operator|(
name|dy
operator|<
literal|0
operator|&&
name|y
operator|==
literal|0
operator|)
operator|||
operator|(
name|dy
operator|>
literal|0
operator|&&
name|y
operator|==
literal|3
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|Character
operator|.
name|toString
argument_list|(
name|encode
argument_list|(
name|x
operator|+
name|dx
argument_list|,
name|y
operator|+
name|dy
argument_list|)
argument_list|)
return|;
block|}
block|}
else|else
block|{
comment|// define grid coordinates for next level
specifier|final
name|int
name|nx
init|=
operator|(
operator|(
name|level
operator|%
literal|2
operator|)
operator|==
literal|1
operator|)
condition|?
operator|(
name|x
operator|+
name|dx
operator|)
else|:
operator|(
name|x
operator|+
name|dy
operator|)
decl_stmt|;
specifier|final
name|int
name|ny
init|=
operator|(
operator|(
name|level
operator|%
literal|2
operator|)
operator|==
literal|1
operator|)
condition|?
operator|(
name|y
operator|+
name|dy
operator|)
else|:
operator|(
name|y
operator|+
name|dx
operator|)
decl_stmt|;
comment|// if the defined neighbor has the same parent a the current cell
comment|// encode the cell directly. Otherwise find the cell next to this
comment|// cell recursively. Since encoding wraps around within a cell
comment|// it can be encoded here.
comment|// xLimit and YLimit must always be respectively 7 and 3
comment|// since x and y semantics are swapping on each level.
if|if
condition|(
name|nx
operator|>=
literal|0
operator|&&
name|nx
operator|<=
literal|7
operator|&&
name|ny
operator|>=
literal|0
operator|&&
name|ny
operator|<=
literal|3
condition|)
block|{
return|return
name|geohash
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|level
operator|-
literal|1
argument_list|)
operator|+
name|encode
argument_list|(
name|nx
argument_list|,
name|ny
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|neighbor
init|=
name|neighbor
argument_list|(
name|geohash
argument_list|,
name|level
operator|-
literal|1
argument_list|,
name|dx
argument_list|,
name|dy
argument_list|)
decl_stmt|;
return|return
operator|(
name|neighbor
operator|!=
literal|null
operator|)
condition|?
name|neighbor
operator|+
name|encode
argument_list|(
name|nx
argument_list|,
name|ny
argument_list|)
else|:
name|neighbor
return|;
block|}
block|}
block|}
comment|/**      * Add all geohashes of the cells next to a given geohash to a list.      *      * @param geohash   Geohash of a specified cell      * @param neighbors list to add the neighbors to      * @return the given list      */
DECL|method|addNeighbors
specifier|public
specifier|static
specifier|final
parameter_list|<
name|E
extends|extends
name|Collection
argument_list|<
name|?
super|super
name|String
argument_list|>
parameter_list|>
name|E
name|addNeighbors
parameter_list|(
name|String
name|geohash
parameter_list|,
name|E
name|neighbors
parameter_list|)
block|{
return|return
name|addNeighbors
argument_list|(
name|geohash
argument_list|,
name|geohash
operator|.
name|length
argument_list|()
argument_list|,
name|neighbors
argument_list|)
return|;
block|}
comment|/**      * Add all geohashes of the cells next to a given geohash to a list.      *      * @param geohash   Geohash of a specified cell      * @param length    level of the given geohash      * @param neighbors list to add the neighbors to      * @return the given list      */
DECL|method|addNeighbors
specifier|public
specifier|static
specifier|final
parameter_list|<
name|E
extends|extends
name|Collection
argument_list|<
name|?
super|super
name|String
argument_list|>
parameter_list|>
name|E
name|addNeighbors
parameter_list|(
name|String
name|geohash
parameter_list|,
name|int
name|length
parameter_list|,
name|E
name|neighbors
parameter_list|)
block|{
name|String
name|south
init|=
name|neighbor
argument_list|(
name|geohash
argument_list|,
name|length
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|String
name|north
init|=
name|neighbor
argument_list|(
name|geohash
argument_list|,
name|length
argument_list|,
literal|0
argument_list|,
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|north
operator|!=
literal|null
condition|)
block|{
name|neighbors
operator|.
name|add
argument_list|(
name|neighbor
argument_list|(
name|north
argument_list|,
name|length
argument_list|,
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|neighbors
operator|.
name|add
argument_list|(
name|north
argument_list|)
expr_stmt|;
name|neighbors
operator|.
name|add
argument_list|(
name|neighbor
argument_list|(
name|north
argument_list|,
name|length
argument_list|,
operator|+
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|neighbors
operator|.
name|add
argument_list|(
name|neighbor
argument_list|(
name|geohash
argument_list|,
name|length
argument_list|,
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|neighbors
operator|.
name|add
argument_list|(
name|neighbor
argument_list|(
name|geohash
argument_list|,
name|length
argument_list|,
operator|+
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|south
operator|!=
literal|null
condition|)
block|{
name|neighbors
operator|.
name|add
argument_list|(
name|neighbor
argument_list|(
name|south
argument_list|,
name|length
argument_list|,
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|neighbors
operator|.
name|add
argument_list|(
name|south
argument_list|)
expr_stmt|;
name|neighbors
operator|.
name|add
argument_list|(
name|neighbor
argument_list|(
name|south
argument_list|,
name|length
argument_list|,
operator|+
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|neighbors
return|;
block|}
block|}
end_class

end_unit

