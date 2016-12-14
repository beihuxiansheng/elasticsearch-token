begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
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
name|Before
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
DECL|class|XContentParserUtilsTests
specifier|public
class|class
name|XContentParserUtilsTests
extends|extends
name|ESTestCase
block|{
DECL|field|xContentType
specifier|private
name|XContentType
name|xContentType
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|xContentType
operator|=
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEnsureFieldName
specifier|public
name|void
name|testEnsureFieldName
parameter_list|()
throws|throws
name|IOException
block|{
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
block|{
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|createBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
comment|// Parser current token is null
name|assertNull
argument_list|(
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|)
expr_stmt|;
name|XContentParserUtils
operator|.
name|ensureFieldName
argument_list|(
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|,
name|parser
operator|::
name|getTokenLocation
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Failed to parse object: expecting token of type [FIELD_NAME] but found [null]"
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|ParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|createBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
comment|// Parser next token is a start object
name|XContentParserUtils
operator|.
name|ensureFieldName
argument_list|(
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
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Failed to parse object: expecting token of type [FIELD_NAME] but found [START_OBJECT]"
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|ParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|createBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
comment|// Moves to start object
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|is
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
comment|// Expected field name is "foo", not "test"
name|XContentParserUtils
operator|.
name|ensureFieldName
argument_list|(
name|parser
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Failed to parse object: expecting field with name [test] but found [foo]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Everything is fine
specifier|final
name|String
name|randomFieldName
init|=
name|randomAsciiOfLength
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|createBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|randomFieldName
argument_list|,
literal|0
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|is
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|XContentParserUtils
operator|.
name|ensureFieldName
argument_list|(
name|parser
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|randomFieldName
argument_list|)
expr_stmt|;
block|}
DECL|method|createBuilder
specifier|private
name|XContentBuilder
name|createBuilder
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|XContentBuilder
operator|.
name|builder
argument_list|(
name|xContentType
operator|.
name|xContent
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createParser
specifier|private
name|XContentParser
name|createParser
parameter_list|(
name|BytesReference
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|xContentType
operator|.
name|xContent
argument_list|()
operator|.
name|createParser
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

