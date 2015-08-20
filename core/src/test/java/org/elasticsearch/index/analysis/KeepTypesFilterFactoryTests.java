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
name|StandardTokenizer
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
name|test
operator|.
name|ESTokenStreamTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|StringReader
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|instanceOf
import|;
end_import

begin_class
DECL|class|KeepTypesFilterFactoryTests
specifier|public
class|class
name|KeepTypesFilterFactoryTests
extends|extends
name|ESTokenStreamTestCase
block|{
annotation|@
name|Test
DECL|method|testKeepTypes
specifier|public
name|void
name|testKeepTypes
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.keep_numbers.type"
argument_list|,
literal|"keep_types"
argument_list|)
operator|.
name|putArray
argument_list|(
literal|"index.analysis.filter.keep_numbers.types"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<NUM>"
block|,
literal|"<SOMETHINGELSE>"
block|}
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|AnalysisTestsHelper
operator|.
name|createAnalysisServiceFromSettings
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|tokenFilter
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"keep_numbers"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|KeepTypesFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|source
init|=
literal|"Hello 123 world"
decl_stmt|;
name|String
index|[]
name|expected
init|=
operator|new
name|String
index|[]
block|{
literal|"123"
block|}
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|StandardTokenizer
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenFilter
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
argument_list|,
name|expected
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
