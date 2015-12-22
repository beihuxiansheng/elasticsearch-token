begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
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
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * An extension to {@link Future} allowing for simplified "get" operations.  *  *  */
end_comment

begin_interface
DECL|interface|ActionFuture
specifier|public
interface|interface
name|ActionFuture
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Future
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * Similar to {@link #get()}, just catching the {@link InterruptedException} and throwing      * an {@link IllegalStateException} instead. Also catches      * {@link java.util.concurrent.ExecutionException} and throws the actual cause instead.      */
DECL|method|actionGet
name|T
name|actionGet
parameter_list|()
function_decl|;
comment|/**      * Similar to {@link #get(long, java.util.concurrent.TimeUnit)}, just catching the {@link InterruptedException} and throwing      * an {@link IllegalStateException} instead. Also catches      * {@link java.util.concurrent.ExecutionException} and throws the actual cause instead.      */
DECL|method|actionGet
name|T
name|actionGet
parameter_list|(
name|String
name|timeout
parameter_list|)
function_decl|;
comment|/**      * Similar to {@link #get(long, java.util.concurrent.TimeUnit)}, just catching the {@link InterruptedException} and throwing      * an {@link IllegalStateException} instead. Also catches      * {@link java.util.concurrent.ExecutionException} and throws the actual cause instead.      *      * @param timeoutMillis Timeout in millis      */
DECL|method|actionGet
name|T
name|actionGet
parameter_list|(
name|long
name|timeoutMillis
parameter_list|)
function_decl|;
comment|/**      * Similar to {@link #get(long, java.util.concurrent.TimeUnit)}, just catching the {@link InterruptedException} and throwing      * an {@link IllegalStateException} instead. Also catches      * {@link java.util.concurrent.ExecutionException} and throws the actual cause instead.      */
DECL|method|actionGet
name|T
name|actionGet
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
function_decl|;
comment|/**      * Similar to {@link #get(long, java.util.concurrent.TimeUnit)}, just catching the {@link InterruptedException} and throwing      * an {@link IllegalStateException} instead. Also catches      * {@link java.util.concurrent.ExecutionException} and throws the actual cause instead.      */
DECL|method|actionGet
name|T
name|actionGet
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

