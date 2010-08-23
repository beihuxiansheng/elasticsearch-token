begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.status
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
name|status
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|ByteSizeValue
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|GatewaySnapshotStatus
specifier|public
class|class
name|GatewaySnapshotStatus
block|{
DECL|enum|Stage
specifier|public
specifier|static
enum|enum
name|Stage
block|{
DECL|enum constant|NONE
name|NONE
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
DECL|enum constant|INDEX
name|INDEX
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|,
DECL|enum constant|TRANSLOG
name|TRANSLOG
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
block|,
DECL|enum constant|FINALIZE
name|FINALIZE
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|)
block|,
DECL|enum constant|DONE
name|DONE
argument_list|(
operator|(
name|byte
operator|)
literal|4
argument_list|)
block|,
DECL|enum constant|FAILURE
name|FAILURE
argument_list|(
operator|(
name|byte
operator|)
literal|5
argument_list|)
block|;
DECL|field|value
specifier|private
specifier|final
name|byte
name|value
decl_stmt|;
DECL|method|Stage
name|Stage
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|value
specifier|public
name|byte
name|value
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
DECL|method|fromValue
specifier|public
specifier|static
name|Stage
name|fromValue
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|0
condition|)
block|{
return|return
name|Stage
operator|.
name|NONE
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|1
condition|)
block|{
return|return
name|Stage
operator|.
name|INDEX
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|2
condition|)
block|{
return|return
name|Stage
operator|.
name|TRANSLOG
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|3
condition|)
block|{
return|return
name|Stage
operator|.
name|FINALIZE
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|4
condition|)
block|{
return|return
name|Stage
operator|.
name|DONE
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|5
condition|)
block|{
return|return
name|Stage
operator|.
name|FAILURE
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No stage found for ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|field|stage
specifier|final
name|Stage
name|stage
decl_stmt|;
DECL|field|startTime
specifier|final
name|long
name|startTime
decl_stmt|;
DECL|field|time
specifier|final
name|long
name|time
decl_stmt|;
DECL|field|indexSize
specifier|final
name|long
name|indexSize
decl_stmt|;
DECL|field|expectedNumberOfOperations
specifier|final
name|int
name|expectedNumberOfOperations
decl_stmt|;
DECL|method|GatewaySnapshotStatus
specifier|public
name|GatewaySnapshotStatus
parameter_list|(
name|Stage
name|stage
parameter_list|,
name|long
name|startTime
parameter_list|,
name|long
name|time
parameter_list|,
name|long
name|indexSize
parameter_list|,
name|int
name|expectedNumberOfOperations
parameter_list|)
block|{
name|this
operator|.
name|stage
operator|=
name|stage
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
name|this
operator|.
name|time
operator|=
name|time
expr_stmt|;
name|this
operator|.
name|indexSize
operator|=
name|indexSize
expr_stmt|;
name|this
operator|.
name|expectedNumberOfOperations
operator|=
name|expectedNumberOfOperations
expr_stmt|;
block|}
DECL|method|stage
specifier|public
name|Stage
name|stage
parameter_list|()
block|{
return|return
name|this
operator|.
name|stage
return|;
block|}
DECL|method|getStage
specifier|public
name|Stage
name|getStage
parameter_list|()
block|{
return|return
name|stage
argument_list|()
return|;
block|}
DECL|method|startTime
specifier|public
name|long
name|startTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
DECL|method|getStartTime
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
argument_list|()
return|;
block|}
DECL|method|time
specifier|public
name|TimeValue
name|time
parameter_list|()
block|{
return|return
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|time
argument_list|)
return|;
block|}
DECL|method|getTime
specifier|public
name|TimeValue
name|getTime
parameter_list|()
block|{
return|return
name|time
argument_list|()
return|;
block|}
DECL|method|indexSize
specifier|public
name|ByteSizeValue
name|indexSize
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|indexSize
argument_list|)
return|;
block|}
DECL|method|getIndexSize
specifier|public
name|ByteSizeValue
name|getIndexSize
parameter_list|()
block|{
return|return
name|indexSize
argument_list|()
return|;
block|}
DECL|method|expectedNumberOfOperations
specifier|public
name|int
name|expectedNumberOfOperations
parameter_list|()
block|{
return|return
name|expectedNumberOfOperations
return|;
block|}
DECL|method|getExpectedNumberOfOperations
specifier|public
name|int
name|getExpectedNumberOfOperations
parameter_list|()
block|{
return|return
name|expectedNumberOfOperations
argument_list|()
return|;
block|}
block|}
end_class

end_unit

