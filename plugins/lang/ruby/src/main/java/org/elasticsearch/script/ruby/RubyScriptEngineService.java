begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.ruby
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
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
name|ScriptEngineService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jruby
operator|.
name|embed
operator|.
name|EmbedEvalUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jruby
operator|.
name|embed
operator|.
name|ScriptingContainer
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_comment
comment|// Need to figure out how to execute compiled scripts in the most optimized manner (passing paramters to them).
end_comment

begin_class
DECL|class|RubyScriptEngineService
specifier|public
class|class
name|RubyScriptEngineService
extends|extends
name|AbstractComponent
implements|implements
name|ScriptEngineService
block|{
DECL|field|container
specifier|private
specifier|final
name|ScriptingContainer
name|container
decl_stmt|;
DECL|method|RubyScriptEngineService
annotation|@
name|Inject
specifier|public
name|RubyScriptEngineService
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|container
operator|=
operator|new
name|ScriptingContainer
argument_list|()
expr_stmt|;
name|container
operator|.
name|setClassLoader
argument_list|(
name|settings
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|types
annotation|@
name|Override
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"ruby"
block|}
return|;
block|}
DECL|method|compile
annotation|@
name|Override
specifier|public
name|Object
name|compile
parameter_list|(
name|String
name|script
parameter_list|)
block|{
return|return
name|container
operator|.
name|parse
argument_list|(
name|script
argument_list|)
return|;
block|}
DECL|method|executable
annotation|@
name|Override
specifier|public
name|ExecutableScript
name|executable
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
block|{
return|return
literal|null
return|;
comment|//To change body of implemented methods use File | Settings | File Templates.
block|}
DECL|method|execute
annotation|@
name|Override
specifier|public
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
block|{
name|EmbedEvalUnit
name|unit
init|=
operator|(
name|EmbedEvalUnit
operator|)
name|compiledScript
decl_stmt|;
return|return
literal|null
return|;
comment|//To change body of implemented methods use File | Settings | File Templates.
block|}
DECL|method|unwrap
annotation|@
name|Override
specifier|public
name|Object
name|unwrap
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|container
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

