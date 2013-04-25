begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.os
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|os
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
name|monitor
operator|.
name|sigar
operator|.
name|SigarService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hyperic
operator|.
name|sigar
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SigarOsProbe
specifier|public
class|class
name|SigarOsProbe
extends|extends
name|AbstractComponent
implements|implements
name|OsProbe
block|{
DECL|field|sigarService
specifier|private
specifier|final
name|SigarService
name|sigarService
decl_stmt|;
annotation|@
name|Inject
DECL|method|SigarOsProbe
specifier|public
name|SigarOsProbe
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|SigarService
name|sigarService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|sigarService
operator|=
name|sigarService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|osInfo
specifier|public
name|OsInfo
name|osInfo
parameter_list|()
block|{
name|Sigar
name|sigar
init|=
name|sigarService
operator|.
name|sigar
argument_list|()
decl_stmt|;
name|OsInfo
name|info
init|=
operator|new
name|OsInfo
argument_list|()
decl_stmt|;
try|try
block|{
name|CpuInfo
index|[]
name|infos
init|=
name|sigar
operator|.
name|getCpuInfoList
argument_list|()
decl_stmt|;
name|info
operator|.
name|cpu
operator|=
operator|new
name|OsInfo
operator|.
name|Cpu
argument_list|()
expr_stmt|;
name|info
operator|.
name|cpu
operator|.
name|vendor
operator|=
name|infos
index|[
literal|0
index|]
operator|.
name|getVendor
argument_list|()
expr_stmt|;
name|info
operator|.
name|cpu
operator|.
name|model
operator|=
name|infos
index|[
literal|0
index|]
operator|.
name|getModel
argument_list|()
expr_stmt|;
name|info
operator|.
name|cpu
operator|.
name|mhz
operator|=
name|infos
index|[
literal|0
index|]
operator|.
name|getMhz
argument_list|()
expr_stmt|;
name|info
operator|.
name|cpu
operator|.
name|totalCores
operator|=
name|infos
index|[
literal|0
index|]
operator|.
name|getTotalCores
argument_list|()
expr_stmt|;
name|info
operator|.
name|cpu
operator|.
name|totalSockets
operator|=
name|infos
index|[
literal|0
index|]
operator|.
name|getTotalSockets
argument_list|()
expr_stmt|;
name|info
operator|.
name|cpu
operator|.
name|coresPerSocket
operator|=
name|infos
index|[
literal|0
index|]
operator|.
name|getCoresPerSocket
argument_list|()
expr_stmt|;
if|if
condition|(
name|infos
index|[
literal|0
index|]
operator|.
name|getCacheSize
argument_list|()
operator|!=
name|Sigar
operator|.
name|FIELD_NOTIMPL
condition|)
block|{
name|info
operator|.
name|cpu
operator|.
name|cacheSize
operator|=
name|infos
index|[
literal|0
index|]
operator|.
name|getCacheSize
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SigarException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
name|Mem
name|mem
init|=
name|sigar
operator|.
name|getMem
argument_list|()
decl_stmt|;
name|info
operator|.
name|mem
operator|=
operator|new
name|OsInfo
operator|.
name|Mem
argument_list|()
expr_stmt|;
name|info
operator|.
name|mem
operator|.
name|total
operator|=
name|mem
operator|.
name|getTotal
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SigarException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
name|Swap
name|swap
init|=
name|sigar
operator|.
name|getSwap
argument_list|()
decl_stmt|;
name|info
operator|.
name|swap
operator|=
operator|new
name|OsInfo
operator|.
name|Swap
argument_list|()
expr_stmt|;
name|info
operator|.
name|swap
operator|.
name|total
operator|=
name|swap
operator|.
name|getTotal
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SigarException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
return|return
name|info
return|;
block|}
annotation|@
name|Override
DECL|method|osStats
specifier|public
name|OsStats
name|osStats
parameter_list|()
block|{
name|Sigar
name|sigar
init|=
name|sigarService
operator|.
name|sigar
argument_list|()
decl_stmt|;
name|OsStats
name|stats
init|=
operator|new
name|OsStats
argument_list|()
decl_stmt|;
name|stats
operator|.
name|timestamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
try|try
block|{
name|stats
operator|.
name|loadAverage
operator|=
name|sigar
operator|.
name|getLoadAverage
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SigarException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
name|stats
operator|.
name|uptime
operator|=
operator|(
name|long
operator|)
name|sigar
operator|.
name|getUptime
argument_list|()
operator|.
name|getUptime
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SigarException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
name|CpuPerc
name|cpuPerc
init|=
name|sigar
operator|.
name|getCpuPerc
argument_list|()
decl_stmt|;
name|stats
operator|.
name|cpu
operator|=
operator|new
name|OsStats
operator|.
name|Cpu
argument_list|()
expr_stmt|;
name|stats
operator|.
name|cpu
operator|.
name|sys
operator|=
call|(
name|short
call|)
argument_list|(
name|cpuPerc
operator|.
name|getSys
argument_list|()
operator|*
literal|100
argument_list|)
expr_stmt|;
name|stats
operator|.
name|cpu
operator|.
name|user
operator|=
call|(
name|short
call|)
argument_list|(
name|cpuPerc
operator|.
name|getUser
argument_list|()
operator|*
literal|100
argument_list|)
expr_stmt|;
name|stats
operator|.
name|cpu
operator|.
name|idle
operator|=
call|(
name|short
call|)
argument_list|(
name|cpuPerc
operator|.
name|getIdle
argument_list|()
operator|*
literal|100
argument_list|)
expr_stmt|;
name|stats
operator|.
name|cpu
operator|.
name|stolen
operator|=
call|(
name|short
call|)
argument_list|(
name|cpuPerc
operator|.
name|getStolen
argument_list|()
operator|*
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SigarException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
name|Mem
name|mem
init|=
name|sigar
operator|.
name|getMem
argument_list|()
decl_stmt|;
name|stats
operator|.
name|mem
operator|=
operator|new
name|OsStats
operator|.
name|Mem
argument_list|()
expr_stmt|;
name|stats
operator|.
name|mem
operator|.
name|free
operator|=
name|mem
operator|.
name|getFree
argument_list|()
expr_stmt|;
name|stats
operator|.
name|mem
operator|.
name|freePercent
operator|=
operator|(
name|short
operator|)
name|mem
operator|.
name|getFreePercent
argument_list|()
expr_stmt|;
name|stats
operator|.
name|mem
operator|.
name|used
operator|=
name|mem
operator|.
name|getUsed
argument_list|()
expr_stmt|;
name|stats
operator|.
name|mem
operator|.
name|usedPercent
operator|=
operator|(
name|short
operator|)
name|mem
operator|.
name|getUsedPercent
argument_list|()
expr_stmt|;
name|stats
operator|.
name|mem
operator|.
name|actualFree
operator|=
name|mem
operator|.
name|getActualFree
argument_list|()
expr_stmt|;
name|stats
operator|.
name|mem
operator|.
name|actualUsed
operator|=
name|mem
operator|.
name|getActualUsed
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SigarException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
name|Swap
name|swap
init|=
name|sigar
operator|.
name|getSwap
argument_list|()
decl_stmt|;
name|stats
operator|.
name|swap
operator|=
operator|new
name|OsStats
operator|.
name|Swap
argument_list|()
expr_stmt|;
name|stats
operator|.
name|swap
operator|.
name|free
operator|=
name|swap
operator|.
name|getFree
argument_list|()
expr_stmt|;
name|stats
operator|.
name|swap
operator|.
name|used
operator|=
name|swap
operator|.
name|getUsed
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SigarException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
return|return
name|stats
return|;
block|}
block|}
end_class

end_unit

