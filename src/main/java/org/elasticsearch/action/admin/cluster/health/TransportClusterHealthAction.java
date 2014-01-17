begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.health
package|package
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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|support
operator|.
name|master
operator|.
name|TransportMasterNodeReadOperationAction
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
name|ClusterName
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
name|ProcessedClusterStateUpdateTask
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
name|Strings
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
name|indices
operator|.
name|IndexMissingException
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
name|TransportService
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
name|CountDownLatch
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportClusterHealthAction
specifier|public
class|class
name|TransportClusterHealthAction
extends|extends
name|TransportMasterNodeReadOperationAction
argument_list|<
name|ClusterHealthRequest
argument_list|,
name|ClusterHealthResponse
argument_list|>
block|{
DECL|field|clusterName
specifier|private
specifier|final
name|ClusterName
name|clusterName
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportClusterHealthAction
specifier|public
name|TransportClusterHealthAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ClusterName
name|clusterName
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
comment|// we block here...
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|GENERIC
return|;
block|}
annotation|@
name|Override
DECL|method|transportAction
specifier|protected
name|String
name|transportAction
parameter_list|()
block|{
return|return
name|ClusterHealthAction
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|ClusterHealthRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|ClusterHealthRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|ClusterHealthResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|ClusterHealthResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|masterOperation
specifier|protected
name|void
name|masterOperation
parameter_list|(
specifier|final
name|ClusterHealthRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|unusedState
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|ClusterHealthResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|request
operator|.
name|timeout
argument_list|()
operator|.
name|millis
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|waitForEvents
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"cluster_health (wait_for_events ["
operator|+
name|request
operator|.
name|waitForEvents
argument_list|()
operator|+
literal|"])"
argument_list|,
name|request
operator|.
name|waitForEvents
argument_list|()
argument_list|,
operator|new
name|ProcessedClusterStateUpdateTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClusterState
name|execute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|)
block|{
return|return
name|currentState
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|String
name|source
parameter_list|,
name|ClusterState
name|oldState
parameter_list|,
name|ClusterState
name|newState
parameter_list|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|String
name|source
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"unexpected failure during [{}]"
argument_list|,
name|t
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|latch
operator|.
name|await
argument_list|(
name|request
operator|.
name|timeout
argument_list|()
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
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
name|int
name|waitFor
init|=
literal|5
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|waitForStatus
argument_list|()
operator|==
literal|null
condition|)
block|{
name|waitFor
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|waitForRelocatingShards
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|waitFor
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|waitForActiveShards
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|waitFor
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|waitFor
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|indices
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// check that they actually exists in the meta data
name|waitFor
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|waitFor
operator|==
literal|0
condition|)
block|{
comment|// no need to wait for anything
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|clusterHealth
argument_list|(
name|request
argument_list|,
name|clusterState
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|waitForCounter
init|=
literal|0
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
name|ClusterHealthResponse
name|response
init|=
name|clusterHealth
argument_list|(
name|request
argument_list|,
name|clusterState
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|waitForStatus
argument_list|()
operator|!=
literal|null
operator|&&
name|response
operator|.
name|getStatus
argument_list|()
operator|.
name|value
argument_list|()
operator|<=
name|request
operator|.
name|waitForStatus
argument_list|()
operator|.
name|value
argument_list|()
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|waitForRelocatingShards
argument_list|()
operator|!=
operator|-
literal|1
operator|&&
name|response
operator|.
name|getRelocatingShards
argument_list|()
operator|<=
name|request
operator|.
name|waitForRelocatingShards
argument_list|()
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|waitForActiveShards
argument_list|()
operator|!=
operator|-
literal|1
operator|&&
name|response
operator|.
name|getActiveShards
argument_list|()
operator|>=
name|request
operator|.
name|waitForActiveShards
argument_list|()
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|indices
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndices
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
name|waitForCounter
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexMissingException
name|e
parameter_list|)
block|{
name|response
operator|.
name|status
operator|=
name|ClusterHealthStatus
operator|.
name|RED
expr_stmt|;
comment|// no indices, make sure its RED
comment|// missing indices, wait a bit more...
block|}
block|}
if|if
condition|(
operator|!
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|">="
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|>=
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"ge("
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|>=
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"<="
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|<=
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"le("
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|<=
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|">"
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|>
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"gt("
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|>
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"<"
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|<
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"lt("
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|<
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|==
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|waitForCounter
operator|==
name|waitFor
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|endTime
condition|)
block|{
name|response
operator|.
name|timedOut
operator|=
literal|true
expr_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|response
operator|.
name|timedOut
operator|=
literal|true
expr_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
DECL|method|clusterHealth
specifier|private
name|ClusterHealthResponse
name|clusterHealth
parameter_list|(
name|ClusterHealthRequest
name|request
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Calculating health based on state version [{}]"
argument_list|,
name|clusterState
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|concreteIndices
decl_stmt|;
try|try
block|{
name|concreteIndices
operator|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndicesIgnoreMissing
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexMissingException
name|e
parameter_list|)
block|{
comment|// one of the specified indices is not there - treat it as RED.
name|ClusterHealthResponse
name|response
init|=
operator|new
name|ClusterHealthResponse
argument_list|(
name|clusterName
operator|.
name|value
argument_list|()
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|,
name|clusterState
argument_list|)
decl_stmt|;
name|response
operator|.
name|status
operator|=
name|ClusterHealthStatus
operator|.
name|RED
expr_stmt|;
return|return
name|response
return|;
block|}
return|return
operator|new
name|ClusterHealthResponse
argument_list|(
name|clusterName
operator|.
name|value
argument_list|()
argument_list|,
name|concreteIndices
argument_list|,
name|clusterState
argument_list|)
return|;
block|}
block|}
end_class

end_unit

