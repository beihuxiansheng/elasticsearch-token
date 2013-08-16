begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
comment|/**  * ClusterInfo is an object representing a map of nodes to {@link DiskUsage}  * and a map of shard ids to shard sizes, see  *<code>InternalClusterInfoService.shardIdentifierFromRouting(String)</code>  * for the key used in the shardSizes map  */
end_comment

begin_class
DECL|class|ClusterInfo
specifier|public
class|class
name|ClusterInfo
block|{
DECL|field|usages
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|DiskUsage
argument_list|>
name|usages
decl_stmt|;
DECL|field|shardSizes
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|shardSizes
decl_stmt|;
DECL|method|ClusterInfo
specifier|public
name|ClusterInfo
parameter_list|(
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|DiskUsage
argument_list|>
name|usages
parameter_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|shardSizes
parameter_list|)
block|{
name|this
operator|.
name|usages
operator|=
name|usages
expr_stmt|;
name|this
operator|.
name|shardSizes
operator|=
name|shardSizes
expr_stmt|;
block|}
DECL|method|getNodeDiskUsages
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|DiskUsage
argument_list|>
name|getNodeDiskUsages
parameter_list|()
block|{
return|return
name|this
operator|.
name|usages
return|;
block|}
DECL|method|getShardSizes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getShardSizes
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardSizes
return|;
block|}
block|}
end_class

end_unit

