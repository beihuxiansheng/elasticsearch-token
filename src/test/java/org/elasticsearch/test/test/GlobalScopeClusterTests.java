begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|test
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Repeat
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
name|ElasticsearchIntegrationTest
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
name|TestCluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  * This test ensures that the cluster initializion for GLOBAL scope is not influencing  * the tests random sequence due to initializtion using the same random instance.  */
end_comment

begin_class
DECL|class|GlobalScopeClusterTests
specifier|public
class|class
name|GlobalScopeClusterTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|field|ITER
specifier|private
specifier|static
name|int
name|ITER
init|=
literal|0
decl_stmt|;
DECL|field|SEQUENCE
specifier|private
specifier|static
name|long
index|[]
name|SEQUENCE
init|=
operator|new
name|long
index|[
literal|100
index|]
decl_stmt|;
annotation|@
name|Test
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
literal|10
argument_list|,
name|useConstantSeed
operator|=
literal|true
argument_list|)
DECL|method|testReproducible
specifier|public
name|void
name|testReproducible
parameter_list|()
block|{
if|if
condition|(
name|ITER
operator|++
operator|==
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|SEQUENCE
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SEQUENCE
index|[
name|i
index|]
operator|=
name|randomLong
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|SEQUENCE
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|SEQUENCE
index|[
name|i
index|]
argument_list|,
name|equalTo
argument_list|(
name|randomLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|buildTestCluster
specifier|protected
name|TestCluster
name|buildTestCluster
parameter_list|(
name|Scope
name|scope
parameter_list|)
throws|throws
name|IOException
block|{
comment|// produce some randomness
name|int
name|iters
init|=
name|between
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|randomLong
argument_list|()
expr_stmt|;
block|}
return|return
name|super
operator|.
name|buildTestCluster
argument_list|(
name|scope
argument_list|)
return|;
block|}
block|}
end_class

end_unit

