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
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_enum
DECL|enum|SnapshotIndexShardStage
specifier|public
enum|enum
name|SnapshotIndexShardStage
block|{
comment|/**      * Snapshot hasn't started yet      */
DECL|enum constant|INIT
name|INIT
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|,
literal|false
argument_list|)
block|,
comment|/**      * Index files are being copied      */
DECL|enum constant|STARTED
name|STARTED
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|,
literal|false
argument_list|)
block|,
comment|/**      * Snapshot metadata is being written      */
DECL|enum constant|FINALIZE
name|FINALIZE
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|,
literal|false
argument_list|)
block|,
comment|/**      * Snapshot completed successfully      */
DECL|enum constant|DONE
name|DONE
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|,
literal|true
argument_list|)
block|,
comment|/**      * Snapshot failed      */
DECL|enum constant|FAILURE
name|FAILURE
argument_list|(
operator|(
name|byte
operator|)
literal|4
argument_list|,
literal|true
argument_list|)
block|;
DECL|field|value
specifier|private
name|byte
name|value
decl_stmt|;
DECL|field|completed
specifier|private
name|boolean
name|completed
decl_stmt|;
DECL|method|SnapshotIndexShardStage
specifier|private
name|SnapshotIndexShardStage
parameter_list|(
name|byte
name|value
parameter_list|,
name|boolean
name|completed
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|completed
operator|=
name|completed
expr_stmt|;
block|}
comment|/**      * Returns code that represents the snapshot state      *      * @return code for the state      */
DECL|method|value
specifier|public
name|byte
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**      * Returns true if snapshot completed (successfully or not)      *      * @return true if snapshot completed, false otherwise      */
DECL|method|completed
specifier|public
name|boolean
name|completed
parameter_list|()
block|{
return|return
name|completed
return|;
block|}
comment|/**      * Generate snapshot state from code      *      * @param value the state code      * @return state      */
DECL|method|fromValue
specifier|public
specifier|static
name|SnapshotIndexShardStage
name|fromValue
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
switch|switch
condition|(
name|value
condition|)
block|{
case|case
literal|0
case|:
return|return
name|INIT
return|;
case|case
literal|1
case|:
return|return
name|STARTED
return|;
case|case
literal|2
case|:
return|return
name|FINALIZE
return|;
case|case
literal|3
case|:
return|return
name|DONE
return|;
case|case
literal|4
case|:
return|return
name|FAILURE
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No snapshot shard stage for value ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_enum

end_unit

