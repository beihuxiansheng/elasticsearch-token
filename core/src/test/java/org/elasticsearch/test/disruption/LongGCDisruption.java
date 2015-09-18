begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.disruption
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|disruption
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
name|SuppressForbidden
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
name|test
operator|.
name|InternalTestCluster
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
name|Random
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
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Suspends all threads on the specified node in order to simulate a long gc.  */
end_comment

begin_class
DECL|class|LongGCDisruption
specifier|public
class|class
name|LongGCDisruption
extends|extends
name|SingleNodeDisruption
block|{
DECL|field|unsafeClasses
specifier|private
specifier|final
specifier|static
name|Pattern
index|[]
name|unsafeClasses
init|=
operator|new
name|Pattern
index|[]
block|{
comment|// logging has shared JVM locks - we may suspend a thread and block other nodes from doing their thing
name|Pattern
operator|.
name|compile
argument_list|(
literal|"Logger"
argument_list|)
block|}
decl_stmt|;
DECL|field|disruptedNode
specifier|protected
specifier|final
name|String
name|disruptedNode
decl_stmt|;
DECL|field|suspendedThreads
specifier|private
name|Set
argument_list|<
name|Thread
argument_list|>
name|suspendedThreads
decl_stmt|;
DECL|method|LongGCDisruption
specifier|public
name|LongGCDisruption
parameter_list|(
name|Random
name|random
parameter_list|,
name|String
name|disruptedNode
parameter_list|)
block|{
name|super
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|this
operator|.
name|disruptedNode
operator|=
name|disruptedNode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDisrupting
specifier|public
specifier|synchronized
name|void
name|startDisrupting
parameter_list|()
block|{
if|if
condition|(
name|suspendedThreads
operator|==
literal|null
condition|)
block|{
name|suspendedThreads
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|stopNodeThreads
argument_list|(
name|disruptedNode
argument_list|,
name|suspendedThreads
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"can't disrupt twice, call stopDisrupting() first"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|stopDisrupting
specifier|public
specifier|synchronized
name|void
name|stopDisrupting
parameter_list|()
block|{
if|if
condition|(
name|suspendedThreads
operator|!=
literal|null
condition|)
block|{
name|resumeThreads
argument_list|(
name|suspendedThreads
argument_list|)
expr_stmt|;
name|suspendedThreads
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|removeAndEnsureHealthy
specifier|public
name|void
name|removeAndEnsureHealthy
parameter_list|(
name|InternalTestCluster
name|cluster
parameter_list|)
block|{
name|removeFromCluster
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
name|ensureNodeCount
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|expectedTimeToHeal
specifier|public
name|TimeValue
name|expectedTimeToHeal
parameter_list|()
block|{
return|return
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"stops/resumes threads intentionally"
argument_list|)
DECL|method|stopNodeThreads
specifier|protected
name|boolean
name|stopNodeThreads
parameter_list|(
name|String
name|node
parameter_list|,
name|Set
argument_list|<
name|Thread
argument_list|>
name|nodeThreads
parameter_list|)
block|{
name|Thread
index|[]
name|allThreads
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|allThreads
operator|==
literal|null
condition|)
block|{
name|allThreads
operator|=
operator|new
name|Thread
index|[
name|Thread
operator|.
name|activeCount
argument_list|()
index|]
expr_stmt|;
if|if
condition|(
name|Thread
operator|.
name|enumerate
argument_list|(
name|allThreads
argument_list|)
operator|>
name|allThreads
operator|.
name|length
condition|)
block|{
comment|// we didn't make enough space, retry
name|allThreads
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|boolean
name|stopped
init|=
literal|false
decl_stmt|;
specifier|final
name|String
name|nodeThreadNamePart
init|=
literal|"["
operator|+
name|node
operator|+
literal|"]"
decl_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|allThreads
control|)
block|{
if|if
condition|(
name|thread
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|String
name|name
init|=
name|thread
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
name|nodeThreadNamePart
argument_list|)
condition|)
block|{
if|if
condition|(
name|thread
operator|.
name|isAlive
argument_list|()
operator|&&
name|nodeThreads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
condition|)
block|{
name|stopped
operator|=
literal|true
expr_stmt|;
name|thread
operator|.
name|suspend
argument_list|()
expr_stmt|;
comment|// double check the thread is not in a shared resource like logging. If so, let it go and come back..
name|boolean
name|safe
init|=
literal|true
decl_stmt|;
name|safe
label|:
for|for
control|(
name|StackTraceElement
name|stackElement
range|:
name|thread
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
name|String
name|className
init|=
name|stackElement
operator|.
name|getClassName
argument_list|()
decl_stmt|;
for|for
control|(
name|Pattern
name|unsafePattern
range|:
name|unsafeClasses
control|)
block|{
if|if
condition|(
name|unsafePattern
operator|.
name|matcher
argument_list|(
name|className
argument_list|)
operator|.
name|find
argument_list|()
condition|)
block|{
name|safe
operator|=
literal|false
expr_stmt|;
break|break
name|safe
break|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|safe
condition|)
block|{
name|thread
operator|.
name|resume
argument_list|()
expr_stmt|;
name|nodeThreads
operator|.
name|remove
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|stopped
return|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"stops/resumes threads intentionally"
argument_list|)
DECL|method|resumeThreads
specifier|protected
name|void
name|resumeThreads
parameter_list|(
name|Set
argument_list|<
name|Thread
argument_list|>
name|threads
parameter_list|)
block|{
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|resume
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

