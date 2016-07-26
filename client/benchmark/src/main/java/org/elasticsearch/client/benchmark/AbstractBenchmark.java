begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.benchmark
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|benchmark
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|benchmark
operator|.
name|ops
operator|.
name|bulk
operator|.
name|BulkBenchmarkTask
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|benchmark
operator|.
name|ops
operator|.
name|bulk
operator|.
name|BulkRequestExecutor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|benchmark
operator|.
name|ops
operator|.
name|search
operator|.
name|SearchBenchmarkTask
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|benchmark
operator|.
name|ops
operator|.
name|search
operator|.
name|SearchRequestExecutor
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
name|SuppressForbidden
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_class
DECL|class|AbstractBenchmark
specifier|public
specifier|abstract
class|class
name|AbstractBenchmark
parameter_list|<
name|T
extends|extends
name|Closeable
parameter_list|>
block|{
DECL|field|SEARCH_BENCHMARK_ITERATIONS
specifier|private
specifier|static
specifier|final
name|int
name|SEARCH_BENCHMARK_ITERATIONS
init|=
literal|10_000
decl_stmt|;
DECL|method|client
specifier|protected
specifier|abstract
name|T
name|client
parameter_list|(
name|String
name|benchmarkTargetHost
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|bulkRequestExecutor
specifier|protected
specifier|abstract
name|BulkRequestExecutor
name|bulkRequestExecutor
parameter_list|(
name|T
name|client
parameter_list|,
name|String
name|indexName
parameter_list|,
name|String
name|typeName
parameter_list|)
function_decl|;
DECL|method|searchRequestExecutor
specifier|protected
specifier|abstract
name|SearchRequestExecutor
name|searchRequestExecutor
parameter_list|(
name|T
name|client
parameter_list|,
name|String
name|indexName
parameter_list|)
function_decl|;
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"system out is ok for a command line tool"
argument_list|)
DECL|method|run
specifier|public
specifier|final
name|void
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|6
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"usage: benchmarkTargetHostIp indexFilePath indexName typeName numberOfDocuments bulkSize [search request body]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|benchmarkTargetHost
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|String
name|indexFilePath
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|String
name|indexName
init|=
name|args
index|[
literal|2
index|]
decl_stmt|;
name|String
name|typeName
init|=
name|args
index|[
literal|3
index|]
decl_stmt|;
name|int
name|totalDocs
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
decl_stmt|;
name|int
name|bulkSize
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|args
index|[
literal|5
index|]
argument_list|)
decl_stmt|;
name|int
name|totalIterationCount
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|totalDocs
operator|/
name|bulkSize
argument_list|)
decl_stmt|;
comment|// consider 40% of all iterations as warmup iterations
name|int
name|warmupIterations
init|=
call|(
name|int
call|)
argument_list|(
literal|0.4d
operator|*
name|totalIterationCount
argument_list|)
decl_stmt|;
name|int
name|iterations
init|=
name|totalIterationCount
operator|-
name|warmupIterations
decl_stmt|;
name|String
name|searchBody
init|=
operator|(
name|args
operator|.
name|length
operator|==
literal|7
operator|)
condition|?
name|args
index|[
literal|6
index|]
else|:
literal|null
decl_stmt|;
name|T
name|client
init|=
name|client
argument_list|(
name|benchmarkTargetHost
argument_list|)
decl_stmt|;
name|BenchmarkRunner
name|benchmark
init|=
operator|new
name|BenchmarkRunner
argument_list|(
name|warmupIterations
argument_list|,
name|iterations
argument_list|,
operator|new
name|BulkBenchmarkTask
argument_list|(
name|bulkRequestExecutor
argument_list|(
name|client
argument_list|,
name|indexName
argument_list|,
name|typeName
argument_list|)
argument_list|,
name|indexFilePath
argument_list|,
name|warmupIterations
operator|+
name|iterations
argument_list|,
name|bulkSize
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|benchmark
operator|.
name|run
argument_list|()
expr_stmt|;
if|if
condition|(
name|searchBody
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|run
init|=
literal|1
init|;
name|run
operator|<=
literal|5
condition|;
name|run
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"============="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Trial run "
operator|+
name|run
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"============="
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|throughput
init|=
literal|100
init|;
name|throughput
operator|<=
literal|100_000
condition|;
name|throughput
operator|*=
literal|10
control|)
block|{
comment|//request a GC between trials to reduce the likelihood of a GC occurring in the middle of a trial.
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|BenchmarkRunner
name|searchBenchmark
init|=
operator|new
name|BenchmarkRunner
argument_list|(
name|SEARCH_BENCHMARK_ITERATIONS
argument_list|,
name|SEARCH_BENCHMARK_ITERATIONS
argument_list|,
operator|new
name|SearchBenchmarkTask
argument_list|(
name|searchRequestExecutor
argument_list|(
name|client
argument_list|,
name|indexName
argument_list|)
argument_list|,
name|searchBody
argument_list|,
literal|2
operator|*
name|SEARCH_BENCHMARK_ITERATIONS
argument_list|,
name|throughput
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Target throughput = %d ops / s%n"
argument_list|,
name|throughput
argument_list|)
expr_stmt|;
name|searchBenchmark
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

