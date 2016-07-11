begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.analysis.kuromoji
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|analysis
operator|.
name|kuromoji
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
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
operator|.
name|AnalyzerProvider
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
name|CharFilterFactory
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
name|JapaneseStopTokenFilterFactory
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
name|KuromojiAnalyzerProvider
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
name|KuromojiBaseFormFilterFactory
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
name|KuromojiIterationMarkCharFilterFactory
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
name|KuromojiKatakanaStemmerFactory
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
name|KuromojiNumberFilterFactory
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
name|KuromojiPartOfSpeechFilterFactory
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
name|KuromojiReadingFormFilterFactory
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
name|KuromojiTokenizerFactory
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
name|TokenizerFactory
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
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|singletonMap
import|;
end_import

begin_class
DECL|class|AnalysisKuromojiPlugin
specifier|public
class|class
name|AnalysisKuromojiPlugin
extends|extends
name|Plugin
implements|implements
name|AnalysisPlugin
block|{
annotation|@
name|Override
DECL|method|getCharFilters
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|AnalysisProvider
argument_list|<
name|CharFilterFactory
argument_list|>
argument_list|>
name|getCharFilters
parameter_list|()
block|{
return|return
name|singletonMap
argument_list|(
literal|"kuromoji_iteration_mark"
argument_list|,
name|KuromojiIterationMarkCharFilterFactory
operator|::
operator|new
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTokenFilters
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
name|Map
argument_list|<
name|String
argument_list|,
name|AnalysisProvider
argument_list|<
name|TokenFilterFactory
argument_list|>
argument_list|>
name|extra
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|extra
operator|.
name|put
argument_list|(
literal|"kuromoji_baseform"
argument_list|,
name|KuromojiBaseFormFilterFactory
operator|::
operator|new
argument_list|)
expr_stmt|;
name|extra
operator|.
name|put
argument_list|(
literal|"kuromoji_part_of_speech"
argument_list|,
name|KuromojiPartOfSpeechFilterFactory
operator|::
operator|new
argument_list|)
expr_stmt|;
name|extra
operator|.
name|put
argument_list|(
literal|"kuromoji_readingform"
argument_list|,
name|KuromojiReadingFormFilterFactory
operator|::
operator|new
argument_list|)
expr_stmt|;
name|extra
operator|.
name|put
argument_list|(
literal|"kuromoji_stemmer"
argument_list|,
name|KuromojiKatakanaStemmerFactory
operator|::
operator|new
argument_list|)
expr_stmt|;
name|extra
operator|.
name|put
argument_list|(
literal|"ja_stop"
argument_list|,
name|JapaneseStopTokenFilterFactory
operator|::
operator|new
argument_list|)
expr_stmt|;
name|extra
operator|.
name|put
argument_list|(
literal|"kuromoji_number"
argument_list|,
name|KuromojiNumberFilterFactory
operator|::
operator|new
argument_list|)
expr_stmt|;
return|return
name|extra
return|;
block|}
annotation|@
name|Override
DECL|method|getTokenizers
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|AnalysisProvider
argument_list|<
name|TokenizerFactory
argument_list|>
argument_list|>
name|getTokenizers
parameter_list|()
block|{
return|return
name|singletonMap
argument_list|(
literal|"kuromoji_tokenizer"
argument_list|,
name|KuromojiTokenizerFactory
operator|::
operator|new
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAnalyzers
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|AnalysisProvider
argument_list|<
name|AnalyzerProvider
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
argument_list|>
argument_list|>
name|getAnalyzers
parameter_list|()
block|{
return|return
name|singletonMap
argument_list|(
literal|"kuromoji"
argument_list|,
name|KuromojiAnalyzerProvider
operator|::
operator|new
argument_list|)
return|;
block|}
block|}
end_class

end_unit

