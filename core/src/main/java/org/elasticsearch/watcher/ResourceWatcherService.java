begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.watcher
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|watcher
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
name|Property
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
name|util
operator|.
name|concurrent
operator|.
name|FutureUtils
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
name|CopyOnWriteArraySet
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

begin_comment
comment|/**  * Generic resource watcher service  *  * Other elasticsearch services can register their resource watchers with this service using {@link #add(ResourceWatcher)}  * method. This service will call {@link org.elasticsearch.watcher.ResourceWatcher#checkAndNotify()} method of all  * registered watcher periodically. The frequency of checks can be specified using {@code resource.reload.interval} setting, which  * defaults to {@code 60s}. The service can be disabled by setting {@code resource.reload.enabled} setting to {@code false}.  */
end_comment

begin_class
DECL|class|ResourceWatcherService
specifier|public
class|class
name|ResourceWatcherService
extends|extends
name|AbstractLifecycleComponent
block|{
DECL|enum|Frequency
specifier|public
enum|enum
name|Frequency
block|{
comment|/**          * Defaults to 5 seconds          */
DECL|enum constant|HIGH
DECL|enum constant|TimeValue.timeValueSeconds
name|HIGH
argument_list|(
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|5
argument_list|)
argument_list|)
block|,
comment|/**          * Defaults to 30 seconds          */
DECL|enum constant|MEDIUM
DECL|enum constant|TimeValue.timeValueSeconds
name|MEDIUM
argument_list|(
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|30
argument_list|)
argument_list|)
block|,
comment|/**          * Defaults to 60 seconds          */
DECL|enum constant|LOW
DECL|enum constant|TimeValue.timeValueSeconds
name|LOW
argument_list|(
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|60
argument_list|)
argument_list|)
block|;
DECL|field|interval
specifier|final
name|TimeValue
name|interval
decl_stmt|;
DECL|method|Frequency
name|Frequency
parameter_list|(
name|TimeValue
name|interval
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
block|}
block|}
DECL|field|ENABLED
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|ENABLED
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"resource.reload.enabled"
argument_list|,
literal|true
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|RELOAD_INTERVAL_HIGH
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|RELOAD_INTERVAL_HIGH
init|=
name|Setting
operator|.
name|timeSetting
argument_list|(
literal|"resource.reload.interval.high"
argument_list|,
name|Frequency
operator|.
name|HIGH
operator|.
name|interval
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|RELOAD_INTERVAL_MEDIUM
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|RELOAD_INTERVAL_MEDIUM
init|=
name|Setting
operator|.
name|timeSetting
argument_list|(
literal|"resource.reload.interval.medium"
argument_list|,
name|Setting
operator|.
name|timeSetting
argument_list|(
literal|"resource.reload.interval"
argument_list|,
name|Frequency
operator|.
name|MEDIUM
operator|.
name|interval
argument_list|)
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|RELOAD_INTERVAL_LOW
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|RELOAD_INTERVAL_LOW
init|=
name|Setting
operator|.
name|timeSetting
argument_list|(
literal|"resource.reload.interval.low"
argument_list|,
name|Frequency
operator|.
name|LOW
operator|.
name|interval
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|enabled
specifier|private
specifier|final
name|boolean
name|enabled
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|lowMonitor
specifier|final
name|ResourceMonitor
name|lowMonitor
decl_stmt|;
DECL|field|mediumMonitor
specifier|final
name|ResourceMonitor
name|mediumMonitor
decl_stmt|;
DECL|field|highMonitor
specifier|final
name|ResourceMonitor
name|highMonitor
decl_stmt|;
DECL|field|lowFuture
specifier|private
specifier|volatile
name|ScheduledFuture
name|lowFuture
decl_stmt|;
DECL|field|mediumFuture
specifier|private
specifier|volatile
name|ScheduledFuture
name|mediumFuture
decl_stmt|;
DECL|field|highFuture
specifier|private
specifier|volatile
name|ScheduledFuture
name|highFuture
decl_stmt|;
annotation|@
name|Inject
DECL|method|ResourceWatcherService
specifier|public
name|ResourceWatcherService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|ENABLED
operator|.
name|get
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
name|TimeValue
name|interval
init|=
name|RELOAD_INTERVAL_LOW
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|lowMonitor
operator|=
operator|new
name|ResourceMonitor
argument_list|(
name|interval
argument_list|,
name|Frequency
operator|.
name|LOW
argument_list|)
expr_stmt|;
name|interval
operator|=
name|RELOAD_INTERVAL_MEDIUM
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|mediumMonitor
operator|=
operator|new
name|ResourceMonitor
argument_list|(
name|interval
argument_list|,
name|Frequency
operator|.
name|MEDIUM
argument_list|)
expr_stmt|;
name|interval
operator|=
name|RELOAD_INTERVAL_HIGH
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|highMonitor
operator|=
operator|new
name|ResourceMonitor
argument_list|(
name|interval
argument_list|,
name|Frequency
operator|.
name|HIGH
argument_list|)
expr_stmt|;
name|logRemovedSetting
argument_list|(
literal|"watcher.enabled"
argument_list|,
literal|"resource.reload.enabled"
argument_list|)
expr_stmt|;
name|logRemovedSetting
argument_list|(
literal|"watcher.interval"
argument_list|,
literal|"resource.reload.interval"
argument_list|)
expr_stmt|;
name|logRemovedSetting
argument_list|(
literal|"watcher.interval.low"
argument_list|,
literal|"resource.reload.interval.low"
argument_list|)
expr_stmt|;
name|logRemovedSetting
argument_list|(
literal|"watcher.interval.medium"
argument_list|,
literal|"resource.reload.interval.medium"
argument_list|)
expr_stmt|;
name|logRemovedSetting
argument_list|(
literal|"watcher.interval.high"
argument_list|,
literal|"resource.reload.interval.high"
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
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return;
block|}
name|lowFuture
operator|=
name|threadPool
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|lowMonitor
argument_list|,
name|lowMonitor
operator|.
name|interval
argument_list|)
expr_stmt|;
name|mediumFuture
operator|=
name|threadPool
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|mediumMonitor
argument_list|,
name|mediumMonitor
operator|.
name|interval
argument_list|)
expr_stmt|;
name|highFuture
operator|=
name|threadPool
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|highMonitor
argument_list|,
name|highMonitor
operator|.
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
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return;
block|}
name|FutureUtils
operator|.
name|cancel
argument_list|(
name|lowFuture
argument_list|)
expr_stmt|;
name|FutureUtils
operator|.
name|cancel
argument_list|(
name|mediumFuture
argument_list|)
expr_stmt|;
name|FutureUtils
operator|.
name|cancel
argument_list|(
name|highFuture
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
block|{     }
comment|/**      * Register new resource watcher that will be checked in default {@link Frequency#MEDIUM MEDIUM} frequency      */
DECL|method|add
specifier|public
parameter_list|<
name|W
extends|extends
name|ResourceWatcher
parameter_list|>
name|WatcherHandle
argument_list|<
name|W
argument_list|>
name|add
parameter_list|(
name|W
name|watcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|add
argument_list|(
name|watcher
argument_list|,
name|Frequency
operator|.
name|MEDIUM
argument_list|)
return|;
block|}
comment|/**      * Register new resource watcher that will be checked in the given frequency      */
DECL|method|add
specifier|public
parameter_list|<
name|W
extends|extends
name|ResourceWatcher
parameter_list|>
name|WatcherHandle
argument_list|<
name|W
argument_list|>
name|add
parameter_list|(
name|W
name|watcher
parameter_list|,
name|Frequency
name|frequency
parameter_list|)
throws|throws
name|IOException
block|{
name|watcher
operator|.
name|init
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|frequency
condition|)
block|{
case|case
name|LOW
case|:
return|return
name|lowMonitor
operator|.
name|add
argument_list|(
name|watcher
argument_list|)
return|;
case|case
name|MEDIUM
case|:
return|return
name|mediumMonitor
operator|.
name|add
argument_list|(
name|watcher
argument_list|)
return|;
case|case
name|HIGH
case|:
return|return
name|highMonitor
operator|.
name|add
argument_list|(
name|watcher
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown frequency ["
operator|+
name|frequency
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|notifyNow
specifier|public
name|void
name|notifyNow
parameter_list|()
block|{
name|notifyNow
argument_list|(
name|Frequency
operator|.
name|MEDIUM
argument_list|)
expr_stmt|;
block|}
DECL|method|notifyNow
specifier|public
name|void
name|notifyNow
parameter_list|(
name|Frequency
name|frequency
parameter_list|)
block|{
switch|switch
condition|(
name|frequency
condition|)
block|{
case|case
name|LOW
case|:
name|lowMonitor
operator|.
name|run
argument_list|()
expr_stmt|;
break|break;
case|case
name|MEDIUM
case|:
name|mediumMonitor
operator|.
name|run
argument_list|()
expr_stmt|;
break|break;
case|case
name|HIGH
case|:
name|highMonitor
operator|.
name|run
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown frequency ["
operator|+
name|frequency
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|class|ResourceMonitor
class|class
name|ResourceMonitor
implements|implements
name|Runnable
block|{
DECL|field|interval
specifier|final
name|TimeValue
name|interval
decl_stmt|;
DECL|field|frequency
specifier|final
name|Frequency
name|frequency
decl_stmt|;
DECL|field|watchers
specifier|final
name|Set
argument_list|<
name|ResourceWatcher
argument_list|>
name|watchers
init|=
operator|new
name|CopyOnWriteArraySet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ResourceMonitor
specifier|private
name|ResourceMonitor
parameter_list|(
name|TimeValue
name|interval
parameter_list|,
name|Frequency
name|frequency
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|frequency
operator|=
name|frequency
expr_stmt|;
block|}
DECL|method|add
specifier|private
parameter_list|<
name|W
extends|extends
name|ResourceWatcher
parameter_list|>
name|WatcherHandle
argument_list|<
name|W
argument_list|>
name|add
parameter_list|(
name|W
name|watcher
parameter_list|)
block|{
name|watchers
operator|.
name|add
argument_list|(
name|watcher
argument_list|)
expr_stmt|;
return|return
operator|new
name|WatcherHandle
argument_list|<>
argument_list|(
name|this
argument_list|,
name|watcher
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|ResourceWatcher
name|watcher
range|:
name|watchers
control|)
block|{
try|try
block|{
name|watcher
operator|.
name|checkAndNotify
argument_list|()
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
name|trace
argument_list|(
literal|"failed to check resource watcher"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

