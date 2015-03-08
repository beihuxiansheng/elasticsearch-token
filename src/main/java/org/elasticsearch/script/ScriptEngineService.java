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
name|Nullable
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
name|Closeable
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

begin_interface
DECL|interface|ScriptEngineService
specifier|public
interface|interface
name|ScriptEngineService
extends|extends
name|Closeable
block|{
DECL|method|types
name|String
index|[]
name|types
parameter_list|()
function_decl|;
DECL|method|extensions
name|String
index|[]
name|extensions
parameter_list|()
function_decl|;
DECL|method|sandboxed
name|boolean
name|sandboxed
parameter_list|()
function_decl|;
DECL|method|compile
name|Object
name|compile
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
DECL|method|executable
name|ExecutableScript
name|executable
parameter_list|(
name|Object
name|compiledScript
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
function_decl|;
DECL|method|search
name|SearchScript
name|search
parameter_list|(
name|Object
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
function_decl|;
DECL|method|execute
name|Object
name|execute
parameter_list|(
name|Object
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
function_decl|;
DECL|method|unwrap
name|Object
name|unwrap
parameter_list|(
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**      * Handler method called when a script is removed from the Guava cache.      *      * The passed script may be null if it has already been garbage collected.      * */
DECL|method|scriptRemoved
name|void
name|scriptRemoved
parameter_list|(
annotation|@
name|Nullable
name|CompiledScript
name|script
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

