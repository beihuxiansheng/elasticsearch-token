begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.analysis
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
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
name|hunspell
operator|.
name|Dictionary
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
name|SimpleFSDirectory
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
name|ModuleTestCase
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
name|xcontent
operator|.
name|XContentType
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
name|IndexSettings
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
name|Analysis
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
name|AnalysisRegistry
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
name|AnalysisTestsHelper
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
name|CustomAnalyzer
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
name|IndexAnalyzers
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
name|MappingCharFilterFactory
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
name|analysis
operator|.
name|PatternReplaceCharFilterFactory
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
name|StandardTokenizerFactory
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
name|StopTokenFilterFactory
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
name|TokenFilterFactory
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
name|indices
operator|.
name|analysis
operator|.
name|AnalysisModule
operator|.
name|AnalysisProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|AnalysisPlugin
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
name|hamcrest
operator|.
name|MatcherAssert
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
name|InputStream
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
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
name|either
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|is
import|;
end_import

begin_class
DECL|class|AnalysisModuleTests
specifier|public
class|class
name|AnalysisModuleTests
extends|extends
name|ModuleTestCase
block|{
DECL|method|getIndexAnalyzers
specifier|public
name|IndexAnalyzers
name|getIndexAnalyzers
parameter_list|(
name|Settings
name|settings
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getIndexAnalyzers
argument_list|(
name|getNewRegistry
argument_list|(
name|settings
argument_list|)
argument_list|,
name|settings
argument_list|)
return|;
block|}
DECL|method|getIndexAnalyzers
specifier|public
name|IndexAnalyzers
name|getIndexAnalyzers
parameter_list|(
name|AnalysisRegistry
name|registry
parameter_list|,
name|Settings
name|settings
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSettings
name|idxSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
literal|"test"
argument_list|,
name|settings
argument_list|)
decl_stmt|;
return|return
name|registry
operator|.
name|build
argument_list|(
name|idxSettings
argument_list|)
return|;
block|}
DECL|method|getNewRegistry
specifier|public
name|AnalysisRegistry
name|getNewRegistry
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|AnalysisModule
argument_list|(
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
argument_list|,
name|singletonList
argument_list|(
operator|new
name|AnalysisPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|AnalysisProvider
argument_list|<
name|TokenFilterFactory
argument_list|>
argument_list|>
name|getTokenFilters
parameter_list|()
block|{
return|return
name|singletonMap
argument_list|(
literal|"myfilter"
argument_list|,
name|MyFilterTokenFilterFactory
operator|::
operator|new
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
operator|.
name|getAnalysisRegistry
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|loadFromClasspath
specifier|private
name|Settings
name|loadFromClasspath
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|loadFromStream
argument_list|(
name|path
argument_list|,
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|path
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
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|testSimpleConfigurationJson
specifier|public
name|void
name|testSimpleConfigurationJson
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|loadFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/analysis/test1.json"
argument_list|)
decl_stmt|;
name|testSimpleConfiguration
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleConfigurationYaml
specifier|public
name|void
name|testSimpleConfigurationYaml
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|loadFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/analysis/test1.yml"
argument_list|)
decl_stmt|;
name|testSimpleConfiguration
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
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
DECL|method|testAnalyzerAliasNotAllowedPost5x
specifier|public
name|void
name|testAnalyzerAliasNotAllowedPost5x
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer.foobar.type"
argument_list|,
literal|"standard"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer.foobar.alias"
argument_list|,
literal|"foobaz"
argument_list|)
comment|// analyzer aliases were removed in v5.0.0 alpha6
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|Version
operator|.
name|V_5_0_0_beta1
argument_list|,
literal|null
argument_list|)
argument_list|)
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
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisRegistry
name|registry
init|=
name|getNewRegistry
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|getIndexAnalyzers
argument_list|(
name|registry
argument_list|,
name|settings
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"setting [index.analysis.analyzer.foobar.alias] is not supported"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testVersionedAnalyzers
specifier|public
name|void
name|testVersionedAnalyzers
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|yaml
init|=
literal|"/org/elasticsearch/index/analysis/test1.yml"
decl_stmt|;
name|Settings
name|settings2
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|loadFromStream
argument_list|(
name|yaml
argument_list|,
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|yaml
argument_list|)
argument_list|)
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
operator|.
name|toString
argument_list|()
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
name|V_5_0_0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisRegistry
name|newRegistry
init|=
name|getNewRegistry
argument_list|(
name|settings2
argument_list|)
decl_stmt|;
name|IndexAnalyzers
name|indexAnalyzers
init|=
name|getIndexAnalyzers
argument_list|(
name|newRegistry
argument_list|,
name|settings2
argument_list|)
decl_stmt|;
comment|// registry always has the current version
name|assertThat
argument_list|(
name|newRegistry
operator|.
name|getAnalyzer
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
name|newRegistry
operator|.
name|getAnalyzer
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
name|indexAnalyzers
operator|.
name|get
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
name|V_5_0_0
operator|.
name|luceneVersion
argument_list|,
name|indexAnalyzers
operator|.
name|get
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
name|V_5_0_0
operator|.
name|luceneVersion
argument_list|,
name|indexAnalyzers
operator|.
name|get
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
name|assertThat
argument_list|(
name|indexAnalyzers
operator|.
name|get
argument_list|(
literal|"custom7"
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|fromBits
argument_list|(
literal|3
argument_list|,
literal|6
argument_list|,
literal|0
argument_list|)
argument_list|,
name|indexAnalyzers
operator|.
name|get
argument_list|(
literal|"custom7"
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
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
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
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|TestAnalysis
name|analysis
init|=
name|AnalysisTestsHelper
operator|.
name|createTestAnalysisFromSettings
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|tokenFilter
init|=
name|analysis
operator|.
name|tokenFilter
operator|.
name|get
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
throws|throws
name|IOException
block|{
name|IndexAnalyzers
name|indexAnalyzers
init|=
name|getIndexAnalyzers
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|indexAnalyzers
operator|.
name|get
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
name|analyzer
operator|=
name|indexAnalyzers
operator|.
name|get
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
comment|// verify position increment gap
name|analyzer
operator|=
name|indexAnalyzers
operator|.
name|get
argument_list|(
literal|"custom6"
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
name|custom6
init|=
operator|(
name|CustomAnalyzer
operator|)
name|analyzer
decl_stmt|;
name|assertThat
argument_list|(
name|custom6
operator|.
name|getPositionIncrementGap
argument_list|(
literal|"any_string"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|256
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify characters  mapping
name|analyzer
operator|=
name|indexAnalyzers
operator|.
name|get
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
comment|// check custom pattern replace filter
name|analyzer
operator|=
name|indexAnalyzers
operator|.
name|get
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
name|indexAnalyzers
operator|.
name|get
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
name|Version
operator|.
name|CURRENT
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
DECL|method|testWordListPath
specifier|public
name|void
name|testWordListPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
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
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Environment
name|env
init|=
operator|new
name|Environment
argument_list|(
name|settings
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
name|settings
operator|=
name|Settings
operator|.
name|builder
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
argument_list|,
name|XContentType
operator|.
name|YAML
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
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
name|Version
operator|.
name|CURRENT
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
name|createTempDir
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
DECL|method|testUnderscoreInAnalyzerName
specifier|public
name|void
name|testUnderscoreInAnalyzerName
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer._invalid_name.tokenizer"
argument_list|,
literal|"keyword"
argument_list|)
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
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
literal|"1"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|getIndexAnalyzers
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"This should fail with IllegalArgumentException because the analyzers name starts with _"
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
name|either
argument_list|(
name|equalTo
argument_list|(
literal|"analyzer name must not start with '_'. got \"_invalid_name\""
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|equalTo
argument_list|(
literal|"analyzer name must not start with '_'. got \"_invalidName\""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRegisterHunspellDictionary
specifier|public
name|void
name|testRegisterHunspellDictionary
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
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
operator|.
name|toString
argument_list|()
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
name|Environment
name|environment
init|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|InputStream
name|aff
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/indices/analyze/conf_dir/hunspell/en_US/en_US.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dic
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/indices/analyze/conf_dir/hunspell/en_US/en_US.dic"
argument_list|)
decl_stmt|;
name|Dictionary
name|dictionary
decl_stmt|;
try|try
init|(
name|Directory
name|tmp
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|environment
operator|.
name|tmpFile
argument_list|()
argument_list|)
init|)
block|{
name|dictionary
operator|=
operator|new
name|Dictionary
argument_list|(
name|tmp
argument_list|,
literal|"hunspell"
argument_list|,
name|aff
argument_list|,
name|dic
argument_list|)
expr_stmt|;
block|}
name|AnalysisModule
name|module
init|=
operator|new
name|AnalysisModule
argument_list|(
name|environment
argument_list|,
name|singletonList
argument_list|(
operator|new
name|AnalysisPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Dictionary
argument_list|>
name|getHunspellDictionaries
parameter_list|()
block|{
return|return
name|singletonMap
argument_list|(
literal|"foo"
argument_list|,
name|dictionary
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|dictionary
argument_list|,
name|module
operator|.
name|getHunspellService
argument_list|()
operator|.
name|getDictionary
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

