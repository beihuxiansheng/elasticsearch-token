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
name|Variables
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
name|elasticsearch
operator|.
name|painless
operator|.
name|MethodWriter
import|;
end_import

begin_comment
comment|/**  * The superclass for all S* (statement) nodes.  */
end_comment

begin_class
DECL|class|AStatement
specifier|public
specifier|abstract
class|class
name|AStatement
extends|extends
name|ANode
block|{
comment|/**      * Set to true when the final statement in an {@link SSource} is reached.      * Used to determine whether or not an auto-return is necessary.      */
DECL|field|lastSource
name|boolean
name|lastSource
init|=
literal|false
decl_stmt|;
comment|/**      * Set to true when a loop begins.  Used by {@link SBlock} to help determine      * when the final statement of a loop is reached.      */
DECL|field|beginLoop
name|boolean
name|beginLoop
init|=
literal|false
decl_stmt|;
comment|/**      * Set to true when inside a loop.  Used by {@link SBreak} and {@link SContinue}      * to determine if a break/continue statement is legal.      */
DECL|field|inLoop
name|boolean
name|inLoop
init|=
literal|false
decl_stmt|;
comment|/**      * Set to true when on the last statement of a loop.  Used by {@link SContinue}      * to prevent extraneous continue statements.      */
DECL|field|lastLoop
name|boolean
name|lastLoop
init|=
literal|false
decl_stmt|;
comment|/**      * Set to true if a statement would cause the method to exit.  Used to      * determine whether or not an auto-return is necessary.      */
DECL|field|methodEscape
name|boolean
name|methodEscape
init|=
literal|false
decl_stmt|;
comment|/**      * Set to true if a statement would cause a loop to exit.  Used to      * prevent unreachable statements.      */
DECL|field|loopEscape
name|boolean
name|loopEscape
init|=
literal|false
decl_stmt|;
comment|/**      * Set to true if all current paths escape from the current {@link SBlock}.      * Used during the analysis phase to prevent unreachable statements and      * the writing phase to prevent extraneous bytecode gotos from being written.      */
DECL|field|allEscape
name|boolean
name|allEscape
init|=
literal|false
decl_stmt|;
comment|/**      * Set to true if any continue statement occurs in a loop.  Used to prevent      * unnecessary infinite loops.      */
DECL|field|anyContinue
name|boolean
name|anyContinue
init|=
literal|false
decl_stmt|;
comment|/**      * Set to true if any break statement occurs in a loop.  Used to prevent      * extraneous loops.      */
DECL|field|anyBreak
name|boolean
name|anyBreak
init|=
literal|false
decl_stmt|;
comment|/**      * Set to the loop counter variable slot as a shortcut if loop statements      * are being counted.      */
DECL|field|loopCounterSlot
name|int
name|loopCounterSlot
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Set to the approximate number of statements in a loop block to prevent      * infinite loops during runtime.      */
DECL|field|statementCount
name|int
name|statementCount
init|=
literal|0
decl_stmt|;
comment|/**      * Set to the beginning of a loop so a continue statement knows where to      * jump to.  Only used during the writing phase.      */
DECL|field|continu
name|Label
name|continu
init|=
literal|null
decl_stmt|;
comment|/**      * Set to the beginning of a loop so a break statement knows where to      * jump to.  Only used during the writing phase.      */
DECL|field|brake
name|Label
name|brake
init|=
literal|null
decl_stmt|;
DECL|method|AStatement
name|AStatement
parameter_list|(
name|int
name|line
parameter_list|,
name|int
name|offset
parameter_list|,
name|String
name|location
parameter_list|)
block|{
name|super
argument_list|(
name|line
argument_list|,
name|offset
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks for errors and collects data for the writing phase.      */
DECL|method|analyze
specifier|abstract
name|void
name|analyze
parameter_list|(
name|Variables
name|variables
parameter_list|)
function_decl|;
comment|/**      * Writes ASM based on the data collected during the analysis phase.      */
DECL|method|write
specifier|abstract
name|void
name|write
parameter_list|(
name|MethodWriter
name|writer
parameter_list|)
function_decl|;
block|}
end_class

end_unit

