begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.compress.lzf
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
operator|.
name|lzf
package|;
end_package

begin_import
import|import
name|com
operator|.
name|ning
operator|.
name|compress
operator|.
name|lzf
operator|.
name|ChunkDecoder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ning
operator|.
name|compress
operator|.
name|lzf
operator|.
name|ChunkEncoder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ning
operator|.
name|compress
operator|.
name|lzf
operator|.
name|LZFChunk
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ning
operator|.
name|compress
operator|.
name|lzf
operator|.
name|util
operator|.
name|ChunkDecoderFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ning
operator|.
name|compress
operator|.
name|lzf
operator|.
name|util
operator|.
name|ChunkEncoderFactory
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
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|io
operator|.
name|InputStream
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
comment|/**  * Test an extremely rare corruption produced by the pure java impl of ChunkEncoder.  */
end_comment

begin_class
DECL|class|CorruptedCompressorTests
specifier|public
class|class
name|CorruptedCompressorTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|method|testCorruption
specifier|public
name|void
name|testCorruption
parameter_list|()
throws|throws
name|IOException
block|{
comment|// this test generates a hash collision: [0,1,153,64] hashes the same as [1,153,64,64]
comment|// and then leverages the bug s/inPos/0/ to corrupt the array
comment|// the first array is used to insert a reference from this hash to offset 6
comment|// and then the hash table is reused and still thinks that there is such a hash at position 6
comment|// and at position 7, it finds a sequence with the same hash
comment|// so it inserts a buggy reference
name|byte
index|[]
name|b1
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
operator|(
name|byte
operator|)
literal|153
block|,
literal|64
block|,
literal|64
block|,
literal|64
block|,
literal|9
block|,
literal|9
block|,
literal|9
block|,
literal|9
block|,
literal|9
block|,
literal|9
block|,
literal|9
block|,
literal|9
block|,
literal|9
block|,
literal|9
block|}
decl_stmt|;
name|byte
index|[]
name|b2
init|=
operator|new
name|byte
index|[]
block|{
literal|1
block|,
operator|(
name|byte
operator|)
literal|153
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
operator|(
name|byte
operator|)
literal|153
block|,
literal|64
block|,
literal|64
block|,
literal|64
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
decl_stmt|;
name|ChunkEncoder
name|encoder
init|=
name|ChunkEncoderFactory
operator|.
name|safeInstance
argument_list|()
decl_stmt|;
name|ChunkDecoder
name|decoder
init|=
name|ChunkDecoderFactory
operator|.
name|safeInstance
argument_list|()
decl_stmt|;
name|check
argument_list|(
name|encoder
argument_list|,
name|decoder
argument_list|,
name|b1
argument_list|,
literal|0
argument_list|,
name|b1
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|int
name|off
init|=
literal|6
decl_stmt|;
name|check
argument_list|(
name|encoder
argument_list|,
name|decoder
argument_list|,
name|b2
argument_list|,
name|off
argument_list|,
name|b2
operator|.
name|length
operator|-
name|off
argument_list|)
expr_stmt|;
block|}
DECL|method|check
specifier|private
name|void
name|check
parameter_list|(
name|ChunkEncoder
name|encoder
parameter_list|,
name|ChunkDecoder
name|decoder
parameter_list|,
name|byte
index|[]
name|bytes
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
name|ByteArrayOutputStream
name|outputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|LZFChunk
operator|.
name|MAX_CHUNK_LEN
index|]
decl_stmt|;
name|byte
index|[]
name|output
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|expected
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|encoder
operator|.
name|encodeAndWriteChunk
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|outputStream
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|Arrays
operator|.
name|copyOf
argument_list|(
name|outputStream
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|InputStream
name|inputStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|outputStream
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|decoder
operator|.
name|decodeChunk
argument_list|(
name|inputStream
argument_list|,
name|buffer
argument_list|,
name|output
argument_list|)
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|Arrays
operator|.
name|copyOf
argument_list|(
name|output
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

