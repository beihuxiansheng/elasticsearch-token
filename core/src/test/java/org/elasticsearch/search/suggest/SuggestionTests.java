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
name|XContent
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
name|common
operator|.
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
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
name|Collections
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertToXContentEquivalent
import|;
end_import

begin_class
DECL|class|SuggestionTests
specifier|public
class|class
name|SuggestionTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|field|SUGGESTION_TYPES
specifier|private
specifier|static
specifier|final
name|Class
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
index|[]
name|SUGGESTION_TYPES
init|=
operator|new
name|Class
index|[]
block|{
name|TermSuggestion
operator|.
name|class
block|,
name|PhraseSuggestion
operator|.
name|class
block|,
name|CompletionSuggestion
operator|.
name|class
block|}
decl_stmt|;
DECL|method|createTestItem
specifier|public
specifier|static
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
name|createTestItem
parameter_list|()
block|{
return|return
name|createTestItem
argument_list|(
name|randomFrom
argument_list|(
name|SUGGESTION_TYPES
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
name|SuggestTests
operator|.
name|getSuggestersRegistry
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|createTestItem
specifier|public
specifier|static
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
name|createTestItem
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Suggestion
argument_list|>
name|type
parameter_list|)
block|{
name|String
name|name
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
comment|// note: size will not be rendered via "toXContent", only passed on internally on transport layer
name|int
name|size
init|=
name|randomInt
argument_list|()
decl_stmt|;
name|Supplier
argument_list|<
name|Entry
argument_list|>
name|entrySupplier
init|=
literal|null
decl_stmt|;
name|Suggestion
name|suggestion
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|TermSuggestion
operator|.
name|class
condition|)
block|{
name|suggestion
operator|=
operator|new
name|TermSuggestion
argument_list|(
name|name
argument_list|,
name|size
argument_list|,
name|randomFrom
argument_list|(
name|SortBy
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|entrySupplier
operator|=
parameter_list|()
lambda|->
name|SuggestionEntryTests
operator|.
name|createTestItem
argument_list|(
name|TermSuggestion
operator|.
name|Entry
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PhraseSuggestion
operator|.
name|class
condition|)
block|{
name|suggestion
operator|=
operator|new
name|PhraseSuggestion
argument_list|(
name|name
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|entrySupplier
operator|=
parameter_list|()
lambda|->
name|SuggestionEntryTests
operator|.
name|createTestItem
argument_list|(
name|PhraseSuggestion
operator|.
name|Entry
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|CompletionSuggestion
operator|.
name|class
condition|)
block|{
name|suggestion
operator|=
operator|new
name|CompletionSuggestion
argument_list|(
name|name
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|entrySupplier
operator|=
parameter_list|()
lambda|->
name|SuggestionEntryTests
operator|.
name|createTestItem
argument_list|(
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"type not supported ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|int
name|numEntries
decl_stmt|;
if|if
condition|(
name|frequently
argument_list|()
condition|)
block|{
if|if
condition|(
name|type
operator|==
name|CompletionSuggestion
operator|.
name|class
condition|)
block|{
name|numEntries
operator|=
literal|1
expr_stmt|;
comment|// CompletionSuggestion can have max. one entry
block|}
else|else
block|{
name|numEntries
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|numEntries
operator|=
literal|0
expr_stmt|;
comment|// also occasionally test zero entries
block|}
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
name|suggestion
operator|.
name|addTerm
argument_list|(
name|entrySupplier
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|suggestion
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|}
argument_list|)
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
for|for
control|(
name|Class
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
name|type
range|:
name|SUGGESTION_TYPES
control|)
block|{
name|Suggestion
name|suggestion
init|=
name|createTestItem
argument_list|(
name|type
argument_list|)
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
name|toXContent
argument_list|(
name|suggestion
argument_list|,
name|xContentType
argument_list|,
name|params
argument_list|,
name|humanReadable
argument_list|)
decl_stmt|;
name|Suggestion
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
name|ensureExpectedToken
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
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
name|Suggestion
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
name|suggestion
operator|.
name|getName
argument_list|()
argument_list|,
name|parsed
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|suggestion
operator|.
name|getEntries
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|parsed
operator|.
name|getEntries
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// We don't parse size via xContent, instead we set it to -1 on the client side
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|parsed
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
comment|/**      * test that we throw error if RestSearchAction.TYPED_KEYS_PARAM isn't set while rendering xContent      */
DECL|method|testFromXContentFailsWithoutTypeParam
specifier|public
name|void
name|testFromXContentFailsWithoutTypeParam
parameter_list|()
throws|throws
name|IOException
block|{
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
name|BytesReference
name|originalBytes
init|=
name|toXContent
argument_list|(
name|createTestItem
argument_list|()
argument_list|,
name|xContentType
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
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
name|ensureExpectedToken
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
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
name|ParsingException
name|e
init|=
name|expectThrows
argument_list|(
name|ParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|Suggestion
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Cannot parse suggestion response without type information. "
operator|+
literal|"Set [typed_keys] parameter on the request to ensure the type information "
operator|+
literal|"is added to the response output"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testUnknownSuggestionTypeThrows
specifier|public
name|void
name|testUnknownSuggestionTypeThrows
parameter_list|()
throws|throws
name|IOException
block|{
name|XContent
name|xContent
init|=
name|JsonXContent
operator|.
name|jsonXContent
decl_stmt|;
name|String
name|suggestionString
init|=
literal|"{\"unknownType#suggestionName\":"
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
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|xContent
operator|.
name|createParser
argument_list|(
name|xContentRegistry
argument_list|()
argument_list|,
name|suggestionString
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
name|ensureExpectedToken
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
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
name|ParsingException
name|e
init|=
name|expectThrows
argument_list|(
name|ParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|Suggestion
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unknown Suggestion [unknownType]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testToXContent
specifier|public
name|void
name|testToXContent
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
name|BytesReference
name|xContent
init|=
name|toXContent
argument_list|(
name|suggestion
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|,
name|params
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"suggestion#suggestionName\":[{"
operator|+
literal|"\"text\":\"entryText\","
operator|+
literal|"\"offset\":42,"
operator|+
literal|"\"length\":313,"
operator|+
literal|"\"options\":[{"
operator|+
literal|"\"text\":\"someText\","
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
argument_list|,
name|xContent
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|PhraseSuggestion
operator|.
name|Entry
name|entry
init|=
operator|new
name|PhraseSuggestion
operator|.
name|Entry
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
argument_list|,
literal|1.0
argument_list|)
decl_stmt|;
name|entry
operator|.
name|addOption
argument_list|(
name|option
argument_list|)
expr_stmt|;
name|PhraseSuggestion
name|suggestion
init|=
operator|new
name|PhraseSuggestion
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
name|BytesReference
name|xContent
init|=
name|toXContent
argument_list|(
name|suggestion
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|,
name|params
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"phrase#suggestionName\":[{"
operator|+
literal|"\"text\":\"entryText\","
operator|+
literal|"\"offset\":42,"
operator|+
literal|"\"length\":313,"
operator|+
literal|"\"options\":[{"
operator|+
literal|"\"text\":\"someText\","
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
argument_list|,
name|xContent
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
name|TermSuggestion
operator|.
name|Entry
operator|.
name|Option
name|option
init|=
operator|new
name|TermSuggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|(
operator|new
name|Text
argument_list|(
literal|"someText"
argument_list|)
argument_list|,
literal|10
argument_list|,
literal|1.3f
argument_list|)
decl_stmt|;
name|TermSuggestion
operator|.
name|Entry
name|entry
init|=
operator|new
name|TermSuggestion
operator|.
name|Entry
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
name|TermSuggestion
name|suggestion
init|=
operator|new
name|TermSuggestion
argument_list|(
literal|"suggestionName"
argument_list|,
literal|5
argument_list|,
name|SortBy
operator|.
name|SCORE
argument_list|)
decl_stmt|;
name|suggestion
operator|.
name|addTerm
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|BytesReference
name|xContent
init|=
name|toXContent
argument_list|(
name|suggestion
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|,
name|params
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"term#suggestionName\":[{"
operator|+
literal|"\"text\":\"entryText\","
operator|+
literal|"\"offset\":42,"
operator|+
literal|"\"length\":313,"
operator|+
literal|"\"options\":[{"
operator|+
literal|"\"text\":\"someText\","
operator|+
literal|"\"score\":1.3,"
operator|+
literal|"\"freq\":10}]"
operator|+
literal|"}]"
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
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|CharSequence
argument_list|>
argument_list|>
name|contexts
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"key"
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
literal|"value"
argument_list|)
argument_list|)
decl_stmt|;
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
name|option
init|=
operator|new
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|(
literal|1
argument_list|,
operator|new
name|Text
argument_list|(
literal|"someText"
argument_list|)
argument_list|,
literal|1.3f
argument_list|,
name|contexts
argument_list|)
decl_stmt|;
name|CompletionSuggestion
operator|.
name|Entry
name|entry
init|=
operator|new
name|CompletionSuggestion
operator|.
name|Entry
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
name|CompletionSuggestion
name|suggestion
init|=
operator|new
name|CompletionSuggestion
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
name|BytesReference
name|xContent
init|=
name|toXContent
argument_list|(
name|suggestion
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|,
name|params
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"completion#suggestionName\":[{"
operator|+
literal|"\"text\":\"entryText\","
operator|+
literal|"\"offset\":42,"
operator|+
literal|"\"length\":313,"
operator|+
literal|"\"options\":[{"
operator|+
literal|"\"text\":\"someText\","
operator|+
literal|"\"score\":1.3,"
operator|+
literal|"\"contexts\":{\"key\":[\"value\"]}"
operator|+
literal|"}]"
operator|+
literal|"}]}"
argument_list|,
name|xContent
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

