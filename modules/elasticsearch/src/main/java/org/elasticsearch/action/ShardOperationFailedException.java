begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|Streamable
import|;
end_import

begin_comment
comment|/**  * An exception indicating that a failure occurred performing an operation on the shard.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
DECL|interface|ShardOperationFailedException
specifier|public
interface|interface
name|ShardOperationFailedException
extends|extends
name|Streamable
block|{
comment|/**      * The index the operation failed on. Might return<tt>null</tt> if it can't be derived.      */
DECL|method|index
name|String
name|index
parameter_list|()
function_decl|;
comment|/**      * The index the operation failed on. Might return<tt>-1</tt> if it can't be derived.      */
DECL|method|shardId
name|int
name|shardId
parameter_list|()
function_decl|;
comment|/**      * The reason of the failure.      */
DECL|method|reason
name|String
name|reason
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

