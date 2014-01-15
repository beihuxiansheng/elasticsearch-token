begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.mapping.delete
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|delete
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ack
operator|.
name|IndicesClusterStateUpdateRequest
import|;
end_import

begin_comment
comment|/**  * Cluster state update request that allows to delete a mapping  */
end_comment

begin_class
DECL|class|DeleteMappingClusterStateUpdateRequest
specifier|public
class|class
name|DeleteMappingClusterStateUpdateRequest
extends|extends
name|IndicesClusterStateUpdateRequest
argument_list|<
name|DeleteMappingClusterStateUpdateRequest
argument_list|>
block|{
DECL|field|types
specifier|private
name|String
index|[]
name|types
decl_stmt|;
DECL|method|DeleteMappingClusterStateUpdateRequest
name|DeleteMappingClusterStateUpdateRequest
parameter_list|()
block|{      }
comment|/**      * Returns the type to be removed      */
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|types
return|;
block|}
comment|/**      * Sets the type to be removed      */
DECL|method|types
specifier|public
name|DeleteMappingClusterStateUpdateRequest
name|types
parameter_list|(
name|String
index|[]
name|types
parameter_list|)
block|{
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

