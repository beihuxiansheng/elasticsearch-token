begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
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
name|client
operator|.
name|Client
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
name|block
operator|.
name|ClusterBlocks
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
name|IndexMetaData
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
name|cluster
operator|.
name|routing
operator|.
name|IndexRoutingTable
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
name|IndexShardRoutingTable
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
name|RoutingTable
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
name|ShardRoutingState
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
name|bytes
operator|.
name|BytesArray
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
name|text
operator|.
name|Text
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
name|gateway
operator|.
name|GatewayService
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
name|search
operator|.
name|internal
operator|.
name|InternalSearchHit
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
name|org
operator|.
name|junit
operator|.
name|Before
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
name|List
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
name|notNullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
name|anyString
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
name|doThrow
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|never
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
name|times
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
name|verify
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
name|when
import|;
end_import

begin_class
DECL|class|IngestBootstrapperTests
specifier|public
class|class
name|IngestBootstrapperTests
extends|extends
name|ESTestCase
block|{
DECL|field|store
specifier|private
name|PipelineStore
name|store
decl_stmt|;
DECL|field|bootstrapper
specifier|private
name|IngestBootstrapper
name|bootstrapper
decl_stmt|;
annotation|@
name|Before
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
block|{
name|ThreadPool
name|threadPool
init|=
name|mock
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|threadPool
operator|.
name|executor
argument_list|(
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Runnable
operator|::
name|run
argument_list|)
expr_stmt|;
name|ClusterService
name|clusterService
init|=
name|mock
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
decl_stmt|;
name|store
operator|=
name|mock
argument_list|(
name|PipelineStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|store
operator|.
name|isStarted
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PipelineExecutionService
name|pipelineExecutionService
init|=
name|mock
argument_list|(
name|PipelineExecutionService
operator|.
name|class
argument_list|)
decl_stmt|;
name|bootstrapper
operator|=
operator|new
name|IngestBootstrapper
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|store
argument_list|,
name|pipelineExecutionService
argument_list|)
expr_stmt|;
block|}
DECL|method|testStartAndStopInBackground
specifier|public
name|void
name|testStartAndStopInBackground
parameter_list|()
throws|throws
name|Exception
block|{
name|ThreadPool
name|threadPool
init|=
operator|new
name|ThreadPool
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Client
name|client
init|=
name|mock
argument_list|(
name|Client
operator|.
name|class
argument_list|)
decl_stmt|;
name|TransportService
name|transportService
init|=
name|mock
argument_list|(
name|TransportService
operator|.
name|class
argument_list|)
decl_stmt|;
name|ClusterService
name|clusterService
init|=
name|mock
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|client
operator|.
name|search
argument_list|(
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PipelineStoreTests
operator|.
name|expectedSearchReponse
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|client
operator|.
name|searchScroll
argument_list|(
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PipelineStoreTests
operator|.
name|expectedSearchReponse
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Settings
name|settings
init|=
name|Settings
operator|.
name|EMPTY
decl_stmt|;
name|PipelineStore
name|store
init|=
operator|new
name|PipelineStore
argument_list|(
name|settings
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|)
decl_stmt|;
name|IngestBootstrapper
name|bootstrapper
init|=
operator|new
name|IngestBootstrapper
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|store
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|bootstrapper
operator|.
name|setClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|InternalSearchHit
argument_list|>
name|hits
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|hits
operator|.
name|add
argument_list|(
operator|new
name|InternalSearchHit
argument_list|(
literal|0
argument_list|,
literal|"1"
argument_list|,
operator|new
name|Text
argument_list|(
literal|"type"
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
operator|.
name|sourceRef
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{\"description\": \"_description1\"}"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|client
operator|.
name|search
argument_list|(
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PipelineStoreTests
operator|.
name|expectedSearchReponse
argument_list|(
name|hits
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|client
operator|.
name|get
argument_list|(
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PipelineStoreTests
operator|.
name|expectedGetResponse
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|store
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"IllegalStateException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"pipeline store isn't ready yet"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|bootstrapper
operator|.
name|startPipelineStore
argument_list|()
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
block|{
name|assertThat
argument_list|(
name|store
operator|.
name|isStarted
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|store
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|store
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|store
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
operator|.
name|getDescription
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_description1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|bootstrapper
operator|.
name|stopPipelineStore
argument_list|(
literal|"testing stop"
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
name|assertThat
argument_list|(
name|store
operator|.
name|isStarted
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// the map internal search hit holds gets emptied after use, which is ok, but in this test we need to reset the source:
name|hits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|sourceRef
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{\"description\": \"_description1\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|.
name|add
argument_list|(
operator|new
name|InternalSearchHit
argument_list|(
literal|0
argument_list|,
literal|"2"
argument_list|,
operator|new
name|Text
argument_list|(
literal|"type"
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
operator|.
name|sourceRef
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{\"description\": \"_description2\"}"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|bootstrapper
operator|.
name|startPipelineStore
argument_list|()
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
block|{
name|assertThat
argument_list|(
name|store
operator|.
name|isStarted
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|store
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|store
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|store
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
operator|.
name|getDescription
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_description1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|store
operator|.
name|get
argument_list|(
literal|"2"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|store
operator|.
name|get
argument_list|(
literal|"2"
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|store
operator|.
name|get
argument_list|(
literal|"2"
argument_list|)
operator|.
name|getDescription
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_description2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|testPipelineStoreBootstrappingGlobalStateNotRecoveredBlock
specifier|public
name|void
name|testPipelineStoreBootstrappingGlobalStateNotRecoveredBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterState
operator|.
name|Builder
name|csBuilder
init|=
operator|new
name|ClusterState
operator|.
name|Builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"_name"
argument_list|)
argument_list|)
decl_stmt|;
name|csBuilder
operator|.
name|blocks
argument_list|(
name|ClusterBlocks
operator|.
name|builder
argument_list|()
operator|.
name|addGlobalBlock
argument_list|(
name|GatewayService
operator|.
name|STATE_NOT_RECOVERED_BLOCK
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterState
name|cs
init|=
name|csBuilder
operator|.
name|metaData
argument_list|(
name|MetaData
operator|.
name|builder
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bootstrapper
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|cs
argument_list|,
name|cs
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|stop
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPipelineStoreBootstrappingGlobalStateNoMasterBlock
specifier|public
name|void
name|testPipelineStoreBootstrappingGlobalStateNoMasterBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterState
operator|.
name|Builder
name|csBuilder
init|=
operator|new
name|ClusterState
operator|.
name|Builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"_name"
argument_list|)
argument_list|)
decl_stmt|;
name|csBuilder
operator|.
name|blocks
argument_list|(
name|ClusterBlocks
operator|.
name|builder
argument_list|()
operator|.
name|addGlobalBlock
argument_list|(
name|randomBoolean
argument_list|()
condition|?
name|DiscoverySettings
operator|.
name|NO_MASTER_BLOCK_WRITES
else|:
name|DiscoverySettings
operator|.
name|NO_MASTER_BLOCK_ALL
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterState
name|cs
init|=
name|csBuilder
operator|.
name|metaData
argument_list|(
name|MetaData
operator|.
name|builder
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// We're not started and there is a no master block, doing nothing:
name|bootstrapper
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|cs
argument_list|,
name|cs
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|stop
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
comment|// We're started and there is a no master block, so we stop the store:
name|when
argument_list|(
name|store
operator|.
name|isStarted
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bootstrapper
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|cs
argument_list|,
name|cs
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|stop
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPipelineStoreBootstrappingNoIngestIndex
specifier|public
name|void
name|testPipelineStoreBootstrappingNoIngestIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterState
operator|.
name|Builder
name|csBuilder
init|=
operator|new
name|ClusterState
operator|.
name|Builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"_name"
argument_list|)
argument_list|)
decl_stmt|;
name|ClusterState
name|cs
init|=
name|csBuilder
operator|.
name|metaData
argument_list|(
name|MetaData
operator|.
name|builder
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bootstrapper
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|cs
argument_list|,
name|cs
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|testPipelineStoreBootstrappingIngestIndexShardsNotStarted
specifier|public
name|void
name|testPipelineStoreBootstrappingIngestIndexShardsNotStarted
parameter_list|()
throws|throws
name|Exception
block|{
comment|// .ingest index, but not all primary shards started:
name|ClusterState
operator|.
name|Builder
name|csBuilder
init|=
operator|new
name|ClusterState
operator|.
name|Builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"_name"
argument_list|)
argument_list|)
decl_stmt|;
name|MetaData
operator|.
name|Builder
name|metaDateBuilder
init|=
name|MetaData
operator|.
name|builder
argument_list|()
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routingTableBuilder
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|metaDateBuilder
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|PipelineStore
operator|.
name|INDEX
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|IndexRoutingTable
operator|.
name|Builder
name|indexRoutingTableBuilder
init|=
name|IndexRoutingTable
operator|.
name|builder
argument_list|(
name|PipelineStore
operator|.
name|INDEX
argument_list|)
decl_stmt|;
name|indexRoutingTableBuilder
operator|.
name|addIndexShard
argument_list|(
operator|new
name|IndexShardRoutingTable
operator|.
name|Builder
argument_list|(
operator|new
name|ShardId
argument_list|(
name|PipelineStore
operator|.
name|INDEX
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|addShard
argument_list|(
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|PipelineStore
operator|.
name|INDEX
argument_list|,
literal|0
argument_list|,
literal|"_node_id"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|,
literal|1
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
literal|""
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|indexRoutingTableBuilder
operator|.
name|addReplica
argument_list|()
expr_stmt|;
name|routingTableBuilder
operator|.
name|add
argument_list|(
name|indexRoutingTableBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|csBuilder
operator|.
name|metaData
argument_list|(
name|metaDateBuilder
argument_list|)
expr_stmt|;
name|csBuilder
operator|.
name|routingTable
argument_list|(
name|routingTableBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterState
name|cs
init|=
name|csBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// We're not running and the cluster state isn't ready, so we don't start.
name|bootstrapper
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|cs
argument_list|,
name|cs
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|stop
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
comment|// We're running and the cluster state indicates that all our shards are unassigned, so we stop.
name|when
argument_list|(
name|store
operator|.
name|isStarted
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bootstrapper
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|cs
argument_list|,
name|cs
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|stop
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPipelineStoreBootstrappingIngestIndexShardsStarted
specifier|public
name|void
name|testPipelineStoreBootstrappingIngestIndexShardsStarted
parameter_list|()
throws|throws
name|Exception
block|{
comment|// .ingest index, but not all primary shards started:
name|ClusterState
operator|.
name|Builder
name|csBuilder
init|=
operator|new
name|ClusterState
operator|.
name|Builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"_name"
argument_list|)
argument_list|)
decl_stmt|;
name|MetaData
operator|.
name|Builder
name|metaDateBuilder
init|=
name|MetaData
operator|.
name|builder
argument_list|()
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routingTableBuilder
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|metaDateBuilder
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|PipelineStore
operator|.
name|INDEX
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|IndexRoutingTable
operator|.
name|Builder
name|indexRoutingTableBuilder
init|=
name|IndexRoutingTable
operator|.
name|builder
argument_list|(
name|PipelineStore
operator|.
name|INDEX
argument_list|)
decl_stmt|;
name|indexRoutingTableBuilder
operator|.
name|addIndexShard
argument_list|(
operator|new
name|IndexShardRoutingTable
operator|.
name|Builder
argument_list|(
operator|new
name|ShardId
argument_list|(
name|PipelineStore
operator|.
name|INDEX
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|addShard
argument_list|(
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|PipelineStore
operator|.
name|INDEX
argument_list|,
literal|0
argument_list|,
literal|"_node_id"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|,
literal|1
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
literal|""
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|indexRoutingTableBuilder
operator|.
name|addReplica
argument_list|()
expr_stmt|;
name|routingTableBuilder
operator|.
name|add
argument_list|(
name|indexRoutingTableBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|csBuilder
operator|.
name|metaData
argument_list|(
name|metaDateBuilder
argument_list|)
expr_stmt|;
name|csBuilder
operator|.
name|routingTable
argument_list|(
name|routingTableBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterState
name|cs
init|=
name|csBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// We're not running and the cluster state is ready, so we start.
name|bootstrapper
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|cs
argument_list|,
name|cs
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|stop
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
comment|// We're running and the cluster state is good, so we do nothing.
name|when
argument_list|(
name|store
operator|.
name|isStarted
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bootstrapper
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|cs
argument_list|,
name|cs
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|stop
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPipelineStoreBootstrappingFailure
specifier|public
name|void
name|testPipelineStoreBootstrappingFailure
parameter_list|()
throws|throws
name|Exception
block|{
comment|// .ingest index, but not all primary shards started:
name|ClusterState
operator|.
name|Builder
name|csBuilder
init|=
operator|new
name|ClusterState
operator|.
name|Builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"_name"
argument_list|)
argument_list|)
decl_stmt|;
name|MetaData
operator|.
name|Builder
name|metaDateBuilder
init|=
name|MetaData
operator|.
name|builder
argument_list|()
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routingTableBuilder
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|metaDateBuilder
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|PipelineStore
operator|.
name|INDEX
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|IndexRoutingTable
operator|.
name|Builder
name|indexRoutingTableBuilder
init|=
name|IndexRoutingTable
operator|.
name|builder
argument_list|(
name|PipelineStore
operator|.
name|INDEX
argument_list|)
decl_stmt|;
name|indexRoutingTableBuilder
operator|.
name|addIndexShard
argument_list|(
operator|new
name|IndexShardRoutingTable
operator|.
name|Builder
argument_list|(
operator|new
name|ShardId
argument_list|(
name|PipelineStore
operator|.
name|INDEX
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|addShard
argument_list|(
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|PipelineStore
operator|.
name|INDEX
argument_list|,
literal|0
argument_list|,
literal|"_node_id"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|,
literal|1
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
literal|""
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|indexRoutingTableBuilder
operator|.
name|addReplica
argument_list|()
expr_stmt|;
name|routingTableBuilder
operator|.
name|add
argument_list|(
name|indexRoutingTableBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|csBuilder
operator|.
name|metaData
argument_list|(
name|metaDateBuilder
argument_list|)
expr_stmt|;
name|csBuilder
operator|.
name|routingTable
argument_list|(
name|routingTableBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterState
name|cs
init|=
name|csBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// fail the first call with an runtime exception and subsequent calls just return:
name|doThrow
argument_list|(
operator|new
name|RuntimeException
argument_list|()
argument_list|)
operator|.
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|store
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|bootstrapper
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|cs
argument_list|,
name|cs
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|store
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|stop
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

