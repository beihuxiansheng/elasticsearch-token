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
name|*
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
operator|.
name|Mode
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
name|component
operator|.
name|AbstractComponent
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
name|Inject
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
name|index
operator|.
name|analysis
operator|.
name|*
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

begin_comment
comment|/**  * Registers indices level analysis components so, if not explicitly configured,  * will be shared among all indices.  */
end_comment

begin_class
DECL|class|KuromojiIndicesAnalysis
specifier|public
class|class
name|KuromojiIndicesAnalysis
extends|extends
name|AbstractComponent
block|{
annotation|@
name|Inject
DECL|method|KuromojiIndicesAnalysis
specifier|public
name|KuromojiIndicesAnalysis
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|IndicesAnalysisService
name|indicesAnalysisService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|indicesAnalysisService
operator|.
name|analyzerProviderFactories
argument_list|()
operator|.
name|put
argument_list|(
literal|"kuromoji"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"kuromoji"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|JapaneseAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|indicesAnalysisService
operator|.
name|charFilterFactories
argument_list|()
operator|.
name|put
argument_list|(
literal|"kuromoji_iteration_mark"
argument_list|,
operator|new
name|PreBuiltCharFilterFactoryFactory
argument_list|(
operator|new
name|CharFilterFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"kuromoji_iteration_mark"
return|;
block|}
annotation|@
name|Override
specifier|public
name|Reader
name|create
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|JapaneseIterationMarkCharFilter
argument_list|(
name|reader
argument_list|,
name|JapaneseIterationMarkCharFilter
operator|.
name|NORMALIZE_KANJI_DEFAULT
argument_list|,
name|JapaneseIterationMarkCharFilter
operator|.
name|NORMALIZE_KANA_DEFAULT
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|indicesAnalysisService
operator|.
name|tokenizerFactories
argument_list|()
operator|.
name|put
argument_list|(
literal|"kuromoji_tokenizer"
argument_list|,
operator|new
name|PreBuiltTokenizerFactoryFactory
argument_list|(
operator|new
name|TokenizerFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"kuromoji_tokenizer"
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tokenizer
name|create
parameter_list|()
block|{
return|return
operator|new
name|JapaneseTokenizer
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
name|Mode
operator|.
name|SEARCH
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|indicesAnalysisService
operator|.
name|tokenFilterFactories
argument_list|()
operator|.
name|put
argument_list|(
literal|"kuromoji_baseform"
argument_list|,
operator|new
name|PreBuiltTokenFilterFactoryFactory
argument_list|(
operator|new
name|TokenFilterFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"kuromoji_baseform"
return|;
block|}
annotation|@
name|Override
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
return|return
operator|new
name|JapaneseBaseFormFilter
argument_list|(
name|tokenStream
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|indicesAnalysisService
operator|.
name|tokenFilterFactories
argument_list|()
operator|.
name|put
argument_list|(
literal|"kuromoji_part_of_speech"
argument_list|,
operator|new
name|PreBuiltTokenFilterFactoryFactory
argument_list|(
operator|new
name|TokenFilterFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"kuromoji_part_of_speech"
return|;
block|}
annotation|@
name|Override
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
return|return
operator|new
name|JapanesePartOfSpeechStopFilter
argument_list|(
name|tokenStream
argument_list|,
name|JapaneseAnalyzer
operator|.
name|getDefaultStopTags
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|indicesAnalysisService
operator|.
name|tokenFilterFactories
argument_list|()
operator|.
name|put
argument_list|(
literal|"kuromoji_readingform"
argument_list|,
operator|new
name|PreBuiltTokenFilterFactoryFactory
argument_list|(
operator|new
name|TokenFilterFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"kuromoji_readingform"
return|;
block|}
annotation|@
name|Override
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
return|return
operator|new
name|JapaneseReadingFormFilter
argument_list|(
name|tokenStream
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|indicesAnalysisService
operator|.
name|tokenFilterFactories
argument_list|()
operator|.
name|put
argument_list|(
literal|"kuromoji_stemmer"
argument_list|,
operator|new
name|PreBuiltTokenFilterFactoryFactory
argument_list|(
operator|new
name|TokenFilterFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"kuromoji_stemmer"
return|;
block|}
annotation|@
name|Override
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
return|return
operator|new
name|JapaneseKatakanaStemFilter
argument_list|(
name|tokenStream
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
