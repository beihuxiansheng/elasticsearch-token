begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.alias.get
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
name|alias
operator|.
name|get
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectObjectCursor
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
name|cluster
operator|.
name|metadata
operator|.
name|AliasMetaData
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
name|ImmutableOpenMap
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  */
end_comment

begin_class
DECL|class|GetAliasesResponse
specifier|public
class|class
name|GetAliasesResponse
extends|extends
name|ActionResponse
block|{
DECL|field|aliases
specifier|private
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|aliases
init|=
name|ImmutableOpenMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|method|GetAliasesResponse
specifier|public
name|GetAliasesResponse
parameter_list|(
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|aliases
parameter_list|)
block|{
name|this
operator|.
name|aliases
operator|=
name|aliases
expr_stmt|;
block|}
DECL|method|GetAliasesResponse
name|GetAliasesResponse
parameter_list|()
block|{     }
DECL|method|getAliases
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|getAliases
parameter_list|()
block|{
return|return
name|aliases
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
name|ImmutableOpenMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|aliasesBuilder
init|=
name|ImmutableOpenMap
operator|.
name|builder
argument_list|()
decl_stmt|;
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
name|String
name|key
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|int
name|valueSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AliasMetaData
argument_list|>
name|value
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|valueSize
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|valueSize
condition|;
name|j
operator|++
control|)
block|{
name|value
operator|.
name|add
argument_list|(
name|AliasMetaData
operator|.
name|Builder
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|aliasesBuilder
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|aliases
operator|=
name|aliasesBuilder
operator|.
name|build
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
name|writeVInt
argument_list|(
name|aliases
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|entry
range|:
name|aliases
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|key
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|entry
operator|.
name|value
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AliasMetaData
name|aliasMetaData
range|:
name|entry
operator|.
name|value
control|)
block|{
name|aliasMetaData
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
