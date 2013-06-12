begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|document
operator|.
name|FieldType
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
name|FieldInfo
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
name|ElasticSearchIllegalArgumentException
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
name|codec
operator|.
name|docvaluesformat
operator|.
name|DocValuesFormatProvider
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
name|codec
operator|.
name|postingsformat
operator|.
name|PostingsFormatProvider
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
name|fielddata
operator|.
name|FieldDataType
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
operator|.
name|AbstractFieldMapper
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

begin_comment
comment|/**  * FieldMapper for indexing {@link com.spatial4j.core.shape.Shape}s.  *<p/>  * Currently Shapes can only be indexed and can only be queried using  * {@link org.elasticsearch.index.query.GeoShapeFilterParser}, consequently  * a lot of behavior in this Mapper is disabled.  *<p/>  * Format supported:  *<p/>  * "field" : {  * "type" : "polygon",  * "coordinates" : [  * [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ]  * ]  * }  */
end_comment

begin_class
DECL|class|GeoShapeFieldMapper
specifier|public
class|class
name|GeoShapeFieldMapper
extends|extends
name|AbstractFieldMapper
argument_list|<
name|String
argument_list|>
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
DECL|field|DISTANCE_ERROR_PCT
specifier|public
specifier|static
specifier|final
name|double
name|DISTANCE_ERROR_PCT
init|=
literal|0.025d
decl_stmt|;
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|FIELD_TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|FIELD_TYPE
operator|.
name|setIndexed
argument_list|(
literal|true
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
name|setIndexOptions
argument_list|(
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_ONLY
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
name|AbstractFieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|GeoShapeFieldMapper
argument_list|>
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
name|double
name|distanceErrorPct
init|=
name|Defaults
operator|.
name|DISTANCE_ERROR_PCT
decl_stmt|;
DECL|field|prefixTree
specifier|private
name|SpatialPrefixTree
name|prefixTree
decl_stmt|;
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
operator|new
name|FieldType
argument_list|(
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|tree
specifier|public
name|Builder
name|tree
parameter_list|(
name|String
name|tree
parameter_list|)
block|{
name|this
operator|.
name|tree
operator|=
name|tree
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|strategy
specifier|public
name|Builder
name|strategy
parameter_list|(
name|String
name|strategy
parameter_list|)
block|{
name|this
operator|.
name|strategyName
operator|=
name|strategy
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|treeLevelsByDistance
specifier|public
name|Builder
name|treeLevelsByDistance
parameter_list|(
name|double
name|meters
parameter_list|)
block|{
name|this
operator|.
name|precisionInMeters
operator|=
name|meters
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|treeLevels
specifier|public
name|Builder
name|treeLevels
parameter_list|(
name|int
name|treeLevels
parameter_list|)
block|{
name|this
operator|.
name|treeLevels
operator|=
name|treeLevels
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|distanceErrorPct
specifier|public
name|Builder
name|distanceErrorPct
parameter_list|(
name|double
name|distanceErrorPct
parameter_list|)
block|{
name|this
operator|.
name|distanceErrorPct
operator|=
name|distanceErrorPct
expr_stmt|;
return|return
name|this
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
specifier|final
name|FieldMapper
operator|.
name|Names
name|names
init|=
name|buildNames
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|Names
operator|.
name|TREE_GEOHASH
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
name|Names
operator|.
name|TREE_QUADTREE
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
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Unknown prefix tree type ["
operator|+
name|tree
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
operator|new
name|GeoShapeFieldMapper
argument_list|(
name|names
argument_list|,
name|prefixTree
argument_list|,
name|strategyName
argument_list|,
name|distanceErrorPct
argument_list|,
name|fieldType
argument_list|,
name|postingsProvider
argument_list|,
name|docValuesProvider
argument_list|)
return|;
block|}
block|}
DECL|method|getLevels
specifier|private
specifier|static
specifier|final
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
operator|new
name|Builder
argument_list|(
name|name
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|node
operator|.
name|entrySet
argument_list|()
control|)
block|{
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
name|tree
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
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
name|treeLevels
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
name|treeLevelsByDistance
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
name|METERS
argument_list|,
name|DistanceUnit
operator|.
name|METERS
argument_list|)
argument_list|)
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
name|distanceErrorPct
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
name|strategy
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|field|defaultStrategy
specifier|private
specifier|final
name|PrefixTreeStrategy
name|defaultStrategy
decl_stmt|;
DECL|field|recursiveStrategy
specifier|private
specifier|final
name|RecursivePrefixTreeStrategy
name|recursiveStrategy
decl_stmt|;
DECL|field|termStrategy
specifier|private
specifier|final
name|TermQueryPrefixTreeStrategy
name|termStrategy
decl_stmt|;
DECL|method|GeoShapeFieldMapper
specifier|public
name|GeoShapeFieldMapper
parameter_list|(
name|FieldMapper
operator|.
name|Names
name|names
parameter_list|,
name|SpatialPrefixTree
name|tree
parameter_list|,
name|String
name|defaultStrategyName
parameter_list|,
name|double
name|distanceErrorPct
parameter_list|,
name|FieldType
name|fieldType
parameter_list|,
name|PostingsFormatProvider
name|postingsProvider
parameter_list|,
name|DocValuesFormatProvider
name|docValuesProvider
parameter_list|)
block|{
name|super
argument_list|(
name|names
argument_list|,
literal|1
argument_list|,
name|fieldType
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|postingsProvider
argument_list|,
name|docValuesProvider
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|recursiveStrategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|tree
argument_list|,
name|names
operator|.
name|indexName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|recursiveStrategy
operator|.
name|setDistErrPct
argument_list|(
name|distanceErrorPct
argument_list|)
expr_stmt|;
name|this
operator|.
name|termStrategy
operator|=
operator|new
name|TermQueryPrefixTreeStrategy
argument_list|(
name|tree
argument_list|,
name|names
operator|.
name|indexName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|termStrategy
operator|.
name|setDistErrPct
argument_list|(
name|distanceErrorPct
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultStrategy
operator|=
name|resolveStrategy
argument_list|(
name|defaultStrategyName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|defaultFieldType
specifier|public
name|FieldType
name|defaultFieldType
parameter_list|()
block|{
return|return
name|Defaults
operator|.
name|FIELD_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|defaultFieldDataType
specifier|public
name|FieldDataType
name|defaultFieldDataType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasDocValues
specifier|public
name|boolean
name|hasDocValues
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|void
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
name|ShapeBuilder
name|shape
init|=
name|ShapeBuilder
operator|.
name|parse
argument_list|(
name|context
operator|.
name|parser
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|shape
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Field
index|[]
name|fields
init|=
name|defaultStrategy
operator|.
name|createIndexableFields
argument_list|(
name|shape
operator|.
name|build
argument_list|()
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
return|return;
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
name|boost
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|listener
argument_list|()
operator|.
name|beforeFieldAdded
argument_list|(
name|this
argument_list|,
name|field
argument_list|,
name|context
argument_list|)
condition|)
block|{
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
name|names
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
comment|// TODO: Come up with a better way to get the name, maybe pass it from builder
if|if
condition|(
name|defaultStrategy
operator|.
name|getGrid
argument_list|()
operator|instanceof
name|GeohashPrefixTree
condition|)
block|{
comment|// Don't emit the tree name since GeohashPrefixTree is the default
comment|// Only emit the tree levels if it isn't the default value
if|if
condition|(
name|defaultStrategy
operator|.
name|getGrid
argument_list|()
operator|.
name|getMaxLevels
argument_list|()
operator|!=
name|Defaults
operator|.
name|GEOHASH_LEVELS
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
name|defaultStrategy
operator|.
name|getGrid
argument_list|()
operator|.
name|getMaxLevels
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
name|Names
operator|.
name|TREE
argument_list|,
name|Names
operator|.
name|TREE_QUADTREE
argument_list|)
expr_stmt|;
if|if
condition|(
name|defaultStrategy
operator|.
name|getGrid
argument_list|()
operator|.
name|getMaxLevels
argument_list|()
operator|!=
name|Defaults
operator|.
name|QUADTREE_LEVELS
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
name|defaultStrategy
operator|.
name|getGrid
argument_list|()
operator|.
name|getMaxLevels
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|defaultStrategy
operator|.
name|getDistErrPct
argument_list|()
operator|!=
name|Defaults
operator|.
name|DISTANCE_ERROR_PCT
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
name|defaultStrategy
operator|.
name|getDistErrPct
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
DECL|method|recursiveStrategy
specifier|public
name|PrefixTreeStrategy
name|recursiveStrategy
parameter_list|()
block|{
return|return
name|this
operator|.
name|recursiveStrategy
return|;
block|}
DECL|method|termStrategy
specifier|public
name|PrefixTreeStrategy
name|termStrategy
parameter_list|()
block|{
return|return
name|this
operator|.
name|termStrategy
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
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Unknown prefix tree strategy ["
operator|+
name|strategyName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

