begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.translog.fs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|FileChannelInputStream
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
name|stream
operator|.
name|BytesStreamInput
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
name|translog
operator|.
name|Translog
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
name|translog
operator|.
name|TranslogStreams
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
name|io
operator|.
name|InputStream
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
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|FsChannelSnapshot
specifier|public
class|class
name|FsChannelSnapshot
implements|implements
name|Translog
operator|.
name|Snapshot
block|{
DECL|field|id
specifier|private
specifier|final
name|long
name|id
decl_stmt|;
DECL|field|totalOperations
specifier|private
specifier|final
name|int
name|totalOperations
decl_stmt|;
DECL|field|raf
specifier|private
specifier|final
name|RafReference
name|raf
decl_stmt|;
DECL|field|channel
specifier|private
specifier|final
name|FileChannel
name|channel
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|lastOperationRead
specifier|private
name|Translog
operator|.
name|Operation
name|lastOperationRead
init|=
literal|null
decl_stmt|;
DECL|field|position
specifier|private
name|int
name|position
init|=
literal|0
decl_stmt|;
DECL|field|cacheBuffer
specifier|private
name|ByteBuffer
name|cacheBuffer
decl_stmt|;
DECL|method|FsChannelSnapshot
specifier|public
name|FsChannelSnapshot
parameter_list|(
name|long
name|id
parameter_list|,
name|RafReference
name|raf
parameter_list|,
name|long
name|length
parameter_list|,
name|int
name|totalOperations
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|raf
operator|=
name|raf
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|raf
operator|.
name|raf
argument_list|()
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|totalOperations
operator|=
name|totalOperations
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|translogId
specifier|public
name|long
name|translogId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|position
specifier|public
name|long
name|position
parameter_list|()
block|{
return|return
name|this
operator|.
name|position
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|this
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|estimatedTotalOperations
specifier|public
name|int
name|estimatedTotalOperations
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalOperations
return|;
block|}
annotation|@
name|Override
DECL|method|stream
specifier|public
name|InputStream
name|stream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|FileChannelInputStream
argument_list|(
name|channel
argument_list|,
name|position
argument_list|,
name|lengthInBytes
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|lengthInBytes
specifier|public
name|long
name|lengthInBytes
parameter_list|()
block|{
return|return
name|length
operator|-
name|position
return|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|position
operator|>
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|cacheBuffer
operator|==
literal|null
condition|)
block|{
name|cacheBuffer
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
block|}
name|cacheBuffer
operator|.
name|limit
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|int
name|bytesRead
init|=
name|channel
operator|.
name|read
argument_list|(
name|cacheBuffer
argument_list|,
name|position
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytesRead
operator|<
literal|4
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cacheBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|int
name|opSize
init|=
name|cacheBuffer
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|position
operator|+=
literal|4
expr_stmt|;
if|if
condition|(
operator|(
name|position
operator|+
name|opSize
operator|)
operator|>
name|length
condition|)
block|{
comment|// restore the position to before we read the opSize
name|position
operator|-=
literal|4
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|cacheBuffer
operator|.
name|capacity
argument_list|()
operator|<
name|opSize
condition|)
block|{
name|cacheBuffer
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|opSize
argument_list|)
expr_stmt|;
block|}
name|cacheBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cacheBuffer
operator|.
name|limit
argument_list|(
name|opSize
argument_list|)
expr_stmt|;
name|channel
operator|.
name|read
argument_list|(
name|cacheBuffer
argument_list|,
name|position
argument_list|)
expr_stmt|;
name|cacheBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|position
operator|+=
name|opSize
expr_stmt|;
name|lastOperationRead
operator|=
name|TranslogStreams
operator|.
name|readTranslogOperation
argument_list|(
operator|new
name|BytesStreamInput
argument_list|(
name|cacheBuffer
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|opSize
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|Translog
operator|.
name|Operation
name|next
parameter_list|()
block|{
return|return
name|this
operator|.
name|lastOperationRead
return|;
block|}
annotation|@
name|Override
DECL|method|seekForward
specifier|public
name|void
name|seekForward
parameter_list|(
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|position
operator|+=
name|length
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|boolean
name|release
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
name|raf
operator|.
name|decreaseRefCount
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

