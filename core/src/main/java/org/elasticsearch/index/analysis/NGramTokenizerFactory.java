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
name|ngram
operator|.
name|Lucene43NGramTokenizer
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
name|ngram
operator|.
name|NGramTokenizer
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
name|index
operator|.
name|IndexSettings
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|Locale
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
name|unmodifiableMap
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|NGramTokenizerFactory
specifier|public
class|class
name|NGramTokenizerFactory
extends|extends
name|AbstractTokenizerFactory
block|{
DECL|field|minGram
specifier|private
specifier|final
name|int
name|minGram
decl_stmt|;
DECL|field|maxGram
specifier|private
specifier|final
name|int
name|maxGram
decl_stmt|;
DECL|field|matcher
specifier|private
specifier|final
name|CharMatcher
name|matcher
decl_stmt|;
DECL|field|esVersion
specifier|private
name|org
operator|.
name|elasticsearch
operator|.
name|Version
name|esVersion
decl_stmt|;
DECL|field|MATCHERS
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CharMatcher
argument_list|>
name|MATCHERS
decl_stmt|;
static|static
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|CharMatcher
argument_list|>
name|matchers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|matchers
operator|.
name|put
argument_list|(
literal|"letter"
argument_list|,
name|CharMatcher
operator|.
name|Basic
operator|.
name|LETTER
argument_list|)
expr_stmt|;
name|matchers
operator|.
name|put
argument_list|(
literal|"digit"
argument_list|,
name|CharMatcher
operator|.
name|Basic
operator|.
name|DIGIT
argument_list|)
expr_stmt|;
name|matchers
operator|.
name|put
argument_list|(
literal|"whitespace"
argument_list|,
name|CharMatcher
operator|.
name|Basic
operator|.
name|WHITESPACE
argument_list|)
expr_stmt|;
name|matchers
operator|.
name|put
argument_list|(
literal|"punctuation"
argument_list|,
name|CharMatcher
operator|.
name|Basic
operator|.
name|PUNCTUATION
argument_list|)
expr_stmt|;
name|matchers
operator|.
name|put
argument_list|(
literal|"symbol"
argument_list|,
name|CharMatcher
operator|.
name|Basic
operator|.
name|SYMBOL
argument_list|)
expr_stmt|;
comment|// Populate with unicode categories from java.lang.Character
for|for
control|(
name|Field
name|field
range|:
name|Character
operator|.
name|class
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|field
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"DIRECTIONALITY"
argument_list|)
operator|&&
name|Modifier
operator|.
name|isPublic
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|Modifier
operator|.
name|isStatic
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|field
operator|.
name|getType
argument_list|()
operator|==
name|byte
operator|.
name|class
condition|)
block|{
try|try
block|{
name|matchers
operator|.
name|put
argument_list|(
name|field
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|CharMatcher
operator|.
name|ByUnicodeCategory
operator|.
name|of
argument_list|(
name|field
operator|.
name|getByte
argument_list|(
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// just ignore
continue|continue;
block|}
block|}
block|}
name|MATCHERS
operator|=
name|unmodifiableMap
argument_list|(
name|matchers
argument_list|)
expr_stmt|;
block|}
DECL|method|parseTokenChars
specifier|static
name|CharMatcher
name|parseTokenChars
parameter_list|(
name|String
index|[]
name|characterClasses
parameter_list|)
block|{
if|if
condition|(
name|characterClasses
operator|==
literal|null
operator|||
name|characterClasses
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|CharMatcher
operator|.
name|Builder
name|builder
init|=
operator|new
name|CharMatcher
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|characterClass
range|:
name|characterClasses
control|)
block|{
name|characterClass
operator|=
name|characterClass
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|CharMatcher
name|matcher
init|=
name|MATCHERS
operator|.
name|get
argument_list|(
name|characterClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown token type: '"
operator|+
name|characterClass
operator|+
literal|"', must be one of "
operator|+
name|MATCHERS
operator|.
name|keySet
argument_list|()
argument_list|)
throw|;
block|}
name|builder
operator|.
name|or
argument_list|(
name|matcher
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Inject
DECL|method|NGramTokenizerFactory
specifier|public
name|NGramTokenizerFactory
parameter_list|(
name|IndexSettings
name|indexSettings
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
name|indexSettings
argument_list|,
name|name
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|minGram
operator|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"min_gram"
argument_list|,
name|NGramTokenizer
operator|.
name|DEFAULT_MIN_NGRAM_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxGram
operator|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"max_gram"
argument_list|,
name|NGramTokenizer
operator|.
name|DEFAULT_MAX_NGRAM_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|matcher
operator|=
name|parseTokenChars
argument_list|(
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"token_chars"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|esVersion
operator|=
name|indexSettings
operator|.
name|getVersion
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
DECL|method|create
specifier|public
name|Tokenizer
name|create
parameter_list|()
block|{
if|if
condition|(
name|version
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_4_3
argument_list|)
operator|&&
name|esVersion
operator|.
name|onOrAfter
argument_list|(
name|org
operator|.
name|elasticsearch
operator|.
name|Version
operator|.
name|V_0_90_2
argument_list|)
condition|)
block|{
comment|/*              * We added this in 0.90.2 but 0.90.1 used LUCENE_43 already so we can not rely on the lucene version.              * Yet if somebody uses 0.90.2 or higher with a prev. lucene version we should also use the deprecated version.              */
specifier|final
name|Version
name|version
init|=
name|this
operator|.
name|version
operator|==
name|Version
operator|.
name|LUCENE_4_3
condition|?
name|Version
operator|.
name|LUCENE_4_4
else|:
name|this
operator|.
name|version
decl_stmt|;
comment|// always use 4.4 or higher
if|if
condition|(
name|matcher
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|NGramTokenizer
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|NGramTokenizer
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|int
name|chr
parameter_list|)
block|{
return|return
name|matcher
operator|.
name|isTokenChar
argument_list|(
name|chr
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|Lucene43NGramTokenizer
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

