begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.ingest.transport.simulate
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|simulate
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
name|StatusToXContent
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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilderString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|Data
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_class
DECL|class|SimulatedItemResponse
specifier|public
class|class
name|SimulatedItemResponse
implements|implements
name|Streamable
implements|,
name|StatusToXContent
block|{
DECL|field|data
specifier|private
name|Data
name|data
decl_stmt|;
DECL|field|failure
specifier|private
name|Throwable
name|failure
decl_stmt|;
DECL|method|SimulatedItemResponse
specifier|public
name|SimulatedItemResponse
parameter_list|()
block|{      }
DECL|method|SimulatedItemResponse
specifier|public
name|SimulatedItemResponse
parameter_list|(
name|Data
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
DECL|method|SimulatedItemResponse
specifier|public
name|SimulatedItemResponse
parameter_list|(
name|Throwable
name|failure
parameter_list|)
block|{
name|this
operator|.
name|failure
operator|=
name|failure
expr_stmt|;
block|}
DECL|method|failed
specifier|public
name|boolean
name|failed
parameter_list|()
block|{
return|return
name|this
operator|.
name|failure
operator|!=
literal|null
return|;
block|}
DECL|method|getData
specifier|public
name|Data
name|getData
parameter_list|()
block|{
return|return
name|data
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
name|boolean
name|failed
init|=
name|in
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|failed
condition|)
block|{
name|this
operator|.
name|failure
operator|=
name|in
operator|.
name|readThrowable
argument_list|()
expr_stmt|;
comment|// TODO(talevy): check out mget for throwable limitations
block|}
else|else
block|{
name|String
name|index
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|type
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|id
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|doc
init|=
name|in
operator|.
name|readMap
argument_list|()
decl_stmt|;
name|this
operator|.
name|data
operator|=
operator|new
name|Data
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|doc
argument_list|)
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
name|out
operator|.
name|writeBoolean
argument_list|(
name|failed
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|failed
argument_list|()
condition|)
block|{
name|out
operator|.
name|writeThrowable
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeString
argument_list|(
name|data
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|data
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|data
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeMap
argument_list|(
name|data
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ERROR
argument_list|,
name|failed
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|failed
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|FAILURE
argument_list|,
name|failure
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MODIFIED
argument_list|,
name|data
operator|.
name|isModified
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DOCUMENT
argument_list|,
name|data
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SimulatedItemResponse
name|other
init|=
operator|(
name|SimulatedItemResponse
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|data
argument_list|,
name|other
operator|.
name|data
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|failure
argument_list|,
name|other
operator|.
name|failure
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|data
argument_list|,
name|failure
argument_list|)
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|DOCUMENT
specifier|static
specifier|final
name|XContentBuilderString
name|DOCUMENT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
DECL|field|ERROR
specifier|static
specifier|final
name|XContentBuilderString
name|ERROR
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"error"
argument_list|)
decl_stmt|;
DECL|field|FAILURE
specifier|static
specifier|final
name|XContentBuilderString
name|FAILURE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"failure"
argument_list|)
decl_stmt|;
DECL|field|MODIFIED
specifier|static
specifier|final
name|XContentBuilderString
name|MODIFIED
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"modified"
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

