begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  *  */
end_comment

begin_package
DECL|package|org.elasticsearch.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
package|;
end_package

begin_comment
comment|/**  * Represents a function that accepts three arguments and produces a result.  *  * @param<S> the type of the first argument  * @param<T> the type of the second argument  * @param<U> the type of the third argument  * @param<R> the return type  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
DECL|interface|TriFunction
specifier|public
interface|interface
name|TriFunction
parameter_list|<
name|S
parameter_list|,
name|T
parameter_list|,
name|U
parameter_list|,
name|R
parameter_list|>
block|{
comment|/**      * Applies this function to the given arguments.      *      * @param s the first function argument      * @param t the second function argument      * @param u the third function argument      * @return the result      */
DECL|method|apply
name|R
name|apply
parameter_list|(
name|S
name|s
parameter_list|,
name|T
name|t
parameter_list|,
name|U
name|u
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

