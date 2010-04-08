begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|action
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
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
name|search
operator|.
name|SearchService
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
name|dfs
operator|.
name|DfsSearchResult
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
name|fetch
operator|.
name|FetchSearchRequest
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
name|fetch
operator|.
name|FetchSearchResult
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
name|fetch
operator|.
name|QueryFetchSearchResult
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
name|InternalScrollSearchRequest
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
name|InternalSearchRequest
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
name|query
operator|.
name|QuerySearchRequest
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
name|query
operator|.
name|QuerySearchResult
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|stream
operator|.
name|LongStreamable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|stream
operator|.
name|VoidStreamable
import|;
end_import

begin_comment
comment|/**  * An encapsulation of {@link org.elasticsearch.search.SearchService} operations exposed through  * transport.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|SearchServiceTransportAction
specifier|public
class|class
name|SearchServiceTransportAction
block|{
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|searchService
specifier|private
specifier|final
name|SearchService
name|searchService
decl_stmt|;
DECL|method|SearchServiceTransportAction
annotation|@
name|Inject
specifier|public
name|SearchServiceTransportAction
parameter_list|(
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|SearchService
name|searchService
parameter_list|)
block|{
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|searchService
operator|=
name|searchService
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|SearchFreeContextTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|SearchFreeContextTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|SearchDfsTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|SearchDfsTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|SearchQueryTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|SearchQueryTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|SearchQueryByIdTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|SearchQueryByIdTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|SearchQueryScrollTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|SearchQueryScrollTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|SearchQueryFetchTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|SearchQueryFetchTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|SearchQueryQueryFetchTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|SearchQueryQueryFetchTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|SearchQueryFetchScrollTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|SearchQueryFetchScrollTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|SearchFetchByIdTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|SearchFetchByIdTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|sendFreeContext
specifier|public
name|void
name|sendFreeContext
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|long
name|contextId
parameter_list|)
block|{
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
name|searchService
operator|.
name|freeContext
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|SearchFreeContextTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|LongStreamable
argument_list|(
name|contextId
argument_list|)
argument_list|,
name|VoidTransportResponseHandler
operator|.
name|INSTANCE_NOSPAWN
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sendExecuteDfs
specifier|public
name|void
name|sendExecuteDfs
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|InternalSearchRequest
name|request
parameter_list|,
specifier|final
name|SearchServiceListener
argument_list|<
name|DfsSearchResult
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|DfsSearchResult
name|result
init|=
name|searchService
operator|.
name|executeDfsPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onResult
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
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|SearchDfsTransportHandler
operator|.
name|ACTION
argument_list|,
name|request
argument_list|,
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|DfsSearchResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DfsSearchResult
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|DfsSearchResult
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|DfsSearchResult
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResult
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|RemoteTransportException
name|exp
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
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
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sendExecuteQuery
specifier|public
name|void
name|sendExecuteQuery
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|InternalSearchRequest
name|request
parameter_list|,
specifier|final
name|SearchServiceListener
argument_list|<
name|QuerySearchResult
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|QuerySearchResult
name|result
init|=
name|searchService
operator|.
name|executeQueryPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onResult
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
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|SearchQueryTransportHandler
operator|.
name|ACTION
argument_list|,
name|request
argument_list|,
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|QuerySearchResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|QuerySearchResult
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|QuerySearchResult
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|QuerySearchResult
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResult
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|RemoteTransportException
name|exp
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
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
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sendExecuteQuery
specifier|public
name|void
name|sendExecuteQuery
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|QuerySearchRequest
name|request
parameter_list|,
specifier|final
name|SearchServiceListener
argument_list|<
name|QuerySearchResult
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|QuerySearchResult
name|result
init|=
name|searchService
operator|.
name|executeQueryPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onResult
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
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|SearchQueryByIdTransportHandler
operator|.
name|ACTION
argument_list|,
name|request
argument_list|,
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|QuerySearchResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|QuerySearchResult
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|QuerySearchResult
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|QuerySearchResult
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResult
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|RemoteTransportException
name|exp
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
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
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sendExecuteQuery
specifier|public
name|void
name|sendExecuteQuery
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|InternalScrollSearchRequest
name|request
parameter_list|,
specifier|final
name|SearchServiceListener
argument_list|<
name|QuerySearchResult
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|QuerySearchResult
name|result
init|=
name|searchService
operator|.
name|executeQueryPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onResult
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
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|SearchQueryScrollTransportHandler
operator|.
name|ACTION
argument_list|,
name|request
argument_list|,
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|QuerySearchResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|QuerySearchResult
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|QuerySearchResult
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|QuerySearchResult
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResult
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|RemoteTransportException
name|exp
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
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
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sendExecuteFetch
specifier|public
name|void
name|sendExecuteFetch
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|InternalSearchRequest
name|request
parameter_list|,
specifier|final
name|SearchServiceListener
argument_list|<
name|QueryFetchSearchResult
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|QueryFetchSearchResult
name|result
init|=
name|searchService
operator|.
name|executeFetchPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onResult
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
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|SearchQueryFetchTransportHandler
operator|.
name|ACTION
argument_list|,
name|request
argument_list|,
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|QueryFetchSearchResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|QueryFetchSearchResult
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|QueryFetchSearchResult
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|QueryFetchSearchResult
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResult
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|RemoteTransportException
name|exp
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
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
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sendExecuteFetch
specifier|public
name|void
name|sendExecuteFetch
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|QuerySearchRequest
name|request
parameter_list|,
specifier|final
name|SearchServiceListener
argument_list|<
name|QueryFetchSearchResult
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|QueryFetchSearchResult
name|result
init|=
name|searchService
operator|.
name|executeFetchPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onResult
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
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|SearchQueryQueryFetchTransportHandler
operator|.
name|ACTION
argument_list|,
name|request
argument_list|,
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|QueryFetchSearchResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|QueryFetchSearchResult
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|QueryFetchSearchResult
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|QueryFetchSearchResult
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResult
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|RemoteTransportException
name|exp
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
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
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sendExecuteFetch
specifier|public
name|void
name|sendExecuteFetch
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|InternalScrollSearchRequest
name|request
parameter_list|,
specifier|final
name|SearchServiceListener
argument_list|<
name|QueryFetchSearchResult
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|QueryFetchSearchResult
name|result
init|=
name|searchService
operator|.
name|executeFetchPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onResult
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
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|SearchQueryFetchScrollTransportHandler
operator|.
name|ACTION
argument_list|,
name|request
argument_list|,
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|QueryFetchSearchResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|QueryFetchSearchResult
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|QueryFetchSearchResult
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|QueryFetchSearchResult
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResult
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|RemoteTransportException
name|exp
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
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
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sendExecuteFetch
specifier|public
name|void
name|sendExecuteFetch
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|FetchSearchRequest
name|request
parameter_list|,
specifier|final
name|SearchServiceListener
argument_list|<
name|FetchSearchResult
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|FetchSearchResult
name|result
init|=
name|searchService
operator|.
name|executeFetchPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onResult
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
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|SearchFetchByIdTransportHandler
operator|.
name|ACTION
argument_list|,
name|request
argument_list|,
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|FetchSearchResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FetchSearchResult
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|FetchSearchResult
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|FetchSearchResult
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResult
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|RemoteTransportException
name|exp
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
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
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SearchFreeContextTransportHandler
specifier|private
class|class
name|SearchFreeContextTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|LongStreamable
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"search/freeContext"
decl_stmt|;
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|LongStreamable
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|LongStreamable
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
name|LongStreamable
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|searchService
operator|.
name|freeContext
argument_list|(
name|request
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|VoidStreamable
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SearchDfsTransportHandler
specifier|private
class|class
name|SearchDfsTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|InternalSearchRequest
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"search/phase/dfs"
decl_stmt|;
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|InternalSearchRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|InternalSearchRequest
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
name|InternalSearchRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|DfsSearchResult
name|result
init|=
name|searchService
operator|.
name|executeDfsPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SearchQueryTransportHandler
specifier|private
class|class
name|SearchQueryTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|InternalSearchRequest
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"search/phase/query"
decl_stmt|;
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|InternalSearchRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|InternalSearchRequest
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
name|InternalSearchRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|QuerySearchResult
name|result
init|=
name|searchService
operator|.
name|executeQueryPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SearchQueryByIdTransportHandler
specifier|private
class|class
name|SearchQueryByIdTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|QuerySearchRequest
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"search/phase/query/id"
decl_stmt|;
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|QuerySearchRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|QuerySearchRequest
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
name|QuerySearchRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|QuerySearchResult
name|result
init|=
name|searchService
operator|.
name|executeQueryPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SearchQueryScrollTransportHandler
specifier|private
class|class
name|SearchQueryScrollTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|InternalScrollSearchRequest
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"search/phase/query/scroll"
decl_stmt|;
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|InternalScrollSearchRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|InternalScrollSearchRequest
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
name|InternalScrollSearchRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|QuerySearchResult
name|result
init|=
name|searchService
operator|.
name|executeQueryPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SearchQueryFetchTransportHandler
specifier|private
class|class
name|SearchQueryFetchTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|InternalSearchRequest
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"search/phase/query+fetch"
decl_stmt|;
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|InternalSearchRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|InternalSearchRequest
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
name|InternalSearchRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryFetchSearchResult
name|result
init|=
name|searchService
operator|.
name|executeFetchPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SearchQueryQueryFetchTransportHandler
specifier|private
class|class
name|SearchQueryQueryFetchTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|QuerySearchRequest
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"search/phase/queyr/query+fetch"
decl_stmt|;
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|QuerySearchRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|QuerySearchRequest
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
name|QuerySearchRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryFetchSearchResult
name|result
init|=
name|searchService
operator|.
name|executeFetchPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SearchFetchByIdTransportHandler
specifier|private
class|class
name|SearchFetchByIdTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|FetchSearchRequest
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"search/phase/fetch/id"
decl_stmt|;
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|FetchSearchRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|FetchSearchRequest
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
name|FetchSearchRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|FetchSearchResult
name|result
init|=
name|searchService
operator|.
name|executeFetchPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SearchQueryFetchScrollTransportHandler
specifier|private
class|class
name|SearchQueryFetchScrollTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|InternalScrollSearchRequest
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"search/phase/query+fetch/scroll"
decl_stmt|;
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|InternalScrollSearchRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|InternalScrollSearchRequest
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
name|InternalScrollSearchRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryFetchSearchResult
name|result
init|=
name|searchService
operator|.
name|executeFetchPhase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

