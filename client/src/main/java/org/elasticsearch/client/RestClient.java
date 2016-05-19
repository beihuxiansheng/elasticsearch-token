begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|Consts
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|Header
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpHost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|config
operator|.
name|RequestConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|CloseableHttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpEntityEnclosingRequestBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpHead
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPut
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpRequestBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|utils
operator|.
name|URIBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|ContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|HttpClientBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|conn
operator|.
name|PoolingHttpClientConnectionManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|TimeUnit
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

begin_class
DECL|class|RestClient
specifier|public
specifier|final
class|class
name|RestClient
implements|implements
name|Closeable
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Log
name|logger
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RestClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|JSON_CONTENT_TYPE
specifier|public
specifier|static
name|ContentType
name|JSON_CONTENT_TYPE
init|=
name|ContentType
operator|.
name|create
argument_list|(
literal|"application/json"
argument_list|,
name|Consts
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|CloseableHttpClient
name|client
decl_stmt|;
DECL|field|maxRetryTimeout
specifier|private
specifier|final
name|long
name|maxRetryTimeout
decl_stmt|;
DECL|field|lastConnectionIndex
specifier|private
specifier|final
name|AtomicInteger
name|lastConnectionIndex
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|connections
specifier|private
specifier|volatile
name|List
argument_list|<
name|Connection
argument_list|>
name|connections
decl_stmt|;
DECL|field|failureListener
specifier|private
specifier|volatile
name|FailureListener
name|failureListener
init|=
operator|new
name|FailureListener
argument_list|()
decl_stmt|;
DECL|method|RestClient
specifier|private
name|RestClient
parameter_list|(
name|CloseableHttpClient
name|client
parameter_list|,
name|long
name|maxRetryTimeout
parameter_list|,
name|HttpHost
modifier|...
name|hosts
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|maxRetryTimeout
operator|=
name|maxRetryTimeout
expr_stmt|;
name|setNodes
argument_list|(
name|hosts
argument_list|)
expr_stmt|;
block|}
DECL|method|setNodes
specifier|public
specifier|synchronized
name|void
name|setNodes
parameter_list|(
name|HttpHost
modifier|...
name|hosts
parameter_list|)
block|{
name|List
argument_list|<
name|Connection
argument_list|>
name|connections
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|hosts
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|HttpHost
name|host
range|:
name|hosts
control|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|host
argument_list|,
literal|"host cannot be null"
argument_list|)
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
operator|new
name|Connection
argument_list|(
name|host
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|connections
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|connections
argument_list|)
expr_stmt|;
block|}
DECL|method|performRequest
specifier|public
name|ElasticsearchResponse
name|performRequest
parameter_list|(
name|String
name|method
parameter_list|,
name|String
name|endpoint
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|HttpEntity
name|entity
parameter_list|,
name|Header
modifier|...
name|headers
parameter_list|)
throws|throws
name|IOException
block|{
name|URI
name|uri
init|=
name|buildUri
argument_list|(
name|endpoint
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|HttpRequestBase
name|request
init|=
name|createHttpRequest
argument_list|(
name|method
argument_list|,
name|uri
argument_list|,
name|entity
argument_list|)
decl_stmt|;
if|if
condition|(
name|headers
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Header
name|header
range|:
name|headers
control|)
block|{
name|request
operator|.
name|addHeader
argument_list|(
name|header
argument_list|)
expr_stmt|;
block|}
block|}
comment|//we apply a soft margin so that e.g. if a request took 59 seconds and timeout is set to 60 we don't do another attempt
name|long
name|retryTimeout
init|=
name|Math
operator|.
name|round
argument_list|(
name|this
operator|.
name|maxRetryTimeout
operator|/
operator|(
name|float
operator|)
literal|100
operator|*
literal|98
argument_list|)
decl_stmt|;
name|IOException
name|lastSeenException
init|=
literal|null
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Connection
argument_list|>
name|connectionIterator
init|=
name|nextConnection
argument_list|()
decl_stmt|;
while|while
condition|(
name|connectionIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Connection
name|connection
init|=
name|connectionIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastSeenException
operator|!=
literal|null
condition|)
block|{
name|long
name|timeElapsed
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|)
decl_stmt|;
name|long
name|timeout
init|=
name|retryTimeout
operator|-
name|timeElapsed
decl_stmt|;
if|if
condition|(
name|timeout
operator|<=
literal|0
condition|)
block|{
name|IOException
name|retryTimeoutException
init|=
operator|new
name|IOException
argument_list|(
literal|"request retries exceeded max retry timeout ["
operator|+
name|retryTimeout
operator|+
literal|"]"
argument_list|)
decl_stmt|;
name|retryTimeoutException
operator|.
name|addSuppressed
argument_list|(
name|lastSeenException
argument_list|)
expr_stmt|;
throw|throw
name|retryTimeoutException
throw|;
block|}
name|request
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|CloseableHttpResponse
name|response
decl_stmt|;
try|try
block|{
name|response
operator|=
name|client
operator|.
name|execute
argument_list|(
name|connection
operator|.
name|getHost
argument_list|()
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|RequestLogger
operator|.
name|log
argument_list|(
name|logger
argument_list|,
literal|"request failed"
argument_list|,
name|request
argument_list|,
name|connection
operator|.
name|getHost
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|onFailure
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|lastSeenException
operator|=
name|addSuppressedException
argument_list|(
name|lastSeenException
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|ElasticsearchResponse
name|elasticsearchResponse
init|=
operator|new
name|ElasticsearchResponse
argument_list|(
name|request
operator|.
name|getRequestLine
argument_list|()
argument_list|,
name|connection
operator|.
name|getHost
argument_list|()
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|int
name|statusCode
init|=
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|statusCode
operator|<
literal|300
operator|||
operator|(
name|request
operator|.
name|getMethod
argument_list|()
operator|.
name|equals
argument_list|(
name|HttpHead
operator|.
name|METHOD_NAME
argument_list|)
operator|&&
name|statusCode
operator|==
literal|404
operator|)
condition|)
block|{
name|RequestLogger
operator|.
name|log
argument_list|(
name|logger
argument_list|,
literal|"request succeeded"
argument_list|,
name|request
argument_list|,
name|connection
operator|.
name|getHost
argument_list|()
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|onSuccess
argument_list|(
name|connection
argument_list|)
expr_stmt|;
return|return
name|elasticsearchResponse
return|;
block|}
else|else
block|{
name|RequestLogger
operator|.
name|log
argument_list|(
name|logger
argument_list|,
literal|"request failed"
argument_list|,
name|request
argument_list|,
name|connection
operator|.
name|getHost
argument_list|()
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|ElasticsearchResponseException
name|elasticsearchResponseException
init|=
operator|new
name|ElasticsearchResponseException
argument_list|(
name|elasticsearchResponse
argument_list|)
decl_stmt|;
name|lastSeenException
operator|=
name|addSuppressedException
argument_list|(
name|lastSeenException
argument_list|,
name|elasticsearchResponseException
argument_list|)
expr_stmt|;
comment|//clients don't retry on 500 because elasticsearch still misuses it instead of 400 in some places
if|if
condition|(
name|statusCode
operator|==
literal|502
operator|||
name|statusCode
operator|==
literal|503
operator|||
name|statusCode
operator|==
literal|504
condition|)
block|{
name|onFailure
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//don't retry and call onSuccess as the error should be a request problem, the node is alive
name|onSuccess
argument_list|(
name|connection
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
assert|assert
name|lastSeenException
operator|!=
literal|null
assert|;
throw|throw
name|lastSeenException
throw|;
block|}
comment|/**      * Returns an iterator of connections that should be used for a request call.      * Ideally, the first connection is retrieved from the iterator and used successfully for the request.      * Otherwise, after each failure the next connection should be retrieved from the iterator so that the request can be retried.      * The maximum total of attempts is equal to the number of connections that are available in the iterator.      * The iterator returned will never be empty, rather an {@link IllegalStateException} will be thrown in that case.      * In case there are no alive connections available, or dead ones that should be retried, one dead connection      * gets resurrected and returned.      */
DECL|method|nextConnection
specifier|private
name|Iterator
argument_list|<
name|Connection
argument_list|>
name|nextConnection
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|connections
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"no connections available"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|Connection
argument_list|>
name|rotatedConnections
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|connections
argument_list|)
decl_stmt|;
comment|//TODO is it possible to make this O(1)? (rotate is O(n))
name|Collections
operator|.
name|rotate
argument_list|(
name|rotatedConnections
argument_list|,
name|rotatedConnections
operator|.
name|size
argument_list|()
operator|-
name|lastConnectionIndex
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Connection
argument_list|>
name|connectionIterator
init|=
name|rotatedConnections
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|connectionIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Connection
name|connection
init|=
name|connectionIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|connection
operator|.
name|isBlacklisted
argument_list|()
condition|)
block|{
name|connectionIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|rotatedConnections
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Connection
argument_list|>
name|sortedConnections
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|connections
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|sortedConnections
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Connection
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Connection
name|o1
parameter_list|,
name|Connection
name|o2
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getDeadUntil
argument_list|()
argument_list|,
name|o2
operator|.
name|getDeadUntil
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|sortedConnections
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"trying to resurrect connection for "
operator|+
name|connection
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|connection
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
return|return
name|rotatedConnections
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**      * Called after each successful request call.      * Receives as an argument the connection that was used for the successful request.      */
DECL|method|onSuccess
specifier|public
name|void
name|onSuccess
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
name|connection
operator|.
name|markAlive
argument_list|()
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"marked connection alive for "
operator|+
name|connection
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called after each failed attempt.      * Receives as an argument the connection that was used for the failed attempt.      */
DECL|method|onFailure
specifier|private
name|void
name|onFailure
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|IOException
block|{
name|connection
operator|.
name|markDead
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"marked connection dead for "
operator|+
name|connection
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|failureListener
operator|.
name|onFailure
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
DECL|method|setFailureListener
specifier|public
specifier|synchronized
name|void
name|setFailureListener
parameter_list|(
name|FailureListener
name|failureListener
parameter_list|)
block|{
name|this
operator|.
name|failureListener
operator|=
name|failureListener
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addSuppressedException
specifier|private
specifier|static
name|IOException
name|addSuppressedException
parameter_list|(
name|IOException
name|suppressedException
parameter_list|,
name|IOException
name|currentException
parameter_list|)
block|{
if|if
condition|(
name|suppressedException
operator|!=
literal|null
condition|)
block|{
name|currentException
operator|.
name|addSuppressed
argument_list|(
name|suppressedException
argument_list|)
expr_stmt|;
block|}
return|return
name|currentException
return|;
block|}
DECL|method|createHttpRequest
specifier|private
specifier|static
name|HttpRequestBase
name|createHttpRequest
parameter_list|(
name|String
name|method
parameter_list|,
name|URI
name|uri
parameter_list|,
name|HttpEntity
name|entity
parameter_list|)
block|{
switch|switch
condition|(
name|method
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
condition|)
block|{
case|case
name|HttpDeleteWithEntity
operator|.
name|METHOD_NAME
case|:
name|HttpDeleteWithEntity
name|httpDeleteWithEntity
init|=
operator|new
name|HttpDeleteWithEntity
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|addRequestBody
argument_list|(
name|httpDeleteWithEntity
argument_list|,
name|entity
argument_list|)
expr_stmt|;
return|return
name|httpDeleteWithEntity
return|;
case|case
name|HttpGetWithEntity
operator|.
name|METHOD_NAME
case|:
name|HttpGetWithEntity
name|httpGetWithEntity
init|=
operator|new
name|HttpGetWithEntity
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|addRequestBody
argument_list|(
name|httpGetWithEntity
argument_list|,
name|entity
argument_list|)
expr_stmt|;
return|return
name|httpGetWithEntity
return|;
case|case
name|HttpHead
operator|.
name|METHOD_NAME
case|:
if|if
condition|(
name|entity
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"HEAD with body is not supported"
argument_list|)
throw|;
block|}
return|return
operator|new
name|HttpHead
argument_list|(
name|uri
argument_list|)
return|;
case|case
name|HttpOptions
operator|.
name|METHOD_NAME
case|:
if|if
condition|(
name|entity
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"OPTIONS with body is not supported"
argument_list|)
throw|;
block|}
return|return
operator|new
name|HttpOptions
argument_list|(
name|uri
argument_list|)
return|;
case|case
name|HttpPost
operator|.
name|METHOD_NAME
case|:
name|HttpPost
name|httpPost
init|=
operator|new
name|HttpPost
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|addRequestBody
argument_list|(
name|httpPost
argument_list|,
name|entity
argument_list|)
expr_stmt|;
return|return
name|httpPost
return|;
case|case
name|HttpPut
operator|.
name|METHOD_NAME
case|:
name|HttpPut
name|httpPut
init|=
operator|new
name|HttpPut
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|addRequestBody
argument_list|(
name|httpPut
argument_list|,
name|entity
argument_list|)
expr_stmt|;
return|return
name|httpPut
return|;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"http method not supported: "
operator|+
name|method
argument_list|)
throw|;
block|}
block|}
DECL|method|addRequestBody
specifier|private
specifier|static
name|void
name|addRequestBody
parameter_list|(
name|HttpEntityEnclosingRequestBase
name|httpRequest
parameter_list|,
name|HttpEntity
name|entity
parameter_list|)
block|{
if|if
condition|(
name|entity
operator|!=
literal|null
condition|)
block|{
name|httpRequest
operator|.
name|setEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|buildUri
specifier|private
specifier|static
name|URI
name|buildUri
parameter_list|(
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
try|try
block|{
name|URIBuilder
name|uriBuilder
init|=
operator|new
name|URIBuilder
argument_list|(
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|param
range|:
name|params
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|uriBuilder
operator|.
name|addParameter
argument_list|(
name|param
operator|.
name|getKey
argument_list|()
argument_list|,
name|param
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|uriBuilder
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns a new {@link Builder} to help with {@link RestClient} creation.      */
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**      * Rest client builder. Helps creating a new {@link RestClient}.      */
DECL|class|Builder
specifier|public
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|DEFAULT_CONNECT_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CONNECT_TIMEOUT
init|=
literal|500
decl_stmt|;
DECL|field|DEFAULT_SOCKET_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SOCKET_TIMEOUT
init|=
literal|5000
decl_stmt|;
DECL|field|DEFAULT_MAX_RETRY_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_RETRY_TIMEOUT
init|=
name|DEFAULT_SOCKET_TIMEOUT
decl_stmt|;
DECL|field|DEFAULT_CONNECTION_REQUEST_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CONNECTION_REQUEST_TIMEOUT
init|=
literal|500
decl_stmt|;
DECL|field|httpClient
specifier|private
name|CloseableHttpClient
name|httpClient
decl_stmt|;
DECL|field|maxRetryTimeout
specifier|private
name|int
name|maxRetryTimeout
init|=
name|DEFAULT_MAX_RETRY_TIMEOUT
decl_stmt|;
DECL|field|hosts
specifier|private
name|HttpHost
index|[]
name|hosts
decl_stmt|;
DECL|field|defaultHeaders
specifier|private
name|Collection
argument_list|<
name|?
extends|extends
name|Header
argument_list|>
name|defaultHeaders
decl_stmt|;
DECL|method|Builder
specifier|private
name|Builder
parameter_list|()
block|{          }
comment|/**          * Sets the http client. A new default one will be created if not          * specified, by calling {@link #createDefaultHttpClient(Collection)}.          *          * @see CloseableHttpClient          */
DECL|method|setHttpClient
specifier|public
name|Builder
name|setHttpClient
parameter_list|(
name|CloseableHttpClient
name|httpClient
parameter_list|)
block|{
name|this
operator|.
name|httpClient
operator|=
name|httpClient
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Sets the maximum timeout to honour in case of multiple retries of the same request.          * {@link #DEFAULT_MAX_RETRY_TIMEOUT} if not specified.          *          * @throws IllegalArgumentException if maxRetryTimeout is not greater than 0          */
DECL|method|setMaxRetryTimeout
specifier|public
name|Builder
name|setMaxRetryTimeout
parameter_list|(
name|int
name|maxRetryTimeout
parameter_list|)
block|{
if|if
condition|(
name|maxRetryTimeout
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxRetryTimeout must be greater than 0"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxRetryTimeout
operator|=
name|maxRetryTimeout
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Sets the hosts that the client will send requests to.          */
DECL|method|setHosts
specifier|public
name|Builder
name|setHosts
parameter_list|(
name|HttpHost
modifier|...
name|hosts
parameter_list|)
block|{
if|if
condition|(
name|hosts
operator|==
literal|null
operator|||
name|hosts
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no hosts provided"
argument_list|)
throw|;
block|}
name|this
operator|.
name|hosts
operator|=
name|hosts
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Sets the default request headers, to be used when creating the default http client instance.          * In case the http client is set through {@link #setHttpClient(CloseableHttpClient)}, the default headers need to be          * set to it externally during http client construction.          */
DECL|method|setDefaultHeaders
specifier|public
name|Builder
name|setDefaultHeaders
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Header
argument_list|>
name|defaultHeaders
parameter_list|)
block|{
name|this
operator|.
name|defaultHeaders
operator|=
name|defaultHeaders
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Creates a new {@link RestClient} based on the provided configuration.          */
DECL|method|build
specifier|public
name|RestClient
name|build
parameter_list|()
block|{
if|if
condition|(
name|httpClient
operator|==
literal|null
condition|)
block|{
name|httpClient
operator|=
name|createDefaultHttpClient
argument_list|(
name|defaultHeaders
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|defaultHeaders
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"defaultHeaders need to be set to the HttpClient directly when manually provided"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|hosts
operator|==
literal|null
operator|||
name|hosts
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no hosts provided"
argument_list|)
throw|;
block|}
return|return
operator|new
name|RestClient
argument_list|(
name|httpClient
argument_list|,
name|maxRetryTimeout
argument_list|,
name|hosts
argument_list|)
return|;
block|}
comment|/**          * Creates an http client with default settings          *          * @see CloseableHttpClient          */
DECL|method|createDefaultHttpClient
specifier|public
specifier|static
name|CloseableHttpClient
name|createDefaultHttpClient
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Header
argument_list|>
name|defaultHeaders
parameter_list|)
block|{
name|PoolingHttpClientConnectionManager
name|connectionManager
init|=
operator|new
name|PoolingHttpClientConnectionManager
argument_list|()
decl_stmt|;
comment|//default settings may be too constraining
name|connectionManager
operator|.
name|setDefaultMaxPerRoute
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|connectionManager
operator|.
name|setMaxTotal
argument_list|(
literal|30
argument_list|)
expr_stmt|;
comment|//default timeouts are all infinite
name|RequestConfig
name|requestConfig
init|=
name|RequestConfig
operator|.
name|custom
argument_list|()
operator|.
name|setConnectTimeout
argument_list|(
name|DEFAULT_CONNECT_TIMEOUT
argument_list|)
operator|.
name|setSocketTimeout
argument_list|(
name|DEFAULT_SOCKET_TIMEOUT
argument_list|)
operator|.
name|setConnectionRequestTimeout
argument_list|(
name|DEFAULT_CONNECTION_REQUEST_TIMEOUT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|HttpClientBuilder
name|httpClientBuilder
init|=
name|HttpClientBuilder
operator|.
name|create
argument_list|()
decl_stmt|;
if|if
condition|(
name|defaultHeaders
operator|!=
literal|null
condition|)
block|{
name|httpClientBuilder
operator|.
name|setDefaultHeaders
argument_list|(
name|defaultHeaders
argument_list|)
expr_stmt|;
block|}
return|return
name|httpClientBuilder
operator|.
name|setConnectionManager
argument_list|(
name|connectionManager
argument_list|)
operator|.
name|setDefaultRequestConfig
argument_list|(
name|requestConfig
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
comment|/**      * Listener that allows to be notified whenever a failure happens. Useful when sniffing is enabled, so that we can sniff on failure.      * The default implementation is a no-op.      */
DECL|class|FailureListener
specifier|public
specifier|static
class|class
name|FailureListener
block|{
DECL|method|onFailure
specifier|public
name|void
name|onFailure
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|IOException
block|{          }
block|}
block|}
end_class

end_unit

