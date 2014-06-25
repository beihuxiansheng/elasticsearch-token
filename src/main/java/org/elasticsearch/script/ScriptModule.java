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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|AbstractModule
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
name|multibindings
operator|.
name|MapBinder
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
name|multibindings
operator|.
name|Multibinder
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
name|script
operator|.
name|groovy
operator|.
name|GroovyScriptEngineService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|mustache
operator|.
name|MustacheScriptEngineService
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ScriptModule
specifier|public
class|class
name|ScriptModule
extends|extends
name|AbstractModule
block|{
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|scriptEngines
specifier|private
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|ScriptEngineService
argument_list|>
argument_list|>
name|scriptEngines
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|scripts
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|NativeScriptFactory
argument_list|>
argument_list|>
name|scripts
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|method|ScriptModule
specifier|public
name|ScriptModule
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
block|}
DECL|method|addScriptEngine
specifier|public
name|void
name|addScriptEngine
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|ScriptEngineService
argument_list|>
name|scriptEngine
parameter_list|)
block|{
name|scriptEngines
operator|.
name|add
argument_list|(
name|scriptEngine
argument_list|)
expr_stmt|;
block|}
DECL|method|registerScript
specifier|public
name|void
name|registerScript
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|NativeScriptFactory
argument_list|>
name|script
parameter_list|)
block|{
name|scripts
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|script
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|MapBinder
argument_list|<
name|String
argument_list|,
name|NativeScriptFactory
argument_list|>
name|scriptsBinder
init|=
name|MapBinder
operator|.
name|newMapBinder
argument_list|(
name|binder
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|NativeScriptFactory
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|NativeScriptFactory
argument_list|>
argument_list|>
name|entry
range|:
name|scripts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|scriptsBinder
operator|.
name|addBinding
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|to
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// now, check for config based ones
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|nativeSettings
init|=
name|settings
operator|.
name|getGroups
argument_list|(
literal|"script.native"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|entry
range|:
name|nativeSettings
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|NativeScriptFactory
argument_list|>
name|type
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getAsClass
argument_list|(
literal|"type"
argument_list|,
name|NativeScriptFactory
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|NativeScriptFactory
operator|.
name|class
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"type is missing for native script ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|scriptsBinder
operator|.
name|addBinding
argument_list|(
name|name
argument_list|)
operator|.
name|to
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
name|Multibinder
argument_list|<
name|ScriptEngineService
argument_list|>
name|multibinder
init|=
name|Multibinder
operator|.
name|newSetBinder
argument_list|(
name|binder
argument_list|()
argument_list|,
name|ScriptEngineService
operator|.
name|class
argument_list|)
decl_stmt|;
name|multibinder
operator|.
name|addBinding
argument_list|()
operator|.
name|to
argument_list|(
name|NativeScriptEngineService
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|settings
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
literal|"groovy.lang.GroovyClassLoader"
argument_list|)
expr_stmt|;
name|multibinder
operator|.
name|addBinding
argument_list|()
operator|.
name|to
argument_list|(
name|GroovyScriptEngineService
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|Loggers
operator|.
name|getLogger
argument_list|(
name|ScriptService
operator|.
name|class
argument_list|,
name|settings
argument_list|)
operator|.
name|debug
argument_list|(
literal|"failed to load groovy"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|settings
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
literal|"com.github.mustachejava.Mustache"
argument_list|)
expr_stmt|;
name|multibinder
operator|.
name|addBinding
argument_list|()
operator|.
name|to
argument_list|(
name|MustacheScriptEngineService
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|Loggers
operator|.
name|getLogger
argument_list|(
name|ScriptService
operator|.
name|class
argument_list|,
name|settings
argument_list|)
operator|.
name|debug
argument_list|(
literal|"failed to load mustache"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|ScriptEngineService
argument_list|>
name|scriptEngine
range|:
name|scriptEngines
control|)
block|{
name|multibinder
operator|.
name|addBinding
argument_list|()
operator|.
name|to
argument_list|(
name|scriptEngine
argument_list|)
expr_stmt|;
block|}
name|bind
argument_list|(
name|ScriptService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

