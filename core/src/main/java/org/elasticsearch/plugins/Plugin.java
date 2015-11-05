begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|IndexModule
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
name|IndexService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  * An extension point allowing to plug in custom functionality.  *<p>  * A plugin can be register custom extensions to builtin behavior by implementing<tt>onModule(AnyModule)</tt>,  * and registering the extension with the given module.  */
end_comment

begin_class
DECL|class|Plugin
specifier|public
specifier|abstract
class|class
name|Plugin
block|{
comment|/**      * The name of the plugin.      */
DECL|method|name
specifier|public
specifier|abstract
name|String
name|name
parameter_list|()
function_decl|;
comment|/**      * The description of the plugin.      */
DECL|method|description
specifier|public
specifier|abstract
name|String
name|description
parameter_list|()
function_decl|;
comment|/**      * Node level modules.      */
DECL|method|nodeModules
specifier|public
name|Collection
argument_list|<
name|Module
argument_list|>
name|nodeModules
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/**      * Node level services that will be automatically started/stopped/closed.      */
DECL|method|nodeServices
specifier|public
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|LifecycleComponent
argument_list|>
argument_list|>
name|nodeServices
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/**      * Additional node settings loaded by the plugin. Note that settings that are explicit in the nodes settings can't be      * overwritten with the additional settings. These settings added if they don't exist.      */
DECL|method|additionalSettings
specifier|public
name|Settings
name|additionalSettings
parameter_list|()
block|{
return|return
name|Settings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
return|;
block|}
comment|/**      * Called once the given {@link IndexService} is fully constructed but not yet published.      * This is used to initialize plugin services that require acess to index level resources      */
DECL|method|onIndexService
specifier|public
name|void
name|onIndexService
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{}
comment|/**      * Called before a new index is created on a node. The given module can be used to regsiter index-leve      * extensions.      */
DECL|method|onIndexModule
specifier|public
name|void
name|onIndexModule
parameter_list|(
name|IndexModule
name|indexModule
parameter_list|)
block|{}
comment|/**      * Old-style guice index level extension point.      *      * @deprecated use #onIndexModule instead      */
annotation|@
name|Deprecated
DECL|method|onModule
specifier|public
specifier|final
name|void
name|onModule
parameter_list|(
name|IndexModule
name|indexModule
parameter_list|)
block|{}
block|}
end_class

end_unit

