begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.index.analysis
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Listeners
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
operator|.
name|Scope
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|TimeoutSuite
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
name|ArrayUtil
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageEstimator
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
name|TimeUnits
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
name|junit
operator|.
name|listeners
operator|.
name|ReproduceInfoPrinter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * @deprecated Remove when IndexableBinaryStringTools is removed.  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|Listeners
argument_list|(
block|{
name|ReproduceInfoPrinter
operator|.
name|class
block|}
argument_list|)
annotation|@
name|ThreadLeakScope
argument_list|(
name|Scope
operator|.
name|NONE
argument_list|)
annotation|@
name|TimeoutSuite
argument_list|(
name|millis
operator|=
name|TimeUnits
operator|.
name|HOUR
argument_list|)
annotation|@
name|LuceneTestCase
operator|.
name|SuppressSysoutChecks
argument_list|(
name|bugUrl
operator|=
literal|"we log a lot on purpose"
argument_list|)
DECL|class|TestIndexableBinaryStringTools
specifier|public
class|class
name|TestIndexableBinaryStringTools
extends|extends
name|LuceneTestCase
block|{
DECL|field|NUM_RANDOM_TESTS
specifier|private
specifier|static
name|int
name|NUM_RANDOM_TESTS
decl_stmt|;
DECL|field|MAX_RANDOM_BINARY_LENGTH
specifier|private
specifier|static
name|int
name|MAX_RANDOM_BINARY_LENGTH
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|NUM_RANDOM_TESTS
operator|=
name|atLeast
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|MAX_RANDOM_BINARY_LENGTH
operator|=
name|atLeast
argument_list|(
literal|300
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleBinaryRoundTrip
specifier|public
name|void
name|testSingleBinaryRoundTrip
parameter_list|()
block|{
name|byte
index|[]
name|binary
init|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0x23
block|,
operator|(
name|byte
operator|)
literal|0x98
block|,
operator|(
name|byte
operator|)
literal|0x13
block|,
operator|(
name|byte
operator|)
literal|0xE4
block|,
operator|(
name|byte
operator|)
literal|0x76
block|,
operator|(
name|byte
operator|)
literal|0x41
block|,
operator|(
name|byte
operator|)
literal|0xB2
block|,
operator|(
name|byte
operator|)
literal|0xC9
block|,
operator|(
name|byte
operator|)
literal|0x7F
block|,
operator|(
name|byte
operator|)
literal|0x0A
block|,
operator|(
name|byte
operator|)
literal|0xA6
block|,
operator|(
name|byte
operator|)
literal|0xD8
block|}
decl_stmt|;
name|int
name|encodedLen
init|=
name|IndexableBinaryStringTools
operator|.
name|getEncodedLength
argument_list|(
name|binary
argument_list|,
literal|0
argument_list|,
name|binary
operator|.
name|length
argument_list|)
decl_stmt|;
name|char
name|encoded
index|[]
init|=
operator|new
name|char
index|[
name|encodedLen
index|]
decl_stmt|;
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|binary
argument_list|,
literal|0
argument_list|,
name|binary
operator|.
name|length
argument_list|,
name|encoded
argument_list|,
literal|0
argument_list|,
name|encoded
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|decodedLen
init|=
name|IndexableBinaryStringTools
operator|.
name|getDecodedLength
argument_list|(
name|encoded
argument_list|,
literal|0
argument_list|,
name|encoded
operator|.
name|length
argument_list|)
decl_stmt|;
name|byte
name|decoded
index|[]
init|=
operator|new
name|byte
index|[
name|decodedLen
index|]
decl_stmt|;
name|IndexableBinaryStringTools
operator|.
name|decode
argument_list|(
name|encoded
argument_list|,
literal|0
argument_list|,
name|encoded
operator|.
name|length
argument_list|,
name|decoded
argument_list|,
literal|0
argument_list|,
name|decoded
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Round trip decode/decode returned different results:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"original: "
operator|+
name|binaryDump
argument_list|(
name|binary
argument_list|,
name|binary
operator|.
name|length
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|" encoded: "
operator|+
name|charArrayDump
argument_list|(
name|encoded
argument_list|,
name|encoded
operator|.
name|length
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|" decoded: "
operator|+
name|binaryDump
argument_list|(
name|decoded
argument_list|,
name|decoded
operator|.
name|length
argument_list|)
argument_list|,
name|binaryDump
argument_list|(
name|binary
argument_list|,
name|binary
operator|.
name|length
argument_list|)
argument_list|,
name|binaryDump
argument_list|(
name|decoded
argument_list|,
name|decoded
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEncodedSortability
specifier|public
name|void
name|testEncodedSortability
parameter_list|()
block|{
name|byte
index|[]
name|originalArray1
init|=
operator|new
name|byte
index|[
name|MAX_RANDOM_BINARY_LENGTH
index|]
decl_stmt|;
name|char
index|[]
name|originalString1
init|=
operator|new
name|char
index|[
name|MAX_RANDOM_BINARY_LENGTH
index|]
decl_stmt|;
name|char
index|[]
name|encoded1
init|=
operator|new
name|char
index|[
name|MAX_RANDOM_BINARY_LENGTH
operator|*
literal|10
index|]
decl_stmt|;
name|byte
index|[]
name|original2
init|=
operator|new
name|byte
index|[
name|MAX_RANDOM_BINARY_LENGTH
index|]
decl_stmt|;
name|char
index|[]
name|originalString2
init|=
operator|new
name|char
index|[
name|MAX_RANDOM_BINARY_LENGTH
index|]
decl_stmt|;
name|char
index|[]
name|encoded2
init|=
operator|new
name|char
index|[
name|MAX_RANDOM_BINARY_LENGTH
operator|*
literal|10
index|]
decl_stmt|;
for|for
control|(
name|int
name|testNum
init|=
literal|0
init|;
name|testNum
operator|<
name|NUM_RANDOM_TESTS
condition|;
operator|++
name|testNum
control|)
block|{
name|int
name|numBytes1
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|MAX_RANDOM_BINARY_LENGTH
operator|-
literal|1
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// Min == 1
for|for
control|(
name|int
name|byteNum
init|=
literal|0
init|;
name|byteNum
operator|<
name|numBytes1
condition|;
operator|++
name|byteNum
control|)
block|{
name|int
name|randomInt
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|0x100
argument_list|)
decl_stmt|;
name|originalArray1
index|[
name|byteNum
index|]
operator|=
operator|(
name|byte
operator|)
name|randomInt
expr_stmt|;
name|originalString1
index|[
name|byteNum
index|]
operator|=
operator|(
name|char
operator|)
name|randomInt
expr_stmt|;
block|}
name|int
name|numBytes2
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|MAX_RANDOM_BINARY_LENGTH
operator|-
literal|1
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// Min == 1
for|for
control|(
name|int
name|byteNum
init|=
literal|0
init|;
name|byteNum
operator|<
name|numBytes2
condition|;
operator|++
name|byteNum
control|)
block|{
name|int
name|randomInt
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|0x100
argument_list|)
decl_stmt|;
name|original2
index|[
name|byteNum
index|]
operator|=
operator|(
name|byte
operator|)
name|randomInt
expr_stmt|;
name|originalString2
index|[
name|byteNum
index|]
operator|=
operator|(
name|char
operator|)
name|randomInt
expr_stmt|;
block|}
name|int
name|originalComparison
init|=
operator|new
name|String
argument_list|(
name|originalString1
argument_list|,
literal|0
argument_list|,
name|numBytes1
argument_list|)
operator|.
name|compareTo
argument_list|(
operator|new
name|String
argument_list|(
name|originalString2
argument_list|,
literal|0
argument_list|,
name|numBytes2
argument_list|)
argument_list|)
decl_stmt|;
name|originalComparison
operator|=
name|originalComparison
operator|<
literal|0
condition|?
operator|-
literal|1
else|:
name|originalComparison
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
expr_stmt|;
name|int
name|encodedLen1
init|=
name|IndexableBinaryStringTools
operator|.
name|getEncodedLength
argument_list|(
name|originalArray1
argument_list|,
literal|0
argument_list|,
name|numBytes1
argument_list|)
decl_stmt|;
if|if
condition|(
name|encodedLen1
operator|>
name|encoded1
operator|.
name|length
condition|)
name|encoded1
operator|=
operator|new
name|char
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|encodedLen1
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_CHAR
argument_list|)
index|]
expr_stmt|;
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|originalArray1
argument_list|,
literal|0
argument_list|,
name|numBytes1
argument_list|,
name|encoded1
argument_list|,
literal|0
argument_list|,
name|encodedLen1
argument_list|)
expr_stmt|;
name|int
name|encodedLen2
init|=
name|IndexableBinaryStringTools
operator|.
name|getEncodedLength
argument_list|(
name|original2
argument_list|,
literal|0
argument_list|,
name|numBytes2
argument_list|)
decl_stmt|;
if|if
condition|(
name|encodedLen2
operator|>
name|encoded2
operator|.
name|length
condition|)
name|encoded2
operator|=
operator|new
name|char
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|encodedLen2
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_CHAR
argument_list|)
index|]
expr_stmt|;
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|original2
argument_list|,
literal|0
argument_list|,
name|numBytes2
argument_list|,
name|encoded2
argument_list|,
literal|0
argument_list|,
name|encodedLen2
argument_list|)
expr_stmt|;
name|int
name|encodedComparison
init|=
operator|new
name|String
argument_list|(
name|encoded1
argument_list|,
literal|0
argument_list|,
name|encodedLen1
argument_list|)
operator|.
name|compareTo
argument_list|(
operator|new
name|String
argument_list|(
name|encoded2
argument_list|,
literal|0
argument_list|,
name|encodedLen2
argument_list|)
argument_list|)
decl_stmt|;
name|encodedComparison
operator|=
name|encodedComparison
operator|<
literal|0
condition|?
operator|-
literal|1
else|:
name|encodedComparison
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test #"
operator|+
operator|(
name|testNum
operator|+
literal|1
operator|)
operator|+
literal|": Original bytes and encoded chars compare differently:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|" binary 1: "
operator|+
name|binaryDump
argument_list|(
name|originalArray1
argument_list|,
name|numBytes1
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|" binary 2: "
operator|+
name|binaryDump
argument_list|(
name|original2
argument_list|,
name|numBytes2
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"encoded 1: "
operator|+
name|charArrayDump
argument_list|(
name|encoded1
argument_list|,
name|encodedLen1
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"encoded 2: "
operator|+
name|charArrayDump
argument_list|(
name|encoded2
argument_list|,
name|encodedLen2
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|,
name|originalComparison
argument_list|,
name|encodedComparison
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEmptyInput
specifier|public
name|void
name|testEmptyInput
parameter_list|()
block|{
name|byte
index|[]
name|binary
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
name|int
name|encodedLen
init|=
name|IndexableBinaryStringTools
operator|.
name|getEncodedLength
argument_list|(
name|binary
argument_list|,
literal|0
argument_list|,
name|binary
operator|.
name|length
argument_list|)
decl_stmt|;
name|char
index|[]
name|encoded
init|=
operator|new
name|char
index|[
name|encodedLen
index|]
decl_stmt|;
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|binary
argument_list|,
literal|0
argument_list|,
name|binary
operator|.
name|length
argument_list|,
name|encoded
argument_list|,
literal|0
argument_list|,
name|encoded
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|decodedLen
init|=
name|IndexableBinaryStringTools
operator|.
name|getDecodedLength
argument_list|(
name|encoded
argument_list|,
literal|0
argument_list|,
name|encoded
operator|.
name|length
argument_list|)
decl_stmt|;
name|byte
index|[]
name|decoded
init|=
operator|new
name|byte
index|[
name|decodedLen
index|]
decl_stmt|;
name|IndexableBinaryStringTools
operator|.
name|decode
argument_list|(
name|encoded
argument_list|,
literal|0
argument_list|,
name|encoded
operator|.
name|length
argument_list|,
name|decoded
argument_list|,
literal|0
argument_list|,
name|decoded
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"decoded empty input was not empty"
argument_list|,
name|decoded
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testAllNullInput
specifier|public
name|void
name|testAllNullInput
parameter_list|()
block|{
name|byte
index|[]
name|binary
init|=
operator|new
name|byte
index|[]
block|{
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
name|int
name|encodedLen
init|=
name|IndexableBinaryStringTools
operator|.
name|getEncodedLength
argument_list|(
name|binary
argument_list|,
literal|0
argument_list|,
name|binary
operator|.
name|length
argument_list|)
decl_stmt|;
name|char
name|encoded
index|[]
init|=
operator|new
name|char
index|[
name|encodedLen
index|]
decl_stmt|;
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|binary
argument_list|,
literal|0
argument_list|,
name|binary
operator|.
name|length
argument_list|,
name|encoded
argument_list|,
literal|0
argument_list|,
name|encoded
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|decodedLen
init|=
name|IndexableBinaryStringTools
operator|.
name|getDecodedLength
argument_list|(
name|encoded
argument_list|,
literal|0
argument_list|,
name|encoded
operator|.
name|length
argument_list|)
decl_stmt|;
name|byte
index|[]
name|decoded
init|=
operator|new
name|byte
index|[
name|decodedLen
index|]
decl_stmt|;
name|IndexableBinaryStringTools
operator|.
name|decode
argument_list|(
name|encoded
argument_list|,
literal|0
argument_list|,
name|encoded
operator|.
name|length
argument_list|,
name|decoded
argument_list|,
literal|0
argument_list|,
name|decoded
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Round trip decode/decode returned different results:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"  original: "
operator|+
name|binaryDump
argument_list|(
name|binary
argument_list|,
name|binary
operator|.
name|length
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"decodedBuf: "
operator|+
name|binaryDump
argument_list|(
name|decoded
argument_list|,
name|decoded
operator|.
name|length
argument_list|)
argument_list|,
name|binaryDump
argument_list|(
name|binary
argument_list|,
name|binary
operator|.
name|length
argument_list|)
argument_list|,
name|binaryDump
argument_list|(
name|decoded
argument_list|,
name|decoded
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomBinaryRoundTrip
specifier|public
name|void
name|testRandomBinaryRoundTrip
parameter_list|()
block|{
name|byte
index|[]
name|binary
init|=
operator|new
name|byte
index|[
name|MAX_RANDOM_BINARY_LENGTH
index|]
decl_stmt|;
name|char
index|[]
name|encoded
init|=
operator|new
name|char
index|[
name|MAX_RANDOM_BINARY_LENGTH
operator|*
literal|10
index|]
decl_stmt|;
name|byte
index|[]
name|decoded
init|=
operator|new
name|byte
index|[
name|MAX_RANDOM_BINARY_LENGTH
index|]
decl_stmt|;
for|for
control|(
name|int
name|testNum
init|=
literal|0
init|;
name|testNum
operator|<
name|NUM_RANDOM_TESTS
condition|;
operator|++
name|testNum
control|)
block|{
name|int
name|numBytes
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|MAX_RANDOM_BINARY_LENGTH
operator|-
literal|1
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// Min == 1
for|for
control|(
name|int
name|byteNum
init|=
literal|0
init|;
name|byteNum
operator|<
name|numBytes
condition|;
operator|++
name|byteNum
control|)
block|{
name|binary
index|[
name|byteNum
index|]
operator|=
operator|(
name|byte
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|0x100
argument_list|)
expr_stmt|;
block|}
name|int
name|encodedLen
init|=
name|IndexableBinaryStringTools
operator|.
name|getEncodedLength
argument_list|(
name|binary
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|encoded
operator|.
name|length
operator|<
name|encodedLen
condition|)
name|encoded
operator|=
operator|new
name|char
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|encodedLen
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_CHAR
argument_list|)
index|]
expr_stmt|;
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|binary
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|,
name|encoded
argument_list|,
literal|0
argument_list|,
name|encodedLen
argument_list|)
expr_stmt|;
name|int
name|decodedLen
init|=
name|IndexableBinaryStringTools
operator|.
name|getDecodedLength
argument_list|(
name|encoded
argument_list|,
literal|0
argument_list|,
name|encodedLen
argument_list|)
decl_stmt|;
name|IndexableBinaryStringTools
operator|.
name|decode
argument_list|(
name|encoded
argument_list|,
literal|0
argument_list|,
name|encodedLen
argument_list|,
name|decoded
argument_list|,
literal|0
argument_list|,
name|decodedLen
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test #"
operator|+
operator|(
name|testNum
operator|+
literal|1
operator|)
operator|+
literal|": Round trip decode/decode returned different results:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"  original: "
operator|+
name|binaryDump
argument_list|(
name|binary
argument_list|,
name|numBytes
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"encodedBuf: "
operator|+
name|charArrayDump
argument_list|(
name|encoded
argument_list|,
name|encodedLen
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"decodedBuf: "
operator|+
name|binaryDump
argument_list|(
name|decoded
argument_list|,
name|decodedLen
argument_list|)
argument_list|,
name|binaryDump
argument_list|(
name|binary
argument_list|,
name|numBytes
argument_list|)
argument_list|,
name|binaryDump
argument_list|(
name|decoded
argument_list|,
name|decodedLen
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|binaryDump
specifier|public
name|String
name|binaryDump
parameter_list|(
name|byte
index|[]
name|binary
parameter_list|,
name|int
name|numBytes
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|byteNum
init|=
literal|0
init|;
name|byteNum
operator|<
name|numBytes
condition|;
operator|++
name|byteNum
control|)
block|{
name|String
name|hex
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
name|binary
index|[
name|byteNum
index|]
operator|&
literal|0xFF
argument_list|)
decl_stmt|;
if|if
condition|(
name|hex
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|hex
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|byteNum
operator|<
name|numBytes
operator|-
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|charArrayDump
specifier|public
name|String
name|charArrayDump
parameter_list|(
name|char
index|[]
name|charArray
parameter_list|,
name|int
name|numBytes
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|charNum
init|=
literal|0
init|;
name|charNum
operator|<
name|numBytes
condition|;
operator|++
name|charNum
control|)
block|{
name|String
name|hex
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
name|charArray
index|[
name|charNum
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|digit
init|=
literal|0
init|;
name|digit
operator|<
literal|4
operator|-
name|hex
operator|.
name|length
argument_list|()
condition|;
operator|++
name|digit
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|hex
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|charNum
operator|<
name|numBytes
operator|-
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

