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
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|Tokenizer
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
name|analysis
operator|.
name|core
operator|.
name|LowerCaseFilter
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
name|analysis
operator|.
name|core
operator|.
name|WhitespaceTokenizer
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
name|analysis
operator|.
name|synonym
operator|.
name|SolrSynonymParser
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
name|analysis
operator|.
name|synonym
operator|.
name|SynonymFilter
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
name|analysis
operator|.
name|synonym
operator|.
name|SynonymMap
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
name|analysis
operator|.
name|synonym
operator|.
name|WordnetSynonymParser
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
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
name|io
operator|.
name|FastStringReader
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
name|env
operator|.
name|Environment
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
name|io
operator|.
name|Reader
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
annotation|@
name|AnalysisSettingsRequired
DECL|class|SynonymTokenFilterFactory
specifier|public
class|class
name|SynonymTokenFilterFactory
extends|extends
name|AbstractTokenFilterFactory
block|{
DECL|field|synonymMap
specifier|private
specifier|final
name|SynonymMap
name|synonymMap
decl_stmt|;
DECL|field|ignoreCase
specifier|private
specifier|final
name|boolean
name|ignoreCase
decl_stmt|;
annotation|@
name|Inject
DECL|method|SynonymTokenFilterFactory
specifier|public
name|SynonymTokenFilterFactory
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|Environment
name|env
parameter_list|,
name|IndicesAnalysisService
name|indicesAnalysisService
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TokenizerFactoryFactory
argument_list|>
name|tokenizerFactories
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
name|Reader
name|rulesReader
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"synonyms"
argument_list|,
literal|null
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|rules
init|=
name|Analysis
operator|.
name|getWordList
argument_list|(
name|env
argument_list|,
name|settings
argument_list|,
literal|"synonyms"
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|rules
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rulesReader
operator|=
operator|new
name|FastStringReader
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|settings
operator|.
name|get
argument_list|(
literal|"synonyms_path"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|rulesReader
operator|=
name|Analysis
operator|.
name|getReaderFromFile
argument_list|(
name|env
argument_list|,
name|settings
argument_list|,
literal|"synonyms_path"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"synonym requires either `synonyms` or `synonyms_path` to be configured"
argument_list|)
throw|;
block|}
name|this
operator|.
name|ignoreCase
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"ignore_case"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|boolean
name|expand
init|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"expand"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|tokenizerName
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"tokenizer"
argument_list|,
literal|"whitespace"
argument_list|)
decl_stmt|;
name|TokenizerFactoryFactory
name|tokenizerFactoryFactory
init|=
name|tokenizerFactories
operator|.
name|get
argument_list|(
name|tokenizerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenizerFactoryFactory
operator|==
literal|null
condition|)
block|{
name|tokenizerFactoryFactory
operator|=
name|indicesAnalysisService
operator|.
name|tokenizerFactoryFactory
argument_list|(
name|tokenizerName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tokenizerFactoryFactory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"failed to find tokenizer ["
operator|+
name|tokenizerName
operator|+
literal|"] for synonym token filter"
argument_list|)
throw|;
block|}
specifier|final
name|TokenizerFactory
name|tokenizerFactory
init|=
name|tokenizerFactoryFactory
operator|.
name|create
argument_list|(
name|tokenizerName
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|indexSettings
argument_list|)
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
name|tokenizerFactory
operator|==
literal|null
condition|?
operator|new
name|WhitespaceTokenizer
argument_list|()
else|:
name|tokenizerFactory
operator|.
name|create
argument_list|()
decl_stmt|;
name|TokenStream
name|stream
init|=
name|ignoreCase
condition|?
operator|new
name|LowerCaseFilter
argument_list|(
name|tokenizer
argument_list|)
else|:
name|tokenizer
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|stream
argument_list|)
return|;
block|}
block|}
decl_stmt|;
try|try
block|{
name|SynonymMap
operator|.
name|Builder
name|parser
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|"wordnet"
operator|.
name|equalsIgnoreCase
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"format"
argument_list|)
argument_list|)
condition|)
block|{
name|parser
operator|=
operator|new
name|WordnetSynonymParser
argument_list|(
literal|true
argument_list|,
name|expand
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
operator|(
operator|(
name|WordnetSynonymParser
operator|)
name|parser
operator|)
operator|.
name|parse
argument_list|(
name|rulesReader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parser
operator|=
operator|new
name|SolrSynonymParser
argument_list|(
literal|true
argument_list|,
name|expand
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
operator|(
operator|(
name|SolrSynonymParser
operator|)
name|parser
operator|)
operator|.
name|parse
argument_list|(
name|rulesReader
argument_list|)
expr_stmt|;
block|}
name|synonymMap
operator|=
name|parser
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"failed to build synonyms"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
comment|// fst is null means no synonyms
return|return
name|synonymMap
operator|.
name|fst
operator|==
literal|null
condition|?
name|tokenStream
else|:
operator|new
name|SynonymFilter
argument_list|(
name|tokenStream
argument_list|,
name|synonymMap
argument_list|,
name|ignoreCase
argument_list|)
return|;
block|}
block|}
end_class

end_unit

