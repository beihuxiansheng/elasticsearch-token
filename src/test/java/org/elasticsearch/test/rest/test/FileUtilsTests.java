begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|test
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
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|support
operator|.
name|FileUtils
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
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|notNullValue
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
name|greaterThan
import|;
end_import

begin_class
DECL|class|FileUtilsTests
specifier|public
class|class
name|FileUtilsTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testLoadSingleYamlSuite
specifier|public
name|void
name|testLoadSingleYamlSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|File
argument_list|>
argument_list|>
name|yamlSuites
init|=
name|FileUtils
operator|.
name|findYamlSuites
argument_list|(
literal|"/rest-api-spec/test"
argument_list|,
literal|"/rest-api-spec/test/get/10_basic"
argument_list|)
decl_stmt|;
name|assertSingleFile
argument_list|(
name|yamlSuites
argument_list|,
literal|"get"
argument_list|,
literal|"10_basic.yaml"
argument_list|)
expr_stmt|;
comment|//the path prefix is optional
name|yamlSuites
operator|=
name|FileUtils
operator|.
name|findYamlSuites
argument_list|(
literal|"/rest-api-spec/test"
argument_list|,
literal|"get/10_basic.yaml"
argument_list|)
expr_stmt|;
name|assertSingleFile
argument_list|(
name|yamlSuites
argument_list|,
literal|"get"
argument_list|,
literal|"10_basic.yaml"
argument_list|)
expr_stmt|;
comment|//extension .yaml is optional
name|yamlSuites
operator|=
name|FileUtils
operator|.
name|findYamlSuites
argument_list|(
literal|"/rest-api-spec/test"
argument_list|,
literal|"get/10_basic"
argument_list|)
expr_stmt|;
name|assertSingleFile
argument_list|(
name|yamlSuites
argument_list|,
literal|"get"
argument_list|,
literal|"10_basic.yaml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoadMultipleYamlSuites
specifier|public
name|void
name|testLoadMultipleYamlSuites
parameter_list|()
throws|throws
name|Exception
block|{
comment|//single directory
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|File
argument_list|>
argument_list|>
name|yamlSuites
init|=
name|FileUtils
operator|.
name|findYamlSuites
argument_list|(
literal|"/rest-api-spec/test"
argument_list|,
literal|"get"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
operator|.
name|size
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
name|yamlSuites
operator|.
name|containsKey
argument_list|(
literal|"get"
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
name|yamlSuites
operator|.
name|get
argument_list|(
literal|"get"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//multiple directories
name|yamlSuites
operator|=
name|FileUtils
operator|.
name|findYamlSuites
argument_list|(
literal|"/rest-api-spec/test"
argument_list|,
literal|"get"
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
operator|.
name|size
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
name|yamlSuites
operator|.
name|containsKey
argument_list|(
literal|"get"
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
name|yamlSuites
operator|.
name|get
argument_list|(
literal|"get"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
operator|.
name|containsKey
argument_list|(
literal|"index"
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
name|yamlSuites
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//multiple paths, which can be both directories or yaml test suites (with optional file extension)
name|yamlSuites
operator|=
name|FileUtils
operator|.
name|findYamlSuites
argument_list|(
literal|"/rest-api-spec/test"
argument_list|,
literal|"get/10_basic"
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
operator|.
name|size
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
name|yamlSuites
operator|.
name|containsKey
argument_list|(
literal|"get"
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
name|yamlSuites
operator|.
name|get
argument_list|(
literal|"get"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertSingleFile
argument_list|(
name|yamlSuites
operator|.
name|get
argument_list|(
literal|"get"
argument_list|)
argument_list|,
literal|"get"
argument_list|,
literal|"10_basic.yaml"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
operator|.
name|containsKey
argument_list|(
literal|"index"
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
name|yamlSuites
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//files can be loaded from classpath and from file system too
name|File
name|dir
init|=
name|newTempDir
argument_list|()
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"test_loading.yaml"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|file
operator|.
name|createNewFile
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|//load from directory outside of the classpath
name|yamlSuites
operator|=
name|FileUtils
operator|.
name|findYamlSuites
argument_list|(
literal|"/rest-api-spec/test"
argument_list|,
literal|"get/10_basic"
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
operator|.
name|size
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
name|yamlSuites
operator|.
name|containsKey
argument_list|(
literal|"get"
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
name|yamlSuites
operator|.
name|get
argument_list|(
literal|"get"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertSingleFile
argument_list|(
name|yamlSuites
operator|.
name|get
argument_list|(
literal|"get"
argument_list|)
argument_list|,
literal|"get"
argument_list|,
literal|"10_basic.yaml"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
operator|.
name|containsKey
argument_list|(
name|dir
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertSingleFile
argument_list|(
name|yamlSuites
operator|.
name|get
argument_list|(
name|dir
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|dir
operator|.
name|getName
argument_list|()
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|//load from external file (optional extension)
name|yamlSuites
operator|=
name|FileUtils
operator|.
name|findYamlSuites
argument_list|(
literal|"/rest-api-spec/test"
argument_list|,
literal|"get/10_basic"
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test_loading"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
operator|.
name|size
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
name|yamlSuites
operator|.
name|containsKey
argument_list|(
literal|"get"
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
name|yamlSuites
operator|.
name|get
argument_list|(
literal|"get"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertSingleFile
argument_list|(
name|yamlSuites
operator|.
name|get
argument_list|(
literal|"get"
argument_list|)
argument_list|,
literal|"get"
argument_list|,
literal|"10_basic.yaml"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
operator|.
name|containsKey
argument_list|(
name|dir
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertSingleFile
argument_list|(
name|yamlSuites
operator|.
name|get
argument_list|(
name|dir
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|dir
operator|.
name|getName
argument_list|()
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSingleFile
specifier|private
specifier|static
name|void
name|assertSingleFile
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|File
argument_list|>
argument_list|>
name|yamlSuites
parameter_list|,
name|String
name|dirName
parameter_list|,
name|String
name|fileName
parameter_list|)
block|{
name|assertThat
argument_list|(
name|yamlSuites
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|yamlSuites
operator|.
name|size
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
name|yamlSuites
operator|.
name|containsKey
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertSingleFile
argument_list|(
name|yamlSuites
operator|.
name|get
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|dirName
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSingleFile
specifier|private
specifier|static
name|void
name|assertSingleFile
parameter_list|(
name|Set
argument_list|<
name|File
argument_list|>
name|files
parameter_list|,
name|String
name|dirName
parameter_list|,
name|String
name|fileName
parameter_list|)
block|{
name|assertThat
argument_list|(
name|files
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|file
init|=
name|files
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|fileName
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|file
operator|.
name|getParentFile
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|dirName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

