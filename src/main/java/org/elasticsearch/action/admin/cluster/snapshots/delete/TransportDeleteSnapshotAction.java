begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.snapshots.delete
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|delete
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|master
operator|.
name|TransportMasterNodeOperationAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|block
operator|.
name|ClusterBlockException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|block
operator|.
name|ClusterBlockLevel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|SnapshotId
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
name|snapshots
operator|.
name|SnapshotsService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportService
import|;
end_import

begin_comment
comment|/**  * Transport action for delete snapshot operation  */
end_comment

begin_class
DECL|class|TransportDeleteSnapshotAction
specifier|public
class|class
name|TransportDeleteSnapshotAction
extends|extends
name|TransportMasterNodeOperationAction
argument_list|<
name|DeleteSnapshotRequest
argument_list|,
name|DeleteSnapshotResponse
argument_list|>
block|{
DECL|field|snapshotsService
specifier|private
specifier|final
name|SnapshotsService
name|snapshotsService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportDeleteSnapshotAction
specifier|public
name|TransportDeleteSnapshotAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|SnapshotsService
name|snapshotsService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
name|this
operator|.
name|snapshotsService
operator|=
name|snapshotsService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|GENERIC
return|;
block|}
annotation|@
name|Override
DECL|method|transportAction
specifier|protected
name|String
name|transportAction
parameter_list|()
block|{
return|return
name|DeleteSnapshotAction
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|DeleteSnapshotRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|DeleteSnapshotRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|DeleteSnapshotResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|DeleteSnapshotResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkBlock
specifier|protected
name|ClusterBlockException
name|checkBlock
parameter_list|(
name|DeleteSnapshotRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indexBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA
argument_list|,
literal|""
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|masterOperation
specifier|protected
name|void
name|masterOperation
parameter_list|(
specifier|final
name|DeleteSnapshotRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|DeleteSnapshotResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
name|SnapshotId
name|snapshotIds
init|=
operator|new
name|SnapshotId
argument_list|(
name|request
operator|.
name|repository
argument_list|()
argument_list|,
name|request
operator|.
name|snapshot
argument_list|()
argument_list|)
decl_stmt|;
name|snapshotsService
operator|.
name|deleteSnapshot
argument_list|(
name|snapshotIds
argument_list|,
operator|new
name|SnapshotsService
operator|.
name|DeleteSnapshotListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|()
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|DeleteSnapshotResponse
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

