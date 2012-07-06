begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.benchmark.compress
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|benchmark
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|IndexWriterConfig
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
name|Directory
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
name|FSDirectory
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
name|NIOFSDirectory
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
name|compress
operator|.
name|CompressedDirectory
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
name|compress
operator|.
name|lzf
operator|.
name|LZFCompressor
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
name|compress
operator|.
name|snappy
operator|.
name|xerial
operator|.
name|XerialSnappyCompressor
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
name|FileSystemUtils
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|LuceneCompressionBenchmark
specifier|public
class|class
name|LuceneCompressionBenchmark
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
specifier|final
name|long
name|MAX_SIZE
init|=
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"50mb"
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|WITH_TV
init|=
literal|true
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
literal|"target/test/compress/lucene"
argument_list|)
decl_stmt|;
name|FileSystemUtils
operator|.
name|deleteRecursively
argument_list|(
name|testFile
argument_list|)
expr_stmt|;
name|testFile
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FSDirectory
name|uncompressedDir
init|=
operator|new
name|NIOFSDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testFile
argument_list|,
literal|"uncompressed"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|uncompressedWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|uncompressedDir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Lucene
operator|.
name|VERSION
argument_list|,
name|Lucene
operator|.
name|STANDARD_ANALYZER
argument_list|)
argument_list|)
decl_stmt|;
name|Directory
name|compressedLzfDir
init|=
operator|new
name|CompressedDirectory
argument_list|(
operator|new
name|NIOFSDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testFile
argument_list|,
literal|"compressed_lzf"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|LZFCompressor
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|"fdt"
argument_list|,
literal|"tvf"
argument_list|)
decl_stmt|;
name|IndexWriter
name|compressedLzfWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|compressedLzfDir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Lucene
operator|.
name|VERSION
argument_list|,
name|Lucene
operator|.
name|STANDARD_ANALYZER
argument_list|)
argument_list|)
decl_stmt|;
name|Directory
name|compressedSnappyDir
init|=
operator|new
name|CompressedDirectory
argument_list|(
operator|new
name|NIOFSDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testFile
argument_list|,
literal|"compressed_snappy"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|XerialSnappyCompressor
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|"fdt"
argument_list|,
literal|"tvf"
argument_list|)
decl_stmt|;
name|IndexWriter
name|compressedSnappyWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|compressedSnappyDir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Lucene
operator|.
name|VERSION
argument_list|,
name|Lucene
operator|.
name|STANDARD_ANALYZER
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"feeding data..."
argument_list|)
expr_stmt|;
name|TestData
name|testData
init|=
operator|new
name|TestData
argument_list|()
decl_stmt|;
while|while
condition|(
name|testData
operator|.
name|next
argument_list|()
operator|&&
name|testData
operator|.
name|getTotalSize
argument_list|()
operator|<
name|MAX_SIZE
condition|)
block|{
comment|// json
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|testData
operator|.
name|current
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|close
argument_list|()
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"_source"
argument_list|,
name|builder
operator|.
name|bytes
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
name|builder
operator|.
name|bytes
argument_list|()
operator|.
name|arrayOffset
argument_list|()
argument_list|,
name|builder
operator|.
name|bytes
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|WITH_TV
condition|)
block|{
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
literal|"text"
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|uncompressedWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|compressedLzfWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|compressedSnappyWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"optimizing..."
argument_list|)
expr_stmt|;
name|uncompressedWriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|compressedLzfWriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|compressedSnappyWriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|uncompressedWriter
operator|.
name|waitForMerges
argument_list|()
expr_stmt|;
name|compressedLzfWriter
operator|.
name|waitForMerges
argument_list|()
expr_stmt|;
name|compressedSnappyWriter
operator|.
name|waitForMerges
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
name|uncompressedWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|compressedLzfWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|compressedSnappyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|compressedLzfDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|compressedSnappyDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|uncompressedDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

