begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.boost
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|boost
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
name|search
operator|.
name|BoostQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermQuery
import|;
end_import

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
name|index
operator|.
name|mapper
operator|.
name|DocumentFieldMappers
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
name|ParsedDocument
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
name|query
operator|.
name|QueryShardContext
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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|instanceOf
import|;
end_import

begin_class
DECL|class|CustomBoostMappingTests
specifier|public
class|class
name|CustomBoostMappingTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|field|BW_SETTINGS
specifier|private
specifier|static
specifier|final
name|Settings
name|BW_SETTINGS
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
name|Version
operator|.
name|V_2_0_0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
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
DECL|method|testBackCompatCustomBoostValues
specifier|public
name|void
name|testBackCompatCustomBoostValues
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
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"s_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"long"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"integer"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"short"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"byte"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"double"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"float"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"date"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
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
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|mapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|BW_SETTINGS
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
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
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
literal|"s_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|"s_value"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|2.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|1L
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|3.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|4.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|5.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|6.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|7.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|8.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|"20100101"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|9.0f
argument_list|)
operator|.
name|endObject
argument_list|()
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
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"s_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|6.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|7.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|8.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|9.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBackCompatFieldMappingBoostValues
specifier|public
name|void
name|testBackCompatFieldMappingBoostValues
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"s_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"keyword"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|2.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"long"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|3.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"integer"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|4.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"short"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|5.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"byte"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|6.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"double"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|7.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"float"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|8.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"date"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|9.0f
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
name|IndexService
name|indexService
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|BW_SETTINGS
argument_list|)
decl_stmt|;
name|QueryShardContext
name|context
init|=
name|indexService
operator|.
name|newQueryShardContext
argument_list|()
decl_stmt|;
name|DocumentMapper
name|mapper
init|=
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
decl_stmt|;
name|DocumentFieldMappers
name|fieldMappers
init|=
name|mapper
operator|.
name|mappers
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"s_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"s_field"
argument_list|,
literal|"s_value"
argument_list|)
operator|.
name|field
argument_list|(
literal|"l_field"
argument_list|,
literal|1L
argument_list|)
operator|.
name|field
argument_list|(
literal|"i_field"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"sh_field"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"b_field"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"d_field"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"f_field"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"date_field"
argument_list|,
literal|"20100101"
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
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"s_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"s_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|6.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|7.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|8.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|9.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"s_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"keyword"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|2.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"long"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|3.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"integer"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|4.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"short"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|5.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"byte"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|6.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"double"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|7.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"float"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|8.0f
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"date"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|9.0f
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
name|IndexService
name|indexService
init|=
name|createIndex
argument_list|(
literal|"text"
argument_list|)
decl_stmt|;
name|QueryShardContext
name|context
init|=
name|indexService
operator|.
name|newQueryShardContext
argument_list|()
decl_stmt|;
name|DocumentMapper
name|mapper
init|=
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
decl_stmt|;
name|DocumentFieldMappers
name|fieldMappers
init|=
name|mapper
operator|.
name|mappers
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"s_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|BoostQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|BoostQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|BoostQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|BoostQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|BoostQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|BoostQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|BoostQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMappers
operator|.
name|getMapper
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|termQuery
argument_list|(
literal|"0"
argument_list|,
name|context
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|BoostQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"s_field"
argument_list|,
literal|"s_value"
argument_list|)
operator|.
name|field
argument_list|(
literal|"l_field"
argument_list|,
literal|1L
argument_list|)
operator|.
name|field
argument_list|(
literal|"i_field"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"sh_field"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"b_field"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"d_field"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"f_field"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"date_field"
argument_list|,
literal|"20100101"
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
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"s_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"s_field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"l_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"i_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"sh_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"b_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"d_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"f_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

