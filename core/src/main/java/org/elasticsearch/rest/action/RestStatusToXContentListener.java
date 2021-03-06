begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
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
name|common
operator|.
name|xcontent
operator|.
name|StatusToXContentObject
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
name|xcontent
operator|.
name|XContentBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|BytesRestResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * Content listener that extracts that {@link RestStatus} from the response.  */
end_comment

begin_class
DECL|class|RestStatusToXContentListener
specifier|public
class|class
name|RestStatusToXContentListener
parameter_list|<
name|Response
extends|extends
name|StatusToXContentObject
parameter_list|>
extends|extends
name|RestToXContentListener
argument_list|<
name|Response
argument_list|>
block|{
DECL|field|extractLocation
specifier|private
specifier|final
name|Function
argument_list|<
name|Response
argument_list|,
name|String
argument_list|>
name|extractLocation
decl_stmt|;
comment|/**      * Build an instance that doesn't support responses with the status {@code 201 CREATED}.      */
DECL|method|RestStatusToXContentListener
specifier|public
name|RestStatusToXContentListener
parameter_list|(
name|RestChannel
name|channel
parameter_list|)
block|{
name|this
argument_list|(
name|channel
argument_list|,
name|r
lambda|->
block|{
assert|assert
literal|false
operator|:
literal|"Returned a 201 CREATED but not set up to support a Location header"
assert|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Build an instance that does support responses with the status {@code 201 CREATED}.      */
DECL|method|RestStatusToXContentListener
specifier|public
name|RestStatusToXContentListener
parameter_list|(
name|RestChannel
name|channel
parameter_list|,
name|Function
argument_list|<
name|Response
argument_list|,
name|String
argument_list|>
name|extractLocation
parameter_list|)
block|{
name|super
argument_list|(
name|channel
argument_list|)
expr_stmt|;
name|this
operator|.
name|extractLocation
operator|=
name|extractLocation
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|buildResponse
specifier|public
name|RestResponse
name|buildResponse
parameter_list|(
name|Response
name|response
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|Exception
block|{
assert|assert
name|response
operator|.
name|isFragment
argument_list|()
operator|==
literal|false
assert|;
comment|//would be nice if we could make default methods final
name|response
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|channel
operator|.
name|request
argument_list|()
argument_list|)
expr_stmt|;
name|RestResponse
name|restResponse
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|response
operator|.
name|status
argument_list|()
argument_list|,
name|builder
argument_list|)
decl_stmt|;
if|if
condition|(
name|RestStatus
operator|.
name|CREATED
operator|==
name|restResponse
operator|.
name|status
argument_list|()
condition|)
block|{
specifier|final
name|String
name|location
init|=
name|extractLocation
operator|.
name|apply
argument_list|(
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|location
operator|!=
literal|null
condition|)
block|{
name|restResponse
operator|.
name|addHeader
argument_list|(
literal|"Location"
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|restResponse
return|;
block|}
block|}
end_class

end_unit

