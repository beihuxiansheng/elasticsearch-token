begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.os
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|os
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
name|ByteSizeValue
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
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|OsStats
specifier|public
class|class
name|OsStats
implements|implements
name|Streamable
implements|,
name|Serializable
implements|,
name|ToXContent
block|{
DECL|field|EMPTY_LOAD
specifier|public
specifier|static
specifier|final
name|double
index|[]
name|EMPTY_LOAD
init|=
operator|new
name|double
index|[
literal|0
index|]
decl_stmt|;
DECL|field|timestamp
name|long
name|timestamp
decl_stmt|;
DECL|field|loadAverage
name|double
index|[]
name|loadAverage
init|=
name|EMPTY_LOAD
decl_stmt|;
DECL|field|uptime
name|long
name|uptime
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|cpu
name|Cpu
name|cpu
init|=
literal|null
decl_stmt|;
DECL|field|mem
name|Mem
name|mem
init|=
literal|null
decl_stmt|;
DECL|field|swap
name|Swap
name|swap
init|=
literal|null
decl_stmt|;
DECL|method|OsStats
name|OsStats
parameter_list|()
block|{     }
DECL|method|timestamp
specifier|public
name|long
name|timestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
DECL|method|getTimestamp
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
argument_list|()
return|;
block|}
DECL|method|loadAverage
specifier|public
name|double
index|[]
name|loadAverage
parameter_list|()
block|{
return|return
name|loadAverage
return|;
block|}
DECL|method|getLoadAverage
specifier|public
name|double
index|[]
name|getLoadAverage
parameter_list|()
block|{
return|return
name|loadAverage
argument_list|()
return|;
block|}
DECL|method|uptime
specifier|public
name|TimeValue
name|uptime
parameter_list|()
block|{
return|return
operator|new
name|TimeValue
argument_list|(
name|uptime
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
return|;
block|}
DECL|method|getUptime
specifier|public
name|TimeValue
name|getUptime
parameter_list|()
block|{
return|return
name|uptime
argument_list|()
return|;
block|}
DECL|method|cpu
specifier|public
name|Cpu
name|cpu
parameter_list|()
block|{
return|return
name|this
operator|.
name|cpu
return|;
block|}
DECL|method|getCpu
specifier|public
name|Cpu
name|getCpu
parameter_list|()
block|{
return|return
name|cpu
argument_list|()
return|;
block|}
DECL|method|mem
specifier|public
name|Mem
name|mem
parameter_list|()
block|{
return|return
name|this
operator|.
name|mem
return|;
block|}
DECL|method|getMem
specifier|public
name|Mem
name|getMem
parameter_list|()
block|{
return|return
name|mem
argument_list|()
return|;
block|}
DECL|method|swap
specifier|public
name|Swap
name|swap
parameter_list|()
block|{
return|return
name|this
operator|.
name|swap
return|;
block|}
DECL|method|getSwap
specifier|public
name|Swap
name|getSwap
parameter_list|()
block|{
return|return
name|swap
argument_list|()
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|OS
specifier|static
specifier|final
name|XContentBuilderString
name|OS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"os"
argument_list|)
decl_stmt|;
DECL|field|TIMESTAMP
specifier|static
specifier|final
name|XContentBuilderString
name|TIMESTAMP
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"timestamp"
argument_list|)
decl_stmt|;
DECL|field|UPTIME
specifier|static
specifier|final
name|XContentBuilderString
name|UPTIME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"uptime"
argument_list|)
decl_stmt|;
DECL|field|UPTIME_IN_MILLIS
specifier|static
specifier|final
name|XContentBuilderString
name|UPTIME_IN_MILLIS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"uptime_in_millis"
argument_list|)
decl_stmt|;
DECL|field|LOAD_AVERAGE
specifier|static
specifier|final
name|XContentBuilderString
name|LOAD_AVERAGE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"load_average"
argument_list|)
decl_stmt|;
DECL|field|CPU
specifier|static
specifier|final
name|XContentBuilderString
name|CPU
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"cpu"
argument_list|)
decl_stmt|;
DECL|field|SYS
specifier|static
specifier|final
name|XContentBuilderString
name|SYS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"sys"
argument_list|)
decl_stmt|;
DECL|field|USER
specifier|static
specifier|final
name|XContentBuilderString
name|USER
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"user"
argument_list|)
decl_stmt|;
DECL|field|IDLE
specifier|static
specifier|final
name|XContentBuilderString
name|IDLE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"idle"
argument_list|)
decl_stmt|;
DECL|field|STOLEN
specifier|static
specifier|final
name|XContentBuilderString
name|STOLEN
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"stolen"
argument_list|)
decl_stmt|;
DECL|field|MEM
specifier|static
specifier|final
name|XContentBuilderString
name|MEM
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"mem"
argument_list|)
decl_stmt|;
DECL|field|SWAP
specifier|static
specifier|final
name|XContentBuilderString
name|SWAP
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"swap"
argument_list|)
decl_stmt|;
DECL|field|FREE
specifier|static
specifier|final
name|XContentBuilderString
name|FREE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"free"
argument_list|)
decl_stmt|;
DECL|field|FREE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|FREE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"free_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|USED
specifier|static
specifier|final
name|XContentBuilderString
name|USED
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"used"
argument_list|)
decl_stmt|;
DECL|field|USED_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|USED_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"used_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|FREE_PERCENT
specifier|static
specifier|final
name|XContentBuilderString
name|FREE_PERCENT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"free_percent"
argument_list|)
decl_stmt|;
DECL|field|USED_PERCENT
specifier|static
specifier|final
name|XContentBuilderString
name|USED_PERCENT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"used_percent"
argument_list|)
decl_stmt|;
DECL|field|ACTUAL_FREE
specifier|static
specifier|final
name|XContentBuilderString
name|ACTUAL_FREE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"actual_free"
argument_list|)
decl_stmt|;
DECL|field|ACTUAL_FREE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|ACTUAL_FREE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"actual_free_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|ACTUAL_USED
specifier|static
specifier|final
name|XContentBuilderString
name|ACTUAL_USED
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"actual_used"
argument_list|)
decl_stmt|;
DECL|field|ACTUAL_USED_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|ACTUAL_USED_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"actual_used_in_bytes"
argument_list|)
decl_stmt|;
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
name|startObject
argument_list|(
name|Fields
operator|.
name|OS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TIMESTAMP
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
if|if
condition|(
name|uptime
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|timeValueField
argument_list|(
name|Fields
operator|.
name|UPTIME_IN_MILLIS
argument_list|,
name|Fields
operator|.
name|UPTIME
argument_list|,
name|uptime
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|loadAverage
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|LOAD_AVERAGE
argument_list|)
expr_stmt|;
for|for
control|(
name|double
name|value
range|:
name|loadAverage
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cpu
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|CPU
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SYS
argument_list|,
name|cpu
operator|.
name|sys
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|USER
argument_list|,
name|cpu
operator|.
name|user
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|IDLE
argument_list|,
name|cpu
operator|.
name|idle
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STOLEN
argument_list|,
name|cpu
operator|.
name|stolen
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mem
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|MEM
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|FREE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|FREE
argument_list|,
name|mem
operator|.
name|free
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|USED_IN_BYTES
argument_list|,
name|Fields
operator|.
name|USED
argument_list|,
name|mem
operator|.
name|used
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|FREE_PERCENT
argument_list|,
name|mem
operator|.
name|freePercent
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|USED_PERCENT
argument_list|,
name|mem
operator|.
name|usedPercent
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|ACTUAL_FREE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|ACTUAL_FREE
argument_list|,
name|mem
operator|.
name|actualFree
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|ACTUAL_USED_IN_BYTES
argument_list|,
name|Fields
operator|.
name|ACTUAL_USED
argument_list|,
name|mem
operator|.
name|actualUsed
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|swap
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|SWAP
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|USED_IN_BYTES
argument_list|,
name|Fields
operator|.
name|USED
argument_list|,
name|swap
operator|.
name|used
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|FREE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|FREE
argument_list|,
name|swap
operator|.
name|free
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
return|return
name|builder
return|;
block|}
DECL|method|readOsStats
specifier|public
specifier|static
name|OsStats
name|readOsStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|OsStats
name|stats
init|=
operator|new
name|OsStats
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
name|timestamp
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|loadAverage
operator|=
operator|new
name|double
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
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
name|loadAverage
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|loadAverage
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
block|}
name|uptime
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|cpu
operator|=
name|Cpu
operator|.
name|readCpu
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|mem
operator|=
name|Mem
operator|.
name|readMem
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|swap
operator|=
name|Swap
operator|.
name|readSwap
argument_list|(
name|in
argument_list|)
expr_stmt|;
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
name|out
operator|.
name|writeVLong
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|loadAverage
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|double
name|val
range|:
name|loadAverage
control|)
block|{
name|out
operator|.
name|writeDouble
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeLong
argument_list|(
name|uptime
argument_list|)
expr_stmt|;
if|if
condition|(
name|cpu
operator|==
literal|null
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
name|cpu
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mem
operator|==
literal|null
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
name|mem
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|swap
operator|==
literal|null
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
name|swap
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Swap
specifier|public
specifier|static
class|class
name|Swap
implements|implements
name|Streamable
implements|,
name|Serializable
block|{
DECL|field|free
name|long
name|free
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|used
name|long
name|used
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|free
specifier|public
name|ByteSizeValue
name|free
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|free
argument_list|)
return|;
block|}
DECL|method|getFree
specifier|public
name|ByteSizeValue
name|getFree
parameter_list|()
block|{
return|return
name|free
argument_list|()
return|;
block|}
DECL|method|used
specifier|public
name|ByteSizeValue
name|used
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|used
argument_list|)
return|;
block|}
DECL|method|getUsed
specifier|public
name|ByteSizeValue
name|getUsed
parameter_list|()
block|{
return|return
name|used
argument_list|()
return|;
block|}
DECL|method|readSwap
specifier|public
specifier|static
name|Swap
name|readSwap
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Swap
name|swap
init|=
operator|new
name|Swap
argument_list|()
decl_stmt|;
name|swap
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|swap
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
name|free
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|used
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
name|writeLong
argument_list|(
name|free
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|used
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Mem
specifier|public
specifier|static
class|class
name|Mem
implements|implements
name|Streamable
implements|,
name|Serializable
block|{
DECL|field|free
name|long
name|free
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|freePercent
name|short
name|freePercent
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|used
name|long
name|used
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|usedPercent
name|short
name|usedPercent
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|actualFree
name|long
name|actualFree
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|actualUsed
name|long
name|actualUsed
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|readMem
specifier|public
specifier|static
name|Mem
name|readMem
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Mem
name|mem
init|=
operator|new
name|Mem
argument_list|()
decl_stmt|;
name|mem
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|mem
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
name|free
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|freePercent
operator|=
name|in
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|used
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|usedPercent
operator|=
name|in
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|actualFree
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|actualUsed
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
name|writeLong
argument_list|(
name|free
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|freePercent
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|used
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|usedPercent
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|actualFree
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|actualUsed
argument_list|)
expr_stmt|;
block|}
DECL|method|used
specifier|public
name|ByteSizeValue
name|used
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|used
argument_list|)
return|;
block|}
DECL|method|getUsed
specifier|public
name|ByteSizeValue
name|getUsed
parameter_list|()
block|{
return|return
name|used
argument_list|()
return|;
block|}
DECL|method|usedPercent
specifier|public
name|short
name|usedPercent
parameter_list|()
block|{
return|return
name|usedPercent
return|;
block|}
DECL|method|getUsedPercent
specifier|public
name|short
name|getUsedPercent
parameter_list|()
block|{
return|return
name|usedPercent
argument_list|()
return|;
block|}
DECL|method|free
specifier|public
name|ByteSizeValue
name|free
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|free
argument_list|)
return|;
block|}
DECL|method|getFree
specifier|public
name|ByteSizeValue
name|getFree
parameter_list|()
block|{
return|return
name|free
argument_list|()
return|;
block|}
DECL|method|freePercent
specifier|public
name|short
name|freePercent
parameter_list|()
block|{
return|return
name|freePercent
return|;
block|}
DECL|method|getFreePercent
specifier|public
name|short
name|getFreePercent
parameter_list|()
block|{
return|return
name|freePercent
argument_list|()
return|;
block|}
DECL|method|actualFree
specifier|public
name|ByteSizeValue
name|actualFree
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|actualFree
argument_list|)
return|;
block|}
DECL|method|getActualFree
specifier|public
name|ByteSizeValue
name|getActualFree
parameter_list|()
block|{
return|return
name|actualFree
argument_list|()
return|;
block|}
DECL|method|actualUsed
specifier|public
name|ByteSizeValue
name|actualUsed
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|actualUsed
argument_list|)
return|;
block|}
DECL|method|getActualUsed
specifier|public
name|ByteSizeValue
name|getActualUsed
parameter_list|()
block|{
return|return
name|actualUsed
argument_list|()
return|;
block|}
block|}
DECL|class|Cpu
specifier|public
specifier|static
class|class
name|Cpu
implements|implements
name|Streamable
implements|,
name|Serializable
block|{
DECL|field|sys
name|short
name|sys
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|user
name|short
name|user
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|idle
name|short
name|idle
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|stolen
name|short
name|stolen
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|Cpu
name|Cpu
parameter_list|()
block|{          }
DECL|method|readCpu
specifier|public
specifier|static
name|Cpu
name|readCpu
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Cpu
name|cpu
init|=
operator|new
name|Cpu
argument_list|()
decl_stmt|;
name|cpu
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|cpu
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
name|sys
operator|=
name|in
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|user
operator|=
name|in
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|idle
operator|=
name|in
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|stolen
operator|=
name|in
operator|.
name|readShort
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
name|writeShort
argument_list|(
name|sys
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|idle
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|stolen
argument_list|)
expr_stmt|;
block|}
DECL|method|sys
specifier|public
name|short
name|sys
parameter_list|()
block|{
return|return
name|sys
return|;
block|}
DECL|method|getSys
specifier|public
name|short
name|getSys
parameter_list|()
block|{
return|return
name|sys
argument_list|()
return|;
block|}
DECL|method|user
specifier|public
name|short
name|user
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|getUser
specifier|public
name|short
name|getUser
parameter_list|()
block|{
return|return
name|user
argument_list|()
return|;
block|}
DECL|method|idle
specifier|public
name|short
name|idle
parameter_list|()
block|{
return|return
name|idle
return|;
block|}
DECL|method|getIdle
specifier|public
name|short
name|getIdle
parameter_list|()
block|{
return|return
name|idle
argument_list|()
return|;
block|}
DECL|method|stolen
specifier|public
name|short
name|stolen
parameter_list|()
block|{
return|return
name|stolen
return|;
block|}
DECL|method|getStolen
specifier|public
name|short
name|getStolen
parameter_list|()
block|{
return|return
name|stolen
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

