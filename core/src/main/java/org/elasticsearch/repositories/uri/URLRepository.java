begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories.uri
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|uri
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
name|blobstore
operator|.
name|url
operator|.
name|URLBlobStore
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
name|settings
operator|.
name|Setting
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
name|Setting
operator|.
name|Property
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
name|util
operator|.
name|URIPattern
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
name|RepositoryException
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
name|util
operator|.
name|Arrays
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * Read-only URL-based implementation of the BlobStoreRepository  *<p>  * This repository supports the following settings  *<dl>  *<dt>{@code url}</dt><dd>URL to the root of repository. This is mandatory parameter.</dd>  *<dt>{@code concurrent_streams}</dt><dd>Number of concurrent read/write stream (per repository on each node). Defaults to 5.</dd>  *</dl>  */
end_comment

begin_class
DECL|class|URLRepository
specifier|public
class|class
name|URLRepository
extends|extends
name|BlobStoreRepository
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"url"
decl_stmt|;
DECL|field|SUPPORTED_PROTOCOLS_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|SUPPORTED_PROTOCOLS_SETTING
init|=
name|Setting
operator|.
name|listSetting
argument_list|(
literal|"repositories.url.supported_protocols"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"http"
argument_list|,
literal|"https"
argument_list|,
literal|"ftp"
argument_list|,
literal|"file"
argument_list|,
literal|"jar"
argument_list|)
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|ALLOWED_URLS_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|List
argument_list|<
name|URIPattern
argument_list|>
argument_list|>
name|ALLOWED_URLS_SETTING
init|=
name|Setting
operator|.
name|listSetting
argument_list|(
literal|"repositories.url.allowed_urls"
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|URIPattern
operator|::
operator|new
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|URL_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|URL
argument_list|>
name|URL_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"url"
argument_list|,
literal|"http:"
argument_list|,
name|URLRepository
operator|::
name|parseURL
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|REPOSITORIES_URL_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|URL
argument_list|>
name|REPOSITORIES_URL_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"repositories.url.url"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|.
name|get
argument_list|(
literal|"repositories.uri.url"
argument_list|,
literal|"http:"
argument_list|)
argument_list|,
name|URLRepository
operator|::
name|parseURL
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|supportedProtocols
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|supportedProtocols
decl_stmt|;
DECL|field|urlWhiteList
specifier|private
specifier|final
name|URIPattern
index|[]
name|urlWhiteList
decl_stmt|;
DECL|field|environment
specifier|private
specifier|final
name|Environment
name|environment
decl_stmt|;
DECL|field|blobStore
specifier|private
specifier|final
name|URLBlobStore
name|blobStore
decl_stmt|;
DECL|field|basePath
specifier|private
specifier|final
name|BlobPath
name|basePath
decl_stmt|;
comment|/**      * Constructs new read-only URL-based repository      *      * @param name                 repository name      * @param repositorySettings   repository settings      */
annotation|@
name|Inject
DECL|method|URLRepository
specifier|public
name|URLRepository
parameter_list|(
name|RepositoryName
name|name
parameter_list|,
name|RepositorySettings
name|repositorySettings
parameter_list|,
name|Environment
name|environment
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
argument_list|)
expr_stmt|;
if|if
condition|(
name|URL_SETTING
operator|.
name|exists
argument_list|(
name|repositorySettings
operator|.
name|settings
argument_list|()
argument_list|)
operator|==
literal|false
operator|&&
name|REPOSITORIES_URL_SETTING
operator|.
name|exists
argument_list|(
name|settings
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|name
operator|.
name|name
argument_list|()
argument_list|,
literal|"missing url"
argument_list|)
throw|;
block|}
name|supportedProtocols
operator|=
name|SUPPORTED_PROTOCOLS_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|urlWhiteList
operator|=
name|ALLOWED_URLS_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|URIPattern
index|[]
block|{}
argument_list|)
expr_stmt|;
name|this
operator|.
name|environment
operator|=
name|environment
expr_stmt|;
name|URL
name|url
init|=
name|URL_SETTING
operator|.
name|exists
argument_list|(
name|repositorySettings
operator|.
name|settings
argument_list|()
argument_list|)
condition|?
name|URL_SETTING
operator|.
name|get
argument_list|(
name|repositorySettings
operator|.
name|settings
argument_list|()
argument_list|)
else|:
name|REPOSITORIES_URL_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|URL
name|normalizedURL
init|=
name|checkURL
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|blobStore
operator|=
operator|new
name|URLBlobStore
argument_list|(
name|settings
argument_list|,
name|normalizedURL
argument_list|)
expr_stmt|;
name|basePath
operator|=
name|BlobPath
operator|.
name|cleanPath
argument_list|()
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
comment|/**      * Makes sure that the url is white listed or if it points to the local file system it matches one on of the root path in path.repo      */
DECL|method|checkURL
specifier|private
name|URL
name|checkURL
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
name|String
name|protocol
init|=
name|url
operator|.
name|getProtocol
argument_list|()
decl_stmt|;
if|if
condition|(
name|protocol
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|repositoryName
argument_list|,
literal|"unknown url protocol from URL ["
operator|+
name|url
operator|+
literal|"]"
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|supportedProtocol
range|:
name|supportedProtocols
control|)
block|{
if|if
condition|(
name|supportedProtocol
operator|.
name|equals
argument_list|(
name|protocol
argument_list|)
condition|)
block|{
try|try
block|{
if|if
condition|(
name|URIPattern
operator|.
name|match
argument_list|(
name|urlWhiteList
argument_list|,
name|url
operator|.
name|toURI
argument_list|()
argument_list|)
condition|)
block|{
comment|// URL matches white list - no additional processing is needed
return|return
name|url
return|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"cannot parse the specified url [{}]"
argument_list|,
name|url
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|repositoryName
argument_list|,
literal|"cannot parse the specified url ["
operator|+
name|url
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|// We didn't match white list - try to resolve against path.repo
name|URL
name|normalizedUrl
init|=
name|environment
operator|.
name|resolveRepoURL
argument_list|(
name|url
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalizedUrl
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"The specified url [{}] doesn't start with any repository paths specified by the path.repo setting or by {} setting: [{}] "
argument_list|,
name|url
argument_list|,
name|ALLOWED_URLS_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|environment
operator|.
name|repoFiles
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|repositoryName
argument_list|,
literal|"file url ["
operator|+
name|url
operator|+
literal|"] doesn't match any of the locations specified by path.repo or "
operator|+
name|ALLOWED_URLS_SETTING
operator|.
name|getKey
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|normalizedUrl
return|;
block|}
block|}
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|repositoryName
argument_list|,
literal|"unsupported url protocol ["
operator|+
name|protocol
operator|+
literal|"] from URL ["
operator|+
name|url
operator|+
literal|"]"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|isReadOnly
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|parseURL
specifier|private
specifier|static
name|URL
name|parseURL
parameter_list|(
name|String
name|s
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|URL
argument_list|(
name|s
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to parse URL repository setting"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

