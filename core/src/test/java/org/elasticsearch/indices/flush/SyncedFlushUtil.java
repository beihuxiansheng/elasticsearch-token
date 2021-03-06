begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.flush
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|flush
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|routing
operator|.
name|ShardRouting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
operator|.
name|Engine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|ShardId
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
name|List
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

begin_comment
comment|/** Utils for SyncedFlush */
end_comment

begin_class
DECL|class|SyncedFlushUtil
specifier|public
class|class
name|SyncedFlushUtil
block|{
DECL|method|SyncedFlushUtil
specifier|private
name|SyncedFlushUtil
parameter_list|()
block|{      }
comment|/**      * Blocking version of {@link SyncedFlushService#attemptSyncedFlush(ShardId, ActionListener)}      */
DECL|method|attemptSyncedFlush
specifier|public
specifier|static
name|ShardsSyncedFlushResult
name|attemptSyncedFlush
parameter_list|(
name|InternalTestCluster
name|cluster
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
block|{
name|SyncedFlushService
name|service
init|=
name|cluster
operator|.
name|getInstance
argument_list|(
name|SyncedFlushService
operator|.
name|class
argument_list|)
decl_stmt|;
name|LatchedListener
argument_list|<
name|ShardsSyncedFlushResult
argument_list|>
name|listener
init|=
operator|new
name|LatchedListener
argument_list|()
decl_stmt|;
name|service
operator|.
name|attemptSyncedFlush
argument_list|(
name|shardId
argument_list|,
name|listener
argument_list|)
expr_stmt|;
try|try
block|{
name|listener
operator|.
name|latch
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
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|listener
operator|.
name|error
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ExceptionsHelper
operator|.
name|convertToElastic
argument_list|(
name|listener
operator|.
name|error
argument_list|)
throw|;
block|}
return|return
name|listener
operator|.
name|result
return|;
block|}
DECL|class|LatchedListener
specifier|public
specifier|static
specifier|final
class|class
name|LatchedListener
parameter_list|<
name|T
parameter_list|>
implements|implements
name|ActionListener
argument_list|<
name|T
argument_list|>
block|{
DECL|field|result
specifier|public
specifier|volatile
name|T
name|result
decl_stmt|;
DECL|field|error
specifier|public
specifier|volatile
name|Exception
name|error
decl_stmt|;
DECL|field|latch
specifier|public
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
annotation|@
name|Override
DECL|method|onResponse
specifier|public
name|void
name|onResponse
parameter_list|(
name|T
name|result
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFailure
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|error
operator|=
name|e
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Blocking version of {@link SyncedFlushService#sendPreSyncRequests(List, ClusterState, ShardId, ActionListener)}      */
DECL|method|sendPreSyncRequests
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Engine
operator|.
name|CommitId
argument_list|>
name|sendPreSyncRequests
parameter_list|(
name|SyncedFlushService
name|service
parameter_list|,
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|activeShards
parameter_list|,
name|ClusterState
name|state
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
block|{
name|LatchedListener
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Engine
operator|.
name|CommitId
argument_list|>
argument_list|>
name|listener
init|=
operator|new
name|LatchedListener
argument_list|<>
argument_list|()
decl_stmt|;
name|service
operator|.
name|sendPreSyncRequests
argument_list|(
name|activeShards
argument_list|,
name|state
argument_list|,
name|shardId
argument_list|,
name|listener
argument_list|)
expr_stmt|;
try|try
block|{
name|listener
operator|.
name|latch
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
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|listener
operator|.
name|error
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ExceptionsHelper
operator|.
name|convertToElastic
argument_list|(
name|listener
operator|.
name|error
argument_list|)
throw|;
block|}
return|return
name|listener
operator|.
name|result
return|;
block|}
block|}
end_class

end_unit

