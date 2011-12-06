begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|UnmodifiableIterator
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
name|util
operator|.
name|concurrent
operator|.
name|Immutable
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
comment|/**  * A holder for several {@link FieldMapper}.  *  *  */
end_comment

begin_class
annotation|@
name|Immutable
DECL|class|FieldMappers
specifier|public
class|class
name|FieldMappers
implements|implements
name|Iterable
argument_list|<
name|FieldMapper
argument_list|>
block|{
DECL|field|fieldMappers
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|FieldMapper
argument_list|>
name|fieldMappers
decl_stmt|;
DECL|method|FieldMappers
specifier|public
name|FieldMappers
parameter_list|()
block|{
name|this
operator|.
name|fieldMappers
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
DECL|method|FieldMappers
specifier|public
name|FieldMappers
parameter_list|(
name|FieldMapper
name|fieldMapper
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|FieldMapper
index|[]
block|{
name|fieldMapper
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldMappers
specifier|public
name|FieldMappers
parameter_list|(
name|FieldMapper
index|[]
name|fieldMappers
parameter_list|)
block|{
if|if
condition|(
name|fieldMappers
operator|==
literal|null
condition|)
block|{
name|fieldMappers
operator|=
operator|new
name|FieldMapper
index|[
literal|0
index|]
expr_stmt|;
block|}
name|this
operator|.
name|fieldMappers
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Iterators
operator|.
name|forArray
argument_list|(
name|fieldMappers
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldMappers
specifier|public
name|FieldMappers
parameter_list|(
name|ImmutableList
argument_list|<
name|FieldMapper
argument_list|>
name|fieldMappers
parameter_list|)
block|{
name|this
operator|.
name|fieldMappers
operator|=
name|fieldMappers
expr_stmt|;
block|}
DECL|method|mapper
specifier|public
name|FieldMapper
name|mapper
parameter_list|()
block|{
if|if
condition|(
name|fieldMappers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|fieldMappers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|fieldMappers
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|mappers
specifier|public
name|ImmutableList
argument_list|<
name|FieldMapper
argument_list|>
name|mappers
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldMappers
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|UnmodifiableIterator
argument_list|<
name|FieldMapper
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|fieldMappers
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**      * Concats and returns a new {@link FieldMappers}.      */
DECL|method|concat
specifier|public
name|FieldMappers
name|concat
parameter_list|(
name|FieldMapper
name|mapper
parameter_list|)
block|{
return|return
operator|new
name|FieldMappers
argument_list|(
operator|new
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|FieldMapper
argument_list|>
argument_list|()
operator|.
name|addAll
argument_list|(
name|fieldMappers
argument_list|)
operator|.
name|add
argument_list|(
name|mapper
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Concats and returns a new {@link FieldMappers}.      */
DECL|method|concat
specifier|public
name|FieldMappers
name|concat
parameter_list|(
name|FieldMappers
name|mappers
parameter_list|)
block|{
return|return
operator|new
name|FieldMappers
argument_list|(
operator|new
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|FieldMapper
argument_list|>
argument_list|()
operator|.
name|addAll
argument_list|(
name|fieldMappers
argument_list|)
operator|.
name|addAll
argument_list|(
name|mappers
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|remove
specifier|public
name|FieldMappers
name|remove
parameter_list|(
name|List
argument_list|<
name|FieldMapper
argument_list|>
name|mappers
parameter_list|)
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|FieldMapper
argument_list|>
name|builder
init|=
operator|new
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|FieldMapper
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldMapper
name|fieldMapper
range|:
name|fieldMappers
control|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|FieldMapper
name|mapper
range|:
name|mappers
control|)
block|{
if|if
condition|(
name|fieldMapper
operator|.
name|equals
argument_list|(
name|mapper
argument_list|)
condition|)
block|{
comment|// identify equality
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|fieldMapper
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|FieldMappers
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|remove
specifier|public
name|FieldMappers
name|remove
parameter_list|(
name|FieldMapper
name|mapper
parameter_list|)
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|FieldMapper
argument_list|>
name|builder
init|=
operator|new
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|FieldMapper
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldMapper
name|fieldMapper
range|:
name|fieldMappers
control|)
block|{
if|if
condition|(
operator|!
name|fieldMapper
operator|.
name|equals
argument_list|(
name|mapper
argument_list|)
condition|)
block|{
comment|// identify equality
name|builder
operator|.
name|add
argument_list|(
name|fieldMapper
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|FieldMappers
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

