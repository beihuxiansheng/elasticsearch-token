begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.math
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|math
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
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

begin_class
DECL|class|MathUtilsTests
specifier|public
class|class
name|MathUtilsTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|Test
DECL|method|mod
specifier|public
name|void
name|mod
parameter_list|()
block|{
specifier|final
name|int
name|iters
init|=
name|scaledRandomIntBetween
argument_list|(
literal|1000
argument_list|,
literal|10000
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
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|v
init|=
name|rarely
argument_list|()
condition|?
name|Integer
operator|.
name|MIN_VALUE
else|:
name|rarely
argument_list|()
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|randomInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|m
init|=
name|rarely
argument_list|()
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|final
name|int
name|mod
init|=
name|MathUtils
operator|.
name|mod
argument_list|(
name|v
argument_list|,
name|m
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|mod
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mod
operator|<
name|m
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

