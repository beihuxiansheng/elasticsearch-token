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

begin_interface
DECL|interface|ConsoleCtrlHandler
specifier|public
interface|interface
name|ConsoleCtrlHandler
block|{
DECL|field|CTRL_CLOSE_EVENT
name|int
name|CTRL_CLOSE_EVENT
init|=
literal|2
decl_stmt|;
comment|/**      * Handles the Ctrl event.      *      * @param code the code corresponding to the Ctrl sent.      * @return true if the handler processed the event, false otherwise. If false, the next handler will be called.      */
DECL|method|handle
name|boolean
name|handle
parameter_list|(
name|int
name|code
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

