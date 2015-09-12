begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|OperatingSystemMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_class
DECL|class|OsProbe
specifier|public
class|class
name|OsProbe
block|{
DECL|field|osMxBean
specifier|private
specifier|static
specifier|final
name|OperatingSystemMXBean
name|osMxBean
init|=
name|ManagementFactory
operator|.
name|getOperatingSystemMXBean
argument_list|()
decl_stmt|;
DECL|field|getFreePhysicalMemorySize
specifier|private
specifier|static
specifier|final
name|Method
name|getFreePhysicalMemorySize
decl_stmt|;
DECL|field|getTotalPhysicalMemorySize
specifier|private
specifier|static
specifier|final
name|Method
name|getTotalPhysicalMemorySize
decl_stmt|;
DECL|field|getFreeSwapSpaceSize
specifier|private
specifier|static
specifier|final
name|Method
name|getFreeSwapSpaceSize
decl_stmt|;
DECL|field|getTotalSwapSpaceSize
specifier|private
specifier|static
specifier|final
name|Method
name|getTotalSwapSpaceSize
decl_stmt|;
DECL|field|getSystemLoadAverage
specifier|private
specifier|static
specifier|final
name|Method
name|getSystemLoadAverage
decl_stmt|;
static|static
block|{
name|getFreePhysicalMemorySize
operator|=
name|getMethod
argument_list|(
literal|"getFreePhysicalMemorySize"
argument_list|)
expr_stmt|;
name|getTotalPhysicalMemorySize
operator|=
name|getMethod
argument_list|(
literal|"getTotalPhysicalMemorySize"
argument_list|)
expr_stmt|;
name|getFreeSwapSpaceSize
operator|=
name|getMethod
argument_list|(
literal|"getFreeSwapSpaceSize"
argument_list|)
expr_stmt|;
name|getTotalSwapSpaceSize
operator|=
name|getMethod
argument_list|(
literal|"getTotalSwapSpaceSize"
argument_list|)
expr_stmt|;
name|getSystemLoadAverage
operator|=
name|getMethod
argument_list|(
literal|"getSystemLoadAverage"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the amount of free physical memory in bytes.      */
DECL|method|getFreePhysicalMemorySize
specifier|public
name|long
name|getFreePhysicalMemorySize
parameter_list|()
block|{
if|if
condition|(
name|getFreePhysicalMemorySize
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
try|try
block|{
return|return
operator|(
name|long
operator|)
name|getFreePhysicalMemorySize
operator|.
name|invoke
argument_list|(
name|osMxBean
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|/**      * Returns the total amount of physical memory in bytes.      */
DECL|method|getTotalPhysicalMemorySize
specifier|public
name|long
name|getTotalPhysicalMemorySize
parameter_list|()
block|{
if|if
condition|(
name|getTotalPhysicalMemorySize
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
try|try
block|{
return|return
operator|(
name|long
operator|)
name|getTotalPhysicalMemorySize
operator|.
name|invoke
argument_list|(
name|osMxBean
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|/**      * Returns the amount of free swap space in bytes.      */
DECL|method|getFreeSwapSpaceSize
specifier|public
name|long
name|getFreeSwapSpaceSize
parameter_list|()
block|{
if|if
condition|(
name|getFreeSwapSpaceSize
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
try|try
block|{
return|return
operator|(
name|long
operator|)
name|getFreeSwapSpaceSize
operator|.
name|invoke
argument_list|(
name|osMxBean
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|/**      * Returns the total amount of swap space in bytes.      */
DECL|method|getTotalSwapSpaceSize
specifier|public
name|long
name|getTotalSwapSpaceSize
parameter_list|()
block|{
if|if
condition|(
name|getTotalSwapSpaceSize
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
try|try
block|{
return|return
operator|(
name|long
operator|)
name|getTotalSwapSpaceSize
operator|.
name|invoke
argument_list|(
name|osMxBean
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|/**      * Returns the system load average for the last minute.      */
DECL|method|getSystemLoadAverage
specifier|public
name|double
name|getSystemLoadAverage
parameter_list|()
block|{
if|if
condition|(
name|getSystemLoadAverage
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
try|try
block|{
return|return
operator|(
name|double
operator|)
name|getSystemLoadAverage
operator|.
name|invoke
argument_list|(
name|osMxBean
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
DECL|class|OsProbeHolder
specifier|private
specifier|static
class|class
name|OsProbeHolder
block|{
DECL|field|INSTANCE
specifier|private
specifier|final
specifier|static
name|OsProbe
name|INSTANCE
init|=
operator|new
name|OsProbe
argument_list|()
decl_stmt|;
block|}
DECL|method|getInstance
specifier|public
specifier|static
name|OsProbe
name|getInstance
parameter_list|()
block|{
return|return
name|OsProbeHolder
operator|.
name|INSTANCE
return|;
block|}
DECL|method|OsProbe
specifier|private
name|OsProbe
parameter_list|()
block|{     }
DECL|method|osInfo
specifier|public
name|OsInfo
name|osInfo
parameter_list|()
block|{
name|OsInfo
name|info
init|=
operator|new
name|OsInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|availableProcessors
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
expr_stmt|;
name|info
operator|.
name|name
operator|=
name|Constants
operator|.
name|OS_NAME
expr_stmt|;
name|info
operator|.
name|arch
operator|=
name|Constants
operator|.
name|OS_ARCH
expr_stmt|;
name|info
operator|.
name|version
operator|=
name|Constants
operator|.
name|OS_VERSION
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|osStats
specifier|public
name|OsStats
name|osStats
parameter_list|()
block|{
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
name|stats
operator|.
name|loadAverage
operator|=
name|getSystemLoadAverage
argument_list|()
expr_stmt|;
name|OsStats
operator|.
name|Mem
name|mem
init|=
operator|new
name|OsStats
operator|.
name|Mem
argument_list|()
decl_stmt|;
name|mem
operator|.
name|total
operator|=
name|getTotalPhysicalMemorySize
argument_list|()
expr_stmt|;
name|mem
operator|.
name|free
operator|=
name|getFreePhysicalMemorySize
argument_list|()
expr_stmt|;
name|stats
operator|.
name|mem
operator|=
name|mem
expr_stmt|;
name|OsStats
operator|.
name|Swap
name|swap
init|=
operator|new
name|OsStats
operator|.
name|Swap
argument_list|()
decl_stmt|;
name|swap
operator|.
name|total
operator|=
name|getTotalSwapSpaceSize
argument_list|()
expr_stmt|;
name|swap
operator|.
name|free
operator|=
name|getFreeSwapSpaceSize
argument_list|()
expr_stmt|;
name|stats
operator|.
name|swap
operator|=
name|swap
expr_stmt|;
return|return
name|stats
return|;
block|}
comment|/**      * Returns a given method of the OperatingSystemMXBean,      * or null if the method is not found or unavailable.      */
DECL|method|getMethod
specifier|private
specifier|static
name|Method
name|getMethod
parameter_list|(
name|String
name|methodName
parameter_list|)
block|{
try|try
block|{
return|return
name|Class
operator|.
name|forName
argument_list|(
literal|"com.sun.management.OperatingSystemMXBean"
argument_list|)
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// not available
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

