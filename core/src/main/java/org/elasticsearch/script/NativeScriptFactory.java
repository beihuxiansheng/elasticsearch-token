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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Nullable
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
comment|/**  * A factory to create instances of either {@link ExecutableScript} or {@link SearchScript}. Note,  * if this factory creates {@link SearchScript}, it must extend {@link AbstractSearchScript}.  *  * @see AbstractExecutableScript  * @see AbstractSearchScript  * @see AbstractLongSearchScript  * @see AbstractDoubleSearchScript  */
end_comment

begin_interface
DECL|interface|NativeScriptFactory
specifier|public
interface|interface
name|NativeScriptFactory
block|{
comment|/**      * Creates a new instance of either a {@link ExecutableScript} or a {@link SearchScript}.      *      * @param params The parameters passed to the script. Can be<tt>null</tt>.      */
DECL|method|newScript
name|ExecutableScript
name|newScript
parameter_list|(
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
function_decl|;
comment|/**      * Indicates if document scores may be needed by the produced scripts.      *      * @return {@code true} if scores are needed.      */
DECL|method|needsScores
name|boolean
name|needsScores
parameter_list|()
function_decl|;
comment|/**      * Returns the name of the script factory      */
DECL|method|getName
name|String
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

