begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.nested
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|nested
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
name|AtomicReaderContext
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
name|index
operator|.
name|Term
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
name|*
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
name|Bits
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
name|FixedBitSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A special query that accepts a top level parent matching query, and returns the nested docs of the matching parent  * doc as well. This is handy when deleting by query, don't use it for other purposes.  *  * @elasticsearch.internal  */
end_comment

begin_class
DECL|class|IncludeNestedDocsQuery
specifier|public
class|class
name|IncludeNestedDocsQuery
extends|extends
name|Query
block|{
DECL|field|parentFilter
specifier|private
specifier|final
name|Filter
name|parentFilter
decl_stmt|;
DECL|field|parentQuery
specifier|private
specifier|final
name|Query
name|parentQuery
decl_stmt|;
comment|// If we are rewritten, this is the original childQuery we
comment|// were passed; we use this for .equals() and
comment|// .hashCode().  This makes rewritten query equal the
comment|// original, so that user does not have to .rewrite() their
comment|// query before searching:
DECL|field|origParentQuery
specifier|private
specifier|final
name|Query
name|origParentQuery
decl_stmt|;
DECL|method|IncludeNestedDocsQuery
specifier|public
name|IncludeNestedDocsQuery
parameter_list|(
name|Query
name|parentQuery
parameter_list|,
name|Filter
name|parentFilter
parameter_list|)
block|{
name|this
operator|.
name|origParentQuery
operator|=
name|parentQuery
expr_stmt|;
name|this
operator|.
name|parentQuery
operator|=
name|parentQuery
expr_stmt|;
name|this
operator|.
name|parentFilter
operator|=
name|parentFilter
expr_stmt|;
block|}
comment|// For rewritting
DECL|method|IncludeNestedDocsQuery
name|IncludeNestedDocsQuery
parameter_list|(
name|Query
name|rewrite
parameter_list|,
name|Query
name|originalQuery
parameter_list|,
name|IncludeNestedDocsQuery
name|previousInstance
parameter_list|)
block|{
name|this
operator|.
name|origParentQuery
operator|=
name|originalQuery
expr_stmt|;
name|this
operator|.
name|parentQuery
operator|=
name|rewrite
expr_stmt|;
name|this
operator|.
name|parentFilter
operator|=
name|previousInstance
operator|.
name|parentFilter
expr_stmt|;
name|setBoost
argument_list|(
name|previousInstance
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// For cloning
DECL|method|IncludeNestedDocsQuery
name|IncludeNestedDocsQuery
parameter_list|(
name|Query
name|originalQuery
parameter_list|,
name|IncludeNestedDocsQuery
name|previousInstance
parameter_list|)
block|{
name|this
operator|.
name|origParentQuery
operator|=
name|originalQuery
expr_stmt|;
name|this
operator|.
name|parentQuery
operator|=
name|originalQuery
expr_stmt|;
name|this
operator|.
name|parentFilter
operator|=
name|previousInstance
operator|.
name|parentFilter
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
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IncludeNestedDocsWeight
argument_list|(
name|parentQuery
argument_list|,
name|parentQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
argument_list|,
name|parentFilter
argument_list|)
return|;
block|}
DECL|class|IncludeNestedDocsWeight
specifier|static
class|class
name|IncludeNestedDocsWeight
extends|extends
name|Weight
block|{
DECL|field|parentQuery
specifier|private
specifier|final
name|Query
name|parentQuery
decl_stmt|;
DECL|field|parentWeight
specifier|private
specifier|final
name|Weight
name|parentWeight
decl_stmt|;
DECL|field|parentsFilter
specifier|private
specifier|final
name|Filter
name|parentsFilter
decl_stmt|;
DECL|method|IncludeNestedDocsWeight
name|IncludeNestedDocsWeight
parameter_list|(
name|Query
name|parentQuery
parameter_list|,
name|Weight
name|parentWeight
parameter_list|,
name|Filter
name|parentsFilter
parameter_list|)
block|{
name|this
operator|.
name|parentQuery
operator|=
name|parentQuery
expr_stmt|;
name|this
operator|.
name|parentWeight
operator|=
name|parentWeight
expr_stmt|;
name|this
operator|.
name|parentsFilter
operator|=
name|parentsFilter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|parentQuery
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|parentWeight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parentWeight
operator|.
name|getValueForNormalization
argument_list|()
return|;
comment|// this query is never boosted so just delegate...
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Scorer
name|parentScorer
init|=
name|parentWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
comment|// no matches
if|if
condition|(
name|parentScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|DocIdSet
name|parents
init|=
name|parentsFilter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|parents
operator|==
literal|null
condition|)
block|{
comment|// No matches
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|parents
operator|instanceof
name|FixedBitSet
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"parentFilter must return FixedBitSet; got "
operator|+
name|parents
argument_list|)
throw|;
block|}
name|int
name|firstParentDoc
init|=
name|parentScorer
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstParentDoc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
comment|// No matches
return|return
literal|null
return|;
block|}
return|return
operator|new
name|IncludeNestedDocsScorer
argument_list|(
name|this
argument_list|,
name|parentScorer
argument_list|,
operator|(
name|FixedBitSet
operator|)
name|parents
argument_list|,
name|firstParentDoc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
comment|//Query is used internally and not by users, so explain can be empty
block|}
annotation|@
name|Override
DECL|method|scoresDocsOutOfOrder
specifier|public
name|boolean
name|scoresDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|IncludeNestedDocsScorer
specifier|static
class|class
name|IncludeNestedDocsScorer
extends|extends
name|Scorer
block|{
DECL|field|parentScorer
specifier|final
name|Scorer
name|parentScorer
decl_stmt|;
DECL|field|parentBits
specifier|final
name|FixedBitSet
name|parentBits
decl_stmt|;
DECL|field|currentChildPointer
name|int
name|currentChildPointer
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentParentPointer
name|int
name|currentParentPointer
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentDoc
name|int
name|currentDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|IncludeNestedDocsScorer
name|IncludeNestedDocsScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Scorer
name|parentScorer
parameter_list|,
name|FixedBitSet
name|parentBits
parameter_list|,
name|int
name|currentParentPointer
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentScorer
operator|=
name|parentScorer
expr_stmt|;
name|this
operator|.
name|parentBits
operator|=
name|parentBits
expr_stmt|;
name|this
operator|.
name|currentParentPointer
operator|=
name|currentParentPointer
expr_stmt|;
if|if
condition|(
name|currentParentPointer
operator|==
literal|0
condition|)
block|{
name|currentChildPointer
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|currentChildPointer
operator|=
name|parentBits
operator|.
name|prevSetBit
argument_list|(
name|currentParentPointer
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentChildPointer
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no previous set parent, we delete from doc 0
name|currentChildPointer
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|currentChildPointer
operator|++
expr_stmt|;
comment|// we only care about children
block|}
block|}
name|currentDoc
operator|=
name|currentChildPointer
expr_stmt|;
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
name|parentScorer
operator|.
name|getChildren
argument_list|()
return|;
block|}
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|currentParentPointer
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
operator|(
name|currentDoc
operator|=
name|NO_MORE_DOCS
operator|)
return|;
block|}
if|if
condition|(
name|currentChildPointer
operator|==
name|currentParentPointer
condition|)
block|{
comment|// we need to return the current parent as well, but prepare to return
comment|// the next set of children
name|currentDoc
operator|=
name|currentParentPointer
expr_stmt|;
name|currentParentPointer
operator|=
name|parentScorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentParentPointer
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|currentChildPointer
operator|=
name|parentBits
operator|.
name|prevSetBit
argument_list|(
name|currentParentPointer
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentChildPointer
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no previous set parent, just set the child to the current parent
name|currentChildPointer
operator|=
name|currentParentPointer
expr_stmt|;
block|}
else|else
block|{
name|currentChildPointer
operator|++
expr_stmt|;
comment|// we only care about children
block|}
block|}
block|}
else|else
block|{
name|currentDoc
operator|=
name|currentChildPointer
operator|++
expr_stmt|;
block|}
assert|assert
name|currentDoc
operator|!=
operator|-
literal|1
assert|;
return|return
name|currentDoc
return|;
block|}
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
if|if
condition|(
name|target
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
operator|(
name|currentDoc
operator|=
name|NO_MORE_DOCS
operator|)
return|;
block|}
if|if
condition|(
name|target
operator|==
literal|0
condition|)
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
if|if
condition|(
name|target
operator|<
name|currentParentPointer
condition|)
block|{
name|currentDoc
operator|=
name|currentParentPointer
operator|=
name|parentScorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentParentPointer
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
operator|(
name|currentDoc
operator|=
name|NO_MORE_DOCS
operator|)
return|;
block|}
if|if
condition|(
name|currentParentPointer
operator|==
literal|0
condition|)
block|{
name|currentChildPointer
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|currentChildPointer
operator|=
name|parentBits
operator|.
name|prevSetBit
argument_list|(
name|currentParentPointer
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentChildPointer
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no previous set parent, just set the child to 0 to delete all up to the parent
name|currentChildPointer
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|currentChildPointer
operator|++
expr_stmt|;
comment|// we only care about children
block|}
block|}
block|}
else|else
block|{
name|currentDoc
operator|=
name|currentChildPointer
operator|++
expr_stmt|;
block|}
return|return
name|currentDoc
return|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parentScorer
operator|.
name|score
argument_list|()
return|;
block|}
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parentScorer
operator|.
name|freq
argument_list|()
return|;
block|}
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|currentDoc
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
name|parentScorer
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|parentQuery
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Query
name|parentRewrite
init|=
name|parentQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentRewrite
operator|!=
name|parentQuery
condition|)
block|{
return|return
operator|new
name|IncludeNestedDocsQuery
argument_list|(
name|parentRewrite
argument_list|,
name|parentQuery
argument_list|,
name|this
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"IncludeNestedDocsQuery ("
operator|+
name|parentQuery
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|_other
parameter_list|)
block|{
if|if
condition|(
name|_other
operator|instanceof
name|IncludeNestedDocsQuery
condition|)
block|{
specifier|final
name|IncludeNestedDocsQuery
name|other
init|=
operator|(
name|IncludeNestedDocsQuery
operator|)
name|_other
decl_stmt|;
return|return
name|origParentQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|origParentQuery
argument_list|)
operator|&&
name|parentFilter
operator|.
name|equals
argument_list|(
name|other
operator|.
name|parentFilter
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|hash
init|=
literal|1
decl_stmt|;
name|hash
operator|=
name|prime
operator|*
name|hash
operator|+
name|origParentQuery
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
name|prime
operator|*
name|hash
operator|+
name|parentFilter
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|hash
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Query
name|clone
parameter_list|()
block|{
name|Query
name|clonedQuery
init|=
name|origParentQuery
operator|.
name|clone
argument_list|()
decl_stmt|;
return|return
operator|new
name|IncludeNestedDocsQuery
argument_list|(
name|clonedQuery
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

