begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.benchmark.metrics
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|benchmark
operator|.
name|metrics
package|;
end_package

begin_class
DECL|class|Sample
specifier|public
specifier|final
class|class
name|Sample
block|{
DECL|field|operation
specifier|private
specifier|final
name|String
name|operation
decl_stmt|;
DECL|field|expectedStartTimestamp
specifier|private
specifier|final
name|long
name|expectedStartTimestamp
decl_stmt|;
DECL|field|startTimestamp
specifier|private
specifier|final
name|long
name|startTimestamp
decl_stmt|;
DECL|field|stopTimestamp
specifier|private
specifier|final
name|long
name|stopTimestamp
decl_stmt|;
DECL|field|success
specifier|private
specifier|final
name|boolean
name|success
decl_stmt|;
DECL|method|Sample
specifier|public
name|Sample
parameter_list|(
name|String
name|operation
parameter_list|,
name|long
name|expectedStartTimestamp
parameter_list|,
name|long
name|startTimestamp
parameter_list|,
name|long
name|stopTimestamp
parameter_list|,
name|boolean
name|success
parameter_list|)
block|{
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
name|this
operator|.
name|expectedStartTimestamp
operator|=
name|expectedStartTimestamp
expr_stmt|;
name|this
operator|.
name|startTimestamp
operator|=
name|startTimestamp
expr_stmt|;
name|this
operator|.
name|stopTimestamp
operator|=
name|stopTimestamp
expr_stmt|;
name|this
operator|.
name|success
operator|=
name|success
expr_stmt|;
block|}
DECL|method|getOperation
specifier|public
name|String
name|getOperation
parameter_list|()
block|{
return|return
name|operation
return|;
block|}
DECL|method|isSuccess
specifier|public
name|boolean
name|isSuccess
parameter_list|()
block|{
return|return
name|success
return|;
block|}
DECL|method|getStartTimestamp
specifier|public
name|long
name|getStartTimestamp
parameter_list|()
block|{
return|return
name|startTimestamp
return|;
block|}
DECL|method|getStopTimestamp
specifier|public
name|long
name|getStopTimestamp
parameter_list|()
block|{
return|return
name|stopTimestamp
return|;
block|}
DECL|method|getServiceTime
specifier|public
name|long
name|getServiceTime
parameter_list|()
block|{
return|return
name|stopTimestamp
operator|-
name|startTimestamp
return|;
block|}
DECL|method|getLatency
specifier|public
name|long
name|getLatency
parameter_list|()
block|{
return|return
name|stopTimestamp
operator|-
name|expectedStartTimestamp
return|;
block|}
block|}
end_class

end_unit

