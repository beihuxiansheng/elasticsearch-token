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
comment|/**      * The line number in the original source used for debugging and errors.      */
DECL|field|line
specifier|final
name|int
name|line
decl_stmt|;
comment|/**      * The character offset in the original source used for debugging and errors.      */
DECL|field|offset
specifier|final
name|int
name|offset
decl_stmt|;
comment|/**      * The location in the original source to be printed in error messages.      */
DECL|field|location
specifier|final
name|String
name|location
decl_stmt|;
DECL|method|ANode
name|ANode
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
name|this
operator|.
name|line
operator|=
name|line
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
block|}
DECL|method|error
specifier|public
name|String
name|error
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
return|return
literal|"Error "
operator|+
name|location
operator|+
literal|": "
operator|+
name|message
return|;
block|}
block|}
end_class

end_unit

