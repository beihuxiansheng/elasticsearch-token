begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bootstrap
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
package|;
end_package

begin_comment
comment|/**  * Encapsulates a bootstrap check.  */
end_comment

begin_interface
DECL|interface|BootstrapCheck
specifier|public
interface|interface
name|BootstrapCheck
block|{
comment|/**      * Test if the node fails the check.      *      * @return {@code true} if the node failed the check      */
DECL|method|check
name|boolean
name|check
parameter_list|()
function_decl|;
comment|/**      * The error message for a failed check.      *      * @return the error message on check failure      */
DECL|method|errorMessage
name|String
name|errorMessage
parameter_list|()
function_decl|;
DECL|method|alwaysEnforce
specifier|default
name|boolean
name|alwaysEnforce
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_interface

end_unit

