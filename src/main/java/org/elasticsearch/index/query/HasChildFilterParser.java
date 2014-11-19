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
name|Filter
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
name|search
operator|.
name|FilteredQuery
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|join
operator|.
name|BitDocIdSetFilter
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
name|fielddata
operator|.
name|plain
operator|.
name|ParentChildIndexFieldData
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
name|DocumentMapper
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
name|internal
operator|.
name|ParentFieldMapper
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
name|query
operator|.
name|support
operator|.
name|XContentStructure
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
name|child
operator|.
name|ChildrenConstantScoreQuery
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
name|child
operator|.
name|ChildrenQuery
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
name|child
operator|.
name|CustomQueryWrappingFilter
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
name|child
operator|.
name|ScoreType
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
name|nested
operator|.
name|NonNestedDocsFilter
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryParserUtils
operator|.
name|ensureNotDeleteByQuery
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|HasChildFilterParser
specifier|public
class|class
name|HasChildFilterParser
implements|implements
name|FilterParser
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"has_child"
decl_stmt|;
annotation|@
name|Inject
DECL|method|HasChildFilterParser
specifier|public
name|HasChildFilterParser
parameter_list|()
block|{     }
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
name|Filter
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
name|ensureNotDeleteByQuery
argument_list|(
name|NAME
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|boolean
name|queryFound
init|=
literal|false
decl_stmt|;
name|boolean
name|filterFound
init|=
literal|false
decl_stmt|;
name|String
name|childType
init|=
literal|null
decl_stmt|;
name|int
name|shortCircuitParentDocSet
init|=
literal|8192
decl_stmt|;
comment|// Tests show a cut of point between 8192 and 16384.
name|int
name|minChildren
init|=
literal|0
decl_stmt|;
name|int
name|maxChildren
init|=
literal|0
decl_stmt|;
name|String
name|filterName
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|XContentStructure
operator|.
name|InnerQuery
name|innerQuery
init|=
literal|null
decl_stmt|;
name|XContentStructure
operator|.
name|InnerFilter
name|innerFilter
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
comment|// Usually, the query would be parsed here, but the child
comment|// type may not have been extracted yet, so use the
comment|// XContentStructure.<type> facade to parse if available,
comment|// or delay parsing if not.
if|if
condition|(
literal|"query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|innerQuery
operator|=
operator|new
name|XContentStructure
operator|.
name|InnerQuery
argument_list|(
name|parseContext
argument_list|,
name|childType
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|String
index|[]
block|{
name|childType
block|}
argument_list|)
expr_stmt|;
name|queryFound
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"filter"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|innerFilter
operator|=
operator|new
name|XContentStructure
operator|.
name|InnerFilter
argument_list|(
name|parseContext
argument_list|,
name|childType
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|String
index|[]
block|{
name|childType
block|}
argument_list|)
expr_stmt|;
name|filterFound
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_child] filter does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
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
literal|"type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"child_type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"childType"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|childType
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
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|filterName
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
literal|"_cache"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// noop to be backwards compatible
block|}
elseif|else
if|if
condition|(
literal|"_cache_key"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"_cacheKey"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// noop to be backwards compatible
block|}
elseif|else
if|if
condition|(
literal|"short_circuit_cutoff"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|shortCircuitParentDocSet
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"min_children"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"minChildren"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|minChildren
operator|=
name|parser
operator|.
name|intValue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"max_children"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"maxChildren"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|maxChildren
operator|=
name|parser
operator|.
name|intValue
argument_list|(
literal|true
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
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_child] filter does not support ["
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
operator|!
name|queryFound
operator|&&
operator|!
name|filterFound
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_child] filter requires 'query' or 'filter' field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|childType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_child] filter requires 'type' field"
argument_list|)
throw|;
block|}
name|Query
name|query
decl_stmt|;
if|if
condition|(
name|queryFound
condition|)
block|{
name|query
operator|=
name|innerQuery
operator|.
name|asQuery
argument_list|(
name|childType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
name|innerFilter
operator|.
name|asFilter
argument_list|(
name|childType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|DocumentMapper
name|childDocMapper
init|=
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|childType
argument_list|)
decl_stmt|;
if|if
condition|(
name|childDocMapper
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"No mapping for for type ["
operator|+
name|childType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|ParentFieldMapper
name|parentFieldMapper
init|=
name|childDocMapper
operator|.
name|parentFieldMapper
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|parentFieldMapper
operator|.
name|active
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"Type ["
operator|+
name|childType
operator|+
literal|"] does not have parent mapping"
argument_list|)
throw|;
block|}
name|String
name|parentType
init|=
name|parentFieldMapper
operator|.
name|type
argument_list|()
decl_stmt|;
comment|// wrap the query with type query
name|query
operator|=
operator|new
name|FilteredQuery
argument_list|(
name|query
argument_list|,
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|childDocMapper
operator|.
name|typeFilter
argument_list|()
argument_list|,
literal|null
argument_list|,
name|parseContext
operator|.
name|autoFilterCachePolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|DocumentMapper
name|parentDocMapper
init|=
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|parentType
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentDocMapper
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_child]  Type ["
operator|+
name|childType
operator|+
literal|"] points to a non existent parent type ["
operator|+
name|parentType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxChildren
operator|>
literal|0
operator|&&
name|maxChildren
operator|<
name|minChildren
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_child] 'max_children' is less than 'min_children'"
argument_list|)
throw|;
block|}
name|BitDocIdSetFilter
name|nonNestedDocsFilter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|parentDocMapper
operator|.
name|hasNestedObjects
argument_list|()
condition|)
block|{
name|nonNestedDocsFilter
operator|=
name|parseContext
operator|.
name|bitsetFilter
argument_list|(
name|NonNestedDocsFilter
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
name|Filter
name|parentFilter
init|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|parentDocMapper
operator|.
name|typeFilter
argument_list|()
argument_list|,
literal|null
argument_list|,
name|parseContext
operator|.
name|autoFilterCachePolicy
argument_list|()
argument_list|)
decl_stmt|;
name|ParentChildIndexFieldData
name|parentChildIndexFieldData
init|=
name|parseContext
operator|.
name|getForField
argument_list|(
name|parentFieldMapper
argument_list|)
decl_stmt|;
name|Query
name|childrenQuery
decl_stmt|;
if|if
condition|(
name|minChildren
operator|>
literal|1
operator|||
name|maxChildren
operator|>
literal|0
condition|)
block|{
name|childrenQuery
operator|=
operator|new
name|ChildrenQuery
argument_list|(
name|parentChildIndexFieldData
argument_list|,
name|parentType
argument_list|,
name|childType
argument_list|,
name|parentFilter
argument_list|,
name|query
argument_list|,
name|ScoreType
operator|.
name|NONE
argument_list|,
name|minChildren
argument_list|,
name|maxChildren
argument_list|,
name|shortCircuitParentDocSet
argument_list|,
name|nonNestedDocsFilter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|childrenQuery
operator|=
operator|new
name|ChildrenConstantScoreQuery
argument_list|(
name|parentChildIndexFieldData
argument_list|,
name|query
argument_list|,
name|parentType
argument_list|,
name|childType
argument_list|,
name|parentFilter
argument_list|,
name|shortCircuitParentDocSet
argument_list|,
name|nonNestedDocsFilter
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filterName
operator|!=
literal|null
condition|)
block|{
name|parseContext
operator|.
name|addNamedFilter
argument_list|(
name|filterName
argument_list|,
operator|new
name|CustomQueryWrappingFilter
argument_list|(
name|childrenQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CustomQueryWrappingFilter
argument_list|(
name|childrenQuery
argument_list|)
return|;
block|}
block|}
end_class

end_unit

