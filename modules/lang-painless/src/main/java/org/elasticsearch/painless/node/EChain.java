begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|node
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Cast
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Sort
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|AnalyzerCaster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Operation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Variables
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|MethodWriter
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

begin_comment
comment|/**  * Represents the entirety of a variable/method chain for read/write operations.  */
end_comment

begin_class
DECL|class|EChain
specifier|public
specifier|final
class|class
name|EChain
extends|extends
name|AExpression
block|{
DECL|field|links
specifier|final
name|List
argument_list|<
name|ALink
argument_list|>
name|links
decl_stmt|;
DECL|field|pre
specifier|final
name|boolean
name|pre
decl_stmt|;
DECL|field|post
specifier|final
name|boolean
name|post
decl_stmt|;
DECL|field|operation
name|Operation
name|operation
decl_stmt|;
DECL|field|expression
name|AExpression
name|expression
decl_stmt|;
DECL|field|cat
name|boolean
name|cat
init|=
literal|false
decl_stmt|;
DECL|field|promote
name|Type
name|promote
init|=
literal|null
decl_stmt|;
DECL|field|there
name|Cast
name|there
init|=
literal|null
decl_stmt|;
DECL|field|back
name|Cast
name|back
init|=
literal|null
decl_stmt|;
DECL|method|EChain
specifier|public
name|EChain
parameter_list|(
name|int
name|line
parameter_list|,
name|String
name|location
parameter_list|,
name|List
argument_list|<
name|ALink
argument_list|>
name|links
parameter_list|,
name|boolean
name|pre
parameter_list|,
name|boolean
name|post
parameter_list|,
name|Operation
name|operation
parameter_list|,
name|AExpression
name|expression
parameter_list|)
block|{
name|super
argument_list|(
name|line
argument_list|,
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|links
operator|=
name|links
expr_stmt|;
name|this
operator|.
name|pre
operator|=
name|pre
expr_stmt|;
name|this
operator|.
name|post
operator|=
name|post
expr_stmt|;
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|analyze
name|void
name|analyze
parameter_list|(
name|Variables
name|variables
parameter_list|)
block|{
name|analyzeLinks
argument_list|(
name|variables
argument_list|)
expr_stmt|;
name|analyzeIncrDecr
argument_list|()
expr_stmt|;
if|if
condition|(
name|operation
operator|!=
literal|null
condition|)
block|{
name|analyzeCompound
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expression
operator|!=
literal|null
condition|)
block|{
name|analyzeWrite
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|analyzeRead
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|analyzeLinks
specifier|private
name|void
name|analyzeLinks
parameter_list|(
name|Variables
name|variables
parameter_list|)
block|{
name|ALink
name|previous
init|=
literal|null
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|index
operator|<
name|links
operator|.
name|size
argument_list|()
condition|)
block|{
specifier|final
name|ALink
name|current
init|=
name|links
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
name|current
operator|.
name|before
operator|=
name|previous
operator|.
name|after
expr_stmt|;
if|if
condition|(
name|index
operator|==
literal|1
condition|)
block|{
name|current
operator|.
name|statik
operator|=
name|previous
operator|.
name|statik
expr_stmt|;
block|}
block|}
if|if
condition|(
name|index
operator|==
name|links
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|current
operator|.
name|load
operator|=
name|read
expr_stmt|;
name|current
operator|.
name|store
operator|=
name|expression
operator|!=
literal|null
operator|||
name|pre
operator|||
name|post
expr_stmt|;
block|}
specifier|final
name|ALink
name|analyzed
init|=
name|current
operator|.
name|analyze
argument_list|(
name|variables
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzed
operator|==
literal|null
condition|)
block|{
name|links
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|analyzed
operator|!=
name|current
condition|)
block|{
name|links
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|analyzed
argument_list|)
expr_stmt|;
block|}
name|previous
operator|=
name|analyzed
expr_stmt|;
operator|++
name|index
expr_stmt|;
block|}
block|}
if|if
condition|(
name|links
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|statik
condition|)
block|{
name|links
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|analyzeIncrDecr
specifier|private
name|void
name|analyzeIncrDecr
parameter_list|()
block|{
specifier|final
name|ALink
name|last
init|=
name|links
operator|.
name|get
argument_list|(
name|links
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|pre
operator|&&
name|post
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|error
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|pre
operator|||
name|post
condition|)
block|{
if|if
condition|(
name|expression
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|error
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|Sort
name|sort
init|=
name|last
operator|.
name|after
operator|.
name|sort
decl_stmt|;
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|INCR
condition|)
block|{
if|if
condition|(
name|sort
operator|==
name|Sort
operator|.
name|DOUBLE
condition|)
block|{
name|expression
operator|=
operator|new
name|EConstant
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
literal|1D
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sort
operator|==
name|Sort
operator|.
name|FLOAT
condition|)
block|{
name|expression
operator|=
operator|new
name|EConstant
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
literal|1F
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sort
operator|==
name|Sort
operator|.
name|LONG
condition|)
block|{
name|expression
operator|=
operator|new
name|EConstant
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expression
operator|=
operator|new
name|EConstant
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|operation
operator|=
name|Operation
operator|.
name|ADD
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|DECR
condition|)
block|{
if|if
condition|(
name|sort
operator|==
name|Sort
operator|.
name|DOUBLE
condition|)
block|{
name|expression
operator|=
operator|new
name|EConstant
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
literal|1D
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sort
operator|==
name|Sort
operator|.
name|FLOAT
condition|)
block|{
name|expression
operator|=
operator|new
name|EConstant
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
literal|1F
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sort
operator|==
name|Sort
operator|.
name|LONG
condition|)
block|{
name|expression
operator|=
operator|new
name|EConstant
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expression
operator|=
operator|new
name|EConstant
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|operation
operator|=
name|Operation
operator|.
name|SUB
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|error
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|analyzeCompound
specifier|private
name|void
name|analyzeCompound
parameter_list|(
name|Variables
name|variables
parameter_list|)
block|{
specifier|final
name|ALink
name|last
init|=
name|links
operator|.
name|get
argument_list|(
name|links
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|expression
operator|.
name|analyze
argument_list|(
name|variables
argument_list|)
expr_stmt|;
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|MUL
condition|)
block|{
name|promote
operator|=
name|AnalyzerCaster
operator|.
name|promoteNumeric
argument_list|(
name|last
operator|.
name|after
argument_list|,
name|expression
operator|.
name|actual
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|DIV
condition|)
block|{
name|promote
operator|=
name|AnalyzerCaster
operator|.
name|promoteNumeric
argument_list|(
name|last
operator|.
name|after
argument_list|,
name|expression
operator|.
name|actual
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|REM
condition|)
block|{
name|promote
operator|=
name|AnalyzerCaster
operator|.
name|promoteNumeric
argument_list|(
name|last
operator|.
name|after
argument_list|,
name|expression
operator|.
name|actual
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|ADD
condition|)
block|{
name|promote
operator|=
name|AnalyzerCaster
operator|.
name|promoteAdd
argument_list|(
name|last
operator|.
name|after
argument_list|,
name|expression
operator|.
name|actual
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|SUB
condition|)
block|{
name|promote
operator|=
name|AnalyzerCaster
operator|.
name|promoteNumeric
argument_list|(
name|last
operator|.
name|after
argument_list|,
name|expression
operator|.
name|actual
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|LSH
condition|)
block|{
name|promote
operator|=
name|AnalyzerCaster
operator|.
name|promoteNumeric
argument_list|(
name|last
operator|.
name|after
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|RSH
condition|)
block|{
name|promote
operator|=
name|AnalyzerCaster
operator|.
name|promoteNumeric
argument_list|(
name|last
operator|.
name|after
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|USH
condition|)
block|{
name|promote
operator|=
name|AnalyzerCaster
operator|.
name|promoteNumeric
argument_list|(
name|last
operator|.
name|after
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|BWAND
condition|)
block|{
name|promote
operator|=
name|AnalyzerCaster
operator|.
name|promoteXor
argument_list|(
name|last
operator|.
name|after
argument_list|,
name|expression
operator|.
name|actual
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|XOR
condition|)
block|{
name|promote
operator|=
name|AnalyzerCaster
operator|.
name|promoteXor
argument_list|(
name|last
operator|.
name|after
argument_list|,
name|expression
operator|.
name|actual
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|BWOR
condition|)
block|{
name|promote
operator|=
name|AnalyzerCaster
operator|.
name|promoteXor
argument_list|(
name|last
operator|.
name|after
argument_list|,
name|expression
operator|.
name|actual
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|error
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|promote
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ClassCastException
argument_list|(
literal|"Cannot apply compound assignment "
operator|+
literal|"["
operator|+
name|operation
operator|.
name|symbol
operator|+
literal|"=] to types ["
operator|+
name|last
operator|.
name|after
operator|+
literal|"] and ["
operator|+
name|expression
operator|.
name|actual
operator|+
literal|"]."
argument_list|)
throw|;
block|}
name|cat
operator|=
name|operation
operator|==
name|Operation
operator|.
name|ADD
operator|&&
name|promote
operator|.
name|sort
operator|==
name|Sort
operator|.
name|STRING
expr_stmt|;
if|if
condition|(
name|cat
condition|)
block|{
if|if
condition|(
name|expression
operator|instanceof
name|EBinary
operator|&&
operator|(
operator|(
name|EBinary
operator|)
name|expression
operator|)
operator|.
name|operation
operator|==
name|Operation
operator|.
name|ADD
operator|&&
name|expression
operator|.
name|actual
operator|.
name|sort
operator|==
name|Sort
operator|.
name|STRING
condition|)
block|{
operator|(
operator|(
name|EBinary
operator|)
name|expression
operator|)
operator|.
name|cat
operator|=
literal|true
expr_stmt|;
block|}
name|expression
operator|.
name|expected
operator|=
name|expression
operator|.
name|actual
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|LSH
operator|||
name|operation
operator|==
name|Operation
operator|.
name|RSH
operator|||
name|operation
operator|==
name|Operation
operator|.
name|USH
condition|)
block|{
name|expression
operator|.
name|expected
operator|=
name|Definition
operator|.
name|INT_TYPE
expr_stmt|;
name|expression
operator|.
name|explicit
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|expression
operator|.
name|expected
operator|=
name|promote
expr_stmt|;
block|}
name|expression
operator|=
name|expression
operator|.
name|cast
argument_list|(
name|variables
argument_list|)
expr_stmt|;
name|there
operator|=
name|AnalyzerCaster
operator|.
name|getLegalCast
argument_list|(
name|location
argument_list|,
name|last
operator|.
name|after
argument_list|,
name|promote
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|back
operator|=
name|AnalyzerCaster
operator|.
name|getLegalCast
argument_list|(
name|location
argument_list|,
name|promote
argument_list|,
name|last
operator|.
name|after
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|statement
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|actual
operator|=
name|read
condition|?
name|last
operator|.
name|after
else|:
name|Definition
operator|.
name|VOID_TYPE
expr_stmt|;
block|}
DECL|method|analyzeWrite
specifier|private
name|void
name|analyzeWrite
parameter_list|(
name|Variables
name|variables
parameter_list|)
block|{
specifier|final
name|ALink
name|last
init|=
name|links
operator|.
name|get
argument_list|(
name|links
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// If the store node is a DEF node, we remove the cast to DEF from the expression
comment|// and promote the real type to it:
if|if
condition|(
name|last
operator|instanceof
name|IDefLink
condition|)
block|{
name|expression
operator|.
name|analyze
argument_list|(
name|variables
argument_list|)
expr_stmt|;
name|last
operator|.
name|after
operator|=
name|expression
operator|.
name|expected
operator|=
name|expression
operator|.
name|actual
expr_stmt|;
block|}
else|else
block|{
comment|// otherwise we adapt the type of the expression to the store type
name|expression
operator|.
name|expected
operator|=
name|last
operator|.
name|after
expr_stmt|;
name|expression
operator|.
name|analyze
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
name|expression
operator|=
name|expression
operator|.
name|cast
argument_list|(
name|variables
argument_list|)
expr_stmt|;
name|this
operator|.
name|statement
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|actual
operator|=
name|read
condition|?
name|last
operator|.
name|after
else|:
name|Definition
operator|.
name|VOID_TYPE
expr_stmt|;
block|}
DECL|method|analyzeRead
specifier|private
name|void
name|analyzeRead
parameter_list|()
block|{
specifier|final
name|ALink
name|last
init|=
name|links
operator|.
name|get
argument_list|(
name|links
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// If the load node is a DEF node, we adapt its after type to use _this_ expected output type:
if|if
condition|(
name|last
operator|instanceof
name|IDefLink
operator|&&
name|this
operator|.
name|expected
operator|!=
literal|null
condition|)
block|{
name|last
operator|.
name|after
operator|=
name|this
operator|.
name|expected
expr_stmt|;
block|}
name|constant
operator|=
name|last
operator|.
name|string
expr_stmt|;
name|statement
operator|=
name|last
operator|.
name|statement
expr_stmt|;
name|actual
operator|=
name|last
operator|.
name|after
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
name|void
name|write
parameter_list|(
name|MethodWriter
name|adapter
parameter_list|)
block|{
if|if
condition|(
name|cat
condition|)
block|{
name|adapter
operator|.
name|writeNewStrings
argument_list|()
expr_stmt|;
block|}
specifier|final
name|ALink
name|last
init|=
name|links
operator|.
name|get
argument_list|(
name|links
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|ALink
name|link
range|:
name|links
control|)
block|{
name|link
operator|.
name|write
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
if|if
condition|(
name|link
operator|==
name|last
operator|&&
name|link
operator|.
name|store
condition|)
block|{
if|if
condition|(
name|cat
condition|)
block|{
name|adapter
operator|.
name|writeDup
argument_list|(
name|link
operator|.
name|size
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|link
operator|.
name|load
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|writeAppendStrings
argument_list|(
name|link
operator|.
name|after
argument_list|)
expr_stmt|;
name|expression
operator|.
name|write
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|expression
operator|instanceof
name|EBinary
operator|)
operator|||
operator|(
operator|(
name|EBinary
operator|)
name|expression
operator|)
operator|.
name|operation
operator|!=
name|Operation
operator|.
name|ADD
operator|||
name|expression
operator|.
name|actual
operator|.
name|sort
operator|!=
name|Sort
operator|.
name|STRING
condition|)
block|{
name|adapter
operator|.
name|writeAppendStrings
argument_list|(
name|expression
operator|.
name|actual
argument_list|)
expr_stmt|;
block|}
name|adapter
operator|.
name|writeToStrings
argument_list|()
expr_stmt|;
name|adapter
operator|.
name|writeCast
argument_list|(
name|back
argument_list|)
expr_stmt|;
if|if
condition|(
name|link
operator|.
name|load
condition|)
block|{
name|adapter
operator|.
name|writeDup
argument_list|(
name|link
operator|.
name|after
operator|.
name|sort
operator|.
name|size
argument_list|,
name|link
operator|.
name|size
argument_list|)
expr_stmt|;
block|}
name|link
operator|.
name|store
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|!=
literal|null
condition|)
block|{
name|adapter
operator|.
name|writeDup
argument_list|(
name|link
operator|.
name|size
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|link
operator|.
name|load
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
if|if
condition|(
name|link
operator|.
name|load
operator|&&
name|post
condition|)
block|{
name|adapter
operator|.
name|writeDup
argument_list|(
name|link
operator|.
name|after
operator|.
name|sort
operator|.
name|size
argument_list|,
name|link
operator|.
name|size
argument_list|)
expr_stmt|;
block|}
name|adapter
operator|.
name|writeCast
argument_list|(
name|there
argument_list|)
expr_stmt|;
name|expression
operator|.
name|write
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|writeBinaryInstruction
argument_list|(
name|location
argument_list|,
name|promote
argument_list|,
name|operation
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|writeCast
argument_list|(
name|back
argument_list|)
expr_stmt|;
if|if
condition|(
name|link
operator|.
name|load
operator|&&
operator|!
name|post
condition|)
block|{
name|adapter
operator|.
name|writeDup
argument_list|(
name|link
operator|.
name|after
operator|.
name|sort
operator|.
name|size
argument_list|,
name|link
operator|.
name|size
argument_list|)
expr_stmt|;
block|}
name|link
operator|.
name|store
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expression
operator|.
name|write
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
if|if
condition|(
name|link
operator|.
name|load
condition|)
block|{
name|adapter
operator|.
name|writeDup
argument_list|(
name|link
operator|.
name|after
operator|.
name|sort
operator|.
name|size
argument_list|,
name|link
operator|.
name|size
argument_list|)
expr_stmt|;
block|}
name|link
operator|.
name|store
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|link
operator|.
name|load
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
block|}
block|}
name|adapter
operator|.
name|writeBranch
argument_list|(
name|tru
argument_list|,
name|fals
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

