begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
package|;
end_package

begin_comment
comment|/**  * Needs to be implemented by all {@link org.elasticsearch.action.ActionRequest} subclasses that relate to  * one or more indices and one or more aliases. Meant to be used for aliases management requests (e.g. add/remove alias,  * get aliases) that hold aliases and indices in separate fields.  * Allows to retrieve which indices and aliases the action relates to.  */
end_comment

begin_interface
DECL|interface|AliasesRequest
specifier|public
interface|interface
name|AliasesRequest
extends|extends
name|IndicesRequest
operator|.
name|Replaceable
block|{
comment|/**      * Returns the array of aliases that the action relates to      */
DECL|method|aliases
name|String
index|[]
name|aliases
parameter_list|()
function_decl|;
comment|/**      * Sets the array of aliases that the action relates to      */
DECL|method|aliases
name|AliasesRequest
name|aliases
parameter_list|(
name|String
modifier|...
name|aliases
parameter_list|)
function_decl|;
comment|/**      * Returns true if wildcards expressions among aliases should be resolved, false otherwise      */
DECL|method|expandAliasesWildcards
name|boolean
name|expandAliasesWildcards
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

