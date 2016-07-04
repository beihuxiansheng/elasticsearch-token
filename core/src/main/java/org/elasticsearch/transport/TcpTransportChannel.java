begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TcpTransportChannel
specifier|public
specifier|final
class|class
name|TcpTransportChannel
parameter_list|<
name|Channel
parameter_list|>
implements|implements
name|TransportChannel
block|{
DECL|field|transport
specifier|private
specifier|final
name|TcpTransport
argument_list|<
name|Channel
argument_list|>
name|transport
decl_stmt|;
DECL|field|version
specifier|protected
specifier|final
name|Version
name|version
decl_stmt|;
DECL|field|action
specifier|protected
specifier|final
name|String
name|action
decl_stmt|;
DECL|field|requestId
specifier|protected
specifier|final
name|long
name|requestId
decl_stmt|;
DECL|field|profileName
specifier|private
specifier|final
name|String
name|profileName
decl_stmt|;
DECL|field|reservedBytes
specifier|private
specifier|final
name|long
name|reservedBytes
decl_stmt|;
DECL|field|released
specifier|private
specifier|final
name|AtomicBoolean
name|released
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|channelType
specifier|private
specifier|final
name|String
name|channelType
decl_stmt|;
DECL|field|channel
specifier|private
specifier|final
name|Channel
name|channel
decl_stmt|;
DECL|method|TcpTransportChannel
specifier|public
name|TcpTransportChannel
parameter_list|(
name|TcpTransport
argument_list|<
name|Channel
argument_list|>
name|transport
parameter_list|,
name|Channel
name|channel
parameter_list|,
name|String
name|channelType
parameter_list|,
name|String
name|action
parameter_list|,
name|long
name|requestId
parameter_list|,
name|Version
name|version
parameter_list|,
name|String
name|profileName
parameter_list|,
name|long
name|reservedBytes
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|requestId
operator|=
name|requestId
expr_stmt|;
name|this
operator|.
name|profileName
operator|=
name|profileName
expr_stmt|;
name|this
operator|.
name|reservedBytes
operator|=
name|reservedBytes
expr_stmt|;
name|this
operator|.
name|channelType
operator|=
name|channelType
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProfileName
specifier|public
specifier|final
name|String
name|getProfileName
parameter_list|()
block|{
return|return
name|profileName
return|;
block|}
annotation|@
name|Override
DECL|method|action
specifier|public
specifier|final
name|String
name|action
parameter_list|()
block|{
return|return
name|this
operator|.
name|action
return|;
block|}
annotation|@
name|Override
DECL|method|sendResponse
specifier|public
specifier|final
name|void
name|sendResponse
parameter_list|(
name|TransportResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|sendResponse
argument_list|(
name|response
argument_list|,
name|TransportResponseOptions
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendResponse
specifier|public
specifier|final
name|void
name|sendResponse
parameter_list|(
name|TransportResponse
name|response
parameter_list|,
name|TransportResponseOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|release
argument_list|()
expr_stmt|;
name|transport
operator|.
name|sendResponse
argument_list|(
name|version
argument_list|,
name|channel
argument_list|,
name|response
argument_list|,
name|requestId
argument_list|,
name|action
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendResponse
specifier|public
name|void
name|sendResponse
parameter_list|(
name|Exception
name|exception
parameter_list|)
throws|throws
name|IOException
block|{
name|release
argument_list|()
expr_stmt|;
name|transport
operator|.
name|sendErrorResponse
argument_list|(
name|version
argument_list|,
name|channel
argument_list|,
name|exception
argument_list|,
name|requestId
argument_list|,
name|action
argument_list|)
expr_stmt|;
block|}
DECL|method|release
specifier|private
name|void
name|release
parameter_list|()
block|{
comment|// attempt to release once atomically
if|if
condition|(
name|released
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"reserved bytes are already released"
argument_list|)
throw|;
block|}
name|transport
operator|.
name|getInFlightRequestBreaker
argument_list|()
operator|.
name|addWithoutBreaking
argument_list|(
operator|-
name|reservedBytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRequestId
specifier|public
specifier|final
name|long
name|getRequestId
parameter_list|()
block|{
return|return
name|requestId
return|;
block|}
annotation|@
name|Override
DECL|method|getChannelType
specifier|public
specifier|final
name|String
name|getChannelType
parameter_list|()
block|{
return|return
name|channelType
return|;
block|}
DECL|method|getChannel
specifier|public
name|Channel
name|getChannel
parameter_list|()
block|{
return|return
name|channel
return|;
block|}
block|}
end_class

end_unit

