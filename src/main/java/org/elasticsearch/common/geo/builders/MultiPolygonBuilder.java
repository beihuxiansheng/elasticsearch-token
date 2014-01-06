begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Geometry
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
name|Polygon
import|;
end_import

begin_class
DECL|class|MultiPolygonBuilder
specifier|public
class|class
name|MultiPolygonBuilder
extends|extends
name|ShapeBuilder
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
name|MULTIPOLYGON
decl_stmt|;
DECL|field|polygons
specifier|protected
specifier|final
name|ArrayList
argument_list|<
name|BasePolygonBuilder
argument_list|<
name|?
argument_list|>
argument_list|>
name|polygons
init|=
operator|new
name|ArrayList
argument_list|<
name|BasePolygonBuilder
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|polygon
specifier|public
name|MultiPolygonBuilder
name|polygon
parameter_list|(
name|BasePolygonBuilder
argument_list|<
name|?
argument_list|>
name|polygon
parameter_list|)
block|{
name|this
operator|.
name|polygons
operator|.
name|add
argument_list|(
name|polygon
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|polygon
specifier|public
name|InternalPolygonBuilder
name|polygon
parameter_list|()
block|{
name|InternalPolygonBuilder
name|polygon
init|=
operator|new
name|InternalPolygonBuilder
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|this
operator|.
name|polygon
argument_list|(
name|polygon
argument_list|)
expr_stmt|;
return|return
name|polygon
return|;
block|}
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
name|startArray
argument_list|(
name|FIELD_COORDINATES
argument_list|)
expr_stmt|;
for|for
control|(
name|BasePolygonBuilder
argument_list|<
name|?
argument_list|>
name|polygon
range|:
name|polygons
control|)
block|{
name|builder
operator|.
name|startArray
argument_list|()
expr_stmt|;
name|polygon
operator|.
name|coordinatesArray
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|endObject
argument_list|()
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
annotation|@
name|Override
DECL|method|build
specifier|public
name|Shape
name|build
parameter_list|()
block|{
name|Polygon
index|[]
name|polygons
decl_stmt|;
if|if
condition|(
name|wrapdateline
condition|)
block|{
name|ArrayList
argument_list|<
name|Polygon
argument_list|>
name|polygonSet
init|=
operator|new
name|ArrayList
argument_list|<
name|Polygon
argument_list|>
argument_list|(
name|this
operator|.
name|polygons
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|BasePolygonBuilder
argument_list|<
name|?
argument_list|>
name|polygon
range|:
name|this
operator|.
name|polygons
control|)
block|{
for|for
control|(
name|Coordinate
index|[]
index|[]
name|part
range|:
name|polygon
operator|.
name|coordinates
argument_list|()
control|)
block|{
name|polygonSet
operator|.
name|add
argument_list|(
name|PolygonBuilder
operator|.
name|polygon
argument_list|(
name|FACTORY
argument_list|,
name|part
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|polygons
operator|=
name|polygonSet
operator|.
name|toArray
argument_list|(
operator|new
name|Polygon
index|[
name|polygonSet
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|polygons
operator|=
operator|new
name|Polygon
index|[
name|this
operator|.
name|polygons
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|Iterator
argument_list|<
name|BasePolygonBuilder
argument_list|<
name|?
argument_list|>
argument_list|>
name|iterator
init|=
name|this
operator|.
name|polygons
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|polygons
index|[
name|i
index|]
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|toPolygon
argument_list|(
name|FACTORY
argument_list|)
expr_stmt|;
block|}
block|}
name|Geometry
name|geometry
init|=
name|polygons
operator|.
name|length
operator|==
literal|1
condition|?
name|polygons
index|[
literal|0
index|]
else|:
name|FACTORY
operator|.
name|createMultiPolygon
argument_list|(
name|polygons
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
operator|!
name|wrapdateline
argument_list|)
return|;
block|}
DECL|class|InternalPolygonBuilder
specifier|public
specifier|static
class|class
name|InternalPolygonBuilder
extends|extends
name|BasePolygonBuilder
argument_list|<
name|InternalPolygonBuilder
argument_list|>
block|{
DECL|field|collection
specifier|private
specifier|final
name|MultiPolygonBuilder
name|collection
decl_stmt|;
DECL|method|InternalPolygonBuilder
specifier|private
name|InternalPolygonBuilder
parameter_list|(
name|MultiPolygonBuilder
name|collection
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
name|this
operator|.
name|shell
operator|=
operator|new
name|Ring
argument_list|<
name|InternalPolygonBuilder
argument_list|>
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|MultiPolygonBuilder
name|close
parameter_list|()
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|collection
return|;
block|}
block|}
block|}
end_class

end_unit

