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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|AcquirableResourceFactory
specifier|public
specifier|final
class|class
name|AcquirableResourceFactory
block|{
DECL|method|newAcquirableResource
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Releasable
parameter_list|>
name|AcquirableResource
argument_list|<
name|T
argument_list|>
name|newAcquirableResource
parameter_list|(
name|T
name|resource
parameter_list|)
block|{
return|return
operator|new
name|BlockingAcquirableResource
argument_list|<
name|T
argument_list|>
argument_list|(
name|resource
argument_list|)
return|;
block|}
DECL|method|AcquirableResourceFactory
specifier|private
name|AcquirableResourceFactory
parameter_list|()
block|{      }
block|}
end_class

end_unit

