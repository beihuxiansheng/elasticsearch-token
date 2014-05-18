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
name|phrase
operator|.
name|DirectCandidateGenerator
operator|.
name|Candidate
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

begin_comment
comment|//TODO public for tests
end_comment

begin_class
DECL|class|Correction
specifier|public
specifier|final
class|class
name|Correction
implements|implements
name|Comparable
argument_list|<
name|Correction
argument_list|>
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|Correction
index|[]
name|EMPTY
init|=
operator|new
name|Correction
index|[
literal|0
index|]
decl_stmt|;
DECL|field|score
specifier|public
name|double
name|score
decl_stmt|;
DECL|field|candidates
specifier|public
specifier|final
name|Candidate
index|[]
name|candidates
decl_stmt|;
DECL|method|Correction
specifier|public
name|Correction
parameter_list|(
name|double
name|score
parameter_list|,
name|Candidate
index|[]
name|candidates
parameter_list|)
block|{
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|this
operator|.
name|candidates
operator|=
name|candidates
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Correction [score="
operator|+
name|score
operator|+
literal|", candidates="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|candidates
argument_list|)
operator|+
literal|"]"
return|;
block|}
DECL|method|join
specifier|public
name|BytesRef
name|join
parameter_list|(
name|BytesRef
name|separator
parameter_list|)
block|{
return|return
name|join
argument_list|(
name|separator
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|join
specifier|public
name|BytesRef
name|join
parameter_list|(
name|BytesRef
name|separator
parameter_list|,
name|BytesRef
name|preTag
parameter_list|,
name|BytesRef
name|postTag
parameter_list|)
block|{
return|return
name|join
argument_list|(
name|separator
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
name|preTag
argument_list|,
name|postTag
argument_list|)
return|;
block|}
DECL|method|join
specifier|public
name|BytesRef
name|join
parameter_list|(
name|BytesRef
name|separator
parameter_list|,
name|BytesRef
name|result
parameter_list|,
name|BytesRef
name|preTag
parameter_list|,
name|BytesRef
name|postTag
parameter_list|)
block|{
name|BytesRef
index|[]
name|toJoin
init|=
operator|new
name|BytesRef
index|[
name|this
operator|.
name|candidates
operator|.
name|length
index|]
decl_stmt|;
name|int
name|len
init|=
name|separator
operator|.
name|length
operator|*
name|this
operator|.
name|candidates
operator|.
name|length
operator|-
literal|1
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
name|toJoin
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Candidate
name|candidate
init|=
name|candidates
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|preTag
operator|==
literal|null
operator|||
name|candidate
operator|.
name|userInput
condition|)
block|{
name|toJoin
index|[
name|i
index|]
operator|=
name|candidate
operator|.
name|term
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|maxLen
init|=
name|preTag
operator|.
name|length
operator|+
name|postTag
operator|.
name|length
operator|+
name|candidate
operator|.
name|term
operator|.
name|length
decl_stmt|;
specifier|final
name|BytesRef
name|highlighted
init|=
operator|new
name|BytesRef
argument_list|(
name|maxLen
argument_list|)
decl_stmt|;
comment|// just allocate once
if|if
condition|(
name|i
operator|==
literal|0
operator|||
name|candidates
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|userInput
condition|)
block|{
name|highlighted
operator|.
name|append
argument_list|(
name|preTag
argument_list|)
expr_stmt|;
block|}
name|highlighted
operator|.
name|append
argument_list|(
name|candidate
operator|.
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|toJoin
operator|.
name|length
operator|==
name|i
operator|+
literal|1
operator|||
name|candidates
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|userInput
condition|)
block|{
name|highlighted
operator|.
name|append
argument_list|(
name|postTag
argument_list|)
expr_stmt|;
block|}
name|toJoin
index|[
name|i
index|]
operator|=
name|highlighted
expr_stmt|;
block|}
name|len
operator|+=
name|toJoin
index|[
name|i
index|]
operator|.
name|length
expr_stmt|;
block|}
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|grow
argument_list|(
name|len
argument_list|)
expr_stmt|;
return|return
name|SuggestUtils
operator|.
name|joinPreAllocated
argument_list|(
name|separator
argument_list|,
name|result
argument_list|,
name|toJoin
argument_list|)
return|;
block|}
comment|/** Lower scores sorts first; if scores are equal,      *  than later terms (zzz) sort first .*/
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Correction
name|other
parameter_list|)
block|{
return|return
name|compareTo
argument_list|(
name|other
operator|.
name|score
argument_list|,
name|other
operator|.
name|candidates
argument_list|)
return|;
block|}
DECL|method|compareTo
name|int
name|compareTo
parameter_list|(
name|double
name|otherScore
parameter_list|,
name|Candidate
index|[]
name|otherCandidates
parameter_list|)
block|{
if|if
condition|(
name|score
operator|==
name|otherScore
condition|)
block|{
name|int
name|limit
init|=
name|Math
operator|.
name|min
argument_list|(
name|candidates
operator|.
name|length
argument_list|,
name|otherCandidates
operator|.
name|length
argument_list|)
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
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|int
name|cmp
init|=
name|candidates
index|[
name|i
index|]
operator|.
name|term
operator|.
name|compareTo
argument_list|(
name|otherCandidates
index|[
name|i
index|]
operator|.
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
comment|// Later (zzz) terms sort before (are weaker than) earlier (aaa) terms:
return|return
operator|-
name|cmp
return|;
block|}
block|}
return|return
name|candidates
operator|.
name|length
operator|-
name|otherCandidates
operator|.
name|length
return|;
block|}
else|else
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|score
argument_list|,
name|otherScore
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

