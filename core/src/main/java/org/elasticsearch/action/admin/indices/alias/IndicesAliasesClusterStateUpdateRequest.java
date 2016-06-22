begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.alias
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
name|alias
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
name|ClusterStateUpdateRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|AliasAction
import|;
end_import

begin_comment
comment|/**  * Cluster state update request that allows to add or remove aliases  */
end_comment

begin_class
DECL|class|IndicesAliasesClusterStateUpdateRequest
specifier|public
class|class
name|IndicesAliasesClusterStateUpdateRequest
extends|extends
name|ClusterStateUpdateRequest
argument_list|<
name|IndicesAliasesClusterStateUpdateRequest
argument_list|>
block|{
DECL|field|actions
name|AliasAction
index|[]
name|actions
decl_stmt|;
DECL|method|IndicesAliasesClusterStateUpdateRequest
specifier|public
name|IndicesAliasesClusterStateUpdateRequest
parameter_list|()
block|{      }
comment|/**      * Returns the alias actions to be performed      */
DECL|method|actions
specifier|public
name|AliasAction
index|[]
name|actions
parameter_list|()
block|{
return|return
name|actions
return|;
block|}
comment|/**      * Sets the alias actions to be executed      */
DECL|method|actions
specifier|public
name|IndicesAliasesClusterStateUpdateRequest
name|actions
parameter_list|(
name|AliasAction
index|[]
name|actions
parameter_list|)
block|{
name|this
operator|.
name|actions
operator|=
name|actions
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

