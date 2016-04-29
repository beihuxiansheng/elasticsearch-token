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
name|compound
operator|.
name|HyphenationCompoundWordTokenFilterFactory
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/**   * Alerts us if new analyzers are added to lucene, so we don't miss them.  *<p>  * If we don't want to expose one for a specific reason, just map it to Void  */
end_comment

begin_class
DECL|class|AnalysisFactoryTests
specifier|public
class|class
name|AnalysisFactoryTests
extends|extends
name|ESTestCase
block|{
DECL|field|KNOWN_TOKENIZERS
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|KNOWN_TOKENIZERS
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
block|{
block|{
comment|// deprecated ones, we dont care about these
name|put
argument_list|(
literal|"arabicletter"
argument_list|,
name|Deprecated
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"chinese"
argument_list|,
name|Deprecated
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"cjk"
argument_list|,
name|Deprecated
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"russianletter"
argument_list|,
name|Deprecated
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// exposed in ES
name|put
argument_list|(
literal|"classic"
argument_list|,
name|ClassicTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"edgengram"
argument_list|,
name|EdgeNGramTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"keyword"
argument_list|,
name|KeywordTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"letter"
argument_list|,
name|LetterTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"lowercase"
argument_list|,
name|LowerCaseTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"ngram"
argument_list|,
name|NGramTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"pathhierarchy"
argument_list|,
name|PathHierarchyTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"pattern"
argument_list|,
name|PatternTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"standard"
argument_list|,
name|StandardTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"thai"
argument_list|,
name|ThaiTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"uax29urlemail"
argument_list|,
name|UAX29URLEmailTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"whitespace"
argument_list|,
name|WhitespaceTokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// this one "seems to mess up offsets". probably shouldn't be a tokenizer...
name|put
argument_list|(
literal|"wikipedia"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
DECL|method|testTokenizers
specifier|public
name|void
name|testTokenizers
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|missing
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|TokenizerFactory
operator|.
name|availableTokenizers
argument_list|()
argument_list|)
decl_stmt|;
name|missing
operator|.
name|removeAll
argument_list|(
name|KNOWN_TOKENIZERS
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"new tokenizers found, please update KNOWN_TOKENIZERS: "
operator|+
name|missing
operator|.
name|toString
argument_list|()
argument_list|,
name|missing
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|KNOWN_TOKENFILTERS
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|KNOWN_TOKENFILTERS
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
block|{
block|{
comment|// deprecated ones, we dont care about these
name|put
argument_list|(
literal|"chinese"
argument_list|,
name|Deprecated
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"collationkey"
argument_list|,
name|Deprecated
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"position"
argument_list|,
name|Deprecated
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"thaiword"
argument_list|,
name|Deprecated
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// exposed in ES
name|put
argument_list|(
literal|"apostrophe"
argument_list|,
name|ApostropheFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"arabicnormalization"
argument_list|,
name|ArabicNormalizationFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"arabicstem"
argument_list|,
name|ArabicStemTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"asciifolding"
argument_list|,
name|ASCIIFoldingTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"brazilianstem"
argument_list|,
name|BrazilianStemTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"bulgarianstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"cjkbigram"
argument_list|,
name|CJKBigramFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"cjkwidth"
argument_list|,
name|CJKWidthFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"classic"
argument_list|,
name|ClassicFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"commongrams"
argument_list|,
name|CommonGramsTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"commongramsquery"
argument_list|,
name|CommonGramsTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"czechstem"
argument_list|,
name|CzechStemTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"decimaldigit"
argument_list|,
name|DecimalDigitFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"delimitedpayload"
argument_list|,
name|DelimitedPayloadTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"dictionarycompoundword"
argument_list|,
name|DictionaryCompoundWordTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"edgengram"
argument_list|,
name|EdgeNGramTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"elision"
argument_list|,
name|ElisionTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"englishminimalstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"englishpossessive"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"finnishlightstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"frenchlightstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"frenchminimalstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"galicianminimalstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"galicianstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"germanstem"
argument_list|,
name|GermanStemTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"germanlightstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"germanminimalstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"germannormalization"
argument_list|,
name|GermanNormalizationFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"greeklowercase"
argument_list|,
name|LowerCaseTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"greekstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"hindinormalization"
argument_list|,
name|HindiNormalizationFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"hindistem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"hungarianlightstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"hunspellstem"
argument_list|,
name|HunspellTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"hyphenationcompoundword"
argument_list|,
name|HyphenationCompoundWordTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"indicnormalization"
argument_list|,
name|IndicNormalizationFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"irishlowercase"
argument_list|,
name|LowerCaseTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"indonesianstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"italianlightstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"keepword"
argument_list|,
name|KeepWordFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"keywordmarker"
argument_list|,
name|KeywordMarkerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"kstem"
argument_list|,
name|KStemTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"latvianstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"length"
argument_list|,
name|LengthTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"limittokencount"
argument_list|,
name|LimitTokenCountFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"lowercase"
argument_list|,
name|LowerCaseTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"ngram"
argument_list|,
name|NGramTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"norwegianlightstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"norwegianminimalstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"patterncapturegroup"
argument_list|,
name|PatternCaptureGroupTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"patternreplace"
argument_list|,
name|PatternReplaceTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"persiannormalization"
argument_list|,
name|PersianNormalizationFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"porterstem"
argument_list|,
name|PorterStemTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"portuguesestem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"portugueselightstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"portugueseminimalstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"reversestring"
argument_list|,
name|ReverseTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"russianlightstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"scandinavianfolding"
argument_list|,
name|ScandinavianFoldingFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"scandinaviannormalization"
argument_list|,
name|ScandinavianNormalizationFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"serbiannormalization"
argument_list|,
name|SerbianNormalizationFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"shingle"
argument_list|,
name|ShingleTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"snowballporter"
argument_list|,
name|SnowballTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"soraninormalization"
argument_list|,
name|SoraniNormalizationFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"soranistem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"spanishlightstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"standard"
argument_list|,
name|StandardTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"stemmeroverride"
argument_list|,
name|StemmerOverrideTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"stop"
argument_list|,
name|StopTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"swedishlightstem"
argument_list|,
name|StemmerTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"synonym"
argument_list|,
name|SynonymTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"trim"
argument_list|,
name|TrimTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"truncate"
argument_list|,
name|TruncateTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"turkishlowercase"
argument_list|,
name|LowerCaseTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"type"
argument_list|,
name|KeepTypesFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"uppercase"
argument_list|,
name|UpperCaseTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"worddelimiter"
argument_list|,
name|WordDelimiterTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// TODO: these tokenfilters are not yet exposed: useful?
comment|// suggest stop
name|put
argument_list|(
literal|"suggeststop"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// capitalizes tokens
name|put
argument_list|(
literal|"capitalization"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// like length filter (but codepoints)
name|put
argument_list|(
literal|"codepointcount"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// puts hyphenated words back together
name|put
argument_list|(
literal|"hyphenatedwords"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// repeats anything marked as keyword
name|put
argument_list|(
literal|"keywordrepeat"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// like limittokencount, but by offset
name|put
argument_list|(
literal|"limittokenoffset"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// like limittokencount, but by position
name|put
argument_list|(
literal|"limittokenposition"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// ???
name|put
argument_list|(
literal|"numericpayload"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// removes duplicates at the same position (this should be used by the existing factory)
name|put
argument_list|(
literal|"removeduplicates"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// ???
name|put
argument_list|(
literal|"tokenoffsetpayload"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// puts the type into the payload
name|put
argument_list|(
literal|"typeaspayload"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// fingerprint
name|put
argument_list|(
literal|"fingerprint"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// for tee-sinks
name|put
argument_list|(
literal|"daterecognizer"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
DECL|method|testTokenFilters
specifier|public
name|void
name|testTokenFilters
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|missing
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|TokenFilterFactory
operator|.
name|availableTokenFilters
argument_list|()
argument_list|)
decl_stmt|;
name|missing
operator|.
name|removeAll
argument_list|(
name|KNOWN_TOKENFILTERS
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"new tokenfilters found, please update KNOWN_TOKENFILTERS: "
operator|+
name|missing
operator|.
name|toString
argument_list|()
argument_list|,
name|missing
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|KNOWN_CHARFILTERS
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|KNOWN_CHARFILTERS
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
block|{
block|{
comment|// exposed in ES
name|put
argument_list|(
literal|"htmlstrip"
argument_list|,
name|HtmlStripCharFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"mapping"
argument_list|,
name|MappingCharFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
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
name|put
argument_list|(
literal|"persian"
argument_list|,
name|Void
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
DECL|method|testCharFilters
specifier|public
name|void
name|testCharFilters
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|missing
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|CharFilterFactory
operator|.
name|availableCharFilters
argument_list|()
argument_list|)
decl_stmt|;
name|missing
operator|.
name|removeAll
argument_list|(
name|KNOWN_CHARFILTERS
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"new charfilters found, please update KNOWN_CHARFILTERS: "
operator|+
name|missing
operator|.
name|toString
argument_list|()
argument_list|,
name|missing
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

