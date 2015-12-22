begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.ingest.transport.reload
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|reload
package|;
end_package

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
name|plugin
operator|.
name|ingest
operator|.
name|PipelineStore
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
name|TransportException
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
name|TransportRequest
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
name|TransportRequestHandler
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
name|TransportResponse
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
name|TransportResponseHandler
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|atomic
operator|.
name|AtomicBoolean
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/**  * An internal api that refreshes the in-memory representation of all the pipelines on all ingest nodes.  */
end_comment

begin_class
DECL|class|ReloadPipelinesAction
specifier|public
class|class
name|ReloadPipelinesAction
extends|extends
name|AbstractComponent
implements|implements
name|TransportRequestHandler
argument_list|<
name|ReloadPipelinesAction
operator|.
name|ReloadPipelinesRequest
argument_list|>
block|{
DECL|field|ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ACTION_NAME
init|=
literal|"internal:admin/ingest/reload/pipelines"
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|pipelineStore
specifier|private
specifier|final
name|PipelineStore
name|pipelineStore
decl_stmt|;
DECL|method|ReloadPipelinesAction
specifier|public
name|ReloadPipelinesAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|PipelineStore
name|pipelineStore
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|pipelineStore
operator|=
name|pipelineStore
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|transportService
operator|.
name|registerRequestHandler
argument_list|(
name|ACTION_NAME
argument_list|,
name|ReloadPipelinesRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|reloadPipelinesOnAllNodes
specifier|public
name|void
name|reloadPipelinesOnAllNodes
parameter_list|(
name|Consumer
argument_list|<
name|Boolean
argument_list|>
name|listener
parameter_list|)
block|{
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|ingestNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|String
name|nodeEnabled
init|=
name|node
operator|.
name|getAttributes
argument_list|()
operator|.
name|get
argument_list|(
literal|"ingest"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|nodeEnabled
argument_list|)
condition|)
block|{
name|ingestNodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ingestNodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"There are no ingest nodes in this cluster"
argument_list|)
throw|;
block|}
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|AtomicInteger
name|expectedResponses
init|=
operator|new
name|AtomicInteger
argument_list|(
name|ingestNodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|ingestNodes
control|)
block|{
name|ReloadPipelinesRequest
name|nodeRequest
init|=
operator|new
name|ReloadPipelinesRequest
argument_list|()
decl_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|ACTION_NAME
argument_list|,
name|nodeRequest
argument_list|,
operator|new
name|TransportResponseHandler
argument_list|<
name|ReloadPipelinesResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ReloadPipelinesResponse
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|ReloadPipelinesResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|ReloadPipelinesResponse
name|response
parameter_list|)
block|{
name|decrementAndReturn
argument_list|()
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
name|warn
argument_list|(
literal|"failed to update pipelines on remote node [{}]"
argument_list|,
name|exp
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|decrementAndReturn
argument_list|()
expr_stmt|;
block|}
name|void
name|decrementAndReturn
parameter_list|()
block|{
if|if
condition|(
name|expectedResponses
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|listener
operator|.
name|accept
argument_list|(
operator|!
name|failed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|MANAGEMENT
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|ReloadPipelinesRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|pipelineStore
operator|.
name|updatePipelines
argument_list|()
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|ReloadPipelinesResponse
argument_list|()
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
literal|"failed to update pipelines"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ReloadPipelinesRequest
specifier|final
specifier|static
class|class
name|ReloadPipelinesRequest
extends|extends
name|TransportRequest
block|{      }
DECL|class|ReloadPipelinesResponse
specifier|final
specifier|static
class|class
name|ReloadPipelinesResponse
extends|extends
name|TransportResponse
block|{      }
block|}
end_class

end_unit

