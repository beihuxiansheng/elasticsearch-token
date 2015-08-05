begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.blobstore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|blobstore
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
comment|/**  *  */
end_comment

begin_interface
DECL|interface|BlobContainer
specifier|public
interface|interface
name|BlobContainer
block|{
DECL|method|path
name|BlobPath
name|path
parameter_list|()
function_decl|;
DECL|method|blobExists
name|boolean
name|blobExists
parameter_list|(
name|String
name|blobName
parameter_list|)
function_decl|;
comment|/**      * Creates a new {@link InputStream} for the given blob name      */
DECL|method|openInput
name|InputStream
name|openInput
parameter_list|(
name|String
name|blobName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a new OutputStream for the given blob name      */
DECL|method|createOutput
name|OutputStream
name|createOutput
parameter_list|(
name|String
name|blobName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Deletes a blob with giving name.      *      * If a blob exists but cannot be deleted an exception has to be thrown.      */
DECL|method|deleteBlob
name|void
name|deleteBlob
parameter_list|(
name|String
name|blobName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Deletes blobs with giving names.      *      * If a blob exists but cannot be deleted an exception has to be thrown.      */
DECL|method|deleteBlobs
name|void
name|deleteBlobs
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|blobNames
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Deletes all blobs in the container that match the specified prefix.      */
DECL|method|deleteBlobsByPrefix
name|void
name|deleteBlobsByPrefix
parameter_list|(
name|String
name|blobNamePrefix
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Lists all blobs in the container      */
DECL|method|listBlobs
name|Map
argument_list|<
name|String
argument_list|,
name|BlobMetaData
argument_list|>
name|listBlobs
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Lists all blobs in the container that match specified prefix      */
DECL|method|listBlobsByPrefix
name|Map
argument_list|<
name|String
argument_list|,
name|BlobMetaData
argument_list|>
name|listBlobsByPrefix
parameter_list|(
name|String
name|blobNamePrefix
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Atomically renames source blob into target blob      */
DECL|method|move
name|void
name|move
parameter_list|(
name|String
name|sourceBlobName
parameter_list|,
name|String
name|targetBlobName
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

