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
name|test
operator|.
name|ESTestCase
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|is
import|;
end_import

begin_class
DECL|class|HunspellTokenFilterFactoryTests
specifier|public
class|class
name|HunspellTokenFilterFactoryTests
extends|extends
name|ESTestCase
block|{
DECL|method|testDedup
specifier|public
name|void
name|testDedup
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
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
name|put
argument_list|(
name|Environment
operator|.
name|PATH_CONF_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|getDataPath
argument_list|(
literal|"/indices/analyze/conf_dir"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.en_US.type"
argument_list|,
literal|"hunspell"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.en_US.locale"
argument_list|,
literal|"en_US"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|TestAnalysis
name|analysis
init|=
name|AnalysisTestsHelper
operator|.
name|createTestAnalysisFromSettings
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|tokenFilter
init|=
name|analysis
operator|.
name|tokenFilter
operator|.
name|get
argument_list|(
literal|"en_US"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|HunspellTokenFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|HunspellTokenFilterFactory
name|hunspellTokenFilter
init|=
operator|(
name|HunspellTokenFilterFactory
operator|)
name|tokenFilter
decl_stmt|;
name|assertThat
argument_list|(
name|hunspellTokenFilter
operator|.
name|dedup
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|settings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
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
name|put
argument_list|(
name|Environment
operator|.
name|PATH_CONF_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|getDataPath
argument_list|(
literal|"/indices/analyze/conf_dir"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.en_US.type"
argument_list|,
literal|"hunspell"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.en_US.dedup"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.en_US.locale"
argument_list|,
literal|"en_US"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|analysis
operator|=
name|AnalysisTestsHelper
operator|.
name|createTestAnalysisFromSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|tokenFilter
operator|=
name|analysis
operator|.
name|tokenFilter
operator|.
name|get
argument_list|(
literal|"en_US"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tokenFilter
argument_list|,
name|instanceOf
argument_list|(
name|HunspellTokenFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|hunspellTokenFilter
operator|=
operator|(
name|HunspellTokenFilterFactory
operator|)
name|tokenFilter
expr_stmt|;
name|assertThat
argument_list|(
name|hunspellTokenFilter
operator|.
name|dedup
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

