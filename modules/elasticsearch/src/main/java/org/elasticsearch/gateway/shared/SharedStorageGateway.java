begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.shared
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|shared
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
name|ClusterChangedEvent
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
name|ClusterStateListener
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
name|metadata
operator|.
name|MetaData
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
name|StopWatch
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
name|AbstractLifecycleComponent
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
name|gateway
operator|.
name|Gateway
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|GatewayException
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
name|ExecutorService
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
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|EsExecutors
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|SharedStorageGateway
specifier|public
specifier|abstract
class|class
name|SharedStorageGateway
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|Gateway
argument_list|>
implements|implements
name|Gateway
implements|,
name|ClusterStateListener
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|executor
specifier|private
specifier|volatile
name|ExecutorService
name|executor
decl_stmt|;
DECL|method|SharedStorageGateway
specifier|public
name|SharedStorageGateway
parameter_list|(
name|Settings
name|settings
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
name|clusterService
operator|=
name|clusterService
expr_stmt|;
block|}
DECL|method|doStart
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|this
operator|.
name|executor
operator|=
name|newSingleThreadExecutor
argument_list|(
name|daemonThreadFactory
argument_list|(
name|settings
argument_list|,
literal|"gateway"
argument_list|)
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|doStop
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|clusterService
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
DECL|method|doClose
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
DECL|method|performStateRecovery
annotation|@
name|Override
specifier|public
name|void
name|performStateRecovery
parameter_list|(
specifier|final
name|GatewayStateRecoveredListener
name|listener
parameter_list|)
throws|throws
name|GatewayException
block|{
name|executor
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
name|logger
operator|.
name|debug
argument_list|(
literal|"reading state from gateway {} ..."
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|StopWatch
name|stopWatch
init|=
operator|new
name|StopWatch
argument_list|()
operator|.
name|start
argument_list|()
decl_stmt|;
name|MetaData
name|metaData
decl_stmt|;
try|try
block|{
name|metaData
operator|=
name|read
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"read state from gateway {}, took {}"
argument_list|,
name|this
argument_list|,
name|stopWatch
operator|.
name|stop
argument_list|()
operator|.
name|totalTime
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|metaData
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"no state read from gateway"
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onSuccess
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onSuccess
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|()
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"failed to read from gateway"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|clusterChanged
annotation|@
name|Override
specifier|public
name|void
name|clusterChanged
parameter_list|(
specifier|final
name|ClusterChangedEvent
name|event
parameter_list|)
block|{
if|if
condition|(
operator|!
name|lifecycle
operator|.
name|started
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// nothing to do until we actually recover from the gateway or any other block indicates we need to disable persistency
if|if
condition|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|blocks
argument_list|()
operator|.
name|disableStatePersistence
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|event
operator|.
name|localNodeMaster
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|event
operator|.
name|metaDataChanged
argument_list|()
condition|)
block|{
return|return;
block|}
name|executor
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
name|logger
operator|.
name|debug
argument_list|(
literal|"writing to gateway {} ..."
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|StopWatch
name|stopWatch
init|=
operator|new
name|StopWatch
argument_list|()
operator|.
name|start
argument_list|()
decl_stmt|;
try|try
block|{
name|write
argument_list|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"wrote to gateway {}, took {}"
argument_list|,
name|this
argument_list|,
name|stopWatch
operator|.
name|stop
argument_list|()
operator|.
name|totalTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO, we need to remember that we failed, maybe add a retry scheduler?
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"failed to write to gateway"
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
block|}
DECL|method|read
specifier|protected
specifier|abstract
name|MetaData
name|read
parameter_list|()
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|write
specifier|protected
specifier|abstract
name|void
name|write
parameter_list|(
name|MetaData
name|metaData
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
block|}
end_class

end_unit

