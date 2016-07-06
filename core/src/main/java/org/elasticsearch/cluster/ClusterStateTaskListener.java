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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_interface
DECL|interface|ClusterStateTaskListener
specifier|public
interface|interface
name|ClusterStateTaskListener
block|{
comment|/**      * A callback called when execute fails.      */
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|String
name|source
parameter_list|,
name|Exception
name|e
parameter_list|)
function_decl|;
comment|/**      * called when the task was rejected because the local node is no longer master      */
DECL|method|onNoLongerMaster
specifier|default
name|void
name|onNoLongerMaster
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|onFailure
argument_list|(
name|source
argument_list|,
operator|new
name|NotMasterException
argument_list|(
literal|"no longer master. source: ["
operator|+
name|source
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called when the result of the {@link ClusterStateTaskExecutor#execute(ClusterState, List)} have been processed      * properly by all listeners.      */
DECL|method|clusterStateProcessed
specifier|default
name|void
name|clusterStateProcessed
parameter_list|(
name|String
name|source
parameter_list|,
name|ClusterState
name|oldState
parameter_list|,
name|ClusterState
name|newState
parameter_list|)
block|{     }
block|}
end_interface

end_unit

