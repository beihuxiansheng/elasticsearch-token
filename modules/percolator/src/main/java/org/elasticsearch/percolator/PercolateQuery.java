begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|percolator
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
name|Explanation
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
name|Query
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
name|ScorerSupplier
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
name|TopDocs
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Accountable
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
name|elasticsearch
operator|.
name|common
operator|.
name|CheckedFunction
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
name|lucene
operator|.
name|Lucene
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
name|Objects
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

begin_class
DECL|class|PercolateQuery
specifier|final
class|class
name|PercolateQuery
extends|extends
name|Query
implements|implements
name|Accountable
block|{
comment|// cost of matching the query against the document, arbitrary as it would be really complex to estimate
DECL|field|MATCH_COST
specifier|public
specifier|static
specifier|final
name|float
name|MATCH_COST
init|=
literal|1000
decl_stmt|;
DECL|field|documentType
specifier|private
specifier|final
name|String
name|documentType
decl_stmt|;
DECL|field|queryStore
specifier|private
specifier|final
name|QueryStore
name|queryStore
decl_stmt|;
DECL|field|documentSource
specifier|private
specifier|final
name|BytesReference
name|documentSource
decl_stmt|;
DECL|field|candidateMatchesQuery
specifier|private
specifier|final
name|Query
name|candidateMatchesQuery
decl_stmt|;
DECL|field|verifiedMatchesQuery
specifier|private
specifier|final
name|Query
name|verifiedMatchesQuery
decl_stmt|;
DECL|field|percolatorIndexSearcher
specifier|private
specifier|final
name|IndexSearcher
name|percolatorIndexSearcher
decl_stmt|;
DECL|method|PercolateQuery
name|PercolateQuery
parameter_list|(
name|String
name|documentType
parameter_list|,
name|QueryStore
name|queryStore
parameter_list|,
name|BytesReference
name|documentSource
parameter_list|,
name|Query
name|candidateMatchesQuery
parameter_list|,
name|IndexSearcher
name|percolatorIndexSearcher
parameter_list|,
name|Query
name|verifiedMatchesQuery
parameter_list|)
block|{
name|this
operator|.
name|documentType
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|documentType
argument_list|)
expr_stmt|;
name|this
operator|.
name|documentSource
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|documentSource
argument_list|)
expr_stmt|;
name|this
operator|.
name|candidateMatchesQuery
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|candidateMatchesQuery
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryStore
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|queryStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|percolatorIndexSearcher
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|percolatorIndexSearcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|verifiedMatchesQuery
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|verifiedMatchesQuery
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
name|Query
name|rewritten
init|=
name|candidateMatchesQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewritten
operator|!=
name|candidateMatchesQuery
condition|)
block|{
return|return
operator|new
name|PercolateQuery
argument_list|(
name|documentType
argument_list|,
name|queryStore
argument_list|,
name|documentSource
argument_list|,
name|rewritten
argument_list|,
name|percolatorIndexSearcher
argument_list|,
name|verifiedMatchesQuery
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
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Weight
name|verifiedMatchesWeight
init|=
name|verifiedMatchesQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|false
argument_list|,
name|boost
argument_list|)
decl_stmt|;
specifier|final
name|Weight
name|candidateMatchesWeight
init|=
name|candidateMatchesQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|false
argument_list|,
name|boost
argument_list|)
decl_stmt|;
return|return
operator|new
name|Weight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|set
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|leafReaderContext
parameter_list|,
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
name|scorer
init|=
name|scorer
argument_list|(
name|leafReaderContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
name|TwoPhaseIterator
name|twoPhaseIterator
init|=
name|scorer
operator|.
name|twoPhaseIterator
argument_list|()
decl_stmt|;
name|int
name|result
init|=
name|twoPhaseIterator
operator|.
name|approximation
argument_list|()
operator|.
name|advance
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
name|docId
condition|)
block|{
if|if
condition|(
name|twoPhaseIterator
operator|.
name|matches
argument_list|()
condition|)
block|{
if|if
condition|(
name|needsScores
condition|)
block|{
name|CheckedFunction
argument_list|<
name|Integer
argument_list|,
name|Query
argument_list|,
name|IOException
argument_list|>
name|percolatorQueries
init|=
name|queryStore
operator|.
name|getQueries
argument_list|(
name|leafReaderContext
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|percolatorQueries
operator|.
name|apply
argument_list|(
name|docId
argument_list|)
decl_stmt|;
name|Explanation
name|detail
init|=
name|percolatorIndexSearcher
operator|.
name|explain
argument_list|(
name|query
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|scorer
operator|.
name|score
argument_list|()
argument_list|,
literal|"PercolateQuery"
argument_list|,
name|detail
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|scorer
operator|.
name|score
argument_list|()
argument_list|,
literal|"PercolateQuery"
argument_list|)
return|;
block|}
block|}
block|}
block|}
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"PercolateQuery"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|leafReaderContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Scorer
name|approximation
init|=
name|candidateMatchesWeight
operator|.
name|scorer
argument_list|(
name|leafReaderContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|approximation
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|CheckedFunction
argument_list|<
name|Integer
argument_list|,
name|Query
argument_list|,
name|IOException
argument_list|>
name|queries
init|=
name|queryStore
operator|.
name|getQueries
argument_list|(
name|leafReaderContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|needsScores
condition|)
block|{
return|return
operator|new
name|BaseScorer
argument_list|(
name|this
argument_list|,
name|approximation
argument_list|,
name|queries
argument_list|,
name|percolatorIndexSearcher
argument_list|)
block|{
name|float
name|score
decl_stmt|;
annotation|@
name|Override
name|boolean
name|matchDocId
parameter_list|(
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
name|percolatorQueries
operator|.
name|apply
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|TopDocs
name|topDocs
init|=
name|percolatorIndexSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|topDocs
operator|.
name|totalHits
operator|>
literal|0
condition|)
block|{
name|score
operator|=
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
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
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|score
return|;
block|}
block|}
return|;
block|}
else|else
block|{
name|ScorerSupplier
name|verifiedDocsScorer
init|=
name|verifiedMatchesWeight
operator|.
name|scorerSupplier
argument_list|(
name|leafReaderContext
argument_list|)
decl_stmt|;
name|Bits
name|verifiedDocsBits
init|=
name|Lucene
operator|.
name|asSequentialAccessBits
argument_list|(
name|leafReaderContext
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|verifiedDocsScorer
argument_list|)
decl_stmt|;
return|return
operator|new
name|BaseScorer
argument_list|(
name|this
argument_list|,
name|approximation
argument_list|,
name|queries
argument_list|,
name|percolatorIndexSearcher
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0f
return|;
block|}
name|boolean
name|matchDocId
parameter_list|(
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We use the verifiedDocsBits to skip the expensive MemoryIndex verification.
comment|// If docId also appears in the verifiedDocsBits then that means during indexing
comment|// we were able to extract all query terms and for this candidate match
comment|// and we determined based on the nature of the query that it is safe to skip
comment|// the MemoryIndex verification.
if|if
condition|(
name|verifiedDocsBits
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Query
name|query
init|=
name|percolatorQueries
operator|.
name|apply
argument_list|(
name|docId
argument_list|)
decl_stmt|;
return|return
name|query
operator|!=
literal|null
operator|&&
name|Lucene
operator|.
name|exists
argument_list|(
name|percolatorIndexSearcher
argument_list|,
name|query
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
block|}
return|;
block|}
DECL|method|getPercolatorIndexSearcher
specifier|public
name|IndexSearcher
name|getPercolatorIndexSearcher
parameter_list|()
block|{
return|return
name|percolatorIndexSearcher
return|;
block|}
DECL|method|getDocumentType
specifier|public
name|String
name|getDocumentType
parameter_list|()
block|{
return|return
name|documentType
return|;
block|}
DECL|method|getDocumentSource
specifier|public
name|BytesReference
name|getDocumentSource
parameter_list|()
block|{
return|return
name|documentSource
return|;
block|}
DECL|method|getQueryStore
specifier|public
name|QueryStore
name|getQueryStore
parameter_list|()
block|{
return|return
name|queryStore
return|;
block|}
comment|// Comparing identity here to avoid being cached
comment|// Note that in theory if the same instance gets used multiple times it could still get cached,
comment|// however since we create a new query instance each time we this query this shouldn't happen and thus
comment|// this risk neglectable.
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|this
operator|==
name|o
return|;
block|}
comment|// Computing hashcode based on identity to avoid caching.
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
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
name|s
parameter_list|)
block|{
return|return
literal|"PercolateQuery{document_type={"
operator|+
name|documentType
operator|+
literal|"},document_source={"
operator|+
name|documentSource
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|"},inner={"
operator|+
name|candidateMatchesQuery
operator|.
name|toString
argument_list|(
name|s
argument_list|)
operator|+
literal|"}}"
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|documentSource
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|FunctionalInterface
DECL|interface|QueryStore
interface|interface
name|QueryStore
block|{
DECL|method|getQueries
name|CheckedFunction
argument_list|<
name|Integer
argument_list|,
name|Query
argument_list|,
name|IOException
argument_list|>
name|getQueries
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|class|BaseScorer
specifier|abstract
specifier|static
class|class
name|BaseScorer
extends|extends
name|Scorer
block|{
DECL|field|approximation
specifier|final
name|Scorer
name|approximation
decl_stmt|;
DECL|field|percolatorQueries
specifier|final
name|CheckedFunction
argument_list|<
name|Integer
argument_list|,
name|Query
argument_list|,
name|IOException
argument_list|>
name|percolatorQueries
decl_stmt|;
DECL|field|percolatorIndexSearcher
specifier|final
name|IndexSearcher
name|percolatorIndexSearcher
decl_stmt|;
DECL|method|BaseScorer
name|BaseScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Scorer
name|approximation
parameter_list|,
name|CheckedFunction
argument_list|<
name|Integer
argument_list|,
name|Query
argument_list|,
name|IOException
argument_list|>
name|percolatorQueries
parameter_list|,
name|IndexSearcher
name|percolatorIndexSearcher
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|approximation
operator|=
name|approximation
expr_stmt|;
name|this
operator|.
name|percolatorQueries
operator|=
name|percolatorQueries
expr_stmt|;
name|this
operator|.
name|percolatorIndexSearcher
operator|=
name|percolatorIndexSearcher
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
specifier|final
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
name|TwoPhaseIterator
operator|.
name|asDocIdSetIterator
argument_list|(
name|twoPhaseIterator
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|twoPhaseIterator
specifier|public
specifier|final
name|TwoPhaseIterator
name|twoPhaseIterator
parameter_list|()
block|{
return|return
operator|new
name|TwoPhaseIterator
argument_list|(
name|approximation
operator|.
name|iterator
argument_list|()
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
return|return
name|matchDocId
argument_list|(
name|approximation
operator|.
name|docID
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
name|MATCH_COST
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
specifier|final
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|approximation
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
specifier|final
name|int
name|docID
parameter_list|()
block|{
return|return
name|approximation
operator|.
name|docID
argument_list|()
return|;
block|}
DECL|method|matchDocId
specifier|abstract
name|boolean
name|matchDocId
parameter_list|(
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_class

end_unit

