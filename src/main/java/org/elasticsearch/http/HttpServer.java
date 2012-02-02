begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  */
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
DECL|field|disableSites
specifier|private
specifier|final
name|boolean
name|disableSites
decl_stmt|;
DECL|field|pluginSiteFilter
specifier|private
specifier|final
name|PluginSiteFilter
name|pluginSiteFilter
init|=
operator|new
name|PluginSiteFilter
argument_list|()
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
name|this
operator|.
name|disableSites
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"disable_sites"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|transport
operator|.
name|httpServerAdapter
argument_list|(
operator|new
name|Dispatcher
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|Dispatcher
specifier|static
class|class
name|Dispatcher
implements|implements
name|HttpServerAdapter
block|{
DECL|field|server
specifier|private
specifier|final
name|HttpServer
name|server
decl_stmt|;
DECL|method|Dispatcher
name|Dispatcher
parameter_list|(
name|HttpServer
name|server
parameter_list|)
block|{
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|dispatchRequest
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
name|server
operator|.
name|internalDispatchRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doStart
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
throws|throws
name|ElasticSearchException
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
throws|throws
name|ElasticSearchException
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
operator|new
name|HttpInfo
argument_list|(
name|transport
operator|.
name|boundAddress
argument_list|()
argument_list|)
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
DECL|method|internalDispatchRequest
specifier|public
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
if|if
condition|(
name|request
operator|.
name|rawPath
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"/_plugin/"
argument_list|)
condition|)
block|{
name|RestFilterChain
name|filterChain
init|=
name|restController
operator|.
name|filterChain
argument_list|(
name|pluginSiteFilter
argument_list|)
decl_stmt|;
name|filterChain
operator|.
name|continueProcessing
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
argument_list|)
expr_stmt|;
block|}
DECL|class|PluginSiteFilter
class|class
name|PluginSiteFilter
extends|extends
name|RestFilter
block|{
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|,
name|RestFilterChain
name|filterChain
parameter_list|)
block|{
name|handlePluginSite
argument_list|(
operator|(
name|HttpRequest
operator|)
name|request
argument_list|,
operator|(
name|HttpChannel
operator|)
name|channel
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handlePluginSite
name|void
name|handlePluginSite
parameter_list|(
name|HttpRequest
name|request
parameter_list|,
name|HttpChannel
name|channel
parameter_list|)
block|{
if|if
condition|(
name|disableSites
condition|)
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|StringRestResponse
argument_list|(
name|FORBIDDEN
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
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
name|OPTIONS
condition|)
block|{
comment|// when we have OPTIONS request, simply send OK by default (with the Access Control Origin header which gets automatically added)
name|StringRestResponse
name|response
init|=
operator|new
name|StringRestResponse
argument_list|(
name|OK
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|request
operator|.
name|method
argument_list|()
operator|!=
name|RestRequest
operator|.
name|Method
operator|.
name|GET
condition|)
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|StringRestResponse
argument_list|(
name|FORBIDDEN
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// TODO for a "/_plugin" endpoint, we should have a page that lists all the plugins?
name|String
name|path
init|=
name|request
operator|.
name|rawPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|"/_plugin/"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|i1
init|=
name|path
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|String
name|pluginName
decl_stmt|;
name|String
name|sitePath
decl_stmt|;
if|if
condition|(
name|i1
operator|==
operator|-
literal|1
condition|)
block|{
name|pluginName
operator|=
name|path
expr_stmt|;
name|sitePath
operator|=
literal|null
expr_stmt|;
comment|// TODO This is a path in the form of "/_plugin/head", without a trailing "/", which messes up
comment|// resources fetching if it does not exists, a better solution would be to send a redirect
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|StringRestResponse
argument_list|(
name|NOT_FOUND
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
name|pluginName
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i1
argument_list|)
expr_stmt|;
name|sitePath
operator|=
name|path
operator|.
name|substring
argument_list|(
name|i1
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sitePath
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|sitePath
operator|=
literal|"/index.html"
expr_stmt|;
block|}
comment|// Convert file separators.
name|sitePath
operator|=
name|sitePath
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
name|File
operator|.
name|separatorChar
argument_list|)
expr_stmt|;
comment|// this is a plugin provided site, serve it as static files from the plugin location
name|File
name|siteFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
argument_list|,
name|pluginName
argument_list|)
argument_list|,
literal|"_site"
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|siteFile
argument_list|,
name|sitePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
operator|||
name|file
operator|.
name|isHidden
argument_list|()
condition|)
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|StringRestResponse
argument_list|(
name|NOT_FOUND
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|StringRestResponse
argument_list|(
name|FORBIDDEN
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|siteFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
condition|)
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|StringRestResponse
argument_list|(
name|FORBIDDEN
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|byte
index|[]
name|data
init|=
name|Streams
operator|.
name|copyToByteArray
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|BytesRestResponse
argument_list|(
name|data
argument_list|,
name|guessMimeType
argument_list|(
name|sitePath
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|StringRestResponse
argument_list|(
name|INTERNAL_SERVER_ERROR
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO: Don't respond with a mime type that violates the request's Accept header
DECL|method|guessMimeType
specifier|private
name|String
name|guessMimeType
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|int
name|lastDot
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastDot
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|""
return|;
block|}
name|String
name|extension
init|=
name|path
operator|.
name|substring
argument_list|(
name|lastDot
operator|+
literal|1
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|String
name|mimeType
init|=
name|DEFAULT_MIME_TYPES
operator|.
name|get
argument_list|(
name|extension
argument_list|)
decl_stmt|;
if|if
condition|(
name|mimeType
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
return|return
name|mimeType
return|;
block|}
static|static
block|{
comment|// This is not an exhaustive list, just the most common types. Call registerMimeType() to add more.
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mimeTypes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"txt"
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"css"
argument_list|,
literal|"text/css"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"csv"
argument_list|,
literal|"text/csv"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"htm"
argument_list|,
literal|"text/html"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"html"
argument_list|,
literal|"text/html"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"xml"
argument_list|,
literal|"text/xml"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"js"
argument_list|,
literal|"text/javascript"
argument_list|)
expr_stmt|;
comment|// Technically it should be application/javascript (RFC 4329), but IE8 struggles with that
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"xhtml"
argument_list|,
literal|"application/xhtml+xml"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"json"
argument_list|,
literal|"application/json"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"pdf"
argument_list|,
literal|"application/pdf"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"zip"
argument_list|,
literal|"application/zip"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"tar"
argument_list|,
literal|"application/x-tar"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"gif"
argument_list|,
literal|"image/gif"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"jpeg"
argument_list|,
literal|"image/jpeg"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"jpg"
argument_list|,
literal|"image/jpeg"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"tiff"
argument_list|,
literal|"image/tiff"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"tif"
argument_list|,
literal|"image/tiff"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"png"
argument_list|,
literal|"image/png"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"svg"
argument_list|,
literal|"image/svg+xml"
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|put
argument_list|(
literal|"ico"
argument_list|,
literal|"image/vnd.microsoft.icon"
argument_list|)
expr_stmt|;
name|DEFAULT_MIME_TYPES
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|mimeTypes
argument_list|)
expr_stmt|;
block|}
DECL|field|DEFAULT_MIME_TYPES
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|DEFAULT_MIME_TYPES
decl_stmt|;
block|}
end_class

end_unit

