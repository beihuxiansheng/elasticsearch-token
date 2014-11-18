begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|MMapDirectory
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
name|inject
operator|.
name|AbstractModule
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
name|Module
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
name|Modules
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
name|SpawnModules
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
name|store
operator|.
name|fs
operator|.
name|DefaultFsIndexStoreModule
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
name|fs
operator|.
name|MmapFsIndexStoreModule
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
name|fs
operator|.
name|NioFsIndexStoreModule
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
name|fs
operator|.
name|SimpleFsIndexStoreModule
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|IndexStoreModule
specifier|public
class|class
name|IndexStoreModule
extends|extends
name|AbstractModule
implements|implements
name|SpawnModules
block|{
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|enum|Type
specifier|public
specifier|static
enum|enum
name|Type
block|{
DECL|enum constant|NIOFS
name|NIOFS
block|{
specifier|public
name|boolean
name|match
parameter_list|(
name|String
name|setting
parameter_list|)
block|{
return|return
name|super
operator|.
name|match
argument_list|(
name|setting
argument_list|)
operator|||
literal|"nio_fs"
operator|.
name|equalsIgnoreCase
argument_list|(
name|setting
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|MMAPFS
name|MMAPFS
block|{
specifier|public
name|boolean
name|match
parameter_list|(
name|String
name|setting
parameter_list|)
block|{
return|return
name|super
operator|.
name|match
argument_list|(
name|setting
argument_list|)
operator|||
literal|"mmap_fs"
operator|.
name|equalsIgnoreCase
argument_list|(
name|setting
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|SIMPLEFS
name|SIMPLEFS
block|{
specifier|public
name|boolean
name|match
parameter_list|(
name|String
name|setting
parameter_list|)
block|{
return|return
name|super
operator|.
name|match
argument_list|(
name|setting
argument_list|)
operator|||
literal|"simple_fs"
operator|.
name|equalsIgnoreCase
argument_list|(
name|setting
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|FS
name|FS
block|,
DECL|enum constant|DEFAULT
name|DEFAULT
block|,;
comment|/**          * Returns true iff this settings matches the type.          */
DECL|method|match
specifier|public
name|boolean
name|match
parameter_list|(
name|String
name|setting
parameter_list|)
block|{
return|return
name|this
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|setting
argument_list|)
return|;
block|}
block|}
DECL|method|IndexStoreModule
specifier|public
name|IndexStoreModule
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|spawnModules
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|spawnModules
parameter_list|()
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|indexStoreModule
init|=
name|NioFsIndexStoreModule
operator|.
name|class
decl_stmt|;
if|if
condition|(
operator|(
name|Constants
operator|.
name|WINDOWS
operator|||
name|Constants
operator|.
name|SUN_OS
operator|||
name|Constants
operator|.
name|LINUX
operator|)
operator|&&
name|Constants
operator|.
name|JRE_IS_64BIT
operator|&&
name|MMapDirectory
operator|.
name|UNMAP_SUPPORTED
condition|)
block|{
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
name|indexStoreModule
operator|=
name|MmapFsIndexStoreModule
operator|.
name|class
expr_stmt|;
block|}
else|else
block|{
comment|// on linux and friends we only mmap dedicated files
name|indexStoreModule
operator|=
name|DefaultFsIndexStoreModule
operator|.
name|class
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
name|indexStoreModule
operator|=
name|SimpleFsIndexStoreModule
operator|.
name|class
expr_stmt|;
block|}
name|String
name|storeType
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"index.store.type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|FS
operator|.
name|match
argument_list|(
name|storeType
argument_list|)
condition|)
block|{
comment|// nothing to set here ... (we default to fs)
block|}
elseif|else
if|if
condition|(
name|Type
operator|.
name|SIMPLEFS
operator|.
name|match
argument_list|(
name|storeType
argument_list|)
condition|)
block|{
name|indexStoreModule
operator|=
name|SimpleFsIndexStoreModule
operator|.
name|class
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Type
operator|.
name|NIOFS
operator|.
name|match
argument_list|(
name|storeType
argument_list|)
condition|)
block|{
name|indexStoreModule
operator|=
name|NioFsIndexStoreModule
operator|.
name|class
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Type
operator|.
name|MMAPFS
operator|.
name|match
argument_list|(
name|storeType
argument_list|)
condition|)
block|{
name|indexStoreModule
operator|=
name|MmapFsIndexStoreModule
operator|.
name|class
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Type
operator|.
name|DEFAULT
operator|.
name|match
argument_list|(
name|storeType
argument_list|)
condition|)
block|{
name|indexStoreModule
operator|=
name|DefaultFsIndexStoreModule
operator|.
name|class
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|storeType
operator|!=
literal|null
condition|)
block|{
name|indexStoreModule
operator|=
name|settings
operator|.
name|getAsClass
argument_list|(
literal|"index.store.type"
argument_list|,
name|indexStoreModule
argument_list|,
literal|"org.elasticsearch.index.store."
argument_list|,
literal|"IndexStoreModule"
argument_list|)
expr_stmt|;
block|}
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|Modules
operator|.
name|createModule
argument_list|(
name|indexStoreModule
argument_list|,
name|settings
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{     }
block|}
end_class

end_unit

