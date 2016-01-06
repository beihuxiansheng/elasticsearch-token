begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
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
name|admin
operator|.
name|cluster
operator|.
name|health
operator|.
name|ClusterHealthRequest
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|NodesInfoRequest
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|stats
operator|.
name|NodesStatsRequest
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|tasks
operator|.
name|list
operator|.
name|ListTasksRequest
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
name|admin
operator|.
name|cluster
operator|.
name|repositories
operator|.
name|delete
operator|.
name|DeleteRepositoryRequest
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
name|admin
operator|.
name|cluster
operator|.
name|repositories
operator|.
name|get
operator|.
name|GetRepositoriesRequest
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
name|admin
operator|.
name|cluster
operator|.
name|repositories
operator|.
name|put
operator|.
name|PutRepositoryRequest
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
name|admin
operator|.
name|cluster
operator|.
name|repositories
operator|.
name|verify
operator|.
name|VerifyRepositoryRequest
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
name|admin
operator|.
name|cluster
operator|.
name|reroute
operator|.
name|ClusterRerouteRequest
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
name|admin
operator|.
name|cluster
operator|.
name|settings
operator|.
name|ClusterUpdateSettingsRequest
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
name|admin
operator|.
name|cluster
operator|.
name|shards
operator|.
name|ClusterSearchShardsRequest
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
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|create
operator|.
name|CreateSnapshotRequest
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
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|delete
operator|.
name|DeleteSnapshotRequest
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
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|get
operator|.
name|GetSnapshotsRequest
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
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|restore
operator|.
name|RestoreSnapshotRequest
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
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|status
operator|.
name|SnapshotsStatusRequest
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
name|admin
operator|.
name|cluster
operator|.
name|state
operator|.
name|ClusterStateRequest
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
name|admin
operator|.
name|cluster
operator|.
name|stats
operator|.
name|ClusterStatsRequest
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
name|admin
operator|.
name|indices
operator|.
name|alias
operator|.
name|IndicesAliasesRequest
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
name|admin
operator|.
name|indices
operator|.
name|cache
operator|.
name|clear
operator|.
name|ClearIndicesCacheRequest
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
name|admin
operator|.
name|indices
operator|.
name|close
operator|.
name|CloseIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|CreateIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|delete
operator|.
name|DeleteIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|exists
operator|.
name|indices
operator|.
name|IndicesExistsRequest
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
name|admin
operator|.
name|indices
operator|.
name|flush
operator|.
name|FlushRequest
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
name|admin
operator|.
name|indices
operator|.
name|flush
operator|.
name|SyncedFlushRequest
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
name|admin
operator|.
name|indices
operator|.
name|forcemerge
operator|.
name|ForceMergeRequest
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
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|put
operator|.
name|PutMappingRequest
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
name|admin
operator|.
name|indices
operator|.
name|open
operator|.
name|OpenIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|refresh
operator|.
name|RefreshRequest
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
name|admin
operator|.
name|indices
operator|.
name|segments
operator|.
name|IndicesSegmentsRequest
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
name|admin
operator|.
name|indices
operator|.
name|settings
operator|.
name|put
operator|.
name|UpdateSettingsRequest
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
name|admin
operator|.
name|indices
operator|.
name|shards
operator|.
name|IndicesShardStoresRequest
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
name|admin
operator|.
name|indices
operator|.
name|upgrade
operator|.
name|post
operator|.
name|UpgradeRequest
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
name|bulk
operator|.
name|BulkRequest
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
name|delete
operator|.
name|DeleteRequest
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
name|get
operator|.
name|GetRequest
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
name|index
operator|.
name|IndexRequest
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
name|suggest
operator|.
name|SuggestRequest
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

begin_comment
comment|/**  * A handy one stop shop for creating requests (make sure to import static this class).  */
end_comment

