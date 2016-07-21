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

begin_comment
comment|/**  * A filter chain allowing to continue and process the rest request.  */
end_comment

begin_interface
DECL|interface|RestFilterChain
specifier|public
interface|interface
name|RestFilterChain
block|{
comment|/**      * Continue processing the request. Should only be called if a response has not been sent      * through the channel.      */
DECL|method|continueProcessing
name|void
name|continueProcessing
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
function_decl|;
block|}
end_interface

end_unit

