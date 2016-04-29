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
comment|/**  * Generic interface to group ActionRequest, which work on single document level  *  * Forces this class return index/type/id getters  */
end_comment

begin_interface
DECL|interface|DocumentRequest
specifier|public
interface|interface
name|DocumentRequest
parameter_list|<
name|T
parameter_list|>
extends|extends
name|IndicesRequest
block|{
comment|/**      * Get the index that this request operates on      * @return the index      */
DECL|method|index
name|String
name|index
parameter_list|()
function_decl|;
comment|/**      * Get the type that this request operates on      * @return the type      */
DECL|method|type
name|String
name|type
parameter_list|()
function_decl|;
comment|/**      * Get the id of the document for this request      * @return the id      */
DECL|method|id
name|String
name|id
parameter_list|()
function_decl|;
comment|/**      * Get the options for this request      * @return the indices options      */
DECL|method|indicesOptions
name|IndicesOptions
name|indicesOptions
parameter_list|()
function_decl|;
comment|/**      * Set the routing for this request      * @return the Request      */
DECL|method|routing
name|T
name|routing
parameter_list|(
name|String
name|routing
parameter_list|)
function_decl|;
comment|/**      * Get the routing for this request      * @return the Routing      */
DECL|method|routing
name|String
name|routing
parameter_list|()
function_decl|;
comment|/**      * Get the parent for this request      * @return the Parent      */
DECL|method|parent
name|String
name|parent
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

