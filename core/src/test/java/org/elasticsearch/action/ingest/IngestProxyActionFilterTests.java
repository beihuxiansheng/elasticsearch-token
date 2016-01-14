begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
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
name|ActionRequest
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
name|bulk
operator|.
name|BulkAction
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
name|bulk
operator|.
name|BulkRequest
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
name|bulk
operator|.
name|BulkResponse
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
name|index
operator|.
name|IndexAction
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
name|index
operator|.
name|IndexRequest
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
name|index
operator|.
name|IndexResponse
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
name|ActionFilterChain
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
name|node
operator|.
name|DiscoveryNodes
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
name|node
operator|.
name|NodeModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|Task
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
name|test
operator|.
name|VersionUtils
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
name|org
operator|.
name|hamcrest
operator|.
name|CustomTypeSafeMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
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
name|Matchers
operator|.
name|argThat
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
name|eq
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
name|same
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
name|doAnswer
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
name|verifyZeroInteractions
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
DECL|class|IngestProxyActionFilterTests
specifier|public
class|class
name|IngestProxyActionFilterTests
extends|extends
name|ESTestCase
block|{
DECL|field|transportService
specifier|private
name|TransportService
name|transportService
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|buildFilter
specifier|private
name|IngestProxyActionFilter
name|buildFilter
parameter_list|(
name|int
name|ingestNodes
parameter_list|,
name|int
name|totalNodes
parameter_list|)
block|{
name|ClusterState
name|clusterState
init|=
name|mock
argument_list|(
name|ClusterState
operator|.
name|class
argument_list|)
decl_stmt|;
name|DiscoveryNodes
operator|.
name|Builder
name|builder
init|=
operator|new
name|DiscoveryNodes
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|DiscoveryNode
name|localNode
init|=
literal|null
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
name|totalNodes
condition|;
name|i
operator|++
control|)
block|{
name|String
name|nodeId
init|=
literal|"node"
operator|+
name|i
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|>=
name|ingestNodes
condition|)
block|{
name|attributes
operator|.
name|put
argument_list|(
literal|"ingest"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|attributes
operator|.
name|put
argument_list|(
literal|"ingest"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|DiscoveryNode
name|node
init|=
operator|new
name|DiscoveryNode
argument_list|(
name|nodeId
argument_list|,
name|nodeId
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|,
name|attributes
argument_list|,
name|VersionUtils
operator|.
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|totalNodes
operator|-
literal|1
condition|)
block|{
name|localNode
operator|=
name|node
expr_stmt|;
block|}
block|}
name|when
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|builder
operator|.
name|build
argument_list|()
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
name|when
argument_list|(
name|clusterService
operator|.
name|localNode
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|localNode
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|clusterState
argument_list|)
expr_stmt|;
name|transportService
operator|=
name|mock
argument_list|(
name|TransportService
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
operator|new
name|IngestProxyActionFilter
argument_list|(
name|clusterService
argument_list|,
name|transportService
argument_list|)
return|;
block|}
DECL|method|testApplyNoIngestNodes
specifier|public
name|void
name|testApplyNoIngestNodes
parameter_list|()
block|{
name|Task
name|task
init|=
name|mock
argument_list|(
name|Task
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionListener
name|actionListener
init|=
name|mock
argument_list|(
name|ActionListener
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionFilterChain
name|actionFilterChain
init|=
name|mock
argument_list|(
name|ActionFilterChain
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|totalNodes
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|IngestProxyActionFilter
name|filter
init|=
name|buildFilter
argument_list|(
literal|0
argument_list|,
name|totalNodes
argument_list|)
decl_stmt|;
name|String
name|action
decl_stmt|;
name|ActionRequest
name|request
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|action
operator|=
name|IndexAction
operator|.
name|NAME
expr_stmt|;
name|request
operator|=
operator|new
name|IndexRequest
argument_list|()
operator|.
name|pipeline
argument_list|(
literal|"_id"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|action
operator|=
name|BulkAction
operator|.
name|NAME
expr_stmt|;
name|request
operator|=
operator|new
name|BulkRequest
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|IndexRequest
argument_list|()
operator|.
name|pipeline
argument_list|(
literal|"_id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|filter
operator|.
name|apply
argument_list|(
name|task
argument_list|,
name|action
argument_list|,
name|request
argument_list|,
name|actionListener
argument_list|,
name|actionFilterChain
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed because there are no ingest nodes"
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
literal|"There are no ingest nodes in this cluster, unable to forward request to an ingest node."
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|verifyZeroInteractions
argument_list|(
name|transportService
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|actionFilterChain
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|actionListener
argument_list|)
expr_stmt|;
block|}
DECL|method|testApplyNoPipelineId
specifier|public
name|void
name|testApplyNoPipelineId
parameter_list|()
block|{
name|Task
name|task
init|=
name|mock
argument_list|(
name|Task
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionListener
name|actionListener
init|=
name|mock
argument_list|(
name|ActionListener
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionFilterChain
name|actionFilterChain
init|=
name|mock
argument_list|(
name|ActionFilterChain
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|totalNodes
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|IngestProxyActionFilter
name|filter
init|=
name|buildFilter
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|totalNodes
operator|-
literal|1
argument_list|)
argument_list|,
name|totalNodes
argument_list|)
decl_stmt|;
name|String
name|action
decl_stmt|;
name|ActionRequest
name|request
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|action
operator|=
name|IndexAction
operator|.
name|NAME
expr_stmt|;
name|request
operator|=
operator|new
name|IndexRequest
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|action
operator|=
name|BulkAction
operator|.
name|NAME
expr_stmt|;
name|request
operator|=
operator|new
name|BulkRequest
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|IndexRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|filter
operator|.
name|apply
argument_list|(
name|task
argument_list|,
name|action
argument_list|,
name|request
argument_list|,
name|actionListener
argument_list|,
name|actionFilterChain
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|transportService
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|actionFilterChain
argument_list|)
operator|.
name|proceed
argument_list|(
name|any
argument_list|(
name|Task
operator|.
name|class
argument_list|)
argument_list|,
name|eq
argument_list|(
name|action
argument_list|)
argument_list|,
name|same
argument_list|(
name|request
argument_list|)
argument_list|,
name|same
argument_list|(
name|actionListener
argument_list|)
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|actionListener
argument_list|)
expr_stmt|;
block|}
DECL|method|testApplyAnyAction
specifier|public
name|void
name|testApplyAnyAction
parameter_list|()
block|{
name|Task
name|task
init|=
name|mock
argument_list|(
name|Task
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionListener
name|actionListener
init|=
name|mock
argument_list|(
name|ActionListener
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionFilterChain
name|actionFilterChain
init|=
name|mock
argument_list|(
name|ActionFilterChain
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionRequest
name|request
init|=
name|mock
argument_list|(
name|ActionRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|totalNodes
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|IngestProxyActionFilter
name|filter
init|=
name|buildFilter
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|totalNodes
operator|-
literal|1
argument_list|)
argument_list|,
name|totalNodes
argument_list|)
decl_stmt|;
name|String
name|action
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|filter
operator|.
name|apply
argument_list|(
name|task
argument_list|,
name|action
argument_list|,
name|request
argument_list|,
name|actionListener
argument_list|,
name|actionFilterChain
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|transportService
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|actionFilterChain
argument_list|)
operator|.
name|proceed
argument_list|(
name|any
argument_list|(
name|Task
operator|.
name|class
argument_list|)
argument_list|,
name|eq
argument_list|(
name|action
argument_list|)
argument_list|,
name|same
argument_list|(
name|request
argument_list|)
argument_list|,
name|same
argument_list|(
name|actionListener
argument_list|)
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|actionListener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testApplyIndexRedirect
specifier|public
name|void
name|testApplyIndexRedirect
parameter_list|()
block|{
name|Task
name|task
init|=
name|mock
argument_list|(
name|Task
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionListener
name|actionListener
init|=
name|mock
argument_list|(
name|ActionListener
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionFilterChain
name|actionFilterChain
init|=
name|mock
argument_list|(
name|ActionFilterChain
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|totalNodes
init|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|IngestProxyActionFilter
name|filter
init|=
name|buildFilter
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|totalNodes
operator|-
literal|1
argument_list|)
argument_list|,
name|totalNodes
argument_list|)
decl_stmt|;
name|Answer
argument_list|<
name|Void
argument_list|>
name|answer
init|=
name|invocationOnMock
lambda|->
block|{
name|TransportResponseHandler
name|transportResponseHandler
init|=
operator|(
name|TransportResponseHandler
operator|)
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
index|[
literal|3
index|]
decl_stmt|;
name|transportResponseHandler
operator|.
name|handleResponse
argument_list|(
operator|new
name|IndexResponse
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
decl_stmt|;
name|doAnswer
argument_list|(
name|answer
argument_list|)
operator|.
name|when
argument_list|(
name|transportService
argument_list|)
operator|.
name|sendRequest
argument_list|(
name|any
argument_list|(
name|DiscoveryNode
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TransportRequest
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TransportResponseHandler
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|IndexRequest
name|indexRequest
init|=
operator|new
name|IndexRequest
argument_list|()
operator|.
name|pipeline
argument_list|(
literal|"_id"
argument_list|)
decl_stmt|;
name|filter
operator|.
name|apply
argument_list|(
name|task
argument_list|,
name|IndexAction
operator|.
name|NAME
argument_list|,
name|indexRequest
argument_list|,
name|actionListener
argument_list|,
name|actionFilterChain
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|transportService
argument_list|)
operator|.
name|sendRequest
argument_list|(
name|argThat
argument_list|(
operator|new
name|IngestNodeMatcher
argument_list|()
argument_list|)
argument_list|,
name|eq
argument_list|(
name|IndexAction
operator|.
name|NAME
argument_list|)
argument_list|,
name|same
argument_list|(
name|indexRequest
argument_list|)
argument_list|,
name|any
argument_list|(
name|TransportResponseHandler
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|actionFilterChain
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|actionListener
argument_list|)
operator|.
name|onResponse
argument_list|(
name|any
argument_list|(
name|IndexResponse
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|actionListener
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|onFailure
argument_list|(
name|any
argument_list|(
name|TransportException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testApplyBulkRedirect
specifier|public
name|void
name|testApplyBulkRedirect
parameter_list|()
block|{
name|Task
name|task
init|=
name|mock
argument_list|(
name|Task
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionListener
name|actionListener
init|=
name|mock
argument_list|(
name|ActionListener
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionFilterChain
name|actionFilterChain
init|=
name|mock
argument_list|(
name|ActionFilterChain
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|totalNodes
init|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|IngestProxyActionFilter
name|filter
init|=
name|buildFilter
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|totalNodes
operator|-
literal|1
argument_list|)
argument_list|,
name|totalNodes
argument_list|)
decl_stmt|;
name|Answer
argument_list|<
name|Void
argument_list|>
name|answer
init|=
name|invocationOnMock
lambda|->
block|{
name|TransportResponseHandler
name|transportResponseHandler
init|=
operator|(
name|TransportResponseHandler
operator|)
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
index|[
literal|3
index|]
decl_stmt|;
name|transportResponseHandler
operator|.
name|handleResponse
argument_list|(
operator|new
name|BulkResponse
argument_list|(
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
decl_stmt|;
name|doAnswer
argument_list|(
name|answer
argument_list|)
operator|.
name|when
argument_list|(
name|transportService
argument_list|)
operator|.
name|sendRequest
argument_list|(
name|any
argument_list|(
name|DiscoveryNode
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TransportRequest
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TransportResponseHandler
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|BulkRequest
name|bulkRequest
init|=
operator|new
name|BulkRequest
argument_list|()
decl_stmt|;
name|bulkRequest
operator|.
name|add
argument_list|(
operator|new
name|IndexRequest
argument_list|()
operator|.
name|pipeline
argument_list|(
literal|"_id"
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|numNoPipelineRequests
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
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
name|numNoPipelineRequests
condition|;
name|i
operator|++
control|)
block|{
name|bulkRequest
operator|.
name|add
argument_list|(
operator|new
name|IndexRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|filter
operator|.
name|apply
argument_list|(
name|task
argument_list|,
name|BulkAction
operator|.
name|NAME
argument_list|,
name|bulkRequest
argument_list|,
name|actionListener
argument_list|,
name|actionFilterChain
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|transportService
argument_list|)
operator|.
name|sendRequest
argument_list|(
name|argThat
argument_list|(
operator|new
name|IngestNodeMatcher
argument_list|()
argument_list|)
argument_list|,
name|eq
argument_list|(
name|BulkAction
operator|.
name|NAME
argument_list|)
argument_list|,
name|same
argument_list|(
name|bulkRequest
argument_list|)
argument_list|,
name|any
argument_list|(
name|TransportResponseHandler
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|actionFilterChain
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|actionListener
argument_list|)
operator|.
name|onResponse
argument_list|(
name|any
argument_list|(
name|BulkResponse
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|actionListener
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|onFailure
argument_list|(
name|any
argument_list|(
name|TransportException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testApplyFailures
specifier|public
name|void
name|testApplyFailures
parameter_list|()
block|{
name|Task
name|task
init|=
name|mock
argument_list|(
name|Task
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionListener
name|actionListener
init|=
name|mock
argument_list|(
name|ActionListener
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActionFilterChain
name|actionFilterChain
init|=
name|mock
argument_list|(
name|ActionFilterChain
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|totalNodes
init|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|IngestProxyActionFilter
name|filter
init|=
name|buildFilter
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|totalNodes
operator|-
literal|1
argument_list|)
argument_list|,
name|totalNodes
argument_list|)
decl_stmt|;
name|Answer
argument_list|<
name|Void
argument_list|>
name|answer
init|=
name|invocationOnMock
lambda|->
block|{
name|TransportResponseHandler
name|transportResponseHandler
init|=
operator|(
name|TransportResponseHandler
operator|)
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
index|[
literal|3
index|]
decl_stmt|;
name|transportResponseHandler
operator|.
name|handleException
argument_list|(
operator|new
name|TransportException
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
decl_stmt|;
name|doAnswer
argument_list|(
name|answer
argument_list|)
operator|.
name|when
argument_list|(
name|transportService
argument_list|)
operator|.
name|sendRequest
argument_list|(
name|any
argument_list|(
name|DiscoveryNode
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TransportRequest
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TransportResponseHandler
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|action
decl_stmt|;
name|ActionRequest
name|request
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|action
operator|=
name|IndexAction
operator|.
name|NAME
expr_stmt|;
name|request
operator|=
operator|new
name|IndexRequest
argument_list|()
operator|.
name|pipeline
argument_list|(
literal|"_id"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|action
operator|=
name|BulkAction
operator|.
name|NAME
expr_stmt|;
name|request
operator|=
operator|new
name|BulkRequest
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|IndexRequest
argument_list|()
operator|.
name|pipeline
argument_list|(
literal|"_id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|filter
operator|.
name|apply
argument_list|(
name|task
argument_list|,
name|action
argument_list|,
name|request
argument_list|,
name|actionListener
argument_list|,
name|actionFilterChain
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|transportService
argument_list|)
operator|.
name|sendRequest
argument_list|(
name|argThat
argument_list|(
operator|new
name|IngestNodeMatcher
argument_list|()
argument_list|)
argument_list|,
name|eq
argument_list|(
name|action
argument_list|)
argument_list|,
name|same
argument_list|(
name|request
argument_list|)
argument_list|,
name|any
argument_list|(
name|TransportResponseHandler
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|actionFilterChain
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|actionListener
argument_list|)
operator|.
name|onFailure
argument_list|(
name|any
argument_list|(
name|TransportException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|actionListener
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|onResponse
argument_list|(
name|any
argument_list|(
name|TransportResponse
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|IngestNodeMatcher
specifier|private
specifier|static
class|class
name|IngestNodeMatcher
extends|extends
name|CustomTypeSafeMatcher
argument_list|<
name|DiscoveryNode
argument_list|>
block|{
DECL|method|IngestNodeMatcher
specifier|private
name|IngestNodeMatcher
parameter_list|()
block|{
name|super
argument_list|(
literal|"discovery node should be an ingest node"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matchesSafely
specifier|protected
name|boolean
name|matchesSafely
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
return|return
name|NodeModule
operator|.
name|isNodeIngestEnabled
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

