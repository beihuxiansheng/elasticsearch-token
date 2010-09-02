begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|TransportActions
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
name|search
operator|.
name|type
operator|.
name|TransportSearchDfsQueryAndFetchAction
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
name|search
operator|.
name|type
operator|.
name|TransportSearchDfsQueryThenFetchAction
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
name|search
operator|.
name|type
operator|.
name|TransportSearchQueryAndFetchAction
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
name|search
operator|.
name|type
operator|.
name|TransportSearchQueryThenFetchAction
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
name|BaseAction
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
name|inject
operator|.
name|Inject
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
name|transport
operator|.
name|BaseTransportRequestHandler
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
name|TransportChannel
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchType
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportSearchAction
specifier|public
class|class
name|TransportSearchAction
extends|extends
name|BaseAction
argument_list|<
name|SearchRequest
argument_list|,
name|SearchResponse
argument_list|>
block|{
DECL|field|dfsQueryThenFetchAction
specifier|private
specifier|final
name|TransportSearchDfsQueryThenFetchAction
name|dfsQueryThenFetchAction
decl_stmt|;
DECL|field|queryThenFetchAction
specifier|private
specifier|final
name|TransportSearchQueryThenFetchAction
name|queryThenFetchAction
decl_stmt|;
DECL|field|dfsQueryAndFetchAction
specifier|private
specifier|final
name|TransportSearchDfsQueryAndFetchAction
name|dfsQueryAndFetchAction
decl_stmt|;
DECL|field|queryAndFetchAction
specifier|private
specifier|final
name|TransportSearchQueryAndFetchAction
name|queryAndFetchAction
decl_stmt|;
DECL|method|TransportSearchAction
annotation|@
name|Inject
specifier|public
name|TransportSearchAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|TransportSearchDfsQueryThenFetchAction
name|dfsQueryThenFetchAction
parameter_list|,
name|TransportSearchQueryThenFetchAction
name|queryThenFetchAction
parameter_list|,
name|TransportSearchDfsQueryAndFetchAction
name|dfsQueryAndFetchAction
parameter_list|,
name|TransportSearchQueryAndFetchAction
name|queryAndFetchAction
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|dfsQueryThenFetchAction
operator|=
name|dfsQueryThenFetchAction
expr_stmt|;
name|this
operator|.
name|queryThenFetchAction
operator|=
name|queryThenFetchAction
expr_stmt|;
name|this
operator|.
name|dfsQueryAndFetchAction
operator|=
name|dfsQueryAndFetchAction
expr_stmt|;
name|this
operator|.
name|queryAndFetchAction
operator|=
name|queryAndFetchAction
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|TransportActions
operator|.
name|SEARCH
argument_list|,
operator|new
name|TransportHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doExecute
annotation|@
name|Override
specifier|protected
name|void
name|doExecute
parameter_list|(
name|SearchRequest
name|searchRequest
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|searchRequest
operator|.
name|searchType
argument_list|()
operator|==
name|DFS_QUERY_THEN_FETCH
condition|)
block|{
name|dfsQueryThenFetchAction
operator|.
name|execute
argument_list|(
name|searchRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|searchRequest
operator|.
name|searchType
argument_list|()
operator|==
name|SearchType
operator|.
name|QUERY_THEN_FETCH
condition|)
block|{
name|queryThenFetchAction
operator|.
name|execute
argument_list|(
name|searchRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|searchRequest
operator|.
name|searchType
argument_list|()
operator|==
name|SearchType
operator|.
name|DFS_QUERY_AND_FETCH
condition|)
block|{
name|dfsQueryAndFetchAction
operator|.
name|execute
argument_list|(
name|searchRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|searchRequest
operator|.
name|searchType
argument_list|()
operator|==
name|SearchType
operator|.
name|QUERY_AND_FETCH
condition|)
block|{
name|queryAndFetchAction
operator|.
name|execute
argument_list|(
name|searchRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TransportHandler
specifier|private
class|class
name|TransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|SearchRequest
argument_list|>
block|{
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|SearchRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|SearchRequest
argument_list|()
return|;
block|}
DECL|method|messageReceived
annotation|@
name|Override
specifier|public
name|void
name|messageReceived
parameter_list|(
name|SearchRequest
name|request
parameter_list|,
specifier|final
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
comment|// no need for a threaded listener
name|request
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we don't spawn, so if we get a request with no threading, change it to single threaded
if|if
condition|(
name|request
operator|.
name|operationThreading
argument_list|()
operator|==
name|SearchOperationThreading
operator|.
name|NO_THREADS
condition|)
block|{
name|request
operator|.
name|operationThreading
argument_list|(
name|SearchOperationThreading
operator|.
name|SINGLE_THREAD
argument_list|)
expr_stmt|;
block|}
name|execute
argument_list|(
name|request
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|SearchResponse
name|result
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to send response for search"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|spawn
annotation|@
name|Override
specifier|public
name|boolean
name|spawn
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

