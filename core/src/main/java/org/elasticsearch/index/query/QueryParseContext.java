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
name|analysis
operator|.
name|Analyzer
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
name|join
operator|.
name|BitDocIdSetFilter
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
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|ParseFieldMatcher
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
name|*
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
name|StringFieldMapper
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
name|query
operator|.
name|support
operator|.
name|NestedScope
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
name|fetch
operator|.
name|innerhits
operator|.
name|InnerHitsContext
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

begin_class
DECL|class|QueryParseContext
specifier|public
class|class
name|QueryParseContext
block|{
DECL|field|CACHE
specifier|private
specifier|static
specifier|final
name|ParseField
name|CACHE
init|=
operator|new
name|ParseField
argument_list|(
literal|"_cache"
argument_list|)
operator|.
name|withAllDeprecated
argument_list|(
literal|"Elasticsearch makes its own caching decisions"
argument_list|)
decl_stmt|;
DECL|field|CACHE_KEY
specifier|private
specifier|static
specifier|final
name|ParseField
name|CACHE_KEY
init|=
operator|new
name|ParseField
argument_list|(
literal|"_cache_key"
argument_list|)
operator|.
name|withAllDeprecated
argument_list|(
literal|"Filters are always used as cache keys"
argument_list|)
decl_stmt|;
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
DECL|field|indexVersionCreated
specifier|private
specifier|final
name|Version
name|indexVersionCreated
decl_stmt|;
DECL|field|indexQueryParser
specifier|private
specifier|final
name|IndexQueryParserService
name|indexQueryParser
decl_stmt|;
DECL|field|namedQueries
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|namedQueries
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
DECL|field|parseFieldMatcher
specifier|private
name|ParseFieldMatcher
name|parseFieldMatcher
decl_stmt|;
DECL|field|allowUnmappedFields
specifier|private
name|boolean
name|allowUnmappedFields
decl_stmt|;
DECL|field|mapUnmappedFieldAsString
specifier|private
name|boolean
name|mapUnmappedFieldAsString
decl_stmt|;
DECL|field|nestedScope
specifier|private
name|NestedScope
name|nestedScope
decl_stmt|;
DECL|field|isFilter
specifier|private
name|boolean
name|isFilter
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
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|indexVersionCreated
operator|=
name|Version
operator|.
name|indexCreated
argument_list|(
name|indexQueryParser
operator|.
name|indexSettings
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexQueryParser
operator|=
name|indexQueryParser
expr_stmt|;
block|}
DECL|method|parseFieldMatcher
specifier|public
name|void
name|parseFieldMatcher
parameter_list|(
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
block|{
name|this
operator|.
name|parseFieldMatcher
operator|=
name|parseFieldMatcher
expr_stmt|;
block|}
DECL|method|parseFieldMatcher
specifier|public
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|()
block|{
return|return
name|parseFieldMatcher
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
name|parseFieldMatcher
operator|=
name|ParseFieldMatcher
operator|.
name|EMPTY
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
name|namedQueries
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|nestedScope
operator|=
operator|new
name|NestedScope
argument_list|()
expr_stmt|;
name|this
operator|.
name|isFilter
operator|=
literal|false
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
DECL|method|indexQueryParserService
specifier|public
name|IndexQueryParserService
name|indexQueryParserService
parameter_list|()
block|{
return|return
name|indexQueryParser
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
DECL|method|bitsetFilter
specifier|public
name|BitDocIdSetFilter
name|bitsetFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|indexQueryParser
operator|.
name|bitsetFilterCache
operator|.
name|getBitDocIdSetFilter
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
name|MappedFieldType
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
name|namedQueries
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|copyNamedFilters
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|copyNamedFilters
parameter_list|()
block|{
return|return
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|namedQueries
argument_list|)
return|;
block|}
DECL|method|combineNamedFilters
specifier|public
name|void
name|combineNamedFilters
parameter_list|(
name|QueryParseContext
name|context
parameter_list|)
block|{
name|namedQueries
operator|.
name|putAll
argument_list|(
name|context
operator|.
name|namedQueries
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return whether we are currently parsing a filter or a query.      */
DECL|method|isFilter
specifier|public
name|boolean
name|isFilter
parameter_list|()
block|{
return|return
name|isFilter
return|;
block|}
DECL|method|addInnerHits
specifier|public
name|void
name|addInnerHits
parameter_list|(
name|String
name|name
parameter_list|,
name|InnerHitsContext
operator|.
name|BaseInnerHits
name|context
parameter_list|)
block|{
name|SearchContext
name|sc
init|=
name|SearchContext
operator|.
name|current
argument_list|()
decl_stmt|;
if|if
condition|(
name|sc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|this
argument_list|,
literal|"inner_hits unsupported"
argument_list|)
throw|;
block|}
name|InnerHitsContext
name|innerHitsContext
decl_stmt|;
if|if
condition|(
name|sc
operator|.
name|innerHits
argument_list|()
operator|==
literal|null
condition|)
block|{
name|innerHitsContext
operator|=
operator|new
name|InnerHitsContext
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|InnerHitsContext
operator|.
name|BaseInnerHits
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|sc
operator|.
name|innerHits
argument_list|(
name|innerHitsContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|innerHitsContext
operator|=
name|sc
operator|.
name|innerHits
argument_list|()
expr_stmt|;
block|}
name|innerHitsContext
operator|.
name|addInnerHitDefinition
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return a new QueryBuilder based on the current state of the parser      * @throws IOException      */
DECL|method|parseInnerQueryBuilder
specifier|public
name|QueryBuilder
name|parseInnerQueryBuilder
parameter_list|()
throws|throws
name|IOException
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
name|this
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
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
comment|// empty query
return|return
literal|null
return|;
block|}
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
name|this
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
name|this
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
name|this
argument_list|,
literal|"No query registered for ["
operator|+
name|queryName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|QueryBuilder
name|result
init|=
name|queryParser
operator|.
name|fromXContent
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
comment|/**      * @deprecated replaced by calls to parseInnerQueryBuilder() for the resulting queries      */
annotation|@
name|Nullable
annotation|@
name|Deprecated
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
name|QueryBuilder
name|builder
init|=
name|parseInnerQueryBuilder
argument_list|()
decl_stmt|;
name|Query
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|builder
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|builder
operator|.
name|toQuery
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * @deprecated replaced by calls to parseInnerFilterToQueryBuilder() for the resulting queries      */
annotation|@
name|Nullable
annotation|@
name|Deprecated
DECL|method|parseInnerFilter
specifier|public
name|Query
name|parseInnerFilter
parameter_list|()
throws|throws
name|QueryParsingException
throws|,
name|IOException
block|{
name|QueryBuilder
name|builder
init|=
name|parseInnerFilterToQueryBuilder
argument_list|()
decl_stmt|;
name|Query
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|builder
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|builder
operator|.
name|toQuery
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * @return      * @throws QueryParsingException      * @throws IOException      */
annotation|@
name|Nullable
DECL|method|parseInnerFilterToQueryBuilder
specifier|public
name|QueryBuilder
name|parseInnerFilterToQueryBuilder
parameter_list|()
throws|throws
name|QueryParsingException
throws|,
name|IOException
block|{
specifier|final
name|boolean
name|originalIsFilter
init|=
name|isFilter
decl_stmt|;
try|try
block|{
name|isFilter
operator|=
literal|true
expr_stmt|;
return|return
name|parseInnerQueryBuilder
argument_list|()
return|;
block|}
finally|finally
block|{
name|isFilter
operator|=
name|originalIsFilter
expr_stmt|;
block|}
block|}
DECL|method|parseInnerFilterToQueryBuilder
specifier|public
name|QueryBuilder
name|parseInnerFilterToQueryBuilder
parameter_list|(
name|String
name|queryName
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
specifier|final
name|boolean
name|originalIsFilter
init|=
name|isFilter
decl_stmt|;
try|try
block|{
name|isFilter
operator|=
literal|true
expr_stmt|;
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
name|this
argument_list|,
literal|"No query registered for ["
operator|+
name|queryName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|queryParser
operator|.
name|fromXContent
argument_list|(
name|this
argument_list|)
return|;
block|}
finally|finally
block|{
name|isFilter
operator|=
name|originalIsFilter
expr_stmt|;
block|}
block|}
comment|/**      * @deprecated replaced by calls to parseInnerFilterToQueryBuilder(String queryName) for the resulting queries      */
annotation|@
name|Nullable
annotation|@
name|Deprecated
DECL|method|parseInnerFilter
specifier|public
name|Query
name|parseInnerFilter
parameter_list|(
name|String
name|queryName
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|QueryBuilder
name|builder
init|=
name|parseInnerFilterToQueryBuilder
argument_list|(
name|queryName
argument_list|)
decl_stmt|;
return|return
operator|(
name|builder
operator|!=
literal|null
operator|)
condition|?
name|builder
operator|.
name|toQuery
argument_list|(
name|this
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|simpleMatchToIndexNames
specifier|public
name|Collection
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
DECL|method|fieldMapper
specifier|public
name|MappedFieldType
name|fieldMapper
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
name|smartNameFieldType
argument_list|(
name|name
argument_list|,
name|getTypes
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getObjectMapper
specifier|public
name|ObjectMapper
name|getObjectMapper
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
name|getObjectMapper
argument_list|(
name|name
argument_list|,
name|getTypes
argument_list|()
argument_list|)
return|;
block|}
comment|/** Gets the search analyzer for the given field, or the default if there is none present for the field      * TODO: remove this by moving defaults into mappers themselves      */
DECL|method|getSearchAnalyzer
specifier|public
name|Analyzer
name|getSearchAnalyzer
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|)
block|{
if|if
condition|(
name|fieldType
operator|.
name|searchAnalyzer
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|fieldType
operator|.
name|searchAnalyzer
argument_list|()
return|;
block|}
return|return
name|mapperService
argument_list|()
operator|.
name|searchAnalyzer
argument_list|()
return|;
block|}
comment|/** Gets the search quote nalyzer for the given field, or the default if there is none present for the field      * TODO: remove this by moving defaults into mappers themselves      */
DECL|method|getSearchQuoteAnalyzer
specifier|public
name|Analyzer
name|getSearchQuoteAnalyzer
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|)
block|{
if|if
condition|(
name|fieldType
operator|.
name|searchQuoteAnalyzer
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|fieldType
operator|.
name|searchQuoteAnalyzer
argument_list|()
return|;
block|}
return|return
name|mapperService
argument_list|()
operator|.
name|searchQuoteAnalyzer
argument_list|()
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
DECL|method|setMapUnmappedFieldAsString
specifier|public
name|void
name|setMapUnmappedFieldAsString
parameter_list|(
name|boolean
name|mapUnmappedFieldAsString
parameter_list|)
block|{
name|this
operator|.
name|mapUnmappedFieldAsString
operator|=
name|mapUnmappedFieldAsString
expr_stmt|;
block|}
DECL|method|failIfFieldMappingNotFound
specifier|private
name|MappedFieldType
name|failIfFieldMappingNotFound
parameter_list|(
name|String
name|name
parameter_list|,
name|MappedFieldType
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
elseif|else
if|if
condition|(
name|mapUnmappedFieldAsString
condition|)
block|{
name|StringFieldMapper
operator|.
name|Builder
name|builder
init|=
name|MapperBuilders
operator|.
name|stringField
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// it would be better to pass the real index settings, but they are not easily accessible from here...
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|indexQueryParser
operator|.
name|getIndexCreatedVersion
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|(
operator|new
name|Mapper
operator|.
name|BuilderContext
argument_list|(
name|settings
argument_list|,
operator|new
name|ContentPath
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|fieldType
argument_list|()
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
name|V_1_4_0_Beta1
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|this
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
DECL|method|nestedScope
specifier|public
name|NestedScope
name|nestedScope
parameter_list|()
block|{
return|return
name|nestedScope
return|;
block|}
comment|/**      * Return whether the setting is deprecated.      */
DECL|method|isDeprecatedSetting
specifier|public
name|boolean
name|isDeprecatedSetting
parameter_list|(
name|String
name|setting
parameter_list|)
block|{
return|return
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|setting
argument_list|,
name|CACHE
argument_list|)
operator|||
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|setting
argument_list|,
name|CACHE_KEY
argument_list|)
return|;
block|}
DECL|method|indexVersionCreated
specifier|public
name|Version
name|indexVersionCreated
parameter_list|()
block|{
return|return
name|indexVersionCreated
return|;
block|}
block|}
end_class

end_unit

