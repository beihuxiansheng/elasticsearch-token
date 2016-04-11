begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|io
operator|.
name|Streams
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
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
name|service
operator|.
name|NodeService
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
name|BytesRestResponse
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
name|RestChannel
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
name|RestStatus
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|InputStream
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
operator|.
name|FORBIDDEN
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
operator|.
name|INTERNAL_SERVER_ERROR
import|;
end_import

begin_comment
comment|/**  * A component to serve http requests, backed by rest handlers.  */
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
implements|implements
name|HttpServerAdapter
block|{
DECL|field|environment
specifier|private
specifier|final
name|Environment
name|environment
decl_stmt|;
DECL|field|transport
specifier|private
specifier|final
name|HttpServerTransport
name|transport
decl_stmt|;
DECL|field|restController
specifier|private
specifier|final
name|RestController
name|restController
decl_stmt|;
DECL|field|nodeService
specifier|private
specifier|final
name|NodeService
name|nodeService
decl_stmt|;
annotation|@
name|Inject
DECL|method|HttpServer
specifier|public
name|HttpServer
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Environment
name|environment
parameter_list|,
name|HttpServerTransport
name|transport
parameter_list|,
name|RestController
name|restController
parameter_list|,
name|NodeService
name|nodeService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|environment
operator|=
name|environment
expr_stmt|;
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
name|this
operator|.
name|restController
operator|=
name|restController
expr_stmt|;
name|this
operator|.
name|nodeService
operator|=
name|nodeService
expr_stmt|;
name|nodeService
operator|.
name|setHttpServer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|transport
operator|.
name|httpServerAdapter
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
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
name|nodeService
operator|.
name|putAttribute
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
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
block|{
name|nodeService
operator|.
name|removeAttribute
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
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{
name|transport
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|info
specifier|public
name|HttpInfo
name|info
parameter_list|()
block|{
return|return
name|transport
operator|.
name|info
argument_list|()
return|;
block|}
DECL|method|stats
specifier|public
name|HttpStats
name|stats
parameter_list|()
block|{
return|return
name|transport
operator|.
name|stats
argument_list|()
return|;
block|}
DECL|method|dispatchRequest
specifier|public
name|void
name|dispatchRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|,
name|ThreadContext
name|threadContext
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|rawPath
argument_list|()
operator|.
name|equals
argument_list|(
literal|"/favicon.ico"
argument_list|)
condition|)
block|{
name|handleFavicon
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
return|return;
block|}
name|restController
operator|.
name|dispatchRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|threadContext
argument_list|)
expr_stmt|;
block|}
DECL|method|handleFavicon
name|void
name|handleFavicon
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|method
argument_list|()
operator|==
name|RestRequest
operator|.
name|Method
operator|.
name|GET
condition|)
block|{
try|try
block|{
try|try
init|(
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/config/favicon.ico"
argument_list|)
init|)
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|Streams
operator|.
name|copy
argument_list|(
name|stream
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|BytesRestResponse
name|restResponse
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|RestStatus
operator|.
name|OK
argument_list|,
literal|"image/x-icon"
argument_list|,
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|restResponse
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|BytesRestResponse
argument_list|(
name|INTERNAL_SERVER_ERROR
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|BytesRestResponse
argument_list|(
name|FORBIDDEN
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

