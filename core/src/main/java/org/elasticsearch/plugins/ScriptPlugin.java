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
name|ScriptContext
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
name|ScriptEngine
import|;
end_import

begin_comment
comment|/**  * An additional extension point for {@link Plugin}s that extends Elasticsearch's scripting functionality.  */
end_comment

begin_interface
DECL|interface|ScriptPlugin
specifier|public
interface|interface
name|ScriptPlugin
block|{
comment|/**      * Returns a {@link ScriptEngine} instance or<code>null</code> if this plugin doesn't add a new script engine.      * @param settings Node settings      * @param contexts The contexts that {@link ScriptEngine#compile(String, String, ScriptContext, Map)} may be called with      */
DECL|method|getScriptEngine
specifier|default
name|ScriptEngine
name|getScriptEngine
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Collection
argument_list|<
name|ScriptContext
argument_list|<
name|?
argument_list|>
argument_list|>
name|contexts
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Return script contexts this plugin wants to allow using.      */
DECL|method|getContexts
specifier|default
name|List
argument_list|<
name|ScriptContext
argument_list|>
name|getContexts
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
end_interface

end_unit

