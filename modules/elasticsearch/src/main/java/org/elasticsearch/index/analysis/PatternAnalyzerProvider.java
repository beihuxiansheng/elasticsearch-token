begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|miscellaneous
operator|.
name|PatternAnalyzer
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|PatternAnalyzerProvider
specifier|public
class|class
name|PatternAnalyzerProvider
extends|extends
name|AbstractIndexAnalyzerProvider
argument_list|<
name|PatternAnalyzer
argument_list|>
block|{
DECL|field|analyzer
specifier|private
specifier|final
name|PatternAnalyzer
name|analyzer
decl_stmt|;
DECL|method|PatternAnalyzerProvider
annotation|@
name|Inject
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
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
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
name|ElasticSearchIllegalArgumentException
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
DECL|method|get
annotation|@
name|Override
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

