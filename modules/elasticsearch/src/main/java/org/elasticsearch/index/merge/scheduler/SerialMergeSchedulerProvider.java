begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.merge.scheduler
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|merge
operator|.
name|scheduler
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
name|CorruptIndexException
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
name|MergeScheduler
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
name|TrackingSerialMergeScheduler
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
name|store
operator|.
name|AlreadyClosedException
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
name|inject
operator|.
name|Inject
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
name|index
operator|.
name|merge
operator|.
name|MergeStats
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
name|merge
operator|.
name|policy
operator|.
name|EnableMergePolicy
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
name|settings
operator|.
name|IndexSettings
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
name|shard
operator|.
name|AbstractIndexShardComponent
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
name|shard
operator|.
name|ShardId
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
name|Set
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
name|CopyOnWriteArraySet
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|SerialMergeSchedulerProvider
specifier|public
class|class
name|SerialMergeSchedulerProvider
extends|extends
name|AbstractIndexShardComponent
implements|implements
name|MergeSchedulerProvider
block|{
DECL|field|schedulers
specifier|private
name|Set
argument_list|<
name|CustomSerialMergeScheduler
argument_list|>
name|schedulers
init|=
operator|new
name|CopyOnWriteArraySet
argument_list|<
name|CustomSerialMergeScheduler
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|SerialMergeSchedulerProvider
annotation|@
name|Inject
specifier|public
name|SerialMergeSchedulerProvider
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"using [serial] merge scheduler"
argument_list|)
expr_stmt|;
block|}
DECL|method|newMergeScheduler
annotation|@
name|Override
specifier|public
name|MergeScheduler
name|newMergeScheduler
parameter_list|()
block|{
name|CustomSerialMergeScheduler
name|scheduler
init|=
operator|new
name|CustomSerialMergeScheduler
argument_list|(
name|logger
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|schedulers
operator|.
name|add
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
return|return
name|scheduler
return|;
block|}
DECL|method|stats
annotation|@
name|Override
specifier|public
name|MergeStats
name|stats
parameter_list|()
block|{
name|MergeStats
name|mergeStats
init|=
operator|new
name|MergeStats
argument_list|()
decl_stmt|;
for|for
control|(
name|CustomSerialMergeScheduler
name|scheduler
range|:
name|schedulers
control|)
block|{
name|mergeStats
operator|.
name|add
argument_list|(
name|scheduler
operator|.
name|totalMerges
argument_list|()
argument_list|,
name|scheduler
operator|.
name|totalMergeTime
argument_list|()
argument_list|,
name|scheduler
operator|.
name|totalMergeNumDocs
argument_list|()
argument_list|,
name|scheduler
operator|.
name|totalMergeSizeInBytes
argument_list|()
argument_list|,
name|scheduler
operator|.
name|currentMerges
argument_list|()
argument_list|,
name|scheduler
operator|.
name|currentMergesNumDocs
argument_list|()
argument_list|,
name|scheduler
operator|.
name|currentMergesSizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|mergeStats
return|;
block|}
DECL|class|CustomSerialMergeScheduler
specifier|public
specifier|static
class|class
name|CustomSerialMergeScheduler
extends|extends
name|TrackingSerialMergeScheduler
block|{
DECL|field|provider
specifier|private
specifier|final
name|SerialMergeSchedulerProvider
name|provider
decl_stmt|;
DECL|method|CustomSerialMergeScheduler
specifier|public
name|CustomSerialMergeScheduler
parameter_list|(
name|ESLogger
name|logger
parameter_list|,
name|SerialMergeSchedulerProvider
name|provider
parameter_list|)
block|{
name|super
argument_list|(
name|logger
argument_list|)
expr_stmt|;
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
block|}
DECL|method|merge
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
try|try
block|{
comment|// if merge is not enabled, don't do any merging...
if|if
condition|(
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|instanceof
name|EnableMergePolicy
condition|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|EnableMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|isMergeEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
comment|// called writer#getMergePolicy can cause an AlreadyClosed failure, so ignore it
comment|// since we are doing it on close, return here and don't do the actual merge
comment|// since we do it outside of a lock in the RobinEngine
return|return;
block|}
try|try
block|{
name|super
operator|.
name|merge
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to merge"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|provider
operator|.
name|schedulers
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

