begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.phrase
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|phrase
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Template
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|AbstractSuggestionBuilderTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|SuggestionSearchContext
operator|.
name|SuggestionContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|phrase
operator|.
name|PhraseSuggestionContext
operator|.
name|DirectCandidateGenerator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
DECL|class|PhraseSuggestionBuilderTests
specifier|public
class|class
name|PhraseSuggestionBuilderTests
extends|extends
name|AbstractSuggestionBuilderTestCase
argument_list|<
name|PhraseSuggestionBuilder
argument_list|>
block|{
annotation|@
name|BeforeClass
DECL|method|initSmoothingModels
specifier|public
specifier|static
name|void
name|initSmoothingModels
parameter_list|()
block|{
name|namedWriteableRegistry
operator|.
name|registerPrototype
argument_list|(
name|SmoothingModel
operator|.
name|class
argument_list|,
name|Laplace
operator|.
name|PROTOTYPE
argument_list|)
expr_stmt|;
name|namedWriteableRegistry
operator|.
name|registerPrototype
argument_list|(
name|SmoothingModel
operator|.
name|class
argument_list|,
name|LinearInterpolation
operator|.
name|PROTOTYPE
argument_list|)
expr_stmt|;
name|namedWriteableRegistry
operator|.
name|registerPrototype
argument_list|(
name|SmoothingModel
operator|.
name|class
argument_list|,
name|StupidBackoff
operator|.
name|PROTOTYPE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|randomSuggestionBuilder
specifier|protected
name|PhraseSuggestionBuilder
name|randomSuggestionBuilder
parameter_list|()
block|{
return|return
name|randomPhraseSuggestionBuilder
argument_list|()
return|;
block|}
DECL|method|randomPhraseSuggestionBuilder
specifier|public
specifier|static
name|PhraseSuggestionBuilder
name|randomPhraseSuggestionBuilder
parameter_list|()
block|{
name|PhraseSuggestionBuilder
name|testBuilder
init|=
operator|new
name|PhraseSuggestionBuilder
argument_list|()
decl_stmt|;
name|maybeSet
argument_list|(
name|testBuilder
operator|::
name|maxErrors
argument_list|,
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|testBuilder
operator|::
name|separator
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|testBuilder
operator|::
name|realWordErrorLikelihood
argument_list|,
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|testBuilder
operator|::
name|confidence
argument_list|,
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|testBuilder
operator|::
name|collateQuery
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
comment|// collate query prune and parameters will only be used when query is set
if|if
condition|(
name|testBuilder
operator|.
name|collateQuery
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|maybeSet
argument_list|(
name|testBuilder
operator|::
name|collatePrune
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|collateParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numParams
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
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
name|numParams
condition|;
name|i
operator|++
control|)
block|{
name|collateParams
operator|.
name|put
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|5
argument_list|)
argument_list|,
name|randomAsciiOfLength
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|testBuilder
operator|.
name|collateParams
argument_list|(
name|collateParams
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
comment|// preTag, postTag
name|testBuilder
operator|.
name|highlight
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|maybeSet
argument_list|(
name|testBuilder
operator|::
name|gramSize
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|testBuilder
operator|::
name|forceUnigrams
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|testBuilder
operator|::
name|tokenLimit
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|testBuilder
operator|.
name|smoothingModel
argument_list|(
name|randomSmoothingModel
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|int
name|numGenerators
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
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
name|numGenerators
condition|;
name|i
operator|++
control|)
block|{
name|testBuilder
operator|.
name|addCandidateGenerator
argument_list|(
name|DirectCandidateGeneratorTests
operator|.
name|randomCandidateGenerator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|testBuilder
return|;
block|}
DECL|method|randomSmoothingModel
specifier|private
specifier|static
name|SmoothingModel
name|randomSmoothingModel
parameter_list|()
block|{
name|SmoothingModel
name|model
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|model
operator|=
name|LaplaceModelTests
operator|.
name|createRandomModel
argument_list|()
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|model
operator|=
name|StupidBackoffModelTests
operator|.
name|createRandomModel
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|model
operator|=
name|LinearInterpolationModelTests
operator|.
name|createRandomModel
argument_list|()
expr_stmt|;
break|break;
block|}
return|return
name|model
return|;
block|}
annotation|@
name|Override
DECL|method|mutateSpecificParameters
specifier|protected
name|void
name|mutateSpecificParameters
parameter_list|(
name|PhraseSuggestionBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|12
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|builder
operator|.
name|maxErrors
argument_list|(
name|randomValueOtherThan
argument_list|(
name|builder
operator|.
name|maxErrors
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomFloat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|builder
operator|.
name|realWordErrorLikelihood
argument_list|(
name|randomValueOtherThan
argument_list|(
name|builder
operator|.
name|realWordErrorLikelihood
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomFloat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|builder
operator|.
name|confidence
argument_list|(
name|randomValueOtherThan
argument_list|(
name|builder
operator|.
name|confidence
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomFloat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|builder
operator|.
name|gramSize
argument_list|(
name|randomValueOtherThan
argument_list|(
name|builder
operator|.
name|gramSize
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|builder
operator|.
name|tokenLimit
argument_list|(
name|randomValueOtherThan
argument_list|(
name|builder
operator|.
name|tokenLimit
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|builder
operator|.
name|separator
argument_list|(
name|randomValueOtherThan
argument_list|(
name|builder
operator|.
name|separator
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|6
case|:
name|Template
name|collateQuery
init|=
name|builder
operator|.
name|collateQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|collateQuery
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|collateQuery
argument_list|(
name|randomValueOtherThan
argument_list|(
name|collateQuery
operator|.
name|getScript
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|collateQuery
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|7
case|:
name|builder
operator|.
name|collatePrune
argument_list|(
name|builder
operator|.
name|collatePrune
argument_list|()
operator|==
literal|null
condition|?
name|randomBoolean
argument_list|()
else|:
operator|!
name|builder
operator|.
name|collatePrune
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|8
case|:
comment|// preTag, postTag
name|String
name|currentPre
init|=
name|builder
operator|.
name|preTag
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentPre
operator|!=
literal|null
condition|)
block|{
comment|// simply double both values
name|builder
operator|.
name|highlight
argument_list|(
name|builder
operator|.
name|preTag
argument_list|()
operator|+
name|builder
operator|.
name|preTag
argument_list|()
argument_list|,
name|builder
operator|.
name|postTag
argument_list|()
operator|+
name|builder
operator|.
name|postTag
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|highlight
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|9
case|:
name|builder
operator|.
name|forceUnigrams
argument_list|(
name|builder
operator|.
name|forceUnigrams
argument_list|()
operator|==
literal|null
condition|?
name|randomBoolean
argument_list|()
else|:
operator|!
name|builder
operator|.
name|forceUnigrams
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|10
case|:
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|collateParams
init|=
name|builder
operator|.
name|collateParams
argument_list|()
operator|==
literal|null
condition|?
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|1
argument_list|)
else|:
name|builder
operator|.
name|collateParams
argument_list|()
decl_stmt|;
name|collateParams
operator|.
name|put
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|5
argument_list|)
argument_list|,
name|randomAsciiOfLength
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|collateParams
argument_list|(
name|collateParams
argument_list|)
expr_stmt|;
break|break;
case|case
literal|11
case|:
name|builder
operator|.
name|smoothingModel
argument_list|(
name|randomValueOtherThan
argument_list|(
name|builder
operator|.
name|smoothingModel
argument_list|()
argument_list|,
name|PhraseSuggestionBuilderTests
operator|::
name|randomSmoothingModel
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|12
case|:
name|builder
operator|.
name|addCandidateGenerator
argument_list|(
name|DirectCandidateGeneratorTests
operator|.
name|randomCandidateGenerator
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
annotation|@
name|Override
DECL|method|assertSuggestionContext
specifier|protected
name|void
name|assertSuggestionContext
parameter_list|(
name|SuggestionContext
name|oldSuggestion
parameter_list|,
name|SuggestionContext
name|newSuggestion
parameter_list|)
block|{
name|assertThat
argument_list|(
name|oldSuggestion
argument_list|,
name|instanceOf
argument_list|(
name|PhraseSuggestionContext
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newSuggestion
argument_list|,
name|instanceOf
argument_list|(
name|PhraseSuggestionContext
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|PhraseSuggestionContext
name|oldPhraseSuggestion
init|=
operator|(
name|PhraseSuggestionContext
operator|)
name|oldSuggestion
decl_stmt|;
name|PhraseSuggestionContext
name|newPhraseSuggestion
init|=
operator|(
name|PhraseSuggestionContext
operator|)
name|newSuggestion
decl_stmt|;
name|assertEquals
argument_list|(
name|oldPhraseSuggestion
operator|.
name|confidence
argument_list|()
argument_list|,
name|newPhraseSuggestion
operator|.
name|confidence
argument_list|()
argument_list|,
name|Float
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldPhraseSuggestion
operator|.
name|collatePrune
argument_list|()
argument_list|,
name|newPhraseSuggestion
operator|.
name|collatePrune
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldPhraseSuggestion
operator|.
name|gramSize
argument_list|()
argument_list|,
name|newPhraseSuggestion
operator|.
name|gramSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldPhraseSuggestion
operator|.
name|realworldErrorLikelyhood
argument_list|()
argument_list|,
name|newPhraseSuggestion
operator|.
name|realworldErrorLikelyhood
argument_list|()
argument_list|,
name|Float
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldPhraseSuggestion
operator|.
name|maxErrors
argument_list|()
argument_list|,
name|newPhraseSuggestion
operator|.
name|maxErrors
argument_list|()
argument_list|,
name|Float
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldPhraseSuggestion
operator|.
name|separator
argument_list|()
argument_list|,
name|newPhraseSuggestion
operator|.
name|separator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldPhraseSuggestion
operator|.
name|getTokenLimit
argument_list|()
argument_list|,
name|newPhraseSuggestion
operator|.
name|getTokenLimit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldPhraseSuggestion
operator|.
name|getRequireUnigram
argument_list|()
argument_list|,
name|newPhraseSuggestion
operator|.
name|getRequireUnigram
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldPhraseSuggestion
operator|.
name|getPreTag
argument_list|()
argument_list|,
name|newPhraseSuggestion
operator|.
name|getPreTag
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldPhraseSuggestion
operator|.
name|getPostTag
argument_list|()
argument_list|,
name|newPhraseSuggestion
operator|.
name|getPostTag
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldPhraseSuggestion
operator|.
name|getCollateQueryScript
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// only assert that we have a compiled script on the other side
name|assertNotNull
argument_list|(
name|newPhraseSuggestion
operator|.
name|getCollateQueryScript
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|oldPhraseSuggestion
operator|.
name|generators
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assertNotNull
argument_list|(
name|newPhraseSuggestion
operator|.
name|generators
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldPhraseSuggestion
operator|.
name|generators
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|newPhraseSuggestion
operator|.
name|generators
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|DirectCandidateGenerator
argument_list|>
name|secondList
init|=
name|newPhraseSuggestion
operator|.
name|generators
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|DirectCandidateGenerator
name|candidateGenerator
range|:
name|newPhraseSuggestion
operator|.
name|generators
argument_list|()
control|)
block|{
name|DirectCandidateGeneratorTests
operator|.
name|assertEqualGenerators
argument_list|(
name|candidateGenerator
argument_list|,
name|secondList
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|oldPhraseSuggestion
operator|.
name|getCollateScriptParams
argument_list|()
argument_list|,
name|newPhraseSuggestion
operator|.
name|getCollateScriptParams
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldPhraseSuggestion
operator|.
name|model
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assertNotNull
argument_list|(
name|newPhraseSuggestion
operator|.
name|model
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

