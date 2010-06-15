begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.deletebyquery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|deletebyquery
package|;
end_package

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
name|Streamable
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
comment|/**  * Delete by query response executed on a specific index.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|IndexDeleteByQueryResponse
specifier|public
class|class
name|IndexDeleteByQueryResponse
implements|implements
name|ActionResponse
implements|,
name|Streamable
block|{
DECL|field|index
specifier|private
name|String
name|index
decl_stmt|;
DECL|field|successfulShards
specifier|private
name|int
name|successfulShards
decl_stmt|;
DECL|field|failedShards
specifier|private
name|int
name|failedShards
decl_stmt|;
DECL|method|IndexDeleteByQueryResponse
name|IndexDeleteByQueryResponse
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|successfulShards
parameter_list|,
name|int
name|failedShards
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|successfulShards
operator|=
name|successfulShards
expr_stmt|;
name|this
operator|.
name|failedShards
operator|=
name|failedShards
expr_stmt|;
block|}
DECL|method|IndexDeleteByQueryResponse
name|IndexDeleteByQueryResponse
parameter_list|()
block|{      }
comment|/**      * The index the delete by query operation was executed against.      */
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
return|;
block|}
comment|/**      * The index the delete by query operation was executed against.      */
DECL|method|getIndex
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
comment|/**      * The total number of shards the delete by query was executed on.      */
DECL|method|totalShards
specifier|public
name|int
name|totalShards
parameter_list|()
block|{
return|return
name|failedShards
operator|+
name|successfulShards
return|;
block|}
comment|/**      * The total number of shards the delete by query was executed on.      */
DECL|method|getTotalShards
specifier|public
name|int
name|getTotalShards
parameter_list|()
block|{
return|return
name|totalShards
argument_list|()
return|;
block|}
comment|/**      * The successful number of shards the delete by query was executed on.      */
DECL|method|successfulShards
specifier|public
name|int
name|successfulShards
parameter_list|()
block|{
return|return
name|successfulShards
return|;
block|}
comment|/**      * The successful number of shards the delete by query was executed on.      */
DECL|method|getSuccessfulShards
specifier|public
name|int
name|getSuccessfulShards
parameter_list|()
block|{
return|return
name|successfulShards
return|;
block|}
comment|/**      * The failed number of shards the delete by query was executed on.      */
DECL|method|failedShards
specifier|public
name|int
name|failedShards
parameter_list|()
block|{
return|return
name|failedShards
return|;
block|}
comment|/**      * The failed number of shards the delete by query was executed on.      */
DECL|method|getFailedShards
specifier|public
name|int
name|getFailedShards
parameter_list|()
block|{
return|return
name|failedShards
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
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
name|index
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|successfulShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|failedShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
DECL|method|writeTo
annotation|@
name|Override
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
name|out
operator|.
name|writeUTF
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|successfulShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|failedShards
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

