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
name|analysis
operator|.
name|en
operator|.
name|PorterStemFilter
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
name|snowball
operator|.
name|SnowballFilter
import|;
end_import

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
name|VersionUtils
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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|scaledRandomIntBetween
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|StemmerTokenFilterFactoryTests
specifier|public
class|class
name|StemmerTokenFilterFactoryTests
extends|extends
name|ESTokenStreamTestCase
block|{
annotation|@
name|Test
DECL|method|testEnglishBackwardsCompatibility
specifier|public
name|void
name|testEnglishBackwardsCompatibility
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|iters
init|=
name|scaledRandomIntBetween
argument_list|(
literal|20
argument_list|,
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Version
name|v
init|=
name|VersionUtils
operator|.
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
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
literal|"index.analysis.filter.my_english.type"
argument_list|,
literal|"stemmer"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_english.language"
argument_list|,
literal|"english"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer.my_english.tokenizer"
argument_list|,
literal|"whitespace"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer.my_english.filter"
argument_list|,
literal|"my_english"
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_VERSION_CREATED
argument_list|,
name|v
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
literal|"my_english"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|StemmerTokenFilterFactory
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
name|NamedAnalyzer
name|analyzer
init|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"my_english"
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_3_0
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|create
argument_list|,
name|instanceOf
argument_list|(
name|PorterStemFilter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"consolingly"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"consolingli"
block|}
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
name|SnowballFilter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"consolingly"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"consol"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testPorter2BackwardsCompatibility
specifier|public
name|void
name|testPorter2BackwardsCompatibility
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|iters
init|=
name|scaledRandomIntBetween
argument_list|(
literal|20
argument_list|,
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Version
name|v
init|=
name|VersionUtils
operator|.
name|randomVersion
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
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
literal|"index.analysis.filter.my_porter2.type"
argument_list|,
literal|"stemmer"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.my_porter2.language"
argument_list|,
literal|"porter2"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer.my_porter2.tokenizer"
argument_list|,
literal|"whitespace"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer.my_porter2.filter"
argument_list|,
literal|"my_porter2"
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_VERSION_CREATED
argument_list|,
name|v
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
literal|"my_porter2"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|StemmerTokenFilterFactory
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
name|NamedAnalyzer
name|analyzer
init|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"my_porter2"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|create
argument_list|,
name|instanceOf
argument_list|(
name|SnowballFilter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_3_0
argument_list|)
condition|)
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"possibly"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"possibl"
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"possibly"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"possibli"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
