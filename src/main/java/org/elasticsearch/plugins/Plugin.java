begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
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
name|common
operator|.
name|inject
operator|.
name|Module
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|CloseableIndexComponent
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * An extension point allowing to plug in custom functionality.  *  *  */
end_comment

begin_interface
DECL|interface|Plugin
specifier|public
interface|interface
name|Plugin
block|{
comment|/**      * The name of the plugin.      */
DECL|method|name
name|String
name|name
parameter_list|()
function_decl|;
comment|/**      * The description of the plugin.      */
DECL|method|description
name|String
name|description
parameter_list|()
function_decl|;
comment|/**      * Node level modules.      */
DECL|method|modules
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
argument_list|>
name|modules
parameter_list|()
function_decl|;
comment|/**      * Node level services that will be automatically started/stopped/closed.      */
DECL|method|services
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|LifecycleComponent
argument_list|>
argument_list|>
name|services
parameter_list|()
function_decl|;
comment|/**      * Per index modules.      */
DECL|method|indexModules
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
argument_list|>
name|indexModules
parameter_list|()
function_decl|;
comment|/**      * Per index services that will be automatically closed.      */
DECL|method|indexServices
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CloseableIndexComponent
argument_list|>
argument_list|>
name|indexServices
parameter_list|()
function_decl|;
comment|/**      * Per index shard module.      */
DECL|method|shardModules
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
argument_list|>
name|shardModules
parameter_list|()
function_decl|;
comment|/**      * Per index shard service that will be automatically closed.      */
DECL|method|shardServices
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CloseableIndexComponent
argument_list|>
argument_list|>
name|shardServices
parameter_list|()
function_decl|;
DECL|method|processModule
name|void
name|processModule
parameter_list|(
name|Module
name|module
parameter_list|)
function_decl|;
comment|/**      * Additional node settings loaded by the plugin      */
DECL|method|additionalSettings
name|Settings
name|additionalSettings
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

