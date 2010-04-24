begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport.netty
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|netty
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
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
name|jmx
operator|.
name|MBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|jmx
operator|.
name|ManagedAttribute
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
annotation|@
name|MBean
argument_list|(
name|objectName
operator|=
literal|"service=transport,transportType=netty"
argument_list|,
name|description
operator|=
literal|"Netty Transport"
argument_list|)
DECL|class|NettyTransportManagement
specifier|public
class|class
name|NettyTransportManagement
block|{
DECL|field|transport
specifier|private
specifier|final
name|NettyTransport
name|transport
decl_stmt|;
DECL|method|NettyTransportManagement
annotation|@
name|Inject
specifier|public
name|NettyTransportManagement
parameter_list|(
name|NettyTransport
name|transport
parameter_list|)
block|{
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Number of connections this node has to other nodes"
argument_list|)
DECL|method|getNumberOfOutboundConnections
specifier|public
name|long
name|getNumberOfOutboundConnections
parameter_list|()
block|{
return|return
name|transport
operator|.
name|connectedNodes
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Number if IO worker threads"
argument_list|)
DECL|method|getWorkerCount
specifier|public
name|int
name|getWorkerCount
parameter_list|()
block|{
return|return
name|transport
operator|.
name|workerCount
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Port(s) netty was configured to bind on"
argument_list|)
DECL|method|getPort
specifier|public
name|String
name|getPort
parameter_list|()
block|{
return|return
name|transport
operator|.
name|port
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Host to bind to"
argument_list|)
DECL|method|getBindHost
specifier|public
name|String
name|getBindHost
parameter_list|()
block|{
return|return
name|transport
operator|.
name|bindHost
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Host to publish"
argument_list|)
DECL|method|getPublishHost
specifier|public
name|String
name|getPublishHost
parameter_list|()
block|{
return|return
name|transport
operator|.
name|publishHost
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Connect timeout"
argument_list|)
DECL|method|getConnectTimeout
specifier|public
name|String
name|getConnectTimeout
parameter_list|()
block|{
return|return
name|transport
operator|.
name|connectTimeout
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"TcpNoDelay"
argument_list|)
DECL|method|getTcpNoDelay
specifier|public
name|Boolean
name|getTcpNoDelay
parameter_list|()
block|{
return|return
name|transport
operator|.
name|tcpNoDelay
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"TcpKeepAlive"
argument_list|)
DECL|method|getTcpKeepAlive
specifier|public
name|Boolean
name|getTcpKeepAlive
parameter_list|()
block|{
return|return
name|transport
operator|.
name|tcpKeepAlive
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"ReuseAddress"
argument_list|)
DECL|method|getReuseAddress
specifier|public
name|Boolean
name|getReuseAddress
parameter_list|()
block|{
return|return
name|transport
operator|.
name|reuseAddress
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"TcpSendBufferSize"
argument_list|)
DECL|method|getTcpSendBufferSize
specifier|public
name|String
name|getTcpSendBufferSize
parameter_list|()
block|{
if|if
condition|(
name|transport
operator|.
name|tcpSendBufferSize
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|transport
operator|.
name|tcpSendBufferSize
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"TcpReceiveBufferSize"
argument_list|)
DECL|method|getTcpReceiveBufferSize
specifier|public
name|String
name|getTcpReceiveBufferSize
parameter_list|()
block|{
if|if
condition|(
name|transport
operator|.
name|tcpReceiveBufferSize
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|transport
operator|.
name|tcpReceiveBufferSize
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

