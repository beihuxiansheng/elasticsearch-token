begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|CloseableIndexComponent
specifier|public
interface|interface
name|CloseableIndexComponent
block|{
comment|/**      * Closes the index component. A boolean indicating if its part of an actual index      * deletion or not is passed.      *      * @param delete<tt>true</tt> if the index is being deleted.      * @throws ElasticSearchException      */
DECL|method|close
name|void
name|close
parameter_list|(
name|boolean
name|delete
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
block|}
end_interface

end_unit

