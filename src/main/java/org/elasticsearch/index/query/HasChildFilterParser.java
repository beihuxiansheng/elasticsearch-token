begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|Query
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
name|lucene
operator|.
name|search
operator|.
name|XConstantScoreQuery
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
name|search
operator|.
name|XFilteredQuery
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
name|DeleteByQueryWrappingFilter
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
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
literal|null
decl_stmt|;
name|boolean
name|queryFound
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
comment|// TODO we need to set the type, but, `query` can come before `type`...
comment|// since we switch types, make sure we change the context
name|String
index|[]
name|origTypes
init|=
name|QueryParseContext
operator|.
name|setTypesWithPrevious
argument_list|(
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
decl_stmt|;
try|try
block|{
name|query
operator|=
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
expr_stmt|;
name|queryFound
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|QueryParseContext
operator|.
name|setTypes
argument_list|(
name|origTypes
argument_list|)
expr_stmt|;
block|}
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
comment|// TODO handle `filter` element before `type` element...
name|String
index|[]
name|origTypes
init|=
name|QueryParseContext
operator|.
name|setTypesWithPrevious
argument_list|(
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
decl_stmt|;
try|try
block|{
name|Filter
name|innerFilter
init|=
name|parseContext
operator|.
name|parseInnerFilter
argument_list|()
decl_stmt|;
name|query
operator|=
operator|new
name|XConstantScoreQuery
argument_list|(
name|innerFilter
argument_list|)
expr_stmt|;
name|queryFound
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|QueryParseContext
operator|.
name|setTypes
argument_list|(
name|origTypes
argument_list|)
expr_stmt|;
block|}
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
literal|"_scope"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
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
literal|"the [_scope] support in [has_child] filter has been removed, use a filter as a facet_filter in the relevant global facet"
argument_list|)
throw|;
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
literal|"[has_child] filter requires 'query' field"
argument_list|)
throw|;
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
if|if
condition|(
name|childDocMapper
operator|.
name|parentFieldMapper
argument_list|()
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
name|childDocMapper
operator|.
name|parentFieldMapper
argument_list|()
operator|.
name|type
argument_list|()
decl_stmt|;
comment|// wrap the query with type query
name|query
operator|=
operator|new
name|XFilteredQuery
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
argument_list|)
decl_stmt|;
name|Query
name|childrenConstantScoreQuery
init|=
operator|new
name|ChildrenConstantScoreQuery
argument_list|(
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
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterName
operator|!=
literal|null
condition|)
block|{
name|parseContext
operator|.
name|addNamedQuery
argument_list|(
name|filterName
argument_list|,
name|childrenConstantScoreQuery
argument_list|)
expr_stmt|;
block|}
name|boolean
name|deleteByQuery
init|=
literal|"delete_by_query"
operator|.
name|equals
argument_list|(
name|SearchContext
operator|.
name|current
argument_list|()
operator|.
name|source
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|deleteByQuery
condition|)
block|{
return|return
operator|new
name|DeleteByQueryWrappingFilter
argument_list|(
name|childrenConstantScoreQuery
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|CustomQueryWrappingFilter
argument_list|(
name|childrenConstantScoreQuery
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

