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
name|ParsingException
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
name|ObjectParser
operator|.
name|ValueType
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
name|List
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
name|Function
import|;
end_import

begin_comment
comment|/**  * Like {@link ObjectParser} but works with objects that have constructors whose arguments are mixed in with its other settings. Queries are  * like this, for example<code>ids</code> requires<code>types</code> but always parses the<code>values</code> field on the same level. If  * this doesn't sounds like what you want to parse have a look at  * {@link ObjectParser#declareNamedObjects(BiConsumer, ObjectParser.NamedObjectParser, Consumer, ParseField)} which solves a slightly  * different but similar sounding problem.  *<p>  * Anyway, {@linkplain ConstructingObjectParser} parses the fields in the order that they are in the XContent, collecting constructor  * arguments and parsing and queueing normal fields until all constructor arguments are parsed. Then it builds the target object and replays  * the queued fields. Any fields that come in after the last constructor arguments are parsed and immediately applied to the target object  * just like {@linkplain ObjectParser}.  *</p>  *<p>  * Declaring a {@linkplain ConstructingObjectParser} is intentionally quite similar to declaring an {@linkplain ObjectParser}. The only  * differences being that constructor arguments are declared with the consumer returned by the static {@link #constructorArg()} method and  * that {@linkplain ConstructingObjectParser}'s constructor takes a lambda that must build the target object from a list of constructor  * arguments:  *</p>  *<pre>{@code  *   private static final ConstructingObjectParser<Thing, SomeContext> PARSER = new ConstructingObjectParser<>("thing",  *           a -> new Thing((String) a[0], (String) a[1], (Integer) a[2]));  *   static {  *       PARSER.declareString(constructorArg(), new ParseField("animal"));  *       PARSER.declareString(constructorArg(), new ParseField("vegetable"));  *       PARSER.declareInt(optionalConstructorArg(), new ParseField("mineral"));  *       PARSER.declareInt(Thing::setFruit, new ParseField("fruit"));  *       PARSER.declareInt(Thing::setBug, new ParseField("bug"));  *   }  * }</pre>  *<p>  * This does add some overhead compared to just using {@linkplain ObjectParser} directly. On a 2.2 GHz Intel Core i7 MacBook Air running on  * battery power in a reasonably unscientific microbenchmark it is about 100 microseconds for a reasonably large object, less if the  * constructor arguments are first. On this platform with the same microbenchmarks just creating the XContentParser is around 900  * microseconds and using {#linkplain ObjectParser} directly adds another 300 or so microseconds. In the best case  * {@linkplain ConstructingObjectParser} allocates two additional objects per parse compared to {#linkplain ObjectParser}. In the worst case  * it allocates<code>3 + 2 * param_count</code> objects per parse. If this overhead is too much for you then feel free to have ObjectParser  * parse a secondary object and have that one call the target object's constructor. That ought to be rare though.  *</p>  *<p>  * Note: if optional constructor arguments aren't specified then the number of allocations is always the worst case.  *</p>  */
end_comment

