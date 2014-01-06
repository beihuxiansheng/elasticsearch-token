begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
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
name|BytesRef
import|;
end_import

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
name|common
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
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffers
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
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * A bytes array reference that caches the hash code.  */
end_comment

begin_class
DECL|class|HashedBytesArray
specifier|public
class|class
name|HashedBytesArray
implements|implements
name|BytesReference
block|{
DECL|field|bytes
specifier|private
specifier|final
name|byte
index|[]
name|bytes
decl_stmt|;
comment|/**      * Cache the hash code for the string      */
DECL|field|hash
specifier|private
name|int
name|hash
decl_stmt|;
comment|// Defaults to 0
DECL|method|HashedBytesArray
specifier|public
name|HashedBytesArray
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
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
return|return
name|bytes
index|[
name|index
index|]
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
name|bytes
operator|.
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
if|if
condition|(
name|from
argument_list|<
literal|0
operator|||
operator|(
name|from
operator|+
name|length
operator|)
argument_list|>
name|bytes
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"can't slice a buffer with length ["
operator|+
name|bytes
operator|.
name|length
operator|+
literal|"], with slice parameters from ["
operator|+
name|from
operator|+
literal|"], length ["
operator|+
name|length
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
operator|new
name|BytesArray
argument_list|(
name|bytes
argument_list|,
name|from
argument_list|,
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|streamInput
specifier|public
name|StreamInput
name|streamInput
parameter_list|()
block|{
return|return
operator|new
name|BytesStreamInput
argument_list|(
name|bytes
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|os
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toBytes
specifier|public
name|byte
index|[]
name|toBytes
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|toBytesArray
specifier|public
name|BytesArray
name|toBytesArray
parameter_list|()
block|{
return|return
operator|new
name|BytesArray
argument_list|(
name|bytes
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyBytesArray
specifier|public
name|BytesArray
name|copyBytesArray
parameter_list|()
block|{
name|byte
index|[]
name|copy
init|=
operator|new
name|byte
index|[
name|bytes
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|copy
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|BytesArray
argument_list|(
name|copy
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toChannelBuffer
specifier|public
name|ChannelBuffer
name|toChannelBuffer
parameter_list|()
block|{
return|return
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|array
specifier|public
name|byte
index|[]
name|array
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|arrayOffset
specifier|public
name|int
name|arrayOffset
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|toUtf8
specifier|public
name|String
name|toUtf8
parameter_list|()
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
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
return|return
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyBytesRef
specifier|public
name|BytesRef
name|copyBytesRef
parameter_list|()
block|{
name|byte
index|[]
name|copy
init|=
operator|new
name|byte
index|[
name|bytes
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|copy
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|BytesRef
argument_list|(
name|copy
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|hash
operator|==
literal|0
condition|)
block|{
name|hash
operator|=
name|Helper
operator|.
name|bytesHashCode
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|Helper
operator|.
name|bytesEqual
argument_list|(
name|this
argument_list|,
operator|(
name|BytesReference
operator|)
name|obj
argument_list|)
return|;
block|}
block|}
end_class

end_unit

