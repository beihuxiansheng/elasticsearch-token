begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.process
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|process
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
name|AbstractComponent
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
name|common
operator|.
name|util
operator|.
name|SingleObjectCache
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ProcessService
specifier|public
specifier|final
class|class
name|ProcessService
extends|extends
name|AbstractComponent
block|{
DECL|field|probe
specifier|private
specifier|final
name|ProcessProbe
name|probe
decl_stmt|;
DECL|field|info
specifier|private
specifier|final
name|ProcessInfo
name|info
decl_stmt|;
DECL|field|processStatsCache
specifier|private
specifier|final
name|SingleObjectCache
argument_list|<
name|ProcessStats
argument_list|>
name|processStatsCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|ProcessService
specifier|public
name|ProcessService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ProcessProbe
name|probe
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|probe
operator|=
name|probe
expr_stmt|;
specifier|final
name|TimeValue
name|refreshInterval
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
literal|"monitor.process.refresh_interval"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|processStatsCache
operator|=
operator|new
name|ProcessStatsCache
argument_list|(
name|refreshInterval
argument_list|,
name|probe
operator|.
name|processStats
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|probe
operator|.
name|processInfo
argument_list|()
expr_stmt|;
name|this
operator|.
name|info
operator|.
name|refreshInterval
operator|=
name|refreshInterval
operator|.
name|millis
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Using probe [{}] with refresh_interval [{}]"
argument_list|,
name|probe
argument_list|,
name|refreshInterval
argument_list|)
expr_stmt|;
block|}
DECL|method|info
specifier|public
name|ProcessInfo
name|info
parameter_list|()
block|{
return|return
name|this
operator|.
name|info
return|;
block|}
DECL|method|stats
specifier|public
name|ProcessStats
name|stats
parameter_list|()
block|{
return|return
name|processStatsCache
operator|.
name|getOrRefresh
argument_list|()
return|;
block|}
DECL|class|ProcessStatsCache
specifier|private
class|class
name|ProcessStatsCache
extends|extends
name|SingleObjectCache
argument_list|<
name|ProcessStats
argument_list|>
block|{
DECL|method|ProcessStatsCache
specifier|public
name|ProcessStatsCache
parameter_list|(
name|TimeValue
name|interval
parameter_list|,
name|ProcessStats
name|initValue
parameter_list|)
block|{
name|super
argument_list|(
name|interval
argument_list|,
name|initValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|refresh
specifier|protected
name|ProcessStats
name|refresh
parameter_list|()
block|{
return|return
name|probe
operator|.
name|processStats
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit
