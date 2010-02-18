begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this   * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|search
operator|.
name|Scroll
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|builder
operator|.
name|SearchSourceBuilder
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
name|Required
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
name|Strings
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
name|TimeValue
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
name|gnu
operator|.
name|trove
operator|.
name|TObjectFloatHashMap
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
name|gnu
operator|.
name|trove
operator|.
name|TObjectFloatIterator
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|Actions
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|Scroll
operator|.
name|*
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
name|TimeValue
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|SearchRequest
specifier|public
class|class
name|SearchRequest
implements|implements
name|ActionRequest
block|{
DECL|field|EMPTY
specifier|private
specifier|static
name|TObjectFloatHashMap
argument_list|<
name|String
argument_list|>
name|EMPTY
init|=
operator|new
name|TObjectFloatHashMap
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|searchType
specifier|private
name|SearchType
name|searchType
init|=
name|SearchType
operator|.
name|QUERY_THEN_FETCH
decl_stmt|;
DECL|field|indices
specifier|private
name|String
index|[]
name|indices
decl_stmt|;
DECL|field|queryHint
specifier|private
name|String
name|queryHint
decl_stmt|;
DECL|field|source
specifier|private
name|String
name|source
decl_stmt|;
DECL|field|scroll
specifier|private
name|Scroll
name|scroll
decl_stmt|;
DECL|field|from
specifier|private
name|int
name|from
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|types
specifier|private
name|String
index|[]
name|types
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|indexBoost
specifier|private
name|TObjectFloatHashMap
argument_list|<
name|String
argument_list|>
name|indexBoost
init|=
name|EMPTY
decl_stmt|;
DECL|field|timeout
specifier|private
name|TimeValue
name|timeout
decl_stmt|;
DECL|field|listenerThreaded
specifier|private
name|boolean
name|listenerThreaded
init|=
literal|false
decl_stmt|;
DECL|field|operationThreading
specifier|private
name|SearchOperationThreading
name|operationThreading
init|=
name|SearchOperationThreading
operator|.
name|SINGLE_THREAD
decl_stmt|;
DECL|method|SearchRequest
name|SearchRequest
parameter_list|()
block|{     }
DECL|method|SearchRequest
specifier|public
name|SearchRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
block|}
DECL|method|SearchRequest
specifier|public
name|SearchRequest
parameter_list|(
name|String
name|index
parameter_list|,
name|SearchSourceBuilder
name|source
parameter_list|)
block|{
name|this
argument_list|(
name|index
argument_list|,
name|source
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|SearchRequest
specifier|public
name|SearchRequest
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|source
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|String
index|[]
block|{
name|index
block|}
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
DECL|method|SearchRequest
specifier|public
name|SearchRequest
parameter_list|(
name|String
index|[]
name|indices
parameter_list|,
name|SearchSourceBuilder
name|source
parameter_list|)
block|{
name|this
argument_list|(
name|indices
argument_list|,
name|source
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|SearchRequest
specifier|public
name|SearchRequest
parameter_list|(
name|String
index|[]
name|indices
parameter_list|,
name|String
name|source
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
DECL|method|validate
annotation|@
name|Override
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"search source is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
DECL|method|listenerThreaded
annotation|@
name|Override
specifier|public
name|boolean
name|listenerThreaded
parameter_list|()
block|{
return|return
name|listenerThreaded
return|;
block|}
DECL|method|listenerThreaded
annotation|@
name|Override
specifier|public
name|SearchRequest
name|listenerThreaded
parameter_list|(
name|boolean
name|listenerThreaded
parameter_list|)
block|{
name|this
operator|.
name|listenerThreaded
operator|=
name|listenerThreaded
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|operationThreading
specifier|public
name|SearchOperationThreading
name|operationThreading
parameter_list|()
block|{
return|return
name|this
operator|.
name|operationThreading
return|;
block|}
DECL|method|operationThreading
specifier|public
name|SearchRequest
name|operationThreading
parameter_list|(
name|SearchOperationThreading
name|operationThreading
parameter_list|)
block|{
name|this
operator|.
name|operationThreading
operator|=
name|operationThreading
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|searchType
specifier|public
name|SearchRequest
name|searchType
parameter_list|(
name|SearchType
name|searchType
parameter_list|)
block|{
name|this
operator|.
name|searchType
operator|=
name|searchType
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|source
annotation|@
name|Required
specifier|public
name|SearchRequest
name|source
parameter_list|(
name|SearchSourceBuilder
name|sourceBuilder
parameter_list|)
block|{
return|return
name|source
argument_list|(
name|sourceBuilder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|source
annotation|@
name|Required
specifier|public
name|SearchRequest
name|source
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|searchType
specifier|public
name|SearchType
name|searchType
parameter_list|()
block|{
return|return
name|searchType
return|;
block|}
DECL|method|indices
specifier|public
name|String
index|[]
name|indices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
DECL|method|queryHint
specifier|public
name|SearchRequest
name|queryHint
parameter_list|(
name|String
name|queryHint
parameter_list|)
block|{
name|this
operator|.
name|queryHint
operator|=
name|queryHint
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|queryHint
specifier|public
name|String
name|queryHint
parameter_list|()
block|{
return|return
name|queryHint
return|;
block|}
DECL|method|source
specifier|public
name|String
name|source
parameter_list|()
block|{
return|return
name|source
return|;
block|}
DECL|method|scroll
specifier|public
name|Scroll
name|scroll
parameter_list|()
block|{
return|return
name|scroll
return|;
block|}
DECL|method|scroll
specifier|public
name|SearchRequest
name|scroll
parameter_list|(
name|Scroll
name|scroll
parameter_list|)
block|{
name|this
operator|.
name|scroll
operator|=
name|scroll
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|from
specifier|public
name|int
name|from
parameter_list|()
block|{
return|return
name|from
return|;
block|}
DECL|method|from
specifier|public
name|SearchRequest
name|from
parameter_list|(
name|int
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|types
return|;
block|}
DECL|method|types
specifier|public
name|SearchRequest
name|types
parameter_list|(
name|String
modifier|...
name|types
parameter_list|)
block|{
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|timeout
specifier|public
name|TimeValue
name|timeout
parameter_list|()
block|{
return|return
name|timeout
return|;
block|}
DECL|method|timeout
specifier|public
name|SearchRequest
name|timeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Allows to set a dynamic query boost on an index level query. Very handy when, for example, each user has      * his own index, and friends matter more than friends of friends.      */
DECL|method|indexBoost
specifier|public
name|TObjectFloatHashMap
argument_list|<
name|String
argument_list|>
name|indexBoost
parameter_list|()
block|{
return|return
name|indexBoost
return|;
block|}
DECL|method|indexBoost
specifier|public
name|SearchRequest
name|indexBoost
parameter_list|(
name|String
name|index
parameter_list|,
name|float
name|indexBoost
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|indexBoost
operator|==
name|EMPTY
condition|)
block|{
name|this
operator|.
name|indexBoost
operator|=
operator|new
name|TObjectFloatHashMap
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|indexBoost
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|indexBoost
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|size
specifier|public
name|SearchRequest
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
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
block|{
name|operationThreading
operator|=
name|SearchOperationThreading
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|searchType
operator|=
name|SearchType
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|indices
operator|=
operator|new
name|String
index|[
name|in
operator|.
name|readInt
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
name|indices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indices
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readUTF
argument_list|()
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
name|queryHint
operator|=
name|in
operator|.
name|readUTF
argument_list|()
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
name|scroll
operator|=
name|readScroll
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|from
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|size
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|timeout
operator|=
name|readTimeValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|source
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
name|indexBoost
operator|=
name|EMPTY
expr_stmt|;
block|}
else|else
block|{
name|indexBoost
operator|=
operator|new
name|TObjectFloatHashMap
argument_list|<
name|String
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
name|indexBoost
operator|.
name|put
argument_list|(
name|in
operator|.
name|readUTF
argument_list|()
argument_list|,
name|in
operator|.
name|readFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|typesSize
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|typesSize
operator|>
literal|0
condition|)
block|{
name|types
operator|=
operator|new
name|String
index|[
name|typesSize
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
name|typesSize
condition|;
name|i
operator|++
control|)
block|{
name|types
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readUTF
argument_list|()
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
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|operationThreading
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|searchType
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|indices
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryHint
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
name|out
operator|.
name|writeUTF
argument_list|(
name|queryHint
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scroll
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
name|scroll
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeInt
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|timeout
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
name|timeout
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeUTF
argument_list|(
name|source
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexBoost
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|indexBoost
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|TObjectFloatIterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|indexBoost
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|it
operator|.
name|advance
argument_list|()
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|it
operator|.
name|key
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|it
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|writeInt
argument_list|(
name|types
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|type
range|:
name|types
control|)
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

