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
name|Comparator
import|;
end_import

begin_comment
comment|//TODO public for tests
end_comment

begin_class
DECL|class|MultiCandidateGeneratorWrapper
specifier|public
specifier|final
class|class
name|MultiCandidateGeneratorWrapper
extends|extends
name|CandidateGenerator
block|{
DECL|field|candidateGenerator
specifier|private
specifier|final
name|CandidateGenerator
index|[]
name|candidateGenerator
decl_stmt|;
DECL|field|numCandidates
specifier|private
name|int
name|numCandidates
decl_stmt|;
DECL|method|MultiCandidateGeneratorWrapper
specifier|public
name|MultiCandidateGeneratorWrapper
parameter_list|(
name|int
name|numCandidates
parameter_list|,
name|CandidateGenerator
modifier|...
name|candidateGenerators
parameter_list|)
block|{
name|this
operator|.
name|candidateGenerator
operator|=
name|candidateGenerators
expr_stmt|;
name|this
operator|.
name|numCandidates
operator|=
name|numCandidates
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isKnownWord
specifier|public
name|boolean
name|isKnownWord
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|candidateGenerator
index|[
literal|0
index|]
operator|.
name|isKnownWord
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
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
return|return
name|candidateGenerator
index|[
literal|0
index|]
operator|.
name|frequency
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|drawCandidates
specifier|public
name|CandidateSet
name|drawCandidates
parameter_list|(
name|CandidateSet
name|set
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|CandidateGenerator
name|generator
range|:
name|candidateGenerator
control|)
block|{
name|generator
operator|.
name|drawCandidates
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
return|return
name|reduce
argument_list|(
name|set
argument_list|,
name|numCandidates
argument_list|)
return|;
block|}
DECL|method|reduce
specifier|private
name|CandidateSet
name|reduce
parameter_list|(
name|CandidateSet
name|set
parameter_list|,
name|int
name|numCandidates
parameter_list|)
block|{
if|if
condition|(
name|set
operator|.
name|candidates
operator|.
name|length
operator|>
name|numCandidates
condition|)
block|{
name|Candidate
index|[]
name|candidates
init|=
name|set
operator|.
name|candidates
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|candidates
argument_list|,
parameter_list|(
name|left
parameter_list|,
name|right
parameter_list|)
lambda|->
name|Double
operator|.
name|compare
argument_list|(
name|right
operator|.
name|score
argument_list|,
name|left
operator|.
name|score
argument_list|)
argument_list|)
expr_stmt|;
name|Candidate
index|[]
name|newSet
init|=
operator|new
name|Candidate
index|[
name|numCandidates
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|candidates
argument_list|,
literal|0
argument_list|,
name|newSet
argument_list|,
literal|0
argument_list|,
name|numCandidates
argument_list|)
expr_stmt|;
name|set
operator|.
name|candidates
operator|=
name|newSet
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
annotation|@
name|Override
DECL|method|createCandidate
specifier|public
name|Candidate
name|createCandidate
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|long
name|frequency
parameter_list|,
name|double
name|channelScore
parameter_list|,
name|boolean
name|userInput
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|candidateGenerator
index|[
literal|0
index|]
operator|.
name|createCandidate
argument_list|(
name|term
argument_list|,
name|frequency
argument_list|,
name|channelScore
argument_list|,
name|userInput
argument_list|)
return|;
block|}
block|}
end_class

end_unit

