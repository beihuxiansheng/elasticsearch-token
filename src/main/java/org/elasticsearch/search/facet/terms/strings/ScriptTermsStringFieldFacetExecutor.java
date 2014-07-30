begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.terms.strings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|terms
operator|.
name|strings
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectIntOpenHashMap
import|;
end_import

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
name|ImmutableList
import|;
end_import

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
name|ImmutableSet
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cache
operator|.
name|recycler
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
name|collect
operator|.
name|BoundedTreeSet
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
name|recycler
operator|.
name|Recycler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|SearchScript
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
name|facet
operator|.
name|FacetExecutor
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
name|facet
operator|.
name|InternalFacet
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
name|facet
operator|.
name|terms
operator|.
name|support
operator|.
name|EntryPriorityQueue
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
name|Arrays
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
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ScriptTermsStringFieldFacetExecutor
specifier|public
class|class
name|ScriptTermsStringFieldFacetExecutor
extends|extends
name|FacetExecutor
block|{
DECL|field|comparatorType
specifier|private
specifier|final
name|InternalStringTermsFacet
operator|.
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|shardSize
specifier|private
specifier|final
name|int
name|shardSize
decl_stmt|;
DECL|field|script
specifier|private
specifier|final
name|SearchScript
name|script
decl_stmt|;
DECL|field|matcher
specifier|private
specifier|final
name|Matcher
name|matcher
decl_stmt|;
DECL|field|excluded
specifier|private
specifier|final
name|ImmutableSet
argument_list|<
name|BytesRef
argument_list|>
name|excluded
decl_stmt|;
DECL|field|numberOfShards
specifier|private
specifier|final
name|int
name|numberOfShards
decl_stmt|;
DECL|field|facets
specifier|final
name|Recycler
operator|.
name|V
argument_list|<
name|ObjectIntOpenHashMap
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|facets
decl_stmt|;
DECL|field|missing
name|long
name|missing
decl_stmt|;
DECL|field|total
name|long
name|total
decl_stmt|;
DECL|method|ScriptTermsStringFieldFacetExecutor
specifier|public
name|ScriptTermsStringFieldFacetExecutor
parameter_list|(
name|int
name|size
parameter_list|,
name|int
name|shardSize
parameter_list|,
name|InternalStringTermsFacet
operator|.
name|ComparatorType
name|comparatorType
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|ImmutableSet
argument_list|<
name|BytesRef
argument_list|>
name|excluded
parameter_list|,
name|Pattern
name|pattern
parameter_list|,
name|String
name|scriptLang
parameter_list|,
name|String
name|script
parameter_list|,
name|ScriptService
operator|.
name|ScriptType
name|scriptType
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|,
name|CacheRecycler
name|cacheRecycler
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|shardSize
operator|=
name|shardSize
expr_stmt|;
name|this
operator|.
name|comparatorType
operator|=
name|comparatorType
expr_stmt|;
name|this
operator|.
name|numberOfShards
operator|=
name|context
operator|.
name|numberOfShards
argument_list|()
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|context
operator|.
name|scriptService
argument_list|()
operator|.
name|search
argument_list|(
name|context
operator|.
name|lookup
argument_list|()
argument_list|,
name|scriptLang
argument_list|,
name|script
argument_list|,
name|scriptType
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|this
operator|.
name|excluded
operator|=
name|excluded
expr_stmt|;
name|this
operator|.
name|matcher
operator|=
name|pattern
operator|!=
literal|null
condition|?
name|pattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|facets
operator|=
name|cacheRecycler
operator|.
name|objectIntMap
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collector
specifier|public
name|Collector
name|collector
parameter_list|()
block|{
return|return
operator|new
name|Collector
argument_list|(
name|matcher
argument_list|,
name|excluded
argument_list|,
name|script
argument_list|,
name|facets
operator|.
name|v
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|buildFacet
specifier|public
name|InternalFacet
name|buildFacet
parameter_list|(
name|String
name|facetName
parameter_list|)
block|{
if|if
condition|(
name|facets
operator|.
name|v
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|facets
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|InternalStringTermsFacet
argument_list|(
name|facetName
argument_list|,
name|comparatorType
argument_list|,
name|size
argument_list|,
name|ImmutableList
operator|.
expr|<
name|InternalStringTermsFacet
operator|.
name|TermEntry
operator|>
name|of
argument_list|()
argument_list|,
name|missing
argument_list|,
name|total
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|boolean
index|[]
name|states
init|=
name|facets
operator|.
name|v
argument_list|()
operator|.
name|allocated
decl_stmt|;
specifier|final
name|Object
index|[]
name|keys
init|=
name|facets
operator|.
name|v
argument_list|()
operator|.
name|keys
decl_stmt|;
specifier|final
name|int
index|[]
name|values
init|=
name|facets
operator|.
name|v
argument_list|()
operator|.
name|values
decl_stmt|;
if|if
condition|(
name|shardSize
operator|<
name|EntryPriorityQueue
operator|.
name|LIMIT
condition|)
block|{
name|EntryPriorityQueue
name|ordered
init|=
operator|new
name|EntryPriorityQueue
argument_list|(
name|shardSize
argument_list|,
name|comparatorType
operator|.
name|comparator
argument_list|()
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
name|states
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|states
index|[
name|i
index|]
condition|)
block|{
name|BytesRef
name|key
init|=
operator|(
name|BytesRef
operator|)
name|keys
index|[
name|i
index|]
decl_stmt|;
name|ordered
operator|.
name|insertWithOverflow
argument_list|(
operator|new
name|InternalStringTermsFacet
operator|.
name|TermEntry
argument_list|(
name|key
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|InternalStringTermsFacet
operator|.
name|TermEntry
index|[]
name|list
init|=
operator|new
name|InternalStringTermsFacet
operator|.
name|TermEntry
index|[
name|ordered
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|ordered
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|list
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|InternalStringTermsFacet
operator|.
name|TermEntry
operator|)
name|ordered
operator|.
name|pop
argument_list|()
operator|)
expr_stmt|;
block|}
name|facets
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|InternalStringTermsFacet
argument_list|(
name|facetName
argument_list|,
name|comparatorType
argument_list|,
name|size
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|list
argument_list|)
argument_list|,
name|missing
argument_list|,
name|total
argument_list|)
return|;
block|}
else|else
block|{
name|BoundedTreeSet
argument_list|<
name|InternalStringTermsFacet
operator|.
name|TermEntry
argument_list|>
name|ordered
init|=
operator|new
name|BoundedTreeSet
argument_list|<>
argument_list|(
name|comparatorType
operator|.
name|comparator
argument_list|()
argument_list|,
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
name|states
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|states
index|[
name|i
index|]
condition|)
block|{
name|BytesRef
name|key
init|=
operator|(
name|BytesRef
operator|)
name|keys
index|[
name|i
index|]
decl_stmt|;
name|ordered
operator|.
name|add
argument_list|(
operator|new
name|InternalStringTermsFacet
operator|.
name|TermEntry
argument_list|(
name|key
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|facets
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|InternalStringTermsFacet
argument_list|(
name|facetName
argument_list|,
name|comparatorType
argument_list|,
name|size
argument_list|,
name|ordered
argument_list|,
name|missing
argument_list|,
name|total
argument_list|)
return|;
block|}
block|}
block|}
DECL|class|Collector
class|class
name|Collector
extends|extends
name|FacetExecutor
operator|.
name|Collector
block|{
DECL|field|matcher
specifier|private
specifier|final
name|Matcher
name|matcher
decl_stmt|;
DECL|field|excluded
specifier|private
specifier|final
name|ImmutableSet
argument_list|<
name|BytesRef
argument_list|>
name|excluded
decl_stmt|;
DECL|field|script
specifier|private
specifier|final
name|SearchScript
name|script
decl_stmt|;
DECL|field|facets
specifier|private
specifier|final
name|ObjectIntOpenHashMap
argument_list|<
name|BytesRef
argument_list|>
name|facets
decl_stmt|;
DECL|field|missing
name|long
name|missing
decl_stmt|;
DECL|field|total
name|long
name|total
decl_stmt|;
DECL|method|Collector
name|Collector
parameter_list|(
name|Matcher
name|matcher
parameter_list|,
name|ImmutableSet
argument_list|<
name|BytesRef
argument_list|>
name|excluded
parameter_list|,
name|SearchScript
name|script
parameter_list|,
name|ObjectIntOpenHashMap
argument_list|<
name|BytesRef
argument_list|>
name|facets
parameter_list|)
block|{
name|this
operator|.
name|matcher
operator|=
name|matcher
expr_stmt|;
name|this
operator|.
name|excluded
operator|=
name|excluded
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|this
operator|.
name|facets
operator|=
name|facets
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
name|script
operator|.
name|setScorer
argument_list|(
name|scorer
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
name|script
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
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
name|script
operator|.
name|setNextDocId
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|script
operator|.
name|run
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|missing
operator|++
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|o
operator|instanceof
name|Iterable
condition|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Object
name|o1
range|:
operator|(
operator|(
name|Iterable
operator|)
name|o
operator|)
control|)
block|{
name|String
name|value
init|=
name|o1
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|match
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
comment|// LUCENE 4 UPGRADE: should be possible to convert directly to BR
name|facets
operator|.
name|addTo
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|total
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|missing
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Object
index|[]
condition|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Object
name|o1
range|:
operator|(
operator|(
name|Object
index|[]
operator|)
name|o
operator|)
control|)
block|{
name|String
name|value
init|=
name|o1
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|match
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
comment|// LUCENE 4 UPGRADE: should be possible to convert directly to BR
name|facets
operator|.
name|addTo
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|total
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|missing
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|value
init|=
name|o
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|match
argument_list|(
name|value
argument_list|)
condition|)
block|{
comment|// LUCENE 4 UPGRADE: should be possible to convert directly to BR
name|facets
operator|.
name|addTo
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|total
operator|++
expr_stmt|;
block|}
else|else
block|{
name|missing
operator|++
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|postCollection
specifier|public
name|void
name|postCollection
parameter_list|()
block|{
name|ScriptTermsStringFieldFacetExecutor
operator|.
name|this
operator|.
name|missing
operator|=
name|missing
expr_stmt|;
name|ScriptTermsStringFieldFacetExecutor
operator|.
name|this
operator|.
name|total
operator|=
name|total
expr_stmt|;
block|}
DECL|method|match
specifier|private
name|boolean
name|match
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|excluded
operator|!=
literal|null
operator|&&
name|excluded
operator|.
name|contains
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|matcher
operator|!=
literal|null
operator|&&
operator|!
name|matcher
operator|.
name|reset
argument_list|(
name|value
argument_list|)
operator|.
name|matches
argument_list|()
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
block|}
block|}
end_class

end_unit

