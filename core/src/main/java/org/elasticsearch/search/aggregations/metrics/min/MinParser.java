begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.min
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
operator|.
name|min
package|;
end_package

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
name|AggregatorFactory
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
name|metrics
operator|.
name|NumericValuesSourceMetricsAggregatorParser
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
name|support
operator|.
name|ValuesSource
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
name|support
operator|.
name|ValuesSourceParser
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MinParser
specifier|public
class|class
name|MinParser
extends|extends
name|NumericValuesSourceMetricsAggregatorParser
argument_list|<
name|InternalMin
argument_list|>
block|{
DECL|method|MinParser
specifier|public
name|MinParser
parameter_list|()
block|{
name|super
argument_list|(
name|InternalMin
operator|.
name|TYPE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createFactory
specifier|protected
name|AggregatorFactory
name|createFactory
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|ValuesSourceParser
operator|.
name|Input
argument_list|<
name|ValuesSource
operator|.
name|Numeric
argument_list|>
name|input
parameter_list|)
block|{
return|return
operator|new
name|MinAggregator
operator|.
name|Factory
argument_list|(
name|aggregationName
argument_list|,
name|input
argument_list|)
return|;
block|}
comment|// NORELEASE implement this method when refactoring this aggregation
annotation|@
name|Override
DECL|method|getFactoryPrototype
specifier|public
name|AggregatorFactory
name|getFactoryPrototype
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

