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
name|Label
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
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/**  * The super class for an expression that can store a value in local memory.  */
end_comment

begin_class
DECL|class|AStoreable
specifier|abstract
class|class
name|AStoreable
extends|extends
name|AExpression
block|{
comment|/**      * Set to true when this node is an lhs-expression and will be storing      * a value from an rhs-expression.      */
DECL|field|write
name|boolean
name|write
init|=
literal|false
decl_stmt|;
comment|/**      * Standard constructor with location used for error tracking.      */
DECL|method|AStoreable
name|AStoreable
parameter_list|(
name|Location
name|location
parameter_list|)
block|{
name|super
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * This constructor is used by variable/method chains when postfixes are specified.      */
DECL|method|AStoreable
name|AStoreable
parameter_list|(
name|Location
name|location
parameter_list|,
name|AExpression
name|prefix
parameter_list|)
block|{
name|super
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns a value based on the number of elements previously placed on the      * stack to load/store a certain piece of a variable/method chain.  This is      * used during the writing phase to dup stack values from this storeable as      * necessary during certain store operations.      *<p>      * Examples:      * {@link EVariable} returns 0 because it requires nothing extra to perform      *                   a load/store      * {@link PSubField} returns 1 because it requires the name of the field as      *                   an index on the stack to perform a load/store      * {@link PSubBrace} returns 2 because it requires both the variable slot and      *                   an index into the array on the stack to perform a      *                   load/store      */
DECL|method|accessElementCount
specifier|abstract
name|int
name|accessElementCount
parameter_list|()
function_decl|;
comment|/**      * Returns true if this node or a sub-node of this node can be optimized with      * rhs actual type to avoid an unnecessary cast.      */
DECL|method|isDefOptimized
specifier|abstract
name|boolean
name|isDefOptimized
parameter_list|()
function_decl|;
comment|/**      * If this node or a sub-node of this node uses dynamic calls then      * actual will be set to this value. This is used for an optimization      * during assignment to def type targets.      */
DECL|method|updateActual
specifier|abstract
name|void
name|updateActual
parameter_list|(
name|Type
name|actual
parameter_list|)
function_decl|;
comment|/**      * Called before a storeable node is loaded or stored.  Used to load prefixes and      * push load/store constants onto the stack if necessary.      */
DECL|method|setup
specifier|abstract
name|void
name|setup
parameter_list|(
name|MethodWriter
name|writer
parameter_list|,
name|Globals
name|globals
parameter_list|)
function_decl|;
comment|/**      * Called to load a storable used for compound assignments.      */
DECL|method|load
specifier|abstract
name|void
name|load
parameter_list|(
name|MethodWriter
name|writer
parameter_list|,
name|Globals
name|globals
parameter_list|)
function_decl|;
comment|/**      * Called to store a storabable to local memory.      */
DECL|method|store
specifier|abstract
name|void
name|store
parameter_list|(
name|MethodWriter
name|writer
parameter_list|,
name|Globals
name|globals
parameter_list|)
function_decl|;
comment|/**      * Writes the opcodes to flip a negative array index (meaning slots from the end of the array) into a 0-based one (meaning slots from      * the start of the array).      */
DECL|method|writeIndexFlip
specifier|static
name|void
name|writeIndexFlip
parameter_list|(
name|MethodWriter
name|writer
parameter_list|,
name|Consumer
argument_list|<
name|MethodWriter
argument_list|>
name|writeGetLength
parameter_list|)
block|{
name|Label
name|noFlip
init|=
operator|new
name|Label
argument_list|()
decl_stmt|;
comment|// Everywhere when it says 'array' below that could also be a list
comment|// The stack after each instruction:       array, unnormalized_index
name|writer
operator|.
name|dup
argument_list|()
expr_stmt|;
comment|// array, unnormalized_index, unnormalized_index
name|writer
operator|.
name|ifZCmp
argument_list|(
name|Opcodes
operator|.
name|IFGE
argument_list|,
name|noFlip
argument_list|)
expr_stmt|;
comment|// array, unnormalized_index
name|writer
operator|.
name|swap
argument_list|()
expr_stmt|;
comment|// negative_index, array
name|writer
operator|.
name|dupX1
argument_list|()
expr_stmt|;
comment|// array, negative_index, array
name|writeGetLength
operator|.
name|accept
argument_list|(
name|writer
argument_list|)
expr_stmt|;
comment|// array, negative_index, length
name|writer
operator|.
name|visitInsn
argument_list|(
name|Opcodes
operator|.
name|IADD
argument_list|)
expr_stmt|;
comment|// array, noralized_index
name|writer
operator|.
name|mark
argument_list|(
name|noFlip
argument_list|)
expr_stmt|;
comment|// array, noralized_index
block|}
block|}
end_class

end_unit

