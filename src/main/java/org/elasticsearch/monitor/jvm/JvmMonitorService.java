begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.jvm
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|component
operator|.
name|AbstractLifecycleComponent
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
name|monitor
operator|.
name|dump
operator|.
name|DumpGenerator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|dump
operator|.
name|DumpMonitorService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
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
name|concurrent
operator|.
name|ScheduledFuture
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
operator|.
name|timeValueSeconds
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|dump
operator|.
name|summary
operator|.
name|SummaryDumpContributor
operator|.
name|SUMMARY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|dump
operator|.
name|thread
operator|.
name|ThreadDumpContributor
operator|.
name|THREAD_DUMP
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|DeadlockAnalyzer
operator|.
name|deadlockAnalyzer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmStats
operator|.
name|GarbageCollector
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmStats
operator|.
name|jvmStats
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|JvmMonitorService
specifier|public
class|class
name|JvmMonitorService
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|JvmMonitorService
argument_list|>
block|{
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|dumpMonitorService
specifier|private
specifier|final
name|DumpMonitorService
name|dumpMonitorService
decl_stmt|;
DECL|field|enabled
specifier|private
specifier|final
name|boolean
name|enabled
decl_stmt|;
DECL|field|interval
specifier|private
specifier|final
name|TimeValue
name|interval
decl_stmt|;
DECL|field|gcThresholds
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|GcThreshold
argument_list|>
name|gcThresholds
decl_stmt|;
DECL|field|scheduledFuture
specifier|private
specifier|volatile
name|ScheduledFuture
name|scheduledFuture
decl_stmt|;
DECL|class|GcThreshold
specifier|static
class|class
name|GcThreshold
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|warnThreshold
specifier|public
specifier|final
name|long
name|warnThreshold
decl_stmt|;
DECL|field|infoThreshold
specifier|public
specifier|final
name|long
name|infoThreshold
decl_stmt|;
DECL|field|debugThreshold
specifier|public
specifier|final
name|long
name|debugThreshold
decl_stmt|;
DECL|method|GcThreshold
name|GcThreshold
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|warnThreshold
parameter_list|,
name|long
name|infoThreshold
parameter_list|,
name|long
name|debugThreshold
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
name|warnThreshold
operator|=
name|warnThreshold
expr_stmt|;
name|this
operator|.
name|infoThreshold
operator|=
name|infoThreshold
expr_stmt|;
name|this
operator|.
name|debugThreshold
operator|=
name|debugThreshold
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
return|return
literal|"GcThreshold{"
operator|+
literal|"name='"
operator|+
name|name
operator|+
literal|'\''
operator|+
literal|", warnThreshold="
operator|+
name|warnThreshold
operator|+
literal|", infoThreshold="
operator|+
name|infoThreshold
operator|+
literal|", debugThreshold="
operator|+
name|debugThreshold
operator|+
literal|'}'
return|;
block|}
block|}
annotation|@
name|Inject
DECL|method|JvmMonitorService
specifier|public
name|JvmMonitorService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|DumpMonitorService
name|dumpMonitorService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|dumpMonitorService
operator|=
name|dumpMonitorService
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|interval
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"interval"
argument_list|,
name|timeValueSeconds
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|GcThreshold
argument_list|>
name|gcThresholds
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|gcThresholdGroups
init|=
name|componentSettings
operator|.
name|getGroups
argument_list|(
literal|"gc"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|entry
range|:
name|gcThresholdGroups
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|TimeValue
name|warn
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getAsTime
argument_list|(
literal|"warn"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|TimeValue
name|info
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getAsTime
argument_list|(
literal|"info"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|TimeValue
name|debug
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getAsTime
argument_list|(
literal|"debug"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|warn
operator|==
literal|null
operator|||
name|info
operator|==
literal|null
operator|||
name|debug
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"ignoring gc_threshold for [{}], missing warn/info/debug values"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|gcThresholds
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|GcThreshold
argument_list|(
name|name
argument_list|,
name|warn
operator|.
name|millis
argument_list|()
argument_list|,
name|info
operator|.
name|millis
argument_list|()
argument_list|,
name|debug
operator|.
name|millis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|gcThresholds
operator|.
name|containsKey
argument_list|(
name|GcNames
operator|.
name|YOUNG
argument_list|)
condition|)
block|{
name|gcThresholds
operator|.
name|put
argument_list|(
name|GcNames
operator|.
name|YOUNG
argument_list|,
operator|new
name|GcThreshold
argument_list|(
name|GcNames
operator|.
name|YOUNG
argument_list|,
literal|1000
argument_list|,
literal|700
argument_list|,
literal|400
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|gcThresholds
operator|.
name|containsKey
argument_list|(
name|GcNames
operator|.
name|OLD
argument_list|)
condition|)
block|{
name|gcThresholds
operator|.
name|put
argument_list|(
name|GcNames
operator|.
name|OLD
argument_list|,
operator|new
name|GcThreshold
argument_list|(
name|GcNames
operator|.
name|OLD
argument_list|,
literal|10000
argument_list|,
literal|5000
argument_list|,
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|gcThresholds
operator|.
name|containsKey
argument_list|(
literal|"default"
argument_list|)
condition|)
block|{
name|gcThresholds
operator|.
name|put
argument_list|(
literal|"default"
argument_list|,
operator|new
name|GcThreshold
argument_list|(
literal|"default"
argument_list|,
literal|10000
argument_list|,
literal|5000
argument_list|,
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|gcThresholds
operator|=
name|gcThresholds
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"enabled [{}], last_gc_enabled [{}], interval [{}], gc_threshold [{}]"
argument_list|,
name|enabled
argument_list|,
name|JvmStats
operator|.
name|isLastGcEnabled
argument_list|()
argument_list|,
name|interval
argument_list|,
name|this
operator|.
name|gcThresholds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return;
block|}
name|scheduledFuture
operator|=
name|threadPool
operator|.
name|scheduleWithFixedDelay
argument_list|(
operator|new
name|JvmMonitor
argument_list|()
argument_list|,
name|interval
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return;
block|}
name|scheduledFuture
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticsearchException
block|{     }
DECL|class|JvmMonitor
specifier|private
class|class
name|JvmMonitor
implements|implements
name|Runnable
block|{
DECL|field|lastJvmStats
specifier|private
name|JvmStats
name|lastJvmStats
init|=
name|jvmStats
argument_list|()
decl_stmt|;
DECL|field|seq
specifier|private
name|long
name|seq
init|=
literal|0
decl_stmt|;
DECL|field|lastSeenDeadlocks
specifier|private
specifier|final
name|Set
argument_list|<
name|DeadlockAnalyzer
operator|.
name|Deadlock
argument_list|>
name|lastSeenDeadlocks
init|=
operator|new
name|HashSet
argument_list|<
name|DeadlockAnalyzer
operator|.
name|Deadlock
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|JvmMonitor
specifier|public
name|JvmMonitor
parameter_list|()
block|{         }
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|//            monitorDeadlock();
name|monitorLongGc
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|monitorLongGc
specifier|private
specifier|synchronized
name|void
name|monitorLongGc
parameter_list|()
block|{
name|seq
operator|++
expr_stmt|;
name|JvmStats
name|currentJvmStats
init|=
name|jvmStats
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
name|currentJvmStats
operator|.
name|gc
argument_list|()
operator|.
name|collectors
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|GarbageCollector
name|gc
init|=
name|currentJvmStats
operator|.
name|gc
argument_list|()
operator|.
name|collectors
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|GarbageCollector
name|prevGc
init|=
name|lastJvmStats
operator|.
name|gc
operator|.
name|collectors
index|[
name|i
index|]
decl_stmt|;
comment|// no collection has happened
name|long
name|collections
init|=
name|gc
operator|.
name|collectionCount
operator|-
name|prevGc
operator|.
name|collectionCount
decl_stmt|;
if|if
condition|(
name|collections
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|long
name|collectionTime
init|=
name|gc
operator|.
name|collectionTime
operator|-
name|prevGc
operator|.
name|collectionTime
decl_stmt|;
if|if
condition|(
name|collectionTime
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|GcThreshold
name|gcThreshold
init|=
name|gcThresholds
operator|.
name|get
argument_list|(
name|gc
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|gcThreshold
operator|==
literal|null
condition|)
block|{
name|gcThreshold
operator|=
name|gcThresholds
operator|.
name|get
argument_list|(
literal|"default"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|gc
operator|.
name|lastGc
argument_list|()
operator|!=
literal|null
operator|&&
name|prevGc
operator|.
name|lastGc
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|GarbageCollector
operator|.
name|LastGc
name|lastGc
init|=
name|gc
operator|.
name|lastGc
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastGc
operator|.
name|startTime
operator|==
name|prevGc
operator|.
name|lastGc
argument_list|()
operator|.
name|startTime
argument_list|()
condition|)
block|{
comment|// we already handled this one...
continue|continue;
block|}
comment|// Ignore any duration> 1hr; getLastGcInfo occasionally returns total crap
if|if
condition|(
name|lastGc
operator|.
name|duration
argument_list|()
operator|.
name|hoursFrac
argument_list|()
operator|>
literal|1
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|lastGc
operator|.
name|duration
argument_list|()
operator|.
name|millis
argument_list|()
operator|>
name|gcThreshold
operator|.
name|warnThreshold
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[last_gc][{}][{}][{}] duration [{}], collections [{}], total [{}]/[{}], reclaimed [{}], leaving [{}][{}]/[{}]"
argument_list|,
name|gc
operator|.
name|name
argument_list|()
argument_list|,
name|seq
argument_list|,
name|gc
operator|.
name|getCollectionCount
argument_list|()
argument_list|,
name|lastGc
operator|.
name|duration
argument_list|()
argument_list|,
name|collections
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|collectionTime
argument_list|)
argument_list|,
name|gc
operator|.
name|collectionTime
argument_list|()
argument_list|,
name|lastGc
operator|.
name|reclaimed
argument_list|()
argument_list|,
name|lastGc
operator|.
name|afterUsed
argument_list|()
argument_list|,
name|lastGc
operator|.
name|max
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lastGc
operator|.
name|duration
argument_list|()
operator|.
name|millis
argument_list|()
operator|>
name|gcThreshold
operator|.
name|infoThreshold
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[last_gc][{}][{}][{}] duration [{}], collections [{}], total [{}]/[{}], reclaimed [{}], leaving [{}]/[{}]"
argument_list|,
name|gc
operator|.
name|name
argument_list|()
argument_list|,
name|seq
argument_list|,
name|gc
operator|.
name|getCollectionCount
argument_list|()
argument_list|,
name|lastGc
operator|.
name|duration
argument_list|()
argument_list|,
name|collections
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|collectionTime
argument_list|)
argument_list|,
name|gc
operator|.
name|collectionTime
argument_list|()
argument_list|,
name|lastGc
operator|.
name|reclaimed
argument_list|()
argument_list|,
name|lastGc
operator|.
name|afterUsed
argument_list|()
argument_list|,
name|lastGc
operator|.
name|max
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lastGc
operator|.
name|duration
argument_list|()
operator|.
name|millis
argument_list|()
operator|>
name|gcThreshold
operator|.
name|debugThreshold
operator|&&
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[last_gc][{}][{}][{}] duration [{}], collections [{}], total [{}]/[{}], reclaimed [{}], leaving [{}]/[{}]"
argument_list|,
name|gc
operator|.
name|name
argument_list|()
argument_list|,
name|seq
argument_list|,
name|gc
operator|.
name|getCollectionCount
argument_list|()
argument_list|,
name|lastGc
operator|.
name|duration
argument_list|()
argument_list|,
name|collections
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|collectionTime
argument_list|)
argument_list|,
name|gc
operator|.
name|collectionTime
argument_list|()
argument_list|,
name|lastGc
operator|.
name|reclaimed
argument_list|()
argument_list|,
name|lastGc
operator|.
name|afterUsed
argument_list|()
argument_list|,
name|lastGc
operator|.
name|max
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|avgCollectionTime
init|=
name|collectionTime
operator|/
name|collections
decl_stmt|;
if|if
condition|(
name|avgCollectionTime
operator|>
name|gcThreshold
operator|.
name|warnThreshold
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[gc][{}][{}][{}] duration [{}], collections [{}]/[{}], total [{}]/[{}], memory [{}]->[{}]/[{}], all_pools {}"
argument_list|,
name|gc
operator|.
name|name
argument_list|()
argument_list|,
name|seq
argument_list|,
name|gc
operator|.
name|collectionCount
argument_list|()
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|collectionTime
argument_list|)
argument_list|,
name|collections
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|currentJvmStats
operator|.
name|timestamp
argument_list|()
operator|-
name|lastJvmStats
operator|.
name|timestamp
argument_list|()
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|collectionTime
argument_list|)
argument_list|,
name|gc
operator|.
name|collectionTime
argument_list|()
argument_list|,
name|lastJvmStats
operator|.
name|mem
argument_list|()
operator|.
name|heapUsed
argument_list|()
argument_list|,
name|currentJvmStats
operator|.
name|mem
argument_list|()
operator|.
name|heapUsed
argument_list|()
argument_list|,
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|mem
argument_list|()
operator|.
name|heapMax
argument_list|()
argument_list|,
name|buildPools
argument_list|(
name|lastJvmStats
argument_list|,
name|currentJvmStats
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|avgCollectionTime
operator|>
name|gcThreshold
operator|.
name|infoThreshold
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[gc][{}][{}][{}] duration [{}], collections [{}]/[{}], total [{}]/[{}], memory [{}]->[{}]/[{}], all_pools {}"
argument_list|,
name|gc
operator|.
name|name
argument_list|()
argument_list|,
name|seq
argument_list|,
name|gc
operator|.
name|collectionCount
argument_list|()
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|collectionTime
argument_list|)
argument_list|,
name|collections
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|currentJvmStats
operator|.
name|timestamp
argument_list|()
operator|-
name|lastJvmStats
operator|.
name|timestamp
argument_list|()
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|collectionTime
argument_list|)
argument_list|,
name|gc
operator|.
name|collectionTime
argument_list|()
argument_list|,
name|lastJvmStats
operator|.
name|mem
argument_list|()
operator|.
name|heapUsed
argument_list|()
argument_list|,
name|currentJvmStats
operator|.
name|mem
argument_list|()
operator|.
name|heapUsed
argument_list|()
argument_list|,
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|mem
argument_list|()
operator|.
name|heapMax
argument_list|()
argument_list|,
name|buildPools
argument_list|(
name|lastJvmStats
argument_list|,
name|currentJvmStats
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|avgCollectionTime
operator|>
name|gcThreshold
operator|.
name|debugThreshold
operator|&&
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[gc][{}][{}][{}] duration [{}], collections [{}]/[{}], total [{}]/[{}], memory [{}]->[{}]/[{}], all_pools {}"
argument_list|,
name|gc
operator|.
name|name
argument_list|()
argument_list|,
name|seq
argument_list|,
name|gc
operator|.
name|collectionCount
argument_list|()
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|collectionTime
argument_list|)
argument_list|,
name|collections
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|currentJvmStats
operator|.
name|timestamp
argument_list|()
operator|-
name|lastJvmStats
operator|.
name|timestamp
argument_list|()
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|collectionTime
argument_list|)
argument_list|,
name|gc
operator|.
name|collectionTime
argument_list|()
argument_list|,
name|lastJvmStats
operator|.
name|mem
argument_list|()
operator|.
name|heapUsed
argument_list|()
argument_list|,
name|currentJvmStats
operator|.
name|mem
argument_list|()
operator|.
name|heapUsed
argument_list|()
argument_list|,
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|mem
argument_list|()
operator|.
name|heapMax
argument_list|()
argument_list|,
name|buildPools
argument_list|(
name|lastJvmStats
argument_list|,
name|currentJvmStats
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|lastJvmStats
operator|=
name|currentJvmStats
expr_stmt|;
block|}
DECL|method|buildPools
specifier|private
name|String
name|buildPools
parameter_list|(
name|JvmStats
name|prev
parameter_list|,
name|JvmStats
name|current
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|JvmStats
operator|.
name|MemoryPool
name|currentPool
range|:
name|current
operator|.
name|mem
argument_list|()
control|)
block|{
name|JvmStats
operator|.
name|MemoryPool
name|prevPool
init|=
literal|null
decl_stmt|;
for|for
control|(
name|JvmStats
operator|.
name|MemoryPool
name|pool
range|:
name|prev
operator|.
name|mem
argument_list|()
control|)
block|{
if|if
condition|(
name|pool
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|currentPool
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|prevPool
operator|=
name|pool
expr_stmt|;
break|break;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"{["
argument_list|)
operator|.
name|append
argument_list|(
name|currentPool
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"] ["
argument_list|)
operator|.
name|append
argument_list|(
name|prevPool
operator|==
literal|null
condition|?
literal|"?"
else|:
name|prevPool
operator|.
name|used
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"]->["
argument_list|)
operator|.
name|append
argument_list|(
name|currentPool
operator|.
name|used
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"]/["
argument_list|)
operator|.
name|append
argument_list|(
name|currentPool
operator|.
name|getMax
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"]}"
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
DECL|method|monitorDeadlock
specifier|private
name|void
name|monitorDeadlock
parameter_list|()
block|{
name|DeadlockAnalyzer
operator|.
name|Deadlock
index|[]
name|deadlocks
init|=
name|deadlockAnalyzer
argument_list|()
operator|.
name|findDeadlocks
argument_list|()
decl_stmt|;
if|if
condition|(
name|deadlocks
operator|!=
literal|null
operator|&&
name|deadlocks
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|ImmutableSet
argument_list|<
name|DeadlockAnalyzer
operator|.
name|Deadlock
argument_list|>
name|asSet
init|=
operator|new
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|DeadlockAnalyzer
operator|.
name|Deadlock
argument_list|>
argument_list|()
operator|.
name|add
argument_list|(
name|deadlocks
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|asSet
operator|.
name|equals
argument_list|(
name|lastSeenDeadlocks
argument_list|)
condition|)
block|{
name|DumpGenerator
operator|.
name|Result
name|genResult
init|=
name|dumpMonitorService
operator|.
name|generateDump
argument_list|(
literal|"deadlock"
argument_list|,
literal|null
argument_list|,
name|SUMMARY
argument_list|,
name|THREAD_DUMP
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Detected Deadlock(s)"
argument_list|)
decl_stmt|;
for|for
control|(
name|DeadlockAnalyzer
operator|.
name|Deadlock
name|deadlock
range|:
name|asSet
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n   ----> "
argument_list|)
operator|.
name|append
argument_list|(
name|deadlock
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\nDump generated ["
argument_list|)
operator|.
name|append
argument_list|(
name|genResult
operator|.
name|location
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|error
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|lastSeenDeadlocks
operator|.
name|clear
argument_list|()
expr_stmt|;
name|lastSeenDeadlocks
operator|.
name|addAll
argument_list|(
name|asSet
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|lastSeenDeadlocks
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

