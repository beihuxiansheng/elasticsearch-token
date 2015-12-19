begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.hadoop.hdfs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|hadoop
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLClassLoader
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|io
operator|.
name|PathUtils
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
name|logging
operator|.
name|Loggers
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|Repository
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
name|hdfs
operator|.
name|HdfsRepository
import|;
end_import

begin_comment
comment|//
end_comment

begin_comment
comment|// Note this plugin is somewhat special as Hadoop itself loads a number of libraries and thus requires a number of permissions to run even in client mode.
end_comment

begin_comment
comment|// This poses two problems:
end_comment

begin_comment
comment|// - Hadoop itself comes with tons of jars, many providing the same classes across packages. In particular Hadoop 2 provides package annotations in the same
end_comment

begin_comment
comment|//   package across jars which trips JarHell. Thus, to allow Hadoop jars to load, the plugin uses a dedicated CL which picks them up from the hadoop-libs folder.
end_comment

begin_comment
comment|// - The issue though with using a different CL is that it picks up the jars from a different location / codeBase and thus it does not fall under the plugin
end_comment

begin_comment
comment|//   permissions. In other words, the plugin permissions don't apply to the hadoop libraries.
end_comment

begin_comment
comment|//   There are different approaches here:
end_comment

begin_comment
comment|//      - implement a custom classloader that loads the jars but 'lies' about the codesource. It is doable but since URLClassLoader is locked down, one would
end_comment

begin_comment
comment|//        would have to implement the whole jar opening and loading from it. Not impossible but still fairly low-level.
end_comment

begin_comment
comment|//        Further more, even if the code has the proper credentials, it needs to use the proper Privileged blocks to use its full permissions which does not
end_comment

begin_comment
comment|//        happen in the Hadoop code base.
end_comment

begin_comment
comment|//      - use a different Policy. Works but the Policy is JVM wide and thus the code needs to be quite efficient - quite a bit impact to cover just some plugin
end_comment

begin_comment
comment|//        libraries
end_comment

begin_comment
comment|//      - use a DomainCombiner. This doesn't change the semantics (it's clear where the code is loaded from, etc..) however it gives us a scoped, fine-grained
end_comment

begin_comment
comment|//        callback on handling the permission intersection for secured calls. Note that DC works only in the current PAC call - the moment another PA is used,
end_comment

begin_comment
comment|//        the domain combiner is going to be ignored (unless the caller specifically uses it). Due to its scoped impact and official Java support, this approach
end_comment

begin_comment
comment|//        was used.
end_comment

begin_comment
comment|// ClassLoading info
end_comment

begin_comment
comment|// - package plugin.hadoop.hdfs is part of the plugin
end_comment

begin_comment
comment|// - all the other packages are assumed to be in the nested Hadoop CL.
end_comment

begin_comment
comment|// Code
end_comment

begin_class
DECL|class|HdfsPlugin
specifier|public
class|class
name|HdfsPlugin
extends|extends
name|Plugin
block|{
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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

