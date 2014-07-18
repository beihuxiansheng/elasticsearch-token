begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.typelevels
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|typelevels
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
name|mapper
operator|.
name|DocumentMapper
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
name|DocumentMapperParser
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
name|ElasticsearchSingleNodeTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ParseMappingTypeLevelTests
specifier|public
class|class
name|ParseMappingTypeLevelTests
extends|extends
name|ElasticsearchSingleNodeTest
block|{
annotation|@
name|Test
DECL|method|testTypeLevel
specifier|public
name|void
name|testTypeLevel
parameter_list|()
throws|throws
name|Exception
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
literal|"_source"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|false
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapperParser
name|parser
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
decl_stmt|;
name|DocumentMapper
name|mapper
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
name|mapping
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|mapper
operator|.
name|type
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mapper
operator|.
name|sourceMapper
argument_list|()
operator|.
name|enabled
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|mapper
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mapper
operator|.
name|type
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mapper
operator|.
name|sourceMapper
argument_list|()
operator|.
name|enabled
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

