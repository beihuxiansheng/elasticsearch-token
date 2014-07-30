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
name|core
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
name|core
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
name|core
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
name|pattern
operator|.
name|PatternTokenizer
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
name|util
operator|.
name|CharArraySet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|regex
operator|.
name|Regex
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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|PatternAnalyzerProvider
specifier|public
class|class
name|PatternAnalyzerProvider
extends|extends
name|AbstractIndexAnalyzerProvider
argument_list|<
name|Analyzer
argument_list|>
block|{
DECL|field|analyzer
specifier|private
specifier|final
name|PatternAnalyzer
name|analyzer
decl_stmt|;
DECL|class|PatternAnalyzer
specifier|private
specifier|static
specifier|final
class|class
name|PatternAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|version
specifier|private
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
name|version
decl_stmt|;
DECL|field|pattern
specifier|private
specifier|final
name|Pattern
name|pattern
decl_stmt|;
DECL|field|lowercase
specifier|private
specifier|final
name|boolean
name|lowercase
decl_stmt|;
DECL|field|stopWords
specifier|private
specifier|final
name|CharArraySet
name|stopWords
decl_stmt|;
DECL|method|PatternAnalyzer
name|PatternAnalyzer
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
name|version
parameter_list|,
name|Pattern
name|pattern
parameter_list|,
name|boolean
name|lowercase
parameter_list|,
name|CharArraySet
name|stopWords
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
name|this
operator|.
name|lowercase
operator|=
name|lowercase
expr_stmt|;
name|this
operator|.
name|stopWords
operator|=
name|stopWords
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|s
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
specifier|final
name|TokenStreamComponents
name|source
init|=
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|PatternTokenizer
argument_list|(
name|reader
argument_list|,
name|pattern
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|lowercase
condition|)
block|{
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|version
argument_list|,
name|source
operator|.
name|getTokenStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|version
argument_list|,
operator|(
name|result
operator|==
literal|null
operator|)
condition|?
name|source
operator|.
name|getTokenStream
argument_list|()
else|:
name|result
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
operator|.
name|getTokenizer
argument_list|()
argument_list|,
name|result
argument_list|)
return|;
block|}
block|}
annotation|@
name|Inject
DECL|method|PatternAnalyzerProvider
specifier|public
name|PatternAnalyzerProvider
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
name|Version
name|esVersion
init|=
name|indexSettings
operator|.
name|getAsVersion
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|org
operator|.
name|elasticsearch
operator|.
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
specifier|final
name|CharArraySet
name|defaultStopwords
decl_stmt|;
if|if
condition|(
name|esVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_0_0_RC1
argument_list|)
condition|)
block|{
name|defaultStopwords
operator|=
name|CharArraySet
operator|.
name|EMPTY_SET
expr_stmt|;
block|}
else|else
block|{
name|defaultStopwords
operator|=
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
expr_stmt|;
block|}
name|boolean
name|lowercase
init|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"lowercase"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|CharArraySet
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
argument_list|,
name|version
argument_list|)
decl_stmt|;
name|String
name|sPattern
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"pattern"
argument_list|,
literal|"\\W+"
comment|/*PatternAnalyzer.NON_WORD_PATTERN*/
argument_list|)
decl_stmt|;
if|if
condition|(
name|sPattern
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Analyzer ["
operator|+
name|name
operator|+
literal|"] of type pattern must have a `pattern` set"
argument_list|)
throw|;
block|}
name|Pattern
name|pattern
init|=
name|Regex
operator|.
name|compile
argument_list|(
name|sPattern
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"flags"
argument_list|)
argument_list|)
decl_stmt|;
name|analyzer
operator|=
operator|new
name|PatternAnalyzer
argument_list|(
name|version
argument_list|,
name|pattern
argument_list|,
name|lowercase
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|PatternAnalyzer
name|get
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
block|}
end_class

end_unit

