begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.benchmark.stress
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|benchmark
operator|.
name|stress
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
name|bulk
operator|.
name|BulkRequestBuilder
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
name|bulk
operator|.
name|BulkResponse
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
name|client
operator|.
name|Requests
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
name|StopWatch
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
name|unit
operator|.
name|SizeValue
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
name|node
operator|.
name|Node
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
name|Random
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
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
name|settings
operator|.
name|ImmutableSettings
operator|.
name|settingsBuilder
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
name|matchAllQuery
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeBuilder
operator|.
name|nodeBuilder
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SingleThreadBulkStress
specifier|public
class|class
name|SingleThreadBulkStress
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
name|Exception
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|shardsCount
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.shards"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|replicaCount
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.replica"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.refresh_interval"
argument_list|,
literal|"1s"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.merge.async"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.translog.flush_threshold_ops"
argument_list|,
literal|5000
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
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
name|shardsCount
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
name|replicaCount
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Node
index|[]
name|nodes
init|=
operator|new
name|Node
index|[
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nodes
index|[
name|i
index|]
operator|=
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"node"
operator|+
name|i
argument_list|)
argument_list|)
operator|.
name|node
argument_list|()
expr_stmt|;
block|}
comment|//Node client = nodeBuilder().settings(settingsBuilder().put(settings).put("name", "client")).client(true).node();
name|Node
name|client
init|=
name|nodes
index|[
literal|0
index|]
decl_stmt|;
name|Client
name|client1
init|=
name|client
operator|.
name|client
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|client1
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type1"
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
literal|"type1"
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
name|startObject
argument_list|(
literal|"_type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"no"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"no"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"not_analyzed"
argument_list|)
operator|.
name|field
argument_list|(
literal|"omit_norms"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
comment|//                .startObject("field").field("index", "analyzed").field("omit_norms", false).endObject()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
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
literal|5000
argument_list|)
expr_stmt|;
name|StopWatch
name|stopWatch
init|=
operator|new
name|StopWatch
argument_list|()
operator|.
name|start
argument_list|()
decl_stmt|;
name|long
name|COUNT
init|=
name|SizeValue
operator|.
name|parseSizeValue
argument_list|(
literal|"2m"
argument_list|)
operator|.
name|singles
argument_list|()
decl_stmt|;
name|int
name|BATCH
init|=
literal|500
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Indexing ["
operator|+
name|COUNT
operator|+
literal|"] ..."
argument_list|)
expr_stmt|;
name|long
name|ITERS
init|=
name|COUNT
operator|/
name|BATCH
decl_stmt|;
name|long
name|i
init|=
literal|1
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<=
name|ITERS
condition|;
name|i
operator|++
control|)
block|{
name|BulkRequestBuilder
name|request
init|=
name|client1
operator|.
name|prepareBulk
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|BATCH
condition|;
name|j
operator|++
control|)
block|{
name|counter
operator|++
expr_stmt|;
name|request
operator|.
name|add
argument_list|(
name|Requests
operator|.
name|indexRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
argument_list|)
operator|.
name|source
argument_list|(
name|source
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
argument_list|,
literal|"test"
operator|+
name|counter
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BulkResponse
name|response
init|=
name|request
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|hasFailures
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"failures..."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|(
name|i
operator|*
name|BATCH
operator|)
operator|%
literal|10000
operator|)
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Indexed "
operator|+
operator|(
name|i
operator|*
name|BATCH
operator|)
operator|+
literal|" took "
operator|+
name|stopWatch
operator|.
name|stop
argument_list|()
operator|.
name|lastTaskTime
argument_list|()
argument_list|)
expr_stmt|;
name|stopWatch
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Indexing took "
operator|+
name|stopWatch
operator|.
name|totalTime
argument_list|()
operator|+
literal|", TPS "
operator|+
operator|(
operator|(
operator|(
name|double
operator|)
name|COUNT
operator|)
operator|/
name|stopWatch
operator|.
name|totalTime
argument_list|()
operator|.
name|secondsFrac
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|client
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
name|prepareRefresh
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Count: "
operator|+
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareCount
argument_list|()
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|node
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|source
specifier|private
specifier|static
name|XContentBuilder
name|source
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|nameValue
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
name|nameValue
argument_list|)
operator|.
name|endObject
argument_list|()
return|;
block|}
block|}
end_class

end_unit

