begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.indexing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|indexing
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|MapBuilder
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
name|metrics
operator|.
name|CounterMetric
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
name|metrics
operator|.
name|MeanMetric
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
name|engine
operator|.
name|Engine
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
name|indexing
operator|.
name|slowlog
operator|.
name|ShardSlowLogIndexingService
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
name|settings
operator|.
name|IndexSettings
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
name|AbstractIndexShardComponent
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ShardIndexingService
specifier|public
class|class
name|ShardIndexingService
extends|extends
name|AbstractIndexShardComponent
block|{
DECL|field|slowLog
specifier|private
specifier|final
name|ShardSlowLogIndexingService
name|slowLog
decl_stmt|;
DECL|field|totalStats
specifier|private
specifier|final
name|StatsHolder
name|totalStats
init|=
operator|new
name|StatsHolder
argument_list|()
decl_stmt|;
DECL|field|listeners
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|IndexingOperationListener
argument_list|>
name|listeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|IndexingOperationListener
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|typesStats
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|StatsHolder
argument_list|>
name|typesStats
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|ShardIndexingService
specifier|public
name|ShardIndexingService
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|ShardSlowLogIndexingService
name|slowLog
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|slowLog
operator|=
name|slowLog
expr_stmt|;
block|}
comment|/**      * Returns the stats, including type specific stats. If the types are null/0 length, then nothing      * is returned for them. If they are set, then only types provided will be returned, or      *<tt>_all</tt> for all types.      */
DECL|method|stats
specifier|public
name|IndexingStats
name|stats
parameter_list|(
name|String
modifier|...
name|types
parameter_list|)
block|{
name|IndexingStats
operator|.
name|Stats
name|total
init|=
name|totalStats
operator|.
name|stats
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|IndexingStats
operator|.
name|Stats
argument_list|>
name|typesSt
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|types
operator|!=
literal|null
operator|&&
name|types
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|types
operator|.
name|length
operator|==
literal|1
operator|&&
name|types
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"_all"
argument_list|)
condition|)
block|{
name|typesSt
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|IndexingStats
operator|.
name|Stats
argument_list|>
argument_list|(
name|typesStats
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|StatsHolder
argument_list|>
name|entry
range|:
name|typesStats
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|typesSt
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|stats
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|typesSt
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|IndexingStats
operator|.
name|Stats
argument_list|>
argument_list|(
name|types
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|type
range|:
name|types
control|)
block|{
name|StatsHolder
name|statsHolder
init|=
name|typesStats
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|statsHolder
operator|!=
literal|null
condition|)
block|{
name|typesSt
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|statsHolder
operator|.
name|stats
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
operator|new
name|IndexingStats
argument_list|(
name|total
argument_list|,
name|typesSt
argument_list|)
return|;
block|}
DECL|method|addListener
specifier|public
name|void
name|addListener
parameter_list|(
name|IndexingOperationListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|removeListener
specifier|public
name|void
name|removeListener
parameter_list|(
name|IndexingOperationListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|preCreate
specifier|public
name|Engine
operator|.
name|Create
name|preCreate
parameter_list|(
name|Engine
operator|.
name|Create
name|create
parameter_list|)
block|{
name|totalStats
operator|.
name|indexCurrent
operator|.
name|inc
argument_list|()
expr_stmt|;
name|typeStats
argument_list|(
name|create
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|indexCurrent
operator|.
name|inc
argument_list|()
expr_stmt|;
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
name|create
operator|=
name|listener
operator|.
name|preCreate
argument_list|(
name|create
argument_list|)
expr_stmt|;
block|}
return|return
name|create
return|;
block|}
DECL|method|postCreateUnderLock
specifier|public
name|void
name|postCreateUnderLock
parameter_list|(
name|Engine
operator|.
name|Create
name|create
parameter_list|)
block|{
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|postCreateUnderLock
argument_list|(
name|create
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
name|warn
argument_list|(
literal|"post listener [{}] failed"
argument_list|,
name|e
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|postCreate
specifier|public
name|void
name|postCreate
parameter_list|(
name|Engine
operator|.
name|Create
name|create
parameter_list|)
block|{
name|long
name|took
init|=
name|create
operator|.
name|endTime
argument_list|()
operator|-
name|create
operator|.
name|startTime
argument_list|()
decl_stmt|;
name|totalStats
operator|.
name|indexMetric
operator|.
name|inc
argument_list|(
name|took
argument_list|)
expr_stmt|;
name|totalStats
operator|.
name|indexCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
name|StatsHolder
name|typeStats
init|=
name|typeStats
argument_list|(
name|create
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|typeStats
operator|.
name|indexMetric
operator|.
name|inc
argument_list|(
name|took
argument_list|)
expr_stmt|;
name|typeStats
operator|.
name|indexCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
name|slowLog
operator|.
name|postCreate
argument_list|(
name|create
argument_list|,
name|took
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|postCreate
argument_list|(
name|create
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
name|warn
argument_list|(
literal|"post listener [{}] failed"
argument_list|,
name|e
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|preIndex
specifier|public
name|Engine
operator|.
name|Index
name|preIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|index
parameter_list|)
block|{
name|totalStats
operator|.
name|indexCurrent
operator|.
name|inc
argument_list|()
expr_stmt|;
name|typeStats
argument_list|(
name|index
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|indexCurrent
operator|.
name|inc
argument_list|()
expr_stmt|;
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
name|index
operator|=
name|listener
operator|.
name|preIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
return|return
name|index
return|;
block|}
DECL|method|postIndexUnderLock
specifier|public
name|void
name|postIndexUnderLock
parameter_list|(
name|Engine
operator|.
name|Index
name|index
parameter_list|)
block|{
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|postIndexUnderLock
argument_list|(
name|index
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
name|warn
argument_list|(
literal|"post listener [{}] failed"
argument_list|,
name|e
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|postIndex
specifier|public
name|void
name|postIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|index
parameter_list|)
block|{
name|long
name|took
init|=
name|index
operator|.
name|endTime
argument_list|()
operator|-
name|index
operator|.
name|startTime
argument_list|()
decl_stmt|;
name|totalStats
operator|.
name|indexMetric
operator|.
name|inc
argument_list|(
name|took
argument_list|)
expr_stmt|;
name|totalStats
operator|.
name|indexCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
name|StatsHolder
name|typeStats
init|=
name|typeStats
argument_list|(
name|index
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|typeStats
operator|.
name|indexMetric
operator|.
name|inc
argument_list|(
name|took
argument_list|)
expr_stmt|;
name|typeStats
operator|.
name|indexCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
name|slowLog
operator|.
name|postIndex
argument_list|(
name|index
argument_list|,
name|took
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|postIndex
argument_list|(
name|index
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
name|warn
argument_list|(
literal|"post listener [{}] failed"
argument_list|,
name|e
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|failedIndex
specifier|public
name|void
name|failedIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|index
parameter_list|)
block|{
name|totalStats
operator|.
name|indexCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
name|typeStats
argument_list|(
name|index
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|indexCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
block|}
DECL|method|preDelete
specifier|public
name|Engine
operator|.
name|Delete
name|preDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|)
block|{
name|totalStats
operator|.
name|deleteCurrent
operator|.
name|inc
argument_list|()
expr_stmt|;
name|typeStats
argument_list|(
name|delete
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|deleteCurrent
operator|.
name|inc
argument_list|()
expr_stmt|;
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
name|delete
operator|=
name|listener
operator|.
name|preDelete
argument_list|(
name|delete
argument_list|)
expr_stmt|;
block|}
return|return
name|delete
return|;
block|}
DECL|method|postDeleteUnderLock
specifier|public
name|void
name|postDeleteUnderLock
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|)
block|{
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|postDeleteUnderLock
argument_list|(
name|delete
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
name|warn
argument_list|(
literal|"post listener [{}] failed"
argument_list|,
name|e
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|postDelete
specifier|public
name|void
name|postDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|)
block|{
name|long
name|took
init|=
name|delete
operator|.
name|endTime
argument_list|()
operator|-
name|delete
operator|.
name|startTime
argument_list|()
decl_stmt|;
name|totalStats
operator|.
name|deleteMetric
operator|.
name|inc
argument_list|(
name|took
argument_list|)
expr_stmt|;
name|totalStats
operator|.
name|deleteCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
name|StatsHolder
name|typeStats
init|=
name|typeStats
argument_list|(
name|delete
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|typeStats
operator|.
name|deleteMetric
operator|.
name|inc
argument_list|(
name|took
argument_list|)
expr_stmt|;
name|typeStats
operator|.
name|deleteCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|postDelete
argument_list|(
name|delete
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
name|warn
argument_list|(
literal|"post listener [{}] failed"
argument_list|,
name|e
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|failedDelete
specifier|public
name|void
name|failedDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|)
block|{
name|totalStats
operator|.
name|deleteCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
name|typeStats
argument_list|(
name|delete
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|deleteCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
block|}
DECL|method|preDeleteByQuery
specifier|public
name|Engine
operator|.
name|DeleteByQuery
name|preDeleteByQuery
parameter_list|(
name|Engine
operator|.
name|DeleteByQuery
name|deleteByQuery
parameter_list|)
block|{
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
name|deleteByQuery
operator|=
name|listener
operator|.
name|preDeleteByQuery
argument_list|(
name|deleteByQuery
argument_list|)
expr_stmt|;
block|}
return|return
name|deleteByQuery
return|;
block|}
DECL|method|postDeleteByQuery
specifier|public
name|void
name|postDeleteByQuery
parameter_list|(
name|Engine
operator|.
name|DeleteByQuery
name|deleteByQuery
parameter_list|)
block|{
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|postDeleteByQuery
argument_list|(
name|deleteByQuery
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|totalStats
operator|.
name|clear
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|typesStats
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|StatsHolder
argument_list|>
name|typesStatsBuilder
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|StatsHolder
argument_list|>
name|typeStats
range|:
name|typesStats
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|typeStats
operator|.
name|getValue
argument_list|()
operator|.
name|totalCurrent
argument_list|()
operator|>
literal|0
condition|)
block|{
name|typeStats
operator|.
name|getValue
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|typesStatsBuilder
operator|.
name|put
argument_list|(
name|typeStats
operator|.
name|getKey
argument_list|()
argument_list|,
name|typeStats
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|typesStats
operator|=
name|typesStatsBuilder
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|typeStats
specifier|private
name|StatsHolder
name|typeStats
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|StatsHolder
name|stats
init|=
name|typesStats
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|stats
operator|=
name|typesStats
operator|.
name|get
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
name|stats
operator|=
operator|new
name|StatsHolder
argument_list|()
expr_stmt|;
name|typesStats
operator|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|(
name|typesStats
argument_list|)
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|stats
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|stats
return|;
block|}
DECL|class|StatsHolder
specifier|static
class|class
name|StatsHolder
block|{
DECL|field|indexMetric
specifier|public
specifier|final
name|MeanMetric
name|indexMetric
init|=
operator|new
name|MeanMetric
argument_list|()
decl_stmt|;
DECL|field|deleteMetric
specifier|public
specifier|final
name|MeanMetric
name|deleteMetric
init|=
operator|new
name|MeanMetric
argument_list|()
decl_stmt|;
DECL|field|indexCurrent
specifier|public
specifier|final
name|CounterMetric
name|indexCurrent
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|deleteCurrent
specifier|public
specifier|final
name|CounterMetric
name|deleteCurrent
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|method|stats
specifier|public
name|IndexingStats
operator|.
name|Stats
name|stats
parameter_list|()
block|{
return|return
operator|new
name|IndexingStats
operator|.
name|Stats
argument_list|(
name|indexMetric
operator|.
name|count
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|indexMetric
operator|.
name|sum
argument_list|()
argument_list|)
argument_list|,
name|indexCurrent
operator|.
name|count
argument_list|()
argument_list|,
name|deleteMetric
operator|.
name|count
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|deleteMetric
operator|.
name|sum
argument_list|()
argument_list|)
argument_list|,
name|deleteCurrent
operator|.
name|count
argument_list|()
argument_list|)
return|;
block|}
DECL|method|totalCurrent
specifier|public
name|long
name|totalCurrent
parameter_list|()
block|{
return|return
name|indexCurrent
operator|.
name|count
argument_list|()
operator|+
name|deleteMetric
operator|.
name|count
argument_list|()
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|indexMetric
operator|.
name|clear
argument_list|()
expr_stmt|;
name|deleteMetric
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

