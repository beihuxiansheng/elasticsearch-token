begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
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
name|ActionRequestValidationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|tasks
operator|.
name|BaseTasksRequest
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ValidateActions
operator|.
name|addValidationError
import|;
end_import

begin_comment
comment|/**  * A request to change throttling on a task.  */
end_comment

begin_class
DECL|class|RethrottleRequest
specifier|public
class|class
name|RethrottleRequest
extends|extends
name|BaseTasksRequest
argument_list|<
name|RethrottleRequest
argument_list|>
block|{
comment|/**      * The throttle to apply to all matching requests in sub-requests per second. 0 means set no throttle. Throttling is done between      * batches, as we start the next scroll requests. That way we can increase the scroll's timeout to make sure that it contains any time      * that we might wait.      */
DECL|field|requestsPerSecond
specifier|private
name|Float
name|requestsPerSecond
decl_stmt|;
comment|/**      * The throttle to apply to all matching requests in sub-requests per second. 0 means set no throttle and that is the default.      */
DECL|method|getRequestsPerSecond
specifier|public
name|float
name|getRequestsPerSecond
parameter_list|()
block|{
return|return
name|requestsPerSecond
return|;
block|}
comment|/**      * Set the throttle to apply to all matching requests in sub-requests per second. {@link Float#POSITIVE_INFINITY} means set no throttle.      * Throttling is done between batches, as we start the next scroll requests. That way we can increase the scroll's timeout to make sure      * that it contains any time that we might wait.      */
DECL|method|setRequestsPerSecond
specifier|public
name|RethrottleRequest
name|setRequestsPerSecond
parameter_list|(
name|float
name|requestsPerSecond
parameter_list|)
block|{
if|if
condition|(
name|requestsPerSecond
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[requests_per_second] must be greater than 0. Use Float.POSITIVE_INFINITY to disable throttling."
argument_list|)
throw|;
block|}
name|this
operator|.
name|requestsPerSecond
operator|=
name|requestsPerSecond
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
name|super
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|requestsPerSecond
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"requests_per_second must be set"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|action
range|:
name|getActions
argument_list|()
control|)
block|{
switch|switch
condition|(
name|action
condition|)
block|{
case|case
name|ReindexAction
operator|.
name|NAME
case|:
case|case
name|UpdateByQueryAction
operator|.
name|NAME
case|:
continue|continue;
default|default:
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"Can only change the throttling on reindex or update-by-query. Not on ["
operator|+
name|action
operator|+
literal|"]"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|validationException
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|requestsPerSecond
operator|=
name|in
operator|.
name|readFloat
argument_list|()
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
name|writeFloat
argument_list|(
name|requestsPerSecond
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