begin_class
DECL|class|Requests
specifier|public
class|class
name|Requests
block|{
comment|/**      * The content type used to generate request builders (query / search).      */
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
name|XContentType
name|CONTENT_TYPE
init|=
name|XContentType
operator|.
name|SMILE
decl_stmt|;
comment|/**      * The default content type to use to generate source documents when indexing.      */
DECL|field|INDEX_CONTENT_TYPE
specifier|public
specifier|static
name|XContentType
name|INDEX_CONTENT_TYPE
init|=
name|XContentType
operator|.
name|JSON
decl_stmt|;
DECL|method|indexRequest
specifier|public
specifier|static
name|IndexRequest
name|indexRequest
parameter_list|()
block|{
return|return
operator|new
name|IndexRequest
argument_list|()
return|;
block|}
comment|/**      * Create an index request against a specific index. Note the {@link IndexRequest#type(String)} must be      * set as well and optionally the {@link IndexRequest#id(String)}.      *      * @param index The index name to index the request against      * @return The index request      * @see org.elasticsearch.client.Client#index(org.elasticsearch.action.index.IndexRequest)      */
DECL|method|indexRequest
specifier|public
specifier|static
name|IndexRequest
name|indexRequest
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|IndexRequest
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**      * Creates a delete request against a specific index. Note the {@link DeleteRequest#type(String)} and      * {@link DeleteRequest#id(String)} must be set.      *      * @param index The index name to delete from      * @return The delete request      * @see org.elasticsearch.client.Client#delete(org.elasticsearch.action.delete.DeleteRequest)      */
DECL|method|deleteRequest
specifier|public
specifier|static
name|DeleteRequest
name|deleteRequest
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|DeleteRequest
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**      * Creats a new bulk request.      */
DECL|method|bulkRequest
specifier|public
specifier|static
name|BulkRequest
name|bulkRequest
parameter_list|()
block|{
return|return
operator|new
name|BulkRequest
argument_list|()
return|;
block|}
comment|/**      * Creates a get request to get the JSON source from an index based on a type and id. Note, the      * {@link GetRequest#type(String)} and {@link GetRequest#id(String)} must be set.      *      * @param index The index to get the JSON source from      * @return The get request      * @see org.elasticsearch.client.Client#get(org.elasticsearch.action.get.GetRequest)      */
DECL|method|getRequest
specifier|public
specifier|static
name|GetRequest
name|getRequest
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|GetRequest
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**      * Creates a suggest request for getting suggestions from provided<code>indices</code>.      * The suggest query has to be set using the JSON source using {@link org.elasticsearch.action.suggest.SuggestRequest#suggest(org.elasticsearch.common.bytes.BytesReference)}.      * @param indices The indices to suggest from. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @see org.elasticsearch.client.Client#suggest(org.elasticsearch.action.suggest.SuggestRequest)      */
DECL|method|suggestRequest
specifier|public
specifier|static
name|SuggestRequest
name|suggestRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|SuggestRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates a search request against one or more indices. Note, the search source must be set either using the      * actual JSON search source, or the {@link org.elasticsearch.search.builder.SearchSourceBuilder}.      *      * @param indices The indices to search against. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The search request      * @see org.elasticsearch.client.Client#search(org.elasticsearch.action.search.SearchRequest)      */
DECL|method|searchRequest
specifier|public
specifier|static
name|SearchRequest
name|searchRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|SearchRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates a search scroll request allowing to continue searching a previous search request.      *      * @param scrollId The scroll id representing the scrollable search      * @return The search scroll request      * @see org.elasticsearch.client.Client#searchScroll(org.elasticsearch.action.search.SearchScrollRequest)      */
DECL|method|searchScrollRequest
specifier|public
specifier|static
name|SearchScrollRequest
name|searchScrollRequest
parameter_list|(
name|String
name|scrollId
parameter_list|)
block|{
return|return
operator|new
name|SearchScrollRequest
argument_list|(
name|scrollId
argument_list|)
return|;
block|}
DECL|method|indicesSegmentsRequest
specifier|public
specifier|static
name|IndicesSegmentsRequest
name|indicesSegmentsRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|IndicesSegmentsRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates an indices shard stores info request.      * @param indices The indices to get shard store information on      * @return The indices shard stores request      * @see org.elasticsearch.client.IndicesAdminClient#shardStores(IndicesShardStoresRequest)      */
DECL|method|indicesShardStoresRequest
specifier|public
specifier|static
name|IndicesShardStoresRequest
name|indicesShardStoresRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|IndicesShardStoresRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates an indices exists request.      *      * @param indices The indices to check if they exists or not.      * @return The indices exists request      * @see org.elasticsearch.client.IndicesAdminClient#exists(org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest)      */
DECL|method|indicesExistsRequest
specifier|public
specifier|static
name|IndicesExistsRequest
name|indicesExistsRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|IndicesExistsRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates a create index request.      *      * @param index The index to create      * @return The index create request      * @see org.elasticsearch.client.IndicesAdminClient#create(org.elasticsearch.action.admin.indices.create.CreateIndexRequest)      */
DECL|method|createIndexRequest
specifier|public
specifier|static
name|CreateIndexRequest
name|createIndexRequest
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|CreateIndexRequest
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**      * Creates a delete index request.      *      * @param index The index to delete      * @return The delete index request      * @see org.elasticsearch.client.IndicesAdminClient#delete(org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest)      */
DECL|method|deleteIndexRequest
specifier|public
specifier|static
name|DeleteIndexRequest
name|deleteIndexRequest
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|DeleteIndexRequest
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**      * Creates a close index request.      *      * @param index The index to close      * @return The delete index request      * @see org.elasticsearch.client.IndicesAdminClient#close(org.elasticsearch.action.admin.indices.close.CloseIndexRequest)      */
DECL|method|closeIndexRequest
specifier|public
specifier|static
name|CloseIndexRequest
name|closeIndexRequest
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|CloseIndexRequest
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**      * Creates an open index request.      *      * @param index The index to open      * @return The delete index request      * @see org.elasticsearch.client.IndicesAdminClient#open(org.elasticsearch.action.admin.indices.open.OpenIndexRequest)      */
DECL|method|openIndexRequest
specifier|public
specifier|static
name|OpenIndexRequest
name|openIndexRequest
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|OpenIndexRequest
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**      * Create a create mapping request against one or more indices.      *      * @param indices The indices to create mapping. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The create mapping request      * @see org.elasticsearch.client.IndicesAdminClient#putMapping(org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest)      */
DECL|method|putMappingRequest
specifier|public
specifier|static
name|PutMappingRequest
name|putMappingRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|PutMappingRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates an index aliases request allowing to add and remove aliases.      *      * @return The index aliases request      */
DECL|method|indexAliasesRequest
specifier|public
specifier|static
name|IndicesAliasesRequest
name|indexAliasesRequest
parameter_list|()
block|{
return|return
operator|new
name|IndicesAliasesRequest
argument_list|()
return|;
block|}
comment|/**      * Creates a refresh indices request.      *      * @param indices The indices to refresh. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The refresh request      * @see org.elasticsearch.client.IndicesAdminClient#refresh(org.elasticsearch.action.admin.indices.refresh.RefreshRequest)      */
DECL|method|refreshRequest
specifier|public
specifier|static
name|RefreshRequest
name|refreshRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|RefreshRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates a flush indices request.      *      * @param indices The indices to flush. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The flush request      * @see org.elasticsearch.client.IndicesAdminClient#flush(org.elasticsearch.action.admin.indices.flush.FlushRequest)      */
DECL|method|flushRequest
specifier|public
specifier|static
name|FlushRequest
name|flushRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|FlushRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates a synced flush indices request.      *      * @param indices The indices to sync flush. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The synced flush request      * @see org.elasticsearch.client.IndicesAdminClient#syncedFlush(SyncedFlushRequest)      */
DECL|method|syncedFlushRequest
specifier|public
specifier|static
name|SyncedFlushRequest
name|syncedFlushRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|SyncedFlushRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates a force merge request.      *      * @param indices The indices to force merge. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The force merge request      * @see org.elasticsearch.client.IndicesAdminClient#forceMerge(org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest)      */
DECL|method|forceMergeRequest
specifier|public
specifier|static
name|ForceMergeRequest
name|forceMergeRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|ForceMergeRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates an upgrade request.      *      * @param indices The indices to upgrade. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The upgrade request      * @see org.elasticsearch.client.IndicesAdminClient#upgrade(UpgradeRequest)      */
DECL|method|upgradeRequest
specifier|public
specifier|static
name|UpgradeRequest
name|upgradeRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|UpgradeRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates a clean indices cache request.      *      * @param indices The indices to clean their caches. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The request      */
DECL|method|clearIndicesCacheRequest
specifier|public
specifier|static
name|ClearIndicesCacheRequest
name|clearIndicesCacheRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|ClearIndicesCacheRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * A request to update indices settings.      *      * @param indices The indices to update the settings for. Use<tt>null</tt> or<tt>_all</tt> to executed against all indices.      * @return The request      */
DECL|method|updateSettingsRequest
specifier|public
specifier|static
name|UpdateSettingsRequest
name|updateSettingsRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|UpdateSettingsRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates a cluster state request.      *      * @return The cluster state request.      * @see org.elasticsearch.client.ClusterAdminClient#state(org.elasticsearch.action.admin.cluster.state.ClusterStateRequest)      */
DECL|method|clusterStateRequest
specifier|public
specifier|static
name|ClusterStateRequest
name|clusterStateRequest
parameter_list|()
block|{
return|return
operator|new
name|ClusterStateRequest
argument_list|()
return|;
block|}
DECL|method|clusterRerouteRequest
specifier|public
specifier|static
name|ClusterRerouteRequest
name|clusterRerouteRequest
parameter_list|()
block|{
return|return
operator|new
name|ClusterRerouteRequest
argument_list|()
return|;
block|}
DECL|method|clusterUpdateSettingsRequest
specifier|public
specifier|static
name|ClusterUpdateSettingsRequest
name|clusterUpdateSettingsRequest
parameter_list|()
block|{
return|return
operator|new
name|ClusterUpdateSettingsRequest
argument_list|()
return|;
block|}
comment|/**      * Creates a cluster health request.      *      * @param indices The indices to provide additional cluster health information for. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The cluster health request      * @see org.elasticsearch.client.ClusterAdminClient#health(org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest)      */
DECL|method|clusterHealthRequest
specifier|public
specifier|static
name|ClusterHealthRequest
name|clusterHealthRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|ClusterHealthRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * List all shards for the give search      */
DECL|method|clusterSearchShardsRequest
specifier|public
specifier|static
name|ClusterSearchShardsRequest
name|clusterSearchShardsRequest
parameter_list|()
block|{
return|return
operator|new
name|ClusterSearchShardsRequest
argument_list|()
return|;
block|}
comment|/**      * List all shards for the give search      */
DECL|method|clusterSearchShardsRequest
specifier|public
specifier|static
name|ClusterSearchShardsRequest
name|clusterSearchShardsRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|ClusterSearchShardsRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates a nodes info request against all the nodes.      *      * @return The nodes info request      * @see org.elasticsearch.client.ClusterAdminClient#nodesInfo(org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest)      */
DECL|method|nodesInfoRequest
specifier|public
specifier|static
name|NodesInfoRequest
name|nodesInfoRequest
parameter_list|()
block|{
return|return
operator|new
name|NodesInfoRequest
argument_list|()
return|;
block|}
comment|/**      * Creates a nodes info request against one or more nodes. Pass<tt>null</tt> or an empty array for all nodes.      *      * @param nodesIds The nodes ids to get the status for      * @return The nodes info request      * @see org.elasticsearch.client.ClusterAdminClient#nodesStats(org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest)      */
DECL|method|nodesInfoRequest
specifier|public
specifier|static
name|NodesInfoRequest
name|nodesInfoRequest
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
block|{
return|return
operator|new
name|NodesInfoRequest
argument_list|(
name|nodesIds
argument_list|)
return|;
block|}
comment|/**      * Creates a nodes stats request against one or more nodes. Pass<tt>null</tt> or an empty array for all nodes.      *      * @param nodesIds The nodes ids to get the stats for      * @return The nodes info request      * @see org.elasticsearch.client.ClusterAdminClient#nodesStats(org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest)      */
DECL|method|nodesStatsRequest
specifier|public
specifier|static
name|NodesStatsRequest
name|nodesStatsRequest
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
block|{
return|return
operator|new
name|NodesStatsRequest
argument_list|(
name|nodesIds
argument_list|)
return|;
block|}
comment|/**      * Creates a cluster stats request.      *      * @return The cluster stats request      * @see org.elasticsearch.client.ClusterAdminClient#clusterStats(org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest)      */
DECL|method|clusterStatsRequest
specifier|public
specifier|static
name|ClusterStatsRequest
name|clusterStatsRequest
parameter_list|()
block|{
return|return
operator|new
name|ClusterStatsRequest
argument_list|()
return|;
block|}
comment|/**      * Creates a nodes tasks request against all the nodes.      *      * @return The nodes tasks request      * @see org.elasticsearch.client.ClusterAdminClient#listTasks(ListTasksRequest)      */
DECL|method|listTasksRequest
specifier|public
specifier|static
name|ListTasksRequest
name|listTasksRequest
parameter_list|()
block|{
return|return
operator|new
name|ListTasksRequest
argument_list|()
return|;
block|}
comment|/**      * Creates a nodes tasks request against one or more nodes. Pass<tt>null</tt> or an empty array for all nodes.      *      * @param nodesIds The nodes ids to get the tasks for      * @return The nodes tasks request      * @see org.elasticsearch.client.ClusterAdminClient#nodesStats(org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest)      */
DECL|method|listTasksRequest
specifier|public
specifier|static
name|ListTasksRequest
name|listTasksRequest
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
block|{
return|return
operator|new
name|ListTasksRequest
argument_list|(
name|nodesIds
argument_list|)
return|;
block|}
comment|/**      * Registers snapshot repository      *      * @param name repository name      * @return repository registration request      */
DECL|method|putRepositoryRequest
specifier|public
specifier|static
name|PutRepositoryRequest
name|putRepositoryRequest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|PutRepositoryRequest
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Gets snapshot repository      *      * @param repositories names of repositories      * @return get repository request      */
DECL|method|getRepositoryRequest
specifier|public
specifier|static
name|GetRepositoriesRequest
name|getRepositoryRequest
parameter_list|(
name|String
modifier|...
name|repositories
parameter_list|)
block|{
return|return
operator|new
name|GetRepositoriesRequest
argument_list|(
name|repositories
argument_list|)
return|;
block|}
comment|/**      * Deletes registration for snapshot repository      *      * @param name repository name      * @return delete repository request      */
DECL|method|deleteRepositoryRequest
specifier|public
specifier|static
name|DeleteRepositoryRequest
name|deleteRepositoryRequest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|DeleteRepositoryRequest
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Verifies snapshot repository      *      * @param name repository name      * @return repository verification request      */
DECL|method|verifyRepositoryRequest
specifier|public
specifier|static
name|VerifyRepositoryRequest
name|verifyRepositoryRequest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|VerifyRepositoryRequest
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Creates new snapshot      *      * @param repository repository name      * @param snapshot   snapshot name      * @return create snapshot request      */
DECL|method|createSnapshotRequest
specifier|public
specifier|static
name|CreateSnapshotRequest
name|createSnapshotRequest
parameter_list|(
name|String
name|repository
parameter_list|,
name|String
name|snapshot
parameter_list|)
block|{
return|return
operator|new
name|CreateSnapshotRequest
argument_list|(
name|repository
argument_list|,
name|snapshot
argument_list|)
return|;
block|}
comment|/**      * Gets snapshots from repository      *      * @param repository repository name      * @return get snapshot  request      */
DECL|method|getSnapshotsRequest
specifier|public
specifier|static
name|GetSnapshotsRequest
name|getSnapshotsRequest
parameter_list|(
name|String
name|repository
parameter_list|)
block|{
return|return
operator|new
name|GetSnapshotsRequest
argument_list|(
name|repository
argument_list|)
return|;
block|}
comment|/**      * Restores new snapshot      *      * @param repository repository name      * @param snapshot   snapshot name      * @return snapshot creation request      */
DECL|method|restoreSnapshotRequest
specifier|public
specifier|static
name|RestoreSnapshotRequest
name|restoreSnapshotRequest
parameter_list|(
name|String
name|repository
parameter_list|,
name|String
name|snapshot
parameter_list|)
block|{
return|return
operator|new
name|RestoreSnapshotRequest
argument_list|(
name|repository
argument_list|,
name|snapshot
argument_list|)
return|;
block|}
comment|/**      * Deletes a snapshot      *      * @param snapshot   snapshot name      * @param repository repository name      * @return delete snapshot request      */
DECL|method|deleteSnapshotRequest
specifier|public
specifier|static
name|DeleteSnapshotRequest
name|deleteSnapshotRequest
parameter_list|(
name|String
name|repository
parameter_list|,
name|String
name|snapshot
parameter_list|)
block|{
return|return
operator|new
name|DeleteSnapshotRequest
argument_list|(
name|repository
argument_list|,
name|snapshot
argument_list|)
return|;
block|}
comment|/**      *  Get status of snapshots      *      * @param repository repository name      * @return snapshot status request      */
DECL|method|snapshotsStatusRequest
specifier|public
specifier|static
name|SnapshotsStatusRequest
name|snapshotsStatusRequest
parameter_list|(
name|String
name|repository
parameter_list|)
block|{
return|return
operator|new
name|SnapshotsStatusRequest
argument_list|(
name|repository
argument_list|)
return|;
block|}
block|}
end_class

end_unit

