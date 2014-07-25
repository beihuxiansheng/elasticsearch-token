begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.translog.fs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
operator|.
name|fs
package|;
end_package

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
name|FileSystemUtils
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
name|index
operator|.
name|translog
operator|.
name|AbstractSimpleTranslogTests
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
name|translog
operator|.
name|Translog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|FsBufferedTranslogTests
specifier|public
class|class
name|FsBufferedTranslogTests
extends|extends
name|AbstractSimpleTranslogTests
block|{
annotation|@
name|Override
DECL|method|create
specifier|protected
name|Translog
name|create
parameter_list|()
block|{
return|return
operator|new
name|FsTranslog
argument_list|(
name|shardId
argument_list|,
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.translog.fs.type"
argument_list|,
name|FsTranslogFile
operator|.
name|Type
operator|.
name|BUFFERED
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.translog.fs.buffer_size"
argument_list|,
literal|10
operator|+
name|randomInt
argument_list|(
literal|128
operator|*
literal|1024
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|translogFileDirectory
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|translogFileDirectory
specifier|protected
name|String
name|translogFileDirectory
parameter_list|()
block|{
return|return
literal|"data/fs-buf-translog"
return|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanup
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
block|{
name|FileSystemUtils
operator|.
name|deleteRecursively
argument_list|(
operator|new
name|File
argument_list|(
literal|"data/fs-buf-translog"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

