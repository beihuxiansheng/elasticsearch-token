begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.master
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|master
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
comment|/**  * A based request for master based operation.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|MasterNodeOperationRequest
specifier|public
specifier|abstract
class|class
name|MasterNodeOperationRequest
implements|implements
name|ActionRequest
block|{
DECL|method|listenerThreaded
annotation|@
name|Override
specifier|public
name|boolean
name|listenerThreaded
parameter_list|()
block|{
comment|// always threaded
return|return
literal|true
return|;
block|}
DECL|method|listenerThreaded
annotation|@
name|Override
specifier|public
name|MasterNodeOperationRequest
name|listenerThreaded
parameter_list|(
name|boolean
name|listenerThreaded
parameter_list|)
block|{
comment|// really, does not mean anything in this case
return|return
name|this
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
specifier|public
name|void
name|readFrom
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{     }
DECL|method|writeTo
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{     }
block|}
end_class

end_unit

