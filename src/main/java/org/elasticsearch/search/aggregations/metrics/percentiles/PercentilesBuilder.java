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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
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
name|ValuesSourceMetricsAggregationBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|PercentilesBuilder
specifier|public
class|class
name|PercentilesBuilder
extends|extends
name|ValuesSourceMetricsAggregationBuilder
argument_list|<
name|PercentilesBuilder
argument_list|>
block|{
DECL|field|percentiles
specifier|private
name|double
index|[]
name|percentiles
decl_stmt|;
DECL|field|estimator
specifier|private
name|Percentiles
operator|.
name|Estimator
name|estimator
decl_stmt|;
DECL|method|PercentilesBuilder
specifier|public
name|PercentilesBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalPercentiles
operator|.
name|TYPE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|percentiles
specifier|public
name|PercentilesBuilder
name|percentiles
parameter_list|(
name|double
modifier|...
name|percentiles
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|percentiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|percentiles
index|[
name|i
index|]
operator|<
literal|0
operator|||
name|percentiles
index|[
name|i
index|]
operator|>
literal|100
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"the percents in the percentiles aggregation ["
operator|+
name|name
operator|+
literal|"] must be in the [0, 100] range"
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|percentiles
operator|=
name|percentiles
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|estimator
specifier|public
name|PercentilesBuilder
name|estimator
parameter_list|(
name|Percentiles
operator|.
name|Estimator
name|estimator
parameter_list|)
block|{
name|this
operator|.
name|estimator
operator|=
name|estimator
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|internalXContent
specifier|protected
name|void
name|internalXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|internalXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|percentiles
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"percents"
argument_list|,
name|percentiles
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|estimator
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"estimator"
argument_list|,
name|estimator
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|estimator
operator|.
name|paramsToXContent
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

