begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2010 Ning, Inc.  *  * Ning licenses this file to you under the Apache License, version 2.0  * (the "License"); you may not use this file except in compliance with the  * License.  You may obtain a copy of the License at:  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the  * License for the specific language governing permissions and limitations  * under the License.  *  */
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
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|http
operator|.
name|url
operator|.
name|Url
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Represents the asynchronous HTTP response callback for an {@link org.elasticsearch.util.http.client.AsyncCompletionHandler}  */
end_comment

begin_interface
DECL|interface|Response
specifier|public
interface|interface
name|Response
block|{
comment|/**      * Returns the status code for the request.      *      * @return The status code      */
DECL|method|getStatusCode
specifier|public
name|int
name|getStatusCode
parameter_list|()
function_decl|;
comment|/**      * Returns the status text for the request.      *      * @return The status text      */
DECL|method|getStatusText
specifier|public
name|String
name|getStatusText
parameter_list|()
function_decl|;
comment|/**      * Returns an input stream for the response body. Note that you should not try to get this more than once,      * and that you should not close the stream.      *      * @return The input stream      * @throws java.io.IOException      */
DECL|method|getResponseBodyAsStream
specifier|public
name|InputStream
name|getResponseBodyAsStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns the first maxLength bytes of the response body as a string. Note that this does not check      * whether the content type is actually a textual one, but it will use the charset if present in the content      * type header.      *      * @param maxLength The maximum number of bytes to read      * @return The response body      * @throws java.io.IOException      */
DECL|method|getResponseBodyExcerpt
specifier|public
name|String
name|getResponseBodyExcerpt
parameter_list|(
name|int
name|maxLength
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Return the entire response body as a String.      *      * @return the entire response body as a String.      * @throws IOException      */
DECL|method|getResponseBody
specifier|public
name|String
name|getResponseBody
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Return the request {@link Url}. Note that if the request got redirected, the value of the {@link Url} will be      * the last valid redirect url.      *      * @return the request {@link Url}.      * @throws MalformedURLException      */
DECL|method|getUrl
specifier|public
name|Url
name|getUrl
parameter_list|()
throws|throws
name|MalformedURLException
function_decl|;
comment|/**      * Return the content-type header value.      *      * @return the content-type header value.      */
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|()
function_decl|;
comment|/**      * Return the response header      *      * @return the response header      */
DECL|method|getHeader
specifier|public
name|String
name|getHeader
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Return a {@link List} of the response header value.      *      * @return the response header      */
DECL|method|getHeaders
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getHeaders
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|getHeaders
specifier|public
name|Headers
name|getHeaders
parameter_list|()
function_decl|;
comment|/**      * Return true if the response redirects to another object.      *      * @return True if the response redirects to another object.      */
DECL|method|isRedirected
name|boolean
name|isRedirected
parameter_list|()
function_decl|;
comment|/**      * Subclasses SHOULD implement toString() in a way that identifies the request for logging.      *      * @return The textual representation      */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
function_decl|;
comment|/**      * Return the list of {@link Cookie}.      */
DECL|method|getCookies
specifier|public
name|List
argument_list|<
name|Cookie
argument_list|>
name|getCookies
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

