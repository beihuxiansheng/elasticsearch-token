begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.service
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|service
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
name|ClusterState
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
name|ClusterStateTaskListener
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_interface
annotation|@
name|FunctionalInterface
DECL|interface|ClusterApplier
specifier|public
interface|interface
name|ClusterApplier
block|{
comment|/**      * Method to invoke when a new cluster state is available to be applied      *      * @param source information where the cluster state came from      * @param clusterStateSupplier the cluster state supplier which provides the latest cluster state to apply      * @param listener callback that is invoked after cluster state is applied      */
DECL|method|onNewClusterState
name|void
name|onNewClusterState
parameter_list|(
name|String
name|source
parameter_list|,
name|Supplier
argument_list|<
name|ClusterState
argument_list|>
name|clusterStateSupplier
parameter_list|,
name|ClusterStateTaskListener
name|listener
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

