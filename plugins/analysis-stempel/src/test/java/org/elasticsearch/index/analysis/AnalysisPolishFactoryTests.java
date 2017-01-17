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
name|BaseTokenStreamTestCase
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
name|MockTokenizer
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
name|TokenFilter
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
name|elasticsearch
operator|.
name|AnalysisFactoryTestCase
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
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|UUIDs
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
name|pl
operator|.
name|PolishStemTokenFilterFactory
import|;
end_import

begin_class
DECL|class|AnalysisPolishFactoryTests
specifier|public
class|class
name|AnalysisPolishFactoryTests
extends|extends
name|AnalysisFactoryTestCase
block|{
annotation|@
name|Override
DECL|method|getTokenFilters
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|getTokenFilters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|filters
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|super
operator|.
name|getTokenFilters
argument_list|()
argument_list|)
decl_stmt|;
name|filters
operator|.
name|put
argument_list|(
literal|"stempelpolishstem"
argument_list|,
name|PolishStemTokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|filters
return|;
block|}
DECL|method|testThreadSafety
specifier|public
name|void
name|testThreadSafety
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: is this the right boilerplate?  I forked this out of TransportAnalyzeAction.java:
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
comment|// for _na_
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_INDEX_UUID
argument_list|,
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Environment
operator|.
name|PATH_HOME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Environment
name|environment
init|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|IndexMetaData
name|metaData
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|IndexMetaData
operator|.
name|INDEX_UUID_NA_VALUE
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexSettings
name|indexSettings
init|=
operator|new
name|IndexSettings
argument_list|(
name|metaData
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|testThreadSafety
argument_list|(
operator|new
name|PolishStemTokenFilterFactory
argument_list|(
name|indexSettings
argument_list|,
name|environment
argument_list|,
literal|"stempelpolishstem"
argument_list|,
name|settings
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// TODO: move to AnalysisFactoryTestCase so we can more easily test thread safety for all factories
DECL|method|testThreadSafety
specifier|private
name|void
name|testThreadSafety
parameter_list|(
name|TokenFilterFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
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
operator|new
name|MockTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

