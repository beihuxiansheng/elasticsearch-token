begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|StopAnalyzer
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
name|snowball
operator|.
name|SnowballAnalyzer
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
name|collect
operator|.
name|ImmutableMap
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
name|collect
operator|.
name|ImmutableSet
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
name|collect
operator|.
name|MapBuilder
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
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
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
name|settings
operator|.
name|IndexSettings
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

begin_comment
comment|/**  * Creates a SnowballAnalyzer initialized with stopwords and Snowball filter. Only  * supports Dutch, English (default), French, German and German2 where stopwords  * are readily available. For other languages available with the Lucene Snowball  * Stemmer, use them directly with the SnowballFilter and a CustomAnalyzer.  * Configuration of language is done with the "language" attribute or the analyzer.  * Also supports additional stopwords via "stopwords" attribute  *  * The SnowballAnalyzer comes with a StandardFilter, LowerCaseFilter, StopFilter  * and the SnowballFilter.  *  * @author kimchy (Shay Banon)  * @author harryf (Harry Fuecks)  */
end_comment

begin_class
DECL|class|SnowballAnalyzerProvider
specifier|public
class|class
name|SnowballAnalyzerProvider
extends|extends
name|AbstractIndexAnalyzerProvider
argument_list|<
name|SnowballAnalyzer
argument_list|>
block|{
DECL|field|defaultLanguageStopwords
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|?
argument_list|>
argument_list|>
name|defaultLanguageStopwords
init|=
name|MapBuilder
operator|.
expr|<
name|String
decl_stmt|,
name|Set
argument_list|<
name|?
argument_list|>
decl|>
name|newMapBuilder
argument_list|()
decl|.
name|put
argument_list|(
literal|"English"
argument_list|,
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
argument_list|)
decl|.
name|put
argument_list|(
literal|"Dutch"
argument_list|,
name|DutchAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"German"
argument_list|,
name|GermanAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"German2"
argument_list|,
name|GermanAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"French"
argument_list|,
name|FrenchAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|immutableMap
argument_list|()
decl_stmt|;
DECL|field|analyzer
specifier|private
specifier|final
name|SnowballAnalyzer
name|analyzer
decl_stmt|;
DECL|method|SnowballAnalyzerProvider
annotation|@
name|Inject
specifier|public
name|SnowballAnalyzerProvider
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|Environment
name|env
parameter_list|,
annotation|@
name|Assisted
name|String
name|name
parameter_list|,
annotation|@
name|Assisted
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|name
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|String
name|language
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"language"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|,
literal|"English"
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|defaultStopwords
init|=
name|defaultLanguageStopwords
operator|.
name|containsKey
argument_list|(
name|language
argument_list|)
condition|?
name|defaultLanguageStopwords
operator|.
name|get
argument_list|(
name|language
argument_list|)
else|:
name|ImmutableSet
operator|.
expr|<
name|Set
argument_list|<
name|?
argument_list|>
operator|>
name|of
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|stopWords
init|=
name|Analysis
operator|.
name|parseStopWords
argument_list|(
name|env
argument_list|,
name|settings
argument_list|,
name|defaultStopwords
argument_list|)
decl_stmt|;
name|analyzer
operator|=
operator|new
name|SnowballAnalyzer
argument_list|(
name|version
argument_list|,
name|language
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
DECL|method|get
annotation|@
name|Override
specifier|public
name|SnowballAnalyzer
name|get
parameter_list|()
block|{
return|return
name|this
operator|.
name|analyzer
return|;
block|}
block|}
end_class

end_unit

