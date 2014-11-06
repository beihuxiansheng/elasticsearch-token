begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.blobstore.fs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|blobstore
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
name|ImmutableMap
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
name|ElasticsearchIllegalStateException
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
name|blobstore
operator|.
name|BlobMetaData
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
name|blobstore
operator|.
name|BlobPath
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
name|blobstore
operator|.
name|support
operator|.
name|AbstractBlobContainer
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
name|blobstore
operator|.
name|support
operator|.
name|PlainBlobMetaData
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
name|io
operator|.
name|FileSystemUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|FsBlobContainer
specifier|public
class|class
name|FsBlobContainer
extends|extends
name|AbstractBlobContainer
block|{
DECL|field|blobStore
specifier|protected
specifier|final
name|FsBlobStore
name|blobStore
decl_stmt|;
DECL|field|path
specifier|protected
specifier|final
name|File
name|path
decl_stmt|;
DECL|method|FsBlobContainer
specifier|public
name|FsBlobContainer
parameter_list|(
name|FsBlobStore
name|blobStore
parameter_list|,
name|BlobPath
name|blobPath
parameter_list|,
name|File
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|blobPath
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
DECL|method|filePath
specifier|public
name|File
name|filePath
parameter_list|()
block|{
return|return
name|this
operator|.
name|path
return|;
block|}
DECL|method|listBlobs
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|BlobMetaData
argument_list|>
name|listBlobs
parameter_list|()
throws|throws
name|IOException
block|{
name|File
index|[]
name|files
init|=
name|path
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
operator|||
name|files
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
comment|// using MapBuilder and not ImmutableMap.Builder as it seems like File#listFiles might return duplicate files!
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|BlobMetaData
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
name|File
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|PlainBlobMetaData
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|,
name|file
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|immutableMap
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|deleteBlob
specifier|public
name|void
name|deleteBlob
parameter_list|(
name|String
name|blobName
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|blobPath
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|,
name|blobName
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|blobPath
argument_list|)
condition|)
block|{
name|Files
operator|.
name|delete
argument_list|(
name|blobPath
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|blobExists
specifier|public
name|boolean
name|blobExists
parameter_list|(
name|String
name|blobName
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|path
argument_list|,
name|blobName
argument_list|)
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|InputStream
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|)
argument_list|,
name|blobStore
operator|.
name|bufferSizeInBytes
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|OutputStream
name|createOutput
parameter_list|(
name|String
name|blobName
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|,
name|blobName
argument_list|)
decl_stmt|;
return|return
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FilterOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|fsync
argument_list|(
name|file
operator|.
name|toPath
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|fsync
argument_list|(
name|path
operator|.
name|toPath
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|blobStore
operator|.
name|bufferSizeInBytes
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

