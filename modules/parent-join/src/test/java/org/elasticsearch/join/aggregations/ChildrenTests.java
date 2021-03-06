begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.join.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|join
operator|.
name|aggregations
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|join
operator|.
name|ParentJoinPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|BaseAggregationTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_class
DECL|class|ChildrenTests
specifier|public
class|class
name|ChildrenTests
extends|extends
name|BaseAggregationTestCase
argument_list|<
name|ChildrenAggregationBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getPlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|getPlugins
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|ParentJoinPlugin
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createTestAggregatorBuilder
specifier|protected
name|ChildrenAggregationBuilder
name|createTestAggregatorBuilder
parameter_list|()
block|{
name|String
name|name
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|String
name|childType
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|40
argument_list|)
decl_stmt|;
name|ChildrenAggregationBuilder
name|factory
init|=
operator|new
name|ChildrenAggregationBuilder
argument_list|(
name|name
argument_list|,
name|childType
argument_list|)
decl_stmt|;
return|return
name|factory
return|;
block|}
block|}
end_class

end_unit

