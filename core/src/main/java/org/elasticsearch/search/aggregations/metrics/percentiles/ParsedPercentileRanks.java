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

begin_class
DECL|class|ParsedPercentileRanks
specifier|public
specifier|abstract
class|class
name|ParsedPercentileRanks
extends|extends
name|ParsedPercentiles
implements|implements
name|PercentileRanks
block|{
annotation|@
name|Override
DECL|method|percent
specifier|public
name|double
name|percent
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
name|getPercentile
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|percentAsString
specifier|public
name|String
name|percentAsString
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
name|getPercentileAsString
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

