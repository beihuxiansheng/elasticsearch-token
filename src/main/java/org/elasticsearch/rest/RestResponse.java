begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
DECL|interface|RestResponse
specifier|public
interface|interface
name|RestResponse
block|{
comment|/**      * Can the content byte[] be used only with this thread (<tt>false</tt>), or by any thread (<tt>true</tt>).      */
DECL|method|contentThreadSafe
name|boolean
name|contentThreadSafe
parameter_list|()
function_decl|;
DECL|method|contentType
name|String
name|contentType
parameter_list|()
function_decl|;
comment|/**      * Returns the actual content. Note, use {@link #contentLength()} in order to know the      * content length of the byte array.      */
DECL|method|content
name|byte
index|[]
name|content
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * The content length.      */
DECL|method|contentLength
name|int
name|contentLength
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|prefixContent
name|byte
index|[]
name|prefixContent
parameter_list|()
function_decl|;
DECL|method|prefixContentLength
name|int
name|prefixContentLength
parameter_list|()
function_decl|;
DECL|method|suffixContent
name|byte
index|[]
name|suffixContent
parameter_list|()
function_decl|;
DECL|method|suffixContentLength
name|int
name|suffixContentLength
parameter_list|()
function_decl|;
DECL|method|status
name|RestStatus
name|status
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

