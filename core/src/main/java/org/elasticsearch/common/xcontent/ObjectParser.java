begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
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
name|ParseField
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
name|ParseFieldMatcher
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
name|ParseFieldMatcherSupplier
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
name|ParsingException
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
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|function
operator|.
name|BiConsumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiFunction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_BOOLEAN
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_EMBEDDED_OBJECT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NULL
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NUMBER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
import|;
end_import

begin_comment
comment|/**  * A declarative, stateless parser that turns XContent into setter calls. A single parser should be defined for each object being parsed,  * nested elements can be added via {@link #declareObject(BiConsumer, BiFunction, ParseField)} which should be satisfied where possible by  * passing another instance of {@link ObjectParser}, this one customized for that Object.  *<p>  * This class works well for object that do have a constructor argument or that can be built using information available from earlier in the  * XContent. For objects that have constructors with required arguments that are specified on the same level as other fields see  * {@link ConstructingObjectParser}.  *</p>  *<p>  * Instances of {@link ObjectParser} should be setup by declaring a constant field for the parsers and declaring all fields in a static  * block just below the creation of the parser. Like this:  *</p>  *<pre>{@code  *   private static final ObjectParser<Thing, SomeContext> PARSER = new ObjectParser<>("thing", Thing::new));  *   static {  *       PARSER.declareInt(Thing::setMineral, new ParseField("mineral"));  *       PARSER.declareInt(Thing::setFruit, new ParseField("fruit"));  *   }  * }</pre>  * It's highly recommended to use the high level declare methods like {@link #declareString(BiConsumer, ParseField)} instead of  * {@link #declareField} which can be used to implement exceptional parsing operations not covered by the high level methods.  */
end_comment

