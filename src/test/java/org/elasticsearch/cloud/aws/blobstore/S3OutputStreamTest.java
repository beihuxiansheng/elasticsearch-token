begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.aws.blobstore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|aws
operator|.
name|blobstore
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
name|base
operator|.
name|Charsets
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
name|ElasticsearchTestCase
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
name|util
operator|.
name|Arrays
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
name|Streams
operator|.
name|copy
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
comment|/**  * Unit test for {@link S3OutputStream}.  */
end_comment

begin_class
DECL|class|S3OutputStreamTest
specifier|public
class|class
name|S3OutputStreamTest
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
name|S3BlobStore
operator|.
name|MIN_BUFFER_SIZE
operator|.
name|bytesAsInt
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testWriteLessDataThanBufferSize
specifier|public
name|void
name|testWriteLessDataThanBufferSize
parameter_list|()
throws|throws
name|IOException
block|{
name|MockDefaultS3OutputStream
name|out
init|=
name|newS3OutputStream
argument_list|(
name|BUFFER_SIZE
argument_list|)
decl_stmt|;
name|byte
index|[]
name|content
init|=
name|randomUnicodeOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|512
argument_list|)
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|copy
argument_list|(
name|content
argument_list|,
name|out
argument_list|)
expr_stmt|;
comment|// Checks length& content
name|assertThat
argument_list|(
name|out
operator|.
name|getLength
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|content
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|content
argument_list|,
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Checks single/multi part upload
name|assertThat
argument_list|(
name|out
operator|.
name|getBufferSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|BUFFER_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getFlushCount
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
name|out
operator|.
name|getNumberOfUploadRequests
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|out
operator|.
name|isMultipart
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteSameDataThanBufferSize
specifier|public
name|void
name|testWriteSameDataThanBufferSize
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|randomIntBetween
argument_list|(
name|BUFFER_SIZE
argument_list|,
literal|2
operator|*
name|BUFFER_SIZE
argument_list|)
decl_stmt|;
name|MockDefaultS3OutputStream
name|out
init|=
name|newS3OutputStream
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|content
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|size
argument_list|)
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|content
operator|.
name|write
argument_list|(
name|randomByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|copy
argument_list|(
name|content
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
comment|// Checks length& content
name|assertThat
argument_list|(
name|out
operator|.
name|getLength
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|size
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|content
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Checks single/multi part upload
name|assertThat
argument_list|(
name|out
operator|.
name|getBufferSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getFlushCount
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
name|out
operator|.
name|getNumberOfUploadRequests
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|out
operator|.
name|isMultipart
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Slow
DECL|method|testWriteExactlyNTimesMoreDataThanBufferSize
specifier|public
name|void
name|testWriteExactlyNTimesMoreDataThanBufferSize
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|n
init|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|n
operator|*
name|BUFFER_SIZE
decl_stmt|;
name|ByteArrayOutputStream
name|content
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|length
argument_list|)
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
name|i
operator|++
control|)
block|{
name|content
operator|.
name|write
argument_list|(
name|randomByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|MockDefaultS3OutputStream
name|out
init|=
name|newS3OutputStream
argument_list|(
name|BUFFER_SIZE
argument_list|)
decl_stmt|;
name|copy
argument_list|(
name|content
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
comment|// Checks length& content
name|assertThat
argument_list|(
name|out
operator|.
name|getLength
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|content
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Checks single/multi part upload
name|assertThat
argument_list|(
name|out
operator|.
name|getBufferSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|BUFFER_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getFlushCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getNumberOfUploadRequests
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|isMultipart
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteRandomNumberOfBytes
specifier|public
name|void
name|testWriteRandomNumberOfBytes
parameter_list|()
throws|throws
name|IOException
block|{
name|Integer
name|randomBufferSize
init|=
name|randomIntBetween
argument_list|(
name|BUFFER_SIZE
argument_list|,
literal|2
operator|*
name|BUFFER_SIZE
argument_list|)
decl_stmt|;
name|MockDefaultS3OutputStream
name|out
init|=
name|newS3OutputStream
argument_list|(
name|randomBufferSize
argument_list|)
decl_stmt|;
name|Integer
name|randomLength
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|2
operator|*
name|BUFFER_SIZE
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|content
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|randomLength
argument_list|)
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
name|randomLength
condition|;
name|i
operator|++
control|)
block|{
name|content
operator|.
name|write
argument_list|(
name|randomByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|copy
argument_list|(
name|content
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
comment|// Checks length& content
name|assertThat
argument_list|(
name|out
operator|.
name|getLength
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|randomLength
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|content
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getBufferSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|randomBufferSize
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|times
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|randomLength
operator|.
name|doubleValue
argument_list|()
operator|/
name|randomBufferSize
operator|.
name|doubleValue
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getFlushCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|times
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|times
operator|>
literal|1
condition|)
block|{
name|assertTrue
argument_list|(
name|out
operator|.
name|isMultipart
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|out
operator|.
name|isMultipart
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testWrongBufferSize
specifier|public
name|void
name|testWrongBufferSize
parameter_list|()
throws|throws
name|IOException
block|{
name|Integer
name|randomBufferSize
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|4
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|MockDefaultS3OutputStream
name|out
init|=
name|newS3OutputStream
argument_list|(
name|randomBufferSize
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Buffer size can't be smaller than 5mb"
argument_list|)
expr_stmt|;
block|}
DECL|method|newS3OutputStream
specifier|private
name|MockDefaultS3OutputStream
name|newS3OutputStream
parameter_list|(
name|int
name|bufferSizeInBytes
parameter_list|)
block|{
return|return
operator|new
name|MockDefaultS3OutputStream
argument_list|(
name|bufferSizeInBytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

