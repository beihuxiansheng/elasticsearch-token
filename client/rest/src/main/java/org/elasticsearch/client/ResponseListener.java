begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
package|;
end_package

begin_comment
comment|/**  * Listener to be provided when calling async performRequest methods provided by {@link RestClient}.  * Those methods that do accept a listener will return immediately, execute asynchronously, and notify  * the listener whenever the request yielded a response, or failed with an exception.  */
end_comment

begin_interface
DECL|interface|ResponseListener
specifier|public
interface|interface
name|ResponseListener
block|{
comment|/**      * Method invoked if the request yielded a successful response      */
DECL|method|onSuccess
name|void
name|onSuccess
parameter_list|(
name|Response
name|response
parameter_list|)
function_decl|;
comment|/**      * Method invoked if the request failed. There are two main categories of failures: connection failures (usually      * {@link java.io.IOException}s, or responses that were treated as errors based on their error response code      * ({@link ResponseException}s).      */
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|Exception
name|exception
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

