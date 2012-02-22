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
name|ArrayList
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
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ByteBufferFile
specifier|public
class|class
name|ByteBufferFile
block|{
DECL|field|dir
specifier|final
name|ByteBufferDirectory
name|dir
decl_stmt|;
DECL|field|bufferSize
specifier|final
name|int
name|bufferSize
decl_stmt|;
DECL|field|buffers
specifier|final
name|List
argument_list|<
name|ByteBuffer
argument_list|>
name|buffers
decl_stmt|;
DECL|field|length
name|long
name|length
decl_stmt|;
DECL|field|lastModified
specifier|volatile
name|long
name|lastModified
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|refCount
specifier|final
name|AtomicInteger
name|refCount
decl_stmt|;
DECL|field|sizeInBytes
name|long
name|sizeInBytes
decl_stmt|;
DECL|method|ByteBufferFile
specifier|public
name|ByteBufferFile
parameter_list|(
name|ByteBufferDirectory
name|dir
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
name|this
operator|.
name|buffers
operator|=
operator|new
name|ArrayList
argument_list|<
name|ByteBuffer
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|refCount
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|ByteBufferFile
name|ByteBufferFile
parameter_list|(
name|ByteBufferFile
name|file
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|file
operator|.
name|dir
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|file
operator|.
name|bufferSize
expr_stmt|;
name|this
operator|.
name|buffers
operator|=
name|file
operator|.
name|buffers
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|file
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|lastModified
operator|=
name|file
operator|.
name|lastModified
expr_stmt|;
name|this
operator|.
name|refCount
operator|=
name|file
operator|.
name|refCount
expr_stmt|;
name|this
operator|.
name|sizeInBytes
operator|=
name|file
operator|.
name|sizeInBytes
expr_stmt|;
block|}
DECL|method|getLength
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|getLastModified
specifier|public
name|long
name|getLastModified
parameter_list|()
block|{
return|return
name|lastModified
return|;
block|}
DECL|method|setLastModified
name|void
name|setLastModified
parameter_list|(
name|long
name|lastModified
parameter_list|)
block|{
name|this
operator|.
name|lastModified
operator|=
name|lastModified
expr_stmt|;
block|}
DECL|method|sizeInBytes
name|long
name|sizeInBytes
parameter_list|()
block|{
return|return
name|sizeInBytes
return|;
block|}
DECL|method|getBuffer
name|ByteBuffer
name|getBuffer
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|buffers
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|method|numBuffers
name|int
name|numBuffers
parameter_list|()
block|{
return|return
name|buffers
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|delete
name|void
name|delete
parameter_list|()
block|{
name|decRef
argument_list|()
expr_stmt|;
block|}
DECL|method|incRef
name|void
name|incRef
parameter_list|()
block|{
name|refCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|decRef
name|void
name|decRef
parameter_list|()
block|{
if|if
condition|(
name|refCount
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|length
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|ByteBuffer
name|buffer
range|:
name|buffers
control|)
block|{
name|dir
operator|.
name|releaseBuffer
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
name|buffers
operator|.
name|clear
argument_list|()
expr_stmt|;
name|sizeInBytes
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

