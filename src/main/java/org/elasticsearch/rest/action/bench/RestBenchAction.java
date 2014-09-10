begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.bench
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|bench
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
name|primitives
operator|.
name|Doubles
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|bench
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
name|action
operator|.
name|search
operator|.
name|SearchType
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
name|Client
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
name|common
operator|.
name|xcontent
operator|.
name|XContent
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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|rest
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
name|rest
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|cache
operator|.
name|clear
operator|.
name|RestClearIndicesCacheAction
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
name|action
operator|.
name|support
operator|.
name|AcknowledgedRestListener
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
name|action
operator|.
name|support
operator|.
name|RestBuilderListener
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
name|ArrayList
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
operator|.
name|contentBuilder
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
name|RestRequest
operator|.
name|Method
operator|.
name|*
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
comment|/**  * REST handler for benchmark actions.  */
end_comment

begin_class
DECL|class|RestBenchAction
specifier|public
class|class
name|RestBenchAction
extends|extends
name|BaseRestHandler
block|{
annotation|@
name|Inject
DECL|method|RestBenchAction
specifier|public
name|RestBenchAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|controller
argument_list|,
name|client
argument_list|)
expr_stmt|;
comment|// List active benchmarks
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_bench"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/_bench"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/{type}/_bench"
argument_list|,
name|this
argument_list|)
expr_stmt|;
comment|// Submit benchmark
name|controller
operator|.
name|registerHandler
argument_list|(
name|PUT
argument_list|,
literal|"/_bench"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|PUT
argument_list|,
literal|"/{index}/_bench"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|PUT
argument_list|,
literal|"/{index}/{type}/_bench"
argument_list|,
name|this
argument_list|)
expr_stmt|;
comment|// Abort benchmark
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/_bench/abort/{name}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
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
name|Client
name|client
parameter_list|)
block|{
switch|switch
condition|(
name|request
operator|.
name|method
argument_list|()
condition|)
block|{
case|case
name|POST
case|:
name|handleAbortRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|client
argument_list|)
expr_stmt|;
break|break;
case|case
name|PUT
case|:
name|handleSubmitRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|client
argument_list|)
expr_stmt|;
break|break;
case|case
name|GET
case|:
name|handleStatusRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|client
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// Politely ignore methods we don't support
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|BytesRestResponse
argument_list|(
name|METHOD_NOT_ALLOWED
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Reports on the status of all actively running benchmarks      */
DECL|method|handleStatusRequest
specifier|private
name|void
name|handleStatusRequest
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
name|Client
name|client
parameter_list|)
block|{
name|BenchmarkStatusRequest
name|benchmarkStatusRequest
init|=
operator|new
name|BenchmarkStatusRequest
argument_list|()
decl_stmt|;
name|client
operator|.
name|benchStatus
argument_list|(
name|benchmarkStatusRequest
argument_list|,
operator|new
name|RestBuilderListener
argument_list|<
name|BenchmarkStatusResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RestResponse
name|buildResponse
parameter_list|(
name|BenchmarkStatusResponse
name|response
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|Exception
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|response
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
operator|new
name|BytesRestResponse
argument_list|(
name|OK
argument_list|,
name|builder
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Aborts an actively running benchmark      */
DECL|method|handleAbortRequest
specifier|private
name|void
name|handleAbortRequest
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
name|Client
name|client
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|benchmarkNames
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
name|AbortBenchmarkRequest
name|abortBenchmarkRequest
init|=
operator|new
name|AbortBenchmarkRequest
argument_list|(
name|benchmarkNames
argument_list|)
decl_stmt|;
name|client
operator|.
name|abortBench
argument_list|(
name|abortBenchmarkRequest
argument_list|,
operator|new
name|AcknowledgedRestListener
argument_list|<
name|AbortBenchmarkResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Submits a benchmark for execution      */
DECL|method|handleSubmitRequest
specifier|private
name|void
name|handleSubmitRequest
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
name|Client
name|client
parameter_list|)
block|{
name|String
index|[]
name|indices
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"index"
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|types
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"type"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BenchmarkRequest
name|benchmarkRequest
decl_stmt|;
try|try
block|{
name|BenchmarkRequestBuilder
name|builder
init|=
operator|new
name|BenchmarkRequestBuilder
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setVerbose
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"verbose"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|benchmarkRequest
operator|=
name|parse
argument_list|(
name|builder
argument_list|,
name|request
operator|.
name|content
argument_list|()
argument_list|,
name|request
operator|.
name|contentUnsafe
argument_list|()
argument_list|)
expr_stmt|;
name|benchmarkRequest
operator|.
name|cascadeGlobalSettings
argument_list|()
expr_stmt|;
comment|// Make sure competitors inherit global settings
name|benchmarkRequest
operator|.
name|applyLateBoundSettings
argument_list|(
name|indices
argument_list|,
name|types
argument_list|)
expr_stmt|;
comment|// Some settings cannot be applied until after parsing
name|Exception
name|ex
init|=
name|benchmarkRequest
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|ex
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ex
throw|;
block|}
name|benchmarkRequest
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to parse search request parameters"
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
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
name|contentBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"error"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
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
literal|"Failed to send failure response"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|client
operator|.
name|bench
argument_list|(
name|benchmarkRequest
argument_list|,
operator|new
name|RestBuilderListener
argument_list|<
name|BenchmarkResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RestResponse
name|buildResponse
parameter_list|(
name|BenchmarkResponse
name|response
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|Exception
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|response
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
operator|new
name|BytesRestResponse
argument_list|(
name|OK
argument_list|,
name|builder
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
specifier|static
name|BenchmarkRequest
name|parse
parameter_list|(
name|BenchmarkRequestBuilder
name|builder
parameter_list|,
name|BytesReference
name|data
parameter_list|,
name|boolean
name|contentUnsafe
parameter_list|)
throws|throws
name|Exception
block|{
name|XContent
name|xContent
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|XContentParser
name|p
init|=
name|xContent
operator|.
name|createParser
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|p
operator|.
name|nextToken
argument_list|()
decl_stmt|;
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
assert|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|p
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
switch|switch
condition|(
name|token
condition|)
block|{
case|case
name|START_ARRAY
case|:
if|if
condition|(
literal|"requests"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|p
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
assert|;
name|XContentBuilder
name|payloadBuilder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|p
operator|.
name|contentType
argument_list|()
argument_list|)
operator|.
name|copyCurrentStructure
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|SearchRequest
name|req
init|=
operator|new
name|SearchRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|source
argument_list|(
name|payloadBuilder
operator|.
name|bytes
argument_list|()
argument_list|,
name|contentUnsafe
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addSearchRequest
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"competitors"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
while|while
condition|(
name|p
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|builder
operator|.
name|addCompetitor
argument_list|(
name|parse
argument_list|(
name|p
argument_list|,
name|contentUnsafe
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"percentiles"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|Double
argument_list|>
name|percentiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|p
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|percentiles
operator|.
name|add
argument_list|(
name|p
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setPercentiles
argument_list|(
name|Doubles
operator|.
name|toArray
argument_list|(
name|percentiles
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing array field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
break|break;
case|case
name|START_OBJECT
case|:
if|if
condition|(
literal|"clear_caches"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|BenchmarkSettings
operator|.
name|ClearCachesSettings
name|clearCachesSettings
init|=
operator|new
name|BenchmarkSettings
operator|.
name|ClearCachesSettings
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setClearCachesSettings
argument_list|(
name|clearCachesSettings
argument_list|)
expr_stmt|;
name|parseClearCaches
argument_list|(
name|p
argument_list|,
name|clearCachesSettings
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing object field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
break|break;
case|case
name|FIELD_NAME
case|:
name|fieldName
operator|=
name|p
operator|.
name|text
argument_list|()
expr_stmt|;
break|break;
case|case
name|VALUE_NUMBER
case|:
if|if
condition|(
literal|"num_executor_nodes"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setNumExecutorNodes
argument_list|(
name|p
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"iterations"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setIterations
argument_list|(
name|p
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"concurrency"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setConcurrency
argument_list|(
name|p
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"multiplier"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setMultiplier
argument_list|(
name|p
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"num_slowest"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setNumSlowest
argument_list|(
name|p
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing numeric field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
break|break;
case|case
name|VALUE_BOOLEAN
case|:
if|if
condition|(
literal|"warmup"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setWarmup
argument_list|(
name|p
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"clear_caches"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|p
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing field ["
operator|+
name|fieldName
operator|+
literal|"] must specify which caches to clear"
argument_list|)
throw|;
block|}
else|else
block|{
name|builder
operator|.
name|setAllowCacheClearing
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing boolean field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
break|break;
case|case
name|VALUE_STRING
case|:
if|if
condition|(
literal|"name"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setBenchmarkId
argument_list|(
name|p
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing string field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
break|break;
default|default:
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing "
operator|+
name|token
operator|.
name|name
argument_list|()
operator|+
literal|" field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
block|}
return|return
name|builder
operator|.
name|request
argument_list|()
return|;
block|}
DECL|method|parse
specifier|private
specifier|static
name|BenchmarkCompetitorBuilder
name|parse
parameter_list|(
name|XContentParser
name|p
parameter_list|,
name|boolean
name|contentUnsafe
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentParser
operator|.
name|Token
name|token
init|=
name|p
operator|.
name|currentToken
argument_list|()
decl_stmt|;
name|BenchmarkCompetitorBuilder
name|builder
init|=
operator|new
name|BenchmarkCompetitorBuilder
argument_list|()
decl_stmt|;
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
assert|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|p
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
switch|switch
condition|(
name|token
condition|)
block|{
case|case
name|START_ARRAY
case|:
if|if
condition|(
literal|"requests"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|p
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
assert|;
name|XContentBuilder
name|payloadBuilder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|p
operator|.
name|contentType
argument_list|()
argument_list|)
operator|.
name|copyCurrentStructure
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|SearchRequest
name|req
init|=
operator|new
name|SearchRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|source
argument_list|(
name|payloadBuilder
operator|.
name|bytes
argument_list|()
argument_list|,
name|contentUnsafe
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addSearchRequest
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"indices"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|perCompetitorIndices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|p
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|perCompetitorIndices
operator|.
name|add
argument_list|(
name|p
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing array field ["
operator|+
name|fieldName
operator|+
literal|"] expected string values but got: "
operator|+
name|token
argument_list|)
throw|;
block|}
block|}
name|builder
operator|.
name|setIndices
argument_list|(
name|perCompetitorIndices
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|perCompetitorIndices
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"types"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|perCompetitorTypes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|p
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|perCompetitorTypes
operator|.
name|add
argument_list|(
name|p
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing array field ["
operator|+
name|fieldName
operator|+
literal|"] expected string values but got: "
operator|+
name|token
argument_list|)
throw|;
block|}
block|}
name|builder
operator|.
name|setTypes
argument_list|(
name|perCompetitorTypes
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|perCompetitorTypes
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing array field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
break|break;
case|case
name|START_OBJECT
case|:
if|if
condition|(
literal|"clear_caches"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|BenchmarkSettings
operator|.
name|ClearCachesSettings
name|clearCachesSettings
init|=
operator|new
name|BenchmarkSettings
operator|.
name|ClearCachesSettings
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setClearCachesSettings
argument_list|(
name|clearCachesSettings
argument_list|)
expr_stmt|;
name|parseClearCaches
argument_list|(
name|p
argument_list|,
name|clearCachesSettings
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing object field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
break|break;
case|case
name|FIELD_NAME
case|:
name|fieldName
operator|=
name|p
operator|.
name|text
argument_list|()
expr_stmt|;
break|break;
case|case
name|VALUE_NUMBER
case|:
if|if
condition|(
literal|"multiplier"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setMultiplier
argument_list|(
name|p
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"num_slowest"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setNumSlowest
argument_list|(
name|p
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"iterations"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setIterations
argument_list|(
name|p
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"concurrency"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setConcurrency
argument_list|(
name|p
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing numeric field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
break|break;
case|case
name|VALUE_BOOLEAN
case|:
if|if
condition|(
literal|"warmup"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setWarmup
argument_list|(
name|p
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"clear_caches"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|p
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing field ["
operator|+
name|fieldName
operator|+
literal|"] must specify which caches to clear"
argument_list|)
throw|;
block|}
else|else
block|{
name|builder
operator|.
name|setAllowCacheClearing
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing boolean field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
break|break;
case|case
name|VALUE_STRING
case|:
if|if
condition|(
literal|"name"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setName
argument_list|(
name|p
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"search_type"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"searchType"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setSearchType
argument_list|(
name|SearchType
operator|.
name|fromString
argument_list|(
name|p
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing string field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
break|break;
default|default:
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing "
operator|+
name|token
operator|.
name|name
argument_list|()
operator|+
literal|" field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
block|}
return|return
name|builder
return|;
block|}
DECL|method|parseClearCaches
specifier|private
specifier|static
name|void
name|parseClearCaches
parameter_list|(
name|XContentParser
name|p
parameter_list|,
name|BenchmarkSettings
operator|.
name|ClearCachesSettings
name|clearCachesSettings
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|p
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
switch|switch
condition|(
name|token
condition|)
block|{
case|case
name|START_OBJECT
case|:
break|break;
case|case
name|VALUE_BOOLEAN
case|:
if|if
condition|(
name|RestClearIndicesCacheAction
operator|.
name|Fields
operator|.
name|FILTER
operator|.
name|match
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|clearCachesSettings
operator|.
name|filterCache
argument_list|(
name|p
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|RestClearIndicesCacheAction
operator|.
name|Fields
operator|.
name|FIELD_DATA
operator|.
name|match
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|clearCachesSettings
operator|.
name|fieldDataCache
argument_list|(
name|p
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|RestClearIndicesCacheAction
operator|.
name|Fields
operator|.
name|ID
operator|.
name|match
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|clearCachesSettings
operator|.
name|idCache
argument_list|(
name|p
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|RestClearIndicesCacheAction
operator|.
name|Fields
operator|.
name|RECYCLER
operator|.
name|match
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|clearCachesSettings
operator|.
name|recycler
argument_list|(
name|p
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing "
operator|+
name|token
operator|.
name|name
argument_list|()
operator|+
literal|" field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
break|break;
case|case
name|START_ARRAY
case|:
name|List
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|p
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|p
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|RestClearIndicesCacheAction
operator|.
name|Fields
operator|.
name|FIELDS
operator|.
name|match
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|clearCachesSettings
operator|.
name|fields
argument_list|(
name|fields
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|RestClearIndicesCacheAction
operator|.
name|Fields
operator|.
name|FILTER_KEYS
operator|.
name|match
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|clearCachesSettings
operator|.
name|filterKeys
argument_list|(
name|fields
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing "
operator|+
name|token
operator|.
name|name
argument_list|()
operator|+
literal|" field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
break|break;
case|case
name|FIELD_NAME
case|:
name|fieldName
operator|=
name|p
operator|.
name|text
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed parsing "
operator|+
name|token
operator|.
name|name
argument_list|()
operator|+
literal|" field ["
operator|+
name|fieldName
operator|+
literal|"] field is not recognized"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

