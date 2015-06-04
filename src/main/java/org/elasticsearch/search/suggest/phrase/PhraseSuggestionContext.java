begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.phrase
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|phrase
package|;
end_package

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
name|HashMap
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
name|util
operator|.
name|BytesRef
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
name|query
operator|.
name|IndexQueryParserService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|CompiledScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|DirectSpellcheckerSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|Suggester
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|SuggestionSearchContext
operator|.
name|SuggestionContext
import|;
end_import

begin_class
DECL|class|PhraseSuggestionContext
class|class
name|PhraseSuggestionContext
extends|extends
name|SuggestionContext
block|{
DECL|field|SEPARATOR
specifier|private
specifier|final
name|BytesRef
name|SEPARATOR
init|=
operator|new
name|BytesRef
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
DECL|field|queryParserService
specifier|private
name|IndexQueryParserService
name|queryParserService
decl_stmt|;
DECL|field|maxErrors
specifier|private
name|float
name|maxErrors
init|=
literal|0.5f
decl_stmt|;
DECL|field|separator
specifier|private
name|BytesRef
name|separator
init|=
name|SEPARATOR
decl_stmt|;
DECL|field|realworldErrorLikelihood
specifier|private
name|float
name|realworldErrorLikelihood
init|=
literal|0.95f
decl_stmt|;
DECL|field|generators
specifier|private
name|List
argument_list|<
name|DirectCandidateGenerator
argument_list|>
name|generators
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|gramSize
specifier|private
name|int
name|gramSize
init|=
literal|1
decl_stmt|;
DECL|field|confidence
specifier|private
name|float
name|confidence
init|=
literal|1.0f
decl_stmt|;
DECL|field|tokenLimit
specifier|private
name|int
name|tokenLimit
init|=
name|NoisyChannelSpellChecker
operator|.
name|DEFAULT_TOKEN_LIMIT
decl_stmt|;
DECL|field|preTag
specifier|private
name|BytesRef
name|preTag
decl_stmt|;
DECL|field|postTag
specifier|private
name|BytesRef
name|postTag
decl_stmt|;
DECL|field|collateQueryScript
specifier|private
name|CompiledScript
name|collateQueryScript
decl_stmt|;
DECL|field|collateFilterScript
specifier|private
name|CompiledScript
name|collateFilterScript
decl_stmt|;
DECL|field|collateScriptParams
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|collateScriptParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|scorer
specifier|private
name|WordScorer
operator|.
name|WordScorerFactory
name|scorer
decl_stmt|;
DECL|field|requireUnigram
specifier|private
name|boolean
name|requireUnigram
init|=
literal|true
decl_stmt|;
DECL|field|prune
specifier|private
name|boolean
name|prune
init|=
literal|false
decl_stmt|;
DECL|method|PhraseSuggestionContext
specifier|public
name|PhraseSuggestionContext
parameter_list|(
name|Suggester
argument_list|<
name|?
extends|extends
name|PhraseSuggestionContext
argument_list|>
name|suggester
parameter_list|)
block|{
name|super
argument_list|(
name|suggester
argument_list|)
expr_stmt|;
block|}
DECL|method|maxErrors
specifier|public
name|float
name|maxErrors
parameter_list|()
block|{
return|return
name|maxErrors
return|;
block|}
DECL|method|setMaxErrors
specifier|public
name|void
name|setMaxErrors
parameter_list|(
name|Float
name|maxErrors
parameter_list|)
block|{
name|this
operator|.
name|maxErrors
operator|=
name|maxErrors
expr_stmt|;
block|}
DECL|method|separator
specifier|public
name|BytesRef
name|separator
parameter_list|()
block|{
return|return
name|separator
return|;
block|}
DECL|method|setSeparator
specifier|public
name|void
name|setSeparator
parameter_list|(
name|BytesRef
name|separator
parameter_list|)
block|{
name|this
operator|.
name|separator
operator|=
name|separator
expr_stmt|;
block|}
DECL|method|realworldErrorLikelyhood
specifier|public
name|Float
name|realworldErrorLikelyhood
parameter_list|()
block|{
return|return
name|realworldErrorLikelihood
return|;
block|}
DECL|method|setRealWordErrorLikelihood
specifier|public
name|void
name|setRealWordErrorLikelihood
parameter_list|(
name|Float
name|realworldErrorLikelihood
parameter_list|)
block|{
name|this
operator|.
name|realworldErrorLikelihood
operator|=
name|realworldErrorLikelihood
expr_stmt|;
block|}
DECL|method|addGenerator
specifier|public
name|void
name|addGenerator
parameter_list|(
name|DirectCandidateGenerator
name|generator
parameter_list|)
block|{
name|this
operator|.
name|generators
operator|.
name|add
argument_list|(
name|generator
argument_list|)
expr_stmt|;
block|}
DECL|method|generators
specifier|public
name|List
argument_list|<
name|DirectCandidateGenerator
argument_list|>
name|generators
parameter_list|()
block|{
return|return
name|this
operator|.
name|generators
return|;
block|}
DECL|method|setGramSize
specifier|public
name|void
name|setGramSize
parameter_list|(
name|int
name|gramSize
parameter_list|)
block|{
name|this
operator|.
name|gramSize
operator|=
name|gramSize
expr_stmt|;
block|}
DECL|method|gramSize
specifier|public
name|int
name|gramSize
parameter_list|()
block|{
return|return
name|gramSize
return|;
block|}
DECL|method|confidence
specifier|public
name|float
name|confidence
parameter_list|()
block|{
return|return
name|confidence
return|;
block|}
DECL|method|setConfidence
specifier|public
name|void
name|setConfidence
parameter_list|(
name|float
name|confidence
parameter_list|)
block|{
name|this
operator|.
name|confidence
operator|=
name|confidence
expr_stmt|;
block|}
DECL|method|setModel
specifier|public
name|void
name|setModel
parameter_list|(
name|WordScorer
operator|.
name|WordScorerFactory
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
DECL|method|model
specifier|public
name|WordScorer
operator|.
name|WordScorerFactory
name|model
parameter_list|()
block|{
return|return
name|scorer
return|;
block|}
DECL|method|setQueryParserService
specifier|public
name|void
name|setQueryParserService
parameter_list|(
name|IndexQueryParserService
name|queryParserService
parameter_list|)
block|{
name|this
operator|.
name|queryParserService
operator|=
name|queryParserService
expr_stmt|;
block|}
DECL|method|getQueryParserService
specifier|public
name|IndexQueryParserService
name|getQueryParserService
parameter_list|()
block|{
return|return
name|queryParserService
return|;
block|}
DECL|class|DirectCandidateGenerator
specifier|static
class|class
name|DirectCandidateGenerator
extends|extends
name|DirectSpellcheckerSettings
block|{
DECL|field|preFilter
specifier|private
name|Analyzer
name|preFilter
decl_stmt|;
DECL|field|postFilter
specifier|private
name|Analyzer
name|postFilter
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
init|=
literal|5
decl_stmt|;
DECL|method|field
specifier|public
name|String
name|field
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|size
specifier|public
name|void
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Size must be positive"
argument_list|)
throw|;
block|}
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
DECL|method|preFilter
specifier|public
name|Analyzer
name|preFilter
parameter_list|()
block|{
return|return
name|preFilter
return|;
block|}
DECL|method|preFilter
specifier|public
name|void
name|preFilter
parameter_list|(
name|Analyzer
name|preFilter
parameter_list|)
block|{
name|this
operator|.
name|preFilter
operator|=
name|preFilter
expr_stmt|;
block|}
DECL|method|postFilter
specifier|public
name|Analyzer
name|postFilter
parameter_list|()
block|{
return|return
name|postFilter
return|;
block|}
DECL|method|postFilter
specifier|public
name|void
name|postFilter
parameter_list|(
name|Analyzer
name|postFilter
parameter_list|)
block|{
name|this
operator|.
name|postFilter
operator|=
name|postFilter
expr_stmt|;
block|}
block|}
DECL|method|setRequireUnigram
specifier|public
name|void
name|setRequireUnigram
parameter_list|(
name|boolean
name|requireUnigram
parameter_list|)
block|{
name|this
operator|.
name|requireUnigram
operator|=
name|requireUnigram
expr_stmt|;
block|}
DECL|method|getRequireUnigram
specifier|public
name|boolean
name|getRequireUnigram
parameter_list|()
block|{
return|return
name|requireUnigram
return|;
block|}
DECL|method|setTokenLimit
specifier|public
name|void
name|setTokenLimit
parameter_list|(
name|int
name|tokenLimit
parameter_list|)
block|{
name|this
operator|.
name|tokenLimit
operator|=
name|tokenLimit
expr_stmt|;
block|}
DECL|method|getTokenLimit
specifier|public
name|int
name|getTokenLimit
parameter_list|()
block|{
return|return
name|tokenLimit
return|;
block|}
DECL|method|setPreTag
specifier|public
name|void
name|setPreTag
parameter_list|(
name|BytesRef
name|preTag
parameter_list|)
block|{
name|this
operator|.
name|preTag
operator|=
name|preTag
expr_stmt|;
block|}
DECL|method|getPreTag
specifier|public
name|BytesRef
name|getPreTag
parameter_list|()
block|{
return|return
name|preTag
return|;
block|}
DECL|method|setPostTag
specifier|public
name|void
name|setPostTag
parameter_list|(
name|BytesRef
name|postTag
parameter_list|)
block|{
name|this
operator|.
name|postTag
operator|=
name|postTag
expr_stmt|;
block|}
DECL|method|getPostTag
specifier|public
name|BytesRef
name|getPostTag
parameter_list|()
block|{
return|return
name|postTag
return|;
block|}
DECL|method|getCollateQueryScript
name|CompiledScript
name|getCollateQueryScript
parameter_list|()
block|{
return|return
name|collateQueryScript
return|;
block|}
DECL|method|setCollateQueryScript
name|void
name|setCollateQueryScript
parameter_list|(
name|CompiledScript
name|collateQueryScript
parameter_list|)
block|{
name|this
operator|.
name|collateQueryScript
operator|=
name|collateQueryScript
expr_stmt|;
block|}
DECL|method|getCollateScriptParams
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getCollateScriptParams
parameter_list|()
block|{
return|return
name|collateScriptParams
return|;
block|}
DECL|method|setCollateScriptParams
name|void
name|setCollateScriptParams
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|collateScriptParams
parameter_list|)
block|{
name|this
operator|.
name|collateScriptParams
operator|=
name|collateScriptParams
expr_stmt|;
block|}
DECL|method|setCollatePrune
name|void
name|setCollatePrune
parameter_list|(
name|boolean
name|prune
parameter_list|)
block|{
name|this
operator|.
name|prune
operator|=
name|prune
expr_stmt|;
block|}
DECL|method|collatePrune
name|boolean
name|collatePrune
parameter_list|()
block|{
return|return
name|prune
return|;
block|}
block|}
end_class

end_unit

