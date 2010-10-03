begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.ruby
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ruby
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
name|inject
operator|.
name|Module
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
name|AbstractPlugin
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|RubyPlugin
specifier|public
class|class
name|RubyPlugin
extends|extends
name|AbstractPlugin
block|{
DECL|method|name
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"lang-ruby"
return|;
block|}
DECL|method|description
annotation|@
name|Override
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"Ruby plugin allowing to add javascript scripting support"
return|;
block|}
DECL|method|processModule
annotation|@
name|Override
specifier|public
name|void
name|processModule
parameter_list|(
name|Module
name|module
parameter_list|)
block|{
if|if
condition|(
name|module
operator|instanceof
name|ScriptModule
condition|)
block|{
comment|//            ((ScriptModule) module).addScriptEngine(JavaScriptScriptEngineService.class);
block|}
block|}
block|}
end_class

end_unit

