begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch (the "Author") under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Author licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories.s3
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|s3
package|;
end_package

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
name|junit
operator|.
name|Before
import|;
end_import

begin_comment
comment|/**  * This will only run if you define in your `elasticsearch.yml` file a s3 specific proxy  * cloud.aws.s3.proxy_host: mys3proxy.company.com  * cloud.aws.s3.proxy_port: 8080  */
end_comment

begin_class
DECL|class|S3ProxiedSnapshotRestoreOverHttpsTest
specifier|public
class|class
name|S3ProxiedSnapshotRestoreOverHttpsTest
extends|extends
name|AbstractS3SnapshotRestoreTest
block|{
DECL|field|proxySet
specifier|private
name|boolean
name|proxySet
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|nodeSettings
specifier|public
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
name|Settings
name|settings
init|=
name|super
operator|.
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
decl_stmt|;
name|String
name|proxyHost
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"cloud.aws.s3.proxy_host"
argument_list|)
decl_stmt|;
name|proxySet
operator|=
name|proxyHost
operator|!=
literal|null
expr_stmt|;
return|return
name|settings
return|;
block|}
annotation|@
name|Before
DECL|method|checkProxySettings
specifier|public
name|void
name|checkProxySettings
parameter_list|()
block|{
name|assumeTrue
argument_list|(
literal|"we are expecting proxy settings in elasticsearch.yml file"
argument_list|,
name|proxySet
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

