begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.logging.log4j
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|logging
operator|.
name|log4j
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
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|PropertyConfigurator
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
name|common
operator|.
name|collect
operator|.
name|MapBuilder
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
name|env
operator|.
name|FailedToResolveConfigException
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
name|nio
operator|.
name|file
operator|.
name|*
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
name|attribute
operator|.
name|BasicFileAttributes
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|LogConfigurator
specifier|public
class|class
name|LogConfigurator
block|{
DECL|field|ALLOWED_SUFFIXES
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|ALLOWED_SUFFIXES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|".yml"
argument_list|,
literal|".yaml"
argument_list|,
literal|".json"
argument_list|,
literal|".properties"
argument_list|)
decl_stmt|;
DECL|field|loaded
specifier|private
specifier|static
name|boolean
name|loaded
decl_stmt|;
DECL|field|replacements
specifier|private
specifier|static
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|replacements
init|=
operator|new
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
operator|.
name|put
argument_list|(
literal|"console"
argument_list|,
literal|"org.elasticsearch.common.logging.log4j.ConsoleAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"async"
argument_list|,
literal|"org.apache.log4j.AsyncAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"dailyRollingFile"
argument_list|,
literal|"org.apache.log4j.DailyRollingFileAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"externallyRolledFile"
argument_list|,
literal|"org.apache.log4j.ExternallyRolledFileAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"file"
argument_list|,
literal|"org.apache.log4j.FileAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"jdbc"
argument_list|,
literal|"org.apache.log4j.jdbc.JDBCAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"jms"
argument_list|,
literal|"org.apache.log4j.net.JMSAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"lf5"
argument_list|,
literal|"org.apache.log4j.lf5.LF5Appender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"ntevent"
argument_list|,
literal|"org.apache.log4j.nt.NTEventLogAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"null"
argument_list|,
literal|"org.apache.log4j.NullAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"rollingFile"
argument_list|,
literal|"org.apache.log4j.RollingFileAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"extrasRollingFile"
argument_list|,
literal|"org.apache.log4j.rolling.RollingFileAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"smtp"
argument_list|,
literal|"org.apache.log4j.net.SMTPAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"socket"
argument_list|,
literal|"org.apache.log4j.net.SocketAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"socketHub"
argument_list|,
literal|"org.apache.log4j.net.SocketHubAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"syslog"
argument_list|,
literal|"org.apache.log4j.net.SyslogAppender"
argument_list|)
operator|.
name|put
argument_list|(
literal|"telnet"
argument_list|,
literal|"org.apache.log4j.net.TelnetAppender"
argument_list|)
comment|// policies
operator|.
name|put
argument_list|(
literal|"timeBased"
argument_list|,
literal|"org.apache.log4j.rolling.TimeBasedRollingPolicy"
argument_list|)
operator|.
name|put
argument_list|(
literal|"sizeBased"
argument_list|,
literal|"org.apache.log4j.rolling.SizeBasedTriggeringPolicy"
argument_list|)
comment|// layouts
operator|.
name|put
argument_list|(
literal|"simple"
argument_list|,
literal|"org.apache.log4j.SimpleLayout"
argument_list|)
operator|.
name|put
argument_list|(
literal|"html"
argument_list|,
literal|"org.apache.log4j.HTMLLayout"
argument_list|)
operator|.
name|put
argument_list|(
literal|"pattern"
argument_list|,
literal|"org.apache.log4j.PatternLayout"
argument_list|)
operator|.
name|put
argument_list|(
literal|"consolePattern"
argument_list|,
literal|"org.apache.log4j.PatternLayout"
argument_list|)
operator|.
name|put
argument_list|(
literal|"enhancedPattern"
argument_list|,
literal|"org.apache.log4j.EnhancedPatternLayout"
argument_list|)
operator|.
name|put
argument_list|(
literal|"ttcc"
argument_list|,
literal|"org.apache.log4j.TTCCLayout"
argument_list|)
operator|.
name|put
argument_list|(
literal|"xml"
argument_list|,
literal|"org.apache.log4j.XMLLayout"
argument_list|)
operator|.
name|immutableMap
argument_list|()
decl_stmt|;
DECL|method|configure
specifier|public
specifier|static
name|void
name|configure
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
if|if
condition|(
name|loaded
condition|)
block|{
return|return;
block|}
name|loaded
operator|=
literal|true
expr_stmt|;
name|Environment
name|environment
init|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|Settings
operator|.
name|Builder
name|settingsBuilder
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|resolveConfig
argument_list|(
name|environment
argument_list|,
name|settingsBuilder
argument_list|)
expr_stmt|;
name|settingsBuilder
operator|.
name|putProperties
argument_list|(
literal|"elasticsearch."
argument_list|,
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
operator|.
name|putProperties
argument_list|(
literal|"es."
argument_list|,
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
operator|.
name|replacePropertyPlaceholders
argument_list|()
expr_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|settingsBuilder
operator|.
name|build
argument_list|()
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
literal|"log4j."
operator|+
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|replacements
operator|.
name|containsKey
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|value
operator|=
name|replacements
operator|.
name|get
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|key
operator|.
name|endsWith
argument_list|(
literal|".value"
argument_list|)
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|key
operator|.
name|length
argument_list|()
operator|-
literal|".value"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|endsWith
argument_list|(
literal|".type"
argument_list|)
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|key
operator|.
name|length
argument_list|()
operator|-
literal|".type"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|props
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|PropertyConfigurator
operator|.
name|configure
argument_list|(
name|props
argument_list|)
expr_stmt|;
block|}
comment|/**      * sets the loaded flag to false so that logging configuration can be      * overridden. Should only be used in tests.      */
DECL|method|reset
specifier|public
specifier|static
name|void
name|reset
parameter_list|()
block|{
name|loaded
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|resolveConfig
specifier|public
specifier|static
name|void
name|resolveConfig
parameter_list|(
name|Environment
name|env
parameter_list|,
specifier|final
name|Settings
operator|.
name|Builder
name|settingsBuilder
parameter_list|)
block|{
try|try
block|{
name|Files
operator|.
name|walkFileTree
argument_list|(
name|env
operator|.
name|configFile
argument_list|()
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|FileVisitOption
operator|.
name|FOLLOW_LINKS
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
operator|new
name|SimpleFileVisitor
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|visitFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fileName
init|=
name|file
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileName
operator|.
name|startsWith
argument_list|(
literal|"logging."
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|allowedSuffix
range|:
name|ALLOWED_SUFFIXES
control|)
block|{
if|if
condition|(
name|fileName
operator|.
name|endsWith
argument_list|(
name|allowedSuffix
argument_list|)
condition|)
block|{
name|loadConfig
argument_list|(
name|file
argument_list|,
name|settingsBuilder
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Failed to load logging configuration"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
DECL|method|loadConfig
specifier|public
specifier|static
name|void
name|loadConfig
parameter_list|(
name|Path
name|file
parameter_list|,
name|Settings
operator|.
name|Builder
name|settingsBuilder
parameter_list|)
block|{
try|try
block|{
name|settingsBuilder
operator|.
name|loadFromUrl
argument_list|(
name|file
operator|.
name|toUri
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailedToResolveConfigException
decl||
name|NoClassDefFoundError
decl||
name|MalformedURLException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
end_class

end_unit

