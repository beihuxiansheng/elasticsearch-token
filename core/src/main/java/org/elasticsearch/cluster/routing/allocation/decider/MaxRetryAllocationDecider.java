begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation.decider
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
operator|.
name|decider
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
name|metadata
operator|.
name|IndexMetaData
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
name|RoutingNode
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
name|UnassignedInfo
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
name|RoutingAllocation
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
name|inject
operator|.
name|Inject
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
name|Setting
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
comment|/**  * An allocation decider that prevents shards from being allocated on any node if the shards allocation has been retried N times without  * success. This means if a shard has been INITIALIZING N times in a row without being moved to STARTED the shard will be ignored until  * the setting for<tt>index.allocation.max_retry</tt> is raised. The default value is<tt>5</tt>.  * Note: This allocation decider also allows allocation of repeatedly failing shards when the<tt>/_cluster/reroute?retry_failed=true</tt>  * API is manually invoked. This allows single retries without raising the limits.  *  * @see RoutingAllocation#isRetryFailed()  */
end_comment

begin_class
DECL|class|MaxRetryAllocationDecider
specifier|public
class|class
name|MaxRetryAllocationDecider
extends|extends
name|AllocationDecider
block|{
DECL|field|SETTING_ALLOCATION_MAX_RETRY
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|SETTING_ALLOCATION_MAX_RETRY
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"index.allocation.max_retries"
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|Dynamic
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"max_retry"
decl_stmt|;
comment|/**      * Initializes a new {@link MaxRetryAllocationDecider}      *      * @param settings {@link Settings} used by this {@link AllocationDecider}      */
annotation|@
name|Inject
DECL|method|MaxRetryAllocationDecider
specifier|public
name|MaxRetryAllocationDecider
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|canAllocate
specifier|public
name|Decision
name|canAllocate
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
name|UnassignedInfo
name|unassignedInfo
init|=
name|shardRouting
operator|.
name|unassignedInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|unassignedInfo
operator|!=
literal|null
operator|&&
name|unassignedInfo
operator|.
name|getNumFailedAllocations
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|IndexMetaData
name|indexMetaData
init|=
name|allocation
operator|.
name|metaData
argument_list|()
operator|.
name|getIndexSafe
argument_list|(
name|shardRouting
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxRetry
init|=
name|SETTING_ALLOCATION_MAX_RETRY
operator|.
name|get
argument_list|(
name|indexMetaData
operator|.
name|getSettings
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|allocation
operator|.
name|isRetryFailed
argument_list|()
condition|)
block|{
comment|// manual allocation - retry
comment|// if we are called via the _reroute API we ignore the failure counter and try to allocate
comment|// this improves the usability since people don't need to raise the limits to issue retries since a simple _reroute call is
comment|// enough to manually retry.
return|return
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|YES
argument_list|,
name|NAME
argument_list|,
literal|"shard has already failed allocating ["
operator|+
name|unassignedInfo
operator|.
name|getNumFailedAllocations
argument_list|()
operator|+
literal|"] times vs. ["
operator|+
name|maxRetry
operator|+
literal|"] retries allowed "
operator|+
name|unassignedInfo
operator|.
name|toString
argument_list|()
operator|+
literal|" - retrying once on manual allocation"
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|unassignedInfo
operator|.
name|getNumFailedAllocations
argument_list|()
operator|>=
name|maxRetry
condition|)
block|{
return|return
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|NO
argument_list|,
name|NAME
argument_list|,
literal|"shard has already failed allocating ["
operator|+
name|unassignedInfo
operator|.
name|getNumFailedAllocations
argument_list|()
operator|+
literal|"] times vs. ["
operator|+
name|maxRetry
operator|+
literal|"] retries allowed "
operator|+
name|unassignedInfo
operator|.
name|toString
argument_list|()
operator|+
literal|" - manually call [/_cluster/reroute?retry_failed=true] to retry"
argument_list|)
return|;
block|}
block|}
return|return
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|YES
argument_list|,
name|NAME
argument_list|,
literal|"shard has no previous failures"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|canAllocate
specifier|public
name|Decision
name|canAllocate
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|RoutingNode
name|node
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
return|return
name|canAllocate
argument_list|(
name|shardRouting
argument_list|,
name|allocation
argument_list|)
return|;
block|}
block|}
end_class

end_unit

