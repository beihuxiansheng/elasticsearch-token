begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.core
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
package|;
end_package

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
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Holds a value. If the value is requested a copy is made and optionally template snippets are resolved too.  */
end_comment

begin_interface
DECL|interface|ValueSource
specifier|public
interface|interface
name|ValueSource
block|{
comment|/**      * Returns a copy of the value this ValueSource holds and resolves templates if there're any.      *      * For immutable values only a copy of the reference to the value is made.      *      * @param model The model to be used when resolving any templates      * @return copy of the wrapped value      */
DECL|method|copyAndResolve
name|Object
name|copyAndResolve
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|model
parameter_list|)
function_decl|;
DECL|method|wrap
specifier|static
name|ValueSource
name|wrap
parameter_list|(
name|Object
name|value
parameter_list|,
name|TemplateService
name|templateService
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Map
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|mapValue
init|=
operator|(
name|Map
operator|)
name|value
decl_stmt|;
name|Map
argument_list|<
name|ValueSource
argument_list|,
name|ValueSource
argument_list|>
name|valueTypeMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|mapValue
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|mapValue
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|valueTypeMap
operator|.
name|put
argument_list|(
name|wrap
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|templateService
argument_list|)
argument_list|,
name|wrap
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|templateService
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MapValue
argument_list|(
name|valueTypeMap
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|List
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Object
argument_list|>
name|listValue
init|=
operator|(
name|List
operator|)
name|value
decl_stmt|;
name|List
argument_list|<
name|ValueSource
argument_list|>
name|valueSourceList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|listValue
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|item
range|:
name|listValue
control|)
block|{
name|valueSourceList
operator|.
name|add
argument_list|(
name|wrap
argument_list|(
name|item
argument_list|,
name|templateService
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ListValue
argument_list|(
name|valueSourceList
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|instanceof
name|Number
operator|||
name|value
operator|instanceof
name|Boolean
condition|)
block|{
return|return
operator|new
name|ObjectValue
argument_list|(
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
return|return
operator|new
name|TemplatedValue
argument_list|(
name|templateService
operator|.
name|compile
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unexpected value type ["
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|class|MapValue
specifier|final
class|class
name|MapValue
implements|implements
name|ValueSource
block|{
DECL|field|map
specifier|private
specifier|final
name|Map
argument_list|<
name|ValueSource
argument_list|,
name|ValueSource
argument_list|>
name|map
decl_stmt|;
DECL|method|MapValue
name|MapValue
parameter_list|(
name|Map
argument_list|<
name|ValueSource
argument_list|,
name|ValueSource
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyAndResolve
specifier|public
name|Object
name|copyAndResolve
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|model
parameter_list|)
block|{
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|copy
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ValueSource
argument_list|,
name|ValueSource
argument_list|>
name|entry
range|:
name|this
operator|.
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|copy
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|copyAndResolve
argument_list|(
name|model
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|copyAndResolve
argument_list|(
name|model
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|copy
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|MapValue
name|mapValue
init|=
operator|(
name|MapValue
operator|)
name|o
decl_stmt|;
return|return
name|map
operator|.
name|equals
argument_list|(
name|mapValue
operator|.
name|map
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
return|return
name|map
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
DECL|class|ListValue
specifier|final
class|class
name|ListValue
implements|implements
name|ValueSource
block|{
DECL|field|values
specifier|private
specifier|final
name|List
argument_list|<
name|ValueSource
argument_list|>
name|values
decl_stmt|;
DECL|method|ListValue
name|ListValue
parameter_list|(
name|List
argument_list|<
name|ValueSource
argument_list|>
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyAndResolve
specifier|public
name|Object
name|copyAndResolve
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|model
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|copy
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ValueSource
name|value
range|:
name|values
control|)
block|{
name|copy
operator|.
name|add
argument_list|(
name|value
operator|.
name|copyAndResolve
argument_list|(
name|model
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|copy
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ListValue
name|listValue
init|=
operator|(
name|ListValue
operator|)
name|o
decl_stmt|;
return|return
name|values
operator|.
name|equals
argument_list|(
name|listValue
operator|.
name|values
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
return|return
name|values
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
DECL|class|ObjectValue
specifier|final
class|class
name|ObjectValue
implements|implements
name|ValueSource
block|{
DECL|field|value
specifier|private
specifier|final
name|Object
name|value
decl_stmt|;
DECL|method|ObjectValue
name|ObjectValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyAndResolve
specifier|public
name|Object
name|copyAndResolve
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|model
parameter_list|)
block|{
return|return
name|value
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ObjectValue
name|objectValue
init|=
operator|(
name|ObjectValue
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|value
argument_list|,
name|objectValue
operator|.
name|value
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
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
DECL|class|TemplatedValue
specifier|final
class|class
name|TemplatedValue
implements|implements
name|ValueSource
block|{
DECL|field|template
specifier|private
specifier|final
name|TemplateService
operator|.
name|Template
name|template
decl_stmt|;
DECL|method|TemplatedValue
name|TemplatedValue
parameter_list|(
name|TemplateService
operator|.
name|Template
name|template
parameter_list|)
block|{
name|this
operator|.
name|template
operator|=
name|template
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyAndResolve
specifier|public
name|Object
name|copyAndResolve
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|model
parameter_list|)
block|{
return|return
name|template
operator|.
name|execute
argument_list|(
name|model
argument_list|)
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|TemplatedValue
name|templatedValue
init|=
operator|(
name|TemplatedValue
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|template
operator|.
name|getKey
argument_list|()
argument_list|,
name|templatedValue
operator|.
name|template
operator|.
name|getKey
argument_list|()
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
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|template
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_interface

end_unit
