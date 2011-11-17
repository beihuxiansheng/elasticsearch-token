begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
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
name|XContentGenerator
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
name|XContentParser
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|LinkedHashMap
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|XContentMapConverter
specifier|public
class|class
name|XContentMapConverter
block|{
DECL|interface|MapFactory
specifier|public
specifier|static
interface|interface
name|MapFactory
block|{
DECL|method|newMap
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|newMap
parameter_list|()
function_decl|;
block|}
DECL|field|SIMPLE_MAP_FACTORY
specifier|public
specifier|static
specifier|final
name|MapFactory
name|SIMPLE_MAP_FACTORY
init|=
operator|new
name|MapFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|newMap
parameter_list|()
block|{
return|return
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|ORDERED_MAP_FACTORY
specifier|public
specifier|static
specifier|final
name|MapFactory
name|ORDERED_MAP_FACTORY
init|=
operator|new
name|MapFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|newMap
parameter_list|()
block|{
return|return
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|method|readMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readMap
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readMap
argument_list|(
name|parser
argument_list|,
name|SIMPLE_MAP_FACTORY
argument_list|)
return|;
block|}
DECL|method|readOrderedMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readOrderedMap
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readMap
argument_list|(
name|parser
argument_list|,
name|ORDERED_MAP_FACTORY
argument_list|)
return|;
block|}
DECL|method|readMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readMap
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|MapFactory
name|mapFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|mapFactory
operator|.
name|newMap
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|t
init|=
name|parser
operator|.
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
name|t
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|t
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|t
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
for|for
control|(
init|;
name|t
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|;
name|t
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
control|)
block|{
comment|// Must point to field name
name|String
name|fieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
comment|// And then the value...
name|t
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|Object
name|value
init|=
name|readValue
argument_list|(
name|parser
argument_list|,
name|mapFactory
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
DECL|method|readList
specifier|private
specifier|static
name|List
argument_list|<
name|Object
argument_list|>
name|readList
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|MapFactory
name|mapFactory
parameter_list|,
name|XContentParser
operator|.
name|Token
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|t
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|readValue
argument_list|(
name|parser
argument_list|,
name|mapFactory
argument_list|,
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
DECL|method|readValue
specifier|private
specifier|static
name|Object
name|readValue
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|MapFactory
name|mapFactory
parameter_list|,
name|XContentParser
operator|.
name|Token
name|t
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|t
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NULL
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|t
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
return|return
name|parser
operator|.
name|text
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|t
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NUMBER
condition|)
block|{
name|XContentParser
operator|.
name|NumberType
name|numberType
init|=
name|parser
operator|.
name|numberType
argument_list|()
decl_stmt|;
if|if
condition|(
name|numberType
operator|==
name|XContentParser
operator|.
name|NumberType
operator|.
name|INT
condition|)
block|{
return|return
name|parser
operator|.
name|intValue
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|numberType
operator|==
name|XContentParser
operator|.
name|NumberType
operator|.
name|LONG
condition|)
block|{
return|return
name|parser
operator|.
name|longValue
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|numberType
operator|==
name|XContentParser
operator|.
name|NumberType
operator|.
name|FLOAT
condition|)
block|{
return|return
name|parser
operator|.
name|floatValue
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|numberType
operator|==
name|XContentParser
operator|.
name|NumberType
operator|.
name|DOUBLE
condition|)
block|{
return|return
name|parser
operator|.
name|doubleValue
argument_list|()
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|t
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_BOOLEAN
condition|)
block|{
return|return
name|parser
operator|.
name|booleanValue
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|t
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
return|return
name|readMap
argument_list|(
name|parser
argument_list|,
name|mapFactory
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|t
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
return|return
name|readList
argument_list|(
name|parser
argument_list|,
name|mapFactory
argument_list|,
name|t
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|writeMap
specifier|public
specifier|static
name|void
name|writeMap
parameter_list|(
name|XContentGenerator
name|gen
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
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
name|gen
operator|.
name|writeFieldName
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|gen
operator|.
name|writeNull
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|writeValue
argument_list|(
name|gen
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|gen
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
DECL|method|writeIterable
specifier|private
specifier|static
name|void
name|writeIterable
parameter_list|(
name|XContentGenerator
name|gen
parameter_list|,
name|Iterable
name|iterable
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|iterable
control|)
block|{
name|writeValue
argument_list|(
name|gen
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|gen
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
DECL|method|writeObjectArray
specifier|private
specifier|static
name|void
name|writeObjectArray
parameter_list|(
name|XContentGenerator
name|gen
parameter_list|,
name|Object
index|[]
name|array
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|array
control|)
block|{
name|writeValue
argument_list|(
name|gen
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|gen
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
DECL|method|writeValue
specifier|private
specifier|static
name|void
name|writeValue
parameter_list|(
name|XContentGenerator
name|gen
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|Class
name|type
init|=
name|value
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|String
operator|.
name|class
condition|)
block|{
name|gen
operator|.
name|writeString
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Integer
operator|.
name|class
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Long
operator|.
name|class
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
operator|(
operator|(
name|Long
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Float
operator|.
name|class
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
operator|(
operator|(
name|Float
operator|)
name|value
operator|)
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Double
operator|.
name|class
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
operator|(
operator|(
name|Double
operator|)
name|value
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Short
operator|.
name|class
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
operator|(
operator|(
name|Short
operator|)
name|value
operator|)
operator|.
name|shortValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Boolean
operator|.
name|class
condition|)
block|{
name|gen
operator|.
name|writeBoolean
argument_list|(
operator|(
operator|(
name|Boolean
operator|)
name|value
operator|)
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Map
condition|)
block|{
name|writeMap
argument_list|(
name|gen
argument_list|,
operator|(
name|Map
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Iterable
condition|)
block|{
name|writeIterable
argument_list|(
name|gen
argument_list|,
operator|(
name|Iterable
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Object
index|[]
condition|)
block|{
name|writeObjectArray
argument_list|(
name|gen
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|byte
index|[]
operator|.
name|class
condition|)
block|{
name|gen
operator|.
name|writeBinary
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Date
condition|)
block|{
name|gen
operator|.
name|writeString
argument_list|(
name|XContentBuilder
operator|.
name|defaultDatePrinter
operator|.
name|print
argument_list|(
operator|(
operator|(
name|Date
operator|)
name|value
operator|)
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|gen
operator|.
name|writeString
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

