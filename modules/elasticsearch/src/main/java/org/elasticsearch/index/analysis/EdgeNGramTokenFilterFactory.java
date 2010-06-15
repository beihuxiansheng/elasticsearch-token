begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ngram
operator|.
name|EdgeNGramTokenFilter
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
name|ngram
operator|.
name|EdgeNGramTokenizer
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
name|ngram
operator|.
name|NGramTokenFilter
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

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|EdgeNGramTokenFilterFactory
specifier|public
class|class
name|EdgeNGramTokenFilterFactory
extends|extends
name|AbstractTokenFilterFactory
block|{
DECL|field|minGram
specifier|private
specifier|final
name|int
name|minGram
decl_stmt|;
DECL|field|maxGram
specifier|private
specifier|final
name|int
name|maxGram
decl_stmt|;
DECL|field|side
specifier|private
specifier|final
name|EdgeNGramTokenFilter
operator|.
name|Side
name|side
decl_stmt|;
DECL|method|EdgeNGramTokenFilterFactory
annotation|@
name|Inject
specifier|public
name|EdgeNGramTokenFilterFactory
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|minGram
operator|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"min_gram"
argument_list|,
name|NGramTokenFilter
operator|.
name|DEFAULT_MIN_NGRAM_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxGram
operator|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"max_gram"
argument_list|,
name|NGramTokenFilter
operator|.
name|DEFAULT_MAX_NGRAM_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|side
operator|=
name|EdgeNGramTokenFilter
operator|.
name|Side
operator|.
name|getSide
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"side"
argument_list|,
name|EdgeNGramTokenizer
operator|.
name|DEFAULT_SIDE
operator|.
name|getLabel
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|create
annotation|@
name|Override
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
return|return
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|tokenStream
argument_list|,
name|side
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
return|;
block|}
block|}
end_class

end_unit

