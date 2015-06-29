begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.snapshots
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
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
name|rest
operator|.
name|RestStatus
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
comment|/**  * Thrown when a user tries to start multiple snapshots at the same time  */
end_comment

begin_class
DECL|class|ConcurrentSnapshotExecutionException
specifier|public
class|class
name|ConcurrentSnapshotExecutionException
extends|extends
name|SnapshotException
block|{
DECL|method|ConcurrentSnapshotExecutionException
specifier|public
name|ConcurrentSnapshotExecutionException
parameter_list|(
name|SnapshotId
name|snapshot
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|snapshot
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|ConcurrentSnapshotExecutionException
specifier|public
name|ConcurrentSnapshotExecutionException
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
return|return
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
return|;
block|}
block|}
end_class

end_unit

