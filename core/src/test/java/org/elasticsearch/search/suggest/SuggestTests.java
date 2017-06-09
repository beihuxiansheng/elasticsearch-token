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
name|ParseField
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
name|bytes
operator|.
name|BytesReference
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
name|text
operator|.
name|Text
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
name|rest
operator|.
name|action
operator|.
name|search
operator|.
name|RestSearchAction
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
name|Suggest
operator|.
name|Suggestion
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
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
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
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
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
name|completion
operator|.
name|CompletionSuggestion
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
name|PhraseSuggestion
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
name|term
operator|.
name|TermSuggestion
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
name|Arrays
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentHelper
operator|.
name|toXContent
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentParserUtils
operator|.
name|ensureExpectedToken
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentParserUtils
operator|.
name|ensureFieldName
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
name|assertToXContentEquivalent
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
DECL|class|SuggestTests
specifier|public
class|class
name|SuggestTests
extends|extends
name|ESTestCase
block|{
DECL|field|xContentRegistry
specifier|private
specifier|static
specifier|final
name|NamedXContentRegistry
name|xContentRegistry
decl_stmt|;
DECL|field|namedXContents
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|>
name|namedXContents
decl_stmt|;
static|static
block|{
name|namedXContents
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|namedXContents
operator|.
name|add
argument_list|(
operator|new
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|(
name|Suggest
operator|.
name|Suggestion
operator|.
name|class
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"term"
argument_list|)
argument_list|,
parameter_list|(
name|parser
parameter_list|,
name|context
parameter_list|)
lambda|->
name|TermSuggestion
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|,
operator|(
name|String
operator|)
name|context
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|namedXContents
operator|.
name|add
argument_list|(
operator|new
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|(
name|Suggest
operator|.
name|Suggestion
operator|.
name|class
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"phrase"
argument_list|)
argument_list|,
parameter_list|(
name|parser
parameter_list|,
name|context
parameter_list|)
lambda|->
name|PhraseSuggestion
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|,
operator|(
name|String
operator|)
name|context
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|namedXContents
operator|.
name|add
argument_list|(
operator|new
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|(
name|Suggest
operator|.
name|Suggestion
operator|.
name|class
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"completion"
argument_list|)
argument_list|,
parameter_list|(
name|parser
parameter_list|,
name|context
parameter_list|)
lambda|->
name|CompletionSuggestion
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|,
operator|(
name|String
operator|)
name|context
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|xContentRegistry
operator|=
operator|new
name|NamedXContentRegistry
argument_list|(
name|namedXContents
argument_list|)
expr_stmt|;
block|}
DECL|method|getDefaultNamedXContents
specifier|public
specifier|static
name|List
argument_list|<
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|>
name|getDefaultNamedXContents
parameter_list|()
block|{
return|return
name|namedXContents
return|;
block|}
DECL|method|getSuggestersRegistry
specifier|static
name|NamedXContentRegistry
name|getSuggestersRegistry
parameter_list|()
block|{
return|return
name|xContentRegistry
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
name|getSuggestersRegistry
argument_list|()
return|;
block|}
DECL|method|createTestItem
specifier|public
specifier|static
name|Suggest
name|createTestItem
parameter_list|()
block|{
name|int
name|numEntries
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Suggestion
argument_list|<
name|?
extends|extends
name|Entry
argument_list|<
name|?
extends|extends
name|Option
argument_list|>
argument_list|>
argument_list|>
name|suggestions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|numEntries
condition|;
name|i
operator|++
control|)
block|{
name|suggestions
operator|.
name|add
argument_list|(
name|SuggestionTests
operator|.
name|createTestItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Suggest
argument_list|(
name|suggestions
argument_list|)
return|;
block|}
DECL|method|testFromXContent
specifier|public
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|ToXContent
operator|.
name|Params
name|params
init|=
operator|new
name|ToXContent
operator|.
name|MapParams
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
name|RestSearchAction
operator|.
name|TYPED_KEYS_PARAM
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
name|Suggest
name|suggest
init|=
name|createTestItem
argument_list|()
decl_stmt|;
name|XContentType
name|xContentType
init|=
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|humanReadable
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|BytesReference
name|originalBytes
init|=
name|toShuffledXContent
argument_list|(
name|suggest
argument_list|,
name|xContentType
argument_list|,
name|params
argument_list|,
name|humanReadable
argument_list|)
decl_stmt|;
name|Suggest
name|parsed
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|xContentType
operator|.
name|xContent
argument_list|()
argument_list|,
name|originalBytes
argument_list|)
init|)
block|{
name|ensureExpectedToken
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|parser
operator|::
name|getTokenLocation
argument_list|)
expr_stmt|;
name|ensureFieldName
argument_list|(
name|parser
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|Suggest
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|ensureExpectedToken
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|parser
operator|::
name|getTokenLocation
argument_list|)
expr_stmt|;
name|parsed
operator|=
name|Suggest
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|,
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|suggest
operator|.
name|size
argument_list|()
argument_list|,
name|parsed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Suggestion
name|suggestion
range|:
name|suggest
control|)
block|{
name|Suggestion
argument_list|<
name|?
extends|extends
name|Entry
argument_list|<
name|?
extends|extends
name|Option
argument_list|>
argument_list|>
name|parsedSuggestion
init|=
name|parsed
operator|.
name|getSuggestion
argument_list|(
name|suggestion
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|parsedSuggestion
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|suggestion
operator|.
name|getClass
argument_list|()
argument_list|,
name|parsedSuggestion
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertToXContentEquivalent
argument_list|(
name|originalBytes
argument_list|,
name|toXContent
argument_list|(
name|parsed
argument_list|,
name|xContentType
argument_list|,
name|params
argument_list|,
name|humanReadable
argument_list|)
argument_list|,
name|xContentType
argument_list|)
expr_stmt|;
block|}
DECL|method|testToXContent
specifier|public
name|void
name|testToXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|Option
name|option
init|=
operator|new
name|Option
argument_list|(
operator|new
name|Text
argument_list|(
literal|"someText"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"somethingHighlighted"
argument_list|)
argument_list|,
literal|1.3f
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Entry
argument_list|<
name|Option
argument_list|>
name|entry
init|=
operator|new
name|Entry
argument_list|<>
argument_list|(
operator|new
name|Text
argument_list|(
literal|"entryText"
argument_list|)
argument_list|,
literal|42
argument_list|,
literal|313
argument_list|)
decl_stmt|;
name|entry
operator|.
name|addOption
argument_list|(
name|option
argument_list|)
expr_stmt|;
name|Suggestion
argument_list|<
name|Entry
argument_list|<
name|Option
argument_list|>
argument_list|>
name|suggestion
init|=
operator|new
name|Suggestion
argument_list|<>
argument_list|(
literal|"suggestionName"
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|suggestion
operator|.
name|addTerm
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|Suggest
name|suggest
init|=
operator|new
name|Suggest
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|suggestion
argument_list|)
argument_list|)
decl_stmt|;
name|BytesReference
name|xContent
init|=
name|toXContent
argument_list|(
name|suggest
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"suggest\":"
operator|+
literal|"{\"suggestionName\":"
operator|+
literal|"[{\"text\":\"entryText\","
operator|+
literal|"\"offset\":42,"
operator|+
literal|"\"length\":313,"
operator|+
literal|"\"options\":[{\"text\":\"someText\","
operator|+
literal|"\"highlighted\":\"somethingHighlighted\","
operator|+
literal|"\"score\":1.3,"
operator|+
literal|"\"collate_match\":true}]"
operator|+
literal|"}]"
operator|+
literal|"}"
operator|+
literal|"}"
argument_list|,
name|xContent
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFilter
specifier|public
name|void
name|testFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Suggest
operator|.
name|Suggestion
argument_list|<
name|?
extends|extends
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
argument_list|<
name|?
extends|extends
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
argument_list|>
argument_list|>
name|suggestions
decl_stmt|;
name|CompletionSuggestion
name|completionSuggestion
init|=
operator|new
name|CompletionSuggestion
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|PhraseSuggestion
name|phraseSuggestion
init|=
operator|new
name|PhraseSuggestion
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|TermSuggestion
name|termSuggestion
init|=
operator|new
name|TermSuggestion
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|2
argument_list|,
name|SortBy
operator|.
name|SCORE
argument_list|)
decl_stmt|;
name|suggestions
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|completionSuggestion
argument_list|,
name|phraseSuggestion
argument_list|,
name|termSuggestion
argument_list|)
expr_stmt|;
name|Suggest
name|suggest
init|=
operator|new
name|Suggest
argument_list|(
name|suggestions
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PhraseSuggestion
argument_list|>
name|phraseSuggestions
init|=
name|suggest
operator|.
name|filter
argument_list|(
name|PhraseSuggestion
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|phraseSuggestions
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|phraseSuggestions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|phraseSuggestion
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TermSuggestion
argument_list|>
name|termSuggestions
init|=
name|suggest
operator|.
name|filter
argument_list|(
name|TermSuggestion
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|termSuggestions
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|termSuggestions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|termSuggestion
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|CompletionSuggestion
argument_list|>
name|completionSuggestions
init|=
name|suggest
operator|.
name|filter
argument_list|(
name|CompletionSuggestion
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|completionSuggestions
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|completionSuggestions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|completionSuggestion
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSuggestionOrdering
specifier|public
name|void
name|testSuggestionOrdering
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Suggest
operator|.
name|Suggestion
argument_list|<
name|?
extends|extends
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
argument_list|<
name|?
extends|extends
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
argument_list|>
argument_list|>
name|suggestions
decl_stmt|;
name|suggestions
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|int
name|n
init|=
name|randomIntBetween
argument_list|(
literal|2
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|suggestions
operator|.
name|add
argument_list|(
operator|new
name|CompletionSuggestion
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|randomIntBetween
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|suggestions
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|Suggest
name|suggest
init|=
operator|new
name|Suggest
argument_list|(
name|suggestions
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Suggest
operator|.
name|Suggestion
argument_list|<
name|?
extends|extends
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
argument_list|<
name|?
extends|extends
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
argument_list|>
argument_list|>
name|sortedSuggestions
decl_stmt|;
name|sortedSuggestions
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|suggestions
argument_list|)
expr_stmt|;
name|sortedSuggestions
operator|.
name|sort
argument_list|(
parameter_list|(
name|o1
parameter_list|,
name|o2
parameter_list|)
lambda|->
name|o1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|CompletionSuggestion
argument_list|>
name|completionSuggestions
init|=
name|suggest
operator|.
name|filter
argument_list|(
name|CompletionSuggestion
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|completionSuggestions
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|completionSuggestions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|sortedSuggestions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

