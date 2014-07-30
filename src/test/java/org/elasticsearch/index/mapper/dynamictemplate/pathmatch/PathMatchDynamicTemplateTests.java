begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.dynamictemplate.pathmatch
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|dynamictemplate
operator|.
name|pathmatch
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexableField
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
name|BytesArray
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
name|FieldMappers
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
name|ParseContext
operator|.
name|Document
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
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|Streams
operator|.
name|copyToBytesFromClasspath
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
name|io
operator|.
name|Streams
operator|.
name|copyToStringFromClasspath
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
DECL|class|PathMatchDynamicTemplateTests
specifier|public
class|class
name|PathMatchDynamicTemplateTests
extends|extends
name|ElasticsearchSingleNodeTest
block|{
annotation|@
name|Test
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/dynamictemplate/pathmatch/test-mapping.json"
argument_list|)
decl_stmt|;
name|DocumentMapper
name|docMapper
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
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|byte
index|[]
name|json
init|=
name|copyToBytesFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/dynamictemplate/pathmatch/test-data.json"
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|json
argument_list|)
argument_list|)
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
name|IndexableField
name|f
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"top_level"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|FieldMappers
name|fieldMappers
init|=
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|mappers
argument_list|()
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
name|fieldMappers
operator|.
name|mapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
literal|"obj1.name"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"obj1.name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|fieldMappers
operator|=
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"obj1.name"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|mappers
argument_list|()
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
name|fieldMappers
operator|.
name|mapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
literal|"obj1.obj2.name"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"obj1.obj2.name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|fieldMappers
operator|=
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"obj1.obj2.name"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|mappers
argument_list|()
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
name|fieldMappers
operator|.
name|mapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify more complex path_match expressions
name|fieldMappers
operator|=
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"obj3.obj4.prop1"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|mappers
argument_list|()
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
block|}
block|}
end_class

end_unit

