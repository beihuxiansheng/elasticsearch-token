begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.stress.refresh
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|stress
operator|.
name|refresh
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
name|index
operator|.
name|query
operator|.
name|FilterBuilders
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
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|RefreshStressTest1
specifier|public
class|class
name|RefreshStressTest1
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|int
name|numberOfShards
init|=
literal|5
decl_stmt|;
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
name|loadConfigSettings
argument_list|(
literal|false
argument_list|)
operator|.
name|clusterName
argument_list|(
literal|"testCluster"
argument_list|)
operator|.
name|settings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
literal|"node1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"gateway.type"
argument_list|,
literal|"none"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
name|numberOfShards
argument_list|)
comment|//.put("path.data", new File("target/data").getAbsolutePath())
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|node
argument_list|()
decl_stmt|;
name|Node
name|node2
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
name|loadConfigSettings
argument_list|(
literal|false
argument_list|)
operator|.
name|clusterName
argument_list|(
literal|"testCluster"
argument_list|)
operator|.
name|settings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
literal|"node2"
argument_list|)
operator|.
name|put
argument_list|(
literal|"gateway.type"
argument_list|,
literal|"none"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
name|numberOfShards
argument_list|)
comment|//.put("path.data", new File("target/data").getAbsolutePath())
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|node
argument_list|()
decl_stmt|;
name|Client
name|client
init|=
name|node
operator|.
name|client
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|loop
init|=
literal|1
init|;
name|loop
operator|<
literal|1000
condition|;
name|loop
operator|++
control|)
block|{
name|String
name|indexName
init|=
literal|"testindex"
operator|+
name|loop
decl_stmt|;
name|String
name|typeName
init|=
literal|"testType"
operator|+
name|loop
decl_stmt|;
name|String
name|id
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|mapping
init|=
literal|"{ \""
operator|+
name|typeName
operator|+
literal|"\" :  {\"dynamic_templates\" : [{\"no_analyze_strings\" : {\"match_mapping_type\" : \"string\",\"match\" : \"*\",\"mapping\" : {\"type\" : \"string\",\"index\" : \"not_analyzed\"}}}]}}"
decl_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
name|indexName
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|preparePutMapping
argument_list|(
name|indexName
argument_list|)
operator|.
name|setType
argument_list|(
name|typeName
argument_list|)
operator|.
name|setSource
argument_list|(
name|mapping
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
comment|//      sleep after put mapping
comment|//      Thread.sleep(100);
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"indexing "
operator|+
name|loop
argument_list|)
expr_stmt|;
name|String
name|name
init|=
literal|"name"
operator|+
name|id
decl_stmt|;
name|client
operator|.
name|prepareIndex
argument_list|(
name|indexName
argument_list|,
name|typeName
argument_list|,
name|id
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{ \"id\": \""
operator|+
name|id
operator|+
literal|"\", \"name\": \""
operator|+
name|name
operator|+
literal|"\" }"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|(
name|indexName
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
comment|//      sleep after refresh
comment|//      Thread.sleep(100);
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"searching "
operator|+
name|loop
argument_list|)
expr_stmt|;
name|SearchResponse
name|result
init|=
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setFilter
argument_list|(
name|FilterBuilders
operator|.
name|termFilter
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"retry "
operator|+
name|loop
operator|+
literal|", "
operator|+
name|i
operator|+
literal|", previous total hits: "
operator|+
name|result
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|(
name|indexName
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|result
operator|=
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setFilter
argument_list|(
name|FilterBuilders
operator|.
name|termFilter
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|(
name|indexName
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|result
operator|=
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setFilter
argument_list|(
name|FilterBuilders
operator|.
name|termFilter
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Record found after "
operator|+
operator|(
name|i
operator|*
literal|100
operator|)
operator|+
literal|" ms, second go: "
operator|+
name|result
operator|.
name|getHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
literal|100
condition|)
block|{
if|if
condition|(
name|client
operator|.
name|prepareGet
argument_list|(
name|indexName
argument_list|,
name|typeName
argument_list|,
name|id
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|isExists
argument_list|()
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Record wasn't found after 10s but can be get by id"
argument_list|)
throw|;
else|else
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Record wasn't found after 10s and can't be get by id"
argument_list|)
throw|;
block|}
block|}
block|}
comment|//client.admin().indices().prepareDelete(indexName).execute().actionGet();
block|}
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|node2
operator|.
name|close
argument_list|()
expr_stmt|;
name|node
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

