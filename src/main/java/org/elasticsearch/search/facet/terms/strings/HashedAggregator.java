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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ArrayUtil
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
name|lucene
operator|.
name|HashedBytesRef
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
name|BytesRefHash
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
name|fielddata
operator|.
name|SortedBinaryDocValues
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
name|TermsFacet
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|HashedAggregator
specifier|public
class|class
name|HashedAggregator
block|{
DECL|field|missing
specifier|private
name|int
name|missing
decl_stmt|;
DECL|field|total
specifier|private
name|int
name|total
decl_stmt|;
DECL|field|hash
specifier|private
specifier|final
name|HashCount
name|hash
decl_stmt|;
DECL|field|assertHash
specifier|private
specifier|final
name|HashCount
name|assertHash
init|=
name|getAssertHash
argument_list|()
decl_stmt|;
DECL|method|HashedAggregator
specifier|public
name|HashedAggregator
parameter_list|()
block|{
name|hash
operator|=
operator|new
name|BytesRefHashHashCount
argument_list|(
operator|new
name|BytesRefHash
argument_list|(
literal|10
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|onDoc
specifier|public
name|void
name|onDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|SortedBinaryDocValues
name|values
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
specifier|final
name|int
name|length
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
name|int
name|pendingMissing
init|=
literal|1
decl_stmt|;
name|total
operator|+=
name|length
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
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|BytesRef
name|value
init|=
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|onValue
argument_list|(
name|docId
argument_list|,
name|value
argument_list|,
name|value
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|pendingMissing
operator|=
literal|0
expr_stmt|;
block|}
name|missing
operator|+=
name|pendingMissing
expr_stmt|;
block|}
DECL|method|addValue
specifier|public
name|void
name|addValue
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|int
name|hashCode
parameter_list|)
block|{
specifier|final
name|boolean
name|added
init|=
name|hash
operator|.
name|addNoCount
argument_list|(
name|value
argument_list|,
name|hashCode
argument_list|)
decl_stmt|;
assert|assert
name|assertHash
operator|.
name|addNoCount
argument_list|(
name|value
argument_list|,
name|hashCode
argument_list|)
operator|==
name|added
operator|:
literal|"asserting counter diverged from current counter - value: "
operator|+
name|value
operator|+
literal|" hash: "
operator|+
name|hashCode
assert|;
block|}
DECL|method|onValue
specifier|protected
name|void
name|onValue
parameter_list|(
name|int
name|docId
parameter_list|,
name|BytesRef
name|value
parameter_list|,
name|int
name|hashCode
parameter_list|)
block|{
specifier|final
name|boolean
name|added
init|=
name|hash
operator|.
name|add
argument_list|(
name|value
argument_list|,
name|hashCode
argument_list|)
decl_stmt|;
comment|// note: we must do a deep copy here the incoming value could have been
comment|// modified by a script or so
assert|assert
name|assertHash
operator|.
name|add
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|value
argument_list|)
argument_list|,
name|hashCode
argument_list|)
operator|==
name|added
operator|:
literal|"asserting counter diverged from current counter - value: "
operator|+
name|value
operator|+
literal|" hash: "
operator|+
name|hashCode
assert|;
block|}
DECL|method|missing
specifier|public
specifier|final
name|int
name|missing
parameter_list|()
block|{
return|return
name|missing
return|;
block|}
DECL|method|total
specifier|public
specifier|final
name|int
name|total
parameter_list|()
block|{
return|return
name|total
return|;
block|}
DECL|method|isEmpty
specifier|public
specifier|final
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|hash
operator|.
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
DECL|method|getIter
specifier|public
name|BytesRefCountIterator
name|getIter
parameter_list|()
block|{
assert|assert
name|hash
operator|.
name|size
argument_list|()
operator|==
name|assertHash
operator|.
name|size
argument_list|()
assert|;
return|return
name|hash
operator|.
name|iter
argument_list|()
return|;
block|}
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
block|{
name|hash
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
DECL|interface|BytesRefCountIterator
specifier|public
specifier|static
interface|interface
name|BytesRefCountIterator
block|{
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
function_decl|;
DECL|method|makeSafe
name|BytesRef
name|makeSafe
parameter_list|()
function_decl|;
DECL|method|count
specifier|public
name|int
name|count
parameter_list|()
function_decl|;
DECL|method|shared
specifier|public
name|boolean
name|shared
parameter_list|()
function_decl|;
block|}
DECL|method|buildFacet
specifier|public
specifier|static
name|InternalFacet
name|buildFacet
parameter_list|(
name|String
name|facetName
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|shardSize
parameter_list|,
name|long
name|missing
parameter_list|,
name|long
name|total
parameter_list|,
name|TermsFacet
operator|.
name|ComparatorType
name|comparatorType
parameter_list|,
name|HashedAggregator
name|aggregator
parameter_list|)
block|{
if|if
condition|(
name|aggregator
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
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
name|BytesRefCountIterator
name|iter
init|=
name|aggregator
operator|.
name|getIter
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ordered
operator|.
name|insertWithOverflow
argument_list|(
operator|new
name|InternalStringTermsFacet
operator|.
name|TermEntry
argument_list|(
name|iter
operator|.
name|makeSafe
argument_list|()
argument_list|,
name|iter
operator|.
name|count
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// maybe we can survive with a 0-copy here if we keep the
comment|// bytes ref hash around?
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
name|BytesRefCountIterator
name|iter
init|=
name|aggregator
operator|.
name|getIter
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ordered
operator|.
name|add
argument_list|(
operator|new
name|InternalStringTermsFacet
operator|.
name|TermEntry
argument_list|(
name|iter
operator|.
name|makeSafe
argument_list|()
argument_list|,
name|iter
operator|.
name|count
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// maybe we can survive with a 0-copy here if we keep the
comment|// bytes ref hash around?
block|}
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
DECL|method|getAssertHash
specifier|private
name|HashCount
name|getAssertHash
parameter_list|()
block|{
name|HashCount
name|count
init|=
literal|null
decl_stmt|;
assert|assert
operator|(
name|count
operator|=
operator|new
name|AssertingHashCount
argument_list|()
operator|)
operator|!=
literal|null
assert|;
return|return
name|count
return|;
block|}
DECL|interface|HashCount
specifier|private
specifier|static
interface|interface
name|HashCount
block|{
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|int
name|hashCode
parameter_list|)
function_decl|;
DECL|method|addNoCount
specifier|public
name|boolean
name|addNoCount
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|int
name|hashCode
parameter_list|)
function_decl|;
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
function_decl|;
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
DECL|method|iter
specifier|public
name|BytesRefCountIterator
name|iter
parameter_list|()
function_decl|;
block|}
DECL|class|BytesRefHashHashCount
specifier|private
specifier|static
specifier|final
class|class
name|BytesRefHashHashCount
implements|implements
name|HashCount
block|{
DECL|field|hash
specifier|private
specifier|final
name|BytesRefHash
name|hash
decl_stmt|;
DECL|field|counts
specifier|private
name|int
index|[]
name|counts
init|=
operator|new
name|int
index|[
literal|10
index|]
decl_stmt|;
DECL|method|BytesRefHashHashCount
specifier|public
name|BytesRefHashHashCount
parameter_list|(
name|BytesRefHash
name|hash
parameter_list|)
block|{
name|this
operator|.
name|hash
operator|=
name|hash
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|int
name|hashCode
parameter_list|)
block|{
name|int
name|key
init|=
operator|(
name|int
operator|)
name|hash
operator|.
name|add
argument_list|(
name|value
argument_list|,
name|hashCode
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|<
literal|0
condition|)
block|{
name|key
operator|=
operator|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
operator|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|>=
name|counts
operator|.
name|length
condition|)
block|{
name|counts
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|counts
argument_list|,
name|key
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|counts
index|[
name|key
index|]
operator|++
operator|)
operator|==
literal|0
return|;
block|}
DECL|method|addNoCount
specifier|public
name|boolean
name|addNoCount
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|int
name|hashCode
parameter_list|)
block|{
name|int
name|key
init|=
operator|(
name|int
operator|)
name|hash
operator|.
name|add
argument_list|(
name|value
argument_list|,
name|hashCode
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|added
init|=
name|key
operator|>=
literal|0
decl_stmt|;
if|if
condition|(
name|key
operator|<
literal|0
condition|)
block|{
name|key
operator|=
operator|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
operator|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|>=
name|counts
operator|.
name|length
condition|)
block|{
name|counts
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|counts
argument_list|,
name|key
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|added
return|;
block|}
annotation|@
name|Override
DECL|method|iter
specifier|public
name|BytesRefCountIterator
name|iter
parameter_list|()
block|{
return|return
operator|new
name|BytesRefCountIteratorImpl
argument_list|()
return|;
block|}
DECL|class|BytesRefCountIteratorImpl
specifier|public
specifier|final
class|class
name|BytesRefCountIteratorImpl
implements|implements
name|BytesRefCountIterator
block|{
DECL|field|spare
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|current
specifier|private
name|int
name|current
init|=
literal|0
decl_stmt|;
DECL|field|currentCount
specifier|private
name|int
name|currentCount
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|BytesRefCountIteratorImpl
name|BytesRefCountIteratorImpl
parameter_list|()
block|{
name|this
operator|.
name|size
operator|=
operator|(
name|int
operator|)
name|hash
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
name|current
operator|<
name|size
condition|)
block|{
name|currentCount
operator|=
name|counts
index|[
name|current
index|]
expr_stmt|;
name|hash
operator|.
name|get
argument_list|(
name|current
operator|++
argument_list|,
name|spare
argument_list|)
expr_stmt|;
return|return
name|spare
return|;
block|}
name|currentCount
operator|=
operator|-
literal|1
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|makeSafe
specifier|public
name|BytesRef
name|makeSafe
parameter_list|()
block|{
return|return
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|spare
argument_list|)
return|;
block|}
DECL|method|count
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|currentCount
return|;
block|}
annotation|@
name|Override
DECL|method|shared
specifier|public
name|boolean
name|shared
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|hash
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
block|{
name|hash
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|AssertingHashCount
specifier|private
specifier|static
specifier|final
class|class
name|AssertingHashCount
implements|implements
name|HashCount
block|{
comment|// simple
comment|// implementation for assertions
DECL|field|valuesAndCount
specifier|private
specifier|final
name|ObjectIntOpenHashMap
argument_list|<
name|HashedBytesRef
argument_list|>
name|valuesAndCount
init|=
operator|new
name|ObjectIntOpenHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|spare
specifier|private
name|HashedBytesRef
name|spare
init|=
operator|new
name|HashedBytesRef
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|int
name|hashCode
parameter_list|)
block|{
name|int
name|adjustedValue
init|=
name|valuesAndCount
operator|.
name|addTo
argument_list|(
name|spare
operator|.
name|reset
argument_list|(
name|value
argument_list|,
name|hashCode
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
assert|assert
name|adjustedValue
operator|>=
literal|1
assert|;
if|if
condition|(
name|adjustedValue
operator|==
literal|1
condition|)
block|{
comment|// only if we added the spare we create a
comment|// new instance
name|spare
operator|.
name|bytes
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|spare
operator|=
operator|new
name|HashedBytesRef
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|valuesAndCount
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|iter
specifier|public
name|BytesRefCountIterator
name|iter
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
block|{         }
annotation|@
name|Override
DECL|method|addNoCount
specifier|public
name|boolean
name|addNoCount
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|int
name|hashCode
parameter_list|)
block|{
if|if
condition|(
operator|!
name|valuesAndCount
operator|.
name|containsKey
argument_list|(
name|spare
operator|.
name|reset
argument_list|(
name|value
argument_list|,
name|hashCode
argument_list|)
argument_list|)
condition|)
block|{
name|valuesAndCount
operator|.
name|addTo
argument_list|(
name|spare
operator|.
name|reset
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|value
argument_list|)
argument_list|,
name|hashCode
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|spare
operator|=
operator|new
name|HashedBytesRef
argument_list|()
expr_stmt|;
comment|// reset the reference since we just added to the hash
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

