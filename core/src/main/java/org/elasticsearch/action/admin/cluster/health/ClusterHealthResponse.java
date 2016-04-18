begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.health
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|health
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionResponse
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
name|health
operator|.
name|ClusterHealthStatus
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
name|health
operator|.
name|ClusterIndexHealth
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
name|health
operator|.
name|ClusterStateHealth
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|unit
operator|.
name|TimeValue
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
name|xcontent
operator|.
name|StatusToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ClusterHealthResponse
specifier|public
class|class
name|ClusterHealthResponse
extends|extends
name|ActionResponse
implements|implements
name|StatusToXContent
block|{
DECL|field|clusterName
specifier|private
name|String
name|clusterName
decl_stmt|;
DECL|field|numberOfPendingTasks
specifier|private
name|int
name|numberOfPendingTasks
init|=
literal|0
decl_stmt|;
DECL|field|numberOfInFlightFetch
specifier|private
name|int
name|numberOfInFlightFetch
init|=
literal|0
decl_stmt|;
DECL|field|delayedUnassignedShards
specifier|private
name|int
name|delayedUnassignedShards
init|=
literal|0
decl_stmt|;
DECL|field|taskMaxWaitingTime
specifier|private
name|TimeValue
name|taskMaxWaitingTime
init|=
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|timedOut
specifier|private
name|boolean
name|timedOut
init|=
literal|false
decl_stmt|;
DECL|field|clusterStateHealth
specifier|private
name|ClusterStateHealth
name|clusterStateHealth
decl_stmt|;
DECL|field|clusterHealthStatus
specifier|private
name|ClusterHealthStatus
name|clusterHealthStatus
decl_stmt|;
DECL|method|ClusterHealthResponse
name|ClusterHealthResponse
parameter_list|()
block|{     }
comment|/** needed for plugins BWC */
DECL|method|ClusterHealthResponse
specifier|public
name|ClusterHealthResponse
parameter_list|(
name|String
name|clusterName
parameter_list|,
name|String
index|[]
name|concreteIndices
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|this
argument_list|(
name|clusterName
argument_list|,
name|concreteIndices
argument_list|,
name|clusterState
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|ClusterHealthResponse
specifier|public
name|ClusterHealthResponse
parameter_list|(
name|String
name|clusterName
parameter_list|,
name|String
index|[]
name|concreteIndices
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|,
name|int
name|numberOfPendingTasks
parameter_list|,
name|int
name|numberOfInFlightFetch
parameter_list|,
name|int
name|delayedUnassignedShards
parameter_list|,
name|TimeValue
name|taskMaxWaitingTime
parameter_list|)
block|{
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|numberOfPendingTasks
operator|=
name|numberOfPendingTasks
expr_stmt|;
name|this
operator|.
name|numberOfInFlightFetch
operator|=
name|numberOfInFlightFetch
expr_stmt|;
name|this
operator|.
name|delayedUnassignedShards
operator|=
name|delayedUnassignedShards
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|numberOfPendingTasks
operator|=
name|numberOfPendingTasks
expr_stmt|;
name|this
operator|.
name|numberOfInFlightFetch
operator|=
name|numberOfInFlightFetch
expr_stmt|;
name|this
operator|.
name|taskMaxWaitingTime
operator|=
name|taskMaxWaitingTime
expr_stmt|;
name|this
operator|.
name|clusterStateHealth
operator|=
operator|new
name|ClusterStateHealth
argument_list|(
name|clusterState
argument_list|,
name|concreteIndices
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterHealthStatus
operator|=
name|clusterStateHealth
operator|.
name|getStatus
argument_list|()
expr_stmt|;
block|}
DECL|method|getClusterName
specifier|public
name|String
name|getClusterName
parameter_list|()
block|{
return|return
name|clusterName
return|;
block|}
comment|//package private for testing
DECL|method|getClusterStateHealth
name|ClusterStateHealth
name|getClusterStateHealth
parameter_list|()
block|{
return|return
name|clusterStateHealth
return|;
block|}
comment|/**      * The validation failures on the cluster level (without index validation failures).      */
DECL|method|getValidationFailures
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getValidationFailures
parameter_list|()
block|{
return|return
name|clusterStateHealth
operator|.
name|getValidationFailures
argument_list|()
return|;
block|}
DECL|method|getActiveShards
specifier|public
name|int
name|getActiveShards
parameter_list|()
block|{
return|return
name|clusterStateHealth
operator|.
name|getActiveShards
argument_list|()
return|;
block|}
DECL|method|getRelocatingShards
specifier|public
name|int
name|getRelocatingShards
parameter_list|()
block|{
return|return
name|clusterStateHealth
operator|.
name|getRelocatingShards
argument_list|()
return|;
block|}
DECL|method|getActivePrimaryShards
specifier|public
name|int
name|getActivePrimaryShards
parameter_list|()
block|{
return|return
name|clusterStateHealth
operator|.
name|getActivePrimaryShards
argument_list|()
return|;
block|}
DECL|method|getInitializingShards
specifier|public
name|int
name|getInitializingShards
parameter_list|()
block|{
return|return
name|clusterStateHealth
operator|.
name|getInitializingShards
argument_list|()
return|;
block|}
DECL|method|getUnassignedShards
specifier|public
name|int
name|getUnassignedShards
parameter_list|()
block|{
return|return
name|clusterStateHealth
operator|.
name|getUnassignedShards
argument_list|()
return|;
block|}
DECL|method|getNumberOfNodes
specifier|public
name|int
name|getNumberOfNodes
parameter_list|()
block|{
return|return
name|clusterStateHealth
operator|.
name|getNumberOfNodes
argument_list|()
return|;
block|}
DECL|method|getNumberOfDataNodes
specifier|public
name|int
name|getNumberOfDataNodes
parameter_list|()
block|{
return|return
name|clusterStateHealth
operator|.
name|getNumberOfDataNodes
argument_list|()
return|;
block|}
DECL|method|getNumberOfPendingTasks
specifier|public
name|int
name|getNumberOfPendingTasks
parameter_list|()
block|{
return|return
name|this
operator|.
name|numberOfPendingTasks
return|;
block|}
DECL|method|getNumberOfInFlightFetch
specifier|public
name|int
name|getNumberOfInFlightFetch
parameter_list|()
block|{
return|return
name|this
operator|.
name|numberOfInFlightFetch
return|;
block|}
comment|/**      * The number of unassigned shards that are currently being delayed (for example,      * due to node leaving the cluster and waiting for a timeout for the node to come      * back in order to allocate the shards back to it).      */
DECL|method|getDelayedUnassignedShards
specifier|public
name|int
name|getDelayedUnassignedShards
parameter_list|()
block|{
return|return
name|this
operator|.
name|delayedUnassignedShards
return|;
block|}
comment|/**      *<tt>true</tt> if the waitForXXX has timeout out and did not match.      */
DECL|method|isTimedOut
specifier|public
name|boolean
name|isTimedOut
parameter_list|()
block|{
return|return
name|this
operator|.
name|timedOut
return|;
block|}
DECL|method|setTimedOut
specifier|public
name|void
name|setTimedOut
parameter_list|(
name|boolean
name|timedOut
parameter_list|)
block|{
name|this
operator|.
name|timedOut
operator|=
name|timedOut
expr_stmt|;
block|}
DECL|method|getStatus
specifier|public
name|ClusterHealthStatus
name|getStatus
parameter_list|()
block|{
return|return
name|clusterHealthStatus
return|;
block|}
comment|/**      * Allows to explicitly override the derived cluster health status.      *      * @param status The override status. Must not be null.      */
DECL|method|setStatus
specifier|public
name|void
name|setStatus
parameter_list|(
name|ClusterHealthStatus
name|status
parameter_list|)
block|{
if|if
condition|(
name|status
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"'status' must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|clusterHealthStatus
operator|=
name|status
expr_stmt|;
block|}
DECL|method|getIndices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ClusterIndexHealth
argument_list|>
name|getIndices
parameter_list|()
block|{
return|return
name|clusterStateHealth
operator|.
name|getIndices
argument_list|()
return|;
block|}
comment|/**      *      * @return The maximum wait time of all tasks in the queue      */
DECL|method|getTaskMaxWaitingTime
specifier|public
name|TimeValue
name|getTaskMaxWaitingTime
parameter_list|()
block|{
return|return
name|taskMaxWaitingTime
return|;
block|}
comment|/**      * The percentage of active shards, should be 100% in a green system      */
DECL|method|getActiveShardsPercent
specifier|public
name|double
name|getActiveShardsPercent
parameter_list|()
block|{
return|return
name|clusterStateHealth
operator|.
name|getActiveShardsPercent
argument_list|()
return|;
block|}
DECL|method|readResponseFrom
specifier|public
specifier|static
name|ClusterHealthResponse
name|readResponseFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ClusterHealthResponse
name|response
init|=
operator|new
name|ClusterHealthResponse
argument_list|()
decl_stmt|;
name|response
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|clusterName
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|clusterHealthStatus
operator|=
name|ClusterHealthStatus
operator|.
name|fromValue
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|clusterStateHealth
operator|=
name|ClusterStateHealth
operator|.
name|readClusterHealth
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|numberOfPendingTasks
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|timedOut
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|numberOfInFlightFetch
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|delayedUnassignedShards
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|taskMaxWaitingTime
operator|=
name|TimeValue
operator|.
name|readTimeValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|clusterName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|clusterHealthStatus
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|clusterStateHealth
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|numberOfPendingTasks
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|timedOut
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|numberOfInFlightFetch
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|delayedUnassignedShards
argument_list|)
expr_stmt|;
name|taskMaxWaitingTime
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|prettyPrint
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|toXContent
argument_list|(
name|builder
argument_list|,
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|string
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|"{ \"error\" : \""
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\"}"
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
return|return
name|isTimedOut
argument_list|()
condition|?
name|RestStatus
operator|.
name|REQUEST_TIMEOUT
else|:
name|RestStatus
operator|.
name|OK
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|CLUSTER_NAME
specifier|static
specifier|final
name|String
name|CLUSTER_NAME
init|=
literal|"cluster_name"
decl_stmt|;
DECL|field|STATUS
specifier|static
specifier|final
name|String
name|STATUS
init|=
literal|"status"
decl_stmt|;
DECL|field|TIMED_OUT
specifier|static
specifier|final
name|String
name|TIMED_OUT
init|=
literal|"timed_out"
decl_stmt|;
DECL|field|NUMBER_OF_NODES
specifier|static
specifier|final
name|String
name|NUMBER_OF_NODES
init|=
literal|"number_of_nodes"
decl_stmt|;
DECL|field|NUMBER_OF_DATA_NODES
specifier|static
specifier|final
name|String
name|NUMBER_OF_DATA_NODES
init|=
literal|"number_of_data_nodes"
decl_stmt|;
DECL|field|NUMBER_OF_PENDING_TASKS
specifier|static
specifier|final
name|String
name|NUMBER_OF_PENDING_TASKS
init|=
literal|"number_of_pending_tasks"
decl_stmt|;
DECL|field|NUMBER_OF_IN_FLIGHT_FETCH
specifier|static
specifier|final
name|String
name|NUMBER_OF_IN_FLIGHT_FETCH
init|=
literal|"number_of_in_flight_fetch"
decl_stmt|;
DECL|field|DELAYED_UNASSIGNED_SHARDS
specifier|static
specifier|final
name|String
name|DELAYED_UNASSIGNED_SHARDS
init|=
literal|"delayed_unassigned_shards"
decl_stmt|;
DECL|field|TASK_MAX_WAIT_TIME_IN_QUEUE
specifier|static
specifier|final
name|String
name|TASK_MAX_WAIT_TIME_IN_QUEUE
init|=
literal|"task_max_waiting_in_queue"
decl_stmt|;
DECL|field|TASK_MAX_WAIT_TIME_IN_QUEUE_IN_MILLIS
specifier|static
specifier|final
name|String
name|TASK_MAX_WAIT_TIME_IN_QUEUE_IN_MILLIS
init|=
literal|"task_max_waiting_in_queue_millis"
decl_stmt|;
DECL|field|ACTIVE_SHARDS_PERCENT_AS_NUMBER
specifier|static
specifier|final
name|String
name|ACTIVE_SHARDS_PERCENT_AS_NUMBER
init|=
literal|"active_shards_percent_as_number"
decl_stmt|;
DECL|field|ACTIVE_SHARDS_PERCENT
specifier|static
specifier|final
name|String
name|ACTIVE_SHARDS_PERCENT
init|=
literal|"active_shards_percent"
decl_stmt|;
DECL|field|ACTIVE_PRIMARY_SHARDS
specifier|static
specifier|final
name|String
name|ACTIVE_PRIMARY_SHARDS
init|=
literal|"active_primary_shards"
decl_stmt|;
DECL|field|ACTIVE_SHARDS
specifier|static
specifier|final
name|String
name|ACTIVE_SHARDS
init|=
literal|"active_shards"
decl_stmt|;
DECL|field|RELOCATING_SHARDS
specifier|static
specifier|final
name|String
name|RELOCATING_SHARDS
init|=
literal|"relocating_shards"
decl_stmt|;
DECL|field|INITIALIZING_SHARDS
specifier|static
specifier|final
name|String
name|INITIALIZING_SHARDS
init|=
literal|"initializing_shards"
decl_stmt|;
DECL|field|UNASSIGNED_SHARDS
specifier|static
specifier|final
name|String
name|UNASSIGNED_SHARDS
init|=
literal|"unassigned_shards"
decl_stmt|;
DECL|field|VALIDATION_FAILURES
specifier|static
specifier|final
name|String
name|VALIDATION_FAILURES
init|=
literal|"validation_failures"
decl_stmt|;
DECL|field|INDICES
specifier|static
specifier|final
name|String
name|INDICES
init|=
literal|"indices"
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|CLUSTER_NAME
argument_list|,
name|getClusterName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STATUS
argument_list|,
name|getStatus
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TIMED_OUT
argument_list|,
name|isTimedOut
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NUMBER_OF_NODES
argument_list|,
name|getNumberOfNodes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NUMBER_OF_DATA_NODES
argument_list|,
name|getNumberOfDataNodes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ACTIVE_PRIMARY_SHARDS
argument_list|,
name|getActivePrimaryShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ACTIVE_SHARDS
argument_list|,
name|getActiveShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|RELOCATING_SHARDS
argument_list|,
name|getRelocatingShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|INITIALIZING_SHARDS
argument_list|,
name|getInitializingShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|UNASSIGNED_SHARDS
argument_list|,
name|getUnassignedShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DELAYED_UNASSIGNED_SHARDS
argument_list|,
name|getDelayedUnassignedShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NUMBER_OF_PENDING_TASKS
argument_list|,
name|getNumberOfPendingTasks
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NUMBER_OF_IN_FLIGHT_FETCH
argument_list|,
name|getNumberOfInFlightFetch
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|timeValueField
argument_list|(
name|Fields
operator|.
name|TASK_MAX_WAIT_TIME_IN_QUEUE_IN_MILLIS
argument_list|,
name|Fields
operator|.
name|TASK_MAX_WAIT_TIME_IN_QUEUE
argument_list|,
name|getTaskMaxWaitingTime
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|percentageField
argument_list|(
name|Fields
operator|.
name|ACTIVE_SHARDS_PERCENT_AS_NUMBER
argument_list|,
name|Fields
operator|.
name|ACTIVE_SHARDS_PERCENT
argument_list|,
name|getActiveShardsPercent
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|level
init|=
name|params
operator|.
name|param
argument_list|(
literal|"level"
argument_list|,
literal|"cluster"
argument_list|)
decl_stmt|;
name|boolean
name|outputIndices
init|=
literal|"indices"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
operator|||
literal|"shards"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|getValidationFailures
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|VALIDATION_FAILURES
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|validationFailure
range|:
name|getValidationFailures
argument_list|()
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|validationFailure
argument_list|)
expr_stmt|;
block|}
comment|// if we don't print index level information, still print the index validation failures
comment|// so we know why the status is red
if|if
condition|(
operator|!
name|outputIndices
condition|)
block|{
for|for
control|(
name|ClusterIndexHealth
name|indexHealth
range|:
name|clusterStateHealth
operator|.
name|getIndices
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|indexHealth
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|indexHealth
operator|.
name|getValidationFailures
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|VALIDATION_FAILURES
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|validationFailure
range|:
name|indexHealth
operator|.
name|getValidationFailures
argument_list|()
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|validationFailure
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|outputIndices
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|INDICES
argument_list|)
expr_stmt|;
for|for
control|(
name|ClusterIndexHealth
name|indexHealth
range|:
name|clusterStateHealth
operator|.
name|getIndices
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|indexHealth
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|indexHealth
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

