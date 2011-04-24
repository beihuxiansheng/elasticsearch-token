begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.io.stream
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
package|;
end_package

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
name|lang
operator|.
name|ref
operator|.
name|SoftReference
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|CachedStreamInput
specifier|public
class|class
name|CachedStreamInput
block|{
DECL|class|Entry
specifier|static
class|class
name|Entry
block|{
DECL|field|handles
specifier|final
name|HandlesStreamInput
name|handles
decl_stmt|;
DECL|field|lzf
specifier|final
name|LZFStreamInput
name|lzf
decl_stmt|;
DECL|method|Entry
name|Entry
parameter_list|(
name|HandlesStreamInput
name|handles
parameter_list|,
name|LZFStreamInput
name|lzf
parameter_list|)
block|{
name|this
operator|.
name|handles
operator|=
name|handles
expr_stmt|;
name|this
operator|.
name|lzf
operator|=
name|lzf
expr_stmt|;
block|}
block|}
DECL|field|cache
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|SoftReference
argument_list|<
name|Entry
argument_list|>
argument_list|>
name|cache
init|=
operator|new
name|ThreadLocal
argument_list|<
name|SoftReference
argument_list|<
name|Entry
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|instance
specifier|static
name|Entry
name|instance
parameter_list|()
block|{
name|SoftReference
argument_list|<
name|Entry
argument_list|>
name|ref
init|=
name|cache
operator|.
name|get
argument_list|()
decl_stmt|;
name|Entry
name|entry
init|=
name|ref
operator|==
literal|null
condition|?
literal|null
else|:
name|ref
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
name|HandlesStreamInput
name|handles
init|=
operator|new
name|HandlesStreamInput
argument_list|()
decl_stmt|;
name|LZFStreamInput
name|lzf
init|=
operator|new
name|LZFStreamInput
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|entry
operator|=
operator|new
name|Entry
argument_list|(
name|handles
argument_list|,
name|lzf
argument_list|)
expr_stmt|;
name|cache
operator|.
name|set
argument_list|(
operator|new
name|SoftReference
argument_list|<
name|Entry
argument_list|>
argument_list|(
name|entry
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|entry
return|;
block|}
DECL|method|clear
specifier|public
specifier|static
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
DECL|method|cachedLzf
specifier|public
specifier|static
name|LZFStreamInput
name|cachedLzf
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|LZFStreamInput
name|lzf
init|=
name|instance
argument_list|()
operator|.
name|lzf
decl_stmt|;
name|lzf
operator|.
name|reset
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|lzf
return|;
block|}
DECL|method|cachedHandles
specifier|public
specifier|static
name|HandlesStreamInput
name|cachedHandles
parameter_list|(
name|StreamInput
name|in
parameter_list|)
block|{
name|HandlesStreamInput
name|handles
init|=
name|instance
argument_list|()
operator|.
name|handles
decl_stmt|;
name|handles
operator|.
name|reset
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|handles
return|;
block|}
DECL|method|cachedHandlesLzf
specifier|public
specifier|static
name|HandlesStreamInput
name|cachedHandlesLzf
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Entry
name|entry
init|=
name|instance
argument_list|()
decl_stmt|;
name|entry
operator|.
name|lzf
operator|.
name|reset
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|entry
operator|.
name|handles
operator|.
name|reset
argument_list|(
name|entry
operator|.
name|lzf
argument_list|)
expr_stmt|;
return|return
name|entry
operator|.
name|handles
return|;
block|}
block|}
end_class

end_unit

