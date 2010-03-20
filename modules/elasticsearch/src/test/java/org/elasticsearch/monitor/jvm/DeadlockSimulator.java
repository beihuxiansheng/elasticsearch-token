begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|monitor
operator|.
name|dump
operator|.
name|DumpMonitorService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|scaling
operator|.
name|ScalingThreadPool
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy  */
end_comment

begin_class
DECL|class|DeadlockSimulator
specifier|public
class|class
name|DeadlockSimulator
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|ThreadPool
name|threadPool
init|=
operator|new
name|ScalingThreadPool
argument_list|()
decl_stmt|;
name|DumpMonitorService
name|dumpMonitorService
init|=
operator|new
name|DumpMonitorService
argument_list|()
decl_stmt|;
name|JvmMonitorService
name|jvmMonitorService
init|=
operator|new
name|JvmMonitorService
argument_list|(
name|EMPTY_SETTINGS
argument_list|,
name|threadPool
argument_list|,
name|dumpMonitorService
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
comment|//These are the two resource objects
comment|//we'll try to get locks for
specifier|final
name|Object
name|resource1
init|=
literal|"resource1"
decl_stmt|;
specifier|final
name|Object
name|resource2
init|=
literal|"resource2"
decl_stmt|;
comment|//Here's the first thread.
comment|//It tries to lock resource1 then resource2
name|Thread
name|t1
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|//Lock resource 1
synchronized|synchronized
init|(
name|resource1
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Thread 1: locked resource 1"
argument_list|)
expr_stmt|;
comment|//Pause for a bit, simulating some file I/O or
comment|//something. Basically, we just want to give the
comment|//other thread a chance to run. Threads and deadlock
comment|//are asynchronous things, but we're trying to force
comment|//deadlock to happen here...
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                     }
comment|//Now wait 'till we can get a lock on resource 2
synchronized|synchronized
init|(
name|resource2
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Thread 1: locked resource 2"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
comment|//Here's the second thread.
comment|//It tries to lock resource2 then resource1
name|Thread
name|t2
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|//This thread locks resource 2 right away
synchronized|synchronized
init|(
name|resource2
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Thread 2: locked resource 2"
argument_list|)
expr_stmt|;
comment|//Then it pauses, for the same reason as the first
comment|//thread does
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                     }
comment|//Then it tries to lock resource1.
comment|//But wait!  Thread 1 locked resource1, and
comment|//won't release it till it gets a lock on resource2.
comment|//This thread holds the lock on resource2, and won't
comment|//release it till it gets resource1.
comment|//We're at an impasse. Neither thread can run,
comment|//and the program freezes up.
synchronized|synchronized
init|(
name|resource1
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Thread 2: locked resource 1"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
comment|//Start the two threads.
comment|//If all goes as planned, deadlock will occur,
comment|//and the program will never exit.
name|t1
operator|.
name|start
argument_list|()
expr_stmt|;
name|t2
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

