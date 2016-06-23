begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.slice
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|slice
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
name|LeafReaderContext
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
name|LeafReader
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
name|index
operator|.
name|PostingsEnum
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
name|IndexSearcher
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
name|Weight
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
name|ConstantScoreWeight
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
name|ConstantScoreScorer
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
name|DocIdSetBuilder
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
comment|/**  * A {@link SliceQuery} that uses the terms dictionary of a field to do the slicing.  *  *<b>NOTE</b>: The cost of this filter is O(N*M) where N is the number of unique terms in the dictionary  * and M is the average number of documents per term.  * For each segment this filter enumerates the terms dictionary, computes the hash code for each term and fills  * a bit set with the documents of all terms whose hash code matches the predicate.  *<b>NOTE</b>: Documents with no value for that field are ignored.  */
end_comment

begin_class
DECL|class|TermsSliceQuery
specifier|public
specifier|final
class|class
name|TermsSliceQuery
extends|extends
name|SliceQuery
block|{
DECL|method|TermsSliceQuery
specifier|public
name|TermsSliceQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|id
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|id
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocIdSet
name|disi
init|=
name|build
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|leafIt
init|=
name|disi
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|leafIt
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**      * Returns a DocIdSet per segments containing the matching docs for the specified slice.      */
DECL|method|build
specifier|private
name|DocIdSet
name|build
parameter_list|(
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocIdSetBuilder
name|builder
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|getField
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|TermsEnum
name|te
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|PostingsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
init|=
name|te
operator|.
name|next
argument_list|()
init|;
name|term
operator|!=
literal|null
condition|;
name|term
operator|=
name|te
operator|.
name|next
argument_list|()
control|)
block|{
name|int
name|hashCode
init|=
name|term
operator|.
name|hashCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|contains
argument_list|(
name|hashCode
argument_list|)
condition|)
block|{
name|docsEnum
operator|=
name|te
operator|.
name|postings
argument_list|(
name|docsEnum
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|docsEnum
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

