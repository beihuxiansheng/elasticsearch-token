begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.gateway.none
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
operator|.
name|none
package|;
end_package

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
name|gateway
operator|.
name|none
operator|.
name|NoneGateway
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
name|gateway
operator|.
name|IndexShardGateway
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
name|gateway
operator|.
name|IndexShardGatewayRecoveryException
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|service
operator|.
name|IndexShard
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
name|service
operator|.
name|InternalIndexShard
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|NoneIndexShardGateway
specifier|public
class|class
name|NoneIndexShardGateway
extends|extends
name|AbstractIndexShardComponent
implements|implements
name|IndexShardGateway
block|{
DECL|field|indexShard
specifier|private
specifier|final
name|InternalIndexShard
name|indexShard
decl_stmt|;
DECL|field|recoveryStatus
specifier|private
specifier|final
name|RecoveryStatus
name|recoveryStatus
init|=
operator|new
name|RecoveryStatus
argument_list|()
decl_stmt|;
DECL|method|NoneIndexShardGateway
annotation|@
name|Inject
specifier|public
name|NoneIndexShardGateway
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|IndexShard
name|indexShard
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexShard
operator|=
operator|(
name|InternalIndexShard
operator|)
name|indexShard
expr_stmt|;
block|}
DECL|method|toString
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"_none_"
return|;
block|}
DECL|method|recoveryStatus
annotation|@
name|Override
specifier|public
name|RecoveryStatus
name|recoveryStatus
parameter_list|()
block|{
return|return
name|recoveryStatus
return|;
block|}
DECL|method|recover
annotation|@
name|Override
specifier|public
name|RecoveryStatus
name|recover
parameter_list|()
throws|throws
name|IndexShardGatewayRecoveryException
block|{
name|recoveryStatus
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|startTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|recoveryStatus
operator|.
name|translog
argument_list|()
operator|.
name|startTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
comment|// in the none case, we simply start the shard
comment|// clean the store, there should be nothing there...
try|try
block|{
name|indexShard
operator|.
name|store
argument_list|()
operator|.
name|deleteContent
argument_list|()
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
literal|"failed to clean store before starting shard"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|indexShard
operator|.
name|start
argument_list|()
expr_stmt|;
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|took
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
name|recoveryStatus
operator|.
name|translog
argument_list|()
operator|.
name|took
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|recoveryStatus
operator|.
name|updateStage
argument_list|(
name|RecoveryStatus
operator|.
name|Stage
operator|.
name|DONE
argument_list|)
return|;
block|}
DECL|method|type
annotation|@
name|Override
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|NoneGateway
operator|.
name|TYPE
return|;
block|}
DECL|method|snapshot
annotation|@
name|Override
specifier|public
name|SnapshotStatus
name|snapshot
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
return|return
name|SnapshotStatus
operator|.
name|NA
return|;
block|}
DECL|method|requiresSnapshotScheduling
annotation|@
name|Override
specifier|public
name|boolean
name|requiresSnapshotScheduling
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|(
name|boolean
name|delete
parameter_list|)
block|{     }
block|}
end_class

end_unit

