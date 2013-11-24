begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.support.numeric
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
operator|.
name|numeric
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
name|util
operator|.
name|ArrayUtil
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
name|LongValues
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
name|SearchScript
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
name|AggregationExecutionException
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
name|ScriptValues
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * {@link LongValues} implementation which is based on a script  */
end_comment

begin_class
DECL|class|ScriptLongValues
specifier|public
class|class
name|ScriptLongValues
extends|extends
name|LongValues
implements|implements
name|ScriptValues
block|{
DECL|field|script
specifier|final
name|SearchScript
name|script
decl_stmt|;
DECL|field|value
specifier|private
name|Object
name|value
decl_stmt|;
DECL|field|values
specifier|private
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
literal|4
index|]
decl_stmt|;
DECL|field|valueCount
specifier|private
name|int
name|valueCount
decl_stmt|;
DECL|field|valueOffset
specifier|private
name|int
name|valueOffset
decl_stmt|;
DECL|method|ScriptLongValues
specifier|public
name|ScriptLongValues
parameter_list|(
name|SearchScript
name|script
parameter_list|)
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// assume multi-valued
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|script
specifier|public
name|SearchScript
name|script
parameter_list|()
block|{
return|return
name|script
return|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
name|script
operator|.
name|setNextDocId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|value
operator|=
name|script
operator|.
name|run
argument_list|()
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|valueCount
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
name|valueCount
operator|=
literal|1
expr_stmt|;
name|values
index|[
literal|0
index|]
operator|=
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|valueCount
operator|=
name|Array
operator|.
name|getLength
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|values
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|values
argument_list|,
name|valueCount
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
name|valueCount
condition|;
operator|++
name|i
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|Number
operator|)
name|Array
operator|.
name|get
argument_list|(
name|value
argument_list|,
name|i
operator|++
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Collection
condition|)
block|{
name|valueCount
operator|=
operator|(
operator|(
name|Collection
argument_list|<
name|?
argument_list|>
operator|)
name|value
operator|)
operator|.
name|size
argument_list|()
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|?
argument_list|>
name|it
init|=
operator|(
operator|(
name|Collection
argument_list|<
name|?
argument_list|>
operator|)
name|value
operator|)
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|Number
operator|)
name|it
operator|.
name|next
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
assert|assert
name|i
operator|==
name|valueCount
assert|;
block|}
else|else
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Unsupported script value ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|valueOffset
operator|=
literal|0
expr_stmt|;
return|return
name|valueCount
return|;
block|}
annotation|@
name|Override
DECL|method|nextValue
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
assert|assert
name|valueOffset
operator|<
name|valueCount
assert|;
return|return
name|values
index|[
name|valueOffset
operator|++
index|]
return|;
block|}
block|}
end_class

end_unit

