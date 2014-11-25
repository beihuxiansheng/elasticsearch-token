begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
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
name|action
operator|.
name|search
operator|.
name|SearchResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|IndicesOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|settings
operator|.
name|ImmutableSettings
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
name|ElasticsearchIntegrationTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|nio
operator|.
name|file
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|matchAllQuery
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
name|assertAcked
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
comment|/**  * Tests for custom data path locations and templates  */
end_comment

begin_class
DECL|class|IndicesCustomDataPathTests
specifier|public
class|class
name|IndicesCustomDataPathTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|path
operator|=
name|newTempDirPath
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDataPathCanBeChanged
specifier|public
name|void
name|testDataPathCanBeChanged
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|INDEX
init|=
literal|"idx"
decl_stmt|;
name|Path
name|root
init|=
name|newTempDirPath
argument_list|()
decl_stmt|;
name|Path
name|startDir
init|=
name|root
operator|.
name|resolve
argument_list|(
literal|"start"
argument_list|)
decl_stmt|;
name|Path
name|endDir
init|=
name|root
operator|.
name|resolve
argument_list|(
literal|"end"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> start dir: [{}]"
argument_list|,
name|startDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-->   end dir: [{}]"
argument_list|,
name|endDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// temp dirs are automatically created, but the end dir is what
comment|// startDir is going to be renamed as, so it needs to be deleted
comment|// otherwise we get all sorts of errors about the directory
comment|// already existing
name|IOUtils
operator|.
name|rm
argument_list|(
name|endDir
argument_list|)
expr_stmt|;
name|ImmutableSettings
operator|.
name|Builder
name|sb
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_DATA_PATH
argument_list|,
name|startDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|ImmutableSettings
operator|.
name|Builder
name|sb2
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_DATA_PATH
argument_list|,
name|endDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> creating an index with data_path [{}]"
argument_list|,
name|startDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
name|INDEX
argument_list|)
operator|.
name|setSettings
argument_list|(
name|sb
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|(
name|INDEX
argument_list|)
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|INDEX
argument_list|,
literal|"doc"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"body\": \"foo\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|SearchResponse
name|resp
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
name|INDEX
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"found the hit"
argument_list|,
name|resp
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> closing the index [{}]"
argument_list|,
name|INDEX
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
name|INDEX
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> index closed, re-opening..."
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
name|INDEX
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> index re-opened"
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
name|INDEX
argument_list|)
expr_stmt|;
name|resp
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
name|INDEX
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"found the hit"
argument_list|,
name|resp
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now, try closing and changing the settings
name|logger
operator|.
name|info
argument_list|(
literal|"--> closing the index [{}]"
argument_list|,
name|INDEX
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
name|INDEX
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> moving data on disk [{}] to [{}]"
argument_list|,
name|startDir
operator|.
name|getFileName
argument_list|()
argument_list|,
name|endDir
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|Files
operator|.
name|exists
argument_list|(
name|endDir
argument_list|)
operator|==
literal|false
operator|:
literal|"end directory should not exist!"
assert|;
name|Files
operator|.
name|move
argument_list|(
name|startDir
argument_list|,
name|endDir
argument_list|,
name|StandardCopyOption
operator|.
name|REPLACE_EXISTING
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> updating settings..."
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
name|INDEX
argument_list|)
operator|.
name|setSettings
argument_list|(
name|sb2
argument_list|)
operator|.
name|setIndicesOptions
argument_list|(
name|IndicesOptions
operator|.
name|fromOptions
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
assert|assert
name|Files
operator|.
name|exists
argument_list|(
name|startDir
argument_list|)
operator|==
literal|false
operator|:
literal|"start dir shouldn't exist"
assert|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> settings updated and files moved, re-opening index"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
name|INDEX
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> index re-opened"
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
name|INDEX
argument_list|)
expr_stmt|;
name|resp
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
name|INDEX
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"found the hit"
argument_list|,
name|resp
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareDelete
argument_list|(
name|INDEX
argument_list|)
argument_list|)
expr_stmt|;
name|assertPathHasBeenCleared
argument_list|(
name|startDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertPathHasBeenCleared
argument_list|(
name|endDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIndexCreatedWithCustomPathAndTemplate
specifier|public
name|void
name|testIndexCreatedWithCustomPathAndTemplate
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|INDEX
init|=
literal|"myindex2"
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> creating an index with data_path [{}]"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|ImmutableSettings
operator|.
name|Builder
name|sb
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_DATA_PATH
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
name|INDEX
argument_list|)
operator|.
name|setSettings
argument_list|(
name|sb
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|(
name|INDEX
argument_list|)
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|INDEX
argument_list|,
literal|"doc"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"body\": \"foo\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|SearchResponse
name|resp
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
name|INDEX
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"found the hit"
argument_list|,
name|resp
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareDelete
argument_list|(
name|INDEX
argument_list|)
argument_list|)
expr_stmt|;
name|assertPathHasBeenCleared
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|assertPathHasBeenCleared
specifier|private
name|void
name|assertPathHasBeenCleared
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
condition|)
block|{
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|file
range|:
name|stream
control|)
block|{
if|if
condition|(
name|Files
operator|.
name|isRegularFile
argument_list|(
name|file
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|file
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|count
operator|+
literal|" files exist that should have been cleaned:\n"
operator|+
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|count
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

