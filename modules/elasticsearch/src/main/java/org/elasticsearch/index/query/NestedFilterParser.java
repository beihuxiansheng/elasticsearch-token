begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|DeletionAwareConstantScoreQuery
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
name|QueryWrapperFilter
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
name|cache
operator|.
name|filter
operator|.
name|support
operator|.
name|CacheKeyFilter
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
name|MapperService
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
name|object
operator|.
name|ObjectMapper
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
name|BlockJoinQuery
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
DECL|class|NestedFilterParser
specifier|public
class|class
name|NestedFilterParser
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
literal|"nested"
decl_stmt|;
DECL|method|NestedFilterParser
annotation|@
name|Inject
specifier|public
name|NestedFilterParser
parameter_list|()
block|{     }
DECL|method|names
annotation|@
name|Override
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
DECL|method|parse
annotation|@
name|Override
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
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|String
name|scope
init|=
literal|null
decl_stmt|;
name|String
name|path
init|=
literal|null
decl_stmt|;
name|boolean
name|cache
init|=
literal|false
decl_stmt|;
name|CacheKeyFilter
operator|.
name|Key
name|cacheKey
init|=
literal|null
decl_stmt|;
name|String
name|filterName
init|=
literal|null
decl_stmt|;
comment|// we need a late binding filter so we can inject a parent nested filter inner nested queries
name|NestedQueryParser
operator|.
name|LateBindingParentFilter
name|currentParentFilterContext
init|=
name|NestedQueryParser
operator|.
name|parentFilterContext
operator|.
name|get
argument_list|()
decl_stmt|;
name|NestedQueryParser
operator|.
name|LateBindingParentFilter
name|usAsParentFilter
init|=
operator|new
name|NestedQueryParser
operator|.
name|LateBindingParentFilter
argument_list|()
decl_stmt|;
name|NestedQueryParser
operator|.
name|parentFilterContext
operator|.
name|set
argument_list|(
name|usAsParentFilter
argument_list|)
expr_stmt|;
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
name|query
operator|=
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
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
name|filter
operator|=
name|parseContext
operator|.
name|parseInnerFilter
argument_list|()
expr_stmt|;
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
literal|"path"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|path
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
literal|"_scope"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|scope
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
name|cache
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
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
name|cacheKey
operator|=
operator|new
name|CacheKeyFilter
operator|.
name|Key
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|query
operator|==
literal|null
operator|&&
name|filter
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
literal|"[nested] requires either 'query' or 'filter' field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|path
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
literal|"[nested] requires 'path' field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|DeletionAwareConstantScoreQuery
argument_list|(
name|filter
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
name|MapperService
operator|.
name|SmartNameObjectMapper
name|mapper
init|=
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|smartNameObjectMapper
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapper
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
literal|"[nested] failed to find nested object under path ["
operator|+
name|path
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|ObjectMapper
name|objectMapper
init|=
name|mapper
operator|.
name|mapper
argument_list|()
decl_stmt|;
if|if
condition|(
name|objectMapper
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
literal|"[nested] failed to find nested object under path ["
operator|+
name|path
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|objectMapper
operator|.
name|nested
argument_list|()
operator|.
name|isNested
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
literal|"[nested] nested object under path ["
operator|+
name|path
operator|+
literal|"] is not of nested type"
argument_list|)
throw|;
block|}
name|Filter
name|childFilter
init|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|objectMapper
operator|.
name|nestedTypeFilter
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|usAsParentFilter
operator|.
name|filter
operator|=
name|childFilter
expr_stmt|;
comment|// wrap the child query to only work on the nested path type
name|query
operator|=
operator|new
name|FilteredQuery
argument_list|(
name|query
argument_list|,
name|childFilter
argument_list|)
expr_stmt|;
name|Filter
name|parentFilter
init|=
name|currentParentFilterContext
decl_stmt|;
if|if
condition|(
name|parentFilter
operator|==
literal|null
condition|)
block|{
name|parentFilter
operator|=
name|NonNestedDocsFilter
operator|.
name|INSTANCE
expr_stmt|;
if|if
condition|(
name|mapper
operator|.
name|hasDocMapper
argument_list|()
condition|)
block|{
comment|// filter based on the type...
name|parentFilter
operator|=
name|mapper
operator|.
name|docMapper
argument_list|()
operator|.
name|typeFilter
argument_list|()
expr_stmt|;
block|}
name|parentFilter
operator|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|parentFilter
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// restore the thread local one...
name|NestedQueryParser
operator|.
name|parentFilterContext
operator|.
name|set
argument_list|(
name|currentParentFilterContext
argument_list|)
expr_stmt|;
name|BlockJoinQuery
name|joinQuery
init|=
operator|new
name|BlockJoinQuery
argument_list|(
name|query
argument_list|,
name|parentFilter
argument_list|,
name|BlockJoinQuery
operator|.
name|ScoreMode
operator|.
name|None
argument_list|)
decl_stmt|;
if|if
condition|(
name|scope
operator|!=
literal|null
condition|)
block|{
name|SearchContext
operator|.
name|current
argument_list|()
operator|.
name|addNestedQuery
argument_list|(
name|scope
argument_list|,
name|joinQuery
argument_list|)
expr_stmt|;
block|}
name|Filter
name|joinFilter
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
name|joinQuery
argument_list|)
decl_stmt|;
if|if
condition|(
name|cache
condition|)
block|{
name|joinFilter
operator|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|joinFilter
argument_list|,
name|cacheKey
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
name|joinFilter
argument_list|)
expr_stmt|;
block|}
return|return
name|joinFilter
return|;
block|}
block|}
end_class

end_unit

