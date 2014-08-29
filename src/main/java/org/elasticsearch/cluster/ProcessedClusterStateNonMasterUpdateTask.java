begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
package|;
end_package

begin_comment
comment|/**  * A combination between {@link org.elasticsearch.cluster.ProcessedClusterStateUpdateTask} and  * {@link org.elasticsearch.cluster.ClusterStateNonMasterUpdateTask} to allow easy creation of anonymous classes  */
end_comment

begin_class
DECL|class|ProcessedClusterStateNonMasterUpdateTask
specifier|abstract
specifier|public
class|class
name|ProcessedClusterStateNonMasterUpdateTask
extends|extends
name|ProcessedClusterStateUpdateTask
block|{
annotation|@
name|Override
DECL|method|runOnlyOnMaster
specifier|public
name|boolean
name|runOnlyOnMaster
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

