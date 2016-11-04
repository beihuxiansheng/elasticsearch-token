begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bootstrap
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
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
name|LogEvent
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
name|ParameterizedMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Constants
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
name|ESTestCase
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
name|MockLogAppender
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|Arrays
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
name|Predicate
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
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
name|greaterThan
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|reset
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|MaxMapCountCheckTests
specifier|public
class|class
name|MaxMapCountCheckTests
extends|extends
name|ESTestCase
block|{
DECL|method|testGetMaxMapCountOnLinux
specifier|public
name|void
name|testGetMaxMapCountOnLinux
parameter_list|()
block|{
if|if
condition|(
name|Constants
operator|.
name|LINUX
condition|)
block|{
specifier|final
name|BootstrapCheck
operator|.
name|MaxMapCountCheck
name|check
init|=
operator|new
name|BootstrapCheck
operator|.
name|MaxMapCountCheck
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|check
operator|.
name|getMaxMapCount
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGetMaxMapCount
specifier|public
name|void
name|testGetMaxMapCount
parameter_list|()
throws|throws
name|IOException
throws|,
name|IllegalAccessException
block|{
specifier|final
name|long
name|procSysVmMaxMapCount
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|final
name|BufferedReader
name|reader
init|=
name|mock
argument_list|(
name|BufferedReader
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|reader
operator|.
name|readLine
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|procSysVmMaxMapCount
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|procSysVmMaxMapCountPath
init|=
name|PathUtils
operator|.
name|get
argument_list|(
literal|"/proc/sys/vm/max_map_count"
argument_list|)
decl_stmt|;
name|BootstrapCheck
operator|.
name|MaxMapCountCheck
name|check
init|=
operator|new
name|BootstrapCheck
operator|.
name|MaxMapCountCheck
argument_list|()
block|{
annotation|@
name|Override
name|BufferedReader
name|getBufferedReader
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|path
argument_list|,
name|procSysVmMaxMapCountPath
argument_list|)
expr_stmt|;
return|return
name|reader
return|;
block|}
block|}
decl_stmt|;
name|assertThat
argument_list|(
name|check
operator|.
name|getMaxMapCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|procSysVmMaxMapCount
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|reader
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|{
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
specifier|final
name|IOException
name|ioException
init|=
operator|new
name|IOException
argument_list|(
literal|"fatal"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|reader
operator|.
name|readLine
argument_list|()
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|ioException
argument_list|)
expr_stmt|;
specifier|final
name|Logger
name|logger
init|=
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"testGetMaxMapCountIOException"
argument_list|)
decl_stmt|;
specifier|final
name|MockLogAppender
name|appender
init|=
operator|new
name|MockLogAppender
argument_list|()
decl_stmt|;
name|appender
operator|.
name|start
argument_list|()
expr_stmt|;
name|appender
operator|.
name|addExpectation
argument_list|(
operator|new
name|ParameterizedMessageLoggingExpectation
argument_list|(
literal|"expected logged I/O exception"
argument_list|,
literal|"testGetMaxMapCountIOException"
argument_list|,
name|Level
operator|.
name|WARN
argument_list|,
literal|"I/O exception while trying to read [{}]"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|procSysVmMaxMapCountPath
block|}
argument_list|,
name|e
lambda|->
name|ioException
operator|==
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|Loggers
operator|.
name|addAppender
argument_list|(
name|logger
argument_list|,
name|appender
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|check
operator|.
name|getMaxMapCount
argument_list|(
name|logger
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|appender
operator|.
name|assertAllExpectationsMatched
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|reader
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|Loggers
operator|.
name|removeAppender
argument_list|(
name|logger
argument_list|,
name|appender
argument_list|)
expr_stmt|;
name|appender
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|{
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|reader
operator|.
name|readLine
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"eof"
argument_list|)
expr_stmt|;
specifier|final
name|Logger
name|logger
init|=
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"testGetMaxMapCountNumberFormatException"
argument_list|)
decl_stmt|;
specifier|final
name|MockLogAppender
name|appender
init|=
operator|new
name|MockLogAppender
argument_list|()
decl_stmt|;
name|appender
operator|.
name|start
argument_list|()
expr_stmt|;
name|appender
operator|.
name|addExpectation
argument_list|(
operator|new
name|ParameterizedMessageLoggingExpectation
argument_list|(
literal|"expected logged number format exception"
argument_list|,
literal|"testGetMaxMapCountNumberFormatException"
argument_list|,
name|Level
operator|.
name|WARN
argument_list|,
literal|"unable to parse vm.max_map_count [{}]"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"eof"
block|}
argument_list|,
name|e
lambda|->
name|e
operator|instanceof
name|NumberFormatException
operator|&&
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"For input string: \"eof\""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Loggers
operator|.
name|addAppender
argument_list|(
name|logger
argument_list|,
name|appender
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|check
operator|.
name|getMaxMapCount
argument_list|(
name|logger
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|appender
operator|.
name|assertAllExpectationsMatched
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|reader
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|Loggers
operator|.
name|removeAppender
argument_list|(
name|logger
argument_list|,
name|appender
argument_list|)
expr_stmt|;
name|appender
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ParameterizedMessageLoggingExpectation
specifier|private
specifier|static
class|class
name|ParameterizedMessageLoggingExpectation
implements|implements
name|MockLogAppender
operator|.
name|LoggingExpectation
block|{
DECL|field|saw
specifier|private
name|boolean
name|saw
init|=
literal|false
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|loggerName
specifier|private
specifier|final
name|String
name|loggerName
decl_stmt|;
DECL|field|level
specifier|private
specifier|final
name|Level
name|level
decl_stmt|;
DECL|field|messagePattern
specifier|private
specifier|final
name|String
name|messagePattern
decl_stmt|;
DECL|field|arguments
specifier|private
specifier|final
name|Object
index|[]
name|arguments
decl_stmt|;
DECL|field|throwablePredicate
specifier|private
specifier|final
name|Predicate
argument_list|<
name|Throwable
argument_list|>
name|throwablePredicate
decl_stmt|;
DECL|method|ParameterizedMessageLoggingExpectation
specifier|private
name|ParameterizedMessageLoggingExpectation
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|loggerName
parameter_list|,
specifier|final
name|Level
name|level
parameter_list|,
specifier|final
name|String
name|messagePattern
parameter_list|,
specifier|final
name|Object
index|[]
name|arguments
parameter_list|,
specifier|final
name|Predicate
argument_list|<
name|Throwable
argument_list|>
name|throwablePredicate
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|loggerName
operator|=
name|loggerName
expr_stmt|;
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
name|this
operator|.
name|messagePattern
operator|=
name|messagePattern
expr_stmt|;
name|this
operator|.
name|arguments
operator|=
name|arguments
expr_stmt|;
name|this
operator|.
name|throwablePredicate
operator|=
name|throwablePredicate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|match
specifier|public
name|void
name|match
parameter_list|(
name|LogEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getLevel
argument_list|()
operator|.
name|equals
argument_list|(
name|level
argument_list|)
operator|&&
name|event
operator|.
name|getLoggerName
argument_list|()
operator|.
name|equals
argument_list|(
name|loggerName
argument_list|)
operator|&&
name|event
operator|.
name|getMessage
argument_list|()
operator|instanceof
name|ParameterizedMessage
condition|)
block|{
specifier|final
name|ParameterizedMessage
name|message
init|=
operator|(
name|ParameterizedMessage
operator|)
name|event
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|saw
operator|=
name|message
operator|.
name|getFormat
argument_list|()
operator|.
name|equals
argument_list|(
name|messagePattern
argument_list|)
operator|&&
name|Arrays
operator|.
name|deepEquals
argument_list|(
name|arguments
argument_list|,
name|message
operator|.
name|getParameters
argument_list|()
argument_list|)
operator|&&
name|throwablePredicate
operator|.
name|test
argument_list|(
name|event
operator|.
name|getThrown
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|assertMatched
specifier|public
name|void
name|assertMatched
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|name
argument_list|,
name|saw
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMaxMapCountCheckRead
specifier|public
name|void
name|testMaxMapCountCheckRead
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|rawProcSysVmMaxMapCount
init|=
name|Long
operator|.
name|toString
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BufferedReader
name|reader
init|=
name|mock
argument_list|(
name|BufferedReader
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|reader
operator|.
name|readLine
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rawProcSysVmMaxMapCount
argument_list|)
expr_stmt|;
specifier|final
name|BootstrapCheck
operator|.
name|MaxMapCountCheck
name|check
init|=
operator|new
name|BootstrapCheck
operator|.
name|MaxMapCountCheck
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|check
operator|.
name|readProcSysVmMaxMapCount
argument_list|(
name|reader
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|rawProcSysVmMaxMapCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxMapCountCheckParse
specifier|public
name|void
name|testMaxMapCountCheckParse
parameter_list|()
block|{
specifier|final
name|long
name|procSysVmMaxMapCount
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|final
name|BootstrapCheck
operator|.
name|MaxMapCountCheck
name|check
init|=
operator|new
name|BootstrapCheck
operator|.
name|MaxMapCountCheck
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|check
operator|.
name|parseProcSysVmMaxMapCount
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|procSysVmMaxMapCount
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|procSysVmMaxMapCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

