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
name|*
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
name|health
operator|.
name|ClusterHealthRequestBuilder
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
name|health
operator|.
name|ClusterHealthResponse
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
name|hotthreads
operator|.
name|NodesHotThreadsRequest
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
name|hotthreads
operator|.
name|NodesHotThreadsRequestBuilder
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
name|hotthreads
operator|.
name|NodesHotThreadsResponse
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
name|info
operator|.
name|NodesInfoRequestBuilder
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
name|NodesInfoResponse
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
name|stats
operator|.
name|NodesStatsRequestBuilder
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
name|NodesStatsResponse
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
name|delete
operator|.
name|DeleteRepositoryRequestBuilder
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
name|DeleteRepositoryResponse
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
name|get
operator|.
name|GetRepositoriesRequestBuilder
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
name|GetRepositoriesResponse
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
name|put
operator|.
name|PutRepositoryRequestBuilder
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
name|PutRepositoryResponse
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
name|repositories
operator|.
name|verify
operator|.
name|VerifyRepositoryRequestBuilder
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
name|VerifyRepositoryResponse
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
name|reroute
operator|.
name|ClusterRerouteRequestBuilder
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
name|ClusterRerouteResponse
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
name|settings
operator|.
name|ClusterUpdateSettingsRequestBuilder
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
name|ClusterUpdateSettingsResponse
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
name|shards
operator|.
name|ClusterSearchShardsRequestBuilder
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
name|ClusterSearchShardsResponse
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
name|create
operator|.
name|CreateSnapshotRequestBuilder
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
name|CreateSnapshotResponse
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
name|delete
operator|.
name|DeleteSnapshotRequestBuilder
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
name|DeleteSnapshotResponse
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
name|get
operator|.
name|GetSnapshotsRequestBuilder
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
name|GetSnapshotsResponse
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
name|restore
operator|.
name|RestoreSnapshotRequestBuilder
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
name|RestoreSnapshotResponse
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
name|snapshots
operator|.
name|status
operator|.
name|SnapshotsStatusRequestBuilder
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
name|SnapshotsStatusResponse
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
name|state
operator|.
name|ClusterStateRequestBuilder
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
name|ClusterStateResponse
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
name|cluster
operator|.
name|stats
operator|.
name|ClusterStatsRequestBuilder
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
name|ClusterStatsResponse
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
name|tasks
operator|.
name|PendingClusterTasksRequest
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
name|tasks
operator|.
name|PendingClusterTasksRequestBuilder
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
name|tasks
operator|.
name|PendingClusterTasksResponse
import|;
end_import

begin_comment
comment|/**  * Administrative actions/operations against indices.  *  * @see AdminClient#cluster()  */
end_comment

