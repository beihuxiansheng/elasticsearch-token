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
comment|/**      * Invoked just before a new document type has been created.      *      * @param type The new document type      */
DECL|method|beforeCreate
name|void
name|beforeCreate
parameter_list|(
name|String
name|type
parameter_list|)
function_decl|;
comment|/**      * Invoked just after an existing document type has been removed.      *      * @param type The existing document type      */
DECL|method|afterRemove
name|void
name|afterRemove
parameter_list|(
name|String
name|type
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

