begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
import|;
end_import

begin_interface
DECL|interface|TransportServiceAdapter
specifier|public
interface|interface
name|TransportServiceAdapter
extends|extends
name|TransportConnectionListener
block|{
comment|/** called by the {@link Transport} implementation once a request has been sent */
DECL|method|onRequestSent
name|void
name|onRequestSent
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|long
name|requestId
parameter_list|,
name|String
name|action
parameter_list|,
name|TransportRequest
name|request
parameter_list|,
name|TransportRequestOptions
name|options
parameter_list|)
function_decl|;
comment|/** called by the {@link Transport} implementation once a response was sent to calling node */
DECL|method|onResponseSent
name|void
name|onResponseSent
parameter_list|(
name|long
name|requestId
parameter_list|,
name|String
name|action
parameter_list|,
name|TransportResponse
name|response
parameter_list|,
name|TransportResponseOptions
name|options
parameter_list|)
function_decl|;
comment|/** called by the {@link Transport} implementation after an exception was sent as a response to an incoming request */
DECL|method|onResponseSent
name|void
name|onResponseSent
parameter_list|(
name|long
name|requestId
parameter_list|,
name|String
name|action
parameter_list|,
name|Exception
name|e
parameter_list|)
function_decl|;
comment|/**      * called by the {@link Transport} implementation when a response or an exception has been received for a previously      * sent request (before any processing or deserialization was done). Returns the appropriate response handler or null if not      * found.      */
DECL|method|onResponseReceived
name|TransportResponseHandler
name|onResponseReceived
parameter_list|(
name|long
name|requestId
parameter_list|)
function_decl|;
comment|/**      * called by the {@link Transport} implementation when an incoming request arrives but before      * any parsing of it has happened (with the exception of the requestId and action)      */
DECL|method|onRequestReceived
name|void
name|onRequestReceived
parameter_list|(
name|long
name|requestId
parameter_list|,
name|String
name|action
parameter_list|)
function_decl|;
DECL|method|getRequestHandler
name|RequestHandlerRegistry
name|getRequestHandler
parameter_list|(
name|String
name|action
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

