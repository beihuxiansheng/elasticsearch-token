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
name|ClassicSimilarity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
comment|/**  * {@link SimilarityProvider} for {@link ClassicSimilarity}.  *<p>  * Configuration options available:  *<ul>  *<li>discount_overlaps</li>  *</ul>  * @see ClassicSimilarity For more information about configuration  */
end_comment

begin_class
DECL|class|ClassicSimilarityProvider
specifier|public
class|class
name|ClassicSimilarityProvider
extends|extends
name|AbstractSimilarityProvider
block|{
DECL|field|similarity
specifier|private
specifier|final
name|ClassicSimilarity
name|similarity
init|=
operator|new
name|ClassicSimilarity
argument_list|()
decl_stmt|;
DECL|method|ClassicSimilarityProvider
specifier|public
name|ClassicSimilarityProvider
parameter_list|(
name|String
name|name
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|boolean
name|discountOverlaps
init|=
name|settings
operator|.
name|getAsBooleanLenientForPreEs6Indices
argument_list|(
name|Version
operator|.
name|indexCreated
argument_list|(
name|indexSettings
argument_list|)
argument_list|,
literal|"discount_overlaps"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|this
operator|.
name|similarity
operator|.
name|setDiscountOverlaps
argument_list|(
name|discountOverlaps
argument_list|)
expr_stmt|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
DECL|method|get
specifier|public
name|ClassicSimilarity
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

