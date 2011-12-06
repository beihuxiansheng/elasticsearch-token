begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.analysis
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
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
name|elasticsearch
operator|.
name|common
operator|.
name|Nullable
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
name|Strings
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
name|component
operator|.
name|CloseableComponent
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
name|settings
operator|.
name|ImmutableSettings
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|AbstractIndexComponent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|Index
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|settings
operator|.
name|IndexSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|analysis
operator|.
name|IndicesAnalysisService
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AnalysisService
specifier|public
class|class
name|AnalysisService
extends|extends
name|AbstractIndexComponent
implements|implements
name|CloseableComponent
block|{
DECL|field|analyzers
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|NamedAnalyzer
argument_list|>
name|analyzers
decl_stmt|;
DECL|field|tokenizers
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|TokenizerFactory
argument_list|>
name|tokenizers
decl_stmt|;
DECL|field|charFilters
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|CharFilterFactory
argument_list|>
name|charFilters
decl_stmt|;
DECL|field|tokenFilters
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|TokenFilterFactory
argument_list|>
name|tokenFilters
decl_stmt|;
DECL|field|defaultAnalyzer
specifier|private
specifier|final
name|NamedAnalyzer
name|defaultAnalyzer
decl_stmt|;
DECL|field|defaultIndexAnalyzer
specifier|private
specifier|final
name|NamedAnalyzer
name|defaultIndexAnalyzer
decl_stmt|;
DECL|field|defaultSearchAnalyzer
specifier|private
specifier|final
name|NamedAnalyzer
name|defaultSearchAnalyzer
decl_stmt|;
DECL|method|AnalysisService
specifier|public
name|AnalysisService
parameter_list|(
name|Index
name|index
parameter_list|)
block|{
name|this
argument_list|(
name|index
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Inject
DECL|method|AnalysisService
specifier|public
name|AnalysisService
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
annotation|@
name|Nullable
name|IndicesAnalysisService
name|indicesAnalysisService
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|AnalyzerProviderFactory
argument_list|>
name|analyzerFactoryFactories
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|TokenizerFactoryFactory
argument_list|>
name|tokenizerFactoryFactories
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|CharFilterFactoryFactory
argument_list|>
name|charFilterFactoryFactories
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|TokenFilterFactoryFactory
argument_list|>
name|tokenFilterFactoryFactories
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|TokenizerFactory
argument_list|>
name|tokenizers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokenizerFactoryFactories
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|tokenizersSettings
init|=
name|indexSettings
operator|.
name|getGroups
argument_list|(
literal|"index.analysis.tokenizer"
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
name|TokenizerFactoryFactory
argument_list|>
name|entry
range|:
name|tokenizerFactoryFactories
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|tokenizerName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|TokenizerFactoryFactory
name|tokenizerFactoryFactory
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Settings
name|tokenizerSettings
init|=
name|tokenizersSettings
operator|.
name|get
argument_list|(
name|tokenizerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenizerSettings
operator|==
literal|null
condition|)
block|{
name|tokenizerSettings
operator|=
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
expr_stmt|;
block|}
name|TokenizerFactory
name|tokenizerFactory
init|=
name|tokenizerFactoryFactory
operator|.
name|create
argument_list|(
name|tokenizerName
argument_list|,
name|tokenizerSettings
argument_list|)
decl_stmt|;
name|tokenizers
operator|.
name|put
argument_list|(
name|tokenizerName
argument_list|,
name|tokenizerFactory
argument_list|)
expr_stmt|;
name|tokenizers
operator|.
name|put
argument_list|(
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|tokenizerName
argument_list|)
argument_list|,
name|tokenizerFactory
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indicesAnalysisService
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PreBuiltTokenizerFactoryFactory
argument_list|>
name|entry
range|:
name|indicesAnalysisService
operator|.
name|tokenizerFactories
argument_list|()
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
if|if
condition|(
operator|!
name|tokenizers
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|tokenizers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|name
operator|=
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|tokenizers
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|tokenizers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|this
operator|.
name|tokenizers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|tokenizers
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|CharFilterFactory
argument_list|>
name|charFilters
init|=
name|newHashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|charFilterFactoryFactories
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|charFiltersSettings
init|=
name|indexSettings
operator|.
name|getGroups
argument_list|(
literal|"index.analysis.char_filter"
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
name|CharFilterFactoryFactory
argument_list|>
name|entry
range|:
name|charFilterFactoryFactories
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|charFilterName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|CharFilterFactoryFactory
name|charFilterFactoryFactory
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Settings
name|charFilterSettings
init|=
name|charFiltersSettings
operator|.
name|get
argument_list|(
name|charFilterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|charFilterSettings
operator|==
literal|null
condition|)
block|{
name|charFilterSettings
operator|=
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
expr_stmt|;
block|}
name|CharFilterFactory
name|tokenFilterFactory
init|=
name|charFilterFactoryFactory
operator|.
name|create
argument_list|(
name|charFilterName
argument_list|,
name|charFilterSettings
argument_list|)
decl_stmt|;
name|charFilters
operator|.
name|put
argument_list|(
name|charFilterName
argument_list|,
name|tokenFilterFactory
argument_list|)
expr_stmt|;
name|charFilters
operator|.
name|put
argument_list|(
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|charFilterName
argument_list|)
argument_list|,
name|tokenFilterFactory
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indicesAnalysisService
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PreBuiltCharFilterFactoryFactory
argument_list|>
name|entry
range|:
name|indicesAnalysisService
operator|.
name|charFilterFactories
argument_list|()
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
if|if
condition|(
operator|!
name|charFilters
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|charFilters
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|name
operator|=
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|charFilters
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|charFilters
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|this
operator|.
name|charFilters
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|charFilters
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|TokenFilterFactory
argument_list|>
name|tokenFilters
init|=
name|newHashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokenFilterFactoryFactories
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|tokenFiltersSettings
init|=
name|indexSettings
operator|.
name|getGroups
argument_list|(
literal|"index.analysis.filter"
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
name|TokenFilterFactoryFactory
argument_list|>
name|entry
range|:
name|tokenFilterFactoryFactories
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|tokenFilterName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|TokenFilterFactoryFactory
name|tokenFilterFactoryFactory
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Settings
name|tokenFilterSettings
init|=
name|tokenFiltersSettings
operator|.
name|get
argument_list|(
name|tokenFilterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenFilterSettings
operator|==
literal|null
condition|)
block|{
name|tokenFilterSettings
operator|=
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
expr_stmt|;
block|}
name|TokenFilterFactory
name|tokenFilterFactory
init|=
name|tokenFilterFactoryFactory
operator|.
name|create
argument_list|(
name|tokenFilterName
argument_list|,
name|tokenFilterSettings
argument_list|)
decl_stmt|;
name|tokenFilters
operator|.
name|put
argument_list|(
name|tokenFilterName
argument_list|,
name|tokenFilterFactory
argument_list|)
expr_stmt|;
name|tokenFilters
operator|.
name|put
argument_list|(
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|tokenFilterName
argument_list|)
argument_list|,
name|tokenFilterFactory
argument_list|)
expr_stmt|;
block|}
block|}
comment|// pre initialize the globally registered ones into the map
if|if
condition|(
name|indicesAnalysisService
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PreBuiltTokenFilterFactoryFactory
argument_list|>
name|entry
range|:
name|indicesAnalysisService
operator|.
name|tokenFilterFactories
argument_list|()
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
if|if
condition|(
operator|!
name|tokenFilters
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|tokenFilters
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|name
operator|=
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|tokenFilters
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|tokenFilters
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|this
operator|.
name|tokenFilters
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|tokenFilters
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|AnalyzerProvider
argument_list|>
name|analyzerProviders
init|=
name|newHashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|analyzerFactoryFactories
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|analyzersSettings
init|=
name|indexSettings
operator|.
name|getGroups
argument_list|(
literal|"index.analysis.analyzer"
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
name|AnalyzerProviderFactory
argument_list|>
name|entry
range|:
name|analyzerFactoryFactories
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|analyzerName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|AnalyzerProviderFactory
name|analyzerFactoryFactory
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Settings
name|analyzerSettings
init|=
name|analyzersSettings
operator|.
name|get
argument_list|(
name|analyzerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzerSettings
operator|==
literal|null
condition|)
block|{
name|analyzerSettings
operator|=
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
expr_stmt|;
block|}
name|AnalyzerProvider
name|analyzerFactory
init|=
name|analyzerFactoryFactory
operator|.
name|create
argument_list|(
name|analyzerName
argument_list|,
name|analyzerSettings
argument_list|)
decl_stmt|;
name|analyzerProviders
operator|.
name|put
argument_list|(
name|analyzerName
argument_list|,
name|analyzerFactory
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indicesAnalysisService
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PreBuiltAnalyzerProviderFactory
argument_list|>
name|entry
range|:
name|indicesAnalysisService
operator|.
name|analyzerProviderFactories
argument_list|()
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
if|if
condition|(
operator|!
name|analyzerProviders
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|analyzerProviders
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|name
operator|=
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|analyzerProviders
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|analyzerProviders
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
operator|!
name|analyzerProviders
operator|.
name|containsKey
argument_list|(
literal|"default"
argument_list|)
condition|)
block|{
name|analyzerProviders
operator|.
name|put
argument_list|(
literal|"default"
argument_list|,
operator|new
name|StandardAnalyzerProvider
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
literal|null
argument_list|,
literal|"default"
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|analyzerProviders
operator|.
name|containsKey
argument_list|(
literal|"default_index"
argument_list|)
condition|)
block|{
name|analyzerProviders
operator|.
name|put
argument_list|(
literal|"default_index"
argument_list|,
name|analyzerProviders
operator|.
name|get
argument_list|(
literal|"default"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|analyzerProviders
operator|.
name|containsKey
argument_list|(
literal|"default_search"
argument_list|)
condition|)
block|{
name|analyzerProviders
operator|.
name|put
argument_list|(
literal|"default_search"
argument_list|,
name|analyzerProviders
operator|.
name|get
argument_list|(
literal|"default"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|NamedAnalyzer
argument_list|>
name|analyzers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AnalyzerProvider
name|analyzerFactory
range|:
name|analyzerProviders
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|analyzerFactory
operator|instanceof
name|CustomAnalyzerProvider
condition|)
block|{
operator|(
operator|(
name|CustomAnalyzerProvider
operator|)
name|analyzerFactory
operator|)
operator|.
name|build
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|NamedAnalyzer
name|analyzer
init|=
operator|new
name|NamedAnalyzer
argument_list|(
name|analyzerFactory
operator|.
name|name
argument_list|()
argument_list|,
name|analyzerFactory
operator|.
name|scope
argument_list|()
argument_list|,
name|analyzerFactory
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|analyzers
operator|.
name|put
argument_list|(
name|analyzerFactory
operator|.
name|name
argument_list|()
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|analyzers
operator|.
name|put
argument_list|(
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|analyzerFactory
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|String
name|strAliases
init|=
name|indexSettings
operator|.
name|get
argument_list|(
literal|"index.analysis.analyzer."
operator|+
name|analyzerFactory
operator|.
name|name
argument_list|()
operator|+
literal|".alias"
argument_list|)
decl_stmt|;
if|if
condition|(
name|strAliases
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|alias
range|:
name|Strings
operator|.
name|commaDelimitedListToStringArray
argument_list|(
name|strAliases
argument_list|)
control|)
block|{
name|analyzers
operator|.
name|put
argument_list|(
name|alias
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|aliases
init|=
name|indexSettings
operator|.
name|getAsArray
argument_list|(
literal|"index.analysis.analyzer."
operator|+
name|analyzerFactory
operator|.
name|name
argument_list|()
operator|+
literal|".alias"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|alias
range|:
name|aliases
control|)
block|{
name|analyzers
operator|.
name|put
argument_list|(
name|alias
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
block|}
name|defaultAnalyzer
operator|=
name|analyzers
operator|.
name|get
argument_list|(
literal|"default"
argument_list|)
expr_stmt|;
name|defaultIndexAnalyzer
operator|=
name|analyzers
operator|.
name|containsKey
argument_list|(
literal|"default_index"
argument_list|)
condition|?
name|analyzers
operator|.
name|get
argument_list|(
literal|"default_index"
argument_list|)
else|:
name|analyzers
operator|.
name|get
argument_list|(
literal|"default"
argument_list|)
expr_stmt|;
name|defaultSearchAnalyzer
operator|=
name|analyzers
operator|.
name|containsKey
argument_list|(
literal|"default_search"
argument_list|)
condition|?
name|analyzers
operator|.
name|get
argument_list|(
literal|"default_search"
argument_list|)
else|:
name|analyzers
operator|.
name|get
argument_list|(
literal|"default"
argument_list|)
expr_stmt|;
name|this
operator|.
name|analyzers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|analyzers
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|NamedAnalyzer
name|analyzer
range|:
name|analyzers
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|analyzer
operator|.
name|scope
argument_list|()
operator|==
name|AnalyzerScope
operator|.
name|INDEX
condition|)
block|{
try|try
block|{
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// because analyzers are aliased, they might be closed several times
comment|// an NPE is thrown in this case, so ignore....
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to close analyzer "
operator|+
name|analyzer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|analyzer
specifier|public
name|NamedAnalyzer
name|analyzer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|analyzers
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|defaultAnalyzer
specifier|public
name|NamedAnalyzer
name|defaultAnalyzer
parameter_list|()
block|{
return|return
name|defaultAnalyzer
return|;
block|}
DECL|method|defaultIndexAnalyzer
specifier|public
name|NamedAnalyzer
name|defaultIndexAnalyzer
parameter_list|()
block|{
return|return
name|defaultIndexAnalyzer
return|;
block|}
DECL|method|defaultSearchAnalyzer
specifier|public
name|NamedAnalyzer
name|defaultSearchAnalyzer
parameter_list|()
block|{
return|return
name|defaultSearchAnalyzer
return|;
block|}
DECL|method|tokenizer
specifier|public
name|TokenizerFactory
name|tokenizer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|tokenizers
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|charFilter
specifier|public
name|CharFilterFactory
name|charFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|charFilters
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|tokenFilter
specifier|public
name|TokenFilterFactory
name|tokenFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|tokenFilters
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

