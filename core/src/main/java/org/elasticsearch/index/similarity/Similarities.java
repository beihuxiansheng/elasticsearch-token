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
name|BM25Similarity
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
name|DefaultSimilarity
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableMap
import|;
end_import

begin_comment
comment|/**  * Cache of pre-defined Similarities  */
end_comment

begin_class
DECL|class|Similarities
specifier|public
class|class
name|Similarities
block|{
DECL|field|PRE_BUILT_SIMILARITIES
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PreBuiltSimilarityProvider
operator|.
name|Factory
argument_list|>
name|PRE_BUILT_SIMILARITIES
decl_stmt|;
static|static
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PreBuiltSimilarityProvider
operator|.
name|Factory
argument_list|>
name|similarities
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|similarities
operator|.
name|put
argument_list|(
name|SimilarityLookupService
operator|.
name|DEFAULT_SIMILARITY
argument_list|,
operator|new
name|PreBuiltSimilarityProvider
operator|.
name|Factory
argument_list|(
name|SimilarityLookupService
operator|.
name|DEFAULT_SIMILARITY
argument_list|,
operator|new
name|DefaultSimilarity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|similarities
operator|.
name|put
argument_list|(
literal|"BM25"
argument_list|,
operator|new
name|PreBuiltSimilarityProvider
operator|.
name|Factory
argument_list|(
literal|"BM25"
argument_list|,
operator|new
name|BM25Similarity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|PRE_BUILT_SIMILARITIES
operator|=
name|unmodifiableMap
argument_list|(
name|similarities
argument_list|)
expr_stmt|;
block|}
DECL|method|Similarities
specifier|private
name|Similarities
parameter_list|()
block|{     }
comment|/**      * Returns the list of pre-defined SimilarityProvider Factories      *      * @return Pre-defined SimilarityProvider Factories      */
DECL|method|listFactories
specifier|public
specifier|static
name|Collection
argument_list|<
name|PreBuiltSimilarityProvider
operator|.
name|Factory
argument_list|>
name|listFactories
parameter_list|()
block|{
return|return
name|PRE_BUILT_SIMILARITIES
operator|.
name|values
argument_list|()
return|;
block|}
block|}
end_class

end_unit

