begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
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
name|shard
operator|.
name|ShardId
import|;
end_import

begin_comment
comment|/**  * Allows to iterate over a set of shard instances (routing) within a shard id group.  */
end_comment

begin_interface
DECL|interface|ShardIterator
specifier|public
interface|interface
name|ShardIterator
extends|extends
name|ShardsIterator
block|{
comment|/**      * The shard id this group relates to.      */
DECL|method|shardId
name|ShardId
name|shardId
parameter_list|()
function_decl|;
comment|/**      * Resets the iterator.      */
DECL|method|reset
name|void
name|reset
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

