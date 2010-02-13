begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.server
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|server
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/**  * A server represent a node within a cluster (<tt>cluster.name</tt>). The {@link #client()} can be used  * in order to use a {@link Client} to perform actions/operations against the cluster.  *  *<p>In order to create a server, the {@link ServerBuilder} can be used. When done with it, make sure to  * call {@link #close()} on it.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
DECL|interface|Server
specifier|public
interface|interface
name|Server
block|{
comment|/**      * The settings that were used to create the server.      */
DECL|method|settings
name|Settings
name|settings
parameter_list|()
function_decl|;
comment|/**      * A client that can be used to execute actions (operations) against the cluster.      */
DECL|method|client
name|Client
name|client
parameter_list|()
function_decl|;
comment|/**      * Start the server. If the server is already started, this method is noop.      */
DECL|method|start
name|Server
name|start
parameter_list|()
function_decl|;
comment|/**      * Stops the server. If the server is already started, this method is noop.      */
DECL|method|stop
name|Server
name|stop
parameter_list|()
function_decl|;
comment|/**      * Closes the server (and {@link #stop}s if its running).      */
DECL|method|close
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

