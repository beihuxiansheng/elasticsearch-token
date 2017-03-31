begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex.remote
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|ContentTooLongException
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
name|util
operator|.
name|EntityUtils
import|;
end_import

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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|ParameterizedMessage
import|;
end_import

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
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchStatusException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|bulk
operator|.
name|BackoffPolicy
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
name|bulk
operator|.
name|byscroll
operator|.
name|ScrollableHitSource
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
name|SearchRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|ResponseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|ResponseListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|RestClient
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
name|ParsingException
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
name|Strings
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
name|BytesReference
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
name|unit
operator|.
name|TimeValue
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
name|AbstractRunnable
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
name|NamedXContentRegistry
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
name|XContentParser
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
name|XContentType
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
name|InputStream
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiFunction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
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
name|emptyMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
operator|.
name|timeValueMillis
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
operator|.
name|timeValueNanos
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
operator|.
name|RemoteRequestBuilders
operator|.
name|clearScrollEntity
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
operator|.
name|RemoteRequestBuilders
operator|.
name|initialSearchEntity
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
operator|.
name|RemoteRequestBuilders
operator|.
name|initialSearchParams
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
operator|.
name|RemoteRequestBuilders
operator|.
name|initialSearchPath
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
operator|.
name|RemoteRequestBuilders
operator|.
name|scrollEntity
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
operator|.
name|RemoteRequestBuilders
operator|.
name|scrollParams
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
operator|.
name|RemoteRequestBuilders
operator|.
name|scrollPath
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
operator|.
name|RemoteResponseParsers
operator|.
name|MAIN_ACTION_PARSER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
operator|.
name|RemoteResponseParsers
operator|.
name|RESPONSE_PARSER
import|;
end_import

