begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2010 Ning, Inc.  *  * Ning licenses this file to you under the Apache License, version 2.0  * (the "License"); you may not use this file except in compliance with the  * License.  You may obtain a copy of the License at:  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the  * License for the specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.http.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|http
operator|.
name|client
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_comment
comment|/**  * Interface to be used when implementing custom asynchronous I/O HTTP client.  * By default, the {@link org.elasticsearch.util.http.client.providers.NettyAsyncHttpProvider} is used.  */
end_comment

begin_interface
DECL|interface|AsyncHttpProvider
specifier|public
interface|interface
name|AsyncHttpProvider
parameter_list|<
name|A
parameter_list|>
block|{
comment|/**      * Execute the request and invoke the {@link AsyncHandler} when the response arrive.      *      * @param handler an instance of {@link AsyncHandler}      * @return a {@link java.util.concurrent.Future} of Type T.      * @throws IOException      */
DECL|method|execute
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Future
argument_list|<
name|T
argument_list|>
name|execute
parameter_list|(
name|Request
name|request
parameter_list|,
name|AsyncHandler
argument_list|<
name|T
argument_list|>
name|handler
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Close the current underlying TCP/HTTP connection.s      */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
comment|/**      * Prepare a {@link Response}      *      * @param status    {@link HttpResponseStatus}      * @param headers   {@link HttpResponseHeaders}      * @param bodyParts list of {@link HttpResponseBodyPart}      * @return a {@link Response}      */
DECL|method|prepareResponse
specifier|public
name|Response
name|prepareResponse
parameter_list|(
name|HttpResponseStatus
argument_list|<
name|A
argument_list|>
name|status
parameter_list|,
name|HttpResponseHeaders
argument_list|<
name|A
argument_list|>
name|headers
parameter_list|,
name|Collection
argument_list|<
name|HttpResponseBodyPart
argument_list|<
name|A
argument_list|>
argument_list|>
name|bodyParts
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

