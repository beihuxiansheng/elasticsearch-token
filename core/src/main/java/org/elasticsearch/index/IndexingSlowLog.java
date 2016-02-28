begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
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
name|Booleans
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
name|Setting
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
name|Setting
operator|.
name|SettingsProperty
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
name|shard
operator|.
name|IndexingOperationListener
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
DECL|class|IndexingSlowLog
specifier|public
specifier|final
class|class
name|IndexingSlowLog
implements|implements
name|IndexingOperationListener
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
comment|/**      * How much of the source to log in the slowlog - 0 means log none and      * anything greater than 0 means log at least that many<em>characters</em>      * of the source.      */
DECL|field|maxSourceCharsToLog
specifier|private
name|int
name|maxSourceCharsToLog
decl_stmt|;
DECL|field|level
specifier|private
name|SlowLogLevel
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
DECL|field|INDEX_INDEXING_SLOWLOG_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_INDEXING_SLOWLOG_PREFIX
init|=
literal|"index.indexing.slowlog"
decl_stmt|;
DECL|field|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_WARN_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_WARN_SETTING
init|=
name|Setting
operator|.
name|timeSetting
argument_list|(
name|INDEX_INDEXING_SLOWLOG_PREFIX
operator|+
literal|".threshold.index.warn"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|SettingsProperty
operator|.
name|Dynamic
argument_list|,
name|SettingsProperty
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|field|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_INFO_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_INFO_SETTING
init|=
name|Setting
operator|.
name|timeSetting
argument_list|(
name|INDEX_INDEXING_SLOWLOG_PREFIX
operator|+
literal|".threshold.index.info"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|SettingsProperty
operator|.
name|Dynamic
argument_list|,
name|SettingsProperty
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|field|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_DEBUG_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_DEBUG_SETTING
init|=
name|Setting
operator|.
name|timeSetting
argument_list|(
name|INDEX_INDEXING_SLOWLOG_PREFIX
operator|+
literal|".threshold.index.debug"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|SettingsProperty
operator|.
name|Dynamic
argument_list|,
name|SettingsProperty
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|field|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_TRACE_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_TRACE_SETTING
init|=
name|Setting
operator|.
name|timeSetting
argument_list|(
name|INDEX_INDEXING_SLOWLOG_PREFIX
operator|+
literal|".threshold.index.trace"
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|SettingsProperty
operator|.
name|Dynamic
argument_list|,
name|SettingsProperty
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|field|INDEX_INDEXING_SLOWLOG_REFORMAT_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|INDEX_INDEXING_SLOWLOG_REFORMAT_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
name|INDEX_INDEXING_SLOWLOG_PREFIX
operator|+
literal|".reformat"
argument_list|,
literal|true
argument_list|,
name|SettingsProperty
operator|.
name|Dynamic
argument_list|,
name|SettingsProperty
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|field|INDEX_INDEXING_SLOWLOG_LEVEL_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|SlowLogLevel
argument_list|>
name|INDEX_INDEXING_SLOWLOG_LEVEL_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
name|INDEX_INDEXING_SLOWLOG_PREFIX
operator|+
literal|".level"
argument_list|,
name|SlowLogLevel
operator|.
name|TRACE
operator|.
name|name
argument_list|()
argument_list|,
name|SlowLogLevel
operator|::
name|parse
argument_list|,
name|SettingsProperty
operator|.
name|Dynamic
argument_list|,
name|SettingsProperty
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
comment|/**      * Reads how much of the source to log. The user can specify any value they      * like and numbers are interpreted the maximum number of characters to log      * and everything else is interpreted as Elasticsearch interprets booleans      * which is then converted to 0 for false and Integer.MAX_VALUE for true.      */
DECL|field|INDEX_INDEXING_SLOWLOG_MAX_SOURCE_CHARS_TO_LOG_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|INDEX_INDEXING_SLOWLOG_MAX_SOURCE_CHARS_TO_LOG_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
name|INDEX_INDEXING_SLOWLOG_PREFIX
operator|+
literal|".source"
argument_list|,
literal|"1000"
argument_list|,
parameter_list|(
name|value
parameter_list|)
lambda|->
block|{
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|,
literal|10
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
return|return
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|,
literal|true
argument_list|)
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
literal|0
return|;
block|}
block|}
argument_list|,
name|SettingsProperty
operator|.
name|Dynamic
argument_list|,
name|SettingsProperty
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|method|IndexingSlowLog
name|IndexingSlowLog
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|)
block|{
name|this
argument_list|(
name|indexSettings
argument_list|,
name|Loggers
operator|.
name|getLogger
argument_list|(
name|INDEX_INDEXING_SLOWLOG_PREFIX
operator|+
literal|".index"
argument_list|)
argument_list|,
name|Loggers
operator|.
name|getLogger
argument_list|(
name|INDEX_INDEXING_SLOWLOG_PREFIX
operator|+
literal|".delete"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Build with the specified loggers. Only used to testing.      */
DECL|method|IndexingSlowLog
name|IndexingSlowLog
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|ESLogger
name|indexLogger
parameter_list|,
name|ESLogger
name|deleteLogger
parameter_list|)
block|{
name|this
operator|.
name|indexLogger
operator|=
name|indexLogger
expr_stmt|;
name|this
operator|.
name|deleteLogger
operator|=
name|deleteLogger
expr_stmt|;
name|indexSettings
operator|.
name|getScopedSettings
argument_list|()
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|INDEX_INDEXING_SLOWLOG_REFORMAT_SETTING
argument_list|,
name|this
operator|::
name|setReformat
argument_list|)
expr_stmt|;
name|this
operator|.
name|reformat
operator|=
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_INDEXING_SLOWLOG_REFORMAT_SETTING
argument_list|)
expr_stmt|;
name|indexSettings
operator|.
name|getScopedSettings
argument_list|()
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_WARN_SETTING
argument_list|,
name|this
operator|::
name|setWarnThreshold
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexWarnThreshold
operator|=
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_WARN_SETTING
argument_list|)
operator|.
name|nanos
argument_list|()
expr_stmt|;
name|indexSettings
operator|.
name|getScopedSettings
argument_list|()
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_INFO_SETTING
argument_list|,
name|this
operator|::
name|setInfoThreshold
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexInfoThreshold
operator|=
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_INFO_SETTING
argument_list|)
operator|.
name|nanos
argument_list|()
expr_stmt|;
name|indexSettings
operator|.
name|getScopedSettings
argument_list|()
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_DEBUG_SETTING
argument_list|,
name|this
operator|::
name|setDebugThreshold
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexDebugThreshold
operator|=
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_DEBUG_SETTING
argument_list|)
operator|.
name|nanos
argument_list|()
expr_stmt|;
name|indexSettings
operator|.
name|getScopedSettings
argument_list|()
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_TRACE_SETTING
argument_list|,
name|this
operator|::
name|setTraceThreshold
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexTraceThreshold
operator|=
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_TRACE_SETTING
argument_list|)
operator|.
name|nanos
argument_list|()
expr_stmt|;
name|indexSettings
operator|.
name|getScopedSettings
argument_list|()
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|INDEX_INDEXING_SLOWLOG_LEVEL_SETTING
argument_list|,
name|this
operator|::
name|setLevel
argument_list|)
expr_stmt|;
name|setLevel
argument_list|(
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_INDEXING_SLOWLOG_LEVEL_SETTING
argument_list|)
argument_list|)
expr_stmt|;
name|indexSettings
operator|.
name|getScopedSettings
argument_list|()
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|INDEX_INDEXING_SLOWLOG_MAX_SOURCE_CHARS_TO_LOG_SETTING
argument_list|,
name|this
operator|::
name|setMaxSourceCharsToLog
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxSourceCharsToLog
operator|=
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_INDEXING_SLOWLOG_MAX_SOURCE_CHARS_TO_LOG_SETTING
argument_list|)
expr_stmt|;
block|}
DECL|method|setMaxSourceCharsToLog
specifier|private
name|void
name|setMaxSourceCharsToLog
parameter_list|(
name|int
name|maxSourceCharsToLog
parameter_list|)
block|{
name|this
operator|.
name|maxSourceCharsToLog
operator|=
name|maxSourceCharsToLog
expr_stmt|;
block|}
DECL|method|setLevel
specifier|private
name|void
name|setLevel
parameter_list|(
name|SlowLogLevel
name|level
parameter_list|)
block|{
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
name|this
operator|.
name|indexLogger
operator|.
name|setLevel
argument_list|(
name|level
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|deleteLogger
operator|.
name|setLevel
argument_list|(
name|level
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setWarnThreshold
specifier|private
name|void
name|setWarnThreshold
parameter_list|(
name|TimeValue
name|warnThreshold
parameter_list|)
block|{
name|this
operator|.
name|indexWarnThreshold
operator|=
name|warnThreshold
operator|.
name|nanos
argument_list|()
expr_stmt|;
block|}
DECL|method|setInfoThreshold
specifier|private
name|void
name|setInfoThreshold
parameter_list|(
name|TimeValue
name|infoThreshold
parameter_list|)
block|{
name|this
operator|.
name|indexInfoThreshold
operator|=
name|infoThreshold
operator|.
name|nanos
argument_list|()
expr_stmt|;
block|}
DECL|method|setDebugThreshold
specifier|private
name|void
name|setDebugThreshold
parameter_list|(
name|TimeValue
name|debugThreshold
parameter_list|)
block|{
name|this
operator|.
name|indexDebugThreshold
operator|=
name|debugThreshold
operator|.
name|nanos
argument_list|()
expr_stmt|;
block|}
DECL|method|setTraceThreshold
specifier|private
name|void
name|setTraceThreshold
parameter_list|(
name|TimeValue
name|traceThreshold
parameter_list|)
block|{
name|this
operator|.
name|indexTraceThreshold
operator|=
name|traceThreshold
operator|.
name|nanos
argument_list|()
expr_stmt|;
block|}
DECL|method|setReformat
specifier|private
name|void
name|setReformat
parameter_list|(
name|boolean
name|reformat
parameter_list|)
block|{
name|this
operator|.
name|reformat
operator|=
name|reformat
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
parameter_list|)
block|{
specifier|final
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
name|postIndexing
argument_list|(
name|index
operator|.
name|parsedDoc
argument_list|()
argument_list|,
name|took
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
argument_list|,
name|maxSourceCharsToLog
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
argument_list|,
name|maxSourceCharsToLog
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
argument_list|,
name|maxSourceCharsToLog
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
argument_list|,
name|maxSourceCharsToLog
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SlowLogParsedDocumentPrinter
specifier|static
specifier|final
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
DECL|field|maxSourceCharsToLog
specifier|private
specifier|final
name|int
name|maxSourceCharsToLog
decl_stmt|;
DECL|method|SlowLogParsedDocumentPrinter
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
parameter_list|,
name|int
name|maxSourceCharsToLog
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
name|this
operator|.
name|maxSourceCharsToLog
operator|=
name|maxSourceCharsToLog
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
literal|"routing[] "
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
literal|"] "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxSourceCharsToLog
operator|==
literal|0
operator|||
name|doc
operator|.
name|source
argument_list|()
operator|==
literal|null
operator|||
name|doc
operator|.
name|source
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
try|try
block|{
name|String
name|source
init|=
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
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", source["
argument_list|)
operator|.
name|append
argument_list|(
name|Strings
operator|.
name|cleanTruncate
argument_list|(
name|source
argument_list|,
name|maxSourceCharsToLog
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
literal|", source[_failed_to_convert_]"
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
DECL|method|isReformat
name|boolean
name|isReformat
parameter_list|()
block|{
return|return
name|reformat
return|;
block|}
DECL|method|getIndexWarnThreshold
name|long
name|getIndexWarnThreshold
parameter_list|()
block|{
return|return
name|indexWarnThreshold
return|;
block|}
DECL|method|getIndexInfoThreshold
name|long
name|getIndexInfoThreshold
parameter_list|()
block|{
return|return
name|indexInfoThreshold
return|;
block|}
DECL|method|getIndexTraceThreshold
name|long
name|getIndexTraceThreshold
parameter_list|()
block|{
return|return
name|indexTraceThreshold
return|;
block|}
DECL|method|getIndexDebugThreshold
name|long
name|getIndexDebugThreshold
parameter_list|()
block|{
return|return
name|indexDebugThreshold
return|;
block|}
DECL|method|getMaxSourceCharsToLog
name|int
name|getMaxSourceCharsToLog
parameter_list|()
block|{
return|return
name|maxSourceCharsToLog
return|;
block|}
DECL|method|getLevel
name|SlowLogLevel
name|getLevel
parameter_list|()
block|{
return|return
name|level
return|;
block|}
block|}
end_class

end_unit