begin_class
DECL|class|RemoteScrollableHitSource
specifier|public
class|class
name|RemoteScrollableHitSource
extends|extends
name|ScrollableHitSource
block|{
DECL|field|client
specifier|private
specifier|final
name|RestClient
name|client
decl_stmt|;
DECL|field|query
specifier|private
specifier|final
name|BytesReference
name|query
decl_stmt|;
DECL|field|searchRequest
specifier|private
specifier|final
name|SearchRequest
name|searchRequest
decl_stmt|;
DECL|field|remoteVersion
name|Version
name|remoteVersion
decl_stmt|;
DECL|method|RemoteScrollableHitSource
specifier|public
name|RemoteScrollableHitSource
parameter_list|(
name|Logger
name|logger
parameter_list|,
name|BackoffPolicy
name|backoffPolicy
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|Runnable
name|countSearchRetry
parameter_list|,
name|Consumer
argument_list|<
name|Exception
argument_list|>
name|fail
parameter_list|,
name|RestClient
name|client
parameter_list|,
name|BytesReference
name|query
parameter_list|,
name|SearchRequest
name|searchRequest
parameter_list|)
block|{
name|super
argument_list|(
name|logger
argument_list|,
name|backoffPolicy
argument_list|,
name|threadPool
argument_list|,
name|countSearchRetry
argument_list|,
name|fail
argument_list|)
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|searchRequest
operator|=
name|searchRequest
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|(
name|Consumer
argument_list|<
name|?
super|super
name|Response
argument_list|>
name|onResponse
parameter_list|)
block|{
name|lookupRemoteVersion
argument_list|(
name|version
lambda|->
block|{
name|remoteVersion
operator|=
name|version
expr_stmt|;
name|execute
argument_list|(
literal|"POST"
argument_list|,
name|initialSearchPath
argument_list|(
name|searchRequest
argument_list|)
argument_list|,
name|initialSearchParams
argument_list|(
name|searchRequest
argument_list|,
name|version
argument_list|)
argument_list|,
name|initialSearchEntity
argument_list|(
name|searchRequest
argument_list|,
name|query
argument_list|)
argument_list|,
name|RESPONSE_PARSER
argument_list|,
name|r
lambda|->
name|onStartResponse
argument_list|(
name|onResponse
argument_list|,
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|lookupRemoteVersion
name|void
name|lookupRemoteVersion
parameter_list|(
name|Consumer
argument_list|<
name|Version
argument_list|>
name|onVersion
parameter_list|)
block|{
name|execute
argument_list|(
literal|"GET"
argument_list|,
literal|""
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
literal|null
argument_list|,
name|MAIN_ACTION_PARSER
argument_list|,
name|onVersion
argument_list|)
expr_stmt|;
block|}
DECL|method|onStartResponse
specifier|private
name|void
name|onStartResponse
parameter_list|(
name|Consumer
argument_list|<
name|?
super|super
name|Response
argument_list|>
name|onResponse
parameter_list|,
name|Response
name|response
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|response
operator|.
name|getScrollId
argument_list|()
argument_list|)
operator|&&
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"First response looks like a scan response. Jumping right to the second. scroll=[{}]"
argument_list|,
name|response
operator|.
name|getScrollId
argument_list|()
argument_list|)
expr_stmt|;
name|doStartNextScroll
argument_list|(
name|response
operator|.
name|getScrollId
argument_list|()
argument_list|,
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
argument_list|,
name|onResponse
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|onResponse
operator|.
name|accept
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doStartNextScroll
specifier|protected
name|void
name|doStartNextScroll
parameter_list|(
name|String
name|scrollId
parameter_list|,
name|TimeValue
name|extraKeepAlive
parameter_list|,
name|Consumer
argument_list|<
name|?
super|super
name|Response
argument_list|>
name|onResponse
parameter_list|)
block|{
name|execute
argument_list|(
literal|"POST"
argument_list|,
name|scrollPath
argument_list|()
argument_list|,
name|scrollParams
argument_list|(
name|timeValueNanos
argument_list|(
name|searchRequest
operator|.
name|scroll
argument_list|()
operator|.
name|keepAlive
argument_list|()
operator|.
name|nanos
argument_list|()
operator|+
name|extraKeepAlive
operator|.
name|nanos
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|scrollEntity
argument_list|(
name|scrollId
argument_list|,
name|remoteVersion
argument_list|)
argument_list|,
name|RESPONSE_PARSER
argument_list|,
name|onResponse
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clearScroll
specifier|protected
name|void
name|clearScroll
parameter_list|(
name|String
name|scrollId
parameter_list|,
name|Runnable
name|onCompletion
parameter_list|)
block|{
name|client
operator|.
name|performRequestAsync
argument_list|(
literal|"DELETE"
argument_list|,
name|scrollPath
argument_list|()
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|clearScrollEntity
argument_list|(
name|scrollId
argument_list|,
name|remoteVersion
argument_list|)
argument_list|,
operator|new
name|ResponseListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Response
name|response
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Successfully cleared [{}]"
argument_list|,
name|scrollId
argument_list|)
expr_stmt|;
name|onCompletion
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|onCompletion
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|logFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|ResponseException
condition|)
block|{
name|ResponseException
name|re
init|=
operator|(
name|ResponseException
operator|)
name|e
decl_stmt|;
if|if
condition|(
name|remoteVersion
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_0_0
argument_list|)
operator|&&
name|re
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|==
literal|404
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"Failed to clear scroll [{}] from pre-2.0 Elasticsearch. This is normal if the request terminated "
operator|+
literal|"normally as the scroll has already been cleared automatically."
argument_list|,
name|scrollId
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"Failed to clear scroll [{}]"
argument_list|,
name|scrollId
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cleanup
specifier|protected
name|void
name|cleanup
parameter_list|(
name|Runnable
name|onCompletion
parameter_list|)
block|{
comment|/* This is called on the RestClient's thread pool and attempting to close the client on its          * own threadpool causes it to fail to close. So we always shutdown the RestClient          * asynchronously on a thread in Elasticsearch's generic thread pool. */
name|threadPool
operator|.
name|generic
argument_list|()
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Shut down remote connection"
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
name|error
argument_list|(
literal|"Failed to shutdown the remote connection"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|onCompletion
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|execute
specifier|private
parameter_list|<
name|T
parameter_list|>
name|void
name|execute
parameter_list|(
name|String
name|method
parameter_list|,
name|String
name|uri
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
name|BiFunction
argument_list|<
name|XContentParser
argument_list|,
name|XContentType
argument_list|,
name|T
argument_list|>
name|parser
parameter_list|,
name|Consumer
argument_list|<
name|?
super|super
name|T
argument_list|>
name|listener
parameter_list|)
block|{
comment|// Preserve the thread context so headers survive after the call
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
argument_list|<
name|ThreadContext
operator|.
name|StoredContext
argument_list|>
name|contextSupplier
init|=
name|threadPool
operator|.
name|getThreadContext
argument_list|()
operator|.
name|newRestorableContext
argument_list|(
literal|true
argument_list|)
decl_stmt|;
class|class
name|RetryHelper
extends|extends
name|AbstractRunnable
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|TimeValue
argument_list|>
name|retries
init|=
name|backoffPolicy
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|doRun
parameter_list|()
throws|throws
name|Exception
block|{
name|client
operator|.
name|performRequestAsync
argument_list|(
name|method
argument_list|,
name|uri
argument_list|,
name|params
argument_list|,
name|entity
argument_list|,
operator|new
name|ResponseListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Response
name|response
parameter_list|)
block|{
comment|// Restore the thread context to get the precious headers
try|try
init|(
name|ThreadContext
operator|.
name|StoredContext
name|ctx
init|=
name|contextSupplier
operator|.
name|get
argument_list|()
init|)
block|{
assert|assert
name|ctx
operator|!=
literal|null
assert|;
comment|// eliminates compiler warning
name|T
name|parsedResponse
decl_stmt|;
try|try
block|{
name|HttpEntity
name|responseEntity
init|=
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|InputStream
name|content
init|=
name|responseEntity
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|XContentType
name|xContentType
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|responseEntity
operator|.
name|getContentType
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|mimeType
init|=
name|ContentType
operator|.
name|parse
argument_list|(
name|responseEntity
operator|.
name|getContentType
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|getMimeType
argument_list|()
decl_stmt|;
name|xContentType
operator|=
name|XContentType
operator|.
name|fromMediaType
argument_list|(
name|mimeType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|xContentType
operator|==
literal|null
condition|)
block|{
try|try
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Response didn't include Content-Type: "
operator|+
name|bodyMessage
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|ElasticsearchException
name|ee
init|=
operator|new
name|ElasticsearchException
argument_list|(
literal|"Error extracting body from response"
argument_list|)
decl_stmt|;
name|ee
operator|.
name|addSuppressed
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ee
throw|;
block|}
block|}
comment|// EMPTY is safe here because we don't call namedObject
try|try
init|(
name|XContentParser
name|xContentParser
init|=
name|xContentType
operator|.
name|xContent
argument_list|()
operator|.
name|createParser
argument_list|(
name|NamedXContentRegistry
operator|.
name|EMPTY
argument_list|,
name|content
argument_list|)
init|)
block|{
name|parsedResponse
operator|=
name|parser
operator|.
name|apply
argument_list|(
name|xContentParser
argument_list|,
name|xContentType
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParsingException
name|e
parameter_list|)
block|{
comment|/* Because we're streaming the response we can't get a copy of it here. The best we can do is hint that it                                  * is totally wrong and we're probably not talking to Elasticsearch. */
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Error parsing the response, remote is likely not an Elasticsearch instance"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Error deserializing response, remote is likely not an Elasticsearch instance"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|listener
operator|.
name|accept
argument_list|(
name|parsedResponse
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
name|Exception
name|e
parameter_list|)
block|{
try|try
init|(
name|ThreadContext
operator|.
name|StoredContext
name|ctx
init|=
name|contextSupplier
operator|.
name|get
argument_list|()
init|)
block|{
assert|assert
name|ctx
operator|!=
literal|null
assert|;
comment|// eliminates compiler warning
if|if
condition|(
name|e
operator|instanceof
name|ResponseException
condition|)
block|{
name|ResponseException
name|re
init|=
operator|(
name|ResponseException
operator|)
name|e
decl_stmt|;
if|if
condition|(
name|RestStatus
operator|.
name|TOO_MANY_REQUESTS
operator|.
name|getStatus
argument_list|()
operator|==
name|re
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
condition|)
block|{
if|if
condition|(
name|retries
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|TimeValue
name|delay
init|=
name|retries
operator|.
name|next
argument_list|()
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"retrying rejected search after [{}]"
argument_list|,
name|delay
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|countSearchRetry
operator|.
name|run
argument_list|()
expr_stmt|;
name|threadPool
operator|.
name|schedule
argument_list|(
name|delay
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
name|RetryHelper
operator|.
name|this
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|e
operator|=
name|wrapExceptionToPreserveStatus
argument_list|(
name|re
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|re
operator|.
name|getResponse
argument_list|()
operator|.
name|getEntity
argument_list|()
argument_list|,
name|re
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|ContentTooLongException
condition|)
block|{
name|e
operator|=
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Remote responded with a chunk that was too large. Use a smaller batch size."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|fail
operator|.
name|accept
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|t
parameter_list|)
block|{
name|fail
operator|.
name|accept
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
operator|new
name|RetryHelper
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
comment|/**      * Wrap the ResponseException in an exception that'll preserve its status code if possible so we can send it back to the user. We might      * not have a constant for the status code so in that case we just use 500 instead. We also extract make sure to include the response      * body in the message so the user can figure out *why* the remote Elasticsearch service threw the error back to us.      */
DECL|method|wrapExceptionToPreserveStatus
specifier|static
name|ElasticsearchStatusException
name|wrapExceptionToPreserveStatus
parameter_list|(
name|int
name|statusCode
parameter_list|,
annotation|@
name|Nullable
name|HttpEntity
name|entity
parameter_list|,
name|Exception
name|cause
parameter_list|)
block|{
name|RestStatus
name|status
init|=
name|RestStatus
operator|.
name|fromCode
argument_list|(
name|statusCode
argument_list|)
decl_stmt|;
name|String
name|messagePrefix
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|status
operator|==
literal|null
condition|)
block|{
name|messagePrefix
operator|=
literal|"Couldn't extract status ["
operator|+
name|statusCode
operator|+
literal|"]. "
expr_stmt|;
name|status
operator|=
name|RestStatus
operator|.
name|INTERNAL_SERVER_ERROR
expr_stmt|;
block|}
try|try
block|{
return|return
operator|new
name|ElasticsearchStatusException
argument_list|(
name|messagePrefix
operator|+
name|bodyMessage
argument_list|(
name|entity
argument_list|)
argument_list|,
name|status
argument_list|,
name|cause
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ElasticsearchStatusException
name|e
init|=
operator|new
name|ElasticsearchStatusException
argument_list|(
name|messagePrefix
operator|+
literal|"Failed to extract body."
argument_list|,
name|status
argument_list|,
name|cause
argument_list|)
decl_stmt|;
name|e
operator|.
name|addSuppressed
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
block|}
DECL|method|bodyMessage
specifier|static
name|String
name|bodyMessage
parameter_list|(
annotation|@
name|Nullable
name|HttpEntity
name|entity
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|entity
operator|==
literal|null
condition|)
block|{
return|return
literal|"No error body."
return|;
block|}
else|else
block|{
return|return
literal|"body="
operator|+
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

