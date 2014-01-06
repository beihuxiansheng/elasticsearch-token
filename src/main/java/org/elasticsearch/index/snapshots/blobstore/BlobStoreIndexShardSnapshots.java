begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.snapshots.blobstore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|snapshots
operator|.
name|blobstore
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
name|elasticsearch
operator|.
name|index
operator|.
name|snapshots
operator|.
name|blobstore
operator|.
name|BlobStoreIndexShardSnapshot
operator|.
name|FileInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/**  * Contains information about all snapshot for the given shard in repository  *<p/>  * This class is used to find files that were already snapshoted and clear out files that no longer referenced by any  * snapshots  */
end_comment

begin_class
DECL|class|BlobStoreIndexShardSnapshots
specifier|public
class|class
name|BlobStoreIndexShardSnapshots
implements|implements
name|Iterable
argument_list|<
name|BlobStoreIndexShardSnapshot
argument_list|>
block|{
DECL|field|shardSnapshots
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|BlobStoreIndexShardSnapshot
argument_list|>
name|shardSnapshots
decl_stmt|;
DECL|method|BlobStoreIndexShardSnapshots
specifier|public
name|BlobStoreIndexShardSnapshots
parameter_list|(
name|List
argument_list|<
name|BlobStoreIndexShardSnapshot
argument_list|>
name|shardSnapshots
parameter_list|)
block|{
name|this
operator|.
name|shardSnapshots
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|shardSnapshots
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns list of snapshots      *      * @return list of snapshots      */
DECL|method|snapshots
specifier|public
name|ImmutableList
argument_list|<
name|BlobStoreIndexShardSnapshot
argument_list|>
name|snapshots
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardSnapshots
return|;
block|}
comment|/**      * Finds reference to a snapshotted file by its original name      *      * @param physicalName original name      * @return file info or null if file is not present in any of snapshots      */
DECL|method|findPhysicalIndexFile
specifier|public
name|FileInfo
name|findPhysicalIndexFile
parameter_list|(
name|String
name|physicalName
parameter_list|)
block|{
for|for
control|(
name|BlobStoreIndexShardSnapshot
name|snapshot
range|:
name|shardSnapshots
control|)
block|{
name|FileInfo
name|fileInfo
init|=
name|snapshot
operator|.
name|findPhysicalIndexFile
argument_list|(
name|physicalName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileInfo
operator|!=
literal|null
condition|)
block|{
return|return
name|fileInfo
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Finds reference to a snapshotted file by its snapshot name      *      * @param name file name      * @return file info or null if file is not present in any of snapshots      */
DECL|method|findNameFile
specifier|public
name|FileInfo
name|findNameFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|BlobStoreIndexShardSnapshot
name|snapshot
range|:
name|shardSnapshots
control|)
block|{
name|FileInfo
name|fileInfo
init|=
name|snapshot
operator|.
name|findNameFile
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileInfo
operator|!=
literal|null
condition|)
block|{
return|return
name|fileInfo
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|BlobStoreIndexShardSnapshot
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|shardSnapshots
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

