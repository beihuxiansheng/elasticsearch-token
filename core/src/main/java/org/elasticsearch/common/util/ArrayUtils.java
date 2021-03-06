begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
package|;
end_package

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
name|Arrays
import|;
end_import

begin_class
DECL|class|ArrayUtils
specifier|public
class|class
name|ArrayUtils
block|{
DECL|method|ArrayUtils
specifier|private
name|ArrayUtils
parameter_list|()
block|{}
comment|/**      * Return the index of<code>value</code> in<code>array</code>, or<tt>-1</tt> if there is no such index.      * If there are several values that are within<code>tolerance</code> or less of<code>value</code>, this method will return the      * index of the closest value. In case of several values being as close ot<code>value</code>, there is no guarantee which index      * will be returned.      * Results are undefined if the array is not sorted.      */
DECL|method|binarySearch
specifier|public
specifier|static
name|int
name|binarySearch
parameter_list|(
name|double
index|[]
name|array
parameter_list|,
name|double
name|value
parameter_list|,
name|double
name|tolerance
parameter_list|)
block|{
if|if
condition|(
name|array
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|binarySearch
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|array
operator|.
name|length
argument_list|,
name|value
argument_list|,
name|tolerance
argument_list|)
return|;
block|}
DECL|method|binarySearch
specifier|private
specifier|static
name|int
name|binarySearch
parameter_list|(
name|double
index|[]
name|array
parameter_list|,
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|,
name|double
name|value
parameter_list|,
name|double
name|tolerance
parameter_list|)
block|{
name|int
name|index
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|array
argument_list|,
name|fromIndex
argument_list|,
name|toIndex
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
specifier|final
name|int
name|highIndex
init|=
operator|-
literal|1
operator|-
name|index
decl_stmt|;
comment|// first index of a value that is> value
specifier|final
name|int
name|lowIndex
init|=
name|highIndex
operator|-
literal|1
decl_stmt|;
comment|// last index of a value that is< value
name|double
name|lowError
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|highError
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
if|if
condition|(
name|lowIndex
operator|>=
literal|0
condition|)
block|{
name|lowError
operator|=
name|value
operator|-
name|array
index|[
name|lowIndex
index|]
expr_stmt|;
block|}
if|if
condition|(
name|highIndex
operator|<
name|array
operator|.
name|length
condition|)
block|{
name|highError
operator|=
name|array
index|[
name|highIndex
index|]
operator|-
name|value
expr_stmt|;
block|}
if|if
condition|(
name|highError
operator|<
name|lowError
condition|)
block|{
if|if
condition|(
name|highError
operator|<
name|tolerance
condition|)
block|{
name|index
operator|=
name|highIndex
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|lowError
operator|<
name|tolerance
condition|)
block|{
name|index
operator|=
name|lowIndex
expr_stmt|;
block|}
else|else
block|{
name|index
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
return|return
name|index
return|;
block|}
comment|/**      * Concatenates 2 arrays      */
DECL|method|concat
specifier|public
specifier|static
name|String
index|[]
name|concat
parameter_list|(
name|String
index|[]
name|one
parameter_list|,
name|String
index|[]
name|other
parameter_list|)
block|{
return|return
name|concat
argument_list|(
name|one
argument_list|,
name|other
argument_list|,
name|String
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**      * Concatenates 2 arrays      */
DECL|method|concat
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
index|[]
name|concat
parameter_list|(
name|T
index|[]
name|one
parameter_list|,
name|T
index|[]
name|other
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
name|T
index|[]
name|target
init|=
operator|(
name|T
index|[]
operator|)
name|Array
operator|.
name|newInstance
argument_list|(
name|clazz
argument_list|,
name|one
operator|.
name|length
operator|+
name|other
operator|.
name|length
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|one
argument_list|,
literal|0
argument_list|,
name|target
argument_list|,
literal|0
argument_list|,
name|one
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|other
argument_list|,
literal|0
argument_list|,
name|target
argument_list|,
name|one
operator|.
name|length
argument_list|,
name|other
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|target
return|;
block|}
block|}
end_class

end_unit

