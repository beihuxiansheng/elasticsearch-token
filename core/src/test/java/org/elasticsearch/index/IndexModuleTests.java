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
name|index
operator|.
name|DirectoryReader
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
name|search
operator|.
name|IndexSearcher
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
name|inject
operator|.
name|ModuleTestCase
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
name|EngineException
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
name|EngineFactory
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
name|InternalEngineFactory
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
name|IndexEventListener
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
name|IndexSearcherWrapper
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
name|store
operator|.
name|IndexStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|store
operator|.
name|IndicesStore
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
name|IndexSettingsModule
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
name|engine
operator|.
name|MockEngineFactory
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|Consumer
import|;
end_import

begin_class
DECL|class|IndexModuleTests
specifier|public
class|class
name|IndexModuleTests
extends|extends
name|ModuleTestCase
block|{
DECL|method|testWrapperIsBound
specifier|public
name|void
name|testWrapperIsBound
parameter_list|()
block|{
specifier|final
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
specifier|final
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
decl_stmt|;
name|IndexSettings
name|indexSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
name|index
argument_list|,
name|settings
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
decl_stmt|;
name|IndexModule
name|module
init|=
operator|new
name|IndexModule
argument_list|(
name|indexSettings
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|IndexSearcherWrapper
operator|.
name|class
argument_list|,
parameter_list|(
name|x
parameter_list|)
lambda|->
name|x
operator|==
literal|null
argument_list|)
expr_stmt|;
name|module
operator|.
name|indexSearcherWrapper
operator|=
name|Wrapper
operator|.
name|class
expr_stmt|;
name|assertBinding
argument_list|(
name|module
argument_list|,
name|IndexSearcherWrapper
operator|.
name|class
argument_list|,
name|Wrapper
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testEngineFactoryBound
specifier|public
name|void
name|testEngineFactoryBound
parameter_list|()
block|{
specifier|final
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
specifier|final
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
decl_stmt|;
name|IndexSettings
name|indexSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
name|index
argument_list|,
name|settings
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
decl_stmt|;
name|IndexModule
name|module
init|=
operator|new
name|IndexModule
argument_list|(
name|indexSettings
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertBinding
argument_list|(
name|module
argument_list|,
name|EngineFactory
operator|.
name|class
argument_list|,
name|InternalEngineFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|module
operator|.
name|engineFactoryImpl
operator|=
name|MockEngineFactory
operator|.
name|class
expr_stmt|;
name|assertBinding
argument_list|(
name|module
argument_list|,
name|EngineFactory
operator|.
name|class
argument_list|,
name|MockEngineFactory
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegisterIndexStore
specifier|public
name|void
name|testRegisterIndexStore
parameter_list|()
block|{
specifier|final
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
specifier|final
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
name|IndexModule
operator|.
name|STORE_TYPE
argument_list|,
literal|"foo_store"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexSettings
name|indexSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
name|index
argument_list|,
name|settings
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
decl_stmt|;
name|IndexModule
name|module
init|=
operator|new
name|IndexModule
argument_list|(
name|indexSettings
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|module
operator|.
name|addIndexStore
argument_list|(
literal|"foo_store"
argument_list|,
name|FooStore
operator|::
operator|new
argument_list|)
expr_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|IndexStore
operator|.
name|class
argument_list|,
parameter_list|(
name|x
parameter_list|)
lambda|->
name|x
operator|.
name|getClass
argument_list|()
operator|==
name|FooStore
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|module
operator|.
name|addIndexStore
argument_list|(
literal|"foo_store"
argument_list|,
name|FooStore
operator|::
operator|new
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"already registered"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// fine
block|}
block|}
DECL|method|testOtherServiceBound
specifier|public
name|void
name|testOtherServiceBound
parameter_list|()
block|{
specifier|final
name|AtomicBoolean
name|atomicBoolean
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|IndexEventListener
name|eventListener
init|=
operator|new
name|IndexEventListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|beforeIndexDeleted
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{
name|atomicBoolean
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|final
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
specifier|final
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
decl_stmt|;
name|IndexSettings
name|indexSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
name|index
argument_list|,
name|settings
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
decl_stmt|;
name|IndexModule
name|module
init|=
operator|new
name|IndexModule
argument_list|(
name|indexSettings
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Consumer
argument_list|<
name|Settings
argument_list|>
name|listener
init|=
parameter_list|(
name|s
parameter_list|)
lambda|->
block|{}
decl_stmt|;
name|module
operator|.
name|addIndexSettingsListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|module
operator|.
name|addIndexEventListener
argument_list|(
name|eventListener
argument_list|)
expr_stmt|;
name|assertBinding
argument_list|(
name|module
argument_list|,
name|IndexService
operator|.
name|class
argument_list|,
name|IndexService
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertBinding
argument_list|(
name|module
argument_list|,
name|IndexServicesProvider
operator|.
name|class
argument_list|,
name|IndexServicesProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|IndexEventListener
operator|.
name|class
argument_list|,
parameter_list|(
name|x
parameter_list|)
lambda|->
block|{
name|x
operator|.
name|beforeIndexDeleted
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|atomicBoolean
operator|.
name|get
argument_list|()
return|;
block|}
argument_list|)
expr_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|IndexSettings
operator|.
name|class
argument_list|,
parameter_list|(
name|x
parameter_list|)
lambda|->
name|x
operator|.
name|getSettings
argument_list|()
operator|.
name|getAsMap
argument_list|()
operator|.
name|equals
argument_list|(
name|indexSettings
operator|.
name|getSettings
argument_list|()
operator|.
name|getAsMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|IndexSettings
operator|.
name|class
argument_list|,
parameter_list|(
name|x
parameter_list|)
lambda|->
name|x
operator|.
name|getIndex
argument_list|()
operator|.
name|equals
argument_list|(
name|indexSettings
operator|.
name|getIndex
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|IndexSettings
operator|.
name|class
argument_list|,
parameter_list|(
name|x
parameter_list|)
lambda|->
name|x
operator|.
name|getUpdateListeners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|==
name|listener
argument_list|)
expr_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|IndexStore
operator|.
name|class
argument_list|,
parameter_list|(
name|x
parameter_list|)
lambda|->
name|x
operator|.
name|getClass
argument_list|()
operator|==
name|IndexStore
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testListener
specifier|public
name|void
name|testListener
parameter_list|()
block|{
specifier|final
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
specifier|final
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
decl_stmt|;
name|IndexSettings
name|indexSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
name|index
argument_list|,
name|settings
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
decl_stmt|;
name|IndexModule
name|module
init|=
operator|new
name|IndexModule
argument_list|(
name|indexSettings
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Consumer
argument_list|<
name|Settings
argument_list|>
name|listener
init|=
parameter_list|(
name|s
parameter_list|)
lambda|->
block|{         }
decl_stmt|;
name|module
operator|.
name|addIndexSettingsListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
try|try
block|{
name|module
operator|.
name|addIndexSettingsListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"already added"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{          }
try|try
block|{
name|module
operator|.
name|addIndexSettingsListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must not be null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{          }
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|IndexSettings
operator|.
name|class
argument_list|,
parameter_list|(
name|x
parameter_list|)
lambda|->
name|x
operator|.
name|getUpdateListeners
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|IndexSettings
operator|.
name|class
argument_list|,
parameter_list|(
name|x
parameter_list|)
lambda|->
name|x
operator|.
name|getUpdateListeners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|==
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|class|Wrapper
specifier|public
specifier|static
specifier|final
class|class
name|Wrapper
extends|extends
name|IndexSearcherWrapper
block|{
annotation|@
name|Override
DECL|method|wrap
specifier|public
name|DirectoryReader
name|wrap
parameter_list|(
name|DirectoryReader
name|reader
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|wrap
specifier|public
name|IndexSearcher
name|wrap
parameter_list|(
name|EngineConfig
name|engineConfig
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|EngineException
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|FooStore
specifier|public
specifier|static
specifier|final
class|class
name|FooStore
extends|extends
name|IndexStore
block|{
DECL|method|FooStore
specifier|public
name|FooStore
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|IndicesStore
name|indicesStore
parameter_list|)
block|{
name|super
argument_list|(
name|indexSettings
argument_list|,
name|indicesStore
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

