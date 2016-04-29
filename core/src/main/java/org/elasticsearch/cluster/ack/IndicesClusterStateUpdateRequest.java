begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.ack
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ack
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|Index
import|;
end_import

begin_comment
comment|/**  * Base cluster state update request that allows to execute update against multiple indices  */
end_comment

begin_class
DECL|class|IndicesClusterStateUpdateRequest
specifier|public
specifier|abstract
class|class
name|IndicesClusterStateUpdateRequest
parameter_list|<
name|T
extends|extends
name|IndicesClusterStateUpdateRequest
parameter_list|<
name|T
parameter_list|>
parameter_list|>
extends|extends
name|ClusterStateUpdateRequest
argument_list|<
name|T
argument_list|>
block|{
DECL|field|indices
specifier|private
name|Index
index|[]
name|indices
decl_stmt|;
comment|/**      * Returns the indices the operation needs to be executed on      */
DECL|method|indices
specifier|public
name|Index
index|[]
name|indices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
comment|/**      * Sets the indices the operation needs to be executed on      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|indices
specifier|public
name|T
name|indices
parameter_list|(
name|Index
index|[]
name|indices
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
block|}
end_class

end_unit

