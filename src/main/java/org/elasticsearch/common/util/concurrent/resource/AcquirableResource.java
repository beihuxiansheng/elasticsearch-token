begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util.concurrent.resource
package|package
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
name|resource
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
name|lease
operator|.
name|Releasable
import|;
end_import

begin_comment
comment|/**  * A wrapper around a resource that can be released. Note, release should not be  * called directly on the resource itself.  *<p/>  *<p>Yea, I now, the fact that the resouce itself is releasable basically means that  * users of this class should take care... .  *  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
DECL|interface|AcquirableResource
specifier|public
interface|interface
name|AcquirableResource
parameter_list|<
name|T
extends|extends
name|Releasable
parameter_list|>
block|{
DECL|method|resource
name|T
name|resource
parameter_list|()
function_decl|;
comment|/**      * Acquires the resource, returning<tt>true</tt> if it was acquired.      */
DECL|method|acquire
name|boolean
name|acquire
parameter_list|()
function_decl|;
comment|/**      * Releases the resource, will close it if there are no more acquirers and it is marked for close.      */
DECL|method|release
name|void
name|release
parameter_list|()
function_decl|;
comment|/**      * Marks the resource to be closed. Will close it if there are no current      * acquires.      */
DECL|method|markForClose
name|void
name|markForClose
parameter_list|()
function_decl|;
comment|/**      * Forces the resource to be closed, regardless of the number of acquirers.      */
DECL|method|forceClose
name|void
name|forceClose
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

