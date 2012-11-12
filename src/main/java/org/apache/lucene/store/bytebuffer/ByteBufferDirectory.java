begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.bytebuffer
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|bytebuffer
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|nio
operator|.
name|ByteBuffer
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * A memory based directory that uses {@link java.nio.ByteBuffer} in order to store the directory content.  *<p/>  *<p>The benefit of using {@link java.nio.ByteBuffer} is the fact that it can be stored in "native" memory  * outside of the JVM heap, thus not incurring the GC overhead of large in memory index.  *<p/>  *<p>Each "file" is segmented into one or more byte buffers.  *<p/>  *<p>If constructed with {@link ByteBufferAllocator}, it allows to control the allocation and release of  * byte buffer. For example, custom implementations can include caching of byte buffers.  */
end_comment

begin_class
DECL|class|ByteBufferDirectory
specifier|public
class|class
name|ByteBufferDirectory
extends|extends
name|Directory
block|{
DECL|field|files
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBufferFile
argument_list|>
name|files
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|ByteBufferFile
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|allocator
specifier|private
specifier|final
name|ByteBufferAllocator
name|allocator
decl_stmt|;
DECL|field|internalAllocator
specifier|private
specifier|final
name|boolean
name|internalAllocator
decl_stmt|;
DECL|field|sizeInBytes
specifier|final
name|AtomicLong
name|sizeInBytes
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|/**      * Constructs a new directory using {@link PlainByteBufferAllocator}.      */
DECL|method|ByteBufferDirectory
specifier|public
name|ByteBufferDirectory
parameter_list|()
block|{
name|this
operator|.
name|allocator
operator|=
operator|new
name|PlainByteBufferAllocator
argument_list|(
literal|false
argument_list|,
literal|1024
argument_list|,
literal|1024
operator|*
literal|10
argument_list|)
expr_stmt|;
name|this
operator|.
name|internalAllocator
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|setLockFactory
argument_list|(
operator|new
name|SingleInstanceLockFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// will not happen
block|}
block|}
comment|/**      * Constructs a new byte buffer directory with a custom allocator.      */
DECL|method|ByteBufferDirectory
specifier|public
name|ByteBufferDirectory
parameter_list|(
name|ByteBufferAllocator
name|allocator
parameter_list|)
block|{
name|this
operator|.
name|allocator
operator|=
name|allocator
expr_stmt|;
name|this
operator|.
name|internalAllocator
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|setLockFactory
argument_list|(
operator|new
name|SingleInstanceLockFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// will not happen
block|}
block|}
comment|/**      * Returns the size in bytes of the directory, chunk by buffer size.      */
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
block|{
return|return
name|sizeInBytes
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nothing to do here
block|}
annotation|@
name|Override
DECL|method|listAll
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
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fileExists
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
name|files
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteFile
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
name|ByteBufferFile
name|file
init|=
name|files
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
name|sizeInBytes
operator|.
name|addAndGet
argument_list|(
operator|-
name|file
operator|.
name|sizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fileLength
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
name|ByteBufferFile
name|file
init|=
name|files
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
return|return
name|file
operator|.
name|getLength
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBufferAllocator
operator|.
name|Type
name|allocatorType
init|=
name|ByteBufferAllocator
operator|.
name|Type
operator|.
name|LARGE
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
literal|"segments"
argument_list|)
operator|||
name|name
operator|.
name|endsWith
argument_list|(
literal|".del"
argument_list|)
condition|)
block|{
name|allocatorType
operator|=
name|ByteBufferAllocator
operator|.
name|Type
operator|.
name|SMALL
expr_stmt|;
block|}
name|ByteBufferFileOutput
name|file
init|=
operator|new
name|ByteBufferFileOutput
argument_list|(
name|this
argument_list|,
name|allocator
operator|.
name|sizeInBytes
argument_list|(
name|allocatorType
argument_list|)
argument_list|)
decl_stmt|;
name|ByteBufferFile
name|existing
init|=
name|files
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
name|sizeInBytes
operator|.
name|addAndGet
argument_list|(
operator|-
name|existing
operator|.
name|sizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
name|existing
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ByteBufferIndexOutput
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|allocator
argument_list|,
name|allocatorType
argument_list|,
name|file
argument_list|)
return|;
block|}
DECL|method|closeOutput
name|void
name|closeOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|ByteBufferFileOutput
name|file
parameter_list|)
block|{
comment|// we replace the output file with a read only file, with no sync
name|files
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|ByteBufferFile
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBufferFile
name|file
init|=
name|files
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
return|return
operator|new
name|ByteBufferIndexInput
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|files
init|=
name|listAll
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|internalAllocator
condition|)
block|{
name|allocator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|releaseBuffer
name|void
name|releaseBuffer
parameter_list|(
name|ByteBuffer
name|byteBuffer
parameter_list|)
block|{
name|allocator
operator|.
name|release
argument_list|(
name|byteBuffer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

