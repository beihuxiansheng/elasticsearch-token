begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.termvectors.dfs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvectors
operator|.
name|dfs
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
name|support
operator|.
name|broadcast
operator|.
name|BroadcastShardRequest
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
name|routing
operator|.
name|ShardRouting
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
name|search
operator|.
name|internal
operator|.
name|ShardSearchRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|ShardSearchTransportRequest
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

begin_class
DECL|class|ShardDfsOnlyRequest
specifier|public
class|class
name|ShardDfsOnlyRequest
extends|extends
name|BroadcastShardRequest
block|{
DECL|field|shardSearchRequest
specifier|private
name|ShardSearchTransportRequest
name|shardSearchRequest
init|=
operator|new
name|ShardSearchTransportRequest
argument_list|()
decl_stmt|;
DECL|method|ShardDfsOnlyRequest
specifier|public
name|ShardDfsOnlyRequest
parameter_list|()
block|{      }
DECL|method|ShardDfsOnlyRequest
name|ShardDfsOnlyRequest
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|int
name|numberOfShards
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|filteringAliases
parameter_list|,
name|long
name|nowInMillis
parameter_list|,
name|DfsOnlyRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardSearchRequest
operator|=
operator|new
name|ShardSearchTransportRequest
argument_list|(
name|request
operator|.
name|getSearchRequest
argument_list|()
argument_list|,
name|shardRouting
argument_list|,
name|numberOfShards
argument_list|,
name|filteringAliases
argument_list|,
name|nowInMillis
argument_list|)
expr_stmt|;
block|}
DECL|method|getShardSearchRequest
specifier|public
name|ShardSearchRequest
name|getShardSearchRequest
parameter_list|()
block|{
return|return
name|shardSearchRequest
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
name|shardSearchRequest
operator|.
name|readFrom
argument_list|(
name|in
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
name|shardSearchRequest
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

