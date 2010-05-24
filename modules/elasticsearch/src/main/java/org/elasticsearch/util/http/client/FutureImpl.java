begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2010 Ning, Inc.  *  * Ning licenses this file to you under the Apache License, version 2.0  * (the "License"); you may not use this file except in compliance with the  * License.  You may obtain a copy of the License at:  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the  * License for the specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.http.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|http
operator|.
name|client
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_comment
comment|/**  * Extended {@link Future}  *  * @param<V> Type of the value that will be returned.  */
end_comment

begin_interface
DECL|interface|FutureImpl
specifier|public
interface|interface
name|FutureImpl
parameter_list|<
name|V
parameter_list|>
extends|extends
name|Future
argument_list|<
name|V
argument_list|>
block|{
DECL|method|done
name|void
name|done
parameter_list|()
function_decl|;
DECL|method|abort
name|void
name|abort
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

