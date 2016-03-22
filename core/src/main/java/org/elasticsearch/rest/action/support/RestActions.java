begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|ShardOperationFailedException
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
name|broadcast
operator|.
name|BroadcastResponse
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
name|bytes
operator|.
name|BytesArray
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
name|uid
operator|.
name|Versions
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
name|ToXContent
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
name|XContentBuilderString
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
name|common
operator|.
name|xcontent
operator|.
name|XContentType
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
name|Operator
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
name|QueryBuilder
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
name|QueryBuilders
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
name|QueryParseContext
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
name|QueryStringQueryBuilder
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
name|rest
operator|.
name|RestRequest
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
name|aggregations
operator|.
name|AggregatorParsers
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
name|builder
operator|.
name|SearchSourceBuilder
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
name|suggest
operator|.
name|Suggesters
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
DECL|class|RestActions
specifier|public
class|class
name|RestActions
block|{
DECL|method|parseVersion
specifier|public
specifier|static
name|long
name|parseVersion
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|hasParam
argument_list|(
literal|"version"
argument_list|)
condition|)
block|{
return|return
name|request
operator|.
name|paramAsLong
argument_list|(
literal|"version"
argument_list|,
name|Versions
operator|.
name|MATCH_ANY
argument_list|)
return|;
block|}
name|String
name|ifMatch
init|=
name|request
operator|.
name|header
argument_list|(
literal|"If-Match"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ifMatch
operator|!=
literal|null
condition|)
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|ifMatch
argument_list|)
return|;
block|}
return|return
name|Versions
operator|.
name|MATCH_ANY
return|;
block|}
DECL|method|parseVersion
specifier|public
specifier|static
name|long
name|parseVersion
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|long
name|defaultVersion
parameter_list|)
block|{
name|long
name|version
init|=
name|parseVersion
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
name|version
operator|==
name|Versions
operator|.
name|MATCH_ANY
operator|)
condition|?
name|defaultVersion
else|:
name|version
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|_SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|_SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_shards"
argument_list|)
decl_stmt|;
DECL|field|TOTAL
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total"
argument_list|)
decl_stmt|;
DECL|field|SUCCESSFUL
specifier|static
specifier|final
name|XContentBuilderString
name|SUCCESSFUL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"successful"
argument_list|)
decl_stmt|;
DECL|field|FAILED
specifier|static
specifier|final
name|XContentBuilderString
name|FAILED
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"failed"
argument_list|)
decl_stmt|;
DECL|field|FAILURES
specifier|static
specifier|final
name|XContentBuilderString
name|FAILURES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"failures"
argument_list|)
decl_stmt|;
block|}
DECL|method|buildBroadcastShardsHeader
specifier|public
specifier|static
name|void
name|buildBroadcastShardsHeader
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|ToXContent
operator|.
name|Params
name|params
parameter_list|,
name|BroadcastResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|buildBroadcastShardsHeader
argument_list|(
name|builder
argument_list|,
name|params
argument_list|,
name|response
operator|.
name|getTotalShards
argument_list|()
argument_list|,
name|response
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|response
operator|.
name|getFailedShards
argument_list|()
argument_list|,
name|response
operator|.
name|getShardFailures
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|buildBroadcastShardsHeader
specifier|public
specifier|static
name|void
name|buildBroadcastShardsHeader
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|ToXContent
operator|.
name|Params
name|params
parameter_list|,
name|int
name|total
parameter_list|,
name|int
name|successful
parameter_list|,
name|int
name|failed
parameter_list|,
name|ShardOperationFailedException
index|[]
name|shardFailures
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|_SHARDS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL
argument_list|,
name|total
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SUCCESSFUL
argument_list|,
name|successful
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|FAILED
argument_list|,
name|failed
argument_list|)
expr_stmt|;
if|if
condition|(
name|shardFailures
operator|!=
literal|null
operator|&&
name|shardFailures
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|FAILURES
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|group
init|=
name|params
operator|.
name|paramAsBoolean
argument_list|(
literal|"group_shard_failures"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// we group by default
for|for
control|(
name|ShardOperationFailedException
name|shardFailure
range|:
name|group
condition|?
name|ExceptionsHelper
operator|.
name|groupBy
argument_list|(
name|shardFailures
argument_list|)
else|:
name|shardFailures
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|shardFailure
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|urlParamsToQueryBuilder
specifier|public
specifier|static
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|urlParamsToQueryBuilder
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
name|String
name|queryString
init|=
name|request
operator|.
name|param
argument_list|(
literal|"q"
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryString
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|QueryStringQueryBuilder
name|queryBuilder
init|=
name|QueryBuilders
operator|.
name|queryStringQuery
argument_list|(
name|queryString
argument_list|)
decl_stmt|;
name|queryBuilder
operator|.
name|defaultField
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"df"
argument_list|)
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|analyzer
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"analyzer"
argument_list|)
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|analyzeWildcard
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"analyze_wildcard"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|lowercaseExpandedTerms
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"lowercase_expanded_terms"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|lenient
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"lenient"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|defaultOperator
init|=
name|request
operator|.
name|param
argument_list|(
literal|"default_operator"
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultOperator
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|defaultOperator
argument_list|(
name|Operator
operator|.
name|fromString
argument_list|(
name|defaultOperator
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|queryBuilder
return|;
block|}
comment|/**      * Get Rest content from either payload or source parameter      * @param request Rest request      * @return rest content      */
DECL|method|getRestContent
specifier|public
specifier|static
name|BytesReference
name|getRestContent
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
assert|assert
name|request
operator|!=
literal|null
assert|;
name|BytesReference
name|content
init|=
name|request
operator|.
name|content
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|request
operator|.
name|hasContent
argument_list|()
condition|)
block|{
name|String
name|source
init|=
name|request
operator|.
name|param
argument_list|(
literal|"source"
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
name|content
operator|=
operator|new
name|BytesArray
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|content
return|;
block|}
DECL|method|getQueryContent
specifier|public
specifier|static
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|getQueryContent
parameter_list|(
name|BytesReference
name|source
parameter_list|,
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
block|{
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|)
decl_stmt|;
try|try
init|(
name|XContentParser
name|requestParser
init|=
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
init|)
block|{
name|context
operator|.
name|reset
argument_list|(
name|requestParser
argument_list|)
expr_stmt|;
name|context
operator|.
name|parseFieldMatcher
argument_list|(
name|parseFieldMatcher
argument_list|)
expr_stmt|;
return|return
name|context
operator|.
name|parseTopLevelQueryBuilder
argument_list|()
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
name|ElasticsearchException
argument_list|(
literal|"failed to parse source"
argument_list|,
name|e
argument_list|)
throw|;
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
comment|/**      * guesses the content type from either payload or source parameter      * @param request Rest request      * @return rest content type or<code>null</code> if not applicable.      */
DECL|method|guessBodyContentType
specifier|public
specifier|static
name|XContentType
name|guessBodyContentType
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|)
block|{
specifier|final
name|BytesReference
name|restContent
init|=
name|RestActions
operator|.
name|getRestContent
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|restContent
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|restContent
argument_list|)
return|;
block|}
comment|/**      * Returns<code>true</code> if either payload or source parameter is present. Otherwise<code>false</code>      */
DECL|method|hasBodyContent
specifier|public
specifier|static
name|boolean
name|hasBodyContent
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|)
block|{
return|return
name|request
operator|.
name|hasContent
argument_list|()
operator|||
name|request
operator|.
name|hasParam
argument_list|(
literal|"source"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

