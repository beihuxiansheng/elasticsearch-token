begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.threadpool
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
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
name|EsExecutors
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
name|ThreadContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
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
name|concurrent
operator|.
name|Executor
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
name|ThreadFactory
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
comment|/**  * A builder for scaling executors.  */
end_comment

begin_class
DECL|class|ScalingExecutorBuilder
specifier|public
specifier|final
class|class
name|ScalingExecutorBuilder
extends|extends
name|ExecutorBuilder
argument_list|<
name|ScalingExecutorBuilder
operator|.
name|ScalingExecutorSettings
argument_list|>
block|{
DECL|field|coreSetting
specifier|private
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|coreSetting
decl_stmt|;
DECL|field|maxSetting
specifier|private
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|maxSetting
decl_stmt|;
DECL|field|keepAliveSetting
specifier|private
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|keepAliveSetting
decl_stmt|;
comment|/**      * Construct a scaling executor builder; the settings will have the      * key prefix "thread_pool." followed by the executor name.      *      * @param name      the name of the executor      * @param core      the minimum number of threads in the pool      * @param max       the maximum number of threads in the pool      * @param keepAlive the time that spare threads above {@code core}      *                  threads will be kept alive      */
DECL|method|ScalingExecutorBuilder
specifier|public
name|ScalingExecutorBuilder
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|int
name|core
parameter_list|,
specifier|final
name|int
name|max
parameter_list|,
specifier|final
name|TimeValue
name|keepAlive
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|core
argument_list|,
name|max
argument_list|,
name|keepAlive
argument_list|,
literal|"thread_pool."
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct a scaling executor builder; the settings will have the      * specified key prefix.      *      * @param name      the name of the executor      * @param core      the minimum number of threads in the pool      * @param max       the maximum number of threads in the pool      * @param keepAlive the time that spare threads above {@code core}      *                  threads will be kept alive      * @param prefix    the prefix for the settings keys      */
DECL|method|ScalingExecutorBuilder
specifier|public
name|ScalingExecutorBuilder
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|int
name|core
parameter_list|,
specifier|final
name|int
name|max
parameter_list|,
specifier|final
name|TimeValue
name|keepAlive
parameter_list|,
specifier|final
name|String
name|prefix
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|coreSetting
operator|=
name|Setting
operator|.
name|intSetting
argument_list|(
name|settingsKey
argument_list|(
name|prefix
argument_list|,
literal|"core"
argument_list|)
argument_list|,
name|core
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxSetting
operator|=
name|Setting
operator|.
name|intSetting
argument_list|(
name|settingsKey
argument_list|(
name|prefix
argument_list|,
literal|"max"
argument_list|)
argument_list|,
name|max
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
expr_stmt|;
name|this
operator|.
name|keepAliveSetting
operator|=
name|Setting
operator|.
name|timeSetting
argument_list|(
name|settingsKey
argument_list|(
name|prefix
argument_list|,
literal|"keep_alive"
argument_list|)
argument_list|,
name|keepAlive
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRegisteredSettings
name|List
argument_list|<
name|Setting
argument_list|<
name|?
argument_list|>
argument_list|>
name|getRegisteredSettings
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|coreSetting
argument_list|,
name|maxSetting
argument_list|,
name|keepAliveSetting
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSettings
name|ScalingExecutorSettings
name|getSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
specifier|final
name|String
name|nodeName
init|=
name|Node
operator|.
name|NODE_NAME_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
specifier|final
name|int
name|coreThreads
init|=
name|coreSetting
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxThreads
init|=
name|maxSetting
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
specifier|final
name|TimeValue
name|keepAlive
init|=
name|keepAliveSetting
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScalingExecutorSettings
argument_list|(
name|nodeName
argument_list|,
name|coreThreads
argument_list|,
name|maxThreads
argument_list|,
name|keepAlive
argument_list|)
return|;
block|}
DECL|method|build
name|ThreadPool
operator|.
name|ExecutorHolder
name|build
parameter_list|(
specifier|final
name|ScalingExecutorSettings
name|settings
parameter_list|,
specifier|final
name|ThreadContext
name|threadContext
parameter_list|)
block|{
name|TimeValue
name|keepAlive
init|=
name|settings
operator|.
name|keepAlive
decl_stmt|;
name|int
name|core
init|=
name|settings
operator|.
name|core
decl_stmt|;
name|int
name|max
init|=
name|settings
operator|.
name|max
decl_stmt|;
specifier|final
name|ThreadPool
operator|.
name|Info
name|info
init|=
operator|new
name|ThreadPool
operator|.
name|Info
argument_list|(
name|name
argument_list|()
argument_list|,
name|ThreadPool
operator|.
name|ThreadPoolType
operator|.
name|SCALING
argument_list|,
name|core
argument_list|,
name|max
argument_list|,
name|keepAlive
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|ThreadFactory
name|threadFactory
init|=
name|EsExecutors
operator|.
name|daemonThreadFactory
argument_list|(
name|EsExecutors
operator|.
name|threadName
argument_list|(
name|settings
operator|.
name|nodeName
argument_list|,
name|name
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Executor
name|executor
init|=
name|EsExecutors
operator|.
name|newScaling
argument_list|(
name|name
argument_list|()
argument_list|,
name|core
argument_list|,
name|max
argument_list|,
name|keepAlive
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|threadFactory
argument_list|,
name|threadContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|ThreadPool
operator|.
name|ExecutorHolder
argument_list|(
name|executor
argument_list|,
name|info
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|formatInfo
name|String
name|formatInfo
parameter_list|(
name|ThreadPool
operator|.
name|Info
name|info
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"name [%s], core [%d], max [%d], keep alive [%s]"
argument_list|,
name|info
operator|.
name|getName
argument_list|()
argument_list|,
name|info
operator|.
name|getMin
argument_list|()
argument_list|,
name|info
operator|.
name|getMax
argument_list|()
argument_list|,
name|info
operator|.
name|getKeepAlive
argument_list|()
argument_list|)
return|;
block|}
DECL|class|ScalingExecutorSettings
specifier|static
class|class
name|ScalingExecutorSettings
extends|extends
name|ExecutorBuilder
operator|.
name|ExecutorSettings
block|{
DECL|field|core
specifier|private
specifier|final
name|int
name|core
decl_stmt|;
DECL|field|max
specifier|private
specifier|final
name|int
name|max
decl_stmt|;
DECL|field|keepAlive
specifier|private
specifier|final
name|TimeValue
name|keepAlive
decl_stmt|;
DECL|method|ScalingExecutorSettings
specifier|public
name|ScalingExecutorSettings
parameter_list|(
specifier|final
name|String
name|nodeName
parameter_list|,
specifier|final
name|int
name|core
parameter_list|,
specifier|final
name|int
name|max
parameter_list|,
specifier|final
name|TimeValue
name|keepAlive
parameter_list|)
block|{
name|super
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|keepAlive
operator|=
name|keepAlive
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

