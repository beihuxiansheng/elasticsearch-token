begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories.azure
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|azure
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|node
operator|.
name|MockNode
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
name|plugin
operator|.
name|repository
operator|.
name|azure
operator|.
name|AzureRepositoryPlugin
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_comment
comment|/**  * Azure Repository  * Main class to easily run Azure from a IDE.  * It sets all the options to run the Azure plugin and access it from Sense.  *  * In order to run this class set configure the following:  * 1) Set `-Des.path.home=` to a directory containing an ES config directory  * 2) Set `-Dcloud.azure.storage.my_account.account=account_name`  * 3) Set `-Dcloud.azure.storage.my_account.key=account_key`  *  * Then you can run REST calls like:  *<pre>  # Clean test env  curl -XDELETE localhost:9200/foo?pretty  curl -XDELETE localhost:9200/_snapshot/my_backup1?pretty  curl -XDELETE localhost:9200/_snapshot/my_backup2?pretty   # Create data  curl -XPUT localhost:9200/foo/bar/1?pretty -d '{  "foo": "bar"  }'  curl -XPOST localhost:9200/foo/_refresh?pretty  curl -XGET localhost:9200/foo/_count?pretty   # Create repository using default account  curl -XPUT localhost:9200/_snapshot/my_backup1?pretty -d '{    "type": "azure"  }'   # Backup  curl -XPOST "localhost:9200/_snapshot/my_backup1/snap1?pretty&amp;wait_for_completion=true"   # Remove data  curl -XDELETE localhost:9200/foo?pretty   # Restore data  curl -XPOST "localhost:9200/_snapshot/my_backup1/snap1/_restore?pretty&amp;wait_for_completion=true"  curl -XGET localhost:9200/foo/_count?pretty</pre>  *  * If you want to define a secondary repository:  *  * 4) Set `-Dcloud.azure.storage.my_account.default=true`  * 5) Set `-Dcloud.azure.storage.my_account2.account=account_name`  * 6) Set `-Dcloud.azure.storage.my_account2.key=account_key_secondary`  *  * Then you can run REST calls like:  *<pre>  # Remove data  curl -XDELETE localhost:9200/foo?pretty   # Create repository using account2 (secondary)  curl -XPUT localhost:9200/_snapshot/my_backup2?pretty -d '{    "type": "azure",    "settings": {      "account" : "my_account2",      "location_mode": "secondary_only"    }  }'   # Restore data from the secondary endpoint  curl -XPOST "localhost:9200/_snapshot/my_backup2/snap1/_restore?pretty&amp;wait_for_completion=true"  curl -XGET localhost:9200/foo/_count?pretty</pre>  */
end_comment

begin_class
DECL|class|AzureRepositoryF
specifier|public
class|class
name|AzureRepositoryF
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
name|Throwable
block|{
name|Settings
operator|.
name|Builder
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|settings
operator|.
name|put
argument_list|(
literal|"http.cors.enabled"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|settings
operator|.
name|put
argument_list|(
literal|"http.cors.allow-origin"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|settings
operator|.
name|put
argument_list|(
literal|"cluster.name"
argument_list|,
name|AzureRepositoryF
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Example for azure repo settings
comment|// settings.put("cloud.azure.storage.my_account1.account", "account_name");
comment|// settings.put("cloud.azure.storage.my_account1.key", "account_key");
comment|// settings.put("cloud.azure.storage.my_account1.default", true);
comment|// settings.put("cloud.azure.storage.my_account2.account", "account_name");
comment|// settings.put("cloud.azure.storage.my_account2.key", "account_key_secondary");
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Node
name|node
init|=
operator|new
name|MockNode
argument_list|(
name|settings
operator|.
name|build
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|AzureRepositoryPlugin
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|node
operator|.
name|start
argument_list|()
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

