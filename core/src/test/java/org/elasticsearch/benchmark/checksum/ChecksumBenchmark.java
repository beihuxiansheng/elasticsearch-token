begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.benchmark.checksum
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|benchmark
operator|.
name|checksum
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Adler32
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|CRC32
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ChecksumBenchmark
specifier|public
class|class
name|ChecksumBenchmark
block|{
DECL|field|BATCH_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|BATCH_SIZE
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Warning up"
argument_list|)
expr_stmt|;
name|long
name|warmSize
init|=
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"1g"
argument_list|,
literal|null
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|crc
argument_list|(
name|warmSize
argument_list|)
expr_stmt|;
name|adler
argument_list|(
name|warmSize
argument_list|)
expr_stmt|;
name|md5
argument_list|(
name|warmSize
argument_list|)
expr_stmt|;
name|long
name|dataSize
init|=
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"10g"
argument_list|,
literal|null
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Running size: "
operator|+
name|dataSize
argument_list|)
expr_stmt|;
name|crc
argument_list|(
name|dataSize
argument_list|)
expr_stmt|;
name|adler
argument_list|(
name|dataSize
argument_list|)
expr_stmt|;
name|md5
argument_list|(
name|dataSize
argument_list|)
expr_stmt|;
block|}
DECL|method|crc
specifier|private
specifier|static
name|void
name|crc
parameter_list|(
name|long
name|dataSize
parameter_list|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|CRC32
name|crc
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|BATCH_SIZE
index|]
decl_stmt|;
name|long
name|iter
init|=
name|dataSize
operator|/
name|BATCH_SIZE
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|crc
operator|.
name|update
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|crc
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CRC took "
operator|+
operator|new
name|TimeValue
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|adler
specifier|private
specifier|static
name|void
name|adler
parameter_list|(
name|long
name|dataSize
parameter_list|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Adler32
name|crc
init|=
operator|new
name|Adler32
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|BATCH_SIZE
index|]
decl_stmt|;
name|long
name|iter
init|=
name|dataSize
operator|/
name|BATCH_SIZE
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|crc
operator|.
name|update
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|crc
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Adler took "
operator|+
operator|new
name|TimeValue
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|md5
specifier|private
specifier|static
name|void
name|md5
parameter_list|(
name|long
name|dataSize
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|BATCH_SIZE
index|]
decl_stmt|;
name|long
name|iter
init|=
name|dataSize
operator|/
name|BATCH_SIZE
decl_stmt|;
name|MessageDigest
name|digest
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|digest
operator|.
name|update
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|digest
operator|.
name|digest
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"md5 took "
operator|+
operator|new
name|TimeValue
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
