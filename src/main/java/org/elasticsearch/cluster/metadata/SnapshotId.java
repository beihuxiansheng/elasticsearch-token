begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
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
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Snapshot ID - repository name + snapshot name  */
end_comment

begin_class
DECL|class|SnapshotId
specifier|public
class|class
name|SnapshotId
implements|implements
name|Serializable
implements|,
name|Streamable
block|{
DECL|field|repository
specifier|private
name|String
name|repository
decl_stmt|;
DECL|field|snapshot
specifier|private
name|String
name|snapshot
decl_stmt|;
comment|// Caching hash code
DECL|field|hashCode
specifier|private
name|int
name|hashCode
decl_stmt|;
DECL|method|SnapshotId
specifier|private
name|SnapshotId
parameter_list|()
block|{     }
comment|/**      * Constructs new snapshot id      *      * @param repository repository name      * @param snapshot   snapshot name      */
DECL|method|SnapshotId
specifier|public
name|SnapshotId
parameter_list|(
name|String
name|repository
parameter_list|,
name|String
name|snapshot
parameter_list|)
block|{
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
name|this
operator|.
name|snapshot
operator|=
name|snapshot
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|computeHashCode
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns repository name      *      * @return repository name      */
DECL|method|getRepository
specifier|public
name|String
name|getRepository
parameter_list|()
block|{
return|return
name|repository
return|;
block|}
comment|/**      * Returns snapshot name      *      * @return snapshot name      */
DECL|method|getSnapshot
specifier|public
name|String
name|getSnapshot
parameter_list|()
block|{
return|return
name|snapshot
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|repository
operator|+
literal|":"
operator|+
name|snapshot
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|SnapshotId
name|snapshotId
init|=
operator|(
name|SnapshotId
operator|)
name|o
decl_stmt|;
return|return
name|snapshot
operator|.
name|equals
argument_list|(
name|snapshotId
operator|.
name|snapshot
argument_list|)
operator|&&
name|repository
operator|.
name|equals
argument_list|(
name|snapshotId
operator|.
name|repository
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hashCode
return|;
block|}
DECL|method|computeHashCode
specifier|private
name|int
name|computeHashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|repository
operator|!=
literal|null
condition|?
name|repository
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|snapshot
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Reads snapshot id from stream input      *      * @param in stream input      * @return snapshot id      * @throws IOException      */
DECL|method|readSnapshotId
specifier|public
specifier|static
name|SnapshotId
name|readSnapshotId
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|SnapshotId
name|snapshot
init|=
operator|new
name|SnapshotId
argument_list|()
decl_stmt|;
name|snapshot
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|snapshot
return|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
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
name|repository
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|snapshot
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|hashCode
operator|=
name|computeHashCode
argument_list|()
expr_stmt|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
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
name|out
operator|.
name|writeString
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|snapshot
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

