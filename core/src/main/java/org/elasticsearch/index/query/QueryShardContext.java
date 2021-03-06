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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SetOnce
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
name|common
operator|.
name|CheckedFunction
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
name|NamedXContentRegistry
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
name|IndexAnalyzers
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
name|mapper
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
name|ExecutableScript
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
name|Script
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
name|ScriptContext
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
name|script
operator|.
name|SearchScript
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|LongSupplier
import|;
end_import

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
DECL|field|shardId
specifier|private
specifier|final
name|int
name|shardId
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
DECL|field|cachable
specifier|private
name|boolean
name|cachable
init|=
literal|true
decl_stmt|;
DECL|field|frozen
specifier|private
specifier|final
name|SetOnce
argument_list|<
name|Boolean
argument_list|>
name|frozen
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
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
DECL|method|QueryShardContext
specifier|public
name|QueryShardContext
parameter_list|(
name|int
name|shardId
parameter_list|,
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
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|,
name|Client
name|client
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|LongSupplier
name|nowInMillis
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
name|xContentRegistry
argument_list|,
name|client
argument_list|,
name|reader
argument_list|,
name|nowInMillis
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
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
name|shardId
argument_list|,
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
name|getXContentRegistry
argument_list|()
argument_list|,
name|source
operator|.
name|client
argument_list|,
name|source
operator|.
name|reader
argument_list|,
name|source
operator|.
name|nowInMillis
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
specifier|private
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
DECL|method|getIndexAnalyzers
specifier|public
name|IndexAnalyzers
name|getIndexAnalyzers
parameter_list|()
block|{
return|return
name|mapperService
operator|.
name|getIndexAnalyzers
argument_list|()
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
comment|/**      * Public for testing only!      *      * Sets whether we are currently parsing a filter or a query      */
DECL|method|setIsFilter
specifier|public
name|void
name|setIsFilter
parameter_list|(
name|boolean
name|isFilter
parameter_list|)
block|{
name|this
operator|.
name|isFilter
operator|=
name|isFilter
expr_stmt|;
block|}
comment|/**      * Returns all the fields that match a given pattern. If prefixed with a      * type then the fields will be returned with a type prefix.      */
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
comment|/**      * Returns s {@link DocumentMapper} instance for the given type.      * Delegates to {@link MapperService#documentMapper(String)}      */
DECL|method|documentMapper
specifier|public
name|DocumentMapper
name|documentMapper
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|type
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
name|types
argument_list|)
expr_stmt|;
block|}
return|return
name|lookup
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
DECL|method|toFilter
specifier|public
name|ParsedQuery
name|toFilter
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
return|return
name|toQuery
argument_list|(
name|queryBuilder
argument_list|,
name|q
lambda|->
block|{
name|Query
name|filter
init|=
name|q
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
name|filter
return|;
block|}
argument_list|)
return|;
block|}
DECL|method|toQuery
specifier|public
name|ParsedQuery
name|toQuery
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
return|return
name|toQuery
argument_list|(
name|queryBuilder
argument_list|,
name|q
lambda|->
block|{
name|Query
name|query
init|=
name|q
operator|.
name|toQuery
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
name|query
operator|=
name|Queries
operator|.
name|newMatchNoDocsQuery
argument_list|(
literal|"No query left after rewrite."
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
argument_list|)
return|;
block|}
DECL|method|toQuery
specifier|private
name|ParsedQuery
name|toQuery
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|,
name|CheckedFunction
argument_list|<
name|QueryBuilder
argument_list|,
name|Query
argument_list|,
name|IOException
argument_list|>
name|filterOrQuery
parameter_list|)
block|{
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|QueryBuilder
name|rewriteQuery
init|=
name|QueryBuilder
operator|.
name|rewriteQuery
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
name|filterOrQuery
operator|.
name|apply
argument_list|(
name|rewriteQuery
argument_list|)
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
name|reset
argument_list|()
expr_stmt|;
block|}
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
comment|/** Return the script service to allow compiling scripts. */
DECL|method|getScriptService
specifier|public
specifier|final
name|ScriptService
name|getScriptService
parameter_list|()
block|{
name|failIfFrozen
argument_list|()
expr_stmt|;
return|return
name|scriptService
return|;
block|}
comment|/**      * if this method is called the query context will throw exception if methods are accessed      * that could yield different results across executions like {@link #getTemplateBytes(Script)}      */
DECL|method|freezeContext
specifier|public
specifier|final
name|void
name|freezeContext
parameter_list|()
block|{
name|this
operator|.
name|frozen
operator|.
name|set
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
comment|/**      * This method fails if {@link #freezeContext()} is called before on this      * context. This is used to<i>seal</i>.      *      * This methods and all methods that call it should be final to ensure that      * setting the request as not cacheable and the freezing behaviour of this      * class cannot be bypassed. This is important so we can trust when this      * class says a request can be cached.      */
DECL|method|failIfFrozen
specifier|protected
specifier|final
name|void
name|failIfFrozen
parameter_list|()
block|{
name|this
operator|.
name|cachable
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|frozen
operator|.
name|get
argument_list|()
operator|==
name|Boolean
operator|.
name|TRUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"features that prevent cachability are disabled on this context"
argument_list|)
throw|;
block|}
else|else
block|{
assert|assert
name|frozen
operator|.
name|get
argument_list|()
operator|==
literal|null
operator|:
name|frozen
operator|.
name|get
argument_list|()
assert|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTemplateBytes
specifier|public
specifier|final
name|String
name|getTemplateBytes
parameter_list|(
name|Script
name|template
parameter_list|)
block|{
name|failIfFrozen
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getTemplateBytes
argument_list|(
name|template
argument_list|)
return|;
block|}
comment|/**      * Returns<code>true</code> iff the result of the processed search request is cachable. Otherwise<code>false</code>      */
DECL|method|isCachable
specifier|public
specifier|final
name|boolean
name|isCachable
parameter_list|()
block|{
return|return
name|cachable
return|;
block|}
comment|/**      * Returns the shard ID this context was created for.      */
DECL|method|getShardId
specifier|public
name|int
name|getShardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
annotation|@
name|Override
DECL|method|nowInMillis
specifier|public
specifier|final
name|long
name|nowInMillis
parameter_list|()
block|{
name|failIfFrozen
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|nowInMillis
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getClient
specifier|public
name|Client
name|getClient
parameter_list|()
block|{
name|failIfFrozen
argument_list|()
expr_stmt|;
comment|// we somebody uses a terms filter with lookup for instance can't be cached...
return|return
name|super
operator|.
name|getClient
argument_list|()
return|;
block|}
block|}
end_class

end_unit

