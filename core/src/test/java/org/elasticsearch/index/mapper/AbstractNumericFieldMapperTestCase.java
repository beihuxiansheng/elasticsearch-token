begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|compress
operator|.
name|CompressedXContent
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
name|plugins
operator|.
name|Plugin
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
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|InternalSettingsPlugin
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
name|VersionUtils
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
name|Collection
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
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|getRandom
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
name|containsString
import|;
end_import

begin_class
DECL|class|AbstractNumericFieldMapperTestCase
specifier|public
specifier|abstract
class|class
name|AbstractNumericFieldMapperTestCase
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|field|TYPES
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|TYPES
decl_stmt|;
DECL|field|indexService
specifier|protected
name|IndexService
name|indexService
decl_stmt|;
DECL|field|parser
specifier|protected
name|DocumentMapperParser
name|parser
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|indexService
operator|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|parser
operator|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
expr_stmt|;
name|setTypeList
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|getPlugins
parameter_list|()
block|{
return|return
name|pluginList
argument_list|(
name|InternalSettingsPlugin
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|setTypeList
specifier|protected
specifier|abstract
name|void
name|setTypeList
parameter_list|()
function_decl|;
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|type
range|:
name|TYPES
control|)
block|{
name|doTestDefaults
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestDefaults
specifier|protected
specifier|abstract
name|void
name|doTestDefaults
parameter_list|(
name|String
name|type
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|testNotIndexed
specifier|public
name|void
name|testNotIndexed
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|type
range|:
name|TYPES
control|)
block|{
name|doTestNotIndexed
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestNotIndexed
specifier|protected
specifier|abstract
name|void
name|doTestNotIndexed
parameter_list|(
name|String
name|type
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|testNoDocValues
specifier|public
name|void
name|testNoDocValues
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|type
range|:
name|TYPES
control|)
block|{
name|doTestNoDocValues
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestNoDocValues
specifier|protected
specifier|abstract
name|void
name|doTestNoDocValues
parameter_list|(
name|String
name|type
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|testStore
specifier|public
name|void
name|testStore
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|type
range|:
name|TYPES
control|)
block|{
name|doTestStore
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestStore
specifier|protected
specifier|abstract
name|void
name|doTestStore
parameter_list|(
name|String
name|type
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|testCoerce
specifier|public
name|void
name|testCoerce
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|type
range|:
name|TYPES
control|)
block|{
name|doTestCoerce
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestCoerce
specifier|protected
specifier|abstract
name|void
name|doTestCoerce
parameter_list|(
name|String
name|type
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|testIncludeInAll
specifier|public
name|void
name|testIncludeInAll
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|type
range|:
name|TYPES
control|)
block|{
name|doTestIncludeInAll
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestIncludeInAll
specifier|protected
specifier|abstract
name|void
name|doTestIncludeInAll
parameter_list|(
name|String
name|type
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|testNullValue
specifier|public
name|void
name|testNullValue
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|type
range|:
name|TYPES
control|)
block|{
name|doTestNullValue
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestNullValue
specifier|protected
specifier|abstract
name|void
name|doTestNullValue
parameter_list|(
name|String
name|type
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|testEmptyName
specifier|public
name|void
name|testEmptyName
parameter_list|()
throws|throws
name|IOException
block|{
comment|// after version 5
for|for
control|(
name|String
name|type
range|:
name|TYPES
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
literal|""
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|type
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
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|parser
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
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"name cannot be empty string"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// before 5.x
name|Version
name|oldVersion
init|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|Version
operator|.
name|V_2_0_0
argument_list|,
name|Version
operator|.
name|V_2_3_5
argument_list|)
decl_stmt|;
name|Settings
name|oldIndexSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|oldVersion
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|indexService
operator|=
name|createIndex
argument_list|(
literal|"test_old"
argument_list|,
name|oldIndexSettings
argument_list|)
expr_stmt|;
name|parser
operator|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|type
range|:
name|TYPES
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
literal|""
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|type
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
name|DocumentMapper
name|defaultMapper
init|=
name|parser
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
decl_stmt|;
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|defaultMapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
