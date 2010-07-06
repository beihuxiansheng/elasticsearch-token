begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store.fs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
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
name|lucene
operator|.
name|store
operator|.
name|Directory
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
name|store
operator|.
name|FSDirectory
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
name|store
operator|.
name|LockFactory
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
name|store
operator|.
name|NIOFSDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cache
operator|.
name|memory
operator|.
name|ByteBufferCache
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
name|lucene
operator|.
name|store
operator|.
name|SwitchDirectory
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
name|index
operator|.
name|settings
operator|.
name|IndexSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|ShardId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
operator|.
name|IndexStore
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|NioFsStore
specifier|public
class|class
name|NioFsStore
extends|extends
name|FsStore
block|{
DECL|field|fsDirectory
specifier|private
specifier|final
name|NIOFSDirectory
name|fsDirectory
decl_stmt|;
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|suggestUseCompoundFile
specifier|private
specifier|final
name|boolean
name|suggestUseCompoundFile
decl_stmt|;
DECL|method|NioFsStore
annotation|@
name|Inject
specifier|public
name|NioFsStore
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|IndexStore
name|indexStore
parameter_list|,
name|ByteBufferCache
name|byteBufferCache
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|,
name|indexStore
argument_list|)
expr_stmt|;
name|LockFactory
name|lockFactory
init|=
name|buildLockFactory
argument_list|()
decl_stmt|;
name|File
name|location
init|=
operator|(
operator|(
name|FsIndexStore
operator|)
name|indexStore
operator|)
operator|.
name|shardIndexLocation
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
name|location
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|this
operator|.
name|fsDirectory
operator|=
operator|new
name|NIOFSDirectory
argument_list|(
name|location
argument_list|,
name|lockFactory
argument_list|)
expr_stmt|;
name|SwitchDirectory
name|switchDirectory
init|=
name|buildSwitchDirectoryIfNeeded
argument_list|(
name|fsDirectory
argument_list|,
name|byteBufferCache
argument_list|)
decl_stmt|;
if|if
condition|(
name|switchDirectory
operator|!=
literal|null
condition|)
block|{
name|suggestUseCompoundFile
operator|=
literal|false
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Using [nio_fs] Store with path [{}], cache [true] with extensions [{}]"
argument_list|,
name|fsDirectory
operator|.
name|getFile
argument_list|()
argument_list|,
name|switchDirectory
operator|.
name|primaryExtensions
argument_list|()
argument_list|)
expr_stmt|;
name|directory
operator|=
name|wrapDirectory
argument_list|(
name|switchDirectory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|suggestUseCompoundFile
operator|=
literal|true
expr_stmt|;
name|directory
operator|=
name|wrapDirectory
argument_list|(
name|fsDirectory
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Using [nio_fs] Store with path [{}]"
argument_list|,
name|fsDirectory
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fsDirectory
annotation|@
name|Override
specifier|public
name|FSDirectory
name|fsDirectory
parameter_list|()
block|{
return|return
name|fsDirectory
return|;
block|}
DECL|method|directory
annotation|@
name|Override
specifier|public
name|Directory
name|directory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
DECL|method|suggestUseCompoundFile
annotation|@
name|Override
specifier|public
name|boolean
name|suggestUseCompoundFile
parameter_list|()
block|{
return|return
name|suggestUseCompoundFile
return|;
block|}
block|}
end_class

end_unit

