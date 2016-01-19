begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
package|;
end_package

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
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|TimeValue
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

begin_class
DECL|class|SearchSlowLogTests
specifier|public
class|class
name|SearchSlowLogTests
extends|extends
name|ESTestCase
block|{
DECL|method|testReformatSetting
specifier|public
name|void
name|testReformatSetting
parameter_list|()
block|{
name|IndexMetaData
name|metaData
init|=
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_REFORMAT
operator|.
name|getKey
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|IndexSettings
name|settings
init|=
operator|new
name|IndexSettings
argument_list|(
name|metaData
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|SearchSlowLog
name|log
init|=
operator|new
name|SearchSlowLog
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|log
operator|.
name|isReformat
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_REFORMAT
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"true"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|log
operator|.
name|isReformat
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_REFORMAT
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"false"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|log
operator|.
name|isReformat
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|log
operator|.
name|isReformat
argument_list|()
argument_list|)
expr_stmt|;
name|metaData
operator|=
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|=
operator|new
name|IndexSettings
argument_list|(
name|metaData
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|log
operator|=
operator|new
name|SearchSlowLog
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|log
operator|.
name|isReformat
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_REFORMAT
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"NOT A BOOLEAN"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Failed to parse value [NOT A BOOLEAN] cannot be parsed to boolean [ true/1/on/yes OR false/0/off/no ]"
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|log
operator|.
name|isReformat
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testLevelSetting
specifier|public
name|void
name|testLevelSetting
parameter_list|()
block|{
name|SlowLogLevel
name|level
init|=
name|randomFrom
argument_list|(
name|SlowLogLevel
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|IndexMetaData
name|metaData
init|=
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_LEVEL
operator|.
name|getKey
argument_list|()
argument_list|,
name|level
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|IndexSettings
name|settings
init|=
operator|new
name|IndexSettings
argument_list|(
name|metaData
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|SearchSlowLog
name|log
init|=
operator|new
name|SearchSlowLog
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|level
argument_list|,
name|log
operator|.
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
name|level
operator|=
name|randomFrom
argument_list|(
name|SlowLogLevel
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_LEVEL
operator|.
name|getKey
argument_list|()
argument_list|,
name|level
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|level
argument_list|,
name|log
operator|.
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
name|level
operator|=
name|randomFrom
argument_list|(
name|SlowLogLevel
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_LEVEL
operator|.
name|getKey
argument_list|()
argument_list|,
name|level
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|level
argument_list|,
name|log
operator|.
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_LEVEL
operator|.
name|getKey
argument_list|()
argument_list|,
name|level
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|level
argument_list|,
name|log
operator|.
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SlowLogLevel
operator|.
name|TRACE
argument_list|,
name|log
operator|.
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
name|metaData
operator|=
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|=
operator|new
name|IndexSettings
argument_list|(
name|metaData
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|log
operator|=
operator|new
name|SearchSlowLog
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|log
operator|.
name|isReformat
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_LEVEL
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"NOT A LEVEL"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"No enum constant org.elasticsearch.index.SlowLogLevel.NOT A LEVEL"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|SlowLogLevel
operator|.
name|TRACE
argument_list|,
name|log
operator|.
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSetQueryLevels
specifier|public
name|void
name|testSetQueryLevels
parameter_list|()
block|{
name|IndexMetaData
name|metaData
init|=
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_TRACE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"100ms"
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_DEBUG_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"200ms"
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_INFO_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"300ms"
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_WARN_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"400ms"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|IndexSettings
name|settings
init|=
operator|new
name|IndexSettings
argument_list|(
name|metaData
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|SearchSlowLog
name|log
init|=
operator|new
name|SearchSlowLog
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|100
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryTraceThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|200
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryDebugThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|300
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryInfoThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|400
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryWarnThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_TRACE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"120ms"
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_DEBUG_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"220ms"
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_INFO_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"320ms"
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_WARN_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"420ms"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|120
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryTraceThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|220
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryDebugThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|320
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryInfoThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|420
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryWarnThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|metaData
operator|=
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryTraceThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryDebugThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryInfoThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryWarnThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|=
operator|new
name|IndexSettings
argument_list|(
name|metaData
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|log
operator|=
operator|new
name|SearchSlowLog
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryTraceThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryDebugThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryInfoThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getQueryWarnThreshold
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_TRACE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"NOT A TIME VALUE"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Failed to parse setting [index.search.slowlog.threshold.query.trace] with value [NOT A TIME VALUE] as a time value: unit is missing or unrecognized"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_DEBUG_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"NOT A TIME VALUE"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Failed to parse setting [index.search.slowlog.threshold.query.debug] with value [NOT A TIME VALUE] as a time value: unit is missing or unrecognized"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_INFO_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"NOT A TIME VALUE"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Failed to parse setting [index.search.slowlog.threshold.query.info] with value [NOT A TIME VALUE] as a time value: unit is missing or unrecognized"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_WARN_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"NOT A TIME VALUE"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Failed to parse setting [index.search.slowlog.threshold.query.warn] with value [NOT A TIME VALUE] as a time value: unit is missing or unrecognized"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSetFetchLevels
specifier|public
name|void
name|testSetFetchLevels
parameter_list|()
block|{
name|IndexMetaData
name|metaData
init|=
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_TRACE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"100ms"
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_DEBUG_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"200ms"
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_INFO_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"300ms"
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_WARN_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"400ms"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|IndexSettings
name|settings
init|=
operator|new
name|IndexSettings
argument_list|(
name|metaData
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|SearchSlowLog
name|log
init|=
operator|new
name|SearchSlowLog
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|100
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchTraceThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|200
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchDebugThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|300
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchInfoThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|400
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchWarnThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_TRACE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"120ms"
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_DEBUG_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"220ms"
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_INFO_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"320ms"
argument_list|)
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_WARN_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"420ms"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|120
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchTraceThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|220
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchDebugThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|320
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchInfoThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|420
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchWarnThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|metaData
operator|=
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchTraceThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchDebugThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchInfoThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchWarnThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|=
operator|new
name|IndexSettings
argument_list|(
name|metaData
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|log
operator|=
operator|new
name|SearchSlowLog
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchTraceThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchDebugThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchInfoThreshold
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|log
operator|.
name|getFetchWarnThreshold
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_TRACE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"NOT A TIME VALUE"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Failed to parse setting [index.search.slowlog.threshold.fetch.trace] with value [NOT A TIME VALUE] as a time value: unit is missing or unrecognized"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_DEBUG_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"NOT A TIME VALUE"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Failed to parse setting [index.search.slowlog.threshold.fetch.debug] with value [NOT A TIME VALUE] as a time value: unit is missing or unrecognized"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_INFO_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"NOT A TIME VALUE"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Failed to parse setting [index.search.slowlog.threshold.fetch.info] with value [NOT A TIME VALUE] as a time value: unit is missing or unrecognized"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|settings
operator|.
name|updateIndexMetaData
argument_list|(
name|newIndexMeta
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_WARN_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"NOT A TIME VALUE"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Failed to parse setting [index.search.slowlog.threshold.fetch.warn] with value [NOT A TIME VALUE] as a time value: unit is missing or unrecognized"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|newIndexMeta
specifier|private
name|IndexMetaData
name|newIndexMeta
parameter_list|(
name|String
name|name
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|Settings
name|build
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|indexSettings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexMetaData
name|metaData
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|name
argument_list|)
operator|.
name|settings
argument_list|(
name|build
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|metaData
return|;
block|}
block|}
end_class

end_unit

