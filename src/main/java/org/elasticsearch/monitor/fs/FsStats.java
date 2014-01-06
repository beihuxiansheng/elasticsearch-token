begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.fs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
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
name|Iterators
import|;
end_import

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
name|Strings
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
name|HashSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|FsStats
specifier|public
class|class
name|FsStats
implements|implements
name|Iterable
argument_list|<
name|FsStats
operator|.
name|Info
argument_list|>
implements|,
name|Streamable
implements|,
name|ToXContent
block|{
DECL|class|Info
specifier|public
specifier|static
class|class
name|Info
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|path
name|String
name|path
decl_stmt|;
annotation|@
name|Nullable
DECL|field|mount
name|String
name|mount
decl_stmt|;
annotation|@
name|Nullable
DECL|field|dev
name|String
name|dev
decl_stmt|;
DECL|field|total
name|long
name|total
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|free
name|long
name|free
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|available
name|long
name|available
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|diskReads
name|long
name|diskReads
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|diskWrites
name|long
name|diskWrites
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|diskReadBytes
name|long
name|diskReadBytes
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|diskWriteBytes
name|long
name|diskWriteBytes
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|diskQueue
name|double
name|diskQueue
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|diskServiceTime
name|double
name|diskServiceTime
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|readInfoFrom
specifier|static
specifier|public
name|Info
name|readInfoFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Info
name|i
init|=
operator|new
name|Info
argument_list|()
decl_stmt|;
name|i
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|i
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
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|after
argument_list|(
name|Version
operator|.
name|V_0_90_7
argument_list|)
condition|)
block|{
name|path
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
name|mount
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|dev
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|total
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|free
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|available
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|diskReads
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|diskWrites
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|diskReadBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|diskWriteBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|diskQueue
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|diskServiceTime
operator|=
name|in
operator|.
name|readDouble
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
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|after
argument_list|(
name|Version
operator|.
name|V_0_90_7
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeOptionalString
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|// total aggregates do not have a path
block|}
else|else
block|{
name|out
operator|.
name|writeString
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeOptionalString
argument_list|(
name|mount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|dev
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|total
argument_list|)
expr_stmt|;
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
name|available
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|diskReads
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|diskWrites
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|diskReadBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|diskWriteBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|diskQueue
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|diskServiceTime
argument_list|)
expr_stmt|;
block|}
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|getMount
specifier|public
name|String
name|getMount
parameter_list|()
block|{
return|return
name|mount
return|;
block|}
DECL|method|getDev
specifier|public
name|String
name|getDev
parameter_list|()
block|{
return|return
name|dev
return|;
block|}
DECL|method|getTotal
specifier|public
name|ByteSizeValue
name|getTotal
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|total
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
operator|new
name|ByteSizeValue
argument_list|(
name|free
argument_list|)
return|;
block|}
DECL|method|getAvailable
specifier|public
name|ByteSizeValue
name|getAvailable
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|available
argument_list|)
return|;
block|}
DECL|method|getDiskReads
specifier|public
name|long
name|getDiskReads
parameter_list|()
block|{
return|return
name|this
operator|.
name|diskReads
return|;
block|}
DECL|method|getDiskWrites
specifier|public
name|long
name|getDiskWrites
parameter_list|()
block|{
return|return
name|this
operator|.
name|diskWrites
return|;
block|}
DECL|method|getDiskReadSizeInBytes
specifier|public
name|long
name|getDiskReadSizeInBytes
parameter_list|()
block|{
return|return
name|diskReadBytes
return|;
block|}
DECL|method|getDiskReadSizeSize
specifier|public
name|ByteSizeValue
name|getDiskReadSizeSize
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|diskReadBytes
argument_list|)
return|;
block|}
DECL|method|getDiskWriteSizeInBytes
specifier|public
name|long
name|getDiskWriteSizeInBytes
parameter_list|()
block|{
return|return
name|diskWriteBytes
return|;
block|}
DECL|method|getDiskWriteSizeSize
specifier|public
name|ByteSizeValue
name|getDiskWriteSizeSize
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|diskWriteBytes
argument_list|)
return|;
block|}
DECL|method|getDiskQueue
specifier|public
name|double
name|getDiskQueue
parameter_list|()
block|{
return|return
name|diskQueue
return|;
block|}
DECL|method|getDiskServiceTime
specifier|public
name|double
name|getDiskServiceTime
parameter_list|()
block|{
return|return
name|diskServiceTime
return|;
block|}
DECL|method|addLong
specifier|private
name|long
name|addLong
parameter_list|(
name|long
name|current
parameter_list|,
name|long
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|current
return|;
block|}
if|if
condition|(
name|current
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|other
return|;
block|}
return|return
name|current
operator|+
name|other
return|;
block|}
DECL|method|addDouble
specifier|private
name|double
name|addDouble
parameter_list|(
name|double
name|current
parameter_list|,
name|double
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|current
return|;
block|}
if|if
condition|(
name|current
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|other
return|;
block|}
return|return
name|current
operator|+
name|other
return|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Info
name|info
parameter_list|)
block|{
name|total
operator|=
name|addLong
argument_list|(
name|total
argument_list|,
name|info
operator|.
name|total
argument_list|)
expr_stmt|;
name|free
operator|=
name|addLong
argument_list|(
name|free
argument_list|,
name|info
operator|.
name|free
argument_list|)
expr_stmt|;
name|available
operator|=
name|addLong
argument_list|(
name|available
argument_list|,
name|info
operator|.
name|available
argument_list|)
expr_stmt|;
name|diskReads
operator|=
name|addLong
argument_list|(
name|diskReads
argument_list|,
name|info
operator|.
name|diskReads
argument_list|)
expr_stmt|;
name|diskWrites
operator|=
name|addLong
argument_list|(
name|diskWrites
argument_list|,
name|info
operator|.
name|diskWrites
argument_list|)
expr_stmt|;
name|diskReadBytes
operator|=
name|addLong
argument_list|(
name|diskReadBytes
argument_list|,
name|info
operator|.
name|diskReadBytes
argument_list|)
expr_stmt|;
name|diskWriteBytes
operator|=
name|addLong
argument_list|(
name|diskWriteBytes
argument_list|,
name|info
operator|.
name|diskWriteBytes
argument_list|)
expr_stmt|;
name|diskQueue
operator|=
name|addDouble
argument_list|(
name|diskQueue
argument_list|,
name|info
operator|.
name|diskQueue
argument_list|)
expr_stmt|;
name|diskServiceTime
operator|=
name|addDouble
argument_list|(
name|diskServiceTime
argument_list|,
name|info
operator|.
name|diskServiceTime
argument_list|)
expr_stmt|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|PATH
specifier|static
specifier|final
name|XContentBuilderString
name|PATH
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
DECL|field|MOUNT
specifier|static
specifier|final
name|XContentBuilderString
name|MOUNT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"mount"
argument_list|)
decl_stmt|;
DECL|field|DEV
specifier|static
specifier|final
name|XContentBuilderString
name|DEV
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"dev"
argument_list|)
decl_stmt|;
DECL|field|TOTAL
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total"
argument_list|)
decl_stmt|;
DECL|field|TOTAL_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total_in_bytes"
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
DECL|field|AVAILABLE
specifier|static
specifier|final
name|XContentBuilderString
name|AVAILABLE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"available"
argument_list|)
decl_stmt|;
DECL|field|AVAILABLE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|AVAILABLE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"available_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|DISK_READS
specifier|static
specifier|final
name|XContentBuilderString
name|DISK_READS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"disk_reads"
argument_list|)
decl_stmt|;
DECL|field|DISK_WRITES
specifier|static
specifier|final
name|XContentBuilderString
name|DISK_WRITES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"disk_writes"
argument_list|)
decl_stmt|;
DECL|field|DISK_IO_OP
specifier|static
specifier|final
name|XContentBuilderString
name|DISK_IO_OP
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"disk_io_op"
argument_list|)
decl_stmt|;
DECL|field|DISK_READ_SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|DISK_READ_SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"disk_read_size"
argument_list|)
decl_stmt|;
DECL|field|DISK_READ_SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|DISK_READ_SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"disk_read_size_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|DISK_WRITE_SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|DISK_WRITE_SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"disk_write_size"
argument_list|)
decl_stmt|;
DECL|field|DISK_WRITE_SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|DISK_WRITE_SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"disk_write_size_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|DISK_IO_SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|DISK_IO_SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"disk_io_size"
argument_list|)
decl_stmt|;
DECL|field|DISK_IO_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|DISK_IO_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"disk_io_size_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|DISK_QUEUE
specifier|static
specifier|final
name|XContentBuilderString
name|DISK_QUEUE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"disk_queue"
argument_list|)
decl_stmt|;
DECL|field|DISK_SERVICE_TIME
specifier|static
specifier|final
name|XContentBuilderString
name|DISK_SERVICE_TIME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"disk_service_time"
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
argument_list|()
expr_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PATH
argument_list|,
name|path
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mount
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MOUNT
argument_list|,
name|mount
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dev
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DEV
argument_list|,
name|dev
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|total
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|TOTAL_IN_BYTES
argument_list|,
name|Fields
operator|.
name|TOTAL
argument_list|,
name|total
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|free
operator|!=
operator|-
literal|1
condition|)
block|{
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
name|free
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|available
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|AVAILABLE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|AVAILABLE
argument_list|,
name|available
argument_list|)
expr_stmt|;
block|}
name|long
name|iop
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|diskReads
operator|!=
operator|-
literal|1
condition|)
block|{
name|iop
operator|=
name|diskReads
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DISK_READS
argument_list|,
name|diskReads
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|diskWrites
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|iop
operator|!=
operator|-
literal|1
condition|)
block|{
name|iop
operator|+=
name|diskWrites
expr_stmt|;
block|}
else|else
block|{
name|iop
operator|=
name|diskWrites
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DISK_WRITES
argument_list|,
name|diskWrites
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|iop
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DISK_IO_OP
argument_list|,
name|iop
argument_list|)
expr_stmt|;
block|}
name|long
name|ioBytes
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|diskReadBytes
operator|!=
operator|-
literal|1
condition|)
block|{
name|ioBytes
operator|=
name|diskReadBytes
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|DISK_READ_SIZE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|DISK_READ_SIZE
argument_list|,
name|diskReadBytes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|diskWriteBytes
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|ioBytes
operator|!=
operator|-
literal|1
condition|)
block|{
name|ioBytes
operator|+=
name|diskWriteBytes
expr_stmt|;
block|}
else|else
block|{
name|ioBytes
operator|=
name|diskWriteBytes
expr_stmt|;
block|}
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|DISK_WRITE_SIZE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|DISK_WRITE_SIZE
argument_list|,
name|diskWriteBytes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ioBytes
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|DISK_IO_IN_BYTES
argument_list|,
name|Fields
operator|.
name|DISK_IO_SIZE
argument_list|,
name|ioBytes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|diskQueue
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DISK_QUEUE
argument_list|,
name|Strings
operator|.
name|format1Decimals
argument_list|(
name|diskQueue
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|diskServiceTime
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DISK_SERVICE_TIME
argument_list|,
name|Strings
operator|.
name|format1Decimals
argument_list|(
name|diskServiceTime
argument_list|,
literal|""
argument_list|)
argument_list|)
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
block|}
DECL|field|timestamp
name|long
name|timestamp
decl_stmt|;
DECL|field|total
name|Info
name|total
decl_stmt|;
DECL|field|infos
name|Info
index|[]
name|infos
decl_stmt|;
DECL|method|FsStats
name|FsStats
parameter_list|()
block|{      }
DECL|method|FsStats
name|FsStats
parameter_list|(
name|long
name|timestamp
parameter_list|,
name|Info
index|[]
name|infos
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|this
operator|.
name|infos
operator|=
name|infos
expr_stmt|;
name|this
operator|.
name|total
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getTotal
specifier|public
name|Info
name|getTotal
parameter_list|()
block|{
return|return
name|total
argument_list|()
return|;
block|}
DECL|method|total
specifier|public
name|Info
name|total
parameter_list|()
block|{
if|if
condition|(
name|total
operator|!=
literal|null
condition|)
block|{
return|return
name|total
return|;
block|}
name|Info
name|res
init|=
operator|new
name|Info
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|seenDevices
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|infos
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|Info
name|subInfo
range|:
name|infos
control|)
block|{
if|if
condition|(
name|subInfo
operator|.
name|dev
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|seenDevices
operator|.
name|add
argument_list|(
name|subInfo
operator|.
name|dev
argument_list|)
condition|)
block|{
continue|continue;
comment|// already added numbers for this device;
block|}
block|}
name|res
operator|.
name|add
argument_list|(
name|subInfo
argument_list|)
expr_stmt|;
block|}
name|total
operator|=
name|res
expr_stmt|;
return|return
name|res
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
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Info
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|forArray
argument_list|(
name|infos
argument_list|)
return|;
block|}
DECL|method|readFsStats
specifier|public
specifier|static
name|FsStats
name|readFsStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|FsStats
name|stats
init|=
operator|new
name|FsStats
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
name|infos
operator|=
operator|new
name|Info
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
name|infos
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|infos
index|[
name|i
index|]
operator|=
name|Info
operator|.
name|readInfoFrom
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
name|infos
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Info
name|info
range|:
name|infos
control|)
block|{
name|info
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|FS
specifier|static
specifier|final
name|XContentBuilderString
name|FS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"fs"
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
DECL|field|DATA
specifier|static
specifier|final
name|XContentBuilderString
name|DATA
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"data"
argument_list|)
decl_stmt|;
DECL|field|TOTAL
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total"
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
name|FS
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
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL
argument_list|)
expr_stmt|;
name|total
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
name|startArray
argument_list|(
name|Fields
operator|.
name|DATA
argument_list|)
expr_stmt|;
for|for
control|(
name|Info
name|info
range|:
name|infos
control|)
block|{
name|info
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
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

