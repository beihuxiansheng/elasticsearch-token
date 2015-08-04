begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|geo
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchResponse
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
name|search
operator|.
name|SearchHit
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|boolQuery
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|geoPolygonQuery
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
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertHitCount
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
name|anyOf
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
annotation|@
name|ESIntegTestCase
operator|.
name|SuiteScopeTestCase
DECL|class|GeoPolygonIT
specifier|public
class|class
name|GeoPolygonIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|setupSuiteScopeCluster
specifier|protected
name|void
name|setupSuiteScopeCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|xContentBuilder
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
literal|"type1"
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
literal|"geo_point"
argument_list|)
operator|.
name|field
argument_list|(
literal|"lat_lon"
argument_list|,
literal|true
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
name|randomNumericFieldDataFormat
argument_list|()
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
decl_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type1"
argument_list|,
name|xContentBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"New York"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"location"
argument_list|)
operator|.
name|field
argument_list|(
literal|"lat"
argument_list|,
literal|40.714
argument_list|)
operator|.
name|field
argument_list|(
literal|"lon"
argument_list|,
operator|-
literal|74.006
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|,
comment|// to NY: 5.286 km
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"Times Square"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"location"
argument_list|)
operator|.
name|field
argument_list|(
literal|"lat"
argument_list|,
literal|40.759
argument_list|)
operator|.
name|field
argument_list|(
literal|"lon"
argument_list|,
operator|-
literal|73.984
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|,
comment|// to NY: 0.4621 km
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"Tribeca"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"location"
argument_list|)
operator|.
name|field
argument_list|(
literal|"lat"
argument_list|,
literal|40.718
argument_list|)
operator|.
name|field
argument_list|(
literal|"lon"
argument_list|,
operator|-
literal|74.008
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|,
comment|// to NY: 1.055 km
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"Wall Street"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"location"
argument_list|)
operator|.
name|field
argument_list|(
literal|"lat"
argument_list|,
literal|40.705
argument_list|)
operator|.
name|field
argument_list|(
literal|"lon"
argument_list|,
operator|-
literal|74.009
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|,
comment|// to NY: 1.258 km
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"5"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"Soho"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"location"
argument_list|)
operator|.
name|field
argument_list|(
literal|"lat"
argument_list|,
literal|40.725
argument_list|)
operator|.
name|field
argument_list|(
literal|"lon"
argument_list|,
operator|-
literal|74
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|,
comment|// to NY: 2.029 km
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"6"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"Greenwich Village"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"location"
argument_list|)
operator|.
name|field
argument_list|(
literal|"lat"
argument_list|,
literal|40.731
argument_list|)
operator|.
name|field
argument_list|(
literal|"lon"
argument_list|,
operator|-
literal|73.996
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|,
comment|// to NY: 8.572 km
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"7"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"Brooklyn"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"location"
argument_list|)
operator|.
name|field
argument_list|(
literal|"lat"
argument_list|,
literal|40.65
argument_list|)
operator|.
name|field
argument_list|(
literal|"lon"
argument_list|,
operator|-
literal|73.95
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|simplePolygonTest
specifier|public
name|void
name|simplePolygonTest
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
comment|// from NY
operator|.
name|setQuery
argument_list|(
name|boolQuery
argument_list|()
operator|.
name|must
argument_list|(
name|geoPolygonQuery
argument_list|(
literal|"location"
argument_list|)
operator|.
name|addPoint
argument_list|(
literal|40.7
argument_list|,
operator|-
literal|74.0
argument_list|)
operator|.
name|addPoint
argument_list|(
literal|40.7
argument_list|,
operator|-
literal|74.1
argument_list|)
operator|.
name|addPoint
argument_list|(
literal|40.8
argument_list|,
operator|-
literal|74.1
argument_list|)
operator|.
name|addPoint
argument_list|(
literal|40.8
argument_list|,
operator|-
literal|74.0
argument_list|)
operator|.
name|addPoint
argument_list|(
literal|40.7
argument_list|,
operator|-
literal|74.0
argument_list|)
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|searchResponse
operator|.
name|getHits
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|id
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"3"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"4"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"5"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|simpleUnclosedPolygon
specifier|public
name|void
name|simpleUnclosedPolygon
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
comment|// from NY
operator|.
name|setQuery
argument_list|(
name|boolQuery
argument_list|()
operator|.
name|must
argument_list|(
name|geoPolygonQuery
argument_list|(
literal|"location"
argument_list|)
operator|.
name|addPoint
argument_list|(
literal|40.7
argument_list|,
operator|-
literal|74.0
argument_list|)
operator|.
name|addPoint
argument_list|(
literal|40.7
argument_list|,
operator|-
literal|74.1
argument_list|)
operator|.
name|addPoint
argument_list|(
literal|40.8
argument_list|,
operator|-
literal|74.1
argument_list|)
operator|.
name|addPoint
argument_list|(
literal|40.8
argument_list|,
operator|-
literal|74.0
argument_list|)
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|searchResponse
operator|.
name|getHits
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|id
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"3"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"4"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"5"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

