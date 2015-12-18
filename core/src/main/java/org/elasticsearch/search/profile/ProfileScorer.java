begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.profile
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|profile
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
name|search
operator|.
name|DocIdSetIterator
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
name|search
operator|.
name|Scorer
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
name|search
operator|.
name|TwoPhaseIterator
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
name|search
operator|.
name|Weight
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
name|Collection
import|;
end_import

begin_comment
comment|/**  * {@link Scorer} wrapper that will compute how much time is spent on moving  * the iterator, confirming matches and computing scores.  */
end_comment

begin_class
DECL|class|ProfileScorer
specifier|final
class|class
name|ProfileScorer
extends|extends
name|Scorer
block|{
DECL|field|scorer
specifier|private
specifier|final
name|Scorer
name|scorer
decl_stmt|;
DECL|field|profileWeight
specifier|private
name|ProfileWeight
name|profileWeight
decl_stmt|;
DECL|field|profile
specifier|private
specifier|final
name|ProfileBreakdown
name|profile
decl_stmt|;
DECL|method|ProfileScorer
name|ProfileScorer
parameter_list|(
name|ProfileWeight
name|w
parameter_list|,
name|Scorer
name|scorer
parameter_list|,
name|ProfileBreakdown
name|profile
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|this
operator|.
name|profileWeight
operator|=
name|w
expr_stmt|;
name|this
operator|.
name|profile
operator|=
name|profile
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|profile
operator|.
name|startTime
argument_list|(
name|ProfileBreakdown
operator|.
name|TimingType
operator|.
name|ADVANCE
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|scorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
finally|finally
block|{
name|profile
operator|.
name|stopAndRecordTime
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|profile
operator|.
name|startTime
argument_list|(
name|ProfileBreakdown
operator|.
name|TimingType
operator|.
name|NEXT_DOC
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|scorer
operator|.
name|nextDoc
argument_list|()
return|;
block|}
finally|finally
block|{
name|profile
operator|.
name|stopAndRecordTime
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
name|profile
operator|.
name|startTime
argument_list|(
name|ProfileBreakdown
operator|.
name|TimingType
operator|.
name|SCORE
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|scorer
operator|.
name|score
argument_list|()
return|;
block|}
finally|finally
block|{
name|profile
operator|.
name|stopAndRecordTime
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|cost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getWeight
specifier|public
name|Weight
name|getWeight
parameter_list|()
block|{
return|return
name|profileWeight
return|;
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|getChildren
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|asTwoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
specifier|final
name|TwoPhaseIterator
name|in
init|=
name|scorer
operator|.
name|asTwoPhaseIterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|DocIdSetIterator
name|inApproximation
init|=
name|in
operator|.
name|approximation
argument_list|()
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|approximation
init|=
operator|new
name|DocIdSetIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|profile
operator|.
name|startTime
argument_list|(
name|ProfileBreakdown
operator|.
name|TimingType
operator|.
name|ADVANCE
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|inApproximation
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
finally|finally
block|{
name|profile
operator|.
name|stopAndRecordTime
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|profile
operator|.
name|startTime
argument_list|(
name|ProfileBreakdown
operator|.
name|TimingType
operator|.
name|NEXT_DOC
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|inApproximation
operator|.
name|nextDoc
argument_list|()
return|;
block|}
finally|finally
block|{
name|profile
operator|.
name|stopAndRecordTime
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|inApproximation
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|inApproximation
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|TwoPhaseIterator
argument_list|(
name|approximation
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
name|profile
operator|.
name|startTime
argument_list|(
name|ProfileBreakdown
operator|.
name|TimingType
operator|.
name|MATCH
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|in
operator|.
name|matches
argument_list|()
return|;
block|}
finally|finally
block|{
name|profile
operator|.
name|stopAndRecordTime
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
name|in
operator|.
name|matchCost
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

