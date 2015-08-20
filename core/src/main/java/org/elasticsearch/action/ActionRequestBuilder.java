begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
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
name|base
operator|.
name|Preconditions
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
name|PlainListenableActionFuture
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
name|ElasticsearchClient
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
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ActionRequestBuilder
specifier|public
specifier|abstract
class|class
name|ActionRequestBuilder
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|,
name|RequestBuilder
extends|extends
name|ActionRequestBuilder
parameter_list|<
name|Request
parameter_list|,
name|Response
parameter_list|,
name|RequestBuilder
parameter_list|>
parameter_list|>
block|{
DECL|field|action
specifier|protected
specifier|final
name|Action
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|>
name|action
decl_stmt|;
DECL|field|request
specifier|protected
specifier|final
name|Request
name|request
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|client
specifier|protected
specifier|final
name|ElasticsearchClient
name|client
decl_stmt|;
DECL|method|ActionRequestBuilder
specifier|protected
name|ActionRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|Action
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|action
argument_list|,
literal|"action must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|threadPool
operator|=
name|client
operator|.
name|threadPool
argument_list|()
expr_stmt|;
block|}
DECL|method|request
specifier|public
name|Request
name|request
parameter_list|()
block|{
return|return
name|this
operator|.
name|request
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|putHeader
specifier|public
specifier|final
name|RequestBuilder
name|putHeader
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|request
operator|.
name|putHeader
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
operator|(
name|RequestBuilder
operator|)
name|this
return|;
block|}
DECL|method|execute
specifier|public
name|ListenableActionFuture
argument_list|<
name|Response
argument_list|>
name|execute
parameter_list|()
block|{
name|PlainListenableActionFuture
argument_list|<
name|Response
argument_list|>
name|future
init|=
operator|new
name|PlainListenableActionFuture
argument_list|<>
argument_list|(
name|threadPool
argument_list|)
decl_stmt|;
name|execute
argument_list|(
name|future
argument_list|)
expr_stmt|;
return|return
name|future
return|;
block|}
comment|/**      * Short version of execute().actionGet().      */
DECL|method|get
specifier|public
name|Response
name|get
parameter_list|()
block|{
return|return
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
return|;
block|}
comment|/**      * Short version of execute().actionGet().      */
DECL|method|get
specifier|public
name|Response
name|get
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
return|return
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|(
name|timeout
argument_list|)
return|;
block|}
comment|/**      * Short version of execute().actionGet().      */
DECL|method|get
specifier|public
name|Response
name|get
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
return|return
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|(
name|timeout
argument_list|)
return|;
block|}
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
name|client
operator|.
name|execute
argument_list|(
name|action
argument_list|,
name|beforeExecute
argument_list|(
name|request
argument_list|)
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**      * A callback to additionally process the request before its executed      */
DECL|method|beforeExecute
specifier|protected
name|Request
name|beforeExecute
parameter_list|(
name|Request
name|request
parameter_list|)
block|{
return|return
name|request
return|;
block|}
block|}
end_class

end_unit
