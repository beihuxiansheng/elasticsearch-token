begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Writeable
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|TransportAddress
specifier|public
interface|interface
name|TransportAddress
extends|extends
name|Writeable
block|{
comment|/**      * Returns the host string for this transport address      */
DECL|method|getHost
name|String
name|getHost
parameter_list|()
function_decl|;
comment|/**      * Returns the address string for this transport address      */
DECL|method|getAddress
name|String
name|getAddress
parameter_list|()
function_decl|;
comment|/**      * Returns the port of this transport address if applicable      */
DECL|method|getPort
name|int
name|getPort
parameter_list|()
function_decl|;
DECL|method|uniqueAddressTypeId
name|short
name|uniqueAddressTypeId
parameter_list|()
function_decl|;
DECL|method|sameHost
name|boolean
name|sameHost
parameter_list|(
name|TransportAddress
name|other
parameter_list|)
function_decl|;
DECL|method|isLoopbackOrLinkLocalAddress
name|boolean
name|isLoopbackOrLinkLocalAddress
parameter_list|()
function_decl|;
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

