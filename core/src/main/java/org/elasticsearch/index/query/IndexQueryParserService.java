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
name|util
operator|.
name|CloseableThreadLocal
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
name|action
operator|.
name|support
operator|.
name|IndicesOptions
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
name|ClusterService
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
name|IndexNameExpressionResolver
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
name|regex
operator|.
name|Regex
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
name|XContentHelper
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
name|AbstractIndexComponent
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
name|IndexCache
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
name|internal
operator|.
name|AllFieldMapper
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
name|InnerHitsQueryParserHelper
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
name|settings
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|IndexQueryParserService
specifier|public
class|class
name|IndexQueryParserService
extends|extends
name|AbstractIndexComponent
block|{
DECL|field|DEFAULT_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_FIELD
init|=
literal|"index.query.default_field"
decl_stmt|;
DECL|field|QUERY_STRING_LENIENT
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_STRING_LENIENT
init|=
literal|"index.query_string.lenient"
decl_stmt|;
DECL|field|QUERY_STRING_ANALYZE_WILDCARD
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_STRING_ANALYZE_WILDCARD
init|=
literal|"indices.query.query_string.analyze_wildcard"
decl_stmt|;
DECL|field|QUERY_STRING_ALLOW_LEADING_WILDCARD
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_STRING_ALLOW_LEADING_WILDCARD
init|=
literal|"indices.query.query_string.allowLeadingWildcard"
decl_stmt|;
DECL|field|PARSE_STRICT
specifier|public
specifier|static
specifier|final
name|String
name|PARSE_STRICT
init|=
literal|"index.query.parse.strict"
decl_stmt|;
DECL|field|ALLOW_UNMAPPED
specifier|public
specifier|static
specifier|final
name|String
name|ALLOW_UNMAPPED
init|=
literal|"index.query.parse.allow_unmapped_fields"
decl_stmt|;
DECL|field|innerHitsQueryParserHelper
specifier|private
specifier|final
name|InnerHitsQueryParserHelper
name|innerHitsQueryParserHelper
decl_stmt|;
DECL|field|cache
specifier|private
name|CloseableThreadLocal
argument_list|<
name|QueryShardContext
argument_list|>
name|cache
init|=
operator|new
name|CloseableThreadLocal
argument_list|<
name|QueryShardContext
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|QueryShardContext
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|QueryShardContext
argument_list|(
name|index
argument_list|,
name|IndexQueryParserService
operator|.
name|this
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|analysisService
specifier|final
name|AnalysisService
name|analysisService
decl_stmt|;
DECL|field|scriptService
specifier|final
name|ScriptService
name|scriptService
decl_stmt|;
DECL|field|mapperService
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|similarityService
specifier|final
name|SimilarityService
name|similarityService
decl_stmt|;
DECL|field|indexCache
specifier|final
name|IndexCache
name|indexCache
decl_stmt|;
DECL|field|fieldDataService
specifier|protected
name|IndexFieldDataService
name|fieldDataService
decl_stmt|;
DECL|field|clusterService
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|indexNameExpressionResolver
specifier|final
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
decl_stmt|;
DECL|field|bitsetFilterCache
specifier|final
name|BitsetFilterCache
name|bitsetFilterCache
decl_stmt|;
DECL|field|indicesQueriesRegistry
specifier|private
specifier|final
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
decl_stmt|;
DECL|field|defaultField
specifier|private
specifier|final
name|String
name|defaultField
decl_stmt|;
DECL|field|queryStringLenient
specifier|private
specifier|final
name|boolean
name|queryStringLenient
decl_stmt|;
DECL|field|queryStringAnalyzeWildcard
specifier|private
specifier|final
name|boolean
name|queryStringAnalyzeWildcard
decl_stmt|;
DECL|field|queryStringAllowLeadingWildcard
specifier|private
specifier|final
name|boolean
name|queryStringAllowLeadingWildcard
decl_stmt|;
DECL|field|parseFieldMatcher
specifier|private
specifier|final
name|ParseFieldMatcher
name|parseFieldMatcher
decl_stmt|;
DECL|field|defaultAllowUnmappedFields
specifier|private
specifier|final
name|boolean
name|defaultAllowUnmappedFields
decl_stmt|;
DECL|field|client
specifier|private
name|Client
name|client
decl_stmt|;
annotation|@
name|Inject
DECL|method|IndexQueryParserService
specifier|public
name|IndexQueryParserService
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|AnalysisService
name|analysisService
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|IndexCache
name|indexCache
parameter_list|,
name|IndexFieldDataService
name|fieldDataService
parameter_list|,
name|BitsetFilterCache
name|bitsetFilterCache
parameter_list|,
annotation|@
name|Nullable
name|SimilarityService
name|similarityService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|InnerHitsQueryParserHelper
name|innerHitsQueryParserHelper
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|scriptService
operator|=
name|scriptService
expr_stmt|;
name|this
operator|.
name|analysisService
operator|=
name|analysisService
expr_stmt|;
name|this
operator|.
name|mapperService
operator|=
name|mapperService
expr_stmt|;
name|this
operator|.
name|similarityService
operator|=
name|similarityService
expr_stmt|;
name|this
operator|.
name|indexCache
operator|=
name|indexCache
expr_stmt|;
name|this
operator|.
name|fieldDataService
operator|=
name|fieldDataService
expr_stmt|;
name|this
operator|.
name|bitsetFilterCache
operator|=
name|bitsetFilterCache
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|indexNameExpressionResolver
operator|=
name|indexNameExpressionResolver
expr_stmt|;
name|this
operator|.
name|defaultField
operator|=
name|indexSettings
operator|.
name|get
argument_list|(
name|DEFAULT_FIELD
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryStringLenient
operator|=
name|indexSettings
operator|.
name|getAsBoolean
argument_list|(
name|QUERY_STRING_LENIENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryStringAnalyzeWildcard
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|QUERY_STRING_ANALYZE_WILDCARD
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryStringAllowLeadingWildcard
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|QUERY_STRING_ALLOW_LEADING_WILDCARD
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|parseFieldMatcher
operator|=
operator|new
name|ParseFieldMatcher
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultAllowUnmappedFields
operator|=
name|indexSettings
operator|.
name|getAsBoolean
argument_list|(
name|ALLOW_UNMAPPED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicesQueriesRegistry
operator|=
name|indicesQueriesRegistry
expr_stmt|;
name|this
operator|.
name|innerHitsQueryParserHelper
operator|=
name|innerHitsQueryParserHelper
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|defaultField
specifier|public
name|String
name|defaultField
parameter_list|()
block|{
return|return
name|this
operator|.
name|defaultField
return|;
block|}
DECL|method|queryStringAnalyzeWildcard
specifier|public
name|boolean
name|queryStringAnalyzeWildcard
parameter_list|()
block|{
return|return
name|this
operator|.
name|queryStringAnalyzeWildcard
return|;
block|}
DECL|method|queryStringAllowLeadingWildcard
specifier|public
name|boolean
name|queryStringAllowLeadingWildcard
parameter_list|()
block|{
return|return
name|this
operator|.
name|queryStringAllowLeadingWildcard
return|;
block|}
DECL|method|queryStringLenient
specifier|public
name|boolean
name|queryStringLenient
parameter_list|()
block|{
return|return
name|this
operator|.
name|queryStringLenient
return|;
block|}
DECL|method|indicesQueriesRegistry
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
parameter_list|()
block|{
return|return
name|indicesQueriesRegistry
return|;
block|}
comment|//norelease this needs to go away
DECL|method|parse
specifier|public
name|ParsedQuery
name|parse
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
name|XContentParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|BytesReference
name|bytes
init|=
name|queryBuilder
operator|.
name|buildAsBytes
argument_list|()
decl_stmt|;
name|parser
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|bytes
argument_list|)
operator|.
name|createParser
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
return|return
name|innerParse
argument_list|(
name|cache
operator|.
name|get
argument_list|()
argument_list|,
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
name|BytesReference
name|source
parameter_list|)
block|{
return|return
name|parse
argument_list|(
name|cache
operator|.
name|get
argument_list|()
argument_list|,
name|source
argument_list|)
return|;
block|}
comment|//norelease
DECL|method|parse
specifier|public
name|ParsedQuery
name|parse
parameter_list|(
name|QueryShardContext
name|context
parameter_list|,
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
name|context
argument_list|,
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
name|String
name|source
parameter_list|)
throws|throws
name|ParsingException
throws|,
name|QueryShardException
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
name|cache
operator|.
name|get
argument_list|()
argument_list|,
name|parser
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
literal|"Failed to parse ["
operator|+
name|source
operator|+
literal|"]"
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
name|cache
operator|.
name|get
argument_list|()
argument_list|,
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
comment|//norelease
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
name|QueryShardContext
name|context
init|=
name|cache
operator|.
name|get
argument_list|()
decl_stmt|;
name|context
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
try|try
block|{
name|Query
name|filter
init|=
name|context
operator|.
name|parseContext
argument_list|()
operator|.
name|parseInnerFilter
argument_list|()
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
name|context
operator|.
name|copyNamedQueries
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|reset
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Nullable
DECL|method|parseInnerQueryBuilder
specifier|public
name|QueryBuilder
name|parseInnerQueryBuilder
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
block|{
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|(
name|parseFieldMatcher
argument_list|)
expr_stmt|;
return|return
name|parseContext
operator|.
name|parseInnerQueryBuilder
argument_list|()
return|;
block|}
annotation|@
name|Nullable
comment|//norelease
DECL|method|parseInnerQuery
specifier|public
name|Query
name|parseInnerQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
name|context
operator|.
name|parseContext
argument_list|()
operator|.
name|parseInnerQueryBuilder
argument_list|()
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
name|query
operator|=
name|Queries
operator|.
name|newMatchNoDocsQuery
argument_list|()
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
DECL|method|getShardContext
specifier|public
name|QueryShardContext
name|getShardContext
parameter_list|()
block|{
return|return
name|cache
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|defaultAllowUnmappedFields
specifier|public
name|boolean
name|defaultAllowUnmappedFields
parameter_list|()
block|{
return|return
name|defaultAllowUnmappedFields
return|;
block|}
comment|/**      * @return The lowest node version in the cluster when the index was created or<code>null</code> if that was unknown      */
DECL|method|getIndexCreatedVersion
specifier|public
name|Version
name|getIndexCreatedVersion
parameter_list|()
block|{
return|return
name|Version
operator|.
name|indexCreated
argument_list|(
name|indexSettings
argument_list|)
return|;
block|}
comment|/**      * Selectively parses a query from a top level query or query_binary json field from the specified source.      */
DECL|method|parseQuery
specifier|public
name|ParsedQuery
name|parseQuery
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
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|ParsedQuery
name|parsedQuery
init|=
literal|null
decl_stmt|;
for|for
control|(
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
init|;
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
control|)
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
name|String
name|fieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"query"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|parsedQuery
operator|=
name|parse
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"query_binary"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"queryBinary"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|byte
index|[]
name|querySource
init|=
name|parser
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
name|XContentParser
name|qSourceParser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|querySource
argument_list|)
operator|.
name|createParser
argument_list|(
name|querySource
argument_list|)
decl_stmt|;
name|parsedQuery
operator|=
name|parse
argument_list|(
name|qSourceParser
argument_list|)
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
literal|"request does not support ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|parsedQuery
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
literal|"Required query is missing"
argument_list|)
throw|;
block|}
return|return
name|parsedQuery
return|;
block|}
catch|catch
parameter_list|(
name|ParsingException
decl||
name|QueryShardException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
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
block|}
comment|//norelease
DECL|method|innerParse
specifier|private
name|ParsedQuery
name|innerParse
parameter_list|(
name|QueryShardContext
name|context
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryShardException
block|{
name|context
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
try|try
block|{
name|context
operator|.
name|parseFieldMatcher
argument_list|(
name|parseFieldMatcher
argument_list|)
expr_stmt|;
return|return
name|innerParse
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|parseContext
argument_list|()
operator|.
name|parseInnerQueryBuilder
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|reset
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|innerParse
specifier|private
specifier|static
name|ParsedQuery
name|innerParse
parameter_list|(
name|QueryShardContext
name|context
parameter_list|,
name|QueryBuilder
name|queryBuilder
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryShardException
block|{
name|Query
name|query
init|=
name|queryBuilder
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
name|query
operator|=
name|Queries
operator|.
name|newMatchNoDocsQuery
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ParsedQuery
argument_list|(
name|query
argument_list|,
name|context
operator|.
name|copyNamedQueries
argument_list|()
argument_list|)
return|;
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
specifier|final
name|String
index|[]
name|concreteIndices
init|=
name|indexNameExpressionResolver
operator|.
name|concreteIndices
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|,
name|IndicesOptions
operator|.
name|lenientExpandOpen
argument_list|()
argument_list|,
name|indices
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|concreteIndices
control|)
block|{
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|index
argument_list|,
name|this
operator|.
name|index
operator|.
name|name
argument_list|()
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
DECL|method|getInnerHitsQueryParserHelper
specifier|public
name|InnerHitsQueryParserHelper
name|getInnerHitsQueryParserHelper
parameter_list|()
block|{
return|return
name|innerHitsQueryParserHelper
return|;
block|}
DECL|method|getClient
specifier|public
name|Client
name|getClient
parameter_list|()
block|{
return|return
name|client
return|;
block|}
block|}
end_class

end_unit

