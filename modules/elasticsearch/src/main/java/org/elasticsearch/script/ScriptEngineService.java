begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|ScriptEngineService
specifier|public
interface|interface
name|ScriptEngineService
block|{
DECL|method|types
name|String
index|[]
name|types
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
name|Map
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
name|vars
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

