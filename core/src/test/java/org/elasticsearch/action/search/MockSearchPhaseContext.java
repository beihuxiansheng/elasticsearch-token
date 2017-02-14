begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|ShardIterator
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
name|common
operator|.
name|Nullable
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
name|Loggers
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
name|SearchShardTarget
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
name|InternalSearchResponse
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
name|ShardSearchTransportRequest
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
name|Transport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UncheckedIOException
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
name|HashSet
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
name|Set
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
name|AtomicInteger
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

begin_comment
comment|/**  * SearchPhaseContext for tests  */
end_comment

begin_class
DECL|class|MockSearchPhaseContext
specifier|public
specifier|final
class|class
name|MockSearchPhaseContext
implements|implements
name|SearchPhaseContext
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|MockSearchPhaseContext
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|phaseFailure
specifier|public
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|phaseFailure
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|numShards
specifier|final
name|int
name|numShards
decl_stmt|;
DECL|field|numSuccess
specifier|final
name|AtomicInteger
name|numSuccess
decl_stmt|;
DECL|field|failures
name|List
argument_list|<
name|ShardSearchFailure
argument_list|>
name|failures
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|searchTransport
name|SearchTransportService
name|searchTransport
decl_stmt|;
DECL|field|releasedSearchContexts
name|Set
argument_list|<
name|Long
argument_list|>
name|releasedSearchContexts
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|MockSearchPhaseContext
specifier|public
name|MockSearchPhaseContext
parameter_list|(
name|int
name|numShards
parameter_list|)
block|{
name|this
operator|.
name|numShards
operator|=
name|numShards
expr_stmt|;
name|numSuccess
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|numShards
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNoFailure
specifier|public
name|void
name|assertNoFailure
parameter_list|()
block|{
if|if
condition|(
name|phaseFailure
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|phaseFailure
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNumShards
specifier|public
name|int
name|getNumShards
parameter_list|()
block|{
return|return
name|numShards
return|;
block|}
annotation|@
name|Override
DECL|method|getLogger
specifier|public
name|Logger
name|getLogger
parameter_list|()
block|{
return|return
name|logger
return|;
block|}
annotation|@
name|Override
DECL|method|getTask
specifier|public
name|SearchTask
name|getTask
parameter_list|()
block|{
return|return
operator|new
name|SearchTask
argument_list|(
literal|0
argument_list|,
literal|"n/a"
argument_list|,
literal|"n/a"
argument_list|,
literal|"test"
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRequest
specifier|public
name|SearchRequest
name|getRequest
parameter_list|()
block|{
return|return
operator|new
name|SearchRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|buildSearchResponse
specifier|public
name|SearchResponse
name|buildSearchResponse
parameter_list|(
name|InternalSearchResponse
name|internalSearchResponse
parameter_list|,
name|String
name|scrollId
parameter_list|)
block|{
return|return
operator|new
name|SearchResponse
argument_list|(
name|internalSearchResponse
argument_list|,
name|scrollId
argument_list|,
name|numShards
argument_list|,
name|numSuccess
operator|.
name|get
argument_list|()
argument_list|,
literal|0
argument_list|,
name|failures
operator|.
name|toArray
argument_list|(
operator|new
name|ShardSearchFailure
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onPhaseFailure
specifier|public
name|void
name|onPhaseFailure
parameter_list|(
name|SearchPhase
name|phase
parameter_list|,
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|phaseFailure
operator|.
name|set
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onShardFailure
specifier|public
name|void
name|onShardFailure
parameter_list|(
name|int
name|shardIndex
parameter_list|,
annotation|@
name|Nullable
name|SearchShardTarget
name|shardTarget
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
name|failures
operator|.
name|add
argument_list|(
operator|new
name|ShardSearchFailure
argument_list|(
name|e
argument_list|,
name|shardTarget
argument_list|)
argument_list|)
expr_stmt|;
name|numSuccess
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConnection
specifier|public
name|Transport
operator|.
name|Connection
name|getConnection
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// null is ok here for this test
block|}
annotation|@
name|Override
DECL|method|getSearchTransport
specifier|public
name|SearchTransportService
name|getSearchTransport
parameter_list|()
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|searchTransport
argument_list|)
expr_stmt|;
return|return
name|searchTransport
return|;
block|}
annotation|@
name|Override
DECL|method|buildShardSearchRequest
specifier|public
name|ShardSearchTransportRequest
name|buildShardSearchRequest
parameter_list|(
name|ShardIterator
name|shardIt
parameter_list|,
name|ShardRouting
name|shard
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"should not be called"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|executeNextPhase
specifier|public
name|void
name|executeNextPhase
parameter_list|(
name|SearchPhase
name|currentPhase
parameter_list|,
name|SearchPhase
name|nextPhase
parameter_list|)
block|{
try|try
block|{
name|nextPhase
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|Runnable
name|command
parameter_list|)
block|{
name|command
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onResponse
specifier|public
name|void
name|onResponse
parameter_list|(
name|SearchResponse
name|response
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"should not be called"
argument_list|)
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
name|Assert
operator|.
name|fail
argument_list|(
literal|"should not be called"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendReleaseSearchContext
specifier|public
name|void
name|sendReleaseSearchContext
parameter_list|(
name|long
name|contextId
parameter_list|,
name|Transport
operator|.
name|Connection
name|connection
parameter_list|)
block|{
name|releasedSearchContexts
operator|.
name|add
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

