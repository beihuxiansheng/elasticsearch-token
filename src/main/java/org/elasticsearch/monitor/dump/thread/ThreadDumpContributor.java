begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.dump.thread
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|dump
operator|.
name|thread
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
name|inject
operator|.
name|Inject
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
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
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
name|monitor
operator|.
name|dump
operator|.
name|Dump
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
name|dump
operator|.
name|DumpContributionFailedException
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
name|dump
operator|.
name|DumpContributor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|MonitorInfo
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
name|ThreadInfo
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
name|ThreadMXBean
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ThreadDumpContributor
specifier|public
class|class
name|ThreadDumpContributor
implements|implements
name|DumpContributor
block|{
DECL|field|threadBean
specifier|private
specifier|static
specifier|final
name|ThreadMXBean
name|threadBean
init|=
name|ManagementFactory
operator|.
name|getThreadMXBean
argument_list|()
decl_stmt|;
DECL|field|THREAD_DUMP
specifier|public
specifier|static
specifier|final
name|String
name|THREAD_DUMP
init|=
literal|"thread"
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
annotation|@
name|Inject
DECL|method|ThreadDumpContributor
specifier|public
name|ThreadDumpContributor
parameter_list|(
annotation|@
name|Assisted
name|String
name|name
parameter_list|,
annotation|@
name|Assisted
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|contribute
specifier|public
name|void
name|contribute
parameter_list|(
name|Dump
name|dump
parameter_list|)
throws|throws
name|DumpContributionFailedException
block|{
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|dump
operator|.
name|createFileWriter
argument_list|(
literal|"threads.txt"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|processDeadlocks
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|processAllThreads
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DumpContributionFailedException
argument_list|(
name|getName
argument_list|()
argument_list|,
literal|"Failed to generate"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
DECL|method|processDeadlocks
specifier|private
name|void
name|processDeadlocks
parameter_list|(
name|PrintWriter
name|dump
parameter_list|)
block|{
name|dump
operator|.
name|println
argument_list|(
literal|"=====  Deadlocked Threads ====="
argument_list|)
expr_stmt|;
name|long
name|deadlockedThreadIds
index|[]
init|=
name|findDeadlockedThreads
argument_list|()
decl_stmt|;
if|if
condition|(
name|deadlockedThreadIds
operator|!=
literal|null
condition|)
name|dumpThreads
argument_list|(
name|dump
argument_list|,
name|getThreadInfo
argument_list|(
name|deadlockedThreadIds
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|processAllThreads
specifier|private
name|void
name|processAllThreads
parameter_list|(
name|PrintWriter
name|dump
parameter_list|)
block|{
name|dump
operator|.
name|println
argument_list|()
expr_stmt|;
name|dump
operator|.
name|println
argument_list|(
literal|"===== All Threads ====="
argument_list|)
expr_stmt|;
name|dumpThreads
argument_list|(
name|dump
argument_list|,
name|dumpAllThreads
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|dumpThreads
specifier|private
name|void
name|dumpThreads
parameter_list|(
name|PrintWriter
name|dump
parameter_list|,
name|ThreadInfo
name|infos
index|[]
parameter_list|)
block|{
for|for
control|(
name|ThreadInfo
name|info
range|:
name|infos
control|)
block|{
name|dump
operator|.
name|println
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|info
argument_list|,
name|dump
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|dumpAllThreads
specifier|private
name|ThreadInfo
index|[]
name|dumpAllThreads
parameter_list|()
block|{
return|return
name|threadBean
operator|.
name|dumpAllThreads
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|findDeadlockedThreads
specifier|public
name|long
index|[]
name|findDeadlockedThreads
parameter_list|()
block|{
return|return
name|threadBean
operator|.
name|findDeadlockedThreads
argument_list|()
return|;
block|}
DECL|method|getThreadInfo
specifier|public
name|ThreadInfo
index|[]
name|getThreadInfo
parameter_list|(
name|long
index|[]
name|threadIds
parameter_list|)
block|{
return|return
name|threadBean
operator|.
name|getThreadInfo
argument_list|(
name|threadIds
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|write
specifier|private
name|void
name|write
parameter_list|(
name|ThreadInfo
name|threadInfo
parameter_list|,
name|PrintWriter
name|writer
parameter_list|)
block|{
name|writer
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"\"%s\" Id=%s %s"
argument_list|,
name|threadInfo
operator|.
name|getThreadName
argument_list|()
argument_list|,
name|threadInfo
operator|.
name|getThreadId
argument_list|()
argument_list|,
name|threadInfo
operator|.
name|getThreadState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|threadInfo
operator|.
name|getLockName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|" on %s"
argument_list|,
name|threadInfo
operator|.
name|getLockName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|threadInfo
operator|.
name|getLockOwnerName
argument_list|()
operator|!=
literal|null
condition|)
name|writer
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|" owned by \"%s\" Id=%s"
argument_list|,
name|threadInfo
operator|.
name|getLockOwnerName
argument_list|()
argument_list|,
name|threadInfo
operator|.
name|getLockOwnerId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|threadInfo
operator|.
name|isInNative
argument_list|()
condition|)
name|writer
operator|.
name|println
argument_list|(
literal|" (in native)"
argument_list|)
expr_stmt|;
else|else
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|MonitorInfo
index|[]
name|lockedMonitors
init|=
name|threadInfo
operator|.
name|getLockedMonitors
argument_list|()
decl_stmt|;
name|StackTraceElement
name|stackTraceElements
index|[]
init|=
name|threadInfo
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
for|for
control|(
name|StackTraceElement
name|stackTraceElement
range|:
name|stackTraceElements
control|)
block|{
name|writer
operator|.
name|println
argument_list|(
literal|"    at "
operator|+
name|stackTraceElement
argument_list|)
expr_stmt|;
name|MonitorInfo
name|lockedMonitor
init|=
name|findLockedMonitor
argument_list|(
name|stackTraceElement
argument_list|,
name|lockedMonitors
argument_list|)
decl_stmt|;
if|if
condition|(
name|lockedMonitor
operator|!=
literal|null
condition|)
name|writer
operator|.
name|println
argument_list|(
operator|(
literal|"    - locked "
operator|+
name|lockedMonitor
operator|.
name|getClassName
argument_list|()
operator|+
literal|"@"
operator|+
name|lockedMonitor
operator|.
name|getIdentityHashCode
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|findLockedMonitor
specifier|private
specifier|static
name|MonitorInfo
name|findLockedMonitor
parameter_list|(
name|StackTraceElement
name|stackTraceElement
parameter_list|,
name|MonitorInfo
name|lockedMonitors
index|[]
parameter_list|)
block|{
for|for
control|(
name|MonitorInfo
name|monitorInfo
range|:
name|lockedMonitors
control|)
block|{
if|if
condition|(
name|stackTraceElement
operator|.
name|equals
argument_list|(
name|monitorInfo
operator|.
name|getLockedStackFrame
argument_list|()
argument_list|)
condition|)
return|return
name|monitorInfo
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

