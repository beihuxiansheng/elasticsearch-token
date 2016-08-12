begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
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
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|analyze
operator|.
name|AnalyzeRequest
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
name|admin
operator|.
name|indices
operator|.
name|analyze
operator|.
name|AnalyzeResponse
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
name|admin
operator|.
name|indices
operator|.
name|analyze
operator|.
name|TransportAnalyzeAction
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
name|UUIDs
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
name|mapper
operator|.
name|AllFieldMapper
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
name|List
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
name|emptyList
import|;
end_import

begin_class
DECL|class|TransportAnalyzeActionTests
specifier|public
class|class
name|TransportAnalyzeActionTests
extends|extends
name|ESTestCase
block|{
DECL|field|analysisService
specifier|private
name|AnalysisService
name|analysisService
decl_stmt|;
DECL|field|registry
specifier|private
name|AnalysisRegistry
name|registry
decl_stmt|;
DECL|field|environment
specifier|private
name|Environment
name|environment
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
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
name|Settings
name|indexSettings
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
name|IndexMetaData
operator|.
name|SETTING_INDEX_UUID
argument_list|,
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.wordDelimiter.type"
argument_list|,
literal|"word_delimiter"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.wordDelimiter.split_on_numerics"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer.custom_analyzer.tokenizer"
argument_list|,
literal|"whitespace"
argument_list|)
operator|.
name|putArray
argument_list|(
literal|"index.analysis.analyzer.custom_analyzer.filter"
argument_list|,
literal|"lowercase"
argument_list|,
literal|"wordDelimiter"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.analyzer.custom_analyzer.tokenizer"
argument_list|,
literal|"whitespace"
argument_list|)
operator|.
name|putArray
argument_list|(
literal|"index.analysis.analyzer.custom_analyzer.filter"
argument_list|,
literal|"lowercase"
argument_list|,
literal|"wordDelimiter"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.tokenizer.trigram.type"
argument_list|,
literal|"ngram"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.tokenizer.trigram.min_gram"
argument_list|,
literal|3
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.tokenizer.trigram.max_gram"
argument_list|,
literal|3
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.synonym.type"
argument_list|,
literal|"synonym"
argument_list|)
operator|.
name|putArray
argument_list|(
literal|"index.analysis.filter.synonym.synonyms"
argument_list|,
literal|"kimchy => shay"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.synonym.tokenizer"
argument_list|,
literal|"trigram"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.synonym.min_gram"
argument_list|,
literal|3
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.synonym.max_gram"
argument_list|,
literal|3
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
name|indexSettings
argument_list|)
decl_stmt|;
name|environment
operator|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|registry
operator|=
operator|new
name|AnalysisModule
argument_list|(
name|environment
argument_list|,
name|emptyList
argument_list|()
argument_list|)
operator|.
name|getAnalysisRegistry
argument_list|()
expr_stmt|;
name|analysisService
operator|=
name|registry
operator|.
name|build
argument_list|(
name|idxSettings
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoAnalysisService
specifier|public
name|void
name|testNoAnalysisService
parameter_list|()
throws|throws
name|IOException
block|{
name|AnalyzeRequest
name|request
init|=
operator|new
name|AnalyzeRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|analyzer
argument_list|(
literal|"standard"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the quick brown fox"
argument_list|)
expr_stmt|;
name|AnalyzeResponse
name|analyze
init|=
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AnalyzeResponse
operator|.
name|AnalyzeToken
argument_list|>
name|tokens
init|=
name|analyze
operator|.
name|getTokens
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|analyzer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|request
operator|.
name|tokenizer
argument_list|(
literal|"whitespace"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addTokenFilter
argument_list|(
literal|"lowercase"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addTokenFilter
argument_list|(
literal|"word_delimiter"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the qu1ck brown fox"
argument_list|)
expr_stmt|;
name|analyze
operator|=
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
name|randomBoolean
argument_list|()
condition|?
name|analysisService
else|:
literal|null
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|analyze
operator|.
name|getTokens
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qu"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ck"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|analyzer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|request
operator|.
name|tokenizer
argument_list|(
literal|"whitespace"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addCharFilter
argument_list|(
literal|"html_strip"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addTokenFilter
argument_list|(
literal|"lowercase"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addTokenFilter
argument_list|(
literal|"word_delimiter"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"<p>the qu1ck brown fox</p>"
argument_list|)
expr_stmt|;
name|analyze
operator|=
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
name|randomBoolean
argument_list|()
condition|?
name|analysisService
else|:
literal|null
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|analyze
operator|.
name|getTokens
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qu"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ck"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"brown"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"fox"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFillsAttributes
specifier|public
name|void
name|testFillsAttributes
parameter_list|()
throws|throws
name|IOException
block|{
name|AnalyzeRequest
name|request
init|=
operator|new
name|AnalyzeRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|analyzer
argument_list|(
literal|"standard"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the 1 brown fox"
argument_list|)
expr_stmt|;
name|AnalyzeResponse
name|analyze
init|=
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AnalyzeResponse
operator|.
name|AnalyzeToken
argument_list|>
name|tokens
init|=
name|analyze
operator|.
name|getTokens
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<ALPHANUM>"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<NUM>"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"brown"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<ALPHANUM>"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"fox"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<ALPHANUM>"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithAnalysisService
specifier|public
name|void
name|testWithAnalysisService
parameter_list|()
throws|throws
name|IOException
block|{
name|AnalyzeRequest
name|request
init|=
operator|new
name|AnalyzeRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|analyzer
argument_list|(
literal|"standard"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the quick brown fox"
argument_list|)
expr_stmt|;
name|request
operator|.
name|analyzer
argument_list|(
literal|"custom_analyzer"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the qu1ck brown fox"
argument_list|)
expr_stmt|;
name|AnalyzeResponse
name|analyze
init|=
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
name|analysisService
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AnalyzeResponse
operator|.
name|AnalyzeToken
argument_list|>
name|tokens
init|=
name|analyze
operator|.
name|getTokens
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|analyzer
argument_list|(
literal|"whitespace"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the qu1ck brown fox-dog"
argument_list|)
expr_stmt|;
name|analyze
operator|=
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
name|analysisService
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|analyze
operator|.
name|getTokens
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|analyzer
argument_list|(
literal|"custom_analyzer"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the qu1ck brown fox-dog"
argument_list|)
expr_stmt|;
name|analyze
operator|=
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
name|analysisService
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|analyze
operator|.
name|getTokens
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|analyzer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|request
operator|.
name|tokenizer
argument_list|(
literal|"whitespace"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addTokenFilter
argument_list|(
literal|"lowercase"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addTokenFilter
argument_list|(
literal|"wordDelimiter"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the qu1ck brown fox-dog"
argument_list|)
expr_stmt|;
name|analyze
operator|=
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
name|analysisService
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|analyze
operator|.
name|getTokens
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qu1ck"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"brown"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"fox"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dog"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|analyzer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|request
operator|.
name|tokenizer
argument_list|(
literal|"trigram"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addTokenFilter
argument_list|(
literal|"synonym"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"kimchy"
argument_list|)
expr_stmt|;
name|analyze
operator|=
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
name|analysisService
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|analyze
operator|.
name|getTokens
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sha"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hay"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetIndexAnalyserWithoutAnalysisService
specifier|public
name|void
name|testGetIndexAnalyserWithoutAnalysisService
parameter_list|()
throws|throws
name|IOException
block|{
name|AnalyzeRequest
name|request
init|=
operator|new
name|AnalyzeRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|analyzer
argument_list|(
literal|"custom_analyzer"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the qu1ck brown fox-dog"
argument_list|)
expr_stmt|;
try|try
block|{
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"no analysis service provided"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"failed to find global analyzer [custom_analyzer]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testUnknown
specifier|public
name|void
name|testUnknown
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|notGlobal
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
try|try
block|{
name|AnalyzeRequest
name|request
init|=
operator|new
name|AnalyzeRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|analyzer
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the qu1ck brown fox"
argument_list|)
expr_stmt|;
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
name|notGlobal
condition|?
name|analysisService
else|:
literal|null
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"no such analyzer"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
if|if
condition|(
name|notGlobal
condition|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"failed to find analyzer [foobar]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"failed to find global analyzer [foobar]"
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|AnalyzeRequest
name|request
init|=
operator|new
name|AnalyzeRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|tokenizer
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the qu1ck brown fox"
argument_list|)
expr_stmt|;
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
name|notGlobal
condition|?
name|analysisService
else|:
literal|null
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"no such analyzer"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
if|if
condition|(
name|notGlobal
condition|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"failed to find tokenizer under [foobar]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"failed to find global tokenizer under [foobar]"
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|AnalyzeRequest
name|request
init|=
operator|new
name|AnalyzeRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|tokenizer
argument_list|(
literal|"whitespace"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addTokenFilter
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the qu1ck brown fox"
argument_list|)
expr_stmt|;
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
name|notGlobal
condition|?
name|analysisService
else|:
literal|null
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"no such analyzer"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
if|if
condition|(
name|notGlobal
condition|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"failed to find token filter under [foobar]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"failed to find global token filter under [foobar]"
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|AnalyzeRequest
name|request
init|=
operator|new
name|AnalyzeRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|tokenizer
argument_list|(
literal|"whitespace"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addTokenFilter
argument_list|(
literal|"lowercase"
argument_list|)
expr_stmt|;
name|request
operator|.
name|addCharFilter
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|request
operator|.
name|text
argument_list|(
literal|"the qu1ck brown fox"
argument_list|)
expr_stmt|;
name|TransportAnalyzeAction
operator|.
name|analyze
argument_list|(
name|request
argument_list|,
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
literal|null
argument_list|,
name|notGlobal
condition|?
name|analysisService
else|:
literal|null
argument_list|,
name|registry
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"no such analyzer"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
if|if
condition|(
name|notGlobal
condition|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"failed to find char filter under [foobar]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"failed to find global char filter under [foobar]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

