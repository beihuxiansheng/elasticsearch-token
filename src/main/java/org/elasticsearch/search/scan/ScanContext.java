begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.scan
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|scan
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|docset
operator|.
name|AllDocIdSet
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * The scan context allows to optimize readers we already processed during scanning. We do that by keeping track  * of the count per reader, and if we are done with it, we no longer process it by using a filter that returns  * null docIdSet for this reader.  */
end_comment

begin_class
DECL|class|ScanContext
specifier|public
class|class
name|ScanContext
block|{
DECL|field|readerStates
specifier|private
specifier|final
name|Map
argument_list|<
name|IndexReader
argument_list|,
name|ReaderState
argument_list|>
name|readerStates
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|readerStates
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|execute
specifier|public
name|TopDocs
name|execute
parameter_list|(
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ScanCollector
name|collector
init|=
operator|new
name|ScanCollector
argument_list|(
name|readerStates
argument_list|,
name|context
operator|.
name|from
argument_list|()
argument_list|,
name|context
operator|.
name|size
argument_list|()
argument_list|,
name|context
operator|.
name|trackScores
argument_list|()
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|XFilteredQuery
argument_list|(
name|context
operator|.
name|query
argument_list|()
argument_list|,
operator|new
name|ScanFilter
argument_list|(
name|readerStates
argument_list|,
name|collector
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ScanCollector
operator|.
name|StopCollectingException
name|e
parameter_list|)
block|{
comment|// all is well
block|}
return|return
name|collector
operator|.
name|topDocs
argument_list|()
return|;
block|}
DECL|class|ScanCollector
specifier|static
class|class
name|ScanCollector
extends|extends
name|Collector
block|{
DECL|field|readerStates
specifier|private
specifier|final
name|Map
argument_list|<
name|IndexReader
argument_list|,
name|ReaderState
argument_list|>
name|readerStates
decl_stmt|;
DECL|field|from
specifier|private
specifier|final
name|int
name|from
decl_stmt|;
DECL|field|to
specifier|private
specifier|final
name|int
name|to
decl_stmt|;
DECL|field|docs
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|ScoreDoc
argument_list|>
name|docs
decl_stmt|;
DECL|field|trackScores
specifier|private
specifier|final
name|boolean
name|trackScores
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
DECL|field|docBase
specifier|private
name|int
name|docBase
decl_stmt|;
DECL|field|counter
specifier|private
name|int
name|counter
decl_stmt|;
DECL|field|currentReader
specifier|private
name|IndexReader
name|currentReader
decl_stmt|;
DECL|field|readerState
specifier|private
name|ReaderState
name|readerState
decl_stmt|;
DECL|method|ScanCollector
name|ScanCollector
parameter_list|(
name|Map
argument_list|<
name|IndexReader
argument_list|,
name|ReaderState
argument_list|>
name|readerStates
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|size
parameter_list|,
name|boolean
name|trackScores
parameter_list|)
block|{
name|this
operator|.
name|readerStates
operator|=
name|readerStates
expr_stmt|;
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|from
operator|+
name|size
expr_stmt|;
name|this
operator|.
name|trackScores
operator|=
name|trackScores
expr_stmt|;
name|this
operator|.
name|docs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|incCounter
name|void
name|incCounter
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|counter
operator|+=
name|count
expr_stmt|;
block|}
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|()
block|{
return|return
operator|new
name|TopDocs
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|,
name|docs
operator|.
name|toArray
argument_list|(
operator|new
name|ScoreDoc
index|[
name|docs
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
literal|0f
argument_list|)
return|;
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
name|counter
operator|>=
name|from
condition|)
block|{
name|docs
operator|.
name|add
argument_list|(
operator|new
name|ScoreDoc
argument_list|(
name|docBase
operator|+
name|doc
argument_list|,
name|trackScores
condition|?
name|scorer
operator|.
name|score
argument_list|()
else|:
literal|0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|readerState
operator|.
name|count
operator|++
expr_stmt|;
name|counter
operator|++
expr_stmt|;
if|if
condition|(
name|counter
operator|>=
name|to
condition|)
block|{
throw|throw
name|StopCollectingException
throw|;
block|}
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
comment|// if we have a reader state, and we haven't registered one already, register it
comment|// we need to check in readersState since even when the filter return null, setNextReader is still
comment|// called for that reader (before)
if|if
condition|(
name|currentReader
operator|!=
literal|null
operator|&&
operator|!
name|readerStates
operator|.
name|containsKey
argument_list|(
name|currentReader
argument_list|)
condition|)
block|{
assert|assert
name|readerState
operator|!=
literal|null
assert|;
name|readerState
operator|.
name|done
operator|=
literal|true
expr_stmt|;
name|readerStates
operator|.
name|put
argument_list|(
name|currentReader
argument_list|,
name|readerState
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|currentReader
operator|=
name|context
operator|.
name|reader
argument_list|()
expr_stmt|;
name|this
operator|.
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
name|this
operator|.
name|readerState
operator|=
operator|new
name|ReaderState
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|field|StopCollectingException
specifier|public
specifier|static
specifier|final
name|RuntimeException
name|StopCollectingException
init|=
operator|new
name|StopCollectingException
argument_list|()
decl_stmt|;
DECL|class|StopCollectingException
specifier|static
class|class
name|StopCollectingException
extends|extends
name|RuntimeException
block|{
annotation|@
name|Override
DECL|method|fillInStackTrace
specifier|public
name|Throwable
name|fillInStackTrace
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
DECL|class|ScanFilter
specifier|public
specifier|static
class|class
name|ScanFilter
extends|extends
name|Filter
block|{
DECL|field|readerStates
specifier|private
specifier|final
name|Map
argument_list|<
name|IndexReader
argument_list|,
name|ReaderState
argument_list|>
name|readerStates
decl_stmt|;
DECL|field|scanCollector
specifier|private
specifier|final
name|ScanCollector
name|scanCollector
decl_stmt|;
DECL|method|ScanFilter
specifier|public
name|ScanFilter
parameter_list|(
name|Map
argument_list|<
name|IndexReader
argument_list|,
name|ReaderState
argument_list|>
name|readerStates
parameter_list|,
name|ScanCollector
name|scanCollector
parameter_list|)
block|{
name|this
operator|.
name|readerStates
operator|=
name|readerStates
expr_stmt|;
name|this
operator|.
name|scanCollector
operator|=
name|scanCollector
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptedDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|ReaderState
name|readerState
init|=
name|readerStates
operator|.
name|get
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|readerState
operator|!=
literal|null
operator|&&
name|readerState
operator|.
name|done
condition|)
block|{
name|scanCollector
operator|.
name|incCounter
argument_list|(
name|readerState
operator|.
name|count
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
operator|new
name|AllDocIdSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|ReaderState
specifier|static
class|class
name|ReaderState
block|{
DECL|field|count
specifier|public
name|int
name|count
decl_stmt|;
DECL|field|done
specifier|public
name|boolean
name|done
decl_stmt|;
block|}
block|}
end_class

end_unit

