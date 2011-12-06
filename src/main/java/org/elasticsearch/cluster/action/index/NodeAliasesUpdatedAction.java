begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.action.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|action
operator|.
name|index
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
name|node
operator|.
name|DiscoveryNodes
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
name|component
operator|.
name|AbstractComponent
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|io
operator|.
name|stream
operator|.
name|VoidStreamable
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
name|unit
operator|.
name|TimeValue
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|BaseTransportRequestHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|VoidTransportResponseHandler
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|NodeAliasesUpdatedAction
specifier|public
class|class
name|NodeAliasesUpdatedAction
extends|extends
name|AbstractComponent
block|{
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|listeners
specifier|private
specifier|final
name|List
argument_list|<
name|Listener
argument_list|>
name|listeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|Listener
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|NodeAliasesUpdatedAction
specifier|public
name|NodeAliasesUpdatedAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|NodeAliasesUpdatedTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|NodeAliasesUpdatedTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|Listener
name|listener
parameter_list|,
name|TimeValue
name|timeout
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|schedule
argument_list|(
name|timeout
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|CACHED
argument_list|,
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|boolean
name|removed
init|=
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed
condition|)
block|{
name|listener
operator|.
name|onTimeout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|nodeAliasesUpdated
specifier|public
name|void
name|nodeAliasesUpdated
parameter_list|(
specifier|final
name|NodeAliasesUpdatedResponse
name|response
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|DiscoveryNodes
name|nodes
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|localNodeMaster
argument_list|()
condition|)
block|{
name|threadPool
operator|.
name|cached
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|innerNodeAliasesUpdated
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
argument_list|,
name|NodeAliasesUpdatedTransportHandler
operator|.
name|ACTION
argument_list|,
name|response
argument_list|,
name|VoidTransportResponseHandler
operator|.
name|INSTANCE_SAME
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|innerNodeAliasesUpdated
specifier|private
name|void
name|innerNodeAliasesUpdated
parameter_list|(
name|NodeAliasesUpdatedResponse
name|response
parameter_list|)
block|{
for|for
control|(
name|Listener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|onAliasesUpdated
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
DECL|interface|Listener
specifier|public
specifier|static
interface|interface
name|Listener
block|{
DECL|method|onAliasesUpdated
name|void
name|onAliasesUpdated
parameter_list|(
name|NodeAliasesUpdatedResponse
name|response
parameter_list|)
function_decl|;
DECL|method|onTimeout
name|void
name|onTimeout
parameter_list|()
function_decl|;
block|}
DECL|class|NodeAliasesUpdatedTransportHandler
specifier|private
class|class
name|NodeAliasesUpdatedTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|NodeAliasesUpdatedResponse
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"cluster/nodeAliasesUpdated"
decl_stmt|;
annotation|@
name|Override
DECL|method|newInstance
specifier|public
name|NodeAliasesUpdatedResponse
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|NodeAliasesUpdatedResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|NodeAliasesUpdatedResponse
name|response
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|innerNodeAliasesUpdated
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|VoidStreamable
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|public
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
return|;
block|}
block|}
DECL|class|NodeAliasesUpdatedResponse
specifier|public
specifier|static
class|class
name|NodeAliasesUpdatedResponse
implements|implements
name|Streamable
block|{
DECL|field|nodeId
specifier|private
name|String
name|nodeId
decl_stmt|;
DECL|field|version
specifier|private
name|long
name|version
decl_stmt|;
DECL|method|NodeAliasesUpdatedResponse
specifier|private
name|NodeAliasesUpdatedResponse
parameter_list|()
block|{         }
DECL|method|NodeAliasesUpdatedResponse
specifier|public
name|NodeAliasesUpdatedResponse
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|long
name|version
parameter_list|)
block|{
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
DECL|method|nodeId
specifier|public
name|String
name|nodeId
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|version
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|nodeId
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|version
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

