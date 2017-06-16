begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.analysis.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|analysis
operator|.
name|common
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
name|charfilter
operator|.
name|HTMLStripCharFilterFactory
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
name|PorterStemFilterFactory
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
name|LimitTokenCountFilterFactory
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
name|payloads
operator|.
name|DelimitedPayloadTokenFilterFactory
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
name|reverse
operator|.
name|ReverseStringFilterFactory
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
name|SnowballPorterFilterFactory
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
name|HtmlStripCharFilterFactory
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
name|AnalysisFactoryTestCase
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
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
import|;
end_import

begin_class
DECL|class|CommonAnalysisFactoryTests
specifier|public
class|class
name|CommonAnalysisFactoryTests
extends|extends
name|AnalysisFactoryTestCase
block|{
DECL|method|CommonAnalysisFactoryTests
specifier|public
name|CommonAnalysisFactoryTests
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|CommonAnalysisPlugin
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTokenizers
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|getTokenizers
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|tokenizers
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|super
operator|.
name|getTokenizers
argument_list|()
argument_list|)
decl_stmt|;
name|tokenizers
operator|.
name|put
argument_list|(
literal|"simplepattern"
argument_list|,
name|SimplePatternTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|tokenizers
operator|.
name|put
argument_list|(
literal|"simplepatternsplit"
argument_list|,
name|SimplePatternSplitTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|tokenizers
return|;
block|}
annotation|@
name|Override
DECL|method|getTokenFilters
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|getTokenFilters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|filters
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|super
operator|.
name|getTokenFilters
argument_list|()
argument_list|)
decl_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"asciifolding"
argument_list|,
name|ASCIIFoldingTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"keywordmarker"
argument_list|,
name|KeywordMarkerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"porterstem"
argument_list|,
name|PorterStemTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"snowballporter"
argument_list|,
name|SnowballTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"trim"
argument_list|,
name|TrimTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"worddelimiter"
argument_list|,
name|WordDelimiterTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"worddelimitergraph"
argument_list|,
name|WordDelimiterGraphTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"flattengraph"
argument_list|,
name|FlattenGraphTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"length"
argument_list|,
name|LengthTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"greeklowercase"
argument_list|,
name|LowerCaseTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"irishlowercase"
argument_list|,
name|LowerCaseTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"lowercase"
argument_list|,
name|LowerCaseTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"turkishlowercase"
argument_list|,
name|LowerCaseTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"uppercase"
argument_list|,
name|UpperCaseTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"ngram"
argument_list|,
name|NGramTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"edgengram"
argument_list|,
name|EdgeNGramTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|filters
return|;
block|}
annotation|@
name|Override
DECL|method|getCharFilters
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|getCharFilters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|filters
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|super
operator|.
name|getCharFilters
argument_list|()
argument_list|)
decl_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"htmlstrip"
argument_list|,
name|HtmlStripCharFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"mapping"
argument_list|,
name|MappingCharFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"patternreplace"
argument_list|,
name|PatternReplaceCharFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// TODO: these charfilters are not yet exposed: useful?
comment|// handling of zwnj for persian
name|filters
operator|.
name|put
argument_list|(
literal|"persian"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|filters
return|;
block|}
annotation|@
name|Override
DECL|method|getPreConfiguredCharFilters
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|getPreConfiguredCharFilters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|filters
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|super
operator|.
name|getPreConfiguredCharFilters
argument_list|()
argument_list|)
decl_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"html_strip"
argument_list|,
name|HTMLStripCharFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"htmlStrip"
argument_list|,
name|HTMLStripCharFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|filters
return|;
block|}
annotation|@
name|Override
DECL|method|getPreConfiguredTokenFilters
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|getPreConfiguredTokenFilters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|filters
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|super
operator|.
name|getPreConfiguredTokenFilters
argument_list|()
argument_list|)
decl_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"apostrophe"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"arabic_normalization"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"arabic_stem"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"asciifolding"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"brazilian_stem"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"cjk_bigram"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"cjk_width"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"classic"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"common_grams"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"czech_stem"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"decimal_digit"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"delimited_payload_filter"
argument_list|,
name|DelimitedPayloadTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"dutch_stem"
argument_list|,
name|SnowballPorterFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"edge_ngram"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"edgeNGram"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"elision"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"french_stem"
argument_list|,
name|SnowballPorterFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"german_stem"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"german_normalization"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"hindi_normalization"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"indic_normalization"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"keyword_repeat"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"kstem"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"length"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"limit"
argument_list|,
name|LimitTokenCountFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"ngram"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"nGram"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"persian_normalization"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"porter_stem"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"reverse"
argument_list|,
name|ReverseStringFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"russian_stem"
argument_list|,
name|SnowballPorterFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"scandinavian_normalization"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"scandinavian_folding"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"shingle"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"snowball"
argument_list|,
name|SnowballPorterFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"sorani_normalization"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"stemmer"
argument_list|,
name|PorterStemFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"stop"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"trim"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"truncate"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"type_as_payload"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"unique"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"uppercase"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"word_delimiter"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"word_delimiter_graph"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|filters
return|;
block|}
annotation|@
name|Override
DECL|method|getPreConfiguredTokenizers
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|getPreConfiguredTokenizers
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|filters
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|super
operator|.
name|getPreConfiguredTokenizers
argument_list|()
argument_list|)
decl_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"keyword"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"lowercase"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|filters
return|;
block|}
comment|/**      * Fails if a tokenizer is marked in the superclass with {@link MovedToAnalysisCommon} but      * hasn't been marked in this class with its proper factory.      */
DECL|method|testAllTokenizersMarked
specifier|public
name|void
name|testAllTokenizersMarked
parameter_list|()
block|{
name|markedTestCase
argument_list|(
literal|"char filter"
argument_list|,
name|getTokenizers
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Fails if a char filter is marked in the superclass with {@link MovedToAnalysisCommon} but      * hasn't been marked in this class with its proper factory.      */
DECL|method|testAllCharFiltersMarked
specifier|public
name|void
name|testAllCharFiltersMarked
parameter_list|()
block|{
name|markedTestCase
argument_list|(
literal|"char filter"
argument_list|,
name|getCharFilters
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Fails if a char filter is marked in the superclass with {@link MovedToAnalysisCommon} but      * hasn't been marked in this class with its proper factory.      */
DECL|method|testAllTokenFiltersMarked
specifier|public
name|void
name|testAllTokenFiltersMarked
parameter_list|()
block|{
name|markedTestCase
argument_list|(
literal|"token filter"
argument_list|,
name|getTokenFilters
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|markedTestCase
specifier|private
name|void
name|markedTestCase
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|map
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|unmarked
init|=
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|e
lambda|->
name|e
operator|.
name|getValue
argument_list|()
operator|==
name|MovedToAnalysisCommon
operator|.
name|class
argument_list|)
operator|.
name|map
argument_list|(
name|Map
operator|.
name|Entry
operator|::
name|getKey
argument_list|)
operator|.
name|sorted
argument_list|()
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|name
operator|+
literal|" marked in AnalysisFactoryTestCase as moved to analysis-common "
operator|+
literal|"but not mapped here"
argument_list|,
name|emptyList
argument_list|()
argument_list|,
name|unmarked
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

