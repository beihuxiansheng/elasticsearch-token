begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.io.stream
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
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
name|bytes
operator|.
name|BytesReference
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
name|bytes
operator|.
name|PagedBytesReference
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
name|BytesStream
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
name|util
operator|.
name|BigArrays
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
name|util
operator|.
name|ByteArray
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

begin_comment
comment|/**  * A @link {@link StreamOutput} that uses{@link BigArrays} to acquire pages of  * bytes, which avoids frequent reallocation& copying of the internal data.  */
end_comment

begin_class
DECL|class|BytesStreamOutput
specifier|public
class|class
name|BytesStreamOutput
extends|extends
name|StreamOutput
implements|implements
name|BytesStream
block|{
comment|/**      * Factory/manager for our ByteArray      */
DECL|field|bigarrays
specifier|private
specifier|final
name|BigArrays
name|bigarrays
decl_stmt|;
comment|/**      * The internal list of pages.      */
DECL|field|bytes
specifier|private
name|ByteArray
name|bytes
decl_stmt|;
comment|/**      * The number of valid bytes in the buffer.      */
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
comment|/**      * Create a nonrecycling {@link BytesStreamOutput} with 1 initial page acquired.      */
DECL|method|BytesStreamOutput
specifier|public
name|BytesStreamOutput
parameter_list|()
block|{
name|this
argument_list|(
name|BigArrays
operator|.
name|PAGE_SIZE_IN_BYTES
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a nonrecycling {@link BytesStreamOutput} with enough initial pages acquired      * to satisfy the capacity given by {@link expectedSize}.      *       * @param expectedSize the expected maximum size of the stream in bytes.      */
DECL|method|BytesStreamOutput
specifier|public
name|BytesStreamOutput
parameter_list|(
name|int
name|expectedSize
parameter_list|)
block|{
name|bigarrays
operator|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
expr_stmt|;
name|bytes
operator|=
name|bigarrays
operator|.
name|newByteArray
argument_list|(
name|expectedSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekPositionSupported
specifier|public
name|boolean
name|seekPositionSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|position
specifier|public
name|long
name|position
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureCapacity
argument_list|(
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|set
argument_list|(
name|count
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nothing to copy
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
comment|// illegal args: offset and/or length exceed array size
if|if
condition|(
name|b
operator|.
name|length
operator|<
operator|(
name|offset
operator|+
name|length
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal offset "
operator|+
name|offset
operator|+
literal|"/length "
operator|+
name|length
operator|+
literal|" for byte[] of length "
operator|+
name|b
operator|.
name|length
argument_list|)
throw|;
block|}
comment|// get enough pages for new size
name|ensureCapacity
argument_list|(
name|count
operator|+
name|length
argument_list|)
expr_stmt|;
comment|// bulk copy
name|bytes
operator|.
name|set
argument_list|(
name|count
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// advance
name|count
operator|+=
name|length
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
comment|// shrink list of pages
if|if
condition|(
name|bytes
operator|.
name|size
argument_list|()
operator|>
name|BigArrays
operator|.
name|PAGE_SIZE_IN_BYTES
condition|)
block|{
name|bytes
operator|=
name|bigarrays
operator|.
name|resize
argument_list|(
name|bytes
argument_list|,
name|BigArrays
operator|.
name|PAGE_SIZE_IN_BYTES
argument_list|)
expr_stmt|;
block|}
comment|// go back to start
name|count
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing to do
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|position
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"position "
operator|+
name|position
operator|+
literal|"> Integer.MAX_VALUE"
argument_list|)
throw|;
block|}
name|count
operator|=
operator|(
name|int
operator|)
name|position
expr_stmt|;
name|ensureCapacity
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
DECL|method|skip
specifier|public
name|void
name|skip
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|count
operator|+=
name|length
expr_stmt|;
name|ensureCapacity
argument_list|(
name|count
argument_list|)
expr_stmt|;
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
comment|// empty for now.
block|}
comment|/**      * Returns the current size of the buffer.      *       * @return the value of the<code>count</code> field, which is the number of valid      *         bytes in this output stream.      * @see java.io.ByteArrayOutputStream#count      */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|bytes
specifier|public
name|BytesReference
name|bytes
parameter_list|()
block|{
name|BytesRef
name|bref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|bytes
operator|.
name|get
argument_list|(
literal|0
argument_list|,
name|count
argument_list|,
name|bref
argument_list|)
expr_stmt|;
return|return
operator|new
name|BytesArray
argument_list|(
name|bref
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|ensureCapacity
specifier|private
name|void
name|ensureCapacity
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|bytes
operator|=
name|bigarrays
operator|.
name|grow
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

