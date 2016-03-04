begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|IndexSettings
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
name|mapper
operator|.
name|core
operator|.
name|TextFieldMapper
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Closeable
block|{
DECL|field|analyzers
specifier|private
specifier|final
name|Map
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
name|Map
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
name|Map
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
name|Map
argument_list|<
name|String
argument_list|,
name|TokenFilterFactory
argument_list|>
name|tokenFilters
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
DECL|field|defaultSearchQuoteAnalyzer
specifier|private
specifier|final
name|NamedAnalyzer
name|defaultSearchQuoteAnalyzer
decl_stmt|;
DECL|method|AnalysisService
specifier|public
name|AnalysisService
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|AnalyzerProvider
argument_list|>
name|analyzerProviders
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TokenizerFactory
argument_list|>
name|tokenizerFactoryFactories
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CharFilterFactory
argument_list|>
name|charFilterFactoryFactories
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TokenFilterFactory
argument_list|>
name|tokenFilterFactoryFactories
parameter_list|)
block|{
name|super
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|tokenizers
operator|=
name|unmodifiableMap
argument_list|(
name|tokenizerFactoryFactories
argument_list|)
expr_stmt|;
name|this
operator|.
name|charFilters
operator|=
name|unmodifiableMap
argument_list|(
name|charFilterFactoryFactories
argument_list|)
expr_stmt|;
name|this
operator|.
name|tokenFilters
operator|=
name|unmodifiableMap
argument_list|(
name|tokenFilterFactoryFactories
argument_list|)
expr_stmt|;
name|analyzerProviders
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|analyzerProviders
argument_list|)
expr_stmt|;
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
name|indexSettings
argument_list|,
literal|null
argument_list|,
literal|"default"
argument_list|,
name|Settings
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
if|if
condition|(
operator|!
name|analyzerProviders
operator|.
name|containsKey
argument_list|(
literal|"default_search_quoted"
argument_list|)
condition|)
block|{
name|analyzerProviders
operator|.
name|put
argument_list|(
literal|"default_search_quoted"
argument_list|,
name|analyzerProviders
operator|.
name|get
argument_list|(
literal|"default_search"
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
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|AnalyzerProvider
argument_list|>
name|entry
range|:
name|analyzerProviders
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|AnalyzerProvider
name|analyzerFactory
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
comment|/*              * Lucene defaults positionIncrementGap to 0 in all analyzers but              * Elasticsearch defaults them to 0 only before version 2.0              * and 100 afterwards so we override the positionIncrementGap if it              * doesn't match here.              */
name|int
name|overridePositionIncrementGap
init|=
name|TextFieldMapper
operator|.
name|Defaults
operator|.
name|POSITION_INCREMENT_GAP
decl_stmt|;
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
comment|/*                  * Custom analyzers already default to the correct, version                  * dependent positionIncrementGap and the user is be able to                  * configure the positionIncrementGap directly on the analyzer so                  * we disable overriding the positionIncrementGap to preserve the                  * user's setting.                  */
name|overridePositionIncrementGap
operator|=
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
name|Analyzer
name|analyzerF
init|=
name|analyzerFactory
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|analyzerF
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"analyzer ["
operator|+
name|analyzerFactory
operator|.
name|name
argument_list|()
operator|+
literal|"] created null analyzer"
argument_list|)
throw|;
block|}
name|NamedAnalyzer
name|analyzer
decl_stmt|;
if|if
condition|(
name|analyzerF
operator|instanceof
name|NamedAnalyzer
condition|)
block|{
comment|// if we got a named analyzer back, use it...
name|analyzer
operator|=
operator|(
name|NamedAnalyzer
operator|)
name|analyzerF
expr_stmt|;
if|if
condition|(
name|overridePositionIncrementGap
operator|>=
literal|0
operator|&&
name|analyzer
operator|.
name|getPositionIncrementGap
argument_list|(
name|analyzer
operator|.
name|name
argument_list|()
argument_list|)
operator|!=
name|overridePositionIncrementGap
condition|)
block|{
comment|// unless the positionIncrementGap needs to be overridden
name|analyzer
operator|=
operator|new
name|NamedAnalyzer
argument_list|(
name|analyzer
argument_list|,
name|overridePositionIncrementGap
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|analyzer
operator|=
operator|new
name|NamedAnalyzer
argument_list|(
name|name
argument_list|,
name|analyzerFactory
operator|.
name|scope
argument_list|()
argument_list|,
name|analyzerF
argument_list|,
name|overridePositionIncrementGap
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|analyzers
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"already registered analyzer with name: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|analyzers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|String
name|strAliases
init|=
name|this
operator|.
name|indexSettings
operator|.
name|getSettings
argument_list|()
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
name|this
operator|.
name|indexSettings
operator|.
name|getSettings
argument_list|()
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
name|NamedAnalyzer
name|defaultAnalyzer
init|=
name|analyzers
operator|.
name|get
argument_list|(
literal|"default"
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultAnalyzer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no default analyzer configured"
argument_list|)
throw|;
block|}
if|if
condition|(
name|analyzers
operator|.
name|containsKey
argument_list|(
literal|"default_index"
argument_list|)
condition|)
block|{
specifier|final
name|Version
name|createdVersion
init|=
name|indexSettings
operator|.
name|getIndexVersionCreated
argument_list|()
decl_stmt|;
if|if
condition|(
name|createdVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_5_0_0
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"setting [index.analysis.analyzer.default_index] is not supported anymore, use [index.analysis.analyzer.default] instead for index ["
operator|+
name|index
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
else|else
block|{
name|deprecationLogger
operator|.
name|deprecated
argument_list|(
literal|"setting [index.analysis.analyzer.default_index] is deprecated, use [index.analysis.analyzer.default] instead for index [{}]"
argument_list|,
name|index
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|defaultAnalyzer
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
name|defaultAnalyzer
expr_stmt|;
name|defaultSearchQuoteAnalyzer
operator|=
name|analyzers
operator|.
name|containsKey
argument_list|(
literal|"default_search_quote"
argument_list|)
condition|?
name|analyzers
operator|.
name|get
argument_list|(
literal|"default_search_quote"
argument_list|)
else|:
name|defaultSearchAnalyzer
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedAnalyzer
argument_list|>
name|analyzer
range|:
name|analyzers
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|analyzer
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"analyzer name must not start with '_'. got \""
operator|+
name|analyzer
operator|.
name|getKey
argument_list|()
operator|+
literal|"\""
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|analyzers
operator|=
name|unmodifiableMap
argument_list|(
name|analyzers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
DECL|method|defaultSearchQuoteAnalyzer
specifier|public
name|NamedAnalyzer
name|defaultSearchQuoteAnalyzer
parameter_list|()
block|{
return|return
name|defaultSearchQuoteAnalyzer
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

