begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.logging
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|logging
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|core
operator|.
name|LoggerContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|core
operator|.
name|config
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
name|logging
operator|.
name|log4j
operator|.
name|core
operator|.
name|config
operator|.
name|Configurator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|core
operator|.
name|config
operator|.
name|LoggerConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|MessageFactory
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
name|Classes
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
name|index
operator|.
name|Index
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
name|shard
operator|.
name|ShardId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
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
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|util
operator|.
name|CollectionUtils
operator|.
name|asArrayList
import|;
end_import

begin_comment
comment|/**  * A set of utilities around Logging.  */
end_comment

begin_class
DECL|class|Loggers
specifier|public
class|class
name|Loggers
block|{
DECL|field|commonPrefix
specifier|private
specifier|static
specifier|final
name|String
name|commonPrefix
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.logger.prefix"
argument_list|,
literal|"org.elasticsearch."
argument_list|)
decl_stmt|;
DECL|field|SPACE
specifier|public
specifier|static
specifier|final
name|String
name|SPACE
init|=
literal|" "
decl_stmt|;
DECL|field|consoleLoggingEnabled
specifier|private
specifier|static
name|boolean
name|consoleLoggingEnabled
init|=
literal|true
decl_stmt|;
DECL|method|disableConsoleLogging
specifier|public
specifier|static
name|void
name|disableConsoleLogging
parameter_list|()
block|{
name|consoleLoggingEnabled
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|enableConsoleLogging
specifier|public
specifier|static
name|void
name|enableConsoleLogging
parameter_list|()
block|{
name|consoleLoggingEnabled
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|consoleLoggingEnabled
specifier|public
specifier|static
name|boolean
name|consoleLoggingEnabled
parameter_list|()
block|{
return|return
name|consoleLoggingEnabled
return|;
block|}
DECL|method|getLogger
specifier|public
specifier|static
name|Logger
name|getLogger
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|ShardId
name|shardId
parameter_list|,
name|String
modifier|...
name|prefixes
parameter_list|)
block|{
return|return
name|getLogger
argument_list|(
name|clazz
argument_list|,
name|settings
argument_list|,
name|shardId
operator|.
name|getIndex
argument_list|()
argument_list|,
name|asArrayList
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
argument_list|,
name|prefixes
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Just like {@link #getLogger(Class, org.elasticsearch.common.settings.Settings, ShardId, String...)} but String loggerName instead of      * Class.      */
DECL|method|getLogger
specifier|public
specifier|static
name|Logger
name|getLogger
parameter_list|(
name|String
name|loggerName
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|ShardId
name|shardId
parameter_list|,
name|String
modifier|...
name|prefixes
parameter_list|)
block|{
return|return
name|getLogger
argument_list|(
name|loggerName
argument_list|,
name|settings
argument_list|,
name|asArrayList
argument_list|(
name|shardId
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
argument_list|,
name|prefixes
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getLogger
specifier|public
specifier|static
name|Logger
name|getLogger
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|Index
name|index
parameter_list|,
name|String
modifier|...
name|prefixes
parameter_list|)
block|{
return|return
name|getLogger
argument_list|(
name|clazz
argument_list|,
name|settings
argument_list|,
name|asArrayList
argument_list|(
name|SPACE
argument_list|,
name|index
operator|.
name|getName
argument_list|()
argument_list|,
name|prefixes
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getLogger
specifier|public
specifier|static
name|Logger
name|getLogger
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|String
modifier|...
name|prefixes
parameter_list|)
block|{
return|return
name|getLogger
argument_list|(
name|buildClassLoggerName
argument_list|(
name|clazz
argument_list|)
argument_list|,
name|settings
argument_list|,
name|prefixes
argument_list|)
return|;
block|}
DECL|method|getLogger
specifier|public
specifier|static
name|Logger
name|getLogger
parameter_list|(
name|String
name|loggerName
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|String
modifier|...
name|prefixes
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|prefixesList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|Node
operator|.
name|NODE_NAME_SETTING
operator|.
name|exists
argument_list|(
name|settings
argument_list|)
condition|)
block|{
name|prefixesList
operator|.
name|add
argument_list|(
name|Node
operator|.
name|NODE_NAME_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prefixes
operator|!=
literal|null
operator|&&
name|prefixes
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|prefixesList
operator|.
name|addAll
argument_list|(
name|asList
argument_list|(
name|prefixes
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getLogger
argument_list|(
name|getLoggerName
argument_list|(
name|loggerName
argument_list|)
argument_list|,
name|prefixesList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|prefixesList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getLogger
specifier|public
specifier|static
name|Logger
name|getLogger
parameter_list|(
name|Logger
name|parentLogger
parameter_list|,
name|String
name|s
parameter_list|)
block|{
return|return
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
name|parentLogger
operator|.
expr|<
name|MessageFactory
operator|>
name|getMessageFactory
argument_list|()
argument_list|,
name|getLoggerName
argument_list|(
name|parentLogger
operator|.
name|getName
argument_list|()
operator|+
name|s
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getLogger
specifier|public
specifier|static
name|Logger
name|getLogger
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
name|getLoggerName
argument_list|(
name|s
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getLogger
specifier|public
specifier|static
name|Logger
name|getLogger
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
name|getLoggerName
argument_list|(
name|buildClassLoggerName
argument_list|(
name|clazz
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getLogger
specifier|public
specifier|static
name|Logger
name|getLogger
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|String
modifier|...
name|prefixes
parameter_list|)
block|{
return|return
name|getLogger
argument_list|(
name|buildClassLoggerName
argument_list|(
name|clazz
argument_list|)
argument_list|,
name|prefixes
argument_list|)
return|;
block|}
DECL|method|getLogger
specifier|public
specifier|static
name|Logger
name|getLogger
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|prefixes
parameter_list|)
block|{
name|String
name|prefix
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|prefixes
operator|!=
literal|null
operator|&&
name|prefixes
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|prefixX
range|:
name|prefixes
control|)
block|{
if|if
condition|(
name|prefixX
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|prefixX
operator|.
name|equals
argument_list|(
name|SPACE
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|prefixX
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|prefix
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
name|prefix
argument_list|,
name|getLoggerName
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Set the level of the logger. If the new level is null, the logger will inherit it's level from its nearest ancestor with a non-null      * level.      */
DECL|method|setLevel
specifier|public
specifier|static
name|void
name|setLevel
parameter_list|(
name|Logger
name|logger
parameter_list|,
name|String
name|level
parameter_list|)
block|{
specifier|final
name|Level
name|l
decl_stmt|;
if|if
condition|(
name|level
operator|==
literal|null
condition|)
block|{
name|l
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|l
operator|=
name|Level
operator|.
name|valueOf
argument_list|(
name|level
argument_list|)
expr_stmt|;
block|}
name|setLevel
argument_list|(
name|logger
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
DECL|method|setLevel
specifier|public
specifier|static
name|void
name|setLevel
parameter_list|(
name|Logger
name|logger
parameter_list|,
name|Level
name|level
parameter_list|)
block|{
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|logger
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|Configurator
operator|.
name|setLevel
argument_list|(
name|logger
operator|.
name|getName
argument_list|()
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LoggerContext
name|ctx
init|=
name|LoggerContext
operator|.
name|getContext
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
name|ctx
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|LoggerConfig
name|loggerConfig
init|=
name|config
operator|.
name|getLoggerConfig
argument_list|(
name|logger
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|loggerConfig
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|updateLoggers
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|buildClassLoggerName
specifier|private
specifier|static
name|String
name|buildClassLoggerName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
name|String
name|name
init|=
name|clazz
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"org.elasticsearch."
argument_list|)
condition|)
block|{
name|name
operator|=
name|Classes
operator|.
name|getPackageName
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
DECL|method|getLoggerName
specifier|private
specifier|static
name|String
name|getLoggerName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"org.elasticsearch."
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|"org.elasticsearch."
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|commonPrefix
operator|+
name|name
return|;
block|}
block|}
end_class

end_unit

