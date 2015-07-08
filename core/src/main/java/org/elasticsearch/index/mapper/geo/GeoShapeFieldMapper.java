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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
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
name|index
operator|.
name|IndexOptions
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
name|prefix
operator|.
name|PrefixTreeStrategy
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
name|prefix
operator|.
name|RecursivePrefixTreeStrategy
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
name|prefix
operator|.
name|TermQueryPrefixTreeStrategy
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
name|prefix
operator|.
name|tree
operator|.
name|GeohashPrefixTree
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
name|prefix
operator|.
name|tree
operator|.
name|PackedQuadPrefixTree
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
name|prefix
operator|.
name|tree
operator|.
name|QuadPrefixTree
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
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|Strings
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
name|GeoUtils
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
name|geo
operator|.
name|builders
operator|.
name|ShapeBuilder
operator|.
name|Orientation
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
name|settings
operator|.
name|Settings
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
name|unit
operator|.
name|DistanceUnit
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|FieldMapper
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
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|Mapper
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
name|MapperParsingException
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
name|ParseContext
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
name|Iterator
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
name|Map
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MapperBuilders
operator|.
name|geoShapeField
import|;
end_import

begin_comment
comment|/**  * FieldMapper for indexing {@link com.spatial4j.core.shape.Shape}s.  *<p/>  * Currently Shapes can only be indexed and can only be queried using  * {@link org.elasticsearch.index.query.GeoShapeQueryParser}, consequently  * a lot of behavior in this Mapper is disabled.  *<p/>  * Format supported:  *<p/>  * "field" : {  * "type" : "polygon",  * "coordinates" : [  * [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ]  * ]  * }  */
end_comment

