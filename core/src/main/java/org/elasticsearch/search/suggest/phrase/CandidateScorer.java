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
name|io
operator|.
name|IOException
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|PriorityQueue
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

begin_class
DECL|class|CandidateScorer
specifier|final
class|class
name|CandidateScorer
block|{
DECL|field|scorer
specifier|private
specifier|final
name|WordScorer
name|scorer
decl_stmt|;
DECL|field|maxNumCorrections
specifier|private
specifier|final
name|int
name|maxNumCorrections
decl_stmt|;
DECL|field|gramSize
specifier|private
specifier|final
name|int
name|gramSize
decl_stmt|;
DECL|method|CandidateScorer
specifier|public
name|CandidateScorer
parameter_list|(
name|WordScorer
name|scorer
parameter_list|,
name|int
name|maxNumCorrections
parameter_list|,
name|int
name|gramSize
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|this
operator|.
name|maxNumCorrections
operator|=
name|maxNumCorrections
expr_stmt|;
name|this
operator|.
name|gramSize
operator|=
name|gramSize
expr_stmt|;
block|}
DECL|method|findBestCandiates
specifier|public
name|Correction
index|[]
name|findBestCandiates
parameter_list|(
name|CandidateSet
index|[]
name|sets
parameter_list|,
name|float
name|errorFraction
parameter_list|,
name|double
name|cutoffScore
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sets
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|Correction
operator|.
name|EMPTY
return|;
block|}
name|PriorityQueue
argument_list|<
name|Correction
argument_list|>
name|corrections
init|=
operator|new
name|PriorityQueue
argument_list|<
name|Correction
argument_list|>
argument_list|(
name|maxNumCorrections
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|Correction
name|a
parameter_list|,
name|Correction
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
operator|<
literal|0
return|;
block|}
block|}
decl_stmt|;
name|int
name|numMissspellings
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|errorFraction
operator|>=
literal|1.0
condition|)
block|{
name|numMissspellings
operator|=
operator|(
name|int
operator|)
name|errorFraction
expr_stmt|;
block|}
else|else
block|{
name|numMissspellings
operator|=
name|Math
operator|.
name|round
argument_list|(
name|errorFraction
operator|*
name|sets
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|findCandidates
argument_list|(
name|sets
argument_list|,
operator|new
name|Candidate
index|[
name|sets
operator|.
name|length
index|]
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|numMissspellings
argument_list|)
argument_list|,
name|corrections
argument_list|,
name|cutoffScore
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|Correction
index|[]
name|result
init|=
operator|new
name|Correction
index|[
name|corrections
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|result
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|corrections
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
assert|assert
name|corrections
operator|.
name|size
argument_list|()
operator|==
literal|0
assert|;
return|return
name|result
return|;
block|}
DECL|method|findCandidates
specifier|public
name|void
name|findCandidates
parameter_list|(
name|CandidateSet
index|[]
name|candidates
parameter_list|,
name|Candidate
index|[]
name|path
parameter_list|,
name|int
name|ord
parameter_list|,
name|int
name|numMissspellingsLeft
parameter_list|,
name|PriorityQueue
argument_list|<
name|Correction
argument_list|>
name|corrections
parameter_list|,
name|double
name|cutoffScore
parameter_list|,
specifier|final
name|double
name|pathScore
parameter_list|)
throws|throws
name|IOException
block|{
name|CandidateSet
name|current
init|=
name|candidates
index|[
name|ord
index|]
decl_stmt|;
if|if
condition|(
name|ord
operator|==
name|candidates
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|path
index|[
name|ord
index|]
operator|=
name|current
operator|.
name|originalTerm
expr_stmt|;
name|updateTop
argument_list|(
name|candidates
argument_list|,
name|path
argument_list|,
name|corrections
argument_list|,
name|cutoffScore
argument_list|,
name|pathScore
operator|+
name|scorer
operator|.
name|score
argument_list|(
name|path
argument_list|,
name|candidates
argument_list|,
name|ord
argument_list|,
name|gramSize
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|numMissspellingsLeft
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|current
operator|.
name|candidates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|path
index|[
name|ord
index|]
operator|=
name|current
operator|.
name|candidates
index|[
name|i
index|]
expr_stmt|;
name|updateTop
argument_list|(
name|candidates
argument_list|,
name|path
argument_list|,
name|corrections
argument_list|,
name|cutoffScore
argument_list|,
name|pathScore
operator|+
name|scorer
operator|.
name|score
argument_list|(
name|path
argument_list|,
name|candidates
argument_list|,
name|ord
argument_list|,
name|gramSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|numMissspellingsLeft
operator|>
literal|0
condition|)
block|{
name|path
index|[
name|ord
index|]
operator|=
name|current
operator|.
name|originalTerm
expr_stmt|;
name|findCandidates
argument_list|(
name|candidates
argument_list|,
name|path
argument_list|,
name|ord
operator|+
literal|1
argument_list|,
name|numMissspellingsLeft
argument_list|,
name|corrections
argument_list|,
name|cutoffScore
argument_list|,
name|pathScore
operator|+
name|scorer
operator|.
name|score
argument_list|(
name|path
argument_list|,
name|candidates
argument_list|,
name|ord
argument_list|,
name|gramSize
argument_list|)
argument_list|)
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
name|current
operator|.
name|candidates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|path
index|[
name|ord
index|]
operator|=
name|current
operator|.
name|candidates
index|[
name|i
index|]
expr_stmt|;
name|findCandidates
argument_list|(
name|candidates
argument_list|,
name|path
argument_list|,
name|ord
operator|+
literal|1
argument_list|,
name|numMissspellingsLeft
operator|-
literal|1
argument_list|,
name|corrections
argument_list|,
name|cutoffScore
argument_list|,
name|pathScore
operator|+
name|scorer
operator|.
name|score
argument_list|(
name|path
argument_list|,
name|candidates
argument_list|,
name|ord
argument_list|,
name|gramSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|path
index|[
name|ord
index|]
operator|=
name|current
operator|.
name|originalTerm
expr_stmt|;
name|findCandidates
argument_list|(
name|candidates
argument_list|,
name|path
argument_list|,
name|ord
operator|+
literal|1
argument_list|,
literal|0
argument_list|,
name|corrections
argument_list|,
name|cutoffScore
argument_list|,
name|pathScore
operator|+
name|scorer
operator|.
name|score
argument_list|(
name|path
argument_list|,
name|candidates
argument_list|,
name|ord
argument_list|,
name|gramSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|updateTop
specifier|private
name|void
name|updateTop
parameter_list|(
name|CandidateSet
index|[]
name|candidates
parameter_list|,
name|Candidate
index|[]
name|path
parameter_list|,
name|PriorityQueue
argument_list|<
name|Correction
argument_list|>
name|corrections
parameter_list|,
name|double
name|cutoffScore
parameter_list|,
name|double
name|score
parameter_list|)
throws|throws
name|IOException
block|{
name|score
operator|=
name|Math
operator|.
name|exp
argument_list|(
name|score
argument_list|)
expr_stmt|;
assert|assert
name|Math
operator|.
name|abs
argument_list|(
name|score
operator|-
name|score
argument_list|(
name|path
argument_list|,
name|candidates
argument_list|)
argument_list|)
operator|<
literal|0.00001
assert|;
if|if
condition|(
name|score
operator|>
name|cutoffScore
condition|)
block|{
if|if
condition|(
name|corrections
operator|.
name|size
argument_list|()
operator|<
name|maxNumCorrections
condition|)
block|{
name|Candidate
index|[]
name|c
init|=
operator|new
name|Candidate
index|[
name|candidates
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
name|c
argument_list|,
literal|0
argument_list|,
name|path
operator|.
name|length
argument_list|)
expr_stmt|;
name|corrections
operator|.
name|add
argument_list|(
operator|new
name|Correction
argument_list|(
name|score
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|corrections
operator|.
name|top
argument_list|()
operator|.
name|compareTo
argument_list|(
name|score
argument_list|,
name|path
argument_list|)
operator|<
literal|0
condition|)
block|{
name|Correction
name|top
init|=
name|corrections
operator|.
name|top
argument_list|()
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
name|top
operator|.
name|candidates
argument_list|,
literal|0
argument_list|,
name|path
operator|.
name|length
argument_list|)
expr_stmt|;
name|top
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|corrections
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
block|}
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
name|candidates
parameter_list|)
throws|throws
name|IOException
block|{
name|double
name|score
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
name|candidates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|score
operator|+=
name|scorer
operator|.
name|score
argument_list|(
name|path
argument_list|,
name|candidates
argument_list|,
name|i
argument_list|,
name|gramSize
argument_list|)
expr_stmt|;
block|}
return|return
name|Math
operator|.
name|exp
argument_list|(
name|score
argument_list|)
return|;
block|}
block|}
end_class

end_unit
