begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|node
operator|.
name|NodeClient
import|;
end_import

begin_comment
comment|/**  * A filter allowing to filter rest operations.  */
end_comment

begin_class
DECL|class|RestFilter
specifier|public
specifier|abstract
class|class
name|RestFilter
implements|implements
name|Closeable
block|{
comment|/**      * Optionally, the order of the filter. Execution is done from lowest value to highest.      * It is a good practice to allow to configure this for the relevant filter.      */
DECL|method|order
specifier|public
name|int
name|order
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// a no op
block|}
comment|/**      * Process the rest request. Using the channel to send a response, or the filter chain to continue      * processing the request.      */
DECL|method|process
specifier|public
specifier|abstract
name|void
name|process
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|,
name|NodeClient
name|client
parameter_list|,
name|RestFilterChain
name|filterChain
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