begin_class
DECL|class|GeoShapeFieldMapper
specifier|public
class|class
name|GeoShapeFieldMapper
extends|extends
name|FieldMapper
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"geo_shape"
decl_stmt|;
DECL|class|Names
specifier|public
specifier|static
class|class
name|Names
block|{
DECL|field|TREE
specifier|public
specifier|static
specifier|final
name|String
name|TREE
init|=
literal|"tree"
decl_stmt|;
DECL|field|TREE_GEOHASH
specifier|public
specifier|static
specifier|final
name|String
name|TREE_GEOHASH
init|=
literal|"geohash"
decl_stmt|;
DECL|field|TREE_QUADTREE
specifier|public
specifier|static
specifier|final
name|String
name|TREE_QUADTREE
init|=
literal|"quadtree"
decl_stmt|;
DECL|field|TREE_LEVELS
specifier|public
specifier|static
specifier|final
name|String
name|TREE_LEVELS
init|=
literal|"tree_levels"
decl_stmt|;
DECL|field|TREE_PRESISION
specifier|public
specifier|static
specifier|final
name|String
name|TREE_PRESISION
init|=
literal|"precision"
decl_stmt|;
DECL|field|DISTANCE_ERROR_PCT
specifier|public
specifier|static
specifier|final
name|String
name|DISTANCE_ERROR_PCT
init|=
literal|"distance_error_pct"
decl_stmt|;
DECL|field|ORIENTATION
specifier|public
specifier|static
specifier|final
name|String
name|ORIENTATION
init|=
literal|"orientation"
decl_stmt|;
DECL|field|STRATEGY
specifier|public
specifier|static
specifier|final
name|String
name|STRATEGY
init|=
literal|"strategy"
decl_stmt|;
block|}
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
block|{
DECL|field|TREE
specifier|public
specifier|static
specifier|final
name|String
name|TREE
init|=
name|Names
operator|.
name|TREE_GEOHASH
decl_stmt|;
DECL|field|STRATEGY
specifier|public
specifier|static
specifier|final
name|String
name|STRATEGY
init|=
name|SpatialStrategy
operator|.
name|RECURSIVE
operator|.
name|getStrategyName
argument_list|()
decl_stmt|;
DECL|field|GEOHASH_LEVELS
specifier|public
specifier|static
specifier|final
name|int
name|GEOHASH_LEVELS
init|=
name|GeoUtils
operator|.
name|geoHashLevelsForPrecision
argument_list|(
literal|"50m"
argument_list|)
decl_stmt|;
DECL|field|QUADTREE_LEVELS
specifier|public
specifier|static
specifier|final
name|int
name|QUADTREE_LEVELS
init|=
name|GeoUtils
operator|.
name|quadTreeLevelsForPrecision
argument_list|(
literal|"50m"
argument_list|)
decl_stmt|;
DECL|field|LEGACY_DISTANCE_ERROR_PCT
specifier|public
specifier|static
specifier|final
name|double
name|LEGACY_DISTANCE_ERROR_PCT
init|=
literal|0.025d
decl_stmt|;
DECL|field|ORIENTATION
specifier|public
specifier|static
specifier|final
name|Orientation
name|ORIENTATION
init|=
name|Orientation
operator|.
name|RIGHT
decl_stmt|;
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|MappedFieldType
name|FIELD_TYPE
init|=
operator|new
name|GeoShapeFieldType
argument_list|()
decl_stmt|;
static|static
block|{
comment|// setting name here is a hack so freeze can be called...instead all these options should be
comment|// moved to the default ctor for GeoShapeFieldType, and defaultFieldType() should be removed from mappers...
name|FIELD_TYPE
operator|.
name|setNames
argument_list|(
operator|new
name|MappedFieldType
operator|.
name|Names
argument_list|(
literal|"DoesNotExist"
argument_list|)
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setStored
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setStoreTermVectors
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|FieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|GeoShapeFieldMapper
argument_list|>
block|{
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
expr_stmt|;
block|}
DECL|method|fieldType
specifier|public
name|GeoShapeFieldType
name|fieldType
parameter_list|()
block|{
return|return
operator|(
name|GeoShapeFieldType
operator|)
name|fieldType
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|GeoShapeFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|GeoShapeFieldType
name|geoShapeFieldType
init|=
operator|(
name|GeoShapeFieldType
operator|)
name|fieldType
decl_stmt|;
if|if
condition|(
name|geoShapeFieldType
operator|.
name|tree
operator|.
name|equals
argument_list|(
literal|"quadtree"
argument_list|)
operator|&&
name|context
operator|.
name|indexCreatedVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_0_0
argument_list|)
condition|)
block|{
name|geoShapeFieldType
operator|.
name|setTree
argument_list|(
literal|"legacyquadtree"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|indexCreatedVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_0_0
argument_list|)
operator|||
operator|(
name|geoShapeFieldType
operator|.
name|treeLevels
argument_list|()
operator|==
literal|0
operator|&&
name|geoShapeFieldType
operator|.
name|precisionInMeters
argument_list|()
operator|<
literal|0
operator|)
condition|)
block|{
name|geoShapeFieldType
operator|.
name|setDefaultDistanceErrorPct
argument_list|(
name|Defaults
operator|.
name|LEGACY_DISTANCE_ERROR_PCT
argument_list|)
expr_stmt|;
block|}
name|setupFieldType
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
operator|new
name|GeoShapeFieldMapper
argument_list|(
name|name
argument_list|,
name|fieldType
argument_list|,
name|context
operator|.
name|indexSettings
argument_list|()
argument_list|,
name|multiFieldsBuilder
operator|.
name|build
argument_list|(
name|this
argument_list|,
name|context
argument_list|)
argument_list|,
name|copyTo
argument_list|)
return|;
block|}
block|}
DECL|class|TypeParser
specifier|public
specifier|static
class|class
name|TypeParser
implements|implements
name|Mapper
operator|.
name|TypeParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Mapper
operator|.
name|Builder
name|parse
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|node
parameter_list|,
name|ParserContext
name|parserContext
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|Builder
name|builder
init|=
name|geoShapeField
argument_list|(
name|name
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|iterator
init|=
name|node
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|fieldNode
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|Names
operator|.
name|TREE
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setTree
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Names
operator|.
name|TREE_LEVELS
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setTreeLevels
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Names
operator|.
name|TREE_PRESISION
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setPrecisionInMeters
argument_list|(
name|DistanceUnit
operator|.
name|parse
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Names
operator|.
name|DISTANCE_ERROR_PCT
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setDistanceErrorPct
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Names
operator|.
name|ORIENTATION
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setOrientation
argument_list|(
name|ShapeBuilder
operator|.
name|orientationFromString
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Names
operator|.
name|STRATEGY
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setStrategyName
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|class|GeoShapeFieldType
specifier|public
specifier|static
specifier|final
class|class
name|GeoShapeFieldType
extends|extends
name|MappedFieldType
block|{
DECL|field|tree
specifier|private
name|String
name|tree
init|=
name|Defaults
operator|.
name|TREE
decl_stmt|;
DECL|field|strategyName
specifier|private
name|String
name|strategyName
init|=
name|Defaults
operator|.
name|STRATEGY
decl_stmt|;
DECL|field|treeLevels
specifier|private
name|int
name|treeLevels
init|=
literal|0
decl_stmt|;
DECL|field|precisionInMeters
specifier|private
name|double
name|precisionInMeters
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|distanceErrorPct
specifier|private
name|Double
name|distanceErrorPct
decl_stmt|;
DECL|field|defaultDistanceErrorPct
specifier|private
name|double
name|defaultDistanceErrorPct
init|=
literal|0.0
decl_stmt|;
DECL|field|orientation
specifier|private
name|Orientation
name|orientation
init|=
name|Defaults
operator|.
name|ORIENTATION
decl_stmt|;
comment|// these are built when the field type is frozen
DECL|field|defaultStrategy
specifier|private
name|PrefixTreeStrategy
name|defaultStrategy
decl_stmt|;
DECL|field|recursiveStrategy
specifier|private
name|RecursivePrefixTreeStrategy
name|recursiveStrategy
decl_stmt|;
DECL|field|termStrategy
specifier|private
name|TermQueryPrefixTreeStrategy
name|termStrategy
decl_stmt|;
DECL|method|GeoShapeFieldType
specifier|public
name|GeoShapeFieldType
parameter_list|()
block|{}
DECL|method|GeoShapeFieldType
specifier|protected
name|GeoShapeFieldType
parameter_list|(
name|GeoShapeFieldType
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|this
operator|.
name|tree
operator|=
name|ref
operator|.
name|tree
expr_stmt|;
name|this
operator|.
name|strategyName
operator|=
name|ref
operator|.
name|strategyName
expr_stmt|;
name|this
operator|.
name|treeLevels
operator|=
name|ref
operator|.
name|treeLevels
expr_stmt|;
name|this
operator|.
name|precisionInMeters
operator|=
name|ref
operator|.
name|precisionInMeters
expr_stmt|;
name|this
operator|.
name|distanceErrorPct
operator|=
name|ref
operator|.
name|distanceErrorPct
expr_stmt|;
name|this
operator|.
name|defaultDistanceErrorPct
operator|=
name|ref
operator|.
name|defaultDistanceErrorPct
expr_stmt|;
name|this
operator|.
name|orientation
operator|=
name|ref
operator|.
name|orientation
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|GeoShapeFieldType
name|clone
parameter_list|()
block|{
return|return
operator|new
name|GeoShapeFieldType
argument_list|(
name|this
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
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|GeoShapeFieldType
name|that
init|=
operator|(
name|GeoShapeFieldType
operator|)
name|o
decl_stmt|;
return|return
name|treeLevels
operator|==
name|that
operator|.
name|treeLevels
operator|&&
name|precisionInMeters
operator|==
name|that
operator|.
name|precisionInMeters
operator|&&
name|defaultDistanceErrorPct
operator|==
name|that
operator|.
name|defaultDistanceErrorPct
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|tree
argument_list|,
name|that
operator|.
name|tree
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|strategyName
argument_list|,
name|that
operator|.
name|strategyName
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|distanceErrorPct
argument_list|,
name|that
operator|.
name|distanceErrorPct
argument_list|)
operator|&&
name|orientation
operator|==
name|that
operator|.
name|orientation
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
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|super
operator|.
name|hashCode
argument_list|()
argument_list|,
name|tree
argument_list|,
name|strategyName
argument_list|,
name|treeLevels
argument_list|,
name|precisionInMeters
argument_list|,
name|distanceErrorPct
argument_list|,
name|defaultDistanceErrorPct
argument_list|,
name|orientation
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|typeName
specifier|public
name|String
name|typeName
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|freeze
specifier|public
name|void
name|freeze
parameter_list|()
block|{
name|super
operator|.
name|freeze
argument_list|()
expr_stmt|;
comment|// This is a bit hackish: we need to setup the spatial tree and strategies once the field name is set, which
comment|// must be by the time freeze is called.
name|SpatialPrefixTree
name|prefixTree
decl_stmt|;
if|if
condition|(
literal|"geohash"
operator|.
name|equals
argument_list|(
name|tree
argument_list|)
condition|)
block|{
name|prefixTree
operator|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ShapeBuilder
operator|.
name|SPATIAL_CONTEXT
argument_list|,
name|getLevels
argument_list|(
name|treeLevels
argument_list|,
name|precisionInMeters
argument_list|,
name|Defaults
operator|.
name|GEOHASH_LEVELS
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"legacyquadtree"
operator|.
name|equals
argument_list|(
name|tree
argument_list|)
condition|)
block|{
name|prefixTree
operator|=
operator|new
name|QuadPrefixTree
argument_list|(
name|ShapeBuilder
operator|.
name|SPATIAL_CONTEXT
argument_list|,
name|getLevels
argument_list|(
name|treeLevels
argument_list|,
name|precisionInMeters
argument_list|,
name|Defaults
operator|.
name|QUADTREE_LEVELS
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"quadtree"
operator|.
name|equals
argument_list|(
name|tree
argument_list|)
condition|)
block|{
name|prefixTree
operator|=
operator|new
name|PackedQuadPrefixTree
argument_list|(
name|ShapeBuilder
operator|.
name|SPATIAL_CONTEXT
argument_list|,
name|getLevels
argument_list|(
name|treeLevels
argument_list|,
name|precisionInMeters
argument_list|,
name|Defaults
operator|.
name|QUADTREE_LEVELS
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown prefix tree type ["
operator|+
name|tree
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|recursiveStrategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|prefixTree
argument_list|,
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
expr_stmt|;
name|recursiveStrategy
operator|.
name|setDistErrPct
argument_list|(
name|distanceErrorPct
argument_list|()
argument_list|)
expr_stmt|;
name|recursiveStrategy
operator|.
name|setPruneLeafyBranches
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|termStrategy
operator|=
operator|new
name|TermQueryPrefixTreeStrategy
argument_list|(
name|prefixTree
argument_list|,
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
expr_stmt|;
name|termStrategy
operator|.
name|setDistErrPct
argument_list|(
name|distanceErrorPct
argument_list|()
argument_list|)
expr_stmt|;
name|defaultStrategy
operator|=
name|resolveStrategy
argument_list|(
name|strategyName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkCompatibility
specifier|public
name|void
name|checkCompatibility
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|conflicts
parameter_list|,
name|boolean
name|strict
parameter_list|)
block|{
name|super
operator|.
name|checkCompatibility
argument_list|(
name|fieldType
argument_list|,
name|conflicts
argument_list|,
name|strict
argument_list|)
expr_stmt|;
name|GeoShapeFieldType
name|other
init|=
operator|(
name|GeoShapeFieldType
operator|)
name|fieldType
decl_stmt|;
comment|// prevent user from changing strategies
if|if
condition|(
name|strategyName
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|strategyName
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"mapper ["
operator|+
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different strategy"
argument_list|)
expr_stmt|;
block|}
comment|// prevent user from changing trees (changes encoding)
if|if
condition|(
name|tree
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|tree
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"mapper ["
operator|+
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different tree"
argument_list|)
expr_stmt|;
block|}
comment|// TODO we should allow this, but at the moment levels is used to build bookkeeping variables
comment|// in lucene's SpatialPrefixTree implementations, need a patch to correct that first
if|if
condition|(
name|treeLevels
argument_list|()
operator|!=
name|other
operator|.
name|treeLevels
argument_list|()
condition|)
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"mapper ["
operator|+
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different tree_levels"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|precisionInMeters
argument_list|()
operator|!=
name|other
operator|.
name|precisionInMeters
argument_list|()
condition|)
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"mapper ["
operator|+
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different precision"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getLevels
specifier|private
specifier|static
name|int
name|getLevels
parameter_list|(
name|int
name|treeLevels
parameter_list|,
name|double
name|precisionInMeters
parameter_list|,
name|int
name|defaultLevels
parameter_list|,
name|boolean
name|geoHash
parameter_list|)
block|{
if|if
condition|(
name|treeLevels
operator|>
literal|0
operator|||
name|precisionInMeters
operator|>=
literal|0
condition|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|treeLevels
argument_list|,
name|precisionInMeters
operator|>=
literal|0
condition|?
operator|(
name|geoHash
condition|?
name|GeoUtils
operator|.
name|geoHashLevelsForPrecision
argument_list|(
name|precisionInMeters
argument_list|)
else|:
name|GeoUtils
operator|.
name|quadTreeLevelsForPrecision
argument_list|(
name|precisionInMeters
argument_list|)
operator|)
else|:
literal|0
argument_list|)
return|;
block|}
return|return
name|defaultLevels
return|;
block|}
DECL|method|tree
specifier|public
name|String
name|tree
parameter_list|()
block|{
return|return
name|tree
return|;
block|}
DECL|method|setTree
specifier|public
name|void
name|setTree
parameter_list|(
name|String
name|tree
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|tree
operator|=
name|tree
expr_stmt|;
block|}
DECL|method|strategyName
specifier|public
name|String
name|strategyName
parameter_list|()
block|{
return|return
name|strategyName
return|;
block|}
DECL|method|setStrategyName
specifier|public
name|void
name|setStrategyName
parameter_list|(
name|String
name|strategyName
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|strategyName
operator|=
name|strategyName
expr_stmt|;
block|}
DECL|method|treeLevels
specifier|public
name|int
name|treeLevels
parameter_list|()
block|{
return|return
name|treeLevels
return|;
block|}
DECL|method|setTreeLevels
specifier|public
name|void
name|setTreeLevels
parameter_list|(
name|int
name|treeLevels
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|treeLevels
operator|=
name|treeLevels
expr_stmt|;
block|}
DECL|method|precisionInMeters
specifier|public
name|double
name|precisionInMeters
parameter_list|()
block|{
return|return
name|precisionInMeters
return|;
block|}
DECL|method|setPrecisionInMeters
specifier|public
name|void
name|setPrecisionInMeters
parameter_list|(
name|double
name|precisionInMeters
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|precisionInMeters
operator|=
name|precisionInMeters
expr_stmt|;
block|}
DECL|method|distanceErrorPct
specifier|public
name|double
name|distanceErrorPct
parameter_list|()
block|{
return|return
name|distanceErrorPct
operator|==
literal|null
condition|?
name|defaultDistanceErrorPct
else|:
name|distanceErrorPct
return|;
block|}
DECL|method|setDistanceErrorPct
specifier|public
name|void
name|setDistanceErrorPct
parameter_list|(
name|double
name|distanceErrorPct
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|distanceErrorPct
operator|=
name|distanceErrorPct
expr_stmt|;
block|}
DECL|method|setDefaultDistanceErrorPct
specifier|public
name|void
name|setDefaultDistanceErrorPct
parameter_list|(
name|double
name|defaultDistanceErrorPct
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|defaultDistanceErrorPct
operator|=
name|defaultDistanceErrorPct
expr_stmt|;
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
DECL|method|setOrientation
specifier|public
name|void
name|setOrientation
parameter_list|(
name|Orientation
name|orientation
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|orientation
operator|=
name|orientation
expr_stmt|;
block|}
DECL|method|defaultStrategy
specifier|public
name|PrefixTreeStrategy
name|defaultStrategy
parameter_list|()
block|{
return|return
name|this
operator|.
name|defaultStrategy
return|;
block|}
DECL|method|resolveStrategy
specifier|public
name|PrefixTreeStrategy
name|resolveStrategy
parameter_list|(
name|String
name|strategyName
parameter_list|)
block|{
if|if
condition|(
name|SpatialStrategy
operator|.
name|RECURSIVE
operator|.
name|getStrategyName
argument_list|()
operator|.
name|equals
argument_list|(
name|strategyName
argument_list|)
condition|)
block|{
return|return
name|recursiveStrategy
return|;
block|}
if|if
condition|(
name|SpatialStrategy
operator|.
name|TERM
operator|.
name|getStrategyName
argument_list|()
operator|.
name|equals
argument_list|(
name|strategyName
argument_list|)
condition|)
block|{
return|return
name|termStrategy
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown prefix tree strategy ["
operator|+
name|strategyName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|String
name|value
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"GeoShape fields cannot be converted to String values"
argument_list|)
throw|;
block|}
block|}
DECL|method|GeoShapeFieldMapper
specifier|public
name|GeoShapeFieldMapper
parameter_list|(
name|String
name|simpleName
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|,
name|Settings
name|indexSettings
parameter_list|,
name|MultiFields
name|multiFields
parameter_list|,
name|CopyTo
name|copyTo
parameter_list|)
block|{
name|super
argument_list|(
name|simpleName
argument_list|,
name|fieldType
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|,
name|indexSettings
argument_list|,
name|multiFields
argument_list|,
name|copyTo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldType
specifier|public
name|GeoShapeFieldType
name|fieldType
parameter_list|()
block|{
return|return
operator|(
name|GeoShapeFieldType
operator|)
name|super
operator|.
name|fieldType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Mapper
name|parse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Shape
name|shape
init|=
name|context
operator|.
name|parseExternalValue
argument_list|(
name|Shape
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|shape
operator|==
literal|null
condition|)
block|{
name|ShapeBuilder
name|shapeBuilder
init|=
name|ShapeBuilder
operator|.
name|parse
argument_list|(
name|context
operator|.
name|parser
argument_list|()
argument_list|,
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|shapeBuilder
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|shape
operator|=
name|shapeBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|Field
index|[]
name|fields
init|=
name|fieldType
argument_list|()
operator|.
name|defaultStrategy
argument_list|()
operator|.
name|createIndexableFields
argument_list|(
name|shape
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|fields
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
operator|!
name|customBoost
argument_list|()
condition|)
block|{
name|field
operator|.
name|setBoost
argument_list|(
name|fieldType
argument_list|()
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|doc
argument_list|()
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"failed to parse ["
operator|+
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|parseCreateField
specifier|protected
name|void
name|parseCreateField
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|List
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|doXContentBody
specifier|protected
name|void
name|doXContentBody
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|boolean
name|includeDefaults
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|contentType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|tree
argument_list|()
operator|.
name|equals
argument_list|(
name|Defaults
operator|.
name|TREE
argument_list|)
operator|==
literal|false
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Names
operator|.
name|TREE
argument_list|,
name|fieldType
argument_list|()
operator|.
name|tree
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|treeLevels
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Names
operator|.
name|TREE_LEVELS
argument_list|,
name|fieldType
argument_list|()
operator|.
name|treeLevels
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|precisionInMeters
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Names
operator|.
name|TREE_PRESISION
argument_list|,
name|DistanceUnit
operator|.
name|METERS
operator|.
name|toString
argument_list|(
name|fieldType
argument_list|()
operator|.
name|precisionInMeters
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|strategyName
argument_list|()
operator|!=
name|Defaults
operator|.
name|STRATEGY
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Names
operator|.
name|STRATEGY
argument_list|,
name|fieldType
argument_list|()
operator|.
name|strategyName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|distanceErrorPct
argument_list|()
operator|!=
name|fieldType
argument_list|()
operator|.
name|defaultDistanceErrorPct
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Names
operator|.
name|DISTANCE_ERROR_PCT
argument_list|,
name|fieldType
argument_list|()
operator|.
name|distanceErrorPct
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|orientation
argument_list|()
operator|!=
name|Defaults
operator|.
name|ORIENTATION
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Names
operator|.
name|ORIENTATION
argument_list|,
name|fieldType
argument_list|()
operator|.
name|orientation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|contentType
specifier|protected
name|String
name|contentType
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
return|;
block|}
block|}
end_class

end_unit

