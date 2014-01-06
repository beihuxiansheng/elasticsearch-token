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
name|util
operator|.
name|AbstractList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|RandomAccess
import|;
end_import

begin_comment
comment|// TODO this could use some javadocs
end_comment

begin_class
DECL|class|SlicedObjectList
specifier|public
specifier|abstract
class|class
name|SlicedObjectList
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractList
argument_list|<
name|T
argument_list|>
implements|implements
name|RandomAccess
block|{
DECL|field|values
specifier|public
name|T
index|[]
name|values
decl_stmt|;
DECL|field|offset
specifier|public
name|int
name|offset
decl_stmt|;
DECL|field|length
specifier|public
name|int
name|length
decl_stmt|;
DECL|method|SlicedObjectList
specifier|public
name|SlicedObjectList
parameter_list|(
name|T
index|[]
name|values
parameter_list|)
block|{
name|this
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|SlicedObjectList
specifier|public
name|SlicedObjectList
parameter_list|(
name|T
index|[]
name|values
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|<
name|size
argument_list|()
assert|;
return|return
name|values
index|[
name|offset
operator|+
name|index
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|T
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|T
name|element
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"modifying list opertations are not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|object
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|object
operator|instanceof
name|SlicedObjectList
condition|)
block|{
name|SlicedObjectList
argument_list|<
name|?
argument_list|>
name|that
init|=
operator|(
name|SlicedObjectList
argument_list|<
name|?
argument_list|>
operator|)
name|object
decl_stmt|;
name|int
name|size
init|=
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|that
operator|.
name|size
argument_list|()
operator|!=
name|size
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|values
index|[
name|offset
operator|+
name|i
index|]
operator|.
name|equals
argument_list|(
name|that
operator|.
name|values
index|[
name|that
operator|.
name|offset
operator|+
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
return|return
name|super
operator|.
name|equals
argument_list|(
name|object
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
literal|1
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|values
index|[
name|offset
operator|+
name|i
index|]
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
name|size
argument_list|()
operator|*
literal|10
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|values
index|[
name|offset
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|values
index|[
name|offset
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|grow
specifier|public
specifier|abstract
name|void
name|grow
parameter_list|(
name|int
name|newLength
parameter_list|)
function_decl|;
block|}
end_class

end_unit

