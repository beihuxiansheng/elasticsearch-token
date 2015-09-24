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
name|ParseField
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
name|ParsingException
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
name|bytes
operator|.
name|BytesReference
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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|XContentParser
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
DECL|class|GeoShapeQueryParser
specifier|public
class|class
name|GeoShapeQueryParser
implements|implements
name|QueryParser
argument_list|<
name|GeoShapeQueryBuilder
argument_list|>
block|{
DECL|field|SHAPE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|SHAPE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"shape"
argument_list|)
decl_stmt|;
DECL|field|STRATEGY_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|STRATEGY_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"strategy"
argument_list|)
decl_stmt|;
DECL|field|RELATION_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|RELATION_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"relation"
argument_list|)
decl_stmt|;
DECL|field|INDEXED_SHAPE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|INDEXED_SHAPE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"indexed_shape"
argument_list|)
decl_stmt|;
DECL|field|SHAPE_ID_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|SHAPE_ID_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
DECL|field|SHAPE_TYPE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|SHAPE_TYPE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
DECL|field|SHAPE_INDEX_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|SHAPE_INDEX_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
DECL|field|SHAPE_PATH_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|SHAPE_PATH_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|names
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|GeoShapeQueryBuilder
operator|.
name|NAME
block|,
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|GeoShapeQueryBuilder
operator|.
name|NAME
argument_list|)
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|GeoShapeQueryBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|ShapeRelation
name|shapeRelation
init|=
literal|null
decl_stmt|;
name|SpatialStrategy
name|strategy
init|=
literal|null
decl_stmt|;
name|BytesReference
name|shape
init|=
literal|null
decl_stmt|;
name|String
name|id
init|=
literal|null
decl_stmt|;
name|String
name|type
init|=
literal|null
decl_stmt|;
name|String
name|index
init|=
literal|null
decl_stmt|;
name|String
name|shapePath
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|float
name|boost
init|=
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|fieldName
operator|=
name|currentFieldName
expr_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|SHAPE_FIELD
argument_list|)
condition|)
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|parser
operator|.
name|contentType
argument_list|()
argument_list|)
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|shape
operator|=
name|builder
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|STRATEGY_FIELD
argument_list|)
condition|)
block|{
name|String
name|strategyName
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
name|strategy
operator|=
name|SpatialStrategy
operator|.
name|fromString
argument_list|(
name|strategyName
argument_list|)
expr_stmt|;
if|if
condition|(
name|strategy
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"Unknown strategy ["
operator|+
name|strategyName
operator|+
literal|" ]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|RELATION_FIELD
argument_list|)
condition|)
block|{
name|shapeRelation
operator|=
name|ShapeRelation
operator|.
name|getRelationByName
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shapeRelation
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"Unknown shape operation ["
operator|+
name|parser
operator|.
name|text
argument_list|()
operator|+
literal|" ]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|INDEXED_SHAPE_FIELD
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|SHAPE_ID_FIELD
argument_list|)
condition|)
block|{
name|id
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|SHAPE_TYPE_FIELD
argument_list|)
condition|)
block|{
name|type
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|SHAPE_INDEX_FIELD
argument_list|)
condition|)
block|{
name|index
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|SHAPE_PATH_FIELD
argument_list|)
condition|)
block|{
name|shapePath
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"[geo_shape] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|AbstractQueryBuilder
operator|.
name|BOOST_FIELD
argument_list|)
condition|)
block|{
name|boost
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|AbstractQueryBuilder
operator|.
name|NAME_FIELD
argument_list|)
condition|)
block|{
name|queryName
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"[geo_shape] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
name|GeoShapeQueryBuilder
name|builder
decl_stmt|;
if|if
condition|(
name|shape
operator|!=
literal|null
condition|)
block|{
name|builder
operator|=
operator|new
name|GeoShapeQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|shape
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|=
operator|new
name|GeoShapeQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|id
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|indexedShapeIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shapePath
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|indexedShapePath
argument_list|(
name|shapePath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shapeRelation
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|relation
argument_list|(
name|shapeRelation
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|strategy
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|strategy
argument_list|(
name|strategy
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|GeoShapeQueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|GeoShapeQueryBuilder
operator|.
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit

