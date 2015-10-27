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
name|Lucene43NGramTokenFilter
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
name|IndexSettings
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|NGramTokenFilterFactory
specifier|public
class|class
name|NGramTokenFilterFactory
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
annotation|@
name|Inject
DECL|method|NGramTokenFilterFactory
specifier|public
name|NGramTokenFilterFactory
parameter_list|(
name|IndexSettings
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
name|indexSettings
argument_list|,
name|name
argument_list|,
name|settings
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
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
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
specifier|final
name|Version
name|version
init|=
name|this
operator|.
name|version
operator|==
name|Version
operator|.
name|LUCENE_4_3
condition|?
name|Version
operator|.
name|LUCENE_4_4
else|:
name|this
operator|.
name|version
decl_stmt|;
comment|// we supported it since 4.3
if|if
condition|(
name|version
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_4_3
argument_list|)
condition|)
block|{
return|return
operator|new
name|NGramTokenFilter
argument_list|(
name|tokenStream
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Lucene43NGramTokenFilter
argument_list|(
name|tokenStream
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

