begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.stats
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|stats
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
name|search
operator|.
name|slowlog
operator|.
name|ShardSlowLogSearchService
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ShardSearchService
specifier|public
class|class
name|ShardSearchService
extends|extends
name|AbstractIndexShardComponent
block|{
DECL|field|slowLogSearchService
specifier|private
specifier|final
name|ShardSlowLogSearchService
name|slowLogSearchService
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
DECL|field|openContexts
specifier|private
specifier|final
name|CounterMetric
name|openContexts
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|groupsStats
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|StatsHolder
argument_list|>
name|groupsStats
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|ShardSearchService
specifier|public
name|ShardSearchService
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|ShardSlowLogSearchService
name|slowLogSearchService
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
name|slowLogSearchService
operator|=
name|slowLogSearchService
expr_stmt|;
block|}
comment|/**      * Returns the stats, including group specific stats. If the groups are null/0 length, then nothing      * is returned for them. If they are set, then only groups provided will be returned, or      *<tt>_all</tt> for all groups.      */
DECL|method|stats
specifier|public
name|SearchStats
name|stats
parameter_list|(
name|String
modifier|...
name|groups
parameter_list|)
block|{
name|SearchStats
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
name|SearchStats
operator|.
name|Stats
argument_list|>
name|groupsSt
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|groups
operator|!=
literal|null
operator|&&
name|groups
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|groups
operator|.
name|length
operator|==
literal|1
operator|&&
name|groups
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
name|groupsSt
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SearchStats
operator|.
name|Stats
argument_list|>
argument_list|(
name|groupsStats
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
name|groupsStats
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|groupsSt
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
name|groupsSt
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SearchStats
operator|.
name|Stats
argument_list|>
argument_list|(
name|groups
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|group
range|:
name|groups
control|)
block|{
name|StatsHolder
name|statsHolder
init|=
name|groupsStats
operator|.
name|get
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|statsHolder
operator|!=
literal|null
condition|)
block|{
name|groupsSt
operator|.
name|put
argument_list|(
name|group
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
name|SearchStats
argument_list|(
name|total
argument_list|,
name|openContexts
operator|.
name|count
argument_list|()
argument_list|,
name|groupsSt
argument_list|)
return|;
block|}
DECL|method|onPreQueryPhase
specifier|public
name|void
name|onPreQueryPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
block|{
name|totalStats
operator|.
name|queryCurrent
operator|.
name|inc
argument_list|()
expr_stmt|;
if|if
condition|(
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|groupStats
argument_list|(
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|queryCurrent
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|onFailedQueryPhase
specifier|public
name|void
name|onFailedQueryPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
block|{
name|totalStats
operator|.
name|queryCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
if|if
condition|(
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|groupStats
argument_list|(
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|queryCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|onQueryPhase
specifier|public
name|void
name|onQueryPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|,
name|long
name|tookInNanos
parameter_list|)
block|{
name|totalStats
operator|.
name|queryMetric
operator|.
name|inc
argument_list|(
name|tookInNanos
argument_list|)
expr_stmt|;
name|totalStats
operator|.
name|queryCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
if|if
condition|(
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|StatsHolder
name|statsHolder
init|=
name|groupStats
argument_list|(
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|statsHolder
operator|.
name|queryMetric
operator|.
name|inc
argument_list|(
name|tookInNanos
argument_list|)
expr_stmt|;
name|statsHolder
operator|.
name|queryCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
block|}
block|}
name|slowLogSearchService
operator|.
name|onQueryPhase
argument_list|(
name|searchContext
argument_list|,
name|tookInNanos
argument_list|)
expr_stmt|;
block|}
DECL|method|onPreFetchPhase
specifier|public
name|void
name|onPreFetchPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
block|{
name|totalStats
operator|.
name|fetchCurrent
operator|.
name|inc
argument_list|()
expr_stmt|;
if|if
condition|(
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|groupStats
argument_list|(
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|fetchCurrent
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|onFailedFetchPhase
specifier|public
name|void
name|onFailedFetchPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
block|{
name|totalStats
operator|.
name|fetchCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
if|if
condition|(
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|groupStats
argument_list|(
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|fetchCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|onFetchPhase
specifier|public
name|void
name|onFetchPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|,
name|long
name|tookInNanos
parameter_list|)
block|{
name|totalStats
operator|.
name|fetchMetric
operator|.
name|inc
argument_list|(
name|tookInNanos
argument_list|)
expr_stmt|;
name|totalStats
operator|.
name|fetchCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
if|if
condition|(
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|StatsHolder
name|statsHolder
init|=
name|groupStats
argument_list|(
name|searchContext
operator|.
name|groupStats
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|statsHolder
operator|.
name|fetchMetric
operator|.
name|inc
argument_list|(
name|tookInNanos
argument_list|)
expr_stmt|;
name|statsHolder
operator|.
name|fetchCurrent
operator|.
name|dec
argument_list|()
expr_stmt|;
block|}
block|}
name|slowLogSearchService
operator|.
name|onFetchPhase
argument_list|(
name|searchContext
argument_list|,
name|tookInNanos
argument_list|)
expr_stmt|;
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
name|groupsStats
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
name|groupsStats
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
name|groupsStats
operator|=
name|typesStatsBuilder
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|groupStats
specifier|private
name|StatsHolder
name|groupStats
parameter_list|(
name|String
name|group
parameter_list|)
block|{
name|StatsHolder
name|stats
init|=
name|groupsStats
operator|.
name|get
argument_list|(
name|group
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
name|groupsStats
operator|.
name|get
argument_list|(
name|group
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
name|groupsStats
operator|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|(
name|groupsStats
argument_list|)
operator|.
name|put
argument_list|(
name|group
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
DECL|method|onNewContext
specifier|public
name|void
name|onNewContext
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
name|openContexts
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
DECL|method|onFreeContext
specifier|public
name|void
name|onFreeContext
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
name|openContexts
operator|.
name|dec
argument_list|()
expr_stmt|;
block|}
DECL|class|StatsHolder
specifier|static
class|class
name|StatsHolder
block|{
DECL|field|queryMetric
specifier|public
specifier|final
name|MeanMetric
name|queryMetric
init|=
operator|new
name|MeanMetric
argument_list|()
decl_stmt|;
DECL|field|fetchMetric
specifier|public
specifier|final
name|MeanMetric
name|fetchMetric
init|=
operator|new
name|MeanMetric
argument_list|()
decl_stmt|;
DECL|field|queryCurrent
specifier|public
specifier|final
name|CounterMetric
name|queryCurrent
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|fetchCurrent
specifier|public
specifier|final
name|CounterMetric
name|fetchCurrent
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|method|stats
specifier|public
name|SearchStats
operator|.
name|Stats
name|stats
parameter_list|()
block|{
return|return
operator|new
name|SearchStats
operator|.
name|Stats
argument_list|(
name|queryMetric
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
name|queryMetric
operator|.
name|sum
argument_list|()
argument_list|)
argument_list|,
name|queryCurrent
operator|.
name|count
argument_list|()
argument_list|,
name|fetchMetric
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
name|fetchMetric
operator|.
name|sum
argument_list|()
argument_list|)
argument_list|,
name|fetchCurrent
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
name|queryCurrent
operator|.
name|count
argument_list|()
operator|+
name|fetchCurrent
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
name|queryMetric
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fetchMetric
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

