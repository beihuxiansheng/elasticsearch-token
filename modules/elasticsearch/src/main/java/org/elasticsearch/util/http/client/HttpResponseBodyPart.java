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
comment|/**  * A callback class used when an HTTP response body is received.  */
end_comment

begin_class
DECL|class|HttpResponseBodyPart
specifier|public
specifier|abstract
class|class
name|HttpResponseBodyPart
parameter_list|<
name|R
parameter_list|>
extends|extends
name|HttpContent
argument_list|<
name|R
argument_list|>
block|{
DECL|method|HttpResponseBodyPart
specifier|public
name|HttpResponseBodyPart
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
comment|/**      * Return the response body's part bytes received.      *      * @return the response body's part bytes received.      */
DECL|method|getBodyPartBytes
specifier|abstract
specifier|public
name|byte
index|[]
name|getBodyPartBytes
parameter_list|()
function_decl|;
block|}
end_class

end_unit

