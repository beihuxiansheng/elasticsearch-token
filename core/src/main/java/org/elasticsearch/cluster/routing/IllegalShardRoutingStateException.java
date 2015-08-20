begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This exception defines illegal states of shard routing  */
end_comment

begin_class
DECL|class|IllegalShardRoutingStateException
specifier|public
class|class
name|IllegalShardRoutingStateException
extends|extends
name|RoutingException
block|{
DECL|field|shard
specifier|private
specifier|final
name|ShardRouting
name|shard
decl_stmt|;
DECL|method|IllegalShardRoutingStateException
specifier|public
name|IllegalShardRoutingStateException
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|this
argument_list|(
name|shard
argument_list|,
name|message
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|IllegalShardRoutingStateException
specifier|public
name|IllegalShardRoutingStateException
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|shard
operator|.
name|shortSummary
argument_list|()
operator|+
literal|": "
operator|+
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|shard
operator|=
name|shard
expr_stmt|;
block|}
DECL|method|IllegalShardRoutingStateException
specifier|public
name|IllegalShardRoutingStateException
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
name|shard
operator|=
name|ShardRouting
operator|.
name|readShardRoutingEntry
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
name|shard
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the shard instance referenced by this exception      * @return shard instance referenced by this exception      */
DECL|method|shard
specifier|public
name|ShardRouting
name|shard
parameter_list|()
block|{
return|return
name|shard
return|;
block|}
block|}
end_class

end_unit
