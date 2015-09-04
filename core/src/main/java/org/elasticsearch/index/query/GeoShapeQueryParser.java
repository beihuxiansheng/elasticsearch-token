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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|*
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
name|query
operator|.
name|SpatialArgs
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
name|query
operator|.
name|SpatialOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|get
operator|.
name|GetRequest
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
name|Nullable
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
name|inject
operator|.
name|Inject
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
name|geo
operator|.
name|GeoShapeFieldMapper
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
name|search
operator|.
name|shape
operator|.
name|ShapeFetchService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
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
DECL|field|fetchService
specifier|private
name|ShapeFetchService
name|fetchService
decl_stmt|;
DECL|class|DEFAULTS
specifier|public
specifier|static
class|class
name|DEFAULTS
block|{
DECL|field|INDEX_NAME
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_NAME
init|=
literal|"shapes"
decl_stmt|;
DECL|field|SHAPE_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SHAPE_FIELD_NAME
init|=
literal|"shape"
decl_stmt|;
block|}
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
name|NAME
block|,
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|NAME
argument_list|)
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
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
name|ShapeRelation
operator|.
name|INTERSECTS
decl_stmt|;
name|String
name|strategyName
init|=
literal|null
decl_stmt|;
name|ShapeBuilder
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
name|DEFAULTS
operator|.
name|INDEX_NAME
decl_stmt|;
name|String
name|shapePath
init|=
name|DEFAULTS
operator|.
name|SHAPE_FIELD_NAME
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
literal|1f
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
literal|"shape"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|shape
operator|=
name|ShapeBuilder
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"strategy"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|strategyName
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
literal|"relation"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
name|QueryParsingException
argument_list|(
name|parseContext
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
literal|"indexed_shape"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"indexedShape"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"id"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"type"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"index"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"path"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"ID for indexed shape not provided"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"Type for indexed shape not provided"
argument_list|)
throw|;
block|}
name|GetRequest
name|getRequest
init|=
operator|new
name|GetRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|getRequest
operator|.
name|copyContextAndHeadersFrom
argument_list|(
name|SearchContext
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
name|shape
operator|=
name|fetchService
operator|.
name|fetch
argument_list|(
name|getRequest
argument_list|,
name|shapePath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
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
literal|"boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
name|QueryParsingException
argument_list|(
name|parseContext
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
if|if
condition|(
name|shape
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"No Shape defined"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|shapeRelation
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"No Shape Relation defined"
argument_list|)
throw|;
block|}
name|MappedFieldType
name|fieldType
init|=
name|parseContext
operator|.
name|fieldMapper
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"Failed to find geo_shape field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|// TODO: This isn't the nicest way to check this
if|if
condition|(
operator|!
operator|(
name|fieldType
operator|instanceof
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
operator|)
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"Field ["
operator|+
name|fieldName
operator|+
literal|"] is not a geo_shape"
argument_list|)
throw|;
block|}
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
name|shapeFieldType
init|=
operator|(
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
operator|)
name|fieldType
decl_stmt|;
name|PrefixTreeStrategy
name|strategy
init|=
name|shapeFieldType
operator|.
name|defaultStrategy
argument_list|()
decl_stmt|;
if|if
condition|(
name|strategyName
operator|!=
literal|null
condition|)
block|{
name|strategy
operator|=
name|shapeFieldType
operator|.
name|resolveStrategy
argument_list|(
name|strategyName
argument_list|)
expr_stmt|;
block|}
name|Query
name|query
decl_stmt|;
if|if
condition|(
name|strategy
operator|instanceof
name|RecursivePrefixTreeStrategy
operator|&&
name|shapeRelation
operator|==
name|ShapeRelation
operator|.
name|DISJOINT
condition|)
block|{
comment|// this strategy doesn't support disjoint anymore: but it did before, including creating lucene fieldcache (!)
comment|// in this case, execute disjoint as exists&& !intersects
name|BooleanQuery
operator|.
name|Builder
name|bool
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|Query
name|exists
init|=
name|ExistsQueryParser
operator|.
name|newFilter
argument_list|(
name|parseContext
argument_list|,
name|fieldName
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Filter
name|intersects
init|=
name|strategy
operator|.
name|makeFilter
argument_list|(
name|getArgs
argument_list|(
name|shape
argument_list|,
name|ShapeRelation
operator|.
name|INTERSECTS
argument_list|)
argument_list|)
decl_stmt|;
name|bool
operator|.
name|add
argument_list|(
name|exists
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bool
operator|.
name|add
argument_list|(
name|intersects
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|bool
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
name|strategy
operator|.
name|makeQuery
argument_list|(
name|getArgs
argument_list|(
name|shape
argument_list|,
name|shapeRelation
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|parseContext
operator|.
name|addNamedQuery
argument_list|(
name|queryName
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Inject
argument_list|(
name|optional
operator|=
literal|true
argument_list|)
DECL|method|setFetchService
specifier|public
name|void
name|setFetchService
parameter_list|(
annotation|@
name|Nullable
name|ShapeFetchService
name|fetchService
parameter_list|)
block|{
name|this
operator|.
name|fetchService
operator|=
name|fetchService
expr_stmt|;
block|}
DECL|method|getArgs
specifier|public
specifier|static
name|SpatialArgs
name|getArgs
parameter_list|(
name|ShapeBuilder
name|shape
parameter_list|,
name|ShapeRelation
name|relation
parameter_list|)
block|{
switch|switch
condition|(
name|relation
condition|)
block|{
case|case
name|DISJOINT
case|:
return|return
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|IsDisjointTo
argument_list|,
name|shape
operator|.
name|build
argument_list|()
argument_list|)
return|;
case|case
name|INTERSECTS
case|:
return|return
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|shape
operator|.
name|build
argument_list|()
argument_list|)
return|;
case|case
name|WITHIN
case|:
return|return
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|IsWithin
argument_list|,
name|shape
operator|.
name|build
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|""
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

