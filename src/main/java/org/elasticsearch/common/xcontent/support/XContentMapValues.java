begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|support
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|Strings
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
name|regex
operator|.
name|Regex
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
name|unit
operator|.
name|TimeValue
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
name|List
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|XContentMapValues
specifier|public
class|class
name|XContentMapValues
block|{
comment|/**      * Extracts raw values (string, int, and so on) based on the path provided returning all of them      * as a single list.      */
DECL|method|extractRawValues
specifier|public
specifier|static
name|List
argument_list|<
name|Object
argument_list|>
name|extractRawValues
parameter_list|(
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|values
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|String
index|[]
name|pathElements
init|=
name|Strings
operator|.
name|splitStringToArray
argument_list|(
name|path
argument_list|,
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathElements
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|values
return|;
block|}
name|extractRawValues
argument_list|(
name|values
argument_list|,
name|map
argument_list|,
name|pathElements
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|values
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|extractRawValues
specifier|private
specifier|static
name|void
name|extractRawValues
parameter_list|(
name|List
name|values
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|part
parameter_list|,
name|String
index|[]
name|pathElements
parameter_list|,
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|==
name|pathElements
operator|.
name|length
condition|)
block|{
return|return;
block|}
name|String
name|key
init|=
name|pathElements
index|[
name|index
index|]
decl_stmt|;
name|Object
name|currentValue
init|=
name|part
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|int
name|nextIndex
init|=
name|index
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|currentValue
operator|==
literal|null
operator|&&
name|nextIndex
operator|!=
name|pathElements
operator|.
name|length
condition|)
block|{
name|key
operator|+=
literal|"."
operator|+
name|pathElements
index|[
name|nextIndex
index|]
expr_stmt|;
name|currentValue
operator|=
name|part
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|nextIndex
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|currentValue
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|currentValue
operator|instanceof
name|Map
condition|)
block|{
name|extractRawValues
argument_list|(
name|values
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|currentValue
argument_list|,
name|pathElements
argument_list|,
name|nextIndex
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentValue
operator|instanceof
name|List
condition|)
block|{
name|extractRawValues
argument_list|(
name|values
argument_list|,
operator|(
name|List
operator|)
name|currentValue
argument_list|,
name|pathElements
argument_list|,
name|nextIndex
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|.
name|add
argument_list|(
name|currentValue
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|extractRawValues
specifier|private
specifier|static
name|void
name|extractRawValues
parameter_list|(
name|List
name|values
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|part
parameter_list|,
name|String
index|[]
name|pathElements
parameter_list|,
name|int
name|index
parameter_list|)
block|{
for|for
control|(
name|Object
name|value
range|:
name|part
control|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Map
condition|)
block|{
name|extractRawValues
argument_list|(
name|values
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|value
argument_list|,
name|pathElements
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|List
condition|)
block|{
name|extractRawValues
argument_list|(
name|values
argument_list|,
operator|(
name|List
operator|)
name|value
argument_list|,
name|pathElements
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|extractValue
specifier|public
specifier|static
name|Object
name|extractValue
parameter_list|(
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|String
index|[]
name|pathElements
init|=
name|Strings
operator|.
name|splitStringToArray
argument_list|(
name|path
argument_list|,
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathElements
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|extractValue
argument_list|(
name|pathElements
argument_list|,
literal|0
argument_list|,
name|map
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|extractValue
specifier|private
specifier|static
name|Object
name|extractValue
parameter_list|(
name|String
index|[]
name|pathElements
parameter_list|,
name|int
name|index
parameter_list|,
name|Object
name|currentValue
parameter_list|)
block|{
if|if
condition|(
name|index
operator|==
name|pathElements
operator|.
name|length
condition|)
block|{
return|return
name|currentValue
return|;
block|}
if|if
condition|(
name|currentValue
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|currentValue
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|currentValue
decl_stmt|;
name|String
name|key
init|=
name|pathElements
index|[
name|index
index|]
decl_stmt|;
name|Object
name|mapValue
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|int
name|nextIndex
init|=
name|index
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|mapValue
operator|==
literal|null
operator|&&
name|nextIndex
operator|!=
name|pathElements
operator|.
name|length
condition|)
block|{
name|key
operator|+=
literal|"."
operator|+
name|pathElements
index|[
name|nextIndex
index|]
expr_stmt|;
name|mapValue
operator|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|nextIndex
operator|++
expr_stmt|;
block|}
return|return
name|extractValue
argument_list|(
name|pathElements
argument_list|,
name|nextIndex
argument_list|,
name|mapValue
argument_list|)
return|;
block|}
if|if
condition|(
name|currentValue
operator|instanceof
name|List
condition|)
block|{
name|List
name|valueList
init|=
operator|(
name|List
operator|)
name|currentValue
decl_stmt|;
name|List
name|newList
init|=
operator|new
name|ArrayList
argument_list|(
name|valueList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|valueList
control|)
block|{
name|Object
name|listValue
init|=
name|extractValue
argument_list|(
name|pathElements
argument_list|,
name|index
argument_list|,
name|o
argument_list|)
decl_stmt|;
if|if
condition|(
name|listValue
operator|!=
literal|null
condition|)
block|{
name|newList
operator|.
name|add
argument_list|(
name|listValue
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|newList
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|filter
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|filter
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|,
name|String
index|[]
name|includes
parameter_list|,
name|String
index|[]
name|excludes
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|filter
argument_list|(
name|map
argument_list|,
name|result
argument_list|,
name|includes
operator|==
literal|null
condition|?
name|Strings
operator|.
name|EMPTY_ARRAY
else|:
name|includes
argument_list|,
name|excludes
operator|==
literal|null
condition|?
name|Strings
operator|.
name|EMPTY_ARRAY
else|:
name|excludes
argument_list|,
operator|new
name|StringBuilder
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|filter
specifier|private
specifier|static
name|void
name|filter
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|into
parameter_list|,
name|String
index|[]
name|includes
parameter_list|,
name|String
index|[]
name|excludes
parameter_list|,
name|StringBuilder
name|sb
parameter_list|)
block|{
if|if
condition|(
name|includes
operator|.
name|length
operator|==
literal|0
operator|&&
name|excludes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|into
operator|.
name|putAll
argument_list|(
name|map
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|int
name|mark
init|=
name|sb
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|boolean
name|excluded
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|exclude
range|:
name|excludes
control|)
block|{
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|exclude
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|excluded
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|excluded
condition|)
block|{
name|sb
operator|.
name|setLength
argument_list|(
name|mark
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|boolean
name|exactIncludeMatch
decl_stmt|;
if|if
condition|(
name|includes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// implied match anything
name|exactIncludeMatch
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|exactIncludeMatch
operator|=
literal|false
expr_stmt|;
name|boolean
name|pathIsPrefixOfAnInclude
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|include
range|:
name|includes
control|)
block|{
comment|// check for prefix matches as well to see if we need to zero in, something like: obj1.arr1.* or *.field
comment|// note, this does not work well with middle matches, like obj1.*.obj3
if|if
condition|(
name|include
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'*'
condition|)
block|{
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|include
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|exactIncludeMatch
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|pathIsPrefixOfAnInclude
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|include
operator|.
name|startsWith
argument_list|(
name|path
argument_list|)
condition|)
block|{
if|if
condition|(
name|include
operator|.
name|length
argument_list|()
operator|==
name|path
operator|.
name|length
argument_list|()
condition|)
block|{
name|exactIncludeMatch
operator|=
literal|true
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|include
operator|.
name|length
argument_list|()
operator|>
name|path
operator|.
name|length
argument_list|()
operator|&&
name|include
operator|.
name|charAt
argument_list|(
name|path
operator|.
name|length
argument_list|()
argument_list|)
operator|==
literal|'.'
condition|)
block|{
comment|// include might may match deeper paths. Dive deeper.
name|pathIsPrefixOfAnInclude
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|include
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|exactIncludeMatch
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|pathIsPrefixOfAnInclude
operator|&&
operator|!
name|exactIncludeMatch
condition|)
block|{
comment|// skip subkeys, not interesting.
name|sb
operator|.
name|setLength
argument_list|(
name|mark
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Map
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|innerInto
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
comment|// if we had an exact match, we want give deeper excludes their chance
name|filter
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|innerInto
argument_list|,
name|exactIncludeMatch
condition|?
name|Strings
operator|.
name|EMPTY_ARRAY
else|:
name|includes
argument_list|,
name|excludes
argument_list|,
name|sb
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|innerInto
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|into
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|innerInto
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|List
condition|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|innerInto
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|// if we had an exact match, we want give deeper excludes their chance
name|filter
argument_list|(
name|list
argument_list|,
name|innerInto
argument_list|,
name|exactIncludeMatch
condition|?
name|Strings
operator|.
name|EMPTY_ARRAY
else|:
name|includes
argument_list|,
name|excludes
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|into
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|innerInto
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|exactIncludeMatch
condition|)
block|{
name|into
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|setLength
argument_list|(
name|mark
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|filter
specifier|private
specifier|static
name|void
name|filter
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|from
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|to
parameter_list|,
name|String
index|[]
name|includes
parameter_list|,
name|String
index|[]
name|excludes
parameter_list|,
name|StringBuilder
name|sb
parameter_list|)
block|{
if|if
condition|(
name|includes
operator|.
name|length
operator|==
literal|0
operator|&&
name|excludes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|to
operator|.
name|addAll
argument_list|(
name|from
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|Object
name|o
range|:
name|from
control|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|innerInto
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|filter
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|o
argument_list|,
name|innerInto
argument_list|,
name|includes
argument_list|,
name|excludes
argument_list|,
name|sb
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|innerInto
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|to
operator|.
name|add
argument_list|(
name|innerInto
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|innerInto
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|filter
argument_list|(
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|o
argument_list|,
name|innerInto
argument_list|,
name|includes
argument_list|,
name|excludes
argument_list|,
name|sb
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|innerInto
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|to
operator|.
name|add
argument_list|(
name|innerInto
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|to
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|isObject
specifier|public
specifier|static
name|boolean
name|isObject
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
return|return
name|node
operator|instanceof
name|Map
return|;
block|}
DECL|method|isArray
specifier|public
specifier|static
name|boolean
name|isArray
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
return|return
name|node
operator|instanceof
name|List
return|;
block|}
DECL|method|nodeStringValue
specifier|public
specifier|static
name|String
name|nodeStringValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|node
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|nodeFloatValue
specifier|public
specifier|static
name|float
name|nodeFloatValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|float
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeFloatValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeFloatValue
specifier|public
specifier|static
name|float
name|nodeFloatValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|floatValue
argument_list|()
return|;
block|}
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeDoubleValue
specifier|public
specifier|static
name|double
name|nodeDoubleValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|double
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeDoubleValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeDoubleValue
specifier|public
specifier|static
name|double
name|nodeDoubleValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
block|}
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeIntegerValue
specifier|public
specifier|static
name|int
name|nodeIntegerValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|intValue
argument_list|()
return|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeIntegerValue
specifier|public
specifier|static
name|int
name|nodeIntegerValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|int
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|intValue
argument_list|()
return|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeShortValue
specifier|public
specifier|static
name|short
name|nodeShortValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|short
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeShortValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeShortValue
specifier|public
specifier|static
name|short
name|nodeShortValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|shortValue
argument_list|()
return|;
block|}
return|return
name|Short
operator|.
name|parseShort
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeByteValue
specifier|public
specifier|static
name|byte
name|nodeByteValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|byte
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeByteValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeByteValue
specifier|public
specifier|static
name|byte
name|nodeByteValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|byteValue
argument_list|()
return|;
block|}
return|return
name|Byte
operator|.
name|parseByte
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeLongValue
specifier|public
specifier|static
name|long
name|nodeLongValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|long
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeLongValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeLongValue
specifier|public
specifier|static
name|long
name|nodeLongValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeBooleanValue
specifier|public
specifier|static
name|boolean
name|nodeBooleanValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeBooleanValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeBooleanValue
specifier|public
specifier|static
name|boolean
name|nodeBooleanValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Boolean
condition|)
block|{
return|return
operator|(
name|Boolean
operator|)
name|node
return|;
block|}
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|intValue
argument_list|()
operator|!=
literal|0
return|;
block|}
name|String
name|value
init|=
name|node
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|!
operator|(
name|value
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
operator|||
name|value
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
operator|||
name|value
operator|.
name|equals
argument_list|(
literal|"off"
argument_list|)
operator|)
return|;
block|}
DECL|method|nodeTimeValue
specifier|public
specifier|static
name|TimeValue
name|nodeTimeValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|TimeValue
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeTimeValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeTimeValue
specifier|public
specifier|static
name|TimeValue
name|nodeTimeValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
return|return
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|nodeMapValue
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nodeMapValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|String
name|desc
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Map
condition|)
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|node
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
name|desc
operator|+
literal|" should be a hash but was of type: "
operator|+
name|node
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

