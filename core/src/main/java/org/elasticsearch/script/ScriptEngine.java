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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A script language implementation.  */
end_comment

begin_interface
DECL|interface|ScriptEngine
specifier|public
interface|interface
name|ScriptEngine
extends|extends
name|Closeable
block|{
comment|/**      * The language name used in the script APIs to refer to this scripting backend.      */
DECL|method|getType
name|String
name|getType
parameter_list|()
function_decl|;
comment|/**      * Compiles a script.      * @param name the name of the script. {@code null} if it is anonymous (inline). For a stored script, its the identifier.      * @param code actual source of the script      * @param context the context this script will be used for      * @param params compile-time parameters (such as flags to the compiler)      * @return A compiled script of the CompiledType from {@link ScriptContext}      */
DECL|method|compile
parameter_list|<
name|CompiledType
parameter_list|>
name|CompiledType
name|compile
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|code
parameter_list|,
name|ScriptContext
argument_list|<
name|CompiledType
argument_list|>
name|context
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|close
specifier|default
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
end_interface

end_unit

