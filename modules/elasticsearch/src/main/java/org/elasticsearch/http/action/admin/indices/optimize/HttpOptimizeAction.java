begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.http.action.admin.indices.optimize
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|optimize
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
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
name|action
operator|.
name|ActionListener
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
name|indices
operator|.
name|optimize
operator|.
name|OptimizeRequest
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
name|indices
operator|.
name|optimize
operator|.
name|OptimizeResponse
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
name|broadcast
operator|.
name|BroadcastOperationThreading
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
name|http
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
name|http
operator|.
name|action
operator|.
name|support
operator|.
name|HttpActions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|action
operator|.
name|support
operator|.
name|HttpJsonBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
operator|.
name|JsonBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|settings
operator|.
name|Settings
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|HttpResponse
operator|.
name|Status
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|HttpOptimizeAction
specifier|public
class|class
name|HttpOptimizeAction
extends|extends
name|BaseHttpServerHandler
block|{
DECL|method|HttpOptimizeAction
annotation|@
name|Inject
specifier|public
name|HttpOptimizeAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|HttpServer
name|httpService
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|httpService
operator|.
name|registerHandler
argument_list|(
name|HttpRequest
operator|.
name|Method
operator|.
name|POST
argument_list|,
literal|"/_optimize"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|httpService
operator|.
name|registerHandler
argument_list|(
name|HttpRequest
operator|.
name|Method
operator|.
name|POST
argument_list|,
literal|"/{index}/_optimize"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|handleRequest
annotation|@
name|Override
specifier|public
name|void
name|handleRequest
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
name|OptimizeRequest
name|optimizeRequest
init|=
operator|new
name|OptimizeRequest
argument_list|(
name|HttpActions
operator|.
name|splitIndices
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"index"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|optimizeRequest
operator|.
name|waitForMerge
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"waitForMerge"
argument_list|,
name|optimizeRequest
operator|.
name|waitForMerge
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|optimizeRequest
operator|.
name|maxNumSegments
argument_list|(
name|request
operator|.
name|paramAsInt
argument_list|(
literal|"maxNumSegments"
argument_list|,
name|optimizeRequest
operator|.
name|maxNumSegments
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|optimizeRequest
operator|.
name|onlyExpungeDeletes
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"onlyExpungeDeletes"
argument_list|,
name|optimizeRequest
operator|.
name|onlyExpungeDeletes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|optimizeRequest
operator|.
name|flush
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"flush"
argument_list|,
name|optimizeRequest
operator|.
name|flush
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|optimizeRequest
operator|.
name|refresh
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"refresh"
argument_list|,
name|optimizeRequest
operator|.
name|refresh
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// we just send back a response, no need to fork a listener
name|optimizeRequest
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|BroadcastOperationThreading
name|operationThreading
init|=
name|BroadcastOperationThreading
operator|.
name|fromString
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"operationThreading"
argument_list|)
argument_list|,
name|BroadcastOperationThreading
operator|.
name|SINGLE_THREAD
argument_list|)
decl_stmt|;
if|if
condition|(
name|operationThreading
operator|==
name|BroadcastOperationThreading
operator|.
name|NO_THREADS
condition|)
block|{
comment|// since we don't spawn, don't allow no_threads, but change it to a single thread
name|operationThreading
operator|=
name|BroadcastOperationThreading
operator|.
name|THREAD_PER_SHARD
expr_stmt|;
block|}
name|optimizeRequest
operator|.
name|operationThreading
argument_list|(
name|operationThreading
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
name|JsonHttpResponse
argument_list|(
name|request
argument_list|,
name|BAD_REQUEST
argument_list|,
name|JsonBuilder
operator|.
name|jsonBuilder
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
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|execOptimize
argument_list|(
name|optimizeRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|OptimizeResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|OptimizeResponse
name|response
parameter_list|)
block|{
try|try
block|{
name|JsonBuilder
name|builder
init|=
name|HttpJsonBuilder
operator|.
name|cached
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"ok"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"_shards"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"total"
argument_list|,
name|response
operator|.
name|totalShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"successful"
argument_list|,
name|response
operator|.
name|successfulShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"failed"
argument_list|,
name|response
operator|.
name|failedShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
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
name|JsonHttpResponse
argument_list|(
name|request
argument_list|,
name|OK
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|e
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
name|JsonThrowableHttpResponse
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
literal|"Failed to send failure response"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|spawn
annotation|@
name|Override
specifier|public
name|boolean
name|spawn
parameter_list|()
block|{
comment|// we don't spawn since we fork in index replication based on operation
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

