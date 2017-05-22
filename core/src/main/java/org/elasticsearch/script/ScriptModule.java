begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
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
name|settings
operator|.
name|ClusterSettings
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
name|plugins
operator|.
name|ScriptPlugin
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Manages building {@link ScriptService}.  */
end_comment

begin_class
DECL|class|ScriptModule
specifier|public
class|class
name|ScriptModule
block|{
DECL|field|scriptService
specifier|private
specifier|final
name|ScriptService
name|scriptService
decl_stmt|;
comment|/**      * Build from {@linkplain ScriptPlugin}s. Convenient for normal use but not great for tests. See      * {@link ScriptModule#ScriptModule(Settings, List, List)} for easier use in tests.      */
DECL|method|create
specifier|public
specifier|static
name|ScriptModule
name|create
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|List
argument_list|<
name|ScriptPlugin
argument_list|>
name|scriptPlugins
parameter_list|)
block|{
name|List
argument_list|<
name|ScriptEngine
argument_list|>
name|scriptEngines
init|=
name|scriptPlugins
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|x
lambda|->
name|x
operator|.
name|getScriptEngine
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|Objects
operator|::
name|nonNull
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ScriptContext
operator|.
name|Plugin
argument_list|>
name|plugins
init|=
name|scriptPlugins
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|x
lambda|->
name|x
operator|.
name|getCustomScriptContexts
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|Objects
operator|::
name|nonNull
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScriptModule
argument_list|(
name|settings
argument_list|,
name|scriptEngines
argument_list|,
name|plugins
argument_list|)
return|;
block|}
comment|/**      * Build {@linkplain ScriptEngine} and {@linkplain ScriptContext.Plugin}.      */
DECL|method|ScriptModule
specifier|public
name|ScriptModule
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|List
argument_list|<
name|ScriptEngine
argument_list|>
name|scriptEngines
parameter_list|,
name|List
argument_list|<
name|ScriptContext
operator|.
name|Plugin
argument_list|>
name|customScriptContexts
parameter_list|)
block|{
name|ScriptContextRegistry
name|scriptContextRegistry
init|=
operator|new
name|ScriptContextRegistry
argument_list|(
name|customScriptContexts
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptEngine
argument_list|>
name|enginesByName
init|=
name|getEnginesByName
argument_list|(
name|scriptEngines
argument_list|)
decl_stmt|;
try|try
block|{
name|scriptService
operator|=
operator|new
name|ScriptService
argument_list|(
name|settings
argument_list|,
name|enginesByName
argument_list|,
name|scriptContextRegistry
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Couldn't setup ScriptService"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getEnginesByName
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptEngine
argument_list|>
name|getEnginesByName
parameter_list|(
name|List
argument_list|<
name|ScriptEngine
argument_list|>
name|engines
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptEngine
argument_list|>
name|enginesByName
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ScriptEngine
name|engine
range|:
name|engines
control|)
block|{
name|ScriptEngine
name|existing
init|=
name|enginesByName
operator|.
name|put
argument_list|(
name|engine
operator|.
name|getType
argument_list|()
argument_list|,
name|engine
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"scripting language ["
operator|+
name|engine
operator|.
name|getType
argument_list|()
operator|+
literal|"] defined for engine ["
operator|+
name|existing
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"] and ["
operator|+
name|engine
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|enginesByName
argument_list|)
return|;
block|}
comment|/**      * Service responsible for managing scripts.      */
DECL|method|getScriptService
specifier|public
name|ScriptService
name|getScriptService
parameter_list|()
block|{
return|return
name|scriptService
return|;
block|}
comment|/**      * Allow the script service to register any settings update handlers on the cluster settings      */
DECL|method|registerClusterSettingsListeners
specifier|public
name|void
name|registerClusterSettingsListeners
parameter_list|(
name|ClusterSettings
name|clusterSettings
parameter_list|)
block|{
name|scriptService
operator|.
name|registerClusterSettingsListeners
argument_list|(
name|clusterSettings
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

