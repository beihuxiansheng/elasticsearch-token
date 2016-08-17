begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|routing
operator|.
name|allocation
operator|.
name|decider
operator|.
name|AllocationDecider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|ClusterSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/**  * An extension point for {@link Plugin} implementations to customer behavior of cluster management.  */
end_comment

begin_interface
DECL|interface|ClusterPlugin
specifier|public
interface|interface
name|ClusterPlugin
block|{
comment|/**      * Return deciders used to customize where shards are allocated.      *      * @param settings Settings for the node      * @param clusterSettings Settings for the cluster      * @return Custom {@link AllocationDecider} instances      */
DECL|method|createAllocationDeciders
specifier|default
name|Collection
argument_list|<
name|AllocationDecider
argument_list|>
name|createAllocationDeciders
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterSettings
name|clusterSettings
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
end_interface

end_unit

