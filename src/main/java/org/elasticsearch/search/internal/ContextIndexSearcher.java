begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
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
name|search
operator|.
name|*
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
name|lucene
operator|.
name|Lucene
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
name|MinimumScoreCollector
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
name|MultiCollector
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
name|FilteredCollector
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
name|XCollector
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
name|XFilteredQuery
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
name|engine
operator|.
name|Engine
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
name|dfs
operator|.
name|CachedDfSource
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
operator|.
name|Lifetime
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
name|List
import|;
end_import

begin_comment
comment|/**  * Context-aware extension of {@link IndexSearcher}.  */
end_comment

begin_class
DECL|class|ContextIndexSearcher
specifier|public
class|class
name|ContextIndexSearcher
extends|extends
name|IndexSearcher
implements|implements
name|Releasable
block|{
DECL|enum|Stage
specifier|public
specifier|static
enum|enum
name|Stage
block|{
DECL|enum constant|NA
name|NA
block|,
DECL|enum constant|MAIN_QUERY
name|MAIN_QUERY
block|}
comment|/** The wrapped {@link IndexSearcher}. The reason why we sometimes prefer delegating to this searcher instead of<tt>super</tt> is that      *  this instance may have more assertions, for example if it comes from MockInternalEngine which wraps the IndexSearcher into an      *  AssertingIndexSearcher. */
DECL|field|in
specifier|private
specifier|final
name|IndexSearcher
name|in
decl_stmt|;
DECL|field|searchContext
specifier|private
specifier|final
name|SearchContext
name|searchContext
decl_stmt|;
DECL|field|dfSource
specifier|private
name|CachedDfSource
name|dfSource
decl_stmt|;
DECL|field|queryCollectors
specifier|private
name|List
argument_list|<
name|Collector
argument_list|>
name|queryCollectors
decl_stmt|;
DECL|field|currentState
specifier|private
name|Stage
name|currentState
init|=
name|Stage
operator|.
name|NA
decl_stmt|;
DECL|field|enableMainDocIdSetCollector
specifier|private
name|boolean
name|enableMainDocIdSetCollector
decl_stmt|;
DECL|field|mainDocIdSetCollector
specifier|private
name|DocIdSetCollector
name|mainDocIdSetCollector
decl_stmt|;
DECL|method|ContextIndexSearcher
specifier|public
name|ContextIndexSearcher
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|,
name|Engine
operator|.
name|Searcher
name|searcher
parameter_list|)
block|{
name|super
argument_list|(
name|searcher
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|=
name|searcher
operator|.
name|searcher
argument_list|()
expr_stmt|;
name|this
operator|.
name|searchContext
operator|=
name|searchContext
expr_stmt|;
name|setSimilarity
argument_list|(
name|searcher
operator|.
name|searcher
argument_list|()
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|Releasables
operator|.
name|close
argument_list|(
name|mainDocIdSetCollector
argument_list|)
expr_stmt|;
block|}
DECL|method|dfSource
specifier|public
name|void
name|dfSource
parameter_list|(
name|CachedDfSource
name|dfSource
parameter_list|)
block|{
name|this
operator|.
name|dfSource
operator|=
name|dfSource
expr_stmt|;
block|}
comment|/**      * Adds a query level collector that runs at {@link Stage#MAIN_QUERY}. Note, supports      * {@link org.elasticsearch.common.lucene.search.XCollector} allowing for a callback      * when collection is done.      */
DECL|method|addMainQueryCollector
specifier|public
name|void
name|addMainQueryCollector
parameter_list|(
name|Collector
name|collector
parameter_list|)
block|{
if|if
condition|(
name|queryCollectors
operator|==
literal|null
condition|)
block|{
name|queryCollectors
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|queryCollectors
operator|.
name|add
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
DECL|method|mainDocIdSetCollector
specifier|public
name|DocIdSetCollector
name|mainDocIdSetCollector
parameter_list|()
block|{
return|return
name|this
operator|.
name|mainDocIdSetCollector
return|;
block|}
DECL|method|enableMainDocIdSetCollector
specifier|public
name|void
name|enableMainDocIdSetCollector
parameter_list|()
block|{
name|this
operator|.
name|enableMainDocIdSetCollector
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|inStage
specifier|public
name|void
name|inStage
parameter_list|(
name|Stage
name|stage
parameter_list|)
block|{
name|this
operator|.
name|currentState
operator|=
name|stage
expr_stmt|;
block|}
DECL|method|finishStage
specifier|public
name|void
name|finishStage
parameter_list|(
name|Stage
name|stage
parameter_list|)
block|{
assert|assert
name|currentState
operator|==
name|stage
operator|:
literal|"Expected stage "
operator|+
name|stage
operator|+
literal|" but was stage "
operator|+
name|currentState
assert|;
name|this
operator|.
name|currentState
operator|=
name|Stage
operator|.
name|NA
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
name|original
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|original
operator|==
name|searchContext
operator|.
name|query
argument_list|()
operator|||
name|original
operator|==
name|searchContext
operator|.
name|parsedQuery
argument_list|()
operator|.
name|query
argument_list|()
condition|)
block|{
comment|// optimize in case its the top level search query and we already rewrote it...
if|if
condition|(
name|searchContext
operator|.
name|queryRewritten
argument_list|()
condition|)
block|{
return|return
name|searchContext
operator|.
name|query
argument_list|()
return|;
block|}
name|Query
name|rewriteQuery
init|=
name|in
operator|.
name|rewrite
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|searchContext
operator|.
name|updateRewriteQuery
argument_list|(
name|rewriteQuery
argument_list|)
expr_stmt|;
return|return
name|rewriteQuery
return|;
block|}
else|else
block|{
return|return
name|in
operator|.
name|rewrite
argument_list|(
name|original
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createNormalizedWeight
specifier|public
name|Weight
name|createNormalizedWeight
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
comment|// if its the main query, use we have dfs data, only then do it
if|if
condition|(
name|dfSource
operator|!=
literal|null
operator|&&
operator|(
name|query
operator|==
name|searchContext
operator|.
name|query
argument_list|()
operator|||
name|query
operator|==
name|searchContext
operator|.
name|parsedQuery
argument_list|()
operator|.
name|query
argument_list|()
operator|)
condition|)
block|{
return|return
name|dfSource
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|)
return|;
block|}
return|return
name|in
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|searchContext
operator|.
name|clearReleasables
argument_list|(
name|Lifetime
operator|.
name|COLLECTION
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|boolean
name|timeoutSet
init|=
name|searchContext
operator|.
name|timeoutInMillis
argument_list|()
operator|!=
operator|-
literal|1
decl_stmt|;
specifier|final
name|boolean
name|terminateAfterSet
init|=
name|searchContext
operator|.
name|terminateAfter
argument_list|()
operator|!=
name|SearchContext
operator|.
name|DEFAULT_TERMINATE_AFTER
decl_stmt|;
if|if
condition|(
name|timeoutSet
condition|)
block|{
comment|// TODO: change to use our own counter that uses the scheduler in ThreadPool
comment|// throws TimeLimitingCollector.TimeExceededException when timeout has reached
name|collector
operator|=
name|Lucene
operator|.
name|wrapTimeLimitingCollector
argument_list|(
name|collector
argument_list|,
name|searchContext
operator|.
name|timeoutInMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|terminateAfterSet
condition|)
block|{
comment|// throws Lucene.EarlyTerminationException when given count is reached
name|collector
operator|=
name|Lucene
operator|.
name|wrapCountBasedEarlyTerminatingCollector
argument_list|(
name|collector
argument_list|,
name|searchContext
operator|.
name|terminateAfter
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|currentState
operator|==
name|Stage
operator|.
name|MAIN_QUERY
condition|)
block|{
if|if
condition|(
name|enableMainDocIdSetCollector
condition|)
block|{
comment|// TODO should we create a cache of segment->docIdSets so we won't create one each time?
name|collector
operator|=
name|this
operator|.
name|mainDocIdSetCollector
operator|=
operator|new
name|DocIdSetCollector
argument_list|(
name|searchContext
operator|.
name|docSetCache
argument_list|()
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|searchContext
operator|.
name|parsedPostFilter
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// this will only get applied to the actual search collector and not
comment|// to any scoped collectors, also, it will only be applied to the main collector
comment|// since that is where the filter should only work
name|collector
operator|=
operator|new
name|FilteredCollector
argument_list|(
name|collector
argument_list|,
name|searchContext
operator|.
name|parsedPostFilter
argument_list|()
operator|.
name|filter
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryCollectors
operator|!=
literal|null
operator|&&
operator|!
name|queryCollectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|collector
operator|=
operator|new
name|MultiCollector
argument_list|(
name|collector
argument_list|,
name|queryCollectors
operator|.
name|toArray
argument_list|(
operator|new
name|Collector
index|[
name|queryCollectors
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// apply the minimum score after multi collector so we filter aggs as well
if|if
condition|(
name|searchContext
operator|.
name|minimumScore
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|collector
operator|=
operator|new
name|MinimumScoreCollector
argument_list|(
name|collector
argument_list|,
name|searchContext
operator|.
name|minimumScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// we only compute the doc id set once since within a context, we execute the same query always...
try|try
block|{
if|if
condition|(
name|timeoutSet
operator|||
name|terminateAfterSet
condition|)
block|{
try|try
block|{
name|super
operator|.
name|search
argument_list|(
name|leaves
argument_list|,
name|weight
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeLimitingCollector
operator|.
name|TimeExceededException
name|e
parameter_list|)
block|{
assert|assert
name|timeoutSet
operator|:
literal|"TimeExceededException thrown even though timeout wasn't set"
assert|;
name|searchContext
operator|.
name|queryResult
argument_list|()
operator|.
name|searchTimedOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Lucene
operator|.
name|EarlyTerminationException
name|e
parameter_list|)
block|{
assert|assert
name|terminateAfterSet
operator|:
literal|"EarlyTerminationException thrown even though terminateAfter wasn't set"
assert|;
name|searchContext
operator|.
name|queryResult
argument_list|()
operator|.
name|terminatedEarly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|terminateAfterSet
operator|&&
name|searchContext
operator|.
name|queryResult
argument_list|()
operator|.
name|terminatedEarly
argument_list|()
operator|==
literal|null
condition|)
block|{
name|searchContext
operator|.
name|queryResult
argument_list|()
operator|.
name|terminatedEarly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|search
argument_list|(
name|leaves
argument_list|,
name|weight
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|currentState
operator|==
name|Stage
operator|.
name|MAIN_QUERY
condition|)
block|{
if|if
condition|(
name|enableMainDocIdSetCollector
condition|)
block|{
name|enableMainDocIdSetCollector
operator|=
literal|false
expr_stmt|;
name|mainDocIdSetCollector
operator|.
name|postCollection
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|queryCollectors
operator|!=
literal|null
operator|&&
operator|!
name|queryCollectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Collector
name|queryCollector
range|:
name|queryCollectors
control|)
block|{
if|if
condition|(
name|queryCollector
operator|instanceof
name|XCollector
condition|)
block|{
operator|(
operator|(
name|XCollector
operator|)
name|queryCollector
operator|)
operator|.
name|postCollection
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
finally|finally
block|{
name|searchContext
operator|.
name|clearReleasables
argument_list|(
name|Lifetime
operator|.
name|COLLECTION
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|searchContext
operator|.
name|aliasFilter
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|doc
argument_list|)
return|;
block|}
name|XFilteredQuery
name|filteredQuery
init|=
operator|new
name|XFilteredQuery
argument_list|(
name|query
argument_list|,
name|searchContext
operator|.
name|aliasFilter
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|super
operator|.
name|explain
argument_list|(
name|filteredQuery
argument_list|,
name|doc
argument_list|)
return|;
block|}
finally|finally
block|{
name|searchContext
operator|.
name|clearReleasables
argument_list|(
name|Lifetime
operator|.
name|COLLECTION
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

