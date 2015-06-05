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
name|Collator
import|;
end_import

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
name|RuleBasedCollator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|util
operator|.
name|ULocale
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
name|core
operator|.
name|KeywordTokenizer
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
name|CharTermAttribute
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
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
operator|.
name|AnalysisTestUtils
operator|.
name|createAnalysisService
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

begin_comment
comment|// Tests borrowed from Solr's Icu collation key filter factory test.
end_comment

begin_class
DECL|class|SimpleIcuCollationTokenFilterTests
specifier|public
class|class
name|SimpleIcuCollationTokenFilterTests
extends|extends
name|ElasticsearchTestCase
block|{
comment|/*     * Turkish has some funny casing.     * This test shows how you can solve this kind of thing easily with collation.     * Instead of using LowerCaseFilter, use a turkish collator with primary strength.     * Then things will sort and match correctly.     */
annotation|@
name|Test
DECL|method|testBasicUsage
specifier|public
name|void
name|testBasicUsage
parameter_list|()
throws|throws
name|Exception
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
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.type"
argument_list|,
literal|"icu_collation"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.language"
argument_list|,
literal|"tr"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.strength"
argument_list|,
literal|"primary"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|filterFactory
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"myCollator"
argument_list|)
decl_stmt|;
name|assertCollatesToSame
argument_list|(
name|filterFactory
argument_list|,
literal|"I WÄ°LL USE TURKÄ°SH CASING"
argument_list|,
literal|"Ä± will use turkish casÄ±ng"
argument_list|)
expr_stmt|;
block|}
comment|/*     * Test usage of the decomposition option for unicode normalization.     */
annotation|@
name|Test
DECL|method|testNormalization
specifier|public
name|void
name|testNormalization
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
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.type"
argument_list|,
literal|"icu_collation"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.language"
argument_list|,
literal|"tr"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.strength"
argument_list|,
literal|"primary"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.decomposition"
argument_list|,
literal|"canonical"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|filterFactory
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"myCollator"
argument_list|)
decl_stmt|;
name|assertCollatesToSame
argument_list|(
name|filterFactory
argument_list|,
literal|"I W\u0049\u0307LL USE TURKÄ°SH CASING"
argument_list|,
literal|"Ä± will use turkish casÄ±ng"
argument_list|)
expr_stmt|;
block|}
comment|/*     * Test secondary strength, for english case is not significant.     */
annotation|@
name|Test
DECL|method|testSecondaryStrength
specifier|public
name|void
name|testSecondaryStrength
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
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.type"
argument_list|,
literal|"icu_collation"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.language"
argument_list|,
literal|"en"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.strength"
argument_list|,
literal|"secondary"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.decomposition"
argument_list|,
literal|"no"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|filterFactory
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"myCollator"
argument_list|)
decl_stmt|;
name|assertCollatesToSame
argument_list|(
name|filterFactory
argument_list|,
literal|"TESTING"
argument_list|,
literal|"testing"
argument_list|)
expr_stmt|;
block|}
comment|/*     * Setting alternate=shifted to shift whitespace, punctuation and symbols     * to quaternary level     */
annotation|@
name|Test
DECL|method|testIgnorePunctuation
specifier|public
name|void
name|testIgnorePunctuation
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
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.type"
argument_list|,
literal|"icu_collation"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.language"
argument_list|,
literal|"en"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.strength"
argument_list|,
literal|"primary"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.alternate"
argument_list|,
literal|"shifted"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|filterFactory
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"myCollator"
argument_list|)
decl_stmt|;
name|assertCollatesToSame
argument_list|(
name|filterFactory
argument_list|,
literal|"foo-bar"
argument_list|,
literal|"foo bar"
argument_list|)
expr_stmt|;
block|}
comment|/*     * Setting alternate=shifted and variableTop to shift whitespace, but not     * punctuation or symbols, to quaternary level     */
annotation|@
name|Test
DECL|method|testIgnoreWhitespace
specifier|public
name|void
name|testIgnoreWhitespace
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
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.type"
argument_list|,
literal|"icu_collation"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.language"
argument_list|,
literal|"en"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.strength"
argument_list|,
literal|"primary"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.alternate"
argument_list|,
literal|"shifted"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.variableTop"
argument_list|,
literal|" "
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|filterFactory
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"myCollator"
argument_list|)
decl_stmt|;
name|assertCollatesToSame
argument_list|(
name|filterFactory
argument_list|,
literal|"foo bar"
argument_list|,
literal|"foobar"
argument_list|)
expr_stmt|;
comment|// now assert that punctuation still matters: foo-bar< foo bar
name|assertCollation
argument_list|(
name|filterFactory
argument_list|,
literal|"foo-bar"
argument_list|,
literal|"foo bar"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/*     * Setting numeric to encode digits with numeric value, so that     * foobar-9 sorts before foobar-10     */
annotation|@
name|Test
DECL|method|testNumerics
specifier|public
name|void
name|testNumerics
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
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.type"
argument_list|,
literal|"icu_collation"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.language"
argument_list|,
literal|"en"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.numeric"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|filterFactory
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"myCollator"
argument_list|)
decl_stmt|;
name|assertCollation
argument_list|(
name|filterFactory
argument_list|,
literal|"foobar-9"
argument_list|,
literal|"foobar-10"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/*     * Setting caseLevel=true to create an additional case level between     * secondary and tertiary     */
annotation|@
name|Test
DECL|method|testIgnoreAccentsButNotCase
specifier|public
name|void
name|testIgnoreAccentsButNotCase
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
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.type"
argument_list|,
literal|"icu_collation"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.language"
argument_list|,
literal|"en"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.strength"
argument_list|,
literal|"primary"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.caseLevel"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|filterFactory
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"myCollator"
argument_list|)
decl_stmt|;
name|assertCollatesToSame
argument_list|(
name|filterFactory
argument_list|,
literal|"rÃ©sumÃ©"
argument_list|,
literal|"resume"
argument_list|)
expr_stmt|;
name|assertCollatesToSame
argument_list|(
name|filterFactory
argument_list|,
literal|"RÃ©sumÃ©"
argument_list|,
literal|"Resume"
argument_list|)
expr_stmt|;
comment|// now assert that case still matters: resume< Resume
name|assertCollation
argument_list|(
name|filterFactory
argument_list|,
literal|"resume"
argument_list|,
literal|"Resume"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/*     * Setting caseFirst=upper to cause uppercase strings to sort     * before lowercase ones.     */
annotation|@
name|Test
DECL|method|testUpperCaseFirst
specifier|public
name|void
name|testUpperCaseFirst
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
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.type"
argument_list|,
literal|"icu_collation"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.language"
argument_list|,
literal|"en"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.strength"
argument_list|,
literal|"tertiary"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.caseFirst"
argument_list|,
literal|"upper"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|filterFactory
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"myCollator"
argument_list|)
decl_stmt|;
name|assertCollation
argument_list|(
name|filterFactory
argument_list|,
literal|"Resume"
argument_list|,
literal|"resume"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/*     * For german, you might want oe to sort and match with o umlaut.     * This is not the default, but you can make a customized ruleset to do this.     *     * The default is DIN 5007-1, this shows how to tailor a collator to get DIN 5007-2 behavior.     *  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4423383     */
annotation|@
name|Test
DECL|method|testCustomRules
specifier|public
name|void
name|testCustomRules
parameter_list|()
throws|throws
name|Exception
block|{
name|RuleBasedCollator
name|baseCollator
init|=
operator|(
name|RuleBasedCollator
operator|)
name|Collator
operator|.
name|getInstance
argument_list|(
operator|new
name|ULocale
argument_list|(
literal|"de_DE"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|DIN5007_2_tailorings
init|=
literal|"& ae , a\u0308& AE , A\u0308"
operator|+
literal|"& oe , o\u0308& OE , O\u0308"
operator|+
literal|"& ue , u\u0308& UE , u\u0308"
decl_stmt|;
name|RuleBasedCollator
name|tailoredCollator
init|=
operator|new
name|RuleBasedCollator
argument_list|(
name|baseCollator
operator|.
name|getRules
argument_list|()
operator|+
name|DIN5007_2_tailorings
argument_list|)
decl_stmt|;
name|String
name|tailoredRules
init|=
name|tailoredCollator
operator|.
name|getRules
argument_list|()
decl_stmt|;
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
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.type"
argument_list|,
literal|"icu_collation"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.rules"
argument_list|,
name|tailoredRules
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.analysis.filter.myCollator.strength"
argument_list|,
literal|"primary"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AnalysisService
name|analysisService
init|=
name|createAnalysisService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|filterFactory
init|=
name|analysisService
operator|.
name|tokenFilter
argument_list|(
literal|"myCollator"
argument_list|)
decl_stmt|;
name|assertCollatesToSame
argument_list|(
name|filterFactory
argument_list|,
literal|"TÃ¶ne"
argument_list|,
literal|"Toene"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCollatesToSame
specifier|private
name|void
name|assertCollatesToSame
parameter_list|(
name|TokenFilterFactory
name|factory
parameter_list|,
name|String
name|string1
parameter_list|,
name|String
name|string2
parameter_list|)
throws|throws
name|IOException
block|{
name|assertCollation
argument_list|(
name|factory
argument_list|,
name|string1
argument_list|,
name|string2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCollation
specifier|private
name|void
name|assertCollation
parameter_list|(
name|TokenFilterFactory
name|factory
parameter_list|,
name|String
name|string1
parameter_list|,
name|String
name|string2
parameter_list|,
name|int
name|comparison
parameter_list|)
throws|throws
name|IOException
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|string1
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|stream1
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|tokenizer
operator|=
operator|new
name|KeywordTokenizer
argument_list|()
expr_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|string2
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|stream2
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertCollation
argument_list|(
name|stream1
argument_list|,
name|stream2
argument_list|,
name|comparison
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCollation
specifier|private
name|void
name|assertCollation
parameter_list|(
name|TokenStream
name|stream1
parameter_list|,
name|TokenStream
name|stream2
parameter_list|,
name|int
name|comparison
parameter_list|)
throws|throws
name|IOException
block|{
name|CharTermAttribute
name|term1
init|=
name|stream1
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|term2
init|=
name|stream2
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|stream1
operator|.
name|reset
argument_list|()
expr_stmt|;
name|stream2
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|stream1
operator|.
name|incrementToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stream2
operator|.
name|incrementToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Integer
operator|.
name|signum
argument_list|(
name|term1
operator|.
name|toString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|term2
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Integer
operator|.
name|signum
argument_list|(
name|comparison
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stream1
operator|.
name|incrementToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stream2
operator|.
name|incrementToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|stream1
operator|.
name|end
argument_list|()
expr_stmt|;
name|stream2
operator|.
name|end
argument_list|()
expr_stmt|;
name|stream1
operator|.
name|close
argument_list|()
expr_stmt|;
name|stream2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

