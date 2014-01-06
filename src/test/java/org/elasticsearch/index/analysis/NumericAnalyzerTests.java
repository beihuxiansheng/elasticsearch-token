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
name|NumericTokenStream
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
name|NumericTokenStream
operator|.
name|NumericTermAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|ElasticsearchTestCase
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
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
DECL|class|NumericAnalyzerTests
specifier|public
class|class
name|NumericAnalyzerTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testAttributeEqual
specifier|public
name|void
name|testAttributeEqual
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|precisionStep
init|=
literal|8
decl_stmt|;
specifier|final
name|double
name|value
init|=
name|randomDouble
argument_list|()
decl_stmt|;
name|NumericDoubleAnalyzer
name|analyzer
init|=
operator|new
name|NumericDoubleAnalyzer
argument_list|(
name|precisionStep
argument_list|)
decl_stmt|;
specifier|final
name|TokenStream
name|ts1
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|NumericTokenStream
name|ts2
init|=
operator|new
name|NumericTokenStream
argument_list|(
name|precisionStep
argument_list|)
decl_stmt|;
name|ts2
operator|.
name|setDoubleValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
specifier|final
name|NumericTermAttribute
name|numTerm1
init|=
name|ts1
operator|.
name|addAttribute
argument_list|(
name|NumericTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|NumericTermAttribute
name|numTerm2
init|=
name|ts1
operator|.
name|addAttribute
argument_list|(
name|NumericTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|PositionIncrementAttribute
name|posInc1
init|=
name|ts1
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|PositionIncrementAttribute
name|posInc2
init|=
name|ts1
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|ts1
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ts2
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|ts1
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|assertThat
argument_list|(
name|ts2
operator|.
name|incrementToken
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|posInc1
argument_list|,
name|equalTo
argument_list|(
name|posInc2
argument_list|)
argument_list|)
expr_stmt|;
comment|// can't use equalTo directly on the numeric attribute cause it doesn't implement equals (LUCENE-5070)
name|assertThat
argument_list|(
name|numTerm1
operator|.
name|getRawValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numTerm2
operator|.
name|getRawValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|numTerm2
operator|.
name|getShift
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numTerm2
operator|.
name|getShift
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|ts2
operator|.
name|incrementToken
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|ts1
operator|.
name|end
argument_list|()
expr_stmt|;
name|ts2
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

