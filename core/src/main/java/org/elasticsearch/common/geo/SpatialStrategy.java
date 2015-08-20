begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
package|;
end_package

begin_comment
comment|/**  *  */
end_comment

begin_enum
DECL|enum|SpatialStrategy
specifier|public
enum|enum
name|SpatialStrategy
block|{
DECL|enum constant|TERM
name|TERM
argument_list|(
literal|"term"
argument_list|)
block|,
DECL|enum constant|RECURSIVE
name|RECURSIVE
argument_list|(
literal|"recursive"
argument_list|)
block|;
DECL|field|strategyName
specifier|private
specifier|final
name|String
name|strategyName
decl_stmt|;
DECL|method|SpatialStrategy
specifier|private
name|SpatialStrategy
parameter_list|(
name|String
name|strategyName
parameter_list|)
block|{
name|this
operator|.
name|strategyName
operator|=
name|strategyName
expr_stmt|;
block|}
DECL|method|getStrategyName
specifier|public
name|String
name|getStrategyName
parameter_list|()
block|{
return|return
name|strategyName
return|;
block|}
block|}
end_enum

end_unit
