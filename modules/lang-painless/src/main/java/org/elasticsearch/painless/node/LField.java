begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|node
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Sort
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Struct
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Variables
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|MethodWriter
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
comment|/**  * Represents a field load/store or defers to a possible shortcuts.  */
end_comment

begin_class
DECL|class|LField
specifier|public
specifier|final
class|class
name|LField
extends|extends
name|ALink
block|{
DECL|field|value
specifier|final
name|String
name|value
decl_stmt|;
DECL|field|field
name|Field
name|field
decl_stmt|;
DECL|method|LField
specifier|public
name|LField
parameter_list|(
name|int
name|line
parameter_list|,
name|String
name|location
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|analyze
name|ALink
name|analyze
parameter_list|(
name|Variables
name|variables
parameter_list|)
block|{
if|if
condition|(
name|before
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|error
argument_list|(
literal|"Illegal tree structure."
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|Sort
name|sort
init|=
name|before
operator|.
name|sort
decl_stmt|;
if|if
condition|(
name|sort
operator|==
name|Sort
operator|.
name|ARRAY
condition|)
block|{
return|return
operator|new
name|LArrayLength
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
name|value
argument_list|)
operator|.
name|copy
argument_list|(
name|this
argument_list|)
operator|.
name|analyze
argument_list|(
name|variables
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|sort
operator|==
name|Sort
operator|.
name|DEF
condition|)
block|{
return|return
operator|new
name|LDefField
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
name|value
argument_list|)
operator|.
name|copy
argument_list|(
name|this
argument_list|)
operator|.
name|analyze
argument_list|(
name|variables
argument_list|)
return|;
block|}
specifier|final
name|Struct
name|struct
init|=
name|before
operator|.
name|struct
decl_stmt|;
name|field
operator|=
name|statik
condition|?
name|struct
operator|.
name|staticMembers
operator|.
name|get
argument_list|(
name|value
argument_list|)
else|:
name|struct
operator|.
name|members
operator|.
name|get
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|store
operator|&&
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
operator|.
name|isFinal
argument_list|(
name|field
operator|.
name|reflect
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|error
argument_list|(
literal|"Cannot write to read-only field ["
operator|+
name|value
operator|+
literal|"] for type ["
operator|+
name|struct
operator|.
name|name
operator|+
literal|"]."
argument_list|)
argument_list|)
throw|;
block|}
name|after
operator|=
name|field
operator|.
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
else|else
block|{
comment|// TODO: improve this: the isXXX case seems missing???
specifier|final
name|boolean
name|shortcut
init|=
name|struct
operator|.
name|methods
operator|.
name|containsKey
argument_list|(
operator|new
name|Definition
operator|.
name|MethodKey
argument_list|(
literal|"get"
operator|+
name|Character
operator|.
name|toUpperCase
argument_list|(
name|value
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|||
name|struct
operator|.
name|methods
operator|.
name|containsKey
argument_list|(
operator|new
name|Definition
operator|.
name|MethodKey
argument_list|(
literal|"set"
operator|+
name|Character
operator|.
name|toUpperCase
argument_list|(
name|value
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|shortcut
condition|)
block|{
return|return
operator|new
name|LShortcut
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
name|value
argument_list|)
operator|.
name|copy
argument_list|(
name|this
argument_list|)
operator|.
name|analyze
argument_list|(
name|variables
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|EConstant
name|index
init|=
operator|new
name|EConstant
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|index
operator|.
name|analyze
argument_list|(
name|variables
argument_list|)
expr_stmt|;
if|if
condition|(
name|Map
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|before
operator|.
name|clazz
argument_list|)
condition|)
block|{
return|return
operator|new
name|LMapShortcut
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
name|index
argument_list|)
operator|.
name|copy
argument_list|(
name|this
argument_list|)
operator|.
name|analyze
argument_list|(
name|variables
argument_list|)
return|;
block|}
if|if
condition|(
name|List
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|before
operator|.
name|clazz
argument_list|)
condition|)
block|{
return|return
operator|new
name|LListShortcut
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
name|index
argument_list|)
operator|.
name|copy
argument_list|(
name|this
argument_list|)
operator|.
name|analyze
argument_list|(
name|variables
argument_list|)
return|;
block|}
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|error
argument_list|(
literal|"Unknown field ["
operator|+
name|value
operator|+
literal|"] for type ["
operator|+
name|struct
operator|.
name|name
operator|+
literal|"]."
argument_list|)
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|write
name|void
name|write
parameter_list|(
name|MethodWriter
name|adapter
parameter_list|)
block|{
comment|// Do nothing.
block|}
annotation|@
name|Override
DECL|method|load
name|void
name|load
parameter_list|(
name|MethodWriter
name|adapter
parameter_list|)
block|{
if|if
condition|(
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
operator|.
name|isStatic
argument_list|(
name|field
operator|.
name|reflect
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
name|adapter
operator|.
name|getStatic
argument_list|(
name|field
operator|.
name|owner
operator|.
name|type
argument_list|,
name|field
operator|.
name|reflect
operator|.
name|getName
argument_list|()
argument_list|,
name|field
operator|.
name|type
operator|.
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|adapter
operator|.
name|getField
argument_list|(
name|field
operator|.
name|owner
operator|.
name|type
argument_list|,
name|field
operator|.
name|reflect
operator|.
name|getName
argument_list|()
argument_list|,
name|field
operator|.
name|type
operator|.
name|type
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|store
name|void
name|store
parameter_list|(
name|MethodWriter
name|adapter
parameter_list|)
block|{
if|if
condition|(
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
operator|.
name|isStatic
argument_list|(
name|field
operator|.
name|reflect
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
name|adapter
operator|.
name|putStatic
argument_list|(
name|field
operator|.
name|owner
operator|.
name|type
argument_list|,
name|field
operator|.
name|reflect
operator|.
name|getName
argument_list|()
argument_list|,
name|field
operator|.
name|type
operator|.
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|adapter
operator|.
name|putField
argument_list|(
name|field
operator|.
name|owner
operator|.
name|type
argument_list|,
name|field
operator|.
name|reflect
operator|.
name|getName
argument_list|()
argument_list|,
name|field
operator|.
name|type
operator|.
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

