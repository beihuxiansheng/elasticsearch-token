begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|Index
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|IndexSettings
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
name|ESTokenStreamTestCase
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
name|IndexSettingsModule
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
operator|.
name|settingsBuilder
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
name|containsString
import|;
end_import

begin_class
DECL|class|PatternCaptureTokenFilterTests
specifier|public
class|class
name|PatternCaptureTokenFilterTests
extends|extends
name|ESTokenStreamTestCase
block|{
DECL|method|testPatternCaptureTokenFilter
specifier|public
name|void
name|testPatternCaptureTokenFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|json
init|=
literal|"/org/elasticsearch/index/analysis/pattern_capture.json"
decl_stmt|;
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|Environment
operator|.
name|PATH_HOME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
operator|.
name|loadFromStream
argument_list|(
name|json
argument_list|,
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|json
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexSettings
name|idxSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
literal|"index"
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
operator|new
name|AnalysisRegistry
argument_list|(
literal|null
argument_list|,
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|build
argument_list|(
name|idxSettings
argument_list|)
decl_stmt|;
name|NamedAnalyzer
name|analyzer1
init|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"single"
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|analyzer1
operator|.
name|tokenStream
argument_list|(
literal|"test"
argument_list|,
literal|"foobarbaz"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foobarbaz"
block|,
literal|"foobar"
block|,
literal|"foo"
block|}
argument_list|)
expr_stmt|;
name|NamedAnalyzer
name|analyzer2
init|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"multi"
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|analyzer2
operator|.
name|tokenStream
argument_list|(
literal|"test"
argument_list|,
literal|"abc123def"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abc123def"
block|,
literal|"abc"
block|,
literal|"123"
block|,
literal|"def"
block|}
argument_list|)
expr_stmt|;
name|NamedAnalyzer
name|analyzer3
init|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"preserve"
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|analyzer3
operator|.
name|tokenStream
argument_list|(
literal|"test"
argument_list|,
literal|"foobarbaz"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foobar"
block|,
literal|"foo"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoPatterns
specifier|public
name|void
name|testNoPatterns
parameter_list|()
block|{
try|try
block|{
operator|new
name|PatternCaptureGroupTokenFilterFactory
argument_list|(
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
literal|"test"
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|"pattern_capture"
argument_list|,
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"pattern"
argument_list|,
literal|"foobar"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"required setting 'patterns' is missing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

