begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
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
name|UUIDs
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
name|ToXContent
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
name|XContentBuilder
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
name|XContentParser
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
name|XContentType
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
name|snapshots
operator|.
name|SnapshotId
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
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThan
import|;
end_import

begin_comment
comment|/**  * Tests for the {@link RepositoryData} class.  */
end_comment

begin_class
DECL|class|RepositoryDataTests
specifier|public
class|class
name|RepositoryDataTests
extends|extends
name|ESTestCase
block|{
DECL|method|testEqualsAndHashCode
specifier|public
name|void
name|testEqualsAndHashCode
parameter_list|()
block|{
name|RepositoryData
name|repositoryData1
init|=
name|generateRandomRepoData
argument_list|()
decl_stmt|;
name|RepositoryData
name|repositoryData2
init|=
name|repositoryData1
operator|.
name|copy
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|repositoryData1
argument_list|,
name|repositoryData2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repositoryData1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|repositoryData2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testXContent
specifier|public
name|void
name|testXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|RepositoryData
name|repositoryData
init|=
name|generateRandomRepoData
argument_list|()
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
decl_stmt|;
name|repositoryData
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|XContentParser
name|parser
init|=
name|XContentType
operator|.
name|JSON
operator|.
name|xContent
argument_list|()
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|repositoryData
argument_list|,
name|RepositoryData
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddSnapshots
specifier|public
name|void
name|testAddSnapshots
parameter_list|()
block|{
name|RepositoryData
name|repositoryData
init|=
name|generateRandomRepoData
argument_list|()
decl_stmt|;
comment|// test that adding the same snapshot id to the repository data throws an exception
specifier|final
name|SnapshotId
name|snapshotId
init|=
name|repositoryData
operator|.
name|getSnapshotIds
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|IndexId
argument_list|>
name|indexIdMap
init|=
name|repositoryData
operator|.
name|getIndices
argument_list|()
decl_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|repositoryData
operator|.
name|addSnapshot
argument_list|(
operator|new
name|SnapshotId
argument_list|(
name|snapshotId
operator|.
name|getName
argument_list|()
argument_list|,
name|snapshotId
operator|.
name|getUUID
argument_list|()
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// test that adding a snapshot and its indices works
name|SnapshotId
name|newSnapshot
init|=
operator|new
name|SnapshotId
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|7
argument_list|)
argument_list|,
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IndexId
argument_list|>
name|indices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|IndexId
argument_list|>
name|newIndices
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numNew
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
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
name|numNew
condition|;
name|i
operator|++
control|)
block|{
name|IndexId
name|indexId
init|=
operator|new
name|IndexId
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|7
argument_list|)
argument_list|,
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
decl_stmt|;
name|newIndices
operator|.
name|add
argument_list|(
name|indexId
argument_list|)
expr_stmt|;
name|indices
operator|.
name|add
argument_list|(
name|indexId
argument_list|)
expr_stmt|;
block|}
name|int
name|numOld
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|indexIdMap
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|indexNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|indexIdMap
operator|.
name|keySet
argument_list|()
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
name|numOld
condition|;
name|i
operator|++
control|)
block|{
name|indices
operator|.
name|add
argument_list|(
name|indexIdMap
operator|.
name|get
argument_list|(
name|indexNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RepositoryData
name|newRepoData
init|=
name|repositoryData
operator|.
name|addSnapshot
argument_list|(
name|newSnapshot
argument_list|,
name|indices
argument_list|)
decl_stmt|;
comment|// verify that the new repository data has the new snapshot and its indices
name|assertTrue
argument_list|(
name|newRepoData
operator|.
name|getSnapshotIds
argument_list|()
operator|.
name|contains
argument_list|(
name|newSnapshot
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexId
name|indexId
range|:
name|indices
control|)
block|{
name|Set
argument_list|<
name|SnapshotId
argument_list|>
name|snapshotIds
init|=
name|newRepoData
operator|.
name|getSnapshots
argument_list|(
name|indexId
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|snapshotIds
operator|.
name|contains
argument_list|(
name|newSnapshot
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|newIndices
operator|.
name|contains
argument_list|(
name|indexId
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|snapshotIds
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// if it was a new index, only the new snapshot should be in its set
block|}
block|}
block|}
DECL|method|testInitIndices
specifier|public
name|void
name|testInitIndices
parameter_list|()
block|{
specifier|final
name|int
name|numSnapshots
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|30
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|snapshotIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numSnapshots
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
name|numSnapshots
condition|;
name|i
operator|++
control|)
block|{
name|snapshotIds
operator|.
name|add
argument_list|(
operator|new
name|SnapshotId
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|8
argument_list|)
argument_list|,
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RepositoryData
name|repositoryData
init|=
operator|new
name|RepositoryData
argument_list|(
name|snapshotIds
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
comment|// test that initializing indices works
name|Map
argument_list|<
name|IndexId
argument_list|,
name|Set
argument_list|<
name|SnapshotId
argument_list|>
argument_list|>
name|indices
init|=
name|randomIndices
argument_list|(
name|snapshotIds
argument_list|)
decl_stmt|;
name|RepositoryData
name|newRepoData
init|=
name|repositoryData
operator|.
name|initIndices
argument_list|(
name|indices
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|repositoryData
operator|.
name|getSnapshotIds
argument_list|()
argument_list|,
name|newRepoData
operator|.
name|getSnapshotIds
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexId
name|indexId
range|:
name|indices
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|indices
operator|.
name|get
argument_list|(
name|indexId
argument_list|)
argument_list|,
name|newRepoData
operator|.
name|getSnapshots
argument_list|(
name|indexId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRemoveSnapshot
specifier|public
name|void
name|testRemoveSnapshot
parameter_list|()
block|{
name|RepositoryData
name|repositoryData
init|=
name|generateRandomRepoData
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|snapshotIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|repositoryData
operator|.
name|getSnapshotIds
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|snapshotIds
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|SnapshotId
name|removedSnapshotId
init|=
name|snapshotIds
operator|.
name|remove
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|snapshotIds
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|RepositoryData
name|newRepositoryData
init|=
name|repositoryData
operator|.
name|removeSnapshot
argument_list|(
name|removedSnapshotId
argument_list|)
decl_stmt|;
comment|// make sure the repository data's indices no longer contain the removed snapshot
for|for
control|(
specifier|final
name|IndexId
name|indexId
range|:
name|newRepositoryData
operator|.
name|getIndices
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|assertFalse
argument_list|(
name|newRepositoryData
operator|.
name|getSnapshots
argument_list|(
name|indexId
argument_list|)
operator|.
name|contains
argument_list|(
name|removedSnapshotId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testResolveIndexId
specifier|public
name|void
name|testResolveIndexId
parameter_list|()
block|{
name|RepositoryData
name|repositoryData
init|=
name|generateRandomRepoData
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|IndexId
argument_list|>
name|indices
init|=
name|repositoryData
operator|.
name|getIndices
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|indexNames
init|=
name|indices
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|indexNames
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|indexName
init|=
name|indexNames
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|IndexId
name|indexId
init|=
name|indices
operator|.
name|get
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|indexId
argument_list|,
name|repositoryData
operator|.
name|resolveIndexId
argument_list|(
name|indexName
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|notInRepoData
init|=
name|randomAsciiOfLength
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|indexName
operator|.
name|contains
argument_list|(
name|notInRepoData
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|IndexId
argument_list|(
name|notInRepoData
argument_list|,
name|notInRepoData
argument_list|)
argument_list|,
name|repositoryData
operator|.
name|resolveIndexId
argument_list|(
name|notInRepoData
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|generateRandomRepoData
specifier|public
specifier|static
name|RepositoryData
name|generateRandomRepoData
parameter_list|()
block|{
return|return
name|generateRandomRepoData
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
return|;
block|}
DECL|method|generateRandomRepoData
specifier|public
specifier|static
name|RepositoryData
name|generateRandomRepoData
parameter_list|(
specifier|final
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|origSnapshotIds
parameter_list|)
block|{
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|snapshotIds
init|=
name|randomSnapshots
argument_list|(
name|origSnapshotIds
argument_list|)
decl_stmt|;
return|return
operator|new
name|RepositoryData
argument_list|(
name|snapshotIds
argument_list|,
name|randomIndices
argument_list|(
name|snapshotIds
argument_list|)
argument_list|)
return|;
block|}
DECL|method|randomSnapshots
specifier|private
specifier|static
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|randomSnapshots
parameter_list|(
specifier|final
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|origSnapshotIds
parameter_list|)
block|{
specifier|final
name|int
name|numSnapshots
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|30
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|snapshotIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|origSnapshotIds
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
name|numSnapshots
condition|;
name|i
operator|++
control|)
block|{
name|snapshotIds
operator|.
name|add
argument_list|(
operator|new
name|SnapshotId
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|8
argument_list|)
argument_list|,
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|snapshotIds
return|;
block|}
DECL|method|randomIndices
specifier|private
specifier|static
name|Map
argument_list|<
name|IndexId
argument_list|,
name|Set
argument_list|<
name|SnapshotId
argument_list|>
argument_list|>
name|randomIndices
parameter_list|(
specifier|final
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|snapshotIds
parameter_list|)
block|{
specifier|final
name|int
name|totalSnapshots
init|=
name|snapshotIds
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numIndices
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|30
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|IndexId
argument_list|,
name|Set
argument_list|<
name|SnapshotId
argument_list|>
argument_list|>
name|indices
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|numIndices
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
name|numIndices
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|IndexId
name|indexId
init|=
operator|new
name|IndexId
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|8
argument_list|)
argument_list|,
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|SnapshotId
argument_list|>
name|indexSnapshots
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numIndicesForSnapshot
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|numIndices
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numIndicesForSnapshot
condition|;
name|j
operator|++
control|)
block|{
name|indexSnapshots
operator|.
name|add
argument_list|(
name|snapshotIds
operator|.
name|get
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|totalSnapshots
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indices
operator|.
name|put
argument_list|(
name|indexId
argument_list|,
name|indexSnapshots
argument_list|)
expr_stmt|;
block|}
return|return
name|indices
return|;
block|}
block|}
end_class

end_unit

