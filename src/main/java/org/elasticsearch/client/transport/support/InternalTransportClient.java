begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.transport.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|transport
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
name|ElasticSearchException
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
name|ActionFuture
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
name|ActionListener
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
name|bulk
operator|.
name|BulkResponse
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
name|count
operator|.
name|CountResponse
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
name|delete
operator|.
name|DeleteResponse
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
name|deletebyquery
operator|.
name|DeleteByQueryResponse
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
name|get
operator|.
name|GetResponse
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
name|MultiGetRequest
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
name|MultiGetResponse
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
name|index
operator|.
name|IndexResponse
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
name|percolate
operator|.
name|PercolateRequest
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
name|percolate
operator|.
name|PercolateResponse
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
name|SearchResponse
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
name|update
operator|.
name|UpdateRequest
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
name|update
operator|.
name|UpdateResponse
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
name|AdminClient
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
name|internal
operator|.
name|InternalClient
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
name|support
operator|.
name|AbstractClient
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
name|transport
operator|.
name|TransportClientNodesService
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
name|transport
operator|.
name|action
operator|.
name|bulk
operator|.
name|ClientTransportBulkAction
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
name|transport
operator|.
name|action
operator|.
name|count
operator|.
name|ClientTransportCountAction
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
name|transport
operator|.
name|action
operator|.
name|delete
operator|.
name|ClientTransportDeleteAction
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
name|transport
operator|.
name|action
operator|.
name|deletebyquery
operator|.
name|ClientTransportDeleteByQueryAction
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
name|transport
operator|.
name|action
operator|.
name|get
operator|.
name|ClientTransportGetAction
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
name|transport
operator|.
name|action
operator|.
name|get
operator|.
name|ClientTransportMultiGetAction
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
name|transport
operator|.
name|action
operator|.
name|index
operator|.
name|ClientTransportIndexAction
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
name|transport
operator|.
name|action
operator|.
name|mlt
operator|.
name|ClientTransportMoreLikeThisAction
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
name|transport
operator|.
name|action
operator|.
name|percolate
operator|.
name|ClientTransportPercolateAction
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
name|transport
operator|.
name|action
operator|.
name|search
operator|.
name|ClientTransportSearchAction
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
name|transport
operator|.
name|action
operator|.
name|search
operator|.
name|ClientTransportSearchScrollAction
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
name|transport
operator|.
name|action
operator|.
name|update
operator|.
name|ClientTransportUpdateAction
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
name|node
operator|.
name|DiscoveryNode
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
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|InternalTransportClient
specifier|public
class|class
name|InternalTransportClient
extends|extends
name|AbstractClient
implements|implements
name|InternalClient
block|{
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|nodesService
specifier|private
specifier|final
name|TransportClientNodesService
name|nodesService
decl_stmt|;
DECL|field|adminClient
specifier|private
specifier|final
name|InternalTransportAdminClient
name|adminClient
decl_stmt|;
DECL|field|indexAction
specifier|private
specifier|final
name|ClientTransportIndexAction
name|indexAction
decl_stmt|;
DECL|field|updateAction
specifier|private
specifier|final
name|ClientTransportUpdateAction
name|updateAction
decl_stmt|;
DECL|field|deleteAction
specifier|private
specifier|final
name|ClientTransportDeleteAction
name|deleteAction
decl_stmt|;
DECL|field|bulkAction
specifier|private
specifier|final
name|ClientTransportBulkAction
name|bulkAction
decl_stmt|;
DECL|field|getAction
specifier|private
specifier|final
name|ClientTransportGetAction
name|getAction
decl_stmt|;
DECL|field|multiGetAction
specifier|private
specifier|final
name|ClientTransportMultiGetAction
name|multiGetAction
decl_stmt|;
DECL|field|deleteByQueryAction
specifier|private
specifier|final
name|ClientTransportDeleteByQueryAction
name|deleteByQueryAction
decl_stmt|;
DECL|field|countAction
specifier|private
specifier|final
name|ClientTransportCountAction
name|countAction
decl_stmt|;
DECL|field|searchAction
specifier|private
specifier|final
name|ClientTransportSearchAction
name|searchAction
decl_stmt|;
DECL|field|searchScrollAction
specifier|private
specifier|final
name|ClientTransportSearchScrollAction
name|searchScrollAction
decl_stmt|;
DECL|field|moreLikeThisAction
specifier|private
specifier|final
name|ClientTransportMoreLikeThisAction
name|moreLikeThisAction
decl_stmt|;
DECL|field|percolateAction
specifier|private
specifier|final
name|ClientTransportPercolateAction
name|percolateAction
decl_stmt|;
annotation|@
name|Inject
DECL|method|InternalTransportClient
specifier|public
name|InternalTransportClient
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|TransportClientNodesService
name|nodesService
parameter_list|,
name|InternalTransportAdminClient
name|adminClient
parameter_list|,
name|ClientTransportIndexAction
name|indexAction
parameter_list|,
name|ClientTransportUpdateAction
name|updateAction
parameter_list|,
name|ClientTransportDeleteAction
name|deleteAction
parameter_list|,
name|ClientTransportBulkAction
name|bulkAction
parameter_list|,
name|ClientTransportGetAction
name|getAction
parameter_list|,
name|ClientTransportMultiGetAction
name|multiGetAction
parameter_list|,
name|ClientTransportDeleteByQueryAction
name|deleteByQueryAction
parameter_list|,
name|ClientTransportCountAction
name|countAction
parameter_list|,
name|ClientTransportSearchAction
name|searchAction
parameter_list|,
name|ClientTransportSearchScrollAction
name|searchScrollAction
parameter_list|,
name|ClientTransportMoreLikeThisAction
name|moreLikeThisAction
parameter_list|,
name|ClientTransportPercolateAction
name|percolateAction
parameter_list|)
block|{
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|nodesService
operator|=
name|nodesService
expr_stmt|;
name|this
operator|.
name|adminClient
operator|=
name|adminClient
expr_stmt|;
name|this
operator|.
name|indexAction
operator|=
name|indexAction
expr_stmt|;
name|this
operator|.
name|updateAction
operator|=
name|updateAction
expr_stmt|;
name|this
operator|.
name|deleteAction
operator|=
name|deleteAction
expr_stmt|;
name|this
operator|.
name|bulkAction
operator|=
name|bulkAction
expr_stmt|;
name|this
operator|.
name|getAction
operator|=
name|getAction
expr_stmt|;
name|this
operator|.
name|multiGetAction
operator|=
name|multiGetAction
expr_stmt|;
name|this
operator|.
name|deleteByQueryAction
operator|=
name|deleteByQueryAction
expr_stmt|;
name|this
operator|.
name|countAction
operator|=
name|countAction
expr_stmt|;
name|this
operator|.
name|searchAction
operator|=
name|searchAction
expr_stmt|;
name|this
operator|.
name|searchScrollAction
operator|=
name|searchScrollAction
expr_stmt|;
name|this
operator|.
name|moreLikeThisAction
operator|=
name|moreLikeThisAction
expr_stmt|;
name|this
operator|.
name|percolateAction
operator|=
name|percolateAction
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// nothing to do here
block|}
annotation|@
name|Override
DECL|method|threadPool
specifier|public
name|ThreadPool
name|threadPool
parameter_list|()
block|{
return|return
name|this
operator|.
name|threadPool
return|;
block|}
annotation|@
name|Override
DECL|method|admin
specifier|public
name|AdminClient
name|admin
parameter_list|()
block|{
return|return
name|adminClient
return|;
block|}
annotation|@
name|Override
DECL|method|index
specifier|public
name|ActionFuture
argument_list|<
name|IndexResponse
argument_list|>
name|index
parameter_list|(
specifier|final
name|IndexRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|IndexResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|IndexResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|indexAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|index
specifier|public
name|void
name|index
parameter_list|(
specifier|final
name|IndexRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeListenerCallback
argument_list|<
name|IndexResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|indexAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|update
specifier|public
name|ActionFuture
argument_list|<
name|UpdateResponse
argument_list|>
name|update
parameter_list|(
specifier|final
name|UpdateRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|UpdateResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|UpdateResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|updateAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
specifier|final
name|UpdateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|UpdateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeListenerCallback
argument_list|<
name|UpdateResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ActionListener
argument_list|<
name|UpdateResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|updateAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|ActionFuture
argument_list|<
name|DeleteResponse
argument_list|>
name|delete
parameter_list|(
specifier|final
name|DeleteRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|DeleteResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|DeleteResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|deleteAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
specifier|final
name|DeleteRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|DeleteResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeListenerCallback
argument_list|<
name|DeleteResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|deleteAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bulk
specifier|public
name|ActionFuture
argument_list|<
name|BulkResponse
argument_list|>
name|bulk
parameter_list|(
specifier|final
name|BulkRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|BulkResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|BulkResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|bulkAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bulk
specifier|public
name|void
name|bulk
parameter_list|(
specifier|final
name|BulkRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeListenerCallback
argument_list|<
name|BulkResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|bulkAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteByQuery
specifier|public
name|ActionFuture
argument_list|<
name|DeleteByQueryResponse
argument_list|>
name|deleteByQuery
parameter_list|(
specifier|final
name|DeleteByQueryRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|DeleteByQueryResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|DeleteByQueryResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|deleteByQueryAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteByQuery
specifier|public
name|void
name|deleteByQuery
parameter_list|(
specifier|final
name|DeleteByQueryRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|DeleteByQueryResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeListenerCallback
argument_list|<
name|DeleteByQueryResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteByQueryResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|deleteByQueryAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|ActionFuture
argument_list|<
name|GetResponse
argument_list|>
name|get
parameter_list|(
specifier|final
name|GetRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|GetResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|GetResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|getAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|void
name|get
parameter_list|(
specifier|final
name|GetRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|GetResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeListenerCallback
argument_list|<
name|GetResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ActionListener
argument_list|<
name|GetResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|getAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|multiGet
specifier|public
name|ActionFuture
argument_list|<
name|MultiGetResponse
argument_list|>
name|multiGet
parameter_list|(
specifier|final
name|MultiGetRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|MultiGetResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|MultiGetResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|multiGetAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|multiGet
specifier|public
name|void
name|multiGet
parameter_list|(
specifier|final
name|MultiGetRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|MultiGetResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeListenerCallback
argument_list|<
name|MultiGetResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ActionListener
argument_list|<
name|MultiGetResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|multiGetAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|ActionFuture
argument_list|<
name|CountResponse
argument_list|>
name|count
parameter_list|(
specifier|final
name|CountRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|CountResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|CountResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|countAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|void
name|count
parameter_list|(
specifier|final
name|CountRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|CountResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeListenerCallback
argument_list|<
name|CountResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ActionListener
argument_list|<
name|CountResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|countAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|search
parameter_list|(
specifier|final
name|SearchRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|searchAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
specifier|final
name|SearchRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeListenerCallback
argument_list|<
name|SearchResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|searchAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|searchScroll
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|searchScroll
parameter_list|(
specifier|final
name|SearchScrollRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|searchScrollAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|searchScroll
specifier|public
name|void
name|searchScroll
parameter_list|(
specifier|final
name|SearchScrollRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeListenerCallback
argument_list|<
name|SearchResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|searchScrollAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|moreLikeThis
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|moreLikeThis
parameter_list|(
specifier|final
name|MoreLikeThisRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|moreLikeThisAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|moreLikeThis
specifier|public
name|void
name|moreLikeThis
parameter_list|(
specifier|final
name|MoreLikeThisRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeListenerCallback
argument_list|<
name|SearchResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|moreLikeThisAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|percolate
specifier|public
name|ActionFuture
argument_list|<
name|PercolateResponse
argument_list|>
name|percolate
parameter_list|(
specifier|final
name|PercolateRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|PercolateResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|PercolateResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|percolateAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|percolate
specifier|public
name|void
name|percolate
parameter_list|(
specifier|final
name|PercolateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|PercolateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeListenerCallback
argument_list|<
name|PercolateResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ActionListener
argument_list|<
name|PercolateResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|percolateAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

