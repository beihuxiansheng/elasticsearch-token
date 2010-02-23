begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
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
name|json
operator|.
name|BinaryJsonBuilder
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
operator|.
name|JsonBuilder
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|RestJsonBuilder
specifier|public
class|class
name|RestJsonBuilder
block|{
DECL|method|restJsonBuilder
specifier|public
specifier|static
name|BinaryJsonBuilder
name|restJsonBuilder
parameter_list|(
name|RestRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryJsonBuilder
name|builder
init|=
name|binaryJsonBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"pretty"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|builder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

