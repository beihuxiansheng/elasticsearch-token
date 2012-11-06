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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|OsInfo
specifier|public
class|class
name|OsInfo
implements|implements
name|Streamable
implements|,
name|Serializable
implements|,
name|ToXContent
block|{
DECL|field|refreshInterval
name|long
name|refreshInterval
decl_stmt|;
DECL|field|availableProcessors
name|int
name|availableProcessors
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
DECL|method|OsInfo
name|OsInfo
parameter_list|()
block|{     }
DECL|method|refreshInterval
specifier|public
name|long
name|refreshInterval
parameter_list|()
block|{
return|return
name|this
operator|.
name|refreshInterval
return|;
block|}
DECL|method|getRefreshInterval
specifier|public
name|long
name|getRefreshInterval
parameter_list|()
block|{
return|return
name|this
operator|.
name|refreshInterval
return|;
block|}
DECL|method|availableProcessors
specifier|public
name|int
name|availableProcessors
parameter_list|()
block|{
return|return
name|this
operator|.
name|availableProcessors
return|;
block|}
DECL|method|getAvailableProcessors
specifier|public
name|int
name|getAvailableProcessors
parameter_list|()
block|{
return|return
name|this
operator|.
name|availableProcessors
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
DECL|field|REFRESH_INTERVAL
specifier|static
specifier|final
name|XContentBuilderString
name|REFRESH_INTERVAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"refresh_interval"
argument_list|)
decl_stmt|;
DECL|field|AVAILABLE_PROCESSORS
specifier|static
specifier|final
name|XContentBuilderString
name|AVAILABLE_PROCESSORS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"available_processors"
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
DECL|field|VENDOR
specifier|static
specifier|final
name|XContentBuilderString
name|VENDOR
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"vendor"
argument_list|)
decl_stmt|;
DECL|field|MODEL
specifier|static
specifier|final
name|XContentBuilderString
name|MODEL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"model"
argument_list|)
decl_stmt|;
DECL|field|MHZ
specifier|static
specifier|final
name|XContentBuilderString
name|MHZ
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"mhz"
argument_list|)
decl_stmt|;
DECL|field|TOTAL_CORES
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL_CORES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total_cores"
argument_list|)
decl_stmt|;
DECL|field|TOTAL_SOCKETS
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL_SOCKETS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total_sockets"
argument_list|)
decl_stmt|;
DECL|field|CORES_PER_SOCKET
specifier|static
specifier|final
name|XContentBuilderString
name|CORES_PER_SOCKET
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"cores_per_socket"
argument_list|)
decl_stmt|;
DECL|field|CACHE_SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|CACHE_SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"cache_size"
argument_list|)
decl_stmt|;
DECL|field|CACHE_SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|CACHE_SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"cache_size_in_bytes"
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
name|REFRESH_INTERVAL
argument_list|,
name|refreshInterval
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|AVAILABLE_PROCESSORS
argument_list|,
name|availableProcessors
argument_list|)
expr_stmt|;
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
name|VENDOR
argument_list|,
name|cpu
operator|.
name|vendor
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MODEL
argument_list|,
name|cpu
operator|.
name|model
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MHZ
argument_list|,
name|cpu
operator|.
name|mhz
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL_CORES
argument_list|,
name|cpu
operator|.
name|totalCores
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL_SOCKETS
argument_list|,
name|cpu
operator|.
name|totalSockets
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|CORES_PER_SOCKET
argument_list|,
name|cpu
operator|.
name|coresPerSocket
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|CACHE_SIZE
argument_list|,
name|cpu
operator|.
name|cacheSize
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|CACHE_SIZE_IN_BYTES
argument_list|,
name|cpu
operator|.
name|cacheSize
argument_list|()
operator|.
name|bytes
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
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL
argument_list|,
name|mem
operator|.
name|total
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL_IN_BYTES
argument_list|,
name|mem
operator|.
name|total
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
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL
argument_list|,
name|swap
operator|.
name|total
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL_IN_BYTES
argument_list|,
name|swap
operator|.
name|total
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
DECL|method|readOsInfo
specifier|public
specifier|static
name|OsInfo
name|readOsInfo
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|OsInfo
name|info
init|=
operator|new
name|OsInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|info
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
name|refreshInterval
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|availableProcessors
operator|=
name|in
operator|.
name|readInt
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
name|writeLong
argument_list|(
name|refreshInterval
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|availableProcessors
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
DECL|field|total
name|long
name|total
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|Swap
name|Swap
parameter_list|()
block|{          }
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
name|total
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
name|total
argument_list|)
expr_stmt|;
block|}
DECL|method|total
specifier|public
name|ByteSizeValue
name|total
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
DECL|method|getTotal
specifier|public
name|ByteSizeValue
name|getTotal
parameter_list|()
block|{
return|return
name|total
argument_list|()
return|;
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
DECL|field|total
name|long
name|total
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|Mem
name|Mem
parameter_list|()
block|{          }
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
name|total
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
name|total
argument_list|)
expr_stmt|;
block|}
DECL|method|total
specifier|public
name|ByteSizeValue
name|total
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
DECL|method|getTotal
specifier|public
name|ByteSizeValue
name|getTotal
parameter_list|()
block|{
return|return
name|total
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
DECL|field|vendor
name|String
name|vendor
init|=
literal|""
decl_stmt|;
DECL|field|model
name|String
name|model
init|=
literal|""
decl_stmt|;
DECL|field|mhz
name|int
name|mhz
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|totalCores
name|int
name|totalCores
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|totalSockets
name|int
name|totalSockets
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|coresPerSocket
name|int
name|coresPerSocket
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|cacheSize
name|long
name|cacheSize
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|Cpu
name|Cpu
parameter_list|()
block|{          }
DECL|method|vendor
specifier|public
name|String
name|vendor
parameter_list|()
block|{
return|return
name|this
operator|.
name|vendor
return|;
block|}
DECL|method|getVendor
specifier|public
name|String
name|getVendor
parameter_list|()
block|{
return|return
name|vendor
argument_list|()
return|;
block|}
DECL|method|model
specifier|public
name|String
name|model
parameter_list|()
block|{
return|return
name|model
return|;
block|}
DECL|method|getModel
specifier|public
name|String
name|getModel
parameter_list|()
block|{
return|return
name|model
return|;
block|}
DECL|method|mhz
specifier|public
name|int
name|mhz
parameter_list|()
block|{
return|return
name|mhz
return|;
block|}
DECL|method|getMhz
specifier|public
name|int
name|getMhz
parameter_list|()
block|{
return|return
name|mhz
return|;
block|}
DECL|method|totalCores
specifier|public
name|int
name|totalCores
parameter_list|()
block|{
return|return
name|totalCores
return|;
block|}
DECL|method|getTotalCores
specifier|public
name|int
name|getTotalCores
parameter_list|()
block|{
return|return
name|totalCores
argument_list|()
return|;
block|}
DECL|method|totalSockets
specifier|public
name|int
name|totalSockets
parameter_list|()
block|{
return|return
name|totalSockets
return|;
block|}
DECL|method|getTotalSockets
specifier|public
name|int
name|getTotalSockets
parameter_list|()
block|{
return|return
name|totalSockets
argument_list|()
return|;
block|}
DECL|method|coresPerSocket
specifier|public
name|int
name|coresPerSocket
parameter_list|()
block|{
return|return
name|coresPerSocket
return|;
block|}
DECL|method|getCoresPerSocket
specifier|public
name|int
name|getCoresPerSocket
parameter_list|()
block|{
return|return
name|coresPerSocket
argument_list|()
return|;
block|}
DECL|method|cacheSize
specifier|public
name|ByteSizeValue
name|cacheSize
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|cacheSize
argument_list|)
return|;
block|}
DECL|method|getCacheSize
specifier|public
name|ByteSizeValue
name|getCacheSize
parameter_list|()
block|{
return|return
name|cacheSize
argument_list|()
return|;
block|}
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
name|vendor
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|model
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|mhz
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|totalCores
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|totalSockets
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|coresPerSocket
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|cacheSize
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
name|writeUTF
argument_list|(
name|vendor
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|model
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|mhz
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|totalCores
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|totalSockets
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|coresPerSocket
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|cacheSize
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

