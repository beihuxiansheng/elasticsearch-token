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
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|SimpleBindings
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
name|js
operator|.
name|JavascriptCompiler
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
name|js
operator|.
name|VariableContext
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|DoubleConstValueSource
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
name|search
operator|.
name|SortField
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
name|Nullable
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
name|index
operator|.
name|fielddata
operator|.
name|IndexFieldData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|FieldMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MapperService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
operator|.
name|NumberFieldMapper
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
name|SearchScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
operator|.
name|SearchLookup
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
comment|/**  * Provides the infrastructure for Lucene expressions as a scripting language for Elasticsearch.  Only  * {@link SearchScript}s are supported.  */
end_comment

begin_class
DECL|class|ExpressionScriptEngineService
specifier|public
class|class
name|ExpressionScriptEngineService
extends|extends
name|AbstractComponent
implements|implements
name|ScriptEngineService
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"expression"
decl_stmt|;
annotation|@
name|Inject
DECL|method|ExpressionScriptEngineService
specifier|public
name|ExpressionScriptEngineService
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
block|}
annotation|@
name|Override
DECL|method|types
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
name|NAME
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|extensions
specifier|public
name|String
index|[]
name|extensions
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|NAME
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|sandboxed
specifier|public
name|boolean
name|sandboxed
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|compile
specifier|public
name|Object
name|compile
parameter_list|(
name|String
name|script
parameter_list|)
block|{
try|try
block|{
comment|// NOTE: validation is delayed to allow runtime vars, and we don't have access to per index stuff here
return|return
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
name|script
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ExpressionScriptCompilationException
argument_list|(
literal|"Failed to parse expression: "
operator|+
name|script
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|SearchScript
name|search
parameter_list|(
name|Object
name|compiledScript
parameter_list|,
name|SearchLookup
name|lookup
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
name|Expression
name|expr
init|=
operator|(
name|Expression
operator|)
name|compiledScript
decl_stmt|;
name|MapperService
name|mapper
init|=
name|lookup
operator|.
name|doc
argument_list|()
operator|.
name|mapperService
argument_list|()
decl_stmt|;
comment|// NOTE: if we need to do anything complicated with bindings in the future, we can just extend Bindings,
comment|// instead of complicating SimpleBindings (which should stay simple)
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|ReplaceableConstValueSource
name|specialValue
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|variable
range|:
name|expr
operator|.
name|variables
control|)
block|{
if|if
condition|(
name|variable
operator|.
name|equals
argument_list|(
literal|"_score"
argument_list|)
condition|)
block|{
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"_score"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|variable
operator|.
name|equals
argument_list|(
literal|"_value"
argument_list|)
condition|)
block|{
name|specialValue
operator|=
operator|new
name|ReplaceableConstValueSource
argument_list|()
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
literal|"_value"
argument_list|,
name|specialValue
argument_list|)
expr_stmt|;
comment|// noop: _value is special for aggregations, and is handled in ExpressionScriptBindings
comment|// TODO: if some uses it in a scoring expression, they will get a nasty failure when evaluating...need a
comment|// way to know this is for aggregations and so _value is ok to have...
block|}
elseif|else
if|if
condition|(
name|vars
operator|!=
literal|null
operator|&&
name|vars
operator|.
name|containsKey
argument_list|(
name|variable
argument_list|)
condition|)
block|{
comment|// TODO: document and/or error if vars contains _score?
comment|// NOTE: by checking for the variable in vars first, it allows masking document fields with a global constant,
comment|// but if we were to reverse it, we could provide a way to supply dynamic defaults for documents missing the field?
name|Object
name|value
init|=
name|vars
operator|.
name|get
argument_list|(
name|variable
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
name|bindings
operator|.
name|add
argument_list|(
name|variable
argument_list|,
operator|new
name|DoubleConstValueSource
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ExpressionScriptCompilationException
argument_list|(
literal|"Parameter ["
operator|+
name|variable
operator|+
literal|"] must be a numeric type"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|VariableContext
index|[]
name|parts
init|=
name|VariableContext
operator|.
name|parse
argument_list|(
name|variable
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
index|[
literal|0
index|]
operator|.
name|text
operator|.
name|equals
argument_list|(
literal|"doc"
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|ExpressionScriptCompilationException
argument_list|(
literal|"Unknown variable ["
operator|+
name|parts
index|[
literal|0
index|]
operator|.
name|text
operator|+
literal|"] in expression"
argument_list|)
throw|;
block|}
if|if
condition|(
name|parts
operator|.
name|length
operator|<
literal|2
operator|||
name|parts
index|[
literal|1
index|]
operator|.
name|type
operator|!=
name|VariableContext
operator|.
name|Type
operator|.
name|STR_INDEX
condition|)
block|{
throw|throw
operator|new
name|ExpressionScriptCompilationException
argument_list|(
literal|"Variable 'doc' in expression must be used with a specific field like: doc['myfield'].value"
argument_list|)
throw|;
block|}
if|if
condition|(
name|parts
operator|.
name|length
operator|<
literal|3
operator|||
name|parts
index|[
literal|2
index|]
operator|.
name|type
operator|!=
name|VariableContext
operator|.
name|Type
operator|.
name|MEMBER
operator|||
name|parts
index|[
literal|2
index|]
operator|.
name|text
operator|.
name|equals
argument_list|(
literal|"value"
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|ExpressionScriptCompilationException
argument_list|(
literal|"Invalid member for field data in expression.  Only '.value' is currently supported."
argument_list|)
throw|;
block|}
name|String
name|fieldname
init|=
name|parts
index|[
literal|1
index|]
operator|.
name|text
decl_stmt|;
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|field
init|=
name|mapper
operator|.
name|smartNameFieldMapper
argument_list|(
name|fieldname
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ExpressionScriptCompilationException
argument_list|(
literal|"Field ["
operator|+
name|fieldname
operator|+
literal|"] used in expression does not exist in mappings"
argument_list|)
throw|;
block|}
if|if
condition|(
name|field
operator|.
name|isNumeric
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// TODO: more context (which expression?)
throw|throw
operator|new
name|ExpressionScriptCompilationException
argument_list|(
literal|"Field ["
operator|+
name|fieldname
operator|+
literal|"] used in expression must be numeric"
argument_list|)
throw|;
block|}
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|fieldData
init|=
name|lookup
operator|.
name|doc
argument_list|()
operator|.
name|fieldDataService
argument_list|()
operator|.
name|getForField
argument_list|(
operator|(
name|NumberFieldMapper
operator|)
name|field
argument_list|)
decl_stmt|;
name|bindings
operator|.
name|add
argument_list|(
name|variable
argument_list|,
operator|new
name|FieldDataValueSource
argument_list|(
name|fieldData
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ExpressionScript
argument_list|(
operator|(
name|Expression
operator|)
name|compiledScript
argument_list|,
name|bindings
argument_list|,
name|specialValue
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|executable
specifier|public
name|ExecutableScript
name|executable
parameter_list|(
name|Object
name|compiledScript
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot use expressions for updates"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|execute
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot use expressions for updates"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|unwrap
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
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|scriptRemoved
specifier|public
name|void
name|scriptRemoved
parameter_list|(
name|CompiledScript
name|script
parameter_list|)
block|{
comment|// Nothing to do
block|}
block|}
end_class

end_unit

