begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
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
name|indices
operator|.
name|recovery
operator|.
name|RecoveryResponse
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
name|stats
operator|.
name|IndexStats
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
name|stats
operator|.
name|ShardStats
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
name|allocation
operator|.
name|decider
operator|.
name|EnableAllocationDecider
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
name|logging
operator|.
name|ESLogger
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
name|indices
operator|.
name|recovery
operator|.
name|RecoveryState
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
name|settings
operator|.
name|Settings
operator|.
name|settingsBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESIntegTestCase
operator|.
name|client
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
operator|.
name|randomBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThan
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Test of file reuse on recovery shared between integration tests and backwards  * compatibility tests.  */
end_comment

begin_class
DECL|class|ReusePeerRecoverySharedTest
specifier|public
class|class
name|ReusePeerRecoverySharedTest
block|{
comment|/**      * Test peer reuse on recovery. This is shared between RecoverFromGatewayIT      * and RecoveryBackwardsCompatibilityIT.      *      * @param indexSettings      *            settings for the index to test      * @param restartCluster      *            runnable that will restart the cluster under test      * @param logger      *            logger for logging      * @param useSyncIds      *            should this use synced flush? can't use synced from in the bwc      *            tests      */
DECL|method|testCase
specifier|public
specifier|static
name|void
name|testCase
parameter_list|(
name|Settings
name|indexSettings
parameter_list|,
name|Runnable
name|restartCluster
parameter_list|,
name|ESLogger
name|logger
parameter_list|,
name|boolean
name|useSyncIds
parameter_list|)
block|{
comment|/*          * prevent any rebalance actions during the peer recovery if we run into          * a relocation the reuse count will be 0 and this fails the test. We          * are testing here if we reuse the files on disk after full restarts          * for replicas.          */
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|indexSettings
argument_list|)
operator|.
name|put
argument_list|(
name|EnableAllocationDecider
operator|.
name|INDEX_ROUTING_REBALANCE_ENABLE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|EnableAllocationDecider
operator|.
name|Rebalance
operator|.
name|NONE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setTimeout
argument_list|(
literal|"30s"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> indexing docs"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|%
literal|200
operator|)
operator|==
literal|0
condition|)
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareFlush
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareFlush
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"--> running cluster health"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setTimeout
argument_list|(
literal|"30s"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// just wait for merges
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareForceMerge
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setMaxNumSegments
argument_list|(
literal|100
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareFlush
argument_list|()
operator|.
name|setWaitIfOngoing
argument_list|(
literal|true
argument_list|)
operator|.
name|setForce
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|useSyncIds
operator|==
literal|false
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> disabling allocation while the cluster is shut down"
argument_list|)
expr_stmt|;
comment|// Disable allocations while we are closing nodes
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|()
operator|.
name|setTransientSettings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|EnableAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_ENABLE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|EnableAllocationDecider
operator|.
name|Allocation
operator|.
name|NONE
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> full cluster restart"
argument_list|)
expr_stmt|;
name|restartCluster
operator|.
name|run
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> waiting for cluster to return to green after first shutdown"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setTimeout
argument_list|(
literal|"30s"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> trying to sync flush"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareSyncedFlush
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|failedShards
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertSyncIdsNotNull
argument_list|()
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"--> disabling allocation while the cluster is shut down"
argument_list|,
name|useSyncIds
condition|?
literal|""
else|:
literal|" a second time"
argument_list|)
expr_stmt|;
comment|// Disable allocations while we are closing nodes
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|()
operator|.
name|setTransientSettings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|EnableAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_ENABLE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|EnableAllocationDecider
operator|.
name|Allocation
operator|.
name|NONE
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> full cluster restart"
argument_list|)
expr_stmt|;
name|restartCluster
operator|.
name|run
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> waiting for cluster to return to green after {}shutdown"
argument_list|,
name|useSyncIds
condition|?
literal|""
else|:
literal|"second "
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setTimeout
argument_list|(
literal|"30s"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|useSyncIds
condition|)
block|{
name|assertSyncIdsNotNull
argument_list|()
expr_stmt|;
block|}
name|RecoveryResponse
name|recoveryResponse
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRecoveries
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|RecoveryState
name|recoveryState
range|:
name|recoveryResponse
operator|.
name|shardRecoveryStates
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
control|)
block|{
name|long
name|recovered
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RecoveryState
operator|.
name|File
name|file
range|:
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|fileDetails
argument_list|()
control|)
block|{
if|if
condition|(
name|file
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"segments"
argument_list|)
condition|)
block|{
name|recovered
operator|+=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|recoveryState
operator|.
name|getPrimary
argument_list|()
operator|&&
operator|(
name|useSyncIds
operator|==
literal|false
operator|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> replica shard {} recovered from {} to {}, recovered {}, reuse {}"
argument_list|,
name|recoveryState
operator|.
name|getShardId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|recoveryState
operator|.
name|getSourceNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|recoveryState
operator|.
name|getTargetNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|recoveredBytes
argument_list|()
argument_list|,
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|reusedBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"no bytes should be recovered"
argument_list|,
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|recoveredBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|recovered
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"data should have been reused"
argument_list|,
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|reusedBytes
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
comment|// we have to recover the segments file since we commit the translog ID on engine startup
name|assertThat
argument_list|(
literal|"all bytes should be reused except of the segments file"
argument_list|,
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|reusedBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|totalBytes
argument_list|()
operator|-
name|recovered
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"no files should be recovered except of the segments file"
argument_list|,
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|recoveredFileCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"all files should be reused except of the segments file"
argument_list|,
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|reusedFileCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|totalFileCount
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"> 0 files should be reused"
argument_list|,
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|reusedFileCount
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|useSyncIds
operator|&&
operator|!
name|recoveryState
operator|.
name|getPrimary
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> replica shard {} recovered from {} to {} using sync id, recovered {}, reuse {}"
argument_list|,
name|recoveryState
operator|.
name|getShardId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|recoveryState
operator|.
name|getSourceNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|recoveryState
operator|.
name|getTargetNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|recoveredBytes
argument_list|()
argument_list|,
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|reusedBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|recoveredBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|reusedBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|totalBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|recoveredFileCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|reusedFileCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|recoveryState
operator|.
name|getIndex
argument_list|()
operator|.
name|totalFileCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|assertSyncIdsNotNull
specifier|public
specifier|static
name|void
name|assertSyncIdsNotNull
parameter_list|()
block|{
name|IndexStats
name|indexStats
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getIndex
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
for|for
control|(
name|ShardStats
name|shardStats
range|:
name|indexStats
operator|.
name|getShards
argument_list|()
control|)
block|{
name|assertNotNull
argument_list|(
name|shardStats
operator|.
name|getCommitStats
argument_list|()
operator|.
name|getUserData
argument_list|()
operator|.
name|get
argument_list|(
name|Engine
operator|.
name|SYNC_COMMIT_ID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

