begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|common
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
name|transport
operator|.
name|Transport
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|MBean
argument_list|(
name|objectName
operator|=
literal|"service=transport,transportType=local"
argument_list|,
name|description
operator|=
literal|"Local Transport"
argument_list|)
DECL|class|LocalTransportManagement
specifier|public
class|class
name|LocalTransportManagement
block|{
DECL|field|transport
specifier|private
specifier|final
name|LocalTransport
name|transport
decl_stmt|;
annotation|@
name|Inject
DECL|method|LocalTransportManagement
specifier|public
name|LocalTransportManagement
parameter_list|(
name|Transport
name|transport
parameter_list|)
block|{
name|this
operator|.
name|transport
operator|=
operator|(
name|LocalTransport
operator|)
name|transport
expr_stmt|;
block|}
block|}
end_class

end_unit

