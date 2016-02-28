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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableMap
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|index
operator|.
name|IndexReader
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
name|BitSetProducer
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
name|client
operator|.
name|Client
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
name|ClusterState
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
name|IndexSettings
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
name|bitset
operator|.
name|BitsetFilterCache
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
name|fielddata
operator|.
name|IndexFieldDataService
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
name|ContentPath
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
name|core
operator|.
name|TextFieldMapper
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
name|percolator
operator|.
name|PercolatorQueryCache
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
name|InnerHitBuilder
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
name|indices
operator|.
name|query
operator|.
name|IndicesQueriesRegistry
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

begin_comment
comment|/**  * Context object used to create lucene queries on the shard level.  */
end_comment

begin_class
DECL|class|QueryShardContext
specifier|public
class|class
name|QueryShardContext
extends|extends
name|QueryRewriteContext
block|{
DECL|field|mapperService
specifier|private
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|similarityService
specifier|private
specifier|final
name|SimilarityService
name|similarityService
decl_stmt|;
DECL|field|bitsetFilterCache
specifier|private
specifier|final
name|BitsetFilterCache
name|bitsetFilterCache
decl_stmt|;
DECL|field|indexFieldDataService
specifier|private
specifier|final
name|IndexFieldDataService
name|indexFieldDataService
decl_stmt|;
DECL|field|indexSettings
specifier|private
specifier|final
name|IndexSettings
name|indexSettings
decl_stmt|;
DECL|field|types
specifier|private
name|String
index|[]
name|types
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|method|setTypes
specifier|public
name|void
name|setTypes
parameter_list|(
name|String
modifier|...
name|types
parameter_list|)
block|{
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
block|}
DECL|method|getTypes
specifier|public
name|String
index|[]
name|getTypes
parameter_list|()
block|{
return|return
name|types
return|;
block|}
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
operator|new
name|HashMap
argument_list|<>
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
DECL|field|indicesQueriesRegistry
specifier|private
specifier|final
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
decl_stmt|;
DECL|field|percolatorQueryCache
specifier|private
specifier|final
name|PercolatorQueryCache
name|percolatorQueryCache
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
name|boolean
name|isFilter
decl_stmt|;
comment|// pkg private for testing
DECL|method|QueryShardContext
specifier|public
name|QueryShardContext
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|BitsetFilterCache
name|bitsetFilterCache
parameter_list|,
name|IndexFieldDataService
name|indexFieldDataService
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|SimilarityService
name|similarityService
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
specifier|final
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
parameter_list|,
name|Client
name|client
parameter_list|,
name|PercolatorQueryCache
name|percolatorQueryCache
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|super
argument_list|(
name|indexSettings
argument_list|,
name|mapperService
argument_list|,
name|scriptService
argument_list|,
name|indicesQueriesRegistry
argument_list|,
name|client
argument_list|,
name|reader
argument_list|,
name|clusterState
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
name|this
operator|.
name|similarityService
operator|=
name|similarityService
expr_stmt|;
name|this
operator|.
name|mapperService
operator|=
name|mapperService
expr_stmt|;
name|this
operator|.
name|bitsetFilterCache
operator|=
name|bitsetFilterCache
expr_stmt|;
name|this
operator|.
name|indexFieldDataService
operator|=
name|indexFieldDataService
expr_stmt|;
name|this
operator|.
name|allowUnmappedFields
operator|=
name|indexSettings
operator|.
name|isDefaultAllowUnmappedFields
argument_list|()
expr_stmt|;
name|this
operator|.
name|indicesQueriesRegistry
operator|=
name|indicesQueriesRegistry
expr_stmt|;
name|this
operator|.
name|percolatorQueryCache
operator|=
name|percolatorQueryCache
expr_stmt|;
name|this
operator|.
name|nestedScope
operator|=
operator|new
name|NestedScope
argument_list|()
expr_stmt|;
block|}
DECL|method|QueryShardContext
specifier|public
name|QueryShardContext
parameter_list|(
name|QueryShardContext
name|source
parameter_list|)
block|{
name|this
argument_list|(
name|source
operator|.
name|indexSettings
argument_list|,
name|source
operator|.
name|bitsetFilterCache
argument_list|,
name|source
operator|.
name|indexFieldDataService
argument_list|,
name|source
operator|.
name|mapperService
argument_list|,
name|source
operator|.
name|similarityService
argument_list|,
name|source
operator|.
name|scriptService
argument_list|,
name|source
operator|.
name|indicesQueriesRegistry
argument_list|,
name|source
operator|.
name|client
argument_list|,
name|source
operator|.
name|percolatorQueryCache
argument_list|,
name|source
operator|.
name|reader
argument_list|,
name|source
operator|.
name|clusterState
argument_list|)
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|source
operator|.
name|getTypes
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|allowUnmappedFields
operator|=
name|indexSettings
operator|.
name|isDefaultAllowUnmappedFields
argument_list|()
expr_stmt|;
name|this
operator|.
name|lookup
operator|=
literal|null
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
DECL|method|getAnalysisService
specifier|public
name|AnalysisService
name|getAnalysisService
parameter_list|()
block|{
return|return
name|mapperService
operator|.
name|analysisService
argument_list|()
return|;
block|}
DECL|method|getPercolatorQueryCache
specifier|public
name|PercolatorQueryCache
name|getPercolatorQueryCache
parameter_list|()
block|{
return|return
name|percolatorQueryCache
return|;
block|}
DECL|method|getSearchSimilarity
specifier|public
name|Similarity
name|getSearchSimilarity
parameter_list|()
block|{
return|return
name|similarityService
operator|!=
literal|null
condition|?
name|similarityService
operator|.
name|similarity
argument_list|(
name|mapperService
argument_list|)
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
name|indexSettings
operator|.
name|getDefaultField
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
name|indexSettings
operator|.
name|isQueryStringLenient
argument_list|()
return|;
block|}
DECL|method|queryStringAnalyzeWildcard
specifier|public
name|boolean
name|queryStringAnalyzeWildcard
parameter_list|()
block|{
return|return
name|indexSettings
operator|.
name|isQueryStringAnalyzeWildcard
argument_list|()
return|;
block|}
DECL|method|queryStringAllowLeadingWildcard
specifier|public
name|boolean
name|queryStringAllowLeadingWildcard
parameter_list|()
block|{
return|return
name|indexSettings
operator|.
name|isQueryStringAllowLeadingWildcard
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
name|BitSetProducer
name|bitsetFilter
parameter_list|(
name|Query
name|filter
parameter_list|)
block|{
return|return
name|bitsetFilterCache
operator|.
name|getBitSetProducer
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
name|indexFieldDataService
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
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
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
block|}
DECL|method|copyNamedQueries
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|copyNamedQueries
parameter_list|()
block|{
comment|// This might be a good use case for CopyOnWriteHashMap
return|return
name|unmodifiableMap
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|namedQueries
argument_list|)
argument_list|)
return|;
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
DECL|method|addInnerHit
specifier|public
name|void
name|addInnerHit
parameter_list|(
name|InnerHitBuilder
name|innerHitBuilder
parameter_list|)
throws|throws
name|IOException
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
name|QueryShardException
argument_list|(
name|this
argument_list|,
literal|"inner_hits unsupported"
argument_list|)
throw|;
block|}
name|InnerHitsContext
name|innerHitsContext
init|=
name|sc
operator|.
name|innerHits
argument_list|()
decl_stmt|;
name|innerHitsContext
operator|.
name|addInnerHitDefinition
argument_list|(
name|innerHitBuilder
operator|.
name|buildInline
argument_list|(
name|sc
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
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
name|mapperService
operator|.
name|simpleMatchToIndexNames
argument_list|(
name|pattern
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
name|mapperService
operator|.
name|fullName
argument_list|(
name|name
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
name|mapperService
operator|.
name|getObjectMapper
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Gets the search analyzer for the given field, or the default if there is none present for the field      * TODO: remove this by moving defaults into mappers themselves      */
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
name|getMapperService
argument_list|()
operator|.
name|searchAnalyzer
argument_list|()
return|;
block|}
comment|/**      * Gets the search quote analyzer for the given field, or the default if there is none present for the field      * TODO: remove this by moving defaults into mappers themselves      */
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
name|getMapperService
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
name|fieldMapping
operator|!=
literal|null
operator|||
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
name|TextFieldMapper
operator|.
name|Builder
name|builder
init|=
operator|new
name|TextFieldMapper
operator|.
name|Builder
argument_list|(
name|name
argument_list|)
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
name|indexSettings
operator|.
name|getSettings
argument_list|()
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
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|this
argument_list|,
literal|"No field mapping can be found for the field with name [{}]"
argument_list|,
name|name
argument_list|)
throw|;
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
name|getMapperService
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
name|getMapperService
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
name|getMapperService
argument_list|()
argument_list|,
name|indexFieldDataService
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
DECL|method|indexVersionCreated
specifier|public
name|Version
name|indexVersionCreated
parameter_list|()
block|{
return|return
name|indexSettings
operator|.
name|getIndexVersionCreated
argument_list|()
return|;
block|}
DECL|method|matchesIndices
specifier|public
name|boolean
name|matchesIndices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
if|if
condition|(
name|indexSettings
operator|.
name|matchesIndexName
argument_list|(
name|index
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|parse
specifier|public
name|ParsedQuery
name|parse
parameter_list|(
name|BytesReference
name|source
parameter_list|)
block|{
name|XContentParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|source
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|innerParse
argument_list|(
name|parser
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParsingException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|==
literal|null
condition|?
literal|null
else|:
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"Failed to parse"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|parse
specifier|public
name|ParsedQuery
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
block|{
try|try
block|{
return|return
name|innerParse
argument_list|(
name|parser
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
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
literal|"Failed to parse"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Parses an inner filter, returning null if the filter should be ignored.      */
annotation|@
name|Nullable
DECL|method|parseInnerFilter
specifier|public
name|ParsedQuery
name|parseInnerFilter
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|Query
name|filter
init|=
name|QueryBuilder
operator|.
name|rewriteQuery
argument_list|(
name|newParseContext
argument_list|(
name|parser
argument_list|)
operator|.
name|parseInnerQueryBuilder
argument_list|()
argument_list|,
name|this
argument_list|)
operator|.
name|toFilter
argument_list|(
name|this
argument_list|)
decl_stmt|;
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
return|return
operator|new
name|ParsedQuery
argument_list|(
name|filter
argument_list|,
name|copyNamedQueries
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|innerParse
specifier|private
name|ParsedQuery
name|innerParse
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryShardException
block|{
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|Query
name|query
init|=
name|parseInnerQuery
argument_list|(
name|parser
argument_list|)
decl_stmt|;
return|return
operator|new
name|ParsedQuery
argument_list|(
name|query
argument_list|,
name|copyNamedQueries
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|parseInnerQuery
specifier|public
name|Query
name|parseInnerQuery
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|toQuery
argument_list|(
name|this
operator|.
name|newParseContext
argument_list|(
name|parser
argument_list|)
operator|.
name|parseInnerQueryBuilder
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|toQuery
specifier|public
name|ParsedQuery
name|toQuery
parameter_list|(
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|queryBuilder
parameter_list|)
block|{
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|Query
name|query
init|=
name|toQuery
argument_list|(
name|queryBuilder
argument_list|,
name|this
argument_list|)
decl_stmt|;
return|return
operator|new
name|ParsedQuery
argument_list|(
name|query
argument_list|,
name|copyNamedQueries
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|QueryShardException
decl||
name|ParsingException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|this
argument_list|,
literal|"failed to create query: {}"
argument_list|,
name|e
argument_list|,
name|queryBuilder
argument_list|)
throw|;
block|}
finally|finally
block|{
name|this
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|toQuery
specifier|private
specifier|static
name|Query
name|toQuery
parameter_list|(
specifier|final
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|queryBuilder
parameter_list|,
specifier|final
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Query
name|query
init|=
name|QueryBuilder
operator|.
name|rewriteQuery
argument_list|(
name|queryBuilder
argument_list|,
name|context
argument_list|)
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
return|return
name|Queries
operator|.
name|newMatchNoDocsQuery
argument_list|()
return|;
block|}
return|return
name|query
return|;
block|}
DECL|method|index
specifier|public
specifier|final
name|Index
name|index
parameter_list|()
block|{
return|return
name|indexSettings
operator|.
name|getIndex
argument_list|()
return|;
block|}
block|}
end_class

end_unit

