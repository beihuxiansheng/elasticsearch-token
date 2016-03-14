begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
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
name|index
operator|.
name|Index
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
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_class
DECL|class|PriorityComparatorTests
specifier|public
class|class
name|PriorityComparatorTests
extends|extends
name|ESTestCase
block|{
DECL|method|testPreferNewIndices
specifier|public
name|void
name|testPreferNewIndices
parameter_list|()
block|{
name|RoutingNodes
operator|.
name|UnassignedShards
name|shards
init|=
operator|new
name|RoutingNodes
operator|.
name|UnassignedShards
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shardRoutings
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"oldest"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|randomFrom
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|values
argument_list|()
argument_list|)
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
argument_list|,
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"newest"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|randomFrom
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|values
argument_list|()
argument_list|)
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|shardRoutings
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardRouting
name|routing
range|:
name|shardRoutings
control|)
block|{
name|shards
operator|.
name|add
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
name|shards
operator|.
name|sort
argument_list|(
operator|new
name|PriorityComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Settings
name|getIndexSettings
parameter_list|(
name|Index
name|index
parameter_list|)
block|{
if|if
condition|(
literal|"oldest"
operator|.
name|equals
argument_list|(
name|index
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_CREATION_DATE
argument_list|,
literal|10
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_PRIORITY
argument_list|,
literal|1
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"newest"
operator|.
name|equals
argument_list|(
name|index
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_CREATION_DATE
argument_list|,
literal|100
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_PRIORITY
argument_list|,
literal|1
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
return|return
name|Settings
operator|.
name|EMPTY
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|RoutingNodes
operator|.
name|UnassignedShards
operator|.
name|UnassignedIterator
name|iterator
init|=
name|shards
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|ShardRouting
name|next
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"newest"
argument_list|,
name|next
operator|.
name|getIndexName
argument_list|()
argument_list|)
expr_stmt|;
name|next
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oldest"
argument_list|,
name|next
operator|.
name|getIndexName
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPreferPriorityIndices
specifier|public
name|void
name|testPreferPriorityIndices
parameter_list|()
block|{
name|RoutingNodes
operator|.
name|UnassignedShards
name|shards
init|=
operator|new
name|RoutingNodes
operator|.
name|UnassignedShards
argument_list|(
operator|(
name|RoutingNodes
operator|)
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shardRoutings
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"oldest"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|randomFrom
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|values
argument_list|()
argument_list|)
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
argument_list|,
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"newest"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|randomFrom
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|values
argument_list|()
argument_list|)
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|shardRoutings
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardRouting
name|routing
range|:
name|shardRoutings
control|)
block|{
name|shards
operator|.
name|add
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
name|shards
operator|.
name|sort
argument_list|(
operator|new
name|PriorityComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Settings
name|getIndexSettings
parameter_list|(
name|Index
name|index
parameter_list|)
block|{
if|if
condition|(
literal|"oldest"
operator|.
name|equals
argument_list|(
name|index
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_CREATION_DATE
argument_list|,
literal|10
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_PRIORITY
argument_list|,
literal|100
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"newest"
operator|.
name|equals
argument_list|(
name|index
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_CREATION_DATE
argument_list|,
literal|100
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_PRIORITY
argument_list|,
literal|1
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
return|return
name|Settings
operator|.
name|EMPTY
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|RoutingNodes
operator|.
name|UnassignedShards
operator|.
name|UnassignedIterator
name|iterator
init|=
name|shards
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|ShardRouting
name|next
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"oldest"
argument_list|,
name|next
operator|.
name|getIndexName
argument_list|()
argument_list|)
expr_stmt|;
name|next
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"newest"
argument_list|,
name|next
operator|.
name|getIndexName
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPriorityComparatorSort
specifier|public
name|void
name|testPriorityComparatorSort
parameter_list|()
block|{
name|RoutingNodes
operator|.
name|UnassignedShards
name|shards
init|=
operator|new
name|RoutingNodes
operator|.
name|UnassignedShards
argument_list|(
operator|(
name|RoutingNodes
operator|)
literal|null
argument_list|)
decl_stmt|;
name|int
name|numIndices
init|=
name|randomIntBetween
argument_list|(
literal|3
argument_list|,
literal|99
argument_list|)
decl_stmt|;
name|IndexMeta
index|[]
name|indices
init|=
operator|new
name|IndexMeta
index|[
name|numIndices
index|]
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|IndexMeta
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
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
name|indices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|frequently
argument_list|()
condition|)
block|{
name|indices
index|[
name|i
index|]
operator|=
operator|new
name|IndexMeta
argument_list|(
literal|"idx_2015_04_"
operator|+
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%02d"
argument_list|,
name|i
argument_list|)
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// sometimes just use defaults
name|indices
index|[
name|i
index|]
operator|=
operator|new
name|IndexMeta
argument_list|(
literal|"idx_2015_04_"
operator|+
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%02d"
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|indices
index|[
name|i
index|]
operator|.
name|name
argument_list|,
name|indices
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|int
name|numShards
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
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
name|IndexMeta
name|indexMeta
init|=
name|randomFrom
argument_list|(
name|indices
argument_list|)
decl_stmt|;
name|shards
operator|.
name|add
argument_list|(
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|indexMeta
operator|.
name|name
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|randomFrom
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|values
argument_list|()
argument_list|)
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|shards
operator|.
name|sort
argument_list|(
operator|new
name|PriorityComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Settings
name|getIndexSettings
parameter_list|(
name|Index
name|index
parameter_list|)
block|{
name|IndexMeta
name|indexMeta
init|=
name|map
operator|.
name|get
argument_list|(
name|index
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|indexMeta
operator|.
name|settings
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|ShardRouting
name|previous
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ShardRouting
name|routing
range|:
name|shards
control|)
block|{
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
name|IndexMeta
name|prevMeta
init|=
name|map
operator|.
name|get
argument_list|(
name|previous
operator|.
name|getIndexName
argument_list|()
argument_list|)
decl_stmt|;
name|IndexMeta
name|currentMeta
init|=
name|map
operator|.
name|get
argument_list|(
name|routing
operator|.
name|getIndexName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|prevMeta
operator|.
name|priority
operator|==
name|currentMeta
operator|.
name|priority
condition|)
block|{
if|if
condition|(
name|prevMeta
operator|.
name|creationDate
operator|==
name|currentMeta
operator|.
name|creationDate
condition|)
block|{
if|if
condition|(
name|prevMeta
operator|.
name|name
operator|.
name|equals
argument_list|(
name|currentMeta
operator|.
name|name
argument_list|)
operator|==
literal|false
condition|)
block|{
name|assertTrue
argument_list|(
literal|"indexName mismatch, expected:"
operator|+
name|currentMeta
operator|.
name|name
operator|+
literal|" after "
operator|+
name|prevMeta
operator|.
name|name
operator|+
literal|" "
operator|+
name|prevMeta
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|currentMeta
operator|.
name|name
argument_list|)
argument_list|,
name|prevMeta
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|currentMeta
operator|.
name|name
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"creationDate mismatch, expected:"
operator|+
name|currentMeta
operator|.
name|creationDate
operator|+
literal|" after "
operator|+
name|prevMeta
operator|.
name|creationDate
argument_list|,
name|prevMeta
operator|.
name|creationDate
operator|>
name|currentMeta
operator|.
name|creationDate
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"priority mismatch, expected:"
operator|+
name|currentMeta
operator|.
name|priority
operator|+
literal|" after "
operator|+
name|prevMeta
operator|.
name|priority
argument_list|,
name|prevMeta
operator|.
name|priority
operator|>
name|currentMeta
operator|.
name|priority
argument_list|)
expr_stmt|;
block|}
block|}
name|previous
operator|=
name|routing
expr_stmt|;
block|}
block|}
DECL|class|IndexMeta
specifier|private
specifier|static
class|class
name|IndexMeta
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|priority
specifier|final
name|int
name|priority
decl_stmt|;
DECL|field|creationDate
specifier|final
name|long
name|creationDate
decl_stmt|;
DECL|field|settings
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|method|IndexMeta
specifier|private
name|IndexMeta
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// default
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|priority
operator|=
literal|1
expr_stmt|;
name|this
operator|.
name|creationDate
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|Settings
operator|.
name|EMPTY
expr_stmt|;
block|}
DECL|method|IndexMeta
specifier|private
name|IndexMeta
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|priority
parameter_list|,
name|long
name|creationDate
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
name|this
operator|.
name|creationDate
operator|=
name|creationDate
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_CREATION_DATE
argument_list|,
name|creationDate
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_PRIORITY
argument_list|,
name|priority
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

