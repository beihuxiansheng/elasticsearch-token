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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|IndicesOptions
import|;
end_import

begin_comment
comment|/**  * Needs to be implemented by all {@link org.elasticsearch.action.ActionRequest} subclasses that relate to  * one or more indices. Allows to retrieve which indices the action relates to.  * In case of internal requests  originated during the distributed execution of an external request,  * they will still return the indices that the original request related to.  */
end_comment

begin_interface
DECL|interface|IndicesRequest
specifier|public
interface|interface
name|IndicesRequest
block|{
comment|/**      * Returns the array of indices that the action relates to      */
DECL|method|indices
name|String
index|[]
name|indices
parameter_list|()
function_decl|;
comment|/**      * Returns the indices options used to resolve indices. They tell for instance whether a single index is      * accepted, whether an empty array will be converted to _all, and how wildcards will be expanded if needed.      */
DECL|method|indicesOptions
name|IndicesOptions
name|indicesOptions
parameter_list|()
function_decl|;
DECL|interface|Replaceable
specifier|static
interface|interface
name|Replaceable
extends|extends
name|IndicesRequest
block|{
comment|/*          * Sets the array of indices that the action relates to          */
DECL|method|indices
name|IndicesRequest
name|indices
parameter_list|(
name|String
index|[]
name|indices
parameter_list|)
function_decl|;
block|}
block|}
end_interface

end_unit
