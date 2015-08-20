begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
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
name|bytes
operator|.
name|BytesReference
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
name|XContentFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilder
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

begin_class
DECL|class|QuerySourceBuilder
specifier|public
class|class
name|QuerySourceBuilder
extends|extends
name|ToXContentToBytes
block|{
DECL|field|queryBuilder
specifier|private
name|QueryBuilder
name|queryBuilder
decl_stmt|;
DECL|field|queryBinary
specifier|private
name|BytesReference
name|queryBinary
decl_stmt|;
DECL|method|setQuery
specifier|public
name|QuerySourceBuilder
name|setQuery
parameter_list|(
name|QueryBuilder
name|query
parameter_list|)
block|{
name|this
operator|.
name|queryBuilder
operator|=
name|query
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setQuery
specifier|public
name|QuerySourceBuilder
name|setQuery
parameter_list|(
name|BytesReference
name|queryBinary
parameter_list|)
block|{
name|this
operator|.
name|queryBinary
operator|=
name|queryBinary
expr_stmt|;
return|return
name|this
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
name|startObject
argument_list|()
expr_stmt|;
name|innerToXContent
argument_list|(
name|builder
argument_list|,
name|params
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
DECL|method|innerToXContent
specifier|public
name|void
name|innerToXContent
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
if|if
condition|(
name|queryBuilder
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"query"
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryBinary
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|queryBinary
argument_list|)
operator|==
name|builder
operator|.
name|contentType
argument_list|()
condition|)
block|{
name|builder
operator|.
name|rawField
argument_list|(
literal|"query"
argument_list|,
name|queryBinary
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"query_binary"
argument_list|,
name|queryBinary
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
