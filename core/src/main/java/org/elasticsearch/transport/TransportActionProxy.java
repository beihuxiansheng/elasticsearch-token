begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UncheckedIOException
import|;
end_import

begin_comment
comment|/**  * TransportActionProxy allows an arbitrary action to be executed on a defined target node while the initial request is send to a second  * node that acts as a request proxy to the target node. This is useful if a node is not directly connected to a target node but is  * connected to an intermediate node that establishes a transitive connection.  */
end_comment

begin_class
DECL|class|TransportActionProxy
specifier|public
specifier|final
class|class
name|TransportActionProxy
block|{
DECL|method|TransportActionProxy
specifier|private
name|TransportActionProxy
parameter_list|()
block|{}
comment|// no instance
DECL|class|ProxyRequestHandler
specifier|private
specifier|static
class|class
name|ProxyRequestHandler
parameter_list|<
name|T
extends|extends
name|ProxyRequest
parameter_list|>
implements|implements
name|TransportRequestHandler
argument_list|<
name|T
argument_list|>
block|{
DECL|field|service
specifier|private
specifier|final
name|TransportService
name|service
decl_stmt|;
DECL|field|action
specifier|private
specifier|final
name|String
name|action
decl_stmt|;
DECL|field|responseFactory
specifier|private
specifier|final
name|Supplier
argument_list|<
name|TransportResponse
argument_list|>
name|responseFactory
decl_stmt|;
DECL|method|ProxyRequestHandler
specifier|public
name|ProxyRequestHandler
parameter_list|(
name|TransportService
name|service
parameter_list|,
name|String
name|action
parameter_list|,
name|Supplier
argument_list|<
name|TransportResponse
argument_list|>
name|responseFactory
parameter_list|)
block|{
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|responseFactory
operator|=
name|responseFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|T
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|DiscoveryNode
name|targetNode
init|=
name|request
operator|.
name|targetNode
decl_stmt|;
name|TransportRequest
name|wrappedRequest
init|=
name|request
operator|.
name|wrapped
decl_stmt|;
name|service
operator|.
name|sendRequest
argument_list|(
name|targetNode
argument_list|,
name|action
argument_list|,
name|wrappedRequest
argument_list|,
operator|new
name|ProxyResponseHandler
argument_list|<>
argument_list|(
name|channel
argument_list|,
name|responseFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ProxyResponseHandler
specifier|private
specifier|static
class|class
name|ProxyResponseHandler
parameter_list|<
name|T
extends|extends
name|TransportResponse
parameter_list|>
implements|implements
name|TransportResponseHandler
argument_list|<
name|T
argument_list|>
block|{
DECL|field|responseFactory
specifier|private
specifier|final
name|Supplier
argument_list|<
name|T
argument_list|>
name|responseFactory
decl_stmt|;
DECL|field|channel
specifier|private
specifier|final
name|TransportChannel
name|channel
decl_stmt|;
DECL|method|ProxyResponseHandler
specifier|public
name|ProxyResponseHandler
parameter_list|(
name|TransportChannel
name|channel
parameter_list|,
name|Supplier
argument_list|<
name|T
argument_list|>
name|responseFactory
parameter_list|)
block|{
name|this
operator|.
name|responseFactory
operator|=
name|responseFactory
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newInstance
specifier|public
name|T
name|newInstance
parameter_list|()
block|{
return|return
name|responseFactory
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|handleResponse
specifier|public
name|void
name|handleResponse
parameter_list|(
name|T
name|response
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|response
argument_list|)
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
DECL|method|handleException
specifier|public
name|void
name|handleException
parameter_list|(
name|TransportException
name|exp
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|exp
argument_list|)
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
DECL|method|executor
specifier|public
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
return|;
block|}
block|}
DECL|class|ProxyRequest
specifier|static
class|class
name|ProxyRequest
parameter_list|<
name|T
extends|extends
name|TransportRequest
parameter_list|>
extends|extends
name|TransportRequest
block|{
DECL|field|wrapped
name|T
name|wrapped
decl_stmt|;
DECL|field|supplier
name|Supplier
argument_list|<
name|T
argument_list|>
name|supplier
decl_stmt|;
DECL|field|targetNode
name|DiscoveryNode
name|targetNode
decl_stmt|;
DECL|method|ProxyRequest
specifier|public
name|ProxyRequest
parameter_list|(
name|Supplier
argument_list|<
name|T
argument_list|>
name|supplier
parameter_list|)
block|{
name|this
operator|.
name|supplier
operator|=
name|supplier
expr_stmt|;
block|}
DECL|method|ProxyRequest
specifier|public
name|ProxyRequest
parameter_list|(
name|T
name|wrapped
parameter_list|,
name|DiscoveryNode
name|targetNode
parameter_list|)
block|{
name|this
operator|.
name|wrapped
operator|=
name|wrapped
expr_stmt|;
name|this
operator|.
name|targetNode
operator|=
name|targetNode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|targetNode
operator|=
operator|new
name|DiscoveryNode
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|wrapped
operator|=
name|supplier
operator|.
name|get
argument_list|()
expr_stmt|;
name|wrapped
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|targetNode
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|wrapped
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Registers a proxy request handler that allows to forward requests for the given action to another node.      */
DECL|method|registerProxyAction
specifier|public
specifier|static
name|void
name|registerProxyAction
parameter_list|(
name|TransportService
name|service
parameter_list|,
name|String
name|action
parameter_list|,
name|Supplier
argument_list|<
name|TransportResponse
argument_list|>
name|responseSupplier
parameter_list|)
block|{
name|RequestHandlerRegistry
name|requestHandler
init|=
name|service
operator|.
name|getRequestHandler
argument_list|(
name|action
argument_list|)
decl_stmt|;
name|service
operator|.
name|registerRequestHandler
argument_list|(
name|getProxyAction
argument_list|(
name|action
argument_list|)
argument_list|,
parameter_list|()
lambda|->
operator|new
name|ProxyRequest
argument_list|(
name|requestHandler
operator|::
name|newRequest
argument_list|)
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
operator|new
name|ProxyRequestHandler
argument_list|<>
argument_list|(
name|service
argument_list|,
name|action
argument_list|,
name|responseSupplier
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the corresponding proxy action for the given action      */
DECL|method|getProxyAction
specifier|public
specifier|static
name|String
name|getProxyAction
parameter_list|(
name|String
name|action
parameter_list|)
block|{
return|return
literal|"internal:transport/proxy/"
operator|+
name|action
return|;
block|}
comment|/**      * Wraps the actual request in a proxy request object that encodes the target node.      */
DECL|method|wrapRequest
specifier|public
specifier|static
name|TransportRequest
name|wrapRequest
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|TransportRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|ProxyRequest
argument_list|<>
argument_list|(
name|request
argument_list|,
name|node
argument_list|)
return|;
block|}
block|}
end_class

end_unit

