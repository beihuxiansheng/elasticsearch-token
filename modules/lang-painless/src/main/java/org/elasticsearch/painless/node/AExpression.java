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
name|Location
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

begin_comment
comment|/**  * The superclass for all E* (expression) and P* (postfix) nodes.  */
end_comment

begin_class
DECL|class|AExpression
specifier|public
specifier|abstract
class|class
name|AExpression
extends|extends
name|ANode
block|{
comment|/**      * Prefix is the predecessor to this node in a variable chain.      * This is used to analyze and write variable chains in a      * more natural order since the parent node of a variable      * chain will want the data from the final postfix to be      * analyzed.      */
DECL|field|prefix
name|AExpression
name|prefix
decl_stmt|;
comment|/**      * Set to false when an expression will not be read from such as      * a basic assignment.  Note this variable is always set by the parent      * as input.      */
DECL|field|read
name|boolean
name|read
init|=
literal|true
decl_stmt|;
comment|/**      * Set to true when an expression can be considered a stand alone      * statement.  Used to prevent extraneous bytecode. This is always      * set by the node as output.      */
DECL|field|statement
name|boolean
name|statement
init|=
literal|false
decl_stmt|;
comment|/**      * Set to the expected type this node needs to be.  Note this variable      * is always set by the parent as input and should never be read from.      */
DECL|field|expected
name|Type
name|expected
init|=
literal|null
decl_stmt|;
comment|/**      * Set to the actual type this node is.  Note this variable is always      * set by the node as output and should only be read from outside of the      * node itself.<b>Also, actual can always be read after a cast is      * called on this node to get the type of the node after the cast.</b>      */
DECL|field|actual
name|Type
name|actual
init|=
literal|null
decl_stmt|;
comment|/**      * Set by {@link EExplicit} if a cast made on an expression node should be      * explicit.      */
DECL|field|explicit
name|boolean
name|explicit
init|=
literal|false
decl_stmt|;
comment|/**      * Set to true if a cast is allowed to boxed/unboxed.  This is used      * for method arguments because casting may be required.      */
DECL|field|internal
name|boolean
name|internal
init|=
literal|false
decl_stmt|;
comment|/**      * Set to the value of the constant this expression node represents if      * and only if the node represents a constant.  If this is not null      * this node will be replaced by an {@link EConstant} during casting      * if it's not already one.      */
DECL|field|constant
name|Object
name|constant
init|=
literal|null
decl_stmt|;
comment|/**      * Set to true by {@link ENull} to represent a null value.      */
DECL|field|isNull
name|boolean
name|isNull
init|=
literal|false
decl_stmt|;
comment|/**      * Standard constructor with location used for error tracking.      */
DECL|method|AExpression
name|AExpression
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
DECL|method|AExpression
name|AExpression
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
comment|/**      * Inserts {@link ECast} nodes into the tree for implicit casts.  Also replaces      * nodes with the constant variable set to a non-null value with {@link EConstant}.      * @return The new child node for the parent node calling this method.      */
DECL|method|cast
name|AExpression
name|cast
parameter_list|(
name|Locals
name|locals
parameter_list|)
block|{
name|Cast
name|cast
init|=
name|AnalyzerCaster
operator|.
name|getLegalCast
argument_list|(
name|location
argument_list|,
name|actual
argument_list|,
name|expected
argument_list|,
name|explicit
argument_list|,
name|internal
argument_list|)
decl_stmt|;
if|if
condition|(
name|cast
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|constant
operator|==
literal|null
operator|||
name|this
operator|instanceof
name|EConstant
condition|)
block|{
comment|// For the case where a cast is not required and a constant is not set
comment|// or the node is already an EConstant no changes are required to the tree.
return|return
name|this
return|;
block|}
else|else
block|{
comment|// For the case where a cast is not required but a
comment|// constant is set, an EConstant replaces this node
comment|// with the constant copied from this node.  Note that
comment|// for constants output data does not need to be copied
comment|// from this node because the output data for the EConstant
comment|// will already be the same.
name|EConstant
name|econstant
init|=
operator|new
name|EConstant
argument_list|(
name|location
argument_list|,
name|constant
argument_list|)
decl_stmt|;
name|econstant
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|expected
operator|.
name|equals
argument_list|(
name|econstant
operator|.
name|actual
argument_list|)
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|econstant
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|constant
operator|==
literal|null
condition|)
block|{
comment|// For the case where a cast is required and a constant is not set.
comment|// Modify the tree to add an ECast between this node and its parent.
comment|// The output data from this node is copied to the ECast for
comment|// further reads done by the parent.
name|ECast
name|ecast
init|=
operator|new
name|ECast
argument_list|(
name|location
argument_list|,
name|this
argument_list|,
name|cast
argument_list|)
decl_stmt|;
name|ecast
operator|.
name|statement
operator|=
name|statement
expr_stmt|;
name|ecast
operator|.
name|actual
operator|=
name|expected
expr_stmt|;
name|ecast
operator|.
name|isNull
operator|=
name|isNull
expr_stmt|;
return|return
name|ecast
return|;
block|}
else|else
block|{
if|if
condition|(
name|expected
operator|.
name|sort
operator|.
name|constant
condition|)
block|{
comment|// For the case where a cast is required, a constant is set,
comment|// and the constant can be immediately cast to the expected type.
comment|// An EConstant replaces this node with the constant cast appropriately
comment|// from the constant value defined by this node.  Note that
comment|// for constants output data does not need to be copied
comment|// from this node because the output data for the EConstant
comment|// will already be the same.
name|constant
operator|=
name|AnalyzerCaster
operator|.
name|constCast
argument_list|(
name|location
argument_list|,
name|constant
argument_list|,
name|cast
argument_list|)
expr_stmt|;
name|EConstant
name|econstant
init|=
operator|new
name|EConstant
argument_list|(
name|location
argument_list|,
name|constant
argument_list|)
decl_stmt|;
name|econstant
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|expected
operator|.
name|equals
argument_list|(
name|econstant
operator|.
name|actual
argument_list|)
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|econstant
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|instanceof
name|EConstant
condition|)
block|{
comment|// For the case where a cast is required, a constant is set,
comment|// the constant cannot be immediately cast to the expected type,
comment|// and this node is already an EConstant.  Modify the tree to add
comment|// an ECast between this node and its parent.  Note that
comment|// for constants output data does not need to be copied
comment|// from this node because the output data for the EConstant
comment|// will already be the same.
name|ECast
name|ecast
init|=
operator|new
name|ECast
argument_list|(
name|location
argument_list|,
name|this
argument_list|,
name|cast
argument_list|)
decl_stmt|;
name|ecast
operator|.
name|actual
operator|=
name|expected
expr_stmt|;
return|return
name|ecast
return|;
block|}
else|else
block|{
comment|// For the case where a cast is required, a constant is set,
comment|// the constant cannot be immediately cast to the expected type,
comment|// and this node is not an EConstant.  Replace this node with
comment|// an Econstant node copying the constant from this node.
comment|// Modify the tree to add an ECast between the EConstant node
comment|// and its parent.  Note that for constants output data does not
comment|// need to be copied from this node because the output data for
comment|// the EConstant will already be the same.
name|EConstant
name|econstant
init|=
operator|new
name|EConstant
argument_list|(
name|location
argument_list|,
name|constant
argument_list|)
decl_stmt|;
name|econstant
operator|.
name|analyze
argument_list|(
name|locals
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|actual
operator|.
name|equals
argument_list|(
name|econstant
operator|.
name|actual
argument_list|)
condition|)
block|{
throw|throw
name|createError
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
name|ECast
name|ecast
init|=
operator|new
name|ECast
argument_list|(
name|location
argument_list|,
name|econstant
argument_list|,
name|cast
argument_list|)
decl_stmt|;
name|ecast
operator|.
name|actual
operator|=
name|expected
expr_stmt|;
return|return
name|ecast
return|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