begin_class
DECL|class|ObjectParser
specifier|public
specifier|final
class|class
name|ObjectParser
parameter_list|<
name|Value
parameter_list|,
name|Context
extends|extends
name|ParseFieldMatcherSupplier
parameter_list|>
extends|extends
name|AbstractObjectParser
argument_list|<
name|Value
argument_list|,
name|Context
argument_list|>
block|{
comment|/**      * Adapts an array (or varags) setter into a list setter.      */
DECL|method|fromList
specifier|public
specifier|static
parameter_list|<
name|Value
parameter_list|,
name|ElementValue
parameter_list|>
name|BiConsumer
argument_list|<
name|Value
argument_list|,
name|List
argument_list|<
name|ElementValue
argument_list|>
argument_list|>
name|fromList
parameter_list|(
name|Class
argument_list|<
name|ElementValue
argument_list|>
name|c
parameter_list|,
name|BiConsumer
argument_list|<
name|Value
argument_list|,
name|ElementValue
index|[]
argument_list|>
name|consumer
parameter_list|)
block|{
return|return
parameter_list|(
name|Value
name|v
parameter_list|,
name|List
argument_list|<
name|ElementValue
argument_list|>
name|l
parameter_list|)
lambda|->
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|ElementValue
index|[]
name|array
init|=
operator|(
name|ElementValue
index|[]
operator|)
name|Array
operator|.
name|newInstance
argument_list|(
name|c
argument_list|,
name|l
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|v
argument_list|,
name|l
operator|.
name|toArray
argument_list|(
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|;
block|}
DECL|field|fieldParserMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FieldParser
argument_list|>
name|fieldParserMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|valueSupplier
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Value
argument_list|>
name|valueSupplier
decl_stmt|;
comment|/**      * Creates a new ObjectParser instance with a name. This name is used to reference the parser in exceptions and messages.      */
DECL|method|ObjectParser
specifier|public
name|ObjectParser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new ObjectParser instance which a name.      * @param name the parsers name, used to reference the parser in exceptions and messages.      * @param valueSupplier a supplier that creates a new Value instance used when the parser is used as an inner object parser.      */
DECL|method|ObjectParser
specifier|public
name|ObjectParser
parameter_list|(
name|String
name|name
parameter_list|,
name|Supplier
argument_list|<
name|Value
argument_list|>
name|valueSupplier
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|valueSupplier
operator|=
name|valueSupplier
expr_stmt|;
block|}
comment|/**      * Parses a Value from the given {@link XContentParser}      * @param parser the parser to build a value from      * @param context must at least provide a {@link ParseFieldMatcher}      * @return a new value instance drawn from the provided value supplier on {@link #ObjectParser(String, Supplier)}      * @throws IOException if an IOException occurs.      */
DECL|method|parse
specifier|public
name|Value
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|valueSupplier
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"valueSupplier is not set"
argument_list|)
throw|;
block|}
return|return
name|parse
argument_list|(
name|parser
argument_list|,
name|valueSupplier
operator|.
name|get
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
block|}
comment|/**      * Parses a Value from the given {@link XContentParser}      * @param parser the parser to build a value from      * @param value the value to fill from the parser      * @param context a context that is passed along to all declared field parsers      * @return the parsed value      * @throws IOException if an IOException occurs.      */
DECL|method|parse
specifier|public
name|Value
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|Value
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
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
name|currentToken
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"["
operator|+
name|name
operator|+
literal|"] Expected START_OBJECT but was: "
operator|+
name|token
argument_list|)
throw|;
block|}
block|}
name|FieldParser
argument_list|<
name|Value
argument_list|>
name|fieldParser
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
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
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
name|fieldParser
operator|=
name|getParser
argument_list|(
name|currentFieldName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|currentFieldName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"["
operator|+
name|name
operator|+
literal|"] no field found"
argument_list|)
throw|;
block|}
assert|assert
name|fieldParser
operator|!=
literal|null
assert|;
name|fieldParser
operator|.
name|assertSupports
argument_list|(
name|name
argument_list|,
name|token
argument_list|,
name|currentFieldName
argument_list|,
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|)
expr_stmt|;
name|parseSub
argument_list|(
name|parser
argument_list|,
name|fieldParser
argument_list|,
name|currentFieldName
argument_list|,
name|value
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|fieldParser
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|apply
specifier|public
name|Value
name|apply
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
if|if
condition|(
name|valueSupplier
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"valueSupplier is not set"
argument_list|)
throw|;
block|}
try|try
block|{
return|return
name|parse
argument_list|(
name|parser
argument_list|,
name|valueSupplier
operator|.
name|get
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"["
operator|+
name|name
operator|+
literal|"] failed to parse object"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|interface|Parser
specifier|public
interface|interface
name|Parser
parameter_list|<
name|Value
parameter_list|,
name|Context
parameter_list|>
block|{
DECL|method|parse
name|void
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|Value
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|method|declareField
specifier|public
name|void
name|declareField
parameter_list|(
name|Parser
argument_list|<
name|Value
argument_list|,
name|Context
argument_list|>
name|p
parameter_list|,
name|ParseField
name|parseField
parameter_list|,
name|ValueType
name|type
parameter_list|)
block|{
name|FieldParser
name|fieldParser
init|=
operator|new
name|FieldParser
argument_list|(
name|p
argument_list|,
name|type
operator|.
name|supportedTokens
argument_list|()
argument_list|,
name|parseField
argument_list|,
name|type
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|fieldValue
range|:
name|parseField
operator|.
name|getAllNamesIncludedDeprecated
argument_list|()
control|)
block|{
name|fieldParserMap
operator|.
name|putIfAbsent
argument_list|(
name|fieldValue
argument_list|,
name|fieldParser
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|declareField
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|declareField
parameter_list|(
name|BiConsumer
argument_list|<
name|Value
argument_list|,
name|T
argument_list|>
name|consumer
parameter_list|,
name|ContextParser
argument_list|<
name|Context
argument_list|,
name|T
argument_list|>
name|parser
parameter_list|,
name|ParseField
name|parseField
parameter_list|,
name|ValueType
name|type
parameter_list|)
block|{
name|declareField
argument_list|(
parameter_list|(
name|p
parameter_list|,
name|v
parameter_list|,
name|c
parameter_list|)
lambda|->
name|consumer
operator|.
name|accept
argument_list|(
name|v
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
name|p
argument_list|,
name|c
argument_list|)
argument_list|)
argument_list|,
name|parseField
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
DECL|method|declareObjectOrDefault
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|declareObjectOrDefault
parameter_list|(
name|BiConsumer
argument_list|<
name|Value
argument_list|,
name|T
argument_list|>
name|consumer
parameter_list|,
name|BiFunction
argument_list|<
name|XContentParser
argument_list|,
name|Context
argument_list|,
name|T
argument_list|>
name|objectParser
parameter_list|,
name|Supplier
argument_list|<
name|T
argument_list|>
name|defaultValue
parameter_list|,
name|ParseField
name|field
parameter_list|)
block|{
name|declareField
argument_list|(
parameter_list|(
name|p
parameter_list|,
name|v
parameter_list|,
name|c
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|p
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_BOOLEAN
condition|)
block|{
if|if
condition|(
name|p
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|consumer
operator|.
name|accept
argument_list|(
name|v
argument_list|,
name|defaultValue
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|consumer
operator|.
name|accept
argument_list|(
name|v
argument_list|,
name|objectParser
operator|.
name|apply
argument_list|(
name|p
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|field
argument_list|,
name|ValueType
operator|.
name|OBJECT_OR_BOOLEAN
argument_list|)
expr_stmt|;
block|}
comment|/**      * Declares named objects in the style of highlighting's field element. These are usually named inside and object like this:      *<pre><code>      * {      *   "highlight": {      *     "fields": {&lt;------ this one      *       "title": {},      *       "body": {},      *       "category": {}      *     }      *   }      * }      *</code></pre>      * but, when order is important, some may be written this way:      *<pre><code>      * {      *   "highlight": {      *     "fields": [&lt;------ this one      *       {"title": {}},      *       {"body": {}},      *       {"category": {}}      *     ]      *   }      * }      *</code></pre>      * This is because json doesn't enforce ordering. Elasticsearch reads it in the order sent but tools that generate json are free to put      * object members in an unordered Map, jumbling them. Thus, if you care about order you can send the object in the second way.      *      * See NamedObjectHolder in ObjectParserTests for examples of how to invoke this.      *      * @param consumer sets the values once they have been parsed      * @param namedObjectParser parses each named object      * @param orderedModeCallback called when the named object is parsed using the "ordered" mode (the array of objects)      * @param field the field to parse      */
DECL|method|declareNamedObjects
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|declareNamedObjects
parameter_list|(
name|BiConsumer
argument_list|<
name|Value
argument_list|,
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
name|consumer
parameter_list|,
name|NamedObjectParser
argument_list|<
name|T
argument_list|,
name|Context
argument_list|>
name|namedObjectParser
parameter_list|,
name|Consumer
argument_list|<
name|Value
argument_list|>
name|orderedModeCallback
parameter_list|,
name|ParseField
name|field
parameter_list|)
block|{
comment|// This creates and parses the named object
name|BiFunction
argument_list|<
name|XContentParser
argument_list|,
name|Context
argument_list|,
name|T
argument_list|>
name|objectParser
init|=
parameter_list|(
name|XContentParser
name|p
parameter_list|,
name|Context
name|c
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|p
operator|.
name|currentToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|p
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"["
operator|+
name|field
operator|+
literal|"] can be a single object with any number of "
operator|+
literal|"fields or an array where each entry is an object with a single field"
argument_list|)
throw|;
block|}
comment|// This messy exception nesting has the nice side effect of telling the use which field failed to parse
try|try
block|{
name|String
name|name
init|=
name|p
operator|.
name|currentName
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|namedObjectParser
operator|.
name|parse
argument_list|(
name|p
argument_list|,
name|c
argument_list|,
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|p
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"["
operator|+
name|field
operator|+
literal|"] failed to parse field ["
operator|+
name|name
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|p
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"["
operator|+
name|field
operator|+
literal|"] error while parsing"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|declareField
argument_list|(
parameter_list|(
name|XContentParser
name|p
parameter_list|,
name|Value
name|v
parameter_list|,
name|Context
name|c
parameter_list|)
lambda|->
block|{
name|List
argument_list|<
name|T
argument_list|>
name|fields
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
if|if
condition|(
name|p
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
comment|// Fields are just named entries in a single object
while|while
condition|(
operator|(
name|token
operator|=
name|p
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|objectParser
operator|.
name|apply
argument_list|(
name|p
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
comment|// Fields are objects in an array. Each object contains a named field.
name|orderedModeCallback
operator|.
name|accept
argument_list|(
name|v
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|p
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
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|p
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"["
operator|+
name|field
operator|+
literal|"] can be a single object with any number of "
operator|+
literal|"fields or an array where each entry is an object with a single field"
argument_list|)
throw|;
block|}
name|p
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// Move to the first field in the object
name|fields
operator|.
name|add
argument_list|(
name|objectParser
operator|.
name|apply
argument_list|(
name|p
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// Move past the object, should be back to into the array
if|if
condition|(
name|p
operator|.
name|currentToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|p
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"["
operator|+
name|field
operator|+
literal|"] can be a single object with any number of "
operator|+
literal|"fields or an array where each entry is an object with a single field"
argument_list|)
throw|;
block|}
block|}
block|}
name|consumer
operator|.
name|accept
argument_list|(
name|v
argument_list|,
name|fields
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|field
argument_list|,
name|ValueType
operator|.
name|OBJECT_ARRAY
argument_list|)
expr_stmt|;
block|}
comment|/**      * Declares named objects in the style of aggregations. These are named inside and object like this:      *<pre><code>      * {      *   "aggregations": {      *     "name_1": { "aggregation_type": {} },      *     "name_2": { "aggregation_type": {} },      *     "name_3": { "aggregation_type": {} }      *     }      *   }      * }      *</code></pre>      * Unlike the other version of this method, "ordered" mode (arrays of objects) is not supported.      *      * See NamedObjectHolder in ObjectParserTests for examples of how to invoke this.      *      * @param consumer sets the values once they have been parsed      * @param namedObjectParser parses each named object      * @param field the field to parse      */
DECL|method|declareNamedObjects
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|declareNamedObjects
parameter_list|(
name|BiConsumer
argument_list|<
name|Value
argument_list|,
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
name|consumer
parameter_list|,
name|NamedObjectParser
argument_list|<
name|T
argument_list|,
name|Context
argument_list|>
name|namedObjectParser
parameter_list|,
name|ParseField
name|field
parameter_list|)
block|{
name|Consumer
argument_list|<
name|Value
argument_list|>
name|orderedModeCallback
init|=
parameter_list|(
name|v
parameter_list|)
lambda|->
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|field
operator|+
literal|"] doesn't support arrays. Use a single object with multiple fields."
argument_list|)
throw|;
block|}
decl_stmt|;
name|declareNamedObjects
argument_list|(
name|consumer
argument_list|,
name|namedObjectParser
argument_list|,
name|orderedModeCallback
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
comment|/**      * Functional interface for instantiating and parsing named objects. See ObjectParserTests#NamedObject for the canonical way to      * implement this for objects that themselves have a parser.      */
annotation|@
name|FunctionalInterface
DECL|interface|NamedObjectParser
specifier|public
interface|interface
name|NamedObjectParser
parameter_list|<
name|T
parameter_list|,
name|Context
parameter_list|>
block|{
DECL|method|parse
name|T
name|parse
parameter_list|(
name|XContentParser
name|p
parameter_list|,
name|Context
name|c
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**      * Get the name of the parser.      */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|parseArray
specifier|private
name|void
name|parseArray
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|FieldParser
argument_list|<
name|Value
argument_list|>
name|fieldParser
parameter_list|,
name|String
name|currentFieldName
parameter_list|,
name|Value
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
operator|:
literal|"Token was: "
operator|+
name|parser
operator|.
name|currentToken
argument_list|()
assert|;
name|parseValue
argument_list|(
name|parser
argument_list|,
name|fieldParser
argument_list|,
name|currentFieldName
argument_list|,
name|value
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|parseValue
specifier|private
name|void
name|parseValue
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|FieldParser
argument_list|<
name|Value
argument_list|>
name|fieldParser
parameter_list|,
name|String
name|currentFieldName
parameter_list|,
name|Value
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|fieldParser
operator|.
name|parser
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|value
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"["
operator|+
name|name
operator|+
literal|"] failed to parse field ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|parseSub
specifier|private
name|void
name|parseSub
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|FieldParser
argument_list|<
name|Value
argument_list|>
name|fieldParser
parameter_list|,
name|String
name|currentFieldName
parameter_list|,
name|Value
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
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
switch|switch
condition|(
name|token
condition|)
block|{
case|case
name|START_OBJECT
case|:
name|parseValue
argument_list|(
name|parser
argument_list|,
name|fieldParser
argument_list|,
name|currentFieldName
argument_list|,
name|value
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
case|case
name|START_ARRAY
case|:
name|parseArray
argument_list|(
name|parser
argument_list|,
name|fieldParser
argument_list|,
name|currentFieldName
argument_list|,
name|value
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
case|case
name|END_OBJECT
case|:
case|case
name|END_ARRAY
case|:
case|case
name|FIELD_NAME
case|:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"["
operator|+
name|name
operator|+
literal|"]"
operator|+
name|token
operator|+
literal|" is unexpected"
argument_list|)
throw|;
case|case
name|VALUE_STRING
case|:
case|case
name|VALUE_NUMBER
case|:
case|case
name|VALUE_BOOLEAN
case|:
case|case
name|VALUE_EMBEDDED_OBJECT
case|:
case|case
name|VALUE_NULL
case|:
name|parseValue
argument_list|(
name|parser
argument_list|,
name|fieldParser
argument_list|,
name|currentFieldName
argument_list|,
name|value
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getParser
specifier|private
name|FieldParser
name|getParser
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|FieldParser
argument_list|<
name|Value
argument_list|>
name|parser
init|=
name|fieldParserMap
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|name
operator|+
literal|"] unknown field ["
operator|+
name|fieldName
operator|+
literal|"], parser not found"
argument_list|)
throw|;
block|}
return|return
name|parser
return|;
block|}
DECL|class|FieldParser
specifier|public
specifier|static
class|class
name|FieldParser
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|parser
specifier|private
specifier|final
name|Parser
name|parser
decl_stmt|;
DECL|field|supportedTokens
specifier|private
specifier|final
name|EnumSet
argument_list|<
name|XContentParser
operator|.
name|Token
argument_list|>
name|supportedTokens
decl_stmt|;
DECL|field|parseField
specifier|private
specifier|final
name|ParseField
name|parseField
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|ValueType
name|type
decl_stmt|;
DECL|method|FieldParser
specifier|public
name|FieldParser
parameter_list|(
name|Parser
name|parser
parameter_list|,
name|EnumSet
argument_list|<
name|XContentParser
operator|.
name|Token
argument_list|>
name|supportedTokens
parameter_list|,
name|ParseField
name|parseField
parameter_list|,
name|ValueType
name|type
parameter_list|)
block|{
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|this
operator|.
name|supportedTokens
operator|=
name|supportedTokens
expr_stmt|;
name|this
operator|.
name|parseField
operator|=
name|parseField
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|assertSupports
specifier|public
name|void
name|assertSupports
parameter_list|(
name|String
name|parserName
parameter_list|,
name|XContentParser
operator|.
name|Token
name|token
parameter_list|,
name|String
name|currentFieldName
parameter_list|,
name|ParseFieldMatcher
name|matcher
parameter_list|)
block|{
if|if
condition|(
name|matcher
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|parseField
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"["
operator|+
name|parserName
operator|+
literal|"] parsefield doesn't accept: "
operator|+
name|currentFieldName
argument_list|)
throw|;
block|}
if|if
condition|(
name|supportedTokens
operator|.
name|contains
argument_list|(
name|token
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|parserName
operator|+
literal|"] "
operator|+
name|currentFieldName
operator|+
literal|" doesn't support values of type: "
operator|+
name|token
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
index|[]
name|deprecatedNames
init|=
name|parseField
operator|.
name|getDeprecatedNames
argument_list|()
decl_stmt|;
name|String
name|allReplacedWith
init|=
name|parseField
operator|.
name|getAllReplacedWith
argument_list|()
decl_stmt|;
name|String
name|deprecated
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|deprecatedNames
operator|!=
literal|null
operator|&&
name|deprecatedNames
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|deprecated
operator|=
literal|", deprecated_names="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|deprecatedNames
argument_list|)
expr_stmt|;
block|}
return|return
literal|"FieldParser{"
operator|+
literal|"preferred_name="
operator|+
name|parseField
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|", supportedTokens="
operator|+
name|supportedTokens
operator|+
name|deprecated
operator|+
operator|(
name|allReplacedWith
operator|==
literal|null
condition|?
literal|""
else|:
literal|", replaced_with="
operator|+
name|allReplacedWith
operator|)
operator|+
literal|", type="
operator|+
name|type
operator|.
name|name
argument_list|()
operator|+
literal|'}'
return|;
block|}
block|}
DECL|enum|ValueType
specifier|public
enum|enum
name|ValueType
block|{
DECL|enum constant|STRING
name|STRING
parameter_list|(
name|VALUE_STRING
parameter_list|)
operator|,
DECL|enum constant|STRING_OR_NULL
constructor|STRING_OR_NULL(VALUE_STRING
operator|,
constructor|VALUE_NULL
block|)
enum|,
DECL|enum constant|FLOAT
name|FLOAT
parameter_list|(
name|VALUE_NUMBER
parameter_list|,
name|VALUE_STRING
parameter_list|)
operator|,
DECL|enum constant|DOUBLE
constructor|DOUBLE(VALUE_NUMBER
operator|,
constructor|VALUE_STRING
block|)
operator|,
DECL|enum constant|LONG
name|LONG
argument_list|(
name|VALUE_NUMBER
argument_list|,
name|VALUE_STRING
argument_list|)
operator|,
DECL|enum constant|INT
name|INT
argument_list|(
name|VALUE_NUMBER
argument_list|,
name|VALUE_STRING
argument_list|)
operator|,
DECL|enum constant|BOOLEAN
name|BOOLEAN
argument_list|(
name|VALUE_BOOLEAN
argument_list|)
operator|,
DECL|enum constant|STRING_ARRAY
name|STRING_ARRAY
argument_list|(
name|START_ARRAY
argument_list|,
name|VALUE_STRING
argument_list|)
operator|,
DECL|enum constant|FLOAT_ARRAY
name|FLOAT_ARRAY
argument_list|(
name|START_ARRAY
argument_list|,
name|VALUE_NUMBER
argument_list|,
name|VALUE_STRING
argument_list|)
operator|,
DECL|enum constant|DOUBLE_ARRAY
name|DOUBLE_ARRAY
argument_list|(
name|START_ARRAY
argument_list|,
name|VALUE_NUMBER
argument_list|,
name|VALUE_STRING
argument_list|)
operator|,
DECL|enum constant|LONG_ARRAY
name|LONG_ARRAY
argument_list|(
name|START_ARRAY
argument_list|,
name|VALUE_NUMBER
argument_list|,
name|VALUE_STRING
argument_list|)
operator|,
DECL|enum constant|INT_ARRAY
name|INT_ARRAY
argument_list|(
name|START_ARRAY
argument_list|,
name|VALUE_NUMBER
argument_list|,
name|VALUE_STRING
argument_list|)
operator|,
DECL|enum constant|BOOLEAN_ARRAY
name|BOOLEAN_ARRAY
argument_list|(
name|START_ARRAY
argument_list|,
name|VALUE_BOOLEAN
argument_list|)
operator|,
DECL|enum constant|OBJECT
name|OBJECT
argument_list|(
name|START_OBJECT
argument_list|)
operator|,
DECL|enum constant|OBJECT_ARRAY
name|OBJECT_ARRAY
argument_list|(
name|START_OBJECT
argument_list|,
name|START_ARRAY
argument_list|)
operator|,
DECL|enum constant|OBJECT_OR_BOOLEAN
name|OBJECT_OR_BOOLEAN
argument_list|(
name|START_OBJECT
argument_list|,
name|VALUE_BOOLEAN
argument_list|)
operator|,
DECL|enum constant|VALUE
name|VALUE
argument_list|(
name|VALUE_BOOLEAN
argument_list|,
name|VALUE_NULL
argument_list|,
name|VALUE_EMBEDDED_OBJECT
argument_list|,
name|VALUE_NUMBER
argument_list|,
name|VALUE_STRING
argument_list|)
expr_stmt|;
end_class

begin_decl_stmt
DECL|field|tokens
specifier|private
specifier|final
name|EnumSet
argument_list|<
name|XContentParser
operator|.
name|Token
argument_list|>
name|tokens
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|ValueType
name|ValueType
argument_list|(
name|XContentParser
operator|.
name|Token
name|first
argument_list|,
name|XContentParser
operator|.
name|Token
operator|...
name|rest
argument_list|)
block|{
name|this
operator|.
name|tokens
operator|=
name|EnumSet
operator|.
name|of
argument_list|(
name|first
argument_list|,
name|rest
argument_list|)
block|;         }
DECL|method|supportedTokens
specifier|public
name|EnumSet
argument_list|<
name|XContentParser
operator|.
name|Token
argument_list|>
name|supportedTokens
argument_list|()
block|{
return|return
name|this
operator|.
name|tokens
return|;
block|}
end_expr_stmt

begin_function
unit|}      @
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ObjectParser{"
operator|+
literal|"name='"
operator|+
name|name
operator|+
literal|'\''
operator|+
literal|", fields="
operator|+
name|fieldParserMap
operator|.
name|values
argument_list|()
operator|+
literal|'}'
return|;
block|}
end_function

unit|}
end_unit

