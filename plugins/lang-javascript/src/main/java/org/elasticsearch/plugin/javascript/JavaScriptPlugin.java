begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.javascript
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|javascript
package|;
end_package

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
name|script
operator|.
name|ScriptModule
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
name|JavaScriptScriptEngineService
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|JavaScriptPlugin
specifier|public
class|class
name|JavaScriptPlugin
extends|extends
name|Plugin
block|{
static|static
block|{
comment|// install rhino policy on plugin init
name|JavaScriptScriptEngineService
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"lang-javascript"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"JavaScript plugin allowing to add javascript scripting support"
return|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|ScriptModule
name|module
parameter_list|)
block|{
name|module
operator|.
name|addScriptEngine
argument_list|(
name|JavaScriptScriptEngineService
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

