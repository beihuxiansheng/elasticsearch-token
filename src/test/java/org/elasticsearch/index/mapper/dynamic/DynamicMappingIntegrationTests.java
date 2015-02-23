begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.dynamic
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|dynamic
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|get
operator|.
name|GetMappingsResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|StrictDynamicMappingException
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
name|ElasticsearchIntegrationTest
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
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
import|;
end_import

begin_class
DECL|class|DynamicMappingIntegrationTests
specifier|public
class|class
name|DynamicMappingIntegrationTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
comment|// https://github.com/elasticsearch/elasticsearch/issues/8423#issuecomment-64229717
annotation|@
name|Test
DECL|method|testStrictAllMapping
specifier|public
name|void
name|testStrictAllMapping
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|defaultMapping
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"_default_"
argument_list|)
operator|.
name|field
argument_list|(
literal|"dynamic"
argument_list|,
literal|"strict"
argument_list|)
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
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"index"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"_default_"
argument_list|,
name|defaultMapping
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
try|try
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StrictDynamicMappingException
name|ex
parameter_list|)
block|{
comment|// this should not be created dynamically so we expect this exception
block|}
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|Object
name|input
parameter_list|)
block|{
name|GetMappingsResponse
name|currentMapping
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetMappings
argument_list|(
literal|"index"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|currentMapping
operator|.
name|getMappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|String
name|docMapping
init|=
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
literal|"_all"
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
try|try
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|preparePutMapping
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
name|docMapping
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// the mapping was created anyway with _all enabled: true, although the index request fails so we expect the update to fail
block|}
comment|// make sure type was created
for|for
control|(
name|Client
name|client
range|:
name|cluster
argument_list|()
control|)
block|{
name|GetMappingsResponse
name|mapping
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetMappings
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setLocal
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|mapping
operator|.
name|getMappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

