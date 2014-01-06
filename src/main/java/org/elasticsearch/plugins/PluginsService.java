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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|PluginInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|PluginsInfo
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
name|Strings
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
name|collect
operator|.
name|MapBuilder
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
name|component
operator|.
name|AbstractComponent
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
name|ImmutableSettings
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|PluginsService
specifier|public
class|class
name|PluginsService
extends|extends
name|AbstractComponent
block|{
DECL|field|ES_PLUGIN_PROPERTIES
specifier|private
specifier|static
specifier|final
name|String
name|ES_PLUGIN_PROPERTIES
init|=
literal|"es-plugin.properties"
decl_stmt|;
DECL|field|environment
specifier|private
specifier|final
name|Environment
name|environment
decl_stmt|;
DECL|field|plugins
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Plugin
argument_list|>
name|plugins
decl_stmt|;
DECL|field|onModuleReferences
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|Plugin
argument_list|,
name|List
argument_list|<
name|OnModuleReference
argument_list|>
argument_list|>
name|onModuleReferences
decl_stmt|;
DECL|field|cachedPluginsInfo
specifier|private
name|PluginsInfo
name|cachedPluginsInfo
decl_stmt|;
DECL|field|refreshInterval
specifier|private
specifier|final
name|TimeValue
name|refreshInterval
decl_stmt|;
DECL|field|lastRefresh
specifier|private
name|long
name|lastRefresh
decl_stmt|;
DECL|class|OnModuleReference
specifier|static
class|class
name|OnModuleReference
block|{
DECL|field|moduleClass
specifier|public
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|moduleClass
decl_stmt|;
DECL|field|onModuleMethod
specifier|public
specifier|final
name|Method
name|onModuleMethod
decl_stmt|;
DECL|method|OnModuleReference
name|OnModuleReference
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|moduleClass
parameter_list|,
name|Method
name|onModuleMethod
parameter_list|)
block|{
name|this
operator|.
name|moduleClass
operator|=
name|moduleClass
expr_stmt|;
name|this
operator|.
name|onModuleMethod
operator|=
name|onModuleMethod
expr_stmt|;
block|}
block|}
comment|/**      * Constructs a new PluginService      * @param settings The settings of the system      * @param environment The environment of the system      */
DECL|method|PluginsService
specifier|public
name|PluginsService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Environment
name|environment
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|environment
operator|=
name|environment
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Plugin
argument_list|>
name|plugins
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
comment|//first we load all the default plugins from the settings
name|String
index|[]
name|defaultPluginsClasses
init|=
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"plugin.types"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|pluginClass
range|:
name|defaultPluginsClasses
control|)
block|{
name|Plugin
name|plugin
init|=
name|loadPlugin
argument_list|(
name|pluginClass
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|plugins
operator|.
name|put
argument_list|(
name|plugin
operator|.
name|name
argument_list|()
argument_list|,
name|plugin
argument_list|)
expr_stmt|;
block|}
comment|// now, find all the ones that are in the classpath
name|loadPluginsIntoClassLoader
argument_list|()
expr_stmt|;
name|plugins
operator|.
name|putAll
argument_list|(
name|loadPluginsFromClasspath
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|sitePlugins
init|=
name|PluginsHelper
operator|.
name|sitePlugins
argument_list|(
name|this
operator|.
name|environment
argument_list|)
decl_stmt|;
name|String
index|[]
name|mandatoryPlugins
init|=
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"plugin.mandatory"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|mandatoryPlugins
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|missingPlugins
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|mandatoryPlugin
range|:
name|mandatoryPlugins
control|)
block|{
if|if
condition|(
operator|!
name|plugins
operator|.
name|containsKey
argument_list|(
name|mandatoryPlugin
argument_list|)
operator|&&
operator|!
name|sitePlugins
operator|.
name|contains
argument_list|(
name|mandatoryPlugin
argument_list|)
operator|&&
operator|!
name|missingPlugins
operator|.
name|contains
argument_list|(
name|mandatoryPlugin
argument_list|)
condition|)
block|{
name|missingPlugins
operator|.
name|add
argument_list|(
name|mandatoryPlugin
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|missingPlugins
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Missing mandatory plugins ["
operator|+
name|Strings
operator|.
name|collectionToDelimitedString
argument_list|(
name|missingPlugins
argument_list|,
literal|", "
argument_list|)
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"loaded {}, sites {}"
argument_list|,
name|plugins
operator|.
name|keySet
argument_list|()
argument_list|,
name|sitePlugins
argument_list|)
expr_stmt|;
name|this
operator|.
name|plugins
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|plugins
argument_list|)
expr_stmt|;
name|MapBuilder
argument_list|<
name|Plugin
argument_list|,
name|List
argument_list|<
name|OnModuleReference
argument_list|>
argument_list|>
name|onModuleReferences
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
operator|.
name|values
argument_list|()
control|)
block|{
name|List
argument_list|<
name|OnModuleReference
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Method
name|method
range|:
name|plugin
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredMethods
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|method
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"onModule"
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|method
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|==
literal|0
operator|||
name|method
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Plugin: {} implementing onModule with no parameters or more than one parameter"
argument_list|,
name|plugin
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|Class
name|moduleClass
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|Module
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|moduleClass
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Plugin: {} implementing onModule by the type is not of Module type {}"
argument_list|,
name|plugin
operator|.
name|name
argument_list|()
argument_list|,
name|moduleClass
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|OnModuleReference
argument_list|(
name|moduleClass
argument_list|,
name|method
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|onModuleReferences
operator|.
name|put
argument_list|(
name|plugin
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|onModuleReferences
operator|=
name|onModuleReferences
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|refreshInterval
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"info_refresh_interval"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|plugins
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Plugin
argument_list|>
name|plugins
parameter_list|()
block|{
return|return
name|plugins
return|;
block|}
DECL|method|processModules
specifier|public
name|void
name|processModules
parameter_list|(
name|Iterable
argument_list|<
name|Module
argument_list|>
name|modules
parameter_list|)
block|{
for|for
control|(
name|Module
name|module
range|:
name|modules
control|)
block|{
name|processModule
argument_list|(
name|module
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processModule
specifier|public
name|void
name|processModule
parameter_list|(
name|Module
name|module
parameter_list|)
block|{
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|plugin
operator|.
name|processModule
argument_list|(
name|module
argument_list|)
expr_stmt|;
comment|// see if there are onModule references
name|List
argument_list|<
name|OnModuleReference
argument_list|>
name|references
init|=
name|onModuleReferences
operator|.
name|get
argument_list|(
name|plugin
argument_list|)
decl_stmt|;
if|if
condition|(
name|references
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|OnModuleReference
name|reference
range|:
name|references
control|)
block|{
if|if
condition|(
name|reference
operator|.
name|moduleClass
operator|.
name|isAssignableFrom
argument_list|(
name|module
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|reference
operator|.
name|onModuleMethod
operator|.
name|invoke
argument_list|(
name|plugin
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"plugin {}, failed to invoke custom onModule method"
argument_list|,
name|e
argument_list|,
name|plugin
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
DECL|method|updatedSettings
specifier|public
name|Settings
name|updatedSettings
parameter_list|()
block|{
name|ImmutableSettings
operator|.
name|Builder
name|builder
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|this
operator|.
name|settings
argument_list|)
decl_stmt|;
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
operator|.
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|plugin
operator|.
name|additionalSettings
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|modules
specifier|public
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
block|{
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
argument_list|>
name|modules
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
operator|.
name|values
argument_list|()
control|)
block|{
name|modules
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|modules
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|modules
return|;
block|}
DECL|method|modules
specifier|public
name|Collection
argument_list|<
name|Module
argument_list|>
name|modules
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
operator|.
name|values
argument_list|()
control|)
block|{
name|modules
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|modules
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|modules
return|;
block|}
DECL|method|services
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
name|services
parameter_list|()
block|{
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|LifecycleComponent
argument_list|>
argument_list|>
name|services
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
operator|.
name|values
argument_list|()
control|)
block|{
name|services
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|services
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|services
return|;
block|}
DECL|method|indexModules
specifier|public
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
block|{
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
argument_list|>
name|modules
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
operator|.
name|values
argument_list|()
control|)
block|{
name|modules
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|indexModules
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|modules
return|;
block|}
DECL|method|indexModules
specifier|public
name|Collection
argument_list|<
name|Module
argument_list|>
name|indexModules
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
operator|.
name|values
argument_list|()
control|)
block|{
name|modules
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|indexModules
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|modules
return|;
block|}
DECL|method|indexServices
specifier|public
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
block|{
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CloseableIndexComponent
argument_list|>
argument_list|>
name|services
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
operator|.
name|values
argument_list|()
control|)
block|{
name|services
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|indexServices
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|services
return|;
block|}
DECL|method|shardModules
specifier|public
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
block|{
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
argument_list|>
name|modules
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
operator|.
name|values
argument_list|()
control|)
block|{
name|modules
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|shardModules
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|modules
return|;
block|}
DECL|method|shardModules
specifier|public
name|Collection
argument_list|<
name|Module
argument_list|>
name|shardModules
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
operator|.
name|values
argument_list|()
control|)
block|{
name|modules
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|shardModules
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|modules
return|;
block|}
DECL|method|shardServices
specifier|public
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
block|{
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CloseableIndexComponent
argument_list|>
argument_list|>
name|services
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
operator|.
name|values
argument_list|()
control|)
block|{
name|services
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|shardServices
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|services
return|;
block|}
comment|/**      * Get information about plugins (jvm and site plugins).      * Information are cached for 10 seconds by default. Modify `plugins.info_refresh_interval` property if needed.      * Setting `plugins.info_refresh_interval` to `-1` will cause infinite caching.      * Setting `plugins.info_refresh_interval` to `0` will disable caching.      * @return List of plugins information      */
DECL|method|info
specifier|synchronized
specifier|public
name|PluginsInfo
name|info
parameter_list|()
block|{
if|if
condition|(
name|refreshInterval
operator|.
name|millis
argument_list|()
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|cachedPluginsInfo
operator|!=
literal|null
operator|&&
operator|(
name|refreshInterval
operator|.
name|millis
argument_list|()
operator|<
literal|0
operator|||
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|lastRefresh
operator|)
operator|<
name|refreshInterval
operator|.
name|millis
argument_list|()
operator|)
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|logger
operator|.
name|trace
argument_list|(
literal|"using cache to retrieve plugins info"
argument_list|)
expr_stmt|;
return|return
name|cachedPluginsInfo
return|;
block|}
name|lastRefresh
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|logger
operator|.
name|trace
argument_list|(
literal|"starting to fetch info on plugins"
argument_list|)
expr_stmt|;
name|cachedPluginsInfo
operator|=
operator|new
name|PluginsInfo
argument_list|()
expr_stmt|;
comment|// We create a map to have only unique values
name|Set
argument_list|<
name|String
argument_list|>
name|plugins
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Plugin
name|plugin
range|:
name|plugins
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
comment|// We should detect if the plugin has also an embedded _site structure
name|File
name|siteFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|environment
operator|.
name|pluginsFile
argument_list|()
argument_list|,
name|plugin
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
literal|"_site"
argument_list|)
decl_stmt|;
name|boolean
name|isSite
init|=
name|siteFile
operator|.
name|exists
argument_list|()
operator|&&
name|siteFile
operator|.
name|isDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|logger
operator|.
name|trace
argument_list|(
literal|"found a jvm plugin [{}], [{}]{}"
argument_list|,
name|plugin
operator|.
name|name
argument_list|()
argument_list|,
name|plugin
operator|.
name|description
argument_list|()
argument_list|,
name|isSite
condition|?
literal|": with _site structure"
else|:
literal|""
argument_list|)
expr_stmt|;
name|cachedPluginsInfo
operator|.
name|add
argument_list|(
operator|new
name|PluginInfo
argument_list|(
name|plugin
operator|.
name|name
argument_list|()
argument_list|,
name|plugin
operator|.
name|description
argument_list|()
argument_list|,
name|isSite
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|plugins
operator|.
name|add
argument_list|(
name|plugin
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|File
name|pluginsFile
init|=
name|environment
operator|.
name|pluginsFile
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|pluginsFile
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|cachedPluginsInfo
return|;
block|}
if|if
condition|(
operator|!
name|pluginsFile
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
name|cachedPluginsInfo
return|;
block|}
name|File
index|[]
name|pluginsFiles
init|=
name|pluginsFile
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|pluginsFiles
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|plugin
range|:
name|pluginsFiles
control|)
block|{
comment|// We skip already known jvm plugins
if|if
condition|(
operator|!
name|plugins
operator|.
name|contains
argument_list|(
name|plugin
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|File
name|sitePluginDir
init|=
operator|new
name|File
argument_list|(
name|plugin
argument_list|,
literal|"_site"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sitePluginDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|plugin
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|description
init|=
literal|"No description found for "
operator|+
name|name
operator|+
literal|"."
decl_stmt|;
comment|// We check if es-plugin.properties exists in plugin/_site dir
name|File
name|pluginPropFile
init|=
operator|new
name|File
argument_list|(
name|sitePluginDir
argument_list|,
name|ES_PLUGIN_PROPERTIES
argument_list|)
decl_stmt|;
if|if
condition|(
name|pluginPropFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|Properties
name|pluginProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|pluginPropFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|pluginProps
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|description
operator|=
name|pluginProps
operator|.
name|getProperty
argument_list|(
literal|"description"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to load plugin description from ["
operator|+
name|pluginPropFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|logger
operator|.
name|trace
argument_list|(
literal|"found a site plugin [{}], [{}]"
argument_list|,
name|name
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|cachedPluginsInfo
operator|.
name|add
argument_list|(
operator|new
name|PluginInfo
argument_list|(
name|name
argument_list|,
name|description
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|cachedPluginsInfo
return|;
block|}
DECL|method|loadPluginsIntoClassLoader
specifier|private
name|void
name|loadPluginsIntoClassLoader
parameter_list|()
block|{
name|File
name|pluginsFile
init|=
name|environment
operator|.
name|pluginsFile
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|pluginsFile
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|pluginsFile
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return;
block|}
name|ClassLoader
name|classLoader
init|=
name|settings
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|Class
name|classLoaderClass
init|=
name|classLoader
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Method
name|addURL
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|classLoaderClass
operator|.
name|equals
argument_list|(
name|Object
operator|.
name|class
argument_list|)
condition|)
block|{
try|try
block|{
name|addURL
operator|=
name|classLoaderClass
operator|.
name|getDeclaredMethod
argument_list|(
literal|"addURL"
argument_list|,
name|URL
operator|.
name|class
argument_list|)
expr_stmt|;
name|addURL
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// no method, try the parent
name|classLoaderClass
operator|=
name|classLoaderClass
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|addURL
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to find addURL method on classLoader ["
operator|+
name|classLoader
operator|+
literal|"] to add methods"
argument_list|)
expr_stmt|;
return|return;
block|}
name|File
index|[]
name|pluginsFiles
init|=
name|pluginsFile
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|pluginsFile
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|pluginFile
range|:
name|pluginsFiles
control|)
block|{
if|if
condition|(
name|pluginFile
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"--- adding plugin ["
operator|+
name|pluginFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// add the root
name|addURL
operator|.
name|invoke
argument_list|(
name|classLoader
argument_list|,
name|pluginFile
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
comment|// gather files to add
name|List
argument_list|<
name|File
argument_list|>
name|libFiles
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|pluginFile
operator|.
name|listFiles
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|libFiles
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|pluginFile
operator|.
name|listFiles
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|File
name|libLocation
init|=
operator|new
name|File
argument_list|(
name|pluginFile
argument_list|,
literal|"lib"
argument_list|)
decl_stmt|;
if|if
condition|(
name|libLocation
operator|.
name|exists
argument_list|()
operator|&&
name|libLocation
operator|.
name|isDirectory
argument_list|()
operator|&&
name|libLocation
operator|.
name|listFiles
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|libFiles
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|libLocation
operator|.
name|listFiles
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// if there are jars in it, add it as well
for|for
control|(
name|File
name|libFile
range|:
name|libFiles
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|libFile
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
operator|||
name|libFile
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
operator|)
condition|)
block|{
continue|continue;
block|}
name|addURL
operator|.
name|invoke
argument_list|(
name|classLoader
argument_list|,
name|libFile
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to add plugin ["
operator|+
name|pluginFile
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to list plugins from {}. Check your right access."
argument_list|,
name|pluginsFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|loadPluginsFromClasspath
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Plugin
argument_list|>
name|loadPluginsFromClasspath
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Plugin
argument_list|>
name|plugins
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|URL
argument_list|>
name|pluginUrls
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pluginUrls
operator|=
name|settings
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResources
argument_list|(
name|ES_PLUGIN_PROPERTIES
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to find plugins from classpath"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
while|while
condition|(
name|pluginUrls
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|URL
name|pluginUrl
init|=
name|pluginUrls
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Properties
name|pluginProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|pluginUrl
operator|.
name|openStream
argument_list|()
expr_stmt|;
name|pluginProps
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|String
name|pluginClassName
init|=
name|pluginProps
operator|.
name|getProperty
argument_list|(
literal|"plugin"
argument_list|)
decl_stmt|;
name|Plugin
name|plugin
init|=
name|loadPlugin
argument_list|(
name|pluginClassName
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|plugins
operator|.
name|put
argument_list|(
name|plugin
operator|.
name|name
argument_list|()
argument_list|,
name|plugin
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to load plugin from ["
operator|+
name|pluginUrl
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
return|return
name|plugins
return|;
block|}
DECL|method|loadPlugin
specifier|private
name|Plugin
name|loadPlugin
parameter_list|(
name|String
name|className
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
name|pluginClass
init|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
operator|)
name|settings
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|className
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|pluginClass
operator|.
name|getConstructor
argument_list|(
name|Settings
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|(
name|settings
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
try|try
block|{
return|return
name|pluginClass
operator|.
name|getConstructor
argument_list|()
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"No constructor for ["
operator|+
name|pluginClass
operator|+
literal|"]. A plugin class must "
operator|+
literal|"have either an empty default constructor or a single argument constructor accepting a "
operator|+
literal|"Settings instance"
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Failed to load plugin class ["
operator|+
name|className
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

