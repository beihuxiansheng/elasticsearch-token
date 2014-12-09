begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.benchmark.fs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|benchmark
operator|.
name|fs
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
name|IOUtils
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
name|StopWatch
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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|Path
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
name|Paths
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
name|StandardOpenOption
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|FsAppendBenchmark
specifier|public
class|class
name|FsAppendBenchmark
block|{
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
name|Path
name|path
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"work/test.log"
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|int
name|CHUNK
init|=
operator|(
name|int
operator|)
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"1k"
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|long
name|DATA
init|=
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"10gb"
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|CHUNK
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|StopWatch
name|watch
init|=
operator|new
name|StopWatch
argument_list|()
operator|.
name|start
argument_list|(
literal|"write"
argument_list|)
decl_stmt|;
try|try
init|(
name|FileChannel
name|channel
init|=
name|FileChannel
operator|.
name|open
argument_list|(
name|path
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE_NEW
argument_list|)
init|)
block|{
name|long
name|position
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|position
operator|<
name|DATA
condition|)
block|{
name|channel
operator|.
name|write
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
name|position
operator|+=
name|data
operator|.
name|length
expr_stmt|;
block|}
name|watch
operator|.
name|stop
argument_list|()
operator|.
name|start
argument_list|(
literal|"flush"
argument_list|)
expr_stmt|;
name|channel
operator|.
name|force
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|watch
operator|.
name|stop
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Wrote ["
operator|+
operator|(
operator|new
name|ByteSizeValue
argument_list|(
name|DATA
argument_list|)
operator|)
operator|+
literal|"], chunk ["
operator|+
operator|(
operator|new
name|ByteSizeValue
argument_list|(
name|CHUNK
argument_list|)
operator|)
operator|+
literal|"], in "
operator|+
name|watch
argument_list|)
expr_stmt|;
block|}
DECL|field|fill
specifier|private
specifier|static
specifier|final
name|ByteBuffer
name|fill
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|//    public static long padLogFile(long position, long currentSize, long preAllocSize) throws IOException {
comment|//        if (position + 4096>= currentSize) {
comment|//            currentSize = currentSize + preAllocSize;
comment|//            fill.position(0);
comment|//            f.getChannel().write(fill, currentSize - fill.remaining());
comment|//        }
comment|//        return currentSize;
comment|//    }
block|}
end_class

end_unit

