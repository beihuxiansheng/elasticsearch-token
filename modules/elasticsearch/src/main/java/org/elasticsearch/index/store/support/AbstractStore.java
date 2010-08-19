begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
operator|.
name|support
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
name|*
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
name|Digest
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
name|Hex
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
name|Strings
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
name|collect
operator|.
name|ImmutableMap
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
name|collect
operator|.
name|MapBuilder
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
name|Directories
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
name|InputStreamIndexInput
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
name|ByteSizeValue
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
name|AbstractIndexShardComponent
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
operator|.
name|Store
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
name|StoreFileMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|security
operator|.
name|MessageDigest
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|AbstractStore
specifier|public
specifier|abstract
class|class
name|AbstractStore
extends|extends
name|AbstractIndexShardComponent
implements|implements
name|Store
block|{
DECL|field|indexStore
specifier|protected
specifier|final
name|IndexStore
name|indexStore
decl_stmt|;
DECL|field|filesMetadata
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|StoreFileMetaData
argument_list|>
name|filesMetadata
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|files
specifier|private
specifier|volatile
name|String
index|[]
name|files
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|mutex
specifier|private
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|sync
specifier|private
specifier|final
name|boolean
name|sync
decl_stmt|;
DECL|method|AbstractStore
specifier|protected
name|AbstractStore
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
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexStore
operator|=
name|indexStore
expr_stmt|;
name|this
operator|.
name|sync
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"sync"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|wrapDirectory
specifier|protected
name|Directory
name|wrapDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StoreDirectory
argument_list|(
name|dir
argument_list|)
return|;
block|}
DECL|method|metaData
annotation|@
name|Override
specifier|public
name|StoreFileMetaData
name|metaData
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|StoreFileMetaData
name|md
init|=
name|filesMetadata
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|md
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// IndexOutput not closed, does not exists
if|if
condition|(
name|md
operator|.
name|lastModified
argument_list|()
operator|==
operator|-
literal|1
operator|||
name|md
operator|.
name|sizeInBytes
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|md
return|;
block|}
DECL|method|metaDataWithMd5
annotation|@
name|Override
specifier|public
name|StoreFileMetaData
name|metaDataWithMd5
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|StoreFileMetaData
name|md
init|=
name|metaData
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|md
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|md
operator|.
name|md5
argument_list|()
operator|==
literal|null
condition|)
block|{
name|IndexInput
name|in
init|=
name|directory
argument_list|()
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|md5
decl_stmt|;
try|try
block|{
name|InputStreamIndexInput
name|is
init|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|in
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|md5
operator|=
name|Digest
operator|.
name|md5Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|md
operator|=
name|metaData
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|md
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|md
operator|.
name|md5
argument_list|()
operator|==
literal|null
condition|)
block|{
name|byte
index|[]
name|md5Bytes
init|=
name|Digest
operator|.
name|md5HexToByteArray
argument_list|(
name|md5
argument_list|)
decl_stmt|;
if|if
condition|(
name|shouldWriteMd5
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|IndexOutput
name|output
init|=
name|directory
argument_list|()
operator|.
name|createOutput
argument_list|(
name|name
operator|+
literal|".md5"
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|md5Bytes
argument_list|,
name|md5Bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|md
operator|=
operator|new
name|StoreFileMetaData
argument_list|(
name|md
operator|.
name|name
argument_list|()
argument_list|,
name|md
operator|.
name|sizeInBytes
argument_list|()
argument_list|,
name|md
operator|.
name|sizeInBytes
argument_list|()
argument_list|,
name|md5
argument_list|)
expr_stmt|;
name|filesMetadata
operator|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|(
name|filesMetadata
argument_list|)
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|md
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|md
return|;
block|}
DECL|method|list
annotation|@
name|Override
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|StoreFileMetaData
argument_list|>
name|list
parameter_list|()
throws|throws
name|IOException
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|StoreFileMetaData
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|files
control|)
block|{
name|StoreFileMetaData
name|md
init|=
name|metaData
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|md
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|md
operator|.
name|name
argument_list|()
argument_list|,
name|md
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|listWithMd5
annotation|@
name|Override
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|StoreFileMetaData
argument_list|>
name|listWithMd5
parameter_list|()
throws|throws
name|IOException
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|StoreFileMetaData
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|files
control|)
block|{
name|StoreFileMetaData
name|md
init|=
name|metaDataWithMd5
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|md
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|md
operator|.
name|name
argument_list|()
argument_list|,
name|md
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|deleteContent
annotation|@
name|Override
specifier|public
name|void
name|deleteContent
parameter_list|()
throws|throws
name|IOException
block|{
name|Directories
operator|.
name|deleteFiles
argument_list|(
name|directory
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|fullDelete
annotation|@
name|Override
specifier|public
name|void
name|fullDelete
parameter_list|()
throws|throws
name|IOException
block|{
name|deleteContent
argument_list|()
expr_stmt|;
block|}
DECL|method|estimateSize
annotation|@
name|Override
specifier|public
name|ByteSizeValue
name|estimateSize
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Directories
operator|.
name|estimateSize
argument_list|(
name|directory
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns<tt>true</tt> by default.      */
DECL|method|suggestUseCompoundFile
annotation|@
name|Override
specifier|public
name|boolean
name|suggestUseCompoundFile
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|directory
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|preComputedMd5
specifier|protected
name|String
name|preComputedMd5
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|shouldWriteMd5
specifier|private
name|boolean
name|shouldWriteMd5
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"segments"
argument_list|)
return|;
block|}
comment|/**      * The idea of the store directory is to cache file level meta data, as well as md5 of it      */
DECL|class|StoreDirectory
class|class
name|StoreDirectory
extends|extends
name|Directory
implements|implements
name|ForceSyncDirectory
block|{
DECL|field|delegate
specifier|private
specifier|final
name|Directory
name|delegate
decl_stmt|;
DECL|method|StoreDirectory
name|StoreDirectory
parameter_list|(
name|Directory
name|delegate
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|StoreFileMetaData
argument_list|>
name|builder
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|delegate
operator|.
name|listAll
argument_list|()
control|)
block|{
try|try
block|{
name|String
name|md5
init|=
name|preComputedMd5
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|md5
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|shouldWriteMd5
argument_list|(
name|file
argument_list|)
condition|)
block|{
name|byte
index|[]
name|md5Bytes
init|=
name|Digest
operator|.
name|md5HexToByteArray
argument_list|(
name|md5
argument_list|)
decl_stmt|;
name|IndexOutput
name|output
init|=
name|delegate
operator|.
name|createOutput
argument_list|(
name|file
operator|+
literal|".md5"
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|md5Bytes
argument_list|,
name|md5Bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|put
argument_list|(
name|file
argument_list|,
operator|new
name|StoreFileMetaData
argument_list|(
name|file
argument_list|,
name|delegate
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
argument_list|,
name|delegate
operator|.
name|fileModified
argument_list|(
name|file
argument_list|)
argument_list|,
name|md5
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
name|filesMetadata
operator|=
name|builder
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|files
operator|=
name|filesMetadata
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|filesMetadata
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|listAll
annotation|@
name|Override
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|files
return|;
block|}
DECL|method|fileExists
annotation|@
name|Override
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|filesMetadata
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|fileModified
annotation|@
name|Override
specifier|public
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|StoreFileMetaData
name|metaData
init|=
name|filesMetadata
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|metaData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
comment|// not set yet (IndexOutput not closed)
if|if
condition|(
name|metaData
operator|.
name|lastModified
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|metaData
operator|.
name|lastModified
argument_list|()
return|;
block|}
return|return
name|delegate
operator|.
name|fileModified
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|touchFile
annotation|@
name|Override
specifier|public
name|void
name|touchFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|touchFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|StoreFileMetaData
name|metaData
init|=
name|filesMetadata
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|metaData
operator|!=
literal|null
condition|)
block|{
name|metaData
operator|=
operator|new
name|StoreFileMetaData
argument_list|(
name|metaData
operator|.
name|name
argument_list|()
argument_list|,
name|metaData
operator|.
name|sizeInBytes
argument_list|()
argument_list|,
name|delegate
operator|.
name|fileModified
argument_list|(
name|name
argument_list|)
argument_list|,
name|metaData
operator|.
name|md5
argument_list|()
argument_list|)
expr_stmt|;
name|filesMetadata
operator|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|(
name|filesMetadata
argument_list|)
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|metaData
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|deleteFile
annotation|@
name|Override
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|delegate
operator|.
name|deleteFile
argument_list|(
name|name
operator|+
literal|".md5"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|filesMetadata
operator|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|(
name|filesMetadata
argument_list|)
operator|.
name|remove
argument_list|(
name|name
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|files
operator|=
name|filesMetadata
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|filesMetadata
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fileLength
annotation|@
name|Override
specifier|public
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|StoreFileMetaData
name|metaData
init|=
name|filesMetadata
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|metaData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
comment|// not set yet (IndexOutput not closed)
if|if
condition|(
name|metaData
operator|.
name|sizeInBytes
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|metaData
operator|.
name|sizeInBytes
argument_list|()
return|;
block|}
return|return
name|delegate
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|createOutput
annotation|@
name|Override
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|out
init|=
name|delegate
operator|.
name|createOutput
argument_list|(
name|name
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|StoreFileMetaData
name|metaData
init|=
operator|new
name|StoreFileMetaData
argument_list|(
name|name
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|filesMetadata
operator|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|(
name|filesMetadata
argument_list|)
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|metaData
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|files
operator|=
name|filesMetadata
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|filesMetadata
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|StoreIndexOutput
argument_list|(
name|out
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|openInput
annotation|@
name|Override
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|filesMetadata
operator|=
name|ImmutableMap
operator|.
name|of
argument_list|()
expr_stmt|;
name|files
operator|=
name|Strings
operator|.
name|EMPTY_ARRAY
expr_stmt|;
block|}
block|}
DECL|method|makeLock
annotation|@
name|Override
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|makeLock
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|openInput
annotation|@
name|Override
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|bufferSize
argument_list|)
return|;
block|}
DECL|method|clearLock
annotation|@
name|Override
specifier|public
name|void
name|clearLock
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|clearLock
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|setLockFactory
annotation|@
name|Override
specifier|public
name|void
name|setLockFactory
parameter_list|(
name|LockFactory
name|lockFactory
parameter_list|)
block|{
name|delegate
operator|.
name|setLockFactory
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
block|}
DECL|method|getLockFactory
annotation|@
name|Override
specifier|public
name|LockFactory
name|getLockFactory
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getLockFactory
argument_list|()
return|;
block|}
DECL|method|getLockID
annotation|@
name|Override
specifier|public
name|String
name|getLockID
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getLockID
argument_list|()
return|;
block|}
DECL|method|sync
annotation|@
name|Override
specifier|public
name|void
name|sync
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sync
condition|)
block|{
name|delegate
operator|.
name|sync
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|forceSync
annotation|@
name|Override
specifier|public
name|void
name|forceSync
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|sync
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|StoreIndexOutput
specifier|private
class|class
name|StoreIndexOutput
extends|extends
name|IndexOutput
block|{
DECL|field|delegate
specifier|private
specifier|final
name|IndexOutput
name|delegate
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|digest
specifier|private
specifier|final
name|MessageDigest
name|digest
decl_stmt|;
DECL|field|ignoreDigest
specifier|private
name|boolean
name|ignoreDigest
init|=
literal|false
decl_stmt|;
DECL|method|StoreIndexOutput
specifier|private
name|StoreIndexOutput
parameter_list|(
name|IndexOutput
name|delegate
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|Digest
operator|.
name|getMd5Digest
argument_list|()
expr_stmt|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|StoreFileMetaData
name|md
init|=
name|filesMetadata
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|md5
init|=
name|md
operator|==
literal|null
condition|?
literal|null
else|:
name|md
operator|.
name|md5
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ignoreDigest
condition|)
block|{
name|md5
operator|=
name|Hex
operator|.
name|encodeHexString
argument_list|(
name|digest
operator|.
name|digest
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|md
operator|==
literal|null
condition|)
block|{
name|md
operator|=
operator|new
name|StoreFileMetaData
argument_list|(
name|name
argument_list|,
name|directory
argument_list|()
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
argument_list|,
name|directory
argument_list|()
operator|.
name|fileModified
argument_list|(
name|name
argument_list|)
argument_list|,
name|md5
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|md
operator|=
operator|new
name|StoreFileMetaData
argument_list|(
name|name
argument_list|,
name|directory
argument_list|()
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
argument_list|,
name|directory
argument_list|()
operator|.
name|fileModified
argument_list|(
name|name
argument_list|)
argument_list|,
name|md5
argument_list|)
expr_stmt|;
block|}
name|filesMetadata
operator|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|(
name|filesMetadata
argument_list|)
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|md
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|files
operator|=
name|filesMetadata
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|filesMetadata
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeByte
annotation|@
name|Override
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|writeByte
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|digest
operator|.
name|update
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|writeBytes
annotation|@
name|Override
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|digest
operator|.
name|update
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|copyBytes
annotation|@
name|Override
specifier|public
name|void
name|copyBytes
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|long
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|copyBytes
argument_list|(
name|input
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
block|}
DECL|method|flush
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|getFilePointer
annotation|@
name|Override
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
DECL|method|seek
annotation|@
name|Override
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
comment|// once we seek, digest is not applicable
name|ignoreDigest
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|length
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|length
argument_list|()
return|;
block|}
DECL|method|setLength
annotation|@
name|Override
specifier|public
name|void
name|setLength
parameter_list|(
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|writeStringStringMap
annotation|@
name|Override
specifier|public
name|void
name|writeStringStringMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|writeStringStringMap
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

