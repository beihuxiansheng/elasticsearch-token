begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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
name|LowerCaseFilter
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
name|StopFilter
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
name|en
operator|.
name|EnglishPossessiveFilter
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
name|SnowballFilter
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
name|StandardFilter
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
name|StandardTokenizer
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
name|TurkishLowerCaseFilter
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

begin_comment
comment|/** Filters {@link StandardTokenizer} with {@link StandardFilter}, {@link  * LowerCaseFilter}, {@link StopFilter} and {@link SnowballFilter}.  *  * Available stemmers are listed in org.tartarus.snowball.ext.  The name of a  * stemmer is the part of the class name before "Stemmer", e.g., the stemmer in  * {@link org.tartarus.snowball.ext.EnglishStemmer} is named "English".   * @deprecated (3.1) Use the language-specific analyzer in modules/analysis instead.  * This analyzer WAS removed in Lucene 5.0  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SnowballAnalyzer
specifier|public
specifier|final
class|class
name|SnowballAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|stopSet
specifier|private
name|CharArraySet
name|stopSet
decl_stmt|;
comment|/** Builds the named analyzer with no stop words. */
DECL|method|SnowballAnalyzer
specifier|public
name|SnowballAnalyzer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/** Builds the named analyzer with the given stop words. */
DECL|method|SnowballAnalyzer
specifier|public
name|SnowballAnalyzer
parameter_list|(
name|String
name|name
parameter_list|,
name|CharArraySet
name|stopWords
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|stopSet
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|CharArraySet
operator|.
name|copy
argument_list|(
name|stopWords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs a {@link StandardTokenizer} filtered by a {@link       StandardFilter}, a {@link LowerCaseFilter}, a {@link StopFilter},       and a {@link SnowballFilter} */
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
specifier|final
name|Tokenizer
name|tokenizer
init|=
operator|new
name|StandardTokenizer
argument_list|()
decl_stmt|;
name|TokenStream
name|result
init|=
name|tokenizer
decl_stmt|;
comment|// remove the possessive 's for english stemmers
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"English"
argument_list|)
operator|||
name|name
operator|.
name|equals
argument_list|(
literal|"Porter"
argument_list|)
operator|||
name|name
operator|.
name|equals
argument_list|(
literal|"Lovins"
argument_list|)
condition|)
name|result
operator|=
operator|new
name|EnglishPossessiveFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
comment|// Use a special lowercase filter for turkish, the stemmer expects it.
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"Turkish"
argument_list|)
condition|)
name|result
operator|=
operator|new
name|TurkishLowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
else|else
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
name|stopSet
operator|!=
literal|null
condition|)
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|SnowballFilter
argument_list|(
name|result
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|result
argument_list|)
return|;
block|}
block|}
end_class

end_unit

