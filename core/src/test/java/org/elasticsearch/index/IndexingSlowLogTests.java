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
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
operator|.
name|Store
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
name|document
operator|.
name|LegacyIntField
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
name|document
operator|.
name|StringField
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
name|bytes
operator|.
name|BytesReference
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
name|common
operator|.
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
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
name|IndexingSlowLog
operator|.
name|SlowLogParsedDocumentPrinter
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
name|mapper
operator|.
name|ParsedDocument
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|containsString
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
name|not
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
name|startsWith
import|;
end_import

begin_class
DECL|class|IndexingSlowLogTests
specifier|public
class|class
name|IndexingSlowLogTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSlowLogParsedDocumentPrinterSourceToLog
specifier|public
name|void
name|testSlowLogParsedDocumentPrinterSourceToLog
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesReference
name|source
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|ParsedDocument
name|pd
init|=
operator|new
name|ParsedDocument
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"uid"
argument_list|,
literal|"test:id"
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|,
operator|new
name|LegacyIntField
argument_list|(
literal|"version"
argument_list|,
literal|1
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|,
literal|"id"
argument_list|,
literal|"test"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
name|source
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
literal|"foo"
argument_list|,
literal|"123"
argument_list|)
decl_stmt|;
comment|// Turning off document logging doesn't log source[]
name|SlowLogParsedDocumentPrinter
name|p
init|=
operator|new
name|SlowLogParsedDocumentPrinter
argument_list|(
name|index
argument_list|,
name|pd
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|toString
argument_list|()
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"source["
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Turning on document logging logs the whole thing
name|p
operator|=
operator|new
name|SlowLogParsedDocumentPrinter
argument_list|(
name|index
argument_list|,
name|pd
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"source[{\"foo\":\"bar\"}]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// And you can truncate the source
name|p
operator|=
operator|new
name|SlowLogParsedDocumentPrinter
argument_list|(
name|index
argument_list|,
name|pd
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"source[{\"f]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// And you can truncate the source
name|p
operator|=
operator|new
name|SlowLogParsedDocumentPrinter
argument_list|(
name|index
argument_list|,
name|pd
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"source[{\"f]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|toString
argument_list|()
argument_list|,
name|startsWith
argument_list|(
literal|"[foo/123] took"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|builder
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_REFORMAT_SETTING
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
name|IndexingSlowLog
name|log
init|=
operator|new
name|IndexingSlowLog
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_REFORMAT_SETTING
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_REFORMAT_SETTING
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
name|builder
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
name|IndexingSlowLog
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_REFORMAT_SETTING
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
name|builder
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_LEVEL_SETTING
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
name|IndexingSlowLog
name|log
init|=
operator|new
name|IndexingSlowLog
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_LEVEL_SETTING
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_LEVEL_SETTING
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_LEVEL_SETTING
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
name|builder
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
name|IndexingSlowLog
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_LEVEL_SETTING
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
DECL|method|testSetLevels
specifier|public
name|void
name|testSetLevels
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
name|builder
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_TRACE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"100ms"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_DEBUG_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"200ms"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_INFO_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"300ms"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_WARN_SETTING
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
name|IndexingSlowLog
name|log
init|=
operator|new
name|IndexingSlowLog
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
name|getIndexTraceThreshold
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
name|getIndexDebugThreshold
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
name|getIndexInfoThreshold
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
name|getIndexWarnThreshold
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_TRACE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"120ms"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_DEBUG_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"220ms"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_INFO_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"320ms"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_WARN_SETTING
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
name|getIndexTraceThreshold
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
name|getIndexDebugThreshold
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
name|getIndexInfoThreshold
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
name|getIndexWarnThreshold
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
name|builder
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
name|getIndexTraceThreshold
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
name|getIndexDebugThreshold
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
name|getIndexInfoThreshold
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
name|getIndexWarnThreshold
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
name|IndexingSlowLog
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
name|getIndexTraceThreshold
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
name|getIndexDebugThreshold
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
name|getIndexInfoThreshold
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
name|getIndexWarnThreshold
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_TRACE_SETTING
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
literal|"Failed to parse setting [index.indexing.slowlog.threshold.index.trace] with value [NOT A TIME VALUE] as a time value: unit is missing or unrecognized"
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_DEBUG_SETTING
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
literal|"Failed to parse setting [index.indexing.slowlog.threshold.index.debug] with value [NOT A TIME VALUE] as a time value: unit is missing or unrecognized"
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_INFO_SETTING
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
literal|"Failed to parse setting [index.indexing.slowlog.threshold.index.info] with value [NOT A TIME VALUE] as a time value: unit is missing or unrecognized"
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
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_WARN_SETTING
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
literal|"Failed to parse setting [index.indexing.slowlog.threshold.index.warn] with value [NOT A TIME VALUE] as a time value: unit is missing or unrecognized"
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
name|builder
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

