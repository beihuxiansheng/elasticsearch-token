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
name|ImmutableSettings
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
name|engine
operator|.
name|EngineConfig
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
name|engine
operator|.
name|InternalEngine
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
name|ElasticsearchSingleNodeTest
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
DECL|class|InternalEngineSettingsTest
specifier|public
class|class
name|InternalEngineSettingsTest
extends|extends
name|ElasticsearchSingleNodeTest
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
name|engine
argument_list|(
name|service
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
name|ImmutableSettings
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
name|ImmutableSettings
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
name|boolean
name|failOnCorruption
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|boolean
name|failOnMerge
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|long
name|gcDeletes
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|randomLong
argument_list|()
argument_list|)
decl_stmt|;
name|Settings
name|build
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|EngineConfig
operator|.
name|INDEX_FAIL_ON_CORRUPTION_SETTING
argument_list|,
name|failOnCorruption
argument_list|)
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
argument_list|)
operator|.
name|put
argument_list|(
name|EngineConfig
operator|.
name|INDEX_FAIL_ON_MERGE_FAILURE_SETTING
argument_list|,
name|failOnMerge
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
name|assertEquals
argument_list|(
name|engine
operator|.
name|config
argument_list|()
operator|.
name|isFailEngineOnCorruption
argument_list|()
argument_list|,
name|failOnCorruption
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|engine
operator|.
name|config
argument_list|()
operator|.
name|isFailOnMergeFailure
argument_list|()
argument_list|,
name|failOnMerge
argument_list|)
expr_stmt|;
comment|// only on the holder
block|}
name|Settings
name|settings
init|=
name|ImmutableSettings
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
name|ImmutableSettings
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
name|ImmutableSettings
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
block|}
block|}
end_class

end_unit

