begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
comment|/**  * This class defines {@link RoutingException}s related to  * the validation of routing  */
end_comment

begin_class
DECL|class|RoutingValidationException
specifier|public
class|class
name|RoutingValidationException
extends|extends
name|RoutingException
block|{
DECL|field|validation
specifier|private
specifier|final
name|RoutingTableValidation
name|validation
decl_stmt|;
DECL|method|RoutingValidationException
specifier|public
name|RoutingValidationException
parameter_list|(
name|RoutingTableValidation
name|validation
parameter_list|)
block|{
name|super
argument_list|(
name|validation
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|validation
operator|=
name|validation
expr_stmt|;
block|}
DECL|method|RoutingValidationException
specifier|public
name|RoutingValidationException
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|validation
operator|=
name|in
operator|.
name|readOptionalStreamable
argument_list|(
name|RoutingTableValidation
operator|::
operator|new
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalStreamable
argument_list|(
name|validation
argument_list|)
expr_stmt|;
block|}
DECL|method|validation
specifier|public
name|RoutingTableValidation
name|validation
parameter_list|()
block|{
return|return
name|this
operator|.
name|validation
return|;
block|}
block|}
end_class

end_unit

