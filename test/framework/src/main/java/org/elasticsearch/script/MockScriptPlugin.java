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
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|ScriptPlugin
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
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * A script plugin that uses {@link MockScriptEngine} as the script engine for tests.  */
end_comment

begin_class
DECL|class|MockScriptPlugin
specifier|public
specifier|abstract
class|class
name|MockScriptPlugin
extends|extends
name|Plugin
implements|implements
name|ScriptPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|MockScriptEngine
operator|.
name|NAME
decl_stmt|;
annotation|@
name|Override
DECL|method|getScriptEngineService
specifier|public
name|ScriptEngineService
name|getScriptEngineService
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
operator|new
name|MockScriptEngine
argument_list|(
name|pluginScriptLang
argument_list|()
argument_list|,
name|pluginScripts
argument_list|()
argument_list|)
return|;
block|}
DECL|method|pluginScripts
specifier|protected
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|>
name|pluginScripts
parameter_list|()
function_decl|;
DECL|method|pluginScriptLang
specifier|public
name|String
name|pluginScriptLang
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
block|}
end_class

end_unit
