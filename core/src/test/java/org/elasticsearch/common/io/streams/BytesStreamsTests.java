begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.io.streams
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|streams
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
name|Constants
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
name|BytesStreamOutput
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
name|lucene
operator|.
name|BytesRefs
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
name|Ignore
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|closeTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  * Tests for {@link BytesStreamOutput} paging behaviour.  */
end_comment

begin_class
DECL|class|BytesStreamsTests
specifier|public
class|class
name|BytesStreamsTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|Test
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
comment|// test empty stream to array
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleByte
specifier|public
name|void
name|testSingleByte
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|expectedSize
init|=
literal|1
decl_stmt|;
name|byte
index|[]
name|expectedData
init|=
name|randomizedByteArrayWithSize
argument_list|(
name|expectedSize
argument_list|)
decl_stmt|;
comment|// write single byte
name|out
operator|.
name|writeByte
argument_list|(
name|expectedData
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedData
argument_list|,
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleShortPage
specifier|public
name|void
name|testSingleShortPage
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|int
name|expectedSize
init|=
literal|10
decl_stmt|;
name|byte
index|[]
name|expectedData
init|=
name|randomizedByteArrayWithSize
argument_list|(
name|expectedSize
argument_list|)
decl_stmt|;
comment|// write byte-by-byte
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedSize
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|expectedData
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedData
argument_list|,
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIllegalBulkWrite
specifier|public
name|void
name|testIllegalBulkWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
comment|// bulk-write with wrong args
try|try
block|{
name|out
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[]
block|{}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected IllegalArgumentException: length> (size-offset)"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iax1
parameter_list|)
block|{
comment|// expected
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleShortPageBulkWrite
specifier|public
name|void
name|testSingleShortPageBulkWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
comment|// first bulk-write empty array: should not change anything
name|int
name|expectedSize
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|expectedData
init|=
name|randomizedByteArrayWithSize
argument_list|(
name|expectedSize
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|expectedData
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedData
argument_list|,
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// bulk-write again with actual bytes
name|expectedSize
operator|=
literal|10
expr_stmt|;
name|expectedData
operator|=
name|randomizedByteArrayWithSize
argument_list|(
name|expectedSize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|expectedData
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedData
argument_list|,
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleFullPageBulkWrite
specifier|public
name|void
name|testSingleFullPageBulkWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|int
name|expectedSize
init|=
name|BigArrays
operator|.
name|BYTE_PAGE_SIZE
decl_stmt|;
name|byte
index|[]
name|expectedData
init|=
name|randomizedByteArrayWithSize
argument_list|(
name|expectedSize
argument_list|)
decl_stmt|;
comment|// write in bulk
name|out
operator|.
name|writeBytes
argument_list|(
name|expectedData
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedData
argument_list|,
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleFullPageBulkWriteWithOffset
specifier|public
name|void
name|testSingleFullPageBulkWriteWithOffset
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|int
name|initialOffset
init|=
literal|10
decl_stmt|;
name|int
name|additionalLength
init|=
name|BigArrays
operator|.
name|BYTE_PAGE_SIZE
decl_stmt|;
name|byte
index|[]
name|expectedData
init|=
name|randomizedByteArrayWithSize
argument_list|(
name|initialOffset
operator|+
name|additionalLength
argument_list|)
decl_stmt|;
comment|// first create initial offset
name|out
operator|.
name|writeBytes
argument_list|(
name|expectedData
argument_list|,
literal|0
argument_list|,
name|initialOffset
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|initialOffset
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// now write the rest - more than fits into the remaining first page
name|out
operator|.
name|writeBytes
argument_list|(
name|expectedData
argument_list|,
name|initialOffset
argument_list|,
name|additionalLength
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedData
operator|.
name|length
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedData
argument_list|,
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleFullPageBulkWriteWithOffsetCrossover
specifier|public
name|void
name|testSingleFullPageBulkWriteWithOffsetCrossover
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|int
name|initialOffset
init|=
literal|10
decl_stmt|;
name|int
name|additionalLength
init|=
name|BigArrays
operator|.
name|BYTE_PAGE_SIZE
operator|*
literal|2
decl_stmt|;
name|byte
index|[]
name|expectedData
init|=
name|randomizedByteArrayWithSize
argument_list|(
name|initialOffset
operator|+
name|additionalLength
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|expectedData
argument_list|,
literal|0
argument_list|,
name|initialOffset
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|initialOffset
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// now write the rest - more than fits into the remaining page + a full page after
comment|// that,
comment|// ie. we cross over into a third
name|out
operator|.
name|writeBytes
argument_list|(
name|expectedData
argument_list|,
name|initialOffset
argument_list|,
name|additionalLength
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedData
operator|.
name|length
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedData
argument_list|,
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleFullPage
specifier|public
name|void
name|testSingleFullPage
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|int
name|expectedSize
init|=
name|BigArrays
operator|.
name|BYTE_PAGE_SIZE
decl_stmt|;
name|byte
index|[]
name|expectedData
init|=
name|randomizedByteArrayWithSize
argument_list|(
name|expectedSize
argument_list|)
decl_stmt|;
comment|// write byte-by-byte
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedSize
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|expectedData
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedData
argument_list|,
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOneFullOneShortPage
specifier|public
name|void
name|testOneFullOneShortPage
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|int
name|expectedSize
init|=
name|BigArrays
operator|.
name|BYTE_PAGE_SIZE
operator|+
literal|10
decl_stmt|;
name|byte
index|[]
name|expectedData
init|=
name|randomizedByteArrayWithSize
argument_list|(
name|expectedSize
argument_list|)
decl_stmt|;
comment|// write byte-by-byte
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedSize
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|expectedData
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedData
argument_list|,
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTwoFullOneShortPage
specifier|public
name|void
name|testTwoFullOneShortPage
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|int
name|expectedSize
init|=
operator|(
name|BigArrays
operator|.
name|BYTE_PAGE_SIZE
operator|*
literal|2
operator|)
operator|+
literal|1
decl_stmt|;
name|byte
index|[]
name|expectedData
init|=
name|randomizedByteArrayWithSize
argument_list|(
name|expectedSize
argument_list|)
decl_stmt|;
comment|// write byte-by-byte
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedSize
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|expectedData
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedData
argument_list|,
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSeek
specifier|public
name|void
name|testSeek
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|int
name|position
init|=
literal|0
decl_stmt|;
name|assertEquals
argument_list|(
name|position
argument_list|,
name|out
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|seek
argument_list|(
name|position
operator|+=
literal|10
argument_list|)
expr_stmt|;
name|out
operator|.
name|seek
argument_list|(
name|position
operator|+=
name|BigArrays
operator|.
name|BYTE_PAGE_SIZE
argument_list|)
expr_stmt|;
name|out
operator|.
name|seek
argument_list|(
name|position
operator|+=
name|BigArrays
operator|.
name|BYTE_PAGE_SIZE
operator|+
literal|10
argument_list|)
expr_stmt|;
name|out
operator|.
name|seek
argument_list|(
name|position
operator|+=
name|BigArrays
operator|.
name|BYTE_PAGE_SIZE
operator|*
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|position
argument_list|,
name|out
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|position
argument_list|,
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkip
specifier|public
name|void
name|testSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|int
name|position
init|=
literal|0
decl_stmt|;
name|assertEquals
argument_list|(
name|position
argument_list|,
name|out
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|forward
init|=
literal|100
decl_stmt|;
name|out
operator|.
name|skip
argument_list|(
name|forward
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|position
operator|+
name|forward
argument_list|,
name|out
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleStreams
specifier|public
name|void
name|testSimpleStreams
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"requires a 64-bit JRE ... ?!"
argument_list|,
name|Constants
operator|.
name|JRE_IS_64BIT
argument_list|)
expr_stmt|;
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
operator|-
literal|3
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
literal|1.1f
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
literal|2.2
argument_list|)
expr_stmt|;
name|int
index|[]
name|intArray
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
decl_stmt|;
name|out
operator|.
name|writeGenericValue
argument_list|(
name|intArray
argument_list|)
expr_stmt|;
name|long
index|[]
name|longArray
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
decl_stmt|;
name|out
operator|.
name|writeGenericValue
argument_list|(
name|longArray
argument_list|)
expr_stmt|;
name|float
index|[]
name|floatArray
init|=
block|{
literal|1.1f
block|,
literal|2.2f
block|,
literal|3.3f
block|}
decl_stmt|;
name|out
operator|.
name|writeGenericValue
argument_list|(
name|floatArray
argument_list|)
expr_stmt|;
name|double
index|[]
name|doubleArray
init|=
block|{
literal|1.1
block|,
literal|2.2
block|,
literal|3.3
block|}
decl_stmt|;
name|out
operator|.
name|writeGenericValue
argument_list|(
name|doubleArray
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
literal|"goodbye"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeGenericValue
argument_list|(
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
literal|"bytesref"
argument_list|)
argument_list|)
expr_stmt|;
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readBoolean
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readShort
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
operator|-
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readVLong
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|in
operator|.
name|readFloat
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|1.1
argument_list|,
literal|0.0001
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readDouble
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|2.2
argument_list|,
literal|0.0001
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readGenericValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Object
operator|)
name|intArray
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readGenericValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Object
operator|)
name|longArray
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readGenericValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Object
operator|)
name|floatArray
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readGenericValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Object
operator|)
name|doubleArray
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"goodbye"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|in
operator|.
name|readGenericValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Object
operator|)
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
literal|"bytesref"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// we ignore this test for now since all existing callers of BytesStreamOutput happily
comment|// call bytes() after close().
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://github.com/elastic/elasticsearch/issues/12620"
argument_list|)
annotation|@
name|Test
DECL|method|testAccessAfterClose
specifier|public
name|void
name|testAccessAfterClose
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
comment|// immediately close
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|out
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
comment|// writing a single byte must fail
try|try
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected IllegalStateException: stream closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|iex1
parameter_list|)
block|{
comment|// expected
block|}
comment|// writing in bulk must fail
try|try
block|{
name|out
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected IllegalStateException: stream closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|iex1
parameter_list|)
block|{
comment|// expected
block|}
comment|// toByteArray() must fail
try|try
block|{
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected IllegalStateException: stream closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|iex1
parameter_list|)
block|{
comment|// expected
block|}
block|}
comment|// create& fill byte[] with randomized data
DECL|method|randomizedByteArrayWithSize
specifier|protected
name|byte
index|[]
name|randomizedByteArrayWithSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|getRandom
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
block|}
end_class

end_unit

