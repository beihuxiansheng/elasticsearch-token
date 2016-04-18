begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.sort
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
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
name|BinaryDocValues
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
name|LeafReaderContext
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
name|search
operator|.
name|Scorer
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
name|search
operator|.
name|SortField
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
name|BytesRef
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
name|BytesRefBuilder
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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
name|index
operator|.
name|fielddata
operator|.
name|FieldData
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
name|fielddata
operator|.
name|IndexFieldData
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
name|fielddata
operator|.
name|IndexFieldData
operator|.
name|XFieldComparatorSource
operator|.
name|Nested
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
name|fielddata
operator|.
name|NumericDoubleValues
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
name|fielddata
operator|.
name|SortedBinaryDocValues
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
name|fielddata
operator|.
name|SortedNumericDoubleValues
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
name|fielddata
operator|.
name|fieldcomparator
operator|.
name|BytesRefFieldComparatorSource
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
name|fielddata
operator|.
name|fieldcomparator
operator|.
name|DoubleValuesComparatorSource
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
name|QueryBuilder
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryShardContext
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
name|QueryShardException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|LeafSearchScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
operator|.
name|ScriptField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptParameterParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptParameterParser
operator|.
name|ScriptParameterValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|SearchScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|MultiValueMode
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
name|Collections
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
name|Locale
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
comment|/**  * Script sort builder allows to sort based on a custom script expression.  */
end_comment

