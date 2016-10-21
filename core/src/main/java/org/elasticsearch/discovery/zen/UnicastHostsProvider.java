begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A pluggable provider of the list of unicast hosts to use for unicast discovery.  */
end_comment

begin_interface
DECL|interface|UnicastHostsProvider
specifier|public
interface|interface
name|UnicastHostsProvider
block|{
comment|/**      * Builds the dynamic list of unicast hosts to be used for unicast discovery.      */
DECL|method|buildDynamicNodes
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|buildDynamicNodes
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

