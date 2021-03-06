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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterInfo
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
name|Nullable
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
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterInfoService
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
DECL|field|refreshInterval
specifier|private
specifier|final
name|TimeValue
name|refreshInterval
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|SingleObjectCache
argument_list|<
name|FsInfo
argument_list|>
name|cache
decl_stmt|;
DECL|field|clusterInfoService
specifier|private
specifier|final
name|ClusterInfoService
name|clusterInfoService
decl_stmt|;
DECL|field|REFRESH_INTERVAL_SETTING
specifier|public
specifier|static
specifier|final
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
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|method|FsService
specifier|public
name|FsService
parameter_list|(
specifier|final
name|Settings
name|settings
parameter_list|,
specifier|final
name|NodeEnvironment
name|nodeEnvironment
parameter_list|,
name|ClusterInfoService
name|clusterInfoService
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
operator|new
name|FsProbe
argument_list|(
name|settings
argument_list|,
name|nodeEnvironment
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterInfoService
operator|=
name|clusterInfoService
expr_stmt|;
name|refreshInterval
operator|=
name|REFRESH_INTERVAL_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using refresh_interval [{}]"
argument_list|,
name|refreshInterval
argument_list|)
expr_stmt|;
name|cache
operator|=
operator|new
name|FsInfoCache
argument_list|(
name|refreshInterval
argument_list|,
name|stats
argument_list|(
name|probe
argument_list|,
literal|null
argument_list|,
name|logger
argument_list|,
literal|null
argument_list|)
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
name|cache
operator|.
name|getOrRefresh
argument_list|()
return|;
block|}
DECL|method|stats
specifier|private
specifier|static
name|FsInfo
name|stats
parameter_list|(
name|FsProbe
name|probe
parameter_list|,
name|FsInfo
name|initialValue
parameter_list|,
name|Logger
name|logger
parameter_list|,
annotation|@
name|Nullable
name|ClusterInfo
name|clusterInfo
parameter_list|)
block|{
try|try
block|{
return|return
name|probe
operator|.
name|stats
argument_list|(
name|initialValue
argument_list|,
name|clusterInfo
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"unexpected exception reading filesystem info"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
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
DECL|field|initialValue
specifier|private
specifier|final
name|FsInfo
name|initialValue
decl_stmt|;
DECL|method|FsInfoCache
name|FsInfoCache
parameter_list|(
name|TimeValue
name|interval
parameter_list|,
name|FsInfo
name|initialValue
parameter_list|)
block|{
name|super
argument_list|(
name|interval
argument_list|,
name|initialValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|initialValue
operator|=
name|initialValue
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
return|return
name|stats
argument_list|(
name|probe
argument_list|,
name|initialValue
argument_list|,
name|logger
argument_list|,
name|clusterInfoService
operator|.
name|getClusterInfo
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

