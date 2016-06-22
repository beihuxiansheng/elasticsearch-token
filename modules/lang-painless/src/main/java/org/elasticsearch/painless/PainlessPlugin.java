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
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptEngineRegistry
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
name|ScriptModule
import|;
end_import

begin_comment
comment|/**  * Registers Painless as a plugin.  */
end_comment

begin_class
DECL|class|PainlessPlugin
specifier|public
specifier|final
class|class
name|PainlessPlugin
extends|extends
name|Plugin
implements|implements
name|ScriptPlugin
block|{
comment|// force to pare our definition at startup (not on the user's first script)
static|static
block|{
name|Definition
operator|.
name|VOID_TYPE
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
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
name|PainlessScriptEngineService
argument_list|(
name|settings
argument_list|)
return|;
block|}
block|}
end_class

end_unit

