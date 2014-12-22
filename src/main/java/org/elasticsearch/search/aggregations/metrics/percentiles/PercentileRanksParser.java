begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.percentiles
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
name|percentiles
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
name|SearchParseException
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
name|support
operator|.
name|ValuesSource
operator|.
name|Numeric
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
name|ValuesSourceConfig
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
name|internal
operator|.
name|SearchContext
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|PercentileRanksParser
specifier|public
class|class
name|PercentileRanksParser
extends|extends
name|AbstractPercentilesParser
block|{
DECL|method|PercentileRanksParser
specifier|public
name|PercentileRanksParser
parameter_list|()
block|{
name|super
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|InternalPercentileRanks
operator|.
name|TYPE
operator|.
name|name
argument_list|()
return|;
block|}
DECL|method|keysFieldName
specifier|protected
name|String
name|keysFieldName
parameter_list|()
block|{
return|return
literal|"values"
return|;
block|}
DECL|method|buildFactory
specifier|protected
name|AggregatorFactory
name|buildFactory
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|String
name|aggregationName
parameter_list|,
name|ValuesSourceConfig
argument_list|<
name|Numeric
argument_list|>
name|valuesSourceConfig
parameter_list|,
name|double
index|[]
name|keys
parameter_list|,
name|double
name|compression
parameter_list|,
name|boolean
name|keyed
parameter_list|)
block|{
if|if
condition|(
name|keys
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Missing token values in ["
operator|+
name|aggregationName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
return|return
operator|new
name|PercentileRanksAggregator
operator|.
name|Factory
argument_list|(
name|aggregationName
argument_list|,
name|valuesSourceConfig
argument_list|,
name|keys
argument_list|,
name|compression
argument_list|,
name|keyed
argument_list|)
return|;
block|}
block|}
end_class

end_unit

