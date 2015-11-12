begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
package|;
end_package

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
name|xcontent
operator|.
name|support
operator|.
name|XContentMapValues
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
name|*
import|;
end_import

begin_comment
comment|/**  * Represents the data and meta data (like id and type) of a single document that is going to be indexed.  */
end_comment

begin_class
DECL|class|Data
specifier|public
specifier|final
class|class
name|Data
block|{
DECL|field|index
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|document
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
decl_stmt|;
DECL|field|modified
specifier|private
name|boolean
name|modified
init|=
literal|false
decl_stmt|;
DECL|method|Data
specifier|public
name|Data
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|document
operator|=
name|document
expr_stmt|;
block|}
DECL|method|Data
specifier|public
name|Data
parameter_list|(
name|Data
name|other
parameter_list|)
block|{
name|this
argument_list|(
name|other
operator|.
name|index
argument_list|,
name|other
operator|.
name|type
argument_list|,
name|other
operator|.
name|id
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|other
operator|.
name|document
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getProperty
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getProperty
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// TODO: we should not rely on any core class, so we should have custom map extract value logic:
comment|// also XContentMapValues has no support to get specific values from arrays, see: https://github.com/elastic/elasticsearch/issues/14324
return|return
operator|(
name|T
operator|)
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
name|path
argument_list|,
name|document
argument_list|)
return|;
block|}
DECL|method|containsProperty
specifier|public
name|boolean
name|containsProperty
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|boolean
name|containsProperty
init|=
literal|false
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
literal|false
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|inner
init|=
name|document
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
name|pathElements
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|inner
operator|==
literal|null
condition|)
block|{
name|containsProperty
operator|=
literal|false
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|i
operator|==
name|pathElements
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|containsProperty
operator|=
name|inner
operator|.
name|containsKey
argument_list|(
name|pathElements
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
block|}
name|Object
name|obj
init|=
name|inner
operator|.
name|get
argument_list|(
name|pathElements
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|Map
condition|)
block|{
name|inner
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|obj
expr_stmt|;
block|}
else|else
block|{
name|inner
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|containsProperty
return|;
block|}
comment|/**      * add `value` to path in document. If path does not exist,      * nested hashmaps will be put in as parent key values until      * leaf key name in path is reached.      *      * @param path The path within the document in dot-notation      * @param value The value to put in for the path key      */
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|path
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|modified
operator|=
literal|true
expr_stmt|;
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
name|String
name|writeKey
init|=
name|pathElements
index|[
name|pathElements
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|inner
init|=
name|document
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
name|pathElements
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|inner
operator|.
name|containsKey
argument_list|(
name|pathElements
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|inner
operator|.
name|put
argument_list|(
name|pathElements
index|[
name|i
index|]
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|inner
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|inner
operator|.
name|get
argument_list|(
name|pathElements
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|inner
operator|.
name|put
argument_list|(
name|writeKey
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|getIndex
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|getDocument
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getDocument
parameter_list|()
block|{
return|return
name|document
return|;
block|}
DECL|method|isModified
specifier|public
name|boolean
name|isModified
parameter_list|()
block|{
return|return
name|modified
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
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
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Data
name|other
init|=
operator|(
name|Data
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|document
argument_list|,
name|other
operator|.
name|document
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|index
argument_list|,
name|other
operator|.
name|index
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|type
argument_list|,
name|other
operator|.
name|type
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|id
argument_list|,
name|other
operator|.
name|id
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
name|hash
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|document
argument_list|)
return|;
block|}
block|}
end_class

end_unit

