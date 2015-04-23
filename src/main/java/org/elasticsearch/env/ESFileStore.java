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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
comment|/** Cached result of Lucene's {@code IOUtils.spins} on path. */
DECL|field|spins
specifier|final
name|Boolean
name|spins
decl_stmt|;
DECL|method|ESFileStore
name|ESFileStore
parameter_list|(
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
name|Boolean
name|spins
decl_stmt|;
comment|// Lucene's IOUtils.spins only works on Linux today:
if|if
condition|(
name|Constants
operator|.
name|LINUX
condition|)
block|{
try|try
block|{
name|spins
operator|=
name|IOUtils
operator|.
name|spins
argument_list|(
name|PathUtils
operator|.
name|get
argument_list|(
name|getMountPointLinux
argument_list|(
name|in
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|spins
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|spins
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|spins
operator|=
name|spins
expr_stmt|;
block|}
comment|// these are hacks that are not guaranteed
DECL|method|getMountPointLinux
specifier|private
specifier|static
name|String
name|getMountPointLinux
parameter_list|(
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
comment|/** Files.getFileStore(Path) useless here!  Don't complain, just try it yourself. */
DECL|method|getMatchingFileStore
specifier|static
name|FileStore
name|getMatchingFileStore
parameter_list|(
name|Path
name|path
parameter_list|,
name|FileStore
name|fileStores
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStore
name|store
init|=
name|Files
operator|.
name|getFileStore
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
return|return
name|store
return|;
comment|// be defensive, don't even try to do anything fancy.
block|}
try|try
block|{
name|String
name|mount
init|=
name|getMountPointLinux
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|FileStore
name|sameMountPoint
init|=
literal|null
decl_stmt|;
for|for
control|(
name|FileStore
name|fs
range|:
name|fileStores
control|)
block|{
if|if
condition|(
name|mount
operator|.
name|equals
argument_list|(
name|getMountPointLinux
argument_list|(
name|fs
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|sameMountPoint
operator|==
literal|null
condition|)
block|{
name|sameMountPoint
operator|=
name|fs
expr_stmt|;
block|}
else|else
block|{
comment|// more than one filesystem has the same mount point; something is wrong!
comment|// fall back to crappy one we got from Files.getFileStore
return|return
name|store
return|;
block|}
block|}
block|}
if|if
condition|(
name|sameMountPoint
operator|!=
literal|null
condition|)
block|{
comment|// ok, we found only one, use it:
return|return
name|sameMountPoint
return|;
block|}
else|else
block|{
comment|// fall back to crappy one we got from Files.getFileStore
return|return
name|store
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
comment|// fall back to crappy one we got from Files.getFileStore
return|return
name|store
return|;
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
return|return
name|in
operator|.
name|getTotalSpace
argument_list|()
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
return|return
name|in
operator|.
name|getUsableSpace
argument_list|()
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
return|return
name|in
operator|.
name|getUnallocatedSpace
argument_list|()
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
if|if
condition|(
literal|"lucene:spins"
operator|.
name|equals
argument_list|(
name|attribute
argument_list|)
condition|)
block|{
return|return
name|spins
return|;
block|}
else|else
block|{
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