begin_class
DECL|class|ScriptSortBuilder
specifier|public
class|class
name|ScriptSortBuilder
extends|extends
name|SortBuilder
argument_list|<
name|ScriptSortBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"_script"
decl_stmt|;
DECL|field|TYPE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|TYPE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
DECL|field|SCRIPT_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|SCRIPT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"script"
argument_list|)
decl_stmt|;
DECL|field|SORTMODE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|SORTMODE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"mode"
argument_list|)
decl_stmt|;
DECL|field|NESTED_PATH_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|NESTED_PATH_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"nested_path"
argument_list|)
decl_stmt|;
DECL|field|NESTED_FILTER_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|NESTED_FILTER_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"nested_filter"
argument_list|)
decl_stmt|;
DECL|field|PARAMS_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|PARAMS_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"params"
argument_list|)
decl_stmt|;
DECL|field|script
specifier|private
specifier|final
name|Script
name|script
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|ScriptSortType
name|type
decl_stmt|;
DECL|field|sortMode
specifier|private
name|SortMode
name|sortMode
decl_stmt|;
DECL|field|nestedFilter
specifier|private
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|nestedFilter
decl_stmt|;
DECL|field|nestedPath
specifier|private
name|String
name|nestedPath
decl_stmt|;
comment|/**      * Constructs a script sort builder with the given script.      *      * @param script      *            The script to use.      * @param type      *            The type of the script, can be either {@link ScriptSortType#STRING} or      *            {@link ScriptSortType#NUMBER}      */
DECL|method|ScriptSortBuilder
specifier|public
name|ScriptSortBuilder
parameter_list|(
name|Script
name|script
parameter_list|,
name|ScriptSortType
name|type
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|script
argument_list|,
literal|"script cannot be null"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|type
argument_list|,
literal|"type cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|ScriptSortBuilder
name|ScriptSortBuilder
parameter_list|(
name|ScriptSortBuilder
name|original
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|original
operator|.
name|script
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|original
operator|.
name|type
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|original
operator|.
name|order
expr_stmt|;
name|this
operator|.
name|sortMode
operator|=
name|original
operator|.
name|sortMode
expr_stmt|;
name|this
operator|.
name|nestedFilter
operator|=
name|original
operator|.
name|nestedFilter
expr_stmt|;
name|this
operator|.
name|nestedPath
operator|=
name|original
operator|.
name|nestedPath
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|ScriptSortBuilder
specifier|public
name|ScriptSortBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|script
operator|=
name|Script
operator|.
name|readScript
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|type
operator|=
name|ScriptSortType
operator|.
name|readFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|order
operator|=
name|SortOrder
operator|.
name|readFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|sortMode
operator|=
name|in
operator|.
name|readOptionalWriteable
argument_list|(
name|SortMode
operator|::
name|readFromStream
argument_list|)
expr_stmt|;
name|nestedPath
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|nestedFilter
operator|=
name|in
operator|.
name|readOptionalNamedWriteable
argument_list|(
name|QueryBuilder
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|script
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|type
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|order
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalWriteable
argument_list|(
name|sortMode
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|nestedPath
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalQuery
argument_list|(
name|nestedFilter
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the script used in this sort.      */
DECL|method|script
specifier|public
name|Script
name|script
parameter_list|()
block|{
return|return
name|this
operator|.
name|script
return|;
block|}
comment|/**      * Get the type used in this sort.      */
DECL|method|type
specifier|public
name|ScriptSortType
name|type
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
comment|/**      * Defines which distance to use for sorting in the case a document contains multiple values.<br>      * For {@link ScriptSortType#STRING}, the set of possible values is restricted to {@link SortMode#MIN} and {@link SortMode#MAX}      */
DECL|method|sortMode
specifier|public
name|ScriptSortBuilder
name|sortMode
parameter_list|(
name|SortMode
name|sortMode
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|sortMode
argument_list|,
literal|"sort mode cannot be null."
argument_list|)
expr_stmt|;
if|if
condition|(
name|ScriptSortType
operator|.
name|STRING
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|&&
operator|(
name|sortMode
operator|==
name|SortMode
operator|.
name|SUM
operator|||
name|sortMode
operator|==
name|SortMode
operator|.
name|AVG
operator|||
name|sortMode
operator|==
name|SortMode
operator|.
name|MEDIAN
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"script sort of type [string] doesn't support mode ["
operator|+
name|sortMode
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|sortMode
operator|=
name|sortMode
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the sort mode.      */
DECL|method|sortMode
specifier|public
name|SortMode
name|sortMode
parameter_list|()
block|{
return|return
name|this
operator|.
name|sortMode
return|;
block|}
comment|/**      * Sets the nested filter that the nested objects should match with in order to be taken into account      * for sorting.      */
DECL|method|setNestedFilter
specifier|public
name|ScriptSortBuilder
name|setNestedFilter
parameter_list|(
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|nestedFilter
parameter_list|)
block|{
name|this
operator|.
name|nestedFilter
operator|=
name|nestedFilter
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Gets the nested filter.      */
DECL|method|getNestedFilter
specifier|public
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|getNestedFilter
parameter_list|()
block|{
return|return
name|this
operator|.
name|nestedFilter
return|;
block|}
comment|/**      * Sets the nested path if sorting occurs on a field that is inside a nested object. For sorting by script this      * needs to be specified.      */
DECL|method|setNestedPath
specifier|public
name|ScriptSortBuilder
name|setNestedPath
parameter_list|(
name|String
name|nestedPath
parameter_list|)
block|{
name|this
operator|.
name|nestedPath
operator|=
name|nestedPath
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Gets the nested path.      */
DECL|method|getNestedPath
specifier|public
name|String
name|getNestedPath
parameter_list|()
block|{
return|return
name|this
operator|.
name|nestedPath
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
name|builderParams
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|SCRIPT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|script
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|TYPE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|ORDER_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|order
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortMode
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|SORTMODE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|sortMode
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nestedPath
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|NESTED_PATH_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|nestedPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nestedFilter
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|NESTED_FILTER_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|nestedFilter
argument_list|,
name|builderParams
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
comment|/**      * Creates a new {@link ScriptSortBuilder} from the query held by the {@link QueryParseContext} in      * {@link org.elasticsearch.common.xcontent.XContent} format.      *      * @param context the input parse context. The state on the parser contained in this context will be changed as a side effect of this      *        method call      * @param elementName in some sort syntax variations the field name precedes the xContent object that specifies further parameters, e.g.      *        in '{Â "foo": { "order" : "asc"} }'. When parsing the inner object, the field name can be passed in via this argument      */
DECL|method|fromXContent
specifier|public
specifier|static
name|ScriptSortBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|context
parameter_list|,
name|String
name|elementName
parameter_list|)
throws|throws
name|IOException
block|{
name|ScriptParameterParser
name|scriptParameterParser
init|=
operator|new
name|ScriptParameterParser
argument_list|()
decl_stmt|;
name|XContentParser
name|parser
init|=
name|context
operator|.
name|parser
argument_list|()
decl_stmt|;
name|ParseFieldMatcher
name|parseField
init|=
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
decl_stmt|;
name|Script
name|script
init|=
literal|null
decl_stmt|;
name|ScriptSortType
name|type
init|=
literal|null
decl_stmt|;
name|SortMode
name|sortMode
init|=
literal|null
decl_stmt|;
name|SortOrder
name|order
init|=
literal|null
decl_stmt|;
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|nestedFilter
init|=
literal|null
decl_stmt|;
name|String
name|nestedPath
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentName
init|=
name|parser
operator|.
name|currentName
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
name|currentName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|parseField
operator|.
name|match
argument_list|(
name|currentName
argument_list|,
name|ScriptField
operator|.
name|SCRIPT
argument_list|)
condition|)
block|{
name|script
operator|=
name|Script
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|parseField
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseField
operator|.
name|match
argument_list|(
name|currentName
argument_list|,
name|PARAMS_FIELD
argument_list|)
condition|)
block|{
name|params
operator|=
name|parser
operator|.
name|map
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseField
operator|.
name|match
argument_list|(
name|currentName
argument_list|,
name|NESTED_FILTER_FIELD
argument_list|)
condition|)
block|{
name|nestedFilter
operator|=
name|context
operator|.
name|parseInnerQueryBuilder
argument_list|()
expr_stmt|;
block|}
else|else
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
name|NAME
operator|+
literal|"] failed to parse field ["
operator|+
name|currentName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
name|parseField
operator|.
name|match
argument_list|(
name|currentName
argument_list|,
name|ORDER_FIELD
argument_list|)
condition|)
block|{
name|order
operator|=
name|SortOrder
operator|.
name|fromString
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
name|scriptParameterParser
operator|.
name|token
argument_list|(
name|currentName
argument_list|,
name|token
argument_list|,
name|parser
argument_list|,
name|parseField
argument_list|)
condition|)
block|{
comment|// Do Nothing (handled by ScriptParameterParser
block|}
elseif|else
if|if
condition|(
name|parseField
operator|.
name|match
argument_list|(
name|currentName
argument_list|,
name|TYPE_FIELD
argument_list|)
condition|)
block|{
name|type
operator|=
name|ScriptSortType
operator|.
name|fromString
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
name|parseField
operator|.
name|match
argument_list|(
name|currentName
argument_list|,
name|SORTMODE_FIELD
argument_list|)
condition|)
block|{
name|sortMode
operator|=
name|SortMode
operator|.
name|fromString
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
name|parseField
operator|.
name|match
argument_list|(
name|currentName
argument_list|,
name|NESTED_PATH_FIELD
argument_list|)
condition|)
block|{
name|nestedPath
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
else|else
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
name|NAME
operator|+
literal|"] failed to parse field ["
operator|+
name|currentName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
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
name|NAME
operator|+
literal|"] unexpected token ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|script
operator|==
literal|null
condition|)
block|{
comment|// Didn't find anything using the new API so try using the old one instead
name|ScriptParameterValue
name|scriptValue
init|=
name|scriptParameterParser
operator|.
name|getDefaultScriptParameterValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|scriptValue
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|params
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|script
operator|=
operator|new
name|Script
argument_list|(
name|scriptValue
operator|.
name|script
argument_list|()
argument_list|,
name|scriptValue
operator|.
name|scriptType
argument_list|()
argument_list|,
name|scriptParameterParser
operator|.
name|lang
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
name|ScriptSortBuilder
name|result
init|=
operator|new
name|ScriptSortBuilder
argument_list|(
name|script
argument_list|,
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|order
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|order
argument_list|(
name|order
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sortMode
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|sortMode
argument_list|(
name|sortMode
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nestedFilter
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|setNestedFilter
argument_list|(
name|nestedFilter
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nestedPath
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|setNestedPath
argument_list|(
name|nestedPath
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|SortField
name|build
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SearchScript
name|searchScript
init|=
name|context
operator|.
name|getScriptService
argument_list|()
operator|.
name|search
argument_list|(
name|context
operator|.
name|lookup
argument_list|()
argument_list|,
name|script
argument_list|,
name|ScriptContext
operator|.
name|Standard
operator|.
name|SEARCH
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|MultiValueMode
name|valueMode
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sortMode
operator|!=
literal|null
condition|)
block|{
name|valueMode
operator|=
name|MultiValueMode
operator|.
name|fromString
argument_list|(
name|sortMode
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|reverse
init|=
operator|(
name|order
operator|==
name|SortOrder
operator|.
name|DESC
operator|)
decl_stmt|;
if|if
condition|(
name|valueMode
operator|==
literal|null
condition|)
block|{
name|valueMode
operator|=
name|reverse
condition|?
name|MultiValueMode
operator|.
name|MAX
else|:
name|MultiValueMode
operator|.
name|MIN
expr_stmt|;
block|}
specifier|final
name|Nested
name|nested
init|=
name|resolveNested
argument_list|(
name|context
argument_list|,
name|nestedPath
argument_list|,
name|nestedFilter
argument_list|)
decl_stmt|;
specifier|final
name|IndexFieldData
operator|.
name|XFieldComparatorSource
name|fieldComparatorSource
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|STRING
case|:
name|fieldComparatorSource
operator|=
operator|new
name|BytesRefFieldComparatorSource
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|valueMode
argument_list|,
name|nested
argument_list|)
block|{
name|LeafSearchScript
name|leafScript
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|SortedBinaryDocValues
name|getValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|leafScript
operator|=
name|searchScript
operator|.
name|getLeafSearchScript
argument_list|(
name|context
argument_list|)
expr_stmt|;
specifier|final
name|BinaryDocValues
name|values
init|=
operator|new
name|BinaryDocValues
argument_list|()
block|{
specifier|final
name|BytesRefBuilder
name|spare
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|leafScript
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|spare
operator|.
name|copyChars
argument_list|(
name|leafScript
operator|.
name|run
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|spare
operator|.
name|get
argument_list|()
return|;
block|}
block|}
decl_stmt|;
return|return
name|FieldData
operator|.
name|singleton
argument_list|(
name|values
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|leafScript
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
break|break;
case|case
name|NUMBER
case|:
name|fieldComparatorSource
operator|=
operator|new
name|DoubleValuesComparatorSource
argument_list|(
literal|null
argument_list|,
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|valueMode
argument_list|,
name|nested
argument_list|)
block|{
name|LeafSearchScript
name|leafScript
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|SortedNumericDoubleValues
name|getValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|leafScript
operator|=
name|searchScript
operator|.
name|getLeafSearchScript
argument_list|(
name|context
argument_list|)
expr_stmt|;
specifier|final
name|NumericDoubleValues
name|values
init|=
operator|new
name|NumericDoubleValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|double
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|leafScript
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
return|return
name|leafScript
operator|.
name|runAsDouble
argument_list|()
return|;
block|}
block|}
decl_stmt|;
return|return
name|FieldData
operator|.
name|singleton
argument_list|(
name|values
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|leafScript
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|context
argument_list|,
literal|"custom script sort type ["
operator|+
name|type
operator|+
literal|"] not supported"
argument_list|)
throw|;
block|}
return|return
operator|new
name|SortField
argument_list|(
literal|"_script"
argument_list|,
name|fieldComparatorSource
argument_list|,
name|reverse
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
name|object
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|object
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|object
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|object
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ScriptSortBuilder
name|other
init|=
operator|(
name|ScriptSortBuilder
operator|)
name|object
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|script
argument_list|,
name|other
operator|.
name|script
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
name|order
argument_list|,
name|other
operator|.
name|order
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|sortMode
argument_list|,
name|other
operator|.
name|sortMode
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|nestedFilter
argument_list|,
name|other
operator|.
name|nestedFilter
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|nestedPath
argument_list|,
name|other
operator|.
name|nestedPath
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
name|script
argument_list|,
name|type
argument_list|,
name|order
argument_list|,
name|sortMode
argument_list|,
name|nestedFilter
argument_list|,
name|nestedPath
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
DECL|enum|ScriptSortType
specifier|public
enum|enum
name|ScriptSortType
implements|implements
name|Writeable
argument_list|<
name|ScriptSortType
argument_list|>
block|{
comment|/** script sort for a string value **/
DECL|enum constant|STRING
name|STRING
block|,
comment|/** script sort for a numeric value **/
DECL|enum constant|NUMBER
name|NUMBER
block|;
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
specifier|final
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**          * Read from a stream.          */
DECL|method|readFromStream
specifier|static
name|ScriptSortType
name|readFromStream
parameter_list|(
specifier|final
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ordinal
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinal
operator|<
literal|0
operator|||
name|ordinal
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown ScriptSortType ordinal ["
operator|+
name|ordinal
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|values
argument_list|()
index|[
name|ordinal
index|]
return|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|ScriptSortType
name|fromString
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|str
argument_list|,
literal|"input string is null"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|str
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
condition|)
block|{
case|case
operator|(
literal|"string"
operator|)
case|:
return|return
name|ScriptSortType
operator|.
name|STRING
return|;
case|case
operator|(
literal|"number"
operator|)
case|:
return|return
name|ScriptSortType
operator|.
name|NUMBER
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown ScriptSortType ["
operator|+
name|str
operator|+
literal|"]"
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
return|return
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

