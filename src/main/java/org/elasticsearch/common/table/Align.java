begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.table
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|table
package|;
end_package

begin_enum
DECL|enum|Align
specifier|public
enum|enum
name|Align
block|{
DECL|enum constant|LEFT
name|LEFT
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
DECL|enum constant|CENTER
name|CENTER
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|,
DECL|enum constant|RIGHT
name|RIGHT
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
block|;
DECL|field|id
specifier|private
name|byte
name|id
decl_stmt|;
DECL|method|Align
name|Align
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
block|}
end_enum

end_unit

