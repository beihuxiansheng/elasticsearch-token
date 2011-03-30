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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|thread
operator|.
name|ThreadLocals
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|CachedStreamOutput
specifier|public
class|class
name|CachedStreamOutput
block|{
DECL|class|Entry
specifier|static
class|class
name|Entry
block|{
DECL|field|bytes
specifier|final
name|BytesStreamOutput
name|bytes
decl_stmt|;
DECL|field|handles
specifier|final
name|HandlesStreamOutput
name|handles
decl_stmt|;
DECL|field|lzf
specifier|final
name|LZFStreamOutput
name|lzf
decl_stmt|;
DECL|method|Entry
name|Entry
parameter_list|(
name|BytesStreamOutput
name|bytes
parameter_list|,
name|HandlesStreamOutput
name|handles
parameter_list|,
name|LZFStreamOutput
name|lzf
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
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
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|Entry
argument_list|>
argument_list|>
name|cache
init|=
operator|new
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|Entry
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|Entry
argument_list|>
name|initialValue
parameter_list|()
block|{
name|BytesStreamOutput
name|bytes
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|HandlesStreamOutput
name|handles
init|=
operator|new
name|HandlesStreamOutput
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|LZFStreamOutput
name|lzf
init|=
operator|new
name|LZFStreamOutput
argument_list|(
name|bytes
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|Entry
argument_list|>
argument_list|(
operator|new
name|Entry
argument_list|(
name|bytes
argument_list|,
name|handles
argument_list|,
name|lzf
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Returns the cached thread local byte stream, with its internal stream cleared.      */
DECL|method|cachedBytes
specifier|public
specifier|static
name|BytesStreamOutput
name|cachedBytes
parameter_list|()
block|{
name|BytesStreamOutput
name|os
init|=
name|cache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|bytes
decl_stmt|;
name|os
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|os
return|;
block|}
DECL|method|cachedLZFBytes
specifier|public
specifier|static
name|LZFStreamOutput
name|cachedLZFBytes
parameter_list|()
throws|throws
name|IOException
block|{
name|LZFStreamOutput
name|lzf
init|=
name|cache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|lzf
decl_stmt|;
name|lzf
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|lzf
return|;
block|}
DECL|method|cachedHandlesLzfBytes
specifier|public
specifier|static
name|HandlesStreamOutput
name|cachedHandlesLzfBytes
parameter_list|()
throws|throws
name|IOException
block|{
name|Entry
name|entry
init|=
name|cache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|HandlesStreamOutput
name|os
init|=
name|entry
operator|.
name|handles
decl_stmt|;
name|os
operator|.
name|reset
argument_list|(
name|entry
operator|.
name|lzf
argument_list|)
expr_stmt|;
return|return
name|os
return|;
block|}
DECL|method|cachedHandlesBytes
specifier|public
specifier|static
name|HandlesStreamOutput
name|cachedHandlesBytes
parameter_list|()
throws|throws
name|IOException
block|{
name|Entry
name|entry
init|=
name|cache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|HandlesStreamOutput
name|os
init|=
name|entry
operator|.
name|handles
decl_stmt|;
name|os
operator|.
name|reset
argument_list|(
name|entry
operator|.
name|bytes
argument_list|)
expr_stmt|;
return|return
name|os
return|;
block|}
block|}
end_class

end_unit

