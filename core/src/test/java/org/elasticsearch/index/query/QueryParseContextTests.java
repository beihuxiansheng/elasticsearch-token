begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|Optional
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

begin_class
DECL|class|QueryParseContextTests
specifier|public
class|class
name|QueryParseContextTests
extends|extends
name|ESTestCase
block|{
DECL|field|indicesQueriesRegistry
specifier|private
specifier|static
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init
specifier|public
specifier|static
name|void
name|init
parameter_list|()
block|{
name|indicesQueriesRegistry
operator|=
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
operator|.
name|getQueryParserRegistry
argument_list|()
expr_stmt|;
block|}
DECL|method|testParseTopLevelBuilder
specifier|public
name|void
name|testParseTopLevelBuilder
parameter_list|()
throws|throws
name|IOException
block|{
name|QueryBuilder
name|query
init|=
operator|new
name|MatchQueryBuilder
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|String
name|requestBody
init|=
literal|"{ \"query\" : "
operator|+
name|query
operator|.
name|toString
argument_list|()
operator|+
literal|"}"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|requestBody
argument_list|)
operator|.
name|createParser
argument_list|(
name|requestBody
argument_list|)
init|)
block|{
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|QueryBuilder
name|actual
init|=
name|context
operator|.
name|parseTopLevelQueryBuilder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|query
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseTopLevelBuilderEmptyObject
specifier|public
name|void
name|testParseTopLevelBuilderEmptyObject
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|requestBody
init|=
literal|"{}"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|requestBody
argument_list|)
operator|.
name|createParser
argument_list|(
name|requestBody
argument_list|)
init|)
block|{
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|QueryBuilder
name|query
init|=
name|context
operator|.
name|parseTopLevelQueryBuilder
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseTopLevelBuilderUnknownParameter
specifier|public
name|void
name|testParseTopLevelBuilderUnknownParameter
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|requestBody
init|=
literal|"{ \"foo\" : \"bar\"}"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|requestBody
argument_list|)
operator|.
name|createParser
argument_list|(
name|requestBody
argument_list|)
init|)
block|{
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|ParsingException
name|exception
init|=
name|expectThrows
argument_list|(
name|ParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|context
operator|.
name|parseTopLevelQueryBuilder
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"request does not support [foo]"
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseInnerQueryBuilder
specifier|public
name|void
name|testParseInnerQueryBuilder
parameter_list|()
throws|throws
name|IOException
block|{
name|QueryBuilder
name|query
init|=
operator|new
name|MatchQueryBuilder
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|String
name|source
init|=
name|query
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|source
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
init|)
block|{
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|Optional
argument_list|<
name|QueryBuilder
argument_list|>
name|actual
init|=
name|context
operator|.
name|parseInnerQueryBuilder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|query
argument_list|,
name|actual
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseInnerQueryBuilderEmptyBody
specifier|public
name|void
name|testParseInnerQueryBuilderEmptyBody
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|source
init|=
literal|"{}"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|source
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
init|)
block|{
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|Optional
argument_list|<
name|QueryBuilder
argument_list|>
name|emptyQuery
init|=
name|context
operator|.
name|parseInnerQueryBuilder
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|emptyQuery
operator|.
name|isPresent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseInnerQueryBuilderExceptions
specifier|public
name|void
name|testParseInnerQueryBuilderExceptions
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|source
init|=
literal|"{ \"foo\": \"bar\" }"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
init|)
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// don't start with START_OBJECT to provoke exception
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|ParsingException
name|exception
init|=
name|expectThrows
argument_list|(
name|ParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|context
operator|.
name|parseInnerQueryBuilder
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[_na] query malformed, must start with start_object"
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|source
operator|=
literal|"{}"
expr_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
init|)
block|{
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|IllegalArgumentException
name|exception
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|context
operator|.
name|parseInnerQueryBuilder
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"query malformed, empty clause found at [1:2]"
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|source
operator|=
literal|"{ \"foo\" : \"bar\" }"
expr_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
init|)
block|{
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|ParsingException
name|exception
init|=
name|expectThrows
argument_list|(
name|ParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|context
operator|.
name|parseInnerQueryBuilder
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[_na] query malformed, no start_object after query name"
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|source
operator|=
literal|"{ \"foo\" : {} }"
expr_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
init|)
block|{
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|ParsingException
name|exception
init|=
name|expectThrows
argument_list|(
name|ParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|context
operator|.
name|parseInnerQueryBuilder
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"no [query] registered for [foo]"
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

