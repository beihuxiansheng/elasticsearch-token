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
name|client
operator|.
name|node
operator|.
name|NodeClient
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
name|bytes
operator|.
name|BytesArray
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
name|logging
operator|.
name|DeprecationLogger
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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptySet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableSet
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
name|BAD_REQUEST
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
name|OK
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
block|{
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
DECL|field|relevantHeaders
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|relevantHeaders
init|=
name|emptySet
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
block|{     }
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
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
comment|/**      * Controls which REST headers get copied over from a {@link org.elasticsearch.rest.RestRequest} to      * its corresponding {@link org.elasticsearch.transport.TransportRequest}(s).      *      * By default no headers get copied but it is possible to extend this behaviour via plugins by calling this method.      */
DECL|method|registerRelevantHeaders
specifier|public
specifier|synchronized
name|void
name|registerRelevantHeaders
parameter_list|(
name|String
modifier|...
name|headers
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|newRelevantHeaders
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|relevantHeaders
operator|.
name|size
argument_list|()
operator|+
name|headers
operator|.
name|length
argument_list|)
decl_stmt|;
name|newRelevantHeaders
operator|.
name|addAll
argument_list|(
name|relevantHeaders
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|newRelevantHeaders
argument_list|,
name|headers
argument_list|)
expr_stmt|;
name|relevantHeaders
operator|=
name|unmodifiableSet
argument_list|(
name|newRelevantHeaders
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the REST headers that get copied over from a {@link org.elasticsearch.rest.RestRequest} to      * its corresponding {@link org.elasticsearch.transport.TransportRequest}(s).      * By default no headers get copied but it is possible to extend this behaviour via plugins by calling {@link #registerRelevantHeaders(String...)}.      */
DECL|method|relevantHeaders
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|relevantHeaders
parameter_list|()
block|{
return|return
name|relevantHeaders
return|;
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
parameter_list|(
name|o1
parameter_list|,
name|o2
parameter_list|)
lambda|->
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
argument_list|)
expr_stmt|;
name|filters
operator|=
name|copy
expr_stmt|;
block|}
comment|/**      * Registers a REST handler to be executed when the provided {@code method} and {@code path} match the request.      *      * @param method GET, POST, etc.      * @param path Path to handle (e.g., "/{index}/{type}/_bulk")      * @param handler The handler to actually execute      * @param deprecationMessage The message to log and send as a header in the response      * @param logger The existing deprecation logger to use      */
DECL|method|registerAsDeprecatedHandler
specifier|public
name|void
name|registerAsDeprecatedHandler
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
parameter_list|,
name|String
name|deprecationMessage
parameter_list|,
name|DeprecationLogger
name|logger
parameter_list|)
block|{
assert|assert
operator|(
name|handler
operator|instanceof
name|DeprecationRestHandler
operator|)
operator|==
literal|false
assert|;
name|registerHandler
argument_list|(
name|method
argument_list|,
name|path
argument_list|,
operator|new
name|DeprecationRestHandler
argument_list|(
name|handler
argument_list|,
name|deprecationMessage
argument_list|,
name|logger
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Registers a REST handler to be executed when the provided {@code method} and {@code path} match the request, or when provided      * with {@code deprecatedMethod} and {@code deprecatedPath}. Expected usage:      *<pre><code>      * // remove deprecation in next major release      * controller.registerWithDeprecatedHandler(POST, "/_forcemerge", this,      *                                          POST, "/_optimize", deprecationLogger);      * controller.registerWithDeprecatedHandler(POST, "/{index}/_forcemerge", this,      *                                          POST, "/{index}/_optimize", deprecationLogger);      *</code></pre>      *<p>      * The registered REST handler ({@code method} with {@code path}) is a normal REST handler that is not deprecated and it is      * replacing the deprecated REST handler ({@code deprecatedMethod} with {@code deprecatedPath}) that is using the<em>same</em>      * {@code handler}.      *<p>      * Deprecated REST handlers without a direct replacement should be deprecated directly using {@link #registerAsDeprecatedHandler}      * and a specific message.      *      * @param method GET, POST, etc.      * @param path Path to handle (e.g., "/_forcemerge")      * @param handler The handler to actually execute      * @param deprecatedMethod GET, POST, etc.      * @param deprecatedPath<em>Deprecated</em> path to handle (e.g., "/_optimize")      * @param logger The existing deprecation logger to use      */
DECL|method|registerWithDeprecatedHandler
specifier|public
name|void
name|registerWithDeprecatedHandler
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
parameter_list|,
name|RestRequest
operator|.
name|Method
name|deprecatedMethod
parameter_list|,
name|String
name|deprecatedPath
parameter_list|,
name|DeprecationLogger
name|logger
parameter_list|)
block|{
comment|// e.g., [POST /_optimize] is deprecated! Use [POST /_forcemerge] instead.
specifier|final
name|String
name|deprecationMessage
init|=
literal|"["
operator|+
name|deprecatedMethod
operator|.
name|name
argument_list|()
operator|+
literal|" "
operator|+
name|deprecatedPath
operator|+
literal|"] is deprecated! Use ["
operator|+
name|method
operator|.
name|name
argument_list|()
operator|+
literal|" "
operator|+
name|path
operator|+
literal|"] instead."
decl_stmt|;
name|registerHandler
argument_list|(
name|method
argument_list|,
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
name|registerAsDeprecatedHandler
argument_list|(
name|deprecatedMethod
argument_list|,
name|deprecatedPath
argument_list|,
name|handler
argument_list|,
name|deprecationMessage
argument_list|,
name|logger
argument_list|)
expr_stmt|;
block|}
comment|/**      * Registers a REST handler to be executed when the provided method and path match the request.      *      * @param method GET, POST, etc.      * @param path Path to handle (e.g., "/{index}/{type}/_bulk")      * @param handler The handler to actually execute      */
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
name|PathTrie
argument_list|<
name|RestHandler
argument_list|>
name|handlers
init|=
name|getHandlersForMethod
argument_list|(
name|method
argument_list|)
decl_stmt|;
if|if
condition|(
name|handlers
operator|!=
literal|null
condition|)
block|{
name|handlers
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
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
comment|/**      * @param request The current request. Must not be null.      * @return true iff the circuit breaker limit must be enforced for processing this request.      */
DECL|method|canTripCircuitBreaker
specifier|public
name|boolean
name|canTripCircuitBreaker
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
name|RestHandler
name|handler
init|=
name|getHandler
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
name|handler
operator|!=
literal|null
operator|)
condition|?
name|handler
operator|.
name|canTripCircuitBreaker
argument_list|()
else|:
literal|true
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
parameter_list|,
specifier|final
name|NodeClient
name|client
parameter_list|,
name|ThreadContext
name|threadContext
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|checkRequestParameters
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
condition|)
block|{
return|return;
block|}
try|try
init|(
name|ThreadContext
operator|.
name|StoredContext
name|t
init|=
name|threadContext
operator|.
name|stashContext
argument_list|()
init|)
block|{
for|for
control|(
name|String
name|key
range|:
name|relevantHeaders
control|)
block|{
name|String
name|httpHeader
init|=
name|request
operator|.
name|header
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|httpHeader
operator|!=
literal|null
condition|)
block|{
name|threadContext
operator|.
name|putHeader
argument_list|(
name|key
argument_list|,
name|httpHeader
argument_list|)
expr_stmt|;
block|}
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
name|executeHandler
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|client
argument_list|)
expr_stmt|;
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
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|sendErrorResponse
specifier|public
name|void
name|sendErrorResponse
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|,
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
name|logger
operator|.
name|error
argument_list|(
literal|"failed to send failure response for uri [{}]"
argument_list|,
name|inner
argument_list|,
name|request
operator|.
name|uri
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Checks the request parameters against enabled settings for error trace support      * @return true if the request does not have any parameters that conflict with system settings      */
DECL|method|checkRequestParameters
name|boolean
name|checkRequestParameters
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
comment|// error_trace cannot be used when we disable detailed errors
if|if
condition|(
name|channel
operator|.
name|detailedErrorsEnabled
argument_list|()
operator|==
literal|false
operator|&&
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"error_trace"
argument_list|,
literal|false
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
name|newErrorBuilder
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
literal|"error traces in responses are disabled."
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
name|BAD_REQUEST
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
literal|"application/json"
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
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
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
parameter_list|,
name|NodeClient
name|client
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
argument_list|,
name|client
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
argument_list|,
name|BytesRestResponse
operator|.
name|TEXT_CONTENT_TYPE
argument_list|,
name|BytesArray
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|String
name|msg
init|=
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
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|BytesRestResponse
argument_list|(
name|BAD_REQUEST
argument_list|,
name|msg
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
name|PathTrie
argument_list|<
name|RestHandler
argument_list|>
name|handlers
init|=
name|getHandlersForMethod
argument_list|(
name|request
operator|.
name|method
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|handlers
operator|!=
literal|null
condition|)
block|{
return|return
name|handlers
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
DECL|method|getHandlersForMethod
specifier|private
name|PathTrie
argument_list|<
name|RestHandler
argument_list|>
name|getHandlersForMethod
parameter_list|(
name|RestRequest
operator|.
name|Method
name|method
parameter_list|)
block|{
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
parameter_list|,
name|NodeClient
name|client
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
name|IllegalStateException
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
name|client
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
name|client
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
literal|"Failed to send failure response for uri [{}]"
argument_list|,
name|e1
argument_list|,
name|request
operator|.
name|uri
argument_list|()
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
name|NodeClient
name|client
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
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

