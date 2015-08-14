begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Build
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
name|http
operator|.
name|client
operator|.
name|HttpDownloadHelper
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
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|util
operator|.
name|Iterator
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
operator|.
name|settingsBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasSize
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|is
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|PluginManagerUnitTests
specifier|public
class|class
name|PluginManagerUnitTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|After
DECL|method|cleanSystemProperty
specifier|public
name|void
name|cleanSystemProperty
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|PluginManager
operator|.
name|PROPERTY_SUPPORT_STAGING_URLS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testThatConfigDirectoryCanBeOutsideOfElasticsearchHomeDirectory
specifier|public
name|void
name|testThatConfigDirectoryCanBeOutsideOfElasticsearchHomeDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|pluginName
init|=
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Path
name|homeFolder
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Path
name|genericConfigFolder
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"path.conf"
argument_list|,
name|genericConfigFolder
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|homeFolder
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Environment
name|environment
init|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|PluginManager
operator|.
name|PluginHandle
name|pluginHandle
init|=
operator|new
name|PluginManager
operator|.
name|PluginHandle
argument_list|(
name|pluginName
argument_list|,
literal|"version"
argument_list|,
literal|"user"
argument_list|,
literal|"repo"
argument_list|)
decl_stmt|;
name|String
name|configDirPath
init|=
name|Files
operator|.
name|simplifyPath
argument_list|(
name|pluginHandle
operator|.
name|configDir
argument_list|(
name|environment
argument_list|)
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|expectedDirPath
init|=
name|Files
operator|.
name|simplifyPath
argument_list|(
name|genericConfigFolder
operator|.
name|resolve
argument_list|(
name|pluginName
argument_list|)
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|configDirPath
argument_list|,
name|is
argument_list|(
name|expectedDirPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimplifiedNaming
specifier|public
name|void
name|testSimplifiedNaming
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|pluginName
init|=
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|PluginManager
operator|.
name|PluginHandle
name|handle
init|=
name|PluginManager
operator|.
name|PluginHandle
operator|.
name|parse
argument_list|(
name|pluginName
argument_list|)
decl_stmt|;
name|boolean
name|supportStagingUrls
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|supportStagingUrls
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|PluginManager
operator|.
name|PROPERTY_SUPPORT_STAGING_URLS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|URL
argument_list|>
name|iterator
init|=
name|handle
operator|.
name|urls
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|supportStagingUrls
condition|)
block|{
name|String
name|expectedStagingURL
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"http://download.elastic.co/elasticsearch/staging/%s/org/elasticsearch/plugin/elasticsearch-%s/%s/elasticsearch-%s-%s.zip"
argument_list|,
name|Build
operator|.
name|CURRENT
operator|.
name|hashShort
argument_list|()
argument_list|,
name|pluginName
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|number
argument_list|()
argument_list|,
name|pluginName
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|number
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
operator|new
name|URL
argument_list|(
name|expectedStagingURL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|URL
name|expected
init|=
operator|new
name|URL
argument_list|(
literal|"http"
argument_list|,
literal|"download.elastic.co"
argument_list|,
literal|"/elasticsearch/release/org/elasticsearch/plugin/elasticsearch-"
operator|+
name|pluginName
operator|+
literal|"/"
operator|+
name|Version
operator|.
name|CURRENT
operator|.
name|number
argument_list|()
operator|+
literal|"/elasticsearch-"
operator|+
name|pluginName
operator|+
literal|"-"
operator|+
name|Version
operator|.
name|CURRENT
operator|.
name|number
argument_list|()
operator|+
literal|".zip"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrimmingElasticsearchFromOfficialPluginName
specifier|public
name|void
name|testTrimmingElasticsearchFromOfficialPluginName
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|randomPluginName
init|=
name|randomFrom
argument_list|(
name|PluginManager
operator|.
name|OFFICIAL_PLUGINS
operator|.
name|asList
argument_list|()
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"elasticsearch-"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|PluginManager
operator|.
name|PluginHandle
name|handle
init|=
name|PluginManager
operator|.
name|PluginHandle
operator|.
name|parse
argument_list|(
name|randomPluginName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|handle
operator|.
name|name
argument_list|,
name|is
argument_list|(
name|randomPluginName
operator|.
name|replaceAll
argument_list|(
literal|"^elasticsearch-"
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|supportStagingUrls
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|supportStagingUrls
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|PluginManager
operator|.
name|PROPERTY_SUPPORT_STAGING_URLS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|URL
argument_list|>
name|iterator
init|=
name|handle
operator|.
name|urls
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|supportStagingUrls
condition|)
block|{
name|String
name|expectedStagingUrl
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"http://download.elastic.co/elasticsearch/staging/elasticsearch-%s-%s/org/elasticsearch/plugin/elasticsearch-%s/%s/elasticsearch-%s-%s.zip"
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|number
argument_list|()
argument_list|,
name|Build
operator|.
name|CURRENT
operator|.
name|hashShort
argument_list|()
argument_list|,
name|randomPluginName
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|number
argument_list|()
argument_list|,
name|randomPluginName
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|number
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
operator|new
name|URL
argument_list|(
name|expectedStagingUrl
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|releaseUrl
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"http://download.elastic.co/elasticsearch/release/org/elasticsearch/plugin/elasticsearch-%s/%s/elasticsearch-%s-%s.zip"
argument_list|,
name|randomPluginName
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|number
argument_list|()
argument_list|,
name|randomPluginName
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|number
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
operator|new
name|URL
argument_list|(
name|releaseUrl
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrimmingElasticsearchFromGithubPluginName
specifier|public
name|void
name|testTrimmingElasticsearchFromGithubPluginName
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|user
init|=
name|randomAsciiOfLength
argument_list|(
literal|6
argument_list|)
decl_stmt|;
name|String
name|randomName
init|=
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|String
name|pluginName
init|=
name|randomFrom
argument_list|(
literal|"elasticsearch-"
argument_list|,
literal|"es-"
argument_list|)
operator|+
name|randomName
decl_stmt|;
name|PluginManager
operator|.
name|PluginHandle
name|handle
init|=
name|PluginManager
operator|.
name|PluginHandle
operator|.
name|parse
argument_list|(
name|user
operator|+
literal|"/"
operator|+
name|pluginName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|handle
operator|.
name|name
argument_list|,
name|is
argument_list|(
name|randomName
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|handle
operator|.
name|urls
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|URL
name|expected
init|=
operator|new
name|URL
argument_list|(
literal|"https"
argument_list|,
literal|"github.com"
argument_list|,
literal|"/"
operator|+
name|user
operator|+
literal|"/"
operator|+
name|pluginName
operator|+
literal|"/"
operator|+
literal|"archive/master.zip"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|handle
operator|.
name|urls
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDownloadHelperChecksums
specifier|public
name|void
name|testDownloadHelperChecksums
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Sanity check to make sure the checksum functions never change how they checksum things
name|assertEquals
argument_list|(
literal|"0beec7b5ea3f0fdbc95d0dd47f3c5bc275da8a33"
argument_list|,
name|HttpDownloadHelper
operator|.
name|SHA1_CHECKSUM
operator|.
name|checksum
argument_list|(
literal|"foo"
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"acbd18db4cc2f85cedef654fccc4a4d8"
argument_list|,
name|HttpDownloadHelper
operator|.
name|MD5_CHECKSUM
operator|.
name|checksum
argument_list|(
literal|"foo"
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

