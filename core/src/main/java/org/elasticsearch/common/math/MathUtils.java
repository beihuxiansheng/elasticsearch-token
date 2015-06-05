begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.math
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|math
package|;
end_package

begin_enum
DECL|enum|MathUtils
specifier|public
enum|enum
name|MathUtils
block|{     ;
comment|/**      * Return the (positive) remainder of the division of<code>v</code> by<code>mod</code>.      */
DECL|method|mod
specifier|public
specifier|static
name|int
name|mod
parameter_list|(
name|int
name|v
parameter_list|,
name|int
name|m
parameter_list|)
block|{
name|int
name|r
init|=
name|v
operator|%
name|m
decl_stmt|;
if|if
condition|(
name|r
operator|<
literal|0
condition|)
block|{
name|r
operator|+=
name|m
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
block|}
end_enum

end_unit

