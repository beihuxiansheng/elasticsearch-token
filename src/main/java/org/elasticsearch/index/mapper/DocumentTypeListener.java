begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
package|;
end_package

begin_comment
comment|/**  */
end_comment

begin_interface
DECL|interface|DocumentTypeListener
specifier|public
interface|interface
name|DocumentTypeListener
block|{
comment|/**      * Invoked when a new document type has been created.      *      * @param type The document type that has been created      */
DECL|method|created
name|void
name|created
parameter_list|(
name|String
name|type
parameter_list|)
function_decl|;
comment|/**      * Invoked when an existing document type has been removed.      *      * @param type The document type that has been removed      */
DECL|method|removed
name|void
name|removed
parameter_list|(
name|String
name|type
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

