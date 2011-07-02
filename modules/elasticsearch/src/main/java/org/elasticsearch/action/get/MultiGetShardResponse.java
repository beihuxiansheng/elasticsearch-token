begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.get
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|get
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|trove
operator|.
name|list
operator|.
name|array
operator|.
name|TIntArrayList
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|MultiGetShardResponse
specifier|public
class|class
name|MultiGetShardResponse
implements|implements
name|ActionResponse
block|{
DECL|field|locations
name|TIntArrayList
name|locations
decl_stmt|;
DECL|field|responses
name|List
argument_list|<
name|GetResponse
argument_list|>
name|responses
decl_stmt|;
DECL|field|failures
name|List
argument_list|<
name|MultiGetResponse
operator|.
name|Failure
argument_list|>
name|failures
decl_stmt|;
DECL|method|MultiGetShardResponse
name|MultiGetShardResponse
parameter_list|()
block|{
name|locations
operator|=
operator|new
name|TIntArrayList
argument_list|()
expr_stmt|;
name|responses
operator|=
operator|new
name|ArrayList
argument_list|<
name|GetResponse
argument_list|>
argument_list|()
expr_stmt|;
name|failures
operator|=
operator|new
name|ArrayList
argument_list|<
name|MultiGetResponse
operator|.
name|Failure
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|location
parameter_list|,
name|GetResponse
name|response
parameter_list|)
block|{
name|locations
operator|.
name|add
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|responses
operator|.
name|add
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|failures
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|location
parameter_list|,
name|MultiGetResponse
operator|.
name|Failure
name|failure
parameter_list|)
block|{
name|locations
operator|.
name|add
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|responses
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|failures
operator|.
name|add
argument_list|(
name|failure
argument_list|)
expr_stmt|;
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
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|locations
operator|=
operator|new
name|TIntArrayList
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|responses
operator|=
operator|new
name|ArrayList
argument_list|<
name|GetResponse
argument_list|>
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|failures
operator|=
operator|new
name|ArrayList
argument_list|<
name|MultiGetResponse
operator|.
name|Failure
argument_list|>
argument_list|(
name|size
argument_list|)
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|locations
operator|.
name|add
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|GetResponse
name|response
init|=
operator|new
name|GetResponse
argument_list|()
decl_stmt|;
name|response
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|responses
operator|.
name|add
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|responses
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|failures
operator|.
name|add
argument_list|(
name|MultiGetResponse
operator|.
name|Failure
operator|.
name|readFailure
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|failures
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
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
name|locations
operator|.
name|size
argument_list|()
argument_list|)
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
name|locations
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|locations
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|responses
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|responses
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|failures
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|failures
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

