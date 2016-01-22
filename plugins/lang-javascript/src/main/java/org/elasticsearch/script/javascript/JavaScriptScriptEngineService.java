begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.javascript
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|javascript
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Scorer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|SpecialPermission
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
name|BootstrapInfo
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
name|ClassPermission
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
name|CompiledScript
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
name|ExecutableScript
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
name|LeafSearchScript
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
name|ScoreAccessor
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
name|ScriptEngineService
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
name|SearchScript
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
name|javascript
operator|.
name|support
operator|.
name|NativeList
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
name|javascript
operator|.
name|support
operator|.
name|NativeMap
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
name|javascript
operator|.
name|support
operator|.
name|ScriptValueConverter
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
name|LeafSearchLookup
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
name|mozilla
operator|.
name|javascript
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|ContextFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|GeneratedClassLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|PolicySecurityController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|Script
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|Scriptable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|ScriptableObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|SecurityController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|WrapFactory
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
name|net
operator|.
name|MalformedURLException
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
name|security
operator|.
name|AccessControlContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|CodeSource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|Certificate
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|JavaScriptScriptEngineService
specifier|public
class|class
name|JavaScriptScriptEngineService
extends|extends
name|AbstractComponent
implements|implements
name|ScriptEngineService
block|{
DECL|field|TYPES
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|TYPES
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"js"
argument_list|,
literal|"javascript"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|counter
specifier|private
specifier|final
name|AtomicLong
name|counter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|wrapFactory
specifier|private
specifier|static
name|WrapFactory
name|wrapFactory
init|=
operator|new
name|CustomWrapFactory
argument_list|()
decl_stmt|;
DECL|field|globalScope
specifier|private
name|Scriptable
name|globalScope
decl_stmt|;
comment|// one time initialization of rhino security manager integration
DECL|field|DOMAIN
specifier|private
specifier|static
specifier|final
name|CodeSource
name|DOMAIN
decl_stmt|;
DECL|field|OPTIMIZATION_LEVEL
specifier|private
specifier|static
specifier|final
name|int
name|OPTIMIZATION_LEVEL
init|=
literal|1
decl_stmt|;
static|static
block|{
try|try
block|{
name|DOMAIN
operator|=
operator|new
name|CodeSource
argument_list|(
operator|new
name|URL
argument_list|(
literal|"file:"
operator|+
name|BootstrapInfo
operator|.
name|UNTRUSTED_CODEBASE
argument_list|)
argument_list|,
operator|(
name|Certificate
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|ContextFactory
name|factory
init|=
operator|new
name|ContextFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|onContextCreated
parameter_list|(
name|Context
name|cx
parameter_list|)
block|{
name|cx
operator|.
name|setWrapFactory
argument_list|(
name|wrapFactory
argument_list|)
expr_stmt|;
name|cx
operator|.
name|setOptimizationLevel
argument_list|(
name|OPTIMIZATION_LEVEL
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|getSecurityManager
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|initApplicationClassLoader
argument_list|(
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|ClassLoader
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClassLoader
name|run
parameter_list|()
block|{
comment|// snapshot our context (which has permissions for classes), since the script has none
specifier|final
name|AccessControlContext
name|engineContext
init|=
name|AccessController
operator|.
name|getContext
argument_list|()
decl_stmt|;
return|return
operator|new
name|ClassLoader
argument_list|(
name|JavaScriptScriptEngineService
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Class
argument_list|<
name|?
argument_list|>
name|loadClass
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|resolve
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
try|try
block|{
name|engineContext
operator|.
name|checkPermission
argument_list|(
operator|new
name|ClassPermission
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ClassNotFoundException
argument_list|(
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|super
operator|.
name|loadClass
argument_list|(
name|name
argument_list|,
name|resolve
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|factory
operator|.
name|seal
argument_list|()
expr_stmt|;
name|ContextFactory
operator|.
name|initGlobal
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|SecurityController
operator|.
name|initGlobal
argument_list|(
operator|new
name|PolicySecurityController
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|GeneratedClassLoader
name|createClassLoader
parameter_list|(
name|ClassLoader
name|parent
parameter_list|,
name|Object
name|securityDomain
parameter_list|)
block|{
comment|// don't let scripts compile other scripts
name|SecurityManager
name|sm
init|=
name|System
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|sm
operator|!=
literal|null
condition|)
block|{
name|sm
operator|.
name|checkPermission
argument_list|(
operator|new
name|SpecialPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check the domain, this is all we allow
if|if
condition|(
name|securityDomain
operator|!=
name|DOMAIN
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"illegal securityDomain: "
operator|+
name|securityDomain
argument_list|)
throw|;
block|}
return|return
name|super
operator|.
name|createClassLoader
argument_list|(
name|parent
argument_list|,
name|securityDomain
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** ensures this engine is initialized */
DECL|method|init
specifier|public
specifier|static
name|void
name|init
parameter_list|()
block|{}
annotation|@
name|Inject
DECL|method|JavaScriptScriptEngineService
specifier|public
name|JavaScriptScriptEngineService
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
name|globalScope
operator|=
name|ctx
operator|.
name|initStandardObjects
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{      }
annotation|@
name|Override
DECL|method|scriptRemoved
specifier|public
name|void
name|scriptRemoved
parameter_list|(
annotation|@
name|Nullable
name|CompiledScript
name|compiledScript
parameter_list|)
block|{
comment|// Nothing to do here
block|}
annotation|@
name|Override
DECL|method|types
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|types
parameter_list|()
block|{
return|return
name|TYPES
return|;
block|}
annotation|@
name|Override
DECL|method|extensions
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"js"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sandboxed
specifier|public
name|boolean
name|sandboxed
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|compile
specifier|public
name|Object
name|compile
parameter_list|(
name|String
name|script
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|ctx
operator|.
name|compileString
argument_list|(
name|script
argument_list|,
name|generateScriptName
argument_list|()
argument_list|,
literal|1
argument_list|,
name|DOMAIN
argument_list|)
return|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|executable
specifier|public
name|ExecutableScript
name|executable
parameter_list|(
name|CompiledScript
name|compiledScript
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
name|Scriptable
name|scope
init|=
name|ctx
operator|.
name|newObject
argument_list|(
name|globalScope
argument_list|)
decl_stmt|;
name|scope
operator|.
name|setPrototype
argument_list|(
name|globalScope
argument_list|)
expr_stmt|;
name|scope
operator|.
name|setParentScope
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|vars
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|JavaScriptExecutableScript
argument_list|(
operator|(
name|Script
operator|)
name|compiledScript
operator|.
name|compiled
argument_list|()
argument_list|,
name|scope
argument_list|)
return|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|SearchScript
name|search
parameter_list|(
specifier|final
name|CompiledScript
name|compiledScript
parameter_list|,
specifier|final
name|SearchLookup
name|lookup
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Scriptable
name|scope
init|=
name|ctx
operator|.
name|newObject
argument_list|(
name|globalScope
argument_list|)
decl_stmt|;
name|scope
operator|.
name|setPrototype
argument_list|(
name|globalScope
argument_list|)
expr_stmt|;
name|scope
operator|.
name|setParentScope
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
operator|new
name|SearchScript
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LeafSearchScript
name|getLeafSearchScript
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafSearchLookup
name|leafLookup
init|=
name|lookup
operator|.
name|getLeafSearchLookup
argument_list|(
name|context
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
name|Object
argument_list|>
name|entry
range|:
name|leafLookup
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|vars
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|vars
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|JavaScriptSearchScript
argument_list|(
operator|(
name|Script
operator|)
name|compiledScript
operator|.
name|compiled
argument_list|()
argument_list|,
name|scope
argument_list|,
name|leafLookup
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
comment|// TODO: can we reliably know if a javascript script makes use of _score
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|generateScriptName
specifier|private
name|String
name|generateScriptName
parameter_list|()
block|{
return|return
literal|"Script"
operator|+
name|counter
operator|.
name|incrementAndGet
argument_list|()
operator|+
literal|".js"
return|;
block|}
DECL|class|JavaScriptExecutableScript
specifier|public
specifier|static
class|class
name|JavaScriptExecutableScript
implements|implements
name|ExecutableScript
block|{
DECL|field|script
specifier|private
specifier|final
name|Script
name|script
decl_stmt|;
DECL|field|scope
specifier|private
specifier|final
name|Scriptable
name|scope
decl_stmt|;
DECL|method|JavaScriptExecutableScript
specifier|public
name|JavaScriptExecutableScript
parameter_list|(
name|Script
name|script
parameter_list|,
name|Scriptable
name|scope
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|ScriptValueConverter
operator|.
name|unwrapValue
argument_list|(
name|script
operator|.
name|exec
argument_list|(
name|ctx
argument_list|,
name|scope
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setNextVar
specifier|public
name|void
name|setNextVar
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|unwrap
specifier|public
name|Object
name|unwrap
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|ScriptValueConverter
operator|.
name|unwrapValue
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
DECL|class|JavaScriptSearchScript
specifier|public
specifier|static
class|class
name|JavaScriptSearchScript
implements|implements
name|LeafSearchScript
block|{
DECL|field|script
specifier|private
specifier|final
name|Script
name|script
decl_stmt|;
DECL|field|scope
specifier|private
specifier|final
name|Scriptable
name|scope
decl_stmt|;
DECL|field|lookup
specifier|private
specifier|final
name|LeafSearchLookup
name|lookup
decl_stmt|;
DECL|method|JavaScriptSearchScript
specifier|public
name|JavaScriptSearchScript
parameter_list|(
name|Script
name|script
parameter_list|,
name|Scriptable
name|scope
parameter_list|,
name|LeafSearchLookup
name|lookup
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
name|this
operator|.
name|lookup
operator|=
name|lookup
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
literal|"_score"
argument_list|,
name|wrapFactory
operator|.
name|wrapAsJavaObject
argument_list|(
name|ctx
argument_list|,
name|scope
argument_list|,
operator|new
name|ScoreAccessor
argument_list|(
name|scorer
argument_list|)
argument_list|,
name|ScoreAccessor
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|lookup
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextVar
specifier|public
name|void
name|setNextVar
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setSource
specifier|public
name|void
name|setSource
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|)
block|{
name|lookup
operator|.
name|source
argument_list|()
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|ScriptValueConverter
operator|.
name|unwrapValue
argument_list|(
name|script
operator|.
name|exec
argument_list|(
name|ctx
argument_list|,
name|scope
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|runAsFloat
specifier|public
name|float
name|runAsFloat
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|run
argument_list|()
operator|)
operator|.
name|floatValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|runAsLong
specifier|public
name|long
name|runAsLong
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|run
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|runAsDouble
specifier|public
name|double
name|runAsDouble
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|run
argument_list|()
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|unwrap
specifier|public
name|Object
name|unwrap
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|ScriptValueConverter
operator|.
name|unwrapValue
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
comment|/**      * Wrap Factory for Rhino Script Engine      */
DECL|class|CustomWrapFactory
specifier|public
specifier|static
class|class
name|CustomWrapFactory
extends|extends
name|WrapFactory
block|{
DECL|method|CustomWrapFactory
specifier|public
name|CustomWrapFactory
parameter_list|()
block|{
name|setJavaPrimitiveWrap
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// RingoJS does that..., claims its annoying...
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|wrapAsJavaObject
specifier|public
name|Scriptable
name|wrapAsJavaObject
parameter_list|(
name|Context
name|cx
parameter_list|,
name|Scriptable
name|scope
parameter_list|,
name|Object
name|javaObject
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|staticType
parameter_list|)
block|{
if|if
condition|(
name|javaObject
operator|instanceof
name|Map
condition|)
block|{
return|return
name|NativeMap
operator|.
name|wrap
argument_list|(
name|scope
argument_list|,
operator|(
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
operator|)
name|javaObject
argument_list|)
return|;
block|}
if|if
condition|(
name|javaObject
operator|instanceof
name|List
condition|)
block|{
return|return
name|NativeList
operator|.
name|wrap
argument_list|(
name|scope
argument_list|,
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|javaObject
argument_list|,
name|staticType
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|wrapAsJavaObject
argument_list|(
name|cx
argument_list|,
name|scope
argument_list|,
name|javaObject
argument_list|,
name|staticType
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

