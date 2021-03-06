begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.recycler
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|recycler
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
name|recycler
operator|.
name|Recycler
operator|.
name|V
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
name|ESTestCase
import|;
end_import

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
name|Arrays
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

begin_class
DECL|class|AbstractRecyclerTestCase
specifier|public
specifier|abstract
class|class
name|AbstractRecyclerTestCase
extends|extends
name|ESTestCase
block|{
comment|// marker states for data
DECL|field|FRESH
specifier|protected
specifier|static
specifier|final
name|byte
name|FRESH
init|=
literal|1
decl_stmt|;
DECL|field|RECYCLED
specifier|protected
specifier|static
specifier|final
name|byte
name|RECYCLED
init|=
literal|2
decl_stmt|;
DECL|field|DEAD
specifier|protected
specifier|static
specifier|final
name|byte
name|DEAD
init|=
literal|42
decl_stmt|;
DECL|field|RECYCLER_C
specifier|protected
specifier|static
specifier|final
name|Recycler
operator|.
name|C
argument_list|<
name|byte
index|[]
argument_list|>
name|RECYCLER_C
init|=
operator|new
name|AbstractRecyclerC
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|newInstance
parameter_list|(
name|int
name|sizing
parameter_list|)
block|{
name|byte
index|[]
name|value
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
comment|// "fresh" is intentionally not 0 to ensure we covered this code path
name|Arrays
operator|.
name|fill
argument_list|(
name|value
argument_list|,
name|FRESH
argument_list|)
expr_stmt|;
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|recycle
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|value
argument_list|,
name|RECYCLED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|destroy
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
comment|// we cannot really free the internals of a byte[], so mark it for verification
name|Arrays
operator|.
name|fill
argument_list|(
name|value
argument_list|,
name|DEAD
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
DECL|method|assertFresh
specifier|protected
name|void
name|assertFresh
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|FRESH
argument_list|,
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertRecycled
specifier|protected
name|void
name|assertRecycled
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|RECYCLED
argument_list|,
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertDead
specifier|protected
name|void
name|assertDead
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|DEAD
argument_list|,
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|newRecycler
specifier|protected
specifier|abstract
name|Recycler
argument_list|<
name|byte
index|[]
argument_list|>
name|newRecycler
parameter_list|(
name|int
name|limit
parameter_list|)
function_decl|;
DECL|field|limit
specifier|protected
name|int
name|limit
init|=
name|randomIntBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
DECL|method|testReuse
specifier|public
name|void
name|testReuse
parameter_list|()
block|{
name|Recycler
argument_list|<
name|byte
index|[]
argument_list|>
name|r
init|=
name|newRecycler
argument_list|(
name|limit
argument_list|)
decl_stmt|;
name|Recycler
operator|.
name|V
argument_list|<
name|byte
index|[]
argument_list|>
name|o
init|=
name|r
operator|.
name|obtain
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|o
operator|.
name|isRecycled
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|b1
init|=
name|o
operator|.
name|v
argument_list|()
decl_stmt|;
name|assertFresh
argument_list|(
name|b1
argument_list|)
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertRecycled
argument_list|(
name|b1
argument_list|)
expr_stmt|;
name|o
operator|=
name|r
operator|.
name|obtain
argument_list|()
expr_stmt|;
specifier|final
name|byte
index|[]
name|b2
init|=
name|o
operator|.
name|v
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|.
name|isRecycled
argument_list|()
condition|)
block|{
name|assertRecycled
argument_list|(
name|b2
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|b1
argument_list|,
name|b2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFresh
argument_list|(
name|b2
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|b1
argument_list|,
name|b2
argument_list|)
expr_stmt|;
block|}
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testRecycle
specifier|public
name|void
name|testRecycle
parameter_list|()
block|{
name|Recycler
argument_list|<
name|byte
index|[]
argument_list|>
name|r
init|=
name|newRecycler
argument_list|(
name|limit
argument_list|)
decl_stmt|;
name|Recycler
operator|.
name|V
argument_list|<
name|byte
index|[]
argument_list|>
name|o
init|=
name|r
operator|.
name|obtain
argument_list|()
decl_stmt|;
name|assertFresh
argument_list|(
name|o
operator|.
name|v
argument_list|()
argument_list|)
expr_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|o
operator|.
name|v
argument_list|()
argument_list|)
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|o
operator|=
name|r
operator|.
name|obtain
argument_list|()
expr_stmt|;
name|assertRecycled
argument_list|(
name|o
operator|.
name|v
argument_list|()
argument_list|)
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDoubleRelease
specifier|public
name|void
name|testDoubleRelease
parameter_list|()
block|{
specifier|final
name|Recycler
argument_list|<
name|byte
index|[]
argument_list|>
name|r
init|=
name|newRecycler
argument_list|(
name|limit
argument_list|)
decl_stmt|;
specifier|final
name|Recycler
operator|.
name|V
argument_list|<
name|byte
index|[]
argument_list|>
name|v1
init|=
name|r
operator|.
name|obtain
argument_list|()
decl_stmt|;
name|v1
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|v1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// impl has protection against double release: ok
return|return;
block|}
comment|// otherwise ensure that the impl may not be returned twice
specifier|final
name|Recycler
operator|.
name|V
argument_list|<
name|byte
index|[]
argument_list|>
name|v2
init|=
name|r
operator|.
name|obtain
argument_list|()
decl_stmt|;
specifier|final
name|Recycler
operator|.
name|V
argument_list|<
name|byte
index|[]
argument_list|>
name|v3
init|=
name|r
operator|.
name|obtain
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|v2
operator|.
name|v
argument_list|()
argument_list|,
name|v3
operator|.
name|v
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDestroyWhenOverCapacity
specifier|public
name|void
name|testDestroyWhenOverCapacity
parameter_list|()
block|{
name|Recycler
argument_list|<
name|byte
index|[]
argument_list|>
name|r
init|=
name|newRecycler
argument_list|(
name|limit
argument_list|)
decl_stmt|;
comment|// get& keep reference to new/recycled data
name|Recycler
operator|.
name|V
argument_list|<
name|byte
index|[]
argument_list|>
name|o
init|=
name|r
operator|.
name|obtain
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|o
operator|.
name|v
argument_list|()
decl_stmt|;
name|assertFresh
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// now exhaust the recycler
name|List
argument_list|<
name|V
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|>
name|vals
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|limit
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
name|limit
condition|;
operator|++
name|i
control|)
block|{
name|vals
operator|.
name|add
argument_list|(
name|r
operator|.
name|obtain
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Recycler size increases on release, not on obtain!
for|for
control|(
name|V
argument_list|<
name|byte
index|[]
argument_list|>
name|v
range|:
name|vals
control|)
block|{
name|v
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// release first ref, verify for destruction
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertDead
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// close the rest
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testClose
specifier|public
name|void
name|testClose
parameter_list|()
block|{
name|Recycler
argument_list|<
name|byte
index|[]
argument_list|>
name|r
init|=
name|newRecycler
argument_list|(
name|limit
argument_list|)
decl_stmt|;
comment|// get& keep reference to pooled data
name|Recycler
operator|.
name|V
argument_list|<
name|byte
index|[]
argument_list|>
name|o
init|=
name|r
operator|.
name|obtain
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|o
operator|.
name|v
argument_list|()
decl_stmt|;
name|assertFresh
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// randomize& return to pool
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify that recycle() ran
name|assertRecycled
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// closing the recycler should mark recycled instances via destroy()
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertDead
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

