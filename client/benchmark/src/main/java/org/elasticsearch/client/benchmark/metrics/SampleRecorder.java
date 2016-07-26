begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.benchmark.metrics
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|benchmark
operator|.
name|metrics
package|;
end_package

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

begin_comment
comment|/**  * Stores measurement samples.  *  * This class is NOT threadsafe.  */
end_comment

begin_class
DECL|class|SampleRecorder
specifier|public
specifier|final
class|class
name|SampleRecorder
block|{
DECL|field|warmupIterations
specifier|private
specifier|final
name|int
name|warmupIterations
decl_stmt|;
DECL|field|samples
specifier|private
specifier|final
name|List
argument_list|<
name|Sample
argument_list|>
name|samples
decl_stmt|;
DECL|field|currentIteration
specifier|private
name|int
name|currentIteration
decl_stmt|;
DECL|method|SampleRecorder
specifier|public
name|SampleRecorder
parameter_list|(
name|int
name|warmupIterations
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|this
operator|.
name|warmupIterations
operator|=
name|warmupIterations
expr_stmt|;
name|this
operator|.
name|samples
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|iterations
argument_list|)
expr_stmt|;
block|}
DECL|method|addSample
specifier|public
name|void
name|addSample
parameter_list|(
name|Sample
name|sample
parameter_list|)
block|{
name|currentIteration
operator|++
expr_stmt|;
comment|// only add samples after warmup
if|if
condition|(
name|currentIteration
operator|>
name|warmupIterations
condition|)
block|{
name|samples
operator|.
name|add
argument_list|(
name|sample
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getSamples
specifier|public
name|List
argument_list|<
name|Sample
argument_list|>
name|getSamples
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|samples
argument_list|)
return|;
block|}
block|}
end_class

end_unit

