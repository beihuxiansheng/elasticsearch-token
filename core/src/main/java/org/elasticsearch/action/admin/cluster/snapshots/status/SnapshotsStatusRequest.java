begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.snapshots.status
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|status
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
name|master
operator|.
name|MasterNodeRequest
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
name|ValidateActions
operator|.
name|addValidationError
import|;
end_import

begin_comment
comment|/**  * Get snapshot status request  */
end_comment

begin_class
DECL|class|SnapshotsStatusRequest
specifier|public
class|class
name|SnapshotsStatusRequest
extends|extends
name|MasterNodeRequest
argument_list|<
name|SnapshotsStatusRequest
argument_list|>
block|{
DECL|field|repository
specifier|private
name|String
name|repository
init|=
literal|"_all"
decl_stmt|;
DECL|field|snapshots
specifier|private
name|String
index|[]
name|snapshots
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|ignoreUnavailable
specifier|private
name|boolean
name|ignoreUnavailable
decl_stmt|;
DECL|method|SnapshotsStatusRequest
specifier|public
name|SnapshotsStatusRequest
parameter_list|()
block|{     }
comment|/**      * Constructs a new get snapshots request with given repository name and list of snapshots      *      * @param repository repository name      * @param snapshots  list of snapshots      */
DECL|method|SnapshotsStatusRequest
specifier|public
name|SnapshotsStatusRequest
parameter_list|(
name|String
name|repository
parameter_list|,
name|String
index|[]
name|snapshots
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
name|snapshots
operator|=
name|snapshots
expr_stmt|;
block|}
comment|/**      * Constructs a new get snapshots request with given repository name      *      * @param repository repository name      */
DECL|method|SnapshotsStatusRequest
specifier|public
name|SnapshotsStatusRequest
parameter_list|(
name|String
name|repository
parameter_list|)
block|{
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|repository
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"repository is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|snapshots
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"snapshots is null"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
comment|/**      * Sets repository name      *      * @param repository repository name      * @return this request      */
DECL|method|repository
specifier|public
name|SnapshotsStatusRequest
name|repository
parameter_list|(
name|String
name|repository
parameter_list|)
block|{
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns repository name      *      * @return repository name      */
DECL|method|repository
specifier|public
name|String
name|repository
parameter_list|()
block|{
return|return
name|this
operator|.
name|repository
return|;
block|}
comment|/**      * Returns the names of the snapshots.      *      * @return the names of snapshots      */
DECL|method|snapshots
specifier|public
name|String
index|[]
name|snapshots
parameter_list|()
block|{
return|return
name|this
operator|.
name|snapshots
return|;
block|}
comment|/**      * Sets the list of snapshots to be returned      *      * @return this request      */
DECL|method|snapshots
specifier|public
name|SnapshotsStatusRequest
name|snapshots
parameter_list|(
name|String
index|[]
name|snapshots
parameter_list|)
block|{
name|this
operator|.
name|snapshots
operator|=
name|snapshots
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set to<code>true</code> to ignore unavailable snapshots, instead of throwing an exception.      * Defaults to<code>false</code>, which means unavailable snapshots cause an exception to be thrown.      *      * @param ignoreUnavailable whether to ignore unavailable snapshots      * @return this request      */
DECL|method|ignoreUnavailable
specifier|public
name|SnapshotsStatusRequest
name|ignoreUnavailable
parameter_list|(
name|boolean
name|ignoreUnavailable
parameter_list|)
block|{
name|this
operator|.
name|ignoreUnavailable
operator|=
name|ignoreUnavailable
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns whether the request permits unavailable snapshots to be ignored.      *      * @return true if the request will ignore unavailable snapshots, false if it will throw an exception on unavailable snapshots      */
DECL|method|ignoreUnavailable
specifier|public
name|boolean
name|ignoreUnavailable
parameter_list|()
block|{
return|return
name|ignoreUnavailable
return|;
block|}
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
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|repository
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|snapshots
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|ignoreUnavailable
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArray
argument_list|(
name|snapshots
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|ignoreUnavailable
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

