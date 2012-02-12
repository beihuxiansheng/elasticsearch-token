begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.env
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|env
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterName
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
name|Streams
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
name|java
operator|.
name|io
operator|.
name|File
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
name|io
operator|.
name|InputStreamReader
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
name|URL
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
name|Strings
operator|.
name|cleanPath
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
name|Builder
operator|.
name|EMPTY_SETTINGS
import|;
end_import

begin_comment
comment|/**  * The environment of where things exists.  */
end_comment

begin_class
DECL|class|Environment
specifier|public
class|class
name|Environment
block|{
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|homeFile
specifier|private
specifier|final
name|File
name|homeFile
decl_stmt|;
DECL|field|workFile
specifier|private
specifier|final
name|File
name|workFile
decl_stmt|;
DECL|field|workWithClusterFile
specifier|private
specifier|final
name|File
name|workWithClusterFile
decl_stmt|;
DECL|field|dataFiles
specifier|private
specifier|final
name|File
index|[]
name|dataFiles
decl_stmt|;
DECL|field|dataWithClusterFiles
specifier|private
specifier|final
name|File
index|[]
name|dataWithClusterFiles
decl_stmt|;
DECL|field|configFile
specifier|private
specifier|final
name|File
name|configFile
decl_stmt|;
DECL|field|pluginsFile
specifier|private
specifier|final
name|File
name|pluginsFile
decl_stmt|;
DECL|field|logsFile
specifier|private
specifier|final
name|File
name|logsFile
decl_stmt|;
DECL|method|Environment
specifier|public
name|Environment
parameter_list|()
block|{
name|this
argument_list|(
name|EMPTY_SETTINGS
argument_list|)
expr_stmt|;
block|}
DECL|method|Environment
specifier|public
name|Environment
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
if|if
condition|(
name|settings
operator|.
name|get
argument_list|(
literal|"path.home"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|homeFile
operator|=
operator|new
name|File
argument_list|(
name|cleanPath
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"path.home"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|homeFile
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|.
name|get
argument_list|(
literal|"path.conf"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|configFile
operator|=
operator|new
name|File
argument_list|(
name|cleanPath
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"path.conf"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|configFile
operator|=
operator|new
name|File
argument_list|(
name|homeFile
argument_list|,
literal|"config"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|.
name|get
argument_list|(
literal|"path.plugins"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|pluginsFile
operator|=
operator|new
name|File
argument_list|(
name|cleanPath
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"path.plugins"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pluginsFile
operator|=
operator|new
name|File
argument_list|(
name|homeFile
argument_list|,
literal|"plugins"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|.
name|get
argument_list|(
literal|"path.work"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|workFile
operator|=
operator|new
name|File
argument_list|(
name|cleanPath
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"path.work"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|workFile
operator|=
operator|new
name|File
argument_list|(
name|homeFile
argument_list|,
literal|"work"
argument_list|)
expr_stmt|;
block|}
name|workWithClusterFile
operator|=
operator|new
name|File
argument_list|(
name|workFile
argument_list|,
name|ClusterName
operator|.
name|clusterNameFromSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|dataPaths
init|=
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"path.data"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataPaths
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|dataFiles
operator|=
operator|new
name|File
index|[
name|dataPaths
operator|.
name|length
index|]
expr_stmt|;
name|dataWithClusterFiles
operator|=
operator|new
name|File
index|[
name|dataPaths
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dataPaths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dataFiles
index|[
name|i
index|]
operator|=
operator|new
name|File
argument_list|(
name|dataPaths
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|dataWithClusterFiles
index|[
name|i
index|]
operator|=
operator|new
name|File
argument_list|(
name|dataFiles
index|[
name|i
index|]
argument_list|,
name|ClusterName
operator|.
name|clusterNameFromSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|dataFiles
operator|=
operator|new
name|File
index|[]
block|{
operator|new
name|File
argument_list|(
name|homeFile
argument_list|,
literal|"data"
argument_list|)
block|}
expr_stmt|;
name|dataWithClusterFiles
operator|=
operator|new
name|File
index|[]
block|{
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|homeFile
argument_list|,
literal|"data"
argument_list|)
argument_list|,
name|ClusterName
operator|.
name|clusterNameFromSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
block|}
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|.
name|get
argument_list|(
literal|"path.logs"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|logsFile
operator|=
operator|new
name|File
argument_list|(
name|cleanPath
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"path.logs"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logsFile
operator|=
operator|new
name|File
argument_list|(
name|homeFile
argument_list|,
literal|"logs"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * The settings used to build this environment.      */
DECL|method|settings
specifier|public
name|Settings
name|settings
parameter_list|()
block|{
return|return
name|this
operator|.
name|settings
return|;
block|}
comment|/**      * The home of the installation.      */
DECL|method|homeFile
specifier|public
name|File
name|homeFile
parameter_list|()
block|{
return|return
name|homeFile
return|;
block|}
comment|/**      * The work location.      */
DECL|method|workFile
specifier|public
name|File
name|workFile
parameter_list|()
block|{
return|return
name|workFile
return|;
block|}
comment|/**      * The work location with the cluster name as a sub directory.      */
DECL|method|workWithClusterFile
specifier|public
name|File
name|workWithClusterFile
parameter_list|()
block|{
return|return
name|workWithClusterFile
return|;
block|}
comment|/**      * The data location.      */
DECL|method|dataFiles
specifier|public
name|File
index|[]
name|dataFiles
parameter_list|()
block|{
return|return
name|dataFiles
return|;
block|}
comment|/**      * The data location with the cluster name as a sub directory.      */
DECL|method|dataWithClusterFiles
specifier|public
name|File
index|[]
name|dataWithClusterFiles
parameter_list|()
block|{
return|return
name|dataWithClusterFiles
return|;
block|}
comment|/**      * The config location.      */
DECL|method|configFile
specifier|public
name|File
name|configFile
parameter_list|()
block|{
return|return
name|configFile
return|;
block|}
DECL|method|pluginsFile
specifier|public
name|File
name|pluginsFile
parameter_list|()
block|{
return|return
name|pluginsFile
return|;
block|}
DECL|method|logsFile
specifier|public
name|File
name|logsFile
parameter_list|()
block|{
return|return
name|logsFile
return|;
block|}
DECL|method|resolveConfigAndLoadToString
specifier|public
name|String
name|resolveConfigAndLoadToString
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|FailedToResolveConfigException
throws|,
name|IOException
block|{
return|return
name|Streams
operator|.
name|copyToString
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|resolveConfig
argument_list|(
name|path
argument_list|)
operator|.
name|openStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|resolveConfig
specifier|public
name|URL
name|resolveConfig
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|FailedToResolveConfigException
block|{
comment|// first, try it as a path on the file system
name|File
name|f1
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|f1
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
block|{
return|return
name|f1
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
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
name|FailedToResolveConfigException
argument_list|(
literal|"Failed to resolve path ["
operator|+
name|f1
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// next, try it relative to the config location
name|File
name|f2
init|=
operator|new
name|File
argument_list|(
name|configFile
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|f2
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
block|{
return|return
name|f2
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
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
name|FailedToResolveConfigException
argument_list|(
literal|"Failed to resolve path ["
operator|+
name|f2
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// try and load it from the classpath directly
name|URL
name|resource
init|=
name|settings
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
return|return
name|resource
return|;
block|}
comment|// try and load it from the classpath with config/ prefix
if|if
condition|(
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"config/"
argument_list|)
condition|)
block|{
name|resource
operator|=
name|settings
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"config/"
operator|+
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
return|return
name|resource
return|;
block|}
block|}
throw|throw
operator|new
name|FailedToResolveConfigException
argument_list|(
literal|"Failed to resolve config path ["
operator|+
name|path
operator|+
literal|"], tried file path ["
operator|+
name|f1
operator|+
literal|"], path file ["
operator|+
name|f2
operator|+
literal|"], and classpath"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

