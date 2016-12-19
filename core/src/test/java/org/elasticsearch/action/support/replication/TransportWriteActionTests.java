begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.replication
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|replication
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
name|support
operator|.
name|ActionFilters
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
name|WriteRequest
operator|.
name|RefreshPolicy
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
name|WriteResponse
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
name|IndexNameExpressionResolver
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
name|shard
operator|.
name|IndexShard
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
name|index
operator|.
name|translog
operator|.
name|Translog
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
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiConsumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
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

begin_class
DECL|class|TransportWriteActionTests
specifier|public
class|class
name|TransportWriteActionTests
extends|extends
name|ESTestCase
block|{
DECL|field|indexShard
specifier|private
name|IndexShard
name|indexShard
decl_stmt|;
DECL|field|location
specifier|private
name|Translog
operator|.
name|Location
name|location
decl_stmt|;
annotation|@
name|Before
DECL|method|initCommonMocks
specifier|public
name|void
name|initCommonMocks
parameter_list|()
block|{
name|indexShard
operator|=
name|mock
argument_list|(
name|IndexShard
operator|.
name|class
argument_list|)
expr_stmt|;
name|location
operator|=
name|mock
argument_list|(
name|Translog
operator|.
name|Location
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrimaryNoRefreshCall
specifier|public
name|void
name|testPrimaryNoRefreshCall
parameter_list|()
throws|throws
name|Exception
block|{
name|noRefreshCall
argument_list|(
name|TestAction
operator|::
name|shardOperationOnPrimary
argument_list|,
name|TestAction
operator|.
name|WritePrimaryResult
operator|::
name|respond
argument_list|)
expr_stmt|;
block|}
DECL|method|testReplicaNoRefreshCall
specifier|public
name|void
name|testReplicaNoRefreshCall
parameter_list|()
throws|throws
name|Exception
block|{
name|noRefreshCall
argument_list|(
name|TestAction
operator|::
name|shardOperationOnReplica
argument_list|,
name|TestAction
operator|.
name|WriteReplicaResult
operator|::
name|respond
argument_list|)
expr_stmt|;
block|}
DECL|method|noRefreshCall
specifier|private
parameter_list|<
name|Result
parameter_list|,
name|Response
parameter_list|>
name|void
name|noRefreshCall
parameter_list|(
name|ThrowingTriFunction
argument_list|<
name|TestAction
argument_list|,
name|TestRequest
argument_list|,
name|IndexShard
argument_list|,
name|Result
argument_list|>
name|action
parameter_list|,
name|BiConsumer
argument_list|<
name|Result
argument_list|,
name|CapturingActionListener
argument_list|<
name|Response
argument_list|>
argument_list|>
name|responder
parameter_list|)
throws|throws
name|Exception
block|{
name|TestRequest
name|request
init|=
operator|new
name|TestRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|NONE
argument_list|)
expr_stmt|;
comment|// The default, but we'll set it anyway just to be explicit
name|Result
name|result
init|=
name|action
operator|.
name|apply
argument_list|(
operator|new
name|TestAction
argument_list|()
argument_list|,
name|request
argument_list|,
name|indexShard
argument_list|)
decl_stmt|;
name|CapturingActionListener
argument_list|<
name|Response
argument_list|>
name|listener
init|=
operator|new
name|CapturingActionListener
argument_list|<>
argument_list|()
decl_stmt|;
name|responder
operator|.
name|accept
argument_list|(
name|result
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|listener
operator|.
name|response
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|failure
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|indexShard
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|refresh
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|indexShard
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|addRefreshListener
argument_list|(
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrimaryImmediateRefresh
specifier|public
name|void
name|testPrimaryImmediateRefresh
parameter_list|()
throws|throws
name|Exception
block|{
name|immediateRefresh
argument_list|(
name|TestAction
operator|::
name|shardOperationOnPrimary
argument_list|,
name|TestAction
operator|.
name|WritePrimaryResult
operator|::
name|respond
argument_list|,
name|r
lambda|->
name|assertTrue
argument_list|(
name|r
operator|.
name|forcedRefresh
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReplicaImmediateRefresh
specifier|public
name|void
name|testReplicaImmediateRefresh
parameter_list|()
throws|throws
name|Exception
block|{
name|immediateRefresh
argument_list|(
name|TestAction
operator|::
name|shardOperationOnReplica
argument_list|,
name|TestAction
operator|.
name|WriteReplicaResult
operator|::
name|respond
argument_list|,
name|r
lambda|->
block|{}
argument_list|)
expr_stmt|;
block|}
DECL|method|immediateRefresh
specifier|private
parameter_list|<
name|Result
parameter_list|,
name|Response
parameter_list|>
name|void
name|immediateRefresh
parameter_list|(
name|ThrowingTriFunction
argument_list|<
name|TestAction
argument_list|,
name|TestRequest
argument_list|,
name|IndexShard
argument_list|,
name|Result
argument_list|>
name|action
parameter_list|,
name|BiConsumer
argument_list|<
name|Result
argument_list|,
name|CapturingActionListener
argument_list|<
name|Response
argument_list|>
argument_list|>
name|responder
parameter_list|,
name|Consumer
argument_list|<
name|Response
argument_list|>
name|responseChecker
parameter_list|)
throws|throws
name|Exception
block|{
name|TestRequest
name|request
init|=
operator|new
name|TestRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|IMMEDIATE
argument_list|)
expr_stmt|;
name|Result
name|result
init|=
name|action
operator|.
name|apply
argument_list|(
operator|new
name|TestAction
argument_list|()
argument_list|,
name|request
argument_list|,
name|indexShard
argument_list|)
decl_stmt|;
name|CapturingActionListener
argument_list|<
name|Response
argument_list|>
name|listener
init|=
operator|new
name|CapturingActionListener
argument_list|<>
argument_list|()
decl_stmt|;
name|responder
operator|.
name|accept
argument_list|(
name|result
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|listener
operator|.
name|response
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|failure
argument_list|)
expr_stmt|;
name|responseChecker
operator|.
name|accept
argument_list|(
name|listener
operator|.
name|response
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|indexShard
argument_list|)
operator|.
name|refresh
argument_list|(
literal|"refresh_flag_index"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|indexShard
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|addRefreshListener
argument_list|(
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrimaryWaitForRefresh
specifier|public
name|void
name|testPrimaryWaitForRefresh
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForRefresh
argument_list|(
name|TestAction
operator|::
name|shardOperationOnPrimary
argument_list|,
name|TestAction
operator|.
name|WritePrimaryResult
operator|::
name|respond
argument_list|,
parameter_list|(
name|r
parameter_list|,
name|forcedRefresh
parameter_list|)
lambda|->
name|assertEquals
argument_list|(
name|forcedRefresh
argument_list|,
name|r
operator|.
name|forcedRefresh
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReplicaWaitForRefresh
specifier|public
name|void
name|testReplicaWaitForRefresh
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForRefresh
argument_list|(
name|TestAction
operator|::
name|shardOperationOnReplica
argument_list|,
name|TestAction
operator|.
name|WriteReplicaResult
operator|::
name|respond
argument_list|,
parameter_list|(
name|r
parameter_list|,
name|forcedRefresh
parameter_list|)
lambda|->
block|{}
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForRefresh
specifier|private
parameter_list|<
name|Result
parameter_list|,
name|Response
parameter_list|>
name|void
name|waitForRefresh
parameter_list|(
name|ThrowingTriFunction
argument_list|<
name|TestAction
argument_list|,
name|TestRequest
argument_list|,
name|IndexShard
argument_list|,
name|Result
argument_list|>
name|action
parameter_list|,
name|BiConsumer
argument_list|<
name|Result
argument_list|,
name|CapturingActionListener
argument_list|<
name|Response
argument_list|>
argument_list|>
name|responder
parameter_list|,
name|BiConsumer
argument_list|<
name|Response
argument_list|,
name|Boolean
argument_list|>
name|resultChecker
parameter_list|)
throws|throws
name|Exception
block|{
name|TestRequest
name|request
init|=
operator|new
name|TestRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|WAIT_UNTIL
argument_list|)
expr_stmt|;
name|Result
name|result
init|=
name|action
operator|.
name|apply
argument_list|(
operator|new
name|TestAction
argument_list|()
argument_list|,
name|request
argument_list|,
name|indexShard
argument_list|)
decl_stmt|;
name|CapturingActionListener
argument_list|<
name|Response
argument_list|>
name|listener
init|=
operator|new
name|CapturingActionListener
argument_list|<>
argument_list|()
decl_stmt|;
name|responder
operator|.
name|accept
argument_list|(
name|result
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|response
argument_list|)
expr_stmt|;
comment|// Haven't reallresponded yet
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
name|ArgumentCaptor
argument_list|<
name|Consumer
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|refreshListener
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
operator|(
name|Class
operator|)
name|Consumer
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|indexShard
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|refresh
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|indexShard
argument_list|)
operator|.
name|addRefreshListener
argument_list|(
name|any
argument_list|()
argument_list|,
name|refreshListener
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now we can fire the listener manually and we'll get a response
name|boolean
name|forcedRefresh
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|refreshListener
operator|.
name|getValue
argument_list|()
operator|.
name|accept
argument_list|(
name|forcedRefresh
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|listener
operator|.
name|response
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|failure
argument_list|)
expr_stmt|;
name|resultChecker
operator|.
name|accept
argument_list|(
name|listener
operator|.
name|response
argument_list|,
name|forcedRefresh
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocumentFailureInShardOperationOnPrimary
specifier|public
name|void
name|testDocumentFailureInShardOperationOnPrimary
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRequest
name|request
init|=
operator|new
name|TestRequest
argument_list|()
decl_stmt|;
name|TestAction
name|testAction
init|=
operator|new
name|TestAction
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TransportWriteAction
argument_list|<
name|TestRequest
argument_list|,
name|TestRequest
argument_list|,
name|TestResponse
argument_list|>
operator|.
name|WritePrimaryResult
name|writePrimaryResult
init|=
name|testAction
operator|.
name|shardOperationOnPrimary
argument_list|(
name|request
argument_list|,
name|indexShard
argument_list|)
decl_stmt|;
name|CapturingActionListener
argument_list|<
name|TestResponse
argument_list|>
name|listener
init|=
operator|new
name|CapturingActionListener
argument_list|<>
argument_list|()
decl_stmt|;
name|writePrimaryResult
operator|.
name|respond
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|response
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|listener
operator|.
name|failure
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocumentFailureInShardOperationOnReplica
specifier|public
name|void
name|testDocumentFailureInShardOperationOnReplica
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRequest
name|request
init|=
operator|new
name|TestRequest
argument_list|()
decl_stmt|;
name|TestAction
name|testAction
init|=
operator|new
name|TestAction
argument_list|(
name|randomBoolean
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TransportWriteAction
argument_list|<
name|TestRequest
argument_list|,
name|TestRequest
argument_list|,
name|TestResponse
argument_list|>
operator|.
name|WriteReplicaResult
name|writeReplicaResult
init|=
name|testAction
operator|.
name|shardOperationOnReplica
argument_list|(
name|request
argument_list|,
name|indexShard
argument_list|)
decl_stmt|;
name|CapturingActionListener
argument_list|<
name|TransportResponse
operator|.
name|Empty
argument_list|>
name|listener
init|=
operator|new
name|CapturingActionListener
argument_list|<>
argument_list|()
decl_stmt|;
name|writeReplicaResult
operator|.
name|respond
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|response
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|listener
operator|.
name|failure
argument_list|)
expr_stmt|;
block|}
DECL|class|TestAction
specifier|private
class|class
name|TestAction
extends|extends
name|TransportWriteAction
argument_list|<
name|TestRequest
argument_list|,
name|TestRequest
argument_list|,
name|TestResponse
argument_list|>
block|{
DECL|field|withDocumentFailureOnPrimary
specifier|private
specifier|final
name|boolean
name|withDocumentFailureOnPrimary
decl_stmt|;
DECL|field|withDocumentFailureOnReplica
specifier|private
specifier|final
name|boolean
name|withDocumentFailureOnReplica
decl_stmt|;
DECL|method|TestAction
specifier|protected
name|TestAction
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|TestAction
specifier|protected
name|TestAction
parameter_list|(
name|boolean
name|withDocumentFailureOnPrimary
parameter_list|,
name|boolean
name|withDocumentFailureOnReplica
parameter_list|)
block|{
name|super
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|"test"
argument_list|,
operator|new
name|TransportService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|TransportService
operator|.
name|NOOP_TRANSPORT_INTERCEPTOR
argument_list|,
literal|null
argument_list|)
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
name|ActionFilters
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
argument_list|,
operator|new
name|IndexNameExpressionResolver
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|,
name|TestRequest
operator|::
operator|new
argument_list|,
name|TestRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|withDocumentFailureOnPrimary
operator|=
name|withDocumentFailureOnPrimary
expr_stmt|;
name|this
operator|.
name|withDocumentFailureOnReplica
operator|=
name|withDocumentFailureOnReplica
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newResponseInstance
specifier|protected
name|TestResponse
name|newResponseInstance
parameter_list|()
block|{
return|return
operator|new
name|TestResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperationOnPrimary
specifier|protected
name|WritePrimaryResult
name|shardOperationOnPrimary
parameter_list|(
name|TestRequest
name|request
parameter_list|,
name|IndexShard
name|primary
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|WritePrimaryResult
name|primaryResult
decl_stmt|;
if|if
condition|(
name|withDocumentFailureOnPrimary
condition|)
block|{
name|primaryResult
operator|=
operator|new
name|WritePrimaryResult
argument_list|(
name|request
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|RuntimeException
argument_list|(
literal|"simulated"
argument_list|)
argument_list|,
name|primary
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|primaryResult
operator|=
operator|new
name|WritePrimaryResult
argument_list|(
name|request
argument_list|,
operator|new
name|TestResponse
argument_list|()
argument_list|,
name|location
argument_list|,
literal|null
argument_list|,
name|primary
argument_list|)
expr_stmt|;
block|}
return|return
name|primaryResult
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperationOnReplica
specifier|protected
name|WriteReplicaResult
name|shardOperationOnReplica
parameter_list|(
name|TestRequest
name|request
parameter_list|,
name|IndexShard
name|replica
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|WriteReplicaResult
name|replicaResult
decl_stmt|;
if|if
condition|(
name|withDocumentFailureOnReplica
condition|)
block|{
name|replicaResult
operator|=
operator|new
name|WriteReplicaResult
argument_list|(
name|request
argument_list|,
literal|null
argument_list|,
operator|new
name|RuntimeException
argument_list|(
literal|"simulated"
argument_list|)
argument_list|,
name|replica
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|replicaResult
operator|=
operator|new
name|WriteReplicaResult
argument_list|(
name|request
argument_list|,
name|location
argument_list|,
literal|null
argument_list|,
name|replica
argument_list|)
expr_stmt|;
block|}
return|return
name|replicaResult
return|;
block|}
block|}
DECL|class|TestRequest
specifier|private
specifier|static
class|class
name|TestRequest
extends|extends
name|ReplicatedWriteRequest
argument_list|<
name|TestRequest
argument_list|>
block|{
DECL|method|TestRequest
specifier|public
name|TestRequest
parameter_list|()
block|{
name|setShardId
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TestRequest{}"
return|;
block|}
block|}
DECL|class|TestResponse
specifier|private
specifier|static
class|class
name|TestResponse
extends|extends
name|ReplicationResponse
implements|implements
name|WriteResponse
block|{
DECL|field|forcedRefresh
name|boolean
name|forcedRefresh
decl_stmt|;
annotation|@
name|Override
DECL|method|setForcedRefresh
specifier|public
name|void
name|setForcedRefresh
parameter_list|(
name|boolean
name|forcedRefresh
parameter_list|)
block|{
name|this
operator|.
name|forcedRefresh
operator|=
name|forcedRefresh
expr_stmt|;
block|}
block|}
DECL|class|CapturingActionListener
specifier|private
specifier|static
class|class
name|CapturingActionListener
parameter_list|<
name|R
parameter_list|>
implements|implements
name|ActionListener
argument_list|<
name|R
argument_list|>
block|{
DECL|field|response
specifier|private
name|R
name|response
decl_stmt|;
DECL|field|failure
specifier|private
name|Exception
name|failure
decl_stmt|;
annotation|@
name|Override
DECL|method|onResponse
specifier|public
name|void
name|onResponse
parameter_list|(
name|R
name|response
parameter_list|)
block|{
name|this
operator|.
name|response
operator|=
name|response
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
name|failure
parameter_list|)
block|{
name|this
operator|.
name|failure
operator|=
name|failure
expr_stmt|;
block|}
block|}
DECL|interface|ThrowingTriFunction
specifier|private
interface|interface
name|ThrowingTriFunction
parameter_list|<
name|A
parameter_list|,
name|B
parameter_list|,
name|C
parameter_list|,
name|R
parameter_list|>
block|{
DECL|method|apply
name|R
name|apply
parameter_list|(
name|A
name|a
parameter_list|,
name|B
name|b
parameter_list|,
name|C
name|c
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
block|}
end_class

end_unit

