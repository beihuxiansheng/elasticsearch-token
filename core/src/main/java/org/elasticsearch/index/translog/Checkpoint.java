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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|CodecUtil
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
name|DataInput
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
name|DataOutput
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
name|Directory
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
name|IOContext
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
name|IndexInput
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
name|OutputStreamIndexOutput
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
name|SimpleFSDirectory
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
name|Channels
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|OpenOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|Checkpoint
class|class
name|Checkpoint
block|{
DECL|field|offset
specifier|final
name|long
name|offset
decl_stmt|;
DECL|field|numOps
specifier|final
name|int
name|numOps
decl_stmt|;
DECL|field|generation
specifier|final
name|long
name|generation
decl_stmt|;
DECL|field|INITIAL_VERSION
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_VERSION
init|=
literal|1
decl_stmt|;
comment|// start with 1, just to recognize there was some magic serialization logic before
DECL|field|CHECKPOINT_CODEC
specifier|private
specifier|static
specifier|final
name|String
name|CHECKPOINT_CODEC
init|=
literal|"ckp"
decl_stmt|;
DECL|field|FILE_SIZE
specifier|static
specifier|final
name|int
name|FILE_SIZE
init|=
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CHECKPOINT_CODEC
argument_list|)
operator|+
name|Integer
operator|.
name|BYTES
comment|// ops
operator|+
name|Long
operator|.
name|BYTES
comment|// offset
operator|+
name|Long
operator|.
name|BYTES
comment|// generation
operator|+
name|CodecUtil
operator|.
name|footerLength
argument_list|()
decl_stmt|;
DECL|field|LEGACY_NON_CHECKSUMMED_FILE_LENGTH
specifier|static
specifier|final
name|int
name|LEGACY_NON_CHECKSUMMED_FILE_LENGTH
init|=
name|Integer
operator|.
name|BYTES
comment|// ops
operator|+
name|Long
operator|.
name|BYTES
comment|// offset
operator|+
name|Long
operator|.
name|BYTES
decl_stmt|;
comment|// generation
DECL|method|Checkpoint
name|Checkpoint
parameter_list|(
name|long
name|offset
parameter_list|,
name|int
name|numOps
parameter_list|,
name|long
name|generation
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|numOps
operator|=
name|numOps
expr_stmt|;
name|this
operator|.
name|generation
operator|=
name|generation
expr_stmt|;
block|}
DECL|method|write
specifier|private
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|numOps
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|generation
argument_list|)
expr_stmt|;
block|}
comment|// reads a checksummed checkpoint introduced in ES 5.0.0
DECL|method|readChecksummedV1
specifier|static
name|Checkpoint
name|readChecksummedV1
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Checkpoint
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
return|;
block|}
comment|// reads checkpoint from ES< 5.0.0
DECL|method|readNonChecksummed
specifier|static
name|Checkpoint
name|readNonChecksummed
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Checkpoint
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
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
return|return
literal|"Checkpoint{"
operator|+
literal|"offset="
operator|+
name|offset
operator|+
literal|", numOps="
operator|+
name|numOps
operator|+
literal|", translogFileGeneration= "
operator|+
name|generation
operator|+
literal|'}'
return|;
block|}
DECL|method|read
specifier|public
specifier|static
name|Checkpoint
name|read
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|Directory
name|dir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
init|)
block|{
try|try
init|(
specifier|final
name|IndexInput
name|indexInput
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|path
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
init|)
block|{
if|if
condition|(
name|indexInput
operator|.
name|length
argument_list|()
operator|==
name|LEGACY_NON_CHECKSUMMED_FILE_LENGTH
condition|)
block|{
comment|// OLD unchecksummed file that was written< ES 5.0.0
return|return
name|Checkpoint
operator|.
name|readNonChecksummed
argument_list|(
name|indexInput
argument_list|)
return|;
block|}
comment|// We checksum the entire file before we even go and parse it. If it's corrupted we barf right here.
name|CodecUtil
operator|.
name|checksumEntireFile
argument_list|(
name|indexInput
argument_list|)
expr_stmt|;
specifier|final
name|int
name|fileVersion
init|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|indexInput
argument_list|,
name|CHECKPOINT_CODEC
argument_list|,
name|INITIAL_VERSION
argument_list|,
name|INITIAL_VERSION
argument_list|)
decl_stmt|;
return|return
name|Checkpoint
operator|.
name|readChecksummedV1
argument_list|(
name|indexInput
argument_list|)
return|;
block|}
block|}
block|}
DECL|method|write
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|ChannelFactory
name|factory
parameter_list|,
name|Path
name|checkpointFile
parameter_list|,
name|Checkpoint
name|checkpoint
parameter_list|,
name|OpenOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ByteArrayOutputStream
name|byteOutputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|FILE_SIZE
argument_list|)
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|byte
index|[]
name|toByteArray
parameter_list|()
block|{
comment|// don't clone
return|return
name|buf
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|String
name|resourceDesc
init|=
literal|"checkpoint(path=\""
operator|+
name|checkpointFile
operator|+
literal|"\", gen="
operator|+
name|checkpoint
operator|+
literal|")"
decl_stmt|;
try|try
init|(
specifier|final
name|OutputStreamIndexOutput
name|indexOutput
init|=
operator|new
name|OutputStreamIndexOutput
argument_list|(
name|resourceDesc
argument_list|,
name|checkpointFile
operator|.
name|toString
argument_list|()
argument_list|,
name|byteOutputStream
argument_list|,
name|FILE_SIZE
argument_list|)
init|)
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|indexOutput
argument_list|,
name|CHECKPOINT_CODEC
argument_list|,
name|INITIAL_VERSION
argument_list|)
expr_stmt|;
name|checkpoint
operator|.
name|write
argument_list|(
name|indexOutput
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|indexOutput
argument_list|)
expr_stmt|;
assert|assert
name|indexOutput
operator|.
name|getFilePointer
argument_list|()
operator|==
name|FILE_SIZE
operator|:
literal|"get you number straights. Bytes written: "
operator|+
name|indexOutput
operator|.
name|getFilePointer
argument_list|()
operator|+
literal|" buffer size: "
operator|+
name|FILE_SIZE
assert|;
assert|assert
name|indexOutput
operator|.
name|getFilePointer
argument_list|()
operator|<
literal|512
operator|:
literal|"checkpoint files have to be smaller 512b for atomic writes. size: "
operator|+
name|indexOutput
operator|.
name|getFilePointer
argument_list|()
assert|;
block|}
comment|// now go and write to the channel, in one go.
try|try
init|(
name|FileChannel
name|channel
init|=
name|factory
operator|.
name|open
argument_list|(
name|checkpointFile
argument_list|,
name|options
argument_list|)
init|)
block|{
name|Channels
operator|.
name|writeToChannel
argument_list|(
name|byteOutputStream
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|channel
argument_list|)
expr_stmt|;
comment|// no need to force metadata, file size stays the same and we did the full fsync
comment|// when we first created the file, so the directory entry doesn't change as well
name|channel
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
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
block|{
return|return
literal|true
return|;
block|}
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
block|{
return|return
literal|false
return|;
block|}
name|Checkpoint
name|that
init|=
operator|(
name|Checkpoint
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|offset
operator|!=
name|that
operator|.
name|offset
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|numOps
operator|!=
name|that
operator|.
name|numOps
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|generation
operator|==
name|that
operator|.
name|generation
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
name|int
name|result
init|=
name|Long
operator|.
name|hashCode
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|numOps
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|Long
operator|.
name|hashCode
argument_list|(
name|generation
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

