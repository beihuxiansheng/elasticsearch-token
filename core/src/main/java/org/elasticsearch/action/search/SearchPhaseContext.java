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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executor
import|;
end_import

begin_comment
comment|/**  * This class provide contextual state and access to resources across multiple search phases.  */
end_comment

begin_interface
DECL|interface|SearchPhaseContext
interface|interface
name|SearchPhaseContext
extends|extends
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
extends|,
name|Executor
block|{
comment|// TODO maybe we can make this concrete later - for now we just implement this in the base class for all initial phases
comment|/**      * Returns the total number of shards to the current search across all indices      */
DECL|method|getNumShards
name|int
name|getNumShards
parameter_list|()
function_decl|;
comment|/**      * Returns a logger for this context to prevent each individual phase to create their own logger.      */
DECL|method|getLogger
name|Logger
name|getLogger
parameter_list|()
function_decl|;
comment|/**      * Returns the currently executing search task      */
DECL|method|getTask
name|SearchTask
name|getTask
parameter_list|()
function_decl|;
comment|/**      * Returns the currently executing search request      */
DECL|method|getRequest
name|SearchRequest
name|getRequest
parameter_list|()
function_decl|;
comment|/**      * Builds the final search response that should be send back to the user.      * @param internalSearchResponse the internal search response      * @param scrollId an optional scroll ID if this search is a scroll search      */
DECL|method|buildSearchResponse
name|SearchResponse
name|buildSearchResponse
parameter_list|(
name|InternalSearchResponse
name|internalSearchResponse
parameter_list|,
name|String
name|scrollId
parameter_list|)
function_decl|;
comment|/**      * This method will communicate a fatal phase failure back to the user. In contrast to a shard failure      * will this method immediately fail the search request and return the failure to the issuer of the request      * @param phase the phase that failed      * @param msg an optional message      * @param cause the cause of the phase failure      */
DECL|method|onPhaseFailure
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
function_decl|;
comment|/**      * This method will record a shard failure for the given shard index. In contrast to a phase failure      * ({@link #onPhaseFailure(SearchPhase, String, Throwable)}) this method will immediately return to the user but will record      * a shard failure for the given shard index. This should be called if a shard failure happens after we successfully retrieved      * a result from that shard in a previous phase.      */
DECL|method|onShardFailure
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
function_decl|;
comment|/**      * Returns a connection to the node if connected otherwise and {@link org.elasticsearch.transport.ConnectTransportException} will be      * thrown.      */
DECL|method|getConnection
name|Transport
operator|.
name|Connection
name|getConnection
parameter_list|(
name|String
name|nodeId
parameter_list|)
function_decl|;
comment|/**      * Returns the {@link SearchTransportService} to send shard request to other nodes      */
DECL|method|getSearchTransport
name|SearchTransportService
name|getSearchTransport
parameter_list|()
function_decl|;
comment|/**      * Releases a search context with the given context ID on the node the given connection is connected to.      * @see org.elasticsearch.search.query.QuerySearchResult#id()      * @see org.elasticsearch.search.fetch.FetchSearchResult#id()      *      */
DECL|method|sendReleaseSearchContext
specifier|default
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
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|getSearchTransport
argument_list|()
operator|.
name|sendFreeContext
argument_list|(
name|connection
argument_list|,
name|contextId
argument_list|,
name|getRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Builds an request for the initial search phase.      */
DECL|method|buildShardSearchRequest
name|ShardSearchTransportRequest
name|buildShardSearchRequest
parameter_list|(
name|ShardIterator
name|shardIt
parameter_list|,
name|ShardRouting
name|shard
parameter_list|)
function_decl|;
comment|/**      * Processes the phase transition from on phase to another. This method handles all errors that happen during the initial run execution      * of the next phase. If there are no successful operations in the context when this method is executed the search is aborted and      * a response is returned to the user indicating that all shards have failed.      */
DECL|method|executeNextPhase
name|void
name|executeNextPhase
parameter_list|(
name|SearchPhase
name|currentPhase
parameter_list|,
name|SearchPhase
name|nextPhase
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

