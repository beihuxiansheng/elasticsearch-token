begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.bulk
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
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
name|ActionResponse
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
name|collect
operator|.
name|Iterators
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
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * A response of a bulk execution. Holding a response for each item responding (in order) of the  * bulk requests. Each item holds the index/type/id is operated on, and if it failed or not (with the  * failure message).  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|BulkResponse
specifier|public
class|class
name|BulkResponse
implements|implements
name|ActionResponse
implements|,
name|Iterable
argument_list|<
name|BulkItemResponse
argument_list|>
block|{
DECL|field|responses
specifier|private
name|BulkItemResponse
index|[]
name|responses
decl_stmt|;
DECL|method|BulkResponse
name|BulkResponse
parameter_list|()
block|{     }
DECL|method|BulkResponse
specifier|public
name|BulkResponse
parameter_list|(
name|BulkItemResponse
index|[]
name|responses
parameter_list|)
block|{
name|this
operator|.
name|responses
operator|=
name|responses
expr_stmt|;
block|}
comment|/**      * Has anything failed with the execution.      */
DECL|method|hasFailures
specifier|public
name|boolean
name|hasFailures
parameter_list|()
block|{
for|for
control|(
name|BulkItemResponse
name|response
range|:
name|responses
control|)
block|{
if|if
condition|(
name|response
operator|.
name|failed
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * The items representing each action performed in the bulk operation (in the same order!).      */
DECL|method|items
specifier|public
name|BulkItemResponse
index|[]
name|items
parameter_list|()
block|{
return|return
name|responses
return|;
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|BulkItemResponse
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|forArray
argument_list|(
name|responses
argument_list|)
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
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
name|responses
operator|=
operator|new
name|BulkItemResponse
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|responses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|responses
index|[
name|i
index|]
operator|=
name|BulkItemResponse
operator|.
name|readBulkItem
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeTo
annotation|@
name|Override
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
name|out
operator|.
name|writeVInt
argument_list|(
name|responses
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|BulkItemResponse
name|response
range|:
name|responses
control|)
block|{
name|response
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

