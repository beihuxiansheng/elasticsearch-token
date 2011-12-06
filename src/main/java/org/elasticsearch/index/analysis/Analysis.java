begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
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
name|com
operator|.
name|google
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
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
name|CharArraySet
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
name|bg
operator|.
name|BulgarianAnalyzer
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
name|ca
operator|.
name|CatalanAnalyzer
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
name|cz
operator|.
name|CzechAnalyzer
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
name|da
operator|.
name|DanishAnalyzer
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
name|en
operator|.
name|EnglishAnalyzer
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
name|es
operator|.
name|SpanishAnalyzer
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
name|eu
operator|.
name|BasqueAnalyzer
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
name|fi
operator|.
name|FinnishAnalyzer
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
name|gl
operator|.
name|GalicianAnalyzer
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
name|hi
operator|.
name|HindiAnalyzer
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
name|hu
operator|.
name|HungarianAnalyzer
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
name|hy
operator|.
name|ArmenianAnalyzer
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
name|id
operator|.
name|IndonesianAnalyzer
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
name|it
operator|.
name|ItalianAnalyzer
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
name|no
operator|.
name|NorwegianAnalyzer
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
name|pt
operator|.
name|PortugueseAnalyzer
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
name|ro
operator|.
name|RomanianAnalyzer
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
name|sv
operator|.
name|SwedishAnalyzer
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
name|tr
operator|.
name|TurkishAnalyzer
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
name|util
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
name|ElasticSearchIllegalArgumentException
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
name|Strings
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
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStreamReader
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
name|net
operator|.
name|URL
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
name|Arrays
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
name|Set
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|Analysis
specifier|public
class|class
name|Analysis
block|{
DECL|method|isNoStopwords
specifier|public
specifier|static
name|boolean
name|isNoStopwords
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|String
name|value
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"stopwords"
argument_list|)
decl_stmt|;
return|return
name|value
operator|!=
literal|null
operator|&&
literal|"_none_"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|parseStemExclusion
specifier|public
specifier|static
name|Set
argument_list|<
name|?
argument_list|>
name|parseStemExclusion
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|defaultStemExclusion
parameter_list|)
block|{
name|String
name|value
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"stem_exclusion"
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"_none_"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Strings
operator|.
name|commaDelimitedListToSet
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
block|}
name|String
index|[]
name|stopWords
init|=
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"stem_exclusion"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|stopWords
operator|!=
literal|null
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Iterators
operator|.
name|forArray
argument_list|(
name|stopWords
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|defaultStemExclusion
return|;
block|}
block|}
DECL|field|namedStopWords
specifier|public
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
name|namedStopWords
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
literal|"_arabic_"
argument_list|,
name|ArabicAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_armenian_"
argument_list|,
name|ArmenianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_basque_"
argument_list|,
name|BasqueAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_brazilian_"
argument_list|,
name|BrazilianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_bulgarian_"
argument_list|,
name|BulgarianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_catalan_"
argument_list|,
name|CatalanAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_czech_"
argument_list|,
name|CzechAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_danish_"
argument_list|,
name|DanishAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_dutch_"
argument_list|,
name|DutchAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_english_"
argument_list|,
name|EnglishAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_finnish_"
argument_list|,
name|FinnishAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_french_"
argument_list|,
name|FrenchAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_galician_"
argument_list|,
name|GalicianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_german_"
argument_list|,
name|GermanAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_greek_"
argument_list|,
name|GreekAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_hindi_"
argument_list|,
name|HindiAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_hungarian_"
argument_list|,
name|HungarianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_indonesian_"
argument_list|,
name|IndonesianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_italian_"
argument_list|,
name|ItalianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_norwegian_"
argument_list|,
name|NorwegianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_persian_"
argument_list|,
name|PersianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_portuguese_"
argument_list|,
name|PortugueseAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_romanian_"
argument_list|,
name|RomanianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_russian_"
argument_list|,
name|RussianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_spanish_"
argument_list|,
name|SpanishAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_swedish_"
argument_list|,
name|SwedishAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"_turkish_"
argument_list|,
name|TurkishAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
decl|.
name|immutableMap
argument_list|()
decl_stmt|;
DECL|method|parseArticles
specifier|public
specifier|static
name|Set
argument_list|<
name|?
argument_list|>
name|parseArticles
parameter_list|(
name|Environment
name|env
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
name|String
name|value
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"articles"
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"_none_"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|CharArraySet
operator|.
name|EMPTY_SET
return|;
block|}
else|else
block|{
return|return
operator|new
name|CharArraySet
argument_list|(
name|version
argument_list|,
name|Strings
operator|.
name|commaDelimitedListToSet
argument_list|(
name|value
argument_list|)
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"articles_case"
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
name|String
index|[]
name|articles
init|=
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"articles"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|articles
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|CharArraySet
argument_list|(
name|version
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|articles
argument_list|)
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"articles_case"
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
name|CharArraySet
name|pathLoadedArticles
init|=
name|getWordSet
argument_list|(
name|env
argument_list|,
name|settings
argument_list|,
literal|"articles"
argument_list|,
name|version
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathLoadedArticles
operator|!=
literal|null
condition|)
block|{
return|return
name|pathLoadedArticles
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|parseStopWords
specifier|public
specifier|static
name|Set
argument_list|<
name|?
argument_list|>
name|parseStopWords
parameter_list|(
name|Environment
name|env
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|defaultStopWords
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
name|String
name|value
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"stopwords"
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"_none_"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|CharArraySet
operator|.
name|EMPTY_SET
return|;
block|}
else|else
block|{
return|return
operator|new
name|CharArraySet
argument_list|(
name|version
argument_list|,
name|Strings
operator|.
name|commaDelimitedListToSet
argument_list|(
name|value
argument_list|)
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"stopwords_case"
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
name|String
index|[]
name|stopWords
init|=
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"stopwords"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|stopWords
operator|!=
literal|null
condition|)
block|{
name|CharArraySet
name|setStopWords
init|=
operator|new
name|CharArraySet
argument_list|(
name|version
argument_list|,
name|stopWords
operator|.
name|length
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"stopwords_case"
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|stopWord
range|:
name|stopWords
control|)
block|{
if|if
condition|(
name|namedStopWords
operator|.
name|containsKey
argument_list|(
name|stopWord
argument_list|)
condition|)
block|{
name|setStopWords
operator|.
name|addAll
argument_list|(
name|namedStopWords
operator|.
name|get
argument_list|(
name|stopWord
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setStopWords
operator|.
name|add
argument_list|(
name|stopWord
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|setStopWords
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|pathLoadedStopWords
init|=
name|getWordList
argument_list|(
name|env
argument_list|,
name|settings
argument_list|,
literal|"stopwords"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathLoadedStopWords
operator|!=
literal|null
condition|)
block|{
name|CharArraySet
name|setStopWords
init|=
operator|new
name|CharArraySet
argument_list|(
name|version
argument_list|,
name|pathLoadedStopWords
operator|.
name|size
argument_list|()
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"stopwords_case"
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|stopWord
range|:
name|pathLoadedStopWords
control|)
block|{
if|if
condition|(
name|namedStopWords
operator|.
name|containsKey
argument_list|(
name|stopWord
argument_list|)
condition|)
block|{
name|setStopWords
operator|.
name|addAll
argument_list|(
name|namedStopWords
operator|.
name|get
argument_list|(
name|stopWord
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setStopWords
operator|.
name|add
argument_list|(
name|stopWord
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|setStopWords
return|;
block|}
return|return
name|defaultStopWords
return|;
block|}
DECL|method|getWordSet
specifier|public
specifier|static
name|CharArraySet
name|getWordSet
parameter_list|(
name|Environment
name|env
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|String
name|settingsPrefix
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|wordList
init|=
name|getWordList
argument_list|(
name|env
argument_list|,
name|settings
argument_list|,
name|settingsPrefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|wordList
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|CharArraySet
argument_list|(
name|version
argument_list|,
name|wordList
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|settingsPrefix
operator|+
literal|"_case"
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Fetches a list of words from the specified settings file. The list should either be available at the key      * specified by settingsPrefix or in a file specified by settingsPrefix + _path.      *      * @throws ElasticSearchIllegalArgumentException      *          If the word list cannot be found at either key.      */
DECL|method|getWordList
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getWordList
parameter_list|(
name|Environment
name|env
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|String
name|settingPrefix
parameter_list|)
block|{
name|String
name|wordListPath
init|=
name|settings
operator|.
name|get
argument_list|(
name|settingPrefix
operator|+
literal|"_path"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|wordListPath
operator|==
literal|null
condition|)
block|{
name|String
index|[]
name|explicitWordList
init|=
name|settings
operator|.
name|getAsArray
argument_list|(
name|settingPrefix
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|explicitWordList
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|explicitWordList
argument_list|)
return|;
block|}
block|}
name|URL
name|wordListFile
init|=
name|env
operator|.
name|resolveConfig
argument_list|(
name|wordListPath
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|loadWordList
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|wordListFile
operator|.
name|openStream
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|"#"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
literal|"IOException while reading %s_path: %s"
argument_list|,
name|settingPrefix
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
DECL|method|loadWordList
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|loadWordList
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|String
name|comment
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|BufferedReader
name|br
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|reader
operator|instanceof
name|BufferedReader
condition|)
block|{
name|br
operator|=
operator|(
name|BufferedReader
operator|)
name|reader
expr_stmt|;
block|}
else|else
block|{
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|String
name|word
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|word
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasText
argument_list|(
name|word
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|word
operator|.
name|startsWith
argument_list|(
name|comment
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|word
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|br
operator|!=
literal|null
condition|)
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * @return null If no settings set for "settingsPrefix" then return<code>null</code>.      * @throws ElasticSearchIllegalArgumentException      *          If the Reader can not be instantiated.      */
DECL|method|getReaderFromFile
specifier|public
specifier|static
name|Reader
name|getReaderFromFile
parameter_list|(
name|Environment
name|env
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|String
name|settingPrefix
parameter_list|)
block|{
name|String
name|filePath
init|=
name|settings
operator|.
name|get
argument_list|(
name|settingPrefix
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|filePath
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|URL
name|fileUrl
init|=
name|env
operator|.
name|resolveConfig
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|fileUrl
operator|.
name|openStream
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
literal|"IOException while reading %s_path: %s"
argument_list|,
name|settingPrefix
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
name|message
argument_list|)
throw|;
block|}
return|return
name|reader
return|;
block|}
block|}
end_class

end_unit

