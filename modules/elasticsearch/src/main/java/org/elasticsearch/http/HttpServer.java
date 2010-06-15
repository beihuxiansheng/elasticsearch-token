begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.http
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|info
operator|.
name|TransportNodesInfoAction
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
name|component
operator|.
name|AbstractLifecycleComponent
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
name|path
operator|.
name|PathTrie
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
name|rest
operator|.
name|RestRequest
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
name|XContentThrowableRestResponse
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|HttpServer
specifier|public
class|class
name|HttpServer
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|HttpServer
argument_list|>
block|{
DECL|field|transport
specifier|private
specifier|final
name|HttpServerTransport
name|transport
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|restController
specifier|private
specifier|final
name|RestController
name|restController
decl_stmt|;
DECL|field|nodesInfoAction
specifier|private
specifier|final
name|TransportNodesInfoAction
name|nodesInfoAction
decl_stmt|;
DECL|field|getHandlers
specifier|private
specifier|final
name|PathTrie
argument_list|<
name|HttpServerHandler
argument_list|>
name|getHandlers
init|=
operator|new
name|PathTrie
argument_list|<
name|HttpServerHandler
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|postHandlers
specifier|private
specifier|final
name|PathTrie
argument_list|<
name|HttpServerHandler
argument_list|>
name|postHandlers
init|=
operator|new
name|PathTrie
argument_list|<
name|HttpServerHandler
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|putHandlers
specifier|private
specifier|final
name|PathTrie
argument_list|<
name|HttpServerHandler
argument_list|>
name|putHandlers
init|=
operator|new
name|PathTrie
argument_list|<
name|HttpServerHandler
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|deleteHandlers
specifier|private
specifier|final
name|PathTrie
argument_list|<
name|HttpServerHandler
argument_list|>
name|deleteHandlers
init|=
operator|new
name|PathTrie
argument_list|<
name|HttpServerHandler
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|headHandlers
specifier|private
specifier|final
name|PathTrie
argument_list|<
name|HttpServerHandler
argument_list|>
name|headHandlers
init|=
operator|new
name|PathTrie
argument_list|<
name|HttpServerHandler
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|optionsHandlers
specifier|private
specifier|final
name|PathTrie
argument_list|<
name|HttpServerHandler
argument_list|>
name|optionsHandlers
init|=
operator|new
name|PathTrie
argument_list|<
name|HttpServerHandler
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|HttpServer
annotation|@
name|Inject
specifier|public
name|HttpServer
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|HttpServerTransport
name|transport
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|RestController
name|restController
parameter_list|,
name|TransportNodesInfoAction
name|nodesInfoAction
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|restController
operator|=
name|restController
expr_stmt|;
name|this
operator|.
name|nodesInfoAction
operator|=
name|nodesInfoAction
expr_stmt|;
name|transport
operator|.
name|httpServerAdapter
argument_list|(
operator|new
name|HttpServerAdapter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|dispatchRequest
parameter_list|(
name|HttpRequest
name|request
parameter_list|,
name|HttpChannel
name|channel
parameter_list|)
block|{
name|internalDispatchRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|registerHandler
specifier|public
name|void
name|registerHandler
parameter_list|(
name|HttpRequest
operator|.
name|Method
name|method
parameter_list|,
name|String
name|path
parameter_list|,
name|HttpServerHandler
name|handler
parameter_list|)
block|{
if|if
condition|(
name|method
operator|==
name|HttpRequest
operator|.
name|Method
operator|.
name|GET
condition|)
block|{
name|getHandlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|method
operator|==
name|HttpRequest
operator|.
name|Method
operator|.
name|POST
condition|)
block|{
name|postHandlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|method
operator|==
name|HttpRequest
operator|.
name|Method
operator|.
name|PUT
condition|)
block|{
name|putHandlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|method
operator|==
name|HttpRequest
operator|.
name|Method
operator|.
name|DELETE
condition|)
block|{
name|deleteHandlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|method
operator|==
name|RestRequest
operator|.
name|Method
operator|.
name|HEAD
condition|)
block|{
name|headHandlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|method
operator|==
name|RestRequest
operator|.
name|Method
operator|.
name|OPTIONS
condition|)
block|{
name|optionsHandlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doStart
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|transport
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"{}"
argument_list|,
name|transport
operator|.
name|boundAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|nodesInfoAction
operator|.
name|putNodeAttribute
argument_list|(
literal|"http_address"
argument_list|,
name|transport
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doStop
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|nodesInfoAction
operator|.
name|removeNodeAttribute
argument_list|(
literal|"http_address"
argument_list|)
expr_stmt|;
name|transport
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|doClose
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|transport
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|internalDispatchRequest
name|void
name|internalDispatchRequest
parameter_list|(
specifier|final
name|HttpRequest
name|request
parameter_list|,
specifier|final
name|HttpChannel
name|channel
parameter_list|)
block|{
specifier|final
name|HttpServerHandler
name|httpHandler
init|=
name|getHandler
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|httpHandler
operator|==
literal|null
condition|)
block|{
name|restController
operator|.
name|dispatchRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|httpHandler
operator|.
name|spawn
argument_list|()
condition|)
block|{
name|threadPool
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|httpHandler
operator|.
name|handleRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|XContentThrowableRestResponse
argument_list|(
name|request
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to send failure response for uri ["
operator|+
name|request
operator|.
name|uri
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|httpHandler
operator|.
name|handleRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|XContentThrowableRestResponse
argument_list|(
name|request
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to send failure response for uri ["
operator|+
name|request
operator|.
name|uri
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|getHandler
specifier|private
name|HttpServerHandler
name|getHandler
parameter_list|(
name|HttpRequest
name|request
parameter_list|)
block|{
name|String
name|path
init|=
name|getPath
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|HttpRequest
operator|.
name|Method
name|method
init|=
name|request
operator|.
name|method
argument_list|()
decl_stmt|;
if|if
condition|(
name|method
operator|==
name|HttpRequest
operator|.
name|Method
operator|.
name|GET
condition|)
block|{
return|return
name|getHandlers
operator|.
name|retrieve
argument_list|(
name|path
argument_list|,
name|request
operator|.
name|params
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|method
operator|==
name|HttpRequest
operator|.
name|Method
operator|.
name|POST
condition|)
block|{
return|return
name|postHandlers
operator|.
name|retrieve
argument_list|(
name|path
argument_list|,
name|request
operator|.
name|params
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|method
operator|==
name|HttpRequest
operator|.
name|Method
operator|.
name|PUT
condition|)
block|{
return|return
name|putHandlers
operator|.
name|retrieve
argument_list|(
name|path
argument_list|,
name|request
operator|.
name|params
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|method
operator|==
name|HttpRequest
operator|.
name|Method
operator|.
name|DELETE
condition|)
block|{
return|return
name|deleteHandlers
operator|.
name|retrieve
argument_list|(
name|path
argument_list|,
name|request
operator|.
name|params
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|method
operator|==
name|RestRequest
operator|.
name|Method
operator|.
name|HEAD
condition|)
block|{
return|return
name|headHandlers
operator|.
name|retrieve
argument_list|(
name|path
argument_list|,
name|request
operator|.
name|params
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|method
operator|==
name|RestRequest
operator|.
name|Method
operator|.
name|OPTIONS
condition|)
block|{
return|return
name|optionsHandlers
operator|.
name|retrieve
argument_list|(
name|path
argument_list|,
name|request
operator|.
name|params
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|getPath
specifier|private
name|String
name|getPath
parameter_list|(
name|HttpRequest
name|request
parameter_list|)
block|{
return|return
name|request
operator|.
name|path
argument_list|()
return|;
block|}
block|}
end_class

end_unit

