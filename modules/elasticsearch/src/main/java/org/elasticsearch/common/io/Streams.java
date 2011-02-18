begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Simple utility methods for file and stream copying.  * All copy methods use a block size of 4096 bytes,  * and close all affected streams when done.  *<p/>  *<p>Mainly for use within the framework,  * but also useful for application code.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|Streams
specifier|public
specifier|abstract
class|class
name|Streams
block|{
DECL|field|BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|1024
operator|*
literal|8
decl_stmt|;
comment|//---------------------------------------------------------------------
comment|// Copy methods for java.io.File
comment|//---------------------------------------------------------------------
comment|/**      * Copy the contents of the given input File to the given output File.      *      * @param in  the file to copy from      * @param out the file to copy to      * @return the number of bytes copied      * @throws IOException in case of I/O errors      */
DECL|method|copy
specifier|public
specifier|static
name|long
name|copy
parameter_list|(
name|File
name|in
parameter_list|,
name|File
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|in
argument_list|,
literal|"No input File specified"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|out
argument_list|,
literal|"No output File specified"
argument_list|)
expr_stmt|;
return|return
name|copy
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
argument_list|,
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Copy the contents of the given byte array to the given output File.      *      * @param in  the byte array to copy from      * @param out the file to copy to      * @throws IOException in case of I/O errors      */
DECL|method|copy
specifier|public
specifier|static
name|void
name|copy
parameter_list|(
name|byte
index|[]
name|in
parameter_list|,
name|File
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|in
argument_list|,
literal|"No input byte array specified"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|out
argument_list|,
literal|"No output File specified"
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|inStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|OutputStream
name|outStream
init|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
decl_stmt|;
name|copy
argument_list|(
name|inStream
argument_list|,
name|outStream
argument_list|)
expr_stmt|;
block|}
comment|/**      * Copy the contents of the given input File into a new byte array.      *      * @param in the file to copy from      * @return the new byte array that has been copied to      * @throws IOException in case of I/O errors      */
DECL|method|copyToByteArray
specifier|public
specifier|static
name|byte
index|[]
name|copyToByteArray
parameter_list|(
name|File
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|in
argument_list|,
literal|"No input File specified"
argument_list|)
expr_stmt|;
return|return
name|copyToByteArray
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|//---------------------------------------------------------------------
comment|// Copy methods for java.io.InputStream / java.io.OutputStream
comment|//---------------------------------------------------------------------
DECL|method|copy
specifier|public
specifier|static
name|long
name|copy
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|copy
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
operator|new
name|byte
index|[
name|BUFFER_SIZE
index|]
argument_list|)
return|;
block|}
comment|/**      * Copy the contents of the given InputStream to the given OutputStream.      * Closes both streams when done.      *      * @param in  the stream to copy from      * @param out the stream to copy to      * @return the number of bytes copied      * @throws IOException in case of I/O errors      */
DECL|method|copy
specifier|public
specifier|static
name|long
name|copy
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|in
argument_list|,
literal|"No InputStream specified"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|out
argument_list|,
literal|"No OutputStream specified"
argument_list|)
expr_stmt|;
try|try
block|{
name|long
name|byteCount
init|=
literal|0
decl_stmt|;
name|int
name|bytesRead
decl_stmt|;
while|while
condition|(
operator|(
name|bytesRead
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
name|byteCount
operator|+=
name|bytesRead
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|byteCount
return|;
block|}
finally|finally
block|{
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// do nothing
block|}
try|try
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
block|}
comment|/**      * Copy the contents of the given byte array to the given OutputStream.      * Closes the stream when done.      *      * @param in  the byte array to copy from      * @param out the OutputStream to copy to      * @throws IOException in case of I/O errors      */
DECL|method|copy
specifier|public
specifier|static
name|void
name|copy
parameter_list|(
name|byte
index|[]
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|in
argument_list|,
literal|"No input byte array specified"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|out
argument_list|,
literal|"No OutputStream specified"
argument_list|)
expr_stmt|;
try|try
block|{
name|out
operator|.
name|write
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
block|}
comment|/**      * Copy the contents of the given InputStream into a new byte array.      * Closes the stream when done.      *      * @param in the stream to copy from      * @return the new byte array that has been copied to      * @throws IOException in case of I/O errors      */
DECL|method|copyToByteArray
specifier|public
specifier|static
name|byte
index|[]
name|copyToByteArray
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|FastByteArrayOutputStream
name|out
init|=
name|FastByteArrayOutputStream
operator|.
name|Cached
operator|.
name|cached
argument_list|()
decl_stmt|;
name|copy
argument_list|(
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|copiedByteArray
argument_list|()
return|;
block|}
comment|//---------------------------------------------------------------------
comment|// Copy methods for java.io.Reader / java.io.Writer
comment|//---------------------------------------------------------------------
comment|/**      * Copy the contents of the given Reader to the given Writer.      * Closes both when done.      *      * @param in  the Reader to copy from      * @param out the Writer to copy to      * @return the number of characters copied      * @throws IOException in case of I/O errors      */
DECL|method|copy
specifier|public
specifier|static
name|int
name|copy
parameter_list|(
name|Reader
name|in
parameter_list|,
name|Writer
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|in
argument_list|,
literal|"No Reader specified"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|out
argument_list|,
literal|"No Writer specified"
argument_list|)
expr_stmt|;
try|try
block|{
name|int
name|byteCount
init|=
literal|0
decl_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|BUFFER_SIZE
index|]
decl_stmt|;
name|int
name|bytesRead
decl_stmt|;
while|while
condition|(
operator|(
name|bytesRead
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
name|byteCount
operator|+=
name|bytesRead
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|byteCount
return|;
block|}
finally|finally
block|{
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// do nothing
block|}
try|try
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
block|}
comment|/**      * Copy the contents of the given String to the given output Writer.      * Closes the write when done.      *      * @param in  the String to copy from      * @param out the Writer to copy to      * @throws IOException in case of I/O errors      */
DECL|method|copy
specifier|public
specifier|static
name|void
name|copy
parameter_list|(
name|String
name|in
parameter_list|,
name|Writer
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|in
argument_list|,
literal|"No input String specified"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|out
argument_list|,
literal|"No Writer specified"
argument_list|)
expr_stmt|;
try|try
block|{
name|out
operator|.
name|write
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
block|}
comment|/**      * Copy the contents of the given Reader into a String.      * Closes the reader when done.      *      * @param in the reader to copy from      * @return the String that has been copied to      * @throws IOException in case of I/O errors      */
DECL|method|copyToString
specifier|public
specifier|static
name|String
name|copyToString
parameter_list|(
name|Reader
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|StringWriter
name|out
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|copy
argument_list|(
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|copyToStringFromClasspath
specifier|public
specifier|static
name|String
name|copyToStringFromClasspath
parameter_list|(
name|ClassLoader
name|classLoader
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
name|classLoader
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Resource ["
operator|+
name|path
operator|+
literal|"] not found in classpath with class loader ["
operator|+
name|classLoader
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|copyToString
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|copyToStringFromClasspath
specifier|public
specifier|static
name|String
name|copyToStringFromClasspath
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
name|Streams
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Resource ["
operator|+
name|path
operator|+
literal|"] not found in classpath"
argument_list|)
throw|;
block|}
return|return
name|copyToString
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|)
argument_list|)
return|;
block|}
DECL|method|copyToBytesFromClasspath
specifier|public
specifier|static
name|byte
index|[]
name|copyToBytesFromClasspath
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
name|Streams
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Resource ["
operator|+
name|path
operator|+
literal|"] not found in classpath"
argument_list|)
throw|;
block|}
return|return
name|copyToByteArray
argument_list|(
name|is
argument_list|)
return|;
block|}
block|}
end_class

end_unit

