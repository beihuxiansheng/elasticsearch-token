begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|IntObjectCursor
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
name|IndexRoutingTable
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
name|IndexShardRoutingTable
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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

begin_comment
comment|/**  * A class whose instances represent a value for counting the number  * of active shard copies for a given shard in an index.  */
end_comment

begin_class
DECL|class|ActiveShardCount
specifier|public
specifier|final
class|class
name|ActiveShardCount
implements|implements
name|Writeable
block|{
DECL|field|ACTIVE_SHARD_COUNT_DEFAULT
specifier|private
specifier|static
specifier|final
name|int
name|ACTIVE_SHARD_COUNT_DEFAULT
init|=
operator|-
literal|2
decl_stmt|;
DECL|field|ALL_ACTIVE_SHARDS
specifier|private
specifier|static
specifier|final
name|int
name|ALL_ACTIVE_SHARDS
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|ActiveShardCount
name|DEFAULT
init|=
operator|new
name|ActiveShardCount
argument_list|(
name|ACTIVE_SHARD_COUNT_DEFAULT
argument_list|)
decl_stmt|;
DECL|field|ALL
specifier|public
specifier|static
specifier|final
name|ActiveShardCount
name|ALL
init|=
operator|new
name|ActiveShardCount
argument_list|(
name|ALL_ACTIVE_SHARDS
argument_list|)
decl_stmt|;
DECL|field|NONE
specifier|public
specifier|static
specifier|final
name|ActiveShardCount
name|NONE
init|=
operator|new
name|ActiveShardCount
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|ONE
specifier|public
specifier|static
specifier|final
name|ActiveShardCount
name|ONE
init|=
operator|new
name|ActiveShardCount
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|int
name|value
decl_stmt|;
DECL|method|ActiveShardCount
specifier|private
name|ActiveShardCount
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Get an ActiveShardCount instance for the given value.  The value is first validated to ensure      * it is a valid shard count and throws an IllegalArgumentException if validation fails.  Valid      * values are any non-negative number.  Directly use {@link ActiveShardCount#DEFAULT} for the      * default value (which is one shard copy) or {@link ActiveShardCount#ALL} to specify all the shards.      */
DECL|method|from
specifier|public
specifier|static
name|ActiveShardCount
name|from
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"shard count cannot be a negative value"
argument_list|)
throw|;
block|}
return|return
name|get
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|get
specifier|private
specifier|static
name|ActiveShardCount
name|get
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
switch|switch
condition|(
name|validateValue
argument_list|(
name|value
argument_list|)
condition|)
block|{
case|case
name|ACTIVE_SHARD_COUNT_DEFAULT
case|:
return|return
name|DEFAULT
return|;
case|case
name|ALL_ACTIVE_SHARDS
case|:
return|return
name|ALL
return|;
case|case
literal|1
case|:
return|return
name|ONE
return|;
case|case
literal|0
case|:
return|return
name|NONE
return|;
default|default:
return|return
operator|new
name|ActiveShardCount
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
specifier|final
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|readFrom
specifier|public
specifier|static
name|ActiveShardCount
name|readFrom
parameter_list|(
specifier|final
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|get
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
return|;
block|}
DECL|method|validateValue
specifier|private
specifier|static
name|int
name|validateValue
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|<
literal|0
operator|&&
name|value
operator|!=
name|ACTIVE_SHARD_COUNT_DEFAULT
operator|&&
name|value
operator|!=
name|ALL_ACTIVE_SHARDS
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid ActiveShardCount["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
comment|/**      * Resolve this instance to an actual integer value for the number of active shard counts.      * If {@link ActiveShardCount#ALL} is specified, then the given {@link IndexMetaData} is      * used to determine what the actual active shard count should be.  The default value indicates      * one active shard.      */
DECL|method|resolve
specifier|public
name|int
name|resolve
parameter_list|(
specifier|final
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|ActiveShardCount
operator|.
name|DEFAULT
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|==
name|ActiveShardCount
operator|.
name|ALL
condition|)
block|{
return|return
name|indexMetaData
operator|.
name|getNumberOfReplicas
argument_list|()
operator|+
literal|1
return|;
block|}
else|else
block|{
return|return
name|value
return|;
block|}
block|}
comment|/**      * Parses the active shard count from the given string.  Valid values are "all" for      * all shard copies, null for the default value (which defaults to one shard copy),      * or a numeric value greater than or equal to 0. Any other input will throw an      * IllegalArgumentException.      */
DECL|method|parseString
specifier|public
specifier|static
name|ActiveShardCount
name|parseString
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
return|return
name|ActiveShardCount
operator|.
name|DEFAULT
return|;
block|}
elseif|else
if|if
condition|(
name|str
operator|.
name|equals
argument_list|(
literal|"all"
argument_list|)
condition|)
block|{
return|return
name|ActiveShardCount
operator|.
name|ALL
return|;
block|}
else|else
block|{
name|int
name|val
decl_stmt|;
try|try
block|{
name|val
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot parse ActiveShardCount["
operator|+
name|str
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|ActiveShardCount
operator|.
name|from
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns true iff the given cluster state's routing table contains enough active      * shards to meet the required shard count represented by this instance.      */
DECL|method|enoughShardsActive
specifier|public
name|boolean
name|enoughShardsActive
parameter_list|(
specifier|final
name|ClusterState
name|clusterState
parameter_list|,
specifier|final
name|String
name|indexName
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|ActiveShardCount
operator|.
name|NONE
condition|)
block|{
comment|// not waiting for any active shards
return|return
literal|true
return|;
block|}
specifier|final
name|IndexMetaData
name|indexMetaData
init|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|==
literal|null
condition|)
block|{
comment|// its possible the index was deleted while waiting for active shard copies,
comment|// in this case, we'll just consider it that we have enough active shard copies
comment|// and we can stop waiting
return|return
literal|true
return|;
block|}
specifier|final
name|IndexRoutingTable
name|indexRoutingTable
init|=
name|clusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
assert|assert
name|indexRoutingTable
operator|!=
literal|null
assert|;
if|if
condition|(
name|indexRoutingTable
operator|.
name|allPrimaryShardsActive
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// all primary shards aren't active yet
return|return
literal|false
return|;
block|}
for|for
control|(
specifier|final
name|IntObjectCursor
argument_list|<
name|IndexShardRoutingTable
argument_list|>
name|shardRouting
range|:
name|indexRoutingTable
operator|.
name|getShards
argument_list|()
control|)
block|{
if|if
condition|(
name|enoughShardsActive
argument_list|(
name|shardRouting
operator|.
name|value
argument_list|,
name|indexMetaData
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// not enough active shard copies yet
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Returns true iff the active shard count in the shard routing table is enough      * to meet the required shard count represented by this instance.      */
DECL|method|enoughShardsActive
specifier|public
name|boolean
name|enoughShardsActive
parameter_list|(
specifier|final
name|IndexShardRoutingTable
name|shardRoutingTable
parameter_list|,
specifier|final
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
if|if
condition|(
name|shardRoutingTable
operator|.
name|activeShards
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|resolve
argument_list|(
name|indexMetaData
argument_list|)
condition|)
block|{
comment|// not enough active shard copies yet
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|hashCode
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|ActiveShardCount
name|that
init|=
operator|(
name|ActiveShardCount
operator|)
name|o
decl_stmt|;
return|return
name|value
operator|==
name|that
operator|.
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|String
name|valStr
decl_stmt|;
switch|switch
condition|(
name|value
condition|)
block|{
case|case
name|ALL_ACTIVE_SHARDS
case|:
name|valStr
operator|=
literal|"ALL"
expr_stmt|;
break|break;
case|case
name|ACTIVE_SHARD_COUNT_DEFAULT
case|:
name|valStr
operator|=
literal|"DEFAULT"
expr_stmt|;
break|break;
default|default:
name|valStr
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
literal|"ActiveShardCount["
operator|+
name|valStr
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

