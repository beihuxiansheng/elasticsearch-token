begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
package|;
end_package

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
name|common
operator|.
name|xcontent
operator|.
name|XContent
import|;
end_import

begin_comment
comment|/**  * Handler for REST requests  */
end_comment

begin_interface
DECL|interface|RestHandler
specifier|public
interface|interface
name|RestHandler
block|{
comment|/**      * Handles a rest request.      * @param request The request to handle      * @param channel The channel to write the request response to      * @param client A client to use to make internal requests on behalf of the original request      */
DECL|method|handleRequest
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
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|canTripCircuitBreaker
specifier|default
name|boolean
name|canTripCircuitBreaker
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Indicates if the RestHandler supports content as a stream. A stream would be multiple objects delineated by      * {@link XContent#streamSeparator()}. If a handler returns true this will affect the types of content that can be sent to      * this endpoint.      */
DECL|method|supportsContentStream
specifier|default
name|boolean
name|supportsContentStream
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_interface

end_unit

