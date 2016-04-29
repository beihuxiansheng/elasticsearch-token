begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.stats
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|stats
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
name|support
operator|.
name|broadcast
operator|.
name|BroadcastOperationRequestBuilder
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
name|ElasticsearchClient
import|;
end_import

begin_comment
comment|/**  * A request to get indices level stats. Allow to enable different stats to be returned.  *<p>  * By default, the {@link #setDocs(boolean)}, {@link #setStore(boolean)}, {@link #setIndexing(boolean)}  * are enabled. Other stats can be enabled as well.  *<p>  * All the stats to be returned can be cleared using {@link #clear()}, at which point, specific  * stats can be enabled.  */
end_comment

begin_class
DECL|class|IndicesStatsRequestBuilder
specifier|public
class|class
name|IndicesStatsRequestBuilder
extends|extends
name|BroadcastOperationRequestBuilder
argument_list|<
name|IndicesStatsRequest
argument_list|,
name|IndicesStatsResponse
argument_list|,
name|IndicesStatsRequestBuilder
argument_list|>
block|{
DECL|method|IndicesStatsRequestBuilder
specifier|public
name|IndicesStatsRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|IndicesStatsAction
name|action
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
name|action
argument_list|,
operator|new
name|IndicesStatsRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets all flags to return all stats.      */
DECL|method|all
specifier|public
name|IndicesStatsRequestBuilder
name|all
parameter_list|()
block|{
name|request
operator|.
name|all
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Clears all stats.      */
DECL|method|clear
specifier|public
name|IndicesStatsRequestBuilder
name|clear
parameter_list|()
block|{
name|request
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Document types to return stats for. Mainly affects {@link #setIndexing(boolean)} when      * enabled, returning specific indexing stats for those types.      */
DECL|method|setTypes
specifier|public
name|IndicesStatsRequestBuilder
name|setTypes
parameter_list|(
name|String
modifier|...
name|types
parameter_list|)
block|{
name|request
operator|.
name|types
argument_list|(
name|types
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setGroups
specifier|public
name|IndicesStatsRequestBuilder
name|setGroups
parameter_list|(
name|String
modifier|...
name|groups
parameter_list|)
block|{
name|request
operator|.
name|groups
argument_list|(
name|groups
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDocs
specifier|public
name|IndicesStatsRequestBuilder
name|setDocs
parameter_list|(
name|boolean
name|docs
parameter_list|)
block|{
name|request
operator|.
name|docs
argument_list|(
name|docs
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setStore
specifier|public
name|IndicesStatsRequestBuilder
name|setStore
parameter_list|(
name|boolean
name|store
parameter_list|)
block|{
name|request
operator|.
name|store
argument_list|(
name|store
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setIndexing
specifier|public
name|IndicesStatsRequestBuilder
name|setIndexing
parameter_list|(
name|boolean
name|indexing
parameter_list|)
block|{
name|request
operator|.
name|indexing
argument_list|(
name|indexing
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setGet
specifier|public
name|IndicesStatsRequestBuilder
name|setGet
parameter_list|(
name|boolean
name|get
parameter_list|)
block|{
name|request
operator|.
name|get
argument_list|(
name|get
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSearch
specifier|public
name|IndicesStatsRequestBuilder
name|setSearch
parameter_list|(
name|boolean
name|search
parameter_list|)
block|{
name|request
operator|.
name|search
argument_list|(
name|search
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMerge
specifier|public
name|IndicesStatsRequestBuilder
name|setMerge
parameter_list|(
name|boolean
name|merge
parameter_list|)
block|{
name|request
operator|.
name|merge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRefresh
specifier|public
name|IndicesStatsRequestBuilder
name|setRefresh
parameter_list|(
name|boolean
name|refresh
parameter_list|)
block|{
name|request
operator|.
name|refresh
argument_list|(
name|refresh
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFlush
specifier|public
name|IndicesStatsRequestBuilder
name|setFlush
parameter_list|(
name|boolean
name|flush
parameter_list|)
block|{
name|request
operator|.
name|flush
argument_list|(
name|flush
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setWarmer
specifier|public
name|IndicesStatsRequestBuilder
name|setWarmer
parameter_list|(
name|boolean
name|warmer
parameter_list|)
block|{
name|request
operator|.
name|warmer
argument_list|(
name|warmer
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setQueryCache
specifier|public
name|IndicesStatsRequestBuilder
name|setQueryCache
parameter_list|(
name|boolean
name|queryCache
parameter_list|)
block|{
name|request
operator|.
name|queryCache
argument_list|(
name|queryCache
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFieldData
specifier|public
name|IndicesStatsRequestBuilder
name|setFieldData
parameter_list|(
name|boolean
name|fieldData
parameter_list|)
block|{
name|request
operator|.
name|fieldData
argument_list|(
name|fieldData
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFieldDataFields
specifier|public
name|IndicesStatsRequestBuilder
name|setFieldDataFields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|request
operator|.
name|fieldDataFields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setPercolate
specifier|public
name|IndicesStatsRequestBuilder
name|setPercolate
parameter_list|(
name|boolean
name|percolate
parameter_list|)
block|{
name|request
operator|.
name|percolate
argument_list|(
name|percolate
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSegments
specifier|public
name|IndicesStatsRequestBuilder
name|setSegments
parameter_list|(
name|boolean
name|segments
parameter_list|)
block|{
name|request
operator|.
name|segments
argument_list|(
name|segments
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCompletion
specifier|public
name|IndicesStatsRequestBuilder
name|setCompletion
parameter_list|(
name|boolean
name|completion
parameter_list|)
block|{
name|request
operator|.
name|completion
argument_list|(
name|completion
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCompletionFields
specifier|public
name|IndicesStatsRequestBuilder
name|setCompletionFields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|request
operator|.
name|completionFields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setTranslog
specifier|public
name|IndicesStatsRequestBuilder
name|setTranslog
parameter_list|(
name|boolean
name|translog
parameter_list|)
block|{
name|request
operator|.
name|translog
argument_list|(
name|translog
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRequestCache
specifier|public
name|IndicesStatsRequestBuilder
name|setRequestCache
parameter_list|(
name|boolean
name|requestCache
parameter_list|)
block|{
name|request
operator|.
name|requestCache
argument_list|(
name|requestCache
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRecovery
specifier|public
name|IndicesStatsRequestBuilder
name|setRecovery
parameter_list|(
name|boolean
name|recovery
parameter_list|)
block|{
name|request
operator|.
name|recovery
argument_list|(
name|recovery
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setIncludeSegmentFileSizes
specifier|public
name|IndicesStatsRequestBuilder
name|setIncludeSegmentFileSizes
parameter_list|(
name|boolean
name|includeSegmentFileSizes
parameter_list|)
block|{
name|request
operator|.
name|includeSegmentFileSizes
argument_list|(
name|includeSegmentFileSizes
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

