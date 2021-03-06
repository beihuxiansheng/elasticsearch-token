begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.useragent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|useragent
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
name|Setting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|Processor
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
name|IngestPlugin
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
name|InputStream
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
name|nio
operator|.
name|file
operator|.
name|PathMatcher
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
name|StandardOpenOption
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
name|HashMap
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
name|stream
operator|.
name|Stream
import|;
end_import

begin_class
DECL|class|IngestUserAgentPlugin
specifier|public
class|class
name|IngestUserAgentPlugin
extends|extends
name|Plugin
implements|implements
name|IngestPlugin
block|{
DECL|field|CACHE_SIZE_SETTING
specifier|private
specifier|final
name|Setting
argument_list|<
name|Long
argument_list|>
name|CACHE_SIZE_SETTING
init|=
name|Setting
operator|.
name|longSetting
argument_list|(
literal|"ingest.user_agent.cache_size"
argument_list|,
literal|1000
argument_list|,
literal|0
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_PARSER_NAME
specifier|static
specifier|final
name|String
name|DEFAULT_PARSER_NAME
init|=
literal|"_default_"
decl_stmt|;
annotation|@
name|Override
DECL|method|getProcessors
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Processor
operator|.
name|Factory
argument_list|>
name|getProcessors
parameter_list|(
name|Processor
operator|.
name|Parameters
name|parameters
parameter_list|)
block|{
name|Path
name|userAgentConfigDirectory
init|=
name|parameters
operator|.
name|env
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"ingest-user-agent"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|userAgentConfigDirectory
argument_list|)
operator|==
literal|false
operator|&&
name|Files
operator|.
name|isDirectory
argument_list|(
name|userAgentConfigDirectory
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"the user agent directory ["
operator|+
name|userAgentConfigDirectory
operator|+
literal|"] containing the regex file doesn't exist"
argument_list|)
throw|;
block|}
name|long
name|cacheSize
init|=
name|CACHE_SIZE_SETTING
operator|.
name|get
argument_list|(
name|parameters
operator|.
name|env
operator|.
name|settings
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|UserAgentParser
argument_list|>
name|userAgentParsers
decl_stmt|;
try|try
block|{
name|userAgentParsers
operator|=
name|createUserAgentParsers
argument_list|(
name|userAgentConfigDirectory
argument_list|,
operator|new
name|UserAgentCache
argument_list|(
name|cacheSize
argument_list|)
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
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
name|UserAgentProcessor
operator|.
name|TYPE
argument_list|,
operator|new
name|UserAgentProcessor
operator|.
name|Factory
argument_list|(
name|userAgentParsers
argument_list|)
argument_list|)
return|;
block|}
DECL|method|createUserAgentParsers
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|UserAgentParser
argument_list|>
name|createUserAgentParsers
parameter_list|(
name|Path
name|userAgentConfigDirectory
parameter_list|,
name|UserAgentCache
name|cache
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|UserAgentParser
argument_list|>
name|userAgentParsers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|UserAgentParser
name|defaultParser
init|=
operator|new
name|UserAgentParser
argument_list|(
name|DEFAULT_PARSER_NAME
argument_list|,
name|IngestUserAgentPlugin
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/regexes.yml"
argument_list|)
argument_list|,
name|cache
argument_list|)
decl_stmt|;
name|userAgentParsers
operator|.
name|put
argument_list|(
name|DEFAULT_PARSER_NAME
argument_list|,
name|defaultParser
argument_list|)
expr_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|userAgentConfigDirectory
argument_list|)
operator|&&
name|Files
operator|.
name|isDirectory
argument_list|(
name|userAgentConfigDirectory
argument_list|)
condition|)
block|{
name|PathMatcher
name|pathMatcher
init|=
name|userAgentConfigDirectory
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getPathMatcher
argument_list|(
literal|"glob:**.yml"
argument_list|)
decl_stmt|;
try|try
init|(
name|Stream
argument_list|<
name|Path
argument_list|>
name|regexFiles
init|=
name|Files
operator|.
name|find
argument_list|(
name|userAgentConfigDirectory
argument_list|,
literal|1
argument_list|,
parameter_list|(
name|path
parameter_list|,
name|attr
parameter_list|)
lambda|->
name|attr
operator|.
name|isRegularFile
argument_list|()
operator|&&
name|pathMatcher
operator|.
name|matches
argument_list|(
name|path
argument_list|)
argument_list|)
init|)
block|{
name|Iterable
argument_list|<
name|Path
argument_list|>
name|iterable
operator|=
name|regexFiles
operator|::
name|iterator
block|;
for|for
control|(
name|Path
name|path
range|:
name|iterable
control|)
block|{
name|String
name|parserName
init|=
name|path
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|regexStream
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|path
argument_list|,
name|StandardOpenOption
operator|.
name|READ
argument_list|)
init|)
block|{
name|userAgentParsers
operator|.
name|put
argument_list|(
name|parserName
argument_list|,
operator|new
name|UserAgentParser
argument_list|(
name|parserName
argument_list|,
name|regexStream
argument_list|,
name|cache
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|userAgentParsers
argument_list|)
return|;
block|}
end_class

unit|}
end_unit

