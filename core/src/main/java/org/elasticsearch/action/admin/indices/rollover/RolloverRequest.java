begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.rollover
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|rollover
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
name|action
operator|.
name|ActionRequestValidationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|IndicesRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|CreateIndexRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|ActiveShardCount
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|IndicesOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|master
operator|.
name|AcknowledgedRequest
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
name|ParseFieldMatcherSupplier
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
name|bytes
operator|.
name|BytesReference
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|unit
operator|.
name|TimeValue
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
name|ObjectParser
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
name|XContentFactory
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
name|HashSet
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ValidateActions
operator|.
name|addValidationError
import|;
end_import

begin_comment
comment|/**  * Request class to swap index under an alias upon satisfying conditions  */
end_comment

begin_class
DECL|class|RolloverRequest
specifier|public
class|class
name|RolloverRequest
extends|extends
name|AcknowledgedRequest
argument_list|<
name|RolloverRequest
argument_list|>
implements|implements
name|IndicesRequest
block|{
DECL|field|PARSER
specifier|public
specifier|static
name|ObjectParser
argument_list|<
name|RolloverRequest
argument_list|,
name|ParseFieldMatcherSupplier
argument_list|>
name|PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
literal|"conditions"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
static|static
block|{
name|PARSER
operator|.
name|declareField
argument_list|(
parameter_list|(
name|parser
parameter_list|,
name|request
parameter_list|,
name|parseFieldMatcherSupplier
parameter_list|)
lambda|->
name|Condition
operator|.
name|PARSER
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|request
operator|.
name|conditions
argument_list|,
name|parseFieldMatcherSupplier
argument_list|)
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"conditions"
argument_list|)
argument_list|,
name|ObjectParser
operator|.
name|ValueType
operator|.
name|OBJECT
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareField
argument_list|(
parameter_list|(
name|parser
parameter_list|,
name|request
parameter_list|,
name|parseFieldMatcherSupplier
parameter_list|)
lambda|->
name|request
operator|.
name|createIndexRequest
operator|.
name|settings
argument_list|(
name|parser
operator|.
name|map
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"settings"
argument_list|)
argument_list|,
name|ObjectParser
operator|.
name|ValueType
operator|.
name|OBJECT
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareField
argument_list|(
parameter_list|(
name|parser
parameter_list|,
name|request
parameter_list|,
name|parseFieldMatcherSupplier
parameter_list|)
lambda|->
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mappingsEntry
range|:
name|parser
operator|.
name|map
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|request
operator|.
name|createIndexRequest
operator|.
name|mapping
argument_list|(
name|mappingsEntry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|mappingsEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"mappings"
argument_list|)
argument_list|,
name|ObjectParser
operator|.
name|ValueType
operator|.
name|OBJECT
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareField
argument_list|(
parameter_list|(
name|parser
parameter_list|,
name|request
parameter_list|,
name|parseFieldMatcherSupplier
parameter_list|)
lambda|->
name|request
operator|.
name|createIndexRequest
operator|.
name|aliases
argument_list|(
name|parser
operator|.
name|map
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"aliases"
argument_list|)
argument_list|,
name|ObjectParser
operator|.
name|ValueType
operator|.
name|OBJECT
argument_list|)
expr_stmt|;
block|}
DECL|field|alias
specifier|private
name|String
name|alias
decl_stmt|;
DECL|field|newIndexName
specifier|private
name|String
name|newIndexName
decl_stmt|;
DECL|field|dryRun
specifier|private
name|boolean
name|dryRun
decl_stmt|;
DECL|field|conditions
specifier|private
name|Set
argument_list|<
name|Condition
argument_list|>
name|conditions
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|field|createIndexRequest
specifier|private
name|CreateIndexRequest
name|createIndexRequest
init|=
operator|new
name|CreateIndexRequest
argument_list|(
literal|"_na_"
argument_list|)
decl_stmt|;
DECL|method|RolloverRequest
name|RolloverRequest
parameter_list|()
block|{}
DECL|method|RolloverRequest
specifier|public
name|RolloverRequest
parameter_list|(
name|String
name|alias
parameter_list|,
name|String
name|newIndexName
parameter_list|)
block|{
name|this
operator|.
name|alias
operator|=
name|alias
expr_stmt|;
name|this
operator|.
name|newIndexName
operator|=
name|newIndexName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
name|createIndexRequest
operator|==
literal|null
condition|?
literal|null
else|:
name|createIndexRequest
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|alias
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"index alias is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|createIndexRequest
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"create index request is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|alias
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|newIndexName
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|dryRun
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|conditions
operator|.
name|add
argument_list|(
name|in
operator|.
name|readNamedWriteable
argument_list|(
name|Condition
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|createIndexRequest
operator|=
operator|new
name|CreateIndexRequest
argument_list|()
expr_stmt|;
name|createIndexRequest
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|alias
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|newIndexName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|dryRun
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|conditions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Condition
name|condition
range|:
name|conditions
control|)
block|{
name|out
operator|.
name|writeNamedWriteable
argument_list|(
name|condition
argument_list|)
expr_stmt|;
block|}
name|createIndexRequest
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|indices
specifier|public
name|String
index|[]
name|indices
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|alias
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|indicesOptions
specifier|public
name|IndicesOptions
name|indicesOptions
parameter_list|()
block|{
return|return
name|IndicesOptions
operator|.
name|strictSingleIndexNoExpandForbidClosed
argument_list|()
return|;
block|}
comment|/**      * Sets the alias to rollover to another index      */
DECL|method|setAlias
specifier|public
name|void
name|setAlias
parameter_list|(
name|String
name|alias
parameter_list|)
block|{
name|this
operator|.
name|alias
operator|=
name|alias
expr_stmt|;
block|}
comment|/**      * Sets the alias to rollover to another index      */
DECL|method|setNewIndexName
specifier|public
name|void
name|setNewIndexName
parameter_list|(
name|String
name|newIndexName
parameter_list|)
block|{
name|this
operator|.
name|newIndexName
operator|=
name|newIndexName
expr_stmt|;
block|}
comment|/**      * Sets if the rollover should not be executed when conditions are met      */
DECL|method|dryRun
specifier|public
name|void
name|dryRun
parameter_list|(
name|boolean
name|dryRun
parameter_list|)
block|{
name|this
operator|.
name|dryRun
operator|=
name|dryRun
expr_stmt|;
block|}
comment|/**      * Adds condition to check if the index is at least<code>age</code> old      */
DECL|method|addMaxIndexAgeCondition
specifier|public
name|void
name|addMaxIndexAgeCondition
parameter_list|(
name|TimeValue
name|age
parameter_list|)
block|{
name|this
operator|.
name|conditions
operator|.
name|add
argument_list|(
operator|new
name|MaxAgeCondition
argument_list|(
name|age
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds condition to check if the index has at least<code>numDocs</code>      */
DECL|method|addMaxIndexDocsCondition
specifier|public
name|void
name|addMaxIndexDocsCondition
parameter_list|(
name|long
name|numDocs
parameter_list|)
block|{
name|this
operator|.
name|conditions
operator|.
name|add
argument_list|(
operator|new
name|MaxDocsCondition
argument_list|(
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets rollover index creation request to override index settings when      * the rolled over index has to be created      */
DECL|method|setCreateIndexRequest
specifier|public
name|void
name|setCreateIndexRequest
parameter_list|(
name|CreateIndexRequest
name|createIndexRequest
parameter_list|)
block|{
name|this
operator|.
name|createIndexRequest
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|createIndexRequest
argument_list|,
literal|"create index request must not be null"
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
DECL|method|isDryRun
name|boolean
name|isDryRun
parameter_list|()
block|{
return|return
name|dryRun
return|;
block|}
DECL|method|getConditions
name|Set
argument_list|<
name|Condition
argument_list|>
name|getConditions
parameter_list|()
block|{
return|return
name|conditions
return|;
block|}
DECL|method|getAlias
name|String
name|getAlias
parameter_list|()
block|{
return|return
name|alias
return|;
block|}
DECL|method|getNewIndexName
name|String
name|getNewIndexName
parameter_list|()
block|{
return|return
name|newIndexName
return|;
block|}
DECL|method|getCreateIndexRequest
name|CreateIndexRequest
name|getCreateIndexRequest
parameter_list|()
block|{
return|return
name|createIndexRequest
return|;
block|}
DECL|method|source
specifier|public
name|void
name|source
parameter_list|(
name|BytesReference
name|source
parameter_list|)
block|{
name|XContentType
name|xContentType
init|=
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|xContentType
operator|!=
literal|null
condition|)
block|{
try|try
init|(
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|xContentType
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
init|)
block|{
name|PARSER
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|this
argument_list|,
parameter_list|()
lambda|->
name|ParseFieldMatcher
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse source for rollover index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse content type for rollover index source"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Sets the number of shard copies that should be active for creation of the      * new rollover index to return. Defaults to {@link ActiveShardCount#DEFAULT}, which will      * wait for one shard copy (the primary) to become active. Set this value to      * {@link ActiveShardCount#ALL} to wait for all shards (primary and all replicas) to be active      * before returning. Otherwise, use {@link ActiveShardCount#from(int)} to set this value to any      * non-negative integer, up to the number of copies per shard (number of replicas + 1),      * to wait for the desired amount of shard copies to become active before returning.      * Index creation will only wait up until the timeout value for the number of shard copies      * to be active before returning.  Check {@link RolloverResponse#isShardsAcked()} to      * determine if the requisite shard copies were all started before returning or timing out.      *      * @param waitForActiveShards number of active shard copies to wait on      */
DECL|method|setWaitForActiveShards
specifier|public
name|void
name|setWaitForActiveShards
parameter_list|(
name|ActiveShardCount
name|waitForActiveShards
parameter_list|)
block|{
name|this
operator|.
name|createIndexRequest
operator|.
name|waitForActiveShards
argument_list|(
name|waitForActiveShards
argument_list|)
expr_stmt|;
block|}
comment|/**      * A shortcut for {@link #setWaitForActiveShards(ActiveShardCount)} where the numerical      * shard count is passed in, instead of having to first call {@link ActiveShardCount#from(int)}      * to get the ActiveShardCount.      */
DECL|method|setWaitForActiveShards
specifier|public
name|void
name|setWaitForActiveShards
parameter_list|(
specifier|final
name|int
name|waitForActiveShards
parameter_list|)
block|{
name|setWaitForActiveShards
argument_list|(
name|ActiveShardCount
operator|.
name|from
argument_list|(
name|waitForActiveShards
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
