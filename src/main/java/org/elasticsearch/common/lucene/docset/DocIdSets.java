begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|OpenBitSetIterator
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
name|Nullable
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|DocIdSets
specifier|public
class|class
name|DocIdSets
block|{
DECL|method|sizeInBytes
specifier|public
specifier|static
name|long
name|sizeInBytes
parameter_list|(
name|DocIdSet
name|docIdSet
parameter_list|)
block|{
if|if
condition|(
name|docIdSet
operator|instanceof
name|FixedBitSet
condition|)
block|{
return|return
operator|(
operator|(
name|FixedBitSet
operator|)
name|docIdSet
operator|)
operator|.
name|getBits
argument_list|()
operator|.
name|length
operator|*
literal|8
operator|+
literal|16
return|;
block|}
comment|// only for empty ones and unknowns...
return|return
literal|1
return|;
block|}
comment|/**      * Is it an empty {@link DocIdSet}?      */
DECL|method|isEmpty
specifier|public
specifier|static
name|boolean
name|isEmpty
parameter_list|(
annotation|@
name|Nullable
name|DocIdSet
name|set
parameter_list|)
block|{
return|return
name|set
operator|==
literal|null
operator|||
name|set
operator|==
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
comment|/**      * Is {@link org.apache.lucene.search.DocIdSetIterator} implemented in a "fast" manner.      * For example, it does not ends up iterating one doc at a time check for its "value".      */
DECL|method|isFastIterator
specifier|public
specifier|static
name|boolean
name|isFastIterator
parameter_list|(
name|DocIdSet
name|set
parameter_list|)
block|{
return|return
name|set
operator|instanceof
name|FixedBitSet
return|;
block|}
comment|/**      * Is {@link org.apache.lucene.search.DocIdSetIterator} implemented in a "fast" manner.      * For example, it does not ends up iterating one doc at a time check for its "value".      */
DECL|method|isFastIterator
specifier|public
specifier|static
name|boolean
name|isFastIterator
parameter_list|(
name|DocIdSetIterator
name|iterator
parameter_list|)
block|{
comment|// this is the iterator in the FixedBitSet.
return|return
name|iterator
operator|instanceof
name|OpenBitSetIterator
return|;
block|}
comment|/**      * Converts to a cacheable {@link DocIdSet}      *<p/>      * Note, we don't use {@link org.apache.lucene.search.DocIdSet#isCacheable()} because execution      * might be expensive even if its cacheable (i.e. not going back to the reader to execute). We effectively      * always either return {@link DocIdSet#EMPTY_DOCIDSET} or {@link FixedBitSet}.      */
DECL|method|toCacheable
specifier|public
specifier|static
name|DocIdSet
name|toCacheable
parameter_list|(
name|AtomicReader
name|reader
parameter_list|,
annotation|@
name|Nullable
name|DocIdSet
name|set
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
if|if
condition|(
name|set
operator|==
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
condition|)
block|{
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
name|DocIdSetIterator
name|it
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|==
literal|null
condition|)
block|{
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
name|int
name|doc
init|=
name|it
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
if|if
condition|(
name|set
operator|instanceof
name|FixedBitSet
condition|)
block|{
return|return
name|set
return|;
block|}
name|FixedBitSet
name|fixedBitSet
init|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
do|do
block|{
name|fixedBitSet
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
name|it
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
do|;
return|return
name|fixedBitSet
return|;
block|}
comment|/**      * Gets a set to bits.      */
DECL|method|toSafeBits
specifier|public
specifier|static
name|Bits
name|toSafeBits
parameter_list|(
name|AtomicReader
name|reader
parameter_list|,
annotation|@
name|Nullable
name|DocIdSet
name|set
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
name|Bits
name|bits
init|=
name|set
operator|.
name|bits
argument_list|()
decl_stmt|;
if|if
condition|(
name|bits
operator|!=
literal|null
condition|)
block|{
return|return
name|bits
return|;
block|}
name|DocIdSetIterator
name|iterator
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iterator
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
return|return
name|toFixedBitSet
argument_list|(
name|iterator
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Creates a {@link FixedBitSet} from an iterator.      */
DECL|method|toFixedBitSet
specifier|public
specifier|static
name|FixedBitSet
name|toFixedBitSet
parameter_list|(
name|DocIdSetIterator
name|iterator
parameter_list|,
name|int
name|numBits
parameter_list|)
throws|throws
name|IOException
block|{
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|set
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
block|}
end_class

end_unit

