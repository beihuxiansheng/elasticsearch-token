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
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Normalizer2
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
name|CharFilter
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
name|plugin
operator|.
name|analysis
operator|.
name|icu
operator|.
name|AnalysisICUPlugin
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
name|StringReader
import|;
end_import

begin_comment
comment|/**  * Test  */
end_comment

begin_class
DECL|class|SimpleIcuNormalizerCharFilterTests
specifier|public
class|class
name|SimpleIcuNormalizerCharFilterTests
extends|extends
name|ESTestCase
block|{
DECL|method|testDefaultSetting
specifier|public
name|void
name|testDefaultSetting
parameter_list|()
throws|throws
name|Exception
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
literal|"index.analysis.char_filter.myNormalizerChar.type"
argument_list|,
literal|"icu_normalizer"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|TestAnalysis
name|analysis
init|=
name|createTestAnalysis
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|,
literal|"_na_"
argument_list|)
argument_list|,
name|settings
argument_list|,
operator|new
name|AnalysisICUPlugin
argument_list|()
argument_list|)
decl_stmt|;
name|CharFilterFactory
name|charFilterFactory
init|=
name|analysis
operator|.
name|charFilter
operator|.
name|get
argument_list|(
literal|"myNormalizerChar"
argument_list|)
decl_stmt|;
name|String
name|input
init|=
literal|"Ê°ã°ã5ââã±ãï¼ãããã¡ã¼ã®æ­£è¦åã®ãã¹ãï¼ãããããï½¶ï½·ï½¸ï½¹ï½ºï½»ï¾ï½¼ï¾ï½½ï¾ï½¾ï¾ï½¿ï¾gÌê°/ê°à®¨à®¿à¹à¸à¤·à¤¿chkÊ·à¤à¥à¤·à¤¿"
decl_stmt|;
name|Normalizer2
name|normalizer
init|=
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc_cf"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
decl_stmt|;
name|String
name|expectedOutput
init|=
name|normalizer
operator|.
name|normalize
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|CharFilter
name|inputReader
init|=
operator|(
name|CharFilter
operator|)
name|charFilterFactory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|char
index|[]
name|tempBuff
init|=
operator|new
name|char
index|[
literal|10
index|]
decl_stmt|;
name|StringBuilder
name|output
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|length
init|=
name|inputReader
operator|.
name|read
argument_list|(
name|tempBuff
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|==
operator|-
literal|1
condition|)
break|break;
name|output
operator|.
name|append
argument_list|(
name|tempBuff
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|output
operator|.
name|toString
argument_list|()
argument_list|,
name|normalizer
operator|.
name|normalize
argument_list|(
name|input
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|inputReader
operator|.
name|correctOffset
argument_list|(
name|output
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedOutput
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNameAndModeSetting
specifier|public
name|void
name|testNameAndModeSetting
parameter_list|()
throws|throws
name|Exception
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
literal|"index.analysis.char_filter.myNormalizerChar.type"
argument_list|,
literal|"icu_normalizer"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.char_filter.myNormalizerChar.name"
argument_list|,
literal|"nfkc"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.char_filter.myNormalizerChar.mode"
argument_list|,
literal|"decompose"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|TestAnalysis
name|analysis
init|=
name|createTestAnalysis
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|,
literal|"_na_"
argument_list|)
argument_list|,
name|settings
argument_list|,
operator|new
name|AnalysisICUPlugin
argument_list|()
argument_list|)
decl_stmt|;
name|CharFilterFactory
name|charFilterFactory
init|=
name|analysis
operator|.
name|charFilter
operator|.
name|get
argument_list|(
literal|"myNormalizerChar"
argument_list|)
decl_stmt|;
name|String
name|input
init|=
literal|"Ê°ã°ã5ââã±ãï¼ãããã¡ã¼ã®æ­£è¦åã®ãã¹ãï¼ãããããï½¶ï½·ï½¸ï½¹ï½ºï½»ï¾ï½¼ï¾ï½½ï¾ï½¾ï¾ï½¿ï¾gÌê°/ê°à®¨à®¿à¹à¸à¤·à¤¿chkÊ·à¤à¥à¤·à¤¿"
decl_stmt|;
name|Normalizer2
name|normalizer
init|=
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|DECOMPOSE
argument_list|)
decl_stmt|;
name|String
name|expectedOutput
init|=
name|normalizer
operator|.
name|normalize
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|CharFilter
name|inputReader
init|=
operator|(
name|CharFilter
operator|)
name|charFilterFactory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|char
index|[]
name|tempBuff
init|=
operator|new
name|char
index|[
literal|10
index|]
decl_stmt|;
name|StringBuilder
name|output
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|length
init|=
name|inputReader
operator|.
name|read
argument_list|(
name|tempBuff
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|==
operator|-
literal|1
condition|)
break|break;
name|output
operator|.
name|append
argument_list|(
name|tempBuff
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|output
operator|.
name|toString
argument_list|()
argument_list|,
name|normalizer
operator|.
name|normalize
argument_list|(
name|input
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|inputReader
operator|.
name|correctOffset
argument_list|(
name|output
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedOutput
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

