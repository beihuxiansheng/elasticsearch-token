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
name|CharArraySet
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
name|ParseField
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
name|IndexSettings
import|;
end_import

begin_comment
comment|/**  * Builds an OpenRefine Fingerprint analyzer.  Uses the default settings from the various components  * (Standard Tokenizer and lowercase + stop + fingerprint + ascii-folding filters)  */
end_comment

begin_class
DECL|class|FingerprintAnalyzerProvider
specifier|public
class|class
name|FingerprintAnalyzerProvider
extends|extends
name|AbstractIndexAnalyzerProvider
argument_list|<
name|Analyzer
argument_list|>
block|{
DECL|field|MAX_OUTPUT_SIZE
specifier|public
specifier|static
name|ParseField
name|MAX_OUTPUT_SIZE
init|=
name|FingerprintTokenFilterFactory
operator|.
name|MAX_OUTPUT_SIZE
decl_stmt|;
DECL|field|DEFAULT_MAX_OUTPUT_SIZE
specifier|public
specifier|static
name|int
name|DEFAULT_MAX_OUTPUT_SIZE
init|=
name|FingerprintTokenFilterFactory
operator|.
name|DEFAULT_MAX_OUTPUT_SIZE
decl_stmt|;
DECL|field|DEFAULT_STOP_WORDS
specifier|public
specifier|static
name|CharArraySet
name|DEFAULT_STOP_WORDS
init|=
name|CharArraySet
operator|.
name|EMPTY_SET
decl_stmt|;
DECL|field|analyzer
specifier|private
specifier|final
name|FingerprintAnalyzer
name|analyzer
decl_stmt|;
DECL|method|FingerprintAnalyzerProvider
specifier|public
name|FingerprintAnalyzerProvider
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|Environment
name|env
parameter_list|,
name|String
name|name
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|indexSettings
argument_list|,
name|name
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|char
name|separator
init|=
name|FingerprintTokenFilterFactory
operator|.
name|parseSeparator
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|int
name|maxOutputSize
init|=
name|settings
operator|.
name|getAsInt
argument_list|(
name|MAX_OUTPUT_SIZE
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|DEFAULT_MAX_OUTPUT_SIZE
argument_list|)
decl_stmt|;
name|CharArraySet
name|stopWords
init|=
name|Analysis
operator|.
name|parseStopWords
argument_list|(
name|env
argument_list|,
name|indexSettings
operator|.
name|getIndexVersionCreated
argument_list|()
argument_list|,
name|settings
argument_list|,
name|DEFAULT_STOP_WORDS
argument_list|)
decl_stmt|;
name|this
operator|.
name|analyzer
operator|=
operator|new
name|FingerprintAnalyzer
argument_list|(
name|stopWords
argument_list|,
name|separator
argument_list|,
name|maxOutputSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|FingerprintAnalyzer
name|get
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
block|}
end_class

end_unit

