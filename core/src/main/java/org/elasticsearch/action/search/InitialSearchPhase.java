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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|ParameterizedMessage
import|;
end_import

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
name|util
operator|.
name|Supplier
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
name|NoShardAvailableActionException
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
name|TransportActions
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
name|GroupShardsIterator
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
name|util
operator|.
name|concurrent
operator|.
name|AtomicArray
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
name|SearchPhaseResult
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
name|transport
operator|.
name|ConnectTransportException
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
name|stream
operator|.
name|Stream
import|;
end_import

begin_comment
comment|/**  * This is an abstract base class that encapsulates the logic to fan out to all shards in provided {@link GroupShardsIterator}  * and collect the results. If a shard request returns a failure this class handles the advance to the next replica of the shard until  * the shards replica iterator is exhausted. Each shard is referenced by position in the {@link GroupShardsIterator} which is later  * referred to as the<tt>shardIndex</tt>.  * The fan out and collect algorithm is traditionally used as the initial phase which can either be a query execution or collection  * distributed frequencies  */
end_comment

begin_class
DECL|class|InitialSearchPhase
specifier|abstract
class|class
name|InitialSearchPhase
parameter_list|<
name|FirstResult
extends|extends
name|SearchPhaseResult
parameter_list|>
extends|extends
name|SearchPhase
block|{
DECL|field|request
specifier|private
specifier|final
name|SearchRequest
name|request
decl_stmt|;
DECL|field|shardsIts
specifier|private
specifier|final
name|GroupShardsIterator
argument_list|<
name|SearchShardIterator
argument_list|>
name|shardsIts
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|field|expectedTotalOps
specifier|private
specifier|final
name|int
name|expectedTotalOps
decl_stmt|;
DECL|field|totalOps
specifier|private
specifier|final
name|AtomicInteger
name|totalOps
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|InitialSearchPhase
name|InitialSearchPhase
parameter_list|(
name|String
name|name
parameter_list|,
name|SearchRequest
name|request
parameter_list|,
name|GroupShardsIterator
argument_list|<
name|SearchShardIterator
argument_list|>
name|shardsIts
parameter_list|,
name|Logger
name|logger
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|shardsIts
operator|=
name|shardsIts
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
comment|// we need to add 1 for non active partition, since we count it in the total. This means for each shard in the iterator we sum up
comment|// it's number of active shards but use 1 as the default if no replica of a shard is active at this point.
comment|// on a per shards level we use shardIt.remaining() to increment the totalOps pointer but add 1 for the current shard result
comment|// we process hence we add one for the non active partition here.
name|this
operator|.
name|expectedTotalOps
operator|=
name|shardsIts
operator|.
name|totalSizeWith1ForEmpty
argument_list|()
expr_stmt|;
block|}
DECL|method|onShardFailure
specifier|private
name|void
name|onShardFailure
parameter_list|(
specifier|final
name|int
name|shardIndex
parameter_list|,
annotation|@
name|Nullable
name|ShardRouting
name|shard
parameter_list|,
annotation|@
name|Nullable
name|String
name|nodeId
parameter_list|,
specifier|final
name|SearchShardIterator
name|shardIt
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
comment|// we always add the shard failure for a specific shard instance
comment|// we do make sure to clean it on a successful response from a shard
name|SearchShardTarget
name|shardTarget
init|=
operator|new
name|SearchShardTarget
argument_list|(
name|nodeId
argument_list|,
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|,
name|shardIt
operator|.
name|getClusterAlias
argument_list|()
argument_list|,
name|shardIt
operator|.
name|getOriginalIndices
argument_list|()
argument_list|)
decl_stmt|;
name|onShardFailure
argument_list|(
name|shardIndex
argument_list|,
name|shardTarget
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|totalOps
operator|.
name|incrementAndGet
argument_list|()
operator|==
name|expectedTotalOps
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|e
operator|!=
literal|null
operator|&&
operator|!
name|TransportActions
operator|.
name|isShardNotAvailableException
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"{}: Failed to execute [{}]"
argument_list|,
name|shard
operator|!=
literal|null
condition|?
name|shard
operator|.
name|shortSummary
argument_list|()
else|:
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|,
name|request
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"{}: Failed to execute [{}]"
argument_list|,
name|shard
argument_list|,
name|request
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|onPhaseDone
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|ShardRouting
name|nextShard
init|=
name|shardIt
operator|.
name|nextOrNull
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|lastShard
init|=
name|nextShard
operator|==
literal|null
decl_stmt|;
comment|// trace log this exception
name|logger
operator|.
name|trace
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"{}: Failed to execute [{}] lastShard [{}]"
argument_list|,
name|shard
operator|!=
literal|null
condition|?
name|shard
operator|.
name|shortSummary
argument_list|()
else|:
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|,
name|request
argument_list|,
name|lastShard
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|lastShard
condition|)
block|{
try|try
block|{
name|performPhaseOnShard
argument_list|(
name|shardIndex
argument_list|,
name|shardIt
argument_list|,
name|nextShard
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|inner
parameter_list|)
block|{
name|inner
operator|.
name|addSuppressed
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|onShardFailure
argument_list|(
name|shardIndex
argument_list|,
name|shard
argument_list|,
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|shardIt
argument_list|,
name|inner
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// no more shards active, add a failure
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
operator|!
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
comment|// do not double log this exception
if|if
condition|(
name|e
operator|!=
literal|null
operator|&&
operator|!
name|TransportActions
operator|.
name|isShardNotAvailableException
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"{}: Failed to execute [{}] lastShard [{}]"
argument_list|,
name|shard
operator|!=
literal|null
condition|?
name|shard
operator|.
name|shortSummary
argument_list|()
else|:
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|,
name|request
argument_list|,
name|lastShard
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
specifier|final
name|void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|shardIndex
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
specifier|final
name|SearchShardIterator
name|shardIt
range|:
name|shardsIts
control|)
block|{
name|shardIndex
operator|++
expr_stmt|;
specifier|final
name|ShardRouting
name|shard
init|=
name|shardIt
operator|.
name|nextOrNull
argument_list|()
decl_stmt|;
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
name|performPhaseOnShard
argument_list|(
name|shardIndex
argument_list|,
name|shardIt
argument_list|,
name|shard
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// really, no shards active in this group
name|onShardFailure
argument_list|(
name|shardIndex
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|shardIt
argument_list|,
operator|new
name|NoShardAvailableActionException
argument_list|(
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|performPhaseOnShard
specifier|private
name|void
name|performPhaseOnShard
parameter_list|(
specifier|final
name|int
name|shardIndex
parameter_list|,
specifier|final
name|SearchShardIterator
name|shardIt
parameter_list|,
specifier|final
name|ShardRouting
name|shard
parameter_list|)
block|{
if|if
condition|(
name|shard
operator|==
literal|null
condition|)
block|{
comment|// TODO upgrade this to an assert...
comment|// no more active shards... (we should not really get here, but just for safety)
name|onShardFailure
argument_list|(
name|shardIndex
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|shardIt
argument_list|,
operator|new
name|NoShardAvailableActionException
argument_list|(
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|executePhaseOnShard
argument_list|(
name|shardIt
argument_list|,
name|shard
argument_list|,
operator|new
name|SearchActionListener
argument_list|<
name|FirstResult
argument_list|>
argument_list|(
operator|new
name|SearchShardTarget
argument_list|(
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|,
name|shardIt
operator|.
name|getClusterAlias
argument_list|()
argument_list|,
name|shardIt
operator|.
name|getOriginalIndices
argument_list|()
argument_list|)
argument_list|,
name|shardIndex
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|innerOnResponse
parameter_list|(
name|FirstResult
name|result
parameter_list|)
block|{
name|onShardResult
argument_list|(
name|result
argument_list|,
name|shardIt
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|t
parameter_list|)
block|{
name|onShardFailure
argument_list|(
name|shardIndex
argument_list|,
name|shard
argument_list|,
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|shardIt
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConnectTransportException
decl||
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// we are getting the connection early here so we might run into nodes that are not connected. in that case we move on to
comment|// the next shard. previously when using discovery nodes here we had a special case for null when a node was not connected
comment|// at all which is not not needed anymore.
name|onShardFailure
argument_list|(
name|shardIndex
argument_list|,
name|shard
argument_list|,
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|shardIt
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|onShardResult
specifier|private
name|void
name|onShardResult
parameter_list|(
name|FirstResult
name|result
parameter_list|,
name|ShardIterator
name|shardIt
parameter_list|)
block|{
assert|assert
name|result
operator|.
name|getShardIndex
argument_list|()
operator|!=
operator|-
literal|1
operator|:
literal|"shard index is not set"
assert|;
assert|assert
name|result
operator|.
name|getSearchShardTarget
argument_list|()
operator|!=
literal|null
operator|:
literal|"search shard target must not be null"
assert|;
name|onShardSuccess
argument_list|(
name|result
argument_list|)
expr_stmt|;
comment|// we need to increment successful ops first before we compare the exit condition otherwise if we
comment|// are fast we could concurrently update totalOps but then preempt one of the threads which can
comment|// cause the successor to read a wrong value from successfulOps if second phase is very fast ie. count etc.
comment|// increment all the "future" shards to update the total ops since we some may work and some may not...
comment|// and when that happens, we break on total ops, so we must maintain them
specifier|final
name|int
name|xTotalOps
init|=
name|totalOps
operator|.
name|addAndGet
argument_list|(
name|shardIt
operator|.
name|remaining
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|xTotalOps
operator|==
name|expectedTotalOps
condition|)
block|{
name|onPhaseDone
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|xTotalOps
operator|>
name|expectedTotalOps
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unexpected higher total ops ["
operator|+
name|xTotalOps
operator|+
literal|"] compared to expected ["
operator|+
name|expectedTotalOps
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Executed once all shard results have been received and processed      * @see #onShardFailure(int, SearchShardTarget, Exception)      * @see #onShardSuccess(SearchPhaseResult)      */
DECL|method|onPhaseDone
specifier|abstract
name|void
name|onPhaseDone
parameter_list|()
function_decl|;
comment|// as a tribute to @kimchy aka. finishHim()
comment|/**      * Executed once for every failed shard level request. This method is invoked before the next replica is tried for the given      * shard target.      * @param shardIndex the internal index for this shard. Each shard has an index / ordinal assigned that is used to reference      *                   it's results      * @param shardTarget the shard target for this failure      * @param ex the failure reason      */
DECL|method|onShardFailure
specifier|abstract
name|void
name|onShardFailure
parameter_list|(
name|int
name|shardIndex
parameter_list|,
name|SearchShardTarget
name|shardTarget
parameter_list|,
name|Exception
name|ex
parameter_list|)
function_decl|;
comment|/**      * Executed once for every successful shard level request.      * @param result the result returned form the shard      *      */
DECL|method|onShardSuccess
specifier|abstract
name|void
name|onShardSuccess
parameter_list|(
name|FirstResult
name|result
parameter_list|)
function_decl|;
comment|/**      * Sends the request to the actual shard.      * @param shardIt the shards iterator      * @param shard the shard routing to send the request for      * @param listener the listener to notify on response      */
DECL|method|executePhaseOnShard
specifier|protected
specifier|abstract
name|void
name|executePhaseOnShard
parameter_list|(
name|SearchShardIterator
name|shardIt
parameter_list|,
name|ShardRouting
name|shard
parameter_list|,
name|SearchActionListener
argument_list|<
name|FirstResult
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * This class acts as a basic result collection that can be extended to do on-the-fly reduction or result processing      */
DECL|class|SearchPhaseResults
specifier|static
class|class
name|SearchPhaseResults
parameter_list|<
name|Result
extends|extends
name|SearchPhaseResult
parameter_list|>
block|{
DECL|field|results
specifier|final
name|AtomicArray
argument_list|<
name|Result
argument_list|>
name|results
decl_stmt|;
DECL|method|SearchPhaseResults
name|SearchPhaseResults
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|results
operator|=
operator|new
name|AtomicArray
argument_list|<>
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**          * Returns the number of expected results this class should collect          */
DECL|method|getNumShards
specifier|final
name|int
name|getNumShards
parameter_list|()
block|{
return|return
name|results
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**          * A stream of all non-null (successful) shard results          */
DECL|method|getSuccessfulResults
specifier|final
name|Stream
argument_list|<
name|Result
argument_list|>
name|getSuccessfulResults
parameter_list|()
block|{
return|return
name|results
operator|.
name|asList
argument_list|()
operator|.
name|stream
argument_list|()
return|;
block|}
comment|/**          * Consumes a single shard result          * @param result the shards result          */
DECL|method|consumeResult
name|void
name|consumeResult
parameter_list|(
name|Result
name|result
parameter_list|)
block|{
assert|assert
name|results
operator|.
name|get
argument_list|(
name|result
operator|.
name|getShardIndex
argument_list|()
argument_list|)
operator|==
literal|null
operator|:
literal|"shardIndex: "
operator|+
name|result
operator|.
name|getShardIndex
argument_list|()
operator|+
literal|" is already set"
assert|;
name|results
operator|.
name|set
argument_list|(
name|result
operator|.
name|getShardIndex
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/**          * Returns<code>true</code> iff a result if present for the given shard ID.          */
DECL|method|hasResult
specifier|final
name|boolean
name|hasResult
parameter_list|(
name|int
name|shardIndex
parameter_list|)
block|{
return|return
name|results
operator|.
name|get
argument_list|(
name|shardIndex
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**          * Reduces the collected results          */
DECL|method|reduce
name|SearchPhaseController
operator|.
name|ReducedQueryPhase
name|reduce
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"reduce is not supported"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

