begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequestValidationException
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
name|GenericAction
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
name|support
operator|.
name|ActiveShardCount
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
name|cluster
operator|.
name|service
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
name|indices
operator|.
name|query
operator|.
name|IndicesQueriesRegistry
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
name|BaseRestHandler
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|AggregatorParsers
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
name|suggest
operator|.
name|Suggesters
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|LoggingTaskListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|Task
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

begin_class
DECL|class|AbstractBaseReindexRestHandler
specifier|public
specifier|abstract
class|class
name|AbstractBaseReindexRestHandler
parameter_list|<
name|Request
extends|extends
name|AbstractBulkByScrollRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|,
name|A
extends|extends
name|GenericAction
parameter_list|<
name|Request
parameter_list|,
name|BulkIndexByScrollResponse
parameter_list|>
parameter_list|>
extends|extends
name|BaseRestHandler
block|{
DECL|field|indicesQueriesRegistry
specifier|protected
specifier|final
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
decl_stmt|;
DECL|field|aggParsers
specifier|protected
specifier|final
name|AggregatorParsers
name|aggParsers
decl_stmt|;
DECL|field|suggesters
specifier|protected
specifier|final
name|Suggesters
name|suggesters
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|action
specifier|private
specifier|final
name|A
name|action
decl_stmt|;
DECL|method|AbstractBaseReindexRestHandler
specifier|protected
name|AbstractBaseReindexRestHandler
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
parameter_list|,
name|AggregatorParsers
name|aggParsers
parameter_list|,
name|Suggesters
name|suggesters
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|A
name|action
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicesQueriesRegistry
operator|=
name|indicesQueriesRegistry
expr_stmt|;
name|this
operator|.
name|aggParsers
operator|=
name|aggParsers
expr_stmt|;
name|this
operator|.
name|suggesters
operator|=
name|suggesters
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
DECL|method|handleRequest
specifier|protected
name|void
name|handleRequest
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
name|boolean
name|includeCreated
parameter_list|,
name|boolean
name|includeUpdated
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Build the internal request
name|Request
name|internal
init|=
name|setCommonOptions
argument_list|(
name|request
argument_list|,
name|buildRequest
argument_list|(
name|request
argument_list|)
argument_list|)
decl_stmt|;
comment|// Executes the request and waits for completion
if|if
condition|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"wait_for_completion"
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|BulkByScrollTask
operator|.
name|Status
operator|.
name|INCLUDE_CREATED
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|includeCreated
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|BulkByScrollTask
operator|.
name|Status
operator|.
name|INCLUDE_UPDATED
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|includeUpdated
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|executeLocally
argument_list|(
name|action
argument_list|,
name|internal
argument_list|,
operator|new
name|BulkIndexByScrollResponseContentListener
argument_list|(
name|channel
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
name|internal
operator|.
name|setShouldStoreResult
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/*          * Let's try and validate before forking so the user gets some error. The          * task can't totally validate until it starts but this is better than          * nothing.          */
name|ActionRequestValidationException
name|validationException
init|=
name|internal
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|validationException
operator|!=
literal|null
condition|)
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
name|validationException
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|sendTask
argument_list|(
name|channel
argument_list|,
name|client
operator|.
name|executeLocally
argument_list|(
name|action
argument_list|,
name|internal
argument_list|,
name|LoggingTaskListener
operator|.
name|instance
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Build the Request based on the RestRequest.      */
DECL|method|buildRequest
specifier|protected
specifier|abstract
name|Request
name|buildRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Sets common options of {@link AbstractBulkByScrollRequest} requests.      */
DECL|method|setCommonOptions
specifier|protected
name|Request
name|setCommonOptions
parameter_list|(
name|RestRequest
name|restRequest
parameter_list|,
name|Request
name|request
parameter_list|)
block|{
assert|assert
name|restRequest
operator|!=
literal|null
operator|:
literal|"RestRequest should not be null"
assert|;
assert|assert
name|request
operator|!=
literal|null
operator|:
literal|"Request should not be null"
assert|;
name|request
operator|.
name|setRefresh
argument_list|(
name|restRequest
operator|.
name|paramAsBoolean
argument_list|(
literal|"refresh"
argument_list|,
name|request
operator|.
name|isRefresh
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|setTimeout
argument_list|(
name|restRequest
operator|.
name|paramAsTime
argument_list|(
literal|"timeout"
argument_list|,
name|request
operator|.
name|getTimeout
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|waitForActiveShards
init|=
name|restRequest
operator|.
name|param
argument_list|(
literal|"wait_for_active_shards"
argument_list|)
decl_stmt|;
if|if
condition|(
name|waitForActiveShards
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|setWaitForActiveShards
argument_list|(
name|ActiveShardCount
operator|.
name|parseString
argument_list|(
name|waitForActiveShards
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Float
name|requestsPerSecond
init|=
name|parseRequestsPerSecond
argument_list|(
name|restRequest
argument_list|)
decl_stmt|;
if|if
condition|(
name|requestsPerSecond
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|setRequestsPerSecond
argument_list|(
name|requestsPerSecond
argument_list|)
expr_stmt|;
block|}
return|return
name|request
return|;
block|}
DECL|method|sendTask
specifier|private
name|void
name|sendTask
parameter_list|(
name|RestChannel
name|channel
parameter_list|,
name|Task
name|task
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|XContentBuilder
name|builder
init|=
name|channel
operator|.
name|newBuilder
argument_list|()
init|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"task"
argument_list|,
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|":"
operator|+
name|task
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|BytesRestResponse
argument_list|(
name|RestStatus
operator|.
name|OK
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return requests_per_second from the request as a float if it was on the request, null otherwise      */
DECL|method|parseRequestsPerSecond
specifier|public
specifier|static
name|Float
name|parseRequestsPerSecond
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
name|String
name|requestsPerSecondString
init|=
name|request
operator|.
name|param
argument_list|(
literal|"requests_per_second"
argument_list|)
decl_stmt|;
if|if
condition|(
name|requestsPerSecondString
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|float
name|requestsPerSecond
decl_stmt|;
try|try
block|{
name|requestsPerSecond
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|requestsPerSecondString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[requests_per_second] must be a float greater than 0. Use -1 to disable throttling."
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|requestsPerSecond
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|Float
operator|.
name|POSITIVE_INFINITY
return|;
block|}
if|if
condition|(
name|requestsPerSecond
operator|<=
literal|0
condition|)
block|{
comment|// We validate here and in the setters because the setters use "Float.POSITIVE_INFINITY" instead of -1
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[requests_per_second] must be a float greater than 0. Use -1 to disable throttling."
argument_list|)
throw|;
block|}
return|return
name|requestsPerSecond
return|;
block|}
block|}
end_class

end_unit

