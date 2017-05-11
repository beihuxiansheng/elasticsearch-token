begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex.remote
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|remote
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|ScrollableHitSource
operator|.
name|BasicHit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|ScrollableHitSource
operator|.
name|Hit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|ScrollableHitSource
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|ScrollableHitSource
operator|.
name|SearchFailure
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
name|collect
operator|.
name|Tuple
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
name|util
operator|.
name|concurrent
operator|.
name|EsRejectedExecutionException
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
name|ConstructingObjectParser
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
name|XContentLocation
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentType
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
name|function
operator|.
name|BiFunction
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|ConstructingObjectParser
operator|.
name|constructorArg
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
name|ConstructingObjectParser
operator|.
name|optionalConstructorArg
import|;
end_import

begin_comment
comment|/**  * Parsers to convert the response from the remote host into objects useful for {@link RemoteScrollableHitSource}.  */
end_comment

begin_class
DECL|class|RemoteResponseParsers
specifier|final
class|class
name|RemoteResponseParsers
block|{
DECL|method|RemoteResponseParsers
specifier|private
name|RemoteResponseParsers
parameter_list|()
block|{}
comment|/**      * Parser for an individual {@code hit} element.      */
DECL|field|HIT_PARSER
specifier|public
specifier|static
specifier|final
name|ConstructingObjectParser
argument_list|<
name|BasicHit
argument_list|,
name|XContentType
argument_list|>
name|HIT_PARSER
init|=
operator|new
name|ConstructingObjectParser
argument_list|<>
argument_list|(
literal|"hit"
argument_list|,
literal|true
argument_list|,
name|a
lambda|->
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
name|index
init|=
operator|(
name|String
operator|)
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
name|String
name|type
init|=
operator|(
name|String
operator|)
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
name|String
name|id
init|=
operator|(
name|String
operator|)
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
name|Long
name|version
init|=
operator|(
name|Long
operator|)
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
return|return
operator|new
name|BasicHit
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|version
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|version
argument_list|)
return|;
block|}
argument_list|)
decl_stmt|;
static|static
block|{
name|HIT_PARSER
operator|.
name|declareString
argument_list|(
name|constructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"_index"
argument_list|)
argument_list|)
expr_stmt|;
name|HIT_PARSER
operator|.
name|declareString
argument_list|(
name|constructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"_type"
argument_list|)
argument_list|)
expr_stmt|;
name|HIT_PARSER
operator|.
name|declareString
argument_list|(
name|constructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"_id"
argument_list|)
argument_list|)
expr_stmt|;
name|HIT_PARSER
operator|.
name|declareLong
argument_list|(
name|optionalConstructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"_version"
argument_list|)
argument_list|)
expr_stmt|;
name|HIT_PARSER
operator|.
name|declareObject
argument_list|(
operator|(
parameter_list|(
name|basicHit
parameter_list|,
name|tuple
parameter_list|)
lambda|->
name|basicHit
operator|.
name|setSource
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
argument_list|,
name|tuple
operator|.
name|v2
argument_list|()
argument_list|)
operator|)
argument_list|,
parameter_list|(
name|p
parameter_list|,
name|s
parameter_list|)
lambda|->
block|{
try|try
block|{
comment|/*                  * We spool the data from the remote back into xcontent so we can get bytes to send. There ought to be a better way but for                  * now this should do.                  */
try|try
init|(
name|XContentBuilder
name|b
init|=
name|XContentBuilder
operator|.
name|builder
argument_list|(
name|s
operator|.
name|xContent
argument_list|()
argument_list|)
init|)
block|{
name|b
operator|.
name|copyCurrentStructure
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|// a hack but this lets us get the right xcontent type to go with the source
return|return
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|b
operator|.
name|bytes
argument_list|()
argument_list|,
name|s
argument_list|)
return|;
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
literal|"[hit] failed to parse [_source]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
operator|,
operator|new
name|ParseField
argument_list|(
literal|"_source"
argument_list|)
block|)
class|;
end_class

begin_decl_stmt
name|ParseField
name|routingField
init|=
operator|new
name|ParseField
argument_list|(
literal|"_routing"
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|ParseField
name|parentField
init|=
operator|new
name|ParseField
argument_list|(
literal|"_parent"
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|ParseField
name|ttlField
init|=
operator|new
name|ParseField
argument_list|(
literal|"_ttl"
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|HIT_PARSER
operator|.
name|declareString
argument_list|(
name|BasicHit
operator|::
name|setRouting
argument_list|,
name|routingField
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|HIT_PARSER
operator|.
name|declareString
argument_list|(
name|BasicHit
operator|::
name|setParent
argument_list|,
name|parentField
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// Pre-2.0.0 parent and routing come back in "fields"
end_comment

begin_class
class|class
name|Fields
block|{
name|String
name|routing
decl_stmt|;
name|String
name|parent
decl_stmt|;
block|}
end_class

begin_decl_stmt
name|ObjectParser
argument_list|<
name|Fields
argument_list|,
name|XContentType
argument_list|>
name|fieldsParser
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
literal|"fields"
argument_list|,
name|Fields
operator|::
operator|new
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|HIT_PARSER
operator|.
name|declareObject
argument_list|(
parameter_list|(
name|hit
parameter_list|,
name|fields
parameter_list|)
lambda|->
block|{
name|hit
operator|.
name|setRouting
argument_list|(
name|fields
operator|.
name|routing
argument_list|)
expr_stmt|;
name|hit
operator|.
name|setParent
argument_list|(
name|fields
operator|.
name|parent
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|fieldsParser
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"fields"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|fieldsParser
operator|.
name|declareString
argument_list|(
parameter_list|(
name|fields
parameter_list|,
name|routing
parameter_list|)
lambda|->
name|fields
operator|.
name|routing
operator|=
name|routing
argument_list|,
name|routingField
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|fieldsParser
operator|.
name|declareString
argument_list|(
parameter_list|(
name|fields
parameter_list|,
name|parent
parameter_list|)
lambda|->
name|fields
operator|.
name|parent
operator|=
name|parent
argument_list|,
name|parentField
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|fieldsParser
operator|.
name|declareLong
argument_list|(
parameter_list|(
name|fields
parameter_list|,
name|ttl
parameter_list|)
lambda|->
block|{}
argument_list|,
name|ttlField
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// ignore ttls since they have been removed
end_comment

begin_comment
unit|}
comment|/**      * Parser for the {@code hits} element. Parsed to an array of {@code [total (Long), hits (List<Hit>)]}.      */
end_comment

begin_decl_stmt
DECL|field|HITS_PARSER
unit|public
specifier|static
specifier|final
name|ConstructingObjectParser
argument_list|<
name|Object
index|[]
argument_list|,
name|XContentType
argument_list|>
name|HITS_PARSER
init|=
operator|new
name|ConstructingObjectParser
argument_list|<>
argument_list|(
literal|"hits"
argument_list|,
literal|true
argument_list|,
name|a
lambda|->
name|a
argument_list|)
decl_stmt|;
end_decl_stmt

begin_static
static|static
block|{
name|HITS_PARSER
operator|.
name|declareLong
argument_list|(
name|constructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"total"
argument_list|)
argument_list|)
expr_stmt|;
name|HITS_PARSER
operator|.
name|declareObjectArray
argument_list|(
name|constructorArg
argument_list|()
argument_list|,
name|HIT_PARSER
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"hits"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_static

begin_comment
comment|/**      * Parser for {@code failed} shards in the {@code _shards} elements.      */
end_comment

begin_decl_stmt
DECL|field|SEARCH_FAILURE_PARSER
specifier|public
specifier|static
specifier|final
name|ConstructingObjectParser
argument_list|<
name|SearchFailure
argument_list|,
name|XContentType
argument_list|>
name|SEARCH_FAILURE_PARSER
init|=
operator|new
name|ConstructingObjectParser
argument_list|<>
argument_list|(
literal|"failure"
argument_list|,
literal|true
argument_list|,
name|a
lambda|->
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
name|index
init|=
operator|(
name|String
operator|)
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
name|Integer
name|shardId
init|=
operator|(
name|Integer
operator|)
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
name|String
name|nodeId
init|=
operator|(
name|String
operator|)
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
name|Object
name|reason
init|=
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
name|Throwable
name|reasonThrowable
decl_stmt|;
if|if
condition|(
name|reason
operator|instanceof
name|String
condition|)
block|{
name|reasonThrowable
operator|=
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown remote exception with reason=["
operator|+
operator|(
name|String
operator|)
name|reason
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reasonThrowable
operator|=
operator|(
name|Throwable
operator|)
name|reason
expr_stmt|;
block|}
return|return
operator|new
name|SearchFailure
argument_list|(
name|reasonThrowable
argument_list|,
name|index
argument_list|,
name|shardId
argument_list|,
name|nodeId
argument_list|)
return|;
block|}
argument_list|)
decl_stmt|;
end_decl_stmt

begin_static
static|static
block|{
name|SEARCH_FAILURE_PARSER
operator|.
name|declareString
argument_list|(
name|optionalConstructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"index"
argument_list|)
argument_list|)
expr_stmt|;
name|SEARCH_FAILURE_PARSER
operator|.
name|declareInt
argument_list|(
name|optionalConstructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"shard"
argument_list|)
argument_list|)
expr_stmt|;
name|SEARCH_FAILURE_PARSER
operator|.
name|declareString
argument_list|(
name|optionalConstructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"node"
argument_list|)
argument_list|)
expr_stmt|;
name|SEARCH_FAILURE_PARSER
operator|.
name|declareField
argument_list|(
name|constructorArg
argument_list|()
argument_list|,
parameter_list|(
name|p
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
name|START_OBJECT
condition|)
block|{
return|return
name|ThrowableBuilder
operator|.
name|PARSER
operator|.
name|apply
argument_list|(
name|p
argument_list|,
name|c
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|p
operator|.
name|text
argument_list|()
return|;
block|}
block|}
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"reason"
argument_list|)
argument_list|,
name|ValueType
operator|.
name|OBJECT_OR_STRING
argument_list|)
expr_stmt|;
block|}
end_static

begin_comment
comment|/**      * Parser for the {@code _shards} element. Throws everything out except the errors array if there is one. If there isn't one then it      * parses to an empty list.      */
end_comment

begin_decl_stmt
DECL|field|SHARDS_PARSER
specifier|public
specifier|static
specifier|final
name|ConstructingObjectParser
argument_list|<
name|List
argument_list|<
name|Throwable
argument_list|>
argument_list|,
name|XContentType
argument_list|>
name|SHARDS_PARSER
init|=
operator|new
name|ConstructingObjectParser
argument_list|<>
argument_list|(
literal|"_shards"
argument_list|,
literal|true
argument_list|,
name|a
lambda|->
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Throwable
argument_list|>
name|failures
init|=
operator|(
name|List
argument_list|<
name|Throwable
argument_list|>
operator|)
name|a
index|[
literal|0
index|]
decl_stmt|;
name|failures
operator|=
name|failures
operator|==
literal|null
condition|?
name|emptyList
argument_list|()
else|:
name|failures
expr_stmt|;
return|return
name|failures
return|;
block|}
argument_list|)
decl_stmt|;
end_decl_stmt

begin_static
static|static
block|{
name|SHARDS_PARSER
operator|.
name|declareObjectArray
argument_list|(
name|optionalConstructorArg
argument_list|()
argument_list|,
name|SEARCH_FAILURE_PARSER
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"failures"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_static

begin_decl_stmt
DECL|field|RESPONSE_PARSER
specifier|public
specifier|static
specifier|final
name|ConstructingObjectParser
argument_list|<
name|Response
argument_list|,
name|XContentType
argument_list|>
name|RESPONSE_PARSER
init|=
operator|new
name|ConstructingObjectParser
argument_list|<>
argument_list|(
literal|"search_response"
argument_list|,
literal|true
argument_list|,
name|a
lambda|->
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|Throwable
name|catastrophicFailure
init|=
operator|(
name|Throwable
operator|)
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|catastrophicFailure
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|Response
argument_list|(
literal|false
argument_list|,
name|singletonList
argument_list|(
operator|new
name|SearchFailure
argument_list|(
name|catastrophicFailure
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|,
name|emptyList
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
name|boolean
name|timedOut
init|=
operator|(
name|boolean
operator|)
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
name|String
name|scroll
init|=
operator|(
name|String
operator|)
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
name|Object
index|[]
name|hitsElement
init|=
operator|(
name|Object
index|[]
operator|)
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|SearchFailure
argument_list|>
name|failures
init|=
operator|(
name|List
argument_list|<
name|SearchFailure
argument_list|>
operator|)
name|a
index|[
name|i
operator|++
index|]
decl_stmt|;
name|long
name|totalHits
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|Hit
argument_list|>
name|hits
init|=
name|emptyList
argument_list|()
decl_stmt|;
comment|// Pull apart the hits element if we got it
if|if
condition|(
name|hitsElement
operator|!=
literal|null
condition|)
block|{
name|i
operator|=
literal|0
expr_stmt|;
name|totalHits
operator|=
operator|(
name|long
operator|)
name|hitsElement
index|[
name|i
operator|++
index|]
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Hit
argument_list|>
name|h
init|=
operator|(
name|List
argument_list|<
name|Hit
argument_list|>
operator|)
name|hitsElement
index|[
name|i
operator|++
index|]
decl_stmt|;
name|hits
operator|=
name|h
expr_stmt|;
block|}
return|return
operator|new
name|Response
argument_list|(
name|timedOut
argument_list|,
name|failures
argument_list|,
name|totalHits
argument_list|,
name|hits
argument_list|,
name|scroll
argument_list|)
return|;
block|}
argument_list|)
decl_stmt|;
end_decl_stmt

begin_static
static|static
block|{
name|RESPONSE_PARSER
operator|.
name|declareObject
argument_list|(
name|optionalConstructorArg
argument_list|()
argument_list|,
name|ThrowableBuilder
operator|.
name|PARSER
operator|::
name|apply
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"error"
argument_list|)
argument_list|)
expr_stmt|;
name|RESPONSE_PARSER
operator|.
name|declareBoolean
argument_list|(
name|optionalConstructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"timed_out"
argument_list|)
argument_list|)
expr_stmt|;
name|RESPONSE_PARSER
operator|.
name|declareString
argument_list|(
name|optionalConstructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"_scroll_id"
argument_list|)
argument_list|)
expr_stmt|;
name|RESPONSE_PARSER
operator|.
name|declareObject
argument_list|(
name|optionalConstructorArg
argument_list|()
argument_list|,
name|HITS_PARSER
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"hits"
argument_list|)
argument_list|)
expr_stmt|;
name|RESPONSE_PARSER
operator|.
name|declareObject
argument_list|(
name|optionalConstructorArg
argument_list|()
argument_list|,
name|SHARDS_PARSER
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"_shards"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_static

begin_comment
comment|/**      * Collects stuff about Throwables and attempts to rebuild them.      */
end_comment

begin_class
DECL|class|ThrowableBuilder
specifier|public
specifier|static
class|class
name|ThrowableBuilder
block|{
DECL|field|PARSER
specifier|public
specifier|static
specifier|final
name|BiFunction
argument_list|<
name|XContentParser
argument_list|,
name|XContentType
argument_list|,
name|Throwable
argument_list|>
name|PARSER
decl_stmt|;
static|static
block|{
name|ObjectParser
argument_list|<
name|ThrowableBuilder
argument_list|,
name|XContentType
argument_list|>
name|parser
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
literal|"reason"
argument_list|,
literal|true
argument_list|,
name|ThrowableBuilder
operator|::
operator|new
argument_list|)
decl_stmt|;
name|PARSER
operator|=
name|parser
operator|.
name|andThen
argument_list|(
name|ThrowableBuilder
operator|::
name|build
argument_list|)
expr_stmt|;
name|parser
operator|.
name|declareString
argument_list|(
name|ThrowableBuilder
operator|::
name|setType
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|parser
operator|.
name|declareString
argument_list|(
name|ThrowableBuilder
operator|::
name|setReason
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"reason"
argument_list|)
argument_list|)
expr_stmt|;
name|parser
operator|.
name|declareObject
argument_list|(
name|ThrowableBuilder
operator|::
name|setCausedBy
argument_list|,
name|PARSER
operator|::
name|apply
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"caused_by"
argument_list|)
argument_list|)
expr_stmt|;
comment|// So we can give a nice error for parsing exceptions
name|parser
operator|.
name|declareInt
argument_list|(
name|ThrowableBuilder
operator|::
name|setLine
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"line"
argument_list|)
argument_list|)
expr_stmt|;
name|parser
operator|.
name|declareInt
argument_list|(
name|ThrowableBuilder
operator|::
name|setColumn
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"col"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|reason
specifier|private
name|String
name|reason
decl_stmt|;
DECL|field|line
specifier|private
name|Integer
name|line
decl_stmt|;
DECL|field|column
specifier|private
name|Integer
name|column
decl_stmt|;
DECL|field|causedBy
specifier|private
name|Throwable
name|causedBy
decl_stmt|;
DECL|method|build
specifier|public
name|Throwable
name|build
parameter_list|()
block|{
name|Throwable
name|t
init|=
name|buildWithoutCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|causedBy
operator|!=
literal|null
condition|)
block|{
name|t
operator|.
name|initCause
argument_list|(
name|causedBy
argument_list|)
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
DECL|method|buildWithoutCause
specifier|private
name|Throwable
name|buildWithoutCause
parameter_list|()
block|{
name|requireNonNull
argument_list|(
name|type
argument_list|,
literal|"[type] is required"
argument_list|)
expr_stmt|;
name|requireNonNull
argument_list|(
name|reason
argument_list|,
literal|"[reason] is required"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
comment|// Make some effort to use the right exceptions
case|case
literal|"es_rejected_execution_exception"
case|:
return|return
operator|new
name|EsRejectedExecutionException
argument_list|(
name|reason
argument_list|)
return|;
case|case
literal|"parsing_exception"
case|:
name|XContentLocation
name|location
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|line
operator|!=
literal|null
operator|&&
name|column
operator|!=
literal|null
condition|)
block|{
name|location
operator|=
operator|new
name|XContentLocation
argument_list|(
name|line
argument_list|,
name|column
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ParsingException
argument_list|(
name|location
argument_list|,
name|reason
argument_list|)
return|;
comment|// But it isn't worth trying to get it perfect....
default|default:
return|return
operator|new
name|RuntimeException
argument_list|(
name|type
operator|+
literal|": "
operator|+
name|reason
argument_list|)
return|;
block|}
block|}
DECL|method|setType
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|setReason
specifier|public
name|void
name|setReason
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
name|this
operator|.
name|reason
operator|=
name|reason
expr_stmt|;
block|}
DECL|method|setLine
specifier|public
name|void
name|setLine
parameter_list|(
name|Integer
name|line
parameter_list|)
block|{
name|this
operator|.
name|line
operator|=
name|line
expr_stmt|;
block|}
DECL|method|setColumn
specifier|public
name|void
name|setColumn
parameter_list|(
name|Integer
name|column
parameter_list|)
block|{
name|this
operator|.
name|column
operator|=
name|column
expr_stmt|;
block|}
DECL|method|setCausedBy
specifier|public
name|void
name|setCausedBy
parameter_list|(
name|Throwable
name|causedBy
parameter_list|)
block|{
name|this
operator|.
name|causedBy
operator|=
name|causedBy
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/**      * Parses the main action to return just the {@linkplain Version} that it returns. We throw everything else out.      */
end_comment

begin_decl_stmt
DECL|field|MAIN_ACTION_PARSER
specifier|public
specifier|static
specifier|final
name|ConstructingObjectParser
argument_list|<
name|Version
argument_list|,
name|XContentType
argument_list|>
name|MAIN_ACTION_PARSER
init|=
operator|new
name|ConstructingObjectParser
argument_list|<>
argument_list|(
literal|"/"
argument_list|,
literal|true
argument_list|,
name|a
lambda|->
operator|(
name|Version
operator|)
name|a
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
end_decl_stmt

begin_static
static|static
block|{
name|ConstructingObjectParser
argument_list|<
name|Version
argument_list|,
name|XContentType
argument_list|>
name|versionParser
init|=
operator|new
name|ConstructingObjectParser
argument_list|<>
argument_list|(
literal|"version"
argument_list|,
literal|true
argument_list|,
name|a
lambda|->
name|Version
operator|.
name|fromString
argument_list|(
operator|(
name|String
operator|)
name|a
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|versionParser
operator|.
name|declareString
argument_list|(
name|constructorArg
argument_list|()
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"number"
argument_list|)
argument_list|)
expr_stmt|;
name|MAIN_ACTION_PARSER
operator|.
name|declareObject
argument_list|(
name|constructorArg
argument_list|()
argument_list|,
name|versionParser
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"version"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_static

unit|}
end_unit

