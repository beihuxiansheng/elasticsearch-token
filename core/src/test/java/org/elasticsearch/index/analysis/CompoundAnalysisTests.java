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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|lucene
operator|.
name|all
operator|.
name|AllEntries
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
name|all
operator|.
name|AllTokenStream
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
name|compound
operator|.
name|DictionaryCompoundWordTokenFilterFactory
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
name|ESTestCase
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|hasItems
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
DECL|class|CompoundAnalysisTests
specifier|public
class|class
name|CompoundAnalysisTests
extends|extends
name|ESTestCase
block|{
DECL|method|testDefaultsCompoundAnalysis
specifier|public
name|void
name|testDefaultsCompoundAnalysis
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|settings
init|=
name|getJsonSettings
argument_list|()
decl_stmt|;
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
name|AnalysisModule
name|analysisModule
init|=
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
decl_stmt|;
name|TokenFilterFactory
name|filterFactory
init|=
name|analysisModule
operator|.
name|getAnalysisRegistry
argument_list|()
operator|.
name|buildTokenFilterFactories
argument_list|(
name|idxSettings
argument_list|)
operator|.
name|get
argument_list|(
literal|"dict_dec"
argument_list|)
decl_stmt|;
name|MatcherAssert
operator|.
name|assertThat
argument_list|(
name|filterFactory
argument_list|,
name|instanceOf
argument_list|(
name|DictionaryCompoundWordTokenFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDictionaryDecompounder
specifier|public
name|void
name|testDictionaryDecompounder
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
index|[]
name|settingsArr
init|=
operator|new
name|Settings
index|[]
block|{
name|getJsonSettings
argument_list|()
block|,
name|getYamlSettings
argument_list|()
block|}
decl_stmt|;
for|for
control|(
name|Settings
name|settings
range|:
name|settingsArr
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
name|analyze
argument_list|(
name|settings
argument_list|,
literal|"decompoundingAnalyzer"
argument_list|,
literal|"donaudampfschiff spargelcremesuppe"
argument_list|)
decl_stmt|;
name|MatcherAssert
operator|.
name|assertThat
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|MatcherAssert
operator|.
name|assertThat
argument_list|(
name|terms
argument_list|,
name|hasItems
argument_list|(
literal|"donau"
argument_list|,
literal|"dampf"
argument_list|,
literal|"schiff"
argument_list|,
literal|"donaudampfschiff"
argument_list|,
literal|"spargel"
argument_list|,
literal|"creme"
argument_list|,
literal|"suppe"
argument_list|,
literal|"spargelcremesuppe"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|analyze
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|analyze
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|analyzerName
parameter_list|,
name|String
name|text
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
name|AnalysisModule
name|analysisModule
init|=
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
decl_stmt|;
name|IndexAnalyzers
name|indexAnalyzers
init|=
name|analysisModule
operator|.
name|getAnalysisRegistry
argument_list|()
operator|.
name|build
argument_list|(
name|idxSettings
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|indexAnalyzers
operator|.
name|get
argument_list|(
name|analyzerName
argument_list|)
operator|.
name|analyzer
argument_list|()
decl_stmt|;
name|AllEntries
name|allEntries
init|=
operator|new
name|AllEntries
argument_list|()
decl_stmt|;
name|allEntries
operator|.
name|addText
argument_list|(
literal|"field1"
argument_list|,
name|text
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|AllTokenStream
operator|.
name|allTokenStream
argument_list|(
literal|"_all"
argument_list|,
name|text
argument_list|,
literal|1.0f
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|tokText
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|tokText
argument_list|)
expr_stmt|;
block|}
return|return
name|terms
return|;
block|}
DECL|method|getJsonSettings
specifier|private
name|Settings
name|getJsonSettings
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|json
init|=
literal|"/org/elasticsearch/index/analysis/test1.json"
decl_stmt|;
return|return
name|Settings
operator|.
name|builder
argument_list|()
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
DECL|method|getYamlSettings
specifier|private
name|Settings
name|getYamlSettings
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|yaml
init|=
literal|"/org/elasticsearch/index/analysis/test1.yml"
decl_stmt|;
return|return
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
block|}
end_class

end_unit

