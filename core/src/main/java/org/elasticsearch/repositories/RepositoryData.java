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
name|snapshots
operator|.
name|SnapshotId
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
name|Objects
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
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * A class that represents the data in a repository, as captured in the  * repository's index blob.  */
end_comment

begin_class
DECL|class|RepositoryData
specifier|public
specifier|final
class|class
name|RepositoryData
implements|implements
name|ToXContent
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|RepositoryData
name|EMPTY
init|=
operator|new
name|RepositoryData
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * The ids of the snapshots in the repository.      */
DECL|field|snapshotIds
specifier|private
specifier|final
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|snapshotIds
decl_stmt|;
comment|/**      * The indices found in the repository across all snapshots, as a name to {@link IndexId} mapping      */
DECL|field|indices
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|IndexId
argument_list|>
name|indices
decl_stmt|;
comment|/**      * The snapshots that each index belongs to.      */
DECL|field|indexSnapshots
specifier|private
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
name|indexSnapshots
decl_stmt|;
DECL|method|RepositoryData
specifier|public
name|RepositoryData
parameter_list|(
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|snapshotIds
parameter_list|,
name|Map
argument_list|<
name|IndexId
argument_list|,
name|Set
argument_list|<
name|SnapshotId
argument_list|>
argument_list|>
name|indexSnapshots
parameter_list|)
block|{
name|this
operator|.
name|snapshotIds
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|snapshotIds
argument_list|)
expr_stmt|;
name|this
operator|.
name|indices
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|indexSnapshots
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toMap
argument_list|(
name|IndexId
operator|::
name|getName
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexSnapshots
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|indexSnapshots
argument_list|)
expr_stmt|;
block|}
DECL|method|copy
specifier|protected
name|RepositoryData
name|copy
parameter_list|()
block|{
return|return
operator|new
name|RepositoryData
argument_list|(
name|snapshotIds
argument_list|,
name|indexSnapshots
argument_list|)
return|;
block|}
comment|/**      * Returns an unmodifiable list of the snapshot ids.      */
DECL|method|getSnapshotIds
specifier|public
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|getSnapshotIds
parameter_list|()
block|{
return|return
name|snapshotIds
return|;
block|}
comment|/**      * Returns an unmodifiable map of the index names to {@link IndexId} in the repository.      */
DECL|method|getIndices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|IndexId
argument_list|>
name|getIndices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
comment|/**      * Add a snapshot and its indices to the repository; returns a new instance.  If the snapshot      * already exists in the repository data, this method throws an IllegalArgumentException.      */
DECL|method|addSnapshot
specifier|public
name|RepositoryData
name|addSnapshot
parameter_list|(
specifier|final
name|SnapshotId
name|snapshotId
parameter_list|,
specifier|final
name|List
argument_list|<
name|IndexId
argument_list|>
name|snapshottedIndices
parameter_list|)
block|{
if|if
condition|(
name|snapshotIds
operator|.
name|contains
argument_list|(
name|snapshotId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|snapshotId
operator|+
literal|"] already exists in the repository data"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|snapshots
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|snapshotIds
argument_list|)
decl_stmt|;
name|snapshots
operator|.
name|add
argument_list|(
name|snapshotId
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|IndexId
argument_list|,
name|Set
argument_list|<
name|SnapshotId
argument_list|>
argument_list|>
name|allIndexSnapshots
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|indexSnapshots
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|IndexId
name|indexId
range|:
name|snapshottedIndices
control|)
block|{
if|if
condition|(
name|allIndexSnapshots
operator|.
name|containsKey
argument_list|(
name|indexId
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|SnapshotId
argument_list|>
name|ids
init|=
name|allIndexSnapshots
operator|.
name|get
argument_list|(
name|indexId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|==
literal|null
condition|)
block|{
name|ids
operator|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|allIndexSnapshots
operator|.
name|put
argument_list|(
name|indexId
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
name|ids
operator|.
name|add
argument_list|(
name|snapshotId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Set
argument_list|<
name|SnapshotId
argument_list|>
name|ids
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|ids
operator|.
name|add
argument_list|(
name|snapshotId
argument_list|)
expr_stmt|;
name|allIndexSnapshots
operator|.
name|put
argument_list|(
name|indexId
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|RepositoryData
argument_list|(
name|snapshots
argument_list|,
name|allIndexSnapshots
argument_list|)
return|;
block|}
comment|/**      * Initializes the indices in the repository metadata; returns a new instance.      */
DECL|method|initIndices
specifier|public
name|RepositoryData
name|initIndices
parameter_list|(
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
name|indexSnapshots
parameter_list|)
block|{
return|return
operator|new
name|RepositoryData
argument_list|(
name|snapshotIds
argument_list|,
name|indexSnapshots
argument_list|)
return|;
block|}
comment|/**      * Remove a snapshot and remove any indices that no longer exist in the repository due to the deletion of the snapshot.      */
DECL|method|removeSnapshot
specifier|public
name|RepositoryData
name|removeSnapshot
parameter_list|(
specifier|final
name|SnapshotId
name|snapshotId
parameter_list|)
block|{
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|newSnapshotIds
init|=
name|snapshotIds
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|id
lambda|->
name|snapshotId
operator|.
name|equals
argument_list|(
name|id
argument_list|)
operator|==
literal|false
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|IndexId
argument_list|,
name|Set
argument_list|<
name|SnapshotId
argument_list|>
argument_list|>
name|indexSnapshots
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|IndexId
name|indexId
range|:
name|indices
operator|.
name|values
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|SnapshotId
argument_list|>
name|set
decl_stmt|;
name|Set
argument_list|<
name|SnapshotId
argument_list|>
name|snapshotIds
init|=
name|this
operator|.
name|indexSnapshots
operator|.
name|get
argument_list|(
name|indexId
argument_list|)
decl_stmt|;
assert|assert
name|snapshotIds
operator|!=
literal|null
assert|;
if|if
condition|(
name|snapshotIds
operator|.
name|contains
argument_list|(
name|snapshotId
argument_list|)
condition|)
block|{
if|if
condition|(
name|snapshotIds
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// removing the snapshot will mean no more snapshots have this index, so just skip over it
continue|continue;
block|}
name|set
operator|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|(
name|snapshotIds
argument_list|)
expr_stmt|;
name|set
operator|.
name|remove
argument_list|(
name|snapshotId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set
operator|=
name|snapshotIds
expr_stmt|;
block|}
name|indexSnapshots
operator|.
name|put
argument_list|(
name|indexId
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RepositoryData
argument_list|(
name|newSnapshotIds
argument_list|,
name|indexSnapshots
argument_list|)
return|;
block|}
comment|/**      * Returns an immutable collection of the snapshot ids for the snapshots that contain the given index.      */
DECL|method|getSnapshots
specifier|public
name|Set
argument_list|<
name|SnapshotId
argument_list|>
name|getSnapshots
parameter_list|(
specifier|final
name|IndexId
name|indexId
parameter_list|)
block|{
name|Set
argument_list|<
name|SnapshotId
argument_list|>
name|snapshotIds
init|=
name|indexSnapshots
operator|.
name|get
argument_list|(
name|indexId
argument_list|)
decl_stmt|;
if|if
condition|(
name|snapshotIds
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown snapshot index "
operator|+
name|indexId
operator|+
literal|""
argument_list|)
throw|;
block|}
return|return
name|snapshotIds
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|RepositoryData
name|that
init|=
operator|(
name|RepositoryData
operator|)
name|obj
decl_stmt|;
return|return
name|snapshotIds
operator|.
name|equals
argument_list|(
name|that
operator|.
name|snapshotIds
argument_list|)
operator|&&
name|indices
operator|.
name|equals
argument_list|(
name|that
operator|.
name|indices
argument_list|)
operator|&&
name|indexSnapshots
operator|.
name|equals
argument_list|(
name|that
operator|.
name|indexSnapshots
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|snapshotIds
argument_list|,
name|indices
argument_list|,
name|indexSnapshots
argument_list|)
return|;
block|}
comment|/**      * Resolve the index name to the index id specific to the repository,      * throwing an exception if the index could not be resolved.      */
DECL|method|resolveIndexId
specifier|public
name|IndexId
name|resolveIndexId
parameter_list|(
specifier|final
name|String
name|indexName
parameter_list|)
block|{
if|if
condition|(
name|indices
operator|.
name|containsKey
argument_list|(
name|indexName
argument_list|)
condition|)
block|{
return|return
name|indices
operator|.
name|get
argument_list|(
name|indexName
argument_list|)
return|;
block|}
else|else
block|{
comment|// on repositories created before 5.0, there was no indices information in the index
comment|// blob, so if the repository hasn't been updated with new snapshots, no new index blob
comment|// would have been written, so we only have old snapshots without the index information.
comment|// in this case, the index id is just the index name
return|return
operator|new
name|IndexId
argument_list|(
name|indexName
argument_list|,
name|indexName
argument_list|)
return|;
block|}
block|}
comment|/**      * Resolve the given index names to index ids.      */
DECL|method|resolveIndices
specifier|public
name|List
argument_list|<
name|IndexId
argument_list|>
name|resolveIndices
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|indices
parameter_list|)
block|{
name|List
argument_list|<
name|IndexId
argument_list|>
name|resolvedIndices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|indices
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|indexName
range|:
name|indices
control|)
block|{
name|resolvedIndices
operator|.
name|add
argument_list|(
name|resolveIndexId
argument_list|(
name|indexName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|resolvedIndices
return|;
block|}
comment|/**      * Resolve the given index names to index ids, creating new index ids for      * new indices in the repository.      */
DECL|method|resolveNewIndices
specifier|public
name|List
argument_list|<
name|IndexId
argument_list|>
name|resolveNewIndices
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|indicesToResolve
parameter_list|)
block|{
name|List
argument_list|<
name|IndexId
argument_list|>
name|snapshotIndices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indicesToResolve
control|)
block|{
specifier|final
name|IndexId
name|indexId
decl_stmt|;
if|if
condition|(
name|indices
operator|.
name|containsKey
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|indexId
operator|=
name|indices
operator|.
name|get
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexId
operator|=
operator|new
name|IndexId
argument_list|(
name|index
argument_list|,
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|snapshotIndices
operator|.
name|add
argument_list|(
name|indexId
argument_list|)
expr_stmt|;
block|}
return|return
name|snapshotIndices
return|;
block|}
DECL|field|SNAPSHOTS
specifier|private
specifier|static
specifier|final
name|String
name|SNAPSHOTS
init|=
literal|"snapshots"
decl_stmt|;
DECL|field|INDICES
specifier|private
specifier|static
specifier|final
name|String
name|INDICES
init|=
literal|"indices"
decl_stmt|;
DECL|field|INDEX_ID
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_ID
init|=
literal|"id"
decl_stmt|;
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
specifier|final
name|XContentBuilder
name|builder
parameter_list|,
specifier|final
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
comment|// write the snapshots list
name|builder
operator|.
name|startArray
argument_list|(
name|SNAPSHOTS
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|SnapshotId
name|snapshot
range|:
name|getSnapshotIds
argument_list|()
control|)
block|{
name|snapshot
operator|.
name|toXContent
argument_list|(
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
comment|// write the indices map
name|builder
operator|.
name|startObject
argument_list|(
name|INDICES
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|IndexId
name|indexId
range|:
name|getIndices
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|indexId
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|INDEX_ID
argument_list|,
name|indexId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
name|SNAPSHOTS
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|SnapshotId
argument_list|>
name|snapshotIds
init|=
name|indexSnapshots
operator|.
name|get
argument_list|(
name|indexId
argument_list|)
decl_stmt|;
assert|assert
name|snapshotIds
operator|!=
literal|null
assert|;
for|for
control|(
specifier|final
name|SnapshotId
name|snapshotId
range|:
name|snapshotIds
control|)
block|{
name|snapshotId
operator|.
name|toXContent
argument_list|(
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|RepositoryData
name|fromXContent
parameter_list|(
specifier|final
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|SnapshotId
argument_list|>
name|snapshots
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|IndexId
argument_list|,
name|Set
argument_list|<
name|SnapshotId
argument_list|>
argument_list|>
name|indexSnapshots
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
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
name|START_OBJECT
condition|)
block|{
while|while
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
name|FIELD_NAME
condition|)
block|{
name|String
name|currentFieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
if|if
condition|(
name|SNAPSHOTS
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
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
name|snapshots
operator|.
name|add
argument_list|(
name|SnapshotId
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"expected array for ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|INDICES
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
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
literal|"start object expected [indices]"
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
name|END_OBJECT
condition|)
block|{
name|String
name|indexName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|String
name|indexId
init|=
literal|null
decl_stmt|;
name|Set
argument_list|<
name|SnapshotId
argument_list|>
name|snapshotIds
init|=
operator|new
name|LinkedHashSet
argument_list|<>
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
literal|"start object expected index["
operator|+
name|indexName
operator|+
literal|"]"
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
name|END_OBJECT
condition|)
block|{
name|String
name|indexMetaFieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|INDEX_ID
operator|.
name|equals
argument_list|(
name|indexMetaFieldName
argument_list|)
condition|)
block|{
name|indexId
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SNAPSHOTS
operator|.
name|equals
argument_list|(
name|indexMetaFieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"start array expected [snapshots]"
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
name|snapshotIds
operator|.
name|add
argument_list|(
name|SnapshotId
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
assert|assert
name|indexId
operator|!=
literal|null
assert|;
name|indexSnapshots
operator|.
name|put
argument_list|(
operator|new
name|IndexId
argument_list|(
name|indexName
argument_list|,
name|indexId
argument_list|)
argument_list|,
name|snapshotIds
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unknown field name  ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"start object expected"
argument_list|)
throw|;
block|}
return|return
operator|new
name|RepositoryData
argument_list|(
name|snapshots
argument_list|,
name|indexSnapshots
argument_list|)
return|;
block|}
block|}
end_class

end_unit
