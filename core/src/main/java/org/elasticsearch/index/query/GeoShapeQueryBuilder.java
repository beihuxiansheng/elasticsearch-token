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
name|ShapeRelation
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
name|SpatialStrategy
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
name|builders
operator|.
name|ShapeBuilder
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

begin_comment
comment|/**  * {@link QueryBuilder} that builds a GeoShape Filter  */
end_comment

begin_class
DECL|class|GeoShapeQueryBuilder
specifier|public
class|class
name|GeoShapeQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|GeoShapeQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"geo_shape"
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|GeoShapeQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|GeoShapeQueryBuilder
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|shape
specifier|private
specifier|final
name|ShapeBuilder
name|shape
decl_stmt|;
DECL|field|strategy
specifier|private
name|SpatialStrategy
name|strategy
init|=
literal|null
decl_stmt|;
DECL|field|indexedShapeId
specifier|private
specifier|final
name|String
name|indexedShapeId
decl_stmt|;
DECL|field|indexedShapeType
specifier|private
specifier|final
name|String
name|indexedShapeType
decl_stmt|;
DECL|field|indexedShapeIndex
specifier|private
name|String
name|indexedShapeIndex
decl_stmt|;
DECL|field|indexedShapePath
specifier|private
name|String
name|indexedShapePath
decl_stmt|;
DECL|field|relation
specifier|private
name|ShapeRelation
name|relation
init|=
literal|null
decl_stmt|;
comment|/**      * Creates a new GeoShapeQueryBuilder whose Filter will be against the      * given field name using the given Shape      *      * @param name  Name of the field that will be filtered      * @param shape Shape used in the filter      */
DECL|method|GeoShapeQueryBuilder
specifier|public
name|GeoShapeQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|ShapeBuilder
name|shape
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|shape
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new GeoShapeQueryBuilder whose Filter will be against the      * given field name using the given Shape      *      * @param name  Name of the field that will be filtered      * @param relation {@link ShapeRelation} of query and indexed shape      * @param shape Shape used in the filter      */
DECL|method|GeoShapeQueryBuilder
specifier|public
name|GeoShapeQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|ShapeBuilder
name|shape
parameter_list|,
name|ShapeRelation
name|relation
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|shape
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|relation
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new GeoShapeQueryBuilder whose Filter will be against the given field name      * and will use the Shape found with the given ID in the given type      *      * @param name             Name of the field that will be filtered      * @param indexedShapeId   ID of the indexed Shape that will be used in the Filter      * @param indexedShapeType Index type of the indexed Shapes      */
DECL|method|GeoShapeQueryBuilder
specifier|public
name|GeoShapeQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexedShapeId
parameter_list|,
name|String
name|indexedShapeType
parameter_list|,
name|ShapeRelation
name|relation
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|indexedShapeId
argument_list|,
name|indexedShapeType
argument_list|,
name|relation
argument_list|)
expr_stmt|;
block|}
DECL|method|GeoShapeQueryBuilder
specifier|private
name|GeoShapeQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|ShapeBuilder
name|shape
parameter_list|,
name|String
name|indexedShapeId
parameter_list|,
name|String
name|indexedShapeType
parameter_list|,
name|ShapeRelation
name|relation
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|shape
operator|=
name|shape
expr_stmt|;
name|this
operator|.
name|indexedShapeId
operator|=
name|indexedShapeId
expr_stmt|;
name|this
operator|.
name|relation
operator|=
name|relation
expr_stmt|;
name|this
operator|.
name|indexedShapeType
operator|=
name|indexedShapeType
expr_stmt|;
block|}
comment|/**      * Defines which spatial strategy will be used for building the geo shape filter. When not set, the strategy that      * will be used will be the one that is associated with the geo shape field in the mappings.      *      * @param strategy The spatial strategy to use for building the geo shape filter      * @return this      */
DECL|method|strategy
specifier|public
name|GeoShapeQueryBuilder
name|strategy
parameter_list|(
name|SpatialStrategy
name|strategy
parameter_list|)
block|{
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the name of the index where the indexed Shape can be found      *      * @param indexedShapeIndex Name of the index where the indexed Shape is      * @return this      */
DECL|method|indexedShapeIndex
specifier|public
name|GeoShapeQueryBuilder
name|indexedShapeIndex
parameter_list|(
name|String
name|indexedShapeIndex
parameter_list|)
block|{
name|this
operator|.
name|indexedShapeIndex
operator|=
name|indexedShapeIndex
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the path of the field in the indexed Shape document that has the Shape itself      *      * @param indexedShapePath Path of the field where the Shape itself is defined      * @return this      */
DECL|method|indexedShapePath
specifier|public
name|GeoShapeQueryBuilder
name|indexedShapePath
parameter_list|(
name|String
name|indexedShapePath
parameter_list|)
block|{
name|this
operator|.
name|indexedShapePath
operator|=
name|indexedShapePath
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the relation of query shape and indexed shape.      *      * @param relation relation of the shapes      * @return this      */
DECL|method|relation
specifier|public
name|GeoShapeQueryBuilder
name|relation
parameter_list|(
name|ShapeRelation
name|relation
parameter_list|)
block|{
name|this
operator|.
name|relation
operator|=
name|relation
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
if|if
condition|(
name|strategy
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"strategy"
argument_list|,
name|strategy
operator|.
name|getStrategyName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shape
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"shape"
argument_list|,
name|shape
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"indexed_shape"
argument_list|)
operator|.
name|field
argument_list|(
literal|"id"
argument_list|,
name|indexedShapeId
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|indexedShapeType
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexedShapeIndex
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
name|indexedShapeIndex
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexedShapePath
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"path"
argument_list|,
name|indexedShapePath
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|relation
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"relation"
argument_list|,
name|relation
operator|.
name|getRelationName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
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
