begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.blobstore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|blobstore
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
name|LuceneTestCase
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
name|blobstore
operator|.
name|fs
operator|.
name|FsBlobStore
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
name|settings
operator|.
name|Settings
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
name|ByteSizeUnit
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
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|InputStream
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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|Map
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|notNullValue
import|;
end_import

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|SuppressFileSystems
argument_list|(
literal|"ExtrasFS"
argument_list|)
DECL|class|BlobStoreTests
specifier|public
class|class
name|BlobStoreTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|Test
DECL|method|testWriteRead
specifier|public
name|void
name|testWriteRead
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|BlobStore
name|store
init|=
name|newBlobStore
argument_list|()
decl_stmt|;
specifier|final
name|BlobContainer
name|container
init|=
name|store
operator|.
name|blobContainer
argument_list|(
operator|new
name|BlobPath
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|randomBytes
argument_list|(
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
name|scaledRandomIntBetween
argument_list|(
literal|1024
argument_list|,
literal|1
operator|<<
literal|16
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|OutputStream
name|stream
init|=
name|container
operator|.
name|createOutput
argument_list|(
literal|"foobar"
argument_list|)
init|)
block|{
name|stream
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|InputStream
name|stream
init|=
name|container
operator|.
name|openInput
argument_list|(
literal|"foobar"
argument_list|)
init|)
block|{
name|BytesRefBuilder
name|target
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|target
operator|.
name|length
argument_list|()
operator|<
name|data
operator|.
name|length
condition|)
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|scaledRandomIntBetween
argument_list|(
literal|1
argument_list|,
name|data
operator|.
name|length
operator|-
name|target
operator|.
name|length
argument_list|()
argument_list|)
index|]
decl_stmt|;
name|int
name|offset
init|=
name|scaledRandomIntBetween
argument_list|(
literal|0
argument_list|,
name|buffer
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
name|int
name|read
init|=
name|stream
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|offset
argument_list|)
decl_stmt|;
name|target
operator|.
name|append
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|read
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|data
operator|.
name|length
argument_list|,
name|target
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|data
argument_list|,
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|target
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|target
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMoveAndList
specifier|public
name|void
name|testMoveAndList
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|BlobStore
name|store
init|=
name|newBlobStore
argument_list|()
decl_stmt|;
specifier|final
name|BlobContainer
name|container
init|=
name|store
operator|.
name|blobContainer
argument_list|(
operator|new
name|BlobPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|container
operator|.
name|listBlobs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|numberOfFooBlobs
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|numberOfBarBlobs
init|=
name|randomIntBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|generatedBlobs
init|=
name|newHashMap
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
name|numberOfFooBlobs
condition|;
name|i
operator|++
control|)
block|{
name|int
name|length
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|String
name|name
init|=
literal|"foo-"
operator|+
name|i
operator|+
literal|"-"
decl_stmt|;
name|generatedBlobs
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|(
name|long
operator|)
name|length
argument_list|)
expr_stmt|;
name|createRandomBlob
argument_list|(
name|container
argument_list|,
name|name
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numberOfBarBlobs
condition|;
name|i
operator|++
control|)
block|{
name|int
name|length
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|String
name|name
init|=
literal|"bar-"
operator|+
name|i
operator|+
literal|"-"
decl_stmt|;
name|generatedBlobs
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|(
name|long
operator|)
name|length
argument_list|)
expr_stmt|;
name|createRandomBlob
argument_list|(
name|container
argument_list|,
name|name
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
name|int
name|length
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|String
name|name
init|=
literal|"bar-0-"
decl_stmt|;
name|generatedBlobs
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|(
name|long
operator|)
name|length
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|createRandomBlob
argument_list|(
name|container
argument_list|,
name|name
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|BlobMetaData
argument_list|>
name|blobs
init|=
name|container
operator|.
name|listBlobs
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|blobs
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numberOfFooBlobs
operator|+
name|numberOfBarBlobs
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|generated
range|:
name|generatedBlobs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|BlobMetaData
name|blobMetaData
init|=
name|blobs
operator|.
name|get
argument_list|(
name|generated
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|generated
operator|.
name|getKey
argument_list|()
argument_list|,
name|blobMetaData
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|blobMetaData
operator|.
name|name
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|generated
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|blobMetaData
operator|.
name|length
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|generated
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|container
operator|.
name|listBlobsByPrefix
argument_list|(
literal|"foo-"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numberOfFooBlobs
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|container
operator|.
name|listBlobsByPrefix
argument_list|(
literal|"bar-"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numberOfBarBlobs
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|container
operator|.
name|listBlobsByPrefix
argument_list|(
literal|"baz-"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|newName
init|=
literal|"bar-new"
decl_stmt|;
comment|// Move to a new location
name|container
operator|.
name|move
argument_list|(
name|name
argument_list|,
name|newName
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|container
operator|.
name|listBlobsByPrefix
argument_list|(
name|name
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|blobs
operator|=
name|container
operator|.
name|listBlobsByPrefix
argument_list|(
name|newName
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|blobs
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|blobs
operator|.
name|get
argument_list|(
name|newName
argument_list|)
operator|.
name|length
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|generatedBlobs
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
argument_list|,
name|equalTo
argument_list|(
name|readBlobFully
argument_list|(
name|container
argument_list|,
name|newName
argument_list|,
name|length
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|createRandomBlob
specifier|protected
name|byte
index|[]
name|createRandomBlob
parameter_list|(
name|BlobContainer
name|container
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
name|randomBytes
argument_list|(
name|length
argument_list|)
decl_stmt|;
try|try
init|(
name|OutputStream
name|stream
init|=
name|container
operator|.
name|createOutput
argument_list|(
name|name
argument_list|)
init|)
block|{
name|stream
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
DECL|method|readBlobFully
specifier|protected
name|byte
index|[]
name|readBlobFully
parameter_list|(
name|BlobContainer
name|container
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
try|try
init|(
name|InputStream
name|inputStream
init|=
name|container
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|inputStream
operator|.
name|read
argument_list|(
name|data
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|inputStream
operator|.
name|read
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
DECL|method|randomBytes
specifier|protected
name|byte
index|[]
name|randomBytes
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
index|]
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|randomInt
argument_list|()
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
DECL|method|newBlobStore
specifier|protected
name|BlobStore
name|newBlobStore
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|tempDir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|randomBoolean
argument_list|()
condition|?
name|Settings
operator|.
name|EMPTY
else|:
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"buffer_size"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|,
name|ByteSizeUnit
operator|.
name|KB
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FsBlobStore
name|store
init|=
operator|new
name|FsBlobStore
argument_list|(
name|settings
argument_list|,
name|tempDir
argument_list|)
decl_stmt|;
return|return
name|store
return|;
block|}
block|}
end_class

end_unit
