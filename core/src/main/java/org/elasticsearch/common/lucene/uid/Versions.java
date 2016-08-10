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
name|LeafReader
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
name|LeafReader
operator|.
name|CoreClosedListener
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
name|UidFieldMapper
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
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_comment
comment|/** Utility class to resolve the Lucene doc ID and version for a given uid. */
end_comment

begin_class
DECL|class|Versions
specifier|public
class|class
name|Versions
block|{
comment|/** used to indicate the write operation should succeed regardless of current version **/
DECL|field|MATCH_ANY
specifier|public
specifier|static
specifier|final
name|long
name|MATCH_ANY
init|=
operator|-
literal|3L
decl_stmt|;
comment|/** indicates that the current document was not found in lucene and in the version map */
DECL|field|NOT_FOUND
specifier|public
specifier|static
specifier|final
name|long
name|NOT_FOUND
init|=
operator|-
literal|1L
decl_stmt|;
comment|// -2 was used for docs that can be found in the index but do not have a version
comment|/**      * used to indicate that the write operation should be executed if the document is currently deleted      * i.e., not found in the index and/or found as deleted (with version) in the version map      */
DECL|field|MATCH_DELETED
specifier|public
specifier|static
specifier|final
name|long
name|MATCH_DELETED
init|=
operator|-
literal|4L
decl_stmt|;
comment|// TODO: is there somewhere else we can store these?
DECL|field|lookupStates
specifier|static
specifier|final
name|ConcurrentMap
argument_list|<
name|Object
argument_list|,
name|CloseableThreadLocal
argument_list|<
name|PerThreadIDAndVersionLookup
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
name|CoreClosedListener
name|removeLookupState
init|=
operator|new
name|CoreClosedListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onClose
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|CloseableThreadLocal
argument_list|<
name|PerThreadIDAndVersionLookup
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
block|}
decl_stmt|;
DECL|method|getLookupState
specifier|private
specifier|static
name|PerThreadIDAndVersionLookup
name|getLookupState
parameter_list|(
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|key
init|=
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
decl_stmt|;
name|CloseableThreadLocal
argument_list|<
name|PerThreadIDAndVersionLookup
argument_list|>
name|ctl
init|=
name|lookupStates
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|ctl
operator|==
literal|null
condition|)
block|{
comment|// First time we are seeing this reader's core; make a
comment|// new CTL:
name|ctl
operator|=
operator|new
name|CloseableThreadLocal
argument_list|<>
argument_list|()
expr_stmt|;
name|CloseableThreadLocal
argument_list|<
name|PerThreadIDAndVersionLookup
argument_list|>
name|other
init|=
name|lookupStates
operator|.
name|putIfAbsent
argument_list|(
name|key
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
comment|// Our CTL won, we must remove it when the
comment|// core is closed:
name|reader
operator|.
name|addCoreClosedListener
argument_list|(
name|removeLookupState
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Another thread beat us to it: just use
comment|// their CTL:
name|ctl
operator|=
name|other
expr_stmt|;
block|}
block|}
name|PerThreadIDAndVersionLookup
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
name|PerThreadIDAndVersionLookup
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|ctl
operator|.
name|set
argument_list|(
name|lookupState
argument_list|)
expr_stmt|;
block|}
return|return
name|lookupState
return|;
block|}
DECL|method|Versions
specifier|private
name|Versions
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
specifier|public
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
assert|assert
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
assert|;
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
if|if
condition|(
name|leaves
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
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
name|LeafReaderContext
name|context
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|LeafReader
name|leaf
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|PerThreadIDAndVersionLookup
name|lookup
init|=
name|getLookupState
argument_list|(
name|leaf
argument_list|)
decl_stmt|;
name|DocIdAndVersion
name|result
init|=
name|lookup
operator|.
name|lookup
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
name|leaf
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
name|context
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
comment|/**      * Load the version for the uid from the reader, returning<ul>      *<li>{@link #NOT_FOUND} if no matching doc exists,      *<li>the version associated with the provided uid otherwise      *</ul>      */
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

