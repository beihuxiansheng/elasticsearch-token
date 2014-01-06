begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.block
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|block
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

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
name|rest
operator|.
name|RestStatus
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ClusterBlockException
specifier|public
class|class
name|ClusterBlockException
extends|extends
name|ElasticsearchException
block|{
DECL|field|blocks
specifier|private
specifier|final
name|ImmutableSet
argument_list|<
name|ClusterBlock
argument_list|>
name|blocks
decl_stmt|;
DECL|method|ClusterBlockException
specifier|public
name|ClusterBlockException
parameter_list|(
name|ImmutableSet
argument_list|<
name|ClusterBlock
argument_list|>
name|blocks
parameter_list|)
block|{
name|super
argument_list|(
name|buildMessage
argument_list|(
name|blocks
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
block|}
DECL|method|retryable
specifier|public
name|boolean
name|retryable
parameter_list|()
block|{
for|for
control|(
name|ClusterBlock
name|block
range|:
name|blocks
control|)
block|{
if|if
condition|(
operator|!
name|block
operator|.
name|retryable
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|blocks
specifier|public
name|ImmutableSet
argument_list|<
name|ClusterBlock
argument_list|>
name|blocks
parameter_list|()
block|{
return|return
name|blocks
return|;
block|}
DECL|method|buildMessage
specifier|private
specifier|static
name|String
name|buildMessage
parameter_list|(
name|ImmutableSet
argument_list|<
name|ClusterBlock
argument_list|>
name|blocks
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"blocked by: "
argument_list|)
decl_stmt|;
for|for
control|(
name|ClusterBlock
name|block
range|:
name|blocks
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|block
operator|.
name|status
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|block
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|block
operator|.
name|description
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"];"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
name|RestStatus
name|status
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ClusterBlock
name|block
range|:
name|blocks
control|)
block|{
if|if
condition|(
name|status
operator|==
literal|null
condition|)
block|{
name|status
operator|=
name|block
operator|.
name|status
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|status
operator|.
name|getStatus
argument_list|()
operator|<
name|block
operator|.
name|status
argument_list|()
operator|.
name|getStatus
argument_list|()
condition|)
block|{
name|status
operator|=
name|block
operator|.
name|status
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|status
return|;
block|}
block|}
end_class

end_unit

