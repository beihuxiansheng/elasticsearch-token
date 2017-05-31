begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.profile
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|profile
package|;
end_package

begin_comment
comment|/** Helps measure how much time is spent running some methods.  *  The {@link #start()} and {@link #stop()} methods should typically be called  *  in a try/finally clause with {@link #start()} being called right before the  *  try block and {@link #stop()} being called at the beginning of the finally  *  block:  *<pre>  *  timer.start();  *  try {  *    // code to time  *  } finally {  *    timer.stop();  *  }  *</pre>  */
end_comment

begin_comment
comment|// TODO: do not time every single call as discussed in https://github.com/elastic/elasticsearch/issues/24799
end_comment

begin_class
DECL|class|Timer
specifier|public
specifier|final
class|class
name|Timer
block|{
DECL|field|timing
DECL|field|count
DECL|field|start
specifier|private
name|long
name|timing
decl_stmt|,
name|count
decl_stmt|,
name|start
decl_stmt|;
comment|/** Start the timer. */
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
block|{
assert|assert
name|start
operator|==
literal|0
operator|:
literal|"#start call misses a matching #stop call"
assert|;
name|count
operator|++
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
comment|/** Stop the timer. */
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|timing
operator|+=
name|Math
operator|.
name|max
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|start
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Return the number of times that {@link #start()} has been called. */
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
if|if
condition|(
name|start
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"#start call misses a matching #stop call"
argument_list|)
throw|;
block|}
return|return
name|count
return|;
block|}
comment|/** Return an approximation of the total time spend between consecutive calls of #start and #stop. */
DECL|method|getTiming
specifier|public
name|long
name|getTiming
parameter_list|()
block|{
if|if
condition|(
name|start
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"#start call misses a matching #stop call"
argument_list|)
throw|;
block|}
return|return
name|timing
return|;
block|}
block|}
end_class

end_unit

