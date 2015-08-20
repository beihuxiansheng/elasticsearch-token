begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.indexing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|indexing
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|Nullable
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|XContentBuilderString
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
name|HashMap
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|IndexingStats
specifier|public
class|class
name|IndexingStats
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|class|Stats
specifier|public
specifier|static
class|class
name|Stats
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|indexCount
specifier|private
name|long
name|indexCount
decl_stmt|;
DECL|field|indexTimeInMillis
specifier|private
name|long
name|indexTimeInMillis
decl_stmt|;
DECL|field|indexCurrent
specifier|private
name|long
name|indexCurrent
decl_stmt|;
DECL|field|deleteCount
specifier|private
name|long
name|deleteCount
decl_stmt|;
DECL|field|deleteTimeInMillis
specifier|private
name|long
name|deleteTimeInMillis
decl_stmt|;
DECL|field|deleteCurrent
specifier|private
name|long
name|deleteCurrent
decl_stmt|;
DECL|field|noopUpdateCount
specifier|private
name|long
name|noopUpdateCount
decl_stmt|;
DECL|field|throttleTimeInMillis
specifier|private
name|long
name|throttleTimeInMillis
decl_stmt|;
DECL|field|isThrottled
specifier|private
name|boolean
name|isThrottled
decl_stmt|;
DECL|method|Stats
name|Stats
parameter_list|()
block|{          }
DECL|method|Stats
specifier|public
name|Stats
parameter_list|(
name|long
name|indexCount
parameter_list|,
name|long
name|indexTimeInMillis
parameter_list|,
name|long
name|indexCurrent
parameter_list|,
name|long
name|deleteCount
parameter_list|,
name|long
name|deleteTimeInMillis
parameter_list|,
name|long
name|deleteCurrent
parameter_list|,
name|long
name|noopUpdateCount
parameter_list|,
name|boolean
name|isThrottled
parameter_list|,
name|long
name|throttleTimeInMillis
parameter_list|)
block|{
name|this
operator|.
name|indexCount
operator|=
name|indexCount
expr_stmt|;
name|this
operator|.
name|indexTimeInMillis
operator|=
name|indexTimeInMillis
expr_stmt|;
name|this
operator|.
name|indexCurrent
operator|=
name|indexCurrent
expr_stmt|;
name|this
operator|.
name|deleteCount
operator|=
name|deleteCount
expr_stmt|;
name|this
operator|.
name|deleteTimeInMillis
operator|=
name|deleteTimeInMillis
expr_stmt|;
name|this
operator|.
name|deleteCurrent
operator|=
name|deleteCurrent
expr_stmt|;
name|this
operator|.
name|noopUpdateCount
operator|=
name|noopUpdateCount
expr_stmt|;
name|this
operator|.
name|isThrottled
operator|=
name|isThrottled
expr_stmt|;
name|this
operator|.
name|throttleTimeInMillis
operator|=
name|throttleTimeInMillis
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Stats
name|stats
parameter_list|)
block|{
name|indexCount
operator|+=
name|stats
operator|.
name|indexCount
expr_stmt|;
name|indexTimeInMillis
operator|+=
name|stats
operator|.
name|indexTimeInMillis
expr_stmt|;
name|indexCurrent
operator|+=
name|stats
operator|.
name|indexCurrent
expr_stmt|;
name|deleteCount
operator|+=
name|stats
operator|.
name|deleteCount
expr_stmt|;
name|deleteTimeInMillis
operator|+=
name|stats
operator|.
name|deleteTimeInMillis
expr_stmt|;
name|deleteCurrent
operator|+=
name|stats
operator|.
name|deleteCurrent
expr_stmt|;
name|noopUpdateCount
operator|+=
name|stats
operator|.
name|noopUpdateCount
expr_stmt|;
name|throttleTimeInMillis
operator|+=
name|stats
operator|.
name|throttleTimeInMillis
expr_stmt|;
if|if
condition|(
name|isThrottled
operator|!=
name|stats
operator|.
name|isThrottled
condition|)
block|{
name|isThrottled
operator|=
literal|true
expr_stmt|;
comment|//When combining if one is throttled set result to throttled.
block|}
block|}
DECL|method|getIndexCount
specifier|public
name|long
name|getIndexCount
parameter_list|()
block|{
return|return
name|indexCount
return|;
block|}
DECL|method|getIndexTime
specifier|public
name|TimeValue
name|getIndexTime
parameter_list|()
block|{
return|return
operator|new
name|TimeValue
argument_list|(
name|indexTimeInMillis
argument_list|)
return|;
block|}
DECL|method|getIndexTimeInMillis
specifier|public
name|long
name|getIndexTimeInMillis
parameter_list|()
block|{
return|return
name|indexTimeInMillis
return|;
block|}
DECL|method|getIndexCurrent
specifier|public
name|long
name|getIndexCurrent
parameter_list|()
block|{
return|return
name|indexCurrent
return|;
block|}
DECL|method|getDeleteCount
specifier|public
name|long
name|getDeleteCount
parameter_list|()
block|{
return|return
name|deleteCount
return|;
block|}
comment|/**          * Returns if the index is under merge throttling control          * @return          */
DECL|method|isThrottled
specifier|public
name|boolean
name|isThrottled
parameter_list|()
block|{
return|return
name|isThrottled
return|;
block|}
comment|/**          * Gets the amount of time in milliseconds that the index has been under merge throttling control          * @return          */
DECL|method|getThrottleTimeInMillis
specifier|public
name|long
name|getThrottleTimeInMillis
parameter_list|()
block|{
return|return
name|throttleTimeInMillis
return|;
block|}
comment|/**          * Gets the amount of time in a TimeValue that the index has been under merge throttling control          * @return          */
DECL|method|getThrottleTime
specifier|public
name|TimeValue
name|getThrottleTime
parameter_list|()
block|{
return|return
operator|new
name|TimeValue
argument_list|(
name|throttleTimeInMillis
argument_list|)
return|;
block|}
DECL|method|getDeleteTime
specifier|public
name|TimeValue
name|getDeleteTime
parameter_list|()
block|{
return|return
operator|new
name|TimeValue
argument_list|(
name|deleteTimeInMillis
argument_list|)
return|;
block|}
DECL|method|getDeleteTimeInMillis
specifier|public
name|long
name|getDeleteTimeInMillis
parameter_list|()
block|{
return|return
name|deleteTimeInMillis
return|;
block|}
DECL|method|getDeleteCurrent
specifier|public
name|long
name|getDeleteCurrent
parameter_list|()
block|{
return|return
name|deleteCurrent
return|;
block|}
DECL|method|getNoopUpdateCount
specifier|public
name|long
name|getNoopUpdateCount
parameter_list|()
block|{
return|return
name|noopUpdateCount
return|;
block|}
DECL|method|readStats
specifier|public
specifier|static
name|Stats
name|readStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Stats
name|stats
init|=
operator|new
name|Stats
argument_list|()
decl_stmt|;
name|stats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|stats
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
name|indexCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|indexTimeInMillis
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|indexCurrent
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|deleteCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|deleteTimeInMillis
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|deleteCurrent
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|noopUpdateCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|isThrottled
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|throttleTimeInMillis
operator|=
name|in
operator|.
name|readLong
argument_list|()
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
name|out
operator|.
name|writeVLong
argument_list|(
name|indexCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|indexTimeInMillis
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|indexCurrent
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|deleteCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|deleteTimeInMillis
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|deleteCurrent
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|noopUpdateCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|isThrottled
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|throttleTimeInMillis
argument_list|)
expr_stmt|;
block|}
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
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|INDEX_TOTAL
argument_list|,
name|indexCount
argument_list|)
expr_stmt|;
name|builder
operator|.
name|timeValueField
argument_list|(
name|Fields
operator|.
name|INDEX_TIME_IN_MILLIS
argument_list|,
name|Fields
operator|.
name|INDEX_TIME
argument_list|,
name|indexTimeInMillis
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|INDEX_CURRENT
argument_list|,
name|indexCurrent
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DELETE_TOTAL
argument_list|,
name|deleteCount
argument_list|)
expr_stmt|;
name|builder
operator|.
name|timeValueField
argument_list|(
name|Fields
operator|.
name|DELETE_TIME_IN_MILLIS
argument_list|,
name|Fields
operator|.
name|DELETE_TIME
argument_list|,
name|deleteTimeInMillis
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DELETE_CURRENT
argument_list|,
name|deleteCurrent
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NOOP_UPDATE_TOTAL
argument_list|,
name|noopUpdateCount
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|IS_THROTTLED
argument_list|,
name|isThrottled
argument_list|)
expr_stmt|;
name|builder
operator|.
name|timeValueField
argument_list|(
name|Fields
operator|.
name|THROTTLED_TIME_IN_MILLIS
argument_list|,
name|Fields
operator|.
name|THROTTLED_TIME
argument_list|,
name|throttleTimeInMillis
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
DECL|field|totalStats
specifier|private
name|Stats
name|totalStats
decl_stmt|;
annotation|@
name|Nullable
DECL|field|typeStats
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Stats
argument_list|>
name|typeStats
decl_stmt|;
DECL|method|IndexingStats
specifier|public
name|IndexingStats
parameter_list|()
block|{
name|totalStats
operator|=
operator|new
name|Stats
argument_list|()
expr_stmt|;
block|}
DECL|method|IndexingStats
specifier|public
name|IndexingStats
parameter_list|(
name|Stats
name|totalStats
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Stats
argument_list|>
name|typeStats
parameter_list|)
block|{
name|this
operator|.
name|totalStats
operator|=
name|totalStats
expr_stmt|;
name|this
operator|.
name|typeStats
operator|=
name|typeStats
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|IndexingStats
name|indexingStats
parameter_list|)
block|{
name|add
argument_list|(
name|indexingStats
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|IndexingStats
name|indexingStats
parameter_list|,
name|boolean
name|includeTypes
parameter_list|)
block|{
if|if
condition|(
name|indexingStats
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|totalStats
operator|.
name|add
argument_list|(
name|indexingStats
operator|.
name|totalStats
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeTypes
operator|&&
name|indexingStats
operator|.
name|typeStats
operator|!=
literal|null
operator|&&
operator|!
name|indexingStats
operator|.
name|typeStats
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|typeStats
operator|==
literal|null
condition|)
block|{
name|typeStats
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|indexingStats
operator|.
name|typeStats
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Stats
argument_list|>
name|entry
range|:
name|indexingStats
operator|.
name|typeStats
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Stats
name|stats
init|=
name|typeStats
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
name|typeStats
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stats
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getTotal
specifier|public
name|Stats
name|getTotal
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalStats
return|;
block|}
annotation|@
name|Nullable
DECL|method|getTypeStats
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Stats
argument_list|>
name|getTypeStats
parameter_list|()
block|{
return|return
name|this
operator|.
name|typeStats
return|;
block|}
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
name|ToXContent
operator|.
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|INDEXING
argument_list|)
expr_stmt|;
name|totalStats
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|typeStats
operator|!=
literal|null
operator|&&
operator|!
name|typeStats
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|TYPES
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
name|Stats
argument_list|>
name|entry
range|:
name|typeStats
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
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
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|INDEXING
specifier|static
specifier|final
name|XContentBuilderString
name|INDEXING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"indexing"
argument_list|)
decl_stmt|;
DECL|field|TYPES
specifier|static
specifier|final
name|XContentBuilderString
name|TYPES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"types"
argument_list|)
decl_stmt|;
DECL|field|INDEX_TOTAL
specifier|static
specifier|final
name|XContentBuilderString
name|INDEX_TOTAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"index_total"
argument_list|)
decl_stmt|;
DECL|field|INDEX_TIME
specifier|static
specifier|final
name|XContentBuilderString
name|INDEX_TIME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"index_time"
argument_list|)
decl_stmt|;
DECL|field|INDEX_TIME_IN_MILLIS
specifier|static
specifier|final
name|XContentBuilderString
name|INDEX_TIME_IN_MILLIS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"index_time_in_millis"
argument_list|)
decl_stmt|;
DECL|field|INDEX_CURRENT
specifier|static
specifier|final
name|XContentBuilderString
name|INDEX_CURRENT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"index_current"
argument_list|)
decl_stmt|;
DECL|field|DELETE_TOTAL
specifier|static
specifier|final
name|XContentBuilderString
name|DELETE_TOTAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"delete_total"
argument_list|)
decl_stmt|;
DECL|field|DELETE_TIME
specifier|static
specifier|final
name|XContentBuilderString
name|DELETE_TIME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"delete_time"
argument_list|)
decl_stmt|;
DECL|field|DELETE_TIME_IN_MILLIS
specifier|static
specifier|final
name|XContentBuilderString
name|DELETE_TIME_IN_MILLIS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"delete_time_in_millis"
argument_list|)
decl_stmt|;
DECL|field|DELETE_CURRENT
specifier|static
specifier|final
name|XContentBuilderString
name|DELETE_CURRENT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"delete_current"
argument_list|)
decl_stmt|;
DECL|field|NOOP_UPDATE_TOTAL
specifier|static
specifier|final
name|XContentBuilderString
name|NOOP_UPDATE_TOTAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"noop_update_total"
argument_list|)
decl_stmt|;
DECL|field|IS_THROTTLED
specifier|static
specifier|final
name|XContentBuilderString
name|IS_THROTTLED
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"is_throttled"
argument_list|)
decl_stmt|;
DECL|field|THROTTLED_TIME_IN_MILLIS
specifier|static
specifier|final
name|XContentBuilderString
name|THROTTLED_TIME_IN_MILLIS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"throttle_time_in_millis"
argument_list|)
decl_stmt|;
DECL|field|THROTTLED_TIME
specifier|static
specifier|final
name|XContentBuilderString
name|THROTTLED_TIME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"throttle_time"
argument_list|)
decl_stmt|;
block|}
DECL|method|readIndexingStats
specifier|public
specifier|static
name|IndexingStats
name|readIndexingStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexingStats
name|indexingStats
init|=
operator|new
name|IndexingStats
argument_list|()
decl_stmt|;
name|indexingStats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|indexingStats
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
name|totalStats
operator|=
name|Stats
operator|.
name|readStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|typeStats
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|typeStats
operator|.
name|put
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|Stats
operator|.
name|readStats
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|totalStats
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|typeStats
operator|==
literal|null
operator|||
name|typeStats
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|typeStats
operator|.
name|size
argument_list|()
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
name|Stats
argument_list|>
name|entry
range|:
name|typeStats
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
