begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.memcached.netty
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|memcached
operator|.
name|netty
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
name|netty
operator|.
name|channel
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
annotation|@
name|ChannelHandler
operator|.
name|Sharable
DECL|class|OpenChannelsHandler
specifier|public
class|class
name|OpenChannelsHandler
implements|implements
name|ChannelUpstreamHandler
block|{
DECL|field|openChannels
specifier|private
name|Set
argument_list|<
name|Channel
argument_list|>
name|openChannels
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentSet
argument_list|()
decl_stmt|;
DECL|field|remover
specifier|private
specifier|final
name|ChannelFutureListener
name|remover
init|=
operator|new
name|ChannelFutureListener
argument_list|()
block|{
specifier|public
name|void
name|operationComplete
parameter_list|(
name|ChannelFuture
name|future
parameter_list|)
throws|throws
name|Exception
block|{
name|openChannels
operator|.
name|remove
argument_list|(
name|future
operator|.
name|getChannel
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
DECL|method|handleUpstream
annotation|@
name|Override
specifier|public
name|void
name|handleUpstream
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|ChannelEvent
name|e
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|e
operator|instanceof
name|ChannelStateEvent
condition|)
block|{
name|ChannelStateEvent
name|evt
init|=
operator|(
name|ChannelStateEvent
operator|)
name|e
decl_stmt|;
if|if
condition|(
name|evt
operator|.
name|getState
argument_list|()
operator|==
name|ChannelState
operator|.
name|OPEN
condition|)
block|{
name|boolean
name|added
init|=
name|openChannels
operator|.
name|add
argument_list|(
name|ctx
operator|.
name|getChannel
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|added
condition|)
block|{
name|ctx
operator|.
name|getChannel
argument_list|()
operator|.
name|getCloseFuture
argument_list|()
operator|.
name|addListener
argument_list|(
name|remover
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|ctx
operator|.
name|sendUpstream
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|Channel
name|channel
range|:
name|openChannels
control|)
block|{
name|channel
operator|.
name|close
argument_list|()
operator|.
name|awaitUninterruptibly
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

