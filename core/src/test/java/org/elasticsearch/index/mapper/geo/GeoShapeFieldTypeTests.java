begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|index
operator|.
name|mapper
operator|.
name|FieldTypeTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MappedFieldType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_class
DECL|class|GeoShapeFieldTypeTests
specifier|public
class|class
name|GeoShapeFieldTypeTests
extends|extends
name|FieldTypeTestCase
block|{
annotation|@
name|Override
DECL|method|createDefaultFieldType
specifier|protected
name|MappedFieldType
name|createDefaultFieldType
parameter_list|()
block|{
return|return
operator|new
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
argument_list|()
return|;
block|}
annotation|@
name|Before
DECL|method|setupProperties
specifier|public
name|void
name|setupProperties
parameter_list|()
block|{
name|addModifier
argument_list|(
operator|new
name|Modifier
argument_list|(
literal|"tree"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|MappedFieldType
name|ft
parameter_list|)
block|{
operator|(
operator|(
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
operator|)
name|ft
operator|)
operator|.
name|setTree
argument_list|(
literal|"quadtree"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addModifier
argument_list|(
operator|new
name|Modifier
argument_list|(
literal|"strategy"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|MappedFieldType
name|ft
parameter_list|)
block|{
operator|(
operator|(
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
operator|)
name|ft
operator|)
operator|.
name|setStrategyName
argument_list|(
literal|"term"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addModifier
argument_list|(
operator|new
name|Modifier
argument_list|(
literal|"tree_levels"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|MappedFieldType
name|ft
parameter_list|)
block|{
operator|(
operator|(
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
operator|)
name|ft
operator|)
operator|.
name|setTreeLevels
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addModifier
argument_list|(
operator|new
name|Modifier
argument_list|(
literal|"precision"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|MappedFieldType
name|ft
parameter_list|)
block|{
operator|(
operator|(
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
operator|)
name|ft
operator|)
operator|.
name|setPrecisionInMeters
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addModifier
argument_list|(
operator|new
name|Modifier
argument_list|(
literal|"distance_error_pct"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|MappedFieldType
name|ft
parameter_list|)
block|{
operator|(
operator|(
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
operator|)
name|ft
operator|)
operator|.
name|setDefaultDistanceErrorPct
argument_list|(
literal|0.5
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addModifier
argument_list|(
operator|new
name|Modifier
argument_list|(
literal|"orientation"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|MappedFieldType
name|ft
parameter_list|)
block|{
operator|(
operator|(
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
operator|)
name|ft
operator|)
operator|.
name|setOrientation
argument_list|(
name|ShapeBuilder
operator|.
name|Orientation
operator|.
name|LEFT
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

