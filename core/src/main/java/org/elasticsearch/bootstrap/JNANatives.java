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
name|com
operator|.
name|sun
operator|.
name|jna
operator|.
name|Native
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jna
operator|.
name|Pointer
import|;
end_import

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
name|monitor
operator|.
name|jvm
operator|.
name|JvmInfo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
operator|.
name|JNAKernel32Library
operator|.
name|SizeT
import|;
end_import

begin_comment
comment|/**  * This class performs the actual work with JNA and library bindings to call native methods. It should only be used after  * we are sure that the JNA classes are available to the JVM  */
end_comment

begin_class
DECL|class|JNANatives
class|class
name|JNANatives
block|{
comment|/** no instantiation */
DECL|method|JNANatives
specifier|private
name|JNANatives
parameter_list|()
block|{}
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|JNANatives
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Set to true, in case native mlockall call was successful
DECL|field|LOCAL_MLOCKALL
specifier|static
name|boolean
name|LOCAL_MLOCKALL
init|=
literal|false
decl_stmt|;
DECL|method|tryMlockall
specifier|static
name|void
name|tryMlockall
parameter_list|()
block|{
name|int
name|errno
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
name|String
name|errMsg
init|=
literal|null
decl_stmt|;
name|boolean
name|rlimitSuccess
init|=
literal|false
decl_stmt|;
name|long
name|softLimit
init|=
literal|0
decl_stmt|;
name|long
name|hardLimit
init|=
literal|0
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|JNACLibrary
operator|.
name|mlockall
argument_list|(
name|JNACLibrary
operator|.
name|MCL_CURRENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
name|LOCAL_MLOCKALL
operator|=
literal|true
expr_stmt|;
return|return;
block|}
name|errno
operator|=
name|Native
operator|.
name|getLastError
argument_list|()
expr_stmt|;
name|errMsg
operator|=
name|JNACLibrary
operator|.
name|strerror
argument_list|(
name|errno
argument_list|)
expr_stmt|;
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
comment|// we only know RLIMIT_MEMLOCK for these two at the moment.
name|JNACLibrary
operator|.
name|Rlimit
name|rlimit
init|=
operator|new
name|JNACLibrary
operator|.
name|Rlimit
argument_list|()
decl_stmt|;
if|if
condition|(
name|JNACLibrary
operator|.
name|getrlimit
argument_list|(
name|JNACLibrary
operator|.
name|RLIMIT_MEMLOCK
argument_list|,
name|rlimit
argument_list|)
operator|==
literal|0
condition|)
block|{
name|rlimitSuccess
operator|=
literal|true
expr_stmt|;
name|softLimit
operator|=
name|rlimit
operator|.
name|rlim_cur
expr_stmt|;
name|hardLimit
operator|=
name|rlimit
operator|.
name|rlim_max
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Unable to retrieve resource limits: "
operator|+
name|JNACLibrary
operator|.
name|strerror
argument_list|(
name|Native
operator|.
name|getLastError
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|UnsatisfiedLinkError
name|e
parameter_list|)
block|{
comment|// this will have already been logged by CLibrary, no need to repeat it
return|return;
block|}
comment|// mlockall failed for some reason
name|logger
operator|.
name|warn
argument_list|(
literal|"Unable to lock JVM Memory: error="
operator|+
name|errno
operator|+
literal|",reason="
operator|+
name|errMsg
operator|+
literal|". This can result in part of the JVM being swapped out."
argument_list|)
expr_stmt|;
if|if
condition|(
name|errno
operator|==
name|JNACLibrary
operator|.
name|ENOMEM
condition|)
block|{
if|if
condition|(
name|rlimitSuccess
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Increase RLIMIT_MEMLOCK, soft limit: "
operator|+
name|rlimitToString
argument_list|(
name|softLimit
argument_list|)
operator|+
literal|", hard limit: "
operator|+
name|rlimitToString
argument_list|(
name|hardLimit
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
comment|// give specific instructions for the linux case to make it easy
name|logger
operator|.
name|warn
argument_list|(
literal|"These can be adjusted by modifying /etc/security/limits.conf, for example: \n"
operator|+
literal|"\t# allow user 'esuser' mlockall\n"
operator|+
literal|"\tesuser soft memlock unlimited\n"
operator|+
literal|"\tesuser hard memlock unlimited"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"If you are logged in interactively, you will have to re-login for the new limits to take effect."
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Increase RLIMIT_MEMLOCK (ulimit)."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|rlimitToString
specifier|static
name|String
name|rlimitToString
parameter_list|(
name|long
name|value
parameter_list|)
block|{
assert|assert
name|Constants
operator|.
name|LINUX
operator|||
name|Constants
operator|.
name|MAC_OS_X
assert|;
if|if
condition|(
name|value
operator|==
name|JNACLibrary
operator|.
name|RLIM_INFINITY
condition|)
block|{
return|return
literal|"unlimited"
return|;
block|}
else|else
block|{
comment|// TODO, on java 8 use Long.toUnsignedString, since thats what it is.
return|return
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
comment|/** Returns true if user is root, false if not, or if we don't know */
DECL|method|definitelyRunningAsRoot
specifier|static
name|boolean
name|definitelyRunningAsRoot
parameter_list|()
block|{
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
return|return
literal|false
return|;
comment|// don't know
block|}
try|try
block|{
return|return
name|JNACLibrary
operator|.
name|geteuid
argument_list|()
operator|==
literal|0
return|;
block|}
catch|catch
parameter_list|(
name|UnsatisfiedLinkError
name|e
parameter_list|)
block|{
comment|// this will have already been logged by Kernel32Library, no need to repeat it
return|return
literal|false
return|;
block|}
block|}
DECL|method|tryVirtualLock
specifier|static
name|void
name|tryVirtualLock
parameter_list|()
block|{
name|JNAKernel32Library
name|kernel
init|=
name|JNAKernel32Library
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Pointer
name|process
init|=
literal|null
decl_stmt|;
try|try
block|{
name|process
operator|=
name|kernel
operator|.
name|GetCurrentProcess
argument_list|()
expr_stmt|;
comment|// By default, Windows limits the number of pages that can be locked.
comment|// Thus, we need to first increase the working set size of the JVM by
comment|// the amount of memory we wish to lock, plus a small overhead (1MB).
name|SizeT
name|size
init|=
operator|new
name|SizeT
argument_list|(
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|getMem
argument_list|()
operator|.
name|getHeapInit
argument_list|()
operator|.
name|getBytes
argument_list|()
operator|+
operator|(
literal|1024
operator|*
literal|1024
operator|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|kernel
operator|.
name|SetProcessWorkingSetSize
argument_list|(
name|process
argument_list|,
name|size
argument_list|,
name|size
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Unable to lock JVM memory. Failed to set working set size. Error code "
operator|+
name|Native
operator|.
name|getLastError
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|JNAKernel32Library
operator|.
name|MemoryBasicInformation
name|memInfo
init|=
operator|new
name|JNAKernel32Library
operator|.
name|MemoryBasicInformation
argument_list|()
decl_stmt|;
name|long
name|address
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|kernel
operator|.
name|VirtualQueryEx
argument_list|(
name|process
argument_list|,
operator|new
name|Pointer
argument_list|(
name|address
argument_list|)
argument_list|,
name|memInfo
argument_list|,
name|memInfo
operator|.
name|size
argument_list|()
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|boolean
name|lockable
init|=
name|memInfo
operator|.
name|State
operator|.
name|longValue
argument_list|()
operator|==
name|JNAKernel32Library
operator|.
name|MEM_COMMIT
operator|&&
operator|(
name|memInfo
operator|.
name|Protect
operator|.
name|longValue
argument_list|()
operator|&
name|JNAKernel32Library
operator|.
name|PAGE_NOACCESS
operator|)
operator|!=
name|JNAKernel32Library
operator|.
name|PAGE_NOACCESS
operator|&&
operator|(
name|memInfo
operator|.
name|Protect
operator|.
name|longValue
argument_list|()
operator|&
name|JNAKernel32Library
operator|.
name|PAGE_GUARD
operator|)
operator|!=
name|JNAKernel32Library
operator|.
name|PAGE_GUARD
decl_stmt|;
if|if
condition|(
name|lockable
condition|)
block|{
name|kernel
operator|.
name|VirtualLock
argument_list|(
name|memInfo
operator|.
name|BaseAddress
argument_list|,
operator|new
name|SizeT
argument_list|(
name|memInfo
operator|.
name|RegionSize
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Move to the next region
name|address
operator|+=
name|memInfo
operator|.
name|RegionSize
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
name|LOCAL_MLOCKALL
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsatisfiedLinkError
name|e
parameter_list|)
block|{
comment|// this will have already been logged by Kernel32Library, no need to repeat it
block|}
finally|finally
block|{
if|if
condition|(
name|process
operator|!=
literal|null
condition|)
block|{
name|kernel
operator|.
name|CloseHandle
argument_list|(
name|process
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addConsoleCtrlHandler
specifier|static
name|void
name|addConsoleCtrlHandler
parameter_list|(
name|ConsoleCtrlHandler
name|handler
parameter_list|)
block|{
comment|// The console Ctrl handler is necessary on Windows platforms only.
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
try|try
block|{
name|boolean
name|result
init|=
name|JNAKernel32Library
operator|.
name|getInstance
argument_list|()
operator|.
name|addConsoleCtrlHandler
argument_list|(
name|handler
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"console ctrl handler correctly set"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"unknown error "
operator|+
name|Native
operator|.
name|getLastError
argument_list|()
operator|+
literal|" when adding console ctrl handler:"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsatisfiedLinkError
name|e
parameter_list|)
block|{
comment|// this will have already been logged by Kernel32Library, no need to repeat it
block|}
block|}
block|}
block|}
end_class

end_unit

