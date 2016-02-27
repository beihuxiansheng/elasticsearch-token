begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.fs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|fs
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|NodeEnvironment
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|FsService
specifier|public
class|class
name|FsService
extends|extends
name|AbstractComponent
block|{
DECL|field|probe
specifier|private
specifier|final
name|FsProbe
name|probe
decl_stmt|;
DECL|field|fsStatsCache
specifier|private
specifier|final
name|SingleObjectCache
argument_list|<
name|FsInfo
argument_list|>
name|fsStatsCache
decl_stmt|;
DECL|field|REFRESH_INTERVAL_SETTING
specifier|public
specifier|final
specifier|static
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|REFRESH_INTERVAL_SETTING
init|=
name|Setting
operator|.
name|timeSetting
argument_list|(
literal|"monitor.fs.refresh_interval"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|1
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|method|FsService
specifier|public
name|FsService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeEnvironment
name|nodeEnvironment
parameter_list|)
throws|throws
name|IOException
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
operator|new
name|FsProbe
argument_list|(
name|settings
argument_list|,
name|nodeEnvironment
argument_list|)
expr_stmt|;
name|TimeValue
name|refreshInterval
init|=
name|REFRESH_INTERVAL_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|fsStatsCache
operator|=
operator|new
name|FsInfoCache
argument_list|(
name|refreshInterval
argument_list|,
name|probe
operator|.
name|stats
argument_list|()
argument_list|)
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
DECL|method|stats
specifier|public
name|FsInfo
name|stats
parameter_list|()
block|{
return|return
name|fsStatsCache
operator|.
name|getOrRefresh
argument_list|()
return|;
block|}
DECL|class|FsInfoCache
specifier|private
class|class
name|FsInfoCache
extends|extends
name|SingleObjectCache
argument_list|<
name|FsInfo
argument_list|>
block|{
DECL|method|FsInfoCache
specifier|public
name|FsInfoCache
parameter_list|(
name|TimeValue
name|interval
parameter_list|,
name|FsInfo
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
name|FsInfo
name|refresh
parameter_list|()
block|{
try|try
block|{
return|return
name|probe
operator|.
name|stats
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to fetch fs stats - returning empty instance"
argument_list|)
expr_stmt|;
return|return
operator|new
name|FsInfo
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

