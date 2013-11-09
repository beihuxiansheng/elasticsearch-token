begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.snapshots
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * Represent information about snapshot  */
end_comment

begin_interface
DECL|interface|Snapshot
specifier|public
interface|interface
name|Snapshot
extends|extends
name|Comparable
argument_list|<
name|Snapshot
argument_list|>
block|{
comment|/**      * Returns snapshot name      *      * @return snapshot name      */
DECL|method|name
name|String
name|name
parameter_list|()
function_decl|;
comment|/**      * Returns current snapshot state      *      * @return snapshot state      */
DECL|method|state
name|SnapshotState
name|state
parameter_list|()
function_decl|;
comment|/**      * Returns reason for complete snapshot failure      *      * @return snapshot failure reason      */
DECL|method|reason
name|String
name|reason
parameter_list|()
function_decl|;
comment|/**      * Returns version of Elasticsearch that was used to create this snapshot      *      * @return Elasticsearch version      */
DECL|method|version
name|Version
name|version
parameter_list|()
function_decl|;
comment|/**      * Returns indices that were included into this snapshot      *      * @return list of indices      */
DECL|method|indices
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|indices
parameter_list|()
function_decl|;
comment|/**      * Returns time when snapshot started      *      * @return snapshot start time      */
DECL|method|startTime
name|long
name|startTime
parameter_list|()
function_decl|;
comment|/**      * Returns time when snapshot ended      *<p/>      * Can be 0L if snapshot is still running      *      * @return snapshot end time      */
DECL|method|endTime
name|long
name|endTime
parameter_list|()
function_decl|;
comment|/**      * Returns total number of shards that were snapshotted      *      * @return number of shards      */
DECL|method|totalShard
name|int
name|totalShard
parameter_list|()
function_decl|;
comment|/**      * Returns total number of shards that were successfully snapshotted      *      * @return number of successful shards      */
DECL|method|successfulShards
name|int
name|successfulShards
parameter_list|()
function_decl|;
comment|/**      * Returns shard failures      *      * @return shard failures      */
DECL|method|shardFailures
name|ImmutableList
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|shardFailures
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

