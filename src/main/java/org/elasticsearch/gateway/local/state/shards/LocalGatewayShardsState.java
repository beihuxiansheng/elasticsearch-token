begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.local.state.shards
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
operator|.
name|state
operator|.
name|shards
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
name|Maps
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
name|ClusterChangedEvent
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
name|ClusterStateListener
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
name|*
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
name|Nullable
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
name|component
operator|.
name|AbstractComponent
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
name|io
operator|.
name|Streams
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|NodeEnvironment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
operator|.
name|state
operator|.
name|meta
operator|.
name|CorruptStateException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
operator|.
name|state
operator|.
name|meta
operator|.
name|MetaDataStateFormat
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
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|LocalGatewayShardsState
specifier|public
class|class
name|LocalGatewayShardsState
extends|extends
name|AbstractComponent
implements|implements
name|ClusterStateListener
block|{
DECL|field|SHARD_STATE_FILE_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|SHARD_STATE_FILE_PREFIX
init|=
literal|"state-"
decl_stmt|;
DECL|field|SHARD_STATE_FILE_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|SHARD_STATE_FILE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|SHARD_STATE_FILE_PREFIX
operator|+
literal|"(\\d+)("
operator|+
name|MetaDataStateFormat
operator|.
name|STATE_FILE_EXTENSION
operator|+
literal|")?"
argument_list|)
decl_stmt|;
DECL|field|PRIMARY_KEY
specifier|private
specifier|static
specifier|final
name|String
name|PRIMARY_KEY
init|=
literal|"primary"
decl_stmt|;
DECL|field|VERSION_KEY
specifier|private
specifier|static
specifier|final
name|String
name|VERSION_KEY
init|=
literal|"version"
decl_stmt|;
DECL|field|nodeEnv
specifier|private
specifier|final
name|NodeEnvironment
name|nodeEnv
decl_stmt|;
DECL|field|currentState
specifier|private
specifier|volatile
name|Map
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|currentState
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|LocalGatewayShardsState
specifier|public
name|LocalGatewayShardsState
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeEnvironment
name|nodeEnv
parameter_list|,
name|TransportNodesListGatewayStartedShards
name|listGatewayStartedShards
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeEnv
operator|=
name|nodeEnv
expr_stmt|;
name|listGatewayStartedShards
operator|.
name|initGateway
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|DiscoveryNode
operator|.
name|dataNode
argument_list|(
name|settings
argument_list|)
condition|)
block|{
try|try
block|{
name|pre019Upgrade
argument_list|()
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|currentState
operator|=
name|loadShardsStateInfo
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"took {} to load started shards state"
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"failed to read local state (started shards), exiting..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
DECL|method|loadShardInfo
specifier|public
name|ShardStateInfo
name|loadShardInfo
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|loadShardStateInfo
argument_list|(
name|shardId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clusterChanged
specifier|public
name|void
name|clusterChanged
parameter_list|(
name|ClusterChangedEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|blocks
argument_list|()
operator|.
name|disableStatePersistence
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|event
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|.
name|dataNode
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|event
operator|.
name|routingTableChanged
argument_list|()
condition|)
block|{
return|return;
block|}
name|Map
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|newState
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|newState
operator|.
name|putAll
argument_list|(
name|this
operator|.
name|currentState
argument_list|)
expr_stmt|;
comment|// remove from the current state all the shards that are completely started somewhere, we won't need them anymore
comment|// and if they are still here, we will add them in the next phase
comment|// Also note, this works well when closing an index, since a closed index will have no routing shards entries
comment|// so they won't get removed (we want to keep the fact that those shards are allocated on this node if needed)
for|for
control|(
name|IndexRoutingTable
name|indexRoutingTable
range|:
name|event
operator|.
name|state
argument_list|()
operator|.
name|routingTable
argument_list|()
control|)
block|{
for|for
control|(
name|IndexShardRoutingTable
name|indexShardRoutingTable
range|:
name|indexRoutingTable
control|)
block|{
if|if
condition|(
name|indexShardRoutingTable
operator|.
name|countWithState
argument_list|(
name|ShardRoutingState
operator|.
name|STARTED
argument_list|)
operator|==
name|indexShardRoutingTable
operator|.
name|size
argument_list|()
condition|)
block|{
name|newState
operator|.
name|remove
argument_list|(
name|indexShardRoutingTable
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// remove deleted indices from the started shards
for|for
control|(
name|ShardId
name|shardId
range|:
name|currentState
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|event
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|shardId
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|newState
operator|.
name|remove
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now, add all the ones that are active and on this node
name|RoutingNode
name|routingNode
init|=
name|event
operator|.
name|state
argument_list|()
operator|.
name|readOnlyRoutingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|routingNode
operator|!=
literal|null
condition|)
block|{
comment|// our node is not in play yet...
for|for
control|(
name|MutableShardRouting
name|shardRouting
range|:
name|routingNode
control|)
block|{
if|if
condition|(
name|shardRouting
operator|.
name|active
argument_list|()
condition|)
block|{
name|newState
operator|.
name|put
argument_list|(
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|,
operator|new
name|ShardStateInfo
argument_list|(
name|shardRouting
operator|.
name|version
argument_list|()
argument_list|,
name|shardRouting
operator|.
name|primary
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// go over the write started shards if needed
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
argument_list|>
name|it
init|=
name|newState
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|ShardId
name|shardId
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|ShardStateInfo
name|shardStateInfo
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|writeReason
init|=
literal|null
decl_stmt|;
name|ShardStateInfo
name|currentShardStateInfo
init|=
name|currentState
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentShardStateInfo
operator|==
literal|null
condition|)
block|{
name|writeReason
operator|=
literal|"freshly started, version ["
operator|+
name|shardStateInfo
operator|.
name|version
operator|+
literal|"]"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentShardStateInfo
operator|.
name|version
operator|!=
name|shardStateInfo
operator|.
name|version
condition|)
block|{
name|writeReason
operator|=
literal|"version changed from ["
operator|+
name|currentShardStateInfo
operator|.
name|version
operator|+
literal|"] to ["
operator|+
name|shardStateInfo
operator|.
name|version
operator|+
literal|"]"
expr_stmt|;
block|}
comment|// we update the write reason if we really need to write a new one...
if|if
condition|(
name|writeReason
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|writeShardState
argument_list|(
name|writeReason
argument_list|,
name|shardId
argument_list|,
name|shardStateInfo
argument_list|,
name|currentShardStateInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// we failed to write the shard state, remove it from our builder, we will try and write
comment|// it next time...
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|// REMOVED: don't delete shard state, rely on IndicesStore to delete the shard location
comment|//          only once all shards are allocated on another node
comment|// now, go over the current ones and delete ones that are not in the new one
comment|//        for (Map.Entry<ShardId, ShardStateInfo> entry : currentState.entrySet()) {
comment|//            ShardId shardId = entry.getKey();
comment|//            if (!newState.containsKey(shardId)) {
comment|//                if (!metaState.isDangling(shardId.index().name())) {
comment|//                    deleteShardState(shardId);
comment|//                }
comment|//            }
comment|//        }
name|this
operator|.
name|currentState
operator|=
name|newState
expr_stmt|;
block|}
DECL|method|loadShardsStateInfo
specifier|private
name|Map
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|loadShardsStateInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|ShardId
argument_list|>
name|shardIds
init|=
name|nodeEnv
operator|.
name|findAllShardIds
argument_list|()
decl_stmt|;
name|long
name|highestVersion
init|=
operator|-
literal|1
decl_stmt|;
name|Map
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|shardsState
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardId
name|shardId
range|:
name|shardIds
control|)
block|{
name|ShardStateInfo
name|shardStateInfo
init|=
name|loadShardStateInfo
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardStateInfo
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|shardsState
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|shardStateInfo
argument_list|)
expr_stmt|;
comment|// update the global version
if|if
condition|(
name|shardStateInfo
operator|.
name|version
operator|>
name|highestVersion
condition|)
block|{
name|highestVersion
operator|=
name|shardStateInfo
operator|.
name|version
expr_stmt|;
block|}
block|}
return|return
name|shardsState
return|;
block|}
DECL|method|loadShardStateInfo
specifier|private
name|ShardStateInfo
name|loadShardStateInfo
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{
return|return
name|MetaDataStateFormat
operator|.
name|loadLatestState
argument_list|(
name|logger
argument_list|,
name|newShardStateInfoFormat
argument_list|(
literal|false
argument_list|)
argument_list|,
name|SHARD_STATE_FILE_PATTERN
argument_list|,
name|shardId
operator|.
name|toString
argument_list|()
argument_list|,
name|nodeEnv
operator|.
name|shardLocations
argument_list|(
name|shardId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|writeShardState
specifier|private
name|void
name|writeShardState
parameter_list|(
name|String
name|reason
parameter_list|,
name|ShardId
name|shardId
parameter_list|,
name|ShardStateInfo
name|shardStateInfo
parameter_list|,
annotation|@
name|Nullable
name|ShardStateInfo
name|previousStateInfo
parameter_list|)
throws|throws
name|Exception
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"{} writing shard state, reason [{}]"
argument_list|,
name|shardId
argument_list|,
name|reason
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|deleteOldFiles
init|=
name|previousStateInfo
operator|!=
literal|null
operator|&&
name|previousStateInfo
operator|.
name|version
operator|!=
name|shardStateInfo
operator|.
name|version
decl_stmt|;
name|newShardStateInfoFormat
argument_list|(
name|deleteOldFiles
argument_list|)
operator|.
name|write
argument_list|(
name|shardStateInfo
argument_list|,
name|SHARD_STATE_FILE_PREFIX
argument_list|,
name|shardStateInfo
operator|.
name|version
argument_list|,
name|nodeEnv
operator|.
name|shardLocations
argument_list|(
name|shardId
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|newShardStateInfoFormat
specifier|private
name|MetaDataStateFormat
argument_list|<
name|ShardStateInfo
argument_list|>
name|newShardStateInfoFormat
parameter_list|(
name|boolean
name|deleteOldFiles
parameter_list|)
block|{
return|return
operator|new
name|MetaDataStateFormat
argument_list|<
name|ShardStateInfo
argument_list|>
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|,
name|deleteOldFiles
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|XContentBuilder
name|newXContentBuilder
parameter_list|(
name|XContentType
name|type
parameter_list|,
name|OutputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|xContentBuilder
init|=
name|super
operator|.
name|newXContentBuilder
argument_list|(
name|type
argument_list|,
name|stream
argument_list|)
decl_stmt|;
name|xContentBuilder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
return|return
name|xContentBuilder
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|ShardStateInfo
name|shardStateInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
name|VERSION_KEY
argument_list|,
name|shardStateInfo
operator|.
name|version
argument_list|)
expr_stmt|;
if|if
condition|(
name|shardStateInfo
operator|.
name|primary
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|PRIMARY_KEY
argument_list|,
name|shardStateInfo
operator|.
name|primary
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|ShardStateInfo
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|version
init|=
operator|-
literal|1
decl_stmt|;
name|Boolean
name|primary
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERSION_KEY
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|version
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|PRIMARY_KEY
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|primary
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptStateException
argument_list|(
literal|"unexpected field in shard state ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptStateException
argument_list|(
literal|"unexpected token in shard state ["
operator|+
name|token
operator|.
name|name
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|primary
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|CorruptStateException
argument_list|(
literal|"missing value for [primary] in shard state"
argument_list|)
throw|;
block|}
if|if
condition|(
name|version
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|CorruptStateException
argument_list|(
literal|"missing value for [version] in shard state"
argument_list|)
throw|;
block|}
return|return
operator|new
name|ShardStateInfo
argument_list|(
name|version
argument_list|,
name|primary
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|pre019Upgrade
specifier|private
name|void
name|pre019Upgrade
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|index
init|=
operator|-
literal|1
decl_stmt|;
name|File
name|latest
init|=
literal|null
decl_stmt|;
for|for
control|(
name|File
name|dataLocation
range|:
name|nodeEnv
operator|.
name|nodeDataLocations
argument_list|()
control|)
block|{
name|File
name|stateLocation
init|=
operator|new
name|File
argument_list|(
name|dataLocation
argument_list|,
literal|"_state"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|stateLocation
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|File
index|[]
name|stateFiles
init|=
name|stateLocation
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|stateFiles
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|File
name|stateFile
range|:
name|stateFiles
control|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[find_latest_state]: processing ["
operator|+
name|stateFile
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|String
name|name
init|=
name|stateFile
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"shards-"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|long
name|fileIndex
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileIndex
operator|>=
name|index
condition|)
block|{
comment|// try and read the meta data
try|try
block|{
name|byte
index|[]
name|data
init|=
name|Streams
operator|.
name|copyToByteArray
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|stateFile
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[upgrade]: not data for ["
operator|+
name|name
operator|+
literal|"], ignoring..."
argument_list|)
expr_stmt|;
block|}
name|pre09ReadState
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|index
operator|=
name|fileIndex
expr_stmt|;
name|latest
operator|=
name|stateFile
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[upgrade]: failed to read state from ["
operator|+
name|name
operator|+
literal|"], ignoring..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|latest
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"found old shards state, loading started shards from [{}] and converting to new shards state locations..."
argument_list|,
name|latest
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|shardsState
init|=
name|pre09ReadState
argument_list|(
name|Streams
operator|.
name|copyToByteArray
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|latest
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|entry
range|:
name|shardsState
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|writeShardState
argument_list|(
literal|"upgrade"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// rename shards state to backup state
name|File
name|backupFile
init|=
operator|new
name|File
argument_list|(
name|latest
operator|.
name|getParentFile
argument_list|()
argument_list|,
literal|"backup-"
operator|+
name|latest
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|latest
operator|.
name|renameTo
argument_list|(
name|backupFile
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"failed to rename old state to backup state ["
operator|+
name|latest
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|// delete all other shards state files
for|for
control|(
name|File
name|dataLocation
range|:
name|nodeEnv
operator|.
name|nodeDataLocations
argument_list|()
control|)
block|{
name|File
name|stateLocation
init|=
operator|new
name|File
argument_list|(
name|dataLocation
argument_list|,
literal|"_state"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|stateLocation
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|File
index|[]
name|stateFiles
init|=
name|stateLocation
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|stateFiles
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|File
name|stateFile
range|:
name|stateFiles
control|)
block|{
name|String
name|name
init|=
name|stateFile
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"shards-"
argument_list|)
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|stateFile
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Failed to delete state file {}"
argument_list|,
name|ex
argument_list|,
name|stateFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"conversion to new shards state location and format done, backup create at [{}]"
argument_list|,
name|backupFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|pre09ReadState
specifier|private
name|Map
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|pre09ReadState
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|shardsState
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
init|)
block|{
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
comment|// no data...
return|return
name|shardsState
return|;
block|}
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"shards"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|String
name|shardIndex
init|=
literal|null
decl_stmt|;
name|int
name|shardId
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|version
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"index"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|shardIndex
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"id"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|shardId
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|VERSION_KEY
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|version
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|shardsState
operator|.
name|put
argument_list|(
operator|new
name|ShardId
argument_list|(
name|shardIndex
argument_list|,
name|shardId
argument_list|)
argument_list|,
operator|new
name|ShardStateInfo
argument_list|(
name|version
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|shardsState
return|;
block|}
block|}
block|}
end_class

end_unit

