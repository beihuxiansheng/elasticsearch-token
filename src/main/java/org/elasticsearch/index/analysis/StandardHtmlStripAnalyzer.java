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
name|StopAnalyzer
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
name|StopFilter
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
name|StandardFilter
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
name|StandardTokenizer
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
name|util
operator|.
name|StopwordAnalyzerBase
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
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_class
DECL|class|StandardHtmlStripAnalyzer
specifier|public
class|class
name|StandardHtmlStripAnalyzer
extends|extends
name|StopwordAnalyzerBase
block|{
DECL|method|StandardHtmlStripAnalyzer
specifier|public
name|StandardHtmlStripAnalyzer
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
name|super
argument_list|(
name|version
argument_list|,
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|Reader
name|reader
parameter_list|)
block|{
specifier|final
name|StandardTokenizer
name|src
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|src
operator|.
name|setMaxTokenLength
argument_list|(
name|StandardAnalyzer
operator|.
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
name|TokenStream
name|tok
init|=
operator|new
name|StandardFilter
argument_list|(
name|matchVersion
argument_list|,
name|src
argument_list|)
decl_stmt|;
name|tok
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|tok
argument_list|)
expr_stmt|;
name|tok
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|tok
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|src
argument_list|,
name|tok
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|setReader
parameter_list|(
specifier|final
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|src
operator|.
name|setMaxTokenLength
argument_list|(
name|StandardAnalyzer
operator|.
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
name|super
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

