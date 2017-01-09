begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
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
name|io
operator|.
name|stream
operator|.
name|NamedWriteableRegistry
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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
name|common
operator|.
name|xcontent
operator|.
name|NamedXContentRegistry
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
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentFactory
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
name|xcontent
operator|.
name|XContentParser
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
name|xcontent
operator|.
name|XContentType
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
name|SearchModule
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
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
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
name|EqualsHashCodeTestUtils
operator|.
name|checkEqualsAndHashCode
import|;
end_import

begin_class
DECL|class|AbstractSuggestionBuilderTestCase
specifier|public
specifier|abstract
class|class
name|AbstractSuggestionBuilderTestCase
parameter_list|<
name|SB
extends|extends
name|SuggestionBuilder
parameter_list|<
name|SB
parameter_list|>
parameter_list|>
extends|extends
name|ESTestCase
block|{
DECL|field|NUMBER_OF_TESTBUILDERS
specifier|private
specifier|static
specifier|final
name|int
name|NUMBER_OF_TESTBUILDERS
init|=
literal|20
decl_stmt|;
DECL|field|namedWriteableRegistry
specifier|protected
specifier|static
name|NamedWriteableRegistry
name|namedWriteableRegistry
decl_stmt|;
DECL|field|xContentRegistry
specifier|protected
specifier|static
name|NamedXContentRegistry
name|xContentRegistry
decl_stmt|;
comment|/**      * setup for the whole base test class      */
annotation|@
name|BeforeClass
DECL|method|init
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|SearchModule
name|searchModule
init|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|false
argument_list|,
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|namedWriteableRegistry
operator|=
operator|new
name|NamedWriteableRegistry
argument_list|(
name|searchModule
operator|.
name|getNamedWriteables
argument_list|()
argument_list|)
expr_stmt|;
name|xContentRegistry
operator|=
operator|new
name|NamedXContentRegistry
argument_list|(
name|searchModule
operator|.
name|getNamedXContents
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|namedWriteableRegistry
operator|=
literal|null
expr_stmt|;
name|xContentRegistry
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Test serialization and deserialization of the suggestion builder      */
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|runs
init|=
literal|0
init|;
name|runs
operator|<
name|NUMBER_OF_TESTBUILDERS
condition|;
name|runs
operator|++
control|)
block|{
name|SB
name|original
init|=
name|randomTestBuilder
argument_list|()
decl_stmt|;
name|SB
name|deserialized
init|=
name|copy
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|deserialized
argument_list|,
name|original
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserialized
operator|.
name|hashCode
argument_list|()
argument_list|,
name|original
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|deserialized
argument_list|,
name|original
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * returns a random suggestion builder, setting the common options randomly      */
DECL|method|randomTestBuilder
specifier|protected
name|SB
name|randomTestBuilder
parameter_list|()
block|{
name|SB
name|randomSuggestion
init|=
name|randomSuggestionBuilder
argument_list|()
decl_stmt|;
return|return
name|randomSuggestion
return|;
block|}
DECL|method|setCommonPropertiesOnRandomBuilder
specifier|public
specifier|static
name|void
name|setCommonPropertiesOnRandomBuilder
parameter_list|(
name|SuggestionBuilder
argument_list|<
name|?
argument_list|>
name|randomSuggestion
parameter_list|)
block|{
name|randomSuggestion
operator|.
name|text
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
comment|// have to set the text because we don't know if the global text was set
name|maybeSet
argument_list|(
name|randomSuggestion
operator|::
name|prefix
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|randomSuggestion
operator|::
name|regex
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|randomSuggestion
operator|::
name|analyzer
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|randomSuggestion
operator|::
name|size
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|randomSuggestion
operator|::
name|shardSize
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * create a randomized {@link SuggestBuilder} that is used in further tests      */
DECL|method|randomSuggestionBuilder
specifier|protected
specifier|abstract
name|SB
name|randomSuggestionBuilder
parameter_list|()
function_decl|;
comment|/**      * Test equality and hashCode properties      */
DECL|method|testEqualsAndHashcode
specifier|public
name|void
name|testEqualsAndHashcode
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|runs
init|=
literal|0
init|;
name|runs
operator|<
name|NUMBER_OF_TESTBUILDERS
condition|;
name|runs
operator|++
control|)
block|{
name|checkEqualsAndHashCode
argument_list|(
name|randomTestBuilder
argument_list|()
argument_list|,
name|this
operator|::
name|copy
argument_list|,
name|this
operator|::
name|mutate
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * creates random suggestion builder, renders it to xContent and back to new      * instance that should be equal to original      */
DECL|method|testFromXContent
specifier|public
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|runs
init|=
literal|0
init|;
name|runs
operator|<
name|NUMBER_OF_TESTBUILDERS
condition|;
name|runs
operator|++
control|)
block|{
name|SB
name|suggestionBuilder
init|=
name|randomTestBuilder
argument_list|()
decl_stmt|;
name|XContentBuilder
name|xContentBuilder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|xContentBuilder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|suggestionBuilder
operator|.
name|toXContent
argument_list|(
name|xContentBuilder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|XContentBuilder
name|shuffled
init|=
name|shuffleXContent
argument_list|(
name|xContentBuilder
argument_list|,
name|shuffleProtectedFields
argument_list|()
argument_list|)
decl_stmt|;
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|shuffled
argument_list|)
decl_stmt|;
comment|// we need to skip the start object and the name, those will be parsed by outer SuggestBuilder
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|SuggestionBuilder
argument_list|<
name|?
argument_list|>
name|secondSuggestionBuilder
init|=
name|SuggestionBuilder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|suggestionBuilder
argument_list|,
name|secondSuggestionBuilder
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|suggestionBuilder
argument_list|,
name|secondSuggestionBuilder
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|suggestionBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|secondSuggestionBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Subclasses can override this method and return a set of fields which should be protected from      * recursive random shuffling in the {@link #testFromXContent()} test case      */
DECL|method|shuffleProtectedFields
specifier|protected
name|String
index|[]
name|shuffleProtectedFields
parameter_list|()
block|{
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
DECL|method|mutate
specifier|private
name|SB
name|mutate
parameter_list|(
name|SB
name|firstBuilder
parameter_list|)
throws|throws
name|IOException
block|{
name|SB
name|mutation
init|=
name|copy
argument_list|(
name|firstBuilder
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|mutation
argument_list|,
name|firstBuilder
argument_list|)
expr_stmt|;
comment|// change ither one of the shared SuggestionBuilder parameters, or delegate to the specific tests mutate method
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|mutation
operator|.
name|text
argument_list|(
name|randomValueOtherThan
argument_list|(
name|mutation
operator|.
name|text
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|mutation
operator|.
name|prefix
argument_list|(
name|randomValueOtherThan
argument_list|(
name|mutation
operator|.
name|prefix
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|mutation
operator|.
name|regex
argument_list|(
name|randomValueOtherThan
argument_list|(
name|mutation
operator|.
name|regex
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|mutation
operator|.
name|analyzer
argument_list|(
name|randomValueOtherThan
argument_list|(
name|mutation
operator|.
name|analyzer
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|mutation
operator|.
name|size
argument_list|(
name|randomValueOtherThan
argument_list|(
name|mutation
operator|.
name|size
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
name|mutation
operator|.
name|shardSize
argument_list|(
name|randomValueOtherThan
argument_list|(
name|mutation
operator|.
name|shardSize
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
block|}
block|}
else|else
block|{
name|mutateSpecificParameters
argument_list|(
name|firstBuilder
argument_list|)
expr_stmt|;
block|}
return|return
name|mutation
return|;
block|}
comment|/**      * take and input {@link SuggestBuilder} and return another one that is      * different in one aspect (to test non-equality)      */
DECL|method|mutateSpecificParameters
specifier|protected
specifier|abstract
name|void
name|mutateSpecificParameters
parameter_list|(
name|SB
name|firstBuilder
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|copy
specifier|protected
name|SB
name|copy
parameter_list|(
name|SB
name|original
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|copyWriteable
argument_list|(
name|original
argument_list|,
name|namedWriteableRegistry
argument_list|,
operator|(
name|Writeable
operator|.
name|Reader
argument_list|<
name|SB
argument_list|>
operator|)
name|namedWriteableRegistry
operator|.
name|getReader
argument_list|(
name|SuggestionBuilder
operator|.
name|class
argument_list|,
name|original
operator|.
name|getWriteableName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|xContentRegistry
specifier|protected
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|()
block|{
return|return
name|xContentRegistry
return|;
block|}
block|}
end_class

end_unit

