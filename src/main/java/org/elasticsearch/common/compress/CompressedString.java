begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.compress
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
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
name|UnicodeUtil
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
name|Unicode
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
name|BytesArray
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
name|common
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
name|Streamable
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|CompressedString
specifier|public
class|class
name|CompressedString
implements|implements
name|Streamable
block|{
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|method|CompressedString
name|CompressedString
parameter_list|()
block|{     }
comment|/**      * Constructor assuming the data provided is compressed (UTF8). It uses the provided      * array without copying it.      */
DECL|method|CompressedString
specifier|public
name|CompressedString
parameter_list|(
name|byte
index|[]
name|compressed
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|compressed
expr_stmt|;
block|}
DECL|method|CompressedString
specifier|public
name|CompressedString
parameter_list|(
name|BytesReference
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|Compressor
name|compressor
init|=
name|CompressorFactory
operator|.
name|compressor
argument_list|(
name|data
argument_list|)
decl_stmt|;
if|if
condition|(
name|compressor
operator|!=
literal|null
condition|)
block|{
comment|// already compressed...
name|this
operator|.
name|bytes
operator|=
name|data
operator|.
name|toBytes
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|BytesArray
name|bytesArray
init|=
name|data
operator|.
name|toBytesArray
argument_list|()
decl_stmt|;
name|this
operator|.
name|bytes
operator|=
name|CompressorFactory
operator|.
name|defaultCompressor
argument_list|()
operator|.
name|compress
argument_list|(
name|bytesArray
operator|.
name|array
argument_list|()
argument_list|,
name|bytesArray
operator|.
name|arrayOffset
argument_list|()
argument_list|,
name|bytesArray
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Constructs a new compressed string, assuming the bytes are UTF8, by copying it over.      *      * @param data   The byte array      * @param offset Offset into the byte array      * @param length The length of the data      * @throws IOException      */
DECL|method|CompressedString
specifier|public
name|CompressedString
parameter_list|(
name|byte
index|[]
name|data
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
name|Compressor
name|compressor
init|=
name|CompressorFactory
operator|.
name|compressor
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|compressor
operator|!=
literal|null
condition|)
block|{
comment|// already compressed...
name|this
operator|.
name|bytes
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|offset
operator|+
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// default to LZF
name|this
operator|.
name|bytes
operator|=
name|CompressorFactory
operator|.
name|defaultCompressor
argument_list|()
operator|.
name|compress
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|CompressedString
specifier|public
name|CompressedString
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|IOException
block|{
name|UnicodeUtil
operator|.
name|UTF8Result
name|result
init|=
name|Unicode
operator|.
name|unsafeFromStringAsUtf8
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|this
operator|.
name|bytes
operator|=
name|CompressorFactory
operator|.
name|defaultCompressor
argument_list|()
operator|.
name|compress
argument_list|(
name|result
operator|.
name|result
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|compressed
specifier|public
name|byte
index|[]
name|compressed
parameter_list|()
block|{
return|return
name|this
operator|.
name|bytes
return|;
block|}
DECL|method|uncompressed
specifier|public
name|byte
index|[]
name|uncompressed
parameter_list|()
throws|throws
name|IOException
block|{
name|Compressor
name|compressor
init|=
name|CompressorFactory
operator|.
name|compressor
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
return|return
name|compressor
operator|.
name|uncompress
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
DECL|method|string
specifier|public
name|String
name|string
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Unicode
operator|.
name|fromBytes
argument_list|(
name|uncompressed
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readCompressedString
specifier|public
specifier|static
name|CompressedString
name|readCompressedString
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|CompressedString
name|compressedString
init|=
operator|new
name|CompressedString
argument_list|()
decl_stmt|;
name|compressedString
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|compressedString
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
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
name|bytes
operator|=
operator|new
name|byte
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
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
name|writeVInt
argument_list|(
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|CompressedString
name|that
init|=
operator|(
name|CompressedString
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|bytes
argument_list|,
name|that
operator|.
name|bytes
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
return|return
name|bytes
operator|!=
literal|null
condition|?
name|Arrays
operator|.
name|hashCode
argument_list|(
name|bytes
argument_list|)
else|:
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
return|return
name|string
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|"_na_"
return|;
block|}
block|}
block|}
end_class

end_unit

