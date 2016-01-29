begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
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
name|bytes
operator|.
name|BytesReference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|TemplateService
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
name|CompiledScript
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
name|Script
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
name|ScriptContext
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
name|ScriptService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_class
DECL|class|InternalTemplateService
specifier|public
class|class
name|InternalTemplateService
implements|implements
name|TemplateService
block|{
DECL|field|scriptService
specifier|private
specifier|final
name|ScriptService
name|scriptService
decl_stmt|;
DECL|method|InternalTemplateService
name|InternalTemplateService
parameter_list|(
name|ScriptService
name|scriptService
parameter_list|)
block|{
name|this
operator|.
name|scriptService
operator|=
name|scriptService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compile
specifier|public
name|Template
name|compile
parameter_list|(
name|String
name|template
parameter_list|)
block|{
name|int
name|mustacheStart
init|=
name|template
operator|.
name|indexOf
argument_list|(
literal|"{{"
argument_list|)
decl_stmt|;
name|int
name|mustacheEnd
init|=
name|template
operator|.
name|indexOf
argument_list|(
literal|"}}"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mustacheStart
operator|!=
operator|-
literal|1
operator|&&
name|mustacheEnd
operator|!=
operator|-
literal|1
operator|&&
name|mustacheStart
operator|<
name|mustacheEnd
condition|)
block|{
name|Script
name|script
init|=
operator|new
name|Script
argument_list|(
name|template
argument_list|,
name|ScriptService
operator|.
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"mustache"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|CompiledScript
name|compiledScript
init|=
name|scriptService
operator|.
name|compile
argument_list|(
name|script
argument_list|,
name|ScriptContext
operator|.
name|Standard
operator|.
name|INGEST
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|Template
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|execute
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|model
parameter_list|)
block|{
name|ExecutableScript
name|executableScript
init|=
name|scriptService
operator|.
name|executable
argument_list|(
name|compiledScript
argument_list|,
name|model
argument_list|)
decl_stmt|;
name|Object
name|result
init|=
name|executableScript
operator|.
name|run
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|instanceof
name|BytesReference
condition|)
block|{
return|return
operator|(
operator|(
name|BytesReference
operator|)
name|result
operator|)
operator|.
name|toUtf8
argument_list|()
return|;
block|}
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|result
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|template
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
operator|new
name|StringTemplate
argument_list|(
name|template
argument_list|)
return|;
block|}
block|}
DECL|class|StringTemplate
class|class
name|StringTemplate
implements|implements
name|Template
block|{
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|method|StringTemplate
specifier|public
name|StringTemplate
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|String
name|execute
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|model
parameter_list|)
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
block|}
end_class

end_unit

