begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
package|;
end_package

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
name|stream
operator|.
name|Streamable
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|TransportResponseHandler
specifier|public
interface|interface
name|TransportResponseHandler
parameter_list|<
name|T
extends|extends
name|Streamable
parameter_list|>
block|{
comment|/**      * creates a new instance of the return type from the remote call.      * called by the infra before de-serializing the response.      *      * @return a new response copy.      */
DECL|method|newInstance
name|T
name|newInstance
parameter_list|()
function_decl|;
DECL|method|handleResponse
name|void
name|handleResponse
parameter_list|(
name|T
name|response
parameter_list|)
function_decl|;
DECL|method|handleException
name|void
name|handleException
parameter_list|(
name|TransportException
name|exp
parameter_list|)
function_decl|;
DECL|method|executor
name|String
name|executor
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

