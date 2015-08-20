begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
name|ObjectObjectAssociativeContainer
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|DelegatingHasContextAndHeaders
specifier|public
class|class
name|DelegatingHasContextAndHeaders
implements|implements
name|HasContextAndHeaders
block|{
DECL|field|delegate
specifier|private
name|HasContextAndHeaders
name|delegate
decl_stmt|;
DECL|method|DelegatingHasContextAndHeaders
specifier|public
name|DelegatingHasContextAndHeaders
parameter_list|(
name|HasContextAndHeaders
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|putHeader
specifier|public
parameter_list|<
name|V
parameter_list|>
name|void
name|putHeader
parameter_list|(
name|String
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
name|delegate
operator|.
name|putHeader
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyContextAndHeadersFrom
specifier|public
name|void
name|copyContextAndHeadersFrom
parameter_list|(
name|HasContextAndHeaders
name|other
parameter_list|)
block|{
name|delegate
operator|.
name|copyContextAndHeadersFrom
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getHeader
specifier|public
parameter_list|<
name|V
parameter_list|>
name|V
name|getHeader
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getHeader
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hasHeader
specifier|public
name|boolean
name|hasHeader
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|hasHeader
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|putInContext
specifier|public
parameter_list|<
name|V
parameter_list|>
name|V
name|putInContext
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|putInContext
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getHeaders
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getHeaders
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getHeaders
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|copyHeadersFrom
specifier|public
name|void
name|copyHeadersFrom
parameter_list|(
name|HasHeaders
name|from
parameter_list|)
block|{
name|delegate
operator|.
name|copyHeadersFrom
argument_list|(
name|from
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|putAllInContext
specifier|public
name|void
name|putAllInContext
parameter_list|(
name|ObjectObjectAssociativeContainer
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|delegate
operator|.
name|putAllInContext
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFromContext
specifier|public
parameter_list|<
name|V
parameter_list|>
name|V
name|getFromContext
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getFromContext
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFromContext
specifier|public
parameter_list|<
name|V
parameter_list|>
name|V
name|getFromContext
parameter_list|(
name|Object
name|key
parameter_list|,
name|V
name|defaultValue
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getFromContext
argument_list|(
name|key
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hasInContext
specifier|public
name|boolean
name|hasInContext
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|hasInContext
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|contextSize
specifier|public
name|int
name|contextSize
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|contextSize
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isContextEmpty
specifier|public
name|boolean
name|isContextEmpty
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isContextEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getContext
specifier|public
name|ImmutableOpenMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|getContext
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getContext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|copyContextFrom
specifier|public
name|void
name|copyContextFrom
parameter_list|(
name|HasContext
name|other
parameter_list|)
block|{
name|delegate
operator|.
name|copyContextFrom
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
