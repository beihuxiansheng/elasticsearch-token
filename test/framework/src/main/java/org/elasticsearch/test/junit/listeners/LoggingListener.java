begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.junit.listeners
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|junit
operator|.
name|listeners
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
name|Logger
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
name|ESLoggerFactory
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
name|test
operator|.
name|junit
operator|.
name|annotations
operator|.
name|TestLogging
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Result
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|RunListener
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * A {@link RunListener} that allows changing the log level for a specific test method. When a test method is annotated with the  * {@link TestLogging} annotation, the level for the specified loggers will be internally saved before the test method execution and  * overridden with the specified ones. At the end of the test method execution the original loggers levels will be restored.  *  * This class is not thread-safe. Given the static nature of the logging API, it assumes that tests are never run concurrently in the same  * JVM. For the very same reason no synchronization has been implemented regarding the save/restore process of the original loggers  * levels.  */
end_comment

begin_class
DECL|class|LoggingListener
specifier|public
class|class
name|LoggingListener
extends|extends
name|RunListener
block|{
DECL|field|previousLoggingMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|previousLoggingMap
decl_stmt|;
DECL|field|previousClassLoggingMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|previousClassLoggingMap
decl_stmt|;
DECL|field|previousPackageLoggingMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|previousPackageLoggingMap
decl_stmt|;
annotation|@
name|Override
DECL|method|testRunStarted
specifier|public
name|void
name|testRunStarted
parameter_list|(
specifier|final
name|Description
name|description
parameter_list|)
throws|throws
name|Exception
block|{
name|Package
name|testClassPackage
init|=
name|description
operator|.
name|getTestClass
argument_list|()
operator|.
name|getPackage
argument_list|()
decl_stmt|;
name|previousPackageLoggingMap
operator|=
name|processTestLogging
argument_list|(
name|testClassPackage
operator|!=
literal|null
condition|?
name|testClassPackage
operator|.
name|getAnnotation
argument_list|(
name|TestLogging
operator|.
name|class
argument_list|)
else|:
literal|null
argument_list|)
expr_stmt|;
name|previousClassLoggingMap
operator|=
name|processTestLogging
argument_list|(
name|description
operator|.
name|getAnnotation
argument_list|(
name|TestLogging
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testRunFinished
specifier|public
name|void
name|testRunFinished
parameter_list|(
specifier|final
name|Result
name|result
parameter_list|)
throws|throws
name|Exception
block|{
name|previousClassLoggingMap
operator|=
name|reset
argument_list|(
name|previousClassLoggingMap
argument_list|)
expr_stmt|;
name|previousPackageLoggingMap
operator|=
name|reset
argument_list|(
name|previousPackageLoggingMap
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testStarted
specifier|public
name|void
name|testStarted
parameter_list|(
specifier|final
name|Description
name|description
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|TestLogging
name|testLogging
init|=
name|description
operator|.
name|getAnnotation
argument_list|(
name|TestLogging
operator|.
name|class
argument_list|)
decl_stmt|;
name|previousLoggingMap
operator|=
name|processTestLogging
argument_list|(
name|testLogging
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testFinished
specifier|public
name|void
name|testFinished
parameter_list|(
specifier|final
name|Description
name|description
parameter_list|)
throws|throws
name|Exception
block|{
name|previousLoggingMap
operator|=
name|reset
argument_list|(
name|previousLoggingMap
argument_list|)
expr_stmt|;
block|}
comment|/**      * Obtain the logger with the given name.      *      * @param loggerName the logger to obtain      * @return the logger      */
DECL|method|resolveLogger
specifier|private
specifier|static
name|Logger
name|resolveLogger
parameter_list|(
name|String
name|loggerName
parameter_list|)
block|{
if|if
condition|(
name|loggerName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"_root"
argument_list|)
condition|)
block|{
return|return
name|ESLoggerFactory
operator|.
name|getRootLogger
argument_list|()
return|;
block|}
return|return
name|Loggers
operator|.
name|getLogger
argument_list|(
name|loggerName
argument_list|)
return|;
block|}
comment|/**      * Applies the test logging annotation and returns the existing logging levels.      *      * @param testLogging the test logging annotation to apply      * @return the existing logging levels      */
DECL|method|processTestLogging
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|processTestLogging
parameter_list|(
specifier|final
name|TestLogging
name|testLogging
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|getLoggersAndLevelsFromAnnotation
argument_list|(
name|testLogging
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
comment|// obtain the existing logging levels so that we can restore them at the end of the test; we have to do this separately from setting
comment|// the logging levels so that setting foo does not impact the logging level for foo.bar when we check the existing logging level for
comment|// for.bar
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|existing
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
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
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Logger
name|logger
init|=
name|resolveLogger
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|existing
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|logger
operator|.
name|getLevel
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
specifier|final
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
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Logger
name|logger
init|=
name|resolveLogger
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Loggers
operator|.
name|setLevel
argument_list|(
name|logger
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|existing
return|;
block|}
comment|/**      * Obtain the logging levels from the test logging annotation.      *      * @param testLogging the test logging annotation      * @return a map from logger name to logging level      */
DECL|method|getLoggersAndLevelsFromAnnotation
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getLoggersAndLevelsFromAnnotation
parameter_list|(
specifier|final
name|TestLogging
name|testLogging
parameter_list|)
block|{
if|if
condition|(
name|testLogging
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
comment|// use a sorted set so that we apply a parent logger before its children thus not overwriting the child setting when processing the
comment|// parent setting
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|loggersAndLevels
init|=
name|testLogging
operator|.
name|value
argument_list|()
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|loggerAndLevel
range|:
name|loggersAndLevels
control|)
block|{
specifier|final
name|String
index|[]
name|loggerAndLevelArray
init|=
name|loggerAndLevel
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|loggerAndLevelArray
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|loggerAndLevelArray
index|[
literal|0
index|]
argument_list|,
name|loggerAndLevelArray
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid test logging annotation ["
operator|+
name|loggerAndLevel
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
return|return
name|map
return|;
block|}
comment|/**      * Reset the logging levels to the state provided by the map.      *      * @param map the logging levels to apply      * @return an empty map      */
DECL|method|reset
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|reset
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|previousLogger
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Logger
name|logger
init|=
name|resolveLogger
argument_list|(
name|previousLogger
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Loggers
operator|.
name|setLevel
argument_list|(
name|logger
argument_list|,
name|previousLogger
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
block|}
end_class

end_unit

