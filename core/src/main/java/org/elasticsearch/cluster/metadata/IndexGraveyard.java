begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|Diff
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
name|ParseField
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
name|ParseFieldMatcher
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
name|ParseFieldMatcherSupplier
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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
name|joda
operator|.
name|Joda
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
name|settings
operator|.
name|Setting
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
name|settings
operator|.
name|Settings
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
name|xcontent
operator|.
name|ObjectParser
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
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentParser
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
name|Index
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiFunction
import|;
end_import

begin_comment
comment|/**  * A collection of tombstones for explicitly marking indices as deleted in the cluster state.  *  * The cluster state contains a list of index tombstones for indices that have been  * deleted in the cluster.  Because cluster states are processed asynchronously by  * nodes and a node could be removed from the cluster for a period of time, the  * tombstones remain in the cluster state for a fixed period of time, after which  * they are purged.  */
end_comment

begin_class
DECL|class|IndexGraveyard
specifier|public
specifier|final
class|class
name|IndexGraveyard
implements|implements
name|MetaData
operator|.
name|Custom
block|{
comment|/**      * Setting for the maximum tombstones allowed in the cluster state;      * prevents the cluster state size from exploding too large, but it opens the      * very unlikely risk that if there are greater than MAX_TOMBSTONES index      * deletions while a node was offline, when it comes back online, it will have      * missed index deletions that it may need to process.      */
DECL|field|SETTING_MAX_TOMBSTONES
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|SETTING_MAX_TOMBSTONES
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"cluster.indices.tombstones.size"
argument_list|,
literal|500
argument_list|,
comment|// the default maximum number of tombstones
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|PROTO
specifier|public
specifier|static
specifier|final
name|IndexGraveyard
name|PROTO
init|=
operator|new
name|IndexGraveyard
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"index-graveyard"
decl_stmt|;
DECL|field|TOMBSTONES_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|TOMBSTONES_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"tombstones"
argument_list|)
decl_stmt|;
DECL|field|GRAVEYARD_PARSER
specifier|private
specifier|static
specifier|final
name|ObjectParser
argument_list|<
name|List
argument_list|<
name|Tombstone
argument_list|>
argument_list|,
name|ParseFieldMatcherSupplier
argument_list|>
name|GRAVEYARD_PARSER
decl_stmt|;
static|static
block|{
name|GRAVEYARD_PARSER
operator|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
literal|"index_graveyard"
argument_list|,
name|ArrayList
operator|::
operator|new
argument_list|)
expr_stmt|;
name|GRAVEYARD_PARSER
operator|.
name|declareObjectArray
argument_list|(
name|List
operator|::
name|addAll
argument_list|,
name|Tombstone
operator|.
name|getParser
argument_list|()
argument_list|,
name|TOMBSTONES_FIELD
argument_list|)
expr_stmt|;
block|}
DECL|field|tombstones
specifier|private
specifier|final
name|List
argument_list|<
name|Tombstone
argument_list|>
name|tombstones
decl_stmt|;
DECL|method|IndexGraveyard
specifier|private
name|IndexGraveyard
parameter_list|(
specifier|final
name|List
argument_list|<
name|Tombstone
argument_list|>
name|list
parameter_list|)
block|{
assert|assert
name|list
operator|!=
literal|null
assert|;
name|tombstones
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
DECL|method|IndexGraveyard
specifier|private
name|IndexGraveyard
parameter_list|(
specifier|final
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|queueSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Tombstone
argument_list|>
name|tombstones
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|queueSize
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
name|queueSize
condition|;
name|i
operator|++
control|)
block|{
name|tombstones
operator|.
name|add
argument_list|(
operator|new
name|Tombstone
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|tombstones
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|tombstones
argument_list|)
expr_stmt|;
block|}
DECL|method|fromStream
specifier|public
specifier|static
name|IndexGraveyard
name|fromStream
parameter_list|(
specifier|final
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IndexGraveyard
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|context
specifier|public
name|EnumSet
argument_list|<
name|MetaData
operator|.
name|XContentContext
argument_list|>
name|context
parameter_list|()
block|{
return|return
name|MetaData
operator|.
name|API_AND_GATEWAY
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|(
name|obj
operator|instanceof
name|IndexGraveyard
operator|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|tombstones
argument_list|,
operator|(
operator|(
name|IndexGraveyard
operator|)
name|obj
operator|)
operator|.
name|tombstones
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|tombstones
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**      * Get the current unmodifiable index tombstone list.      */
DECL|method|getTombstones
specifier|public
name|List
argument_list|<
name|Tombstone
argument_list|>
name|getTombstones
parameter_list|()
block|{
return|return
name|tombstones
return|;
block|}
comment|/**      * Returns true if the graveyard contains a tombstone for the given index.      */
DECL|method|containsIndex
specifier|public
name|boolean
name|containsIndex
parameter_list|(
specifier|final
name|Index
name|index
parameter_list|)
block|{
for|for
control|(
name|Tombstone
name|tombstone
range|:
name|tombstones
control|)
block|{
if|if
condition|(
name|tombstone
operator|.
name|getIndex
argument_list|()
operator|.
name|equals
argument_list|(
name|index
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
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
specifier|final
name|XContentBuilder
name|builder
parameter_list|,
specifier|final
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|TOMBSTONES_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Tombstone
name|tombstone
range|:
name|tombstones
control|)
block|{
name|tombstone
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|endArray
argument_list|()
return|;
block|}
DECL|method|fromXContent
specifier|public
name|IndexGraveyard
name|fromXContent
parameter_list|(
specifier|final
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IndexGraveyard
argument_list|(
name|GRAVEYARD_PARSER
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
parameter_list|()
lambda|->
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
argument_list|)
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
return|return
literal|"IndexGraveyard["
operator|+
name|tombstones
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
specifier|final
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|tombstones
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Tombstone
name|tombstone
range|:
name|tombstones
control|)
block|{
name|tombstone
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|IndexGraveyard
name|readFrom
parameter_list|(
specifier|final
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IndexGraveyard
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|diff
specifier|public
name|Diff
argument_list|<
name|MetaData
operator|.
name|Custom
argument_list|>
name|diff
parameter_list|(
specifier|final
name|MetaData
operator|.
name|Custom
name|previous
parameter_list|)
block|{
return|return
operator|new
name|IndexGraveyardDiff
argument_list|(
operator|(
name|IndexGraveyard
operator|)
name|previous
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readDiffFrom
specifier|public
name|Diff
argument_list|<
name|MetaData
operator|.
name|Custom
argument_list|>
name|readDiffFrom
parameter_list|(
specifier|final
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IndexGraveyardDiff
argument_list|(
name|in
argument_list|)
return|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|IndexGraveyard
operator|.
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|IndexGraveyard
operator|.
name|Builder
argument_list|()
return|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|IndexGraveyard
operator|.
name|Builder
name|builder
parameter_list|(
specifier|final
name|IndexGraveyard
name|graveyard
parameter_list|)
block|{
return|return
operator|new
name|IndexGraveyard
operator|.
name|Builder
argument_list|(
name|graveyard
argument_list|)
return|;
block|}
comment|/**      * A class to build an IndexGraveyard.      */
DECL|class|Builder
specifier|public
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|tombstones
specifier|private
name|List
argument_list|<
name|Tombstone
argument_list|>
name|tombstones
decl_stmt|;
DECL|field|numPurged
specifier|private
name|int
name|numPurged
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentTime
specifier|private
specifier|final
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|method|Builder
specifier|private
name|Builder
parameter_list|()
block|{
name|tombstones
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|Builder
specifier|private
name|Builder
parameter_list|(
name|IndexGraveyard
name|that
parameter_list|)
block|{
name|tombstones
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|that
operator|.
name|getTombstones
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**          * A copy of the current tombstones in the builder.          */
DECL|method|tombstones
specifier|public
name|List
argument_list|<
name|Tombstone
argument_list|>
name|tombstones
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|tombstones
argument_list|)
return|;
block|}
comment|/**          * Add a deleted index to the list of tombstones in the cluster state.          */
DECL|method|addTombstone
specifier|public
name|Builder
name|addTombstone
parameter_list|(
specifier|final
name|Index
name|index
parameter_list|)
block|{
name|tombstones
operator|.
name|add
argument_list|(
operator|new
name|Tombstone
argument_list|(
name|index
argument_list|,
name|currentTime
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Add a set of deleted indexes to the list of tombstones in the cluster state.          */
DECL|method|addTombstones
specifier|public
name|Builder
name|addTombstones
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|Index
argument_list|>
name|indices
parameter_list|)
block|{
for|for
control|(
name|Index
name|index
range|:
name|indices
control|)
block|{
name|addTombstone
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**          * Add a list of tombstones to the graveyard.          */
DECL|method|addBuiltTombstones
name|Builder
name|addBuiltTombstones
parameter_list|(
specifier|final
name|List
argument_list|<
name|Tombstone
argument_list|>
name|tombstones
parameter_list|)
block|{
name|this
operator|.
name|tombstones
operator|.
name|addAll
argument_list|(
name|tombstones
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Get the number of tombstones that were purged.  This should *only* be called          * after build() has been called.          */
DECL|method|getNumPurged
specifier|public
name|int
name|getNumPurged
parameter_list|()
block|{
assert|assert
name|numPurged
operator|!=
operator|-
literal|1
assert|;
return|return
name|numPurged
return|;
block|}
comment|/**          * Purge tombstone entries.  Returns the number of entries that were purged.          *          * Tombstones are purged if the number of tombstones in the list          * is greater than the input parameter of maximum allowed tombstones.          * Tombstones are purged until the list is equal to the maximum allowed.          */
DECL|method|purge
specifier|private
name|int
name|purge
parameter_list|(
specifier|final
name|int
name|maxTombstones
parameter_list|)
block|{
name|int
name|count
init|=
name|tombstones
argument_list|()
operator|.
name|size
argument_list|()
operator|-
name|maxTombstones
decl_stmt|;
if|if
condition|(
name|count
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|tombstones
operator|=
name|tombstones
operator|.
name|subList
argument_list|(
name|count
argument_list|,
name|tombstones
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|count
return|;
block|}
DECL|method|build
specifier|public
name|IndexGraveyard
name|build
parameter_list|()
block|{
return|return
name|build
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
return|;
block|}
DECL|method|build
specifier|public
name|IndexGraveyard
name|build
parameter_list|(
specifier|final
name|Settings
name|settings
parameter_list|)
block|{
comment|// first, purge the necessary amount of entries
name|numPurged
operator|=
name|purge
argument_list|(
name|SETTING_MAX_TOMBSTONES
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|IndexGraveyard
argument_list|(
name|tombstones
argument_list|)
return|;
block|}
block|}
comment|/**      * A class representing a diff of two IndexGraveyard objects.      */
DECL|class|IndexGraveyardDiff
specifier|public
specifier|static
specifier|final
class|class
name|IndexGraveyardDiff
implements|implements
name|Diff
argument_list|<
name|MetaData
operator|.
name|Custom
argument_list|>
block|{
DECL|field|added
specifier|private
specifier|final
name|List
argument_list|<
name|Tombstone
argument_list|>
name|added
decl_stmt|;
DECL|field|removedCount
specifier|private
specifier|final
name|int
name|removedCount
decl_stmt|;
DECL|method|IndexGraveyardDiff
name|IndexGraveyardDiff
parameter_list|(
specifier|final
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|added
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|in
operator|.
name|readList
argument_list|(
parameter_list|(
name|streamInput
parameter_list|)
lambda|->
operator|new
name|Tombstone
argument_list|(
name|streamInput
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|removedCount
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
DECL|method|IndexGraveyardDiff
name|IndexGraveyardDiff
parameter_list|(
specifier|final
name|IndexGraveyard
name|previous
parameter_list|,
specifier|final
name|IndexGraveyard
name|current
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Tombstone
argument_list|>
name|previousTombstones
init|=
name|previous
operator|.
name|tombstones
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Tombstone
argument_list|>
name|currentTombstones
init|=
name|current
operator|.
name|tombstones
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Tombstone
argument_list|>
name|added
decl_stmt|;
specifier|final
name|int
name|removed
decl_stmt|;
if|if
condition|(
name|previousTombstones
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// nothing will have been removed, and all entries in current are new
name|added
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|currentTombstones
argument_list|)
expr_stmt|;
name|removed
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentTombstones
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// nothing will have been added, and all entries in previous are removed
name|added
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
name|removed
operator|=
name|previousTombstones
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// look through the back, starting from the end, for added tombstones
specifier|final
name|Tombstone
name|lastAddedTombstone
init|=
name|previousTombstones
operator|.
name|get
argument_list|(
name|previousTombstones
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|addedIndex
init|=
name|currentTombstones
operator|.
name|lastIndexOf
argument_list|(
name|lastAddedTombstone
argument_list|)
decl_stmt|;
if|if
condition|(
name|addedIndex
operator|<
name|currentTombstones
operator|.
name|size
argument_list|()
condition|)
block|{
name|added
operator|=
name|currentTombstones
operator|.
name|subList
argument_list|(
name|addedIndex
operator|+
literal|1
argument_list|,
name|currentTombstones
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|added
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
comment|// look from the front for the removed tombstones
specifier|final
name|Tombstone
name|firstTombstone
init|=
name|currentTombstones
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
name|previousTombstones
operator|.
name|indexOf
argument_list|(
name|firstTombstone
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
comment|// the first tombstone in the current list wasn't found in the previous list,
comment|// which means all tombstones from the previous list have been deleted.
assert|assert
name|added
operator|.
name|equals
argument_list|(
name|currentTombstones
argument_list|)
assert|;
comment|// all previous are removed, so the current list must be the same as the added
name|idx
operator|=
name|previousTombstones
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|removed
operator|=
name|idx
expr_stmt|;
block|}
name|this
operator|.
name|added
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|added
argument_list|)
expr_stmt|;
name|this
operator|.
name|removedCount
operator|=
name|removed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
specifier|final
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeList
argument_list|(
name|added
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|removedCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply
specifier|public
name|IndexGraveyard
name|apply
parameter_list|(
specifier|final
name|MetaData
operator|.
name|Custom
name|previous
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|IndexGraveyard
name|old
init|=
operator|(
name|IndexGraveyard
operator|)
name|previous
decl_stmt|;
if|if
condition|(
name|removedCount
operator|>
name|old
operator|.
name|tombstones
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"IndexGraveyardDiff cannot remove ["
operator|+
name|removedCount
operator|+
literal|"] entries from ["
operator|+
name|old
operator|.
name|tombstones
operator|.
name|size
argument_list|()
operator|+
literal|"] tombstones."
argument_list|)
throw|;
block|}
specifier|final
name|List
argument_list|<
name|Tombstone
argument_list|>
name|newTombstones
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|old
operator|.
name|tombstones
operator|.
name|subList
argument_list|(
name|removedCount
argument_list|,
name|old
operator|.
name|tombstones
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Tombstone
name|tombstone
range|:
name|added
control|)
block|{
name|newTombstones
operator|.
name|add
argument_list|(
name|tombstone
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|IndexGraveyard
operator|.
name|Builder
argument_list|()
operator|.
name|addBuiltTombstones
argument_list|(
name|newTombstones
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/** The index tombstones that were added between two states */
DECL|method|getAdded
specifier|public
name|List
argument_list|<
name|Tombstone
argument_list|>
name|getAdded
parameter_list|()
block|{
return|return
name|added
return|;
block|}
comment|/** The number of index tombstones that were removed between two states */
DECL|method|getRemovedCount
specifier|public
name|int
name|getRemovedCount
parameter_list|()
block|{
return|return
name|removedCount
return|;
block|}
block|}
comment|/**      * An individual tombstone entry for representing a deleted index.      */
DECL|class|Tombstone
specifier|public
specifier|static
specifier|final
class|class
name|Tombstone
implements|implements
name|ToXContent
implements|,
name|Writeable
block|{
DECL|field|INDEX_KEY
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_KEY
init|=
literal|"index"
decl_stmt|;
DECL|field|DELETE_DATE_IN_MILLIS_KEY
specifier|private
specifier|static
specifier|final
name|String
name|DELETE_DATE_IN_MILLIS_KEY
init|=
literal|"delete_date_in_millis"
decl_stmt|;
DECL|field|DELETE_DATE_KEY
specifier|private
specifier|static
specifier|final
name|String
name|DELETE_DATE_KEY
init|=
literal|"delete_date"
decl_stmt|;
DECL|field|TOMBSTONE_PARSER
specifier|private
specifier|static
specifier|final
name|ObjectParser
argument_list|<
name|Tombstone
operator|.
name|Builder
argument_list|,
name|ParseFieldMatcherSupplier
argument_list|>
name|TOMBSTONE_PARSER
decl_stmt|;
static|static
block|{
name|TOMBSTONE_PARSER
operator|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
literal|"tombstoneEntry"
argument_list|,
name|Tombstone
operator|.
name|Builder
operator|::
operator|new
argument_list|)
expr_stmt|;
name|TOMBSTONE_PARSER
operator|.
name|declareObject
argument_list|(
name|Tombstone
operator|.
name|Builder
operator|::
name|index
argument_list|,
name|Index
operator|::
name|parseIndex
argument_list|,
operator|new
name|ParseField
argument_list|(
name|INDEX_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|TOMBSTONE_PARSER
operator|.
name|declareLong
argument_list|(
name|Tombstone
operator|.
name|Builder
operator|::
name|deleteDateInMillis
argument_list|,
operator|new
name|ParseField
argument_list|(
name|DELETE_DATE_IN_MILLIS_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|TOMBSTONE_PARSER
operator|.
name|declareString
argument_list|(
parameter_list|(
name|b
parameter_list|,
name|s
parameter_list|)
lambda|->
block|{}
argument_list|,
operator|new
name|ParseField
argument_list|(
name|DELETE_DATE_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getParser
specifier|static
name|BiFunction
argument_list|<
name|XContentParser
argument_list|,
name|ParseFieldMatcherSupplier
argument_list|,
name|Tombstone
argument_list|>
name|getParser
parameter_list|()
block|{
return|return
parameter_list|(
name|p
parameter_list|,
name|c
parameter_list|)
lambda|->
name|TOMBSTONE_PARSER
operator|.
name|apply
argument_list|(
name|p
argument_list|,
name|c
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|field|index
specifier|private
specifier|final
name|Index
name|index
decl_stmt|;
DECL|field|deleteDateInMillis
specifier|private
specifier|final
name|long
name|deleteDateInMillis
decl_stmt|;
DECL|method|Tombstone
specifier|private
name|Tombstone
parameter_list|(
specifier|final
name|Index
name|index
parameter_list|,
specifier|final
name|long
name|deleteDateInMillis
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|index
argument_list|)
expr_stmt|;
if|if
condition|(
name|deleteDateInMillis
operator|<
literal|0L
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid deleteDateInMillis ["
operator|+
name|deleteDateInMillis
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|deleteDateInMillis
operator|=
name|deleteDateInMillis
expr_stmt|;
block|}
comment|// create from stream
DECL|method|Tombstone
specifier|private
name|Tombstone
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|index
operator|=
operator|new
name|Index
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|deleteDateInMillis
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
comment|/**          * The deleted index.          */
DECL|method|getIndex
specifier|public
name|Index
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
comment|/**          * The date in milliseconds that the index deletion event occurred, used for logging/debugging.          */
DECL|method|getDeleteDateInMillis
specifier|public
name|long
name|getDeleteDateInMillis
parameter_list|()
block|{
return|return
name|deleteDateInMillis
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
specifier|final
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|index
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|deleteDateInMillis
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|other
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Tombstone
name|that
init|=
operator|(
name|Tombstone
operator|)
name|other
decl_stmt|;
return|return
name|index
operator|.
name|equals
argument_list|(
name|that
operator|.
name|index
argument_list|)
operator|&&
name|deleteDateInMillis
operator|==
name|that
operator|.
name|deleteDateInMillis
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|index
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|Long
operator|.
name|hashCode
argument_list|(
name|deleteDateInMillis
argument_list|)
expr_stmt|;
return|return
name|result
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
return|return
literal|"[index="
operator|+
name|index
operator|+
literal|", deleteDate="
operator|+
name|Joda
operator|.
name|getStrictStandardDateFormatter
argument_list|()
operator|.
name|printer
argument_list|()
operator|.
name|print
argument_list|(
name|deleteDateInMillis
argument_list|)
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
specifier|final
name|XContentBuilder
name|builder
parameter_list|,
specifier|final
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|INDEX_KEY
argument_list|)
expr_stmt|;
name|index
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|timeValueField
argument_list|(
name|DELETE_DATE_IN_MILLIS_KEY
argument_list|,
name|DELETE_DATE_KEY
argument_list|,
name|deleteDateInMillis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|endObject
argument_list|()
return|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|Tombstone
name|fromXContent
parameter_list|(
specifier|final
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|TOMBSTONE_PARSER
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
parameter_list|()
lambda|->
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**          * A builder for building tombstone entries.          */
DECL|class|Builder
specifier|private
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|index
specifier|private
name|Index
name|index
decl_stmt|;
DECL|field|deleteDateInMillis
specifier|private
name|long
name|deleteDateInMillis
init|=
operator|-
literal|1L
decl_stmt|;
DECL|method|index
specifier|public
name|void
name|index
parameter_list|(
specifier|final
name|Index
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
DECL|method|deleteDateInMillis
specifier|public
name|void
name|deleteDateInMillis
parameter_list|(
specifier|final
name|long
name|deleteDate
parameter_list|)
block|{
name|this
operator|.
name|deleteDateInMillis
operator|=
name|deleteDate
expr_stmt|;
block|}
DECL|method|build
specifier|public
name|Tombstone
name|build
parameter_list|()
block|{
assert|assert
name|index
operator|!=
literal|null
assert|;
assert|assert
name|deleteDateInMillis
operator|>
operator|-
literal|1L
assert|;
return|return
operator|new
name|Tombstone
argument_list|(
name|index
argument_list|,
name|deleteDateInMillis
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit
