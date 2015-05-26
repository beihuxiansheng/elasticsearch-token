begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.analyze
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|analyze
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
name|analysis
operator|.
name|hunspell
operator|.
name|Dictionary
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|analysis
operator|.
name|HunspellService
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
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
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
operator|.
name|Scope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|analysis
operator|.
name|HunspellService
operator|.
name|*
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
name|notNullValue
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|TEST
argument_list|,
name|numDataNodes
operator|=
literal|0
argument_list|)
DECL|class|HunspellServiceTests
specifier|public
class|class
name|HunspellServiceTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testLocaleDirectoryWithNodeLevelConfig
specifier|public
name|void
name|testLocaleDirectoryWithNodeLevelConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"path.conf"
argument_list|,
name|getDataPath
argument_list|(
literal|"/indices/analyze/conf_dir"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|HUNSPELL_LAZY_LOAD
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|HUNSPELL_IGNORE_CASE
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|Dictionary
name|dictionary
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|HunspellService
operator|.
name|class
argument_list|)
operator|.
name|getDictionary
argument_list|(
literal|"en_US"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|dictionary
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertIgnoreCase
argument_list|(
literal|true
argument_list|,
name|dictionary
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLocaleDirectoryWithLocaleSpecificConfig
specifier|public
name|void
name|testLocaleDirectoryWithLocaleSpecificConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"path.conf"
argument_list|,
name|getDataPath
argument_list|(
literal|"/indices/analyze/conf_dir"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|HUNSPELL_LAZY_LOAD
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|HUNSPELL_IGNORE_CASE
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"indices.analysis.hunspell.dictionary.en_US.strict_affix_parsing"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"indices.analysis.hunspell.dictionary.en_US.ignore_case"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|Dictionary
name|dictionary
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|HunspellService
operator|.
name|class
argument_list|)
operator|.
name|getDictionary
argument_list|(
literal|"en_US"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|dictionary
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertIgnoreCase
argument_list|(
literal|false
argument_list|,
name|dictionary
argument_list|)
expr_stmt|;
comment|// testing that dictionary specific settings override node level settings
name|dictionary
operator|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|HunspellService
operator|.
name|class
argument_list|)
operator|.
name|getDictionary
argument_list|(
literal|"en_US_custom"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|dictionary
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertIgnoreCase
argument_list|(
literal|true
argument_list|,
name|dictionary
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDicWithNoAff
specifier|public
name|void
name|testDicWithNoAff
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"path.conf"
argument_list|,
name|getDataPath
argument_list|(
literal|"/indices/analyze/no_aff_conf_dir"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|HUNSPELL_LAZY_LOAD
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Dictionary
name|dictionary
init|=
literal|null
decl_stmt|;
try|try
block|{
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|dictionary
operator|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|HunspellService
operator|.
name|class
argument_list|)
operator|.
name|getDictionary
argument_list|(
literal|"en_US"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Missing affix file didn't throw an error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|assertNull
argument_list|(
name|dictionary
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ExceptionsHelper
operator|.
name|unwrap
argument_list|(
name|t
argument_list|,
name|ElasticsearchException
operator|.
name|class
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|Matchers
operator|.
name|containsString
argument_list|(
literal|"Missing affix file"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDicWithTwoAffs
specifier|public
name|void
name|testDicWithTwoAffs
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"path.conf"
argument_list|,
name|getDataPath
argument_list|(
literal|"/indices/analyze/two_aff_conf_dir"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|HUNSPELL_LAZY_LOAD
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Dictionary
name|dictionary
init|=
literal|null
decl_stmt|;
try|try
block|{
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|dictionary
operator|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|HunspellService
operator|.
name|class
argument_list|)
operator|.
name|getDictionary
argument_list|(
literal|"en_US"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Multiple affix files didn't throw an error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|assertNull
argument_list|(
name|dictionary
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ExceptionsHelper
operator|.
name|unwrap
argument_list|(
name|t
argument_list|,
name|ElasticsearchException
operator|.
name|class
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|Matchers
operator|.
name|containsString
argument_list|(
literal|"Too many affix files"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO: open up a getter on Dictionary
DECL|method|assertIgnoreCase
specifier|private
name|void
name|assertIgnoreCase
parameter_list|(
name|boolean
name|expected
parameter_list|,
name|Dictionary
name|dictionary
parameter_list|)
throws|throws
name|Exception
block|{
name|Field
name|f
init|=
name|Dictionary
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"ignoreCase"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|f
operator|.
name|getBoolean
argument_list|(
name|dictionary
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

