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

begin_comment
comment|/**  * An asynchronous handler or callback which gets invoked as soon as some data are available when  * processing an asynchronous response. Callbacks method gets invoked in the following order:  * (1) {@link #onStatusReceived(HttpResponseStatus)}  * (2) {@link #onHeadersReceived(HttpResponseHeaders)}  * (3) {@link #onBodyPartReceived(HttpResponseBodyPart)}, which could be invoked multiple times  * (4) {@link #onCompleted()}, once the response has been fully read.  *  * Interrupting the process of the asynchronous response can be achieved by  * returning a {@link AsyncHandler.STATE#ABORT} at any moment during the  * processing of the asynchronous response.  *  * @param<T> Type of object returned by the {@link java.util.concurrent.Future#get}  */
end_comment

begin_interface
DECL|interface|AsyncHandler
specifier|public
interface|interface
name|AsyncHandler
parameter_list|<
name|T
parameter_list|>
block|{
DECL|enum|STATE
specifier|public
specifier|static
enum|enum
name|STATE
block|{
DECL|enum constant|ABORT
DECL|enum constant|CONTINUE
name|ABORT
block|,
name|CONTINUE
block|}
comment|/**      * Invoked when an unexpected exception occurs during the processing of the response      *      * @param t a {@link Throwable}      */
DECL|method|onThrowable
name|void
name|onThrowable
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
comment|/**      * Invoked as soon as some response body part are received.      *      * @param bodyPart response's body part.      * @throws Exception      */
DECL|method|onBodyPartReceived
name|STATE
name|onBodyPartReceived
parameter_list|(
name|HttpResponseBodyPart
argument_list|<
name|T
argument_list|>
name|bodyPart
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Invoked as soon as the HTTP status line has been received      *      * @param responseStatus the status code and test of the response      * @throws Exception      */
DECL|method|onStatusReceived
name|STATE
name|onStatusReceived
parameter_list|(
name|HttpResponseStatus
argument_list|<
name|T
argument_list|>
name|responseStatus
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Invoked as soon as the HTTP headers has been received.      *      * @param headers the HTTP headers.      * @throws Exception      */
DECL|method|onHeadersReceived
name|STATE
name|onHeadersReceived
parameter_list|(
name|HttpResponseHeaders
argument_list|<
name|T
argument_list|>
name|headers
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Invoked once the HTTP response has been fully received      *      * @return T Type of the value that will be returned by the associated {@link java.util.concurrent.Future}      * @throws Exception      */
DECL|method|onCompleted
name|T
name|onCompleted
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

