begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.index.store.distributor
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|index
operator|.
name|store
operator|.
name|distributor
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
name|index
operator|.
name|store
operator|.
name|DirectoryService
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
name|distributor
operator|.
name|LeastUsedDistributor
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
name|distributor
operator|.
name|RandomWeightedDistributor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
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
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|DistributorTests
specifier|public
class|class
name|DistributorTests
block|{
annotation|@
name|Test
DECL|method|testLeastUsedDistributor
specifier|public
name|void
name|testLeastUsedDistributor
parameter_list|()
throws|throws
name|Exception
block|{
name|FakeFsDirectory
index|[]
name|directories
init|=
operator|new
name|FakeFsDirectory
index|[]
block|{
operator|new
name|FakeFsDirectory
argument_list|(
literal|"dir0"
argument_list|,
literal|10L
argument_list|)
block|,
operator|new
name|FakeFsDirectory
argument_list|(
literal|"dir1"
argument_list|,
literal|20L
argument_list|)
block|,
operator|new
name|FakeFsDirectory
argument_list|(
literal|"dir2"
argument_list|,
literal|30L
argument_list|)
block|}
decl_stmt|;
name|FakeDirectoryService
name|directoryService
init|=
operator|new
name|FakeDirectoryService
argument_list|(
name|directories
argument_list|)
decl_stmt|;
name|LeastUsedDistributor
name|distributor
init|=
operator|new
name|LeastUsedDistributor
argument_list|(
name|directoryService
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|distributor
operator|.
name|any
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Directory
operator|)
name|directories
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|directories
index|[
literal|2
index|]
operator|.
name|setUsableSpace
argument_list|(
literal|5L
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|distributor
operator|.
name|any
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Directory
operator|)
name|directories
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|directories
index|[
literal|1
index|]
operator|.
name|setUsableSpace
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|distributor
operator|.
name|any
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Directory
operator|)
name|directories
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|directories
index|[
literal|0
index|]
operator|.
name|setUsableSpace
argument_list|(
literal|10L
argument_list|)
expr_stmt|;
name|directories
index|[
literal|1
index|]
operator|.
name|setUsableSpace
argument_list|(
literal|20L
argument_list|)
expr_stmt|;
name|directories
index|[
literal|2
index|]
operator|.
name|setUsableSpace
argument_list|(
literal|20L
argument_list|)
expr_stmt|;
for|for
control|(
name|FakeFsDirectory
name|directory
range|:
name|directories
control|)
block|{
name|directory
operator|.
name|resetAllocationCount
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|FakeFsDirectory
operator|)
name|distributor
operator|.
name|any
argument_list|()
operator|)
operator|.
name|incrementAllocationCount
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|directories
index|[
literal|0
index|]
operator|.
name|getAllocationCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|directories
index|[
literal|1
index|]
operator|.
name|getAllocationCount
argument_list|()
operator|/
name|directories
index|[
literal|2
index|]
operator|.
name|getAllocationCount
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|1.0
argument_list|,
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test failover scenario
for|for
control|(
name|FakeFsDirectory
name|directory
range|:
name|directories
control|)
block|{
name|directory
operator|.
name|resetAllocationCount
argument_list|()
expr_stmt|;
block|}
name|directories
index|[
literal|0
index|]
operator|.
name|setUsableSpace
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|directories
index|[
literal|1
index|]
operator|.
name|setUsableSpace
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|directories
index|[
literal|2
index|]
operator|.
name|setUsableSpace
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|FakeFsDirectory
operator|)
name|distributor
operator|.
name|any
argument_list|()
operator|)
operator|.
name|incrementAllocationCount
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|FakeFsDirectory
name|directory
range|:
name|directories
control|)
block|{
name|assertThat
argument_list|(
name|directory
operator|.
name|getAllocationCount
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|directories
index|[
literal|0
index|]
operator|.
name|getAllocationCount
argument_list|()
operator|/
name|directories
index|[
literal|2
index|]
operator|.
name|getAllocationCount
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|1.0
argument_list|,
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|directories
index|[
literal|1
index|]
operator|.
name|getAllocationCount
argument_list|()
operator|/
name|directories
index|[
literal|2
index|]
operator|.
name|getAllocationCount
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|1.0
argument_list|,
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandomWeightedDistributor
specifier|public
name|void
name|testRandomWeightedDistributor
parameter_list|()
throws|throws
name|Exception
block|{
name|FakeFsDirectory
index|[]
name|directories
init|=
operator|new
name|FakeFsDirectory
index|[]
block|{
operator|new
name|FakeFsDirectory
argument_list|(
literal|"dir0"
argument_list|,
literal|10L
argument_list|)
block|,
operator|new
name|FakeFsDirectory
argument_list|(
literal|"dir1"
argument_list|,
literal|20L
argument_list|)
block|,
operator|new
name|FakeFsDirectory
argument_list|(
literal|"dir2"
argument_list|,
literal|30L
argument_list|)
block|}
decl_stmt|;
name|FakeDirectoryService
name|directoryService
init|=
operator|new
name|FakeDirectoryService
argument_list|(
name|directories
argument_list|)
decl_stmt|;
name|RandomWeightedDistributor
name|randomWeightedDistributor
init|=
operator|new
name|RandomWeightedDistributor
argument_list|(
name|directoryService
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|FakeFsDirectory
operator|)
name|randomWeightedDistributor
operator|.
name|any
argument_list|()
operator|)
operator|.
name|incrementAllocationCount
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|FakeFsDirectory
name|directory
range|:
name|directories
control|)
block|{
name|assertThat
argument_list|(
name|directory
operator|.
name|getAllocationCount
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|directories
index|[
literal|1
index|]
operator|.
name|getAllocationCount
argument_list|()
operator|/
name|directories
index|[
literal|0
index|]
operator|.
name|getAllocationCount
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|2.0
argument_list|,
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|directories
index|[
literal|2
index|]
operator|.
name|getAllocationCount
argument_list|()
operator|/
name|directories
index|[
literal|0
index|]
operator|.
name|getAllocationCount
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|3.0
argument_list|,
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|FakeFsDirectory
name|directory
range|:
name|directories
control|)
block|{
name|directory
operator|.
name|resetAllocationCount
argument_list|()
expr_stmt|;
block|}
name|directories
index|[
literal|1
index|]
operator|.
name|setUsableSpace
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|FakeFsDirectory
operator|)
name|randomWeightedDistributor
operator|.
name|any
argument_list|()
operator|)
operator|.
name|incrementAllocationCount
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|directories
index|[
literal|0
index|]
operator|.
name|getAllocationCount
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|directories
index|[
literal|1
index|]
operator|.
name|getAllocationCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|directories
index|[
literal|2
index|]
operator|.
name|getAllocationCount
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|FakeDirectoryService
specifier|public
specifier|static
class|class
name|FakeDirectoryService
implements|implements
name|DirectoryService
block|{
DECL|field|directories
specifier|private
specifier|final
name|Directory
index|[]
name|directories
decl_stmt|;
DECL|method|FakeDirectoryService
specifier|public
name|FakeDirectoryService
parameter_list|(
name|Directory
index|[]
name|directories
parameter_list|)
block|{
name|this
operator|.
name|directories
operator|=
name|directories
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|Directory
index|[]
name|build
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|directories
return|;
block|}
annotation|@
name|Override
DECL|method|throttleTimeInNanos
specifier|public
name|long
name|throttleTimeInNanos
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|renameFile
specifier|public
name|void
name|renameFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
throws|throws
name|IOException
block|{         }
annotation|@
name|Override
DECL|method|fullDelete
specifier|public
name|void
name|fullDelete
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{         }
block|}
DECL|class|FakeFsDirectory
specifier|public
specifier|static
class|class
name|FakeFsDirectory
extends|extends
name|FSDirectory
block|{
DECL|field|allocationCount
specifier|public
name|int
name|allocationCount
decl_stmt|;
DECL|field|fakeFile
specifier|public
name|FakeFile
name|fakeFile
decl_stmt|;
DECL|method|FakeFsDirectory
specifier|public
name|FakeFsDirectory
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|usableSpace
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|,
name|NoLockFactory
operator|.
name|getNoLockFactory
argument_list|()
argument_list|)
expr_stmt|;
name|fakeFile
operator|=
operator|new
name|FakeFile
argument_list|(
name|path
argument_list|,
name|usableSpace
argument_list|)
expr_stmt|;
name|allocationCount
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Shouldn't be called in the test"
argument_list|)
throw|;
block|}
DECL|method|setUsableSpace
specifier|public
name|void
name|setUsableSpace
parameter_list|(
name|long
name|usableSpace
parameter_list|)
block|{
name|fakeFile
operator|.
name|setUsableSpace
argument_list|(
name|usableSpace
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementAllocationCount
specifier|public
name|void
name|incrementAllocationCount
parameter_list|()
block|{
name|allocationCount
operator|++
expr_stmt|;
block|}
DECL|method|getAllocationCount
specifier|public
name|int
name|getAllocationCount
parameter_list|()
block|{
return|return
name|allocationCount
return|;
block|}
DECL|method|resetAllocationCount
specifier|public
name|void
name|resetAllocationCount
parameter_list|()
block|{
name|allocationCount
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDirectory
specifier|public
name|File
name|getDirectory
parameter_list|()
block|{
return|return
name|fakeFile
return|;
block|}
block|}
DECL|class|FakeFile
specifier|public
specifier|static
class|class
name|FakeFile
extends|extends
name|File
block|{
DECL|field|usableSpace
specifier|private
name|long
name|usableSpace
decl_stmt|;
DECL|method|FakeFile
specifier|public
name|FakeFile
parameter_list|(
name|String
name|s
parameter_list|,
name|long
name|usableSpace
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|this
operator|.
name|usableSpace
operator|=
name|usableSpace
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUsableSpace
specifier|public
name|long
name|getUsableSpace
parameter_list|()
block|{
return|return
name|usableSpace
return|;
block|}
DECL|method|setUsableSpace
specifier|public
name|void
name|setUsableSpace
parameter_list|(
name|long
name|usableSpace
parameter_list|)
block|{
name|this
operator|.
name|usableSpace
operator|=
name|usableSpace
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

