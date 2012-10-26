begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.id.simple
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|id
operator|.
name|simple
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
name|ImmutableMap
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
name|bytes
operator|.
name|HashedBytesArray
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
name|cache
operator|.
name|id
operator|.
name|IdReaderCache
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
name|cache
operator|.
name|id
operator|.
name|IdReaderTypeCache
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SimpleIdReaderCache
specifier|public
class|class
name|SimpleIdReaderCache
implements|implements
name|IdReaderCache
block|{
DECL|field|readerCacheKey
specifier|private
specifier|final
name|Object
name|readerCacheKey
decl_stmt|;
DECL|field|types
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|SimpleIdReaderTypeCache
argument_list|>
name|types
decl_stmt|;
DECL|method|SimpleIdReaderCache
specifier|public
name|SimpleIdReaderCache
parameter_list|(
name|Object
name|readerCacheKey
parameter_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|SimpleIdReaderTypeCache
argument_list|>
name|types
parameter_list|)
block|{
name|this
operator|.
name|readerCacheKey
operator|=
name|readerCacheKey
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readerCacheKey
specifier|public
name|Object
name|readerCacheKey
parameter_list|()
block|{
return|return
name|this
operator|.
name|readerCacheKey
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|IdReaderTypeCache
name|type
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|types
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parentIdByDoc
specifier|public
name|HashedBytesArray
name|parentIdByDoc
parameter_list|(
name|String
name|type
parameter_list|,
name|int
name|docId
parameter_list|)
block|{
name|SimpleIdReaderTypeCache
name|typeCache
init|=
name|types
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeCache
operator|!=
literal|null
condition|)
block|{
return|return
name|typeCache
operator|.
name|parentIdByDoc
argument_list|(
name|docId
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|docById
specifier|public
name|int
name|docById
parameter_list|(
name|String
name|type
parameter_list|,
name|HashedBytesArray
name|id
parameter_list|)
block|{
name|SimpleIdReaderTypeCache
name|typeCache
init|=
name|types
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeCache
operator|!=
literal|null
condition|)
block|{
return|return
name|typeCache
operator|.
name|docById
argument_list|(
name|id
argument_list|)
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
block|{
name|long
name|sizeInBytes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SimpleIdReaderTypeCache
name|readerTypeCache
range|:
name|types
operator|.
name|values
argument_list|()
control|)
block|{
name|sizeInBytes
operator|+=
name|readerTypeCache
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
return|return
name|sizeInBytes
return|;
block|}
comment|/**      * Returns an already stored instance if exists, if not, returns null;      */
DECL|method|canReuse
specifier|public
name|HashedBytesArray
name|canReuse
parameter_list|(
name|HashedBytesArray
name|id
parameter_list|)
block|{
for|for
control|(
name|SimpleIdReaderTypeCache
name|typeCache
range|:
name|types
operator|.
name|values
argument_list|()
control|)
block|{
name|HashedBytesArray
name|wrap
init|=
name|typeCache
operator|.
name|canReuse
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|wrap
operator|!=
literal|null
condition|)
block|{
return|return
name|wrap
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

