begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
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
name|not
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
name|notNullValue
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
name|nullValue
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|AllocationIdTests
specifier|public
class|class
name|AllocationIdTests
extends|extends
name|ESTestCase
block|{
DECL|method|testShardToStarted
specifier|public
name|void
name|testShardToStarted
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"-- create unassigned shard"
argument_list|)
expr_stmt|;
name|ShardRouting
name|shard
init|=
name|ShardRouting
operator|.
name|newUnassigned
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-- initialize the shard"
argument_list|)
expr_stmt|;
name|shard
operator|.
name|initialize
argument_list|(
literal|"node1"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|AllocationId
name|allocationId
init|=
name|shard
operator|.
name|allocationId
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|allocationId
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allocationId
operator|.
name|getId
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allocationId
operator|.
name|getRelocationId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-- start the shard"
argument_list|)
expr_stmt|;
name|shard
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|allocationId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|allocationId
operator|=
name|shard
operator|.
name|allocationId
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|allocationId
operator|.
name|getId
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allocationId
operator|.
name|getRelocationId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSuccessfulRelocation
specifier|public
name|void
name|testSuccessfulRelocation
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"-- build started shard"
argument_list|)
expr_stmt|;
name|ShardRouting
name|shard
init|=
name|ShardRouting
operator|.
name|newUnassigned
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|shard
operator|.
name|initialize
argument_list|(
literal|"node1"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|shard
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
name|AllocationId
name|allocationId
init|=
name|shard
operator|.
name|allocationId
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-- relocate the shard"
argument_list|)
expr_stmt|;
name|shard
operator|.
name|relocate
argument_list|(
literal|"node2"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|allocationId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|allocationId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getRelocationId
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|ShardRouting
name|target
init|=
name|shard
operator|.
name|buildTargetRelocatingShard
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|allocationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getRelocationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|allocationId
argument_list|()
operator|.
name|getRelocationId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-- finalize the relocation"
argument_list|)
expr_stmt|;
name|target
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|allocationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getRelocationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|allocationId
argument_list|()
operator|.
name|getRelocationId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCancelRelocation
specifier|public
name|void
name|testCancelRelocation
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"-- build started shard"
argument_list|)
expr_stmt|;
name|ShardRouting
name|shard
init|=
name|ShardRouting
operator|.
name|newUnassigned
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|shard
operator|.
name|initialize
argument_list|(
literal|"node1"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|shard
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
name|AllocationId
name|allocationId
init|=
name|shard
operator|.
name|allocationId
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-- relocate the shard"
argument_list|)
expr_stmt|;
name|shard
operator|.
name|relocate
argument_list|(
literal|"node2"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|allocationId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|allocationId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getRelocationId
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|allocationId
operator|=
name|shard
operator|.
name|allocationId
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-- cancel relocation"
argument_list|)
expr_stmt|;
name|shard
operator|.
name|cancelRelocation
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|allocationId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getRelocationId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMoveToUnassigned
specifier|public
name|void
name|testMoveToUnassigned
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"-- build started shard"
argument_list|)
expr_stmt|;
name|ShardRouting
name|shard
init|=
name|ShardRouting
operator|.
name|newUnassigned
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|shard
operator|.
name|initialize
argument_list|(
literal|"node1"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|shard
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-- move to unassigned"
argument_list|)
expr_stmt|;
name|shard
operator|.
name|moveToUnassigned
argument_list|(
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|NODE_LEFT
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testReinitializing
specifier|public
name|void
name|testReinitializing
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"-- build started shard"
argument_list|)
expr_stmt|;
name|ShardRouting
name|shard
init|=
name|ShardRouting
operator|.
name|newUnassigned
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|shard
operator|.
name|initialize
argument_list|(
literal|"node1"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|shard
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
name|AllocationId
name|allocationId
init|=
name|shard
operator|.
name|allocationId
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-- reinitializing shard"
argument_list|)
expr_stmt|;
name|shard
operator|.
name|reinitializeShard
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getRelocationId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shard
operator|.
name|allocationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|allocationId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

