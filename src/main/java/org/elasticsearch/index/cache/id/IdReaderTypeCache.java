begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.id
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|id
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
name|bytes
operator|.
name|HashedBytesArray
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|IdReaderTypeCache
specifier|public
interface|interface
name|IdReaderTypeCache
block|{
comment|/**      * @param docId The Lucene docId of the child document to return the parent _uid for.      * @return The parent _uid for the specified docId (which is a child document)      */
DECL|method|parentIdByDoc
name|HashedBytesArray
name|parentIdByDoc
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
comment|/**      * @param uid The uid of the document to return the lucene docId for      * @return The lucene docId for the specified uid      */
DECL|method|docById
name|int
name|docById
parameter_list|(
name|HashedBytesArray
name|uid
parameter_list|)
function_decl|;
comment|/**      * @param docId The lucene docId of the document to return _uid for      * @return The _uid of the specified docId      */
DECL|method|idByDoc
name|HashedBytesArray
name|idByDoc
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
comment|/**      * @return The size in bytes for this particular instance      */
DECL|method|sizeInBytes
name|long
name|sizeInBytes
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

