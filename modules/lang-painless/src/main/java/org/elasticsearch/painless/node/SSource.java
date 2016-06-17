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
name|Constant
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
name|Method
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
name|MethodKey
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
name|Executable
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
name|Globals
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
name|Locals
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
name|Locals
operator|.
name|Variable
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
name|node
operator|.
name|SFunction
operator|.
name|Reserved
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
name|WriterConstants
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
name|Location
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
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|ClassVisitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|ClassWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|Opcodes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|util
operator|.
name|Printer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|util
operator|.
name|TraceClassVisitor
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
name|Collection
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|WriterConstants
operator|.
name|BASE_CLASS_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|WriterConstants
operator|.
name|CLASS_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|WriterConstants
operator|.
name|CONSTRUCTOR
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|WriterConstants
operator|.
name|EXECUTE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|WriterConstants
operator|.
name|MAP_GET
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|WriterConstants
operator|.
name|MAP_TYPE
import|;
end_import

begin_comment
comment|/**  * The root of all Painless trees.  Contains a series of statements.  */
end_comment

begin_class
DECL|class|SSource
specifier|public
specifier|final
class|class
name|SSource
extends|extends
name|AStatement
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|source
specifier|final
name|String
name|source
decl_stmt|;
DECL|field|debugStream
specifier|final
name|Printer
name|debugStream
decl_stmt|;
DECL|field|reserved
specifier|final
name|MainMethodReserved
name|reserved
decl_stmt|;
DECL|field|functions
specifier|final
name|List
argument_list|<
name|SFunction
argument_list|>
name|functions
decl_stmt|;
DECL|field|globals
specifier|final
name|Globals
name|globals
decl_stmt|;
DECL|field|statements
specifier|final
name|List
argument_list|<
name|AStatement
argument_list|>
name|statements
decl_stmt|;
DECL|field|mainMethod
specifier|private
name|Locals
name|mainMethod
decl_stmt|;
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|method|SSource
specifier|public
name|SSource
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|source
parameter_list|,
name|Printer
name|debugStream
parameter_list|,
name|MainMethodReserved
name|reserved
parameter_list|,
name|Location
name|location
parameter_list|,
name|List
argument_list|<
name|SFunction
argument_list|>
name|functions
parameter_list|,
name|Globals
name|globals
parameter_list|,
name|List
argument_list|<
name|AStatement
argument_list|>
name|statements
parameter_list|)
block|{
name|super
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|debugStream
operator|=
name|debugStream
expr_stmt|;
name|this
operator|.
name|reserved
operator|=
name|reserved
expr_stmt|;
comment|// process any synthetic functions generated by walker (because right now, thats still easy)
name|functions
operator|.
name|addAll
argument_list|(
name|globals
operator|.
name|getSyntheticMethods
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|globals
operator|.
name|getSyntheticMethods
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|functions
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|functions
argument_list|)
expr_stmt|;
name|this
operator|.
name|statements
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|statements
argument_list|)
expr_stmt|;
name|this
operator|.
name|globals
operator|=
name|globals
expr_stmt|;
block|}
DECL|method|analyze
specifier|public
name|void
name|analyze
parameter_list|()
block|{
name|Map
argument_list|<
name|MethodKey
argument_list|,
name|Method
argument_list|>
name|methods
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SFunction
name|function
range|:
name|functions
control|)
block|{
name|function
operator|.
name|generate
argument_list|()
expr_stmt|;
name|MethodKey
name|key
init|=
operator|new
name|MethodKey
argument_list|(
name|function
operator|.
name|name
argument_list|,
name|function
operator|.
name|parameters
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|methods
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|function
operator|.
name|method
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Duplicate functions with name ["
operator|+
name|function
operator|.
name|name
operator|+
literal|"]."
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|analyze
argument_list|(
name|Locals
operator|.
name|newProgramScope
argument_list|(
name|methods
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|analyze
name|void
name|analyze
parameter_list|(
name|Locals
name|program
parameter_list|)
block|{
for|for
control|(
name|SFunction
name|function
range|:
name|functions
control|)
block|{
name|Locals
name|functionLocals
init|=
name|Locals
operator|.
name|newFunctionScope
argument_list|(
name|program
argument_list|,
name|function
operator|.
name|rtnType
argument_list|,
name|function
operator|.
name|parameters
argument_list|,
name|function
operator|.
name|reserved
operator|.
name|getMaxLoopCounter
argument_list|()
argument_list|)
decl_stmt|;
name|function
operator|.
name|analyze
argument_list|(
name|functionLocals
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|statements
operator|==
literal|null
operator|||
name|statements
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot generate an empty script."
argument_list|)
argument_list|)
throw|;
block|}
name|mainMethod
operator|=
name|Locals
operator|.
name|newMainMethodScope
argument_list|(
name|program
argument_list|,
name|reserved
operator|.
name|usesScore
argument_list|()
argument_list|,
name|reserved
operator|.
name|usesCtx
argument_list|()
argument_list|,
name|reserved
operator|.
name|getMaxLoopCounter
argument_list|()
argument_list|)
expr_stmt|;
name|AStatement
name|last
init|=
name|statements
operator|.
name|get
argument_list|(
name|statements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|AStatement
name|statement
range|:
name|statements
control|)
block|{
comment|// Note that we do not need to check after the last statement because
comment|// there is no statement that can be unreachable after the last.
if|if
condition|(
name|allEscape
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unreachable statement."
argument_list|)
argument_list|)
throw|;
block|}
name|statement
operator|.
name|lastSource
operator|=
name|statement
operator|==
name|last
expr_stmt|;
name|statement
operator|.
name|analyze
argument_list|(
name|mainMethod
argument_list|)
expr_stmt|;
name|methodEscape
operator|=
name|statement
operator|.
name|methodEscape
expr_stmt|;
name|allEscape
operator|=
name|statement
operator|.
name|allEscape
expr_stmt|;
block|}
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|()
block|{
comment|// Create the ClassWriter.
name|int
name|classFrames
init|=
name|ClassWriter
operator|.
name|COMPUTE_FRAMES
operator||
name|ClassWriter
operator|.
name|COMPUTE_MAXS
decl_stmt|;
name|int
name|classVersion
init|=
name|Opcodes
operator|.
name|V1_8
decl_stmt|;
name|int
name|classAccess
init|=
name|Opcodes
operator|.
name|ACC_PUBLIC
operator||
name|Opcodes
operator|.
name|ACC_SUPER
operator||
name|Opcodes
operator|.
name|ACC_FINAL
decl_stmt|;
name|String
name|classBase
init|=
name|BASE_CLASS_TYPE
operator|.
name|getInternalName
argument_list|()
decl_stmt|;
name|String
name|className
init|=
name|CLASS_TYPE
operator|.
name|getInternalName
argument_list|()
decl_stmt|;
name|String
name|classInterfaces
index|[]
init|=
name|reserved
operator|.
name|usesScore
argument_list|()
condition|?
operator|new
name|String
index|[]
block|{
name|WriterConstants
operator|.
name|NEEDS_SCORE_TYPE
operator|.
name|getInternalName
argument_list|()
block|}
else|:
literal|null
decl_stmt|;
name|ClassWriter
name|writer
init|=
operator|new
name|ClassWriter
argument_list|(
name|classFrames
argument_list|)
decl_stmt|;
name|ClassVisitor
name|visitor
init|=
name|writer
decl_stmt|;
if|if
condition|(
name|debugStream
operator|!=
literal|null
condition|)
block|{
name|visitor
operator|=
operator|new
name|TraceClassVisitor
argument_list|(
name|visitor
argument_list|,
name|debugStream
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|visitor
operator|.
name|visit
argument_list|(
name|classVersion
argument_list|,
name|classAccess
argument_list|,
name|className
argument_list|,
literal|null
argument_list|,
name|classBase
argument_list|,
name|classInterfaces
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visitSource
argument_list|(
name|Location
operator|.
name|computeSourceName
argument_list|(
name|name
argument_list|,
name|source
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Write the constructor:
name|MethodWriter
name|constructor
init|=
operator|new
name|MethodWriter
argument_list|(
name|Opcodes
operator|.
name|ACC_PUBLIC
argument_list|,
name|CONSTRUCTOR
argument_list|,
name|visitor
argument_list|,
name|globals
operator|.
name|getStatements
argument_list|()
argument_list|)
decl_stmt|;
name|constructor
operator|.
name|visitCode
argument_list|()
expr_stmt|;
name|constructor
operator|.
name|loadThis
argument_list|()
expr_stmt|;
name|constructor
operator|.
name|loadArgs
argument_list|()
expr_stmt|;
name|constructor
operator|.
name|invokeConstructor
argument_list|(
name|org
operator|.
name|objectweb
operator|.
name|asm
operator|.
name|Type
operator|.
name|getType
argument_list|(
name|Executable
operator|.
name|class
argument_list|)
argument_list|,
name|CONSTRUCTOR
argument_list|)
expr_stmt|;
name|constructor
operator|.
name|returnValue
argument_list|()
expr_stmt|;
name|constructor
operator|.
name|endMethod
argument_list|()
expr_stmt|;
comment|// Write the execute method:
name|MethodWriter
name|execute
init|=
operator|new
name|MethodWriter
argument_list|(
name|Opcodes
operator|.
name|ACC_PUBLIC
argument_list|,
name|EXECUTE
argument_list|,
name|visitor
argument_list|,
name|globals
operator|.
name|getStatements
argument_list|()
argument_list|)
decl_stmt|;
name|execute
operator|.
name|visitCode
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|execute
argument_list|,
name|globals
argument_list|)
expr_stmt|;
name|execute
operator|.
name|endMethod
argument_list|()
expr_stmt|;
comment|// Write all functions:
for|for
control|(
name|SFunction
name|function
range|:
name|functions
control|)
block|{
name|function
operator|.
name|write
argument_list|(
name|visitor
argument_list|,
name|globals
argument_list|)
expr_stmt|;
block|}
comment|// Write all synthetic functions. Note that this process may add more :)
while|while
condition|(
operator|!
name|globals
operator|.
name|getSyntheticMethods
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|SFunction
argument_list|>
name|current
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|globals
operator|.
name|getSyntheticMethods
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|globals
operator|.
name|getSyntheticMethods
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|SFunction
name|function
range|:
name|current
control|)
block|{
name|function
operator|.
name|write
argument_list|(
name|visitor
argument_list|,
name|globals
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Write the constants
if|if
condition|(
literal|false
operator|==
name|globals
operator|.
name|getConstantInitializers
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Collection
argument_list|<
name|Constant
argument_list|>
name|inits
init|=
name|globals
operator|.
name|getConstantInitializers
argument_list|()
operator|.
name|values
argument_list|()
decl_stmt|;
comment|// Fields
for|for
control|(
name|Constant
name|constant
range|:
name|inits
control|)
block|{
name|visitor
operator|.
name|visitField
argument_list|(
name|Opcodes
operator|.
name|ACC_FINAL
operator||
name|Opcodes
operator|.
name|ACC_PRIVATE
operator||
name|Opcodes
operator|.
name|ACC_STATIC
argument_list|,
name|constant
operator|.
name|name
argument_list|,
name|constant
operator|.
name|type
operator|.
name|getDescriptor
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|visitEnd
argument_list|()
expr_stmt|;
block|}
comment|// Initialize the constants in a static initializer
specifier|final
name|MethodWriter
name|clinit
init|=
operator|new
name|MethodWriter
argument_list|(
name|Opcodes
operator|.
name|ACC_STATIC
argument_list|,
name|WriterConstants
operator|.
name|CLINIT
argument_list|,
name|visitor
argument_list|,
name|globals
operator|.
name|getStatements
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Constant
name|constant
range|:
name|inits
control|)
block|{
name|constant
operator|.
name|initializer
operator|.
name|accept
argument_list|(
name|clinit
argument_list|)
expr_stmt|;
name|clinit
operator|.
name|putStatic
argument_list|(
name|CLASS_TYPE
argument_list|,
name|constant
operator|.
name|name
argument_list|,
name|constant
operator|.
name|type
argument_list|)
expr_stmt|;
block|}
name|clinit
operator|.
name|returnValue
argument_list|()
expr_stmt|;
name|clinit
operator|.
name|endMethod
argument_list|()
expr_stmt|;
block|}
comment|// End writing the class and store the generated bytes.
name|visitor
operator|.
name|visitEnd
argument_list|()
expr_stmt|;
name|bytes
operator|=
name|writer
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
name|void
name|write
parameter_list|(
name|MethodWriter
name|writer
parameter_list|,
name|Globals
name|globals
parameter_list|)
block|{
if|if
condition|(
name|reserved
operator|.
name|usesScore
argument_list|()
condition|)
block|{
comment|// if the _score value is used, we do this once:
comment|// final double _score = scorer.score();
name|Variable
name|scorer
init|=
name|mainMethod
operator|.
name|getVariable
argument_list|(
literal|null
argument_list|,
name|Locals
operator|.
name|SCORER
argument_list|)
decl_stmt|;
name|Variable
name|score
init|=
name|mainMethod
operator|.
name|getVariable
argument_list|(
literal|null
argument_list|,
name|Locals
operator|.
name|SCORE
argument_list|)
decl_stmt|;
name|writer
operator|.
name|visitVarInsn
argument_list|(
name|Opcodes
operator|.
name|ALOAD
argument_list|,
name|scorer
operator|.
name|getSlot
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|invokeVirtual
argument_list|(
name|WriterConstants
operator|.
name|SCORER_TYPE
argument_list|,
name|WriterConstants
operator|.
name|SCORER_SCORE
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visitInsn
argument_list|(
name|Opcodes
operator|.
name|F2D
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visitVarInsn
argument_list|(
name|Opcodes
operator|.
name|DSTORE
argument_list|,
name|score
operator|.
name|getSlot
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reserved
operator|.
name|usesCtx
argument_list|()
condition|)
block|{
comment|// if the _ctx value is used, we do this once:
comment|// final Map<String,Object> ctx = input.get("ctx");
name|Variable
name|input
init|=
name|mainMethod
operator|.
name|getVariable
argument_list|(
literal|null
argument_list|,
name|Locals
operator|.
name|PARAMS
argument_list|)
decl_stmt|;
name|Variable
name|ctx
init|=
name|mainMethod
operator|.
name|getVariable
argument_list|(
literal|null
argument_list|,
name|Locals
operator|.
name|CTX
argument_list|)
decl_stmt|;
name|writer
operator|.
name|visitVarInsn
argument_list|(
name|Opcodes
operator|.
name|ALOAD
argument_list|,
name|input
operator|.
name|getSlot
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|push
argument_list|(
name|Locals
operator|.
name|CTX
argument_list|)
expr_stmt|;
name|writer
operator|.
name|invokeInterface
argument_list|(
name|MAP_TYPE
argument_list|,
name|MAP_GET
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visitVarInsn
argument_list|(
name|Opcodes
operator|.
name|ASTORE
argument_list|,
name|ctx
operator|.
name|getSlot
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reserved
operator|.
name|getMaxLoopCounter
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// if there is infinite loop protection, we do this once:
comment|// int #loop = settings.getMaxLoopCounter()
name|Variable
name|loop
init|=
name|mainMethod
operator|.
name|getVariable
argument_list|(
literal|null
argument_list|,
name|Locals
operator|.
name|LOOP
argument_list|)
decl_stmt|;
name|writer
operator|.
name|push
argument_list|(
name|reserved
operator|.
name|getMaxLoopCounter
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|visitVarInsn
argument_list|(
name|Opcodes
operator|.
name|ISTORE
argument_list|,
name|loop
operator|.
name|getSlot
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AStatement
name|statement
range|:
name|statements
control|)
block|{
name|statement
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|globals
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|methodEscape
condition|)
block|{
name|writer
operator|.
name|visitInsn
argument_list|(
name|Opcodes
operator|.
name|ACONST_NULL
argument_list|)
expr_stmt|;
name|writer
operator|.
name|returnValue
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getStatements
specifier|public
name|BitSet
name|getStatements
parameter_list|()
block|{
return|return
name|globals
operator|.
name|getStatements
argument_list|()
return|;
block|}
DECL|method|getBytes
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
DECL|class|MainMethodReserved
specifier|public
specifier|static
specifier|final
class|class
name|MainMethodReserved
implements|implements
name|Reserved
block|{
DECL|field|score
specifier|private
name|boolean
name|score
init|=
literal|false
decl_stmt|;
DECL|field|ctx
specifier|private
name|boolean
name|ctx
init|=
literal|false
decl_stmt|;
DECL|field|maxLoopCounter
specifier|private
name|int
name|maxLoopCounter
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|markReserved
specifier|public
name|void
name|markReserved
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|Locals
operator|.
name|SCORE
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|score
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Locals
operator|.
name|CTX
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|ctx
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isReserved
specifier|public
name|boolean
name|isReserved
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|Locals
operator|.
name|KEYWORDS
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|usesScore
specifier|public
name|boolean
name|usesScore
parameter_list|()
block|{
return|return
name|score
return|;
block|}
DECL|method|usesCtx
specifier|public
name|boolean
name|usesCtx
parameter_list|()
block|{
return|return
name|ctx
return|;
block|}
annotation|@
name|Override
DECL|method|setMaxLoopCounter
specifier|public
name|void
name|setMaxLoopCounter
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|maxLoopCounter
operator|=
name|max
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxLoopCounter
specifier|public
name|int
name|getMaxLoopCounter
parameter_list|()
block|{
return|return
name|maxLoopCounter
return|;
block|}
block|}
block|}
end_class

end_unit

