begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.docset
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|docset
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
name|util
operator|.
name|Bits
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
comment|/**  *  */
end_comment

begin_class
DECL|class|AndDocIdSet
specifier|public
class|class
name|AndDocIdSet
extends|extends
name|DocIdSet
block|{
DECL|field|sets
specifier|private
specifier|final
name|DocIdSet
index|[]
name|sets
decl_stmt|;
DECL|method|AndDocIdSet
specifier|public
name|AndDocIdSet
parameter_list|(
name|DocIdSet
index|[]
name|sets
parameter_list|)
block|{
name|this
operator|.
name|sets
operator|=
name|sets
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isCacheable
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
for|for
control|(
name|DocIdSet
name|set
range|:
name|sets
control|)
block|{
if|if
condition|(
operator|!
name|set
operator|.
name|isCacheable
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|bits
specifier|public
name|Bits
name|bits
parameter_list|()
throws|throws
name|IOException
block|{
name|Bits
index|[]
name|bits
init|=
operator|new
name|Bits
index|[
name|sets
operator|.
name|length
index|]
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
name|sets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bits
index|[
name|i
index|]
operator|=
name|sets
index|[
name|i
index|]
operator|.
name|bits
argument_list|()
expr_stmt|;
if|if
condition|(
name|bits
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
operator|new
name|AndBits
argument_list|(
name|bits
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
comment|// we try and be smart here, if we can iterate through docsets quickly, prefer to iterate
comment|// over them as much as possible, before actually going to "bits" based ones to check
name|List
argument_list|<
name|DocIdSet
argument_list|>
name|iterators
init|=
operator|new
name|ArrayList
argument_list|<
name|DocIdSet
argument_list|>
argument_list|(
name|sets
operator|.
name|length
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Bits
argument_list|>
name|bits
init|=
operator|new
name|ArrayList
argument_list|<
name|Bits
argument_list|>
argument_list|(
name|sets
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|DocIdSet
name|set
range|:
name|sets
control|)
block|{
if|if
condition|(
name|DocIdSets
operator|.
name|isFastIterator
argument_list|(
name|set
argument_list|)
condition|)
block|{
name|iterators
operator|.
name|add
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Bits
name|bit
init|=
name|set
operator|.
name|bits
argument_list|()
decl_stmt|;
if|if
condition|(
name|bit
operator|!=
literal|null
condition|)
block|{
name|bits
operator|.
name|add
argument_list|(
name|bit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|iterators
operator|.
name|add
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|bits
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|IteratorBasedIterator
argument_list|(
name|iterators
operator|.
name|toArray
argument_list|(
operator|new
name|DocIdSet
index|[
name|iterators
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|iterators
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|BitsDocIdSetIterator
argument_list|(
operator|new
name|AndBits
argument_list|(
name|bits
operator|.
name|toArray
argument_list|(
operator|new
name|Bits
index|[
name|bits
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|// combination of both..., first iterating over the "fast" ones, and then checking on the more
comment|// expensive ones
return|return
operator|new
name|BitsDocIdSetIterator
operator|.
name|FilteredIterator
argument_list|(
operator|new
name|IteratorBasedIterator
argument_list|(
name|iterators
operator|.
name|toArray
argument_list|(
operator|new
name|DocIdSet
index|[
name|iterators
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|,
operator|new
name|AndBits
argument_list|(
name|bits
operator|.
name|toArray
argument_list|(
operator|new
name|Bits
index|[
name|bits
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|class|AndBits
specifier|static
class|class
name|AndBits
implements|implements
name|Bits
block|{
DECL|field|bits
specifier|private
specifier|final
name|Bits
index|[]
name|bits
decl_stmt|;
DECL|method|AndBits
name|AndBits
parameter_list|(
name|Bits
index|[]
name|bits
parameter_list|)
block|{
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
for|for
control|(
name|Bits
name|bit
range|:
name|bits
control|)
block|{
if|if
condition|(
operator|!
name|bit
operator|.
name|get
argument_list|(
name|index
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|bits
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
return|;
block|}
block|}
DECL|class|IteratorBasedIterator
specifier|static
class|class
name|IteratorBasedIterator
extends|extends
name|DocIdSetIterator
block|{
DECL|field|lastReturn
name|int
name|lastReturn
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|iterators
specifier|private
name|DocIdSetIterator
index|[]
name|iterators
init|=
literal|null
decl_stmt|;
DECL|field|cost
specifier|private
specifier|final
name|long
name|cost
decl_stmt|;
DECL|method|IteratorBasedIterator
name|IteratorBasedIterator
parameter_list|(
name|DocIdSet
index|[]
name|sets
parameter_list|)
throws|throws
name|IOException
block|{
name|iterators
operator|=
operator|new
name|DocIdSetIterator
index|[
name|sets
operator|.
name|length
index|]
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
name|long
name|cost
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|DocIdSet
name|set
range|:
name|sets
control|)
block|{
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|lastReturn
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
expr_stmt|;
comment|// non matching
break|break;
block|}
else|else
block|{
name|DocIdSetIterator
name|dcit
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|dcit
operator|==
literal|null
condition|)
block|{
name|lastReturn
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
expr_stmt|;
comment|// non matching
break|break;
block|}
name|iterators
index|[
name|j
operator|++
index|]
operator|=
name|dcit
expr_stmt|;
name|cost
operator|=
name|Math
operator|.
name|min
argument_list|(
name|cost
argument_list|,
name|dcit
operator|.
name|cost
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
if|if
condition|(
name|lastReturn
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|lastReturn
operator|=
operator|(
name|iterators
operator|.
name|length
operator|>
literal|0
condition|?
operator|-
literal|1
else|:
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
operator|)
expr_stmt|;
block|}
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
name|lastReturn
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
specifier|final
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|lastReturn
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
name|DocIdSetIterator
name|dcit
init|=
name|iterators
index|[
literal|0
index|]
decl_stmt|;
name|int
name|target
init|=
name|dcit
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|iterators
operator|.
name|length
decl_stmt|;
name|int
name|skip
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|size
condition|)
block|{
if|if
condition|(
name|i
operator|!=
name|skip
condition|)
block|{
name|dcit
operator|=
name|iterators
index|[
name|i
index|]
expr_stmt|;
name|int
name|docid
init|=
name|dcit
operator|.
name|advance
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|docid
operator|>
name|target
condition|)
block|{
name|target
operator|=
name|docid
expr_stmt|;
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
name|skip
operator|=
name|i
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
continue|continue;
block|}
else|else
name|skip
operator|=
literal|0
expr_stmt|;
block|}
block|}
name|i
operator|++
expr_stmt|;
block|}
return|return
operator|(
name|lastReturn
operator|=
name|target
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
specifier|final
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
name|lastReturn
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
name|DocIdSetIterator
name|dcit
init|=
name|iterators
index|[
literal|0
index|]
decl_stmt|;
name|target
operator|=
name|dcit
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|iterators
operator|.
name|length
decl_stmt|;
name|int
name|skip
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|size
condition|)
block|{
if|if
condition|(
name|i
operator|!=
name|skip
condition|)
block|{
name|dcit
operator|=
name|iterators
index|[
name|i
index|]
expr_stmt|;
name|int
name|docid
init|=
name|dcit
operator|.
name|advance
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|docid
operator|>
name|target
condition|)
block|{
name|target
operator|=
name|docid
expr_stmt|;
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
name|skip
operator|=
name|i
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
continue|continue;
block|}
else|else
block|{
name|skip
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
name|i
operator|++
expr_stmt|;
block|}
return|return
operator|(
name|lastReturn
operator|=
name|target
operator|)
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
name|cost
return|;
block|}
block|}
block|}
end_class

end_unit

