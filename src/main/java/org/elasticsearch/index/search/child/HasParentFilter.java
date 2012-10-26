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
name|set
operator|.
name|hash
operator|.
name|THashSet
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
name|AtomicReader
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
name|Collector
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
name|Filter
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
name|CacheRecycler
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
name|collect
operator|.
name|Tuple
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
name|GetDocSet
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
name|Map
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_comment
comment|/**  * A filter that only return child documents that are linked to the parent documents that matched with the inner query.  */
end_comment

begin_class
DECL|class|HasParentFilter
specifier|public
specifier|abstract
class|class
name|HasParentFilter
extends|extends
name|Filter
implements|implements
name|ScopePhase
operator|.
name|CollectorPhase
block|{
DECL|field|parentQuery
specifier|final
name|Query
name|parentQuery
decl_stmt|;
DECL|field|scope
specifier|final
name|String
name|scope
decl_stmt|;
DECL|field|parentType
specifier|final
name|String
name|parentType
decl_stmt|;
DECL|field|context
specifier|final
name|SearchContext
name|context
decl_stmt|;
DECL|method|HasParentFilter
name|HasParentFilter
parameter_list|(
name|Query
name|parentQuery
parameter_list|,
name|String
name|scope
parameter_list|,
name|String
name|parentType
parameter_list|,
name|SearchContext
name|context
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
name|scope
operator|=
name|scope
expr_stmt|;
name|this
operator|.
name|parentType
operator|=
name|parentType
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
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
DECL|method|query
specifier|public
name|Query
name|query
parameter_list|()
block|{
return|return
name|parentQuery
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
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
literal|"parent_filter["
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
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|create
specifier|public
specifier|static
name|HasParentFilter
name|create
parameter_list|(
name|String
name|executionType
parameter_list|,
name|Query
name|query
parameter_list|,
name|String
name|scope
parameter_list|,
name|String
name|parentType
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
comment|// This mechanism is experimental and will most likely be removed.
if|if
condition|(
literal|"bitset"
operator|.
name|equals
argument_list|(
name|executionType
argument_list|)
condition|)
block|{
return|return
operator|new
name|Bitset
argument_list|(
name|query
argument_list|,
name|scope
argument_list|,
name|parentType
argument_list|,
name|context
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"uid"
operator|.
name|equals
argument_list|(
name|executionType
argument_list|)
condition|)
block|{
return|return
operator|new
name|Uid
argument_list|(
name|query
argument_list|,
name|scope
argument_list|,
name|parentType
argument_list|,
name|context
argument_list|)
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Illegal has_parent execution type: "
operator|+
name|executionType
argument_list|)
throw|;
block|}
DECL|class|Uid
specifier|static
class|class
name|Uid
extends|extends
name|HasParentFilter
block|{
DECL|field|parents
name|THashSet
argument_list|<
name|HashedBytesArray
argument_list|>
name|parents
decl_stmt|;
DECL|method|Uid
name|Uid
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|scope
parameter_list|,
name|String
name|parentType
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|,
name|scope
argument_list|,
name|parentType
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|requiresProcessing
specifier|public
name|boolean
name|requiresProcessing
parameter_list|()
block|{
return|return
name|parents
operator|==
literal|null
return|;
block|}
DECL|method|collector
specifier|public
name|Collector
name|collector
parameter_list|()
block|{
name|parents
operator|=
name|CacheRecycler
operator|.
name|popHashSet
argument_list|()
expr_stmt|;
return|return
operator|new
name|ParentUidsCollector
argument_list|(
name|parents
argument_list|,
name|context
argument_list|,
name|parentType
argument_list|)
return|;
block|}
DECL|method|processCollector
specifier|public
name|void
name|processCollector
parameter_list|(
name|Collector
name|collector
parameter_list|)
block|{
name|parents
operator|=
operator|(
operator|(
name|ParentUidsCollector
operator|)
name|collector
operator|)
operator|.
name|collectedUids
expr_stmt|;
block|}
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|parents
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"has_parent filter/query hasn't executed properly"
argument_list|)
throw|;
block|}
name|IdReaderTypeCache
name|idReaderTypeCache
init|=
name|context
operator|.
name|idCache
argument_list|()
operator|.
name|reader
argument_list|(
name|readerContext
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
name|idReaderTypeCache
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ChildrenDocSet
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|parents
argument_list|,
name|idReaderTypeCache
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
if|if
condition|(
name|parents
operator|!=
literal|null
condition|)
block|{
name|CacheRecycler
operator|.
name|pushHashSet
argument_list|(
name|parents
argument_list|)
expr_stmt|;
block|}
name|parents
operator|=
literal|null
expr_stmt|;
block|}
DECL|class|ChildrenDocSet
specifier|static
class|class
name|ChildrenDocSet
extends|extends
name|GetDocSet
block|{
DECL|field|reader
specifier|final
name|IndexReader
name|reader
decl_stmt|;
DECL|field|parents
specifier|final
name|THashSet
argument_list|<
name|HashedBytesArray
argument_list|>
name|parents
decl_stmt|;
DECL|field|idReaderTypeCache
specifier|final
name|IdReaderTypeCache
name|idReaderTypeCache
decl_stmt|;
DECL|field|acceptDocs
specifier|final
name|Bits
name|acceptDocs
decl_stmt|;
DECL|method|ChildrenDocSet
name|ChildrenDocSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|THashSet
argument_list|<
name|HashedBytesArray
argument_list|>
name|parents
parameter_list|,
name|IdReaderTypeCache
name|idReaderTypeCache
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
name|super
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|parents
operator|=
name|parents
expr_stmt|;
name|this
operator|.
name|idReaderTypeCache
operator|=
name|idReaderTypeCache
expr_stmt|;
name|this
operator|.
name|acceptDocs
operator|=
name|acceptDocs
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|!
name|acceptDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|&&
name|parents
operator|.
name|contains
argument_list|(
name|idReaderTypeCache
operator|.
name|parentIdByDoc
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|ParentUidsCollector
specifier|static
class|class
name|ParentUidsCollector
extends|extends
name|NoopCollector
block|{
DECL|field|collectedUids
specifier|final
name|THashSet
argument_list|<
name|HashedBytesArray
argument_list|>
name|collectedUids
decl_stmt|;
DECL|field|context
specifier|final
name|SearchContext
name|context
decl_stmt|;
DECL|field|parentType
specifier|final
name|String
name|parentType
decl_stmt|;
DECL|field|typeCache
name|IdReaderTypeCache
name|typeCache
decl_stmt|;
DECL|method|ParentUidsCollector
name|ParentUidsCollector
parameter_list|(
name|THashSet
argument_list|<
name|HashedBytesArray
argument_list|>
name|collectedUids
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|String
name|parentType
parameter_list|)
block|{
name|this
operator|.
name|collectedUids
operator|=
name|collectedUids
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|parentType
operator|=
name|parentType
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
name|collectedUids
operator|.
name|add
argument_list|(
name|typeCache
operator|.
name|idByDoc
argument_list|(
name|doc
argument_list|)
argument_list|)
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
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|typeCache
operator|=
name|context
operator|.
name|idCache
argument_list|()
operator|.
name|reader
argument_list|(
name|readerContext
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
block|}
DECL|class|Bitset
specifier|static
class|class
name|Bitset
extends|extends
name|HasParentFilter
block|{
DECL|field|parentDocs
name|Map
argument_list|<
name|Object
argument_list|,
name|FixedBitSet
argument_list|>
name|parentDocs
decl_stmt|;
DECL|method|Bitset
name|Bitset
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|scope
parameter_list|,
name|String
name|parentType
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|,
name|scope
argument_list|,
name|parentType
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|requiresProcessing
specifier|public
name|boolean
name|requiresProcessing
parameter_list|()
block|{
return|return
name|parentDocs
operator|==
literal|null
return|;
block|}
DECL|method|collector
specifier|public
name|Collector
name|collector
parameter_list|()
block|{
return|return
operator|new
name|ParentDocsCollector
argument_list|()
return|;
block|}
DECL|method|processCollector
specifier|public
name|void
name|processCollector
parameter_list|(
name|Collector
name|collector
parameter_list|)
block|{
name|parentDocs
operator|=
operator|(
operator|(
name|ParentDocsCollector
operator|)
name|collector
operator|)
operator|.
name|segmentResults
expr_stmt|;
block|}
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|parentDocs
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"has_parent filter/query hasn't executed properly"
argument_list|)
throw|;
block|}
return|return
operator|new
name|ChildrenDocSet
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|parentDocs
argument_list|,
name|context
argument_list|,
name|parentType
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
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
block|}
DECL|class|ChildrenDocSet
specifier|static
class|class
name|ChildrenDocSet
extends|extends
name|GetDocSet
block|{
DECL|field|currentTypeCache
specifier|final
name|IdReaderTypeCache
name|currentTypeCache
decl_stmt|;
DECL|field|currentReader
specifier|final
name|AtomicReader
name|currentReader
decl_stmt|;
DECL|field|readersToTypeCache
specifier|final
name|Tuple
argument_list|<
name|AtomicReader
argument_list|,
name|IdReaderTypeCache
argument_list|>
index|[]
name|readersToTypeCache
decl_stmt|;
DECL|field|parentDocs
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|FixedBitSet
argument_list|>
name|parentDocs
decl_stmt|;
DECL|field|acceptDocs
specifier|final
name|Bits
name|acceptDocs
decl_stmt|;
DECL|method|ChildrenDocSet
name|ChildrenDocSet
parameter_list|(
name|AtomicReader
name|currentReader
parameter_list|,
name|Map
argument_list|<
name|Object
argument_list|,
name|FixedBitSet
argument_list|>
name|parentDocs
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|String
name|parentType
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
name|super
argument_list|(
name|currentReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|acceptDocs
operator|=
name|acceptDocs
expr_stmt|;
name|this
operator|.
name|currentTypeCache
operator|=
name|context
operator|.
name|idCache
argument_list|()
operator|.
name|reader
argument_list|(
name|currentReader
argument_list|)
operator|.
name|type
argument_list|(
name|parentType
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentReader
operator|=
name|currentReader
expr_stmt|;
name|this
operator|.
name|parentDocs
operator|=
name|parentDocs
expr_stmt|;
name|this
operator|.
name|readersToTypeCache
operator|=
operator|new
name|Tuple
index|[
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
index|]
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
name|readersToTypeCache
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|AtomicReader
name|reader
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
name|readersToTypeCache
index|[
name|i
index|]
operator|=
operator|new
name|Tuple
argument_list|<
name|AtomicReader
argument_list|,
name|IdReaderTypeCache
argument_list|>
argument_list|(
name|reader
argument_list|,
name|context
operator|.
name|idCache
argument_list|()
operator|.
name|reader
argument_list|(
name|reader
argument_list|)
operator|.
name|type
argument_list|(
name|parentType
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
if|if
condition|(
name|acceptDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|||
name|doc
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
name|HashedBytesArray
name|parentId
init|=
name|currentTypeCache
operator|.
name|parentIdByDoc
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentId
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|Tuple
argument_list|<
name|AtomicReader
argument_list|,
name|IdReaderTypeCache
argument_list|>
name|readerTypeCacheTuple
range|:
name|readersToTypeCache
control|)
block|{
name|int
name|parentDocId
init|=
name|readerTypeCacheTuple
operator|.
name|v2
argument_list|()
operator|.
name|docById
argument_list|(
name|parentId
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentDocId
operator|==
operator|-
literal|1
condition|)
block|{
continue|continue;
block|}
name|FixedBitSet
name|currentParentDocs
init|=
name|parentDocs
operator|.
name|get
argument_list|(
name|readerTypeCacheTuple
operator|.
name|v1
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentParentDocs
operator|.
name|get
argument_list|(
name|parentDocId
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
DECL|class|ParentDocsCollector
specifier|static
class|class
name|ParentDocsCollector
extends|extends
name|NoopCollector
block|{
DECL|field|segmentResults
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|FixedBitSet
argument_list|>
name|segmentResults
init|=
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|current
name|FixedBitSet
name|current
decl_stmt|;
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
name|current
operator|.
name|set
argument_list|(
name|doc
argument_list|)
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
name|segmentResults
operator|.
name|put
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|,
name|current
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

