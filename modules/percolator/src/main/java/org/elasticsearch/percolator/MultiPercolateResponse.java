begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|percolator
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|Nullable
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|ToXContent
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
name|Arrays
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
comment|/**  * Represents the response of a multi percolate request.  *  * Each item represents the response of a percolator request and the order of the items is in the same order as the  * percolator requests were defined in the multi percolate request.  *  * @deprecated Instead use multi search API with {@link PercolateQueryBuilder}  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|MultiPercolateResponse
specifier|public
class|class
name|MultiPercolateResponse
extends|extends
name|ActionResponse
implements|implements
name|Iterable
argument_list|<
name|MultiPercolateResponse
operator|.
name|Item
argument_list|>
implements|,
name|ToXContent
block|{
DECL|field|items
specifier|private
name|Item
index|[]
name|items
decl_stmt|;
DECL|method|MultiPercolateResponse
name|MultiPercolateResponse
parameter_list|(
name|Item
index|[]
name|items
parameter_list|)
block|{
name|this
operator|.
name|items
operator|=
name|items
expr_stmt|;
block|}
DECL|method|MultiPercolateResponse
name|MultiPercolateResponse
parameter_list|()
block|{
name|this
operator|.
name|items
operator|=
operator|new
name|Item
index|[
literal|0
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Item
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|items
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**      * Same as {@link #getItems()}      */
DECL|method|items
specifier|public
name|Item
index|[]
name|items
parameter_list|()
block|{
return|return
name|items
return|;
block|}
comment|/**      * @return the percolate responses as items.      */
DECL|method|getItems
specifier|public
name|Item
index|[]
name|getItems
parameter_list|()
block|{
return|return
name|items
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|RESPONSES
argument_list|)
expr_stmt|;
for|for
control|(
name|MultiPercolateResponse
operator|.
name|Item
name|item
range|:
name|items
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|item
operator|.
name|isFailure
argument_list|()
condition|)
block|{
name|ElasticsearchException
operator|.
name|renderException
argument_list|(
name|builder
argument_list|,
name|params
argument_list|,
name|item
operator|.
name|getFailure
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|item
operator|.
name|getResponse
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
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
name|writeVInt
argument_list|(
name|items
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Item
name|item
range|:
name|items
control|)
block|{
name|item
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
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
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|items
operator|=
operator|new
name|Item
index|[
name|size
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
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|items
index|[
name|i
index|]
operator|=
operator|new
name|Item
argument_list|()
expr_stmt|;
name|items
index|[
name|i
index|]
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Encapsulates a single percolator response which may contain an error or the actual percolator response itself.      */
DECL|class|Item
specifier|public
specifier|static
class|class
name|Item
implements|implements
name|Streamable
block|{
DECL|field|response
specifier|private
name|PercolateResponse
name|response
decl_stmt|;
DECL|field|exception
specifier|private
name|Exception
name|exception
decl_stmt|;
DECL|method|Item
name|Item
parameter_list|(
name|PercolateResponse
name|response
parameter_list|)
block|{
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
block|}
DECL|method|Item
name|Item
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
name|this
operator|.
name|exception
operator|=
name|exception
expr_stmt|;
block|}
DECL|method|Item
name|Item
parameter_list|()
block|{         }
comment|/**          * @return The percolator response or<code>null</code> if there was error.          */
annotation|@
name|Nullable
DECL|method|getResponse
specifier|public
name|PercolateResponse
name|getResponse
parameter_list|()
block|{
return|return
name|response
return|;
block|}
comment|/**          * @return An error description if there was an error or<code>null</code> if the percolate request was successful          */
annotation|@
name|Nullable
DECL|method|getErrorMessage
specifier|public
name|String
name|getErrorMessage
parameter_list|()
block|{
return|return
name|exception
operator|==
literal|null
condition|?
literal|null
else|:
name|exception
operator|.
name|getMessage
argument_list|()
return|;
block|}
comment|/**          * @return<code>true</code> if the percolator request that this item represents failed otherwise          *<code>false</code> is returned.          */
DECL|method|isFailure
specifier|public
name|boolean
name|isFailure
parameter_list|()
block|{
return|return
name|exception
operator|!=
literal|null
return|;
block|}
DECL|method|getFailure
specifier|public
name|Exception
name|getFailure
parameter_list|()
block|{
return|return
name|exception
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
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|response
operator|=
operator|new
name|PercolateResponse
argument_list|()
expr_stmt|;
name|response
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|exception
operator|=
name|in
operator|.
name|readException
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|response
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeThrowable
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|RESPONSES
specifier|static
specifier|final
name|String
name|RESPONSES
init|=
literal|"responses"
decl_stmt|;
DECL|field|ERROR
specifier|static
specifier|final
name|String
name|ERROR
init|=
literal|"error"
decl_stmt|;
block|}
block|}
end_class

end_unit

