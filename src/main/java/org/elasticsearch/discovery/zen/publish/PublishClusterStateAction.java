begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen.publish
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|publish
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|ClusterState
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
name|compress
operator|.
name|Compressor
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
name|compress
operator|.
name|CompressorFactory
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
name|*
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
name|discovery
operator|.
name|AckClusterStatePublishResponseHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|ClusterStatePublishResponseHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|Discovery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|DiscoverySettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|DiscoveryNodesProvider
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|PublishClusterStateAction
specifier|public
class|class
name|PublishClusterStateAction
extends|extends
name|AbstractComponent
block|{
DECL|field|ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ACTION_NAME
init|=
literal|"internal:discovery/zen/publish"
decl_stmt|;
DECL|interface|NewClusterStateListener
specifier|public
specifier|static
interface|interface
name|NewClusterStateListener
block|{
DECL|interface|NewStateProcessed
specifier|static
interface|interface
name|NewStateProcessed
block|{
DECL|method|onNewClusterStateProcessed
name|void
name|onNewClusterStateProcessed
parameter_list|()
function_decl|;
DECL|method|onNewClusterStateFailed
name|void
name|onNewClusterStateFailed
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
DECL|method|onNewClusterState
name|void
name|onNewClusterState
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|NewStateProcessed
name|newStateProcessed
parameter_list|)
function_decl|;
block|}
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|nodesProvider
specifier|private
specifier|final
name|DiscoveryNodesProvider
name|nodesProvider
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|NewClusterStateListener
name|listener
decl_stmt|;
DECL|field|discoverySettings
specifier|private
specifier|final
name|DiscoverySettings
name|discoverySettings
decl_stmt|;
DECL|method|PublishClusterStateAction
specifier|public
name|PublishClusterStateAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|DiscoveryNodesProvider
name|nodesProvider
parameter_list|,
name|NewClusterStateListener
name|listener
parameter_list|,
name|DiscoverySettings
name|discoverySettings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|nodesProvider
operator|=
name|nodesProvider
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|discoverySettings
operator|=
name|discoverySettings
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|ACTION_NAME
argument_list|,
operator|new
name|PublishClusterStateRequestHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|transportService
operator|.
name|removeHandler
argument_list|(
name|ACTION_NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|publish
specifier|public
name|void
name|publish
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
specifier|final
name|Discovery
operator|.
name|AckListener
name|ackListener
parameter_list|)
block|{
name|publish
argument_list|(
name|clusterState
argument_list|,
operator|new
name|AckClusterStatePublishResponseHandler
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|ackListener
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|publish
specifier|private
name|void
name|publish
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
specifier|final
name|ClusterStatePublishResponseHandler
name|publishResponseHandler
parameter_list|)
block|{
name|DiscoveryNode
name|localNode
init|=
name|nodesProvider
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Version
argument_list|,
name|BytesReference
argument_list|>
name|serializedStates
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|DiscoveryNode
name|node
range|:
name|clusterState
operator|.
name|nodes
argument_list|()
control|)
block|{
if|if
condition|(
name|node
operator|.
name|equals
argument_list|(
name|localNode
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// try and serialize the cluster state once (or per version), so we don't serialize it
comment|// per node when we send it over the wire, compress it while we are at it...
name|BytesReference
name|bytes
init|=
name|serializedStates
operator|.
name|get
argument_list|(
name|node
operator|.
name|version
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|BytesStreamOutput
name|bStream
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|StreamOutput
name|stream
init|=
operator|new
name|HandlesStreamOutput
argument_list|(
name|CompressorFactory
operator|.
name|defaultCompressor
argument_list|()
operator|.
name|streamOutput
argument_list|(
name|bStream
argument_list|)
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setVersion
argument_list|(
name|node
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterState
operator|.
name|Builder
operator|.
name|writeTo
argument_list|(
name|clusterState
argument_list|,
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|bytes
operator|=
name|bStream
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|serializedStates
operator|.
name|put
argument_list|(
name|node
operator|.
name|version
argument_list|()
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to serialize cluster_state before publishing it to node {}"
argument_list|,
name|e
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|publishResponseHandler
operator|.
name|onFailure
argument_list|(
name|node
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
try|try
block|{
name|TransportRequestOptions
name|options
init|=
name|TransportRequestOptions
operator|.
name|options
argument_list|()
operator|.
name|withType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|STATE
argument_list|)
operator|.
name|withCompress
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// no need to put a timeout on the options here, because we want the response to eventually be received
comment|// and not log an error if it arrives after the timeout
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|ACTION_NAME
argument_list|,
operator|new
name|BytesTransportRequest
argument_list|(
name|bytes
argument_list|,
name|node
operator|.
name|version
argument_list|()
argument_list|)
argument_list|,
name|options
argument_list|,
comment|// no need to compress, we already compressed the bytes
operator|new
name|EmptyTransportResponseHandler
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|TransportResponse
operator|.
name|Empty
name|response
parameter_list|)
block|{
name|publishResponseHandler
operator|.
name|onResponse
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|TransportException
name|exp
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to send cluster state to [{}]"
argument_list|,
name|exp
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|publishResponseHandler
operator|.
name|onFailure
argument_list|(
name|node
argument_list|,
name|exp
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"error sending cluster state to [{}]"
argument_list|,
name|t
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|publishResponseHandler
operator|.
name|onFailure
argument_list|(
name|node
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
name|TimeValue
name|publishTimeout
init|=
name|discoverySettings
operator|.
name|getPublishTimeout
argument_list|()
decl_stmt|;
if|if
condition|(
name|publishTimeout
operator|.
name|millis
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// only wait if the publish timeout is configured...
try|try
block|{
name|boolean
name|awaited
init|=
name|publishResponseHandler
operator|.
name|awaitAllNodes
argument_list|(
name|publishTimeout
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|awaited
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"awaiting all nodes to process published state {} timed out, timeout {}"
argument_list|,
name|clusterState
operator|.
name|version
argument_list|()
argument_list|,
name|publishTimeout
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore& restore interrupt
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|PublishClusterStateRequestHandler
specifier|private
class|class
name|PublishClusterStateRequestHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|BytesTransportRequest
argument_list|>
block|{
annotation|@
name|Override
DECL|method|newInstance
specifier|public
name|BytesTransportRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|BytesTransportRequest
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
name|BytesTransportRequest
name|request
parameter_list|,
specifier|final
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|Compressor
name|compressor
init|=
name|CompressorFactory
operator|.
name|compressor
argument_list|(
name|request
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|StreamInput
name|in
decl_stmt|;
if|if
condition|(
name|compressor
operator|!=
literal|null
condition|)
block|{
name|in
operator|=
name|CachedStreamInput
operator|.
name|cachedHandlesCompressed
argument_list|(
name|compressor
argument_list|,
name|request
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|=
name|CachedStreamInput
operator|.
name|cachedHandles
argument_list|(
name|request
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|setVersion
argument_list|(
name|request
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|Builder
operator|.
name|readFrom
argument_list|(
name|in
argument_list|,
name|nodesProvider
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
argument_list|)
decl_stmt|;
name|clusterState
operator|.
name|status
argument_list|(
name|ClusterState
operator|.
name|ClusterStateStatus
operator|.
name|RECEIVED
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"received cluster state version {}"
argument_list|,
name|clusterState
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onNewClusterState
argument_list|(
name|clusterState
argument_list|,
operator|new
name|NewClusterStateListener
operator|.
name|NewStateProcessed
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onNewClusterStateProcessed
parameter_list|()
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|TransportResponse
operator|.
name|Empty
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to send response on cluster state processed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onNewClusterStateFailed
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to send response on cluster state processed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
block|}
end_class

end_unit

