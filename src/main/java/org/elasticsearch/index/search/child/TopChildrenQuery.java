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
name|gnu
operator|.
name|trove
operator|.
name|map
operator|.
name|hash
operator|.
name|TIntObjectHashMap
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
name|ToStringUtils
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
name|EmptyScorer
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
name|ScopePhase
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
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TopChildrenQuery
specifier|public
class|class
name|TopChildrenQuery
extends|extends
name|Query
implements|implements
name|ScopePhase
operator|.
name|TopDocsPhase
block|{
DECL|enum|ScoreType
specifier|public
specifier|static
enum|enum
name|ScoreType
block|{
DECL|enum constant|MAX
name|MAX
block|,
DECL|enum constant|AVG
name|AVG
block|,
DECL|enum constant|SUM
name|SUM
block|;
DECL|method|fromString
specifier|public
specifier|static
name|ScoreType
name|fromString
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
literal|"max"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|MAX
return|;
block|}
elseif|else
if|if
condition|(
literal|"avg"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|AVG
return|;
block|}
elseif|else
if|if
condition|(
literal|"sum"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|SUM
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No score type for child query ["
operator|+
name|type
operator|+
literal|"] found"
argument_list|)
throw|;
block|}
block|}
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|field|scope
specifier|private
name|String
name|scope
decl_stmt|;
DECL|field|parentType
specifier|private
name|String
name|parentType
decl_stmt|;
DECL|field|childType
specifier|private
name|String
name|childType
decl_stmt|;
DECL|field|scoreType
specifier|private
name|ScoreType
name|scoreType
decl_stmt|;
DECL|field|factor
specifier|private
name|int
name|factor
decl_stmt|;
DECL|field|incrementalFactor
specifier|private
name|int
name|incrementalFactor
decl_stmt|;
DECL|field|parentDocs
specifier|private
name|Map
argument_list|<
name|Object
argument_list|,
name|ParentDoc
index|[]
argument_list|>
name|parentDocs
decl_stmt|;
DECL|field|numHits
specifier|private
name|int
name|numHits
init|=
literal|0
decl_stmt|;
comment|// Note, the query is expected to already be filtered to only child type docs
DECL|method|TopChildrenQuery
specifier|public
name|TopChildrenQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|scope
parameter_list|,
name|String
name|childType
parameter_list|,
name|String
name|parentType
parameter_list|,
name|ScoreType
name|scoreType
parameter_list|,
name|int
name|factor
parameter_list|,
name|int
name|incrementalFactor
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
name|this
operator|.
name|childType
operator|=
name|childType
expr_stmt|;
name|this
operator|.
name|parentType
operator|=
name|parentType
expr_stmt|;
name|this
operator|.
name|scoreType
operator|=
name|scoreType
expr_stmt|;
name|this
operator|.
name|factor
operator|=
name|factor
expr_stmt|;
name|this
operator|.
name|incrementalFactor
operator|=
name|incrementalFactor
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|query
specifier|public
name|Query
name|query
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|scope
specifier|public
name|String
name|scope
parameter_list|()
block|{
return|return
name|scope
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|parentDocs
operator|=
literal|null
expr_stmt|;
name|numHits
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|numHits
specifier|public
name|int
name|numHits
parameter_list|()
block|{
return|return
name|numHits
return|;
block|}
annotation|@
name|Override
DECL|method|factor
specifier|public
name|int
name|factor
parameter_list|()
block|{
return|return
name|this
operator|.
name|factor
return|;
block|}
annotation|@
name|Override
DECL|method|incrementalFactor
specifier|public
name|int
name|incrementalFactor
parameter_list|()
block|{
return|return
name|this
operator|.
name|incrementalFactor
return|;
block|}
annotation|@
name|Override
DECL|method|processResults
specifier|public
name|void
name|processResults
parameter_list|(
name|TopDocs
name|topDocs
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
name|Map
argument_list|<
name|Object
argument_list|,
name|TIntObjectHashMap
argument_list|<
name|ParentDoc
argument_list|>
argument_list|>
name|parentDocsPerReader
init|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|TIntObjectHashMap
argument_list|<
name|ParentDoc
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|topDocs
operator|.
name|scoreDocs
control|)
block|{
name|int
name|readerIndex
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|readerIndex
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
decl_stmt|;
name|IndexReader
name|subReader
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|subReaders
argument_list|()
index|[
name|readerIndex
index|]
decl_stmt|;
name|int
name|subDoc
init|=
name|scoreDoc
operator|.
name|doc
operator|-
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|docStarts
argument_list|()
index|[
name|readerIndex
index|]
decl_stmt|;
comment|// find the parent id
name|HashedBytesArray
name|parentId
init|=
name|context
operator|.
name|idCache
argument_list|()
operator|.
name|reader
argument_list|(
name|subReader
argument_list|)
operator|.
name|parentIdByDoc
argument_list|(
name|parentType
argument_list|,
name|subDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentId
operator|==
literal|null
condition|)
block|{
comment|// no parent found
continue|continue;
block|}
comment|// now go over and find the parent doc Id and reader tuple
for|for
control|(
name|IndexReader
name|indexReader
range|:
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|subReaders
argument_list|()
control|)
block|{
name|int
name|parentDocId
init|=
name|context
operator|.
name|idCache
argument_list|()
operator|.
name|reader
argument_list|(
name|indexReader
argument_list|)
operator|.
name|docById
argument_list|(
name|parentType
argument_list|,
name|parentId
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentDocId
operator|!=
operator|-
literal|1
operator|&&
operator|!
name|indexReader
operator|.
name|isDeleted
argument_list|(
name|parentDocId
argument_list|)
condition|)
block|{
comment|// we found a match, add it and break
name|TIntObjectHashMap
argument_list|<
name|ParentDoc
argument_list|>
name|readerParentDocs
init|=
name|parentDocsPerReader
operator|.
name|get
argument_list|(
name|indexReader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|readerParentDocs
operator|==
literal|null
condition|)
block|{
name|readerParentDocs
operator|=
operator|new
name|TIntObjectHashMap
argument_list|<
name|ParentDoc
argument_list|>
argument_list|()
expr_stmt|;
name|parentDocsPerReader
operator|.
name|put
argument_list|(
name|indexReader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|,
name|readerParentDocs
argument_list|)
expr_stmt|;
block|}
name|ParentDoc
name|parentDoc
init|=
name|readerParentDocs
operator|.
name|get
argument_list|(
name|parentDocId
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentDoc
operator|==
literal|null
condition|)
block|{
name|numHits
operator|++
expr_stmt|;
comment|// we have a hit on a parent
name|parentDoc
operator|=
operator|new
name|ParentDoc
argument_list|()
expr_stmt|;
name|parentDoc
operator|.
name|docId
operator|=
name|parentDocId
expr_stmt|;
name|parentDoc
operator|.
name|count
operator|=
literal|1
expr_stmt|;
name|parentDoc
operator|.
name|maxScore
operator|=
name|scoreDoc
operator|.
name|score
expr_stmt|;
name|parentDoc
operator|.
name|sumScores
operator|=
name|scoreDoc
operator|.
name|score
expr_stmt|;
name|readerParentDocs
operator|.
name|put
argument_list|(
name|parentDocId
argument_list|,
name|parentDoc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parentDoc
operator|.
name|count
operator|++
expr_stmt|;
name|parentDoc
operator|.
name|sumScores
operator|+=
name|scoreDoc
operator|.
name|score
expr_stmt|;
if|if
condition|(
name|scoreDoc
operator|.
name|score
operator|>
name|parentDoc
operator|.
name|maxScore
condition|)
block|{
name|parentDoc
operator|.
name|maxScore
operator|=
name|scoreDoc
operator|.
name|score
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|this
operator|.
name|parentDocs
operator|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|ParentDoc
index|[]
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|TIntObjectHashMap
argument_list|<
name|ParentDoc
argument_list|>
argument_list|>
name|entry
range|:
name|parentDocsPerReader
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ParentDoc
index|[]
name|values
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|values
argument_list|(
operator|new
name|ParentDoc
index|[
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|,
name|PARENT_DOC_COMP
argument_list|)
expr_stmt|;
name|parentDocs
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|PARENT_DOC_COMP
specifier|private
specifier|static
specifier|final
name|ParentDocComparator
name|PARENT_DOC_COMP
init|=
operator|new
name|ParentDocComparator
argument_list|()
decl_stmt|;
DECL|class|ParentDocComparator
specifier|static
class|class
name|ParentDocComparator
implements|implements
name|Comparator
argument_list|<
name|ParentDoc
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|ParentDoc
name|o1
parameter_list|,
name|ParentDoc
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|docId
operator|-
name|o2
operator|.
name|docId
return|;
block|}
block|}
DECL|class|ParentDoc
specifier|public
specifier|static
class|class
name|ParentDoc
block|{
DECL|field|docId
specifier|public
name|int
name|docId
decl_stmt|;
DECL|field|count
specifier|public
name|int
name|count
decl_stmt|;
DECL|field|maxScore
specifier|public
name|float
name|maxScore
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
DECL|field|sumScores
specifier|public
name|float
name|sumScores
init|=
literal|0
decl_stmt|;
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
name|Query
name|newQ
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|newQ
operator|==
name|query
condition|)
return|return
name|this
return|;
name|TopChildrenQuery
name|bq
init|=
operator|(
name|TopChildrenQuery
operator|)
name|this
operator|.
name|clone
argument_list|()
decl_stmt|;
name|bq
operator|.
name|query
operator|=
name|newQ
expr_stmt|;
return|return
name|bq
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
name|query
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
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|parentDocs
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ParentWeight
argument_list|(
name|searcher
argument_list|,
name|query
operator|.
name|weight
argument_list|(
name|searcher
argument_list|)
argument_list|)
return|;
block|}
return|return
name|query
operator|.
name|weight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
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
literal|"score_child["
argument_list|)
operator|.
name|append
argument_list|(
name|childType
argument_list|)
operator|.
name|append
argument_list|(
literal|"/"
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
name|query
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
expr_stmt|;
name|sb
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
DECL|class|ParentWeight
class|class
name|ParentWeight
extends|extends
name|Weight
block|{
DECL|field|searcher
specifier|final
name|Searcher
name|searcher
decl_stmt|;
DECL|field|queryWeight
specifier|final
name|Weight
name|queryWeight
decl_stmt|;
DECL|method|ParentWeight
specifier|public
name|ParentWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|,
name|Weight
name|queryWeight
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|queryWeight
operator|=
name|queryWeight
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|TopChildrenQuery
operator|.
name|this
return|;
block|}
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|getBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
throws|throws
name|IOException
block|{
name|float
name|sum
init|=
name|queryWeight
operator|.
name|sumOfSquaredWeights
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
parameter_list|)
block|{
comment|// nothing to do here....
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|)
throws|throws
name|IOException
block|{
name|ParentDoc
index|[]
name|readerParentDocs
init|=
name|parentDocs
operator|.
name|get
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|readerParentDocs
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ParentScorer
argument_list|(
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
argument_list|,
name|readerParentDocs
argument_list|)
return|;
block|}
return|return
operator|new
name|EmptyScorer
argument_list|(
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
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
name|IndexReader
name|reader
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
block|}
DECL|class|ParentScorer
class|class
name|ParentScorer
extends|extends
name|Scorer
block|{
DECL|field|docs
specifier|private
specifier|final
name|ParentDoc
index|[]
name|docs
decl_stmt|;
DECL|field|index
specifier|private
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|ParentScorer
specifier|private
name|ParentScorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|,
name|ParentDoc
index|[]
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
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
if|if
condition|(
name|index
operator|>=
name|docs
operator|.
name|length
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
return|return
name|docs
index|[
name|index
index|]
operator|.
name|docId
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
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|nextDoc
argument_list|()
operator|)
operator|<
name|target
condition|)
block|{             }
return|return
name|doc
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
if|if
condition|(
operator|++
name|index
operator|>=
name|docs
operator|.
name|length
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
return|return
name|docs
index|[
name|index
index|]
operator|.
name|docId
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
if|if
condition|(
name|scoreType
operator|==
name|ScoreType
operator|.
name|MAX
condition|)
block|{
return|return
name|docs
index|[
name|index
index|]
operator|.
name|maxScore
return|;
block|}
elseif|else
if|if
condition|(
name|scoreType
operator|==
name|ScoreType
operator|.
name|AVG
condition|)
block|{
return|return
name|docs
index|[
name|index
index|]
operator|.
name|sumScores
operator|/
name|docs
index|[
name|index
index|]
operator|.
name|count
return|;
block|}
elseif|else
if|if
condition|(
name|scoreType
operator|==
name|ScoreType
operator|.
name|SUM
condition|)
block|{
return|return
name|docs
index|[
name|index
index|]
operator|.
name|sumScores
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"No support for score type ["
operator|+
name|scoreType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

