begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2010 Ning, Inc.  *  * Ning licenses this file to you under the Apache License, version 2.0  * (the "License"); you may not use this file except in compliance with the  * License.  You may obtain a copy of the License at:  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the  * License for the specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.http.multipart
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|http
operator|.
name|multipart
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * This class is an adaptation of the Apache HttpClient implementation  *  * @link http://hc.apache.org/httpclient-3.x/  */
end_comment

begin_interface
DECL|interface|RequestEntity
specifier|public
interface|interface
name|RequestEntity
block|{
comment|/**      * Tests if {@link #writeRequest(java.io.OutputStream)} can be called more than once.      *      * @return<tt>true</tt> if the entity can be written to {@link java.io.OutputStream} more than once,      *<tt>false</tt> otherwise.      */
DECL|method|isRepeatable
name|boolean
name|isRepeatable
parameter_list|()
function_decl|;
comment|/**      * Writes the request entity to the given stream.      *      * @param out      * @throws java.io.IOException      */
DECL|method|writeRequest
name|void
name|writeRequest
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Gets the request entity's length. This method should return a non-negative value if the content      * length is known or a negative value if it is not. In the latter case the      * EntityEnclosingMethod will use chunk encoding to      * transmit the request entity.      *      * @return a non-negative value when content length is known or a negative value when content length      *         is not known      */
DECL|method|getContentLength
name|long
name|getContentLength
parameter_list|()
function_decl|;
comment|/**      * Gets the entity's content type.  This content type will be used as the value for the      * "Content-Type" header.      *      * @return the entity's content type      */
DECL|method|getContentType
name|String
name|getContentType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

