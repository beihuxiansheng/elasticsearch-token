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
name|InputStream
import|;
end_import

begin_comment
comment|/**  * This class is an adaptation of the Apache HttpClient implementation  *  * @link http://hc.apache.org/httpclient-3.x/  */
end_comment

begin_interface
DECL|interface|PartSource
specifier|public
interface|interface
name|PartSource
block|{
comment|/**      * Gets the number of bytes contained in this source.      *      * @return a value>= 0      */
DECL|method|getLength
name|long
name|getLength
parameter_list|()
function_decl|;
comment|/**      * Gets the name of the file this source represents.      *      * @return the fileName used for posting a MultiPart file part      */
DECL|method|getFileName
name|String
name|getFileName
parameter_list|()
function_decl|;
comment|/**      * Gets a new InputStream for reading this source.  This method can be      * called more than once and should therefore return a new stream every      * time.      *      * @return a new InputStream      * @throws java.io.IOException if an error occurs when creating the InputStream      */
DECL|method|createInputStream
name|InputStream
name|createInputStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

