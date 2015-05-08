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
name|QuerySourceBuilder
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
name|BroadcastOperationResponse
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
name|QueryStringQueryBuilder
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
DECL|field|INDEX
specifier|static
specifier|final
name|XContentBuilderString
name|INDEX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
DECL|field|SHARD
specifier|static
specifier|final
name|XContentBuilderString
name|SHARD
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"shard"
argument_list|)
decl_stmt|;
DECL|field|STATUS
specifier|static
specifier|final
name|XContentBuilderString
name|STATUS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"status"
argument_list|)
decl_stmt|;
DECL|field|REASON
specifier|static
specifier|final
name|XContentBuilderString
name|REASON
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"reason"
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
name|BroadcastOperationResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|buildBroadcastShardsHeader
argument_list|(
name|builder
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
for|for
control|(
name|ShardOperationFailedException
name|shardFailure
range|:
name|shardFailures
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|shardFailure
operator|.
name|index
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|INDEX
argument_list|,
name|shardFailure
operator|.
name|index
argument_list|()
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shardFailure
operator|.
name|shardId
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SHARD
argument_list|,
name|shardFailure
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STATUS
argument_list|,
name|shardFailure
operator|.
name|status
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|REASON
argument_list|,
name|shardFailure
operator|.
name|reason
argument_list|()
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
DECL|method|parseQuerySource
specifier|public
specifier|static
name|QuerySourceBuilder
name|parseQuerySource
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
if|if
condition|(
literal|"OR"
operator|.
name|equals
argument_list|(
name|defaultOperator
argument_list|)
condition|)
block|{
name|queryBuilder
operator|.
name|defaultOperator
argument_list|(
name|QueryStringQueryBuilder
operator|.
name|Operator
operator|.
name|OR
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"AND"
operator|.
name|equals
argument_list|(
name|defaultOperator
argument_list|)
condition|)
block|{
name|queryBuilder
operator|.
name|defaultOperator
argument_list|(
name|QueryStringQueryBuilder
operator|.
name|Operator
operator|.
name|AND
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported defaultOperator ["
operator|+
name|defaultOperator
operator|+
literal|"], can either be [OR] or [AND]"
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|QuerySourceBuilder
argument_list|()
operator|.
name|setQuery
argument_list|(
name|queryBuilder
argument_list|)
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

