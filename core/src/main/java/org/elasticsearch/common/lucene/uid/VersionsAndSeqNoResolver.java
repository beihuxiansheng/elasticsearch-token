begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.uid
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|uid
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
name|NumericDocValues
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
name|util
operator|.
name|CloseableThreadLocal
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
name|concurrent
operator|.
name|ConcurrentCollections
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
name|mapper
operator|.
name|SeqNoFieldMapper
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
name|List
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
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|uid
operator|.
name|Versions
operator|.
name|NOT_FOUND
import|;
end_import

begin_comment
comment|/** Utility class to resolve the Lucene doc ID, version, seqNo and primaryTerms for a given uid. */
end_comment

begin_class
DECL|class|VersionsAndSeqNoResolver
specifier|public
specifier|final
class|class
name|VersionsAndSeqNoResolver
block|{
DECL|field|lookupStates
specifier|static
specifier|final
name|ConcurrentMap
argument_list|<
name|IndexReader
operator|.
name|CacheKey
argument_list|,
name|CloseableThreadLocal
argument_list|<
name|PerThreadIDVersionAndSeqNoLookup
index|[]
argument_list|>
argument_list|>
name|lookupStates
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMapWithAggressiveConcurrency
argument_list|()
decl_stmt|;
comment|// Evict this reader from lookupStates once it's closed:
DECL|field|removeLookupState
specifier|private
specifier|static
specifier|final
name|IndexReader
operator|.
name|ClosedListener
name|removeLookupState
init|=
name|key
lambda|->
block|{
name|CloseableThreadLocal
argument_list|<
name|PerThreadIDVersionAndSeqNoLookup
index|[]
argument_list|>
name|ctl
init|=
name|lookupStates
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|ctl
operator|!=
literal|null
condition|)
block|{
name|ctl
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
DECL|method|getLookupState
specifier|private
specifier|static
name|PerThreadIDVersionAndSeqNoLookup
index|[]
name|getLookupState
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|uidField
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We cache on the top level
comment|// This means cache entries have a shorter lifetime, maybe as low as 1s with the
comment|// default refresh interval and a steady indexing rate, but on the other hand it
comment|// proved to be cheaper than having to perform a CHM and a TL get for every segment.
comment|// See https://github.com/elastic/elasticsearch/pull/19856.
name|IndexReader
operator|.
name|CacheHelper
name|cacheHelper
init|=
name|reader
operator|.
name|getReaderCacheHelper
argument_list|()
decl_stmt|;
name|CloseableThreadLocal
argument_list|<
name|PerThreadIDVersionAndSeqNoLookup
index|[]
argument_list|>
name|ctl
init|=
name|lookupStates
operator|.
name|get
argument_list|(
name|cacheHelper
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ctl
operator|==
literal|null
condition|)
block|{
comment|// First time we are seeing this reader's core; make a new CTL:
name|ctl
operator|=
operator|new
name|CloseableThreadLocal
argument_list|<>
argument_list|()
expr_stmt|;
name|CloseableThreadLocal
argument_list|<
name|PerThreadIDVersionAndSeqNoLookup
index|[]
argument_list|>
name|other
init|=
name|lookupStates
operator|.
name|putIfAbsent
argument_list|(
name|cacheHelper
operator|.
name|getKey
argument_list|()
argument_list|,
name|ctl
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
comment|// Our CTL won, we must remove it when the reader is closed:
name|cacheHelper
operator|.
name|addClosedListener
argument_list|(
name|removeLookupState
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Another thread beat us to it: just use their CTL:
name|ctl
operator|=
name|other
expr_stmt|;
block|}
block|}
name|PerThreadIDVersionAndSeqNoLookup
index|[]
name|lookupState
init|=
name|ctl
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|lookupState
operator|==
literal|null
condition|)
block|{
name|lookupState
operator|=
operator|new
name|PerThreadIDVersionAndSeqNoLookup
index|[
name|reader
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
name|LeafReaderContext
name|leaf
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|lookupState
index|[
name|leaf
operator|.
name|ord
index|]
operator|=
operator|new
name|PerThreadIDVersionAndSeqNoLookup
argument_list|(
name|leaf
operator|.
name|reader
argument_list|()
argument_list|,
name|uidField
argument_list|)
expr_stmt|;
block|}
name|ctl
operator|.
name|set
argument_list|(
name|lookupState
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lookupState
operator|.
name|length
operator|!=
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Mismatched numbers of leaves: "
operator|+
name|lookupState
operator|.
name|length
operator|+
literal|" != "
operator|+
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|lookupState
operator|.
name|length
operator|>
literal|0
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|lookupState
index|[
literal|0
index|]
operator|.
name|uidField
argument_list|,
name|uidField
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Index does not consistently use the same uid field: ["
operator|+
name|uidField
operator|+
literal|"] != ["
operator|+
name|lookupState
index|[
literal|0
index|]
operator|.
name|uidField
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|lookupState
return|;
block|}
DECL|method|VersionsAndSeqNoResolver
specifier|private
name|VersionsAndSeqNoResolver
parameter_list|()
block|{     }
comment|/** Wraps an {@link LeafReaderContext}, a doc ID<b>relative to the context doc base</b> and a version. */
DECL|class|DocIdAndVersion
specifier|public
specifier|static
class|class
name|DocIdAndVersion
block|{
DECL|field|docId
specifier|public
specifier|final
name|int
name|docId
decl_stmt|;
DECL|field|version
specifier|public
specifier|final
name|long
name|version
decl_stmt|;
DECL|field|context
specifier|public
specifier|final
name|LeafReaderContext
name|context
decl_stmt|;
DECL|method|DocIdAndVersion
name|DocIdAndVersion
parameter_list|(
name|int
name|docId
parameter_list|,
name|long
name|version
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
block|}
comment|/** Wraps an {@link LeafReaderContext}, a doc ID<b>relative to the context doc base</b> and a seqNo. */
DECL|class|DocIdAndSeqNo
specifier|public
specifier|static
class|class
name|DocIdAndSeqNo
block|{
DECL|field|docId
specifier|public
specifier|final
name|int
name|docId
decl_stmt|;
DECL|field|seqNo
specifier|public
specifier|final
name|long
name|seqNo
decl_stmt|;
DECL|field|context
specifier|public
specifier|final
name|LeafReaderContext
name|context
decl_stmt|;
DECL|method|DocIdAndSeqNo
name|DocIdAndSeqNo
parameter_list|(
name|int
name|docId
parameter_list|,
name|long
name|seqNo
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
name|this
operator|.
name|seqNo
operator|=
name|seqNo
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
block|}
comment|/**      * Load the internal doc ID and version for the uid from the reader, returning<ul>      *<li>null if the uid wasn't found,      *<li>a doc ID and a version otherwise      *</ul>      */
DECL|method|loadDocIdAndVersion
specifier|public
specifier|static
name|DocIdAndVersion
name|loadDocIdAndVersion
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|PerThreadIDVersionAndSeqNoLookup
index|[]
name|lookups
init|=
name|getLookupState
argument_list|(
name|reader
argument_list|,
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
comment|// iterate backwards to optimize for the frequently updated documents
comment|// which are likely to be in the last segments
for|for
control|(
name|int
name|i
init|=
name|leaves
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
specifier|final
name|LeafReaderContext
name|leaf
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|PerThreadIDVersionAndSeqNoLookup
name|lookup
init|=
name|lookups
index|[
name|leaf
operator|.
name|ord
index|]
decl_stmt|;
name|DocIdAndVersion
name|result
init|=
name|lookup
operator|.
name|lookupVersion
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
name|leaf
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Load the internal doc ID and sequence number for the uid from the reader, returning<ul>      *<li>null if the uid wasn't found,      *<li>a doc ID and the associated seqNo otherwise      *</ul>      */
DECL|method|loadDocIdAndSeqNo
specifier|public
specifier|static
name|DocIdAndSeqNo
name|loadDocIdAndSeqNo
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|PerThreadIDVersionAndSeqNoLookup
index|[]
name|lookups
init|=
name|getLookupState
argument_list|(
name|reader
argument_list|,
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
comment|// iterate backwards to optimize for the frequently updated documents
comment|// which are likely to be in the last segments
for|for
control|(
name|int
name|i
init|=
name|leaves
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
specifier|final
name|LeafReaderContext
name|leaf
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|PerThreadIDVersionAndSeqNoLookup
name|lookup
init|=
name|lookups
index|[
name|leaf
operator|.
name|ord
index|]
decl_stmt|;
name|DocIdAndSeqNo
name|result
init|=
name|lookup
operator|.
name|lookupSeqNo
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
name|leaf
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Load the primaryTerm associated with the given {@link DocIdAndSeqNo}      */
DECL|method|loadPrimaryTerm
specifier|public
specifier|static
name|long
name|loadPrimaryTerm
parameter_list|(
name|DocIdAndSeqNo
name|docIdAndSeqNo
parameter_list|,
name|String
name|uidField
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericDocValues
name|primaryTerms
init|=
name|docIdAndSeqNo
operator|.
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
name|SeqNoFieldMapper
operator|.
name|PRIMARY_TERM_NAME
argument_list|)
decl_stmt|;
name|long
name|result
decl_stmt|;
if|if
condition|(
name|primaryTerms
operator|!=
literal|null
operator|&&
name|primaryTerms
operator|.
name|advanceExact
argument_list|(
name|docIdAndSeqNo
operator|.
name|docId
argument_list|)
condition|)
block|{
name|result
operator|=
name|primaryTerms
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
literal|0
expr_stmt|;
block|}
assert|assert
name|result
operator|>
literal|0
operator|:
literal|"should always resolve a primary term for a resolved sequence number. primary_term ["
operator|+
name|result
operator|+
literal|"]"
operator|+
literal|" docId ["
operator|+
name|docIdAndSeqNo
operator|.
name|docId
operator|+
literal|"] seqNo ["
operator|+
name|docIdAndSeqNo
operator|.
name|seqNo
operator|+
literal|"]"
assert|;
return|return
name|result
return|;
block|}
comment|/**      * Load the version for the uid from the reader, returning<ul>      *<li>{@link Versions#NOT_FOUND} if no matching doc exists,      *<li>the version associated with the provided uid otherwise      *</ul>      */
DECL|method|loadVersion
specifier|public
specifier|static
name|long
name|loadVersion
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocIdAndVersion
name|docIdAndVersion
init|=
name|loadDocIdAndVersion
argument_list|(
name|reader
argument_list|,
name|term
argument_list|)
decl_stmt|;
return|return
name|docIdAndVersion
operator|==
literal|null
condition|?
name|NOT_FOUND
else|:
name|docIdAndVersion
operator|.
name|version
return|;
block|}
block|}
end_class

end_unit

