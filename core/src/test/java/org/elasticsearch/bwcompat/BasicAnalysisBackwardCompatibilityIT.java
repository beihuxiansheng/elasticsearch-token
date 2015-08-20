begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bwcompat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bwcompat
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
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
name|util
operator|.
name|TestUtil
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
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|analyze
operator|.
name|AnalyzeResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|analysis
operator|.
name|PreBuiltAnalyzers
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
name|ESBackcompatTestCase
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
name|ESIntegTestCase
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
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

begin_class
annotation|@
name|ESIntegTestCase
operator|.
name|ClusterScope
argument_list|(
name|numDataNodes
operator|=
literal|0
argument_list|,
name|scope
operator|=
name|ESIntegTestCase
operator|.
name|Scope
operator|.
name|SUITE
argument_list|,
name|numClientNodes
operator|=
literal|0
argument_list|,
name|transportClientRatio
operator|=
literal|0.0
argument_list|)
DECL|class|BasicAnalysisBackwardCompatibilityIT
specifier|public
class|class
name|BasicAnalysisBackwardCompatibilityIT
extends|extends
name|ESBackcompatTestCase
block|{
comment|// This pattern match characters with Line_Break = Complex_Content.
DECL|field|complexUnicodeChars
specifier|final
specifier|static
name|Pattern
name|complexUnicodeChars
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[\u17B4\u17B5\u17D3\u17CB-\u17D1\u17DD\u1036\u17C6\u1A74\u1038\u17C7\u0E4E\u0E47-\u0E4D\u0EC8-\u0ECD\uAABF\uAAC1\u1037\u17C8-\u17CA\u1A75-\u1A7C\u1AA8-\u1AAB\uAADE\uAADF\u1AA0-\u1AA6\u1AAC\u1AAD\u109E\u109F\uAA77-\uAA79\u0E46\u0EC6\u17D7\u1AA7\uA9E6\uAA70\uAADD\u19DA\u0E01-\u0E3A\u0E40-\u0E45\u0EDE\u0E81\u0E82\u0E84\u0E87\u0E88\u0EAA\u0E8A\u0EDF\u0E8D\u0E94-\u0E97\u0E99-\u0E9F\u0EA1-\u0EA3\u0EA5\u0EA7\u0EAB\u0EDC\u0EDD\u0EAD-\u0EB9\u0EBB-\u0EBD\u0EC0-\u0EC4\uAA80-\uAABE\uAAC0\uAAC2\uAADB\uAADC\u1000\u1075\u1001\u1076\u1002\u1077\uAA60\uA9E9\u1003\uA9E0\uA9EA\u1004\u105A\u1005\u1078\uAA61\u1006\uA9E1\uAA62\uAA7E\u1007\uAA63\uA9EB\u1079\uAA72\u1008\u105B\uA9E2\uAA64\uA9EC\u1061\uAA7F\u1009\u107A\uAA65\uA9E7\u100A\u100B\uAA66\u100C\uAA67\u100D\uAA68\uA9ED\u100E\uAA69\uA9EE\u100F\u106E\uA9E3\uA9EF\u1010-\u1012\u107B\uA9FB\u1013\uAA6A\uA9FC\u1014\u107C\uAA6B\u105E\u1015\u1016\u107D\u107E\uAA6F\u108E\uA9E8\u1017\u107F\uA9FD\u1018\uA9E4\uA9FE\u1019\u105F\u101A\u103B\u101B\uAA73\uAA7A\u103C\u101C\u1060\u101D\u103D\u1082\u1080\u1050\u1051\u1065\u101E\u103F\uAA6C\u101F\u1081\uAA6D\u103E\uAA6E\uAA71\u1020\uA9FA\u105C\u105D\u106F\u1070\u1066\u1021-\u1026\u1052-\u1055\u1027-\u102A\u102C\u102B\u1083\u1072\u109C\u102D\u1071\u102E\u1033\u102F\u1073\u1074\u1030\u1056-\u1059\u1031\u1084\u1035\u1085\u1032\u109D\u1034\u1062\u1067\u1068\uA9E5\u1086\u1039\u103A\u1063\u1064\u1069-\u106D\u1087\u108B\u1088\u108C\u108D\u1089\u108A\u108F\u109A\u109B\uAA7B-\uAA7D\uAA74-\uAA76\u1780-\u17A2\u17DC\u17A3-\u17B3\u17B6-\u17C5\u17D2\u1950-\u196D\u1970-\u1974\u1980-\u199C\u19DE\u19DF\u199D-\u19AB\u19B0-\u19C9\u1A20-\u1A26\u1A58\u1A59\u1A27-\u1A3B\u1A5A\u1A5B\u1A3C-\u1A46\u1A54\u1A47-\u1A4C\u1A53\u1A6B\u1A55-\u1A57\u1A5C-\u1A5E\u1A4D-\u1A52\u1A61\u1A6C\u1A62-\u1A6A\u1A6E\u1A6F\u1A73\u1A70-\u1A72\u1A6D\u1A60]"
argument_list|)
decl_stmt|;
comment|/**      * Simple upgrade test for analyzers to make sure they analyze to the same tokens after upgrade      * TODO we need this for random tokenizers / tokenfilters as well      */
annotation|@
name|Test
DECL|method|testAnalyzerTokensAfterUpgrade
specifier|public
name|void
name|testAnalyzerTokensAfterUpgrade
parameter_list|()
throws|throws
name|IOException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|int
name|numFields
init|=
name|randomIntBetween
argument_list|(
name|PreBuiltAnalyzers
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|,
name|PreBuiltAnalyzers
operator|.
name|values
argument_list|()
operator|.
name|length
operator|*
literal|10
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
index|[]
name|fields
init|=
operator|new
name|String
index|[
name|numFields
operator|*
literal|2
index|]
decl_stmt|;
name|int
name|fieldId
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fields
index|[
name|i
operator|++
index|]
operator|=
literal|"field_"
operator|+
name|fieldId
operator|++
expr_stmt|;
name|String
name|analyzer
init|=
name|randomAnalyzer
argument_list|()
decl_stmt|;
name|fields
index|[
name|i
index|]
operator|=
literal|"type=string,analyzer="
operator|+
name|analyzer
expr_stmt|;
block|}
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
name|fields
argument_list|)
operator|.
name|setSettings
argument_list|(
name|indexSettings
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|()
expr_stmt|;
name|InputOutput
index|[]
name|inout
init|=
operator|new
name|InputOutput
index|[
name|numFields
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numFields
condition|;
name|i
operator|++
control|)
block|{
name|String
name|input
decl_stmt|;
name|Matcher
name|matcher
decl_stmt|;
do|do
block|{
comment|// In Lucene 4.10, a bug was fixed in StandardTokenizer which was causing breaks on complex characters.
comment|// The bug was fixed without backcompat Version handling, so testing between>=4.10 vs<= 4.9 can
comment|// cause differences when the random string generated contains these complex characters. To mitigate
comment|// the problem, we skip any strings containing these characters.
comment|// TODO: only skip strings containing complex chars when comparing against ES<= 1.3.x
name|input
operator|=
name|TestUtil
operator|.
name|randomAnalysisString
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|complexUnicodeChars
operator|.
name|matcher
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
do|;
name|AnalyzeResponse
name|test
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareAnalyze
argument_list|(
literal|"test"
argument_list|,
name|input
argument_list|)
operator|.
name|setField
argument_list|(
literal|"field_"
operator|+
name|i
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|inout
index|[
name|i
index|]
operator|=
operator|new
name|InputOutput
argument_list|(
name|test
argument_list|,
name|input
argument_list|,
literal|"field_"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|logClusterState
argument_list|()
expr_stmt|;
name|boolean
name|upgraded
decl_stmt|;
do|do
block|{
name|logClusterState
argument_list|()
expr_stmt|;
name|upgraded
operator|=
name|backwardsCluster
argument_list|()
operator|.
name|upgradeOneNode
argument_list|()
expr_stmt|;
name|ensureYellow
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|upgraded
condition|)
do|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|inout
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|InputOutput
name|inputOutput
init|=
name|inout
index|[
name|i
index|]
decl_stmt|;
name|AnalyzeResponse
name|test
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareAnalyze
argument_list|(
literal|"test"
argument_list|,
name|inputOutput
operator|.
name|input
argument_list|)
operator|.
name|setField
argument_list|(
name|inputOutput
operator|.
name|field
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AnalyzeResponse
operator|.
name|AnalyzeToken
argument_list|>
name|tokens
init|=
name|test
operator|.
name|getTokens
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AnalyzeResponse
operator|.
name|AnalyzeToken
argument_list|>
name|expectedTokens
init|=
name|inputOutput
operator|.
name|response
operator|.
name|getTokens
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"size mismatch field: "
operator|+
name|fields
index|[
name|i
operator|*
literal|2
index|]
operator|+
literal|" analyzer: "
operator|+
name|fields
index|[
name|i
operator|*
literal|2
operator|+
literal|1
index|]
operator|+
literal|" input: "
operator|+
name|BaseTokenStreamTestCase
operator|.
name|escape
argument_list|(
name|inputOutput
operator|.
name|input
argument_list|)
argument_list|,
name|expectedTokens
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|tokens
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|String
name|msg
init|=
literal|"failed for term: "
operator|+
name|expectedTokens
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getTerm
argument_list|()
operator|+
literal|" field: "
operator|+
name|fields
index|[
name|i
operator|*
literal|2
index|]
operator|+
literal|" analyzer: "
operator|+
name|fields
index|[
name|i
operator|*
literal|2
operator|+
literal|1
index|]
operator|+
literal|" input: "
operator|+
name|BaseTokenStreamTestCase
operator|.
name|escape
argument_list|(
name|inputOutput
operator|.
name|input
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|msg
argument_list|,
name|BaseTokenStreamTestCase
operator|.
name|escape
argument_list|(
name|expectedTokens
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|BaseTokenStreamTestCase
operator|.
name|escape
argument_list|(
name|tokens
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getTerm
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|msg
argument_list|,
name|expectedTokens
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getPosition
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|tokens
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getPosition
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|msg
argument_list|,
name|expectedTokens
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|tokens
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|msg
argument_list|,
name|expectedTokens
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|tokens
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|msg
argument_list|,
name|expectedTokens
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|tokens
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|randomAnalyzer
specifier|private
name|String
name|randomAnalyzer
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|PreBuiltAnalyzers
name|preBuiltAnalyzers
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|PreBuiltAnalyzers
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|preBuiltAnalyzers
operator|==
name|PreBuiltAnalyzers
operator|.
name|SORANI
operator|&&
name|compatibilityVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_1_3_0
argument_list|)
condition|)
block|{
continue|continue;
comment|// SORANI was added in 1.3.0
block|}
if|if
condition|(
name|preBuiltAnalyzers
operator|==
name|PreBuiltAnalyzers
operator|.
name|LITHUANIAN
operator|&&
name|compatibilityVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_1_0
argument_list|)
condition|)
block|{
continue|continue;
comment|// LITHUANIAN was added in 2.1.0
block|}
return|return
name|preBuiltAnalyzers
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
block|}
DECL|class|InputOutput
specifier|private
specifier|static
specifier|final
class|class
name|InputOutput
block|{
DECL|field|response
specifier|final
name|AnalyzeResponse
name|response
decl_stmt|;
DECL|field|input
specifier|final
name|String
name|input
decl_stmt|;
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|method|InputOutput
specifier|public
name|InputOutput
parameter_list|(
name|AnalyzeResponse
name|response
parameter_list|,
name|String
name|input
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
