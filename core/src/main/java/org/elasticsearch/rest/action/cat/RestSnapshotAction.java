begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.cat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|cat
package|;
end_package

begin_import
import|import
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
name|get
operator|.
name|GetSnapshotsRequest
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
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|get
operator|.
name|GetSnapshotsResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|Table
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestResponseListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestTable
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
name|SnapshotInfo
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
name|SnapshotState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormatter
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|GET
import|;
end_import

begin_comment
comment|/**  * Cat API class to display information about snapshots  */
end_comment

begin_class
DECL|class|RestSnapshotAction
specifier|public
class|class
name|RestSnapshotAction
extends|extends
name|AbstractCatAction
block|{
annotation|@
name|Inject
DECL|method|RestSnapshotAction
specifier|public
name|RestSnapshotAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|controller
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_cat/snapshots/{repository}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doRequest
specifier|protected
name|void
name|doRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
name|GetSnapshotsRequest
name|getSnapshotsRequest
init|=
operator|new
name|GetSnapshotsRequest
argument_list|()
decl_stmt|;
name|getSnapshotsRequest
operator|.
name|repository
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"repository"
argument_list|)
argument_list|)
expr_stmt|;
name|getSnapshotsRequest
operator|.
name|snapshots
argument_list|(
operator|new
name|String
index|[]
block|{
name|GetSnapshotsRequest
operator|.
name|ALL_SNAPSHOTS
block|}
argument_list|)
expr_stmt|;
name|getSnapshotsRequest
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|paramAsTime
argument_list|(
literal|"master_timeout"
argument_list|,
name|getSnapshotsRequest
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|getSnapshots
argument_list|(
name|getSnapshotsRequest
argument_list|,
operator|new
name|RestResponseListener
argument_list|<
name|GetSnapshotsResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RestResponse
name|buildResponse
parameter_list|(
name|GetSnapshotsResponse
name|getSnapshotsResponse
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|RestTable
operator|.
name|buildResponse
argument_list|(
name|buildTable
argument_list|(
name|request
argument_list|,
name|getSnapshotsResponse
argument_list|)
argument_list|,
name|channel
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|documentation
specifier|protected
name|void
name|documentation
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"/_cat/snapshots/{repository}\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTableWithHeader
specifier|protected
name|Table
name|getTableWithHeader
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|Table
argument_list|()
operator|.
name|startHeaders
argument_list|()
operator|.
name|addCell
argument_list|(
literal|"id"
argument_list|,
literal|"alias:id,snapshotId;desc:unique snapshot id"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"status"
argument_list|,
literal|"alias:s,status;text-align:right;desc:snapshot name"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"start_epoch"
argument_list|,
literal|"alias:ste,startEpoch;desc:start time in seconds since 1970-01-01 00:00:00"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"start_time"
argument_list|,
literal|"alias:sti,startTime;desc:start time in HH:MM:SS"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"end_epoch"
argument_list|,
literal|"alias:ete,endEpoch;desc:end time in seconds since 1970-01-01 00:00:00"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"end_time"
argument_list|,
literal|"alias:eti,endTime;desc:end time in HH:MM:SS"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"duration"
argument_list|,
literal|"alias:dur,duration;text-align:right;desc:duration"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"indices"
argument_list|,
literal|"alias:i,indices;text-align:right;desc:number of indices"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"successful_shards"
argument_list|,
literal|"alias:ss,successful_shards;text-align:right;desc:number of successful shards"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"failed_shards"
argument_list|,
literal|"alias:fs,failed_shards;text-align:right;desc:number of failed shards"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"total_shards"
argument_list|,
literal|"alias:ts,total_shards;text-align:right;desc:number of total shards"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"reason"
argument_list|,
literal|"default:false;alias:r,reason;desc:reason for failures"
argument_list|)
operator|.
name|endHeaders
argument_list|()
return|;
block|}
DECL|field|dateFormat
specifier|private
name|DateTimeFormatter
name|dateFormat
init|=
name|DateTimeFormat
operator|.
name|forPattern
argument_list|(
literal|"HH:mm:ss"
argument_list|)
decl_stmt|;
DECL|method|buildTable
specifier|private
name|Table
name|buildTable
parameter_list|(
name|RestRequest
name|req
parameter_list|,
name|GetSnapshotsResponse
name|getSnapshotsResponse
parameter_list|)
block|{
name|Table
name|table
init|=
name|getTableWithHeader
argument_list|(
name|req
argument_list|)
decl_stmt|;
for|for
control|(
name|SnapshotInfo
name|snapshotStatus
range|:
name|getSnapshotsResponse
operator|.
name|getSnapshots
argument_list|()
control|)
block|{
name|table
operator|.
name|startRow
argument_list|()
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|snapshotStatus
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|snapshotStatus
operator|.
name|state
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|convert
argument_list|(
name|snapshotStatus
operator|.
name|startTime
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|dateFormat
operator|.
name|print
argument_list|(
name|snapshotStatus
operator|.
name|startTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|convert
argument_list|(
name|snapshotStatus
operator|.
name|endTime
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|dateFormat
operator|.
name|print
argument_list|(
name|snapshotStatus
operator|.
name|endTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|long
name|durationMillis
decl_stmt|;
if|if
condition|(
name|snapshotStatus
operator|.
name|state
argument_list|()
operator|==
name|SnapshotState
operator|.
name|IN_PROGRESS
condition|)
block|{
name|durationMillis
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|snapshotStatus
operator|.
name|startTime
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|durationMillis
operator|=
name|snapshotStatus
operator|.
name|endTime
argument_list|()
operator|-
name|snapshotStatus
operator|.
name|startTime
argument_list|()
expr_stmt|;
block|}
name|table
operator|.
name|addCell
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|durationMillis
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|snapshotStatus
operator|.
name|indices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|snapshotStatus
operator|.
name|successfulShards
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|snapshotStatus
operator|.
name|failedShards
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|snapshotStatus
operator|.
name|totalShards
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|snapshotStatus
operator|.
name|reason
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|endRow
argument_list|()
expr_stmt|;
block|}
return|return
name|table
return|;
block|}
block|}
end_class

end_unit

