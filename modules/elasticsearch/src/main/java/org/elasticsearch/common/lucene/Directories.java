begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
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
name|index
operator|.
name|IndexCommit
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
name|index
operator|.
name|IndexReader
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
name|store
operator|.
name|*
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
name|unit
operator|.
name|ByteSizeValue
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
name|store
operator|.
name|support
operator|.
name|ForceSyncDirectory
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|FileSystemUtils
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A set of utilities for Lucene {@link Directory}.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|Directories
specifier|public
class|class
name|Directories
block|{
comment|/**      * Deletes all the files from a directory.      *      * @param directory The directoy to delete all the files from      * @throws IOException if an exception occurs during the delete process      */
DECL|method|deleteFiles
specifier|public
specifier|static
name|void
name|deleteFiles
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|files
init|=
name|directory
operator|.
name|listAll
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|directory
operator|.
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the estimated size of a {@link Directory}.      */
DECL|method|estimateSize
specifier|public
specifier|static
name|ByteSizeValue
name|estimateSize
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|estimatedSize
init|=
literal|0
decl_stmt|;
name|String
index|[]
name|files
init|=
name|directory
operator|.
name|listAll
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
try|try
block|{
name|estimatedSize
operator|+=
name|directory
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// ignore, the file is not there no more
block|}
block|}
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|estimatedSize
argument_list|)
return|;
block|}
comment|/**      * Lists all the commit point in a directory.      */
DECL|method|listCommits
specifier|public
specifier|static
name|Collection
argument_list|<
name|IndexCommit
argument_list|>
name|listCommits
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|IndexReader
operator|.
name|listCommits
argument_list|(
name|directory
argument_list|)
return|;
block|}
comment|/**      * Computes the checksum of the given file name with the directory.      */
DECL|method|checksum
specifier|public
specifier|static
name|long
name|checksum
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|checksum
argument_list|(
name|dir
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
DECL|method|copyFromDirectory
specifier|public
specifier|static
name|void
name|copyFromDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|File
name|copyTo
parameter_list|,
name|boolean
name|nativeCopy
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nativeCopy
operator|&&
operator|(
name|dir
operator|instanceof
name|FSDirectory
operator|)
condition|)
block|{
if|if
condition|(
operator|!
name|copyTo
operator|.
name|exists
argument_list|()
condition|)
block|{
name|copyTo
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
block|}
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
operator|(
operator|(
name|FSDirectory
operator|)
name|dir
operator|)
operator|.
name|getFile
argument_list|()
argument_list|,
name|fileName
argument_list|)
argument_list|,
name|copyTo
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|copyFromDirectory
argument_list|(
name|dir
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|)
argument_list|,
operator|new
name|FileOutputStream
argument_list|(
name|copyTo
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// sync the file
name|syncFile
argument_list|(
name|copyTo
argument_list|)
expr_stmt|;
block|}
DECL|method|copyFromDirectory
specifier|public
specifier|static
name|void
name|copyFromDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|copyFromDirectory
argument_list|(
name|dir
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|)
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
DECL|method|copyFromDirectory
specifier|public
specifier|static
name|void
name|copyFromDirectory
parameter_list|(
name|IndexInput
name|ii
parameter_list|,
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|BUFFER_SIZE
init|=
name|ii
operator|.
name|length
argument_list|()
operator|<
literal|16384
condition|?
operator|(
name|int
operator|)
name|ii
operator|.
name|length
argument_list|()
else|:
literal|16384
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|BUFFER_SIZE
index|]
decl_stmt|;
try|try
block|{
name|long
name|len
init|=
name|ii
operator|.
name|length
argument_list|()
decl_stmt|;
name|long
name|readCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|readCount
operator|<
name|len
condition|)
block|{
name|int
name|toRead
init|=
name|readCount
operator|+
name|BUFFER_SIZE
operator|>
name|len
condition|?
call|(
name|int
call|)
argument_list|(
name|len
operator|-
name|readCount
argument_list|)
else|:
name|BUFFER_SIZE
decl_stmt|;
name|ii
operator|.
name|readBytes
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|toRead
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|readCount
operator|+=
name|toRead
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|toRead
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
if|if
condition|(
name|ii
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
DECL|method|copyToDirectory
specifier|public
specifier|static
name|void
name|copyToDirectory
parameter_list|(
name|File
name|copyFrom
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|boolean
name|nativeCopy
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nativeCopy
operator|&&
operator|(
name|dir
operator|instanceof
name|FSDirectory
operator|)
condition|)
block|{
name|File
name|destinationFile
init|=
operator|new
name|File
argument_list|(
operator|(
operator|(
name|FSDirectory
operator|)
name|dir
operator|)
operator|.
name|getFile
argument_list|()
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|destinationFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|destinationFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
block|}
name|copyFile
argument_list|(
name|copyFrom
argument_list|,
name|destinationFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileInputStream
name|is
init|=
literal|null
decl_stmt|;
name|IndexOutput
name|output
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|copyFrom
argument_list|)
expr_stmt|;
name|output
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|copyToDirectory
argument_list|(
name|is
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
name|sync
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
DECL|method|copyToDirectory
specifier|public
specifier|static
name|void
name|copyToDirectory
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|output
init|=
literal|null
decl_stmt|;
try|try
block|{
name|output
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|copyToDirectory
argument_list|(
name|is
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
name|sync
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
DECL|method|sync
specifier|public
specifier|static
name|void
name|sync
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dir
operator|instanceof
name|ForceSyncDirectory
condition|)
block|{
operator|(
operator|(
name|ForceSyncDirectory
operator|)
name|dir
operator|)
operator|.
name|forceSync
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|.
name|sync
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|copyToDirectory
specifier|public
specifier|static
name|void
name|copyToDirectory
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|IndexOutput
name|io
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|16384
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|len
operator|=
name|is
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
name|io
operator|.
name|writeBytes
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|io
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
comment|/**      * Computes the checksum of the content represented by the provided index input.      *      *<p>Closes the index input once checksum is computed.      */
DECL|method|checksum
specifier|public
specifier|static
name|long
name|checksum
parameter_list|(
name|IndexInput
name|indexInput
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|16384
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|BUFFER_SIZE
index|]
decl_stmt|;
name|ChecksumIndexInput
name|cii
init|=
operator|new
name|ChecksumIndexInput
argument_list|(
name|indexInput
argument_list|)
decl_stmt|;
name|long
name|len
init|=
name|cii
operator|.
name|length
argument_list|()
decl_stmt|;
name|long
name|readCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|readCount
operator|<
name|len
condition|)
block|{
name|int
name|toRead
init|=
name|readCount
operator|+
name|BUFFER_SIZE
operator|>
name|len
condition|?
call|(
name|int
call|)
argument_list|(
name|len
operator|-
name|readCount
argument_list|)
else|:
name|BUFFER_SIZE
decl_stmt|;
name|cii
operator|.
name|readBytes
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|toRead
argument_list|)
expr_stmt|;
name|readCount
operator|+=
name|toRead
expr_stmt|;
block|}
name|cii
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|cii
operator|.
name|getChecksum
argument_list|()
return|;
block|}
DECL|method|Directories
specifier|private
name|Directories
parameter_list|()
block|{      }
block|}
end_class

end_unit

