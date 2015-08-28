begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.context
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|context
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
name|base
operator|.
name|Joiner
import|;
end_import

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
name|Iterables
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|PrefixAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import

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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|Automata
import|;
end_import

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
name|automaton
operator|.
name|Automaton
import|;
end_import

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
name|automaton
operator|.
name|Operations
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
name|Arrays
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
comment|/**  * The {@link CategoryContextMapping} is used to define a {@link ContextMapping} that  * references a field within a document. The value of the field in turn will be  * used to setup the suggestions made by the completion suggester.  */
end_comment

begin_class
DECL|class|CategoryContextMapping
specifier|public
class|class
name|CategoryContextMapping
extends|extends
name|ContextMapping
block|{
DECL|field|TYPE
specifier|protected
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"category"
decl_stmt|;
DECL|field|FIELD_FIELDNAME
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_FIELDNAME
init|=
literal|"path"
decl_stmt|;
DECL|field|DEFAULT_FIELDNAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_FIELDNAME
init|=
literal|"_type"
decl_stmt|;
DECL|field|EMPTY_VALUES
specifier|private
specifier|static
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|EMPTY_VALUES
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|defaultValues
specifier|private
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|defaultValues
decl_stmt|;
DECL|field|defaultConfig
specifier|private
specifier|final
name|FieldConfig
name|defaultConfig
decl_stmt|;
comment|/**      * Create a new {@link CategoryContextMapping} with the default field      *<code>[_type]</code>      */
DECL|method|CategoryContextMapping
specifier|public
name|CategoryContextMapping
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|DEFAULT_FIELDNAME
argument_list|,
name|EMPTY_VALUES
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new {@link CategoryContextMapping} with the default field      *<code>[_type]</code>      */
DECL|method|CategoryContextMapping
specifier|public
name|CategoryContextMapping
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|fieldName
argument_list|,
name|EMPTY_VALUES
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new {@link CategoryContextMapping} with the default field      *<code>[_type]</code>      */
DECL|method|CategoryContextMapping
specifier|public
name|CategoryContextMapping
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|defaultValues
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|DEFAULT_FIELDNAME
argument_list|,
name|defaultValues
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new {@link CategoryContextMapping} with the default field      *<code>[_type]</code>      */
DECL|method|CategoryContextMapping
specifier|public
name|CategoryContextMapping
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|defaultValues
parameter_list|)
block|{
name|super
argument_list|(
name|TYPE
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
name|this
operator|.
name|defaultValues
operator|=
name|defaultValues
expr_stmt|;
name|this
operator|.
name|defaultConfig
operator|=
operator|new
name|FieldConfig
argument_list|(
name|fieldName
argument_list|,
name|defaultValues
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Name of the field used by this {@link CategoryContextMapping}      */
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
DECL|method|getDefaultValues
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|getDefaultValues
parameter_list|()
block|{
return|return
name|defaultValues
return|;
block|}
annotation|@
name|Override
DECL|method|defaultConfig
specifier|public
name|FieldConfig
name|defaultConfig
parameter_list|()
block|{
return|return
name|defaultConfig
return|;
block|}
comment|/**      * Load the specification of a {@link CategoryContextMapping}      *       * @param field      *            name of the field to use. If<code>null</code> default field      *            will be used      * @return new {@link CategoryContextMapping}      */
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
name|Object
name|defaultValues
init|=
name|config
operator|.
name|get
argument_list|(
name|FIELD_MISSING
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
name|fieldName
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
if|if
condition|(
name|defaultValues
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|defaultValues
operator|instanceof
name|Iterable
condition|)
block|{
for|for
control|(
name|Object
name|value
range|:
operator|(
name|Iterable
operator|)
name|defaultValues
control|)
block|{
name|mapping
operator|.
name|addDefaultValue
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|mapping
operator|.
name|addDefaultValue
argument_list|(
name|defaultValues
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|remove
argument_list|(
name|FIELD_MISSING
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
name|builder
operator|.
name|startArray
argument_list|(
name|FIELD_MISSING
argument_list|)
expr_stmt|;
for|for
control|(
name|CharSequence
name|value
range|:
name|defaultValues
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|parseContext
specifier|public
name|ContextConfig
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
name|VALUE_NULL
condition|)
block|{
return|return
operator|new
name|FieldConfig
argument_list|(
name|fieldName
argument_list|,
name|defaultValues
argument_list|,
literal|null
argument_list|)
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
operator|new
name|FieldConfig
argument_list|(
name|fieldName
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
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
operator|new
name|FieldConfig
argument_list|(
name|fieldName
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_BOOLEAN
condition|)
block|{
return|return
operator|new
name|FieldConfig
argument_list|(
name|fieldName
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
return|;
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
name|ArrayList
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|values
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
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"FieldConfig must contain a least one category"
argument_list|)
throw|;
block|}
return|return
operator|new
name|FieldConfig
argument_list|(
name|fieldName
argument_list|,
literal|null
argument_list|,
name|values
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"FieldConfig must be either [null], a string or a list of strings"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|parseQuery
specifier|public
name|FieldQuery
name|parseQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
throws|,
name|ElasticsearchParseException
block|{
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|values
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
name|START_ARRAY
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|list
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
name|values
operator|=
name|list
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_NULL
condition|)
block|{
name|values
operator|=
name|defaultValues
expr_stmt|;
block|}
else|else
block|{
name|values
operator|=
name|Collections
operator|.
name|singleton
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FieldQuery
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
DECL|method|query
specifier|public
specifier|static
name|FieldQuery
name|query
parameter_list|(
name|String
name|name
parameter_list|,
name|CharSequence
modifier|...
name|fieldvalues
parameter_list|)
block|{
return|return
name|query
argument_list|(
name|name
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|fieldvalues
argument_list|)
argument_list|)
return|;
block|}
DECL|method|query
specifier|public
specifier|static
name|FieldQuery
name|query
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|fieldvalues
parameter_list|)
block|{
return|return
operator|new
name|FieldQuery
argument_list|(
name|name
argument_list|,
name|fieldvalues
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|CategoryContextMapping
condition|)
block|{
name|CategoryContextMapping
name|other
init|=
operator|(
name|CategoryContextMapping
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|fieldName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fieldName
argument_list|)
condition|)
block|{
return|return
name|Iterables
operator|.
name|elementsEqual
argument_list|(
name|this
operator|.
name|defaultValues
argument_list|,
name|other
operator|.
name|defaultValues
argument_list|)
return|;
block|}
block|}
return|return
literal|false
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
name|int
name|hashCode
init|=
name|fieldName
operator|.
name|hashCode
argument_list|()
decl_stmt|;
for|for
control|(
name|CharSequence
name|seq
range|:
name|defaultValues
control|)
block|{
name|hashCode
operator|=
literal|31
operator|*
name|hashCode
operator|+
name|seq
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|hashCode
return|;
block|}
DECL|class|FieldConfig
specifier|private
specifier|static
class|class
name|FieldConfig
extends|extends
name|ContextConfig
block|{
DECL|field|fieldname
specifier|private
specifier|final
name|String
name|fieldname
decl_stmt|;
DECL|field|defaultValues
specifier|private
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|defaultValues
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|values
decl_stmt|;
DECL|method|FieldConfig
specifier|public
name|FieldConfig
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|defaultValues
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|values
parameter_list|)
block|{
name|this
operator|.
name|fieldname
operator|=
name|fieldname
expr_stmt|;
name|this
operator|.
name|defaultValues
operator|=
name|defaultValues
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|wrapTokenStream
specifier|protected
name|TokenStream
name|wrapTokenStream
parameter_list|(
name|Document
name|doc
parameter_list|,
name|TokenStream
name|stream
parameter_list|)
block|{
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|PrefixAnalyzer
operator|.
name|PrefixTokenFilter
argument_list|(
name|stream
argument_list|,
name|ContextMapping
operator|.
name|SEPARATOR
argument_list|,
name|values
argument_list|)
return|;
comment|// if fieldname is default, BUT our default values are set, we take that one
block|}
elseif|else
if|if
condition|(
operator|(
name|doc
operator|.
name|getFields
argument_list|(
name|fieldname
argument_list|)
operator|.
name|length
operator|==
literal|0
operator|||
name|fieldname
operator|.
name|equals
argument_list|(
name|DEFAULT_FIELDNAME
argument_list|)
operator|)
operator|&&
name|defaultValues
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
operator|new
name|PrefixAnalyzer
operator|.
name|PrefixTokenFilter
argument_list|(
name|stream
argument_list|,
name|ContextMapping
operator|.
name|SEPARATOR
argument_list|,
name|defaultValues
argument_list|)
return|;
block|}
else|else
block|{
name|IndexableField
index|[]
name|fields
init|=
name|doc
operator|.
name|getFields
argument_list|(
name|fieldname
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|CharSequence
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|fields
operator|.
name|length
argument_list|)
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|fields
index|[
name|i
index|]
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PrefixAnalyzer
operator|.
name|PrefixTokenFilter
argument_list|(
name|stream
argument_list|,
name|ContextMapping
operator|.
name|SEPARATOR
argument_list|,
name|values
argument_list|)
return|;
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"FieldConfig("
operator|+
name|fieldname
operator|+
literal|" = ["
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|values
operator|!=
literal|null
operator|&&
name|this
operator|.
name|values
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
operator|.
name|append
argument_list|(
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|join
argument_list|(
name|this
operator|.
name|values
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|defaultValues
operator|!=
literal|null
operator|&&
name|this
operator|.
name|defaultValues
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" default("
argument_list|)
operator|.
name|append
argument_list|(
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|join
argument_list|(
name|this
operator|.
name|defaultValues
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|"])"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|FieldQuery
specifier|private
specifier|static
class|class
name|FieldQuery
extends|extends
name|ContextQuery
block|{
DECL|field|values
specifier|private
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|values
decl_stmt|;
DECL|method|FieldQuery
specifier|public
name|FieldQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toAutomaton
specifier|public
name|Automaton
name|toAutomaton
parameter_list|()
block|{
name|List
argument_list|<
name|Automaton
argument_list|>
name|automatons
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CharSequence
name|value
range|:
name|values
control|)
block|{
name|automatons
operator|.
name|add
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Operations
operator|.
name|union
argument_list|(
name|automatons
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
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
name|builder
operator|.
name|startArray
argument_list|(
name|name
argument_list|)
expr_stmt|;
for|for
control|(
name|CharSequence
name|value
range|:
name|values
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
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
DECL|field|fieldname
specifier|private
name|String
name|fieldname
decl_stmt|;
DECL|field|defaultValues
specifier|private
name|List
argument_list|<
name|CharSequence
argument_list|>
name|defaultValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|DEFAULT_FIELDNAME
argument_list|)
expr_stmt|;
block|}
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|fieldname
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldname
operator|=
name|fieldname
expr_stmt|;
block|}
comment|/**          * Set the name of the field to use          */
DECL|method|fieldName
specifier|public
name|Builder
name|fieldName
parameter_list|(
name|String
name|fieldname
parameter_list|)
block|{
name|this
operator|.
name|fieldname
operator|=
name|fieldname
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Add value to the default values of the mapping          */
DECL|method|addDefaultValue
specifier|public
name|Builder
name|addDefaultValue
parameter_list|(
name|CharSequence
name|defaultValue
parameter_list|)
block|{
name|this
operator|.
name|defaultValues
operator|.
name|add
argument_list|(
name|defaultValue
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Add set of default values to the mapping          */
DECL|method|addDefaultValues
specifier|public
name|Builder
name|addDefaultValues
parameter_list|(
name|CharSequence
modifier|...
name|defaultValues
parameter_list|)
block|{
for|for
control|(
name|CharSequence
name|defaultValue
range|:
name|defaultValues
control|)
block|{
name|this
operator|.
name|defaultValues
operator|.
name|add
argument_list|(
name|defaultValue
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**          * Add set of default values to the mapping          */
DECL|method|addDefaultValues
specifier|public
name|Builder
name|addDefaultValues
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|CharSequence
argument_list|>
name|defaultValues
parameter_list|)
block|{
for|for
control|(
name|CharSequence
name|defaultValue
range|:
name|defaultValues
control|)
block|{
name|this
operator|.
name|defaultValues
operator|.
name|add
argument_list|(
name|defaultValue
argument_list|)
expr_stmt|;
block|}
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
name|fieldname
argument_list|,
name|defaultValues
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

