begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|base
operator|.
name|Charsets
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
name|cache
operator|.
name|Cache
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
name|cache
operator|.
name|CacheBuilder
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
name|Nullable
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
name|common
operator|.
name|io
operator|.
name|Streams
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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
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
name|fielddata
operator|.
name|IndexFieldDataService
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
name|mapper
operator|.
name|MapperService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
operator|.
name|SearchLookup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|watcher
operator|.
name|FileChangesListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|watcher
operator|.
name|FileWatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|watcher
operator|.
name|ResourceWatcherService
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
name|InputStreamReader
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ScriptService
specifier|public
class|class
name|ScriptService
extends|extends
name|AbstractComponent
block|{
DECL|field|defaultLang
specifier|private
specifier|final
name|String
name|defaultLang
decl_stmt|;
DECL|field|scriptEngines
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|ScriptEngineService
argument_list|>
name|scriptEngines
decl_stmt|;
DECL|field|staticCache
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|CompiledScript
argument_list|>
name|staticCache
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Cache
argument_list|<
name|CacheKey
argument_list|,
name|CompiledScript
argument_list|>
name|cache
decl_stmt|;
DECL|field|scriptsDirectory
specifier|private
specifier|final
name|File
name|scriptsDirectory
decl_stmt|;
DECL|field|disableDynamic
specifier|private
specifier|final
name|boolean
name|disableDynamic
decl_stmt|;
annotation|@
name|Inject
DECL|method|ScriptService
specifier|public
name|ScriptService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Environment
name|env
parameter_list|,
name|Set
argument_list|<
name|ScriptEngineService
argument_list|>
name|scriptEngines
parameter_list|,
name|ResourceWatcherService
name|resourceWatcherService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|int
name|cacheMaxSize
init|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"cache.max_size"
argument_list|,
literal|500
argument_list|)
decl_stmt|;
name|TimeValue
name|cacheExpire
init|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"cache.expire"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using script cache with max_size [{}], expire [{}]"
argument_list|,
name|cacheMaxSize
argument_list|,
name|cacheExpire
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultLang
operator|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"default_lang"
argument_list|,
literal|"mvel"
argument_list|)
expr_stmt|;
name|this
operator|.
name|disableDynamic
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"disable_dynamic"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|CacheBuilder
name|cacheBuilder
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|cacheMaxSize
operator|>=
literal|0
condition|)
block|{
name|cacheBuilder
operator|.
name|maximumSize
argument_list|(
name|cacheMaxSize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cacheExpire
operator|!=
literal|null
condition|)
block|{
name|cacheBuilder
operator|.
name|expireAfterAccess
argument_list|(
name|cacheExpire
operator|.
name|nanos
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|cache
operator|=
name|cacheBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|ScriptEngineService
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|ScriptEngineService
name|scriptEngine
range|:
name|scriptEngines
control|)
block|{
for|for
control|(
name|String
name|type
range|:
name|scriptEngine
operator|.
name|types
argument_list|()
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|scriptEngine
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|scriptEngines
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// put some default optimized scripts
name|staticCache
operator|.
name|put
argument_list|(
literal|"doc.score"
argument_list|,
operator|new
name|CompiledScript
argument_list|(
literal|"native"
argument_list|,
operator|new
name|DocScoreNativeScriptFactory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// add file watcher for static scripts
name|scriptsDirectory
operator|=
operator|new
name|File
argument_list|(
name|env
operator|.
name|configFile
argument_list|()
argument_list|,
literal|"scripts"
argument_list|)
expr_stmt|;
name|FileWatcher
name|fileWatcher
init|=
operator|new
name|FileWatcher
argument_list|(
name|scriptsDirectory
argument_list|)
decl_stmt|;
name|fileWatcher
operator|.
name|addListener
argument_list|(
operator|new
name|ScriptChangesListener
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"auto_reload_enabled"
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|// automatic reload is enabled - register scripts
name|resourceWatcherService
operator|.
name|add
argument_list|(
name|fileWatcher
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// automatic reload is disable just load scripts once
name|fileWatcher
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|ScriptEngineService
name|engineService
range|:
name|scriptEngines
operator|.
name|values
argument_list|()
control|)
block|{
name|engineService
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|compile
specifier|public
name|CompiledScript
name|compile
parameter_list|(
name|String
name|script
parameter_list|)
block|{
return|return
name|compile
argument_list|(
name|defaultLang
argument_list|,
name|script
argument_list|)
return|;
block|}
DECL|method|compile
specifier|public
name|CompiledScript
name|compile
parameter_list|(
name|String
name|lang
parameter_list|,
name|String
name|script
parameter_list|)
block|{
name|CompiledScript
name|compiled
init|=
name|staticCache
operator|.
name|get
argument_list|(
name|script
argument_list|)
decl_stmt|;
if|if
condition|(
name|compiled
operator|!=
literal|null
condition|)
block|{
return|return
name|compiled
return|;
block|}
if|if
condition|(
name|lang
operator|==
literal|null
condition|)
block|{
name|lang
operator|=
name|defaultLang
expr_stmt|;
block|}
if|if
condition|(
name|dynamicScriptDisabled
argument_list|(
name|lang
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ScriptException
argument_list|(
literal|"dynamic scripting disabled"
argument_list|)
throw|;
block|}
name|CacheKey
name|cacheKey
init|=
operator|new
name|CacheKey
argument_list|(
name|lang
argument_list|,
name|script
argument_list|)
decl_stmt|;
name|compiled
operator|=
name|cache
operator|.
name|getIfPresent
argument_list|(
name|cacheKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|compiled
operator|!=
literal|null
condition|)
block|{
return|return
name|compiled
return|;
block|}
comment|// not the end of the world if we compile it twice...
name|ScriptEngineService
name|service
init|=
name|scriptEngines
operator|.
name|get
argument_list|(
name|lang
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"script_lang not supported ["
operator|+
name|lang
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|compiled
operator|=
operator|new
name|CompiledScript
argument_list|(
name|lang
argument_list|,
name|service
operator|.
name|compile
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|cacheKey
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
return|return
name|compiled
return|;
block|}
DECL|method|executable
specifier|public
name|ExecutableScript
name|executable
parameter_list|(
name|String
name|lang
parameter_list|,
name|String
name|script
parameter_list|,
name|Map
name|vars
parameter_list|)
block|{
return|return
name|executable
argument_list|(
name|compile
argument_list|(
name|lang
argument_list|,
name|script
argument_list|)
argument_list|,
name|vars
argument_list|)
return|;
block|}
DECL|method|executable
specifier|public
name|ExecutableScript
name|executable
parameter_list|(
name|CompiledScript
name|compiledScript
parameter_list|,
name|Map
name|vars
parameter_list|)
block|{
return|return
name|scriptEngines
operator|.
name|get
argument_list|(
name|compiledScript
operator|.
name|lang
argument_list|()
argument_list|)
operator|.
name|executable
argument_list|(
name|compiledScript
operator|.
name|compiled
argument_list|()
argument_list|,
name|vars
argument_list|)
return|;
block|}
DECL|method|search
specifier|public
name|SearchScript
name|search
parameter_list|(
name|CompiledScript
name|compiledScript
parameter_list|,
name|SearchLookup
name|lookup
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
return|return
name|scriptEngines
operator|.
name|get
argument_list|(
name|compiledScript
operator|.
name|lang
argument_list|()
argument_list|)
operator|.
name|search
argument_list|(
name|compiledScript
operator|.
name|compiled
argument_list|()
argument_list|,
name|lookup
argument_list|,
name|vars
argument_list|)
return|;
block|}
DECL|method|search
specifier|public
name|SearchScript
name|search
parameter_list|(
name|SearchLookup
name|lookup
parameter_list|,
name|String
name|lang
parameter_list|,
name|String
name|script
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
return|return
name|search
argument_list|(
name|compile
argument_list|(
name|lang
argument_list|,
name|script
argument_list|)
argument_list|,
name|lookup
argument_list|,
name|vars
argument_list|)
return|;
block|}
DECL|method|search
specifier|public
name|SearchScript
name|search
parameter_list|(
name|MapperService
name|mapperService
parameter_list|,
name|IndexFieldDataService
name|fieldDataService
parameter_list|,
name|String
name|lang
parameter_list|,
name|String
name|script
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
return|return
name|search
argument_list|(
name|compile
argument_list|(
name|lang
argument_list|,
name|script
argument_list|)
argument_list|,
operator|new
name|SearchLookup
argument_list|(
name|mapperService
argument_list|,
name|fieldDataService
argument_list|,
literal|null
argument_list|)
argument_list|,
name|vars
argument_list|)
return|;
block|}
DECL|method|execute
specifier|public
name|Object
name|execute
parameter_list|(
name|CompiledScript
name|compiledScript
parameter_list|,
name|Map
name|vars
parameter_list|)
block|{
return|return
name|scriptEngines
operator|.
name|get
argument_list|(
name|compiledScript
operator|.
name|lang
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|(
name|compiledScript
operator|.
name|compiled
argument_list|()
argument_list|,
name|vars
argument_list|)
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
DECL|method|dynamicScriptDisabled
specifier|private
name|boolean
name|dynamicScriptDisabled
parameter_list|(
name|String
name|lang
parameter_list|)
block|{
if|if
condition|(
operator|!
name|disableDynamic
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// we allow "native" executions since they register through plugins, so they are "allowed"
return|return
operator|!
literal|"native"
operator|.
name|equals
argument_list|(
name|lang
argument_list|)
return|;
block|}
DECL|class|ScriptChangesListener
specifier|private
class|class
name|ScriptChangesListener
extends|extends
name|FileChangesListener
block|{
DECL|method|scriptNameExt
specifier|private
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|scriptNameExt
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|String
name|scriptPath
init|=
name|scriptsDirectory
operator|.
name|toURI
argument_list|()
operator|.
name|relativize
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|int
name|extIndex
init|=
name|scriptPath
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|extIndex
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|ext
init|=
name|scriptPath
operator|.
name|substring
argument_list|(
name|extIndex
operator|+
literal|1
argument_list|)
decl_stmt|;
name|String
name|scriptName
init|=
name|scriptPath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|extIndex
argument_list|)
operator|.
name|replace
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|,
literal|'_'
argument_list|)
decl_stmt|;
return|return
operator|new
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|scriptName
argument_list|,
name|ext
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|onFileInit
specifier|public
name|void
name|onFileInit
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|scriptNameExt
init|=
name|scriptNameExt
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|scriptNameExt
operator|!=
literal|null
condition|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ScriptEngineService
name|engineService
range|:
name|scriptEngines
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|s
range|:
name|engineService
operator|.
name|extensions
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
name|scriptNameExt
operator|.
name|v2
argument_list|()
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"compiling script file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|script
init|=
name|Streams
operator|.
name|copyToString
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|staticCache
operator|.
name|put
argument_list|(
name|scriptNameExt
operator|.
name|v1
argument_list|()
argument_list|,
operator|new
name|CompiledScript
argument_list|(
name|engineService
operator|.
name|types
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|engineService
operator|.
name|compile
argument_list|(
name|script
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to load/compile script [{}]"
argument_list|,
name|e
argument_list|,
name|scriptNameExt
operator|.
name|v1
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
if|if
condition|(
name|found
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"no script engine found for [{}]"
argument_list|,
name|scriptNameExt
operator|.
name|v2
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onFileCreated
specifier|public
name|void
name|onFileCreated
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|onFileInit
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFileDeleted
specifier|public
name|void
name|onFileDeleted
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|scriptNameExt
init|=
name|scriptNameExt
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"removing script file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|staticCache
operator|.
name|remove
argument_list|(
name|scriptNameExt
operator|.
name|v1
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFileChanged
specifier|public
name|void
name|onFileChanged
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|onFileInit
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CacheKey
specifier|public
specifier|static
class|class
name|CacheKey
block|{
DECL|field|lang
specifier|public
specifier|final
name|String
name|lang
decl_stmt|;
DECL|field|script
specifier|public
specifier|final
name|String
name|script
decl_stmt|;
DECL|method|CacheKey
specifier|public
name|CacheKey
parameter_list|(
name|String
name|lang
parameter_list|,
name|String
name|script
parameter_list|)
block|{
name|this
operator|.
name|lang
operator|=
name|lang
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|CacheKey
name|other
init|=
operator|(
name|CacheKey
operator|)
name|o
decl_stmt|;
return|return
name|lang
operator|.
name|equals
argument_list|(
name|other
operator|.
name|lang
argument_list|)
operator|&&
name|script
operator|.
name|equals
argument_list|(
name|other
operator|.
name|script
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|lang
operator|.
name|hashCode
argument_list|()
operator|+
literal|31
operator|*
name|script
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
DECL|class|DocScoreNativeScriptFactory
specifier|public
specifier|static
class|class
name|DocScoreNativeScriptFactory
implements|implements
name|NativeScriptFactory
block|{
annotation|@
name|Override
DECL|method|newScript
specifier|public
name|ExecutableScript
name|newScript
parameter_list|(
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
return|return
operator|new
name|DocScoreSearchScript
argument_list|()
return|;
block|}
block|}
DECL|class|DocScoreSearchScript
specifier|public
specifier|static
class|class
name|DocScoreSearchScript
extends|extends
name|AbstractFloatSearchScript
block|{
annotation|@
name|Override
DECL|method|runAsFloat
specifier|public
name|float
name|runAsFloat
parameter_list|()
block|{
try|try
block|{
return|return
name|doc
argument_list|()
operator|.
name|score
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

