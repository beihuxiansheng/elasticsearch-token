begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
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
name|Version
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
name|Writeable
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
name|lucene
operator|.
name|Lucene
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
name|text
operator|.
name|ParseException
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

begin_class
DECL|class|StoreFileMetaData
specifier|public
class|class
name|StoreFileMetaData
implements|implements
name|Writeable
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|// the actual file size on "disk", if compressed, the compressed size
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|checksum
specifier|private
specifier|final
name|String
name|checksum
decl_stmt|;
DECL|field|writtenBy
specifier|private
specifier|final
name|Version
name|writtenBy
decl_stmt|;
DECL|field|hash
specifier|private
specifier|final
name|BytesRef
name|hash
decl_stmt|;
DECL|method|StoreFileMetaData
specifier|public
name|StoreFileMetaData
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|length
parameter_list|,
name|String
name|checksum
parameter_list|,
name|Version
name|writtenBy
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|length
argument_list|,
name|checksum
argument_list|,
name|writtenBy
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|StoreFileMetaData
specifier|public
name|StoreFileMetaData
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|length
parameter_list|,
name|String
name|checksum
parameter_list|,
name|Version
name|writtenBy
parameter_list|,
name|BytesRef
name|hash
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|name
argument_list|,
literal|"name must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|checksum
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|checksum
argument_list|,
literal|"checksum must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|writtenBy
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|writtenBy
argument_list|,
literal|"writtenBy must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|hash
operator|=
name|hash
operator|==
literal|null
condition|?
operator|new
name|BytesRef
argument_list|()
else|:
name|hash
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|StoreFileMetaData
specifier|public
name|StoreFileMetaData
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|name
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|length
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|checksum
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
try|try
block|{
name|writtenBy
operator|=
name|Version
operator|.
name|parse
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|hash
operator|=
name|in
operator|.
name|readBytesRef
argument_list|()
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
name|writeString
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|checksum
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|writtenBy
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytesRef
argument_list|(
name|hash
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the name of this file      */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * the actual file size on "disk", if compressed, the compressed size      */
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/**      * Returns a string representation of the files checksum. Since Lucene 4.8 this is a CRC32 checksum written      * by lucene.      */
DECL|method|checksum
specifier|public
name|String
name|checksum
parameter_list|()
block|{
return|return
name|this
operator|.
name|checksum
return|;
block|}
comment|/**      * Returns<code>true</code> iff the length and the checksums are the same. otherwise<code>false</code>      */
DECL|method|isSame
specifier|public
name|boolean
name|isSame
parameter_list|(
name|StoreFileMetaData
name|other
parameter_list|)
block|{
if|if
condition|(
name|checksum
operator|==
literal|null
operator|||
name|other
operator|.
name|checksum
operator|==
literal|null
condition|)
block|{
comment|// we can't tell if either or is null so we return false in this case! this is why we don't use equals for this!
return|return
literal|false
return|;
block|}
return|return
name|length
operator|==
name|other
operator|.
name|length
operator|&&
name|checksum
operator|.
name|equals
argument_list|(
name|other
operator|.
name|checksum
argument_list|)
operator|&&
name|hash
operator|.
name|equals
argument_list|(
name|other
operator|.
name|hash
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
literal|"name ["
operator|+
name|name
operator|+
literal|"], length ["
operator|+
name|length
operator|+
literal|"], checksum ["
operator|+
name|checksum
operator|+
literal|"], writtenBy ["
operator|+
name|writtenBy
operator|+
literal|"]"
return|;
block|}
comment|/**      * Returns the Lucene version this file has been written by or<code>null</code> if unknown      */
DECL|method|writtenBy
specifier|public
name|Version
name|writtenBy
parameter_list|()
block|{
return|return
name|writtenBy
return|;
block|}
comment|/**      * Returns a variable length hash of the file represented by this metadata object. This can be the file      * itself if the file is small enough. If the length of the hash is<tt>0</tt> no hash value is available      */
DECL|method|hash
specifier|public
name|BytesRef
name|hash
parameter_list|()
block|{
return|return
name|hash
return|;
block|}
block|}
end_class

end_unit

