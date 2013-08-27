begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.jvm
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|MemoryMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|RuntimeMXBean
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
comment|/**  *  */
end_comment

begin_class
DECL|class|JvmInfo
specifier|public
class|class
name|JvmInfo
implements|implements
name|Streamable
implements|,
name|Serializable
implements|,
name|ToXContent
block|{
DECL|field|INSTANCE
specifier|private
specifier|static
name|JvmInfo
name|INSTANCE
decl_stmt|;
static|static
block|{
name|RuntimeMXBean
name|runtimeMXBean
init|=
name|ManagementFactory
operator|.
name|getRuntimeMXBean
argument_list|()
decl_stmt|;
name|MemoryMXBean
name|memoryMXBean
init|=
name|ManagementFactory
operator|.
name|getMemoryMXBean
argument_list|()
decl_stmt|;
comment|// returns the<process id>@<host>
name|long
name|pid
decl_stmt|;
name|String
name|xPid
init|=
name|runtimeMXBean
operator|.
name|getName
argument_list|()
decl_stmt|;
try|try
block|{
name|xPid
operator|=
name|xPid
operator|.
name|split
argument_list|(
literal|"@"
argument_list|)
index|[
literal|0
index|]
expr_stmt|;
name|pid
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|xPid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|pid
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|JvmInfo
name|info
init|=
operator|new
name|JvmInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|pid
operator|=
name|pid
expr_stmt|;
name|info
operator|.
name|startTime
operator|=
name|runtimeMXBean
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
name|info
operator|.
name|version
operator|=
name|runtimeMXBean
operator|.
name|getSystemProperties
argument_list|()
operator|.
name|get
argument_list|(
literal|"java.version"
argument_list|)
expr_stmt|;
name|info
operator|.
name|vmName
operator|=
name|runtimeMXBean
operator|.
name|getVmName
argument_list|()
expr_stmt|;
name|info
operator|.
name|vmVendor
operator|=
name|runtimeMXBean
operator|.
name|getVmVendor
argument_list|()
expr_stmt|;
name|info
operator|.
name|vmVersion
operator|=
name|runtimeMXBean
operator|.
name|getVmVersion
argument_list|()
expr_stmt|;
name|info
operator|.
name|mem
operator|=
operator|new
name|Mem
argument_list|()
expr_stmt|;
name|info
operator|.
name|mem
operator|.
name|heapInit
operator|=
name|memoryMXBean
operator|.
name|getHeapMemoryUsage
argument_list|()
operator|.
name|getInit
argument_list|()
operator|<
literal|0
condition|?
literal|0
else|:
name|memoryMXBean
operator|.
name|getHeapMemoryUsage
argument_list|()
operator|.
name|getInit
argument_list|()
expr_stmt|;
name|info
operator|.
name|mem
operator|.
name|heapMax
operator|=
name|memoryMXBean
operator|.
name|getHeapMemoryUsage
argument_list|()
operator|.
name|getMax
argument_list|()
operator|<
literal|0
condition|?
literal|0
else|:
name|memoryMXBean
operator|.
name|getHeapMemoryUsage
argument_list|()
operator|.
name|getMax
argument_list|()
expr_stmt|;
name|info
operator|.
name|mem
operator|.
name|nonHeapInit
operator|=
name|memoryMXBean
operator|.
name|getNonHeapMemoryUsage
argument_list|()
operator|.
name|getInit
argument_list|()
operator|<
literal|0
condition|?
literal|0
else|:
name|memoryMXBean
operator|.
name|getNonHeapMemoryUsage
argument_list|()
operator|.
name|getInit
argument_list|()
expr_stmt|;
name|info
operator|.
name|mem
operator|.
name|nonHeapMax
operator|=
name|memoryMXBean
operator|.
name|getNonHeapMemoryUsage
argument_list|()
operator|.
name|getMax
argument_list|()
operator|<
literal|0
condition|?
literal|0
else|:
name|memoryMXBean
operator|.
name|getNonHeapMemoryUsage
argument_list|()
operator|.
name|getMax
argument_list|()
expr_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|vmClass
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.misc.VM"
argument_list|)
decl_stmt|;
name|info
operator|.
name|mem
operator|.
name|directMemoryMax
operator|=
operator|(
name|Long
operator|)
name|vmClass
operator|.
name|getMethod
argument_list|(
literal|"maxDirectMemory"
argument_list|)
operator|.
name|invoke
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// ignore
block|}
name|info
operator|.
name|inputArguments
operator|=
name|runtimeMXBean
operator|.
name|getInputArguments
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|runtimeMXBean
operator|.
name|getInputArguments
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|info
operator|.
name|bootClassPath
operator|=
name|runtimeMXBean
operator|.
name|getBootClassPath
argument_list|()
expr_stmt|;
name|info
operator|.
name|classPath
operator|=
name|runtimeMXBean
operator|.
name|getClassPath
argument_list|()
expr_stmt|;
name|info
operator|.
name|systemProperties
operator|=
name|runtimeMXBean
operator|.
name|getSystemProperties
argument_list|()
expr_stmt|;
name|INSTANCE
operator|=
name|info
expr_stmt|;
block|}
DECL|method|jvmInfo
specifier|public
specifier|static
name|JvmInfo
name|jvmInfo
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
DECL|field|pid
name|long
name|pid
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|version
name|String
name|version
init|=
literal|""
decl_stmt|;
DECL|field|vmName
name|String
name|vmName
init|=
literal|""
decl_stmt|;
DECL|field|vmVersion
name|String
name|vmVersion
init|=
literal|""
decl_stmt|;
DECL|field|vmVendor
name|String
name|vmVendor
init|=
literal|""
decl_stmt|;
DECL|field|startTime
name|long
name|startTime
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|mem
name|Mem
name|mem
decl_stmt|;
DECL|field|inputArguments
name|String
index|[]
name|inputArguments
decl_stmt|;
DECL|field|bootClassPath
name|String
name|bootClassPath
decl_stmt|;
DECL|field|classPath
name|String
name|classPath
decl_stmt|;
DECL|field|systemProperties
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|systemProperties
decl_stmt|;
DECL|method|JvmInfo
specifier|private
name|JvmInfo
parameter_list|()
block|{     }
comment|/**      * The process id.      */
DECL|method|pid
specifier|public
name|long
name|pid
parameter_list|()
block|{
return|return
name|this
operator|.
name|pid
return|;
block|}
comment|/**      * The process id.      */
DECL|method|getPid
specifier|public
name|long
name|getPid
parameter_list|()
block|{
return|return
name|pid
return|;
block|}
DECL|method|version
specifier|public
name|String
name|version
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|versionAsInteger
specifier|public
name|int
name|versionAsInteger
parameter_list|()
block|{
try|try
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
name|sVersion
init|=
literal|""
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|version
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|Character
operator|.
name|isDigit
argument_list|(
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
operator|&&
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|!=
literal|'.'
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|!=
literal|'.'
condition|)
block|{
name|sVersion
operator|+=
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|sVersion
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
DECL|method|versionUpdatePack
specifier|public
name|int
name|versionUpdatePack
parameter_list|()
block|{
try|try
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
name|sVersion
init|=
literal|""
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|version
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|Character
operator|.
name|isDigit
argument_list|(
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
operator|&&
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|!=
literal|'.'
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|!=
literal|'.'
condition|)
block|{
name|sVersion
operator|+=
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|Integer
operator|.
name|parseInt
argument_list|(
name|sVersion
argument_list|)
expr_stmt|;
name|int
name|from
decl_stmt|;
if|if
condition|(
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'_'
condition|)
block|{
comment|// 1.7.0_4
name|from
operator|=
operator|++
name|i
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'-'
operator|&&
name|version
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
literal|'u'
condition|)
block|{
comment|// 1.7.0-u2-b21
name|i
operator|=
name|i
operator|+
literal|2
expr_stmt|;
name|from
operator|=
name|i
expr_stmt|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
for|for
control|(
init|;
name|i
operator|<
name|version
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|Character
operator|.
name|isDigit
argument_list|(
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
operator|&&
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|!=
literal|'.'
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|from
operator|==
name|i
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|version
operator|.
name|substring
argument_list|(
name|from
argument_list|,
name|i
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
DECL|method|vmName
specifier|public
name|String
name|vmName
parameter_list|()
block|{
return|return
name|vmName
return|;
block|}
DECL|method|getVmName
specifier|public
name|String
name|getVmName
parameter_list|()
block|{
return|return
name|vmName
return|;
block|}
DECL|method|vmVersion
specifier|public
name|String
name|vmVersion
parameter_list|()
block|{
return|return
name|vmVersion
return|;
block|}
DECL|method|getVmVersion
specifier|public
name|String
name|getVmVersion
parameter_list|()
block|{
return|return
name|vmVersion
return|;
block|}
DECL|method|vmVendor
specifier|public
name|String
name|vmVendor
parameter_list|()
block|{
return|return
name|vmVendor
return|;
block|}
DECL|method|getVmVendor
specifier|public
name|String
name|getVmVendor
parameter_list|()
block|{
return|return
name|vmVendor
return|;
block|}
DECL|method|startTime
specifier|public
name|long
name|startTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
DECL|method|getStartTime
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
DECL|method|mem
specifier|public
name|Mem
name|mem
parameter_list|()
block|{
return|return
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
DECL|method|inputArguments
specifier|public
name|String
index|[]
name|inputArguments
parameter_list|()
block|{
return|return
name|inputArguments
return|;
block|}
DECL|method|getInputArguments
specifier|public
name|String
index|[]
name|getInputArguments
parameter_list|()
block|{
return|return
name|inputArguments
return|;
block|}
DECL|method|bootClassPath
specifier|public
name|String
name|bootClassPath
parameter_list|()
block|{
return|return
name|bootClassPath
return|;
block|}
DECL|method|getBootClassPath
specifier|public
name|String
name|getBootClassPath
parameter_list|()
block|{
return|return
name|bootClassPath
return|;
block|}
DECL|method|classPath
specifier|public
name|String
name|classPath
parameter_list|()
block|{
return|return
name|classPath
return|;
block|}
DECL|method|getClassPath
specifier|public
name|String
name|getClassPath
parameter_list|()
block|{
return|return
name|classPath
return|;
block|}
DECL|method|systemProperties
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|systemProperties
parameter_list|()
block|{
return|return
name|systemProperties
return|;
block|}
DECL|method|getSystemProperties
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getSystemProperties
parameter_list|()
block|{
return|return
name|systemProperties
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
name|JVM
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PID
argument_list|,
name|pid
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|VERSION
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|VM_NAME
argument_list|,
name|vmName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|VM_VERSION
argument_list|,
name|vmVersion
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|VM_VENDOR
argument_list|,
name|vmVendor
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|START_TIME
argument_list|,
name|startTime
argument_list|)
expr_stmt|;
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
name|HEAP_INIT_IN_BYTES
argument_list|,
name|Fields
operator|.
name|HEAP_INIT
argument_list|,
name|mem
operator|.
name|heapInit
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|HEAP_MAX_IN_BYTES
argument_list|,
name|Fields
operator|.
name|HEAP_MAX
argument_list|,
name|mem
operator|.
name|heapMax
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|NON_HEAP_INIT_IN_BYTES
argument_list|,
name|Fields
operator|.
name|NON_HEAP_INIT
argument_list|,
name|mem
operator|.
name|nonHeapInit
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|NON_HEAP_MAX_IN_BYTES
argument_list|,
name|Fields
operator|.
name|NON_HEAP_MAX
argument_list|,
name|mem
operator|.
name|nonHeapMax
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|DIRECT_MAX_IN_BYTES
argument_list|,
name|Fields
operator|.
name|DIRECT_MAX
argument_list|,
name|mem
operator|.
name|directMemoryMax
argument_list|)
expr_stmt|;
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
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|JVM
specifier|static
specifier|final
name|XContentBuilderString
name|JVM
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"jvm"
argument_list|)
decl_stmt|;
DECL|field|PID
specifier|static
specifier|final
name|XContentBuilderString
name|PID
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"pid"
argument_list|)
decl_stmt|;
DECL|field|VERSION
specifier|static
specifier|final
name|XContentBuilderString
name|VERSION
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"version"
argument_list|)
decl_stmt|;
DECL|field|VM_NAME
specifier|static
specifier|final
name|XContentBuilderString
name|VM_NAME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"vm_name"
argument_list|)
decl_stmt|;
DECL|field|VM_VERSION
specifier|static
specifier|final
name|XContentBuilderString
name|VM_VERSION
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"vm_version"
argument_list|)
decl_stmt|;
DECL|field|VM_VENDOR
specifier|static
specifier|final
name|XContentBuilderString
name|VM_VENDOR
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"vm_vendor"
argument_list|)
decl_stmt|;
DECL|field|START_TIME
specifier|static
specifier|final
name|XContentBuilderString
name|START_TIME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"start_time"
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
DECL|field|HEAP_INIT
specifier|static
specifier|final
name|XContentBuilderString
name|HEAP_INIT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"heap_init"
argument_list|)
decl_stmt|;
DECL|field|HEAP_INIT_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|HEAP_INIT_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"heap_init_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|HEAP_MAX
specifier|static
specifier|final
name|XContentBuilderString
name|HEAP_MAX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"heap_max"
argument_list|)
decl_stmt|;
DECL|field|HEAP_MAX_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|HEAP_MAX_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"heap_max_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|NON_HEAP_INIT
specifier|static
specifier|final
name|XContentBuilderString
name|NON_HEAP_INIT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"non_heap_init"
argument_list|)
decl_stmt|;
DECL|field|NON_HEAP_INIT_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|NON_HEAP_INIT_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"non_heap_init_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|NON_HEAP_MAX
specifier|static
specifier|final
name|XContentBuilderString
name|NON_HEAP_MAX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"non_heap_max"
argument_list|)
decl_stmt|;
DECL|field|NON_HEAP_MAX_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|NON_HEAP_MAX_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"non_heap_max_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|DIRECT_MAX
specifier|static
specifier|final
name|XContentBuilderString
name|DIRECT_MAX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"direct_max"
argument_list|)
decl_stmt|;
DECL|field|DIRECT_MAX_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|DIRECT_MAX_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"direct_max_in_bytes"
argument_list|)
decl_stmt|;
block|}
DECL|method|readJvmInfo
specifier|public
specifier|static
name|JvmInfo
name|readJvmInfo
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|JvmInfo
name|jvmInfo
init|=
operator|new
name|JvmInfo
argument_list|()
decl_stmt|;
name|jvmInfo
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|jvmInfo
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
name|pid
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|version
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|vmName
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|vmVersion
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|vmVendor
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|startTime
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|inputArguments
operator|=
operator|new
name|String
index|[
name|in
operator|.
name|readInt
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
name|inputArguments
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|inputArguments
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
name|bootClassPath
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|classPath
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|systemProperties
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readInt
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
name|systemProperties
operator|.
name|put
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mem
operator|=
operator|new
name|Mem
argument_list|()
expr_stmt|;
name|mem
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
name|out
operator|.
name|writeLong
argument_list|(
name|pid
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|vmName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|vmVersion
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|vmVendor
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|inputArguments
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|inputArgument
range|:
name|inputArguments
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|inputArgument
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeString
argument_list|(
name|bootClassPath
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|classPath
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|systemProperties
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
name|String
argument_list|>
name|entry
range|:
name|systemProperties
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
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mem
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
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
DECL|field|heapInit
name|long
name|heapInit
init|=
literal|0
decl_stmt|;
DECL|field|heapMax
name|long
name|heapMax
init|=
literal|0
decl_stmt|;
DECL|field|nonHeapInit
name|long
name|nonHeapInit
init|=
literal|0
decl_stmt|;
DECL|field|nonHeapMax
name|long
name|nonHeapMax
init|=
literal|0
decl_stmt|;
DECL|field|directMemoryMax
name|long
name|directMemoryMax
init|=
literal|0
decl_stmt|;
DECL|method|Mem
name|Mem
parameter_list|()
block|{         }
DECL|method|heapInit
specifier|public
name|ByteSizeValue
name|heapInit
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|heapInit
argument_list|)
return|;
block|}
DECL|method|getHeapInit
specifier|public
name|ByteSizeValue
name|getHeapInit
parameter_list|()
block|{
return|return
name|heapInit
argument_list|()
return|;
block|}
DECL|method|heapMax
specifier|public
name|ByteSizeValue
name|heapMax
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|heapMax
argument_list|)
return|;
block|}
DECL|method|getHeapMax
specifier|public
name|ByteSizeValue
name|getHeapMax
parameter_list|()
block|{
return|return
name|heapMax
argument_list|()
return|;
block|}
DECL|method|nonHeapInit
specifier|public
name|ByteSizeValue
name|nonHeapInit
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|nonHeapInit
argument_list|)
return|;
block|}
DECL|method|getNonHeapInit
specifier|public
name|ByteSizeValue
name|getNonHeapInit
parameter_list|()
block|{
return|return
name|nonHeapInit
argument_list|()
return|;
block|}
DECL|method|nonHeapMax
specifier|public
name|ByteSizeValue
name|nonHeapMax
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|nonHeapMax
argument_list|)
return|;
block|}
DECL|method|getNonHeapMax
specifier|public
name|ByteSizeValue
name|getNonHeapMax
parameter_list|()
block|{
return|return
name|nonHeapMax
argument_list|()
return|;
block|}
DECL|method|directMemoryMax
specifier|public
name|ByteSizeValue
name|directMemoryMax
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|directMemoryMax
argument_list|)
return|;
block|}
DECL|method|getDirectMemoryMax
specifier|public
name|ByteSizeValue
name|getDirectMemoryMax
parameter_list|()
block|{
return|return
name|directMemoryMax
argument_list|()
return|;
block|}
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
name|heapInit
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|heapMax
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|nonHeapInit
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|nonHeapMax
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|directMemoryMax
operator|=
name|in
operator|.
name|readVLong
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
name|heapInit
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|heapMax
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|nonHeapInit
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|nonHeapMax
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|directMemoryMax
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

