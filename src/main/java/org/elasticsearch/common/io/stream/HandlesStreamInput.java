begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|gnu
operator|.
name|trove
operator|.
name|map
operator|.
name|hash
operator|.
name|TIntObjectHashMap
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
comment|/**  *  */
end_comment

begin_class
DECL|class|HandlesStreamInput
specifier|public
class|class
name|HandlesStreamInput
extends|extends
name|AdapterStreamInput
block|{
DECL|field|handles
specifier|private
specifier|final
name|TIntObjectHashMap
argument_list|<
name|String
argument_list|>
name|handles
init|=
operator|new
name|TIntObjectHashMap
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|identityHandles
specifier|private
specifier|final
name|TIntObjectHashMap
argument_list|<
name|String
argument_list|>
name|identityHandles
init|=
operator|new
name|TIntObjectHashMap
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|HandlesStreamInput
name|HandlesStreamInput
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|HandlesStreamInput
specifier|public
name|HandlesStreamInput
parameter_list|(
name|StreamInput
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readUTF
specifier|public
name|String
name|readUTF
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|b
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
comment|// full string with handle
name|int
name|handle
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|String
name|s
init|=
name|in
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|handles
operator|.
name|put
argument_list|(
name|handle
argument_list|,
name|s
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|1
condition|)
block|{
return|return
name|handles
operator|.
name|get
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|2
condition|)
block|{
comment|// full string with handle
name|int
name|handle
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|String
name|s
init|=
name|in
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|identityHandles
operator|.
name|put
argument_list|(
name|handle
argument_list|,
name|s
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|3
condition|)
block|{
return|return
name|identityHandles
operator|.
name|get
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expected handle header, got ["
operator|+
name|b
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|handles
operator|.
name|clear
argument_list|()
expr_stmt|;
name|identityHandles
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|StreamInput
name|in
parameter_list|)
block|{
name|super
operator|.
name|reset
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|handles
operator|.
name|clear
argument_list|()
expr_stmt|;
name|identityHandles
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|cleanHandles
specifier|public
name|void
name|cleanHandles
parameter_list|()
block|{
name|handles
operator|.
name|clear
argument_list|()
expr_stmt|;
name|identityHandles
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

