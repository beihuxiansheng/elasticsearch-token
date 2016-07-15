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

begin_comment
comment|/**  * A cluster state update response with specific fields for index creation.  */
end_comment

begin_class
DECL|class|CreateIndexClusterStateUpdateResponse
specifier|public
class|class
name|CreateIndexClusterStateUpdateResponse
extends|extends
name|ClusterStateUpdateResponse
block|{
DECL|field|shardsAcked
specifier|private
specifier|final
name|boolean
name|shardsAcked
decl_stmt|;
DECL|method|CreateIndexClusterStateUpdateResponse
specifier|public
name|CreateIndexClusterStateUpdateResponse
parameter_list|(
name|boolean
name|acknowledged
parameter_list|,
name|boolean
name|shardsAcked
parameter_list|)
block|{
name|super
argument_list|(
name|acknowledged
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardsAcked
operator|=
name|shardsAcked
expr_stmt|;
block|}
comment|/**      * Returns whether the requisite number of shard copies started before the completion of the operation.      */
DECL|method|isShardsAcked
specifier|public
name|boolean
name|isShardsAcked
parameter_list|()
block|{
return|return
name|shardsAcked
return|;
block|}
block|}
end_class

end_unit

