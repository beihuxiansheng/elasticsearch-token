begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search.type
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|type
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalStateException
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
name|search
operator|.
name|SearchRequest
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
name|search
operator|.
name|SearchScrollRequest
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
name|search
operator|.
name|SearchType
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
name|search
operator|.
name|ShardSearchFailure
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
name|routing
operator|.
name|ShardRouting
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
name|FetchSearchResultProvider
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
name|InternalScrollSearchRequest
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
name|InternalSearchRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|Tuple
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|TransportSearchHelper
specifier|public
specifier|abstract
class|class
name|TransportSearchHelper
block|{
DECL|field|scrollIdPattern
specifier|private
specifier|final
specifier|static
name|Pattern
name|scrollIdPattern
decl_stmt|;
static|static
block|{
name|scrollIdPattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds the shard failures, and releases the cache (meaning this should only be called once!).      */
DECL|method|buildShardFailures
specifier|public
specifier|static
name|ShardSearchFailure
index|[]
name|buildShardFailures
parameter_list|(
name|Collection
argument_list|<
name|ShardSearchFailure
argument_list|>
name|shardFailures
parameter_list|,
name|TransportSearchCache
name|searchCache
parameter_list|)
block|{
name|ShardSearchFailure
index|[]
name|ret
decl_stmt|;
if|if
condition|(
name|shardFailures
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ret
operator|=
name|ShardSearchFailure
operator|.
name|EMPTY_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
name|shardFailures
operator|.
name|toArray
argument_list|(
name|ShardSearchFailure
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
block|}
name|searchCache
operator|.
name|releaseShardFailures
argument_list|(
name|shardFailures
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|internalSearchRequest
specifier|public
specifier|static
name|InternalSearchRequest
name|internalSearchRequest
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|SearchRequest
name|request
parameter_list|)
block|{
name|InternalSearchRequest
name|internalRequest
init|=
operator|new
name|InternalSearchRequest
argument_list|(
name|shardRouting
argument_list|,
name|request
operator|.
name|source
argument_list|()
argument_list|)
decl_stmt|;
name|internalRequest
operator|.
name|from
argument_list|(
name|request
operator|.
name|from
argument_list|()
argument_list|)
operator|.
name|size
argument_list|(
name|request
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|internalRequest
operator|.
name|scroll
argument_list|(
name|request
operator|.
name|scroll
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|indexBoost
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|request
operator|.
name|indexBoost
argument_list|()
operator|.
name|containsKey
argument_list|(
name|shardRouting
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
name|internalRequest
operator|.
name|queryBoost
argument_list|(
name|request
operator|.
name|indexBoost
argument_list|()
operator|.
name|get
argument_list|(
name|shardRouting
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|internalRequest
operator|.
name|timeout
argument_list|(
name|request
operator|.
name|timeout
argument_list|()
argument_list|)
expr_stmt|;
name|internalRequest
operator|.
name|types
argument_list|(
name|request
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|internalRequest
return|;
block|}
DECL|method|internalScrollSearchRequest
specifier|public
specifier|static
name|InternalScrollSearchRequest
name|internalScrollSearchRequest
parameter_list|(
name|long
name|id
parameter_list|,
name|SearchScrollRequest
name|request
parameter_list|)
block|{
name|InternalScrollSearchRequest
name|internalRequest
init|=
operator|new
name|InternalScrollSearchRequest
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|internalRequest
operator|.
name|scroll
argument_list|(
name|request
operator|.
name|scroll
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|internalRequest
return|;
block|}
DECL|method|buildScrollId
specifier|public
specifier|static
name|String
name|buildScrollId
parameter_list|(
name|SearchType
name|searchType
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|FetchSearchResultProvider
argument_list|>
name|fetchResults
parameter_list|)
block|{
if|if
condition|(
name|searchType
operator|==
name|SearchType
operator|.
name|DFS_QUERY_THEN_FETCH
operator|||
name|searchType
operator|==
name|SearchType
operator|.
name|QUERY_THEN_FETCH
condition|)
block|{
return|return
name|buildScrollId
argument_list|(
name|ParsedScrollId
operator|.
name|QUERY_THEN_FETCH_TYPE
argument_list|,
name|fetchResults
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|searchType
operator|==
name|SearchType
operator|.
name|QUERY_AND_FETCH
operator|||
name|searchType
operator|==
name|SearchType
operator|.
name|DFS_QUERY_AND_FETCH
condition|)
block|{
return|return
name|buildScrollId
argument_list|(
name|ParsedScrollId
operator|.
name|QUERY_AND_FETCH_TYPE
argument_list|,
name|fetchResults
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|()
throw|;
block|}
block|}
DECL|method|buildScrollId
specifier|public
specifier|static
name|String
name|buildScrollId
parameter_list|(
name|String
name|type
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|FetchSearchResultProvider
argument_list|>
name|fetchResults
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
decl_stmt|;
for|for
control|(
name|FetchSearchResultProvider
name|fetchResult
range|:
name|fetchResults
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|fetchResult
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|fetchResult
operator|.
name|shardTarget
argument_list|()
operator|.
name|nodeId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|parseScrollId
specifier|public
specifier|static
name|ParsedScrollId
name|parseScrollId
parameter_list|(
name|String
name|scrollId
parameter_list|)
block|{
name|String
index|[]
name|elements
init|=
name|scrollIdPattern
operator|.
name|split
argument_list|(
name|scrollId
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
index|[]
name|values
init|=
operator|new
name|Tuple
index|[
name|elements
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|elements
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|element
init|=
name|elements
index|[
name|i
index|]
decl_stmt|;
name|int
name|index
init|=
name|element
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|values
index|[
name|i
operator|-
literal|1
index|]
operator|=
operator|new
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|(
name|element
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|element
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ParsedScrollId
argument_list|(
name|scrollId
argument_list|,
name|elements
index|[
literal|0
index|]
argument_list|,
name|values
argument_list|)
return|;
block|}
DECL|method|TransportSearchHelper
specifier|private
name|TransportSearchHelper
parameter_list|()
block|{      }
block|}
end_class

end_unit

