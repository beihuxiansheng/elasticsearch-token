begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
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
name|ActionListener
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
name|SearchPhaseResult
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
name|SearchShardTarget
import|;
end_import

begin_comment
comment|/**  * An base action listener that ensures shard target and shard index is set on all responses  * received by this listener.  */
end_comment

begin_class
DECL|class|SearchActionListener
specifier|abstract
class|class
name|SearchActionListener
parameter_list|<
name|T
extends|extends
name|SearchPhaseResult
parameter_list|>
implements|implements
name|ActionListener
argument_list|<
name|T
argument_list|>
block|{
DECL|field|requestIndex
specifier|private
specifier|final
name|int
name|requestIndex
decl_stmt|;
DECL|field|searchShardTarget
specifier|private
specifier|final
name|SearchShardTarget
name|searchShardTarget
decl_stmt|;
DECL|method|SearchActionListener
specifier|protected
name|SearchActionListener
parameter_list|(
name|SearchShardTarget
name|searchShardTarget
parameter_list|,
name|int
name|shardIndex
parameter_list|)
block|{
assert|assert
name|shardIndex
operator|>=
literal|0
operator|:
literal|"shard index must be positive"
assert|;
name|this
operator|.
name|searchShardTarget
operator|=
name|searchShardTarget
expr_stmt|;
name|this
operator|.
name|requestIndex
operator|=
name|shardIndex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onResponse
specifier|public
specifier|final
name|void
name|onResponse
parameter_list|(
name|T
name|response
parameter_list|)
block|{
name|response
operator|.
name|setShardIndex
argument_list|(
name|requestIndex
argument_list|)
expr_stmt|;
name|setSearchShardTarget
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|innerOnResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|setSearchShardTarget
specifier|protected
name|void
name|setSearchShardTarget
parameter_list|(
name|T
name|response
parameter_list|)
block|{
comment|// some impls need to override this
name|response
operator|.
name|setSearchShardTarget
argument_list|(
name|searchShardTarget
argument_list|)
expr_stmt|;
block|}
DECL|method|innerOnResponse
specifier|protected
specifier|abstract
name|void
name|innerOnResponse
parameter_list|(
name|T
name|response
parameter_list|)
function_decl|;
block|}
end_class

end_unit

