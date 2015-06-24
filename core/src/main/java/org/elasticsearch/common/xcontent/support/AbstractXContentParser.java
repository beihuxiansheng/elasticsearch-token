begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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
name|Booleans
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
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AbstractXContentParser
specifier|public
specifier|abstract
class|class
name|AbstractXContentParser
implements|implements
name|XContentParser
block|{
comment|//Currently this is not a setting that can be changed and is a policy
comment|// that relates to how parsing of things like "boost" are done across
comment|// the whole of Elasticsearch (eg if String "1.0" is a valid float).
comment|// The idea behind keeping it as a constant is that we can track
comment|// references to this policy decision throughout the codebase and find
comment|// and change any code that needs to apply an alternative policy.
DECL|field|DEFAULT_NUMBER_COEERCE_POLICY
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_NUMBER_COEERCE_POLICY
init|=
literal|true
decl_stmt|;
DECL|method|checkCoerceString
specifier|private
specifier|static
name|void
name|checkCoerceString
parameter_list|(
name|boolean
name|coeerce
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
operator|!
name|coeerce
condition|)
block|{
comment|//Need to throw type IllegalArgumentException as current catch logic in
comment|//NumberFieldMapper.parseCreateField relies on this for "malformed" value detection
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|clazz
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" value passed as String"
argument_list|)
throw|;
block|}
block|}
comment|// The 3rd party parsers we rely on are known to silently truncate fractions: see
comment|//   http://fasterxml.github.io/jackson-core/javadoc/2.3.0/com/fasterxml/jackson/core/JsonParser.html#getShortValue()
comment|// If this behaviour is flagged as undesirable and any truncation occurs
comment|// then this method is called to trigger the"malformed" handling logic
DECL|method|ensureNumberConversion
name|void
name|ensureNumberConversion
parameter_list|(
name|boolean
name|coerce
parameter_list|,
name|long
name|result
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|coerce
condition|)
block|{
name|double
name|fullVal
init|=
name|doDoubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|!=
name|fullVal
condition|)
block|{
comment|// Need to throw type IllegalArgumentException as current catch
comment|// logic in NumberFieldMapper.parseCreateField relies on this
comment|// for "malformed" value detection
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|fullVal
operator|+
literal|" cannot be converted to "
operator|+
name|clazz
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" without data loss"
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|isBooleanValue
specifier|public
name|boolean
name|isBooleanValue
parameter_list|()
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|currentToken
argument_list|()
condition|)
block|{
case|case
name|VALUE_BOOLEAN
case|:
return|return
literal|true
return|;
case|case
name|VALUE_NUMBER
case|:
name|NumberType
name|numberType
init|=
name|numberType
argument_list|()
decl_stmt|;
return|return
name|numberType
operator|==
name|NumberType
operator|.
name|LONG
operator|||
name|numberType
operator|==
name|NumberType
operator|.
name|INT
return|;
case|case
name|VALUE_STRING
case|:
return|return
name|Booleans
operator|.
name|isBoolean
argument_list|(
name|textCharacters
argument_list|()
argument_list|,
name|textOffset
argument_list|()
argument_list|,
name|textLength
argument_list|()
argument_list|)
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|booleanValue
specifier|public
name|boolean
name|booleanValue
parameter_list|()
throws|throws
name|IOException
block|{
name|Token
name|token
init|=
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_NUMBER
condition|)
block|{
return|return
name|intValue
argument_list|()
operator|!=
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
return|return
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|textCharacters
argument_list|()
argument_list|,
name|textOffset
argument_list|()
argument_list|,
name|textLength
argument_list|()
argument_list|,
literal|false
comment|/* irrelevant */
argument_list|)
return|;
block|}
return|return
name|doBooleanValue
argument_list|()
return|;
block|}
DECL|method|doBooleanValue
specifier|protected
specifier|abstract
name|boolean
name|doBooleanValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|shortValue
specifier|public
name|short
name|shortValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|shortValue
argument_list|(
name|DEFAULT_NUMBER_COEERCE_POLICY
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shortValue
specifier|public
name|short
name|shortValue
parameter_list|(
name|boolean
name|coerce
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
name|token
init|=
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|checkCoerceString
argument_list|(
name|coerce
argument_list|,
name|Short
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|Short
operator|.
name|parseShort
argument_list|(
name|text
argument_list|()
argument_list|)
return|;
block|}
name|short
name|result
init|=
name|doShortValue
argument_list|()
decl_stmt|;
name|ensureNumberConversion
argument_list|(
name|coerce
argument_list|,
name|result
argument_list|,
name|Short
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|doShortValue
specifier|protected
specifier|abstract
name|short
name|doShortValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|intValue
specifier|public
name|int
name|intValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|intValue
argument_list|(
name|DEFAULT_NUMBER_COEERCE_POLICY
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|intValue
specifier|public
name|int
name|intValue
parameter_list|(
name|boolean
name|coerce
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
name|token
init|=
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|checkCoerceString
argument_list|(
name|coerce
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|text
argument_list|()
argument_list|)
return|;
block|}
name|int
name|result
init|=
name|doIntValue
argument_list|()
decl_stmt|;
name|ensureNumberConversion
argument_list|(
name|coerce
argument_list|,
name|result
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|doIntValue
specifier|protected
specifier|abstract
name|int
name|doIntValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|longValue
specifier|public
name|long
name|longValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|longValue
argument_list|(
name|DEFAULT_NUMBER_COEERCE_POLICY
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|longValue
specifier|public
name|long
name|longValue
parameter_list|(
name|boolean
name|coerce
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
name|token
init|=
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|checkCoerceString
argument_list|(
name|coerce
argument_list|,
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|text
argument_list|()
argument_list|)
return|;
block|}
name|long
name|result
init|=
name|doLongValue
argument_list|()
decl_stmt|;
name|ensureNumberConversion
argument_list|(
name|coerce
argument_list|,
name|result
argument_list|,
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|doLongValue
specifier|protected
specifier|abstract
name|long
name|doLongValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|floatValue
specifier|public
name|float
name|floatValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|floatValue
argument_list|(
name|DEFAULT_NUMBER_COEERCE_POLICY
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|floatValue
specifier|public
name|float
name|floatValue
parameter_list|(
name|boolean
name|coerce
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
name|token
init|=
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|checkCoerceString
argument_list|(
name|coerce
argument_list|,
name|Float
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|text
argument_list|()
argument_list|)
return|;
block|}
return|return
name|doFloatValue
argument_list|()
return|;
block|}
DECL|method|doFloatValue
specifier|protected
specifier|abstract
name|float
name|doFloatValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|doubleValue
specifier|public
name|double
name|doubleValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|doubleValue
argument_list|(
name|DEFAULT_NUMBER_COEERCE_POLICY
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doubleValue
specifier|public
name|double
name|doubleValue
parameter_list|(
name|boolean
name|coerce
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
name|token
init|=
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|checkCoerceString
argument_list|(
name|coerce
argument_list|,
name|Double
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|text
argument_list|()
argument_list|)
return|;
block|}
return|return
name|doDoubleValue
argument_list|()
return|;
block|}
DECL|method|doDoubleValue
specifier|protected
specifier|abstract
name|double
name|doDoubleValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|textOrNull
specifier|public
name|String
name|textOrNull
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|currentToken
argument_list|()
operator|==
name|Token
operator|.
name|VALUE_NULL
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|text
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|utf8BytesOrNull
specifier|public
name|BytesRef
name|utf8BytesOrNull
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|currentToken
argument_list|()
operator|==
name|Token
operator|.
name|VALUE_NULL
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|utf8Bytes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|map
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|readMap
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|mapOrdered
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapOrdered
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|readOrderedMap
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|list
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|list
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|readList
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|listOrderedMap
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|listOrderedMap
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|readListOrderedMap
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|interface|MapFactory
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
argument_list|<>
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|ORDERED_MAP_FACTORY
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
argument_list|<>
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|method|readMap
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
DECL|method|readList
specifier|static
name|List
argument_list|<
name|Object
argument_list|>
name|readList
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readList
argument_list|(
name|parser
argument_list|,
name|SIMPLE_MAP_FACTORY
argument_list|)
return|;
block|}
DECL|method|readListOrderedMap
specifier|static
name|List
argument_list|<
name|Object
argument_list|>
name|readListOrderedMap
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readList
argument_list|(
name|parser
argument_list|,
name|ORDERED_MAP_FACTORY
argument_list|)
return|;
block|}
DECL|method|readMap
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
name|token
init|=
name|parser
operator|.
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|token
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
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|;
name|token
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
name|token
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
name|token
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
argument_list|<>
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
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
name|token
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
DECL|method|readValue
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
name|token
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|token
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
name|token
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
name|token
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
name|token
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
name|token
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
name|token
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
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_EMBEDDED_OBJECT
condition|)
block|{
return|return
name|parser
operator|.
name|binaryValue
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

