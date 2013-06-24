begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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

begin_comment
comment|/**  * Utilities for encoding and decoding geohashes. Based on  * http://en.wikipedia.org/wiki/Geohash.  */
end_comment

begin_comment
comment|// LUCENE MONITOR: monitor against spatial package
end_comment

begin_comment
comment|// replaced with native DECODE_MAP
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
DECL|field|PRECISION
specifier|public
specifier|static
specifier|final
name|int
name|PRECISION
init|=
literal|12
decl_stmt|;
DECL|field|BITS
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|BITS
init|=
block|{
literal|16
block|,
literal|8
block|,
literal|4
block|,
literal|2
block|,
literal|1
block|}
decl_stmt|;
DECL|method|GeoHashUtils
specifier|private
name|GeoHashUtils
parameter_list|()
block|{     }
DECL|method|encode
specifier|public
specifier|static
name|String
name|encode
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
return|return
name|encode
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|PRECISION
argument_list|)
return|;
block|}
comment|/**      * Encodes the given latitude and longitude into a geohash      *      * @param latitude  Latitude to encode      * @param longitude Longitude to encode      * @return Geohash encoding of the longitude and latitude      */
DECL|method|encode
specifier|public
specifier|static
name|String
name|encode
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|,
name|int
name|precision
parameter_list|)
block|{
comment|//        double[] latInterval = {-90.0, 90.0};
comment|//        double[] lngInterval = {-180.0, 180.0};
name|double
name|latInterval0
init|=
operator|-
literal|90.0
decl_stmt|;
name|double
name|latInterval1
init|=
literal|90.0
decl_stmt|;
name|double
name|lngInterval0
init|=
operator|-
literal|180.0
decl_stmt|;
name|double
name|lngInterval1
init|=
literal|180.0
decl_stmt|;
specifier|final
name|StringBuilder
name|geohash
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|isEven
init|=
literal|true
decl_stmt|;
name|int
name|bit
init|=
literal|0
decl_stmt|;
name|int
name|ch
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|geohash
operator|.
name|length
argument_list|()
operator|<
name|precision
condition|)
block|{
name|double
name|mid
init|=
literal|0.0
decl_stmt|;
if|if
condition|(
name|isEven
condition|)
block|{
comment|//                mid = (lngInterval[0] + lngInterval[1]) / 2D;
name|mid
operator|=
operator|(
name|lngInterval0
operator|+
name|lngInterval1
operator|)
operator|/
literal|2D
expr_stmt|;
if|if
condition|(
name|longitude
operator|>
name|mid
condition|)
block|{
name|ch
operator||=
name|BITS
index|[
name|bit
index|]
expr_stmt|;
comment|//                    lngInterval[0] = mid;
name|lngInterval0
operator|=
name|mid
expr_stmt|;
block|}
else|else
block|{
comment|//                    lngInterval[1] = mid;
name|lngInterval1
operator|=
name|mid
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//                mid = (latInterval[0] + latInterval[1]) / 2D;
name|mid
operator|=
operator|(
name|latInterval0
operator|+
name|latInterval1
operator|)
operator|/
literal|2D
expr_stmt|;
if|if
condition|(
name|latitude
operator|>
name|mid
condition|)
block|{
name|ch
operator||=
name|BITS
index|[
name|bit
index|]
expr_stmt|;
comment|//                    latInterval[0] = mid;
name|latInterval0
operator|=
name|mid
expr_stmt|;
block|}
else|else
block|{
comment|//                    latInterval[1] = mid;
name|latInterval1
operator|=
name|mid
expr_stmt|;
block|}
block|}
name|isEven
operator|=
operator|!
name|isEven
expr_stmt|;
if|if
condition|(
name|bit
operator|<
literal|4
condition|)
block|{
name|bit
operator|++
expr_stmt|;
block|}
else|else
block|{
name|geohash
operator|.
name|append
argument_list|(
name|BASE_32
index|[
name|ch
index|]
argument_list|)
expr_stmt|;
name|bit
operator|=
literal|0
expr_stmt|;
name|ch
operator|=
literal|0
expr_stmt|;
block|}
block|}
return|return
name|geohash
operator|.
name|toString
argument_list|()
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
comment|/**      * Calculate all neighbors of a given geohash cell.      *      * @param geohash Geohash of the defines cell      * @return geohashes of all neighbor cells      */
DECL|method|neighbors
specifier|public
specifier|static
name|List
argument_list|<
name|String
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
name|String
argument_list|>
argument_list|(
literal|8
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Calculate the geohash of a neighbor of a geohash      *      * @param geohash the geohash of a cell      * @param level   level of the geohash      * @param dx      delta of the first grid coordinate (must be -1, 0 or +1)      * @param dy      delta of the second grid coordinate (must be -1, 0 or +1)      * @return geohash of the defined cell      */
DECL|method|neighbor
specifier|private
specifier|final
specifier|static
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
name|decode
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
comment|// define grid limits for current level
specifier|final
name|int
name|xLimit
init|=
operator|(
operator|(
name|level
operator|%
literal|2
operator|)
operator|==
literal|0
operator|)
condition|?
literal|7
else|:
literal|3
decl_stmt|;
specifier|final
name|int
name|yLimit
init|=
operator|(
operator|(
name|level
operator|%
literal|2
operator|)
operator|==
literal|0
operator|)
condition|?
literal|3
else|:
literal|7
decl_stmt|;
comment|// if the defined neighbor has the same parent a the current cell
comment|// encode the cell direcly. Otherwise find the cell next to this
comment|// cell recursively. Since encoding wraps around within a cell
comment|// it can be encoded here.
if|if
condition|(
name|nx
operator|>=
literal|0
operator|&&
name|nx
operator|<=
name|xLimit
operator|&&
name|ny
operator|>=
literal|0
operator|&&
name|ny
operator|<
name|yLimit
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
if|if
condition|(
name|neighbor
operator|!=
literal|null
condition|)
block|{
return|return
name|neighbor
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
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
comment|/**      * Add all geohashes of the cells next to a given geohash to a list.      *      * @param geohash   Geohash of a specified cell      * @param length    level of the given geohash      * @param neighbors list to add the neighbors to      * @return the given list      */
DECL|method|addNeighbors
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|addNeighbors
parameter_list|(
name|String
name|geohash
parameter_list|,
name|int
name|length
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
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
DECL|method|decode
specifier|private
specifier|static
specifier|final
name|int
name|decode
parameter_list|(
name|char
name|geo
parameter_list|)
block|{
switch|switch
condition|(
name|geo
condition|)
block|{
case|case
literal|'0'
case|:
return|return
literal|0
return|;
case|case
literal|'1'
case|:
return|return
literal|1
return|;
case|case
literal|'2'
case|:
return|return
literal|2
return|;
case|case
literal|'3'
case|:
return|return
literal|3
return|;
case|case
literal|'4'
case|:
return|return
literal|4
return|;
case|case
literal|'5'
case|:
return|return
literal|5
return|;
case|case
literal|'6'
case|:
return|return
literal|6
return|;
case|case
literal|'7'
case|:
return|return
literal|7
return|;
case|case
literal|'8'
case|:
return|return
literal|8
return|;
case|case
literal|'9'
case|:
return|return
literal|9
return|;
case|case
literal|'b'
case|:
return|return
literal|10
return|;
case|case
literal|'c'
case|:
return|return
literal|11
return|;
case|case
literal|'d'
case|:
return|return
literal|12
return|;
case|case
literal|'e'
case|:
return|return
literal|13
return|;
case|case
literal|'f'
case|:
return|return
literal|14
return|;
case|case
literal|'g'
case|:
return|return
literal|15
return|;
case|case
literal|'h'
case|:
return|return
literal|16
return|;
case|case
literal|'j'
case|:
return|return
literal|17
return|;
case|case
literal|'k'
case|:
return|return
literal|18
return|;
case|case
literal|'m'
case|:
return|return
literal|19
return|;
case|case
literal|'n'
case|:
return|return
literal|20
return|;
case|case
literal|'p'
case|:
return|return
literal|21
return|;
case|case
literal|'q'
case|:
return|return
literal|22
return|;
case|case
literal|'r'
case|:
return|return
literal|23
return|;
case|case
literal|'s'
case|:
return|return
literal|24
return|;
case|case
literal|'t'
case|:
return|return
literal|25
return|;
case|case
literal|'u'
case|:
return|return
literal|26
return|;
case|case
literal|'v'
case|:
return|return
literal|27
return|;
case|case
literal|'w'
case|:
return|return
literal|28
return|;
case|case
literal|'x'
case|:
return|return
literal|29
return|;
case|case
literal|'y'
case|:
return|return
literal|30
return|;
case|case
literal|'z'
case|:
return|return
literal|31
return|;
default|default:
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"the character '"
operator|+
name|geo
operator|+
literal|"' is not a valid geohash character"
argument_list|)
throw|;
block|}
block|}
DECL|method|decode
specifier|public
specifier|static
name|GeoPoint
name|decode
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
operator|new
name|GeoPoint
argument_list|()
decl_stmt|;
name|decode
argument_list|(
name|geohash
argument_list|,
name|point
argument_list|)
expr_stmt|;
return|return
name|point
return|;
block|}
comment|/**      * Decodes the given geohash into a latitude and longitude      *      * @param geohash Geohash to deocde      * @return Array with the latitude at index 0, and longitude at index 1      */
DECL|method|decode
specifier|public
specifier|static
name|void
name|decode
parameter_list|(
name|String
name|geohash
parameter_list|,
name|GeoPoint
name|ret
parameter_list|)
block|{
name|double
index|[]
name|interval
init|=
name|decodeCell
argument_list|(
name|geohash
argument_list|)
decl_stmt|;
name|ret
operator|.
name|reset
argument_list|(
operator|(
name|interval
index|[
literal|0
index|]
operator|+
name|interval
index|[
literal|1
index|]
operator|)
operator|/
literal|2D
argument_list|,
operator|(
name|interval
index|[
literal|2
index|]
operator|+
name|interval
index|[
literal|3
index|]
operator|)
operator|/
literal|2D
argument_list|)
expr_stmt|;
block|}
comment|/**      * Decodes the given geohash into a geohash cell defined by the points nothWest and southEast      *      * @param geohash   Geohash to deocde      * @param northWest the point north/west of the cell      * @param southEast the point south/east of the cell      */
DECL|method|decodeCell
specifier|public
specifier|static
name|void
name|decodeCell
parameter_list|(
name|String
name|geohash
parameter_list|,
name|GeoPoint
name|northWest
parameter_list|,
name|GeoPoint
name|southEast
parameter_list|)
block|{
name|double
index|[]
name|interval
init|=
name|decodeCell
argument_list|(
name|geohash
argument_list|)
decl_stmt|;
name|northWest
operator|.
name|reset
argument_list|(
name|interval
index|[
literal|1
index|]
argument_list|,
name|interval
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|southEast
operator|.
name|reset
argument_list|(
name|interval
index|[
literal|0
index|]
argument_list|,
name|interval
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|decodeCell
specifier|private
specifier|static
name|double
index|[]
name|decodeCell
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
name|double
index|[]
name|interval
init|=
block|{
operator|-
literal|90.0
block|,
literal|90.0
block|,
operator|-
literal|180.0
block|,
literal|180.0
block|}
decl_stmt|;
name|boolean
name|isEven
init|=
literal|true
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
name|geohash
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|cd
init|=
name|decode
argument_list|(
name|geohash
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|mask
range|:
name|BITS
control|)
block|{
if|if
condition|(
name|isEven
condition|)
block|{
if|if
condition|(
operator|(
name|cd
operator|&
name|mask
operator|)
operator|!=
literal|0
condition|)
block|{
name|interval
index|[
literal|2
index|]
operator|=
operator|(
name|interval
index|[
literal|2
index|]
operator|+
name|interval
index|[
literal|3
index|]
operator|)
operator|/
literal|2D
expr_stmt|;
block|}
else|else
block|{
name|interval
index|[
literal|3
index|]
operator|=
operator|(
name|interval
index|[
literal|2
index|]
operator|+
name|interval
index|[
literal|3
index|]
operator|)
operator|/
literal|2D
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|(
name|cd
operator|&
name|mask
operator|)
operator|!=
literal|0
condition|)
block|{
name|interval
index|[
literal|0
index|]
operator|=
operator|(
name|interval
index|[
literal|0
index|]
operator|+
name|interval
index|[
literal|1
index|]
operator|)
operator|/
literal|2D
expr_stmt|;
block|}
else|else
block|{
name|interval
index|[
literal|1
index|]
operator|=
operator|(
name|interval
index|[
literal|0
index|]
operator|+
name|interval
index|[
literal|1
index|]
operator|)
operator|/
literal|2D
expr_stmt|;
block|}
block|}
name|isEven
operator|=
operator|!
name|isEven
expr_stmt|;
block|}
block|}
return|return
name|interval
return|;
block|}
block|}
end_class

end_unit

