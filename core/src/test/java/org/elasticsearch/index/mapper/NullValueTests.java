begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|compress
operator|.
name|CompressedXContent
import|;
end_import

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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
name|index
operator|.
name|IndexService
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
name|mapper
operator|.
name|MapperParsingException
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
name|ESSingleNodeTestCase
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
DECL|class|NullValueTests
specifier|public
class|class
name|NullValueTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|method|testNullNullValue
specifier|public
name|void
name|testNullNullValue
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexService
name|indexService
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|typesToTest
init|=
block|{
literal|"integer"
block|,
literal|"long"
block|,
literal|"double"
block|,
literal|"float"
block|,
literal|"short"
block|,
literal|"date"
block|,
literal|"ip"
block|,
literal|"keyword"
block|,
literal|"boolean"
block|,
literal|"byte"
block|}
decl_stmt|;
for|for
control|(
name|String
name|type
range|:
name|typesToTest
control|)
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"numeric"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|type
argument_list|)
operator|.
name|field
argument_list|(
literal|"null_value"
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
try|try
block|{
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Test should have failed because [null_value] was null."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"Property [null_value] cannot be null."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

