begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories.hdfs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|hdfs
package|;
end_package

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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|SpecialPermission
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
name|SuppressForbidden
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
name|snapshots
operator|.
name|blobstore
operator|.
name|BlobStoreIndexShardRepository
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
name|repositories
operator|.
name|RepositoriesModule
import|;
end_import

begin_comment
comment|// Code
end_comment

begin_class
DECL|class|HdfsPlugin
specifier|public
specifier|final
class|class
name|HdfsPlugin
extends|extends
name|Plugin
block|{
comment|// initialize some problematic classes with elevated privileges
static|static
block|{
name|SecurityManager
name|sm
init|=
name|System
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|sm
operator|!=
literal|null
condition|)
block|{
name|sm
operator|.
name|checkPermission
argument_list|(
operator|new
name|SpecialPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
block|{
return|return
name|evilHadoopInit
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Needs a security hack for hadoop on windows, until HADOOP-XXXX is fixed"
argument_list|)
DECL|method|evilHadoopInit
specifier|private
specifier|static
name|Void
name|evilHadoopInit
parameter_list|()
block|{
comment|// hack: on Windows, Shell's clinit has a similar problem that on unix,
comment|// but here we can workaround it for now by setting hadoop home
comment|// on unix: we still want to set this to something we control, because
comment|// if the user happens to have HADOOP_HOME in their environment -> checkHadoopHome goes boom
comment|// TODO: remove THIS when hadoop is fixed
name|Path
name|hadoopHome
init|=
literal|null
decl_stmt|;
name|String
name|oldValue
init|=
literal|null
decl_stmt|;
try|try
block|{
name|hadoopHome
operator|=
name|Files
operator|.
name|createTempDirectory
argument_list|(
literal|"hadoop"
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
expr_stmt|;
name|oldValue
operator|=
name|System
operator|.
name|setProperty
argument_list|(
literal|"hadoop.home.dir"
argument_list|,
name|hadoopHome
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.hadoop.security.UserGroupInformation"
argument_list|)
expr_stmt|;
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.hadoop.util.StringUtils"
argument_list|)
expr_stmt|;
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.hadoop.util.ShutdownHookManager"
argument_list|)
expr_stmt|;
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.hadoop.conf.Configuration"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// try to clean up the hack
if|if
condition|(
name|oldValue
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"hadoop.home.dir"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"hadoop.home.dir"
argument_list|,
name|oldValue
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// try to clean up our temp dir too if we can
if|if
condition|(
name|hadoopHome
operator|!=
literal|null
condition|)
block|{
name|Files
operator|.
name|delete
argument_list|(
name|hadoopHome
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|thisIsBestEffort
parameter_list|)
block|{}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"repository-hdfs"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"HDFS Repository Plugin"
return|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|RepositoriesModule
name|repositoriesModule
parameter_list|)
block|{
name|repositoriesModule
operator|.
name|registerRepository
argument_list|(
literal|"hdfs"
argument_list|,
name|HdfsRepository
operator|.
name|class
argument_list|,
name|BlobStoreIndexShardRepository
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

