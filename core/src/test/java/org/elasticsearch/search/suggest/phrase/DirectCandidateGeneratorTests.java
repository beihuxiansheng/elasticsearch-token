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
name|common
operator|.
name|ParseFieldMatcher
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
name|ParsingException
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
name|XContentHelper
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
name|index
operator|.
name|query
operator|.
name|QueryParseContext
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
name|query
operator|.
name|IndicesQueriesRegistry
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
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|function
operator|.
name|Supplier
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
DECL|class|DirectCandidateGeneratorTests
specifier|public
class|class
name|DirectCandidateGeneratorTests
extends|extends
name|ESTestCase
block|{
DECL|field|mockRegistry
specifier|private
specifier|static
specifier|final
name|IndicesQueriesRegistry
name|mockRegistry
init|=
operator|new
name|IndicesQueriesRegistry
argument_list|()
decl_stmt|;
DECL|field|NUMBER_OF_RUNS
specifier|private
specifier|static
specifier|final
name|int
name|NUMBER_OF_RUNS
init|=
literal|20
decl_stmt|;
comment|/**      * Test serialization and deserialization of the generator      */
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
name|NUMBER_OF_RUNS
condition|;
name|runs
operator|++
control|)
block|{
name|DirectCandidateGeneratorBuilder
name|original
init|=
name|randomCandidateGenerator
argument_list|()
decl_stmt|;
name|DirectCandidateGeneratorBuilder
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
name|NUMBER_OF_RUNS
condition|;
name|runs
operator|++
control|)
block|{
specifier|final
name|DirectCandidateGeneratorBuilder
name|original
init|=
name|randomCandidateGenerator
argument_list|()
decl_stmt|;
name|checkEqualsAndHashCode
argument_list|(
name|original
argument_list|,
name|DirectCandidateGeneratorTests
operator|::
name|copy
argument_list|,
name|DirectCandidateGeneratorTests
operator|::
name|mutate
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|mutate
specifier|private
specifier|static
name|DirectCandidateGeneratorBuilder
name|mutate
parameter_list|(
name|DirectCandidateGeneratorBuilder
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|DirectCandidateGeneratorBuilder
name|mutation
init|=
name|copy
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Supplier
argument_list|<
name|DirectCandidateGeneratorBuilder
argument_list|>
argument_list|>
name|mutators
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
operator|new
name|DirectCandidateGeneratorBuilder
argument_list|(
name|original
operator|.
name|field
argument_list|()
operator|+
literal|"_other"
argument_list|)
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|mutation
operator|.
name|accuracy
argument_list|(
name|original
operator|.
name|accuracy
argument_list|()
operator|==
literal|null
condition|?
literal|0.1f
else|:
name|original
operator|.
name|accuracy
argument_list|()
operator|+
literal|0.1f
argument_list|)
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
block|{
name|Integer
name|maxEdits
init|=
name|original
operator|.
name|maxEdits
argument_list|()
operator|==
literal|null
condition|?
literal|1
else|:
name|original
operator|.
name|maxEdits
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxEdits
operator|==
literal|1
condition|)
block|{
name|maxEdits
operator|=
literal|2
expr_stmt|;
block|}
else|else
block|{
name|maxEdits
operator|=
literal|1
expr_stmt|;
block|}
return|return
name|mutation
operator|.
name|maxEdits
argument_list|(
name|maxEdits
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|mutation
operator|.
name|maxInspections
argument_list|(
name|original
operator|.
name|maxInspections
argument_list|()
operator|==
literal|null
condition|?
literal|1
else|:
name|original
operator|.
name|maxInspections
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|mutation
operator|.
name|minWordLength
argument_list|(
name|original
operator|.
name|minWordLength
argument_list|()
operator|==
literal|null
condition|?
literal|1
else|:
name|original
operator|.
name|minWordLength
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|mutation
operator|.
name|prefixLength
argument_list|(
name|original
operator|.
name|prefixLength
argument_list|()
operator|==
literal|null
condition|?
literal|1
else|:
name|original
operator|.
name|prefixLength
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|mutation
operator|.
name|size
argument_list|(
name|original
operator|.
name|size
argument_list|()
operator|==
literal|null
condition|?
literal|1
else|:
name|original
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|mutation
operator|.
name|maxTermFreq
argument_list|(
name|original
operator|.
name|maxTermFreq
argument_list|()
operator|==
literal|null
condition|?
literal|0.1f
else|:
name|original
operator|.
name|maxTermFreq
argument_list|()
operator|+
literal|0.1f
argument_list|)
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|mutation
operator|.
name|minDocFreq
argument_list|(
name|original
operator|.
name|minDocFreq
argument_list|()
operator|==
literal|null
condition|?
literal|0.1f
else|:
name|original
operator|.
name|minDocFreq
argument_list|()
operator|+
literal|0.1f
argument_list|)
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|mutation
operator|.
name|postFilter
argument_list|(
name|original
operator|.
name|postFilter
argument_list|()
operator|==
literal|null
condition|?
literal|"postFilter"
else|:
name|original
operator|.
name|postFilter
argument_list|()
operator|+
literal|"_other"
argument_list|)
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|mutation
operator|.
name|preFilter
argument_list|(
name|original
operator|.
name|preFilter
argument_list|()
operator|==
literal|null
condition|?
literal|"preFilter"
else|:
name|original
operator|.
name|preFilter
argument_list|()
operator|+
literal|"_other"
argument_list|)
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|mutation
operator|.
name|sort
argument_list|(
name|original
operator|.
name|sort
argument_list|()
operator|==
literal|null
condition|?
literal|"score"
else|:
name|original
operator|.
name|sort
argument_list|()
operator|+
literal|"_other"
argument_list|)
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|mutation
operator|.
name|stringDistance
argument_list|(
name|original
operator|.
name|stringDistance
argument_list|()
operator|==
literal|null
condition|?
literal|"levenstein"
else|:
name|original
operator|.
name|stringDistance
argument_list|()
operator|+
literal|"_other"
argument_list|)
argument_list|)
expr_stmt|;
name|mutators
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|mutation
operator|.
name|suggestMode
argument_list|(
name|original
operator|.
name|suggestMode
argument_list|()
operator|==
literal|null
condition|?
literal|"missing"
else|:
name|original
operator|.
name|suggestMode
argument_list|()
operator|+
literal|"_other"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|randomFrom
argument_list|(
name|mutators
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      *  creates random candidate generator, renders it to xContent and back to new instance that should be equal to original      */
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
name|NUMBER_OF_RUNS
condition|;
name|runs
operator|++
control|)
block|{
name|DirectCandidateGeneratorBuilder
name|generator
init|=
name|randomCandidateGenerator
argument_list|()
decl_stmt|;
name|XContentBuilder
name|builder
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
name|builder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
name|generator
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|XContentParser
name|parser
init|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|shuffleXContent
argument_list|(
name|builder
argument_list|)
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|mockRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|DirectCandidateGeneratorBuilder
name|secondGenerator
init|=
name|DirectCandidateGeneratorBuilder
operator|.
name|fromXContent
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|generator
argument_list|,
name|secondGenerator
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|generator
argument_list|,
name|secondGenerator
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|generator
operator|.
name|hashCode
argument_list|()
argument_list|,
name|secondGenerator
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertEqualGenerators
specifier|public
specifier|static
name|void
name|assertEqualGenerators
parameter_list|(
name|DirectCandidateGenerator
name|first
parameter_list|,
name|DirectCandidateGenerator
name|second
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|first
operator|.
name|field
argument_list|()
argument_list|,
name|second
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|accuracy
argument_list|()
argument_list|,
name|second
operator|.
name|accuracy
argument_list|()
argument_list|,
name|Float
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|maxTermFreq
argument_list|()
argument_list|,
name|second
operator|.
name|maxTermFreq
argument_list|()
argument_list|,
name|Float
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|maxEdits
argument_list|()
argument_list|,
name|second
operator|.
name|maxEdits
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|maxInspections
argument_list|()
argument_list|,
name|second
operator|.
name|maxInspections
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|minDocFreq
argument_list|()
argument_list|,
name|second
operator|.
name|minDocFreq
argument_list|()
argument_list|,
name|Float
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|minWordLength
argument_list|()
argument_list|,
name|second
operator|.
name|minWordLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|postFilter
argument_list|()
argument_list|,
name|second
operator|.
name|postFilter
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|prefixLength
argument_list|()
argument_list|,
name|second
operator|.
name|prefixLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|preFilter
argument_list|()
argument_list|,
name|second
operator|.
name|preFilter
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|sort
argument_list|()
argument_list|,
name|second
operator|.
name|sort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|size
argument_list|()
argument_list|,
name|second
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// some instances of StringDistance don't support equals, just checking the class here
name|assertEquals
argument_list|(
name|first
operator|.
name|stringDistance
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|,
name|second
operator|.
name|stringDistance
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|suggestMode
argument_list|()
argument_list|,
name|second
operator|.
name|suggestMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * test that bad xContent throws exception      */
DECL|method|testIllegalXContent
specifier|public
name|void
name|testIllegalXContent
parameter_list|()
throws|throws
name|IOException
block|{
comment|// test missing fieldname
name|String
name|directGenerator
init|=
literal|"{ }"
decl_stmt|;
name|assertIllegalXContent
argument_list|(
name|directGenerator
argument_list|,
name|IllegalArgumentException
operator|.
name|class
argument_list|,
literal|"Required [field]"
argument_list|)
expr_stmt|;
comment|// test two fieldnames
name|directGenerator
operator|=
literal|"{ \"field\" : \"f1\", \"field\" : \"f2\" }"
expr_stmt|;
name|assertIllegalXContent
argument_list|(
name|directGenerator
argument_list|,
name|ParsingException
operator|.
name|class
argument_list|,
literal|"[direct_generator] failed to parse field [field]"
argument_list|)
expr_stmt|;
comment|// test unknown field
name|directGenerator
operator|=
literal|"{ \"unknown_param\" : \"f1\" }"
expr_stmt|;
name|assertIllegalXContent
argument_list|(
name|directGenerator
argument_list|,
name|IllegalArgumentException
operator|.
name|class
argument_list|,
literal|"[direct_generator] unknown field [unknown_param], parser not found"
argument_list|)
expr_stmt|;
comment|// test bad value for field (e.g. size expects an int)
name|directGenerator
operator|=
literal|"{ \"size\" : \"xxl\" }"
expr_stmt|;
name|assertIllegalXContent
argument_list|(
name|directGenerator
argument_list|,
name|ParsingException
operator|.
name|class
argument_list|,
literal|"[direct_generator] failed to parse field [size]"
argument_list|)
expr_stmt|;
comment|// test unexpected token
name|directGenerator
operator|=
literal|"{ \"size\" : [ \"xxl\" ] }"
expr_stmt|;
name|assertIllegalXContent
argument_list|(
name|directGenerator
argument_list|,
name|IllegalArgumentException
operator|.
name|class
argument_list|,
literal|"[direct_generator] size doesn't support values of type: START_ARRAY"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertIllegalXContent
specifier|private
specifier|static
name|void
name|assertIllegalXContent
parameter_list|(
name|String
name|directGenerator
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
name|exceptionClass
parameter_list|,
name|String
name|exceptionMsg
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|directGenerator
argument_list|)
operator|.
name|createParser
argument_list|(
name|directGenerator
argument_list|)
decl_stmt|;
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|mockRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|exceptionClass
argument_list|,
parameter_list|()
lambda|->
name|DirectCandidateGeneratorBuilder
operator|.
name|fromXContent
argument_list|(
name|context
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|exceptionMsg
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * create random {@link DirectCandidateGeneratorBuilder}      */
DECL|method|randomCandidateGenerator
specifier|public
specifier|static
name|DirectCandidateGeneratorBuilder
name|randomCandidateGenerator
parameter_list|()
block|{
name|DirectCandidateGeneratorBuilder
name|generator
init|=
operator|new
name|DirectCandidateGeneratorBuilder
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|maybeSet
argument_list|(
name|generator
operator|::
name|accuracy
argument_list|,
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|generator
operator|::
name|maxEdits
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|generator
operator|::
name|maxInspections
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
name|generator
operator|::
name|maxTermFreq
argument_list|,
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|generator
operator|::
name|minDocFreq
argument_list|,
name|randomFloat
argument_list|()
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|generator
operator|::
name|minWordLength
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
name|generator
operator|::
name|prefixLength
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
name|generator
operator|::
name|preFilter
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|generator
operator|::
name|postFilter
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|generator
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
name|generator
operator|::
name|sort
argument_list|,
name|randomFrom
argument_list|(
literal|"score"
argument_list|,
literal|"frequency"
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|generator
operator|::
name|stringDistance
argument_list|,
name|randomFrom
argument_list|(
literal|"internal"
argument_list|,
literal|"damerau_levenshtein"
argument_list|,
literal|"levenstein"
argument_list|,
literal|"jarowinkler"
argument_list|,
literal|"ngram"
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|generator
operator|::
name|suggestMode
argument_list|,
name|randomFrom
argument_list|(
literal|"missing"
argument_list|,
literal|"popular"
argument_list|,
literal|"always"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|generator
return|;
block|}
DECL|method|copy
specifier|private
specifier|static
name|DirectCandidateGeneratorBuilder
name|copy
parameter_list|(
name|DirectCandidateGeneratorBuilder
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
operator|new
name|NamedWriteableRegistry
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
argument_list|,
name|DirectCandidateGeneratorBuilder
operator|::
operator|new
argument_list|)
return|;
block|}
block|}
end_class

end_unit

