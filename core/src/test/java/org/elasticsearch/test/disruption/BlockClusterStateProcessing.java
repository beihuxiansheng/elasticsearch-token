begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.disruption
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|disruption
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
name|ClusterStateUpdateTask
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
name|Priority
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
name|test
operator|.
name|InternalTestCluster
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_class
DECL|class|BlockClusterStateProcessing
specifier|public
class|class
name|BlockClusterStateProcessing
extends|extends
name|SingleNodeDisruption
block|{
DECL|field|disruptionLatch
name|AtomicReference
argument_list|<
name|CountDownLatch
argument_list|>
name|disruptionLatch
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|BlockClusterStateProcessing
specifier|public
name|BlockClusterStateProcessing
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|random
argument_list|)
expr_stmt|;
block|}
DECL|method|BlockClusterStateProcessing
specifier|public
name|BlockClusterStateProcessing
parameter_list|(
name|String
name|disruptedNode
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|this
operator|.
name|disruptedNode
operator|=
name|disruptedNode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDisrupting
specifier|public
name|void
name|startDisrupting
parameter_list|()
block|{
specifier|final
name|String
name|disruptionNodeCopy
init|=
name|disruptedNode
decl_stmt|;
if|if
condition|(
name|disruptionNodeCopy
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|ClusterService
name|clusterService
init|=
name|cluster
operator|.
name|getInstance
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|,
name|disruptionNodeCopy
argument_list|)
decl_stmt|;
if|if
condition|(
name|clusterService
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"delaying cluster state updates on node [{}]"
argument_list|,
name|disruptionNodeCopy
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
name|disruptionLatch
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|success
operator|:
literal|"startDisrupting called without waiting on stopDistrupting to complete"
assert|;
specifier|final
name|CountDownLatch
name|started
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
literal|"service_disruption_block"
argument_list|,
name|Priority
operator|.
name|IMMEDIATE
argument_list|,
operator|new
name|ClusterStateUpdateTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|runOnlyOnMaster
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|ClusterState
name|execute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|)
throws|throws
name|Exception
block|{
name|started
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|CountDownLatch
name|latch
init|=
name|disruptionLatch
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|latch
operator|!=
literal|null
condition|)
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
return|return
name|currentState
return|;
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
literal|"unexpected error during disruption"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|started
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
block|}
annotation|@
name|Override
DECL|method|stopDisrupting
specifier|public
name|void
name|stopDisrupting
parameter_list|()
block|{
name|CountDownLatch
name|latch
init|=
name|disruptionLatch
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|latch
operator|!=
literal|null
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|removeAndEnsureHealthy
specifier|public
name|void
name|removeAndEnsureHealthy
parameter_list|(
name|InternalTestCluster
name|cluster
parameter_list|)
block|{
name|removeFromCluster
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|expectedTimeToHeal
specifier|public
name|TimeValue
name|expectedTimeToHeal
parameter_list|()
block|{
return|return
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit

