begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.cat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|cat
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
name|ShardOperationFailedException
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
name|RecoverySource
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
name|RecoverySource
operator|.
name|SnapshotRecoverySource
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
name|TestShardRouting
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
name|Randomness
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
name|Table
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
name|index
operator|.
name|Index
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
name|indices
operator|.
name|recovery
operator|.
name|RecoveryState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestController
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
name|Collections
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|elasticsearch
operator|.
name|mock
operator|.
name|orig
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_class
DECL|class|RestRecoveryActionTests
specifier|public
class|class
name|RestRecoveryActionTests
extends|extends
name|ESTestCase
block|{
DECL|method|testRestRecoveryAction
specifier|public
name|void
name|testRestRecoveryAction
parameter_list|()
block|{
specifier|final
name|Settings
name|settings
init|=
name|Settings
operator|.
name|EMPTY
decl_stmt|;
specifier|final
name|RestController
name|restController
init|=
operator|new
name|RestController
argument_list|(
name|settings
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|RestRecoveryAction
name|action
init|=
operator|new
name|RestRecoveryAction
argument_list|(
name|settings
argument_list|,
name|restController
argument_list|,
name|restController
argument_list|)
decl_stmt|;
specifier|final
name|int
name|totalShards
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|32
argument_list|)
decl_stmt|;
specifier|final
name|int
name|successfulShards
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|totalShards
operator|-
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|failedShards
init|=
name|totalShards
operator|-
name|successfulShards
decl_stmt|;
specifier|final
name|boolean
name|detailed
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RecoveryState
argument_list|>
argument_list|>
name|shardRecoveryStates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RecoveryState
argument_list|>
name|recoveryStates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|successfulShards
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RecoveryState
name|state
init|=
name|mock
argument_list|(
name|RecoveryState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|state
operator|.
name|getShardId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ShardId
argument_list|(
operator|new
name|Index
argument_list|(
literal|"index"
argument_list|,
literal|"_na_"
argument_list|)
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RecoveryState
operator|.
name|Timer
name|timer
init|=
name|mock
argument_list|(
name|RecoveryState
operator|.
name|Timer
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|timer
operator|.
name|time
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|(
name|long
operator|)
name|randomIntBetween
argument_list|(
literal|1000000
argument_list|,
literal|10
operator|*
literal|1000000
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|state
operator|.
name|getTimer
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|timer
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|state
operator|.
name|getRecoverySource
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|TestShardRouting
operator|.
name|randomRecoverySource
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|state
operator|.
name|getStage
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|randomFrom
argument_list|(
name|RecoveryState
operator|.
name|Stage
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|DiscoveryNode
name|sourceNode
init|=
name|randomBoolean
argument_list|()
condition|?
name|mock
argument_list|(
name|DiscoveryNode
operator|.
name|class
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|sourceNode
operator|!=
literal|null
condition|)
block|{
name|when
argument_list|(
name|sourceNode
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|state
operator|.
name|getSourceNode
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|sourceNode
argument_list|)
expr_stmt|;
specifier|final
name|DiscoveryNode
name|targetNode
init|=
name|mock
argument_list|(
name|DiscoveryNode
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|targetNode
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|state
operator|.
name|getTargetNode
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|targetNode
argument_list|)
expr_stmt|;
name|RecoveryState
operator|.
name|Index
name|index
init|=
name|mock
argument_list|(
name|RecoveryState
operator|.
name|Index
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|int
name|totalRecoveredFiles
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|64
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|index
operator|.
name|totalRecoverFiles
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|totalRecoveredFiles
argument_list|)
expr_stmt|;
specifier|final
name|int
name|recoveredFileCount
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|totalRecoveredFiles
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|index
operator|.
name|recoveredFileCount
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|recoveredFileCount
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|index
operator|.
name|recoveredFilesPercent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|(
literal|100f
operator|*
name|recoveredFileCount
operator|)
operator|/
name|totalRecoveredFiles
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|index
operator|.
name|totalFileCount
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|randomIntBetween
argument_list|(
name|totalRecoveredFiles
argument_list|,
literal|2
operator|*
name|totalRecoveredFiles
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|totalRecoveredBytes
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|1
operator|<<
literal|24
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|index
operator|.
name|totalRecoverBytes
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|(
name|long
operator|)
name|totalRecoveredBytes
argument_list|)
expr_stmt|;
specifier|final
name|int
name|recoveredBytes
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|totalRecoveredBytes
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|index
operator|.
name|recoveredBytes
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|(
name|long
operator|)
name|recoveredBytes
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|index
operator|.
name|recoveredBytesPercent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|(
literal|100f
operator|*
name|recoveredBytes
operator|)
operator|/
name|totalRecoveredBytes
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|index
operator|.
name|totalRecoverBytes
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|(
name|long
operator|)
name|randomIntBetween
argument_list|(
name|totalRecoveredBytes
argument_list|,
literal|2
operator|*
name|totalRecoveredBytes
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|state
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|index
argument_list|)
expr_stmt|;
specifier|final
name|RecoveryState
operator|.
name|Translog
name|translog
init|=
name|mock
argument_list|(
name|RecoveryState
operator|.
name|Translog
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|int
name|translogOps
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|1
operator|<<
literal|18
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|translog
operator|.
name|totalOperations
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|translogOps
argument_list|)
expr_stmt|;
specifier|final
name|int
name|translogOpsRecovered
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|translogOps
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|translog
operator|.
name|recoveredOperations
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|translogOpsRecovered
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|translog
operator|.
name|recoveredPercent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|translogOps
operator|==
literal|0
condition|?
literal|100f
else|:
operator|(
literal|100f
operator|*
name|translogOpsRecovered
operator|/
name|translogOps
operator|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|state
operator|.
name|getTranslog
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|translog
argument_list|)
expr_stmt|;
name|recoveryStates
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|RecoveryState
argument_list|>
name|shuffle
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|recoveryStates
argument_list|)
decl_stmt|;
name|Randomness
operator|.
name|shuffle
argument_list|(
name|shuffle
argument_list|)
expr_stmt|;
name|shardRecoveryStates
operator|.
name|put
argument_list|(
literal|"index"
argument_list|,
name|shuffle
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|ShardOperationFailedException
argument_list|>
name|shardFailures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RecoveryResponse
name|response
init|=
operator|new
name|RecoveryResponse
argument_list|(
name|totalShards
argument_list|,
name|successfulShards
argument_list|,
name|failedShards
argument_list|,
name|detailed
argument_list|,
name|shardRecoveryStates
argument_list|,
name|shardFailures
argument_list|)
decl_stmt|;
specifier|final
name|Table
name|table
init|=
name|action
operator|.
name|buildRecoveryTable
argument_list|(
literal|null
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|table
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Table
operator|.
name|Cell
argument_list|>
name|headers
init|=
name|table
operator|.
name|getHeaders
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"index"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"shard"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"time"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"stage"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"source_host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"source_node"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"target_host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|8
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"target_node"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|9
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"repository"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|10
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"snapshot"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|11
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"files"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|12
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"files_recovered"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|13
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"files_percent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|14
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"files_total"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|15
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"bytes"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|16
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"bytes_recovered"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|17
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"bytes_percent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|18
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"bytes_total"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|19
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"translog_ops"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|20
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"translog_ops_recovered"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
literal|21
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"translog_ops_percent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|table
operator|.
name|getRows
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|successfulShards
argument_list|)
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
name|successfulShards
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RecoveryState
name|state
init|=
name|recoveryStates
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Table
operator|.
name|Cell
argument_list|>
name|cells
init|=
name|table
operator|.
name|getRows
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"index"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
name|state
operator|.
name|getTimer
argument_list|()
operator|.
name|time
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getRecoverySource
argument_list|()
operator|.
name|getType
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getStage
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getSourceNode
argument_list|()
operator|==
literal|null
condition|?
literal|"n/a"
else|:
name|state
operator|.
name|getSourceNode
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getSourceNode
argument_list|()
operator|==
literal|null
condition|?
literal|"n/a"
else|:
name|state
operator|.
name|getSourceNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getTargetNode
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|8
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getTargetNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|9
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getRecoverySource
argument_list|()
operator|==
literal|null
operator|||
name|state
operator|.
name|getRecoverySource
argument_list|()
operator|.
name|getType
argument_list|()
operator|!=
name|RecoverySource
operator|.
name|Type
operator|.
name|SNAPSHOT
condition|?
literal|"n/a"
else|:
operator|(
operator|(
name|SnapshotRecoverySource
operator|)
name|state
operator|.
name|getRecoverySource
argument_list|()
operator|)
operator|.
name|snapshot
argument_list|()
operator|.
name|getRepository
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|10
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getRecoverySource
argument_list|()
operator|==
literal|null
operator|||
name|state
operator|.
name|getRecoverySource
argument_list|()
operator|.
name|getType
argument_list|()
operator|!=
name|RecoverySource
operator|.
name|Type
operator|.
name|SNAPSHOT
condition|?
literal|"n/a"
else|:
operator|(
operator|(
name|SnapshotRecoverySource
operator|)
name|state
operator|.
name|getRecoverySource
argument_list|()
operator|)
operator|.
name|snapshot
argument_list|()
operator|.
name|getSnapshotId
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|11
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getIndex
argument_list|()
operator|.
name|totalRecoverFiles
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|12
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getIndex
argument_list|()
operator|.
name|recoveredFileCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|13
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|percent
argument_list|(
name|state
operator|.
name|getIndex
argument_list|()
operator|.
name|recoveredFilesPercent
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|14
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getIndex
argument_list|()
operator|.
name|totalFileCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|15
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getIndex
argument_list|()
operator|.
name|totalRecoverBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|16
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getIndex
argument_list|()
operator|.
name|recoveredBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|17
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|percent
argument_list|(
name|state
operator|.
name|getIndex
argument_list|()
operator|.
name|recoveredBytesPercent
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|18
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
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
name|cells
operator|.
name|get
argument_list|(
literal|19
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getTranslog
argument_list|()
operator|.
name|totalOperations
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|20
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|state
operator|.
name|getTranslog
argument_list|()
operator|.
name|recoveredOperations
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cells
operator|.
name|get
argument_list|(
literal|21
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|percent
argument_list|(
name|state
operator|.
name|getTranslog
argument_list|()
operator|.
name|recoveredPercent
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|percent
specifier|private
specifier|static
name|String
name|percent
parameter_list|(
name|float
name|percent
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%1.1f%%"
argument_list|,
name|percent
argument_list|)
return|;
block|}
block|}
end_class

end_unit

