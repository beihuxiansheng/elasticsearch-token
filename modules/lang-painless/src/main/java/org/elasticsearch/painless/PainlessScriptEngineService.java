begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
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
name|search
operator|.
name|lookup
operator|.
name|SearchLookup
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
name|Permissions
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
name|ProtectionDomain
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

begin_comment
comment|/**  * Implementation of a ScriptEngine for the Painless language.  */
end_comment

begin_class
DECL|class|PainlessScriptEngineService
specifier|public
class|class
name|PainlessScriptEngineService
extends|extends
name|AbstractComponent
implements|implements
name|ScriptEngineService
block|{
comment|/**      * Standard name of the Painless language.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"painless"
decl_stmt|;
comment|/**      * Standard list of names for the Painless language.  (There is only one.)      */
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
name|singletonList
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
comment|/**      * Standard extension of the Painless language.      */
DECL|field|EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|EXTENSION
init|=
literal|"pain"
decl_stmt|;
comment|/**      * Standard list of extensions for the Painless language.  (There is only one.)      */
DECL|field|EXTENSIONS
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|EXTENSIONS
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|EXTENSION
argument_list|)
decl_stmt|;
comment|/**      * Default compiler settings to be used.      */
DECL|field|DEFAULT_COMPILER_SETTINGS
specifier|private
specifier|static
specifier|final
name|CompilerSettings
name|DEFAULT_COMPILER_SETTINGS
init|=
operator|new
name|CompilerSettings
argument_list|()
decl_stmt|;
comment|/**      * Permissions context used during compilation.      */
DECL|field|COMPILATION_CONTEXT
specifier|private
specifier|static
specifier|final
name|AccessControlContext
name|COMPILATION_CONTEXT
decl_stmt|;
comment|/**      * Setup the allowed permissions.      */
static|static
block|{
specifier|final
name|Permissions
name|none
init|=
operator|new
name|Permissions
argument_list|()
decl_stmt|;
name|none
operator|.
name|setReadOnly
argument_list|()
expr_stmt|;
name|COMPILATION_CONTEXT
operator|=
operator|new
name|AccessControlContext
argument_list|(
operator|new
name|ProtectionDomain
index|[]
block|{
operator|new
name|ProtectionDomain
argument_list|(
literal|null
argument_list|,
name|none
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Used only for testing.      */
DECL|field|definition
specifier|private
name|Definition
name|definition
init|=
literal|null
decl_stmt|;
comment|/**      * Used only for testing.      */
DECL|method|setDefinition
name|void
name|setDefinition
parameter_list|(
specifier|final
name|Definition
name|definition
parameter_list|)
block|{
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
block|}
comment|/**      * Constructor.      * @param settings The settings to initialize the engine with.      */
annotation|@
name|Inject
DECL|method|PainlessScriptEngineService
specifier|public
name|PainlessScriptEngineService
parameter_list|(
specifier|final
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the type name(s) for the language.      * @return Always contains only the single name of the language.      */
annotation|@
name|Override
DECL|method|getTypes
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getTypes
parameter_list|()
block|{
return|return
name|TYPES
return|;
block|}
comment|/**      * Get the extension(s) for the language.      * @return Always contains only the single extension of the language.      */
annotation|@
name|Override
DECL|method|getExtensions
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getExtensions
parameter_list|()
block|{
return|return
name|EXTENSIONS
return|;
block|}
comment|/**      * Whether or not the engine is secure.      * @return Always true as the engine should be secure at runtime.      */
annotation|@
name|Override
DECL|method|isSandboxed
specifier|public
name|boolean
name|isSandboxed
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Compiles a Painless script with the specified parameters.      * @param script The code to be compiled.      * @param params The params used to modify the compiler settings on a per script basis.      * @return Compiled script object represented by an {@link Executable}.      */
annotation|@
name|Override
DECL|method|compile
specifier|public
name|Object
name|compile
parameter_list|(
specifier|final
name|String
name|script
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
specifier|final
name|CompilerSettings
name|compilerSettings
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Use the default settings.
name|compilerSettings
operator|=
name|DEFAULT_COMPILER_SETTINGS
expr_stmt|;
block|}
else|else
block|{
comment|// Use custom settings specified by params.
name|compilerSettings
operator|=
operator|new
name|CompilerSettings
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|copy
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|copy
operator|.
name|remove
argument_list|(
name|CompilerSettings
operator|.
name|NUMERIC_OVERFLOW
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|compilerSettings
operator|.
name|setNumericOverflow
argument_list|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|value
operator|=
name|copy
operator|.
name|remove
argument_list|(
name|CompilerSettings
operator|.
name|MAX_LOOP_COUNTER
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|compilerSettings
operator|.
name|setMaxLoopCounter
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|copy
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unrecognized compile-time parameter(s): "
operator|+
name|copy
argument_list|)
throw|;
block|}
block|}
comment|// Check we ourselves are not being called by unprivileged code.
specifier|final
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
comment|// Create our loader (which loads compiled code with no permissions).
specifier|final
name|Compiler
operator|.
name|Loader
name|loader
init|=
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Compiler
operator|.
name|Loader
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Compiler
operator|.
name|Loader
name|run
parameter_list|()
block|{
return|return
operator|new
name|Compiler
operator|.
name|Loader
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Drop all permissions to actually compile the code itself.
return|return
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Executable
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Executable
name|run
parameter_list|()
block|{
return|return
name|Compiler
operator|.
name|compile
argument_list|(
name|loader
argument_list|,
literal|"unknown"
argument_list|,
name|script
argument_list|,
name|definition
argument_list|,
name|compilerSettings
argument_list|)
return|;
block|}
block|}
argument_list|,
name|COMPILATION_CONTEXT
argument_list|)
return|;
block|}
comment|/**      * Retrieve an {@link ExecutableScript} for later use.      * @param compiledScript A previously compiled script.      * @param vars The variables to be used in the script.      * @return An {@link ExecutableScript} with the currently specified variables.      */
annotation|@
name|Override
DECL|method|executable
specifier|public
name|ExecutableScript
name|executable
parameter_list|(
specifier|final
name|CompiledScript
name|compiledScript
parameter_list|,
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
return|return
operator|new
name|ScriptImpl
argument_list|(
operator|(
name|Executable
operator|)
name|compiledScript
operator|.
name|compiled
argument_list|()
argument_list|,
name|vars
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Retrieve a {@link SearchScript} for later use.      * @param compiledScript A previously compiled script.      * @param lookup The object that ultimately allows access to search fields.      * @param vars The variables to be used in the script.      * @return An {@link SearchScript} with the currently specified variables.      */
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
return|return
operator|new
name|SearchScript
argument_list|()
block|{
comment|/**              * Get the search script that will have access to search field values.              * @param context The LeafReaderContext to be used.              * @return A script that will have the search fields from the current context available for use.              */
annotation|@
name|Override
specifier|public
name|LeafSearchScript
name|getLeafSearchScript
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ScriptImpl
argument_list|(
operator|(
name|Executable
operator|)
name|compiledScript
operator|.
name|compiled
argument_list|()
argument_list|,
name|vars
argument_list|,
name|lookup
operator|.
name|getLeafSearchLookup
argument_list|(
name|context
argument_list|)
argument_list|)
return|;
block|}
comment|/**              * Whether or not the score is needed.              * @return Always true as it's assumed score is needed.              */
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
comment|/**      * Action taken when a script is removed from the cache.      * @param script The removed script.      */
annotation|@
name|Override
DECL|method|scriptRemoved
specifier|public
name|void
name|scriptRemoved
parameter_list|(
specifier|final
name|CompiledScript
name|script
parameter_list|)
block|{
comment|// Nothing to do.
block|}
comment|/**      * Action taken when the engine is closed.      */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Nothing to do.
block|}
block|}
end_class

end_unit
