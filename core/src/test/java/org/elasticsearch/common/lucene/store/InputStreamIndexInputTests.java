begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
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
name|IndexOutput
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
name|RAMDirectory
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
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|lessThan
import|;
end_import

begin_class
DECL|class|InputStreamIndexInputTests
specifier|public
class|class
name|InputStreamIndexInputTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSingleReadSingleByteLimit
specifier|public
name|void
name|testSingleReadSingleByteLimit
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|InputStreamIndexInput
name|is
init|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
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
name|is
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|InputStreamIndexInput
name|is
init|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
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
name|is
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
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|InputStreamIndexInput
name|is
init|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
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
DECL|method|testReadMultiSingleByteLimit1
specifier|public
name|void
name|testReadMultiSingleByteLimit1
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|byte
index|[]
name|read
init|=
operator|new
name|byte
index|[
literal|2
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|InputStreamIndexInput
name|is
init|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
argument_list|(
name|read
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|read
index|[
literal|0
index|]
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
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|InputStreamIndexInput
name|is
init|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
argument_list|(
name|read
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|read
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|InputStreamIndexInput
name|is
init|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
argument_list|(
name|read
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleReadTwoBytesLimit
specifier|public
name|void
name|testSingleReadTwoBytesLimit
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|InputStreamIndexInput
name|is
init|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
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
name|is
operator|.
name|read
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
name|is
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
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
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
name|is
operator|.
name|read
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
name|is
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
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
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
name|is
operator|.
name|read
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
name|is
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
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
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
DECL|method|testReadMultiTwoBytesLimit1
specifier|public
name|void
name|testReadMultiTwoBytesLimit1
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|byte
index|[]
name|read
init|=
operator|new
name|byte
index|[
literal|2
index|]
decl_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|InputStreamIndexInput
name|is
init|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
argument_list|(
name|read
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|read
index|[
literal|0
index|]
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
name|read
index|[
literal|1
index|]
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
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
argument_list|(
name|read
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|read
index|[
literal|0
index|]
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
name|read
index|[
literal|1
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
argument_list|(
name|read
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|read
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|read
index|[
literal|1
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
argument_list|(
name|read
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReadMultiFourBytesLimit
specifier|public
name|void
name|testReadMultiFourBytesLimit
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|byte
index|[]
name|read
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|InputStreamIndexInput
name|is
init|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
argument_list|(
name|read
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|read
index|[
literal|0
index|]
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
name|read
index|[
literal|1
index|]
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
name|read
index|[
literal|2
index|]
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
name|read
index|[
literal|3
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
argument_list|(
name|read
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|read
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|read
index|[
literal|1
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|actualSizeToRead
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
argument_list|(
name|read
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMarkRest
specifier|public
name|void
name|testMarkRest
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|InputStreamIndexInput
name|is
init|=
operator|new
name|InputStreamIndexInput
argument_list|(
name|input
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|markSupported
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
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
name|is
operator|.
name|read
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|.
name|mark
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
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
name|is
operator|.
name|read
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|is
operator|.
name|read
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
name|is
operator|.
name|read
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

