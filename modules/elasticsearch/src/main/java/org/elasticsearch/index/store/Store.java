begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|IndexShardComponent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|IndexShardLifecycle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|SizeValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
annotation|@
name|IndexShardLifecycle
DECL|interface|Store
specifier|public
interface|interface
name|Store
parameter_list|<
name|T
extends|extends
name|Directory
parameter_list|>
extends|extends
name|IndexShardComponent
block|{
comment|/**      * The Lucene {@link Directory} this store is using.      */
DECL|method|directory
name|T
name|directory
parameter_list|()
function_decl|;
comment|/**      * Just deletes the content of the store.      */
DECL|method|deleteContent
name|void
name|deleteContent
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Deletes the store completely. For example, in FS ones, also deletes the parent      * directory.      */
DECL|method|fullDelete
name|void
name|fullDelete
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * The estimated size this store is using.      */
DECL|method|estimateSize
name|SizeValue
name|estimateSize
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * The store can suggest the best setting for compound file the      * {@link org.apache.lucene.index.MergePolicy} will use.      */
DECL|method|suggestUseCompoundFile
name|boolean
name|suggestUseCompoundFile
parameter_list|()
function_decl|;
comment|/**      * Close the store.      */
DECL|method|close
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

