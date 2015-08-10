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
name|Maps
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
name|AbstractModule
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
name|Scopes
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
name|FactoryProvider
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
name|multibindings
operator|.
name|MapBinder
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * {@link SimilarityModule} is responsible gathering registered and configured {@link SimilarityProvider}  * implementations and making them available through the {@link SimilarityLookupService} and {@link SimilarityService}.  *  * New {@link SimilarityProvider} implementations can be registered through {@link #addSimilarity(String, Class)}  * while existing Providers can be referenced through Settings under the {@link #SIMILARITY_SETTINGS_PREFIX} prefix  * along with the "type" value.  For example, to reference the {@link BM25SimilarityProvider}, the configuration  *<tt>"index.similarity.my_similarity.type : "BM25"</tt> can be used.  */
end_comment

begin_class
DECL|class|SimilarityModule
specifier|public
class|class
name|SimilarityModule
extends|extends
name|AbstractModule
block|{
DECL|field|SIMILARITY_SETTINGS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|SIMILARITY_SETTINGS_PREFIX
init|=
literal|"index.similarity"
decl_stmt|;
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|similarities
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|SimilarityProvider
argument_list|>
argument_list|>
name|similarities
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|method|SimilarityModule
specifier|public
name|SimilarityModule
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|addSimilarity
argument_list|(
literal|"default"
argument_list|,
name|DefaultSimilarityProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|addSimilarity
argument_list|(
literal|"BM25"
argument_list|,
name|BM25SimilarityProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|addSimilarity
argument_list|(
literal|"DFR"
argument_list|,
name|DFRSimilarityProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|addSimilarity
argument_list|(
literal|"IB"
argument_list|,
name|IBSimilarityProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|addSimilarity
argument_list|(
literal|"LMDirichlet"
argument_list|,
name|LMDirichletSimilarityProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|addSimilarity
argument_list|(
literal|"LMJelinekMercer"
argument_list|,
name|LMJelinekMercerSimilarityProvider
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**      * Registers the given {@link SimilarityProvider} with the given name      *      * @param name Name of the SimilarityProvider      * @param similarity SimilarityProvider to register      */
DECL|method|addSimilarity
specifier|public
name|void
name|addSimilarity
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|SimilarityProvider
argument_list|>
name|similarity
parameter_list|)
block|{
name|similarities
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|similarity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|MapBinder
argument_list|<
name|String
argument_list|,
name|SimilarityProvider
operator|.
name|Factory
argument_list|>
name|similarityBinder
init|=
name|MapBinder
operator|.
name|newMapBinder
argument_list|(
name|binder
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|SimilarityProvider
operator|.
name|Factory
operator|.
name|class
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|similaritySettings
init|=
name|settings
operator|.
name|getGroups
argument_list|(
name|SIMILARITY_SETTINGS_PREFIX
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|entry
range|:
name|similaritySettings
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|typeName
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Similarity ["
operator|+
name|name
operator|+
literal|"] must have an associated type"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|similarities
operator|.
name|containsKey
argument_list|(
name|typeName
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown Similarity type ["
operator|+
name|typeName
operator|+
literal|"] for ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|similarityBinder
operator|.
name|addBinding
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|toProvider
argument_list|(
name|FactoryProvider
operator|.
name|newFactory
argument_list|(
name|SimilarityProvider
operator|.
name|Factory
operator|.
name|class
argument_list|,
name|similarities
operator|.
name|get
argument_list|(
name|typeName
argument_list|)
argument_list|)
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|PreBuiltSimilarityProvider
operator|.
name|Factory
name|factory
range|:
name|Similarities
operator|.
name|listFactories
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|similarities
operator|.
name|containsKey
argument_list|(
name|factory
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|similarityBinder
operator|.
name|addBinding
argument_list|(
name|factory
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|toInstance
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
block|}
name|bind
argument_list|(
name|SimilarityLookupService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|SimilarityService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

