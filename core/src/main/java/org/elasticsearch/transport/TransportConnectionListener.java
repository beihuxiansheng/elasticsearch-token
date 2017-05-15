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
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
import|;
end_import

begin_interface
DECL|interface|TransportConnectionListener
specifier|public
interface|interface
name|TransportConnectionListener
block|{
comment|/**      * Called once a node connection is opened and registered.      */
DECL|method|onNodeConnected
specifier|default
name|void
name|onNodeConnected
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{}
comment|/**      * Called once a node connection is closed and unregistered.      */
DECL|method|onNodeDisconnected
specifier|default
name|void
name|onNodeDisconnected
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{}
comment|/**      * Called once a node connection is closed. The connection might not have been registered in the      * transport as a shared connection to a specific node      */
DECL|method|onConnectionClosed
specifier|default
name|void
name|onConnectionClosed
parameter_list|(
name|Transport
operator|.
name|Connection
name|connection
parameter_list|)
block|{}
comment|/**      * Called once a node connection is opened.      */
DECL|method|onConnectionOpened
specifier|default
name|void
name|onConnectionOpened
parameter_list|(
name|Transport
operator|.
name|Connection
name|connection
parameter_list|)
block|{}
block|}
end_interface

end_unit

