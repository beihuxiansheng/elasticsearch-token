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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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
name|Principal
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
name|Collections
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|UnsupportedFileSystemException
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
name|cluster
operator|.
name|metadata
operator|.
name|RepositoryMetaData
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
name|ByteSizeUnit
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
name|env
operator|.
name|Environment
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

begin_class
DECL|class|HdfsRepository
specifier|public
specifier|final
class|class
name|HdfsRepository
extends|extends
name|BlobStoreRepository
block|{
DECL|field|basePath
specifier|private
specifier|final
name|BlobPath
name|basePath
init|=
name|BlobPath
operator|.
name|cleanPath
argument_list|()
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
DECL|field|blobStore
specifier|private
name|HdfsBlobStore
name|blobStore
decl_stmt|;
comment|// buffer size passed to HDFS read/write methods
comment|// TODO: why 100KB?
DECL|field|DEFAULT_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|ByteSizeValue
name|DEFAULT_BUFFER_SIZE
init|=
operator|new
name|ByteSizeValue
argument_list|(
literal|100
argument_list|,
name|ByteSizeUnit
operator|.
name|KB
argument_list|)
decl_stmt|;
DECL|method|HdfsRepository
specifier|public
name|HdfsRepository
parameter_list|(
name|RepositoryMetaData
name|metadata
parameter_list|,
name|Environment
name|environment
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|metadata
argument_list|,
name|environment
operator|.
name|settings
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|metadata
operator|.
name|settings
argument_list|()
operator|.
name|getAsBytesSize
argument_list|(
literal|"chunk_size"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|compress
operator|=
name|metadata
operator|.
name|settings
argument_list|()
operator|.
name|getAsBoolean
argument_list|(
literal|"compress"
argument_list|,
literal|false
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
name|String
name|uriSetting
init|=
name|getMetadata
argument_list|()
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"uri"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasText
argument_list|(
name|uriSetting
argument_list|)
operator|==
literal|false
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
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|uriSetting
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"hdfs"
operator|.
name|equalsIgnoreCase
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|)
operator|==
literal|false
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
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|,
name|uriSetting
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
operator|&&
name|uri
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
operator|==
literal|false
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
name|uri
operator|.
name|getPath
argument_list|()
argument_list|,
name|uriSetting
argument_list|)
argument_list|)
throw|;
block|}
name|String
name|pathSetting
init|=
name|getMetadata
argument_list|()
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
comment|// get configuration
if|if
condition|(
name|pathSetting
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
name|int
name|bufferSize
init|=
name|getMetadata
argument_list|()
operator|.
name|settings
argument_list|()
operator|.
name|getAsBytesSize
argument_list|(
literal|"buffer_size"
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|)
operator|.
name|bytesAsInt
argument_list|()
decl_stmt|;
try|try
block|{
comment|// initialize our filecontext
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
name|FileContext
name|fileContext
init|=
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
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
block|{
return|return
name|createContext
argument_list|(
name|uri
argument_list|,
name|getMetadata
argument_list|()
operator|.
name|settings
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|blobStore
operator|=
operator|new
name|HdfsBlobStore
argument_list|(
name|fileContext
argument_list|,
name|pathSetting
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Using file-system [{}] for URI [{}], path [{}]"
argument_list|,
name|fileContext
operator|.
name|getDefaultFileSystem
argument_list|()
argument_list|,
name|fileContext
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
argument_list|,
name|pathSetting
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
literal|"Cannot create HDFS repository for uri [%s]"
argument_list|,
name|uri
argument_list|)
argument_list|,
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
comment|// create hadoop filecontext
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"lesser of two evils (the other being a bunch of JNI/classloader nightmares)"
argument_list|)
DECL|method|createContext
specifier|private
specifier|static
name|FileContext
name|createContext
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Settings
name|repositorySettings
parameter_list|)
block|{
name|Configuration
name|cfg
init|=
operator|new
name|Configuration
argument_list|(
name|repositorySettings
operator|.
name|getAsBoolean
argument_list|(
literal|"load_defaults"
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|cfg
operator|.
name|setClassLoader
argument_list|(
name|HdfsRepository
operator|.
name|class
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
comment|// create a hadoop user. if we want some auth, it must be done different anyway, and tested.
name|Subject
name|subject
decl_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.hadoop.security.User"
argument_list|)
decl_stmt|;
name|Constructor
argument_list|<
name|?
argument_list|>
name|ctor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|ctor
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Principal
name|principal
init|=
operator|(
name|Principal
operator|)
name|ctor
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|)
decl_stmt|;
name|subject
operator|=
operator|new
name|Subject
argument_list|(
literal|false
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|principal
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
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
comment|// create the filecontext with our user
return|return
name|Subject
operator|.
name|doAs
argument_list|(
name|subject
argument_list|,
operator|new
name|PrivilegedAction
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
block|{
try|try
block|{
name|AbstractFileSystem
name|fs
init|=
name|AbstractFileSystem
operator|.
name|get
argument_list|(
name|uri
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
name|UnsupportedFileSystemException
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
block|}
block|}
argument_list|)
return|;
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
block|}
end_class

end_unit

