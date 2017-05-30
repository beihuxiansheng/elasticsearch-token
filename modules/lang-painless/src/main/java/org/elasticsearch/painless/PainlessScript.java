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
name|script
operator|.
name|ScriptException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  * Abstract superclass on top of which all Painless scripts are built.  */
end_comment

begin_interface
DECL|interface|PainlessScript
specifier|public
interface|interface
name|PainlessScript
block|{
comment|/**      * @return The name of the script retrieved from a static variable generated      * during compilation of a Painless script.      */
DECL|method|getName
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * @return The source for a script retrieved from a static variable generated      * during compilation of a Painless script.      */
DECL|method|getSource
name|String
name|getSource
parameter_list|()
function_decl|;
comment|/**      * @return The {@link BitSet} tracking the boundaries for statements necessary      * for good exception messages.      */
DECL|method|getStatements
name|BitSet
name|getStatements
parameter_list|()
function_decl|;
comment|/**      * Adds stack trace and other useful information to exceptions thrown      * from a Painless script.      * @param t The throwable to build an exception around.      * @return The generated ScriptException.      */
DECL|method|convertToScriptException
specifier|default
name|ScriptException
name|convertToScriptException
parameter_list|(
name|Throwable
name|t
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|extraMetadata
parameter_list|)
block|{
comment|// create a script stack: this is just the script portion
name|List
argument_list|<
name|String
argument_list|>
name|scriptStack
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|StackTraceElement
name|element
range|:
name|t
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
if|if
condition|(
name|WriterConstants
operator|.
name|CLASS_NAME
operator|.
name|equals
argument_list|(
name|element
operator|.
name|getClassName
argument_list|()
argument_list|)
condition|)
block|{
comment|// found the script portion
name|int
name|offset
init|=
name|element
operator|.
name|getLineNumber
argument_list|()
decl_stmt|;
if|if
condition|(
name|offset
operator|==
operator|-
literal|1
condition|)
block|{
name|scriptStack
operator|.
name|add
argument_list|(
literal|"<<< unknown portion of script>>>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|offset
operator|--
expr_stmt|;
comment|// offset is 1 based, line numbers must be!
name|int
name|startOffset
init|=
name|getPreviousStatement
argument_list|(
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|startOffset
operator|==
operator|-
literal|1
condition|)
block|{
assert|assert
literal|false
assert|;
comment|// should never happen unless we hit exc in ctor prologue...
name|startOffset
operator|=
literal|0
expr_stmt|;
block|}
name|int
name|endOffset
init|=
name|getNextStatement
argument_list|(
name|startOffset
argument_list|)
decl_stmt|;
if|if
condition|(
name|endOffset
operator|==
operator|-
literal|1
condition|)
block|{
name|endOffset
operator|=
name|getSource
argument_list|()
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
comment|// TODO: if this is still too long, truncate and use ellipses
name|String
name|snippet
init|=
name|getSource
argument_list|()
operator|.
name|substring
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
decl_stmt|;
name|scriptStack
operator|.
name|add
argument_list|(
name|snippet
argument_list|)
expr_stmt|;
name|StringBuilder
name|pointer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startOffset
init|;
name|i
operator|<
name|offset
condition|;
name|i
operator|++
control|)
block|{
name|pointer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|pointer
operator|.
name|append
argument_list|(
literal|"^---- HERE"
argument_list|)
expr_stmt|;
name|scriptStack
operator|.
name|add
argument_list|(
name|pointer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
comment|// but filter our own internal stacks (e.g. indy bootstrap)
block|}
elseif|else
if|if
condition|(
operator|!
name|shouldFilter
argument_list|(
name|element
argument_list|)
condition|)
block|{
name|scriptStack
operator|.
name|add
argument_list|(
name|element
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// build a name for the script:
specifier|final
name|String
name|name
decl_stmt|;
if|if
condition|(
name|PainlessScriptEngine
operator|.
name|INLINE_NAME
operator|.
name|equals
argument_list|(
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|name
operator|=
name|getSource
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
name|getName
argument_list|()
expr_stmt|;
block|}
name|ScriptException
name|scriptException
init|=
operator|new
name|ScriptException
argument_list|(
literal|"runtime error"
argument_list|,
name|t
argument_list|,
name|scriptStack
argument_list|,
name|name
argument_list|,
name|PainlessScriptEngine
operator|.
name|NAME
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|extraMetadata
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|scriptException
operator|.
name|addMetadata
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|scriptException
return|;
block|}
comment|/** returns true for methods that are part of the runtime */
DECL|method|shouldFilter
specifier|default
name|boolean
name|shouldFilter
parameter_list|(
name|StackTraceElement
name|element
parameter_list|)
block|{
return|return
name|element
operator|.
name|getClassName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"org.elasticsearch.painless."
argument_list|)
operator|||
name|element
operator|.
name|getClassName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"java.lang.invoke."
argument_list|)
operator|||
name|element
operator|.
name|getClassName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"sun.invoke."
argument_list|)
return|;
block|}
comment|/**      * Finds the start of the first statement boundary that is on or before {@code offset}. If one is not found, {@code -1} is returned.      */
DECL|method|getPreviousStatement
specifier|default
name|int
name|getPreviousStatement
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|getStatements
argument_list|()
operator|.
name|previousSetBit
argument_list|(
name|offset
argument_list|)
return|;
block|}
comment|/**      * Finds the start of the first statement boundary that is after {@code offset}. If one is not found, {@code -1} is returned.      */
DECL|method|getNextStatement
specifier|default
name|int
name|getNextStatement
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|getStatements
argument_list|()
operator|.
name|nextSetBit
argument_list|(
name|offset
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

