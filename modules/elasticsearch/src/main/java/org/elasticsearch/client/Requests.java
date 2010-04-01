begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|shutdown
operator|.
name|NodesShutdownRequest
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
name|ping
operator|.
name|broadcast
operator|.
name|BroadcastPingRequest
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
name|ping
operator|.
name|replication
operator|.
name|ReplicationPingRequest
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
name|ping
operator|.
name|single
operator|.
name|SinglePingRequest
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
name|gateway
operator|.
name|snapshot
operator|.
name|GatewaySnapshotRequest
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
name|optimize
operator|.
name|OptimizeRequest
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
name|status
operator|.
name|IndicesStatusRequest
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
name|count
operator|.
name|CountRequest
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
name|deletebyquery
operator|.
name|DeleteByQueryRequest
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
name|mlt
operator|.
name|MoreLikeThisRequest
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
name|terms
operator|.
name|TermsRequest
import|;
end_import

begin_comment
comment|/**  * A handy one stop shop for creating requests (make sure to import static this class).  *  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|Requests
specifier|public
class|class
name|Requests
block|{
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
comment|/**      * Creates a delete by query request. Note, the query itself must be set either by setting the JSON source      * of the query, or by using a {@link org.elasticsearch.index.query.QueryBuilder} (using {@link org.elasticsearch.index.query.json.JsonQueryBuilders}).      *      * @param indices The indices the delete by query against. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The delete by query request      * @see org.elasticsearch.client.Client#deleteByQuery(org.elasticsearch.action.deletebyquery.DeleteByQueryRequest)      */
DECL|method|deleteByQueryRequest
specifier|public
specifier|static
name|DeleteByQueryRequest
name|deleteByQueryRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|DeleteByQueryRequest
argument_list|(
name|indices
argument_list|)
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
comment|/**      * Creates a count request which counts the hits matched against a query. Note, the query itself must be set      * either using the JSON source of the query, or using a {@link org.elasticsearch.index.query.QueryBuilder} (using {@link org.elasticsearch.index.query.json.JsonQueryBuilders}).      *      * @param indices The indices the delete by query against. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The count request      * @see org.elasticsearch.client.Client#count(org.elasticsearch.action.count.CountRequest)      */
DECL|method|countRequest
specifier|public
specifier|static
name|CountRequest
name|countRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|CountRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Terms request represent a request to get terms in one or more indices of specific fields and their      * document frequencies (in how many document each term exists).      *      * @param indices The indices the delete by query against. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The terms request      * @see org.elasticsearch.client.Client#terms(org.elasticsearch.action.terms.TermsRequest)      */
DECL|method|termsRequest
specifier|public
specifier|static
name|TermsRequest
name|termsRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|TermsRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * More like this request represents a request to search for documents that are "like" the provided (fetched)      * document.      *      * @param index The index to load the document from      * @return The more like this request      * @see org.elasticsearch.client.Client#moreLikeThis(org.elasticsearch.action.mlt.MoreLikeThisRequest)      */
DECL|method|moreLikeThisRequest
specifier|public
specifier|static
name|MoreLikeThisRequest
name|moreLikeThisRequest
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|MoreLikeThisRequest
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**      * Creates a search request against one or more indices. Note, the search source must be set either using the      * actual JSON search source, or the {@link org.elasticsearch.search.builder.SearchSourceBuilder}.      *      * @param indices The indices the delete by query against. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The search request      * @see org.elasticsearch.client.Client#search(org.elasticsearch.action.search.SearchRequest)      */
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
comment|/**      * Creates an indices status request.      *      * @param indices The indices the delete by query against. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The indices status request      * @see org.elasticsearch.client.IndicesAdminClient#status(org.elasticsearch.action.admin.indices.status.IndicesStatusRequest)      */
DECL|method|indicesStatus
specifier|public
specifier|static
name|IndicesStatusRequest
name|indicesStatus
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|IndicesStatusRequest
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
comment|/**      * Create a create mapping request against one or more indices.      *      * @param indices The indices the delete by query against. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The create mapping request      * @see org.elasticsearch.client.IndicesAdminClient#putMapping(org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest)      */
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
comment|/**      * Creates a refresh indices request.      *      * @param indices The indices the delete by query against. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The refresh request      * @see org.elasticsearch.client.IndicesAdminClient#refresh(org.elasticsearch.action.admin.indices.refresh.RefreshRequest)      */
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
comment|/**      * Creates a flush indices request.      *      * @param indices The indices the delete by query against. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The flush request      * @see org.elasticsearch.client.IndicesAdminClient#flush(org.elasticsearch.action.admin.indices.flush.FlushRequest)      */
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
comment|/**      * Creates an optimize request.      *      * @param indices The indices to optimize. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The optimize request      * @see org.elasticsearch.client.IndicesAdminClient#optimize(org.elasticsearch.action.admin.indices.optimize.OptimizeRequest)      */
DECL|method|optimizeRequest
specifier|public
specifier|static
name|OptimizeRequest
name|optimizeRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|OptimizeRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates a gateway snapshot indices request.      *      * @param indices The indices the gateway wil be performed ont. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The gateway snapshot request      * @see org.elasticsearch.client.IndicesAdminClient#gatewaySnapshot(org.elasticsearch.action.admin.indices.gateway.snapshot.GatewaySnapshotRequest)      */
DECL|method|gatewaySnapshotRequest
specifier|public
specifier|static
name|GatewaySnapshotRequest
name|gatewaySnapshotRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|GatewaySnapshotRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
comment|/**      * Creates a clean indices cache request.      *      * @param indices The indices the gateway wil be performed ont. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The request      */
DECL|method|clearIndicesCache
specifier|public
specifier|static
name|ClearIndicesCacheRequest
name|clearIndicesCache
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
comment|/**      * Creates a cluster state request.      *      * @return The cluster state request.      * @see org.elasticsearch.client.ClusterAdminClient#state(org.elasticsearch.action.admin.cluster.state.ClusterStateRequest)      */
DECL|method|clusterState
specifier|public
specifier|static
name|ClusterStateRequest
name|clusterState
parameter_list|()
block|{
return|return
operator|new
name|ClusterStateRequest
argument_list|()
return|;
block|}
comment|/**      * Creates a cluster health request.      *      * @param indices The indices to optimize. Use<tt>null</tt> or<tt>_all</tt> to execute against all indices      * @return The cluster health request      * @see org.elasticsearch.client.ClusterAdminClient#health(org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest)      */
DECL|method|clusterHealth
specifier|public
specifier|static
name|ClusterHealthRequest
name|clusterHealth
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
comment|/**      * Creates a nodes info request against all the nodes.      *      * @return The nodes info request      * @see org.elasticsearch.client.ClusterAdminClient#nodesInfo(org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest)      */
DECL|method|nodesInfo
specifier|public
specifier|static
name|NodesInfoRequest
name|nodesInfo
parameter_list|()
block|{
return|return
operator|new
name|NodesInfoRequest
argument_list|()
return|;
block|}
comment|/**      * Creates a nodes info request against one or more nodes. Pass<tt>null</tt> or an empty array for all nodes.      *      * @param nodesIds The nodes ids to get the status for      * @return The nodes info request      * @see org.elasticsearch.client.ClusterAdminClient#nodesInfo(org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest)      */
DECL|method|nodesInfo
specifier|public
specifier|static
name|NodesInfoRequest
name|nodesInfo
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
comment|/**      * Shuts down all nodes in the cluster.      */
DECL|method|nodesShutdown
specifier|public
specifier|static
name|NodesShutdownRequest
name|nodesShutdown
parameter_list|()
block|{
return|return
operator|new
name|NodesShutdownRequest
argument_list|()
return|;
block|}
comment|/**      * Shuts down the specified nodes in the cluster.      *      * @param nodesIds The nodes ids to get the status for      * @return The nodes info request      * @see org.elasticsearch.client.ClusterAdminClient#nodesShutdown(org.elasticsearch.action.admin.cluster.node.shutdown.NodesShutdownRequest)      */
DECL|method|nodesShutdown
specifier|public
specifier|static
name|NodesShutdownRequest
name|nodesShutdown
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
block|{
return|return
operator|new
name|NodesShutdownRequest
argument_list|(
name|nodesIds
argument_list|)
return|;
block|}
DECL|method|pingSingleRequest
specifier|public
specifier|static
name|SinglePingRequest
name|pingSingleRequest
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|SinglePingRequest
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|method|pingBroadcastRequest
specifier|public
specifier|static
name|BroadcastPingRequest
name|pingBroadcastRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|BroadcastPingRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
DECL|method|pingReplicationRequest
specifier|public
specifier|static
name|ReplicationPingRequest
name|pingReplicationRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|ReplicationPingRequest
argument_list|(
name|indices
argument_list|)
return|;
block|}
block|}
end_class

end_unit

