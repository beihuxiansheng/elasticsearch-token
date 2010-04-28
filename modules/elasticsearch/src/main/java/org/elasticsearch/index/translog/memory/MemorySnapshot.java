begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.translog.memory
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
operator|.
name|memory
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|util
operator|.
name|gcommon
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
operator|.
name|TranslogStreams
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|MemorySnapshot
specifier|public
class|class
name|MemorySnapshot
implements|implements
name|Translog
operator|.
name|Snapshot
block|{
DECL|field|id
specifier|private
name|long
name|id
decl_stmt|;
DECL|field|operations
name|Translog
operator|.
name|Operation
index|[]
name|operations
decl_stmt|;
DECL|method|MemorySnapshot
specifier|public
name|MemorySnapshot
parameter_list|()
block|{     }
DECL|method|MemorySnapshot
specifier|public
name|MemorySnapshot
parameter_list|(
name|Translog
operator|.
name|Snapshot
name|snapshot
parameter_list|)
block|{
name|this
argument_list|(
name|snapshot
operator|.
name|translogId
argument_list|()
argument_list|,
name|Iterables
operator|.
name|toArray
argument_list|(
name|snapshot
argument_list|,
name|Translog
operator|.
name|Operation
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|MemorySnapshot
specifier|public
name|MemorySnapshot
parameter_list|(
name|long
name|id
parameter_list|,
name|Translog
operator|.
name|Operation
index|[]
name|operations
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|operations
operator|=
name|operations
expr_stmt|;
block|}
DECL|method|translogId
annotation|@
name|Override
specifier|public
name|long
name|translogId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|release
annotation|@
name|Override
specifier|public
name|boolean
name|release
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
return|return
literal|true
return|;
block|}
DECL|method|size
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|operations
operator|.
name|length
return|;
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Translog
operator|.
name|Operation
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|operations
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|skipTo
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Translog
operator|.
name|Operation
argument_list|>
name|skipTo
parameter_list|(
name|int
name|skipTo
parameter_list|)
block|{
if|if
condition|(
name|operations
operator|.
name|length
operator|<
name|skipTo
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"skipTo ["
operator|+
name|skipTo
operator|+
literal|"] is bigger than size ["
operator|+
name|size
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|operations
argument_list|,
name|skipTo
argument_list|,
name|operations
operator|.
name|length
argument_list|)
argument_list|)
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|id
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|operations
operator|=
operator|new
name|Translog
operator|.
name|Operation
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|operations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|operations
index|[
name|i
index|]
operator|=
name|readTranslogOperation
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeTo
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|operations
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Translog
operator|.
name|Operation
name|op
range|:
name|operations
control|)
block|{
name|writeTranslogOperation
argument_list|(
name|out
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

