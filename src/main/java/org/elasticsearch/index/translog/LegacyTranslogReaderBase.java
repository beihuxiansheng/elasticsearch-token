begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.translog
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
package|;
end_package

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

begin_comment
comment|/**  * Version 1 of the translog format, there is checkpoint and therefore no notion of op count  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|LegacyTranslogReaderBase
class|class
name|LegacyTranslogReaderBase
extends|extends
name|ImmutableTranslogReader
block|{
comment|/**      * Create a snapshot of translog file channel. The length parameter should be consistent with totalOperations and point      * at the end of the last operation in this snapshot.      *      */
DECL|method|LegacyTranslogReaderBase
name|LegacyTranslogReaderBase
parameter_list|(
name|long
name|generation
parameter_list|,
name|ChannelReference
name|channelReference
parameter_list|,
name|long
name|firstOperationOffset
parameter_list|,
name|long
name|fileLength
parameter_list|)
block|{
name|super
argument_list|(
name|generation
argument_list|,
name|channelReference
argument_list|,
name|firstOperationOffset
argument_list|,
name|fileLength
argument_list|,
name|TranslogReader
operator|.
name|UNKNOWN_OP_COUNT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newReaderSnapshot
specifier|protected
name|Translog
operator|.
name|Snapshot
name|newReaderSnapshot
parameter_list|(
specifier|final
name|int
name|totalOperations
parameter_list|,
name|ByteBuffer
name|reusableBuffer
parameter_list|)
block|{
assert|assert
name|totalOperations
operator|==
operator|-
literal|1
operator|:
literal|"legacy we had no idea how many ops: "
operator|+
name|totalOperations
assert|;
return|return
operator|new
name|ReaderSnapshot
argument_list|(
name|totalOperations
argument_list|,
name|reusableBuffer
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Translog
operator|.
name|Operation
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|position
operator|>=
name|sizeInBytes
argument_list|()
condition|)
block|{
comment|// this is the legacy case....
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|readOperation
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|TruncatedTranslogException
name|ex
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// legacy case
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|newReader
specifier|protected
name|ImmutableTranslogReader
name|newReader
parameter_list|(
name|long
name|generation
parameter_list|,
name|ChannelReference
name|channelReference
parameter_list|,
name|long
name|firstOperationOffset
parameter_list|,
name|long
name|length
parameter_list|,
name|int
name|totalOperations
parameter_list|)
block|{
assert|assert
name|totalOperations
operator|==
operator|-
literal|1
operator|:
literal|"expected unknown but was: "
operator|+
name|totalOperations
assert|;
return|return
operator|new
name|LegacyTranslogReaderBase
argument_list|(
name|generation
argument_list|,
name|channelReference
argument_list|,
name|firstOperationOffset
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
end_class

end_unit

