begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
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
name|index
operator|.
name|LiveIndexWriterConfig
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
name|IndexService
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
name|EngineAccess
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
name|ESSingleNodeTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|is
import|;
end_import

begin_class
DECL|class|InternalEngineSettingsTests
specifier|public
class|class
name|InternalEngineSettingsTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|method|testSettingsUpdate
specifier|public
name|void
name|testSettingsUpdate
parameter_list|()
block|{
specifier|final
name|IndexService
name|service
init|=
name|createIndex
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
comment|// INDEX_COMPOUND_ON_FLUSH
name|InternalEngine
name|engine
init|=
operator|(
operator|(
name|InternalEngine
operator|)
name|EngineAccess
operator|.
name|engine
argument_list|(
name|service
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|)
decl_stmt|;
name|assertThat
argument_list|(
name|engine
operator|.
name|getCurrentIndexWriterConfig
argument_list|()
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|EngineConfig
operator|.
name|INDEX_COMPOUND_ON_FLUSH
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|engine
operator|.
name|getCurrentIndexWriterConfig
argument_list|()
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|EngineConfig
operator|.
name|INDEX_COMPOUND_ON_FLUSH
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|engine
operator|.
name|getCurrentIndexWriterConfig
argument_list|()
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// VERSION MAP SIZE
name|long
name|indexBufferSize
init|=
name|engine
operator|.
name|config
argument_list|()
operator|.
name|getIndexingBufferSize
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|long
name|versionMapSize
init|=
name|engine
operator|.
name|config
argument_list|()
operator|.
name|getVersionMapSize
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|versionMapSize
argument_list|,
name|equalTo
argument_list|(
call|(
name|long
call|)
argument_list|(
name|indexBufferSize
operator|*
literal|0.25
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|iters
init|=
name|between
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|compoundOnFlush
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
comment|// Tricky: TimeValue.parseTimeValue casts this long to a double, which steals 11 of the 64 bits for exponent, so we can't use
comment|// the full long range here else the assert below fails:
name|long
name|gcDeletes
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
operator|&
operator|(
name|Long
operator|.
name|MAX_VALUE
operator|>>
literal|11
operator|)
decl_stmt|;
name|boolean
name|versionMapAsPercent
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|double
name|versionMapPercent
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|long
name|versionMapSizeInMB
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|String
name|versionMapString
init|=
name|versionMapAsPercent
condition|?
name|versionMapPercent
operator|+
literal|"%"
else|:
name|versionMapSizeInMB
operator|+
literal|"mb"
decl_stmt|;
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
name|EngineConfig
operator|.
name|INDEX_COMPOUND_ON_FLUSH
argument_list|,
name|compoundOnFlush
argument_list|)
operator|.
name|put
argument_list|(
name|EngineConfig
operator|.
name|INDEX_GC_DELETES_SETTING
argument_list|,
name|gcDeletes
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|put
argument_list|(
name|EngineConfig
operator|.
name|INDEX_VERSION_MAP_SIZE
argument_list|,
name|versionMapString
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|gcDeletes
argument_list|,
name|build
operator|.
name|getAsTime
argument_list|(
name|EngineConfig
operator|.
name|INDEX_GC_DELETES_SETTING
argument_list|,
literal|null
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|build
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|LiveIndexWriterConfig
name|currentIndexWriterConfig
init|=
name|engine
operator|.
name|getCurrentIndexWriterConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|engine
operator|.
name|config
argument_list|()
operator|.
name|isCompoundOnFlush
argument_list|()
argument_list|,
name|compoundOnFlush
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|currentIndexWriterConfig
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|,
name|compoundOnFlush
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|engine
operator|.
name|config
argument_list|()
operator|.
name|getGcDeletesInMillis
argument_list|()
argument_list|,
name|gcDeletes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|engine
operator|.
name|getGcDeletesInMillis
argument_list|()
argument_list|,
name|gcDeletes
argument_list|)
expr_stmt|;
name|indexBufferSize
operator|=
name|engine
operator|.
name|config
argument_list|()
operator|.
name|getIndexingBufferSize
argument_list|()
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|versionMapSize
operator|=
name|engine
operator|.
name|config
argument_list|()
operator|.
name|getVersionMapSize
argument_list|()
operator|.
name|bytes
argument_list|()
expr_stmt|;
if|if
condition|(
name|versionMapAsPercent
condition|)
block|{
name|assertThat
argument_list|(
name|versionMapSize
argument_list|,
name|equalTo
argument_list|(
call|(
name|long
call|)
argument_list|(
name|indexBufferSize
operator|*
operator|(
name|versionMapPercent
operator|/
literal|100
operator|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|versionMapSize
argument_list|,
name|equalTo
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
name|versionMapSizeInMB
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|EngineConfig
operator|.
name|INDEX_GC_DELETES_SETTING
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|engine
operator|.
name|getGcDeletesInMillis
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|engine
operator|.
name|config
argument_list|()
operator|.
name|isEnableGcDeletes
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|EngineConfig
operator|.
name|INDEX_GC_DELETES_SETTING
argument_list|,
literal|"0ms"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|engine
operator|.
name|getGcDeletesInMillis
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|engine
operator|.
name|config
argument_list|()
operator|.
name|isEnableGcDeletes
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|EngineConfig
operator|.
name|INDEX_GC_DELETES_SETTING
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|engine
operator|.
name|getGcDeletesInMillis
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|engine
operator|.
name|config
argument_list|()
operator|.
name|isEnableGcDeletes
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|EngineConfig
operator|.
name|INDEX_VERSION_MAP_SIZE
argument_list|,
literal|"sdfasfd"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"settings update didn't fail, but should have"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// good
block|}
name|settings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|EngineConfig
operator|.
name|INDEX_VERSION_MAP_SIZE
argument_list|,
literal|"-12%"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"settings update didn't fail, but should have"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// good
block|}
name|settings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|EngineConfig
operator|.
name|INDEX_VERSION_MAP_SIZE
argument_list|,
literal|"130%"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"settings update didn't fail, but should have"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// good
block|}
block|}
block|}
end_class

end_unit

