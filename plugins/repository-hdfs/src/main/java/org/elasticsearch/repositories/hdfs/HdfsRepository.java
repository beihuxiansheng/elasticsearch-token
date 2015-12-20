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
name|net
operator|.
name|MalformedURLException
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
name|URL
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
name|PrivilegedActionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|AbstractFileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
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
name|ElasticsearchGenerationException
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
name|Strings
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
name|blobstore
operator|.
name|BlobPath
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
name|blobstore
operator|.
name|BlobStore
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
name|inject
operator|.
name|Inject
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
name|unit
operator|.
name|ByteSizeValue
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
name|IndexShardRepository
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
name|RepositoryName
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
name|RepositorySettings
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
name|blobstore
operator|.
name|BlobStoreRepository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_class
DECL|class|HdfsRepository
specifier|public
class|class
name|HdfsRepository
extends|extends
name|BlobStoreRepository
implements|implements
name|FileContextFactory
block|{
DECL|field|TYPE
specifier|public
specifier|final
specifier|static
name|String
name|TYPE
init|=
literal|"hdfs"
decl_stmt|;
DECL|field|basePath
specifier|private
specifier|final
name|BlobPath
name|basePath
decl_stmt|;
DECL|field|chunkSize
specifier|private
specifier|final
name|ByteSizeValue
name|chunkSize
decl_stmt|;
DECL|field|compress
specifier|private
specifier|final
name|boolean
name|compress
decl_stmt|;
DECL|field|repositorySettings
specifier|private
specifier|final
name|RepositorySettings
name|repositorySettings
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|uri
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
DECL|field|fc
specifier|private
name|FileContext
name|fc
decl_stmt|;
DECL|field|blobStore
specifier|private
name|HdfsBlobStore
name|blobStore
decl_stmt|;
annotation|@
name|Inject
DECL|method|HdfsRepository
specifier|public
name|HdfsRepository
parameter_list|(
name|RepositoryName
name|name
parameter_list|,
name|RepositorySettings
name|repositorySettings
parameter_list|,
name|IndexShardRepository
name|indexShardRepository
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|name
operator|.
name|getName
argument_list|()
argument_list|,
name|repositorySettings
argument_list|,
name|indexShardRepository
argument_list|)
expr_stmt|;
name|this
operator|.
name|repositorySettings
operator|=
name|repositorySettings
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|uri
operator|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"uri"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"uri"
argument_list|)
argument_list|)
expr_stmt|;
name|path
operator|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|basePath
operator|=
name|BlobPath
operator|.
name|cleanPath
argument_list|()
expr_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|getAsBytesSize
argument_list|(
literal|"chunk_size"
argument_list|,
name|settings
operator|.
name|getAsBytesSize
argument_list|(
literal|"chunk_size"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|compress
operator|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|getAsBoolean
argument_list|(
literal|"compress"
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"compress"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasText
argument_list|(
name|uri
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No 'uri' defined for hdfs snapshot/restore"
argument_list|)
throw|;
block|}
name|URI
name|actualUri
init|=
name|URI
operator|.
name|create
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|String
name|scheme
init|=
name|actualUri
operator|.
name|getScheme
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasText
argument_list|(
name|scheme
argument_list|)
operator|||
operator|!
name|scheme
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hdfs"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid scheme [%s] specified in uri [%s]; only 'hdfs' uri allowed for hdfs snapshot/restore"
argument_list|,
name|scheme
argument_list|,
name|uri
argument_list|)
argument_list|)
throw|;
block|}
name|String
name|p
init|=
name|actualUri
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasText
argument_list|(
name|p
argument_list|)
operator|&&
operator|!
name|p
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Use 'path' option to specify a path [%s], not the uri [%s] for hdfs snapshot/restore"
argument_list|,
name|p
argument_list|,
name|uri
argument_list|)
argument_list|)
throw|;
block|}
comment|// get configuration
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No 'path' defined for hdfs snapshot/restore"
argument_list|)
throw|;
block|}
try|try
block|{
name|fc
operator|=
name|getFileContext
argument_list|()
expr_stmt|;
name|Path
name|hdfsPath
init|=
name|SecurityUtils
operator|.
name|execute
argument_list|(
name|fc
argument_list|,
operator|new
name|FcCallback
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Path
name|doInHdfs
parameter_list|(
name|FileContext
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fc
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Using file-system [{}] for URI [{}], path [{}]"
argument_list|,
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
argument_list|,
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
argument_list|,
name|hdfsPath
argument_list|)
expr_stmt|;
name|blobStore
operator|=
operator|new
name|HdfsBlobStore
argument_list|(
name|settings
argument_list|,
name|this
argument_list|,
name|hdfsPath
argument_list|,
name|threadPool
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|super
operator|.
name|doStart
argument_list|()
expr_stmt|;
block|}
comment|// as the FileSystem is long-lived and might go away, make sure to check it before it's being used.
annotation|@
name|Override
DECL|method|getFileContext
specifier|public
name|FileContext
name|getFileContext
parameter_list|()
throws|throws
name|IOException
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
comment|// unprivileged code such as scripts do not have SpecialPermission
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
try|try
block|{
return|return
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|FileContext
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileContext
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|doGetFileContext
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PrivilegedActionException
name|pae
parameter_list|)
block|{
name|Throwable
name|th
init|=
name|pae
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|th
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|th
throw|;
block|}
if|if
condition|(
name|th
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|th
throw|;
block|}
if|if
condition|(
name|th
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|th
throw|;
block|}
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
name|pae
argument_list|)
throw|;
block|}
block|}
DECL|method|doGetFileContext
specifier|private
name|FileContext
name|doGetFileContext
parameter_list|()
throws|throws
name|IOException
block|{
comment|// check if the fs is still alive
comment|// make a cheap call that triggers little to no security checks
if|if
condition|(
name|fc
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|fc
operator|.
name|util
argument_list|()
operator|.
name|exists
argument_list|(
name|fc
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Filesystem closed"
argument_list|)
condition|)
block|{
name|fc
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|ex
throw|;
block|}
block|}
block|}
if|if
condition|(
name|fc
operator|==
literal|null
condition|)
block|{
name|Thread
name|th
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|ClassLoader
name|oldCL
init|=
name|th
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
try|try
block|{
name|th
operator|.
name|setContextClassLoader
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|initFileContext
argument_list|(
name|repositorySettings
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|th
operator|.
name|setContextClassLoader
argument_list|(
name|oldCL
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fc
return|;
block|}
DECL|method|initFileContext
specifier|private
name|FileContext
name|initFileContext
parameter_list|(
name|RepositorySettings
name|repositorySettings
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|cfg
init|=
operator|new
name|Configuration
argument_list|(
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|getAsBoolean
argument_list|(
literal|"load_defaults"
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"load_defaults"
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|cfg
operator|.
name|setClassLoader
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|reloadConfiguration
argument_list|()
expr_stmt|;
name|String
name|confLocation
init|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"conf_location"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"conf_location"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasText
argument_list|(
name|confLocation
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|entry
range|:
name|Strings
operator|.
name|commaDelimitedListToStringArray
argument_list|(
name|confLocation
argument_list|)
control|)
block|{
name|addConfigLocation
argument_list|(
name|cfg
argument_list|,
name|entry
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|repositorySettings
operator|.
name|settings
argument_list|()
operator|.
name|getByPrefix
argument_list|(
literal|"conf."
argument_list|)
operator|.
name|getAsMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|cfg
operator|.
name|set
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchGenerationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Cannot initialize Hadoop"
argument_list|)
argument_list|,
name|th
argument_list|)
throw|;
block|}
name|URI
name|actualUri
init|=
name|URI
operator|.
name|create
argument_list|(
name|uri
argument_list|)
decl_stmt|;
try|try
block|{
comment|// disable FS cache
name|cfg
operator|.
name|setBoolean
argument_list|(
literal|"fs.hdfs.impl.disable.cache"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// create the AFS manually since through FileContext is relies on Subject.doAs for no reason at all
name|AbstractFileSystem
name|fs
init|=
name|AbstractFileSystem
operator|.
name|get
argument_list|(
name|actualUri
argument_list|,
name|cfg
argument_list|)
decl_stmt|;
return|return
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|fs
argument_list|,
name|cfg
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchGenerationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Cannot create Hdfs file-system for uri [%s]"
argument_list|,
name|actualUri
argument_list|)
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|addConfigLocation
specifier|private
name|void
name|addConfigLocation
parameter_list|(
name|Configuration
name|cfg
parameter_list|,
name|String
name|confLocation
parameter_list|)
block|{
name|URL
name|cfgURL
init|=
literal|null
decl_stmt|;
comment|// it's an URL
if|if
condition|(
operator|!
name|confLocation
operator|.
name|contains
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
name|cfgURL
operator|=
name|cfg
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|confLocation
argument_list|)
expr_stmt|;
comment|// fall back to file
if|if
condition|(
name|cfgURL
operator|==
literal|null
condition|)
block|{
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
name|path
init|=
name|PathUtils
operator|.
name|get
argument_list|(
name|confLocation
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|isReadable
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Cannot find classpath resource or file 'conf_location' [%s] defined for hdfs snapshot/restore"
argument_list|,
name|confLocation
argument_list|)
argument_list|)
throw|;
block|}
name|String
name|pathLocation
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Adding path [{}] as file [{}]"
argument_list|,
name|confLocation
argument_list|,
name|pathLocation
argument_list|)
expr_stmt|;
name|confLocation
operator|=
name|pathLocation
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Resolving path [{}] to classpath [{}]"
argument_list|,
name|confLocation
argument_list|,
name|cfgURL
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Adding path [{}] as URL"
argument_list|,
name|confLocation
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cfgURL
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|cfgURL
operator|=
operator|new
name|URL
argument_list|(
name|confLocation
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid 'conf_location' URL [%s] defined for hdfs snapshot/restore"
argument_list|,
name|confLocation
argument_list|)
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
name|cfg
operator|.
name|addResource
argument_list|(
name|cfgURL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|blobStore
specifier|protected
name|BlobStore
name|blobStore
parameter_list|()
block|{
return|return
name|blobStore
return|;
block|}
annotation|@
name|Override
DECL|method|basePath
specifier|protected
name|BlobPath
name|basePath
parameter_list|()
block|{
return|return
name|basePath
return|;
block|}
annotation|@
name|Override
DECL|method|isCompress
specifier|protected
name|boolean
name|isCompress
parameter_list|()
block|{
return|return
name|compress
return|;
block|}
annotation|@
name|Override
DECL|method|chunkSize
specifier|protected
name|ByteSizeValue
name|chunkSize
parameter_list|()
block|{
return|return
name|chunkSize
return|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
name|super
operator|.
name|doClose
argument_list|()
expr_stmt|;
comment|// TODO: FileContext does not support any close - is there really no way
comment|// to handle it?
name|fc
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

