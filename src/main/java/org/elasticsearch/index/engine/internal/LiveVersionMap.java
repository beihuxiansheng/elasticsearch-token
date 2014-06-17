begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
operator|.
name|internal
package|;
end_package

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
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ReferenceManager
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
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
import|;
end_import

begin_comment
comment|// TODO: use Lucene's LiveFieldValues, but we need to somehow extend it to handle SearcherManager changing, and to handle long-lasting (GC'd
end_comment

begin_comment
comment|// by time) tombstones
end_comment

begin_comment
comment|/** Maps _uid value to its version information. */
end_comment

begin_class
DECL|class|LiveVersionMap
class|class
name|LiveVersionMap
implements|implements
name|ReferenceManager
operator|.
name|RefreshListener
block|{
comment|// All writes go into here:
DECL|field|addsCurrent
specifier|private
specifier|volatile
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|VersionValue
argument_list|>
name|addsCurrent
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMapWithAggressiveConcurrency
argument_list|()
decl_stmt|;
comment|// Only used while refresh is running:
DECL|field|addsOld
specifier|private
specifier|volatile
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|VersionValue
argument_list|>
name|addsOld
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMapWithAggressiveConcurrency
argument_list|()
decl_stmt|;
comment|// Holds tombstones for deleted docs, expiring by their own schedule; not private so InternalEngine can prune:
DECL|field|deletes
specifier|private
specifier|final
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|VersionValue
argument_list|>
name|deletes
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMapWithAggressiveConcurrency
argument_list|()
decl_stmt|;
DECL|field|mgr
specifier|private
name|ReferenceManager
name|mgr
decl_stmt|;
DECL|method|setManager
specifier|public
name|void
name|setManager
parameter_list|(
name|ReferenceManager
name|newMgr
parameter_list|)
block|{
if|if
condition|(
name|mgr
operator|!=
literal|null
condition|)
block|{
name|mgr
operator|.
name|removeListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|mgr
operator|=
name|newMgr
expr_stmt|;
comment|// So we are notified when reopen starts and finishes
name|mgr
operator|.
name|addListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|beforeRefresh
specifier|public
name|void
name|beforeRefresh
parameter_list|()
throws|throws
name|IOException
block|{
name|addsOld
operator|=
name|addsCurrent
expr_stmt|;
comment|// Start sending all updates after this point to the new
comment|// map.  While reopen is running, any lookup will first
comment|// try this new map, then fallback to old, then to the
comment|// current searcher:
name|addsCurrent
operator|=
name|ConcurrentCollections
operator|.
name|newConcurrentMapWithAggressiveConcurrency
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|afterRefresh
specifier|public
name|void
name|afterRefresh
parameter_list|(
name|boolean
name|didRefresh
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Now drop all the old values because they are now
comment|// visible via the searcher that was just opened; if
comment|// didRefresh is false, it's possible old has some
comment|// entries in it, which is fine: it means they were
comment|// actually already included in the previously opened
comment|// reader.  So we can safely clear old here:
name|addsOld
operator|=
name|ConcurrentCollections
operator|.
name|newConcurrentMapWithAggressiveConcurrency
argument_list|()
expr_stmt|;
block|}
comment|/** Caller has a lock, so that this uid will not be concurrently added/deleted by another thread. */
DECL|method|getUnderLock
specifier|public
name|VersionValue
name|getUnderLock
parameter_list|(
name|BytesRef
name|uid
parameter_list|)
block|{
comment|// First try to get the "live" value:
name|VersionValue
name|value
init|=
name|addsCurrent
operator|.
name|get
argument_list|(
name|uid
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
name|value
operator|=
name|addsOld
operator|.
name|get
argument_list|(
name|uid
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
name|value
operator|=
name|deletes
operator|.
name|get
argument_list|(
name|uid
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** Adds this uid/version to the pending adds map. */
DECL|method|putUnderLock
specifier|public
name|void
name|putUnderLock
parameter_list|(
name|BytesRef
name|uid
parameter_list|,
name|VersionValue
name|version
parameter_list|)
block|{
name|deletes
operator|.
name|remove
argument_list|(
name|uid
argument_list|)
expr_stmt|;
name|addsCurrent
operator|.
name|put
argument_list|(
name|uid
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
comment|/** Adds this uid/version to the pending deletes map. */
DECL|method|putDeleteUnderLock
specifier|public
name|void
name|putDeleteUnderLock
parameter_list|(
name|BytesRef
name|uid
parameter_list|,
name|VersionValue
name|version
parameter_list|)
block|{
name|addsCurrent
operator|.
name|remove
argument_list|(
name|uid
argument_list|)
expr_stmt|;
name|addsOld
operator|.
name|remove
argument_list|(
name|uid
argument_list|)
expr_stmt|;
name|deletes
operator|.
name|put
argument_list|(
name|uid
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the current deleted version for this uid. */
DECL|method|getDeleteUnderLock
specifier|public
name|VersionValue
name|getDeleteUnderLock
parameter_list|(
name|BytesRef
name|uid
parameter_list|)
block|{
return|return
name|deletes
operator|.
name|get
argument_list|(
name|uid
argument_list|)
return|;
block|}
comment|/** Removes this uid from the pending deletes map. */
DECL|method|removeDeleteUnderLock
specifier|public
name|void
name|removeDeleteUnderLock
parameter_list|(
name|BytesRef
name|uid
parameter_list|)
block|{
name|deletes
operator|.
name|remove
argument_list|(
name|uid
argument_list|)
expr_stmt|;
block|}
comment|/** Iterates over all pending deletions. */
DECL|method|getAllDeletes
specifier|public
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|BytesRef
argument_list|,
name|VersionValue
argument_list|>
argument_list|>
name|getAllDeletes
parameter_list|()
block|{
return|return
name|deletes
operator|.
name|entrySet
argument_list|()
return|;
block|}
comment|/** Called when this index is closed. */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|addsCurrent
operator|.
name|clear
argument_list|()
expr_stmt|;
name|addsOld
operator|.
name|clear
argument_list|()
expr_stmt|;
name|deletes
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|mgr
operator|!=
literal|null
condition|)
block|{
name|mgr
operator|.
name|removeListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|mgr
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

