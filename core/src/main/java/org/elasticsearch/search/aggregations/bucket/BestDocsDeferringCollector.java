begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
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
name|LeafCollector
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
name|ScoreDoc
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
name|TopDocsCollector
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
name|TopScoreDocCollector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|lease
operator|.
name|Releasable
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
name|lease
operator|.
name|Releasables
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
name|util
operator|.
name|BigArrays
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
name|util
operator|.
name|ObjectArray
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
name|aggregations
operator|.
name|BucketCollector
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
name|aggregations
operator|.
name|LeafBucketCollector
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
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A specialization of {@link DeferringBucketCollector} that collects all  * matches and then replays only the top scoring documents to child  * aggregations. The method  * {@link BestDocsDeferringCollector#createTopDocsCollector(int)} is designed to  * be overridden and allows subclasses to choose a custom collector  * implementation for determining the top N matches.  *   */
end_comment

begin_class
DECL|class|BestDocsDeferringCollector
specifier|public
class|class
name|BestDocsDeferringCollector
extends|extends
name|DeferringBucketCollector
implements|implements
name|Releasable
block|{
DECL|field|entries
specifier|final
name|List
argument_list|<
name|PerSegmentCollects
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|deferred
name|BucketCollector
name|deferred
decl_stmt|;
DECL|field|perBucketSamples
name|ObjectArray
argument_list|<
name|PerParentBucketSamples
argument_list|>
name|perBucketSamples
decl_stmt|;
DECL|field|shardSize
specifier|private
name|int
name|shardSize
decl_stmt|;
DECL|field|perSegCollector
specifier|private
name|PerSegmentCollects
name|perSegCollector
decl_stmt|;
DECL|field|bigArrays
specifier|private
specifier|final
name|BigArrays
name|bigArrays
decl_stmt|;
comment|/**      * Sole constructor.      *       * @param shardSize      *            The number of top-scoring docs to collect for each bucket      */
DECL|method|BestDocsDeferringCollector
specifier|public
name|BestDocsDeferringCollector
parameter_list|(
name|int
name|shardSize
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|)
block|{
name|this
operator|.
name|shardSize
operator|=
name|shardSize
expr_stmt|;
name|this
operator|.
name|bigArrays
operator|=
name|bigArrays
expr_stmt|;
name|perBucketSamples
operator|=
name|bigArrays
operator|.
name|newObjectArray
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** Set the deferred collectors. */
annotation|@
name|Override
DECL|method|setDeferredCollector
specifier|public
name|void
name|setDeferredCollector
parameter_list|(
name|Iterable
argument_list|<
name|BucketCollector
argument_list|>
name|deferredCollectors
parameter_list|)
block|{
name|this
operator|.
name|deferred
operator|=
name|BucketCollector
operator|.
name|wrap
argument_list|(
name|deferredCollectors
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafBucketCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|perSegCollector
operator|=
operator|new
name|PerSegmentCollects
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|perSegCollector
argument_list|)
expr_stmt|;
comment|// Deferring collector
return|return
operator|new
name|LeafBucketCollector
argument_list|()
block|{
annotation|@
name|Override
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
name|perSegCollector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|long
name|bucket
parameter_list|)
throws|throws
name|IOException
block|{
name|perSegCollector
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|// Designed to be overridden by subclasses that may score docs by criteria
comment|// other than Lucene score
DECL|method|createTopDocsCollector
specifier|protected
name|TopDocsCollector
argument_list|<
name|?
extends|extends
name|ScoreDoc
argument_list|>
name|createTopDocsCollector
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|preCollection
specifier|public
name|void
name|preCollection
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|postCollection
specifier|public
name|void
name|postCollection
parameter_list|()
throws|throws
name|IOException
block|{
name|runDeferredAggs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareSelectedBuckets
specifier|public
name|void
name|prepareSelectedBuckets
parameter_list|(
name|long
modifier|...
name|selectedBuckets
parameter_list|)
throws|throws
name|IOException
block|{
comment|// no-op - deferred aggs processed in postCollection call
block|}
DECL|method|runDeferredAggs
specifier|private
name|void
name|runDeferredAggs
parameter_list|()
throws|throws
name|IOException
block|{
name|deferred
operator|.
name|preCollection
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ScoreDoc
argument_list|>
name|allDocs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|shardSize
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
name|perBucketSamples
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PerParentBucketSamples
name|perBucketSample
init|=
name|perBucketSamples
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|perBucketSample
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|perBucketSample
operator|.
name|getMatches
argument_list|(
name|allDocs
argument_list|)
expr_stmt|;
block|}
comment|// Sort the top matches by docID for the benefit of deferred collector
name|ScoreDoc
index|[]
name|docsArr
init|=
name|allDocs
operator|.
name|toArray
argument_list|(
operator|new
name|ScoreDoc
index|[
name|allDocs
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
name|docsArr
argument_list|,
operator|new
name|Comparator
argument_list|<
name|ScoreDoc
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|ScoreDoc
name|o1
parameter_list|,
name|ScoreDoc
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|.
name|doc
operator|==
name|o2
operator|.
name|doc
condition|)
block|{
return|return
name|o1
operator|.
name|shardIndex
operator|-
name|o2
operator|.
name|shardIndex
return|;
block|}
return|return
name|o1
operator|.
name|doc
operator|-
name|o2
operator|.
name|doc
return|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|PerSegmentCollects
name|perSegDocs
range|:
name|entries
control|)
block|{
name|perSegDocs
operator|.
name|replayRelatedMatches
argument_list|(
name|docsArr
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"IOException collecting best scoring results"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|deferred
operator|.
name|postCollection
argument_list|()
expr_stmt|;
block|}
DECL|class|PerParentBucketSamples
class|class
name|PerParentBucketSamples
block|{
DECL|field|currentLeafCollector
specifier|private
name|LeafCollector
name|currentLeafCollector
decl_stmt|;
DECL|field|tdc
specifier|private
name|TopDocsCollector
argument_list|<
name|?
extends|extends
name|ScoreDoc
argument_list|>
name|tdc
decl_stmt|;
DECL|field|parentBucket
specifier|private
name|long
name|parentBucket
decl_stmt|;
DECL|field|matchedDocs
specifier|private
name|int
name|matchedDocs
decl_stmt|;
DECL|method|PerParentBucketSamples
specifier|public
name|PerParentBucketSamples
parameter_list|(
name|long
name|parentBucket
parameter_list|,
name|Scorer
name|scorer
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|parentBucket
operator|=
name|parentBucket
expr_stmt|;
name|tdc
operator|=
name|createTopDocsCollector
argument_list|(
name|shardSize
argument_list|)
expr_stmt|;
name|currentLeafCollector
operator|=
name|tdc
operator|.
name|getLeafCollector
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"IO error creating collector"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getMatches
specifier|public
name|void
name|getMatches
parameter_list|(
name|List
argument_list|<
name|ScoreDoc
argument_list|>
name|allDocs
parameter_list|)
block|{
name|TopDocs
name|topDocs
init|=
name|tdc
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|matchedDocs
operator|=
name|sd
operator|.
name|length
expr_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|sd
control|)
block|{
comment|// A bit of a hack to (ab)use shardIndex property here to
comment|// hold a bucket ID but avoids allocating extra data structures
comment|// and users should have bigger concerns if bucket IDs
comment|// exceed int capacity..
name|scoreDoc
operator|.
name|shardIndex
operator|=
operator|(
name|int
operator|)
name|parentBucket
expr_stmt|;
block|}
name|allDocs
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|sd
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|currentLeafCollector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
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
name|currentLeafCollector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
DECL|method|changeSegment
specifier|public
name|void
name|changeSegment
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|currentLeafCollector
operator|=
name|tdc
operator|.
name|getLeafCollector
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
block|}
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|()
block|{
return|return
name|matchedDocs
return|;
block|}
block|}
DECL|class|PerSegmentCollects
class|class
name|PerSegmentCollects
extends|extends
name|Scorer
block|{
DECL|field|readerContext
specifier|private
name|LeafReaderContext
name|readerContext
decl_stmt|;
DECL|field|maxDocId
name|int
name|maxDocId
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|currentScore
specifier|private
name|float
name|currentScore
decl_stmt|;
DECL|field|currentDocId
specifier|private
name|int
name|currentDocId
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentScorer
specifier|private
name|Scorer
name|currentScorer
decl_stmt|;
DECL|method|PerSegmentCollects
name|PerSegmentCollects
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
comment|// The publisher behaviour for Reader/Scorer listeners triggers a
comment|// call to this constructor with a null scorer so we can't call
comment|// scorer.getWeight() and pass the Weight to our base class.
comment|// However, passing null seems to have no adverse effects here...
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|readerContext
operator|=
name|readerContext
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
name|perBucketSamples
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PerParentBucketSamples
name|perBucketSample
init|=
name|perBucketSamples
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|perBucketSample
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|perBucketSample
operator|.
name|changeSegment
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
block|}
block|}
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
name|currentScorer
operator|=
name|scorer
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
name|perBucketSamples
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PerParentBucketSamples
name|perBucketSample
init|=
name|perBucketSamples
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|perBucketSample
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|perBucketSample
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|replayRelatedMatches
specifier|public
name|void
name|replayRelatedMatches
parameter_list|(
name|ScoreDoc
index|[]
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafBucketCollector
name|leafCollector
init|=
name|deferred
operator|.
name|getLeafCollector
argument_list|(
name|readerContext
argument_list|)
decl_stmt|;
name|leafCollector
operator|.
name|setScorer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|currentScore
operator|=
literal|0
expr_stmt|;
name|currentDocId
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|maxDocId
operator|<
literal|0
condition|)
block|{
return|return;
block|}
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|sd
control|)
block|{
comment|// Doc ids from TopDocCollector are root-level Reader so
comment|// need rebasing
name|int
name|rebased
init|=
name|scoreDoc
operator|.
name|doc
operator|-
name|readerContext
operator|.
name|docBase
decl_stmt|;
if|if
condition|(
operator|(
name|rebased
operator|>=
literal|0
operator|)
operator|&&
operator|(
name|rebased
operator|<=
name|maxDocId
operator|)
condition|)
block|{
name|currentScore
operator|=
name|scoreDoc
operator|.
name|score
expr_stmt|;
name|currentDocId
operator|=
name|rebased
expr_stmt|;
comment|// We stored the bucket ID in Lucene's shardIndex property
comment|// for convenience.
name|leafCollector
operator|.
name|collect
argument_list|(
name|rebased
argument_list|,
name|scoreDoc
operator|.
name|shardIndex
argument_list|)
expr_stmt|;
block|}
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
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"This caching scorer implementation only implements score() and docID()"
argument_list|)
throw|;
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
name|currentDocId
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"This caching scorer implementation only implements score() and docID()"
argument_list|)
throw|;
block|}
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|docId
parameter_list|,
name|long
name|parentBucket
parameter_list|)
throws|throws
name|IOException
block|{
name|perBucketSamples
operator|=
name|bigArrays
operator|.
name|grow
argument_list|(
name|perBucketSamples
argument_list|,
name|parentBucket
operator|+
literal|1
argument_list|)
expr_stmt|;
name|PerParentBucketSamples
name|sampler
init|=
name|perBucketSamples
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|parentBucket
argument_list|)
decl_stmt|;
if|if
condition|(
name|sampler
operator|==
literal|null
condition|)
block|{
name|sampler
operator|=
operator|new
name|PerParentBucketSamples
argument_list|(
name|parentBucket
argument_list|,
name|currentScorer
argument_list|,
name|readerContext
argument_list|)
expr_stmt|;
name|perBucketSamples
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|parentBucket
argument_list|,
name|sampler
argument_list|)
expr_stmt|;
block|}
name|sampler
operator|.
name|collect
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|maxDocId
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxDocId
argument_list|,
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|(
name|long
name|parentBucket
parameter_list|)
block|{
name|PerParentBucketSamples
name|sampler
init|=
name|perBucketSamples
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|parentBucket
argument_list|)
decl_stmt|;
if|if
condition|(
name|sampler
operator|==
literal|null
condition|)
block|{
comment|// There are conditions where no docs are collected and the aggs
comment|// framework still asks for doc count.
return|return
literal|0
return|;
block|}
return|return
name|sampler
operator|.
name|getDocCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
name|Releasables
operator|.
name|close
argument_list|(
name|perBucketSamples
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

