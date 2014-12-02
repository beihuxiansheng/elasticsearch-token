begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.io
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
package|;
end_package

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
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|File
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
name|nio
operator|.
name|file
operator|.
name|Files
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
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertFileExists
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
name|containsString
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
name|is
import|;
end_import

begin_comment
comment|/**  * Unit tests for {@link org.elasticsearch.common.io.FileSystemUtils}.  */
end_comment

begin_class
DECL|class|FileSystemUtilsTests
specifier|public
class|class
name|FileSystemUtilsTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|src
name|Path
name|src
decl_stmt|;
DECL|field|dst
name|Path
name|dst
decl_stmt|;
annotation|@
name|Before
DECL|method|copySourceFilesToTarget
specifier|public
name|void
name|copySourceFilesToTarget
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|globalTempDir
init|=
name|globalTempDir
argument_list|()
operator|.
name|toPath
argument_list|()
decl_stmt|;
name|src
operator|=
name|globalTempDir
operator|.
name|resolve
argument_list|(
literal|"iocopyappend-src"
argument_list|)
expr_stmt|;
name|dst
operator|=
name|globalTempDir
operator|.
name|resolve
argument_list|(
literal|"iocopyappend-dst"
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|dst
argument_list|)
expr_stmt|;
comment|// We first copy sources test files from src/test/resources
comment|// Because after when the test runs, src files are moved to their destination
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|FileSystemUtilsTests
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"rootdir.properties"
argument_list|)
operator|.
name|openStream
argument_list|()
init|)
block|{
name|props
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
name|FileSystemUtils
operator|.
name|copyDirectoryRecursively
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"copyappend.root.dir"
argument_list|)
argument_list|)
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMoveOverExistingFileAndAppend
specifier|public
name|void
name|testMoveOverExistingFileAndAppend
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystemUtils
operator|.
name|moveFilesWithoutOverwriting
argument_list|(
name|src
operator|.
name|resolve
argument_list|(
literal|"v1"
argument_list|)
argument_list|,
name|dst
argument_list|,
literal|".new"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"file1.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"dir/file2.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|FileSystemUtils
operator|.
name|moveFilesWithoutOverwriting
argument_list|(
name|src
operator|.
name|resolve
argument_list|(
literal|"v2"
argument_list|)
argument_list|,
name|dst
argument_list|,
literal|".new"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"file1.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"dir/file2.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"file1.txt.new"
argument_list|,
literal|"version2"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"dir/file2.txt.new"
argument_list|,
literal|"version2"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"file3.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"dir/subdir/file4.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|FileSystemUtils
operator|.
name|moveFilesWithoutOverwriting
argument_list|(
name|src
operator|.
name|resolve
argument_list|(
literal|"v3"
argument_list|)
argument_list|,
name|dst
argument_list|,
literal|".new"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"file1.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"dir/file2.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"file1.txt.new"
argument_list|,
literal|"version3"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"dir/file2.txt.new"
argument_list|,
literal|"version3"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"file3.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"dir/subdir/file4.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"file3.txt.new"
argument_list|,
literal|"version2"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"dir/subdir/file4.txt.new"
argument_list|,
literal|"version2"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dst
argument_list|,
literal|"dir/subdir/file5.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMoveOverExistingFileAndIgnore
specifier|public
name|void
name|testMoveOverExistingFileAndIgnore
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dest
init|=
name|globalTempDir
argument_list|()
operator|.
name|toPath
argument_list|()
decl_stmt|;
name|FileSystemUtils
operator|.
name|moveFilesWithoutOverwriting
argument_list|(
name|src
operator|.
name|resolve
argument_list|(
literal|"v1"
argument_list|)
argument_list|,
name|dest
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"file1.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"dir/file2.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|FileSystemUtils
operator|.
name|moveFilesWithoutOverwriting
argument_list|(
name|src
operator|.
name|resolve
argument_list|(
literal|"v2"
argument_list|)
argument_list|,
name|dest
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"file1.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"dir/file2.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"file1.txt.new"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"dir/file2.txt.new"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"file3.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"dir/subdir/file4.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|FileSystemUtils
operator|.
name|moveFilesWithoutOverwriting
argument_list|(
name|src
operator|.
name|resolve
argument_list|(
literal|"v3"
argument_list|)
argument_list|,
name|dest
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"file1.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"dir/file2.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"file1.txt.new"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"dir/file2.txt.new"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"file3.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"dir/subdir/file4.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"file3.txt.new"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"dir/subdir/file4.txt.new"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFileContent
argument_list|(
name|dest
argument_list|,
literal|"dir/subdir/file5.txt"
argument_list|,
literal|"version1"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Check that a file contains a given String      * @param dir root dir for file      * @param filename relative path from root dir to file      * @param expected expected content (if null, we don't expect any file)      */
DECL|method|assertFileContent
specifier|public
specifier|static
name|void
name|assertFileContent
parameter_list|(
name|Path
name|dir
parameter_list|,
name|String
name|filename
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertThat
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|dir
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|file
init|=
name|dir
operator|.
name|resolve
argument_list|(
name|filename
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
name|Assert
operator|.
name|assertThat
argument_list|(
literal|"file ["
operator|+
name|file
operator|+
literal|"] should not exist."
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|file
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFileExists
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|String
name|fileContent
init|=
operator|new
name|String
argument_list|(
name|Files
operator|.
name|readAllBytes
argument_list|(
name|file
argument_list|)
argument_list|,
name|UTF8
argument_list|)
decl_stmt|;
comment|// trim the string content to prevent different handling on windows vs. unix and CR chars...
name|Assert
operator|.
name|assertThat
argument_list|(
name|fileContent
operator|.
name|trim
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expected
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAppend
specifier|public
name|void
name|testAppend
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|FileSystemUtils
operator|.
name|append
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"/hello/world/this_is/awesome"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"/foo/bar/hello/world/this_is/awesome"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FileSystemUtils
operator|.
name|append
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"/hello/world/this_is/awesome"
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"/foo/bar/this_is/awesome"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FileSystemUtils
operator|.
name|append
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"/hello/world/this_is/awesome"
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"/foo/bar/world/this_is/awesome"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

