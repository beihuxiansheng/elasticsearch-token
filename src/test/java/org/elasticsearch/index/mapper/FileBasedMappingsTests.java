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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Files
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
name|cluster
operator|.
name|ClusterName
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
name|MappingMetaData
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
name|FileSystemUtils
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
name|ImmutableSettings
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
name|json
operator|.
name|JsonXContent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeBuilder
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
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
import|;
end_import

begin_class
DECL|class|FileBasedMappingsTests
specifier|public
class|class
name|FileBasedMappingsTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|NAME
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
name|FileBasedMappingsTests
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
DECL|method|testFileBasedMappings
specifier|public
name|void
name|testFileBasedMappings
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|configDir
init|=
name|Files
operator|.
name|createTempDir
argument_list|()
decl_stmt|;
name|File
name|mappingsDir
init|=
operator|new
name|File
argument_list|(
name|configDir
argument_list|,
literal|"mappings"
argument_list|)
decl_stmt|;
name|File
name|indexMappings
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|mappingsDir
argument_list|,
literal|"index"
argument_list|)
argument_list|,
literal|"type.json"
argument_list|)
decl_stmt|;
name|File
name|defaultMappings
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|mappingsDir
argument_list|,
literal|"_default"
argument_list|)
argument_list|,
literal|"type.json"
argument_list|)
decl_stmt|;
try|try
block|{
name|indexMappings
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|defaultMappings
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
try|try
init|(
name|XContentBuilder
name|builder
init|=
operator|new
name|XContentBuilder
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
operator|new
name|FileOutputStream
argument_list|(
name|indexMappings
argument_list|)
argument_list|)
init|)
block|{
name|builder
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
literal|"f"
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
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
try|try
init|(
name|XContentBuilder
name|builder
init|=
operator|new
name|XContentBuilder
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
operator|new
name|FileOutputStream
argument_list|(
name|defaultMappings
argument_list|)
argument_list|)
init|)
block|{
name|builder
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
literal|"g"
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
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|ClusterName
operator|.
name|SETTING
argument_list|,
name|NAME
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
name|NAME
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.conf"
argument_list|,
name|configDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"http.enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.store.type"
argument_list|,
literal|"ram"
argument_list|)
operator|.
name|put
argument_list|(
literal|"gateway.type"
argument_list|,
literal|"none"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
init|(
name|Node
name|node
init|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|local
argument_list|(
literal|true
argument_list|)
operator|.
name|data
argument_list|(
literal|true
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|node
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertAcked
argument_list|(
name|node
operator|.
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
literal|"type"
argument_list|,
literal|"h"
argument_list|,
literal|"type=string"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|GetMappingsResponse
name|response
init|=
name|node
operator|.
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
name|assertTrue
argument_list|(
name|response
operator|.
name|mappings
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|response
operator|.
name|mappings
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"index"
argument_list|)
argument_list|)
expr_stmt|;
name|MappingMetaData
name|mappings
init|=
name|response
operator|.
name|mappings
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
decl_stmt|;
name|assertNotNull
argument_list|(
name|mappings
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|properties
init|=
call|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
call|)
argument_list|(
name|mappings
operator|.
name|getSourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"properties"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"f"
argument_list|,
literal|"g"
argument_list|,
literal|"h"
argument_list|)
argument_list|,
name|properties
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|FileSystemUtils
operator|.
name|deleteRecursively
argument_list|(
name|configDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

