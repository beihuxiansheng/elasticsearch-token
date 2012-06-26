begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|context
operator|.
name|SpatialContext
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
name|context
operator|.
name|jts
operator|.
name|JtsSpatialContext
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
name|distance
operator|.
name|DistanceUnits
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
name|document
operator|.
name|Fieldable
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
name|lucene
operator|.
name|spatial
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
name|elasticsearch
operator|.
name|common
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
name|elasticsearch
operator|.
name|common
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
name|GeoJSONShapeParser
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
name|Map
import|;
end_import

begin_comment
comment|/**  * FieldMapper for indexing {@link com.spatial4j.core.shape.Shape}s.  *  * Currently Shapes can only be indexed and can only be queried using  * {@link org.elasticsearch.index.query.GeoShapeFilterParser}, consequently  * a lot of behavior in this Mapper is disabled.  *  * Format supported:  *  * "field" : {  *     "type" : "polygon",  *     "coordinates" : [  *          [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ]  *     ]  * }  */
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
comment|// TODO: Unsure if the units actually matter since we dont do distance calculations
DECL|field|SPATIAL_CONTEXT
specifier|public
specifier|static
specifier|final
name|SpatialContext
name|SPATIAL_CONTEXT
init|=
operator|new
name|JtsSpatialContext
argument_list|(
name|DistanceUnits
operator|.
name|KILOMETERS
argument_list|)
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
DECL|field|TREE_LEVELS
specifier|public
specifier|static
specifier|final
name|String
name|TREE_LEVELS
init|=
literal|"tree_levels"
decl_stmt|;
DECL|field|GEOHASH
specifier|public
specifier|static
specifier|final
name|String
name|GEOHASH
init|=
literal|"geohash"
decl_stmt|;
DECL|field|QUADTREE
specifier|public
specifier|static
specifier|final
name|String
name|QUADTREE
init|=
literal|"quadtree"
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
name|GEOHASH
decl_stmt|;
DECL|field|GEOHASH_LEVELS
specifier|public
specifier|static
specifier|final
name|int
name|GEOHASH_LEVELS
init|=
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
decl_stmt|;
DECL|field|QUADTREE_LEVELS
specifier|public
specifier|static
specifier|final
name|int
name|QUADTREE_LEVELS
init|=
name|QuadPrefixTree
operator|.
name|DEFAULT_MAX_LEVELS
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
DECL|field|treeLevels
specifier|private
name|int
name|treeLevels
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
if|if
condition|(
name|tree
operator|.
name|equals
argument_list|(
name|Names
operator|.
name|GEOHASH
argument_list|)
condition|)
block|{
name|int
name|levels
init|=
name|treeLevels
operator|!=
literal|0
condition|?
name|treeLevels
else|:
name|Defaults
operator|.
name|GEOHASH_LEVELS
decl_stmt|;
name|prefixTree
operator|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|SPATIAL_CONTEXT
argument_list|,
name|levels
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tree
operator|.
name|equals
argument_list|(
name|Names
operator|.
name|QUADTREE
argument_list|)
condition|)
block|{
name|int
name|levels
init|=
name|treeLevels
operator|!=
literal|0
condition|?
name|treeLevels
else|:
name|Defaults
operator|.
name|QUADTREE_LEVELS
decl_stmt|;
name|prefixTree
operator|=
operator|new
name|QuadPrefixTree
argument_list|(
name|SPATIAL_CONTEXT
argument_list|,
name|levels
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
name|buildNames
argument_list|(
name|context
argument_list|)
argument_list|,
name|prefixTree
argument_list|,
name|distanceErrorPct
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
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|field|spatialStrategy
specifier|private
specifier|final
name|SpatialStrategy
name|spatialStrategy
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
name|prefixTree
parameter_list|,
name|double
name|distanceErrorPct
parameter_list|)
block|{
name|super
argument_list|(
name|names
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|spatialStrategy
operator|=
operator|new
name|TermQueryPrefixTreeStrategy
argument_list|(
name|names
argument_list|,
name|prefixTree
argument_list|,
name|distanceErrorPct
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseCreateField
specifier|protected
name|Fieldable
name|parseCreateField
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|spatialStrategy
operator|.
name|createField
argument_list|(
name|GeoJSONShapeParser
operator|.
name|parse
argument_list|(
name|context
operator|.
name|parser
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
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
name|spatialStrategy
operator|.
name|getPrefixTree
argument_list|()
operator|instanceof
name|GeohashPrefixTree
condition|)
block|{
comment|// Don't emit the tree name since GeohashPrefixTree is the default
comment|// Only emit the tree levels if it isn't the default value
if|if
condition|(
name|spatialStrategy
operator|.
name|getPrefixTree
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
name|spatialStrategy
operator|.
name|getPrefixTree
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
name|QUADTREE
argument_list|)
expr_stmt|;
if|if
condition|(
name|spatialStrategy
operator|.
name|getPrefixTree
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
name|spatialStrategy
operator|.
name|getPrefixTree
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
name|spatialStrategy
operator|.
name|getDistanceErrorPct
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
name|spatialStrategy
operator|.
name|getDistanceErrorPct
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
name|Fieldable
name|field
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
annotation|@
name|Override
DECL|method|valueFromString
specifier|public
name|String
name|valueFromString
parameter_list|(
name|String
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
annotation|@
name|Override
DECL|method|valueAsString
specifier|public
name|String
name|valueAsString
parameter_list|(
name|Fieldable
name|field
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
DECL|method|spatialStrategy
specifier|public
name|SpatialStrategy
name|spatialStrategy
parameter_list|()
block|{
return|return
name|this
operator|.
name|spatialStrategy
return|;
block|}
block|}
end_class

end_unit

