begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
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
name|generators
operator|.
name|RandomStrings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Scorer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|LeafSearchScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
operator|.
name|values
operator|.
name|ScriptBytesValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
operator|.
name|values
operator|.
name|ScriptDoubleValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
operator|.
name|values
operator|.
name|ScriptLongValues
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
name|io
operator|.
name|IOException
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
name|Map
import|;
end_import

begin_class
DECL|class|ScriptValuesTests
specifier|public
class|class
name|ScriptValuesTests
extends|extends
name|ESTestCase
block|{
DECL|class|FakeSearchScript
specifier|private
specifier|static
class|class
name|FakeSearchScript
implements|implements
name|LeafSearchScript
block|{
DECL|field|values
specifier|private
specifier|final
name|Object
index|[]
index|[]
name|values
decl_stmt|;
DECL|field|index
name|int
name|index
decl_stmt|;
DECL|method|FakeSearchScript
name|FakeSearchScript
parameter_list|(
name|Object
index|[]
index|[]
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|index
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextVar
specifier|public
name|void
name|setNextVar
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{         }
annotation|@
name|Override
DECL|method|run
specifier|public
name|Object
name|run
parameter_list|()
block|{
comment|// Script values are supposed to support null, single values, arrays and collections
specifier|final
name|Object
index|[]
name|values
init|=
name|this
operator|.
name|values
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|length
operator|<=
literal|1
operator|&&
name|randomBoolean
argument_list|()
condition|)
block|{
return|return
name|values
operator|.
name|length
operator|==
literal|0
condition|?
literal|null
else|:
name|values
index|[
literal|0
index|]
return|;
block|}
return|return
name|randomBoolean
argument_list|()
condition|?
name|values
else|:
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{         }
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|index
operator|=
name|doc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setSource
specifier|public
name|void
name|setSource
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|)
block|{         }
annotation|@
name|Override
DECL|method|runAsLong
specifier|public
name|long
name|runAsLong
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|runAsDouble
specifier|public
name|double
name|runAsDouble
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|method|testLongs
specifier|public
name|void
name|testLongs
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Object
index|[]
index|[]
name|values
init|=
operator|new
name|Long
index|[
name|randomInt
argument_list|(
literal|10
argument_list|)
index|]
index|[]
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
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Long
index|[]
name|longs
init|=
operator|new
name|Long
index|[
name|randomInt
argument_list|(
literal|8
argument_list|)
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|longs
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|longs
index|[
name|j
index|]
operator|=
name|randomLong
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|longs
argument_list|)
expr_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|longs
expr_stmt|;
block|}
name|FakeSearchScript
name|script
init|=
operator|new
name|FakeSearchScript
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|ScriptLongValues
name|scriptValues
init|=
operator|new
name|ScriptLongValues
argument_list|(
name|script
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
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
argument_list|,
name|scriptValues
operator|.
name|advanceExact
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
argument_list|,
name|scriptValues
operator|.
name|docValueCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|scriptValues
operator|.
name|nextValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testBooleans
specifier|public
name|void
name|testBooleans
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Object
index|[]
index|[]
name|values
init|=
operator|new
name|Boolean
index|[
name|randomInt
argument_list|(
literal|10
argument_list|)
index|]
index|[]
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
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Boolean
index|[]
name|booleans
init|=
operator|new
name|Boolean
index|[
name|randomInt
argument_list|(
literal|8
argument_list|)
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|booleans
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|booleans
index|[
name|j
index|]
operator|=
name|randomBoolean
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|booleans
argument_list|)
expr_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|booleans
expr_stmt|;
block|}
name|FakeSearchScript
name|script
init|=
operator|new
name|FakeSearchScript
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|ScriptLongValues
name|scriptValues
init|=
operator|new
name|ScriptLongValues
argument_list|(
name|script
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
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
argument_list|,
name|scriptValues
operator|.
name|advanceExact
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
argument_list|,
name|scriptValues
operator|.
name|docValueCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|scriptValues
operator|.
name|nextValue
argument_list|()
operator|==
literal|1L
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testDoubles
specifier|public
name|void
name|testDoubles
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Object
index|[]
index|[]
name|values
init|=
operator|new
name|Double
index|[
name|randomInt
argument_list|(
literal|10
argument_list|)
index|]
index|[]
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
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Double
index|[]
name|doubles
init|=
operator|new
name|Double
index|[
name|randomInt
argument_list|(
literal|8
argument_list|)
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|doubles
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|doubles
index|[
name|j
index|]
operator|=
name|randomDouble
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|doubles
argument_list|)
expr_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|doubles
expr_stmt|;
block|}
name|FakeSearchScript
name|script
init|=
operator|new
name|FakeSearchScript
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|ScriptDoubleValues
name|scriptValues
init|=
operator|new
name|ScriptDoubleValues
argument_list|(
name|script
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
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
argument_list|,
name|scriptValues
operator|.
name|advanceExact
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
argument_list|,
name|scriptValues
operator|.
name|docValueCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|scriptValues
operator|.
name|nextValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testBytes
specifier|public
name|void
name|testBytes
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
index|[]
index|[]
name|values
init|=
operator|new
name|String
index|[
name|randomInt
argument_list|(
literal|10
argument_list|)
index|]
index|[]
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
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|String
index|[]
name|strings
init|=
operator|new
name|String
index|[
name|randomInt
argument_list|(
literal|8
argument_list|)
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|strings
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|strings
index|[
name|j
index|]
operator|=
name|RandomStrings
operator|.
name|randomAsciiOfLength
argument_list|(
name|random
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|strings
argument_list|)
expr_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|strings
expr_stmt|;
block|}
name|FakeSearchScript
name|script
init|=
operator|new
name|FakeSearchScript
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|ScriptBytesValues
name|scriptValues
init|=
operator|new
name|ScriptBytesValues
argument_list|(
name|script
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
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
argument_list|,
name|scriptValues
operator|.
name|advanceExact
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
argument_list|,
name|scriptValues
operator|.
name|docValueCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|values
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|)
argument_list|,
name|scriptValues
operator|.
name|nextValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

