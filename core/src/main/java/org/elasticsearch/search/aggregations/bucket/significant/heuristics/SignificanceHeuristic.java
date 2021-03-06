begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.significant.heuristics
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|significant
operator|.
name|heuristics
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
name|io
operator|.
name|stream
operator|.
name|NamedWriteable
import|;
end_import

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
name|ToXContent
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
name|InternalAggregation
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
name|bucket
operator|.
name|significant
operator|.
name|SignificantTerms
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
comment|/**  * Heuristic for that {@link SignificantTerms} uses to pick out significant terms.  */
end_comment

begin_class
DECL|class|SignificanceHeuristic
specifier|public
specifier|abstract
class|class
name|SignificanceHeuristic
implements|implements
name|NamedWriteable
implements|,
name|ToXContent
block|{
comment|/**      * @param subsetFreq   The frequency of the term in the selected sample      * @param subsetSize   The size of the selected sample (typically number of docs)      * @param supersetFreq The frequency of the term in the superset from which the sample was taken      * @param supersetSize The size of the superset from which the sample was taken  (typically number of docs)      * @return a "significance" score      */
DECL|method|getScore
specifier|public
specifier|abstract
name|double
name|getScore
parameter_list|(
name|long
name|subsetFreq
parameter_list|,
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetFreq
parameter_list|,
name|long
name|supersetSize
parameter_list|)
function_decl|;
DECL|method|checkFrequencyValidity
specifier|protected
name|void
name|checkFrequencyValidity
parameter_list|(
name|long
name|subsetFreq
parameter_list|,
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetFreq
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|String
name|scoreFunctionName
parameter_list|)
block|{
if|if
condition|(
name|subsetFreq
operator|<
literal|0
operator|||
name|subsetSize
operator|<
literal|0
operator|||
name|supersetFreq
operator|<
literal|0
operator|||
name|supersetSize
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Frequencies of subset and superset must be positive in "
operator|+
name|scoreFunctionName
operator|+
literal|".getScore()"
argument_list|)
throw|;
block|}
if|if
condition|(
name|subsetFreq
operator|>
name|subsetSize
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"subsetFreq> subsetSize, in "
operator|+
name|scoreFunctionName
argument_list|)
throw|;
block|}
if|if
condition|(
name|supersetFreq
operator|>
name|supersetSize
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"supersetFreq> supersetSize, in "
operator|+
name|scoreFunctionName
argument_list|)
throw|;
block|}
block|}
comment|/**      * Provides a hook for subclasses to provide a version of the heuristic      * prepared for execution on data on the coordinating node.      * @param reduceContext the reduce context on the coordinating node      * @return a version of this heuristic suitable for execution      */
DECL|method|rewrite
specifier|public
name|SignificanceHeuristic
name|rewrite
parameter_list|(
name|InternalAggregation
operator|.
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
return|return
name|this
return|;
block|}
comment|/**      * Provides a hook for subclasses to provide a version of the heuristic      * prepared for execution on data on a shard.       * @param context the search context on the data node      * @return a version of this heuristic suitable for execution      */
DECL|method|rewrite
specifier|public
name|SignificanceHeuristic
name|rewrite
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

