begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.messy.tests
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|messy
operator|.
name|tests
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterState
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
name|routing
operator|.
name|IndexShardRoutingTable
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
name|geo
operator|.
name|builders
operator|.
name|ShapeBuilder
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
name|geo
operator|.
name|GeoShapeFieldMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesService
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
name|script
operator|.
name|groovy
operator|.
name|GroovyPlugin
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
name|ESIntegTestCase
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
name|Collections
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|GeoShapeIntegrationTests
specifier|public
class|class
name|GeoShapeIntegrationTests
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|nodePlugins
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
name|nodePlugins
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|GroovyPlugin
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**      * Test that orientation parameter correctly persists across cluster restart      */
DECL|method|testOrientationPersistence
specifier|public
name|void
name|testOrientationPersistence
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|idxName
init|=
literal|"orientation"
decl_stmt|;
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
literal|"shape"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"location"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"geo_shape"
argument_list|)
operator|.
name|field
argument_list|(
literal|"orientation"
argument_list|,
literal|"left"
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
comment|// create index
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
name|idxName
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"shape"
argument_list|,
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
name|mapping
operator|=
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
literal|"shape"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"location"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"geo_shape"
argument_list|)
operator|.
name|field
argument_list|(
literal|"orientation"
argument_list|,
literal|"right"
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
expr_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
name|idxName
operator|+
literal|"2"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"shape"
argument_list|,
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
name|idxName
argument_list|,
name|idxName
operator|+
literal|"2"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|fullRestart
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|(
name|idxName
argument_list|,
name|idxName
operator|+
literal|"2"
argument_list|)
expr_stmt|;
comment|// left orientation test
name|IndicesService
name|indicesService
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|,
name|findNodeName
argument_list|(
name|idxName
argument_list|)
argument_list|)
decl_stmt|;
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
name|idxName
argument_list|)
argument_list|)
decl_stmt|;
name|MappedFieldType
name|fieldType
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"location"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fieldType
argument_list|,
name|instanceOf
argument_list|(
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
name|gsfm
init|=
operator|(
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
operator|)
name|fieldType
decl_stmt|;
name|ShapeBuilder
operator|.
name|Orientation
name|orientation
init|=
name|gsfm
operator|.
name|orientation
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|orientation
argument_list|,
name|equalTo
argument_list|(
name|ShapeBuilder
operator|.
name|Orientation
operator|.
name|CLOCKWISE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|orientation
argument_list|,
name|equalTo
argument_list|(
name|ShapeBuilder
operator|.
name|Orientation
operator|.
name|LEFT
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|orientation
argument_list|,
name|equalTo
argument_list|(
name|ShapeBuilder
operator|.
name|Orientation
operator|.
name|CW
argument_list|)
argument_list|)
expr_stmt|;
comment|// right orientation test
name|indicesService
operator|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|,
name|findNodeName
argument_list|(
name|idxName
operator|+
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|indexService
operator|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
operator|(
name|idxName
operator|+
literal|"2"
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|fieldType
operator|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"location"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldType
argument_list|,
name|instanceOf
argument_list|(
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|gsfm
operator|=
operator|(
name|GeoShapeFieldMapper
operator|.
name|GeoShapeFieldType
operator|)
name|fieldType
expr_stmt|;
name|orientation
operator|=
name|gsfm
operator|.
name|orientation
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|orientation
argument_list|,
name|equalTo
argument_list|(
name|ShapeBuilder
operator|.
name|Orientation
operator|.
name|COUNTER_CLOCKWISE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|orientation
argument_list|,
name|equalTo
argument_list|(
name|ShapeBuilder
operator|.
name|Orientation
operator|.
name|RIGHT
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|orientation
argument_list|,
name|equalTo
argument_list|(
name|ShapeBuilder
operator|.
name|Orientation
operator|.
name|CCW
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|findNodeName
specifier|private
name|String
name|findNodeName
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|ClusterState
name|state
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
name|IndexShardRoutingTable
name|shard
init|=
name|state
operator|.
name|getRoutingTable
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|nodeId
init|=
name|shard
operator|.
name|assignedShards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|currentNodeId
argument_list|()
decl_stmt|;
return|return
name|state
operator|.
name|getNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

