begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|FileSystem
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
name|FileSystemMap
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
name|FileSystemUsage
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
name|Sigar
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
name|SigarException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|SigarFsProbe
specifier|public
class|class
name|SigarFsProbe
extends|extends
name|AbstractComponent
implements|implements
name|FsProbe
block|{
DECL|field|nodeEnv
specifier|private
specifier|final
name|NodeEnvironment
name|nodeEnv
decl_stmt|;
DECL|field|sigarService
specifier|private
specifier|final
name|SigarService
name|sigarService
decl_stmt|;
DECL|field|fileSystems
specifier|private
name|Map
argument_list|<
name|File
argument_list|,
name|FileSystem
argument_list|>
name|fileSystems
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|SigarFsProbe
specifier|public
name|SigarFsProbe
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeEnvironment
name|nodeEnv
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
name|nodeEnv
operator|=
name|nodeEnv
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
DECL|method|stats
specifier|public
specifier|synchronized
name|FsStats
name|stats
parameter_list|()
block|{
if|if
condition|(
operator|!
name|nodeEnv
operator|.
name|hasNodeFile
argument_list|()
condition|)
block|{
return|return
operator|new
name|FsStats
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
operator|new
name|FsStats
operator|.
name|Info
index|[
literal|0
index|]
argument_list|)
return|;
block|}
name|File
index|[]
name|dataLocations
init|=
name|nodeEnv
operator|.
name|nodeDataLocations
argument_list|()
decl_stmt|;
name|FsStats
operator|.
name|Info
index|[]
name|infos
init|=
operator|new
name|FsStats
operator|.
name|Info
index|[
name|dataLocations
operator|.
name|length
index|]
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
name|dataLocations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|dataLocation
init|=
name|dataLocations
index|[
name|i
index|]
decl_stmt|;
name|FsStats
operator|.
name|Info
name|info
init|=
operator|new
name|FsStats
operator|.
name|Info
argument_list|()
decl_stmt|;
name|info
operator|.
name|path
operator|=
name|dataLocation
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
try|try
block|{
name|FileSystem
name|fileSystem
init|=
name|fileSystems
operator|.
name|get
argument_list|(
name|dataLocation
argument_list|)
decl_stmt|;
name|Sigar
name|sigar
init|=
name|sigarService
operator|.
name|sigar
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileSystem
operator|==
literal|null
condition|)
block|{
name|FileSystemMap
name|fileSystemMap
init|=
name|sigar
operator|.
name|getFileSystemMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileSystemMap
operator|!=
literal|null
condition|)
block|{
name|fileSystem
operator|=
name|fileSystemMap
operator|.
name|getMountPoint
argument_list|(
name|dataLocation
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|fileSystems
operator|.
name|put
argument_list|(
name|dataLocation
argument_list|,
name|fileSystem
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fileSystem
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|mount
operator|=
name|fileSystem
operator|.
name|getDirName
argument_list|()
expr_stmt|;
name|info
operator|.
name|dev
operator|=
name|fileSystem
operator|.
name|getDevName
argument_list|()
expr_stmt|;
name|FileSystemUsage
name|fileSystemUsage
init|=
name|sigar
operator|.
name|getFileSystemUsage
argument_list|(
name|fileSystem
operator|.
name|getDirName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileSystemUsage
operator|!=
literal|null
condition|)
block|{
comment|// total/free/available seem to be in megabytes?
name|info
operator|.
name|total
operator|=
name|fileSystemUsage
operator|.
name|getTotal
argument_list|()
operator|*
literal|1024
expr_stmt|;
name|info
operator|.
name|free
operator|=
name|fileSystemUsage
operator|.
name|getFree
argument_list|()
operator|*
literal|1024
expr_stmt|;
name|info
operator|.
name|available
operator|=
name|fileSystemUsage
operator|.
name|getAvail
argument_list|()
operator|*
literal|1024
expr_stmt|;
name|info
operator|.
name|diskReads
operator|=
name|fileSystemUsage
operator|.
name|getDiskReads
argument_list|()
expr_stmt|;
name|info
operator|.
name|diskWrites
operator|=
name|fileSystemUsage
operator|.
name|getDiskWrites
argument_list|()
expr_stmt|;
name|info
operator|.
name|diskReadBytes
operator|=
name|fileSystemUsage
operator|.
name|getDiskReadBytes
argument_list|()
expr_stmt|;
name|info
operator|.
name|diskWriteBytes
operator|=
name|fileSystemUsage
operator|.
name|getDiskWriteBytes
argument_list|()
expr_stmt|;
name|info
operator|.
name|diskQueue
operator|=
name|fileSystemUsage
operator|.
name|getDiskQueue
argument_list|()
expr_stmt|;
name|info
operator|.
name|diskServiceTime
operator|=
name|fileSystemUsage
operator|.
name|getDiskServiceTime
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SigarException
name|e
parameter_list|)
block|{
comment|// failed...
block|}
name|infos
index|[
name|i
index|]
operator|=
name|info
expr_stmt|;
block|}
return|return
operator|new
name|FsStats
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|infos
argument_list|)
return|;
block|}
block|}
end_class

end_unit

