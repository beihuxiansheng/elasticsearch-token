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
name|standard
operator|.
name|StandardAnalyzer
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
name|standard
operator|.
name|UAX29URLEmailTokenizer
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
name|standard
operator|.
name|std40
operator|.
name|UAX29URLEmailTokenizer40
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
name|util
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
comment|/**  *  */
end_comment

begin_class
DECL|class|UAX29URLEmailTokenizerFactory
specifier|public
class|class
name|UAX29URLEmailTokenizerFactory
extends|extends
name|AbstractTokenizerFactory
block|{
DECL|field|maxTokenLength
specifier|private
specifier|final
name|int
name|maxTokenLength
decl_stmt|;
DECL|method|UAX29URLEmailTokenizerFactory
specifier|public
name|UAX29URLEmailTokenizerFactory
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|Environment
name|environment
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
name|maxTokenLength
operator|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"max_token_length"
argument_list|,
name|StandardAnalyzer
operator|.
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|Tokenizer
name|create
parameter_list|()
block|{
if|if
condition|(
name|version
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_4_7
argument_list|)
condition|)
block|{
name|UAX29URLEmailTokenizer
name|tokenizer
init|=
operator|new
name|UAX29URLEmailTokenizer
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|setMaxTokenLength
argument_list|(
name|maxTokenLength
argument_list|)
expr_stmt|;
return|return
name|tokenizer
return|;
block|}
else|else
block|{
name|UAX29URLEmailTokenizer40
name|tokenizer
init|=
operator|new
name|UAX29URLEmailTokenizer40
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|setMaxTokenLength
argument_list|(
name|maxTokenLength
argument_list|)
expr_stmt|;
return|return
name|tokenizer
return|;
block|}
block|}
block|}
end_class

end_unit

