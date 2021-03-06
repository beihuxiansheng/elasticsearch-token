begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.replication
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|replication
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
name|xcontent
operator|.
name|XContentBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|Task
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|TaskId
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
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Task that tracks replication actions.  */
end_comment

begin_class
DECL|class|ReplicationTask
specifier|public
class|class
name|ReplicationTask
extends|extends
name|Task
block|{
DECL|field|phase
specifier|private
specifier|volatile
name|String
name|phase
init|=
literal|"starting"
decl_stmt|;
DECL|method|ReplicationTask
specifier|public
name|ReplicationTask
parameter_list|(
name|long
name|id
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|action
parameter_list|,
name|String
name|description
parameter_list|,
name|TaskId
name|parentTaskId
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|type
argument_list|,
name|action
argument_list|,
name|description
argument_list|,
name|parentTaskId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the current phase of the task.      */
DECL|method|setPhase
specifier|public
name|void
name|setPhase
parameter_list|(
name|String
name|phase
parameter_list|)
block|{
name|this
operator|.
name|phase
operator|=
name|phase
expr_stmt|;
block|}
comment|/**      * Get the current phase of the task.      */
DECL|method|getPhase
specifier|public
name|String
name|getPhase
parameter_list|()
block|{
return|return
name|phase
return|;
block|}
annotation|@
name|Override
DECL|method|getStatus
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
operator|new
name|Status
argument_list|(
name|phase
argument_list|)
return|;
block|}
DECL|class|Status
specifier|public
specifier|static
class|class
name|Status
implements|implements
name|Task
operator|.
name|Status
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"replication"
decl_stmt|;
DECL|field|phase
specifier|private
specifier|final
name|String
name|phase
decl_stmt|;
DECL|method|Status
specifier|public
name|Status
parameter_list|(
name|String
name|phase
parameter_list|)
block|{
name|this
operator|.
name|phase
operator|=
name|requireNonNull
argument_list|(
name|phase
argument_list|,
literal|"Phase cannot be null"
argument_list|)
expr_stmt|;
block|}
DECL|method|Status
specifier|public
name|Status
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|phase
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"phase"
argument_list|,
name|phase
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
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
name|out
operator|.
name|writeString
argument_list|(
name|phase
argument_list|)
expr_stmt|;
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
name|Strings
operator|.
name|toString
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|// Implements equals and hashcode for testing
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|ReplicationTask
operator|.
name|Status
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ReplicationTask
operator|.
name|Status
name|other
init|=
operator|(
name|Status
operator|)
name|obj
decl_stmt|;
return|return
name|phase
operator|.
name|equals
argument_list|(
name|other
operator|.
name|phase
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
name|phase
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

