begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectIntOpenHashMap
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
name|text
operator|.
name|Text
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
DECL|class|HandlesStreamOutput
specifier|public
class|class
name|HandlesStreamOutput
extends|extends
name|AdapterStreamOutput
block|{
DECL|field|handles
specifier|private
specifier|final
name|ObjectIntOpenHashMap
argument_list|<
name|String
argument_list|>
name|handles
init|=
operator|new
name|ObjectIntOpenHashMap
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|handlesText
specifier|private
specifier|final
name|ObjectIntOpenHashMap
argument_list|<
name|Text
argument_list|>
name|handlesText
init|=
operator|new
name|ObjectIntOpenHashMap
argument_list|<
name|Text
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|HandlesStreamOutput
specifier|public
name|HandlesStreamOutput
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeSharedString
specifier|public
name|void
name|writeSharedString
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|handles
operator|.
name|containsKey
argument_list|(
name|str
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|handles
operator|.
name|lget
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|handle
init|=
name|handles
operator|.
name|size
argument_list|()
decl_stmt|;
name|handles
operator|.
name|put
argument_list|(
name|str
argument_list|,
name|handle
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|handle
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeString
specifier|public
name|void
name|writeString
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeSharedText
specifier|public
name|void
name|writeSharedText
parameter_list|(
name|Text
name|text
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|handlesText
operator|.
name|containsKey
argument_list|(
name|text
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|handlesText
operator|.
name|lget
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|handle
init|=
name|handlesText
operator|.
name|size
argument_list|()
decl_stmt|;
name|handlesText
operator|.
name|put
argument_list|(
name|text
argument_list|,
name|handle
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|handle
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeText
argument_list|(
name|text
argument_list|)
expr_stmt|;
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
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|handles
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handlesText
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

