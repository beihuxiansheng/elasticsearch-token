begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
package|;
end_package

begin_comment
comment|/**  * Rest pre processor allowing to pre process REST requests.  *<p/>  * Experimental interface.  */
end_comment

begin_interface
DECL|interface|RestPreProcessor
specifier|public
interface|interface
name|RestPreProcessor
block|{
comment|/**      * Optionally, the order the processor will work on. Execution is done from lowest value to highest.      * It is a good practice to allow to configure this for the relevant processor.      */
DECL|method|order
name|int
name|order
parameter_list|()
function_decl|;
comment|/**      * Should this processor also process external (non REST) requests, like plugin site requests.      */
DECL|method|handleExternal
name|boolean
name|handleExternal
parameter_list|()
function_decl|;
comment|/**      * Process the request, returning<tt>false</tt> if no further processing should be done. Note,      * make sure to send a response if returning<tt>false</tt>, otherwise, no response will be sent.      *<p/>      * It is recommended that the process method will not do blocking calls, or heavily cache data      * if a blocking call is done.      */
DECL|method|process
name|boolean
name|process
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

