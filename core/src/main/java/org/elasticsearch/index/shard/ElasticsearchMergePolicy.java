begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
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
name|IndexWriter
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
name|MergePolicy
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
name|MergeTrigger
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
name|SegmentCommitInfo
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
name|SegmentInfos
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|Collections
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
comment|/**  * A {@link MergePolicy} that upgrades segments and can upgrade merges.  *<p>  * It can be useful to use the background merging process to upgrade segments,  * for example when we perform internal changes that imply different index  * options or when a user modifies his mapping in non-breaking ways: we could  * imagine using this merge policy to be able to add doc values to fields after  * the fact or on the opposite to remove them.  *<p>  * For now, this {@link MergePolicy} takes care of moving versions that used to  * be stored as payloads to numeric doc values.  */
end_comment

begin_class
DECL|class|ElasticsearchMergePolicy
specifier|public
specifier|final
class|class
name|ElasticsearchMergePolicy
extends|extends
name|MergePolicy
block|{
DECL|field|logger
specifier|private
specifier|static
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|ElasticsearchMergePolicy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|delegate
specifier|private
specifier|final
name|MergePolicy
name|delegate
decl_stmt|;
comment|// True if the next merge request should do segment upgrades:
DECL|field|upgradeInProgress
specifier|private
specifier|volatile
name|boolean
name|upgradeInProgress
decl_stmt|;
comment|// True if the next merge request should only upgrade ancient (an older Lucene major version than current) segments;
DECL|field|upgradeOnlyAncientSegments
specifier|private
specifier|volatile
name|boolean
name|upgradeOnlyAncientSegments
decl_stmt|;
DECL|field|MAX_CONCURRENT_UPGRADE_MERGES
specifier|private
specifier|static
specifier|final
name|int
name|MAX_CONCURRENT_UPGRADE_MERGES
init|=
literal|5
decl_stmt|;
comment|/** @param delegate the merge policy to wrap */
DECL|method|ElasticsearchMergePolicy
specifier|public
name|ElasticsearchMergePolicy
parameter_list|(
name|MergePolicy
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|findMerges
specifier|public
name|MergeSpecification
name|findMerges
parameter_list|(
name|MergeTrigger
name|mergeTrigger
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|findMerges
argument_list|(
name|mergeTrigger
argument_list|,
name|segmentInfos
argument_list|,
name|writer
argument_list|)
return|;
block|}
DECL|method|shouldUpgrade
specifier|private
name|boolean
name|shouldUpgrade
parameter_list|(
name|SegmentCommitInfo
name|info
parameter_list|)
block|{
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
name|old
init|=
name|info
operator|.
name|info
operator|.
name|getVersion
argument_list|()
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
name|cur
init|=
name|Version
operator|.
name|CURRENT
operator|.
name|luceneVersion
decl_stmt|;
comment|// Something seriously wrong if this trips:
assert|assert
name|old
operator|.
name|major
operator|<=
name|cur
operator|.
name|major
assert|;
if|if
condition|(
name|cur
operator|.
name|major
operator|>
name|old
operator|.
name|major
condition|)
block|{
comment|// Always upgrade segment if Lucene's major version is too old
return|return
literal|true
return|;
block|}
if|if
condition|(
name|upgradeOnlyAncientSegments
operator|==
literal|false
operator|&&
name|cur
operator|.
name|minor
operator|>
name|old
operator|.
name|minor
condition|)
block|{
comment|// If it's only a minor version difference, and we are not upgrading only ancient segments,
comment|// also upgrade:
return|return
literal|true
return|;
block|}
comment|// Version matches, or segment is not ancient and we are only upgrading ancient segments:
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|findForcedMerges
specifier|public
name|MergeSpecification
name|findForcedMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|int
name|maxSegmentCount
parameter_list|,
name|Map
argument_list|<
name|SegmentCommitInfo
argument_list|,
name|Boolean
argument_list|>
name|segmentsToMerge
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|upgradeInProgress
condition|)
block|{
name|MergeSpecification
name|spec
init|=
operator|new
name|MergeSpecification
argument_list|()
decl_stmt|;
for|for
control|(
name|SegmentCommitInfo
name|info
range|:
name|segmentInfos
control|)
block|{
if|if
condition|(
name|shouldUpgrade
argument_list|(
name|info
argument_list|)
condition|)
block|{
comment|// TODO: Use IndexUpgradeMergePolicy instead.  We should be comparing codecs,
comment|// for now we just assume every minor upgrade has a new format.
name|logger
operator|.
name|debug
argument_list|(
literal|"Adding segment {} to be upgraded"
argument_list|,
name|info
operator|.
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
name|spec
operator|.
name|add
argument_list|(
operator|new
name|OneMerge
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|info
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// TODO: we could check IndexWriter.getMergingSegments and avoid adding merges that IW will just reject?
if|if
condition|(
name|spec
operator|.
name|merges
operator|.
name|size
argument_list|()
operator|==
name|MAX_CONCURRENT_UPGRADE_MERGES
condition|)
block|{
comment|// hit our max upgrades, so return the spec.  we will get a cascaded call to continue.
name|logger
operator|.
name|debug
argument_list|(
literal|"Returning {} merges for upgrade"
argument_list|,
name|spec
operator|.
name|merges
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|spec
return|;
block|}
block|}
comment|// We must have less than our max upgrade merges, so the next return will be our last in upgrading mode.
if|if
condition|(
name|spec
operator|.
name|merges
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Returning {} merges for end of upgrade"
argument_list|,
name|spec
operator|.
name|merges
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|spec
return|;
block|}
comment|// Only set this once there are 0 segments needing upgrading, because when we return a
comment|// spec, IndexWriter may (silently!) reject that merge if some of the segments we asked
comment|// to be merged were already being (naturally) merged:
name|upgradeInProgress
operator|=
literal|false
expr_stmt|;
comment|// fall through, so when we don't have any segments to upgrade, the delegate policy
comment|// has a chance to decide what to do (e.g. collapse the segments to satisfy maxSegmentCount)
block|}
return|return
name|delegate
operator|.
name|findForcedMerges
argument_list|(
name|segmentInfos
argument_list|,
name|maxSegmentCount
argument_list|,
name|segmentsToMerge
argument_list|,
name|writer
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|findForcedDeletesMerges
specifier|public
name|MergeSpecification
name|findForcedDeletesMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|findForcedDeletesMerges
argument_list|(
name|segmentInfos
argument_list|,
name|writer
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|useCompoundFile
specifier|public
name|boolean
name|useCompoundFile
parameter_list|(
name|SegmentInfos
name|segments
parameter_list|,
name|SegmentCommitInfo
name|newSegment
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|useCompoundFile
argument_list|(
name|segments
argument_list|,
name|newSegment
argument_list|,
name|writer
argument_list|)
return|;
block|}
comment|/**      * When<code>upgrade</code> is true, running a force merge will upgrade any segments written      * with older versions. This will apply to the next call to      * {@link IndexWriter#forceMerge} that is handled by this {@link MergePolicy}, as well as      * cascading calls made by {@link IndexWriter}.      */
DECL|method|setUpgradeInProgress
specifier|public
name|void
name|setUpgradeInProgress
parameter_list|(
name|boolean
name|upgrade
parameter_list|,
name|boolean
name|onlyAncientSegments
parameter_list|)
block|{
name|this
operator|.
name|upgradeInProgress
operator|=
name|upgrade
expr_stmt|;
name|this
operator|.
name|upgradeOnlyAncientSegments
operator|=
name|onlyAncientSegments
expr_stmt|;
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|delegate
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

