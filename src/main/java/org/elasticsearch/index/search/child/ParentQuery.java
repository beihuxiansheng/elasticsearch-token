begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|gnu
operator|.
name|trove
operator|.
name|map
operator|.
name|hash
operator|.
name|TObjectFloatHashMap
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
name|ToStringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalStateException
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
name|bytes
operator|.
name|BytesReference
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
name|bytes
operator|.
name|HashedBytesArray
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
name|search
operator|.
name|ApplyAcceptedDocsFilter
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
name|search
operator|.
name|NoopCollector
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
name|search
operator|.
name|Queries
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
name|cache
operator|.
name|id
operator|.
name|IdReaderTypeCache
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
name|Set
import|;
end_import

begin_comment
comment|/**  * A query implementation that executes the wrapped parent query and  * connects the matching parent docs to the related child documents  * using the {@link IdReaderTypeCache}.  */
end_comment

begin_comment
comment|// TODO We use a score of 0 to indicate a doc was not scored in uidToScore, this means score of 0 can be problematic, if we move to HPCC, we can use lset/...
end_comment

begin_class
DECL|class|ParentQuery
specifier|public
class|class
name|ParentQuery
extends|extends
name|Query
implements|implements
name|SearchContext
operator|.
name|Rewrite
block|{
DECL|field|searchContext
specifier|private
specifier|final
name|SearchContext
name|searchContext
decl_stmt|;
DECL|field|originalParentQuery
specifier|private
specifier|final
name|Query
name|originalParentQuery
decl_stmt|;
DECL|field|parentType
specifier|private
specifier|final
name|String
name|parentType
decl_stmt|;
DECL|field|childrenFilter
specifier|private
specifier|final
name|Filter
name|childrenFilter
decl_stmt|;
DECL|field|rewrittenParentQuery
specifier|private
name|Query
name|rewrittenParentQuery
decl_stmt|;
DECL|field|uidToScore
specifier|private
name|TObjectFloatHashMap
argument_list|<
name|HashedBytesArray
argument_list|>
name|uidToScore
decl_stmt|;
DECL|method|ParentQuery
specifier|public
name|ParentQuery
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|,
name|Query
name|parentQuery
parameter_list|,
name|String
name|parentType
parameter_list|,
name|Filter
name|childrenFilter
parameter_list|)
block|{
name|this
operator|.
name|searchContext
operator|=
name|searchContext
expr_stmt|;
name|this
operator|.
name|originalParentQuery
operator|=
name|parentQuery
expr_stmt|;
name|this
operator|.
name|parentType
operator|=
name|parentType
expr_stmt|;
name|this
operator|.
name|childrenFilter
operator|=
operator|new
name|ApplyAcceptedDocsFilter
argument_list|(
name|childrenFilter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|contextRewrite
specifier|public
name|void
name|contextRewrite
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
throws|throws
name|Exception
block|{
name|searchContext
operator|.
name|idCache
argument_list|()
operator|.
name|refresh
argument_list|(
name|searchContext
operator|.
name|searcher
argument_list|()
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
argument_list|)
expr_stmt|;
name|uidToScore
operator|=
name|searchContext
operator|.
name|cacheRecycler
argument_list|()
operator|.
name|popObjectFloatMap
argument_list|()
expr_stmt|;
name|ParentUidCollector
name|collector
init|=
operator|new
name|ParentUidCollector
argument_list|(
name|uidToScore
argument_list|,
name|searchContext
argument_list|,
name|parentType
argument_list|)
decl_stmt|;
name|Query
name|parentQuery
decl_stmt|;
if|if
condition|(
name|rewrittenParentQuery
operator|==
literal|null
condition|)
block|{
name|parentQuery
operator|=
name|rewrittenParentQuery
operator|=
name|searchContext
operator|.
name|searcher
argument_list|()
operator|.
name|rewrite
argument_list|(
name|originalParentQuery
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parentQuery
operator|=
name|rewrittenParentQuery
expr_stmt|;
block|}
name|searchContext
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|parentQuery
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|contextClear
specifier|public
name|void
name|contextClear
parameter_list|()
block|{
if|if
condition|(
name|uidToScore
operator|!=
literal|null
condition|)
block|{
name|searchContext
operator|.
name|cacheRecycler
argument_list|()
operator|.
name|pushObjectFloatMap
argument_list|(
name|uidToScore
argument_list|)
expr_stmt|;
block|}
name|uidToScore
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ParentQuery
name|that
init|=
operator|(
name|ParentQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|originalParentQuery
operator|.
name|equals
argument_list|(
name|that
operator|.
name|originalParentQuery
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|parentType
operator|.
name|equals
argument_list|(
name|that
operator|.
name|parentType
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|originalParentQuery
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|parentType
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ParentQuery["
argument_list|)
operator|.
name|append
argument_list|(
name|parentType
argument_list|)
operator|.
name|append
argument_list|(
literal|"]("
argument_list|)
operator|.
name|append
argument_list|(
name|originalParentQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// See TopChildrenQuery#rewrite
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
if|if
condition|(
name|rewrittenParentQuery
operator|==
literal|null
condition|)
block|{
name|rewrittenParentQuery
operator|=
name|originalParentQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
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
name|rewrittenParentQuery
operator|.
name|extractTerms
argument_list|(
name|terms
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
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|uidToScore
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"has_parent query hasn't executed properly"
argument_list|)
throw|;
block|}
if|if
condition|(
name|uidToScore
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Queries
operator|.
name|NO_MATCH_QUERY
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
return|return
operator|new
name|ChildWeight
argument_list|(
name|rewrittenParentQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
argument_list|)
return|;
block|}
DECL|class|ParentUidCollector
specifier|static
class|class
name|ParentUidCollector
extends|extends
name|NoopCollector
block|{
DECL|field|uidToScore
specifier|final
name|TObjectFloatHashMap
argument_list|<
name|HashedBytesArray
argument_list|>
name|uidToScore
decl_stmt|;
DECL|field|searchContext
specifier|final
name|SearchContext
name|searchContext
decl_stmt|;
DECL|field|parentType
specifier|final
name|String
name|parentType
decl_stmt|;
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
DECL|field|typeCache
name|IdReaderTypeCache
name|typeCache
decl_stmt|;
DECL|method|ParentUidCollector
name|ParentUidCollector
parameter_list|(
name|TObjectFloatHashMap
argument_list|<
name|HashedBytesArray
argument_list|>
name|uidToScore
parameter_list|,
name|SearchContext
name|searchContext
parameter_list|,
name|String
name|parentType
parameter_list|)
block|{
name|this
operator|.
name|uidToScore
operator|=
name|uidToScore
expr_stmt|;
name|this
operator|.
name|searchContext
operator|=
name|searchContext
expr_stmt|;
name|this
operator|.
name|parentType
operator|=
name|parentType
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|typeCache
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|HashedBytesArray
name|parentUid
init|=
name|typeCache
operator|.
name|idByDoc
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|uidToScore
operator|.
name|put
argument_list|(
name|parentUid
argument_list|,
name|scorer
operator|.
name|score
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|typeCache
operator|=
name|searchContext
operator|.
name|idCache
argument_list|()
operator|.
name|reader
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|)
operator|.
name|type
argument_list|(
name|parentType
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ChildWeight
class|class
name|ChildWeight
extends|extends
name|Weight
block|{
DECL|field|parentWeight
specifier|private
specifier|final
name|Weight
name|parentWeight
decl_stmt|;
DECL|method|ChildWeight
name|ChildWeight
parameter_list|(
name|Weight
name|parentWeight
parameter_list|)
block|{
name|this
operator|.
name|parentWeight
operator|=
name|parentWeight
expr_stmt|;
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
operator|new
name|Explanation
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"not implemented yet..."
argument_list|)
return|;
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
name|ParentQuery
operator|.
name|this
return|;
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
name|float
name|sum
init|=
name|parentWeight
operator|.
name|getValueForNormalization
argument_list|()
decl_stmt|;
name|sum
operator|*=
name|getBoost
argument_list|()
operator|*
name|getBoost
argument_list|()
expr_stmt|;
return|return
name|sum
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
block|{         }
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
name|DocIdSet
name|childrenDocSet
init|=
name|childrenFilter
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
name|childrenDocSet
operator|==
literal|null
operator|||
name|childrenDocSet
operator|==
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
condition|)
block|{
return|return
literal|null
return|;
block|}
name|IdReaderTypeCache
name|idTypeCache
init|=
name|searchContext
operator|.
name|idCache
argument_list|()
operator|.
name|reader
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|)
operator|.
name|type
argument_list|(
name|parentType
argument_list|)
decl_stmt|;
if|if
condition|(
name|idTypeCache
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ChildScorer
argument_list|(
name|this
argument_list|,
name|uidToScore
argument_list|,
name|childrenDocSet
operator|.
name|iterator
argument_list|()
argument_list|,
name|idTypeCache
argument_list|)
return|;
block|}
block|}
DECL|class|ChildScorer
specifier|static
class|class
name|ChildScorer
extends|extends
name|Scorer
block|{
DECL|field|uidToScore
specifier|final
name|TObjectFloatHashMap
argument_list|<
name|HashedBytesArray
argument_list|>
name|uidToScore
decl_stmt|;
DECL|field|childrenIterator
specifier|final
name|DocIdSetIterator
name|childrenIterator
decl_stmt|;
DECL|field|typeCache
specifier|final
name|IdReaderTypeCache
name|typeCache
decl_stmt|;
DECL|field|currentChildDoc
name|int
name|currentChildDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentScore
name|float
name|currentScore
decl_stmt|;
DECL|method|ChildScorer
name|ChildScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|TObjectFloatHashMap
argument_list|<
name|HashedBytesArray
argument_list|>
name|uidToScore
parameter_list|,
name|DocIdSetIterator
name|childrenIterator
parameter_list|,
name|IdReaderTypeCache
name|typeCache
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|uidToScore
operator|=
name|uidToScore
expr_stmt|;
name|this
operator|.
name|childrenIterator
operator|=
name|childrenIterator
expr_stmt|;
name|this
operator|.
name|typeCache
operator|=
name|typeCache
expr_stmt|;
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
return|return
name|currentScore
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
comment|// We don't have the original child query hit info here...
comment|// But the freq of the children could be collector and returned here, but makes this Scorer more expensive.
return|return
literal|1
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
name|currentChildDoc
return|;
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
while|while
condition|(
literal|true
condition|)
block|{
name|currentChildDoc
operator|=
name|childrenIterator
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentChildDoc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|currentChildDoc
return|;
block|}
name|BytesReference
name|uid
init|=
name|typeCache
operator|.
name|parentIdByDoc
argument_list|(
name|currentChildDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|uid
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|currentScore
operator|=
name|uidToScore
operator|.
name|get
argument_list|(
name|uid
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentScore
operator|!=
literal|0
condition|)
block|{
return|return
name|currentChildDoc
return|;
block|}
block|}
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
name|currentChildDoc
operator|=
name|childrenIterator
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentChildDoc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|currentChildDoc
return|;
block|}
name|BytesReference
name|uid
init|=
name|typeCache
operator|.
name|idByDoc
argument_list|(
name|currentChildDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|uid
operator|==
literal|null
condition|)
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
name|currentScore
operator|=
name|uidToScore
operator|.
name|get
argument_list|(
name|uid
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentScore
operator|==
literal|0
condition|)
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
return|return
name|currentChildDoc
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
name|childrenIterator
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

