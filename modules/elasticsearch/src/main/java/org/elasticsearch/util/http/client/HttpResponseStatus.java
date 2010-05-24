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

begin_comment
comment|/**  * A class that represent the HTTP response' status line (code + text)  */
end_comment

begin_class
DECL|class|HttpResponseStatus
specifier|public
specifier|abstract
class|class
name|HttpResponseStatus
parameter_list|<
name|R
parameter_list|>
extends|extends
name|HttpContent
argument_list|<
name|R
argument_list|>
block|{
DECL|method|HttpResponseStatus
specifier|public
name|HttpResponseStatus
parameter_list|(
name|Url
name|url
parameter_list|,
name|R
name|response
parameter_list|,
name|AsyncHttpProvider
argument_list|<
name|R
argument_list|>
name|provider
parameter_list|)
block|{
name|super
argument_list|(
name|url
argument_list|,
name|response
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return the response status code      *      * @return the response status code      */
DECL|method|getStatusCode
specifier|abstract
specifier|public
name|int
name|getStatusCode
parameter_list|()
function_decl|;
comment|/**      * Return the response status text      *      * @return the response status text      */
DECL|method|getStatusText
specifier|abstract
specifier|public
name|String
name|getStatusText
parameter_list|()
function_decl|;
comment|/**      * Protocol name from status line.      *      * @return Protocol name.      */
DECL|method|getProtocolName
specifier|abstract
specifier|public
name|String
name|getProtocolName
parameter_list|()
function_decl|;
comment|/**      * Protocol major version.      *      * @return Major version.      */
DECL|method|getProtocolMajorVersion
specifier|abstract
specifier|public
name|int
name|getProtocolMajorVersion
parameter_list|()
function_decl|;
comment|/**      * Protocol minor version.      *      * @return Minor version.      */
DECL|method|getProtocolMinorVersion
specifier|abstract
specifier|public
name|int
name|getProtocolMinorVersion
parameter_list|()
function_decl|;
comment|/**      * Full protocol name + version      *      * @return protocol name + version      */
DECL|method|getProtocolText
specifier|abstract
specifier|public
name|String
name|getProtocolText
parameter_list|()
function_decl|;
block|}
end_class

end_unit

