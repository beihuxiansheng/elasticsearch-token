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
name|index
operator|.
name|IndexSettings
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
name|MappedFieldType
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
name|MapperService
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
name|TextFieldMapper
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|notNullValue
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
name|nullValue
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
name|sameInstance
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|QueryShardContextTests
specifier|public
class|class
name|QueryShardContextTests
extends|extends
name|ESTestCase
block|{
DECL|method|testFailIfFieldMappingNotFound
specifier|public
name|void
name|testFailIfFieldMappingNotFound
parameter_list|()
block|{
name|IndexMetaData
operator|.
name|Builder
name|indexMetadata
init|=
operator|new
name|IndexMetaData
operator|.
name|Builder
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
name|indexMetadata
operator|.
name|settings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.version.created"
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|IndexSettings
name|indexSettings
init|=
operator|new
name|IndexSettings
argument_list|(
name|indexMetadata
operator|.
name|build
argument_list|()
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|MapperService
name|mapperService
init|=
name|mock
argument_list|(
name|MapperService
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mapperService
operator|.
name|getIndexSettings
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
specifier|final
name|long
name|nowInMillis
init|=
name|randomPositiveLong
argument_list|()
decl_stmt|;
name|QueryShardContext
name|context
init|=
operator|new
name|QueryShardContext
argument_list|(
literal|0
argument_list|,
name|indexSettings
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|mapperService
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
parameter_list|()
lambda|->
name|nowInMillis
argument_list|)
decl_stmt|;
name|context
operator|.
name|setAllowUnmappedFields
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|MappedFieldType
name|fieldType
init|=
operator|new
name|TextFieldMapper
operator|.
name|TextFieldType
argument_list|()
decl_stmt|;
name|MappedFieldType
name|result
init|=
name|context
operator|.
name|failIfFieldMappingNotFound
argument_list|(
literal|"name"
argument_list|,
name|fieldType
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|,
name|sameInstance
argument_list|(
name|fieldType
argument_list|)
argument_list|)
expr_stmt|;
name|QueryShardException
name|e
init|=
name|expectThrows
argument_list|(
name|QueryShardException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|context
operator|.
name|failIfFieldMappingNotFound
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"No field mapping can be found for the field with name [name]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setAllowUnmappedFields
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|result
operator|=
name|context
operator|.
name|failIfFieldMappingNotFound
argument_list|(
literal|"name"
argument_list|,
name|fieldType
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|,
name|sameInstance
argument_list|(
name|fieldType
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|context
operator|.
name|failIfFieldMappingNotFound
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setAllowUnmappedFields
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|context
operator|.
name|setMapUnmappedFieldAsString
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|result
operator|=
name|context
operator|.
name|failIfFieldMappingNotFound
argument_list|(
literal|"name"
argument_list|,
name|fieldType
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|,
name|sameInstance
argument_list|(
name|fieldType
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|context
operator|.
name|failIfFieldMappingNotFound
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|,
name|instanceOf
argument_list|(
name|TextFieldMapper
operator|.
name|TextFieldType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
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
block|}
block|}
end_class

end_unit

