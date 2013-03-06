begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_comment
comment|/*  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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
name|index
operator|.
name|IndexReader
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
name|xcontent
operator|.
name|XContentParser
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
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
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
name|ShingleTokenFilterFactory
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
name|internal
operator|.
name|SearchContext
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
name|SuggestContextParser
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
name|SuggestUtils
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
name|phrase
operator|.
name|PhraseSuggestionContext
operator|.
name|DirectCandidateGenerator
import|;
end_import

begin_class
DECL|class|PhraseSuggestParser
specifier|public
specifier|final
class|class
name|PhraseSuggestParser
implements|implements
name|SuggestContextParser
block|{
DECL|field|suggester
specifier|private
specifier|final
name|PhraseSuggester
name|suggester
init|=
operator|new
name|PhraseSuggester
argument_list|()
decl_stmt|;
DECL|method|parse
specifier|public
name|SuggestionSearchContext
operator|.
name|SuggestionContext
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|PhraseSuggestionContext
name|suggestion
init|=
operator|new
name|PhraseSuggestionContext
argument_list|(
name|suggester
argument_list|)
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|boolean
name|gramSizeSet
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|fieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|SuggestUtils
operator|.
name|parseSuggestContext
argument_list|(
name|parser
argument_list|,
name|context
argument_list|,
name|fieldName
argument_list|,
name|suggestion
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"real_word_error_likelihood"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|suggestion
operator|.
name|setRealWordErrorLikelihood
argument_list|(
name|parser
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|suggestion
operator|.
name|realworldErrorLikelyhood
argument_list|()
operator|<=
literal|0.0
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"real_word_error_likelihood must be> 0.0"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"confidence"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|suggestion
operator|.
name|setConfidence
argument_list|(
name|parser
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|suggestion
operator|.
name|confidence
argument_list|()
operator|<
literal|0.0
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"confidence must be>= 0.0"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"separator"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|suggestion
operator|.
name|setSeparator
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"max_errors"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|suggestion
operator|.
name|setMaxErrors
argument_list|(
name|parser
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|suggestion
operator|.
name|maxErrors
argument_list|()
operator|<=
literal|0.0
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"max_error must be> 0.0"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"gram_size"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|suggestion
operator|.
name|setGramSize
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|suggestion
operator|.
name|gramSize
argument_list|()
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"gram_size must be>= 1"
argument_list|)
throw|;
block|}
name|gramSizeSet
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"force_unigrams"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|suggestion
operator|.
name|setRequireUnigram
argument_list|(
name|parser
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"suggester[phrase] doesn't support field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"direct_generator"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
comment|// for now we only have a single type of generators
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|==
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|PhraseSuggestionContext
operator|.
name|DirectCandidateGenerator
name|generator
init|=
operator|new
name|PhraseSuggestionContext
operator|.
name|DirectCandidateGenerator
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|fieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
name|parseCandidateGenerator
argument_list|(
name|parser
argument_list|,
name|context
argument_list|,
name|fieldName
argument_list|,
name|generator
argument_list|)
expr_stmt|;
block|}
block|}
name|verifyGenerator
argument_list|(
name|context
argument_list|,
name|generator
argument_list|)
expr_stmt|;
name|suggestion
operator|.
name|addGenerator
argument_list|(
name|generator
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"suggester[phrase]  doesn't support array field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|START_OBJECT
operator|&&
literal|"smoothing"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|parseSmoothingModel
argument_list|(
name|parser
argument_list|,
name|suggestion
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"suggester[phrase] doesn't support field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|suggestion
operator|.
name|getField
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"The required field option is missing"
argument_list|)
throw|;
block|}
if|if
condition|(
name|suggestion
operator|.
name|model
argument_list|()
operator|==
literal|null
condition|)
block|{
name|suggestion
operator|.
name|setModel
argument_list|(
name|LaplaceScorer
operator|.
name|FACTORY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|gramSizeSet
operator|||
name|suggestion
operator|.
name|generators
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|ShingleTokenFilterFactory
operator|.
name|Factory
name|shingleFilterFactory
init|=
name|SuggestUtils
operator|.
name|getShingleFilterFactory
argument_list|(
name|suggestion
operator|.
name|getAnalyzer
argument_list|()
operator|==
literal|null
condition|?
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|fieldSearchAnalyzer
argument_list|(
name|suggestion
operator|.
name|getField
argument_list|()
argument_list|)
else|:
name|suggestion
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
empty_stmt|;
if|if
condition|(
operator|!
name|gramSizeSet
condition|)
block|{
comment|// try to detect the shingle size
if|if
condition|(
name|shingleFilterFactory
operator|!=
literal|null
condition|)
block|{
name|suggestion
operator|.
name|setGramSize
argument_list|(
name|shingleFilterFactory
operator|.
name|getMaxShingleSize
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|suggestion
operator|.
name|getAnalyzer
argument_list|()
operator|==
literal|null
operator|&&
name|shingleFilterFactory
operator|.
name|getMinShingleSize
argument_list|()
operator|>
literal|1
operator|&&
operator|!
name|shingleFilterFactory
operator|.
name|getOutputUnigrams
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"The default analyzer for field: ["
operator|+
name|suggestion
operator|.
name|getField
argument_list|()
operator|+
literal|"] doesn't emit unigrams. If this is intentional try to set the analyzer explicitly"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|suggestion
operator|.
name|generators
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|shingleFilterFactory
operator|!=
literal|null
operator|&&
name|shingleFilterFactory
operator|.
name|getMinShingleSize
argument_list|()
operator|>
literal|1
operator|&&
operator|!
name|shingleFilterFactory
operator|.
name|getOutputUnigrams
argument_list|()
operator|&&
name|suggestion
operator|.
name|getRequireUnigram
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"The default candidate generator for phrase suggest can't operate on field: ["
operator|+
name|suggestion
operator|.
name|getField
argument_list|()
operator|+
literal|"] since it doesn't emit unigrams. If this is intentional try to set the candidate generator field explicitly"
argument_list|)
throw|;
block|}
comment|// use a default generator on the same field
name|DirectCandidateGenerator
name|generator
init|=
operator|new
name|DirectCandidateGenerator
argument_list|()
decl_stmt|;
name|generator
operator|.
name|setField
argument_list|(
name|suggestion
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|suggestion
operator|.
name|addGenerator
argument_list|(
name|generator
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|suggestion
return|;
block|}
DECL|method|parseSmoothingModel
specifier|public
name|void
name|parseSmoothingModel
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|PhraseSuggestionContext
name|suggestion
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|fieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
if|if
condition|(
literal|"linear"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|ensureNoSmoothing
argument_list|(
name|suggestion
argument_list|)
expr_stmt|;
specifier|final
name|double
index|[]
name|lambdas
init|=
operator|new
name|double
index|[
literal|3
index|]
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|fieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"trigram_lambda"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|lambdas
index|[
literal|0
index|]
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|lambdas
index|[
literal|0
index|]
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"trigram_lambda must be positive"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"bigram_lambda"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|lambdas
index|[
literal|1
index|]
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|lambdas
index|[
literal|1
index|]
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"bigram_lambda must be positive"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"unigram_lambda"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|lambdas
index|[
literal|2
index|]
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|lambdas
index|[
literal|2
index|]
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"unigram_lambda must be positive"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"suggester[phrase][smoothing][linear] doesn't support field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
name|double
name|sum
init|=
literal|0.0d
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|lambdas
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sum
operator|+=
name|lambdas
index|[
name|i
index|]
expr_stmt|;
block|}
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|sum
operator|-
literal|1.0
argument_list|)
operator|>
literal|0.001
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"linear smoothing lambdas must sum to 1"
argument_list|)
throw|;
block|}
name|suggestion
operator|.
name|setModel
argument_list|(
operator|new
name|WordScorer
operator|.
name|WordScorerFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|WordScorer
name|newScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|double
name|realWordLikelyhood
parameter_list|,
name|BytesRef
name|separator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|LinearInterpoatingScorer
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|realWordLikelyhood
argument_list|,
name|separator
argument_list|,
name|lambdas
index|[
literal|0
index|]
argument_list|,
name|lambdas
index|[
literal|1
index|]
argument_list|,
name|lambdas
index|[
literal|2
index|]
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"laplace"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|ensureNoSmoothing
argument_list|(
name|suggestion
argument_list|)
expr_stmt|;
name|double
name|theAlpha
init|=
literal|0.5
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|fieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
operator|&&
literal|"alpha"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|theAlpha
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|double
name|alpha
init|=
name|theAlpha
decl_stmt|;
name|suggestion
operator|.
name|setModel
argument_list|(
operator|new
name|WordScorer
operator|.
name|WordScorerFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|WordScorer
name|newScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|double
name|realWordLikelyhood
parameter_list|,
name|BytesRef
name|separator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|LaplaceScorer
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|realWordLikelyhood
argument_list|,
name|separator
argument_list|,
name|alpha
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"stupid_backoff"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|ensureNoSmoothing
argument_list|(
name|suggestion
argument_list|)
expr_stmt|;
name|double
name|theDiscount
init|=
literal|0.4
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|fieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
operator|&&
literal|"discount"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|theDiscount
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|double
name|discount
init|=
name|theDiscount
decl_stmt|;
name|suggestion
operator|.
name|setModel
argument_list|(
operator|new
name|WordScorer
operator|.
name|WordScorerFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|WordScorer
name|newScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|double
name|realWordLikelyhood
parameter_list|,
name|BytesRef
name|separator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StupidBackoffScorer
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|realWordLikelyhood
argument_list|,
name|separator
argument_list|,
name|discount
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"suggester[phrase] doesn't support object field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|ensureNoSmoothing
specifier|private
name|void
name|ensureNoSmoothing
parameter_list|(
name|PhraseSuggestionContext
name|suggestion
parameter_list|)
block|{
if|if
condition|(
name|suggestion
operator|.
name|model
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"only one smoothing model supported"
argument_list|)
throw|;
block|}
block|}
DECL|method|verifyGenerator
specifier|private
name|void
name|verifyGenerator
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|PhraseSuggestionContext
operator|.
name|DirectCandidateGenerator
name|suggestion
parameter_list|)
block|{
comment|// Verify options and set defaults
if|if
condition|(
name|suggestion
operator|.
name|field
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"The required field option is missing"
argument_list|)
throw|;
block|}
block|}
DECL|method|parseCandidateGenerator
specifier|private
name|void
name|parseCandidateGenerator
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|PhraseSuggestionContext
operator|.
name|DirectCandidateGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|SuggestUtils
operator|.
name|parseDirectSpellcheckerSettings
argument_list|(
name|parser
argument_list|,
name|fieldName
argument_list|,
name|generator
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"field"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|generator
operator|.
name|setField
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"size"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|generator
operator|.
name|size
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"pre_filter"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"preFilter"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|String
name|analyzerName
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|analysisService
argument_list|()
operator|.
name|analyzer
argument_list|(
name|analyzerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
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
name|analyzerName
operator|+
literal|"] doesn't exists"
argument_list|)
throw|;
block|}
name|generator
operator|.
name|preFilter
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"post_filter"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"postFilter"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|String
name|analyzerName
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|analysisService
argument_list|()
operator|.
name|analyzer
argument_list|(
name|analyzerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
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
name|analyzerName
operator|+
literal|"] doesn't exists"
argument_list|)
throw|;
block|}
name|generator
operator|.
name|postFilter
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"CandidateGenerator doesn't support ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

