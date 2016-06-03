begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
package|;
end_package

begin_comment
comment|/**  * An executable script, can't be used concurrently.  */
end_comment

begin_interface
DECL|interface|ExecutableScript
specifier|public
interface|interface
name|ExecutableScript
block|{
comment|/**      * Sets a runtime script parameter.      *<p>      * Note that this method may be slow, involving put() and get() calls      * to a hashmap or similar.      * @param name parameter name      * @param value parameter value      */
DECL|method|setNextVar
name|void
name|setNextVar
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**      * Executes the script.      */
DECL|method|run
name|Object
name|run
parameter_list|()
function_decl|;
comment|/**      * Unwraps a possible script value. For example, when passing vars and      * expecting the returned value to be part of the vars. Javascript and      * Python need this but other scripting engines just return the values      * passed in.      */
DECL|method|unwrap
specifier|default
name|Object
name|unwrap
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
block|}
end_interface

end_unit

