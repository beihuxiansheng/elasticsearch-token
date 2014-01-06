begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.analyze
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|analyze
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
name|Iterator
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AnalyzeResponse
specifier|public
class|class
name|AnalyzeResponse
extends|extends
name|ActionResponse
implements|implements
name|Iterable
argument_list|<
name|AnalyzeResponse
operator|.
name|AnalyzeToken
argument_list|>
implements|,
name|ToXContent
block|{
DECL|class|AnalyzeToken
specifier|public
specifier|static
class|class
name|AnalyzeToken
implements|implements
name|Streamable
block|{
DECL|field|term
specifier|private
name|String
name|term
decl_stmt|;
DECL|field|startOffset
specifier|private
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
specifier|private
name|int
name|endOffset
decl_stmt|;
DECL|field|position
specifier|private
name|int
name|position
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|method|AnalyzeToken
name|AnalyzeToken
parameter_list|()
block|{         }
DECL|method|AnalyzeToken
specifier|public
name|AnalyzeToken
parameter_list|(
name|String
name|term
parameter_list|,
name|int
name|position
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|getTerm
specifier|public
name|String
name|getTerm
parameter_list|()
block|{
return|return
name|this
operator|.
name|term
return|;
block|}
DECL|method|getStartOffset
specifier|public
name|int
name|getStartOffset
parameter_list|()
block|{
return|return
name|this
operator|.
name|startOffset
return|;
block|}
DECL|method|getEndOffset
specifier|public
name|int
name|getEndOffset
parameter_list|()
block|{
return|return
name|this
operator|.
name|endOffset
return|;
block|}
DECL|method|getPosition
specifier|public
name|int
name|getPosition
parameter_list|()
block|{
return|return
name|this
operator|.
name|position
return|;
block|}
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
DECL|method|readAnalyzeToken
specifier|public
specifier|static
name|AnalyzeToken
name|readAnalyzeToken
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|AnalyzeToken
name|analyzeToken
init|=
operator|new
name|AnalyzeToken
argument_list|()
decl_stmt|;
name|analyzeToken
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|analyzeToken
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
name|term
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|startOffset
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|endOffset
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|position
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|type
operator|=
name|in
operator|.
name|readOptionalString
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
name|out
operator|.
name|writeString
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|startOffset
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|endOffset
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|tokens
specifier|private
name|List
argument_list|<
name|AnalyzeToken
argument_list|>
name|tokens
decl_stmt|;
DECL|method|AnalyzeResponse
name|AnalyzeResponse
parameter_list|()
block|{     }
DECL|method|AnalyzeResponse
specifier|public
name|AnalyzeResponse
parameter_list|(
name|List
argument_list|<
name|AnalyzeToken
argument_list|>
name|tokens
parameter_list|)
block|{
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
block|}
DECL|method|getTokens
specifier|public
name|List
argument_list|<
name|AnalyzeToken
argument_list|>
name|getTokens
parameter_list|()
block|{
return|return
name|this
operator|.
name|tokens
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|AnalyzeToken
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|tokens
operator|.
name|iterator
argument_list|()
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
name|TOKENS
argument_list|)
expr_stmt|;
for|for
control|(
name|AnalyzeToken
name|token
range|:
name|tokens
control|)
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
name|TOKEN
argument_list|,
name|token
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|START_OFFSET
argument_list|,
name|token
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|END_OFFSET
argument_list|,
name|token
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TYPE
argument_list|,
name|token
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|POSITION
argument_list|,
name|token
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
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
name|tokens
operator|=
operator|new
name|ArrayList
argument_list|<
name|AnalyzeToken
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
name|tokens
operator|.
name|add
argument_list|(
name|AnalyzeToken
operator|.
name|readAnalyzeToken
argument_list|(
name|in
argument_list|)
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
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AnalyzeToken
name|token
range|:
name|tokens
control|)
block|{
name|token
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|TOKENS
specifier|static
specifier|final
name|XContentBuilderString
name|TOKENS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"tokens"
argument_list|)
decl_stmt|;
DECL|field|TOKEN
specifier|static
specifier|final
name|XContentBuilderString
name|TOKEN
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"token"
argument_list|)
decl_stmt|;
DECL|field|START_OFFSET
specifier|static
specifier|final
name|XContentBuilderString
name|START_OFFSET
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"start_offset"
argument_list|)
decl_stmt|;
DECL|field|END_OFFSET
specifier|static
specifier|final
name|XContentBuilderString
name|END_OFFSET
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"end_offset"
argument_list|)
decl_stmt|;
DECL|field|TYPE
specifier|static
specifier|final
name|XContentBuilderString
name|TYPE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
DECL|field|POSITION
specifier|static
specifier|final
name|XContentBuilderString
name|POSITION
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"position"
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

