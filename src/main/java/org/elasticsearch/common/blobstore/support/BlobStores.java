begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.blobstore.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|blobstore
operator|.
name|support
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
name|ImmutableBlobContainer
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
name|InterruptedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|BlobStores
specifier|public
class|class
name|BlobStores
block|{
DECL|method|syncWriteBlob
specifier|public
specifier|static
name|void
name|syncWriteBlob
parameter_list|(
name|ImmutableBlobContainer
name|blobContainer
parameter_list|,
name|String
name|blobName
parameter_list|,
name|InputStream
name|is
parameter_list|,
name|long
name|sizeInBytes
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|failure
init|=
operator|new
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
name|blobContainer
operator|.
name|writeBlob
argument_list|(
name|blobName
argument_list|,
name|is
argument_list|,
name|sizeInBytes
argument_list|,
operator|new
name|ImmutableBlobContainer
operator|.
name|WriterListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onCompleted
parameter_list|()
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|failure
operator|.
name|set
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|(
literal|"Interrupted while waiting to write ["
operator|+
name|blobName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|failure
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|failure
operator|.
name|get
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|failure
operator|.
name|get
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to get ["
operator|+
name|blobName
operator|+
literal|"]"
argument_list|,
name|failure
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

