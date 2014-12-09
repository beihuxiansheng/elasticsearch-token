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
name|Analyzer
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
name|ar
operator|.
name|ArabicNormalizationFilter
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
name|fa
operator|.
name|PersianNormalizationFilter
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
name|miscellaneous
operator|.
name|KeywordRepeatFilter
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
name|standard
operator|.
name|StandardAnalyzer
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
name|filter1
operator|.
name|MyFilterTokenFilterFactory
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
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
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
name|BufferedWriter
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
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|util
operator|.
name|Set
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AnalysisModuleTests
specifier|public
class|class
name|AnalysisModuleTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|injector
specifier|private
name|Injector
name|injector
decl_stmt|;
DECL|method|getAnalysisService
specifier|public
name|AnalysisService
name|getAnalysisService
parameter_list|(
name|Settings
name|settings
parameter_list|)
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
name|injector
operator|=
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
expr_stmt|;
return|return
name|injector
operator|.
name|getInstance
argument_list|(
name|AnalysisService
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|loadFromClasspath
specifier|private
specifier|static
name|Settings
name|loadFromClasspath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|settingsBuilder
argument_list|()
operator|.
name|loadFromClasspath
argument_list|(
name|path
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
return|;
block|}
annotation|@
name|Test
DECL|method|testSimpleConfigurationJson
specifier|public
name|void
name|testSimpleConfigurationJson
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|loadFromClasspath
argument_list|(
literal|"org/elasticsearch/index/analysis/test1.json"
argument_list|)
decl_stmt|;
name|testSimpleConfiguration
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleConfigurationYaml
specifier|public
name|void
name|testSimpleConfigurationYaml
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|loadFromClasspath
argument_list|(
literal|"org/elasticsearch/index/analysis/test1.yml"
argument_list|)
decl_stmt|;
name|testSimpleConfiguration
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultFactoryTokenFilters
specifier|public
name|void
name|testDefaultFactoryTokenFilters
parameter_list|()
throws|throws
name|IOException
block|{
name|assertTokenFilter
argument_list|(
literal|"keyword_repeat"
argument_list|,
name|KeywordRepeatFilter
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertTokenFilter
argument_list|(
literal|"persian_normalization"
argument_list|,
name|PersianNormalizationFilter
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertTokenFilter
argument_list|(
literal|"arabic_normalization"
argument_list|,
name|ArabicNormalizationFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVersionedAnalyzers
specifier|public
name|void
name|testVersionedAnalyzers
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|settings2
init|=
name|settingsBuilder
argument_list|()
operator|.
name|loadFromClasspath
argument_list|(
literal|"org/elasticsearch/index/analysis/test1.yml"
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
name|V_0_90_0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService2
init|=
name|getAnalysisService
argument_list|(
name|settings2
argument_list|)
decl_stmt|;
comment|// indicesanalysisservice always has the current version
name|IndicesAnalysisService
name|indicesAnalysisService2
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|IndicesAnalysisService
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|indicesAnalysisService2
operator|.
name|analyzer
argument_list|(
literal|"default"
argument_list|)
argument_list|,
name|is
argument_list|(
name|instanceOf
argument_list|(
name|NamedAnalyzer
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|NamedAnalyzer
name|defaultNamedAnalyzer
init|=
operator|(
name|NamedAnalyzer
operator|)
name|indicesAnalysisService2
operator|.
name|analyzer
argument_list|(
literal|"default"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|defaultNamedAnalyzer
operator|.
name|analyzer
argument_list|()
argument_list|,
name|is
argument_list|(
name|instanceOf
argument_list|(
name|StandardAnalyzer
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|CURRENT
operator|.
name|luceneVersion
argument_list|,
name|defaultNamedAnalyzer
operator|.
name|analyzer
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// analysis service has the expected version
name|assertThat
argument_list|(
name|analysisService2
operator|.
name|analyzer
argument_list|(
literal|"standard"
argument_list|)
operator|.
name|analyzer
argument_list|()
argument_list|,
name|is
argument_list|(
name|instanceOf
argument_list|(
name|StandardAnalyzer
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|V_0_90_0
operator|.
name|luceneVersion
argument_list|,
name|analysisService2
operator|.
name|analyzer
argument_list|(
literal|"standard"
argument_list|)
operator|.
name|analyzer
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|V_0_90_0
operator|.
name|luceneVersion
argument_list|,
name|analysisService2
operator|.
name|analyzer
argument_list|(
literal|"thai"
argument_list|)
operator|.
name|analyzer
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTokenFilter
specifier|private
name|void
name|assertTokenFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
name|clazz
parameter_list|)
throws|throws
name|IOException
block|{
name|AnalysisService
name|analysisService
init|=
name|AnalysisTestsHelper
operator|.
name|createAnalysisServiceFromSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
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
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|tokenFilter
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
name|name
argument_list|)
decl_stmt|;
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
name|stream
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
name|stream
argument_list|,
name|instanceOf
argument_list|(
name|clazz
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleConfiguration
specifier|private
name|void
name|testSimpleConfiguration
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|AnalysisService
name|analysisService
init|=
name|getAnalysisService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"custom1"
argument_list|)
operator|.
name|analyzer
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|analyzer
argument_list|,
name|instanceOf
argument_list|(
name|CustomAnalyzer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|CustomAnalyzer
name|custom1
init|=
operator|(
name|CustomAnalyzer
operator|)
name|analyzer
decl_stmt|;
name|assertThat
argument_list|(
name|custom1
operator|.
name|tokenizerFactory
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|StandardTokenizerFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|custom1
operator|.
name|tokenFilters
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|StopTokenFilterFactory
name|stop1
init|=
operator|(
name|StopTokenFilterFactory
operator|)
name|custom1
operator|.
name|tokenFilters
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertThat
argument_list|(
name|stop1
operator|.
name|stopWords
argument_list|()
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
comment|//assertThat((Iterable<char[]>) stop1.stopWords(), hasItem("test-stop".toCharArray()));
name|analyzer
operator|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"custom2"
argument_list|)
operator|.
name|analyzer
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|analyzer
argument_list|,
name|instanceOf
argument_list|(
name|CustomAnalyzer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|CustomAnalyzer
name|custom2
init|=
operator|(
name|CustomAnalyzer
operator|)
name|analyzer
decl_stmt|;
comment|//        HtmlStripCharFilterFactory html = (HtmlStripCharFilterFactory) custom2.charFilters()[0];
comment|//        assertThat(html.readAheadLimit(), equalTo(HTMLStripCharFilter.DEFAULT_READ_AHEAD));
comment|//
comment|//        html = (HtmlStripCharFilterFactory) custom2.charFilters()[1];
comment|//        assertThat(html.readAheadLimit(), equalTo(1024));
comment|// verify characters  mapping
name|analyzer
operator|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"custom5"
argument_list|)
operator|.
name|analyzer
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|analyzer
argument_list|,
name|instanceOf
argument_list|(
name|CustomAnalyzer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|CustomAnalyzer
name|custom5
init|=
operator|(
name|CustomAnalyzer
operator|)
name|analyzer
decl_stmt|;
name|assertThat
argument_list|(
name|custom5
operator|.
name|charFilters
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|instanceOf
argument_list|(
name|MappingCharFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify aliases
name|analyzer
operator|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"alias1"
argument_list|)
operator|.
name|analyzer
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|analyzer
argument_list|,
name|instanceOf
argument_list|(
name|StandardAnalyzer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// check custom pattern replace filter
name|analyzer
operator|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"custom3"
argument_list|)
operator|.
name|analyzer
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|analyzer
argument_list|,
name|instanceOf
argument_list|(
name|CustomAnalyzer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|CustomAnalyzer
name|custom3
init|=
operator|(
name|CustomAnalyzer
operator|)
name|analyzer
decl_stmt|;
name|PatternReplaceCharFilterFactory
name|patternReplaceCharFilterFactory
init|=
operator|(
name|PatternReplaceCharFilterFactory
operator|)
name|custom3
operator|.
name|charFilters
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertThat
argument_list|(
name|patternReplaceCharFilterFactory
operator|.
name|getPattern
argument_list|()
operator|.
name|pattern
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"sample(.*)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|patternReplaceCharFilterFactory
operator|.
name|getReplacement
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"replacedSample $1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check custom class name (my)
name|analyzer
operator|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"custom4"
argument_list|)
operator|.
name|analyzer
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|analyzer
argument_list|,
name|instanceOf
argument_list|(
name|CustomAnalyzer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|CustomAnalyzer
name|custom4
init|=
operator|(
name|CustomAnalyzer
operator|)
name|analyzer
decl_stmt|;
name|assertThat
argument_list|(
name|custom4
operator|.
name|tokenFilters
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|instanceOf
argument_list|(
name|MyFilterTokenFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|//        // verify Czech stemmer
comment|//        analyzer = analysisService.analyzer("czechAnalyzerWithStemmer").analyzer();
comment|//        assertThat(analyzer, instanceOf(CustomAnalyzer.class));
comment|//        CustomAnalyzer czechstemmeranalyzer = (CustomAnalyzer) analyzer;
comment|//        assertThat(czechstemmeranalyzer.tokenizerFactory(), instanceOf(StandardTokenizerFactory.class));
comment|//        assertThat(czechstemmeranalyzer.tokenFilters().length, equalTo(4));
comment|//        assertThat(czechstemmeranalyzer.tokenFilters()[3], instanceOf(CzechStemTokenFilterFactory.class));
comment|//
comment|//        // check dictionary decompounder
comment|//        analyzer = analysisService.analyzer("decompoundingAnalyzer").analyzer();
comment|//        assertThat(analyzer, instanceOf(CustomAnalyzer.class));
comment|//        CustomAnalyzer dictionaryDecompounderAnalyze = (CustomAnalyzer) analyzer;
comment|//        assertThat(dictionaryDecompounderAnalyze.tokenizerFactory(), instanceOf(StandardTokenizerFactory.class));
comment|//        assertThat(dictionaryDecompounderAnalyze.tokenFilters().length, equalTo(1));
comment|//        assertThat(dictionaryDecompounderAnalyze.tokenFilters()[0], instanceOf(DictionaryCompoundWordTokenFilterFactory.class));
name|Set
argument_list|<
name|?
argument_list|>
name|wordList
init|=
name|Analysis
operator|.
name|getWordSet
argument_list|(
literal|null
argument_list|,
name|settings
argument_list|,
literal|"index.analysis.filter.dict_dec.word_list"
argument_list|)
decl_stmt|;
name|MatcherAssert
operator|.
name|assertThat
argument_list|(
name|wordList
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|//        MatcherAssert.assertThat(wordList, hasItems("donau", "dampf", "schiff", "spargel", "creme", "suppe"));
block|}
annotation|@
name|Test
DECL|method|testWordListPath
specifier|public
name|void
name|testWordListPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Environment
name|env
init|=
operator|new
name|Environment
argument_list|(
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
decl_stmt|;
name|String
index|[]
name|words
init|=
operator|new
name|String
index|[]
block|{
literal|"donau"
block|,
literal|"dampf"
block|,
literal|"schiff"
block|,
literal|"spargel"
block|,
literal|"creme"
block|,
literal|"suppe"
block|}
decl_stmt|;
name|Path
name|wordListFile
init|=
name|generateWordList
argument_list|(
name|words
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|loadFromSource
argument_list|(
literal|"index: \n  word_list_path: "
operator|+
name|wordListFile
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|wordList
init|=
name|Analysis
operator|.
name|getWordSet
argument_list|(
name|env
argument_list|,
name|settings
argument_list|,
literal|"index.word_list"
argument_list|)
decl_stmt|;
name|MatcherAssert
operator|.
name|assertThat
argument_list|(
name|wordList
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|//        MatcherAssert.assertThat(wordList, hasItems(words));
name|Files
operator|.
name|delete
argument_list|(
name|wordListFile
argument_list|)
expr_stmt|;
block|}
DECL|method|generateWordList
specifier|private
name|Path
name|generateWordList
parameter_list|(
name|String
index|[]
name|words
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|wordListFile
init|=
name|newTempDirPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"wordlist.txt"
argument_list|)
decl_stmt|;
try|try
init|(
name|BufferedWriter
name|writer
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|wordListFile
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
for|for
control|(
name|String
name|word
range|:
name|words
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|word
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|wordListFile
return|;
block|}
block|}
end_class

end_unit

