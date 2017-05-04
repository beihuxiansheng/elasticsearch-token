begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.env
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|env
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|SuppressForbidden
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
name|io
operator|.
name|PathUtils
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
name|nio
operator|.
name|file
operator|.
name|FileStore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|FileSystemException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|FileAttributeView
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|FileStoreAttributeView
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

begin_comment
comment|/**   * Implementation of FileStore that supports  * additional features, such as SSD detection and better  * filesystem information for the root filesystem.  * @see Environment#getFileStore(Path)  */
end_comment

begin_class
DECL|class|ESFileStore
class|class
name|ESFileStore
extends|extends
name|FileStore
block|{
comment|/** Underlying filestore */
DECL|field|in
specifier|final
name|FileStore
name|in
decl_stmt|;
DECL|field|majorDeviceNumber
specifier|private
name|int
name|majorDeviceNumber
decl_stmt|;
DECL|field|minorDeviceNumber
specifier|private
name|int
name|minorDeviceNumber
decl_stmt|;
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"tries to determine if disk is spinning"
argument_list|)
comment|// TODO: move PathUtils to be package-private here instead of
comment|// public+forbidden api!
DECL|method|ESFileStore
name|ESFileStore
parameter_list|(
specifier|final
name|FileStore
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
if|if
condition|(
name|Constants
operator|.
name|LINUX
condition|)
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|Files
operator|.
name|readAllLines
argument_list|(
name|PathUtils
operator|.
name|get
argument_list|(
literal|"/proc/self/mountinfo"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|line
range|:
name|lines
control|)
block|{
specifier|final
name|String
index|[]
name|fields
init|=
name|line
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|mountPoint
init|=
name|fields
index|[
literal|4
index|]
decl_stmt|;
if|if
condition|(
name|mountPoint
operator|.
name|equals
argument_list|(
name|getMountPointLinux
argument_list|(
name|in
argument_list|)
argument_list|)
condition|)
block|{
specifier|final
name|String
index|[]
name|deviceNumbers
init|=
name|fields
index|[
literal|2
index|]
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|majorDeviceNumber
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|deviceNumbers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|minorDeviceNumber
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|deviceNumbers
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|majorDeviceNumber
operator|=
operator|-
literal|1
expr_stmt|;
name|minorDeviceNumber
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
name|majorDeviceNumber
operator|=
operator|-
literal|1
expr_stmt|;
name|minorDeviceNumber
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
comment|// these are hacks that are not guaranteed
DECL|method|getMountPointLinux
specifier|private
specifier|static
name|String
name|getMountPointLinux
parameter_list|(
specifier|final
name|FileStore
name|store
parameter_list|)
block|{
name|String
name|desc
init|=
name|store
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|desc
operator|.
name|lastIndexOf
argument_list|(
literal|" ("
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|desc
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|desc
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|in
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|in
operator|.
name|type
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isReadOnly
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
name|in
operator|.
name|isReadOnly
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalSpace
specifier|public
name|long
name|getTotalSpace
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|result
init|=
name|in
operator|.
name|getTotalSpace
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|<
literal|0
condition|)
block|{
comment|// see https://bugs.openjdk.java.net/browse/JDK-8162520:
name|result
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getUsableSpace
specifier|public
name|long
name|getUsableSpace
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|result
init|=
name|in
operator|.
name|getUsableSpace
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|<
literal|0
condition|)
block|{
comment|// see https://bugs.openjdk.java.net/browse/JDK-8162520:
name|result
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getUnallocatedSpace
specifier|public
name|long
name|getUnallocatedSpace
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|result
init|=
name|in
operator|.
name|getUnallocatedSpace
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|<
literal|0
condition|)
block|{
comment|// see https://bugs.openjdk.java.net/browse/JDK-8162520:
name|result
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|supportsFileAttributeView
specifier|public
name|boolean
name|supportsFileAttributeView
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|FileAttributeView
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|in
operator|.
name|supportsFileAttributeView
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|supportsFileAttributeView
specifier|public
name|boolean
name|supportsFileAttributeView
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
literal|"lucene"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|in
operator|.
name|supportsFileAttributeView
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFileStoreAttributeView
specifier|public
parameter_list|<
name|V
extends|extends
name|FileStoreAttributeView
parameter_list|>
name|V
name|getFileStoreAttributeView
parameter_list|(
name|Class
argument_list|<
name|V
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|in
operator|.
name|getFileStoreAttributeView
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAttribute
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|attribute
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|attribute
condition|)
block|{
comment|// for the partition
case|case
literal|"lucene:major_device_number"
case|:
return|return
name|majorDeviceNumber
return|;
case|case
literal|"lucene:minor_device_number"
case|:
return|return
name|minorDeviceNumber
return|;
default|default:
return|return
name|in
operator|.
name|getAttribute
argument_list|(
name|attribute
argument_list|)
return|;
block|}
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
name|in
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