begin_class
DECL|class|ConstructingObjectParser
specifier|public
specifier|final
class|class
name|ConstructingObjectParser
parameter_list|<
name|Value
parameter_list|,
name|Context
parameter_list|>
extends|extends
name|AbstractObjectParser
argument_list|<
name|Value
argument_list|,
name|Context
argument_list|>
block|{
comment|/**      * Consumer that marks a field as a required constructor argument instead of a real object field.      */
DECL|field|REQUIRED_CONSTRUCTOR_ARG_MARKER
specifier|private
specifier|static
specifier|final
name|BiConsumer
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|REQUIRED_CONSTRUCTOR_ARG_MARKER
init|=
parameter_list|(
name|a
parameter_list|,
name|b
parameter_list|)
lambda|->
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"I am just a marker I should never be called."
argument_list|)
throw|;
block|}
decl_stmt|;
comment|/**      * Consumer that marks a field as an optional constructor argument instead of a real object field.      */
DECL|field|OPTIONAL_CONSTRUCTOR_ARG_MARKER
specifier|private
specifier|static
specifier|final
name|BiConsumer
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|OPTIONAL_CONSTRUCTOR_ARG_MARKER
init|=
parameter_list|(
name|a
parameter_list|,
name|b
parameter_list|)
lambda|->
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"I am just a marker I should never be called."
argument_list|)
throw|;
block|}
decl_stmt|;
comment|/**      * List of constructor names used for generating the error message if not all arrive.      */
DECL|field|constructorArgInfos
specifier|private
specifier|final
name|List
argument_list|<
name|ConstructorArgInfo
argument_list|>
name|constructorArgInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|objectParser
specifier|private
specifier|final
name|ObjectParser
argument_list|<
name|Target
argument_list|,
name|Context
argument_list|>
name|objectParser
decl_stmt|;
DECL|field|builder
specifier|private
specifier|final
name|Function
argument_list|<
name|Object
index|[]
argument_list|,
name|Value
argument_list|>
name|builder
decl_stmt|;
comment|/**      * The number of fields on the targetObject. This doesn't include any constructor arguments and is the size used for the array backing      * the field queue.      */
DECL|field|numberOfFields
specifier|private
name|int
name|numberOfFields
init|=
literal|0
decl_stmt|;
comment|/**      * Build the parser.      *      * @param name The name given to the delegate ObjectParser for error identification. Use what you'd use if the object worked with      *        ObjectParser.      * @param builder A function that builds the object from an array of Objects. Declare this inline with the parser, casting the elements      *        of the array to the arguments so they work with your favorite constructor. The objects in the array will be in the same order      *        that you declared the {{@link #constructorArg()}s and none will be null. If any of the constructor arguments aren't defined in      *        the XContent then parsing will throw an error. We use an array here rather than a {@code Map<String, Object>} to save on      *        allocations.      */
DECL|method|ConstructingObjectParser
specifier|public
name|ConstructingObjectParser
parameter_list|(
name|String
name|name
parameter_list|,
name|Function
argument_list|<
name|Object
index|[]
argument_list|,
name|Value
argument_list|>
name|builder
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
comment|/**      * Build the parser.      *      * @param name The name given to the delegate ObjectParser for error identification. Use what you'd use if the object worked with      *        ObjectParser.      * @param ignoreUnknownFields Should this parser ignore unknown fields? This should generally be set to true only when parsing responses      *        from external systems, never when parsing requests from users.      * @param builder A function that builds the object from an array of Objects. Declare this inline with the parser, casting the elements      *        of the array to the arguments so they work with your favorite constructor. The objects in the array will be in the same order      *        that you declared the {{@link #constructorArg()}s and none will be null. If any of the constructor arguments aren't defined in      *        the XContent then parsing will throw an error. We use an array here rather than a {@code Map<String, Object>} to save on      *        allocations.      */
DECL|method|ConstructingObjectParser
specifier|public
name|ConstructingObjectParser
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|ignoreUnknownFields
parameter_list|,
name|Function
argument_list|<
name|Object
index|[]
argument_list|,
name|Value
argument_list|>
name|builder
parameter_list|)
block|{
name|objectParser
operator|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|name
argument_list|,
name|ignoreUnknownFields
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
block|}
comment|/**      * Call this to do the actual parsing. This implements {@link BiFunction} for conveniently integrating with ObjectParser.      */
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
try|try
block|{
return|return
name|parse
argument_list|(
name|parser
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
name|objectParser
operator|.
name|getName
argument_list|()
operator|+
literal|"] failed to parse object"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
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
return|return
name|objectParser
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
operator|new
name|Target
argument_list|(
name|parser
argument_list|)
argument_list|,
name|context
argument_list|)
operator|.
name|finish
argument_list|()
return|;
block|}
comment|/**      * Pass the {@linkplain BiConsumer} this returns the declare methods to declare a required constructor argument. See this class's      * javadoc for an example. The order in which these are declared matters: it is the order that they come in the array passed to      * {@link #builder} and the order that missing arguments are reported to the user if any are missing. When all of these parameters are      * parsed from the {@linkplain XContentParser} the target object is immediately built.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// Safe because we never call the method. This is just trickery to make the interface pretty.
DECL|method|constructorArg
specifier|public
specifier|static
parameter_list|<
name|Value
parameter_list|,
name|FieldT
parameter_list|>
name|BiConsumer
argument_list|<
name|Value
argument_list|,
name|FieldT
argument_list|>
name|constructorArg
parameter_list|()
block|{
return|return
operator|(
name|BiConsumer
argument_list|<
name|Value
argument_list|,
name|FieldT
argument_list|>
operator|)
name|REQUIRED_CONSTRUCTOR_ARG_MARKER
return|;
block|}
comment|/**      * Pass the {@linkplain BiConsumer} this returns the declare methods to declare an optional constructor argument. See this class's      * javadoc for an example. The order in which these are declared matters: it is the order that they come in the array passed to      * {@link #builder} and the order that missing arguments are reported to the user if any are missing. When all of these parameters are      * parsed from the {@linkplain XContentParser} the target object is immediately built.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// Safe because we never call the method. This is just trickery to make the interface pretty.
DECL|method|optionalConstructorArg
specifier|public
specifier|static
parameter_list|<
name|Value
parameter_list|,
name|FieldT
parameter_list|>
name|BiConsumer
argument_list|<
name|Value
argument_list|,
name|FieldT
argument_list|>
name|optionalConstructorArg
parameter_list|()
block|{
return|return
operator|(
name|BiConsumer
argument_list|<
name|Value
argument_list|,
name|FieldT
argument_list|>
operator|)
name|OPTIONAL_CONSTRUCTOR_ARG_MARKER
return|;
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
if|if
condition|(
name|consumer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[consumer] is required"
argument_list|)
throw|;
block|}
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
literal|"[parser] is required"
argument_list|)
throw|;
block|}
if|if
condition|(
name|parseField
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[parseField] is required"
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[type] is required"
argument_list|)
throw|;
block|}
if|if
condition|(
name|consumer
operator|==
name|REQUIRED_CONSTRUCTOR_ARG_MARKER
operator|||
name|consumer
operator|==
name|OPTIONAL_CONSTRUCTOR_ARG_MARKER
condition|)
block|{
comment|/*              * Constructor arguments are detected by this "marker" consumer. It keeps the API looking clean even if it is a bit sleezy. We              * then build a new consumer directly against the object parser that triggers the "constructor arg just arrived behavior" of the              * parser. Conveniently, we can close over the position of the constructor in the argument list so we don't need to do any fancy              * or expensive lookups whenever the constructor args come in.              */
name|int
name|position
init|=
name|constructorArgInfos
operator|.
name|size
argument_list|()
decl_stmt|;
name|boolean
name|required
init|=
name|consumer
operator|==
name|REQUIRED_CONSTRUCTOR_ARG_MARKER
decl_stmt|;
name|constructorArgInfos
operator|.
name|add
argument_list|(
operator|new
name|ConstructorArgInfo
argument_list|(
name|parseField
argument_list|,
name|required
argument_list|)
argument_list|)
expr_stmt|;
name|objectParser
operator|.
name|declareField
argument_list|(
parameter_list|(
name|target
parameter_list|,
name|v
parameter_list|)
lambda|->
name|target
operator|.
name|constructorArg
argument_list|(
name|position
argument_list|,
name|parseField
argument_list|,
name|v
argument_list|)
argument_list|,
name|parser
argument_list|,
name|parseField
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|numberOfFields
operator|+=
literal|1
expr_stmt|;
name|objectParser
operator|.
name|declareField
argument_list|(
name|queueingConsumer
argument_list|(
name|consumer
argument_list|,
name|parseField
argument_list|)
argument_list|,
name|parser
argument_list|,
name|parseField
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Creates the consumer that does the "field just arrived" behavior. If the targetObject hasn't been built then it queues the value.      * Otherwise it just applies the value just like {@linkplain ObjectParser} does.      */
DECL|method|queueingConsumer
specifier|private
parameter_list|<
name|T
parameter_list|>
name|BiConsumer
argument_list|<
name|Target
argument_list|,
name|T
argument_list|>
name|queueingConsumer
parameter_list|(
name|BiConsumer
argument_list|<
name|Value
argument_list|,
name|T
argument_list|>
name|consumer
parameter_list|,
name|ParseField
name|parseField
parameter_list|)
block|{
return|return
parameter_list|(
name|target
parameter_list|,
name|v
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|target
operator|.
name|targetObject
operator|!=
literal|null
condition|)
block|{
comment|// The target has already been built. Just apply the consumer now.
name|consumer
operator|.
name|accept
argument_list|(
name|target
operator|.
name|targetObject
argument_list|,
name|v
argument_list|)
expr_stmt|;
return|return;
block|}
comment|/*              * The target hasn't been built. Queue the consumer. The next two lines are the only allocations that ConstructingObjectParser              * does during parsing other than the boxing the ObjectParser might do. The first one is to preserve a snapshot of the current              * location so we can add it to the error message if parsing fails. The second one (the lambda) is the actual operation being              * queued. Note that we don't do any of this if the target object has already been built.              */
name|XContentLocation
name|location
init|=
name|target
operator|.
name|parser
operator|.
name|getTokenLocation
argument_list|()
decl_stmt|;
name|target
operator|.
name|queue
argument_list|(
name|targetObject
lambda|->
block|{
try|try
block|{
name|consumer
operator|.
name|accept
argument_list|(
name|targetObject
argument_list|,
name|v
argument_list|)
expr_stmt|;
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
name|location
argument_list|,
literal|"["
operator|+
name|objectParser
operator|.
name|getName
argument_list|()
operator|+
literal|"] failed to parse field ["
operator|+
name|parseField
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|;
block|}
comment|/**      * The target of the {@linkplain ConstructingObjectParser}. One of these is built every time you call      * {@linkplain ConstructingObjectParser#apply(XContentParser, Object)} Note that it is not static so it inherits      * {@linkplain ConstructingObjectParser}'s type parameters.      */
DECL|class|Target
specifier|private
class|class
name|Target
block|{
comment|/**          * Array of constructor args to be passed to the {@link ConstructingObjectParser#builder}.          */
DECL|field|constructorArgs
specifier|private
specifier|final
name|Object
index|[]
name|constructorArgs
init|=
operator|new
name|Object
index|[
name|constructorArgInfos
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|/**          * The parser this class is working against. We store it here so we can fetch it conveniently when queueing fields to lookup the          * location of each field so that we can give a useful error message when replaying the queue.          */
DECL|field|parser
specifier|private
specifier|final
name|XContentParser
name|parser
decl_stmt|;
comment|/**          * How many of the constructor parameters have we collected? We keep track of this so we don't have to count the          * {@link #constructorArgs} array looking for nulls when we receive another constructor parameter. When this is equal to the size of          * {@link #constructorArgs} we build the target object.          */
DECL|field|constructorArgsCollected
specifier|private
name|int
name|constructorArgsCollected
init|=
literal|0
decl_stmt|;
comment|/**          * Fields to be saved to the target object when we can build it. This is only allocated if a field has to be queued.          */
DECL|field|queuedFields
specifier|private
name|Consumer
argument_list|<
name|Value
argument_list|>
index|[]
name|queuedFields
decl_stmt|;
comment|/**          * The count of fields already queued.          */
DECL|field|queuedFieldsCount
specifier|private
name|int
name|queuedFieldsCount
init|=
literal|0
decl_stmt|;
comment|/**          * The target object. This will be instantiated with the constructor arguments are all parsed.          */
DECL|field|targetObject
specifier|private
name|Value
name|targetObject
decl_stmt|;
DECL|method|Target
name|Target
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
block|{
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
comment|/**          * Set a constructor argument and build the target object if all constructor arguments have arrived.          */
DECL|method|constructorArg
specifier|private
name|void
name|constructorArg
parameter_list|(
name|int
name|position
parameter_list|,
name|ParseField
name|parseField
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|constructorArgs
index|[
name|position
index|]
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't repeat param ["
operator|+
name|parseField
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|constructorArgs
index|[
name|position
index|]
operator|=
name|value
expr_stmt|;
name|constructorArgsCollected
operator|++
expr_stmt|;
if|if
condition|(
name|constructorArgsCollected
operator|==
name|constructorArgInfos
operator|.
name|size
argument_list|()
condition|)
block|{
name|buildTarget
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**          * Queue a consumer that we'll call once the targetObject is built. If targetObject has been built this will fail because the caller          * should have just applied the consumer immediately.          */
DECL|method|queue
specifier|private
name|void
name|queue
parameter_list|(
name|Consumer
argument_list|<
name|Value
argument_list|>
name|queueMe
parameter_list|)
block|{
assert|assert
name|targetObject
operator|==
literal|null
operator|:
literal|"Don't queue after the targetObject has been built! Just apply the consumer directly."
assert|;
if|if
condition|(
name|queuedFields
operator|==
literal|null
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Consumer
argument_list|<
name|Value
argument_list|>
index|[]
name|queuedFields
init|=
operator|new
name|Consumer
index|[
name|numberOfFields
index|]
decl_stmt|;
name|this
operator|.
name|queuedFields
operator|=
name|queuedFields
expr_stmt|;
block|}
name|queuedFields
index|[
name|queuedFieldsCount
index|]
operator|=
name|queueMe
expr_stmt|;
name|queuedFieldsCount
operator|++
expr_stmt|;
block|}
comment|/**          * Finish parsing the object.          */
DECL|method|finish
specifier|private
name|Value
name|finish
parameter_list|()
block|{
if|if
condition|(
name|targetObject
operator|!=
literal|null
condition|)
block|{
return|return
name|targetObject
return|;
block|}
comment|/*              * The object hasn't been built which ought to mean we're missing some constructor arguments. But they could be optional! We'll              * check if they are all optional and build the error message at the same time - if we don't start the error message then they              * were all optional!              */
name|StringBuilder
name|message
init|=
literal|null
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
name|constructorArgs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|constructorArgs
index|[
name|i
index|]
operator|!=
literal|null
condition|)
continue|continue;
name|ConstructorArgInfo
name|arg
init|=
name|constructorArgInfos
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|false
operator|==
name|arg
operator|.
name|required
condition|)
continue|continue;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
name|message
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"Required ["
argument_list|)
operator|.
name|append
argument_list|(
name|arg
operator|.
name|field
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|message
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|arg
operator|.
name|field
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
comment|// There were non-optional constructor arguments missing.
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|message
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
comment|/*              * If there weren't any constructor arguments declared at all then we won't get an error message but this isn't really a valid              * use of ConstructingObjectParser. You should be using ObjectParser instead. Since this is more of a programmer error and the              * parser ought to still work we just assert this.              */
assert|assert
literal|false
operator|==
name|constructorArgInfos
operator|.
name|isEmpty
argument_list|()
operator|:
literal|"["
operator|+
name|objectParser
operator|.
name|getName
argument_list|()
operator|+
literal|"] must configure at least on constructor "
operator|+
literal|"argument. If it doesn't have any it should use ObjectParser instead of ConstructingObjectParser. This is a bug "
operator|+
literal|"in the parser declaration."
assert|;
comment|// All missing constructor arguments were optional. Just build the target and return it.
name|buildTarget
argument_list|()
expr_stmt|;
return|return
name|targetObject
return|;
block|}
DECL|method|buildTarget
specifier|private
name|void
name|buildTarget
parameter_list|()
block|{
try|try
block|{
name|targetObject
operator|=
name|builder
operator|.
name|apply
argument_list|(
name|constructorArgs
argument_list|)
expr_stmt|;
while|while
condition|(
name|queuedFieldsCount
operator|>
literal|0
condition|)
block|{
name|queuedFieldsCount
operator|-=
literal|1
expr_stmt|;
name|queuedFields
index|[
name|queuedFieldsCount
index|]
operator|.
name|accept
argument_list|(
name|targetObject
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ParsingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|e
operator|.
name|getLineNumber
argument_list|()
argument_list|,
name|e
operator|.
name|getColumnNumber
argument_list|()
argument_list|,
literal|"failed to build ["
operator|+
name|objectParser
operator|.
name|getName
argument_list|()
operator|+
literal|"] after last required field arrived"
argument_list|,
name|e
argument_list|)
throw|;
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
literal|null
argument_list|,
literal|"Failed to build ["
operator|+
name|objectParser
operator|.
name|getName
argument_list|()
operator|+
literal|"] after last required field arrived"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|ConstructorArgInfo
specifier|private
specifier|static
class|class
name|ConstructorArgInfo
block|{
DECL|field|field
specifier|final
name|ParseField
name|field
decl_stmt|;
DECL|field|required
specifier|final
name|boolean
name|required
decl_stmt|;
DECL|method|ConstructorArgInfo
name|ConstructorArgInfo
parameter_list|(
name|ParseField
name|field
parameter_list|,
name|boolean
name|required
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|required
operator|=
name|required
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

