begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
comment|/**  * marker interface  */
end_comment

begin_interface
DECL|interface|HasContextAndHeaders
specifier|public
interface|interface
name|HasContextAndHeaders
extends|extends
name|HasContext
extends|,
name|HasHeaders
block|{
comment|/**      * copies over the context and the headers      * @param other another object supporting headers and context      */
DECL|method|copyContextAndHeadersFrom
name|void
name|copyContextAndHeadersFrom
parameter_list|(
name|HasContextAndHeaders
name|other
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

