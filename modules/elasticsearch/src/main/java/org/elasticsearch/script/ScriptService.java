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
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|collect
operator|.
name|ImmutableMap
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
name|collect
operator|.
name|ImmutableSet
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
name|collect
operator|.
name|MapMaker
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
name|mvel
operator|.
name|MvelScriptEngineService
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ScriptService
specifier|public
class|class
name|ScriptService
extends|extends
name|AbstractComponent
block|{
DECL|field|defaultLang
specifier|private
specifier|final
name|String
name|defaultLang
decl_stmt|;
DECL|field|scriptEngines
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|ScriptEngineService
argument_list|>
name|scriptEngines
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|CompiledScript
argument_list|>
name|cache
init|=
operator|new
name|MapMaker
argument_list|()
operator|.
name|softValues
argument_list|()
operator|.
name|makeMap
argument_list|()
decl_stmt|;
DECL|method|ScriptService
specifier|public
name|ScriptService
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
argument_list|(
name|settings
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|ScriptEngineService
operator|>
name|builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|MvelScriptEngineService
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|ScriptService
annotation|@
name|Inject
specifier|public
name|ScriptService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Set
argument_list|<
name|ScriptEngineService
argument_list|>
name|scriptEngines
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultLang
operator|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"default_lang"
argument_list|,
literal|"mvel"
argument_list|)
expr_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|ScriptEngineService
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|ScriptEngineService
name|scriptEngine
range|:
name|scriptEngines
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|scriptEngine
operator|.
name|type
argument_list|()
argument_list|,
name|scriptEngine
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|scriptEngines
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|compile
specifier|public
name|CompiledScript
name|compile
parameter_list|(
name|String
name|script
parameter_list|)
block|{
return|return
name|compile
argument_list|(
name|defaultLang
argument_list|,
name|script
argument_list|)
return|;
block|}
DECL|method|compile
specifier|public
name|CompiledScript
name|compile
parameter_list|(
name|String
name|lang
parameter_list|,
name|String
name|script
parameter_list|)
block|{
name|CompiledScript
name|compiled
init|=
name|cache
operator|.
name|get
argument_list|(
name|script
argument_list|)
decl_stmt|;
if|if
condition|(
name|compiled
operator|!=
literal|null
condition|)
block|{
return|return
name|compiled
return|;
block|}
if|if
condition|(
name|lang
operator|==
literal|null
condition|)
block|{
name|lang
operator|=
name|defaultLang
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|cache
init|)
block|{
name|compiled
operator|=
name|cache
operator|.
name|get
argument_list|(
name|script
argument_list|)
expr_stmt|;
if|if
condition|(
name|compiled
operator|!=
literal|null
condition|)
block|{
return|return
name|compiled
return|;
block|}
name|ScriptEngineService
name|service
init|=
name|scriptEngines
operator|.
name|get
argument_list|(
name|lang
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"script_lang not supported ["
operator|+
name|lang
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|compiled
operator|=
operator|new
name|CompiledScript
argument_list|(
name|lang
argument_list|,
name|service
operator|.
name|compile
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|script
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
block|}
return|return
name|compiled
return|;
block|}
DECL|method|executable
specifier|public
name|ExecutableScript
name|executable
parameter_list|(
name|String
name|lang
parameter_list|,
name|String
name|script
parameter_list|,
name|Map
name|vars
parameter_list|)
block|{
return|return
name|executable
argument_list|(
name|compile
argument_list|(
name|lang
argument_list|,
name|script
argument_list|)
argument_list|,
name|vars
argument_list|)
return|;
block|}
DECL|method|executable
specifier|public
name|ExecutableScript
name|executable
parameter_list|(
name|CompiledScript
name|compiledScript
parameter_list|,
name|Map
name|vars
parameter_list|)
block|{
return|return
name|scriptEngines
operator|.
name|get
argument_list|(
name|compiledScript
operator|.
name|lang
argument_list|()
argument_list|)
operator|.
name|executable
argument_list|(
name|compiledScript
operator|.
name|compiled
argument_list|()
argument_list|,
name|vars
argument_list|)
return|;
block|}
DECL|method|execute
specifier|public
name|Object
name|execute
parameter_list|(
name|CompiledScript
name|compiledScript
parameter_list|,
name|Map
name|vars
parameter_list|)
block|{
return|return
name|scriptEngines
operator|.
name|get
argument_list|(
name|compiledScript
operator|.
name|lang
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|(
name|compiledScript
operator|.
name|compiled
argument_list|()
argument_list|,
name|vars
argument_list|)
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

