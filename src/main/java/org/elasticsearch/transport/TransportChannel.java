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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A transport channel allows to send a response to a request on the channel.  */
end_comment

begin_interface
DECL|interface|TransportChannel
specifier|public
interface|interface
name|TransportChannel
block|{
DECL|method|action
name|String
name|action
parameter_list|()
function_decl|;
DECL|method|sendResponse
name|void
name|sendResponse
parameter_list|(
name|TransportResponse
name|response
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|sendResponse
name|void
name|sendResponse
parameter_list|(
name|TransportResponse
name|response
parameter_list|,
name|TransportResponseOptions
name|options
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|sendResponse
name|void
name|sendResponse
parameter_list|(
name|Throwable
name|error
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

