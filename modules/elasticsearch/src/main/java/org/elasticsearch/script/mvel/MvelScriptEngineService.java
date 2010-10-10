begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.mvel
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|mvel
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
name|math
operator|.
name|UnboxedMathUtils
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
name|mvel2
operator|.
name|MVEL
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
name|mvel2
operator|.
name|ParserContext
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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

begin_class
DECL|class|MvelScriptEngineService
specifier|public
class|class
name|MvelScriptEngineService
extends|extends
name|AbstractComponent
implements|implements
name|ScriptEngineService
block|{
DECL|field|parserContext
specifier|private
specifier|final
name|ParserContext
name|parserContext
decl_stmt|;
DECL|method|MvelScriptEngineService
annotation|@
name|Inject
specifier|public
name|MvelScriptEngineService
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
name|parserContext
operator|=
operator|new
name|ParserContext
argument_list|()
expr_stmt|;
name|parserContext
operator|.
name|addPackageImport
argument_list|(
literal|"java.util"
argument_list|)
expr_stmt|;
name|parserContext
operator|.
name|addPackageImport
argument_list|(
literal|"org.elasticsearch.common.trove"
argument_list|)
expr_stmt|;
name|parserContext
operator|.
name|addPackageImport
argument_list|(
literal|"org.elasticsearch.common.joda"
argument_list|)
expr_stmt|;
name|parserContext
operator|.
name|addImport
argument_list|(
literal|"time"
argument_list|,
name|MVEL
operator|.
name|getStaticMethod
argument_list|(
name|System
operator|.
name|class
argument_list|,
literal|"currentTimeMillis"
argument_list|,
operator|new
name|Class
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// unboxed version of Math, better performance since conversion from boxed to unboxed my mvel is not needed
for|for
control|(
name|Method
name|m
range|:
name|UnboxedMathUtils
operator|.
name|class
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|m
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|STATIC
operator|)
operator|>
literal|0
condition|)
block|{
name|parserContext
operator|.
name|addImport
argument_list|(
name|m
operator|.
name|getName
argument_list|()
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// nothing to do here...
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
literal|"mvel"
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
name|MVEL
operator|.
name|compileExpression
argument_list|(
name|script
argument_list|,
name|parserContext
argument_list|)
return|;
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
name|vars
parameter_list|)
block|{
return|return
name|MVEL
operator|.
name|executeExpression
argument_list|(
name|compiledScript
argument_list|,
name|vars
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
name|vars
parameter_list|)
block|{
return|return
operator|new
name|MvelExecutableScript
argument_list|(
name|compiledScript
argument_list|,
name|vars
argument_list|)
return|;
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
name|value
return|;
block|}
DECL|class|MvelExecutableScript
specifier|public
specifier|static
class|class
name|MvelExecutableScript
implements|implements
name|ExecutableScript
block|{
DECL|field|compiledScript
specifier|private
specifier|final
name|Object
name|compiledScript
decl_stmt|;
DECL|field|vars
specifier|private
specifier|final
name|Map
name|vars
decl_stmt|;
DECL|method|MvelExecutableScript
specifier|public
name|MvelExecutableScript
parameter_list|(
name|Object
name|compiledScript
parameter_list|,
name|Map
name|vars
parameter_list|)
block|{
name|this
operator|.
name|compiledScript
operator|=
name|compiledScript
expr_stmt|;
name|this
operator|.
name|vars
operator|=
name|vars
expr_stmt|;
block|}
DECL|method|run
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
block|{
return|return
name|MVEL
operator|.
name|executeExpression
argument_list|(
name|compiledScript
argument_list|,
name|vars
argument_list|)
return|;
block|}
DECL|method|run
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|(
name|Map
name|vars
parameter_list|)
block|{
name|vars
operator|.
name|putAll
argument_list|(
name|this
operator|.
name|vars
argument_list|)
expr_stmt|;
return|return
name|MVEL
operator|.
name|executeExpression
argument_list|(
name|compiledScript
argument_list|,
name|vars
argument_list|)
return|;
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
name|value
return|;
block|}
block|}
block|}
end_class

end_unit

