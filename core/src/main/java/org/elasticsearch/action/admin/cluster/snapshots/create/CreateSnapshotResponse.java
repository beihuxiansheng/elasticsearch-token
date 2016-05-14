begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.snapshots.create
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
name|create
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
name|ActionResponse
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
name|rest
operator|.
name|RestStatus
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Create snapshot response  */
end_comment

begin_class
DECL|class|CreateSnapshotResponse
specifier|public
class|class
name|CreateSnapshotResponse
extends|extends
name|ActionResponse
implements|implements
name|ToXContent
block|{
annotation|@
name|Nullable
DECL|field|snapshotInfo
specifier|private
name|SnapshotInfo
name|snapshotInfo
decl_stmt|;
DECL|method|CreateSnapshotResponse
name|CreateSnapshotResponse
parameter_list|(
annotation|@
name|Nullable
name|SnapshotInfo
name|snapshotInfo
parameter_list|)
block|{
name|this
operator|.
name|snapshotInfo
operator|=
name|snapshotInfo
expr_stmt|;
block|}
DECL|method|CreateSnapshotResponse
name|CreateSnapshotResponse
parameter_list|()
block|{     }
comment|/**      * Returns snapshot information if snapshot was completed by the time this method returned or null otherwise.      *      * @return snapshot information or null      */
DECL|method|getSnapshotInfo
specifier|public
name|SnapshotInfo
name|getSnapshotInfo
parameter_list|()
block|{
return|return
name|snapshotInfo
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|snapshotInfo
operator|=
name|in
operator|.
name|readOptionalWriteable
argument_list|(
name|SnapshotInfo
operator|::
operator|new
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalWriteable
argument_list|(
name|snapshotInfo
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns HTTP status      *<ul>      *<li>{@link RestStatus#ACCEPTED} if snapshot is still in progress</li>      *<li>{@link RestStatus#OK} if snapshot was successful or partially successful</li>      *<li>{@link RestStatus#INTERNAL_SERVER_ERROR} if snapshot failed completely</li>      *</ul>      */
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
if|if
condition|(
name|snapshotInfo
operator|==
literal|null
condition|)
block|{
return|return
name|RestStatus
operator|.
name|ACCEPTED
return|;
block|}
return|return
name|snapshotInfo
operator|.
name|status
argument_list|()
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|SNAPSHOT
specifier|static
specifier|final
name|String
name|SNAPSHOT
init|=
literal|"snapshot"
decl_stmt|;
DECL|field|ACCEPTED
specifier|static
specifier|final
name|String
name|ACCEPTED
init|=
literal|"accepted"
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|snapshotInfo
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SNAPSHOT
argument_list|)
expr_stmt|;
name|snapshotInfo
operator|.
name|toExternalXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ACCEPTED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

