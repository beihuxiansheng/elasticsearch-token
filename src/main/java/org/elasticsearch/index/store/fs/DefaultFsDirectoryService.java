begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store.fs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
operator|.
name|fs
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
name|Sets
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
name|*
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
name|Inject
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
name|settings
operator|.
name|IndexSettings
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
name|index
operator|.
name|store
operator|.
name|IndexStore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|DefaultFsDirectoryService
specifier|public
class|class
name|DefaultFsDirectoryService
extends|extends
name|FsDirectoryService
block|{
comment|/*      * We are mmapping docvalues as well as term dictionaries, all other files are served through NIOFS      * this provides good random access performance while not creating unnecessary mmaps for files like stored      * fields etc.      */
DECL|field|PRIMARY_EXTENSIONS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|PRIMARY_EXTENSIONS
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"dvd"
argument_list|,
literal|"tim"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Inject
DECL|method|DefaultFsDirectoryService
specifier|public
name|DefaultFsDirectoryService
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|IndexStore
name|indexStore
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|,
name|indexStore
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newFSDirectory
specifier|protected
name|Directory
name|newFSDirectory
parameter_list|(
name|Path
name|location
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FileSwitchDirectory
argument_list|(
name|PRIMARY_EXTENSIONS
argument_list|,
operator|new
name|MMapDirectory
argument_list|(
name|location
argument_list|,
name|lockFactory
argument_list|)
argument_list|,
operator|new
name|NIOFSDirectory
argument_list|(
name|location
argument_list|,
name|lockFactory
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

