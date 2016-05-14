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
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|ParseField
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
name|ParseFieldMatcher
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
name|FromXContentBuilder
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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableMap
import|;
end_import

begin_comment
comment|/**  * Contains information about all snapshot for the given shard in repository  *<p>  * This class is used to find files that were already snapshotted and clear out files that no longer referenced by any  * snapshots  */
end_comment

begin_class
DECL|class|BlobStoreIndexShardSnapshots
specifier|public
class|class
name|BlobStoreIndexShardSnapshots
implements|implements
name|Iterable
argument_list|<
name|SnapshotFiles
argument_list|>
implements|,
name|ToXContent
implements|,
name|FromXContentBuilder
argument_list|<
name|BlobStoreIndexShardSnapshots
argument_list|>
block|{
DECL|field|PROTO
specifier|public
specifier|static
specifier|final
name|BlobStoreIndexShardSnapshots
name|PROTO
init|=
operator|new
name|BlobStoreIndexShardSnapshots
argument_list|()
decl_stmt|;
DECL|field|shardSnapshots
specifier|private
specifier|final
name|List
argument_list|<
name|SnapshotFiles
argument_list|>
name|shardSnapshots
decl_stmt|;
DECL|field|files
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileInfo
argument_list|>
name|files
decl_stmt|;
DECL|field|physicalFiles
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FileInfo
argument_list|>
argument_list|>
name|physicalFiles
decl_stmt|;
DECL|method|BlobStoreIndexShardSnapshots
specifier|public
name|BlobStoreIndexShardSnapshots
parameter_list|(
name|List
argument_list|<
name|SnapshotFiles
argument_list|>
name|shardSnapshots
parameter_list|)
block|{
name|this
operator|.
name|shardSnapshots
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|shardSnapshots
argument_list|)
argument_list|)
expr_stmt|;
comment|// Map between blob names and file info
name|Map
argument_list|<
name|String
argument_list|,
name|FileInfo
argument_list|>
name|newFiles
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Map between original physical names and file info
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FileInfo
argument_list|>
argument_list|>
name|physicalFiles
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SnapshotFiles
name|snapshot
range|:
name|shardSnapshots
control|)
block|{
comment|// First we build map between filenames in the repo and their original file info
comment|// this map will be used in the next loop
for|for
control|(
name|FileInfo
name|fileInfo
range|:
name|snapshot
operator|.
name|indexFiles
argument_list|()
control|)
block|{
name|FileInfo
name|oldFile
init|=
name|newFiles
operator|.
name|put
argument_list|(
name|fileInfo
operator|.
name|name
argument_list|()
argument_list|,
name|fileInfo
argument_list|)
decl_stmt|;
assert|assert
name|oldFile
operator|==
literal|null
operator|||
name|oldFile
operator|.
name|isSame
argument_list|(
name|fileInfo
argument_list|)
assert|;
block|}
comment|// We are doing it in two loops here so we keep only one copy of the fileInfo per blob
comment|// the first loop de-duplicates fileInfo objects that were loaded from different snapshots but refer to
comment|// the same blob
for|for
control|(
name|FileInfo
name|fileInfo
range|:
name|snapshot
operator|.
name|indexFiles
argument_list|()
control|)
block|{
name|List
argument_list|<
name|FileInfo
argument_list|>
name|physicalFileList
init|=
name|physicalFiles
operator|.
name|get
argument_list|(
name|fileInfo
operator|.
name|physicalName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|physicalFileList
operator|==
literal|null
condition|)
block|{
name|physicalFileList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|physicalFiles
operator|.
name|put
argument_list|(
name|fileInfo
operator|.
name|physicalName
argument_list|()
argument_list|,
name|physicalFileList
argument_list|)
expr_stmt|;
block|}
name|physicalFileList
operator|.
name|add
argument_list|(
name|newFiles
operator|.
name|get
argument_list|(
name|fileInfo
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FileInfo
argument_list|>
argument_list|>
name|mapBuilder
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FileInfo
argument_list|>
argument_list|>
name|entry
range|:
name|physicalFiles
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|mapBuilder
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Collections
operator|.
name|unmodifiableList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|physicalFiles
operator|=
name|unmodifiableMap
argument_list|(
name|mapBuilder
argument_list|)
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|unmodifiableMap
argument_list|(
name|newFiles
argument_list|)
expr_stmt|;
block|}
DECL|method|BlobStoreIndexShardSnapshots
specifier|private
name|BlobStoreIndexShardSnapshots
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|FileInfo
argument_list|>
name|files
parameter_list|,
name|List
argument_list|<
name|SnapshotFiles
argument_list|>
name|shardSnapshots
parameter_list|)
block|{
name|this
operator|.
name|shardSnapshots
operator|=
name|shardSnapshots
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|files
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FileInfo
argument_list|>
argument_list|>
name|physicalFiles
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SnapshotFiles
name|snapshot
range|:
name|shardSnapshots
control|)
block|{
for|for
control|(
name|FileInfo
name|fileInfo
range|:
name|snapshot
operator|.
name|indexFiles
argument_list|()
control|)
block|{
name|List
argument_list|<
name|FileInfo
argument_list|>
name|physicalFileList
init|=
name|physicalFiles
operator|.
name|get
argument_list|(
name|fileInfo
operator|.
name|physicalName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|physicalFileList
operator|==
literal|null
condition|)
block|{
name|physicalFileList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|physicalFiles
operator|.
name|put
argument_list|(
name|fileInfo
operator|.
name|physicalName
argument_list|()
argument_list|,
name|physicalFileList
argument_list|)
expr_stmt|;
block|}
name|physicalFileList
operator|.
name|add
argument_list|(
name|files
operator|.
name|get
argument_list|(
name|fileInfo
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FileInfo
argument_list|>
argument_list|>
name|mapBuilder
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FileInfo
argument_list|>
argument_list|>
name|entry
range|:
name|physicalFiles
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|mapBuilder
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Collections
operator|.
name|unmodifiableList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|physicalFiles
operator|=
name|unmodifiableMap
argument_list|(
name|mapBuilder
argument_list|)
expr_stmt|;
block|}
DECL|method|BlobStoreIndexShardSnapshots
specifier|private
name|BlobStoreIndexShardSnapshots
parameter_list|()
block|{
name|shardSnapshots
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
name|files
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
name|physicalFiles
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns list of snapshots      *      * @return list of snapshots      */
DECL|method|snapshots
specifier|public
name|List
argument_list|<
name|SnapshotFiles
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
comment|/**      * Finds reference to a snapshotted file by its original name      *      * @param physicalName original name      * @return a list of file infos that match specified physical file or null if the file is not present in any of snapshots      */
DECL|method|findPhysicalIndexFiles
specifier|public
name|List
argument_list|<
name|FileInfo
argument_list|>
name|findPhysicalIndexFiles
parameter_list|(
name|String
name|physicalName
parameter_list|)
block|{
return|return
name|physicalFiles
operator|.
name|get
argument_list|(
name|physicalName
argument_list|)
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
return|return
name|files
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|SnapshotFiles
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
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|FILES
specifier|static
specifier|final
name|String
name|FILES
init|=
literal|"files"
decl_stmt|;
DECL|field|SNAPSHOTS
specifier|static
specifier|final
name|String
name|SNAPSHOTS
init|=
literal|"snapshots"
decl_stmt|;
block|}
DECL|class|ParseFields
specifier|static
specifier|final
class|class
name|ParseFields
block|{
DECL|field|FILES
specifier|static
specifier|final
name|ParseField
name|FILES
init|=
operator|new
name|ParseField
argument_list|(
literal|"files"
argument_list|)
decl_stmt|;
DECL|field|SNAPSHOTS
specifier|static
specifier|final
name|ParseField
name|SNAPSHOTS
init|=
operator|new
name|ParseField
argument_list|(
literal|"snapshots"
argument_list|)
decl_stmt|;
block|}
comment|/**      * Writes index file for the shard in the following format.      *<pre>      *<code>      * {      *     "files": [{      *         "name": "__3",      *         "physical_name": "_0.si",      *         "length": 310,      *         "checksum": "1tpsg3p",      *         "written_by": "5.1.0",      *         "meta_hash": "P9dsFxNMdWNlb......"      *     }, {      *         "name": "__2",      *         "physical_name": "segments_2",      *         "length": 150,      *         "checksum": "11qjpz6",      *         "written_by": "5.1.0",      *         "meta_hash": "P9dsFwhzZWdtZ......."      *     }, {      *         "name": "__1",      *         "physical_name": "_0.cfe",      *         "length": 363,      *         "checksum": "er9r9g",      *         "written_by": "5.1.0"      *     }, {      *         "name": "__0",      *         "physical_name": "_0.cfs",      *         "length": 3354,      *         "checksum": "491liz",      *         "written_by": "5.1.0"      *     }, {      *         "name": "__4",      *         "physical_name": "segments_3",      *         "length": 150,      *         "checksum": "134567",      *         "written_by": "5.1.0",      *         "meta_hash": "P9dsFwhzZWdtZ......."      *     }],      *     "snapshots": {      *         "snapshot_1": {      *             "files": ["__0", "__1", "__2", "__3"]      *         },      *         "snapshot_2": {      *             "files": ["__0", "__1", "__2", "__4"]      *         }      *     }      * }      * }      *</code>      *</pre>      */
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
comment|// First we list all blobs with their file infos:
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|FILES
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FileInfo
argument_list|>
name|entry
range|:
name|files
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|FileInfo
operator|.
name|toXContent
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
comment|// Then we list all snapshots with list of all blobs that are used by the snapshot
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|SNAPSHOTS
argument_list|)
expr_stmt|;
for|for
control|(
name|SnapshotFiles
name|snapshot
range|:
name|shardSnapshots
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|snapshot
operator|.
name|snapshot
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|FILES
argument_list|)
expr_stmt|;
for|for
control|(
name|FileInfo
name|fileInfo
range|:
name|snapshot
operator|.
name|indexFiles
argument_list|()
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|fileInfo
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|BlobStoreIndexShardSnapshots
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
comment|// New parser
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|snapshotsMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|FileInfo
argument_list|>
name|files
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unexpected token [{}]"
argument_list|,
name|token
argument_list|)
throw|;
block|}
name|String
name|currentFieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|ParseFields
operator|.
name|FILES
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unknown array [{}]"
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|FileInfo
name|fileInfo
init|=
name|FileInfo
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|files
operator|.
name|put
argument_list|(
name|fileInfo
operator|.
name|name
argument_list|()
argument_list|,
name|fileInfo
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|ParseFields
operator|.
name|SNAPSHOTS
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unknown object [{}]"
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unknown object [{}]"
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
name|String
name|snapshot
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unknown object [{}]"
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
if|if
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|ParseFields
operator|.
name|FILES
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unknown array [{}]"
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|fileNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|fileNames
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|snapshotsMap
operator|.
name|put
argument_list|(
name|snapshot
argument_list|,
name|fileNames
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unexpected token [{}]"
argument_list|,
name|token
argument_list|)
throw|;
block|}
block|}
block|}
name|List
argument_list|<
name|SnapshotFiles
argument_list|>
name|snapshots
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|snapshotsMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|FileInfo
argument_list|>
name|fileInfosBuilder
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|FileInfo
name|fileInfo
init|=
name|files
operator|.
name|get
argument_list|(
name|file
argument_list|)
decl_stmt|;
assert|assert
name|fileInfo
operator|!=
literal|null
assert|;
name|fileInfosBuilder
operator|.
name|add
argument_list|(
name|fileInfo
argument_list|)
expr_stmt|;
block|}
name|snapshots
operator|.
name|add
argument_list|(
operator|new
name|SnapshotFiles
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|fileInfosBuilder
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BlobStoreIndexShardSnapshots
argument_list|(
name|files
argument_list|,
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|snapshots
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

