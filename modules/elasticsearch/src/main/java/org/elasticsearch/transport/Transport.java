begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|Node
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
name|component
operator|.
name|LifecycleComponent
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|transport
operator|.
name|BoundTransportAddress
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
name|transport
operator|.
name|TransportAddress
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|Transport
specifier|public
interface|interface
name|Transport
extends|extends
name|LifecycleComponent
argument_list|<
name|Transport
argument_list|>
block|{
DECL|class|Helper
class|class
name|Helper
block|{
DECL|field|TRANSPORT_TYPE
specifier|public
specifier|static
specifier|final
name|byte
name|TRANSPORT_TYPE
init|=
literal|1
decl_stmt|;
DECL|field|RESPONSE_TYPE
specifier|public
specifier|static
specifier|final
name|byte
name|RESPONSE_TYPE
init|=
literal|1
operator|<<
literal|1
decl_stmt|;
DECL|method|isRequest
specifier|public
specifier|static
name|boolean
name|isRequest
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
return|return
operator|(
name|value
operator|&
name|TRANSPORT_TYPE
operator|)
operator|==
literal|0
return|;
block|}
DECL|method|setRequest
specifier|public
specifier|static
name|byte
name|setRequest
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|value
operator|&=
operator|~
name|TRANSPORT_TYPE
expr_stmt|;
return|return
name|value
return|;
block|}
DECL|method|setResponse
specifier|public
specifier|static
name|byte
name|setResponse
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|value
operator||=
name|TRANSPORT_TYPE
expr_stmt|;
return|return
name|value
return|;
block|}
DECL|method|isError
specifier|public
specifier|static
name|boolean
name|isError
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
return|return
operator|(
name|value
operator|&
name|RESPONSE_TYPE
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|setError
specifier|public
specifier|static
name|byte
name|setError
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|value
operator||=
name|RESPONSE_TYPE
expr_stmt|;
return|return
name|value
return|;
block|}
block|}
DECL|method|transportServiceAdapter
name|void
name|transportServiceAdapter
parameter_list|(
name|TransportServiceAdapter
name|service
parameter_list|)
function_decl|;
DECL|method|boundAddress
name|BoundTransportAddress
name|boundAddress
parameter_list|()
function_decl|;
comment|/**      * Is the address type supported.      */
DECL|method|addressSupported
name|boolean
name|addressSupported
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|TransportAddress
argument_list|>
name|address
parameter_list|)
function_decl|;
DECL|method|nodesAdded
name|void
name|nodesAdded
parameter_list|(
name|Iterable
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|)
function_decl|;
DECL|method|nodesRemoved
name|void
name|nodesRemoved
parameter_list|(
name|Iterable
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|)
function_decl|;
DECL|method|sendRequest
parameter_list|<
name|T
extends|extends
name|Streamable
parameter_list|>
name|void
name|sendRequest
parameter_list|(
name|Node
name|node
parameter_list|,
name|long
name|requestId
parameter_list|,
name|String
name|action
parameter_list|,
name|Streamable
name|message
parameter_list|,
name|TransportResponseHandler
argument_list|<
name|T
argument_list|>
name|handler
parameter_list|)
throws|throws
name|IOException
throws|,
name|TransportException
function_decl|;
block|}
end_interface

end_unit

