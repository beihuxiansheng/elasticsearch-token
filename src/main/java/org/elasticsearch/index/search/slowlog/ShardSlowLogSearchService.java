begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.slowlog
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|slowlog
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|XContentHelper
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
name|settings
operator|.
name|IndexSettingsService
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
name|io
operator|.
name|IOException
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
DECL|class|ShardSlowLogSearchService
specifier|public
class|class
name|ShardSlowLogSearchService
extends|extends
name|AbstractIndexShardComponent
block|{
DECL|field|reformat
specifier|private
name|boolean
name|reformat
decl_stmt|;
DECL|field|queryWarnThreshold
specifier|private
name|long
name|queryWarnThreshold
decl_stmt|;
DECL|field|queryInfoThreshold
specifier|private
name|long
name|queryInfoThreshold
decl_stmt|;
DECL|field|queryDebugThreshold
specifier|private
name|long
name|queryDebugThreshold
decl_stmt|;
DECL|field|queryTraceThreshold
specifier|private
name|long
name|queryTraceThreshold
decl_stmt|;
DECL|field|fetchWarnThreshold
specifier|private
name|long
name|fetchWarnThreshold
decl_stmt|;
DECL|field|fetchInfoThreshold
specifier|private
name|long
name|fetchInfoThreshold
decl_stmt|;
DECL|field|fetchDebugThreshold
specifier|private
name|long
name|fetchDebugThreshold
decl_stmt|;
DECL|field|fetchTraceThreshold
specifier|private
name|long
name|fetchTraceThreshold
decl_stmt|;
DECL|field|level
specifier|private
name|String
name|level
decl_stmt|;
DECL|field|queryLogger
specifier|private
specifier|final
name|ESLogger
name|queryLogger
decl_stmt|;
DECL|field|fetchLogger
specifier|private
specifier|final
name|ESLogger
name|fetchLogger
decl_stmt|;
static|static
block|{
name|IndexMetaData
operator|.
name|addDynamicSettings
argument_list|(
literal|"index.search.slowlog.threshold.query.warn"
argument_list|,
literal|"index.search.slowlog.threshold.query.info"
argument_list|,
literal|"index.search.slowlog.threshold.query.debug"
argument_list|,
literal|"index.search.slowlog.threshold.query.trace"
argument_list|,
literal|"index.search.slowlog.threshold.fetch.warn"
argument_list|,
literal|"index.search.slowlog.threshold.fetch.info"
argument_list|,
literal|"index.search.slowlog.threshold.fetch.debug"
argument_list|,
literal|"index.search.slowlog.threshold.fetch.trace"
argument_list|,
literal|"index.search.slowlog.reformat"
argument_list|,
literal|"index.search.slowlog.level"
argument_list|)
expr_stmt|;
block|}
DECL|class|ApplySettings
class|class
name|ApplySettings
implements|implements
name|IndexSettingsService
operator|.
name|Listener
block|{
annotation|@
name|Override
DECL|method|onRefreshSettings
specifier|public
specifier|synchronized
name|void
name|onRefreshSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|long
name|queryWarnThreshold
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
literal|"index.search.slowlog.threshold.query.warn"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryWarnThreshold
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryWarnThreshold
operator|!=
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryWarnThreshold
condition|)
block|{
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryWarnThreshold
operator|=
name|queryWarnThreshold
expr_stmt|;
block|}
name|long
name|queryInfoThreshold
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
literal|"index.search.slowlog.threshold.query.info"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryInfoThreshold
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryInfoThreshold
operator|!=
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryInfoThreshold
condition|)
block|{
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryInfoThreshold
operator|=
name|queryInfoThreshold
expr_stmt|;
block|}
name|long
name|queryDebugThreshold
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
literal|"index.search.slowlog.threshold.query.debug"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryDebugThreshold
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryDebugThreshold
operator|!=
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryDebugThreshold
condition|)
block|{
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryDebugThreshold
operator|=
name|queryDebugThreshold
expr_stmt|;
block|}
name|long
name|queryTraceThreshold
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
literal|"index.search.slowlog.threshold.query.trace"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryTraceThreshold
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryTraceThreshold
operator|!=
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryTraceThreshold
condition|)
block|{
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryTraceThreshold
operator|=
name|queryTraceThreshold
expr_stmt|;
block|}
name|long
name|fetchWarnThreshold
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
literal|"index.search.slowlog.threshold.fetch.warn"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchWarnThreshold
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|fetchWarnThreshold
operator|!=
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchWarnThreshold
condition|)
block|{
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchWarnThreshold
operator|=
name|fetchWarnThreshold
expr_stmt|;
block|}
name|long
name|fetchInfoThreshold
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
literal|"index.search.slowlog.threshold.fetch.info"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchInfoThreshold
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|fetchInfoThreshold
operator|!=
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchInfoThreshold
condition|)
block|{
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchInfoThreshold
operator|=
name|fetchInfoThreshold
expr_stmt|;
block|}
name|long
name|fetchDebugThreshold
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
literal|"index.search.slowlog.threshold.fetch.debug"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchDebugThreshold
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|fetchDebugThreshold
operator|!=
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchDebugThreshold
condition|)
block|{
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchDebugThreshold
operator|=
name|fetchDebugThreshold
expr_stmt|;
block|}
name|long
name|fetchTraceThreshold
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
literal|"index.search.slowlog.threshold.fetch.trace"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchTraceThreshold
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|fetchTraceThreshold
operator|!=
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchTraceThreshold
condition|)
block|{
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchTraceThreshold
operator|=
name|fetchTraceThreshold
expr_stmt|;
block|}
name|String
name|level
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"index.search.slowlog.level"
argument_list|,
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|level
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|level
operator|.
name|equals
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|level
argument_list|)
condition|)
block|{
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|queryLogger
operator|.
name|setLevel
argument_list|(
name|level
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|fetchLogger
operator|.
name|setLevel
argument_list|(
name|level
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
block|}
name|boolean
name|reformat
init|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"index.search.slowlog.reformat"
argument_list|,
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|reformat
argument_list|)
decl_stmt|;
if|if
condition|(
name|reformat
operator|!=
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|reformat
condition|)
block|{
name|ShardSlowLogSearchService
operator|.
name|this
operator|.
name|reformat
operator|=
name|reformat
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Inject
DECL|method|ShardSlowLogSearchService
specifier|public
name|ShardSlowLogSearchService
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|IndexSettingsService
name|indexSettingsService
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
name|reformat
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"reformat"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryWarnThreshold
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"threshold.query.warn"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
expr_stmt|;
name|this
operator|.
name|queryInfoThreshold
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"threshold.query.info"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
expr_stmt|;
name|this
operator|.
name|queryDebugThreshold
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"threshold.query.debug"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
expr_stmt|;
name|this
operator|.
name|queryTraceThreshold
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"threshold.query.trace"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
expr_stmt|;
name|this
operator|.
name|fetchWarnThreshold
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"threshold.fetch.warn"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
expr_stmt|;
name|this
operator|.
name|fetchInfoThreshold
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"threshold.fetch.info"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
expr_stmt|;
name|this
operator|.
name|fetchDebugThreshold
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"threshold.fetch.debug"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
expr_stmt|;
name|this
operator|.
name|fetchTraceThreshold
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"threshold.fetch.trace"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
expr_stmt|;
name|this
operator|.
name|level
operator|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"level"
argument_list|,
literal|"TRACE"
argument_list|)
operator|.
name|toUpperCase
argument_list|()
expr_stmt|;
name|this
operator|.
name|queryLogger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|logger
argument_list|,
literal|".query"
argument_list|)
expr_stmt|;
name|this
operator|.
name|fetchLogger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|logger
argument_list|,
literal|".fetch"
argument_list|)
expr_stmt|;
name|queryLogger
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|fetchLogger
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|indexSettingsService
operator|.
name|addListener
argument_list|(
operator|new
name|ApplySettings
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|onQueryPhase
specifier|public
name|void
name|onQueryPhase
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|long
name|tookInNanos
parameter_list|)
block|{
if|if
condition|(
name|queryWarnThreshold
operator|>=
literal|0
operator|&&
name|tookInNanos
operator|>
name|queryWarnThreshold
operator|&&
name|queryLogger
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|queryLogger
operator|.
name|warn
argument_list|(
literal|"{}"
argument_list|,
operator|new
name|SlowLogSearchContextPrinter
argument_list|(
name|context
argument_list|,
name|tookInNanos
argument_list|,
name|reformat
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queryInfoThreshold
operator|>=
literal|0
operator|&&
name|tookInNanos
operator|>
name|queryInfoThreshold
operator|&&
name|queryLogger
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|queryLogger
operator|.
name|info
argument_list|(
literal|"{}"
argument_list|,
operator|new
name|SlowLogSearchContextPrinter
argument_list|(
name|context
argument_list|,
name|tookInNanos
argument_list|,
name|reformat
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queryDebugThreshold
operator|>=
literal|0
operator|&&
name|tookInNanos
operator|>
name|queryDebugThreshold
operator|&&
name|queryLogger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|queryLogger
operator|.
name|debug
argument_list|(
literal|"{}"
argument_list|,
operator|new
name|SlowLogSearchContextPrinter
argument_list|(
name|context
argument_list|,
name|tookInNanos
argument_list|,
name|reformat
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queryTraceThreshold
operator|>=
literal|0
operator|&&
name|tookInNanos
operator|>
name|queryTraceThreshold
operator|&&
name|queryLogger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|queryLogger
operator|.
name|trace
argument_list|(
literal|"{}"
argument_list|,
operator|new
name|SlowLogSearchContextPrinter
argument_list|(
name|context
argument_list|,
name|tookInNanos
argument_list|,
name|reformat
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onFetchPhase
specifier|public
name|void
name|onFetchPhase
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|long
name|tookInNanos
parameter_list|)
block|{
if|if
condition|(
name|fetchWarnThreshold
operator|>=
literal|0
operator|&&
name|tookInNanos
operator|>
name|fetchWarnThreshold
operator|&&
name|fetchLogger
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|fetchLogger
operator|.
name|warn
argument_list|(
literal|"{}"
argument_list|,
operator|new
name|SlowLogSearchContextPrinter
argument_list|(
name|context
argument_list|,
name|tookInNanos
argument_list|,
name|reformat
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fetchInfoThreshold
operator|>=
literal|0
operator|&&
name|tookInNanos
operator|>
name|fetchInfoThreshold
operator|&&
name|fetchLogger
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|fetchLogger
operator|.
name|info
argument_list|(
literal|"{}"
argument_list|,
operator|new
name|SlowLogSearchContextPrinter
argument_list|(
name|context
argument_list|,
name|tookInNanos
argument_list|,
name|reformat
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fetchDebugThreshold
operator|>=
literal|0
operator|&&
name|tookInNanos
operator|>
name|fetchDebugThreshold
operator|&&
name|fetchLogger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|fetchLogger
operator|.
name|debug
argument_list|(
literal|"{}"
argument_list|,
operator|new
name|SlowLogSearchContextPrinter
argument_list|(
name|context
argument_list|,
name|tookInNanos
argument_list|,
name|reformat
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fetchTraceThreshold
operator|>=
literal|0
operator|&&
name|tookInNanos
operator|>
name|fetchTraceThreshold
operator|&&
name|fetchLogger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|fetchLogger
operator|.
name|trace
argument_list|(
literal|"{}"
argument_list|,
operator|new
name|SlowLogSearchContextPrinter
argument_list|(
name|context
argument_list|,
name|tookInNanos
argument_list|,
name|reformat
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SlowLogSearchContextPrinter
specifier|public
specifier|static
class|class
name|SlowLogSearchContextPrinter
block|{
DECL|field|context
specifier|private
specifier|final
name|SearchContext
name|context
decl_stmt|;
DECL|field|tookInNanos
specifier|private
specifier|final
name|long
name|tookInNanos
decl_stmt|;
DECL|field|reformat
specifier|private
specifier|final
name|boolean
name|reformat
decl_stmt|;
DECL|method|SlowLogSearchContextPrinter
specifier|public
name|SlowLogSearchContextPrinter
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|long
name|tookInNanos
parameter_list|,
name|boolean
name|reformat
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|tookInNanos
operator|=
name|tookInNanos
expr_stmt|;
name|this
operator|.
name|reformat
operator|=
name|reformat
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"took["
argument_list|)
operator|.
name|append
argument_list|(
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|tookInNanos
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"], took_millis["
argument_list|)
operator|.
name|append
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|tookInNanos
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"], "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"search_type["
argument_list|)
operator|.
name|append
argument_list|(
name|context
operator|.
name|searchType
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"], total_shards["
argument_list|)
operator|.
name|append
argument_list|(
name|context
operator|.
name|numberOfShards
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"], "
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|request
argument_list|()
operator|.
name|source
argument_list|()
operator|!=
literal|null
operator|&&
name|context
operator|.
name|request
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"source["
argument_list|)
operator|.
name|append
argument_list|(
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
name|context
operator|.
name|request
argument_list|()
operator|.
name|source
argument_list|()
argument_list|,
name|reformat
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"], "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"source[_failed_to_convert_], "
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"source[], "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|request
argument_list|()
operator|.
name|extraSource
argument_list|()
operator|!=
literal|null
operator|&&
name|context
operator|.
name|request
argument_list|()
operator|.
name|extraSource
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"extra_source["
argument_list|)
operator|.
name|append
argument_list|(
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
name|context
operator|.
name|request
argument_list|()
operator|.
name|extraSource
argument_list|()
argument_list|,
name|reformat
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"], "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"extra_source[_failed_to_convert_], "
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"extra_source[], "
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

