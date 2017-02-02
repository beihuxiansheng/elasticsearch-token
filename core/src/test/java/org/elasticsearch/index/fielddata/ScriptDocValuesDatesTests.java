begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|SortedNumericDocValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ScriptDocValues
operator|.
name|Dates
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
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTime
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTimeZone
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|ReadableDateTime
import|;
end_import

begin_class
DECL|class|ScriptDocValuesDatesTests
specifier|public
class|class
name|ScriptDocValuesDatesTests
extends|extends
name|ESTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|long
index|[]
index|[]
name|values
init|=
operator|new
name|long
index|[
name|between
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
index|]
index|[]
decl_stmt|;
name|ReadableDateTime
index|[]
index|[]
name|expectedDates
init|=
operator|new
name|ReadableDateTime
index|[
name|values
operator|.
name|length
index|]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|values
operator|.
name|length
condition|;
name|d
operator|++
control|)
block|{
name|values
index|[
name|d
index|]
operator|=
operator|new
name|long
index|[
name|randomBoolean
argument_list|()
condition|?
name|randomBoolean
argument_list|()
condition|?
literal|0
else|:
literal|1
else|:
name|between
argument_list|(
literal|2
argument_list|,
literal|100
argument_list|)
index|]
expr_stmt|;
name|expectedDates
index|[
name|d
index|]
operator|=
operator|new
name|ReadableDateTime
index|[
name|values
index|[
name|d
index|]
operator|.
name|length
index|]
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
name|values
index|[
name|d
index|]
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|expectedDates
index|[
name|d
index|]
index|[
name|i
index|]
operator|=
operator|new
name|DateTime
argument_list|(
name|randomNonNegativeLong
argument_list|()
argument_list|,
name|DateTimeZone
operator|.
name|UTC
argument_list|)
expr_stmt|;
name|values
index|[
name|d
index|]
index|[
name|i
index|]
operator|=
name|expectedDates
index|[
name|d
index|]
index|[
name|i
index|]
operator|.
name|getMillis
argument_list|()
expr_stmt|;
block|}
block|}
name|Dates
name|dates
init|=
name|wrap
argument_list|(
name|values
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|round
init|=
literal|0
init|;
name|round
operator|<
literal|10
condition|;
name|round
operator|++
control|)
block|{
name|int
name|d
init|=
name|between
argument_list|(
literal|0
argument_list|,
name|values
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
name|dates
operator|.
name|setNextDocId
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedDates
index|[
name|d
index|]
operator|.
name|length
operator|>
literal|0
condition|?
name|expectedDates
index|[
name|d
index|]
index|[
literal|0
index|]
else|:
operator|new
name|DateTime
argument_list|(
literal|0
argument_list|,
name|DateTimeZone
operator|.
name|UTC
argument_list|)
argument_list|,
name|dates
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|values
index|[
name|d
index|]
operator|.
name|length
argument_list|,
name|dates
operator|.
name|size
argument_list|()
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
name|values
index|[
name|d
index|]
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expectedDates
index|[
name|d
index|]
index|[
name|i
index|]
argument_list|,
name|dates
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|UnsupportedOperationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|dates
operator|.
name|add
argument_list|(
operator|new
name|DateTime
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"doc values are unmodifiable"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|wrap
specifier|private
name|Dates
name|wrap
parameter_list|(
name|long
index|[]
index|[]
name|values
parameter_list|)
block|{
return|return
operator|new
name|Dates
argument_list|(
operator|new
name|SortedNumericDocValues
argument_list|()
block|{
name|long
index|[]
name|current
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|current
operator|=
name|values
index|[
name|doc
index|]
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|current
operator|.
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|current
index|[
name|index
index|]
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

