begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|allocation
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
name|ClusterInfo
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
name|routing
operator|.
name|RoutingNodes
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
name|ShardRouting
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
name|AllocationDeciders
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * This {@link RoutingAllocation} holds a list of started shards within a  * cluster  */
end_comment

begin_class
DECL|class|StartedRerouteAllocation
specifier|public
class|class
name|StartedRerouteAllocation
extends|extends
name|RoutingAllocation
block|{
DECL|field|startedShards
specifier|private
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|ShardRouting
argument_list|>
name|startedShards
decl_stmt|;
DECL|method|StartedRerouteAllocation
specifier|public
name|StartedRerouteAllocation
parameter_list|(
name|AllocationDeciders
name|deciders
parameter_list|,
name|RoutingNodes
name|routingNodes
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|ShardRouting
argument_list|>
name|startedShards
parameter_list|,
name|ClusterInfo
name|clusterInfo
parameter_list|)
block|{
name|super
argument_list|(
name|deciders
argument_list|,
name|routingNodes
argument_list|,
name|clusterState
argument_list|,
name|clusterInfo
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|startedShards
operator|=
name|startedShards
expr_stmt|;
block|}
comment|/**      * Get started shards      * @return list of started shards      */
DECL|method|startedShards
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|ShardRouting
argument_list|>
name|startedShards
parameter_list|()
block|{
return|return
name|startedShards
return|;
block|}
block|}
end_class

end_unit

