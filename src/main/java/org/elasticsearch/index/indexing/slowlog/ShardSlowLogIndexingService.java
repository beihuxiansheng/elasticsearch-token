begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.indexing.slowlog
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|indexing
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
name|mapper
operator|.
name|ParsedDocument
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
name|Locale
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
DECL|class|ShardSlowLogIndexingService
specifier|public
class|class
name|ShardSlowLogIndexingService
extends|extends
name|AbstractIndexShardComponent
block|{
DECL|field|reformat
specifier|private
name|boolean
name|reformat
decl_stmt|;
DECL|field|indexWarnThreshold
specifier|private
name|long
name|indexWarnThreshold
decl_stmt|;
DECL|field|indexInfoThreshold
specifier|private
name|long
name|indexInfoThreshold
decl_stmt|;
DECL|field|indexDebugThreshold
specifier|private
name|long
name|indexDebugThreshold
decl_stmt|;
DECL|field|indexTraceThreshold
specifier|private
name|long
name|indexTraceThreshold
decl_stmt|;
DECL|field|level
specifier|private
name|String
name|level
decl_stmt|;
DECL|field|indexLogger
specifier|private
specifier|final
name|ESLogger
name|indexLogger
decl_stmt|;
DECL|field|deleteLogger
specifier|private
specifier|final
name|ESLogger
name|deleteLogger
decl_stmt|;
DECL|field|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_WARN
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_WARN
init|=
literal|"index.indexing.slowlog.threshold.index.warn"
decl_stmt|;
DECL|field|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_INFO
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_INFO
init|=
literal|"index.indexing.slowlog.threshold.index.info"
decl_stmt|;
DECL|field|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_DEBUG
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_DEBUG
init|=
literal|"index.indexing.slowlog.threshold.index.debug"
decl_stmt|;
DECL|field|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_TRACE
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_TRACE
init|=
literal|"index.indexing.slowlog.threshold.index.trace"
decl_stmt|;
DECL|field|INDEX_INDEXING_SLOWLOG_REFORMAT
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_INDEXING_SLOWLOG_REFORMAT
init|=
literal|"index.indexing.slowlog.reformat"
decl_stmt|;
DECL|field|INDEX_INDEXING_SLOWLOG_LEVEL
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_INDEXING_SLOWLOG_LEVEL
init|=
literal|"index.indexing.slowlog.level"
decl_stmt|;
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
name|indexWarnThreshold
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_WARN
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexWarnThreshold
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexWarnThreshold
operator|!=
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexWarnThreshold
condition|)
block|{
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexWarnThreshold
operator|=
name|indexWarnThreshold
expr_stmt|;
block|}
name|long
name|indexInfoThreshold
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_INFO
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexInfoThreshold
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexInfoThreshold
operator|!=
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexInfoThreshold
condition|)
block|{
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexInfoThreshold
operator|=
name|indexInfoThreshold
expr_stmt|;
block|}
name|long
name|indexDebugThreshold
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_DEBUG
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexDebugThreshold
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexDebugThreshold
operator|!=
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexDebugThreshold
condition|)
block|{
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexDebugThreshold
operator|=
name|indexDebugThreshold
expr_stmt|;
block|}
name|long
name|indexTraceThreshold
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_TRACE
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexTraceThreshold
argument_list|)
argument_list|)
operator|.
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexTraceThreshold
operator|!=
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexTraceThreshold
condition|)
block|{
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexTraceThreshold
operator|=
name|indexTraceThreshold
expr_stmt|;
block|}
name|String
name|level
init|=
name|settings
operator|.
name|get
argument_list|(
name|INDEX_INDEXING_SLOWLOG_LEVEL
argument_list|,
name|ShardSlowLogIndexingService
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
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|level
argument_list|)
condition|)
block|{
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|indexLogger
operator|.
name|setLevel
argument_list|(
name|level
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|deleteLogger
operator|.
name|setLevel
argument_list|(
name|level
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|ShardSlowLogIndexingService
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
name|INDEX_INDEXING_SLOWLOG_REFORMAT
argument_list|,
name|ShardSlowLogIndexingService
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
name|ShardSlowLogIndexingService
operator|.
name|this
operator|.
name|reformat
condition|)
block|{
name|ShardSlowLogIndexingService
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
DECL|method|ShardSlowLogIndexingService
specifier|public
name|ShardSlowLogIndexingService
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
name|indexWarnThreshold
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"threshold.index.warn"
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
name|indexInfoThreshold
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"threshold.index.info"
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
name|indexDebugThreshold
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"threshold.index.debug"
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
name|indexTraceThreshold
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"threshold.index.trace"
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
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexLogger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|logger
argument_list|,
literal|".index"
argument_list|)
expr_stmt|;
name|this
operator|.
name|deleteLogger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|logger
argument_list|,
literal|".delete"
argument_list|)
expr_stmt|;
name|indexLogger
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|deleteLogger
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
name|long
name|tookInNanos
parameter_list|)
block|{
name|postIndexing
argument_list|(
name|index
operator|.
name|parsedDoc
argument_list|()
argument_list|,
name|tookInNanos
argument_list|)
expr_stmt|;
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
parameter_list|,
name|long
name|tookInNanos
parameter_list|)
block|{
name|postIndexing
argument_list|(
name|create
operator|.
name|parsedDoc
argument_list|()
argument_list|,
name|tookInNanos
argument_list|)
expr_stmt|;
block|}
DECL|method|postIndexing
specifier|private
name|void
name|postIndexing
parameter_list|(
name|ParsedDocument
name|doc
parameter_list|,
name|long
name|tookInNanos
parameter_list|)
block|{
if|if
condition|(
name|indexWarnThreshold
operator|>=
literal|0
operator|&&
name|tookInNanos
operator|>
name|indexWarnThreshold
condition|)
block|{
name|indexLogger
operator|.
name|warn
argument_list|(
literal|"{}"
argument_list|,
operator|new
name|SlowLogParsedDocumentPrinter
argument_list|(
name|doc
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
name|indexInfoThreshold
operator|>=
literal|0
operator|&&
name|tookInNanos
operator|>
name|indexInfoThreshold
condition|)
block|{
name|indexLogger
operator|.
name|info
argument_list|(
literal|"{}"
argument_list|,
operator|new
name|SlowLogParsedDocumentPrinter
argument_list|(
name|doc
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
name|indexDebugThreshold
operator|>=
literal|0
operator|&&
name|tookInNanos
operator|>
name|indexDebugThreshold
condition|)
block|{
name|indexLogger
operator|.
name|debug
argument_list|(
literal|"{}"
argument_list|,
operator|new
name|SlowLogParsedDocumentPrinter
argument_list|(
name|doc
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
name|indexTraceThreshold
operator|>=
literal|0
operator|&&
name|tookInNanos
operator|>
name|indexTraceThreshold
condition|)
block|{
name|indexLogger
operator|.
name|trace
argument_list|(
literal|"{}"
argument_list|,
operator|new
name|SlowLogParsedDocumentPrinter
argument_list|(
name|doc
argument_list|,
name|tookInNanos
argument_list|,
name|reformat
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SlowLogParsedDocumentPrinter
specifier|public
specifier|static
class|class
name|SlowLogParsedDocumentPrinter
block|{
DECL|field|doc
specifier|private
specifier|final
name|ParsedDocument
name|doc
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
DECL|method|SlowLogParsedDocumentPrinter
specifier|public
name|SlowLogParsedDocumentPrinter
parameter_list|(
name|ParsedDocument
name|doc
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
name|doc
operator|=
name|doc
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
literal|"type["
argument_list|)
operator|.
name|append
argument_list|(
name|doc
operator|.
name|type
argument_list|()
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
literal|"id["
argument_list|)
operator|.
name|append
argument_list|(
name|doc
operator|.
name|id
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
name|doc
operator|.
name|routing
argument_list|()
operator|==
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"routing[], "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"routing["
argument_list|)
operator|.
name|append
argument_list|(
name|doc
operator|.
name|routing
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"], "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|.
name|source
argument_list|()
operator|!=
literal|null
operator|&&
name|doc
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
name|doc
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
literal|"]"
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
literal|"source[_failed_to_convert_]"
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
literal|"source[]"
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

