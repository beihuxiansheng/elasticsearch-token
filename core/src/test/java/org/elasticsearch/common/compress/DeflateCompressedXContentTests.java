begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.compress
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
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
name|TestUtil
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
name|bytes
operator|.
name|BytesReference
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
name|StreamOutput
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
name|Assert
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
name|Random
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
name|not
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|DeflateCompressedXContentTests
specifier|public
class|class
name|DeflateCompressedXContentTests
extends|extends
name|ESTestCase
block|{
DECL|field|compressor
specifier|private
specifier|final
name|Compressor
name|compressor
init|=
operator|new
name|DeflateCompressor
argument_list|()
decl_stmt|;
DECL|method|assertEquals
specifier|private
name|void
name|assertEquals
parameter_list|(
name|CompressedXContent
name|s1
parameter_list|,
name|CompressedXContent
name|s2
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|s1
operator|.
name|uncompressed
argument_list|()
argument_list|,
name|s2
operator|.
name|uncompressed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|s2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|simpleTests
specifier|public
name|void
name|simpleTests
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|str
init|=
literal|"---\nf:this is a simple string"
decl_stmt|;
name|CompressedXContent
name|cstr
init|=
operator|new
name|CompressedXContent
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cstr
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|CompressedXContent
argument_list|(
name|str
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|cstr
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|str2
init|=
literal|"---\nf:this is a simple string 2"
decl_stmt|;
name|CompressedXContent
name|cstr2
init|=
operator|new
name|CompressedXContent
argument_list|(
name|str2
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cstr2
operator|.
name|string
argument_list|()
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|str
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|CompressedXContent
argument_list|(
name|str2
argument_list|)
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|cstr
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|CompressedXContent
argument_list|(
name|str2
argument_list|)
argument_list|,
name|cstr2
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|IOException
block|{
name|Random
name|r
init|=
name|random
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|string
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|r
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
comment|// hack to make it detected as YAML
name|string
operator|=
literal|"---\n"
operator|+
name|string
expr_stmt|;
name|CompressedXContent
name|compressedXContent
init|=
operator|new
name|CompressedXContent
argument_list|(
name|string
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|compressedXContent
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|string
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDifferentCompressedRepresentation
specifier|public
name|void
name|testDifferentCompressedRepresentation
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|b
init|=
literal|"---\nf:abcdefghijabcdefghij"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|BytesStreamOutput
name|bout
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|StreamOutput
name|out
init|=
name|compressor
operator|.
name|streamOutput
argument_list|(
name|bout
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|BytesReference
name|b1
init|=
name|bout
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|bout
operator|=
operator|new
name|BytesStreamOutput
argument_list|()
expr_stmt|;
name|out
operator|=
name|compressor
operator|.
name|streamOutput
argument_list|(
name|bout
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|BytesReference
name|b2
init|=
name|bout
operator|.
name|bytes
argument_list|()
decl_stmt|;
comment|// because of the intermediate flush, the two compressed representations
comment|// are different. It can also happen for other reasons like if hash tables
comment|// of different size are being used
name|assertFalse
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
comment|// we used the compressed representation directly and did not recompress
name|assertArrayEquals
argument_list|(
name|BytesReference
operator|.
name|toBytes
argument_list|(
name|b1
argument_list|)
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|b1
argument_list|)
operator|.
name|compressed
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|BytesReference
operator|.
name|toBytes
argument_list|(
name|b2
argument_list|)
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|b2
argument_list|)
operator|.
name|compressed
argument_list|()
argument_list|)
expr_stmt|;
comment|// but compressedstring instances are still equal
name|assertEquals
argument_list|(
operator|new
name|CompressedXContent
argument_list|(
name|b1
argument_list|)
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testHashCode
specifier|public
name|void
name|testHashCode
parameter_list|()
throws|throws
name|IOException
block|{
name|assertFalse
argument_list|(
operator|new
name|CompressedXContent
argument_list|(
literal|"{\"a\":\"b\"}"
argument_list|)
operator|.
name|hashCode
argument_list|()
operator|==
operator|new
name|CompressedXContent
argument_list|(
literal|"{\"a\":\"c\"}"
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

