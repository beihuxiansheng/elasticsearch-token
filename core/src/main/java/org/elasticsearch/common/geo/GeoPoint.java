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
name|BitUtil
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
name|XGeoHashUtils
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
name|XGeoUtils
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|GeoPoint
specifier|public
specifier|final
class|class
name|GeoPoint
block|{
DECL|field|lat
specifier|private
name|double
name|lat
decl_stmt|;
DECL|field|lon
specifier|private
name|double
name|lon
decl_stmt|;
DECL|field|TOLERANCE
specifier|private
specifier|final
specifier|static
name|double
name|TOLERANCE
init|=
name|XGeoUtils
operator|.
name|TOLERANCE
decl_stmt|;
DECL|method|GeoPoint
specifier|public
name|GeoPoint
parameter_list|()
block|{     }
comment|/**      * Create a new Geopointform a string. This String must either be a geohash      * or a lat-lon tuple.      *      * @param value String to create the point from      */
DECL|method|GeoPoint
specifier|public
name|GeoPoint
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|resetFromString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|GeoPoint
specifier|public
name|GeoPoint
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
name|lat
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
block|}
DECL|method|GeoPoint
specifier|public
name|GeoPoint
parameter_list|(
name|GeoPoint
name|template
parameter_list|)
block|{
name|this
argument_list|(
name|template
operator|.
name|getLat
argument_list|()
argument_list|,
name|template
operator|.
name|getLon
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|GeoPoint
name|reset
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
name|lat
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|resetLat
specifier|public
name|GeoPoint
name|resetLat
parameter_list|(
name|double
name|lat
parameter_list|)
block|{
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|resetLon
specifier|public
name|GeoPoint
name|resetLon
parameter_list|(
name|double
name|lon
parameter_list|)
block|{
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|resetFromString
specifier|public
name|GeoPoint
name|resetFromString
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|int
name|comma
init|=
name|value
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
decl_stmt|;
if|if
condition|(
name|comma
operator|!=
operator|-
literal|1
condition|)
block|{
name|lat
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|comma
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|lon
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|comma
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resetFromGeoHash
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|resetFromIndexHash
specifier|public
name|GeoPoint
name|resetFromIndexHash
parameter_list|(
name|long
name|hash
parameter_list|)
block|{
name|lon
operator|=
name|XGeoUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|hash
argument_list|)
expr_stmt|;
name|lat
operator|=
name|XGeoUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|hash
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|resetFromGeoHash
specifier|public
name|GeoPoint
name|resetFromGeoHash
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
specifier|final
name|long
name|hash
init|=
name|XGeoHashUtils
operator|.
name|mortonEncode
argument_list|(
name|geohash
argument_list|)
decl_stmt|;
return|return
name|this
operator|.
name|reset
argument_list|(
name|XGeoUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|hash
argument_list|)
argument_list|,
name|XGeoUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|hash
argument_list|)
argument_list|)
return|;
block|}
DECL|method|resetFromGeoHash
specifier|public
name|GeoPoint
name|resetFromGeoHash
parameter_list|(
name|long
name|geohashLong
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
literal|12
operator|-
operator|(
name|geohashLong
operator|&
literal|15
operator|)
argument_list|)
decl_stmt|;
return|return
name|this
operator|.
name|resetFromIndexHash
argument_list|(
name|BitUtil
operator|.
name|flipFlop
argument_list|(
operator|(
name|geohashLong
operator|>>>
literal|4
operator|)
operator|<<
operator|(
operator|(
name|level
operator|*
literal|5
operator|)
operator|+
literal|2
operator|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|lat
specifier|public
specifier|final
name|double
name|lat
parameter_list|()
block|{
return|return
name|this
operator|.
name|lat
return|;
block|}
DECL|method|getLat
specifier|public
specifier|final
name|double
name|getLat
parameter_list|()
block|{
return|return
name|this
operator|.
name|lat
return|;
block|}
DECL|method|lon
specifier|public
specifier|final
name|double
name|lon
parameter_list|()
block|{
return|return
name|this
operator|.
name|lon
return|;
block|}
DECL|method|getLon
specifier|public
specifier|final
name|double
name|getLon
parameter_list|()
block|{
return|return
name|this
operator|.
name|lon
return|;
block|}
DECL|method|geohash
specifier|public
specifier|final
name|String
name|geohash
parameter_list|()
block|{
return|return
name|XGeoHashUtils
operator|.
name|stringEncode
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|)
return|;
block|}
DECL|method|getGeohash
specifier|public
specifier|final
name|String
name|getGeohash
parameter_list|()
block|{
return|return
name|XGeoHashUtils
operator|.
name|stringEncode
argument_list|(
name|lon
argument_list|,
name|lat
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|GeoPoint
name|geoPoint
init|=
operator|(
name|GeoPoint
operator|)
name|o
decl_stmt|;
specifier|final
name|double
name|lonCompare
init|=
name|geoPoint
operator|.
name|lon
operator|-
name|lon
decl_stmt|;
specifier|final
name|double
name|latCompare
init|=
name|geoPoint
operator|.
name|lat
operator|-
name|lat
decl_stmt|;
if|if
condition|(
operator|(
name|lonCompare
argument_list|<
operator|-
name|TOLERANCE
operator|||
name|lonCompare
argument_list|>
name|TOLERANCE
operator|)
operator|||
operator|(
name|latCompare
argument_list|<
operator|-
name|TOLERANCE
operator|||
name|latCompare
argument_list|>
name|TOLERANCE
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|temp
operator|=
name|lat
operator|!=
operator|+
literal|0.0d
condition|?
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|lat
argument_list|)
else|:
literal|0L
expr_stmt|;
name|result
operator|=
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|lon
operator|!=
operator|+
literal|0.0d
condition|?
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|lon
argument_list|)
else|:
literal|0L
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|lat
operator|+
literal|", "
operator|+
name|lon
operator|+
literal|"]"
return|;
block|}
DECL|method|parseFromLatLon
specifier|public
specifier|static
name|GeoPoint
name|parseFromLatLon
parameter_list|(
name|String
name|latLon
parameter_list|)
block|{
name|GeoPoint
name|point
init|=
operator|new
name|GeoPoint
argument_list|(
name|latLon
argument_list|)
decl_stmt|;
return|return
name|point
return|;
block|}
DECL|method|fromGeohash
specifier|public
specifier|static
name|GeoPoint
name|fromGeohash
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
return|return
operator|new
name|GeoPoint
argument_list|()
operator|.
name|resetFromGeoHash
argument_list|(
name|geohash
argument_list|)
return|;
block|}
DECL|method|fromGeohash
specifier|public
specifier|static
name|GeoPoint
name|fromGeohash
parameter_list|(
name|long
name|geohashLong
parameter_list|)
block|{
return|return
operator|new
name|GeoPoint
argument_list|()
operator|.
name|resetFromGeoHash
argument_list|(
name|geohashLong
argument_list|)
return|;
block|}
DECL|method|fromIndexLong
specifier|public
specifier|static
name|GeoPoint
name|fromIndexLong
parameter_list|(
name|long
name|indexLong
parameter_list|)
block|{
return|return
operator|new
name|GeoPoint
argument_list|()
operator|.
name|resetFromIndexHash
argument_list|(
name|indexLong
argument_list|)
return|;
block|}
block|}
end_class

end_unit

