begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.completion.context
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
operator|.
name|context
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
name|index
operator|.
name|IndexableField
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
name|Version
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
name|XContentParser
operator|.
name|Token
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
name|mapper
operator|.
name|KeywordFieldMapper
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
name|mapper
operator|.
name|ParseContext
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
name|mapper
operator|.
name|ParseContext
operator|.
name|Document
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
name|query
operator|.
name|QueryParseContext
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * A {@link ContextMapping} that uses a simple string as a criteria  * The suggestions are boosted and/or filtered by their associated  * category (string) value.  * {@link CategoryQueryContext} defines options for constructing  * a unit of query context for this context type  */
end_comment

begin_class
DECL|class|CategoryContextMapping
specifier|public
class|class
name|CategoryContextMapping
extends|extends
name|ContextMapping
argument_list|<
name|CategoryQueryContext
argument_list|>
block|{
DECL|field|FIELD_FIELDNAME
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_FIELDNAME
init|=
literal|"path"
decl_stmt|;
DECL|field|CONTEXT_VALUE
specifier|static
specifier|final
name|String
name|CONTEXT_VALUE
init|=
literal|"context"
decl_stmt|;
DECL|field|CONTEXT_BOOST
specifier|static
specifier|final
name|String
name|CONTEXT_BOOST
init|=
literal|"boost"
decl_stmt|;
DECL|field|CONTEXT_PREFIX
specifier|static
specifier|final
name|String
name|CONTEXT_PREFIX
init|=
literal|"prefix"
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
comment|/**      * Create a new {@link CategoryContextMapping} with field      *<code>fieldName</code>      */
DECL|method|CategoryContextMapping
specifier|private
name|CategoryContextMapping
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|Type
operator|.
name|CATEGORY
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
comment|/**      * Name of the field to get contexts from at index-time      */
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
comment|/**      * Loads a<code>name</code>d {@link CategoryContextMapping} instance      * from a map.      * see {@link ContextMappings#load(Object, Version)}      *      * Acceptable map param:<code>path</code>      */
DECL|method|load
specifier|protected
specifier|static
name|CategoryContextMapping
name|load
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
throws|throws
name|ElasticsearchParseException
block|{
name|CategoryContextMapping
operator|.
name|Builder
name|mapping
init|=
operator|new
name|CategoryContextMapping
operator|.
name|Builder
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Object
name|fieldName
init|=
name|config
operator|.
name|get
argument_list|(
name|FIELD_FIELDNAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
name|mapping
operator|.
name|field
argument_list|(
name|fieldName
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|remove
argument_list|(
name|FIELD_FIELDNAME
argument_list|)
expr_stmt|;
block|}
return|return
name|mapping
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toInnerXContent
specifier|protected
name|XContentBuilder
name|toInnerXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|FIELD_FIELDNAME
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
comment|/**      * Parse a set of {@link CharSequence} contexts at index-time.      * Acceptable formats:      *      *<ul>      *<li>Array:<pre>[<i>&lt;string&gt;</i>, ..]</pre></li>      *<li>String:<pre>&quot;string&quot;</pre></li>      *</ul>      */
annotation|@
name|Override
DECL|method|parseContext
specifier|public
name|Set
argument_list|<
name|CharSequence
argument_list|>
name|parseContext
parameter_list|(
name|ParseContext
name|parseContext
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
throws|,
name|ElasticsearchParseException
block|{
specifier|final
name|Set
argument_list|<
name|CharSequence
argument_list|>
name|contexts
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
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
name|Token
operator|.
name|VALUE_STRING
operator|||
name|token
operator|==
name|Token
operator|.
name|VALUE_NUMBER
operator|||
name|token
operator|==
name|Token
operator|.
name|VALUE_BOOLEAN
condition|)
block|{
name|contexts
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
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
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_STRING
operator|||
name|token
operator|==
name|Token
operator|.
name|VALUE_NUMBER
operator|||
name|token
operator|==
name|Token
operator|.
name|VALUE_BOOLEAN
condition|)
block|{
name|contexts
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"context array must have string, number or boolean values, but was ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"contexts must be a string, number or boolean or a list of string, number or boolean, but was ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|contexts
return|;
block|}
annotation|@
name|Override
DECL|method|parseContext
specifier|public
name|Set
argument_list|<
name|CharSequence
argument_list|>
name|parseContext
parameter_list|(
name|Document
name|document
parameter_list|)
block|{
name|Set
argument_list|<
name|CharSequence
argument_list|>
name|values
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
name|IndexableField
index|[]
name|fields
init|=
name|document
operator|.
name|getFields
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|values
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexableField
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|instanceof
name|KeywordFieldMapper
operator|.
name|KeywordFieldType
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|field
operator|.
name|binaryValue
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|.
name|add
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|(
name|values
operator|==
literal|null
operator|)
condition|?
name|Collections
operator|.
name|emptySet
argument_list|()
else|:
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|protected
name|CategoryQueryContext
name|fromXContent
parameter_list|(
name|QueryParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|CategoryQueryContext
operator|.
name|fromXContent
argument_list|(
name|context
argument_list|)
return|;
block|}
comment|/**      * Parse a list of {@link CategoryQueryContext}      * using<code>parser</code>. A QueryContexts accepts one of the following forms:      *      *<ul>      *<li>Object: CategoryQueryContext</li>      *<li>String: CategoryQueryContext value with prefix=false and boost=1</li>      *<li>Array:<pre>[CategoryQueryContext, ..]</pre></li>      *</ul>      *      *  A CategoryQueryContext has one of the following forms:      *<ul>      *<li>Object:<pre>{&quot;context&quot;:<i>&lt;string&gt;</i>,&quot;boost&quot;:<i>&lt;int&gt;</i>,&quot;prefix&quot;:<i>&lt;boolean&gt;</i>}</pre></li>      *<li>String:<pre>&quot;string&quot;</pre></li>      *</ul>      */
annotation|@
name|Override
DECL|method|toInternalQueryContexts
specifier|public
name|List
argument_list|<
name|InternalQueryContext
argument_list|>
name|toInternalQueryContexts
parameter_list|(
name|List
argument_list|<
name|CategoryQueryContext
argument_list|>
name|queryContexts
parameter_list|)
block|{
name|List
argument_list|<
name|InternalQueryContext
argument_list|>
name|internalInternalQueryContexts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|queryContexts
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|internalInternalQueryContexts
operator|.
name|addAll
argument_list|(
name|queryContexts
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|queryContext
lambda|->
operator|new
name|InternalQueryContext
argument_list|(
name|queryContext
operator|.
name|getCategory
argument_list|()
argument_list|,
name|queryContext
operator|.
name|getBoost
argument_list|()
argument_list|,
name|queryContext
operator|.
name|isPrefix
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|internalInternalQueryContexts
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
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|CategoryContextMapping
name|mapping
init|=
operator|(
name|CategoryContextMapping
operator|)
name|o
decl_stmt|;
return|return
operator|!
operator|(
name|fieldName
operator|!=
literal|null
condition|?
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|mapping
operator|.
name|fieldName
argument_list|)
else|:
name|mapping
operator|.
name|fieldName
operator|!=
literal|null
operator|)
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
name|super
operator|.
name|hashCode
argument_list|()
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
comment|/**      * Builder for {@link CategoryContextMapping}      */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|ContextBuilder
argument_list|<
name|CategoryContextMapping
argument_list|>
block|{
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
comment|/**          * Create a builder for          * a named {@link CategoryContextMapping}          * @param name name of the mapping          */
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**          * Set the name of the field to use          */
DECL|method|field
specifier|public
name|Builder
name|field
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|CategoryContextMapping
name|build
parameter_list|()
block|{
return|return
operator|new
name|CategoryContextMapping
argument_list|(
name|name
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

