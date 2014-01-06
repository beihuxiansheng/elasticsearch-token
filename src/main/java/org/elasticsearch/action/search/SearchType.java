begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
import|;
end_import

begin_comment
comment|/**  * Search type represent the manner at which the search operation is executed.  *  *  */
end_comment

begin_enum
DECL|enum|SearchType
specifier|public
enum|enum
name|SearchType
block|{
comment|/**      * Same as {@link #QUERY_THEN_FETCH}, except for an initial scatter phase which goes and computes the distributed      * term frequencies for more accurate scoring.      */
DECL|enum constant|DFS_QUERY_THEN_FETCH
name|DFS_QUERY_THEN_FETCH
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
comment|/**      * The query is executed against all shards, but only enough information is returned (not the document content).      * The results are then sorted and ranked, and based on it, only the relevant shards are asked for the actual      * document content. The return number of hits is exactly as specified in size, since they are the only ones that      * are fetched. This is very handy when the index has a lot of shards (not replicas, shard id groups).      */
DECL|enum constant|QUERY_THEN_FETCH
name|QUERY_THEN_FETCH
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|,
comment|/**      * Same as {@link #QUERY_AND_FETCH}, except for an initial scatter phase which goes and computes the distributed      * term frequencies for more accurate scoring.      */
DECL|enum constant|DFS_QUERY_AND_FETCH
name|DFS_QUERY_AND_FETCH
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
block|,
comment|/**      * The most naive (and possibly fastest) implementation is to simply execute the query on all relevant shards      * and return the results. Each shard returns size results. Since each shard already returns size hits, this      * type actually returns size times number of shards results back to the caller.      */
DECL|enum constant|QUERY_AND_FETCH
name|QUERY_AND_FETCH
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|)
block|,
comment|/**      * Performs scanning of the results which executes the search without any sorting.      * It will automatically start scrolling the result set.      */
DECL|enum constant|SCAN
name|SCAN
argument_list|(
operator|(
name|byte
operator|)
literal|4
argument_list|)
block|,
comment|/**      * Only counts the results, will still execute facets and the like.      */
DECL|enum constant|COUNT
name|COUNT
argument_list|(
operator|(
name|byte
operator|)
literal|5
argument_list|)
block|;
comment|/**      * The default search type ({@link #QUERY_THEN_FETCH}.      */
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|SearchType
name|DEFAULT
init|=
name|QUERY_THEN_FETCH
decl_stmt|;
DECL|field|id
specifier|private
name|byte
name|id
decl_stmt|;
DECL|method|SearchType
name|SearchType
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|/**      * The internal id of the type.      */
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
comment|/**      * Constructs search type based on the internal id.      */
DECL|method|fromId
specifier|public
specifier|static
name|SearchType
name|fromId
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|0
condition|)
block|{
return|return
name|DFS_QUERY_THEN_FETCH
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|1
condition|)
block|{
return|return
name|QUERY_THEN_FETCH
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|2
condition|)
block|{
return|return
name|DFS_QUERY_AND_FETCH
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|3
condition|)
block|{
return|return
name|QUERY_AND_FETCH
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|4
condition|)
block|{
return|return
name|SCAN
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|5
condition|)
block|{
return|return
name|COUNT
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"No search type for ["
operator|+
name|id
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**      * The a string representation search type to execute, defaults to {@link SearchType#DEFAULT}. Can be      * one of "dfs_query_then_fetch"/"dfsQueryThenFetch", "dfs_query_and_fetch"/"dfsQueryAndFetch",      * "query_then_fetch"/"queryThenFetch", "query_and_fetch"/"queryAndFetch", and "scan".      */
DECL|method|fromString
specifier|public
specifier|static
name|SearchType
name|fromString
parameter_list|(
name|String
name|searchType
parameter_list|)
throws|throws
name|ElasticsearchIllegalArgumentException
block|{
if|if
condition|(
name|searchType
operator|==
literal|null
condition|)
block|{
return|return
name|SearchType
operator|.
name|DEFAULT
return|;
block|}
if|if
condition|(
literal|"dfs_query_then_fetch"
operator|.
name|equals
argument_list|(
name|searchType
argument_list|)
condition|)
block|{
return|return
name|SearchType
operator|.
name|DFS_QUERY_THEN_FETCH
return|;
block|}
elseif|else
if|if
condition|(
literal|"dfs_query_and_fetch"
operator|.
name|equals
argument_list|(
name|searchType
argument_list|)
condition|)
block|{
return|return
name|SearchType
operator|.
name|DFS_QUERY_AND_FETCH
return|;
block|}
elseif|else
if|if
condition|(
literal|"query_then_fetch"
operator|.
name|equals
argument_list|(
name|searchType
argument_list|)
condition|)
block|{
return|return
name|SearchType
operator|.
name|QUERY_THEN_FETCH
return|;
block|}
elseif|else
if|if
condition|(
literal|"query_and_fetch"
operator|.
name|equals
argument_list|(
name|searchType
argument_list|)
condition|)
block|{
return|return
name|SearchType
operator|.
name|QUERY_AND_FETCH
return|;
block|}
elseif|else
if|if
condition|(
literal|"scan"
operator|.
name|equals
argument_list|(
name|searchType
argument_list|)
condition|)
block|{
return|return
name|SearchType
operator|.
name|SCAN
return|;
block|}
elseif|else
if|if
condition|(
literal|"count"
operator|.
name|equals
argument_list|(
name|searchType
argument_list|)
condition|)
block|{
return|return
name|SearchType
operator|.
name|COUNT
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"No search type for ["
operator|+
name|searchType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_enum

end_unit

