begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.similarity
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|similarity
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
operator|.
name|*
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
name|collect
operator|.
name|MapBuilder
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
name|inject
operator|.
name|Inject
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
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/**  * {@link SimilarityProvider} for {@link IBSimilarity}.  *<p>  * Configuration options available:  *<ul>  *<li>distribution</li>  *<li>lambda</li>  *<li>normalization</li>  *</ul>  * @see IBSimilarity For more information about configuration  */
end_comment

begin_class
DECL|class|IBSimilarityProvider
specifier|public
class|class
name|IBSimilarityProvider
extends|extends
name|AbstractSimilarityProvider
block|{
DECL|field|DISTRIBUTION_CACHE
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Distribution
argument_list|>
name|DISTRIBUTION_CACHE
decl_stmt|;
DECL|field|LAMBDA_CACHE
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Lambda
argument_list|>
name|LAMBDA_CACHE
decl_stmt|;
static|static
block|{
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|Distribution
argument_list|>
name|distributions
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
name|distributions
operator|.
name|put
argument_list|(
literal|"ll"
argument_list|,
operator|new
name|DistributionLL
argument_list|()
argument_list|)
expr_stmt|;
name|distributions
operator|.
name|put
argument_list|(
literal|"spl"
argument_list|,
operator|new
name|DistributionSPL
argument_list|()
argument_list|)
expr_stmt|;
name|DISTRIBUTION_CACHE
operator|=
name|distributions
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|Lambda
argument_list|>
name|lamdas
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
name|lamdas
operator|.
name|put
argument_list|(
literal|"df"
argument_list|,
operator|new
name|LambdaDF
argument_list|()
argument_list|)
expr_stmt|;
name|lamdas
operator|.
name|put
argument_list|(
literal|"ttf"
argument_list|,
operator|new
name|LambdaTTF
argument_list|()
argument_list|)
expr_stmt|;
name|LAMBDA_CACHE
operator|=
name|lamdas
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
DECL|field|similarity
specifier|private
specifier|final
name|IBSimilarity
name|similarity
decl_stmt|;
annotation|@
name|Inject
DECL|method|IBSimilarityProvider
specifier|public
name|IBSimilarityProvider
parameter_list|(
annotation|@
name|Assisted
name|String
name|name
parameter_list|,
annotation|@
name|Assisted
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|Distribution
name|distribution
init|=
name|parseDistribution
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|Lambda
name|lambda
init|=
name|parseLambda
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|Normalization
name|normalization
init|=
name|parseNormalization
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|this
operator|.
name|similarity
operator|=
operator|new
name|IBSimilarity
argument_list|(
name|distribution
argument_list|,
name|lambda
argument_list|,
name|normalization
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parses the given Settings and creates the appropriate {@link Distribution}      *      * @param settings Settings to parse      * @return {@link Normalization} referred to in the Settings      */
DECL|method|parseDistribution
specifier|protected
name|Distribution
name|parseDistribution
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|String
name|rawDistribution
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"distribution"
argument_list|)
decl_stmt|;
name|Distribution
name|distribution
init|=
name|DISTRIBUTION_CACHE
operator|.
name|get
argument_list|(
name|rawDistribution
argument_list|)
decl_stmt|;
if|if
condition|(
name|distribution
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported Distribution ["
operator|+
name|rawDistribution
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|distribution
return|;
block|}
comment|/**      * Parses the given Settings and creates the appropriate {@link Lambda}      *      * @param settings Settings to parse      * @return {@link Normalization} referred to in the Settings      */
DECL|method|parseLambda
specifier|protected
name|Lambda
name|parseLambda
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|String
name|rawLambda
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"lambda"
argument_list|)
decl_stmt|;
name|Lambda
name|lambda
init|=
name|LAMBDA_CACHE
operator|.
name|get
argument_list|(
name|rawLambda
argument_list|)
decl_stmt|;
if|if
condition|(
name|lambda
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported Lambda ["
operator|+
name|rawLambda
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|lambda
return|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
DECL|method|get
specifier|public
name|Similarity
name|get
parameter_list|()
block|{
return|return
name|similarity
return|;
block|}
block|}
end_class

end_unit

