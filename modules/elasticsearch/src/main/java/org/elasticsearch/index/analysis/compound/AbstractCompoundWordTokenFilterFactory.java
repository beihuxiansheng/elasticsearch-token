begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.analysis.compound
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
operator|.
name|compound
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
name|WordlistLoader
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
name|compound
operator|.
name|CompoundWordTokenFilterBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|analysis
operator|.
name|AbstractTokenFilterFactory
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
name|io
operator|.
name|File
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
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Contains the common configuration settings between subclasses of this class.  *  * @author Edward Dale (scompt@scompt.com)  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|AbstractCompoundWordTokenFilterFactory
specifier|public
specifier|abstract
class|class
name|AbstractCompoundWordTokenFilterFactory
extends|extends
name|AbstractTokenFilterFactory
block|{
DECL|field|minWordSize
specifier|protected
specifier|final
name|int
name|minWordSize
decl_stmt|;
DECL|field|minSubwordSize
specifier|protected
specifier|final
name|int
name|minSubwordSize
decl_stmt|;
DECL|field|maxSubwordSize
specifier|protected
specifier|final
name|int
name|maxSubwordSize
decl_stmt|;
DECL|field|onlyLongestMatch
specifier|protected
specifier|final
name|boolean
name|onlyLongestMatch
decl_stmt|;
DECL|field|wordList
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|wordList
decl_stmt|;
DECL|method|AbstractCompoundWordTokenFilterFactory
annotation|@
name|Inject
specifier|public
name|AbstractCompoundWordTokenFilterFactory
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
name|minWordSize
operator|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"min_word_size"
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|)
expr_stmt|;
name|minSubwordSize
operator|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"min_subword_size"
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|)
expr_stmt|;
name|maxSubwordSize
operator|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"max_subword_size"
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|)
expr_stmt|;
name|onlyLongestMatch
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"only_longest_max"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|wordListPath
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"word_list_path"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|wordListPath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"word_list_path is a required setting."
argument_list|)
throw|;
block|}
name|File
name|wordListFile
init|=
operator|new
name|File
argument_list|(
name|wordListPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|wordListFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"word_list_path file must exist."
argument_list|)
throw|;
block|}
try|try
block|{
name|wordList
operator|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|wordListFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"IOException while reading word_list_path: "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

