begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store.distributor
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
operator|.
name|distributor
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_comment
comment|/**  * Keeps track of available directories and selects a directory  * based on some distribution strategy  */
end_comment

begin_interface
DECL|interface|Distributor
specifier|public
interface|interface
name|Distributor
block|{
comment|/**      * Returns primary directory (typically first directory in the list)      */
DECL|method|primary
name|Directory
name|primary
parameter_list|()
function_decl|;
comment|/**      * Returns all directories      */
DECL|method|all
name|Directory
index|[]
name|all
parameter_list|()
function_decl|;
comment|/**      * Selects one of the directories based on distribution strategy      */
DECL|method|any
name|Directory
name|any
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

