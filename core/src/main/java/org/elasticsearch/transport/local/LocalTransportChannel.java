begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport.local
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|local
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|bytes
operator|.
name|BytesReference
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
name|BytesStreamOutput
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadContext
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
name|RemoteTransportException
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
name|TransportChannel
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
name|TransportResponse
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
name|TransportResponseOptions
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
name|TransportServiceAdapter
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
name|support
operator|.
name|TransportStatus
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
DECL|class|LocalTransportChannel
specifier|public
class|class
name|LocalTransportChannel
implements|implements
name|TransportChannel
block|{
DECL|field|LOCAL_TRANSPORT_PROFILE
specifier|private
specifier|static
specifier|final
name|String
name|LOCAL_TRANSPORT_PROFILE
init|=
literal|"default"
decl_stmt|;
DECL|field|sourceTransport
specifier|private
specifier|final
name|LocalTransport
name|sourceTransport
decl_stmt|;
DECL|field|sourceTransportServiceAdapter
specifier|private
specifier|final
name|TransportServiceAdapter
name|sourceTransportServiceAdapter
decl_stmt|;
comment|// the transport we will *send to*
DECL|field|targetTransport
specifier|private
specifier|final
name|LocalTransport
name|targetTransport
decl_stmt|;
DECL|field|action
specifier|private
specifier|final
name|String
name|action
decl_stmt|;
DECL|field|requestId
specifier|private
specifier|final
name|long
name|requestId
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|Version
name|version
decl_stmt|;
DECL|field|reservedBytes
specifier|private
specifier|final
name|long
name|reservedBytes
decl_stmt|;
DECL|field|closed
specifier|private
specifier|final
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|method|LocalTransportChannel
specifier|public
name|LocalTransportChannel
parameter_list|(
name|LocalTransport
name|sourceTransport
parameter_list|,
name|TransportServiceAdapter
name|sourceTransportServiceAdapter
parameter_list|,
name|LocalTransport
name|targetTransport
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
name|long
name|reservedBytes
parameter_list|)
block|{
name|this
operator|.
name|sourceTransport
operator|=
name|sourceTransport
expr_stmt|;
name|this
operator|.
name|sourceTransportServiceAdapter
operator|=
name|sourceTransportServiceAdapter
expr_stmt|;
name|this
operator|.
name|targetTransport
operator|=
name|targetTransport
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
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|reservedBytes
operator|=
name|reservedBytes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|action
specifier|public
name|String
name|action
parameter_list|()
block|{
return|return
name|action
return|;
block|}
annotation|@
name|Override
DECL|method|getProfileName
specifier|public
name|String
name|getProfileName
parameter_list|()
block|{
return|return
name|LOCAL_TRANSPORT_PROFILE
return|;
block|}
annotation|@
name|Override
DECL|method|sendResponse
specifier|public
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
try|try
init|(
name|BytesStreamOutput
name|stream
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|stream
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|stream
operator|.
name|writeLong
argument_list|(
name|requestId
argument_list|)
expr_stmt|;
name|byte
name|status
init|=
literal|0
decl_stmt|;
name|status
operator|=
name|TransportStatus
operator|.
name|setResponse
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|stream
operator|.
name|writeByte
argument_list|(
name|status
argument_list|)
expr_stmt|;
comment|// 0 for request, 1 for response.
name|response
operator|.
name|writeTo
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|sendResponseData
argument_list|(
name|BytesReference
operator|.
name|toBytes
argument_list|(
name|stream
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sourceTransportServiceAdapter
operator|.
name|onResponseSent
argument_list|(
name|requestId
argument_list|,
name|action
argument_list|,
name|response
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
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
name|BytesStreamOutput
name|stream
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|writeResponseExceptionHeader
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|RemoteTransportException
name|tx
init|=
operator|new
name|RemoteTransportException
argument_list|(
name|targetTransport
operator|.
name|nodeName
argument_list|()
argument_list|,
name|targetTransport
operator|.
name|boundAddress
argument_list|()
operator|.
name|boundAddresses
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|action
argument_list|,
name|exception
argument_list|)
decl_stmt|;
name|stream
operator|.
name|writeThrowable
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|sendResponseData
argument_list|(
name|BytesReference
operator|.
name|toBytes
argument_list|(
name|stream
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sourceTransportServiceAdapter
operator|.
name|onResponseSent
argument_list|(
name|requestId
argument_list|,
name|action
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
DECL|method|sendResponseData
specifier|private
name|void
name|sendResponseData
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|close
argument_list|()
expr_stmt|;
name|targetTransport
operator|.
name|workers
argument_list|()
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
name|ThreadContext
name|threadContext
init|=
name|targetTransport
operator|.
name|threadPool
operator|.
name|getThreadContext
argument_list|()
decl_stmt|;
try|try
init|(
name|ThreadContext
operator|.
name|StoredContext
name|ignore
init|=
name|threadContext
operator|.
name|stashContext
argument_list|()
init|)
block|{
name|targetTransport
operator|.
name|messageReceived
argument_list|(
name|data
argument_list|,
name|action
argument_list|,
name|sourceTransport
argument_list|,
name|version
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|private
name|void
name|close
parameter_list|()
block|{
comment|// attempt to close once atomically
if|if
condition|(
name|closed
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
literal|"Channel is already closed"
argument_list|)
throw|;
block|}
name|sourceTransport
operator|.
name|inFlightRequestsBreaker
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
name|String
name|getChannelType
parameter_list|()
block|{
return|return
literal|"local"
return|;
block|}
DECL|method|writeResponseExceptionHeader
specifier|private
name|void
name|writeResponseExceptionHeader
parameter_list|(
name|BytesStreamOutput
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|writeLong
argument_list|(
name|requestId
argument_list|)
expr_stmt|;
name|byte
name|status
init|=
literal|0
decl_stmt|;
name|status
operator|=
name|TransportStatus
operator|.
name|setResponse
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|status
operator|=
name|TransportStatus
operator|.
name|setError
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|stream
operator|.
name|writeByte
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

