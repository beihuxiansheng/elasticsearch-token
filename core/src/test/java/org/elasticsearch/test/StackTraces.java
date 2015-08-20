begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Formatter
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
name|Map
import|;
end_import

begin_class
DECL|class|StackTraces
specifier|public
class|class
name|StackTraces
block|{
comment|/** Dump threads and their current stack trace. */
DECL|method|formatThreadStacks
specifier|public
specifier|static
name|String
name|formatThreadStacks
parameter_list|(
name|Map
argument_list|<
name|Thread
argument_list|,
name|StackTraceElement
index|[]
argument_list|>
name|threads
parameter_list|)
block|{
name|StringBuilder
name|message
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|cnt
init|=
literal|1
decl_stmt|;
specifier|final
name|Formatter
name|f
init|=
operator|new
name|Formatter
argument_list|(
name|message
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Thread
argument_list|,
name|StackTraceElement
index|[]
argument_list|>
name|e
range|:
name|threads
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|f
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"\n  %2d) %s"
argument_list|,
name|cnt
operator|++
argument_list|,
name|threadName
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|message
operator|.
name|append
argument_list|(
literal|"\n        at (empty stack)"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|StackTraceElement
name|ste
range|:
name|e
operator|.
name|getValue
argument_list|()
control|)
block|{
name|message
operator|.
name|append
argument_list|(
literal|"\n        at "
argument_list|)
operator|.
name|append
argument_list|(
name|ste
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|message
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|groupName
specifier|private
specifier|static
name|String
name|groupName
parameter_list|(
name|ThreadGroup
name|threadGroup
parameter_list|)
block|{
if|if
condition|(
name|threadGroup
operator|==
literal|null
condition|)
block|{
return|return
literal|"{null group}"
return|;
block|}
else|else
block|{
return|return
name|threadGroup
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
DECL|method|threadName
specifier|private
specifier|static
name|String
name|threadName
parameter_list|(
name|Thread
name|t
parameter_list|)
block|{
return|return
literal|"Thread["
operator|+
literal|"id="
operator|+
name|t
operator|.
name|getId
argument_list|()
operator|+
literal|", name="
operator|+
name|t
operator|.
name|getName
argument_list|()
operator|+
literal|", state="
operator|+
name|t
operator|.
name|getState
argument_list|()
operator|+
literal|", group="
operator|+
name|groupName
argument_list|(
name|t
operator|.
name|getThreadGroup
argument_list|()
argument_list|)
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit
