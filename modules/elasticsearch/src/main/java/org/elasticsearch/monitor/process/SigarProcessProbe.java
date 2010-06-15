begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|SigarProcessProbe
specifier|public
class|class
name|SigarProcessProbe
extends|extends
name|AbstractComponent
implements|implements
name|ProcessProbe
block|{
DECL|field|sigarService
specifier|private
specifier|final
name|SigarService
name|sigarService
decl_stmt|;
DECL|method|SigarProcessProbe
annotation|@
name|Inject
specifier|public
name|SigarProcessProbe
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
DECL|method|processInfo
annotation|@
name|Override
specifier|public
specifier|synchronized
name|ProcessInfo
name|processInfo
parameter_list|()
block|{
return|return
operator|new
name|ProcessInfo
argument_list|(
name|sigarService
operator|.
name|sigar
argument_list|()
operator|.
name|getPid
argument_list|()
argument_list|)
return|;
block|}
DECL|method|processStats
annotation|@
name|Override
specifier|public
specifier|synchronized
name|ProcessStats
name|processStats
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
name|ProcessStats
name|stats
init|=
operator|new
name|ProcessStats
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
name|ProcCpu
name|cpu
init|=
name|sigar
operator|.
name|getProcCpu
argument_list|(
name|sigar
operator|.
name|getPid
argument_list|()
argument_list|)
decl_stmt|;
name|stats
operator|.
name|cpu
operator|=
operator|new
name|ProcessStats
operator|.
name|Cpu
argument_list|()
expr_stmt|;
name|stats
operator|.
name|cpu
operator|.
name|percent
operator|=
call|(
name|short
call|)
argument_list|(
name|cpu
operator|.
name|getPercent
argument_list|()
operator|*
literal|100
argument_list|)
expr_stmt|;
name|stats
operator|.
name|cpu
operator|.
name|sys
operator|=
name|cpu
operator|.
name|getSys
argument_list|()
expr_stmt|;
name|stats
operator|.
name|cpu
operator|.
name|user
operator|=
name|cpu
operator|.
name|getUser
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
name|ProcMem
name|mem
init|=
name|sigar
operator|.
name|getProcMem
argument_list|(
name|sigar
operator|.
name|getPid
argument_list|()
argument_list|)
decl_stmt|;
name|stats
operator|.
name|mem
operator|=
operator|new
name|ProcessStats
operator|.
name|Mem
argument_list|()
expr_stmt|;
name|stats
operator|.
name|mem
operator|.
name|totalVirtual
operator|=
name|mem
operator|.
name|getSize
argument_list|()
expr_stmt|;
name|stats
operator|.
name|mem
operator|.
name|resident
operator|=
name|mem
operator|.
name|getResident
argument_list|()
expr_stmt|;
name|stats
operator|.
name|mem
operator|.
name|share
operator|=
name|mem
operator|.
name|getShare
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
name|ProcFd
name|fd
init|=
name|sigar
operator|.
name|getProcFd
argument_list|(
name|sigar
operator|.
name|getPid
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fd
operator|.
name|getTotal
argument_list|()
operator|!=
name|Sigar
operator|.
name|FIELD_NOTIMPL
condition|)
block|{
name|stats
operator|.
name|fd
operator|=
operator|new
name|ProcessStats
operator|.
name|Fd
argument_list|()
expr_stmt|;
name|stats
operator|.
name|fd
operator|.
name|total
operator|=
name|fd
operator|.
name|getTotal
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
return|return
name|stats
return|;
block|}
block|}
end_class

end_unit

