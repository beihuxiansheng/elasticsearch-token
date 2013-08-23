begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.index.analysis
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
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
name|common
operator|.
name|inject
operator|.
name|Injector
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
name|inject
operator|.
name|ModulesBuilder
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
name|common
operator|.
name|settings
operator|.
name|SettingsModule
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
name|env
operator|.
name|EnvironmentModule
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
name|IndexNameModule
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
name|analysis
operator|.
name|AnalysisModule
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
name|analysis
operator|.
name|AnalysisService
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
name|analysis
operator|.
name|NamedAnalyzer
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
name|settings
operator|.
name|IndexSettingsModule
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
name|IndicesAnalysisModule
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
name|IndicesAnalysisService
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
name|integration
operator|.
name|ElasticSearchTokenStreamTestCase
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|settingsBuilder
import|;
end_import

begin_class
DECL|class|PatternCaptureTokenFilterTests
specifier|public
class|class
name|PatternCaptureTokenFilterTests
extends|extends
name|ElasticSearchTokenStreamTestCase
block|{
annotation|@
name|Test
DECL|method|testPatternCaptureTokenFilter
specifier|public
name|void
name|testPatternCaptureTokenFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|loadFromClasspath
argument_list|(
literal|"org/elasticsearch/test/unit/index/analysis/pattern_capture.json"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Injector
name|parentInjector
init|=
operator|new
name|ModulesBuilder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|EnvironmentModule
argument_list|(
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
argument_list|)
argument_list|,
operator|new
name|IndicesAnalysisModule
argument_list|()
argument_list|)
operator|.
name|createInjector
argument_list|()
decl_stmt|;
name|Injector
name|injector
init|=
operator|new
name|ModulesBuilder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|IndexSettingsModule
argument_list|(
name|index
argument_list|,
name|settings
argument_list|)
argument_list|,
operator|new
name|IndexNameModule
argument_list|(
name|index
argument_list|)
argument_list|,
operator|new
name|AnalysisModule
argument_list|(
name|settings
argument_list|,
name|parentInjector
operator|.
name|getInstance
argument_list|(
name|IndicesAnalysisService
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|createChildInjector
argument_list|(
name|parentInjector
argument_list|)
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|AnalysisService
operator|.
name|class
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
block|}
end_class

end_unit

