begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.memcached
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|memcached
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
name|Unicode
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
name|ImmutableSet
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
name|support
operator|.
name|AbstractRestRequest
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
name|support
operator|.
name|RestUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Set
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|MemcachedRestRequest
specifier|public
class|class
name|MemcachedRestRequest
extends|extends
name|AbstractRestRequest
block|{
DECL|field|method
specifier|private
specifier|final
name|Method
name|method
decl_stmt|;
DECL|field|uri
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
DECL|field|uriBytes
specifier|private
specifier|final
name|byte
index|[]
name|uriBytes
decl_stmt|;
DECL|field|dataSize
specifier|private
specifier|final
name|int
name|dataSize
decl_stmt|;
DECL|field|binary
specifier|private
name|boolean
name|binary
decl_stmt|;
DECL|field|params
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|data
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
DECL|field|opaque
specifier|private
name|int
name|opaque
decl_stmt|;
DECL|field|quiet
specifier|private
name|boolean
name|quiet
decl_stmt|;
DECL|method|MemcachedRestRequest
specifier|public
name|MemcachedRestRequest
parameter_list|(
name|Method
name|method
parameter_list|,
name|String
name|uri
parameter_list|,
name|byte
index|[]
name|uriBytes
parameter_list|,
name|int
name|dataSize
parameter_list|,
name|boolean
name|binary
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|uriBytes
operator|=
name|uriBytes
expr_stmt|;
name|this
operator|.
name|dataSize
operator|=
name|dataSize
expr_stmt|;
name|this
operator|.
name|binary
operator|=
name|binary
expr_stmt|;
name|this
operator|.
name|params
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|int
name|pathEndPos
init|=
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathEndPos
operator|<
literal|0
condition|)
block|{
name|this
operator|.
name|path
operator|=
name|uri
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|path
operator|=
name|uri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pathEndPos
argument_list|)
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|pathEndPos
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|method
annotation|@
name|Override
specifier|public
name|Method
name|method
parameter_list|()
block|{
return|return
name|this
operator|.
name|method
return|;
block|}
DECL|method|uri
annotation|@
name|Override
specifier|public
name|String
name|uri
parameter_list|()
block|{
return|return
name|this
operator|.
name|uri
return|;
block|}
DECL|method|path
annotation|@
name|Override
specifier|public
name|String
name|path
parameter_list|()
block|{
return|return
name|this
operator|.
name|path
return|;
block|}
DECL|method|getUriBytes
specifier|public
name|byte
index|[]
name|getUriBytes
parameter_list|()
block|{
return|return
name|uriBytes
return|;
block|}
DECL|method|isBinary
specifier|public
name|boolean
name|isBinary
parameter_list|()
block|{
return|return
name|binary
return|;
block|}
DECL|method|getOpaque
specifier|public
name|int
name|getOpaque
parameter_list|()
block|{
return|return
name|opaque
return|;
block|}
DECL|method|setOpaque
specifier|public
name|void
name|setOpaque
parameter_list|(
name|int
name|opaque
parameter_list|)
block|{
name|this
operator|.
name|opaque
operator|=
name|opaque
expr_stmt|;
block|}
DECL|method|isQuiet
specifier|public
name|boolean
name|isQuiet
parameter_list|()
block|{
return|return
name|quiet
return|;
block|}
DECL|method|setQuiet
specifier|public
name|void
name|setQuiet
parameter_list|(
name|boolean
name|quiet
parameter_list|)
block|{
name|this
operator|.
name|quiet
operator|=
name|quiet
expr_stmt|;
block|}
DECL|method|getDataSize
specifier|public
name|int
name|getDataSize
parameter_list|()
block|{
return|return
name|dataSize
return|;
block|}
DECL|method|setData
specifier|public
name|void
name|setData
parameter_list|(
name|byte
index|[]
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
DECL|method|hasContent
annotation|@
name|Override
specifier|public
name|boolean
name|hasContent
parameter_list|()
block|{
return|return
name|data
operator|!=
literal|null
return|;
block|}
DECL|method|contentUnsafe
annotation|@
name|Override
specifier|public
name|boolean
name|contentUnsafe
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|contentByteArray
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|contentByteArray
parameter_list|()
block|{
return|return
name|data
return|;
block|}
DECL|method|contentByteArrayOffset
annotation|@
name|Override
specifier|public
name|int
name|contentByteArrayOffset
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|contentLength
annotation|@
name|Override
specifier|public
name|int
name|contentLength
parameter_list|()
block|{
return|return
name|dataSize
return|;
block|}
DECL|method|contentAsString
annotation|@
name|Override
specifier|public
name|String
name|contentAsString
parameter_list|()
block|{
return|return
name|Unicode
operator|.
name|fromBytes
argument_list|(
name|data
argument_list|)
return|;
block|}
DECL|method|headerNames
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|headerNames
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
DECL|method|header
annotation|@
name|Override
specifier|public
name|String
name|header
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|cookie
annotation|@
name|Override
specifier|public
name|String
name|cookie
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|hasParam
annotation|@
name|Override
specifier|public
name|boolean
name|hasParam
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|params
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|param
annotation|@
name|Override
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|params
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|()
block|{
return|return
name|params
return|;
block|}
DECL|method|param
annotation|@
name|Override
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|value
init|=
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

