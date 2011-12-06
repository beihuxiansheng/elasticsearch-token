begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|geo
package|;
end_package

begin_import
import|import
name|gnu
operator|.
name|trove
operator|.
name|map
operator|.
name|hash
operator|.
name|TIntIntHashMap
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
comment|//    private final static Map<Character, Integer> DECODE_MAP = new HashMap<Character, Integer>();
DECL|field|DECODE_MAP
specifier|private
specifier|final
specifier|static
name|TIntIntHashMap
name|DECODE_MAP
init|=
operator|new
name|TIntIntHashMap
argument_list|()
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
static|static
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
name|BASE_32
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DECODE_MAP
operator|.
name|put
argument_list|(
name|BASE_32
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
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
DECL|method|decode
specifier|public
specifier|static
name|double
index|[]
name|decode
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
name|double
index|[]
name|ret
init|=
operator|new
name|double
index|[
literal|2
index|]
decl_stmt|;
name|decode
argument_list|(
name|geohash
argument_list|,
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
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
name|double
index|[]
name|ret
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
name|DECODE_MAP
operator|.
name|get
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
comment|//                        lngInterval[0] = (lngInterval[0] + lngInterval[1]) / 2D;
name|lngInterval0
operator|=
operator|(
name|lngInterval0
operator|+
name|lngInterval1
operator|)
operator|/
literal|2D
expr_stmt|;
block|}
else|else
block|{
comment|//                        lngInterval[1] = (lngInterval[0] + lngInterval[1]) / 2D;
name|lngInterval1
operator|=
operator|(
name|lngInterval0
operator|+
name|lngInterval1
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
comment|//                        latInterval[0] = (latInterval[0] + latInterval[1]) / 2D;
name|latInterval0
operator|=
operator|(
name|latInterval0
operator|+
name|latInterval1
operator|)
operator|/
literal|2D
expr_stmt|;
block|}
else|else
block|{
comment|//                        latInterval[1] = (latInterval[0] + latInterval[1]) / 2D;
name|latInterval1
operator|=
operator|(
name|latInterval0
operator|+
name|latInterval1
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
comment|//        latitude = (latInterval[0] + latInterval[1]) / 2D;
name|ret
index|[
literal|0
index|]
operator|=
operator|(
name|latInterval0
operator|+
name|latInterval1
operator|)
operator|/
literal|2D
expr_stmt|;
comment|//        longitude = (lngInterval[0] + lngInterval[1]) / 2D;
name|ret
index|[
literal|1
index|]
operator|=
operator|(
name|lngInterval0
operator|+
name|lngInterval1
operator|)
operator|/
literal|2D
expr_stmt|;
comment|//        return ret;
block|}
block|}
end_class

end_unit

