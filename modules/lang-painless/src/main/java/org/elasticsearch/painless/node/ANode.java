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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * The superclass for all other nodes.  */
end_comment

begin_class
DECL|class|ANode
specifier|public
specifier|abstract
class|class
name|ANode
block|{
comment|/**      * The identifier of the script and character offset used for debugging and errors.      */
DECL|field|location
specifier|final
name|Location
name|location
decl_stmt|;
DECL|method|ANode
name|ANode
parameter_list|(
name|Location
name|location
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds all variable names referenced to the variable set.      *<p>      * This can be called at any time, e.g. to support lambda capture.      * @param variables set of variables referenced (any scope)      */
DECL|method|extractVariables
specifier|abstract
name|void
name|extractVariables
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|variables
parameter_list|)
function_decl|;
DECL|method|createError
specifier|public
name|RuntimeException
name|createError
parameter_list|(
name|RuntimeException
name|exception
parameter_list|)
block|{
return|return
name|location
operator|.
name|createError
argument_list|(
name|exception
argument_list|)
return|;
block|}
block|}
end_class

end_unit

