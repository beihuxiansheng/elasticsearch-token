begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|ContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|StringEntity
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
name|XContentParser
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_comment
comment|/**  * This test works against a {@link RestHighLevelClient} subclass that simulats how custom response sections returned by  * Elasticsearch plugins can be parsed using the high level client.  */
end_comment

begin_class
DECL|class|RestHighLevelClientExtTests
specifier|public
class|class
name|RestHighLevelClientExtTests
extends|extends
name|ESTestCase
block|{
DECL|field|restHighLevelClient
specifier|private
name|RestHighLevelClient
name|restHighLevelClient
decl_stmt|;
annotation|@
name|Before
DECL|method|initClient
specifier|public
name|void
name|initClient
parameter_list|()
throws|throws
name|IOException
block|{
name|RestClient
name|restClient
init|=
name|mock
argument_list|(
name|RestClient
operator|.
name|class
argument_list|)
decl_stmt|;
name|restHighLevelClient
operator|=
operator|new
name|RestHighLevelClientExt
argument_list|(
name|restClient
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseEntityCustomResponseSection
specifier|public
name|void
name|testParseEntityCustomResponseSection
parameter_list|()
throws|throws
name|IOException
block|{
block|{
name|HttpEntity
name|jsonEntity
init|=
operator|new
name|StringEntity
argument_list|(
literal|"{\"custom1\":{ \"field\":\"value\"}}"
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
decl_stmt|;
name|BaseCustomResponseSection
name|customSection
init|=
name|restHighLevelClient
operator|.
name|parseEntity
argument_list|(
name|jsonEntity
argument_list|,
name|BaseCustomResponseSection
operator|::
name|fromXContent
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|customSection
argument_list|,
name|instanceOf
argument_list|(
name|CustomResponseSection1
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|CustomResponseSection1
name|customResponseSection1
init|=
operator|(
name|CustomResponseSection1
operator|)
name|customSection
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|customResponseSection1
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|{
name|HttpEntity
name|jsonEntity
init|=
operator|new
name|StringEntity
argument_list|(
literal|"{\"custom2\":{ \"array\": [\"item1\", \"item2\"]}}"
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
decl_stmt|;
name|BaseCustomResponseSection
name|customSection
init|=
name|restHighLevelClient
operator|.
name|parseEntity
argument_list|(
name|jsonEntity
argument_list|,
name|BaseCustomResponseSection
operator|::
name|fromXContent
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|customSection
argument_list|,
name|instanceOf
argument_list|(
name|CustomResponseSection2
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|CustomResponseSection2
name|customResponseSection2
init|=
operator|(
name|CustomResponseSection2
operator|)
name|customSection
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"item1"
block|,
literal|"item2"
block|}
argument_list|,
name|customResponseSection2
operator|.
name|values
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|RestHighLevelClientExt
specifier|private
specifier|static
class|class
name|RestHighLevelClientExt
extends|extends
name|RestHighLevelClient
block|{
DECL|method|RestHighLevelClientExt
specifier|private
name|RestHighLevelClientExt
parameter_list|(
name|RestClient
name|restClient
parameter_list|)
block|{
name|super
argument_list|(
name|restClient
argument_list|,
name|getNamedXContentsExt
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getNamedXContentsExt
specifier|private
specifier|static
name|List
argument_list|<
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|>
name|getNamedXContentsExt
parameter_list|()
block|{
name|List
argument_list|<
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|entries
operator|.
name|add
argument_list|(
operator|new
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|(
name|BaseCustomResponseSection
operator|.
name|class
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"custom1"
argument_list|)
argument_list|,
name|CustomResponseSection1
operator|::
name|fromXContent
argument_list|)
argument_list|)
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
operator|new
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|(
name|BaseCustomResponseSection
operator|.
name|class
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"custom2"
argument_list|)
argument_list|,
name|CustomResponseSection2
operator|::
name|fromXContent
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|entries
return|;
block|}
block|}
DECL|class|BaseCustomResponseSection
specifier|private
specifier|abstract
specifier|static
class|class
name|BaseCustomResponseSection
block|{
DECL|method|fromXContent
specifier|static
name|BaseCustomResponseSection
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
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
argument_list|)
expr_stmt|;
name|assertEquals
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
argument_list|)
expr_stmt|;
name|BaseCustomResponseSection
name|custom
init|=
name|parser
operator|.
name|namedObject
argument_list|(
name|BaseCustomResponseSection
operator|.
name|class
argument_list|,
name|parser
operator|.
name|currentName
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
return|return
name|custom
return|;
block|}
block|}
DECL|class|CustomResponseSection1
specifier|private
specifier|static
class|class
name|CustomResponseSection1
extends|extends
name|BaseCustomResponseSection
block|{
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|method|CustomResponseSection1
specifier|private
name|CustomResponseSection1
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|static
name|CustomResponseSection1
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
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
argument_list|)
expr_stmt|;
name|assertEquals
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
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field"
argument_list|,
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|CustomResponseSection1
name|responseSection1
init|=
operator|new
name|CustomResponseSection1
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
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
return|return
name|responseSection1
return|;
block|}
block|}
DECL|class|CustomResponseSection2
specifier|private
specifier|static
class|class
name|CustomResponseSection2
extends|extends
name|BaseCustomResponseSection
block|{
DECL|field|values
specifier|private
specifier|final
name|String
index|[]
name|values
decl_stmt|;
DECL|method|CustomResponseSection2
specifier|private
name|CustomResponseSection2
parameter_list|(
name|String
index|[]
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|static
name|CustomResponseSection2
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
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
argument_list|)
expr_stmt|;
name|assertEquals
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
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"array"
argument_list|,
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|.
name|isValue
argument_list|()
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
argument_list|,
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|)
expr_stmt|;
name|CustomResponseSection2
name|responseSection2
init|=
operator|new
name|CustomResponseSection2
argument_list|(
name|values
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|values
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
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
return|return
name|responseSection2
return|;
block|}
block|}
block|}
end_class

end_unit

