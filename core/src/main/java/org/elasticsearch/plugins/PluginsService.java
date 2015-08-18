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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

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
name|ImmutableMap
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
name|PluginsInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
operator|.
name|Bootstrap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
operator|.
name|JarHell
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
name|collect
operator|.
name|Tuple
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
name|io
operator|.
name|FileSystemUtils
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|env
operator|.
name|Environment
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
name|io
operator|.
name|IOException
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
name|net
operator|.
name|URLClassLoader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|DirectoryStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|FileSystemUtils
operator|.
name|isAccessibleDirectory
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
comment|/**      * We keep around a list of plugins      */
DECL|field|plugins
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
argument_list|>
name|plugins
decl_stmt|;
DECL|field|info
specifier|private
specifier|final
name|PluginsInfo
name|info
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
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
argument_list|>
name|tupleBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// first we load specified plugins via 'plugin.types' settings parameter.
comment|// this is a hack for what is between unit and integration tests...
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
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|PluginInfo
name|pluginInfo
init|=
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
literal|false
argument_list|,
literal|"NA"
argument_list|,
literal|true
argument_list|,
name|pluginClass
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"plugin loaded from settings [{}]"
argument_list|,
name|pluginInfo
argument_list|)
expr_stmt|;
block|}
name|tupleBuilder
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|pluginInfo
argument_list|,
name|plugin
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// now, find all the ones that are in plugins/
try|try
block|{
name|List
argument_list|<
name|Bundle
argument_list|>
name|bundles
init|=
name|getPluginBundles
argument_list|(
name|environment
argument_list|)
decl_stmt|;
name|tupleBuilder
operator|.
name|addAll
argument_list|(
name|loadBundles
argument_list|(
name|bundles
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
name|plugins
operator|=
name|tupleBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|info
operator|=
operator|new
name|PluginsInfo
argument_list|()
expr_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
name|tuple
range|:
name|plugins
control|)
block|{
name|info
operator|.
name|add
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// We need to build a List of jvm and site plugins for checking mandatory plugins
name|Map
argument_list|<
name|String
argument_list|,
name|Plugin
argument_list|>
name|jvmPlugins
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|sitePlugins
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
name|tuple
range|:
name|plugins
control|)
block|{
name|PluginInfo
name|info
init|=
name|tuple
operator|.
name|v1
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|isJvm
argument_list|()
condition|)
block|{
name|jvmPlugins
operator|.
name|put
argument_list|(
name|tuple
operator|.
name|v2
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|tuple
operator|.
name|v2
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|isSite
argument_list|()
condition|)
block|{
name|sitePlugins
operator|.
name|add
argument_list|(
name|info
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Checking expected plugins
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
operator|new
name|HashSet
argument_list|<>
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
name|jvmPlugins
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
name|jvmPlugins
operator|.
name|keySet
argument_list|()
argument_list|,
name|sitePlugins
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
name|jvmPlugins
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
operator|new
name|ArrayList
argument_list|<>
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
name|getMethods
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
block|}
DECL|method|plugins
specifier|public
name|ImmutableList
argument_list|<
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
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
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
name|plugin
range|:
name|plugins
argument_list|()
control|)
block|{
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
operator|.
name|v2
argument_list|()
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
operator|.
name|v2
argument_list|()
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
name|v2
argument_list|()
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
specifier|final
name|Settings
operator|.
name|Builder
name|builder
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
name|plugin
range|:
name|plugins
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|plugin
operator|.
name|v2
argument_list|()
operator|.
name|additionalSettings
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|put
argument_list|(
name|this
operator|.
name|settings
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|nodeModules
specifier|public
name|Collection
argument_list|<
name|Module
argument_list|>
name|nodeModules
parameter_list|()
block|{
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
name|plugin
range|:
name|plugins
control|)
block|{
name|modules
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|v2
argument_list|()
operator|.
name|nodeModules
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|modules
return|;
block|}
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
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
name|plugin
range|:
name|plugins
control|)
block|{
name|services
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|v2
argument_list|()
operator|.
name|nodeServices
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
name|Module
argument_list|>
name|indexModules
parameter_list|()
block|{
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
name|plugin
range|:
name|plugins
control|)
block|{
name|modules
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|v2
argument_list|()
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
DECL|method|indexServices
specifier|public
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Closeable
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
name|Closeable
argument_list|>
argument_list|>
name|services
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
name|plugin
range|:
name|plugins
control|)
block|{
name|services
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|v2
argument_list|()
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
name|Module
argument_list|>
name|shardModules
parameter_list|()
block|{
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
name|plugin
range|:
name|plugins
control|)
block|{
name|modules
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|v2
argument_list|()
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
DECL|method|shardServices
specifier|public
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Closeable
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
name|Closeable
argument_list|>
argument_list|>
name|services
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
name|plugin
range|:
name|plugins
control|)
block|{
name|services
operator|.
name|addAll
argument_list|(
name|plugin
operator|.
name|v2
argument_list|()
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
comment|/**      * Get information about plugins (jvm and site plugins).      */
DECL|method|info
specifier|public
name|PluginsInfo
name|info
parameter_list|()
block|{
return|return
name|info
return|;
block|}
comment|// a "bundle" is a group of plugins in a single classloader
comment|// really should be 1-1, but we are not so fortunate
DECL|class|Bundle
specifier|static
class|class
name|Bundle
block|{
DECL|field|plugins
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|plugins
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|urls
name|List
argument_list|<
name|URL
argument_list|>
name|urls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
block|}
DECL|method|getPluginBundles
specifier|static
name|List
argument_list|<
name|Bundle
argument_list|>
name|getPluginBundles
parameter_list|(
name|Environment
name|environment
parameter_list|)
throws|throws
name|IOException
block|{
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Bootstrap
operator|.
name|class
argument_list|)
decl_stmt|;
name|Path
name|pluginsDirectory
init|=
name|environment
operator|.
name|pluginsFile
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isAccessibleDirectory
argument_list|(
name|pluginsDirectory
argument_list|,
name|logger
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|Bundle
argument_list|>
name|bundles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// a special purgatory for plugins that directly depend on each other
name|bundles
operator|.
name|add
argument_list|(
operator|new
name|Bundle
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|pluginsDirectory
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|plugin
range|:
name|stream
control|)
block|{
if|if
condition|(
name|FileSystemUtils
operator|.
name|isHidden
argument_list|(
name|plugin
argument_list|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"--- skip hidden plugin file[{}]"
argument_list|,
name|plugin
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|logger
operator|.
name|trace
argument_list|(
literal|"--- adding plugin [{}]"
argument_list|,
name|plugin
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|PluginInfo
name|info
init|=
name|PluginInfo
operator|.
name|readFromProperties
argument_list|(
name|plugin
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|URL
argument_list|>
name|urls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|isJvm
argument_list|()
condition|)
block|{
comment|// a jvm plugin: gather urls for jar files
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|jarStream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|plugin
argument_list|,
literal|"*.jar"
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|jar
range|:
name|jarStream
control|)
block|{
name|urls
operator|.
name|add
argument_list|(
name|jar
operator|.
name|toUri
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|Bundle
name|bundle
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|isJvm
argument_list|()
operator|&&
name|info
operator|.
name|isIsolated
argument_list|()
operator|==
literal|false
condition|)
block|{
name|bundle
operator|=
name|bundles
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// purgatory
block|}
else|else
block|{
name|bundle
operator|=
operator|new
name|Bundle
argument_list|()
expr_stmt|;
name|bundles
operator|.
name|add
argument_list|(
name|bundle
argument_list|)
expr_stmt|;
block|}
name|bundle
operator|.
name|plugins
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|bundle
operator|.
name|urls
operator|.
name|addAll
argument_list|(
name|urls
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bundles
return|;
block|}
DECL|method|loadBundles
specifier|private
name|List
argument_list|<
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
argument_list|>
name|loadBundles
parameter_list|(
name|List
argument_list|<
name|Bundle
argument_list|>
name|bundles
parameter_list|)
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Tuple
argument_list|<
name|PluginInfo
argument_list|,
name|Plugin
argument_list|>
argument_list|>
name|plugins
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Bundle
name|bundle
range|:
name|bundles
control|)
block|{
comment|// jar-hell check the bundle against the parent classloader
comment|// pluginmanager does it, but we do it again, in case lusers mess with jar files manually
try|try
block|{
specifier|final
name|List
argument_list|<
name|URL
argument_list|>
name|jars
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ClassLoader
name|parentLoader
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentLoader
operator|instanceof
name|URLClassLoader
condition|)
block|{
for|for
control|(
name|URL
name|url
range|:
operator|(
operator|(
name|URLClassLoader
operator|)
name|parentLoader
operator|)
operator|.
name|getURLs
argument_list|()
control|)
block|{
name|jars
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
block|}
name|jars
operator|.
name|addAll
argument_list|(
name|bundle
operator|.
name|urls
argument_list|)
expr_stmt|;
name|JarHell
operator|.
name|checkJarHell
argument_list|(
name|jars
operator|.
name|toArray
argument_list|(
operator|new
name|URL
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed to load bundle "
operator|+
name|bundle
operator|.
name|urls
operator|+
literal|" due to jar hell"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// create a child to load the plugins in this bundle
name|ClassLoader
name|loader
init|=
name|URLClassLoader
operator|.
name|newInstance
argument_list|(
name|bundle
operator|.
name|urls
operator|.
name|toArray
argument_list|(
operator|new
name|URL
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|PluginInfo
name|pluginInfo
range|:
name|bundle
operator|.
name|plugins
control|)
block|{
specifier|final
name|Plugin
name|plugin
decl_stmt|;
if|if
condition|(
name|pluginInfo
operator|.
name|isJvm
argument_list|()
condition|)
block|{
name|plugin
operator|=
name|loadPlugin
argument_list|(
name|pluginInfo
operator|.
name|getClassname
argument_list|()
argument_list|,
name|settings
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|plugin
operator|=
operator|new
name|SitePlugin
argument_list|(
name|pluginInfo
operator|.
name|getName
argument_list|()
argument_list|,
name|pluginInfo
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|plugins
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|pluginInfo
argument_list|,
name|plugin
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|plugins
operator|.
name|build
argument_list|()
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
parameter_list|,
name|ClassLoader
name|loader
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
name|loader
operator|.
name|loadClass
argument_list|(
name|className
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|Plugin
operator|.
name|class
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
name|Throwable
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

