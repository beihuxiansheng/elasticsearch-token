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
name|ActionRequestValidationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|replication
operator|.
name|IndexReplicationOperationRequest
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
name|Nullable
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
name|Required
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
name|Strings
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
name|unit
operator|.
name|TimeValue
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
name|query
operator|.
name|QueryBuilder
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|Actions
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Delete by query request to execute on a specific index.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|IndexDeleteByQueryRequest
specifier|public
class|class
name|IndexDeleteByQueryRequest
extends|extends
name|IndexReplicationOperationRequest
block|{
DECL|field|querySource
specifier|private
name|byte
index|[]
name|querySource
decl_stmt|;
DECL|field|queryParserName
specifier|private
name|String
name|queryParserName
decl_stmt|;
DECL|field|types
specifier|private
name|String
index|[]
name|types
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|routing
annotation|@
name|Nullable
specifier|private
name|String
name|routing
decl_stmt|;
DECL|method|IndexDeleteByQueryRequest
name|IndexDeleteByQueryRequest
parameter_list|(
name|DeleteByQueryRequest
name|request
parameter_list|,
name|String
name|index
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
name|timeout
operator|=
name|request
operator|.
name|timeout
argument_list|()
expr_stmt|;
name|this
operator|.
name|querySource
operator|=
name|request
operator|.
name|querySource
argument_list|()
expr_stmt|;
name|this
operator|.
name|queryParserName
operator|=
name|request
operator|.
name|queryParserName
argument_list|()
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|request
operator|.
name|types
argument_list|()
expr_stmt|;
name|this
operator|.
name|replicationType
operator|=
name|request
operator|.
name|replicationType
argument_list|()
expr_stmt|;
name|this
operator|.
name|consistencyLevel
operator|=
name|request
operator|.
name|consistencyLevel
argument_list|()
expr_stmt|;
name|this
operator|.
name|routing
operator|=
name|request
operator|.
name|routing
argument_list|()
expr_stmt|;
block|}
DECL|method|IndexDeleteByQueryRequest
name|IndexDeleteByQueryRequest
parameter_list|()
block|{     }
DECL|method|querySource
name|byte
index|[]
name|querySource
parameter_list|()
block|{
return|return
name|querySource
return|;
block|}
DECL|method|validate
annotation|@
name|Override
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
name|super
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|querySource
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"querySource is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
DECL|method|querySource
annotation|@
name|Required
specifier|public
name|IndexDeleteByQueryRequest
name|querySource
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
return|return
name|querySource
argument_list|(
name|queryBuilder
operator|.
name|buildAsBytes
argument_list|()
argument_list|)
return|;
block|}
DECL|method|querySource
annotation|@
name|Required
specifier|public
name|IndexDeleteByQueryRequest
name|querySource
parameter_list|(
name|byte
index|[]
name|querySource
parameter_list|)
block|{
name|this
operator|.
name|querySource
operator|=
name|querySource
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|queryParserName
name|String
name|queryParserName
parameter_list|()
block|{
return|return
name|queryParserName
return|;
block|}
DECL|method|routing
name|String
name|routing
parameter_list|()
block|{
return|return
name|this
operator|.
name|routing
return|;
block|}
DECL|method|types
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|this
operator|.
name|types
return|;
block|}
DECL|method|queryParserName
specifier|public
name|IndexDeleteByQueryRequest
name|queryParserName
parameter_list|(
name|String
name|queryParserName
parameter_list|)
block|{
name|this
operator|.
name|queryParserName
operator|=
name|queryParserName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|timeout
specifier|public
name|IndexDeleteByQueryRequest
name|timeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|readFrom
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
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|querySource
operator|=
operator|new
name|byte
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|querySource
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|queryParserName
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
name|int
name|typesSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|typesSize
operator|>
literal|0
condition|)
block|{
name|types
operator|=
operator|new
name|String
index|[
name|typesSize
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|typesSize
condition|;
name|i
operator|++
control|)
block|{
name|types
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|routing
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writeTo
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|querySource
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|querySource
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryParserName
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|queryParserName
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|types
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|type
range|:
name|types
control|)
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|routing
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

