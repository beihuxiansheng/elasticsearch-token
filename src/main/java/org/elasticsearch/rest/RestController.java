begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalStateException
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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
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
name|support
operator|.
name|RestUtils
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
DECL|class|RestController
specifier|public
class|class
name|RestController
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|RestController
argument_list|>
block|{
DECL|field|HTTP_JSON_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_JSON_ENABLE
init|=
literal|"http.jsonp.enable"
decl_stmt|;
DECL|field|getHandlers
specifier|private
specifier|final
name|PathTrie
argument_list|<
name|RestHandler
argument_list|>
name|getHandlers
init|=
operator|new
name|PathTrie
argument_list|<>
argument_list|(
name|RestUtils
operator|.
name|REST_DECODER
argument_list|)
decl_stmt|;
DECL|field|postHandlers
specifier|private
specifier|final
name|PathTrie
argument_list|<
name|RestHandler
argument_list|>
name|postHandlers
init|=
operator|new
name|PathTrie
argument_list|<>
argument_list|(
name|RestUtils
operator|.
name|REST_DECODER
argument_list|)
decl_stmt|;
DECL|field|putHandlers
specifier|private
specifier|final
name|PathTrie
argument_list|<
name|RestHandler
argument_list|>
name|putHandlers
init|=
operator|new
name|PathTrie
argument_list|<>
argument_list|(
name|RestUtils
operator|.
name|REST_DECODER
argument_list|)
decl_stmt|;
DECL|field|deleteHandlers
specifier|private
specifier|final
name|PathTrie
argument_list|<
name|RestHandler
argument_list|>
name|deleteHandlers
init|=
operator|new
name|PathTrie
argument_list|<>
argument_list|(
name|RestUtils
operator|.
name|REST_DECODER
argument_list|)
decl_stmt|;
DECL|field|headHandlers
specifier|private
specifier|final
name|PathTrie
argument_list|<
name|RestHandler
argument_list|>
name|headHandlers
init|=
operator|new
name|PathTrie
argument_list|<>
argument_list|(
name|RestUtils
operator|.
name|REST_DECODER
argument_list|)
decl_stmt|;
DECL|field|optionsHandlers
specifier|private
specifier|final
name|PathTrie
argument_list|<
name|RestHandler
argument_list|>
name|optionsHandlers
init|=
operator|new
name|PathTrie
argument_list|<>
argument_list|(
name|RestUtils
operator|.
name|REST_DECODER
argument_list|)
decl_stmt|;
DECL|field|handlerFilter
specifier|private
specifier|final
name|RestHandlerFilter
name|handlerFilter
init|=
operator|new
name|RestHandlerFilter
argument_list|()
decl_stmt|;
comment|// non volatile since the assumption is that pre processors are registered on startup
DECL|field|filters
specifier|private
name|RestFilter
index|[]
name|filters
init|=
operator|new
name|RestFilter
index|[
literal|0
index|]
decl_stmt|;
annotation|@
name|Inject
DECL|method|RestController
specifier|public
name|RestController
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
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
throws|throws
name|ElasticsearchException
block|{     }
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticsearchException
block|{     }
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
for|for
control|(
name|RestFilter
name|filter
range|:
name|filters
control|)
block|{
name|filter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Registers a pre processor to be executed before the rest request is actually handled.      */
DECL|method|registerFilter
specifier|public
specifier|synchronized
name|void
name|registerFilter
parameter_list|(
name|RestFilter
name|preProcessor
parameter_list|)
block|{
name|RestFilter
index|[]
name|copy
init|=
operator|new
name|RestFilter
index|[
name|filters
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|filters
argument_list|,
literal|0
argument_list|,
name|copy
argument_list|,
literal|0
argument_list|,
name|filters
operator|.
name|length
argument_list|)
expr_stmt|;
name|copy
index|[
name|filters
operator|.
name|length
index|]
operator|=
name|preProcessor
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|copy
argument_list|,
operator|new
name|Comparator
argument_list|<
name|RestFilter
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|RestFilter
name|o1
parameter_list|,
name|RestFilter
name|o2
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|order
argument_list|()
argument_list|,
name|o2
operator|.
name|order
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|filters
operator|=
name|copy
expr_stmt|;
block|}
comment|/**      * Registers a rest handler to be execute when the provided method and path match the request.      */
DECL|method|registerHandler
specifier|public
name|void
name|registerHandler
parameter_list|(
name|RestRequest
operator|.
name|Method
name|method
parameter_list|,
name|String
name|path
parameter_list|,
name|RestHandler
name|handler
parameter_list|)
block|{
switch|switch
condition|(
name|method
condition|)
block|{
case|case
name|GET
case|:
name|getHandlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETE
case|:
name|deleteHandlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
break|break;
case|case
name|POST
case|:
name|postHandlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
break|break;
case|case
name|PUT
case|:
name|putHandlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
break|break;
case|case
name|OPTIONS
case|:
name|optionsHandlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
break|break;
case|case
name|HEAD
case|:
name|headHandlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Can't handle ["
operator|+
name|method
operator|+
literal|"] for path ["
operator|+
name|path
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns a filter chain (if needed) to execute. If this method returns null, simply execute      * as usual.      */
annotation|@
name|Nullable
DECL|method|filterChainOrNull
specifier|public
name|RestFilterChain
name|filterChainOrNull
parameter_list|(
name|RestFilter
name|executionFilter
parameter_list|)
block|{
if|if
condition|(
name|filters
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ControllerFilterChain
argument_list|(
name|executionFilter
argument_list|)
return|;
block|}
comment|/**      * Returns a filter chain with the final filter being the provided filter.      */
DECL|method|filterChain
specifier|public
name|RestFilterChain
name|filterChain
parameter_list|(
name|RestFilter
name|executionFilter
parameter_list|)
block|{
return|return
operator|new
name|ControllerFilterChain
argument_list|(
name|executionFilter
argument_list|)
return|;
block|}
DECL|method|dispatchRequest
specifier|public
name|void
name|dispatchRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|)
block|{
comment|// If JSONP is disabled and someone sends a callback parameter we should bail out before querying
if|if
condition|(
operator|!
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|HTTP_JSON_ENABLE
argument_list|,
literal|false
argument_list|)
operator|&&
name|request
operator|.
name|hasParam
argument_list|(
literal|"callback"
argument_list|)
condition|)
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|channel
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"error"
argument_list|,
literal|"JSONP is disabled."
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
expr_stmt|;
name|RestResponse
name|response
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|FORBIDDEN
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|response
operator|.
name|addHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/javascript"
argument_list|)
expr_stmt|;
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
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to send response"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
return|return;
block|}
if|if
condition|(
name|filters
operator|.
name|length
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|executeHandler
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
catch|catch
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
operator|new
name|BytesRestResponse
argument_list|(
name|channel
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"failed to send failure response for uri ["
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
else|else
block|{
name|ControllerFilterChain
name|filterChain
init|=
operator|new
name|ControllerFilterChain
argument_list|(
name|handlerFilter
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
block|}
block|}
DECL|method|executeHandler
name|void
name|executeHandler
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|RestHandler
name|handler
init|=
name|getHandler
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
name|handler
operator|.
name|handleRequest
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
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|BytesRestResponse
argument_list|(
name|OK
argument_list|)
argument_list|)
expr_stmt|;
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
name|BAD_REQUEST
argument_list|,
literal|"No handler found for uri ["
operator|+
name|request
operator|.
name|uri
argument_list|()
operator|+
literal|"] and method ["
operator|+
name|request
operator|.
name|method
argument_list|()
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getHandler
specifier|private
name|RestHandler
name|getHandler
parameter_list|(
name|RestRequest
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
name|RestRequest
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
name|RestRequest
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
name|RestRequest
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
name|RestRequest
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
name|RestRequest
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
name|RestRequest
name|request
parameter_list|)
block|{
comment|// we use rawPath since we don't want to decode it while processing the path resolution
comment|// so we can handle things like:
comment|// my_index/my_type/http%3A%2F%2Fwww.google.com
return|return
name|request
operator|.
name|rawPath
argument_list|()
return|;
block|}
DECL|class|ControllerFilterChain
class|class
name|ControllerFilterChain
implements|implements
name|RestFilterChain
block|{
DECL|field|executionFilter
specifier|private
specifier|final
name|RestFilter
name|executionFilter
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|AtomicInteger
name|index
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|ControllerFilterChain
name|ControllerFilterChain
parameter_list|(
name|RestFilter
name|executionFilter
parameter_list|)
block|{
name|this
operator|.
name|executionFilter
operator|=
name|executionFilter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|continueProcessing
specifier|public
name|void
name|continueProcessing
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|)
block|{
try|try
block|{
name|int
name|loc
init|=
name|index
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|loc
operator|>
name|filters
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"filter continueProcessing was called more than expected"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|loc
operator|==
name|filters
operator|.
name|length
condition|)
block|{
name|executionFilter
operator|.
name|process
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|RestFilter
name|preProcessor
init|=
name|filters
index|[
name|loc
index|]
decl_stmt|;
name|preProcessor
operator|.
name|process
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
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
name|BytesRestResponse
argument_list|(
name|channel
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
DECL|class|RestHandlerFilter
class|class
name|RestHandlerFilter
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
throws|throws
name|Exception
block|{
name|executeHandler
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

