begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.child
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|child
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
name|DocIdSet
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
name|Weight
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
name|docset
operator|.
name|DocIdSets
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
comment|/**  * A scorer that wraps a {@link DocIdSetIterator} and emits a constant score.  */
end_comment

begin_comment
comment|// Borrowed from ConstantScoreQuery
end_comment

begin_class
DECL|class|ConstantScorer
class|class
name|ConstantScorer
extends|extends
name|Scorer
block|{
DECL|method|create
specifier|static
name|ConstantScorer
name|create
parameter_list|(
name|DocIdSet
name|docIdSet
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|float
name|constantScore
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|DocIdSets
operator|.
name|isEmpty
argument_list|(
name|docIdSet
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ConstantScorer
argument_list|(
name|docIdSet
operator|.
name|iterator
argument_list|()
argument_list|,
name|weight
argument_list|,
name|constantScore
argument_list|)
return|;
block|}
DECL|field|docIdSetIterator
specifier|private
specifier|final
name|DocIdSetIterator
name|docIdSetIterator
decl_stmt|;
DECL|field|constantScore
specifier|private
specifier|final
name|float
name|constantScore
decl_stmt|;
DECL|method|ConstantScorer
specifier|private
name|ConstantScorer
parameter_list|(
name|DocIdSetIterator
name|docIdSetIterator
parameter_list|,
name|Weight
name|w
parameter_list|,
name|float
name|constantScore
parameter_list|)
block|{
name|super
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|this
operator|.
name|constantScore
operator|=
name|constantScore
expr_stmt|;
name|this
operator|.
name|docIdSetIterator
operator|=
name|docIdSetIterator
expr_stmt|;
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
return|return
name|docIdSetIterator
operator|.
name|nextDoc
argument_list|()
return|;
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
name|docIdSetIterator
operator|.
name|docID
argument_list|()
return|;
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
assert|assert
name|docIdSetIterator
operator|.
name|docID
argument_list|()
operator|!=
name|NO_MORE_DOCS
assert|;
return|return
name|constantScore
return|;
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
literal|1
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
return|return
name|docIdSetIterator
operator|.
name|advance
argument_list|(
name|target
argument_list|)
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
name|docIdSetIterator
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
end_class

end_unit

