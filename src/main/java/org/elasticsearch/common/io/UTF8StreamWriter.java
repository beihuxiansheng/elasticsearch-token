begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.io
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|CharConversionException
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|UTF8StreamWriter
specifier|public
specifier|final
class|class
name|UTF8StreamWriter
extends|extends
name|Writer
block|{
comment|/**      * Holds the current output stream or<code>null</code> if closed.      */
DECL|field|_outputStream
specifier|private
name|OutputStream
name|_outputStream
decl_stmt|;
comment|/**      * Holds the bytes' buffer.      */
DECL|field|_bytes
specifier|private
specifier|final
name|byte
index|[]
name|_bytes
decl_stmt|;
comment|/**      * Holds the bytes buffer index.      */
DECL|field|_index
specifier|private
name|int
name|_index
decl_stmt|;
comment|/**      * Creates a UTF-8 writer having a byte buffer of moderate capacity (2048).      */
DECL|method|UTF8StreamWriter
specifier|public
name|UTF8StreamWriter
parameter_list|()
block|{
name|_bytes
operator|=
operator|new
name|byte
index|[
literal|2048
index|]
expr_stmt|;
block|}
comment|/**      * Creates a UTF-8 writer having a byte buffer of specified capacity.      *      * @param capacity the capacity of the byte buffer.      */
DECL|method|UTF8StreamWriter
specifier|public
name|UTF8StreamWriter
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|_bytes
operator|=
operator|new
name|byte
index|[
name|capacity
index|]
expr_stmt|;
block|}
comment|/**      * Sets the output stream to use for writing until this writer is closed.      * For example:[code]      * Writer writer = new UTF8StreamWriter().setOutputStream(out);      * [/code] is equivalent but writes faster than [code]      * Writer writer = new java.io.OutputStreamWriter(out, "UTF-8");      * [/code]      *      * @param out the output stream.      * @return this UTF-8 writer.      * @throws IllegalStateException if this writer is being reused and      *                               it has not been {@link #close closed} or {@link #reset reset}.      */
DECL|method|setOutput
specifier|public
name|UTF8StreamWriter
name|setOutput
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
if|if
condition|(
name|_outputStream
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Writer not closed or reset"
argument_list|)
throw|;
name|_outputStream
operator|=
name|out
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Writes a single character. This method supports 16-bits      * character surrogates.      *      * @param c<code>char</code> the character to be written (possibly      *          a surrogate).      * @throws IOException if an I/O error occurs.      */
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|char
name|c
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|c
operator|<
literal|0xd800
operator|)
operator|||
operator|(
name|c
operator|>
literal|0xdfff
operator|)
condition|)
block|{
name|write
argument_list|(
operator|(
name|int
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|<
literal|0xdc00
condition|)
block|{
comment|// High surrogate.
name|_highSurrogate
operator|=
name|c
expr_stmt|;
block|}
else|else
block|{
comment|// Low surrogate.
name|int
name|code
init|=
operator|(
operator|(
name|_highSurrogate
operator|-
literal|0xd800
operator|)
operator|<<
literal|10
operator|)
operator|+
operator|(
name|c
operator|-
literal|0xdc00
operator|)
operator|+
literal|0x10000
decl_stmt|;
name|write
argument_list|(
name|code
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|_highSurrogate
specifier|private
name|char
name|_highSurrogate
decl_stmt|;
comment|/**      * Writes a character given its 31-bits Unicode.      *      * @param code the 31 bits Unicode of the character to be written.      * @throws IOException if an I/O error occurs.      */
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|int
name|code
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|code
operator|&
literal|0xffffff80
operator|)
operator|==
literal|0
condition|)
block|{
name|_bytes
index|[
name|_index
index|]
operator|=
operator|(
name|byte
operator|)
name|code
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Writes more than one byte.
name|write2
argument_list|(
name|code
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|write2
specifier|private
name|void
name|write2
parameter_list|(
name|int
name|c
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|c
operator|&
literal|0xfffff800
operator|)
operator|==
literal|0
condition|)
block|{
comment|// 2 bytes.
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xc0
operator||
operator|(
name|c
operator|>>
literal|6
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|c
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|(
name|c
operator|&
literal|0xffff0000
operator|)
operator|==
literal|0
condition|)
block|{
comment|// 3 bytes.
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xe0
operator||
operator|(
name|c
operator|>>
literal|12
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|6
operator|)
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|c
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|(
name|c
operator|&
literal|0xff200000
operator|)
operator|==
literal|0
condition|)
block|{
comment|// 4 bytes.
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xf0
operator||
operator|(
name|c
operator|>>
literal|18
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|12
operator|)
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|6
operator|)
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|c
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|(
name|c
operator|&
literal|0xf4000000
operator|)
operator|==
literal|0
condition|)
block|{
comment|// 5 bytes.
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xf8
operator||
operator|(
name|c
operator|>>
literal|24
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|18
operator|)
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|12
operator|)
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|6
operator|)
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|c
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|(
name|c
operator|&
literal|0x80000000
operator|)
operator|==
literal|0
condition|)
block|{
comment|// 6 bytes.
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xfc
operator||
operator|(
name|c
operator|>>
literal|30
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|24
operator|)
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|18
operator|)
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|12
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|_bytes
index|[
name|_index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|c
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|CharConversionException
argument_list|(
literal|"Illegal character U+"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|c
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**      * Writes a portion of an array of characters.      *      * @param cbuf the array of characters.      * @param off  the offset from which to start writing characters.      * @param len  the number of characters to write.      * @throws IOException if an I/O error occurs.      */
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|char
name|cbuf
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|off_plus_len
init|=
name|off
operator|+
name|len
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|off
init|;
name|i
operator|<
name|off_plus_len
condition|;
control|)
block|{
name|char
name|c
init|=
name|cbuf
index|[
name|i
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0x80
condition|)
block|{
name|_bytes
index|[
name|_index
index|]
operator|=
operator|(
name|byte
operator|)
name|c
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Writes a portion of a string.      *      * @param str a String.      * @param off the offset from which to start writing characters.      * @param len the number of characters to write.      * @throws IOException if an I/O error occurs      */
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|String
name|str
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|off_plus_len
init|=
name|off
operator|+
name|len
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|off
init|;
name|i
operator|<
name|off_plus_len
condition|;
control|)
block|{
name|char
name|c
init|=
name|str
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0x80
condition|)
block|{
name|_bytes
index|[
name|_index
index|]
operator|=
operator|(
name|byte
operator|)
name|c
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Writes the specified character sequence.      *      * @param csq the character sequence.      * @throws IOException if an I/O error occurs      */
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|CharSequence
name|csq
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|length
init|=
name|csq
operator|.
name|length
argument_list|()
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
name|length
condition|;
control|)
block|{
name|char
name|c
init|=
name|csq
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0x80
condition|)
block|{
name|_bytes
index|[
name|_index
index|]
operator|=
operator|(
name|byte
operator|)
name|c
expr_stmt|;
if|if
condition|(
operator|++
name|_index
operator|>=
name|_bytes
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Flushes the stream.  If the stream has saved any characters from the      * various write() methods in a buffer, write them immediately to their      * intended destination.  Then, if that destination is another character or      * byte stream, flush it.  Thus one flush() invocation will flush all the      * buffers in a chain of Writers and OutputStreams.      *      * @throws IOException if an I/O error occurs.      */
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
name|flushBuffer
argument_list|()
expr_stmt|;
name|_outputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**      * Closes and {@link #reset resets} this writer for reuse.      *      * @throws IOException if an I/O error occurs      */
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
if|if
condition|(
name|_outputStream
operator|!=
literal|null
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
name|_outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Flushes the internal bytes buffer.      *      * @throws IOException if an I/O error occurs      */
DECL|method|flushBuffer
specifier|private
name|void
name|flushBuffer
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|_outputStream
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Stream closed"
argument_list|)
throw|;
name|_outputStream
operator|.
name|write
argument_list|(
name|_bytes
argument_list|,
literal|0
argument_list|,
name|_index
argument_list|)
expr_stmt|;
name|_index
operator|=
literal|0
expr_stmt|;
block|}
comment|// Implements Reusable.
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|_highSurrogate
operator|=
literal|0
expr_stmt|;
name|_index
operator|=
literal|0
expr_stmt|;
name|_outputStream
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * @deprecated Replaced by {@link #setOutput(OutputStream)}      */
DECL|method|setOutputStream
specifier|public
name|UTF8StreamWriter
name|setOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
return|return
name|this
operator|.
name|setOutput
argument_list|(
name|out
argument_list|)
return|;
block|}
block|}
end_class

end_unit

