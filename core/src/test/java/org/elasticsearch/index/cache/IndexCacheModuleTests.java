begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
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
name|search
operator|.
name|QueryCachingPolicy
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
name|Weight
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
name|cache
operator|.
name|query
operator|.
name|QueryCache
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
name|cache
operator|.
name|query
operator|.
name|index
operator|.
name|IndexQueryCache
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
name|cache
operator|.
name|query
operator|.
name|none
operator|.
name|NoneQueryCache
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

begin_class
DECL|class|IndexCacheModuleTests
specifier|public
class|class
name|IndexCacheModuleTests
extends|extends
name|ModuleTestCase
block|{
DECL|method|testCannotRegisterProvidedImplementations
specifier|public
name|void
name|testCannotRegisterProvidedImplementations
parameter_list|()
block|{
name|IndexCacheModule
name|module
init|=
operator|new
name|IndexCacheModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
try|try
block|{
name|module
operator|.
name|registerQueryCache
argument_list|(
literal|"index"
argument_list|,
name|IndexQueryCache
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Can't register the same [query_cache] more than once for [index]"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|module
operator|.
name|registerQueryCache
argument_list|(
literal|"none"
argument_list|,
name|NoneQueryCache
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Can't register the same [query_cache] more than once for [none]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRegisterCustomQueryCache
specifier|public
name|void
name|testRegisterCustomQueryCache
parameter_list|()
block|{
name|IndexCacheModule
name|module
init|=
operator|new
name|IndexCacheModule
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexCacheModule
operator|.
name|QUERY_CACHE_TYPE
argument_list|,
literal|"custom"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerQueryCache
argument_list|(
literal|"custom"
argument_list|,
name|CustomQueryCache
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|module
operator|.
name|registerQueryCache
argument_list|(
literal|"custom"
argument_list|,
name|CustomQueryCache
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Can't register the same [query_cache] more than once for [custom]"
argument_list|)
expr_stmt|;
block|}
name|assertBinding
argument_list|(
name|module
argument_list|,
name|QueryCache
operator|.
name|class
argument_list|,
name|CustomQueryCache
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultQueryCacheImplIsSelected
specifier|public
name|void
name|testDefaultQueryCacheImplIsSelected
parameter_list|()
block|{
name|IndexCacheModule
name|module
init|=
operator|new
name|IndexCacheModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertBinding
argument_list|(
name|module
argument_list|,
name|QueryCache
operator|.
name|class
argument_list|,
name|IndexQueryCache
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|class|CustomQueryCache
class|class
name|CustomQueryCache
implements|implements
name|QueryCache
block|{
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|String
name|reason
parameter_list|)
block|{         }
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{         }
annotation|@
name|Override
DECL|method|index
specifier|public
name|Index
name|index
parameter_list|()
block|{
return|return
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doCache
specifier|public
name|Weight
name|doCache
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|QueryCachingPolicy
name|policy
parameter_list|)
block|{
return|return
name|weight
return|;
block|}
block|}
block|}
end_class

end_unit