begin_interface
DECL|interface|ClusterAdminClient
specifier|public
interface|interface
name|ClusterAdminClient
extends|extends
name|ElasticsearchClient
block|{
comment|/**      * The health of the cluster.      *      * @param request The cluster state request      * @return The result future      * @see Requests#clusterHealthRequest(String...)      */
DECL|method|health
name|ActionFuture
argument_list|<
name|ClusterHealthResponse
argument_list|>
name|health
parameter_list|(
name|ClusterHealthRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * The health of the cluster.      *      * @param request  The cluster state request      * @param listener A listener to be notified with a result      * @see Requests#clusterHealthRequest(String...)      */
DECL|method|health
name|void
name|health
parameter_list|(
name|ClusterHealthRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ClusterHealthResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * The health of the cluster.      */
DECL|method|prepareHealth
name|ClusterHealthRequestBuilder
name|prepareHealth
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
function_decl|;
comment|/**      * The state of the cluster.      *      * @param request The cluster state request.      * @return The result future      * @see Requests#clusterStateRequest()      */
DECL|method|state
name|ActionFuture
argument_list|<
name|ClusterStateResponse
argument_list|>
name|state
parameter_list|(
name|ClusterStateRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * The state of the cluster.      *      * @param request  The cluster state request.      * @param listener A listener to be notified with a result      * @see Requests#clusterStateRequest()      */
DECL|method|state
name|void
name|state
parameter_list|(
name|ClusterStateRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ClusterStateResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * The state of the cluster.      */
DECL|method|prepareState
name|ClusterStateRequestBuilder
name|prepareState
parameter_list|()
function_decl|;
comment|/**      * Updates settings in the cluster.      */
DECL|method|updateSettings
name|ActionFuture
argument_list|<
name|ClusterUpdateSettingsResponse
argument_list|>
name|updateSettings
parameter_list|(
name|ClusterUpdateSettingsRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Update settings in the cluster.      */
DECL|method|updateSettings
name|void
name|updateSettings
parameter_list|(
name|ClusterUpdateSettingsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ClusterUpdateSettingsResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Update settings in the cluster.      */
DECL|method|prepareUpdateSettings
name|ClusterUpdateSettingsRequestBuilder
name|prepareUpdateSettings
parameter_list|()
function_decl|;
comment|/**      * Reroutes allocation of shards. Advance API.      */
DECL|method|reroute
name|ActionFuture
argument_list|<
name|ClusterRerouteResponse
argument_list|>
name|reroute
parameter_list|(
name|ClusterRerouteRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Reroutes allocation of shards. Advance API.      */
DECL|method|reroute
name|void
name|reroute
parameter_list|(
name|ClusterRerouteRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ClusterRerouteResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Update settings in the cluster.      */
DECL|method|prepareReroute
name|ClusterRerouteRequestBuilder
name|prepareReroute
parameter_list|()
function_decl|;
comment|/**      * Nodes info of the cluster.      *      * @param request The nodes info request      * @return The result future      * @see org.elasticsearch.client.Requests#nodesInfoRequest(String...)      */
DECL|method|nodesInfo
name|ActionFuture
argument_list|<
name|NodesInfoResponse
argument_list|>
name|nodesInfo
parameter_list|(
name|NodesInfoRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Nodes info of the cluster.      *      * @param request  The nodes info request      * @param listener A listener to be notified with a result      * @see org.elasticsearch.client.Requests#nodesInfoRequest(String...)      */
DECL|method|nodesInfo
name|void
name|nodesInfo
parameter_list|(
name|NodesInfoRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|NodesInfoResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Nodes info of the cluster.      */
DECL|method|prepareNodesInfo
name|NodesInfoRequestBuilder
name|prepareNodesInfo
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
function_decl|;
comment|/**      * Cluster wide aggregated stats.      *      * @param request The cluster stats request      * @return The result future      * @see org.elasticsearch.client.Requests#clusterStatsRequest      */
DECL|method|clusterStats
name|ActionFuture
argument_list|<
name|ClusterStatsResponse
argument_list|>
name|clusterStats
parameter_list|(
name|ClusterStatsRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Cluster wide aggregated stats      *      * @param request  The cluster stats request      * @param listener A listener to be notified with a result      * @see org.elasticsearch.client.Requests#clusterStatsRequest()      */
DECL|method|clusterStats
name|void
name|clusterStats
parameter_list|(
name|ClusterStatsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ClusterStatsResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
DECL|method|prepareClusterStats
name|ClusterStatsRequestBuilder
name|prepareClusterStats
parameter_list|()
function_decl|;
comment|/**      * Nodes stats of the cluster.      *      * @param request The nodes stats request      * @return The result future      * @see org.elasticsearch.client.Requests#nodesStatsRequest(String...)      */
DECL|method|nodesStats
name|ActionFuture
argument_list|<
name|NodesStatsResponse
argument_list|>
name|nodesStats
parameter_list|(
name|NodesStatsRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Nodes stats of the cluster.      *      * @param request  The nodes info request      * @param listener A listener to be notified with a result      * @see org.elasticsearch.client.Requests#nodesStatsRequest(String...)      */
DECL|method|nodesStats
name|void
name|nodesStats
parameter_list|(
name|NodesStatsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|NodesStatsResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Nodes stats of the cluster.      */
DECL|method|prepareNodesStats
name|NodesStatsRequestBuilder
name|prepareNodesStats
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
function_decl|;
comment|/**      * Returns top N hot-threads samples per node. The hot-threads are only sampled      * for the node ids specified in the request.      */
DECL|method|nodesHotThreads
name|ActionFuture
argument_list|<
name|NodesHotThreadsResponse
argument_list|>
name|nodesHotThreads
parameter_list|(
name|NodesHotThreadsRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Returns top N hot-threads samples per node. The hot-threads are only sampled      * for the node ids specified in the request.      */
DECL|method|nodesHotThreads
name|void
name|nodesHotThreads
parameter_list|(
name|NodesHotThreadsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|NodesHotThreadsResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Returns a request builder to fetch top N hot-threads samples per node. The hot-threads are only sampled      * for the node ids provided. Note: Use<tt>*</tt> to fetch samples for all nodes      */
DECL|method|prepareNodesHotThreads
name|NodesHotThreadsRequestBuilder
name|prepareNodesHotThreads
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
function_decl|;
comment|/**      * Returns list of shards the given search would be executed on.      */
DECL|method|searchShards
name|ActionFuture
argument_list|<
name|ClusterSearchShardsResponse
argument_list|>
name|searchShards
parameter_list|(
name|ClusterSearchShardsRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Returns list of shards the given search would be executed on.      */
DECL|method|searchShards
name|void
name|searchShards
parameter_list|(
name|ClusterSearchShardsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ClusterSearchShardsResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Returns list of shards the given search would be executed on.      */
DECL|method|prepareSearchShards
name|ClusterSearchShardsRequestBuilder
name|prepareSearchShards
parameter_list|()
function_decl|;
comment|/**      * Returns list of shards the given search would be executed on.      */
DECL|method|prepareSearchShards
name|ClusterSearchShardsRequestBuilder
name|prepareSearchShards
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
function_decl|;
comment|/**      * Registers a snapshot repository.      */
DECL|method|putRepository
name|ActionFuture
argument_list|<
name|PutRepositoryResponse
argument_list|>
name|putRepository
parameter_list|(
name|PutRepositoryRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Registers a snapshot repository.      */
DECL|method|putRepository
name|void
name|putRepository
parameter_list|(
name|PutRepositoryRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|PutRepositoryResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Registers a snapshot repository.      */
DECL|method|preparePutRepository
name|PutRepositoryRequestBuilder
name|preparePutRepository
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Unregisters a repository.      */
DECL|method|deleteRepository
name|ActionFuture
argument_list|<
name|DeleteRepositoryResponse
argument_list|>
name|deleteRepository
parameter_list|(
name|DeleteRepositoryRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Unregisters a repository.      */
DECL|method|deleteRepository
name|void
name|deleteRepository
parameter_list|(
name|DeleteRepositoryRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteRepositoryResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Unregisters a repository.      */
DECL|method|prepareDeleteRepository
name|DeleteRepositoryRequestBuilder
name|prepareDeleteRepository
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Gets repositories.      */
DECL|method|getRepositories
name|ActionFuture
argument_list|<
name|GetRepositoriesResponse
argument_list|>
name|getRepositories
parameter_list|(
name|GetRepositoriesRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Gets repositories.      */
DECL|method|getRepositories
name|void
name|getRepositories
parameter_list|(
name|GetRepositoriesRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|GetRepositoriesResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Gets repositories.      */
DECL|method|prepareGetRepositories
name|GetRepositoriesRequestBuilder
name|prepareGetRepositories
parameter_list|(
name|String
modifier|...
name|name
parameter_list|)
function_decl|;
comment|/**      * Verifies a repository.      */
DECL|method|verifyRepository
name|ActionFuture
argument_list|<
name|VerifyRepositoryResponse
argument_list|>
name|verifyRepository
parameter_list|(
name|VerifyRepositoryRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Verifies a repository.      */
DECL|method|verifyRepository
name|void
name|verifyRepository
parameter_list|(
name|VerifyRepositoryRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|VerifyRepositoryResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Verifies a repository.      */
DECL|method|prepareVerifyRepository
name|VerifyRepositoryRequestBuilder
name|prepareVerifyRepository
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Creates a new snapshot.      */
DECL|method|createSnapshot
name|ActionFuture
argument_list|<
name|CreateSnapshotResponse
argument_list|>
name|createSnapshot
parameter_list|(
name|CreateSnapshotRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Creates a new snapshot.      */
DECL|method|createSnapshot
name|void
name|createSnapshot
parameter_list|(
name|CreateSnapshotRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|CreateSnapshotResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Creates a new snapshot.      */
DECL|method|prepareCreateSnapshot
name|CreateSnapshotRequestBuilder
name|prepareCreateSnapshot
parameter_list|(
name|String
name|repository
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Get snapshot.      */
DECL|method|getSnapshots
name|ActionFuture
argument_list|<
name|GetSnapshotsResponse
argument_list|>
name|getSnapshots
parameter_list|(
name|GetSnapshotsRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Get snapshot.      */
DECL|method|getSnapshots
name|void
name|getSnapshots
parameter_list|(
name|GetSnapshotsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|GetSnapshotsResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Get snapshot.      */
DECL|method|prepareGetSnapshots
name|GetSnapshotsRequestBuilder
name|prepareGetSnapshots
parameter_list|(
name|String
name|repository
parameter_list|)
function_decl|;
comment|/**      * Delete snapshot.      */
DECL|method|deleteSnapshot
name|ActionFuture
argument_list|<
name|DeleteSnapshotResponse
argument_list|>
name|deleteSnapshot
parameter_list|(
name|DeleteSnapshotRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Delete snapshot.      */
DECL|method|deleteSnapshot
name|void
name|deleteSnapshot
parameter_list|(
name|DeleteSnapshotRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteSnapshotResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Delete snapshot.      */
DECL|method|prepareDeleteSnapshot
name|DeleteSnapshotRequestBuilder
name|prepareDeleteSnapshot
parameter_list|(
name|String
name|repository
parameter_list|,
name|String
name|snapshot
parameter_list|)
function_decl|;
comment|/**      * Restores a snapshot.      */
DECL|method|restoreSnapshot
name|ActionFuture
argument_list|<
name|RestoreSnapshotResponse
argument_list|>
name|restoreSnapshot
parameter_list|(
name|RestoreSnapshotRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Restores a snapshot.      */
DECL|method|restoreSnapshot
name|void
name|restoreSnapshot
parameter_list|(
name|RestoreSnapshotRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|RestoreSnapshotResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Restores a snapshot.      */
DECL|method|prepareRestoreSnapshot
name|RestoreSnapshotRequestBuilder
name|prepareRestoreSnapshot
parameter_list|(
name|String
name|repository
parameter_list|,
name|String
name|snapshot
parameter_list|)
function_decl|;
comment|/**      * Returns a list of the pending cluster tasks, that are scheduled to be executed. This includes operations      * that update the cluster state (for example, a create index operation)      */
DECL|method|pendingClusterTasks
name|void
name|pendingClusterTasks
parameter_list|(
name|PendingClusterTasksRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|PendingClusterTasksResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Returns a list of the pending cluster tasks, that are scheduled to be executed. This includes operations      * that update the cluster state (for example, a create index operation)      */
DECL|method|pendingClusterTasks
name|ActionFuture
argument_list|<
name|PendingClusterTasksResponse
argument_list|>
name|pendingClusterTasks
parameter_list|(
name|PendingClusterTasksRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Returns a list of the pending cluster tasks, that are scheduled to be executed. This includes operations      * that update the cluster state (for example, a create index operation)      */
DECL|method|preparePendingClusterTasks
name|PendingClusterTasksRequestBuilder
name|preparePendingClusterTasks
parameter_list|()
function_decl|;
comment|/**      * Get snapshot status.      */
DECL|method|snapshotsStatus
name|ActionFuture
argument_list|<
name|SnapshotsStatusResponse
argument_list|>
name|snapshotsStatus
parameter_list|(
name|SnapshotsStatusRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Get snapshot status.      */
DECL|method|snapshotsStatus
name|void
name|snapshotsStatus
parameter_list|(
name|SnapshotsStatusRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|SnapshotsStatusResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Get snapshot status.      */
DECL|method|prepareSnapshotStatus
name|SnapshotsStatusRequestBuilder
name|prepareSnapshotStatus
parameter_list|(
name|String
name|repository
parameter_list|)
function_decl|;
comment|/**      * Get snapshot status.      */
DECL|method|prepareSnapshotStatus
name|SnapshotsStatusRequestBuilder
name|prepareSnapshotStatus
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

