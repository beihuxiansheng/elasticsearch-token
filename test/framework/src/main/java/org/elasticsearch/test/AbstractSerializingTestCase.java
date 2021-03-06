begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
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
name|bytes
operator|.
name|BytesArray
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

begin_class
DECL|class|AbstractSerializingTestCase
specifier|public
specifier|abstract
class|class
name|AbstractSerializingTestCase
parameter_list|<
name|T
extends|extends
name|ToXContent
operator|&
name|Writeable
parameter_list|>
extends|extends
name|AbstractWireSerializingTestCase
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * Generic test that creates new instance from the test instance and checks      * both for equality and asserts equality on the two instances.      */
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
name|NUMBER_OF_TEST_RUNS
condition|;
name|runs
operator|++
control|)
block|{
name|T
name|testInstance
init|=
name|createTestInstance
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
name|XContentBuilder
name|builder
init|=
name|toXContent
argument_list|(
name|testInstance
argument_list|,
name|xContentType
argument_list|)
decl_stmt|;
name|XContentBuilder
name|shuffled
init|=
name|shuffleXContent
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|assertParsedInstance
argument_list|(
name|xContentType
argument_list|,
name|shuffled
operator|.
name|bytes
argument_list|()
argument_list|,
name|testInstance
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|alternateVersion
range|:
name|getAlternateVersions
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|instanceAsString
init|=
name|alternateVersion
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|assertParsedInstance
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|,
operator|new
name|BytesArray
argument_list|(
name|instanceAsString
argument_list|)
argument_list|,
name|alternateVersion
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|assertParsedInstance
specifier|protected
name|void
name|assertParsedInstance
parameter_list|(
name|XContentType
name|xContentType
parameter_list|,
name|BytesReference
name|instanceAsBytes
parameter_list|,
name|T
name|expectedInstance
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|xContentType
argument_list|)
argument_list|,
name|instanceAsBytes
argument_list|)
decl_stmt|;
name|T
name|newInstance
init|=
name|parseInstance
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|newInstance
argument_list|,
name|expectedInstance
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedInstance
argument_list|,
name|newInstance
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedInstance
operator|.
name|hashCode
argument_list|()
argument_list|,
name|newInstance
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|parseInstance
specifier|protected
name|T
name|parseInstance
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|T
name|parsedInstance
init|=
name|doParseInstance
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|parsedInstance
return|;
block|}
comment|/**      * Parses to a new instance using the provided {@link XContentParser}      */
DECL|method|doParseInstance
specifier|protected
specifier|abstract
name|T
name|doParseInstance
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Renders the provided instance in XContent      *      * @param instance      *            the instance to render      * @param contentType      *            the content type to render to      */
DECL|method|toXContent
specifier|protected
name|XContentBuilder
name|toXContent
parameter_list|(
name|T
name|instance
parameter_list|,
name|XContentType
name|contentType
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|contentType
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
if|if
condition|(
name|instance
operator|.
name|isFragment
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
block|}
name|instance
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
if|if
condition|(
name|instance
operator|.
name|isFragment
argument_list|()
condition|)
block|{
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
comment|/**      * Returns alternate string representation of the instance that need to be      * tested as they are never used as output of the test instance. By default      * there are no alternate versions.      *      * These alternatives must be JSON strings.      */
DECL|method|getAlternateVersions
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|getAlternateVersions
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
block|}
end_class

end_unit

