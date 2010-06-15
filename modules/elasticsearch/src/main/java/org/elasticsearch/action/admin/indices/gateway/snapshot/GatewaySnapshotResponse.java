begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.gateway.snapshot
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
name|gateway
operator|.
name|snapshot
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
name|Iterator
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

begin_comment
comment|/**  * Reponse for the gateway snapshot action.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|GatewaySnapshotResponse
specifier|public
class|class
name|GatewaySnapshotResponse
implements|implements
name|ActionResponse
implements|,
name|Streamable
implements|,
name|Iterable
argument_list|<
name|IndexGatewaySnapshotResponse
argument_list|>
block|{
DECL|field|indices
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|IndexGatewaySnapshotResponse
argument_list|>
name|indices
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|IndexGatewaySnapshotResponse
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|GatewaySnapshotResponse
name|GatewaySnapshotResponse
parameter_list|()
block|{      }
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|IndexGatewaySnapshotResponse
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|indices
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**      * A map of index level responses of the gateway snapshot operation.      */
DECL|method|indices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|IndexGatewaySnapshotResponse
argument_list|>
name|indices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
comment|/**      * A map of index level responses of the gateway snapshot operation.      */
DECL|method|getIndices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|IndexGatewaySnapshotResponse
argument_list|>
name|getIndices
parameter_list|()
block|{
return|return
name|indices
argument_list|()
return|;
block|}
comment|/**      * The index level gateway snapshot response for the given index.      */
DECL|method|index
specifier|public
name|IndexGatewaySnapshotResponse
name|index
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
name|indices
operator|.
name|get
argument_list|(
name|index
argument_list|)
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
name|int
name|size
init|=
name|in
operator|.
name|readVInt
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
name|IndexGatewaySnapshotResponse
name|response
init|=
operator|new
name|IndexGatewaySnapshotResponse
argument_list|()
decl_stmt|;
name|response
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|indices
operator|.
name|put
argument_list|(
name|response
operator|.
name|index
argument_list|()
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
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
name|writeVInt
argument_list|(
name|indices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexGatewaySnapshotResponse
name|indexGatewaySnapshotResponse
range|:
name|indices
operator|.
name|values
argument_list|()
control|)
block|{
name|indexGatewaySnapshotResponse
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

