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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A string template rendered as a script.  */
end_comment

begin_interface
DECL|interface|TemplateScript
specifier|public
interface|interface
name|TemplateScript
block|{
comment|/** Run a template and return the resulting string, encoded in utf8 bytes. */
DECL|method|execute
name|String
name|execute
parameter_list|()
function_decl|;
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|newInstance
name|TemplateScript
name|newInstance
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
function_decl|;
block|}
DECL|field|CONTEXT
name|ScriptContext
argument_list|<
name|Factory
argument_list|>
name|CONTEXT
init|=
operator|new
name|ScriptContext
argument_list|<>
argument_list|(
literal|"template"
argument_list|,
name|Factory
operator|.
name|class
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

