begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.shards
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
name|shards
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
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|ShardRoutingState
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
name|TestShardRouting
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
name|Strings
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
name|BytesStreamOutput
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
name|NamedWriteableAwareStreamInput
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
name|NamedWriteableRegistry
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
name|settings
operator|.
name|Settings
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
name|transport
operator|.
name|TransportAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|RandomQueryBuilder
import|;
end_import

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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|AliasFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|VersionUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|ClusterSearchShardsResponseTests
specifier|public
class|class
name|ClusterSearchShardsResponseTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|AliasFilter
argument_list|>
name|indicesAndFilters
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numShards
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|ClusterSearchShardsGroup
index|[]
name|clusterSearchShardsGroups
init|=
operator|new
name|ClusterSearchShardsGroup
index|[
name|numShards
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numShards
condition|;
name|i
operator|++
control|)
block|{
name|String
name|index
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|ShardId
name|shardId
init|=
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|randomAsciiOfLength
argument_list|(
literal|12
argument_list|)
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|String
name|nodeId
init|=
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|ShardRouting
name|shardRouting
init|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|shardId
argument_list|,
name|nodeId
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|)
decl_stmt|;
name|clusterSearchShardsGroups
index|[
name|i
index|]
operator|=
operator|new
name|ClusterSearchShardsGroup
argument_list|(
name|shardId
argument_list|,
operator|new
name|ShardRouting
index|[]
block|{
name|shardRouting
block|}
argument_list|)
expr_stmt|;
name|DiscoveryNode
name|node
init|=
operator|new
name|DiscoveryNode
argument_list|(
name|shardRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|,
operator|new
name|TransportAddress
argument_list|(
name|TransportAddress
operator|.
name|META_ADDRESS
argument_list|,
name|randomInt
argument_list|(
literal|0xFFFF
argument_list|)
argument_list|)
argument_list|,
name|VersionUtils
operator|.
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|AliasFilter
name|aliasFilter
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|aliasFilter
operator|=
operator|new
name|AliasFilter
argument_list|(
name|RandomQueryBuilder
operator|.
name|createQuery
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|"alias-"
operator|+
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|aliasFilter
operator|=
operator|new
name|AliasFilter
argument_list|(
literal|null
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
block|}
name|indicesAndFilters
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|aliasFilter
argument_list|)
expr_stmt|;
block|}
name|ClusterSearchShardsResponse
name|clusterSearchShardsResponse
init|=
operator|new
name|ClusterSearchShardsResponse
argument_list|(
name|clusterSearchShardsGroups
argument_list|,
name|nodes
operator|.
name|toArray
argument_list|(
operator|new
name|DiscoveryNode
index|[
name|nodes
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|indicesAndFilters
argument_list|)
decl_stmt|;
name|SearchModule
name|searchModule
init|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|false
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NamedWriteableRegistry
operator|.
name|Entry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|entries
operator|.
name|addAll
argument_list|(
name|searchModule
operator|.
name|getNamedWriteables
argument_list|()
argument_list|)
expr_stmt|;
name|NamedWriteableRegistry
name|namedWriteableRegistry
init|=
operator|new
name|NamedWriteableRegistry
argument_list|(
name|entries
argument_list|)
decl_stmt|;
name|Version
name|version
init|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|Version
operator|.
name|V_5_0_0
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|out
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|clusterSearchShardsResponse
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
operator|new
name|NamedWriteableAwareStreamInput
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
argument_list|,
name|namedWriteableRegistry
argument_list|)
init|)
block|{
name|in
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|ClusterSearchShardsResponse
name|deserialized
init|=
operator|new
name|ClusterSearchShardsResponse
argument_list|()
decl_stmt|;
name|deserialized
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|clusterSearchShardsResponse
operator|.
name|getNodes
argument_list|()
argument_list|,
name|deserialized
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|clusterSearchShardsResponse
operator|.
name|getGroups
argument_list|()
operator|.
name|length
argument_list|,
name|deserialized
operator|.
name|getGroups
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|clusterSearchShardsResponse
operator|.
name|getGroups
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ClusterSearchShardsGroup
name|clusterSearchShardsGroup
init|=
name|clusterSearchShardsResponse
operator|.
name|getGroups
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|ClusterSearchShardsGroup
name|deserializedGroup
init|=
name|deserialized
operator|.
name|getGroups
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|clusterSearchShardsGroup
operator|.
name|getShardId
argument_list|()
argument_list|,
name|deserializedGroup
operator|.
name|getShardId
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|clusterSearchShardsGroup
operator|.
name|getShards
argument_list|()
argument_list|,
name|deserializedGroup
operator|.
name|getShards
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|version
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_5_1_1_UNRELEASED
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|clusterSearchShardsResponse
operator|.
name|getIndicesAndFilters
argument_list|()
argument_list|,
name|deserialized
operator|.
name|getIndicesAndFilters
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNull
argument_list|(
name|deserialized
operator|.
name|getIndicesAndFilters
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

