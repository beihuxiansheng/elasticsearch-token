begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|ActionRequestBuilder
specifier|public
interface|interface
name|ActionRequestBuilder
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|>
block|{
DECL|method|request
name|Request
name|request
parameter_list|()
function_decl|;
DECL|method|execute
name|ListenableActionFuture
argument_list|<
name|Response
argument_list|>
name|execute
parameter_list|()
function_decl|;
DECL|method|execute
name|void
name|execute
parameter_list|(
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

