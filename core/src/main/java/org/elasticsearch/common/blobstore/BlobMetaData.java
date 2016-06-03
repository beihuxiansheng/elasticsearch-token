begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.blobstore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|blobstore
package|;
end_package

begin_comment
comment|/**  * An interface for providing basic metadata about a blob.  */
end_comment

begin_interface
DECL|interface|BlobMetaData
specifier|public
interface|interface
name|BlobMetaData
block|{
comment|/**      * Gets the name of the blob.      */
DECL|method|name
name|String
name|name
parameter_list|()
function_decl|;
comment|/**      * Gets the size of the blob in bytes.      */
DECL|method|length
name|long
name|length
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

