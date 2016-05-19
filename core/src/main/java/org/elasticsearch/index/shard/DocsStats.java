begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|DocsStats
specifier|public
class|class
name|DocsStats
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|count
name|long
name|count
init|=
literal|0
decl_stmt|;
DECL|field|deleted
name|long
name|deleted
init|=
literal|0
decl_stmt|;
DECL|method|DocsStats
specifier|public
name|DocsStats
parameter_list|()
block|{      }
DECL|method|DocsStats
specifier|public
name|DocsStats
parameter_list|(
name|long
name|count
parameter_list|,
name|long
name|deleted
parameter_list|)
block|{
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|deleted
operator|=
name|deleted
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|DocsStats
name|docsStats
parameter_list|)
block|{
if|if
condition|(
name|docsStats
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|count
operator|+=
name|docsStats
operator|.
name|count
expr_stmt|;
name|deleted
operator|+=
name|docsStats
operator|.
name|deleted
expr_stmt|;
block|}
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|count
return|;
block|}
DECL|method|getDeleted
specifier|public
name|long
name|getDeleted
parameter_list|()
block|{
return|return
name|this
operator|.
name|deleted
return|;
block|}
DECL|method|readDocStats
specifier|public
specifier|static
name|DocsStats
name|readDocStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|DocsStats
name|docsStats
init|=
operator|new
name|DocsStats
argument_list|()
decl_stmt|;
name|docsStats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|docsStats
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
name|count
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|deleted
operator|=
name|in
operator|.
name|readVLong
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
name|writeVLong
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|deleted
argument_list|)
expr_stmt|;
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
argument_list|(
name|Fields
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|COUNT
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DELETED
argument_list|,
name|deleted
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|DOCS
specifier|static
specifier|final
name|String
name|DOCS
init|=
literal|"docs"
decl_stmt|;
DECL|field|COUNT
specifier|static
specifier|final
name|String
name|COUNT
init|=
literal|"count"
decl_stmt|;
DECL|field|DELETED
specifier|static
specifier|final
name|String
name|DELETED
init|=
literal|"deleted"
decl_stmt|;
block|}
block|}
end_class

end_unit

