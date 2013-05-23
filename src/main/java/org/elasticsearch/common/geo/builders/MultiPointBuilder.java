begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.geo.builders
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
operator|.
name|builders
package|;
end_package

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|jts
operator|.
name|JtsGeometry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|vividsolutions
operator|.
name|jts
operator|.
name|geom
operator|.
name|Coordinate
import|;
end_import

begin_import
import|import
name|com
operator|.
name|vividsolutions
operator|.
name|jts
operator|.
name|geom
operator|.
name|MultiPoint
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
DECL|class|MultiPointBuilder
specifier|public
class|class
name|MultiPointBuilder
extends|extends
name|PointCollection
argument_list|<
name|MultiPointBuilder
argument_list|>
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|GeoShapeType
name|TYPE
init|=
name|GeoShapeType
operator|.
name|MULTIPOINT
decl_stmt|;
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
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
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|FIELD_TYPE
argument_list|,
name|TYPE
operator|.
name|shapename
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|FIELD_COORDINATES
argument_list|)
expr_stmt|;
name|super
operator|.
name|coordinatesToXcontent
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|Shape
name|build
parameter_list|()
block|{
name|MultiPoint
name|geometry
init|=
name|FACTORY
operator|.
name|createMultiPoint
argument_list|(
name|points
operator|.
name|toArray
argument_list|(
operator|new
name|Coordinate
index|[
name|points
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|JtsGeometry
argument_list|(
name|geometry
argument_list|,
name|SPATIAL_CONTEXT
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|GeoShapeType
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
block|}
end_class

end_unit

