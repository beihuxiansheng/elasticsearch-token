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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Scorer
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
name|LeafSearchScript
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
name|ScriptException
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
name|LeafDocLookup
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
name|LeafSearchLookup
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
name|HashMap
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
comment|/**  * ScriptImpl can be used as either an {@link ExecutableScript} or a {@link LeafSearchScript}  * to run a previously compiled Painless script.  */
end_comment

begin_class
DECL|class|ScriptImpl
specifier|final
class|class
name|ScriptImpl
implements|implements
name|ExecutableScript
implements|,
name|LeafSearchScript
block|{
comment|/**      * The Painless Executable script that can be run.      */
DECL|field|executable
specifier|private
specifier|final
name|Executable
name|executable
decl_stmt|;
comment|/**      * A map that can be used to access input parameters at run-time.      */
DECL|field|variables
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|variables
decl_stmt|;
comment|/**      * The lookup is used to access search field values at run-time.      */
DECL|field|lookup
specifier|private
specifier|final
name|LeafSearchLookup
name|lookup
decl_stmt|;
comment|/**      * the 'doc' object accessed by the script, if available.      */
DECL|field|doc
specifier|private
specifier|final
name|LeafDocLookup
name|doc
decl_stmt|;
comment|/**      * Current scorer being used      * @see #setScorer(Scorer)      */
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
comment|/**      * Current _value for aggregation      * @see #setNextAggregationValue(Object)      */
DECL|field|aggregationValue
specifier|private
name|Object
name|aggregationValue
decl_stmt|;
comment|/**      * Creates a ScriptImpl for the a previously compiled Painless script.      * @param executable The previously compiled Painless script.      * @param vars The initial variables to run the script with.      * @param lookup The lookup to allow search fields to be available if this is run as a search script.      */
DECL|method|ScriptImpl
name|ScriptImpl
parameter_list|(
specifier|final
name|Executable
name|executable
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|,
specifier|final
name|LeafSearchLookup
name|lookup
parameter_list|)
block|{
name|this
operator|.
name|executable
operator|=
name|executable
expr_stmt|;
name|this
operator|.
name|lookup
operator|=
name|lookup
expr_stmt|;
name|this
operator|.
name|variables
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
name|vars
operator|!=
literal|null
condition|)
block|{
name|variables
operator|.
name|putAll
argument_list|(
name|vars
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lookup
operator|!=
literal|null
condition|)
block|{
name|variables
operator|.
name|putAll
argument_list|(
name|lookup
operator|.
name|asMap
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
name|lookup
operator|.
name|doc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      * Set a variable for the script to be run against.      * @param name The variable name.      * @param value The variable value.      */
annotation|@
name|Override
DECL|method|setNextVar
specifier|public
name|void
name|setNextVar
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Object
name|value
parameter_list|)
block|{
name|variables
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the next aggregation value.      * @param value Per-document value, typically a String, Long, or Double.      */
annotation|@
name|Override
DECL|method|setNextAggregationValue
specifier|public
name|void
name|setNextAggregationValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|aggregationValue
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Run the script.      * @return The script result.      */
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
name|executable
operator|.
name|execute
argument_list|(
name|variables
argument_list|,
name|scorer
argument_list|,
name|doc
argument_list|,
name|aggregationValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PainlessError
decl||
name|Exception
name|t
parameter_list|)
block|{
throw|throw
name|convertToScriptException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
DECL|method|convertToScriptException
specifier|private
name|ScriptException
name|convertToScriptException
parameter_list|(
name|Throwable
name|t
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
name|executable
operator|.
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
name|executable
operator|.
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
name|executable
operator|.
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
name|executable
operator|.
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
name|PainlessScriptEngineService
operator|.
name|INLINE_NAME
operator|.
name|equals
argument_list|(
name|executable
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|name
operator|=
name|executable
operator|.
name|getSource
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
name|executable
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
throw|throw
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
name|PainlessScriptEngineService
operator|.
name|NAME
argument_list|)
throw|;
block|}
comment|/** returns true for methods that are part of the runtime */
DECL|method|shouldFilter
specifier|private
specifier|static
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
comment|/**      * Run the script.      * @return The script result as a double.      */
annotation|@
name|Override
DECL|method|runAsDouble
specifier|public
name|double
name|runAsDouble
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|run
argument_list|()
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
block|}
comment|/**      * Run the script.      * @return The script result as a long.      */
annotation|@
name|Override
DECL|method|runAsLong
specifier|public
name|long
name|runAsLong
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|run
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
comment|/**      * Sets the scorer to be accessible within a script.      * @param scorer The scorer used for a search.      */
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
specifier|final
name|Scorer
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
comment|/**      * Sets the current document.      * @param doc The current document.      */
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|)
block|{
if|if
condition|(
name|lookup
operator|!=
literal|null
condition|)
block|{
name|lookup
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Sets the current source.      * @param source The current source.      */
annotation|@
name|Override
DECL|method|setSource
specifier|public
name|void
name|setSource
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|)
block|{
if|if
condition|(
name|lookup
operator|!=
literal|null
condition|)
block|{
name|lookup
operator|.
name|source
argument_list|()
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

