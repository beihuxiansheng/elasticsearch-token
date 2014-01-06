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
name|java
operator|.
name|util
operator|.
name|List
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
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_comment
comment|/**  * A custom analyzer that is built out of a single {@link org.apache.lucene.analysis.Tokenizer} and a list  * of {@link org.apache.lucene.analysis.TokenFilter}s.  */
end_comment

begin_class
DECL|class|CustomAnalyzerProvider
specifier|public
class|class
name|CustomAnalyzerProvider
extends|extends
name|AbstractIndexAnalyzerProvider
argument_list|<
name|CustomAnalyzer
argument_list|>
block|{
DECL|field|analyzerSettings
specifier|private
specifier|final
name|Settings
name|analyzerSettings
decl_stmt|;
DECL|field|customAnalyzer
specifier|private
name|CustomAnalyzer
name|customAnalyzer
decl_stmt|;
annotation|@
name|Inject
DECL|method|CustomAnalyzerProvider
specifier|public
name|CustomAnalyzerProvider
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
name|index
argument_list|,
name|indexSettings
argument_list|,
name|name
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|analyzerSettings
operator|=
name|settings
expr_stmt|;
block|}
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|AnalysisService
name|analysisService
parameter_list|)
block|{
name|String
name|tokenizerName
init|=
name|analyzerSettings
operator|.
name|get
argument_list|(
literal|"tokenizer"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenizerName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Custom Analyzer ["
operator|+
name|name
argument_list|()
operator|+
literal|"] must be configured with a tokenizer"
argument_list|)
throw|;
block|}
name|TokenizerFactory
name|tokenizer
init|=
name|analysisService
operator|.
name|tokenizer
argument_list|(
name|tokenizerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenizer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Custom Analyzer ["
operator|+
name|name
argument_list|()
operator|+
literal|"] failed to find tokenizer under name ["
operator|+
name|tokenizerName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|CharFilterFactory
argument_list|>
name|charFilters
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|String
index|[]
name|charFilterNames
init|=
name|analyzerSettings
operator|.
name|getAsArray
argument_list|(
literal|"char_filter"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|charFilterName
range|:
name|charFilterNames
control|)
block|{
name|CharFilterFactory
name|charFilter
init|=
name|analysisService
operator|.
name|charFilter
argument_list|(
name|charFilterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|charFilter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Custom Analyzer ["
operator|+
name|name
argument_list|()
operator|+
literal|"] failed to find char_filter under name ["
operator|+
name|charFilterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|charFilters
operator|.
name|add
argument_list|(
name|charFilter
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|TokenFilterFactory
argument_list|>
name|tokenFilters
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|String
index|[]
name|tokenFilterNames
init|=
name|analyzerSettings
operator|.
name|getAsArray
argument_list|(
literal|"filter"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|tokenFilterName
range|:
name|tokenFilterNames
control|)
block|{
name|TokenFilterFactory
name|tokenFilter
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
name|tokenFilterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenFilter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Custom Analyzer ["
operator|+
name|name
argument_list|()
operator|+
literal|"] failed to find filter under name ["
operator|+
name|tokenFilterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|tokenFilters
operator|.
name|add
argument_list|(
name|tokenFilter
argument_list|)
expr_stmt|;
block|}
name|int
name|positionOffsetGap
init|=
name|analyzerSettings
operator|.
name|getAsInt
argument_list|(
literal|"position_offset_gap"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|offsetGap
init|=
name|analyzerSettings
operator|.
name|getAsInt
argument_list|(
literal|"offset_gap"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|this
operator|.
name|customAnalyzer
operator|=
operator|new
name|CustomAnalyzer
argument_list|(
name|tokenizer
argument_list|,
name|charFilters
operator|.
name|toArray
argument_list|(
operator|new
name|CharFilterFactory
index|[
name|charFilters
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|tokenFilters
operator|.
name|toArray
argument_list|(
operator|new
name|TokenFilterFactory
index|[
name|tokenFilters
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|positionOffsetGap
argument_list|,
name|offsetGap
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|CustomAnalyzer
name|get
parameter_list|()
block|{
return|return
name|this
operator|.
name|customAnalyzer
return|;
block|}
block|}
end_class

end_unit

