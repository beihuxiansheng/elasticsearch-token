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
name|ja
operator|.
name|JapaneseAnalyzer
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
name|ja
operator|.
name|JapaneseTokenizer
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
name|plugin
operator|.
name|analysis
operator|.
name|kuromoji
operator|.
name|AnalysisKuromojiPlugin
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
name|Reader
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
name|greaterThan
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
name|notNullValue
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|KuromojiAnalysisTests
specifier|public
class|class
name|KuromojiAnalysisTests
extends|extends
name|ESTestCase
block|{
DECL|method|testDefaultsKuromojiAnalysis
specifier|public
name|void
name|testDefaultsKuromojiAnalysis
parameter_list|()
throws|throws
name|IOException
block|{
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|()
decl_stmt|;
name|TokenizerFactory
name|tokenizerFactory
init|=
name|analysisService
operator|.
name|tokenizer
argument_list|(
literal|"kuromoji_tokenizer"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenizerFactory
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiTokenizerFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|TokenFilterFactory
name|filterFactory
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"kuromoji_part_of_speech"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filterFactory
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiPartOfSpeechFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|filterFactory
operator|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"kuromoji_readingform"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filterFactory
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiReadingFormFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|filterFactory
operator|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"kuromoji_baseform"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filterFactory
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiBaseFormFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|filterFactory
operator|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"kuromoji_stemmer"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filterFactory
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiKatakanaStemmerFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|filterFactory
operator|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"ja_stop"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filterFactory
argument_list|,
name|instanceOf
argument_list|(
name|JapaneseStopTokenFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|NamedAnalyzer
name|analyzer
init|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"kuromoji"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|analyzer
operator|.
name|analyzer
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|JapaneseAnalyzer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|analyzer
operator|=
name|analysisService
operator|.
name|analyzer
argument_list|(
literal|"my_analyzer"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|analyzer
operator|.
name|analyzer
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|CustomAnalyzer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|analyzer
operator|.
name|analyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|JapaneseTokenizer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|CharFilterFactory
name|charFilterFactory
init|=
name|analysisService
operator|.
name|charFilter
argument_list|(
literal|"kuromoji_iteration_mark"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|charFilterFactory
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiIterationMarkCharFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBaseFormFilterFactory
specifier|public
name|void
name|testBaseFormFilterFactory
parameter_list|()
throws|throws
name|IOException
block|{
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|()
decl_stmt|;
name|TokenFilterFactory
name|tokenFilter
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"kuromoji_pos"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiPartOfSpeechFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|source
init|=
literal|"ç§ã¯å¶éã¹ãã¼ããè¶ããã"
decl_stmt|;
name|String
index|[]
name|expected
init|=
operator|new
name|String
index|[]
block|{
literal|"ç§"
block|,
literal|"ã¯"
block|,
literal|"å¶é"
block|,
literal|"ã¹ãã¼ã"
block|,
literal|"ã"
block|}
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|JapaneseTokenizer
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
name|JapaneseTokenizer
operator|.
name|Mode
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|assertSimpleTSOutput
argument_list|(
name|tokenFilter
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
DECL|method|testReadingFormFilterFactory
specifier|public
name|void
name|testReadingFormFilterFactory
parameter_list|()
throws|throws
name|IOException
block|{
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|()
decl_stmt|;
name|TokenFilterFactory
name|tokenFilter
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"kuromoji_rf"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiReadingFormFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|source
init|=
literal|"ä»å¤ã¯ã­ãã¼ãåçã¨è©±ãã"
decl_stmt|;
name|String
index|[]
name|expected_tokens_romaji
init|=
operator|new
name|String
index|[]
block|{
literal|"kon'ya"
block|,
literal|"ha"
block|,
literal|"robato"
block|,
literal|"sensei"
block|,
literal|"to"
block|,
literal|"hanashi"
block|,
literal|"ta"
block|}
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|JapaneseTokenizer
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
name|JapaneseTokenizer
operator|.
name|Mode
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|assertSimpleTSOutput
argument_list|(
name|tokenFilter
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
argument_list|,
name|expected_tokens_romaji
argument_list|)
expr_stmt|;
name|tokenizer
operator|=
operator|new
name|JapaneseTokenizer
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
name|JapaneseTokenizer
operator|.
name|Mode
operator|.
name|SEARCH
argument_list|)
expr_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|expected_tokens_katakana
init|=
operator|new
name|String
index|[]
block|{
literal|"ã³ã³ã¤"
block|,
literal|"ã"
block|,
literal|"ã­ãã¼ã"
block|,
literal|"ã»ã³ã»ã¤"
block|,
literal|"ã"
block|,
literal|"ããã·"
block|,
literal|"ã¿"
block|}
decl_stmt|;
name|tokenFilter
operator|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"kuromoji_readingform"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiReadingFormFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertSimpleTSOutput
argument_list|(
name|tokenFilter
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
argument_list|,
name|expected_tokens_katakana
argument_list|)
expr_stmt|;
block|}
DECL|method|testKatakanaStemFilter
specifier|public
name|void
name|testKatakanaStemFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|()
decl_stmt|;
name|TokenFilterFactory
name|tokenFilter
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"kuromoji_stemmer"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiKatakanaStemmerFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|source
init|=
literal|"æå¾æ¥ãã¼ãã£ã¼ã«è¡ãäºå®ããããå³æ¸é¤¨ã§è³æãã³ãã¼ãã¾ããã"
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|JapaneseTokenizer
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
name|JapaneseTokenizer
operator|.
name|Mode
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
comment|// ãã¼ãã£ã¼ should be stemmed by default
comment|// (min len) ã³ãã¼ should not be stemmed
name|String
index|[]
name|expected_tokens_katakana
init|=
operator|new
name|String
index|[]
block|{
literal|"æå¾æ¥"
block|,
literal|"ãã¼ãã£"
block|,
literal|"ã«"
block|,
literal|"è¡ã"
block|,
literal|"äºå®"
block|,
literal|"ã"
block|,
literal|"ãã"
block|,
literal|"å³æ¸é¤¨"
block|,
literal|"ã§"
block|,
literal|"è³æ"
block|,
literal|"ã"
block|,
literal|"ã³ãã¼"
block|,
literal|"ã"
block|,
literal|"ã¾ã"
block|,
literal|"ã"
block|}
decl_stmt|;
name|assertSimpleTSOutput
argument_list|(
name|tokenFilter
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
argument_list|,
name|expected_tokens_katakana
argument_list|)
expr_stmt|;
name|tokenFilter
operator|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"kuromoji_ks"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiKatakanaStemmerFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|tokenizer
operator|=
operator|new
name|JapaneseTokenizer
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
name|JapaneseTokenizer
operator|.
name|Mode
operator|.
name|SEARCH
argument_list|)
expr_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
comment|// ãã¼ãã£ã¼ should not be stemmed since min len == 6
comment|// ã³ãã¼ should not be stemmed
name|expected_tokens_katakana
operator|=
operator|new
name|String
index|[]
block|{
literal|"æå¾æ¥"
block|,
literal|"ãã¼ãã£ã¼"
block|,
literal|"ã«"
block|,
literal|"è¡ã"
block|,
literal|"äºå®"
block|,
literal|"ã"
block|,
literal|"ãã"
block|,
literal|"å³æ¸é¤¨"
block|,
literal|"ã§"
block|,
literal|"è³æ"
block|,
literal|"ã"
block|,
literal|"ã³ãã¼"
block|,
literal|"ã"
block|,
literal|"ã¾ã"
block|,
literal|"ã"
block|}
expr_stmt|;
name|assertSimpleTSOutput
argument_list|(
name|tokenFilter
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
argument_list|,
name|expected_tokens_katakana
argument_list|)
expr_stmt|;
block|}
DECL|method|testIterationMarkCharFilter
specifier|public
name|void
name|testIterationMarkCharFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|()
decl_stmt|;
comment|// test only kanji
name|CharFilterFactory
name|charFilterFactory
init|=
name|analysisService
operator|.
name|charFilter
argument_list|(
literal|"kuromoji_im_only_kanji"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|charFilterFactory
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|charFilterFactory
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiIterationMarkCharFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|source
init|=
literal|"ã¨ããããããã¸ã¾ããæããé¦¬é¹¿ãããã"
decl_stmt|;
name|String
name|expected
init|=
literal|"ã¨ããããããã¸ã¾ããææãé¦¬é¹¿é¦¬é¹¿ãã"
decl_stmt|;
name|assertCharFilterEquals
argument_list|(
name|charFilterFactory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|source
argument_list|)
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// test only kana
name|charFilterFactory
operator|=
name|analysisService
operator|.
name|charFilter
argument_list|(
literal|"kuromoji_im_only_kana"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|charFilterFactory
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|charFilterFactory
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiIterationMarkCharFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
literal|"ã¨ããã©ãããã¸ã¸ããæããé¦¬é¹¿ãããã"
expr_stmt|;
name|assertCharFilterEquals
argument_list|(
name|charFilterFactory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|source
argument_list|)
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// test default
name|charFilterFactory
operator|=
name|analysisService
operator|.
name|charFilter
argument_list|(
literal|"kuromoji_im_default"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|charFilterFactory
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|charFilterFactory
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiIterationMarkCharFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
literal|"ã¨ããã©ãããã¸ã¸ããææãé¦¬é¹¿é¦¬é¹¿ãã"
expr_stmt|;
name|assertCharFilterEquals
argument_list|(
name|charFilterFactory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|source
argument_list|)
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
DECL|method|testJapaneseStopFilterFactory
specifier|public
name|void
name|testJapaneseStopFilterFactory
parameter_list|()
throws|throws
name|IOException
block|{
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|()
decl_stmt|;
name|TokenFilterFactory
name|tokenFilter
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"ja_stop"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|JapaneseStopTokenFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|source
init|=
literal|"ç§ã¯å¶éã¹ãã¼ããè¶ããã"
decl_stmt|;
name|String
index|[]
name|expected
init|=
operator|new
name|String
index|[]
block|{
literal|"ç§"
block|,
literal|"å¶é"
block|,
literal|"è¶ãã"
block|}
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|JapaneseTokenizer
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
name|JapaneseTokenizer
operator|.
name|Mode
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|assertSimpleTSOutput
argument_list|(
name|tokenFilter
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
DECL|method|createAnalysisService
specifier|public
name|AnalysisService
name|createAnalysisService
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|empty_dict
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"empty_user_dict.txt"
argument_list|)
decl_stmt|;
name|InputStream
name|dict
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"user_dict.txt"
argument_list|)
decl_stmt|;
name|Path
name|home
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Path
name|config
init|=
name|home
operator|.
name|resolve
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|empty_dict
argument_list|,
name|config
operator|.
name|resolve
argument_list|(
literal|"empty_user_dict.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|dict
argument_list|,
name|config
operator|.
name|resolve
argument_list|(
literal|"user_dict.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|json
init|=
literal|"/org/elasticsearch/index/analysis/kuromoji_analysis.json"
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
literal|"path.home"
argument_list|,
name|home
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
argument_list|)
operator|.
name|createInjector
argument_list|()
decl_stmt|;
name|AnalysisModule
name|analysisModule
init|=
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
decl_stmt|;
operator|new
name|AnalysisKuromojiPlugin
argument_list|()
operator|.
name|onModule
argument_list|(
name|analysisModule
argument_list|)
expr_stmt|;
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
name|analysisModule
argument_list|)
operator|.
name|createChildInjector
argument_list|(
name|parentInjector
argument_list|)
decl_stmt|;
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
DECL|method|assertSimpleTSOutput
specifier|public
specifier|static
name|void
name|assertSimpleTSOutput
parameter_list|(
name|TokenStream
name|stream
parameter_list|,
name|String
index|[]
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|CharTermAttribute
name|termAttr
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|termAttr
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|assertThat
argument_list|(
name|expected
operator|.
name|length
argument_list|,
name|greaterThan
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"expected different term at index "
operator|+
name|i
argument_list|,
name|expected
index|[
name|i
operator|++
index|]
argument_list|,
name|equalTo
argument_list|(
name|termAttr
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
literal|"not all tokens produced"
argument_list|,
name|i
argument_list|,
name|equalTo
argument_list|(
name|expected
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCharFilterEquals
specifier|private
name|void
name|assertCharFilterEquals
parameter_list|(
name|Reader
name|filtered
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|actual
init|=
name|readFully
argument_list|(
name|filtered
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|actual
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|readFully
specifier|private
name|String
name|readFully
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|ch
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|testKuromojiUserDict
specifier|public
name|void
name|testKuromojiUserDict
parameter_list|()
throws|throws
name|IOException
block|{
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|()
decl_stmt|;
name|TokenizerFactory
name|tokenizerFactory
init|=
name|analysisService
operator|.
name|tokenizer
argument_list|(
literal|"kuromoji_user_dict"
argument_list|)
decl_stmt|;
name|String
name|source
init|=
literal|"ç§ã¯å¶éã¹ãã¼ããè¶ããã"
decl_stmt|;
name|String
index|[]
name|expected
init|=
operator|new
name|String
index|[]
block|{
literal|"ç§"
block|,
literal|"ã¯"
block|,
literal|"å¶éã¹ãã¼ã"
block|,
literal|"ã"
block|,
literal|"è¶ãã"
block|}
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
name|tokenizerFactory
operator|.
name|create
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|assertSimpleTSOutput
argument_list|(
name|tokenizer
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
comment|// fix #59
DECL|method|testKuromojiEmptyUserDict
specifier|public
name|void
name|testKuromojiEmptyUserDict
parameter_list|()
throws|throws
name|IOException
block|{
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|()
decl_stmt|;
name|TokenizerFactory
name|tokenizerFactory
init|=
name|analysisService
operator|.
name|tokenizer
argument_list|(
literal|"kuromoji_empty_user_dict"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenizerFactory
argument_list|,
name|instanceOf
argument_list|(
name|KuromojiTokenizerFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

