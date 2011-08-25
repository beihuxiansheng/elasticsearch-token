begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|component
operator|.
name|LifecycleComponent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadSafe
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
name|service
operator|.
name|IndexService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_interface
annotation|@
name|ThreadSafe
DECL|interface|IndicesService
specifier|public
interface|interface
name|IndicesService
extends|extends
name|Iterable
argument_list|<
name|IndexService
argument_list|>
extends|,
name|LifecycleComponent
argument_list|<
name|IndicesService
argument_list|>
block|{
comment|/**      * Returns<tt>true</tt> if changes (adding / removing) indices, shards and so on are allowed.      */
DECL|method|changesAllowed
specifier|public
name|boolean
name|changesAllowed
parameter_list|()
function_decl|;
comment|/**      * Returns the node stats indices stats. The<tt>includePrevious</tt> flag controls      * if old shards stats will be aggregated as well (only for relevant stats, such as      * refresh and indexing, not for docs/store).      */
DECL|method|stats
name|NodeIndicesStats
name|stats
parameter_list|(
name|boolean
name|includePrevious
parameter_list|)
function_decl|;
DECL|method|hasIndex
name|boolean
name|hasIndex
parameter_list|(
name|String
name|index
parameter_list|)
function_decl|;
DECL|method|indicesLifecycle
name|IndicesLifecycle
name|indicesLifecycle
parameter_list|()
function_decl|;
DECL|method|indices
name|Set
argument_list|<
name|String
argument_list|>
name|indices
parameter_list|()
function_decl|;
DECL|method|indexService
name|IndexService
name|indexService
parameter_list|(
name|String
name|index
parameter_list|)
function_decl|;
DECL|method|indexServiceSafe
name|IndexService
name|indexServiceSafe
parameter_list|(
name|String
name|index
parameter_list|)
throws|throws
name|IndexMissingException
function_decl|;
DECL|method|createIndex
name|IndexService
name|createIndex
parameter_list|(
name|String
name|index
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|String
name|localNodeId
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|deleteIndex
name|void
name|deleteIndex
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|reason
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
comment|/**      * Cleans the index without actually deleting any content for it.      */
DECL|method|cleanIndex
name|void
name|cleanIndex
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|reason
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
block|}
end_interface

end_unit

