begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|util
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
comment|/**  */
end_comment

begin_class
DECL|class|StringArrayRef
specifier|public
class|class
name|StringArrayRef
extends|extends
name|AbstractList
argument_list|<
name|String
argument_list|>
implements|implements
name|RandomAccess
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|StringArrayRef
name|EMPTY
init|=
operator|new
name|StringArrayRef
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
DECL|field|values
specifier|public
name|String
index|[]
name|values
decl_stmt|;
DECL|field|start
specifier|public
name|int
name|start
decl_stmt|;
DECL|field|end
specifier|public
name|int
name|end
decl_stmt|;
DECL|method|StringArrayRef
specifier|public
name|StringArrayRef
parameter_list|(
name|String
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
DECL|method|StringArrayRef
specifier|public
name|StringArrayRef
parameter_list|(
name|String
index|[]
name|values
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|StringArrayRef
specifier|public
name|StringArrayRef
parameter_list|(
name|String
index|[]
name|values
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
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
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|int
name|newLength
parameter_list|)
block|{
assert|assert
name|start
operator|==
literal|0
assert|;
comment|// NOTE: senseless if offset != 0
name|end
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|values
operator|.
name|length
operator|<
name|newLength
condition|)
block|{
name|values
operator|=
operator|new
name|String
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|newLength
argument_list|,
literal|32
argument_list|)
index|]
expr_stmt|;
block|}
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
name|end
operator|-
name|start
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
operator|!=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|size
argument_list|()
assert|;
return|return
name|values
index|[
name|start
operator|+
name|index
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|Object
name|target
parameter_list|)
block|{
name|String
name|sTarget
init|=
name|target
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|values
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|sTarget
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|indexOf
specifier|public
name|int
name|indexOf
parameter_list|(
name|Object
name|target
parameter_list|)
block|{
name|String
name|sTarget
init|=
name|target
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|values
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|sTarget
argument_list|)
condition|)
return|return
operator|(
name|i
operator|-
name|start
operator|)
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|lastIndexOf
specifier|public
name|int
name|lastIndexOf
parameter_list|(
name|Object
name|target
parameter_list|)
block|{
name|String
name|sTarget
init|=
name|target
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|end
operator|-
literal|1
init|;
name|i
operator|>=
name|start
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
name|values
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|sTarget
argument_list|)
condition|)
return|return
operator|(
name|i
operator|-
name|start
operator|)
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|String
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|String
name|element
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|size
argument_list|()
assert|;
name|String
name|oldValue
init|=
name|values
index|[
name|start
operator|+
name|index
index|]
decl_stmt|;
name|values
index|[
name|start
operator|+
name|index
index|]
operator|=
name|element
expr_stmt|;
return|return
name|oldValue
return|;
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
name|StringArrayRef
condition|)
block|{
name|StringArrayRef
name|that
init|=
operator|(
name|StringArrayRef
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
operator|!
name|values
index|[
name|start
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
name|start
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
name|start
init|;
name|i
operator|<
name|end
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
operator|.
name|append
argument_list|(
name|values
index|[
name|start
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
operator|+
literal|1
init|;
name|i
operator|<
name|end
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
name|i
index|]
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

