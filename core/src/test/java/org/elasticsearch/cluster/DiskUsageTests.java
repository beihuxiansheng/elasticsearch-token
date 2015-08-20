begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
package|;
end_package

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
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|stats
operator|.
name|NodeStats
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
name|cluster
operator|.
name|node
operator|.
name|stats
operator|.
name|NodesStatsResponse
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
name|CommonStats
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
name|cluster
operator|.
name|routing
operator|.
name|ShardRoutingHelper
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
name|UnassignedInfo
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
name|transport
operator|.
name|DummyTransportAddress
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
name|ShardPath
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
name|store
operator|.
name|StoreStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|fs
operator|.
name|FsInfo
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
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_class
DECL|class|DiskUsageTests
specifier|public
class|class
name|DiskUsageTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|Test
DECL|method|diskUsageCalcTest
specifier|public
name|void
name|diskUsageCalcTest
parameter_list|()
block|{
name|DiskUsage
name|du
init|=
operator|new
name|DiskUsage
argument_list|(
literal|"node1"
argument_list|,
literal|"n1"
argument_list|,
literal|"random"
argument_list|,
literal|100
argument_list|,
literal|40
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getFreeDiskAsPercentage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|40.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getUsedDiskAsPercentage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100.0
operator|-
literal|40.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getFreeBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|40L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getUsedBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|60L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getTotalBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test that DiskUsage handles invalid numbers, as reported by some
comment|// filesystems (ZFS& NTFS)
name|DiskUsage
name|du2
init|=
operator|new
name|DiskUsage
argument_list|(
literal|"node1"
argument_list|,
literal|"n1"
argument_list|,
literal|"random"
argument_list|,
literal|100
argument_list|,
literal|101
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|du2
operator|.
name|getFreeDiskAsPercentage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|101.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du2
operator|.
name|getFreeBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|101L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du2
operator|.
name|getUsedBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du2
operator|.
name|getTotalBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100L
argument_list|)
argument_list|)
expr_stmt|;
name|DiskUsage
name|du3
init|=
operator|new
name|DiskUsage
argument_list|(
literal|"node1"
argument_list|,
literal|"n1"
argument_list|,
literal|"random"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|du3
operator|.
name|getFreeDiskAsPercentage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du3
operator|.
name|getFreeBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du3
operator|.
name|getUsedBytes
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
name|du3
operator|.
name|getTotalBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|DiskUsage
name|du4
init|=
operator|new
name|DiskUsage
argument_list|(
literal|"node1"
argument_list|,
literal|"n1"
argument_list|,
literal|"random"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|du4
operator|.
name|getFreeDiskAsPercentage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du4
operator|.
name|getFreeBytes
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
name|du4
operator|.
name|getUsedBytes
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
name|du4
operator|.
name|getTotalBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|randomDiskUsageTest
specifier|public
name|void
name|randomDiskUsageTest
parameter_list|()
block|{
name|int
name|iters
init|=
name|scaledRandomIntBetween
argument_list|(
literal|1000
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|long
name|total
init|=
name|between
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|long
name|free
init|=
name|between
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|DiskUsage
name|du
init|=
operator|new
name|DiskUsage
argument_list|(
literal|"random"
argument_list|,
literal|"random"
argument_list|,
literal|"random"
argument_list|,
name|total
argument_list|,
name|free
argument_list|)
decl_stmt|;
if|if
condition|(
name|total
operator|==
literal|0
condition|)
block|{
name|assertThat
argument_list|(
name|du
operator|.
name|getFreeBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|free
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getTotalBytes
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
name|du
operator|.
name|getUsedBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|-
name|free
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getFreeDiskAsPercentage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getUsedDiskAsPercentage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|du
operator|.
name|getFreeBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|free
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getTotalBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|total
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getUsedBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|total
operator|-
name|free
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getFreeDiskAsPercentage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100.0
operator|*
operator|(
operator|(
name|double
operator|)
name|free
operator|/
name|total
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|du
operator|.
name|getUsedDiskAsPercentage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100.0
operator|-
operator|(
literal|100.0
operator|*
operator|(
operator|(
name|double
operator|)
name|free
operator|/
name|total
operator|)
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testFillShardLevelInfo
specifier|public
name|void
name|testFillShardLevelInfo
parameter_list|()
block|{
name|ShardRouting
name|test_0
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
literal|false
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
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|ShardRoutingHelper
operator|.
name|initialize
argument_list|(
name|test_0
argument_list|,
literal|"node1"
argument_list|)
expr_stmt|;
name|ShardRoutingHelper
operator|.
name|moveToStarted
argument_list|(
name|test_0
argument_list|)
expr_stmt|;
name|Path
name|test0Path
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"indices"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"test"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"0"
argument_list|)
decl_stmt|;
name|CommonStats
name|commonStats0
init|=
operator|new
name|CommonStats
argument_list|()
decl_stmt|;
name|commonStats0
operator|.
name|store
operator|=
operator|new
name|StoreStats
argument_list|(
literal|100
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ShardRouting
name|test_1
init|=
name|ShardRouting
operator|.
name|newUnassigned
argument_list|(
literal|"test"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
literal|false
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
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|ShardRoutingHelper
operator|.
name|initialize
argument_list|(
name|test_1
argument_list|,
literal|"node2"
argument_list|)
expr_stmt|;
name|ShardRoutingHelper
operator|.
name|moveToStarted
argument_list|(
name|test_1
argument_list|)
expr_stmt|;
name|Path
name|test1Path
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"indices"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"test"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|CommonStats
name|commonStats1
init|=
operator|new
name|CommonStats
argument_list|()
decl_stmt|;
name|commonStats1
operator|.
name|store
operator|=
operator|new
name|StoreStats
argument_list|(
literal|1000
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ShardStats
index|[]
name|stats
init|=
operator|new
name|ShardStats
index|[]
block|{
operator|new
name|ShardStats
argument_list|(
name|test_0
argument_list|,
operator|new
name|ShardPath
argument_list|(
literal|false
argument_list|,
name|test0Path
argument_list|,
name|test0Path
argument_list|,
literal|"0xdeadbeef"
argument_list|,
name|test_0
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|,
name|commonStats0
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|ShardStats
argument_list|(
name|test_1
argument_list|,
operator|new
name|ShardPath
argument_list|(
literal|false
argument_list|,
name|test1Path
argument_list|,
name|test1Path
argument_list|,
literal|"0xdeadbeef"
argument_list|,
name|test_1
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|,
name|commonStats1
argument_list|,
literal|null
argument_list|)
block|}
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|shardSizes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|ShardRouting
argument_list|,
name|String
argument_list|>
name|routingToPath
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|InternalClusterInfoService
operator|.
name|buildShardLevelInfo
argument_list|(
name|logger
argument_list|,
name|stats
argument_list|,
name|shardSizes
argument_list|,
name|routingToPath
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|shardSizes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|shardSizes
operator|.
name|containsKey
argument_list|(
name|ClusterInfo
operator|.
name|shardIdentifierFromRouting
argument_list|(
name|test_0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|shardSizes
operator|.
name|containsKey
argument_list|(
name|ClusterInfo
operator|.
name|shardIdentifierFromRouting
argument_list|(
name|test_1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100l
argument_list|,
name|shardSizes
operator|.
name|get
argument_list|(
name|ClusterInfo
operator|.
name|shardIdentifierFromRouting
argument_list|(
name|test_0
argument_list|)
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000l
argument_list|,
name|shardSizes
operator|.
name|get
argument_list|(
name|ClusterInfo
operator|.
name|shardIdentifierFromRouting
argument_list|(
name|test_1
argument_list|)
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|routingToPath
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|routingToPath
operator|.
name|containsKey
argument_list|(
name|test_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|routingToPath
operator|.
name|containsKey
argument_list|(
name|test_1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|test0Path
operator|.
name|getParent
argument_list|()
operator|.
name|getParent
argument_list|()
operator|.
name|getParent
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|routingToPath
operator|.
name|get
argument_list|(
name|test_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|test1Path
operator|.
name|getParent
argument_list|()
operator|.
name|getParent
argument_list|()
operator|.
name|getParent
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|routingToPath
operator|.
name|get
argument_list|(
name|test_1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFillDiskUsage
specifier|public
name|void
name|testFillDiskUsage
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|DiskUsage
argument_list|>
name|newLeastAvaiableUsages
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|DiskUsage
argument_list|>
name|newMostAvaiableUsages
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|FsInfo
operator|.
name|Path
index|[]
name|node1FSInfo
init|=
operator|new
name|FsInfo
operator|.
name|Path
index|[]
block|{
operator|new
name|FsInfo
operator|.
name|Path
argument_list|(
literal|"/middle"
argument_list|,
literal|"/dev/sda"
argument_list|,
literal|100
argument_list|,
literal|90
argument_list|,
literal|80
argument_list|)
block|,
operator|new
name|FsInfo
operator|.
name|Path
argument_list|(
literal|"/least"
argument_list|,
literal|"/dev/sdb"
argument_list|,
literal|200
argument_list|,
literal|190
argument_list|,
literal|70
argument_list|)
block|,
operator|new
name|FsInfo
operator|.
name|Path
argument_list|(
literal|"/most"
argument_list|,
literal|"/dev/sdc"
argument_list|,
literal|300
argument_list|,
literal|290
argument_list|,
literal|280
argument_list|)
block|,         }
decl_stmt|;
name|FsInfo
operator|.
name|Path
index|[]
name|node2FSInfo
init|=
operator|new
name|FsInfo
operator|.
name|Path
index|[]
block|{
operator|new
name|FsInfo
operator|.
name|Path
argument_list|(
literal|"/least_most"
argument_list|,
literal|"/dev/sda"
argument_list|,
literal|100
argument_list|,
literal|90
argument_list|,
literal|80
argument_list|)
block|,         }
decl_stmt|;
name|FsInfo
operator|.
name|Path
index|[]
name|node3FSInfo
init|=
operator|new
name|FsInfo
operator|.
name|Path
index|[]
block|{
operator|new
name|FsInfo
operator|.
name|Path
argument_list|(
literal|"/least"
argument_list|,
literal|"/dev/sda"
argument_list|,
literal|100
argument_list|,
literal|90
argument_list|,
literal|70
argument_list|)
block|,
operator|new
name|FsInfo
operator|.
name|Path
argument_list|(
literal|"/most"
argument_list|,
literal|"/dev/sda"
argument_list|,
literal|100
argument_list|,
literal|90
argument_list|,
literal|80
argument_list|)
block|,         }
decl_stmt|;
name|NodeStats
index|[]
name|nodeStats
init|=
operator|new
name|NodeStats
index|[]
block|{
operator|new
name|NodeStats
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"node_1"
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|FsInfo
argument_list|(
literal|0
argument_list|,
name|node1FSInfo
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|NodeStats
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"node_2"
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|FsInfo
argument_list|(
literal|0
argument_list|,
name|node2FSInfo
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|NodeStats
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"node_3"
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|FsInfo
argument_list|(
literal|0
argument_list|,
name|node3FSInfo
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|}
decl_stmt|;
name|InternalClusterInfoService
operator|.
name|fillDiskUsagePerNode
argument_list|(
name|logger
argument_list|,
name|nodeStats
argument_list|,
name|newLeastAvaiableUsages
argument_list|,
name|newMostAvaiableUsages
argument_list|)
expr_stmt|;
name|DiskUsage
name|leastNode_1
init|=
name|newLeastAvaiableUsages
operator|.
name|get
argument_list|(
literal|"node_1"
argument_list|)
decl_stmt|;
name|DiskUsage
name|mostNode_1
init|=
name|newMostAvaiableUsages
operator|.
name|get
argument_list|(
literal|"node_1"
argument_list|)
decl_stmt|;
name|assertDiskUsage
argument_list|(
name|mostNode_1
argument_list|,
name|node1FSInfo
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertDiskUsage
argument_list|(
name|leastNode_1
argument_list|,
name|node1FSInfo
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|DiskUsage
name|leastNode_2
init|=
name|newLeastAvaiableUsages
operator|.
name|get
argument_list|(
literal|"node_2"
argument_list|)
decl_stmt|;
name|DiskUsage
name|mostNode_2
init|=
name|newMostAvaiableUsages
operator|.
name|get
argument_list|(
literal|"node_2"
argument_list|)
decl_stmt|;
name|assertDiskUsage
argument_list|(
name|leastNode_2
argument_list|,
name|node2FSInfo
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertDiskUsage
argument_list|(
name|mostNode_2
argument_list|,
name|node2FSInfo
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|DiskUsage
name|leastNode_3
init|=
name|newLeastAvaiableUsages
operator|.
name|get
argument_list|(
literal|"node_3"
argument_list|)
decl_stmt|;
name|DiskUsage
name|mostNode_3
init|=
name|newMostAvaiableUsages
operator|.
name|get
argument_list|(
literal|"node_3"
argument_list|)
decl_stmt|;
name|assertDiskUsage
argument_list|(
name|leastNode_3
argument_list|,
name|node3FSInfo
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertDiskUsage
argument_list|(
name|mostNode_3
argument_list|,
name|node3FSInfo
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|assertDiskUsage
specifier|private
name|void
name|assertDiskUsage
parameter_list|(
name|DiskUsage
name|usage
parameter_list|,
name|FsInfo
operator|.
name|Path
name|path
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|usage
operator|.
name|toString
argument_list|()
argument_list|,
name|usage
operator|.
name|getPath
argument_list|()
argument_list|,
name|path
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|usage
operator|.
name|toString
argument_list|()
argument_list|,
name|usage
operator|.
name|getTotalBytes
argument_list|()
argument_list|,
name|path
operator|.
name|getTotal
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|usage
operator|.
name|toString
argument_list|()
argument_list|,
name|usage
operator|.
name|getFreeBytes
argument_list|()
argument_list|,
name|path
operator|.
name|getAvailable
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
