begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.expression
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|expression
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|Expression
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
name|GeneralScriptException
import|;
end_import

begin_comment
comment|/**  * A bridge to evaluate an {@link Expression} against a map of variables in the context  * of an {@link ExecutableScript}.  */
end_comment

begin_class
DECL|class|ExpressionExecutableScript
specifier|public
class|class
name|ExpressionExecutableScript
implements|implements
name|ExecutableScript
block|{
DECL|field|NO_DOCUMENT
specifier|private
specifier|final
name|int
name|NO_DOCUMENT
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|compiledScript
specifier|public
specifier|final
name|CompiledScript
name|compiledScript
decl_stmt|;
DECL|field|functionValuesMap
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ReplaceableConstFunctionValues
argument_list|>
name|functionValuesMap
decl_stmt|;
DECL|field|functionValuesArray
specifier|public
specifier|final
name|ReplaceableConstFunctionValues
index|[]
name|functionValuesArray
decl_stmt|;
DECL|method|ExpressionExecutableScript
specifier|public
name|ExpressionExecutableScript
parameter_list|(
name|CompiledScript
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
name|this
operator|.
name|compiledScript
operator|=
name|compiledScript
expr_stmt|;
name|Expression
name|expression
init|=
operator|(
name|Expression
operator|)
name|this
operator|.
name|compiledScript
operator|.
name|compiled
argument_list|()
decl_stmt|;
name|int
name|functionValuesLength
init|=
name|expression
operator|.
name|variables
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|vars
operator|.
name|size
argument_list|()
operator|!=
name|functionValuesLength
condition|)
block|{
throw|throw
operator|new
name|GeneralScriptException
argument_list|(
literal|"Error using "
operator|+
name|compiledScript
operator|+
literal|". "
operator|+
literal|"The number of variables in an executable expression script ["
operator|+
name|functionValuesLength
operator|+
literal|"] must match the number of variables in the variable map"
operator|+
literal|" ["
operator|+
name|vars
operator|.
name|size
argument_list|()
operator|+
literal|"]."
argument_list|)
throw|;
block|}
name|functionValuesArray
operator|=
operator|new
name|ReplaceableConstFunctionValues
index|[
name|functionValuesLength
index|]
expr_stmt|;
name|functionValuesMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|functionValuesIndex
init|=
literal|0
init|;
name|functionValuesIndex
operator|<
name|functionValuesLength
condition|;
operator|++
name|functionValuesIndex
control|)
block|{
name|String
name|variableName
init|=
name|expression
operator|.
name|variables
index|[
name|functionValuesIndex
index|]
decl_stmt|;
name|functionValuesArray
index|[
name|functionValuesIndex
index|]
operator|=
operator|new
name|ReplaceableConstFunctionValues
argument_list|()
expr_stmt|;
name|functionValuesMap
operator|.
name|put
argument_list|(
name|variableName
argument_list|,
name|functionValuesArray
index|[
name|functionValuesIndex
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|varsName
range|:
name|vars
operator|.
name|keySet
argument_list|()
control|)
block|{
name|setNextVar
argument_list|(
name|varsName
argument_list|,
name|vars
operator|.
name|get
argument_list|(
name|varsName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setNextVar
specifier|public
name|void
name|setNextVar
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|functionValuesMap
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
name|double
name|doubleValue
init|=
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|functionValuesMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|.
name|setValue
argument_list|(
name|doubleValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|GeneralScriptException
argument_list|(
literal|"Error using "
operator|+
name|compiledScript
operator|+
literal|". "
operator|+
literal|"Executable expressions scripts can only process numbers."
operator|+
literal|"  The variable ["
operator|+
name|name
operator|+
literal|"] is not a number."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|GeneralScriptException
argument_list|(
literal|"Error using "
operator|+
name|compiledScript
operator|+
literal|". "
operator|+
literal|"The variable ["
operator|+
name|name
operator|+
literal|"] does not exist in the executable expressions script."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|Object
name|run
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
operator|(
name|Expression
operator|)
name|compiledScript
operator|.
name|compiled
argument_list|()
operator|)
operator|.
name|evaluate
argument_list|(
name|NO_DOCUMENT
argument_list|,
name|functionValuesArray
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
throw|throw
operator|new
name|GeneralScriptException
argument_list|(
literal|"Error evaluating "
operator|+
name|compiledScript
argument_list|,
name|exception
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

