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
name|xcontent
operator|.
name|XContentBuilder
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

begin_class
DECL|class|GeoBoundingBoxQueryBuilder
specifier|public
class|class
name|GeoBoundingBoxQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|GeoBoundingBoxQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"geo_bbox"
decl_stmt|;
DECL|field|TOP_LEFT
specifier|public
specifier|static
specifier|final
name|String
name|TOP_LEFT
init|=
name|GeoBoundingBoxQueryParser
operator|.
name|TOP_LEFT
decl_stmt|;
DECL|field|BOTTOM_RIGHT
specifier|public
specifier|static
specifier|final
name|String
name|BOTTOM_RIGHT
init|=
name|GeoBoundingBoxQueryParser
operator|.
name|BOTTOM_RIGHT
decl_stmt|;
DECL|field|TOP
specifier|private
specifier|static
specifier|final
name|int
name|TOP
init|=
literal|0
decl_stmt|;
DECL|field|LEFT
specifier|private
specifier|static
specifier|final
name|int
name|LEFT
init|=
literal|1
decl_stmt|;
DECL|field|BOTTOM
specifier|private
specifier|static
specifier|final
name|int
name|BOTTOM
init|=
literal|2
decl_stmt|;
DECL|field|RIGHT
specifier|private
specifier|static
specifier|final
name|int
name|RIGHT
init|=
literal|3
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|box
specifier|private
name|double
index|[]
name|box
init|=
block|{
name|Double
operator|.
name|NaN
block|,
name|Double
operator|.
name|NaN
block|,
name|Double
operator|.
name|NaN
block|,
name|Double
operator|.
name|NaN
block|}
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|coerce
specifier|private
name|Boolean
name|coerce
decl_stmt|;
DECL|field|ignoreMalformed
specifier|private
name|Boolean
name|ignoreMalformed
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|GeoBoundingBoxQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|GeoBoundingBoxQueryBuilder
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|method|GeoBoundingBoxQueryBuilder
specifier|public
name|GeoBoundingBoxQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * Adds top left point.      *      * @param lat The latitude      * @param lon The longitude      */
DECL|method|topLeft
specifier|public
name|GeoBoundingBoxQueryBuilder
name|topLeft
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|box
index|[
name|TOP
index|]
operator|=
name|lat
expr_stmt|;
name|box
index|[
name|LEFT
index|]
operator|=
name|lon
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|topLeft
specifier|public
name|GeoBoundingBoxQueryBuilder
name|topLeft
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|topLeft
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
DECL|method|topLeft
specifier|public
name|GeoBoundingBoxQueryBuilder
name|topLeft
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
return|return
name|topLeft
argument_list|(
name|GeoHashUtils
operator|.
name|decode
argument_list|(
name|geohash
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Adds bottom right corner.      *      * @param lat The latitude      * @param lon The longitude      */
DECL|method|bottomRight
specifier|public
name|GeoBoundingBoxQueryBuilder
name|bottomRight
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|box
index|[
name|BOTTOM
index|]
operator|=
name|lat
expr_stmt|;
name|box
index|[
name|RIGHT
index|]
operator|=
name|lon
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|bottomRight
specifier|public
name|GeoBoundingBoxQueryBuilder
name|bottomRight
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|bottomRight
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
DECL|method|bottomRight
specifier|public
name|GeoBoundingBoxQueryBuilder
name|bottomRight
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
return|return
name|bottomRight
argument_list|(
name|GeoHashUtils
operator|.
name|decode
argument_list|(
name|geohash
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Adds bottom left corner.      *      * @param lat The latitude      * @param lon The longitude      */
DECL|method|bottomLeft
specifier|public
name|GeoBoundingBoxQueryBuilder
name|bottomLeft
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|box
index|[
name|BOTTOM
index|]
operator|=
name|lat
expr_stmt|;
name|box
index|[
name|LEFT
index|]
operator|=
name|lon
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|bottomLeft
specifier|public
name|GeoBoundingBoxQueryBuilder
name|bottomLeft
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|bottomLeft
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
DECL|method|bottomLeft
specifier|public
name|GeoBoundingBoxQueryBuilder
name|bottomLeft
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
return|return
name|bottomLeft
argument_list|(
name|GeoHashUtils
operator|.
name|decode
argument_list|(
name|geohash
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Adds top right point.      *      * @param lat The latitude      * @param lon The longitude      */
DECL|method|topRight
specifier|public
name|GeoBoundingBoxQueryBuilder
name|topRight
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|box
index|[
name|TOP
index|]
operator|=
name|lat
expr_stmt|;
name|box
index|[
name|RIGHT
index|]
operator|=
name|lon
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|topRight
specifier|public
name|GeoBoundingBoxQueryBuilder
name|topRight
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|topRight
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
DECL|method|topRight
specifier|public
name|GeoBoundingBoxQueryBuilder
name|topRight
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
return|return
name|topRight
argument_list|(
name|GeoHashUtils
operator|.
name|decode
argument_list|(
name|geohash
argument_list|)
argument_list|)
return|;
block|}
DECL|method|coerce
specifier|public
name|GeoBoundingBoxQueryBuilder
name|coerce
parameter_list|(
name|boolean
name|coerce
parameter_list|)
block|{
name|this
operator|.
name|coerce
operator|=
name|coerce
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|ignoreMalformed
specifier|public
name|GeoBoundingBoxQueryBuilder
name|ignoreMalformed
parameter_list|(
name|boolean
name|ignoreMalformed
parameter_list|)
block|{
name|this
operator|.
name|ignoreMalformed
operator|=
name|ignoreMalformed
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the type of executing of the geo bounding box. Can be either `memory` or `indexed`. Defaults      * to `memory`.      */
DECL|method|type
specifier|public
name|GeoBoundingBoxQueryBuilder
name|type
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
return|return
name|this
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
comment|// check values
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|box
index|[
name|TOP
index|]
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"geo_bounding_box requires top latitude to be set"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|box
index|[
name|BOTTOM
index|]
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"geo_bounding_box requires bottom latitude to be set"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|box
index|[
name|RIGHT
index|]
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"geo_bounding_box requires right longitude to be set"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|box
index|[
name|LEFT
index|]
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"geo_bounding_box requires left longitude to be set"
argument_list|)
throw|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|array
argument_list|(
name|TOP_LEFT
argument_list|,
name|box
index|[
name|LEFT
index|]
argument_list|,
name|box
index|[
name|TOP
index|]
argument_list|)
expr_stmt|;
name|builder
operator|.
name|array
argument_list|(
name|BOTTOM_RIGHT
argument_list|,
name|box
index|[
name|RIGHT
index|]
argument_list|,
name|box
index|[
name|BOTTOM
index|]
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|type
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
name|builder
operator|.
name|field
argument_list|(
literal|"coerce"
argument_list|,
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
name|builder
operator|.
name|field
argument_list|(
literal|"ignore_malformed"
argument_list|,
name|ignoreMalformed
argument_list|)
expr_stmt|;
block|}
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
