begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Base implementation for an async action.  */
end_comment

begin_class
DECL|class|AbstractAsyncAction
specifier|abstract
class|class
name|AbstractAsyncAction
block|{
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
decl_stmt|;
DECL|method|AbstractAsyncAction
specifier|protected
name|AbstractAsyncAction
parameter_list|()
block|{
name|this
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AbstractAsyncAction
specifier|protected
name|AbstractAsyncAction
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
comment|/**      * Return the time when the action started.      */
DECL|method|startTime
specifier|protected
specifier|final
name|long
name|startTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
comment|/**      * Builds how long it took to execute the search.      */
DECL|method|buildTookInMillis
specifier|protected
specifier|final
name|long
name|buildTookInMillis
parameter_list|()
block|{
comment|// protect ourselves against time going backwards
comment|// negative values don't make sense and we want to be able to serialize that thing as a vLong
return|return
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
return|;
block|}
DECL|method|start
specifier|abstract
name|void
name|start
parameter_list|()
function_decl|;
block|}
end_class

end_unit

