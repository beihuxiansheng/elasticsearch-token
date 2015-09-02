begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.recycler
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|recycler
package|;
end_package

begin_class
DECL|class|NoneRecyclerTests
specifier|public
class|class
name|NoneRecyclerTests
extends|extends
name|AbstractRecyclerTestCase
block|{
annotation|@
name|Override
DECL|method|newRecycler
specifier|protected
name|Recycler
argument_list|<
name|byte
index|[]
argument_list|>
name|newRecycler
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
return|return
name|Recyclers
operator|.
name|none
argument_list|(
name|RECYCLER_C
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|assertRecycled
specifier|protected
name|void
name|assertRecycled
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
comment|// will never match
block|}
annotation|@
name|Override
DECL|method|assertDead
specifier|protected
name|void
name|assertDead
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
comment|// will never match
block|}
block|}
end_class

end_unit

