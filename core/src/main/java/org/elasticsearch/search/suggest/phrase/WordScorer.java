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
name|index
operator|.
name|MultiFields
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
name|Terms
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
name|TermsEnum
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRefBuilder
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
name|lucene
operator|.
name|index
operator|.
name|FreqTermsEnum
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
name|util
operator|.
name|BigArrays
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
name|DirectCandidateGenerator
operator|.
name|Candidate
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
name|DirectCandidateGenerator
operator|.
name|CandidateSet
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

begin_comment
comment|//TODO public for tests
end_comment

begin_class
DECL|class|WordScorer
specifier|public
specifier|abstract
class|class
name|WordScorer
block|{
DECL|field|reader
specifier|protected
specifier|final
name|IndexReader
name|reader
decl_stmt|;
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|terms
specifier|protected
specifier|final
name|Terms
name|terms
decl_stmt|;
DECL|field|vocabluarySize
specifier|protected
specifier|final
name|long
name|vocabluarySize
decl_stmt|;
DECL|field|realWordLikelyhood
specifier|protected
specifier|final
name|double
name|realWordLikelyhood
decl_stmt|;
DECL|field|spare
specifier|protected
specifier|final
name|BytesRefBuilder
name|spare
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|separator
specifier|protected
specifier|final
name|BytesRef
name|separator
decl_stmt|;
DECL|field|termsEnum
specifier|private
specifier|final
name|TermsEnum
name|termsEnum
decl_stmt|;
DECL|field|numTerms
specifier|private
specifier|final
name|long
name|numTerms
decl_stmt|;
DECL|field|useTotalTermFreq
specifier|private
specifier|final
name|boolean
name|useTotalTermFreq
decl_stmt|;
DECL|method|WordScorer
specifier|public
name|WordScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|double
name|realWordLikelyHood
parameter_list|,
name|BytesRef
name|separator
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|reader
argument_list|,
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
argument_list|,
name|field
argument_list|,
name|realWordLikelyHood
argument_list|,
name|separator
argument_list|)
expr_stmt|;
block|}
DECL|method|WordScorer
specifier|public
name|WordScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Terms
name|terms
parameter_list|,
name|String
name|field
parameter_list|,
name|double
name|realWordLikelyHood
parameter_list|,
name|BytesRef
name|separator
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Field: ["
operator|+
name|field
operator|+
literal|"] does not exist"
argument_list|)
throw|;
block|}
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
specifier|final
name|long
name|vocSize
init|=
name|terms
operator|.
name|getSumTotalTermFreq
argument_list|()
decl_stmt|;
name|this
operator|.
name|vocabluarySize
operator|=
name|vocSize
operator|==
operator|-
literal|1
condition|?
name|reader
operator|.
name|maxDoc
argument_list|()
else|:
name|vocSize
expr_stmt|;
name|this
operator|.
name|useTotalTermFreq
operator|=
name|vocSize
operator|!=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|numTerms
operator|=
name|terms
operator|.
name|size
argument_list|()
expr_stmt|;
name|this
operator|.
name|termsEnum
operator|=
operator|new
name|FreqTermsEnum
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|!
name|useTotalTermFreq
argument_list|,
name|useTotalTermFreq
argument_list|,
literal|null
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|)
expr_stmt|;
comment|// non recycling for now
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|realWordLikelyhood
operator|=
name|realWordLikelyHood
expr_stmt|;
name|this
operator|.
name|separator
operator|=
name|separator
expr_stmt|;
block|}
DECL|method|frequency
specifier|public
name|long
name|frequency
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|)
condition|)
block|{
return|return
name|useTotalTermFreq
condition|?
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
else|:
name|termsEnum
operator|.
name|docFreq
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|channelScore
specifier|protected
name|double
name|channelScore
parameter_list|(
name|Candidate
name|candidate
parameter_list|,
name|Candidate
name|original
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|candidate
operator|.
name|stringDistance
operator|==
literal|1.0d
condition|)
block|{
return|return
name|realWordLikelyhood
return|;
block|}
return|return
name|candidate
operator|.
name|stringDistance
return|;
block|}
DECL|method|score
specifier|public
name|double
name|score
parameter_list|(
name|Candidate
index|[]
name|path
parameter_list|,
name|CandidateSet
index|[]
name|candidateSet
parameter_list|,
name|int
name|at
parameter_list|,
name|int
name|gramSize
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|at
operator|==
literal|0
operator|||
name|gramSize
operator|==
literal|1
condition|)
block|{
return|return
name|Math
operator|.
name|log10
argument_list|(
name|channelScore
argument_list|(
name|path
index|[
name|at
index|]
argument_list|,
name|candidateSet
index|[
name|at
index|]
operator|.
name|originalTerm
argument_list|)
operator|*
name|scoreUnigram
argument_list|(
name|path
index|[
name|at
index|]
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|at
operator|==
literal|1
operator|||
name|gramSize
operator|==
literal|2
condition|)
block|{
return|return
name|Math
operator|.
name|log10
argument_list|(
name|channelScore
argument_list|(
name|path
index|[
name|at
index|]
argument_list|,
name|candidateSet
index|[
name|at
index|]
operator|.
name|originalTerm
argument_list|)
operator|*
name|scoreBigram
argument_list|(
name|path
index|[
name|at
index|]
argument_list|,
name|path
index|[
name|at
operator|-
literal|1
index|]
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Math
operator|.
name|log10
argument_list|(
name|channelScore
argument_list|(
name|path
index|[
name|at
index|]
argument_list|,
name|candidateSet
index|[
name|at
index|]
operator|.
name|originalTerm
argument_list|)
operator|*
name|scoreTrigram
argument_list|(
name|path
index|[
name|at
index|]
argument_list|,
name|path
index|[
name|at
operator|-
literal|1
index|]
argument_list|,
name|path
index|[
name|at
operator|-
literal|2
index|]
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|scoreUnigram
specifier|protected
name|double
name|scoreUnigram
parameter_list|(
name|Candidate
name|word
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
literal|1.0
operator|+
name|frequency
argument_list|(
name|word
operator|.
name|term
argument_list|)
operator|)
operator|/
operator|(
name|vocabluarySize
operator|+
name|numTerms
operator|)
return|;
block|}
DECL|method|scoreBigram
specifier|protected
name|double
name|scoreBigram
parameter_list|(
name|Candidate
name|word
parameter_list|,
name|Candidate
name|w_1
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|scoreUnigram
argument_list|(
name|word
argument_list|)
return|;
block|}
DECL|method|scoreTrigram
specifier|protected
name|double
name|scoreTrigram
parameter_list|(
name|Candidate
name|word
parameter_list|,
name|Candidate
name|w_1
parameter_list|,
name|Candidate
name|w_2
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|scoreBigram
argument_list|(
name|word
argument_list|,
name|w_1
argument_list|)
return|;
block|}
DECL|method|join
specifier|public
specifier|static
name|BytesRef
name|join
parameter_list|(
name|BytesRef
name|separator
parameter_list|,
name|BytesRefBuilder
name|result
parameter_list|,
name|BytesRef
modifier|...
name|toJoin
parameter_list|)
block|{
name|result
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|toJoin
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|append
argument_list|(
name|toJoin
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|separator
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
name|toJoin
index|[
name|toJoin
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|get
argument_list|()
return|;
block|}
DECL|interface|WordScorerFactory
specifier|public
interface|interface
name|WordScorerFactory
block|{
DECL|method|newScorer
name|WordScorer
name|newScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Terms
name|terms
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
function_decl|;
block|}
block|}
end_class

end_unit

