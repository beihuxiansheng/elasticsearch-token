begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ar
operator|.
name|ArabicAnalyzer
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
name|br
operator|.
name|BrazilianAnalyzer
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
name|cn
operator|.
name|ChineseAnalyzer
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
name|de
operator|.
name|GermanAnalyzer
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
name|el
operator|.
name|GreekAnalyzer
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
name|PersianAnalyzer
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
name|fr
operator|.
name|FrenchAnalyzer
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
name|nl
operator|.
name|DutchAnalyzer
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
name|ru
operator|.
name|RussianAnalyzer
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
name|analysis
operator|.
name|th
operator|.
name|ThaiAnalyzer
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
name|lucene
operator|.
name|analysis
operator|.
name|cz
operator|.
name|CzechAnalyzer
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
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
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
name|AnalyzerScope
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
name|PreBuiltAnalyzerProviderFactory
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
name|Builder
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A node level registry of analyzers, to be reused by different indices which use default analyzers.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|IndicesAnalysisService
specifier|public
class|class
name|IndicesAnalysisService
extends|extends
name|AbstractComponent
block|{
DECL|field|analyzerProviderFactories
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PreBuiltAnalyzerProviderFactory
argument_list|>
name|analyzerProviderFactories
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|method|IndicesAnalysisService
specifier|public
name|IndicesAnalysisService
parameter_list|()
block|{
name|super
argument_list|(
name|EMPTY_SETTINGS
argument_list|)
expr_stmt|;
block|}
DECL|method|IndicesAnalysisService
annotation|@
name|Inject
specifier|public
name|IndicesAnalysisService
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"standard"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"standard"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|Lucene
operator|.
name|ANALYZER_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"keyword"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"keyword"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|KeywordAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"stop"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"stop"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|StopAnalyzer
argument_list|(
name|Lucene
operator|.
name|ANALYZER_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"whitespace"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"whitespace"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"simple"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"simple"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// extended ones
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"arabic"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"arabic"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|ArabicAnalyzer
argument_list|(
name|Lucene
operator|.
name|ANALYZER_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"brazilian"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"brazilian"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|BrazilianAnalyzer
argument_list|(
name|Lucene
operator|.
name|ANALYZER_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"chinese"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"chinese"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|ChineseAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"cjk"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"cjk"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|ChineseAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"czech"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"czech"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|CzechAnalyzer
argument_list|(
name|Lucene
operator|.
name|ANALYZER_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"dutch"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"dutch"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|DutchAnalyzer
argument_list|(
name|Lucene
operator|.
name|ANALYZER_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"french"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"french"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|FrenchAnalyzer
argument_list|(
name|Lucene
operator|.
name|ANALYZER_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"german"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"german"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|GermanAnalyzer
argument_list|(
name|Lucene
operator|.
name|ANALYZER_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"greek"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"greek"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|GreekAnalyzer
argument_list|(
name|Lucene
operator|.
name|ANALYZER_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"persian"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"persian"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|PersianAnalyzer
argument_list|(
name|Lucene
operator|.
name|ANALYZER_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"russian"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"russian"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|RussianAnalyzer
argument_list|(
name|Lucene
operator|.
name|ANALYZER_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzerProviderFactories
operator|.
name|put
argument_list|(
literal|"thai"
argument_list|,
operator|new
name|PreBuiltAnalyzerProviderFactory
argument_list|(
literal|"thai"
argument_list|,
name|AnalyzerScope
operator|.
name|INDICES
argument_list|,
operator|new
name|ThaiAnalyzer
argument_list|(
name|Lucene
operator|.
name|ANALYZER_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|analyzerProviderFactory
specifier|public
name|PreBuiltAnalyzerProviderFactory
name|analyzerProviderFactory
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|analyzerProviderFactories
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|hasAnalyzer
specifier|public
name|boolean
name|hasAnalyzer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|analyzer
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
DECL|method|analyzer
specifier|public
name|Analyzer
name|analyzer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|PreBuiltAnalyzerProviderFactory
name|analyzerProviderFactory
init|=
name|analyzerProviderFactory
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzerProviderFactory
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|analyzerProviderFactory
operator|.
name|analyzer
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|PreBuiltAnalyzerProviderFactory
name|analyzerProviderFactory
range|:
name|analyzerProviderFactories
operator|.
name|values
argument_list|()
control|)
block|{
name|analyzerProviderFactory
operator|.
name|analyzer
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

