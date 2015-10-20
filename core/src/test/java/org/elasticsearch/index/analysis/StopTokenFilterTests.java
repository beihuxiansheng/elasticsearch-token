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
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|Tokenizer
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
name|analysis
operator|.
name|core
operator|.
name|Lucene43StopFilter
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
name|analysis
operator|.
name|core
operator|.
name|StopFilter
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
name|analysis
operator|.
name|core
operator|.
name|WhitespaceTokenizer
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
name|search
operator|.
name|suggest
operator|.
name|analyzing
operator|.
name|SuggestStopFilter
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
name|Version
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
name|ProvisionException
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
name|Settings
operator|.
name|Builder
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
name|StringReader
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|instanceOf
import|;
end_import

begin_class
DECL|class|StopTokenFilterTests
specifier|public
class|class
name|StopTokenFilterTests
extends|extends
name|ESTokenStreamTestCase
block|{
DECL|method|testPositionIncrementSetting
specifier|public
name|void
name|testPositionIncrementSetting
parameter_list|()
throws|throws
name|IOException
block|{
name|Builder
name|builder
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_stop.type"
argument_list|,
literal|"stop"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_stop.enable_position_increments"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_stop.version"
argument_list|,
literal|"5.0"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Settings
name|settings
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|AnalysisTestsHelper
operator|.
name|createAnalysisServiceFromSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected ProvisionException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ProvisionException
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
literal|"enable_position_increments is not supported anymore"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCorrectPositionIncrementSetting
specifier|public
name|void
name|testCorrectPositionIncrementSetting
parameter_list|()
throws|throws
name|IOException
block|{
name|Builder
name|builder
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_stop.type"
argument_list|,
literal|"stop"
argument_list|)
decl_stmt|;
name|int
name|thingToDo
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|thingToDo
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_stop.version"
argument_list|,
name|Version
operator|.
name|LATEST
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thingToDo
operator|==
literal|1
condition|)
block|{
name|builder
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_stop.version"
argument_list|,
name|Version
operator|.
name|LUCENE_4_0
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_stop.enable_position_increments"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// don't specify
block|}
name|builder
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|AnalysisService
name|analysisService
init|=
name|AnalysisTestsHelper
operator|.
name|createAnalysisServiceFromSettings
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|tokenFilter
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"my_stop"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|StopTokenFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"foo bar"
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|create
init|=
name|tokenFilter
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
if|if
condition|(
name|thingToDo
operator|==
literal|1
condition|)
block|{
name|assertThat
argument_list|(
name|create
argument_list|,
name|instanceOf
argument_list|(
name|Lucene43StopFilter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|create
argument_list|,
name|instanceOf
argument_list|(
name|StopFilter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDeprecatedPositionIncrementSettingWithVersions
specifier|public
name|void
name|testDeprecatedPositionIncrementSettingWithVersions
parameter_list|()
throws|throws
name|IOException
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
literal|"index.analysis.filter.my_stop.type"
argument_list|,
literal|"stop"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_stop.enable_position_increments"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_stop.version"
argument_list|,
literal|"4.3"
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|AnalysisTestsHelper
operator|.
name|createAnalysisServiceFromSettings
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|tokenFilter
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"my_stop"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|StopTokenFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"foo bar"
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|create
init|=
name|tokenFilter
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|create
argument_list|,
name|instanceOf
argument_list|(
name|Lucene43StopFilter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatSuggestStopFilterWorks
specifier|public
name|void
name|testThatSuggestStopFilterWorks
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
literal|"index.analysis.filter.my_stop.type"
argument_list|,
literal|"stop"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_stop.remove_trailing"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|AnalysisTestsHelper
operator|.
name|createAnalysisServiceFromSettings
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|tokenFilter
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"my_stop"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|StopTokenFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"foo an"
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|create
init|=
name|tokenFilter
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|create
argument_list|,
name|instanceOf
argument_list|(
name|SuggestStopFilter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

