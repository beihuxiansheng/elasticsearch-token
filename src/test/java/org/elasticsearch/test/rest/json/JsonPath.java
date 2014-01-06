begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elasticsearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|json
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
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
comment|/**  * Holds a json object and allows to extract specific values from it  */
end_comment

begin_class
DECL|class|JsonPath
specifier|public
class|class
name|JsonPath
block|{
DECL|field|json
specifier|final
name|String
name|json
decl_stmt|;
DECL|field|jsonMap
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonMap
decl_stmt|;
DECL|method|JsonPath
specifier|public
name|JsonPath
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|json
operator|=
name|json
expr_stmt|;
name|this
operator|.
name|jsonMap
operator|=
name|convertToMap
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
DECL|method|convertToMap
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|convertToMap
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|json
argument_list|)
operator|.
name|mapOrderedAndClose
argument_list|()
return|;
block|}
comment|/**      * Returns the object corresponding to the provided path if present, null otherwise      */
DECL|method|evaluate
specifier|public
name|Object
name|evaluate
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
index|[]
name|parts
init|=
name|parsePath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Object
name|object
init|=
name|jsonMap
decl_stmt|;
for|for
control|(
name|String
name|part
range|:
name|parts
control|)
block|{
name|object
operator|=
name|evaluate
argument_list|(
name|part
argument_list|,
name|object
argument_list|)
expr_stmt|;
if|if
condition|(
name|object
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|object
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|evaluate
specifier|private
name|Object
name|evaluate
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|object
operator|instanceof
name|Map
condition|)
block|{
return|return
operator|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|object
operator|)
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
if|if
condition|(
name|object
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
name|object
decl_stmt|;
try|try
block|{
return|return
name|list
operator|.
name|get
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"element was a list, but ["
operator|+
name|key
operator|+
literal|"] was not numeric"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"element was a list with "
operator|+
name|list
operator|.
name|size
argument_list|()
operator|+
literal|" elements, but ["
operator|+
name|key
operator|+
literal|"] was out of bounds"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no object found for ["
operator|+
name|key
operator|+
literal|"] within object of class ["
operator|+
name|object
operator|.
name|getClass
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|method|parsePath
specifier|private
name|String
index|[]
name|parsePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|StringBuilder
name|current
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|escape
init|=
literal|false
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
name|path
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|path
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
name|escape
operator|=
literal|true
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|c
operator|==
literal|'.'
condition|)
block|{
if|if
condition|(
name|escape
condition|)
block|{
name|escape
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|current
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|current
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|current
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
block|}
name|current
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|current
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|current
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

