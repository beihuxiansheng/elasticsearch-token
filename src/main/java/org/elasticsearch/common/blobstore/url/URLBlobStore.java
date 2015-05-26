begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.blobstore.url
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|blobstore
operator|.
name|url
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
name|blobstore
operator|.
name|BlobContainer
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
name|BlobStore
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
name|BlobStoreException
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
name|ByteSizeUnit
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
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_comment
comment|/**  * Read-only URL-based blob store  */
end_comment

begin_class
DECL|class|URLBlobStore
specifier|public
class|class
name|URLBlobStore
extends|extends
name|AbstractComponent
implements|implements
name|BlobStore
block|{
DECL|field|path
specifier|private
specifier|final
name|URL
name|path
decl_stmt|;
DECL|field|bufferSizeInBytes
specifier|private
specifier|final
name|int
name|bufferSizeInBytes
decl_stmt|;
comment|/**      * Constructs new read-only URL-based blob store      *<p/>      * The following settings are supported      *<dl>      *<dt>buffer_size</dt>      *<dd>- size of the read buffer, defaults to 100KB</dd>      *</dl>      *      * @param settings settings      * @param path     base URL      */
DECL|method|URLBlobStore
specifier|public
name|URLBlobStore
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|URL
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|bufferSizeInBytes
operator|=
operator|(
name|int
operator|)
name|settings
operator|.
name|getAsBytesSize
argument_list|(
literal|"repositories.uri.buffer_size"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|100
argument_list|,
name|ByteSizeUnit
operator|.
name|KB
argument_list|)
argument_list|)
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|path
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns base URL      *      * @return base URL      */
DECL|method|path
specifier|public
name|URL
name|path
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**      * Returns read buffer size      *      * @return read buffer size      */
DECL|method|bufferSizeInBytes
specifier|public
name|int
name|bufferSizeInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|bufferSizeInBytes
return|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
DECL|method|blobContainer
specifier|public
name|BlobContainer
name|blobContainer
parameter_list|(
name|BlobPath
name|path
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|URLBlobContainer
argument_list|(
name|this
argument_list|,
name|path
argument_list|,
name|buildPath
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|BlobStoreException
argument_list|(
literal|"malformed URL "
operator|+
name|path
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**      * This operation is not supported by URL Blob Store      *      * @param path      */
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|BlobPath
name|path
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"URL repository is read only"
argument_list|)
throw|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// nothing to do here...
block|}
comment|/**      * Builds URL using base URL and specified path      *      * @param path relative path      * @return Base URL + path      * @throws MalformedURLException      */
DECL|method|buildPath
specifier|private
name|URL
name|buildPath
parameter_list|(
name|BlobPath
name|path
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|String
index|[]
name|paths
init|=
name|path
operator|.
name|toArray
argument_list|()
decl_stmt|;
if|if
condition|(
name|paths
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|path
argument_list|()
return|;
block|}
name|URL
name|blobPath
init|=
operator|new
name|URL
argument_list|(
name|this
operator|.
name|path
argument_list|,
name|paths
index|[
literal|0
index|]
operator|+
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|paths
operator|.
name|length
operator|>
literal|1
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|paths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|blobPath
operator|=
operator|new
name|URL
argument_list|(
name|blobPath
argument_list|,
name|paths
index|[
name|i
index|]
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|blobPath
return|;
block|}
block|}
end_class

end_unit

