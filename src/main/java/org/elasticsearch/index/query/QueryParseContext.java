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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|queryparser
operator|.
name|classic
operator|.
name|MapperQueryParser
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParserSettings
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
name|similarities
operator|.
name|Similarity
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
name|lucene
operator|.
name|search
operator|.
name|NoCacheFilter
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
name|Queries
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
name|Index
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
name|analysis
operator|.
name|AnalysisService
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
name|cache
operator|.
name|query
operator|.
name|parser
operator|.
name|QueryParserCache
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
name|engine
operator|.
name|IndexEngine
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
name|fixedbitset
operator|.
name|FixedBitSetFilter
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
name|IndexFieldData
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
name|FieldMappers
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
name|similarity
operator|.
name|SimilarityService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
operator|.
name|SearchLookup
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
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|QueryParseContext
specifier|public
class|class
name|QueryParseContext
block|{
DECL|field|typesContext
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|String
index|[]
argument_list|>
name|typesContext
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|setTypes
specifier|public
specifier|static
name|void
name|setTypes
parameter_list|(
name|String
index|[]
name|types
parameter_list|)
block|{
name|typesContext
operator|.
name|set
argument_list|(
name|types
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypes
specifier|public
specifier|static
name|String
index|[]
name|getTypes
parameter_list|()
block|{
return|return
name|typesContext
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|setTypesWithPrevious
specifier|public
specifier|static
name|String
index|[]
name|setTypesWithPrevious
parameter_list|(
name|String
index|[]
name|types
parameter_list|)
block|{
name|String
index|[]
name|old
init|=
name|typesContext
operator|.
name|get
argument_list|()
decl_stmt|;
name|setTypes
argument_list|(
name|types
argument_list|)
expr_stmt|;
return|return
name|old
return|;
block|}
DECL|method|removeTypes
specifier|public
specifier|static
name|void
name|removeTypes
parameter_list|()
block|{
name|typesContext
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
DECL|field|index
specifier|private
specifier|final
name|Index
name|index
decl_stmt|;
DECL|field|propagateNoCache
specifier|private
name|boolean
name|propagateNoCache
init|=
literal|false
decl_stmt|;
DECL|field|indexQueryParser
specifier|final
name|IndexQueryParserService
name|indexQueryParser
decl_stmt|;
DECL|field|namedFilters
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Filter
argument_list|>
name|namedFilters
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|queryParser
specifier|private
specifier|final
name|MapperQueryParser
name|queryParser
init|=
operator|new
name|MapperQueryParser
argument_list|(
name|this
argument_list|)
decl_stmt|;
DECL|field|parser
specifier|private
name|XContentParser
name|parser
decl_stmt|;
DECL|field|parseFlags
specifier|private
name|EnumSet
argument_list|<
name|ParseField
operator|.
name|Flag
argument_list|>
name|parseFlags
init|=
name|ParseField
operator|.
name|EMPTY_FLAGS
decl_stmt|;
DECL|field|disableFilterCaching
specifier|private
specifier|final
name|boolean
name|disableFilterCaching
decl_stmt|;
DECL|field|allowUnmappedFields
specifier|private
name|boolean
name|allowUnmappedFields
decl_stmt|;
DECL|method|QueryParseContext
specifier|public
name|QueryParseContext
parameter_list|(
name|Index
name|index
parameter_list|,
name|IndexQueryParserService
name|indexQueryParser
parameter_list|)
block|{
name|this
argument_list|(
name|index
argument_list|,
name|indexQueryParser
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|QueryParseContext
specifier|public
name|QueryParseContext
parameter_list|(
name|Index
name|index
parameter_list|,
name|IndexQueryParserService
name|indexQueryParser
parameter_list|,
name|boolean
name|disableFilterCaching
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|indexQueryParser
operator|=
name|indexQueryParser
expr_stmt|;
name|this
operator|.
name|propagateNoCache
operator|=
name|disableFilterCaching
expr_stmt|;
name|this
operator|.
name|disableFilterCaching
operator|=
name|disableFilterCaching
expr_stmt|;
block|}
DECL|method|parseFlags
specifier|public
name|void
name|parseFlags
parameter_list|(
name|EnumSet
argument_list|<
name|ParseField
operator|.
name|Flag
argument_list|>
name|parseFlags
parameter_list|)
block|{
name|this
operator|.
name|parseFlags
operator|=
name|parseFlags
operator|==
literal|null
condition|?
name|ParseField
operator|.
name|EMPTY_FLAGS
else|:
name|parseFlags
expr_stmt|;
block|}
DECL|method|parseFlags
specifier|public
name|EnumSet
argument_list|<
name|ParseField
operator|.
name|Flag
argument_list|>
name|parseFlags
parameter_list|()
block|{
return|return
name|parseFlags
return|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|XContentParser
name|jp
parameter_list|)
block|{
name|allowUnmappedFields
operator|=
name|indexQueryParser
operator|.
name|defaultAllowUnmappedFields
argument_list|()
expr_stmt|;
name|this
operator|.
name|parseFlags
operator|=
name|ParseField
operator|.
name|EMPTY_FLAGS
expr_stmt|;
name|this
operator|.
name|lookup
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|jp
expr_stmt|;
name|this
operator|.
name|namedFilters
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|index
specifier|public
name|Index
name|index
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
return|;
block|}
DECL|method|parser
specifier|public
name|void
name|parser
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
block|{
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
DECL|method|parser
specifier|public
name|XContentParser
name|parser
parameter_list|()
block|{
return|return
name|parser
return|;
block|}
DECL|method|analysisService
specifier|public
name|AnalysisService
name|analysisService
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|analysisService
return|;
block|}
DECL|method|scriptService
specifier|public
name|ScriptService
name|scriptService
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|scriptService
return|;
block|}
DECL|method|mapperService
specifier|public
name|MapperService
name|mapperService
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|mapperService
return|;
block|}
DECL|method|indexEngine
specifier|public
name|IndexEngine
name|indexEngine
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|indexEngine
return|;
block|}
annotation|@
name|Nullable
DECL|method|similarityService
specifier|public
name|SimilarityService
name|similarityService
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|similarityService
return|;
block|}
DECL|method|searchSimilarity
specifier|public
name|Similarity
name|searchSimilarity
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|similarityService
operator|!=
literal|null
condition|?
name|indexQueryParser
operator|.
name|similarityService
operator|.
name|similarity
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|queryParserCache
specifier|public
name|QueryParserCache
name|queryParserCache
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|indexCache
operator|.
name|queryParserCache
argument_list|()
return|;
block|}
DECL|method|defaultField
specifier|public
name|String
name|defaultField
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|defaultField
argument_list|()
return|;
block|}
DECL|method|queryStringLenient
specifier|public
name|boolean
name|queryStringLenient
parameter_list|()
block|{
return|return
name|indexQueryParser
operator|.
name|queryStringLenient
argument_list|()
return|;
block|}
DECL|method|queryParser
specifier|public
name|MapperQueryParser
name|queryParser
parameter_list|(
name|QueryParserSettings
name|settings
parameter_list|)
block|{
name|queryParser
operator|.
name|reset
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|queryParser
return|;
block|}
DECL|method|fixedBitSetFilter
specifier|public
name|FixedBitSetFilter
name|fixedBitSetFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|indexQueryParser
operator|.
name|fixedBitSetFilterCache
operator|.
name|getFixedBitSetFilter
argument_list|(
name|filter
argument_list|)
return|;
block|}
DECL|method|cacheFilter
specifier|public
name|Filter
name|cacheFilter
parameter_list|(
name|Filter
name|filter
parameter_list|,
annotation|@
name|Nullable
name|CacheKeyFilter
operator|.
name|Key
name|cacheKey
parameter_list|)
block|{
if|if
condition|(
name|filter
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
name|this
operator|.
name|disableFilterCaching
operator|||
name|this
operator|.
name|propagateNoCache
operator|||
name|filter
operator|instanceof
name|NoCacheFilter
condition|)
block|{
return|return
name|filter
return|;
block|}
if|if
condition|(
name|cacheKey
operator|!=
literal|null
condition|)
block|{
name|filter
operator|=
operator|new
name|CacheKeyFilter
operator|.
name|Wrapper
argument_list|(
name|filter
argument_list|,
name|cacheKey
argument_list|)
expr_stmt|;
block|}
return|return
name|indexQueryParser
operator|.
name|indexCache
operator|.
name|filter
argument_list|()
operator|.
name|cache
argument_list|(
name|filter
argument_list|)
return|;
block|}
DECL|method|getForField
specifier|public
parameter_list|<
name|IFD
extends|extends
name|IndexFieldData
argument_list|<
name|?
argument_list|>
parameter_list|>
name|IFD
name|getForField
parameter_list|(
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|mapper
parameter_list|)
block|{
return|return
name|indexQueryParser
operator|.
name|fieldDataService
operator|.
name|getForField
argument_list|(
name|mapper
argument_list|)
return|;
block|}
DECL|method|addNamedFilter
specifier|public
name|void
name|addNamedFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
name|namedFilters
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
DECL|method|addNamedQuery
specifier|public
name|void
name|addNamedQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Query
name|query
parameter_list|)
block|{
name|namedFilters
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|Queries
operator|.
name|wrap
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|copyNamedFilters
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Filter
argument_list|>
name|copyNamedFilters
parameter_list|()
block|{
if|if
condition|(
name|namedFilters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
return|return
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|namedFilters
argument_list|)
return|;
block|}
annotation|@
name|Nullable
DECL|method|parseInnerQuery
specifier|public
name|Query
name|parseInnerQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
comment|// move to START object
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"[_na] query malformed, must start with start_object"
argument_list|)
throw|;
block|}
block|}
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"[_na] query malformed, no field after start_object"
argument_list|)
throw|;
block|}
name|String
name|queryName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
comment|// move to the next START_OBJECT
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|&&
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"[_na] query malformed, no field after start_object"
argument_list|)
throw|;
block|}
name|QueryParser
name|queryParser
init|=
name|indexQueryParser
operator|.
name|queryParser
argument_list|(
name|queryName
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryParser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"No query registered for ["
operator|+
name|queryName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|Query
name|result
init|=
name|queryParser
operator|.
name|parse
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
operator|||
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
comment|// if we are at END_OBJECT, move to the next one...
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Nullable
DECL|method|parseInnerFilter
specifier|public
name|Filter
name|parseInnerFilter
parameter_list|()
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
comment|// move to START object
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"[_na] filter malformed, must start with start_object"
argument_list|)
throw|;
block|}
block|}
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
comment|// empty filter
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
operator|||
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NULL
condition|)
block|{
return|return
literal|null
return|;
block|}
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"[_na] filter malformed, no field after start_object"
argument_list|)
throw|;
block|}
name|String
name|filterName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
comment|// move to the next START_OBJECT or START_ARRAY
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|&&
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"[_na] filter malformed, no field after start_object"
argument_list|)
throw|;
block|}
name|FilterParser
name|filterParser
init|=
name|indexQueryParser
operator|.
name|filterParser
argument_list|(
name|filterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterParser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"No filter registered for ["
operator|+
name|filterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|Filter
name|result
init|=
name|executeFilterParser
argument_list|(
name|filterParser
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
operator|||
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
comment|// if we are at END_OBJECT, move to the next one...
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|parseInnerFilter
specifier|public
name|Filter
name|parseInnerFilter
parameter_list|(
name|String
name|filterName
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|FilterParser
name|filterParser
init|=
name|indexQueryParser
operator|.
name|filterParser
argument_list|(
name|filterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterParser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"No filter registered for ["
operator|+
name|filterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|executeFilterParser
argument_list|(
name|filterParser
argument_list|)
return|;
block|}
DECL|method|executeFilterParser
specifier|private
name|Filter
name|executeFilterParser
parameter_list|(
name|FilterParser
name|filterParser
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|boolean
name|propagateNoCache
init|=
name|this
operator|.
name|propagateNoCache
decl_stmt|;
comment|// first safe the state that we need to restore
name|this
operator|.
name|propagateNoCache
operator|=
literal|false
expr_stmt|;
comment|// parse the subfilter with caching, that's fine
name|Filter
name|result
init|=
name|filterParser
operator|.
name|parse
argument_list|(
name|this
argument_list|)
decl_stmt|;
comment|// now make sure we set propagateNoCache to true if it is true already or if the result is
comment|// an instance of NoCacheFilter or if we used to be true! all filters above will
comment|// be not cached ie. wrappers of this filter!
name|this
operator|.
name|propagateNoCache
operator||=
operator|(
name|result
operator|instanceof
name|NoCacheFilter
operator|)
operator|||
name|propagateNoCache
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|fieldMapper
specifier|public
name|FieldMapper
name|fieldMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|FieldMappers
name|fieldMappers
init|=
name|indexQueryParser
operator|.
name|mapperService
operator|.
name|smartNameFieldMappers
argument_list|(
name|name
argument_list|,
name|getTypes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMappers
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|fieldMappers
operator|.
name|mapper
argument_list|()
return|;
block|}
DECL|method|indexName
specifier|public
name|String
name|indexName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|FieldMapper
name|smartMapper
init|=
name|fieldMapper
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|smartMapper
operator|==
literal|null
condition|)
block|{
return|return
name|name
return|;
block|}
return|return
name|smartMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
return|;
block|}
DECL|method|simpleMatchToIndexNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|simpleMatchToIndexNames
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
return|return
name|indexQueryParser
operator|.
name|mapperService
operator|.
name|simpleMatchToIndexNames
argument_list|(
name|pattern
argument_list|,
name|getTypes
argument_list|()
argument_list|)
return|;
block|}
DECL|method|smartFieldMappers
specifier|public
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartFieldMappers
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|failIfFieldMappingNotFound
argument_list|(
name|name
argument_list|,
name|indexQueryParser
operator|.
name|mapperService
operator|.
name|smartName
argument_list|(
name|name
argument_list|,
name|getTypes
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|smartNameFieldMapper
specifier|public
name|FieldMapper
name|smartNameFieldMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|failIfFieldMappingNotFound
argument_list|(
name|name
argument_list|,
name|indexQueryParser
operator|.
name|mapperService
operator|.
name|smartNameFieldMapper
argument_list|(
name|name
argument_list|,
name|getTypes
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|smartObjectMapper
specifier|public
name|MapperService
operator|.
name|SmartNameObjectMapper
name|smartObjectMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|indexQueryParser
operator|.
name|mapperService
operator|.
name|smartNameObjectMapper
argument_list|(
name|name
argument_list|,
name|getTypes
argument_list|()
argument_list|)
return|;
block|}
DECL|method|setAllowUnmappedFields
specifier|public
name|void
name|setAllowUnmappedFields
parameter_list|(
name|boolean
name|allowUnmappedFields
parameter_list|)
block|{
name|this
operator|.
name|allowUnmappedFields
operator|=
name|allowUnmappedFields
expr_stmt|;
block|}
DECL|method|failIfFieldMappingNotFound
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|failIfFieldMappingNotFound
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|fieldMapping
parameter_list|)
block|{
if|if
condition|(
name|allowUnmappedFields
condition|)
block|{
return|return
name|fieldMapping
return|;
block|}
else|else
block|{
name|Version
name|indexCreatedVersion
init|=
name|indexQueryParser
operator|.
name|getIndexCreatedVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldMapping
operator|==
literal|null
operator|&&
name|indexCreatedVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_4_0
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"Strict field resolution and no field mapping can be found for the field with name ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|fieldMapping
return|;
block|}
block|}
block|}
comment|/**      * Returns the narrowed down explicit types, or, if not set, all types.      */
DECL|method|queryTypes
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|queryTypes
parameter_list|()
block|{
name|String
index|[]
name|types
init|=
name|getTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|types
operator|==
literal|null
operator|||
name|types
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|mapperService
argument_list|()
operator|.
name|types
argument_list|()
return|;
block|}
if|if
condition|(
name|types
operator|.
name|length
operator|==
literal|1
operator|&&
name|types
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"_all"
argument_list|)
condition|)
block|{
return|return
name|mapperService
argument_list|()
operator|.
name|types
argument_list|()
return|;
block|}
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|types
argument_list|)
return|;
block|}
DECL|field|lookup
specifier|private
name|SearchLookup
name|lookup
init|=
literal|null
decl_stmt|;
DECL|method|lookup
specifier|public
name|SearchLookup
name|lookup
parameter_list|()
block|{
name|SearchContext
name|current
init|=
name|SearchContext
operator|.
name|current
argument_list|()
decl_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
return|return
name|current
operator|.
name|lookup
argument_list|()
return|;
block|}
if|if
condition|(
name|lookup
operator|==
literal|null
condition|)
block|{
name|lookup
operator|=
operator|new
name|SearchLookup
argument_list|(
name|mapperService
argument_list|()
argument_list|,
name|indexQueryParser
operator|.
name|fieldDataService
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|lookup
return|;
block|}
DECL|method|nowInMillis
specifier|public
name|long
name|nowInMillis
parameter_list|()
block|{
name|SearchContext
name|current
init|=
name|SearchContext
operator|.
name|current
argument_list|()
decl_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
return|return
name|current
operator|.
name|nowInMillis
argument_list|()
return|;
block|}
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
block|}
end_class

end_unit

