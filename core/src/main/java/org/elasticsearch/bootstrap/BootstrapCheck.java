begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bootstrap
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Constants
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|settings
operator|.
name|Settings
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
name|transport
operator|.
name|BoundTransportAddress
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
name|transport
operator|.
name|TransportAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|elect
operator|.
name|ElectMasterService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|process
operator|.
name|ProcessProbe
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
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
name|Arrays
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
comment|/**  * We enforce limits once any network host is configured. In this case we assume the node is running in production  * and all production limit checks must pass. This should be extended as we go to settings like:  * - discovery.zen.ping.unicast.hosts is set if we use zen disco  * - ensure we can write in all data directories  * - fail if vm.max_map_count is under a certain limit (not sure if this works cross platform)  * - fail if the default cluster.name is used, if this is setup on network a real clustername should be used?  */
end_comment

begin_class
DECL|class|BootstrapCheck
specifier|final
class|class
name|BootstrapCheck
block|{
DECL|method|BootstrapCheck
specifier|private
name|BootstrapCheck
parameter_list|()
block|{     }
comment|/**      * checks the current limits against the snapshot or release build      * checks      *      * @param settings              the current node settings      * @param boundTransportAddress the node network bindings      */
DECL|method|check
specifier|static
name|void
name|check
parameter_list|(
specifier|final
name|Settings
name|settings
parameter_list|,
specifier|final
name|BoundTransportAddress
name|boundTransportAddress
parameter_list|)
block|{
name|check
argument_list|(
name|enforceLimits
argument_list|(
name|boundTransportAddress
argument_list|)
argument_list|,
name|checks
argument_list|(
name|settings
argument_list|)
argument_list|,
name|Node
operator|.
name|NODE_NAME_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * executes the provided checks and fails the node if      * enforceLimits is true, otherwise logs warnings      *      * @param enforceLimits true if the checks should be enforced or      *                      warned      * @param checks        the checks to execute      * @param nodeName      the node name to be used as a logging prefix      */
comment|// visible for testing
DECL|method|check
specifier|static
name|void
name|check
parameter_list|(
specifier|final
name|boolean
name|enforceLimits
parameter_list|,
specifier|final
name|List
argument_list|<
name|Check
argument_list|>
name|checks
parameter_list|,
specifier|final
name|String
name|nodeName
parameter_list|)
block|{
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|BootstrapCheck
operator|.
name|class
argument_list|,
name|nodeName
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|errors
init|=
name|checks
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|BootstrapCheck
operator|.
name|Check
operator|::
name|check
argument_list|)
operator|.
name|map
argument_list|(
name|BootstrapCheck
operator|.
name|Check
operator|::
name|errorMessage
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
if|if
condition|(
operator|!
name|errors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|messages
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
operator|+
name|errors
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|messages
operator|.
name|add
argument_list|(
literal|"bootstrap checks failed"
argument_list|)
expr_stmt|;
name|messages
operator|.
name|addAll
argument_list|(
name|errors
argument_list|)
expr_stmt|;
if|if
condition|(
name|enforceLimits
condition|)
block|{
specifier|final
name|RuntimeException
name|re
init|=
operator|new
name|RuntimeException
argument_list|(
name|String
operator|.
name|join
argument_list|(
literal|"\n"
argument_list|,
name|messages
argument_list|)
argument_list|)
decl_stmt|;
name|errors
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|IllegalStateException
operator|::
operator|new
argument_list|)
operator|.
name|forEach
argument_list|(
name|re
operator|::
name|addSuppressed
argument_list|)
expr_stmt|;
throw|throw
name|re
throw|;
block|}
else|else
block|{
name|messages
operator|.
name|forEach
argument_list|(
name|message
lambda|->
name|logger
operator|.
name|warn
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Tests if the checks should be enforced      *      * @param boundTransportAddress the node network bindings      * @return true if the checks should be enforced      */
comment|// visible for testing
DECL|method|enforceLimits
specifier|static
name|boolean
name|enforceLimits
parameter_list|(
name|BoundTransportAddress
name|boundTransportAddress
parameter_list|)
block|{
return|return
operator|!
operator|(
name|Arrays
operator|.
name|stream
argument_list|(
name|boundTransportAddress
operator|.
name|boundAddresses
argument_list|()
argument_list|)
operator|.
name|allMatch
argument_list|(
name|TransportAddress
operator|::
name|isLoopbackOrLinkLocalAddress
argument_list|)
operator|&&
name|boundTransportAddress
operator|.
name|publishAddress
argument_list|()
operator|.
name|isLoopbackOrLinkLocalAddress
argument_list|()
operator|)
return|;
block|}
comment|// the list of checks to execute
DECL|method|checks
specifier|static
name|List
argument_list|<
name|Check
argument_list|>
name|checks
parameter_list|(
specifier|final
name|Settings
name|settings
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Check
argument_list|>
name|checks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|checks
operator|.
name|add
argument_list|(
operator|new
name|HeapSizeCheck
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FileDescriptorCheck
name|fileDescriptorCheck
init|=
name|Constants
operator|.
name|MAC_OS_X
condition|?
operator|new
name|OsXFileDescriptorCheck
argument_list|()
else|:
operator|new
name|FileDescriptorCheck
argument_list|()
decl_stmt|;
name|checks
operator|.
name|add
argument_list|(
name|fileDescriptorCheck
argument_list|)
expr_stmt|;
name|checks
operator|.
name|add
argument_list|(
operator|new
name|MlockallCheck
argument_list|(
name|BootstrapSettings
operator|.
name|MLOCKALL_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|Constants
operator|.
name|LINUX
condition|)
block|{
name|checks
operator|.
name|add
argument_list|(
operator|new
name|MaxNumberOfThreadsCheck
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Constants
operator|.
name|LINUX
operator|||
name|Constants
operator|.
name|MAC_OS_X
condition|)
block|{
name|checks
operator|.
name|add
argument_list|(
operator|new
name|MaxSizeVirtualMemoryCheck
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|checks
operator|.
name|add
argument_list|(
operator|new
name|MinMasterNodesCheck
argument_list|(
name|ElectMasterService
operator|.
name|DISCOVERY_ZEN_MINIMUM_MASTER_NODES_SETTING
operator|.
name|exists
argument_list|(
name|settings
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|checks
argument_list|)
return|;
block|}
comment|/**      * Encapsulates a limit check      */
DECL|interface|Check
interface|interface
name|Check
block|{
comment|/**          * test if the node fails the check          *          * @return true if the node failed the check          */
DECL|method|check
name|boolean
name|check
parameter_list|()
function_decl|;
comment|/**          * the message for a failed check          *          * @return the error message on check failure          */
DECL|method|errorMessage
name|String
name|errorMessage
parameter_list|()
function_decl|;
block|}
DECL|class|HeapSizeCheck
specifier|static
class|class
name|HeapSizeCheck
implements|implements
name|BootstrapCheck
operator|.
name|Check
block|{
annotation|@
name|Override
DECL|method|check
specifier|public
name|boolean
name|check
parameter_list|()
block|{
specifier|final
name|long
name|initialHeapSize
init|=
name|getInitialHeapSize
argument_list|()
decl_stmt|;
specifier|final
name|long
name|maxHeapSize
init|=
name|getMaxHeapSize
argument_list|()
decl_stmt|;
return|return
name|initialHeapSize
operator|!=
literal|0
operator|&&
name|maxHeapSize
operator|!=
literal|0
operator|&&
name|initialHeapSize
operator|!=
name|maxHeapSize
return|;
block|}
annotation|@
name|Override
DECL|method|errorMessage
specifier|public
name|String
name|errorMessage
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"initial heap size [%d] not equal to maximum heap size [%d]; "
operator|+
literal|"this can cause resize pauses and prevents mlockall from locking the entire heap"
argument_list|,
name|getInitialHeapSize
argument_list|()
argument_list|,
name|getMaxHeapSize
argument_list|()
argument_list|)
return|;
block|}
comment|// visible for testing
DECL|method|getInitialHeapSize
name|long
name|getInitialHeapSize
parameter_list|()
block|{
return|return
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|getConfiguredInitialHeapSize
argument_list|()
return|;
block|}
comment|// visible for testing
DECL|method|getMaxHeapSize
name|long
name|getMaxHeapSize
parameter_list|()
block|{
return|return
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|getConfiguredMaxHeapSize
argument_list|()
return|;
block|}
block|}
DECL|class|OsXFileDescriptorCheck
specifier|static
class|class
name|OsXFileDescriptorCheck
extends|extends
name|FileDescriptorCheck
block|{
DECL|method|OsXFileDescriptorCheck
specifier|public
name|OsXFileDescriptorCheck
parameter_list|()
block|{
comment|// see constant OPEN_MAX defined in
comment|// /usr/include/sys/syslimits.h on OS X and its use in JVM
comment|// initialization in int os:init_2(void) defined in the JVM
comment|// code for BSD (contains OS X)
name|super
argument_list|(
literal|10240
argument_list|)
expr_stmt|;
block|}
block|}
comment|// visible for testing
DECL|class|FileDescriptorCheck
specifier|static
class|class
name|FileDescriptorCheck
implements|implements
name|Check
block|{
DECL|field|limit
specifier|private
specifier|final
name|int
name|limit
decl_stmt|;
DECL|method|FileDescriptorCheck
name|FileDescriptorCheck
parameter_list|()
block|{
name|this
argument_list|(
literal|1
operator|<<
literal|16
argument_list|)
expr_stmt|;
block|}
DECL|method|FileDescriptorCheck
specifier|protected
name|FileDescriptorCheck
parameter_list|(
specifier|final
name|int
name|limit
parameter_list|)
block|{
if|if
condition|(
name|limit
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"limit must be positive but was ["
operator|+
name|limit
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
DECL|method|check
specifier|public
specifier|final
name|boolean
name|check
parameter_list|()
block|{
specifier|final
name|long
name|maxFileDescriptorCount
init|=
name|getMaxFileDescriptorCount
argument_list|()
decl_stmt|;
return|return
name|maxFileDescriptorCount
operator|!=
operator|-
literal|1
operator|&&
name|maxFileDescriptorCount
operator|<
name|limit
return|;
block|}
annotation|@
name|Override
DECL|method|errorMessage
specifier|public
specifier|final
name|String
name|errorMessage
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"max file descriptors [%d] for elasticsearch process likely too low, increase to at least [%d]"
argument_list|,
name|getMaxFileDescriptorCount
argument_list|()
argument_list|,
name|limit
argument_list|)
return|;
block|}
comment|// visible for testing
DECL|method|getMaxFileDescriptorCount
name|long
name|getMaxFileDescriptorCount
parameter_list|()
block|{
return|return
name|ProcessProbe
operator|.
name|getInstance
argument_list|()
operator|.
name|getMaxFileDescriptorCount
argument_list|()
return|;
block|}
block|}
comment|// visible for testing
DECL|class|MlockallCheck
specifier|static
class|class
name|MlockallCheck
implements|implements
name|Check
block|{
DECL|field|mlockallSet
specifier|private
specifier|final
name|boolean
name|mlockallSet
decl_stmt|;
DECL|method|MlockallCheck
specifier|public
name|MlockallCheck
parameter_list|(
specifier|final
name|boolean
name|mlockAllSet
parameter_list|)
block|{
name|this
operator|.
name|mlockallSet
operator|=
name|mlockAllSet
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|check
specifier|public
name|boolean
name|check
parameter_list|()
block|{
return|return
name|mlockallSet
operator|&&
operator|!
name|isMemoryLocked
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|errorMessage
specifier|public
name|String
name|errorMessage
parameter_list|()
block|{
return|return
literal|"memory locking requested for elasticsearch process but memory is not locked"
return|;
block|}
comment|// visible for testing
DECL|method|isMemoryLocked
name|boolean
name|isMemoryLocked
parameter_list|()
block|{
return|return
name|Natives
operator|.
name|isMemoryLocked
argument_list|()
return|;
block|}
block|}
DECL|class|MinMasterNodesCheck
specifier|static
class|class
name|MinMasterNodesCheck
implements|implements
name|Check
block|{
DECL|field|minMasterNodesIsSet
specifier|final
name|boolean
name|minMasterNodesIsSet
decl_stmt|;
DECL|method|MinMasterNodesCheck
name|MinMasterNodesCheck
parameter_list|(
name|boolean
name|minMasterNodesIsSet
parameter_list|)
block|{
name|this
operator|.
name|minMasterNodesIsSet
operator|=
name|minMasterNodesIsSet
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|check
specifier|public
name|boolean
name|check
parameter_list|()
block|{
return|return
name|minMasterNodesIsSet
operator|==
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|errorMessage
specifier|public
name|String
name|errorMessage
parameter_list|()
block|{
return|return
literal|"please set ["
operator|+
name|ElectMasterService
operator|.
name|DISCOVERY_ZEN_MINIMUM_MASTER_NODES_SETTING
operator|.
name|getKey
argument_list|()
operator|+
literal|"] to a majority of the number of master eligible nodes in your cluster."
return|;
block|}
block|}
DECL|class|MaxNumberOfThreadsCheck
specifier|static
class|class
name|MaxNumberOfThreadsCheck
implements|implements
name|Check
block|{
DECL|field|maxNumberOfThreadsThreshold
specifier|private
specifier|final
name|long
name|maxNumberOfThreadsThreshold
init|=
literal|1
operator|<<
literal|11
decl_stmt|;
annotation|@
name|Override
DECL|method|check
specifier|public
name|boolean
name|check
parameter_list|()
block|{
return|return
name|getMaxNumberOfThreads
argument_list|()
operator|!=
operator|-
literal|1
operator|&&
name|getMaxNumberOfThreads
argument_list|()
operator|<
name|maxNumberOfThreadsThreshold
return|;
block|}
annotation|@
name|Override
DECL|method|errorMessage
specifier|public
name|String
name|errorMessage
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"max number of threads [%d] for user [%s] likely too low, increase to at least [%d]"
argument_list|,
name|getMaxNumberOfThreads
argument_list|()
argument_list|,
name|BootstrapInfo
operator|.
name|getSystemProperties
argument_list|()
operator|.
name|get
argument_list|(
literal|"user.name"
argument_list|)
argument_list|,
name|maxNumberOfThreadsThreshold
argument_list|)
return|;
block|}
comment|// visible for testing
DECL|method|getMaxNumberOfThreads
name|long
name|getMaxNumberOfThreads
parameter_list|()
block|{
return|return
name|JNANatives
operator|.
name|MAX_NUMBER_OF_THREADS
return|;
block|}
block|}
DECL|class|MaxSizeVirtualMemoryCheck
specifier|static
class|class
name|MaxSizeVirtualMemoryCheck
implements|implements
name|Check
block|{
annotation|@
name|Override
DECL|method|check
specifier|public
name|boolean
name|check
parameter_list|()
block|{
return|return
name|getMaxSizeVirtualMemory
argument_list|()
operator|!=
name|Long
operator|.
name|MIN_VALUE
operator|&&
name|getMaxSizeVirtualMemory
argument_list|()
operator|!=
name|getRlimInfinity
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|errorMessage
specifier|public
name|String
name|errorMessage
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"max size virtual memory [%d] for user [%s] likely too low, increase to [unlimited]"
argument_list|,
name|getMaxSizeVirtualMemory
argument_list|()
argument_list|,
name|BootstrapInfo
operator|.
name|getSystemProperties
argument_list|()
operator|.
name|get
argument_list|(
literal|"user.name"
argument_list|)
argument_list|)
return|;
block|}
comment|// visible for testing
DECL|method|getRlimInfinity
name|long
name|getRlimInfinity
parameter_list|()
block|{
return|return
name|JNACLibrary
operator|.
name|RLIM_INFINITY
return|;
block|}
comment|// visible for testing
DECL|method|getMaxSizeVirtualMemory
name|long
name|getMaxSizeVirtualMemory
parameter_list|()
block|{
return|return
name|JNANatives
operator|.
name|MAX_SIZE_VIRTUAL_MEMORY
return|;
block|}
block|}
block|}
end_class

end_unit

