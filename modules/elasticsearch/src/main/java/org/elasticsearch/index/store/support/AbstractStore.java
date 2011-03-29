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
name|Unicode
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
name|util
operator|.
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Adler32
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Checksum
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
DECL|field|CHECKSUMS_PREFIX
specifier|static
specifier|final
name|String
name|CHECKSUMS_PREFIX
init|=
literal|"_checksums-"
decl_stmt|;
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
literal|true
argument_list|)
expr_stmt|;
comment|// TODO we don't really need to fsync when using shared gateway...
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
DECL|method|metaData
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
name|length
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
name|String
index|[]
name|files
init|=
name|directory
argument_list|()
operator|.
name|listAll
argument_list|()
decl_stmt|;
name|IOException
name|lastException
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|file
operator|.
name|startsWith
argument_list|(
name|CHECKSUMS_PREFIX
argument_list|)
condition|)
block|{
operator|(
operator|(
name|StoreDirectory
operator|)
name|directory
argument_list|()
operator|)
operator|.
name|deleteFileChecksum
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|directory
argument_list|()
operator|.
name|deleteFile
argument_list|(
name|file
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|lastException
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|lastException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|lastException
throw|;
block|}
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
DECL|method|readChecksums
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|readChecksums
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|lastFound
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
name|CHECKSUMS_PREFIX
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|long
name|current
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|CHECKSUMS_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|>
name|lastFound
condition|)
block|{
name|lastFound
operator|=
name|current
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lastFound
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
name|IndexInput
name|indexInput
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|CHECKSUMS_PREFIX
operator|+
name|lastFound
argument_list|)
decl_stmt|;
try|try
block|{
name|indexInput
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// version
return|return
name|indexInput
operator|.
name|readStringStringMap
argument_list|()
return|;
block|}
finally|finally
block|{
name|indexInput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writeChecksums
specifier|public
name|void
name|writeChecksums
parameter_list|()
throws|throws
name|IOException
block|{
name|writeChecksums
argument_list|(
operator|(
name|StoreDirectory
operator|)
name|directory
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|writeChecksums
specifier|private
name|void
name|writeChecksums
parameter_list|(
name|StoreDirectory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|checksumName
init|=
name|CHECKSUMS_PREFIX
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|StoreFileMetaData
argument_list|>
name|files
init|=
name|list
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|checksums
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|StoreFileMetaData
name|metaData
range|:
name|files
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|metaData
operator|.
name|checksum
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|checksums
operator|.
name|put
argument_list|(
name|metaData
operator|.
name|name
argument_list|()
argument_list|,
name|metaData
operator|.
name|checksum
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|checksumName
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// version
name|output
operator|.
name|writeStringStringMap
argument_list|(
name|checksums
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|StoreFileMetaData
name|metaData
range|:
name|files
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|metaData
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
name|CHECKSUMS_PREFIX
argument_list|)
operator|&&
operator|!
name|checksumName
operator|.
name|equals
argument_list|(
name|metaData
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|dir
operator|.
name|deleteFileChecksum
argument_list|(
name|metaData
operator|.
name|name
argument_list|()
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
block|}
block|}
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
DECL|method|createOutputWithNoChecksum
annotation|@
name|Override
specifier|public
name|IndexOutput
name|createOutputWithNoChecksum
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|StoreDirectory
operator|)
name|directory
argument_list|()
operator|)
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|writeChecksum
annotation|@
name|Override
specifier|public
name|void
name|writeChecksum
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|checksum
parameter_list|)
throws|throws
name|IOException
block|{
comment|// update the metadata to include the checksum and write a new checksums file
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
name|length
argument_list|()
argument_list|,
name|metaData
operator|.
name|lastModified
argument_list|()
argument_list|,
name|checksum
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
name|writeChecksums
argument_list|()
expr_stmt|;
block|}
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
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|checksums
init|=
name|readChecksums
argument_list|(
name|delegate
argument_list|)
decl_stmt|;
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
comment|// BACKWARD CKS SUPPORT
if|if
condition|(
name|file
operator|.
name|endsWith
argument_list|(
literal|".cks"
argument_list|)
condition|)
block|{
comment|// ignore checksum files here
continue|continue;
block|}
name|String
name|checksum
init|=
name|checksums
operator|.
name|get
argument_list|(
name|file
argument_list|)
decl_stmt|;
comment|// BACKWARD CKS SUPPORT
if|if
condition|(
name|checksum
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|delegate
operator|.
name|fileExists
argument_list|(
name|file
operator|+
literal|".cks"
argument_list|)
condition|)
block|{
name|IndexInput
name|indexInput
init|=
name|delegate
operator|.
name|openInput
argument_list|(
name|file
operator|+
literal|".cks"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|indexInput
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|checksumBytes
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|indexInput
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|indexInput
operator|.
name|readBytes
argument_list|(
name|checksumBytes
argument_list|,
literal|0
argument_list|,
name|checksumBytes
operator|.
name|length
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checksum
operator|=
name|Unicode
operator|.
name|fromBytes
argument_list|(
name|checksumBytes
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|indexInput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|checksum
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|method|delegate
specifier|public
name|Directory
name|delegate
parameter_list|()
block|{
return|return
name|delegate
return|;
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
name|length
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
name|checksum
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
DECL|method|deleteFileChecksum
specifier|public
name|void
name|deleteFileChecksum
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
comment|// we don't allow to delete the checksums files, only using the deleteChecksum method
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|CHECKSUMS_PREFIX
argument_list|)
condition|)
block|{
return|return;
block|}
name|delegate
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
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
name|length
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|metaData
operator|.
name|length
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
return|return
name|createOutput
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|computeChecksum
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
argument_list|,
name|computeChecksum
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
comment|// write the checksums file when we sync on the segments file (committed)
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"segments.gen"
argument_list|)
operator|&&
name|name
operator|.
name|startsWith
argument_list|(
literal|"segments"
argument_list|)
condition|)
block|{
name|writeChecksums
argument_list|()
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
name|Checksum
name|digest
decl_stmt|;
DECL|method|StoreIndexOutput
name|StoreIndexOutput
parameter_list|(
name|IndexOutput
name|delegate
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|computeChecksum
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
if|if
condition|(
name|computeChecksum
condition|)
block|{
if|if
condition|(
literal|"segments.gen"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// no need to create checksum for segments.gen since its not snapshot to recovery
name|this
operator|.
name|digest
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"segments"
argument_list|)
condition|)
block|{
comment|// don't compute checksum for segments files, so pure Lucene can open this directory
comment|// and since we, in any case, always recover the segments files
name|this
operator|.
name|digest
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|//                    this.digest = new CRC32();
comment|// adler is faster, and we compare on length as well, should be enough to check for difference
comment|// between files
name|this
operator|.
name|digest
operator|=
operator|new
name|Adler32
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|digest
operator|=
literal|null
expr_stmt|;
block|}
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
name|String
name|checksum
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|digest
operator|!=
literal|null
condition|)
block|{
name|checksum
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|digest
operator|.
name|getValue
argument_list|()
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|StoreFileMetaData
name|md
init|=
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
name|checksum
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
if|if
condition|(
name|digest
operator|!=
literal|null
condition|)
block|{
name|digest
operator|.
name|update
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|digest
operator|!=
literal|null
condition|)
block|{
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
block|}
comment|// don't override it, base class method simple reads from input and writes to this output
comment|//        @Override public void copyBytes(IndexInput input, long numBytes) throws IOException {
comment|//            delegate.copyBytes(input, numBytes);
comment|//        }
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
comment|// seek might be called on files, which means that the checksum is not file checksum
comment|// but a checksum of the bytes written to this stream, which is the same for each
comment|// type of file in lucene
name|delegate
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
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

