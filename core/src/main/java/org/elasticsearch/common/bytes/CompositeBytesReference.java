begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.bytes
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|bytes
package|;
end_package

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
name|BytesRef
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
name|BytesRefBuilder
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
name|BytesRefIterator
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
name|RamUsageEstimator
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * A composite {@link BytesReference} that allows joining multiple bytes references  * into one without copying.  *  * Note, {@link #toBytesRef()} will materialize all pages in this BytesReference.  */
end_comment

begin_class
DECL|class|CompositeBytesReference
specifier|public
specifier|final
class|class
name|CompositeBytesReference
extends|extends
name|BytesReference
block|{
DECL|field|references
specifier|private
specifier|final
name|BytesReference
index|[]
name|references
decl_stmt|;
DECL|field|offsets
specifier|private
specifier|final
name|int
index|[]
name|offsets
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
DECL|field|ramBytesUsed
specifier|private
specifier|final
name|long
name|ramBytesUsed
decl_stmt|;
DECL|method|CompositeBytesReference
specifier|public
name|CompositeBytesReference
parameter_list|(
name|BytesReference
modifier|...
name|references
parameter_list|)
block|{
name|this
operator|.
name|references
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|references
argument_list|,
literal|"references must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|offsets
operator|=
operator|new
name|int
index|[
name|references
operator|.
name|length
index|]
expr_stmt|;
name|long
name|ramBytesUsed
init|=
literal|0
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|references
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|BytesReference
name|reference
init|=
name|references
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|reference
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"references must not be null"
argument_list|)
throw|;
block|}
name|offsets
index|[
name|i
index|]
operator|=
name|offset
expr_stmt|;
comment|// we use the offsets to seek into the right BytesReference for random access and slicing
name|offset
operator|+=
name|reference
operator|.
name|length
argument_list|()
expr_stmt|;
name|ramBytesUsed
operator|+=
name|reference
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|ramBytesUsed
operator|=
name|ramBytesUsed
operator|+
operator|(
name|Integer
operator|.
name|BYTES
operator|*
name|offsets
operator|.
name|length
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|)
comment|// offsets
operator|+
operator|(
name|references
operator|.
name|length
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|)
comment|// references
operator|+
name|Integer
operator|.
name|BYTES
comment|// length
operator|+
name|Long
operator|.
name|BYTES
expr_stmt|;
comment|// ramBytesUsed
name|length
operator|=
name|offset
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|byte
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|int
name|i
init|=
name|getOffsetIndex
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|references
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|index
operator|-
name|offsets
index|[
name|i
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|slice
specifier|public
name|BytesReference
name|slice
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|length
parameter_list|)
block|{
comment|// for slices we only need to find the start and the end reference
comment|// adjust them and pass on the references in between as they are fully contained
specifier|final
name|int
name|to
init|=
name|from
operator|+
name|length
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|getOffsetIndex
argument_list|(
name|from
operator|+
name|length
argument_list|)
decl_stmt|;
specifier|final
name|int
name|start
init|=
name|getOffsetIndex
argument_list|(
name|from
argument_list|)
decl_stmt|;
specifier|final
name|BytesReference
index|[]
name|inSlice
init|=
operator|new
name|BytesReference
index|[
literal|1
operator|+
operator|(
name|limit
operator|-
name|start
operator|)
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|j
init|=
name|start
init|;
name|i
operator|<
name|inSlice
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|inSlice
index|[
name|i
index|]
operator|=
name|references
index|[
name|j
operator|++
index|]
expr_stmt|;
block|}
name|int
name|inSliceOffset
init|=
name|from
operator|-
name|offsets
index|[
name|start
index|]
decl_stmt|;
if|if
condition|(
name|inSlice
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|inSlice
index|[
literal|0
index|]
operator|.
name|slice
argument_list|(
name|inSliceOffset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|// now adjust slices in front and at the end
name|inSlice
index|[
literal|0
index|]
operator|=
name|inSlice
index|[
literal|0
index|]
operator|.
name|slice
argument_list|(
name|inSliceOffset
argument_list|,
name|inSlice
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
operator|-
name|inSliceOffset
argument_list|)
expr_stmt|;
name|inSlice
index|[
name|inSlice
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|inSlice
index|[
name|inSlice
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|slice
argument_list|(
literal|0
argument_list|,
name|to
operator|-
name|offsets
index|[
name|limit
index|]
argument_list|)
expr_stmt|;
return|return
operator|new
name|CompositeBytesReference
argument_list|(
name|inSlice
argument_list|)
return|;
block|}
DECL|method|getOffsetIndex
specifier|private
name|int
name|getOffsetIndex
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
specifier|final
name|int
name|i
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|offsets
argument_list|,
name|offset
argument_list|)
decl_stmt|;
return|return
name|i
operator|<
literal|0
condition|?
operator|(
operator|-
operator|(
name|i
operator|+
literal|1
operator|)
operator|)
operator|-
literal|1
else|:
name|i
return|;
block|}
annotation|@
name|Override
DECL|method|toBytesRef
specifier|public
name|BytesRef
name|toBytesRef
parameter_list|()
block|{
name|BytesRefBuilder
name|builder
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|grow
argument_list|(
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|spare
decl_stmt|;
name|BytesRefIterator
name|iterator
init|=
name|iterator
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|spare
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|spare
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"won't happen"
argument_list|,
name|ex
argument_list|)
throw|;
comment|// this is really an error since we don't do IO in our bytesreferences
block|}
return|return
name|builder
operator|.
name|toBytesRef
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|BytesRefIterator
name|iterator
parameter_list|()
block|{
if|if
condition|(
name|references
operator|.
name|length
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|BytesRefIterator
argument_list|()
block|{
name|int
name|index
init|=
literal|0
decl_stmt|;
specifier|private
name|BytesRefIterator
name|current
init|=
name|references
index|[
name|index
operator|++
index|]
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesRef
name|next
init|=
name|current
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
while|while
condition|(
name|index
operator|<
name|references
operator|.
name|length
condition|)
block|{
name|current
operator|=
name|references
index|[
name|index
operator|++
index|]
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|next
operator|=
name|current
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
block|}
block|}
return|return
name|next
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
parameter_list|()
lambda|->
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|ramBytesUsed
return|;
block|}
block|}
end_class

end_unit

