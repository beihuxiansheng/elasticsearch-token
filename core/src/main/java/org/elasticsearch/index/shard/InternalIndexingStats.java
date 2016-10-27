begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
package|;
end_package

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
name|regex
operator|.
name|Regex
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
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_comment
comment|/**  * Internal class that maintains relevant indexing statistics / metrics.  * @see IndexShard  */
end_comment

begin_class
DECL|class|InternalIndexingStats
specifier|final
class|class
name|InternalIndexingStats
implements|implements
name|IndexingOperationListener
block|{
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
name|emptyMap
argument_list|()
decl_stmt|;
comment|/**      * Returns the stats, including type specific stats. If the types are null/0 length, then nothing      * is returned for them. If they are set, then only types provided will be returned, or      *<tt>_all</tt> for all types.      */
DECL|method|stats
name|IndexingStats
name|stats
parameter_list|(
name|boolean
name|isThrottled
parameter_list|,
name|long
name|currentThrottleInMillis
parameter_list|,
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
argument_list|(
name|isThrottled
argument_list|,
name|currentThrottleInMillis
argument_list|)
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
name|typesSt
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|typesStats
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
argument_list|(
name|isThrottled
argument_list|,
name|currentThrottleInMillis
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
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
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|types
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
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
argument_list|(
name|isThrottled
argument_list|,
name|currentThrottleInMillis
argument_list|)
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
annotation|@
name|Override
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
name|operation
parameter_list|)
block|{
if|if
condition|(
operator|!
name|operation
operator|.
name|origin
argument_list|()
operator|.
name|isRecovery
argument_list|()
condition|)
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
name|operation
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
block|}
return|return
name|operation
return|;
block|}
annotation|@
name|Override
DECL|method|postIndex
specifier|public
name|void
name|postIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|index
parameter_list|,
name|Engine
operator|.
name|IndexResult
name|result
parameter_list|)
block|{
if|if
condition|(
name|result
operator|.
name|hasFailure
argument_list|()
operator|==
literal|false
condition|)
block|{
if|if
condition|(
operator|!
name|index
operator|.
name|origin
argument_list|()
operator|.
name|isRecovery
argument_list|()
condition|)
block|{
name|long
name|took
init|=
name|result
operator|.
name|getTook
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
block|}
block|}
else|else
block|{
name|postIndex
argument_list|(
name|index
argument_list|,
name|result
operator|.
name|getFailure
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|postIndex
specifier|public
name|void
name|postIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|index
parameter_list|,
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|index
operator|.
name|origin
argument_list|()
operator|.
name|isRecovery
argument_list|()
condition|)
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
name|totalStats
operator|.
name|indexFailed
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
name|indexFailed
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
if|if
condition|(
operator|!
name|delete
operator|.
name|origin
argument_list|()
operator|.
name|isRecovery
argument_list|()
condition|)
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
block|}
return|return
name|delete
return|;
block|}
annotation|@
name|Override
DECL|method|postDelete
specifier|public
name|void
name|postDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|,
name|Engine
operator|.
name|DeleteResult
name|result
parameter_list|)
block|{
if|if
condition|(
name|result
operator|.
name|hasFailure
argument_list|()
operator|==
literal|false
condition|)
block|{
if|if
condition|(
operator|!
name|delete
operator|.
name|origin
argument_list|()
operator|.
name|isRecovery
argument_list|()
condition|)
block|{
name|long
name|took
init|=
name|result
operator|.
name|getTook
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
block|}
block|}
else|else
block|{
name|postDelete
argument_list|(
name|delete
argument_list|,
name|result
operator|.
name|getFailure
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|postDelete
specifier|public
name|void
name|postDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|,
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|delete
operator|.
name|origin
argument_list|()
operator|.
name|isRecovery
argument_list|()
condition|)
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
block|}
DECL|method|noopUpdate
specifier|public
name|void
name|noopUpdate
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|totalStats
operator|.
name|noopUpdates
operator|.
name|inc
argument_list|()
expr_stmt|;
name|typeStats
argument_list|(
name|type
argument_list|)
operator|.
name|noopUpdates
operator|.
name|inc
argument_list|()
expr_stmt|;
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
specifier|private
specifier|final
name|MeanMetric
name|indexMetric
init|=
operator|new
name|MeanMetric
argument_list|()
decl_stmt|;
DECL|field|deleteMetric
specifier|private
specifier|final
name|MeanMetric
name|deleteMetric
init|=
operator|new
name|MeanMetric
argument_list|()
decl_stmt|;
DECL|field|indexCurrent
specifier|private
specifier|final
name|CounterMetric
name|indexCurrent
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|indexFailed
specifier|private
specifier|final
name|CounterMetric
name|indexFailed
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|deleteCurrent
specifier|private
specifier|final
name|CounterMetric
name|deleteCurrent
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|noopUpdates
specifier|private
specifier|final
name|CounterMetric
name|noopUpdates
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|method|stats
name|IndexingStats
operator|.
name|Stats
name|stats
parameter_list|(
name|boolean
name|isThrottled
parameter_list|,
name|long
name|currentThrottleMillis
parameter_list|)
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
name|indexFailed
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
argument_list|,
name|noopUpdates
operator|.
name|count
argument_list|()
argument_list|,
name|isThrottled
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
name|currentThrottleMillis
argument_list|)
argument_list|)
return|;
block|}
DECL|method|clear
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

