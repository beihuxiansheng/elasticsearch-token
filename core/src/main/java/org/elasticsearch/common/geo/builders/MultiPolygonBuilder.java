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
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
operator|.
name|XShapeCollection
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
specifier|private
specifier|final
name|List
argument_list|<
name|PolygonBuilder
argument_list|>
name|polygons
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|orientation
specifier|private
specifier|final
name|Orientation
name|orientation
decl_stmt|;
comment|/**      * Build a MultiPolygonBuilder with RIGHT orientation.      */
DECL|method|MultiPolygonBuilder
specifier|public
name|MultiPolygonBuilder
parameter_list|()
block|{
name|this
argument_list|(
name|Orientation
operator|.
name|RIGHT
argument_list|)
expr_stmt|;
block|}
comment|/**      * Build a MultiPolygonBuilder with an arbitrary orientation.      */
DECL|method|MultiPolygonBuilder
specifier|public
name|MultiPolygonBuilder
parameter_list|(
name|Orientation
name|orientation
parameter_list|)
block|{
name|this
operator|.
name|orientation
operator|=
name|orientation
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|MultiPolygonBuilder
specifier|public
name|MultiPolygonBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|orientation
operator|=
name|Orientation
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|holes
init|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|holes
condition|;
name|i
operator|++
control|)
block|{
name|polygon
argument_list|(
operator|new
name|PolygonBuilder
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|orientation
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|polygons
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PolygonBuilder
name|polygon
range|:
name|polygons
control|)
block|{
name|polygon
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|orientation
specifier|public
name|Orientation
name|orientation
parameter_list|()
block|{
return|return
name|this
operator|.
name|orientation
return|;
block|}
comment|/**      * Add a shallow copy of the polygon to the multipolygon. This will apply the orientation of the      * {@link MultiPolygonBuilder} to the polygon if polygon has different orientation.      */
DECL|method|polygon
specifier|public
name|MultiPolygonBuilder
name|polygon
parameter_list|(
name|PolygonBuilder
name|polygon
parameter_list|)
block|{
name|PolygonBuilder
name|pb
init|=
operator|new
name|PolygonBuilder
argument_list|(
operator|new
name|CoordinatesBuilder
argument_list|()
operator|.
name|coordinates
argument_list|(
name|polygon
operator|.
name|shell
argument_list|()
operator|.
name|coordinates
argument_list|(
literal|false
argument_list|)
argument_list|)
argument_list|,
name|this
operator|.
name|orientation
argument_list|)
decl_stmt|;
for|for
control|(
name|LineStringBuilder
name|hole
range|:
name|polygon
operator|.
name|holes
argument_list|()
control|)
block|{
name|pb
operator|.
name|hole
argument_list|(
name|hole
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|polygons
operator|.
name|add
argument_list|(
name|pb
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * get the list of polygons      */
DECL|method|polygons
specifier|public
name|List
argument_list|<
name|PolygonBuilder
argument_list|>
name|polygons
parameter_list|()
block|{
return|return
name|polygons
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
name|shapeName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|FIELD_ORIENTATION
argument_list|,
name|orientation
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
name|startArray
argument_list|(
name|FIELD_COORDINATES
argument_list|)
expr_stmt|;
for|for
control|(
name|PolygonBuilder
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
name|List
argument_list|<
name|Shape
argument_list|>
name|shapes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|this
operator|.
name|polygons
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|wrapdateline
condition|)
block|{
for|for
control|(
name|PolygonBuilder
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
name|shapes
operator|.
name|add
argument_list|(
name|jtsGeometry
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
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|PolygonBuilder
name|polygon
range|:
name|this
operator|.
name|polygons
control|)
block|{
name|shapes
operator|.
name|add
argument_list|(
name|jtsGeometry
argument_list|(
name|polygon
operator|.
name|toPolygon
argument_list|(
name|FACTORY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|shapes
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
return|return
name|shapes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
else|else
return|return
operator|new
name|XShapeCollection
argument_list|<>
argument_list|(
name|shapes
argument_list|,
name|SPATIAL_CONTEXT
argument_list|)
return|;
comment|//note: ShapeCollection is probably faster than a Multi* geom.
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|polygons
argument_list|,
name|orientation
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|MultiPolygonBuilder
name|other
init|=
operator|(
name|MultiPolygonBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|polygons
argument_list|,
name|other
operator|.
name|polygons
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|orientation
argument_list|,
name|other
operator|.
name|orientation
argument_list|)
return|;
block|}
block|}
end_class

end_unit

