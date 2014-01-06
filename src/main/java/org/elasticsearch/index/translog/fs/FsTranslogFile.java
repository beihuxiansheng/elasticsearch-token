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
name|ElasticsearchIllegalArgumentException
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
name|TranslogException
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

begin_interface
DECL|interface|FsTranslogFile
specifier|public
interface|interface
name|FsTranslogFile
block|{
DECL|enum|Type
specifier|public
specifier|static
enum|enum
name|Type
block|{
DECL|method|SIMPLE
DECL|method|SIMPLE
name|SIMPLE
parameter_list|()
block|{
annotation|@
name|Override
specifier|public
name|FsTranslogFile
name|create
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|long
name|id
parameter_list|,
name|RafReference
name|raf
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimpleFsTranslogFile
argument_list|(
name|shardId
argument_list|,
name|id
argument_list|,
name|raf
argument_list|)
return|;
block|}
block|}
block|,
DECL|method|BUFFERED
DECL|method|BUFFERED
name|BUFFERED
parameter_list|()
block|{
annotation|@
name|Override
specifier|public
name|FsTranslogFile
name|create
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|long
name|id
parameter_list|,
name|RafReference
name|raf
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BufferingFsTranslogFile
argument_list|(
name|shardId
argument_list|,
name|id
argument_list|,
name|raf
argument_list|,
name|bufferSize
argument_list|)
return|;
block|}
block|}
block|;
DECL|method|create
specifier|public
specifier|abstract
name|FsTranslogFile
name|create
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|long
name|id
parameter_list|,
name|RafReference
name|raf
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|fromString
specifier|public
specifier|static
name|Type
name|fromString
parameter_list|(
name|String
name|type
parameter_list|)
throws|throws
name|ElasticsearchIllegalArgumentException
block|{
if|if
condition|(
name|SIMPLE
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|SIMPLE
return|;
block|}
elseif|else
if|if
condition|(
name|BUFFERED
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|BUFFERED
return|;
block|}
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"No translog fs type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|id
name|long
name|id
parameter_list|()
function_decl|;
DECL|method|estimatedNumberOfOperations
name|int
name|estimatedNumberOfOperations
parameter_list|()
function_decl|;
DECL|method|translogSizeInBytes
name|long
name|translogSizeInBytes
parameter_list|()
function_decl|;
DECL|method|add
name|Translog
operator|.
name|Location
name|add
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|read
name|byte
index|[]
name|read
parameter_list|(
name|Translog
operator|.
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|close
name|void
name|close
parameter_list|(
name|boolean
name|delete
parameter_list|)
throws|throws
name|TranslogException
function_decl|;
DECL|method|snapshot
name|FsChannelSnapshot
name|snapshot
parameter_list|()
throws|throws
name|TranslogException
function_decl|;
DECL|method|reuse
name|void
name|reuse
parameter_list|(
name|FsTranslogFile
name|other
parameter_list|)
throws|throws
name|TranslogException
function_decl|;
DECL|method|updateBufferSize
name|void
name|updateBufferSize
parameter_list|(
name|int
name|bufferSize
parameter_list|)
throws|throws
name|TranslogException
function_decl|;
DECL|method|sync
name|void
name|sync
parameter_list|()
function_decl|;
DECL|method|syncNeeded
name|boolean
name|syncNeeded
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

