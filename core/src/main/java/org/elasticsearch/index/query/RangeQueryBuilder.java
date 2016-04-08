begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|IndexReader
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
name|Query
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
name|TermRangeQuery
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
name|joda
operator|.
name|DateMathParser
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
name|joda
operator|.
name|FormatDateTimeFormatter
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
name|joda
operator|.
name|Joda
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
name|lucene
operator|.
name|BytesRefs
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
name|mapper
operator|.
name|MappedFieldType
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
name|MapperService
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
name|core
operator|.
name|DateFieldMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTimeZone
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * A Query that matches documents within an range of terms.  */
end_comment

begin_class
DECL|class|RangeQueryBuilder
specifier|public
class|class
name|RangeQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|RangeQueryBuilder
argument_list|>
implements|implements
name|MultiTermQueryBuilder
argument_list|<
name|RangeQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"range"
decl_stmt|;
DECL|field|QUERY_NAME_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|QUERY_NAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_INCLUDE_UPPER
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_INCLUDE_UPPER
init|=
literal|true
decl_stmt|;
DECL|field|DEFAULT_INCLUDE_LOWER
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_INCLUDE_LOWER
init|=
literal|true
decl_stmt|;
DECL|field|FIELDDATA_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|FIELDDATA_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|withAllDeprecated
argument_list|(
literal|"[no replacement]"
argument_list|)
decl_stmt|;
DECL|field|NAME_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|NAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"_name"
argument_list|)
operator|.
name|withAllDeprecated
argument_list|(
literal|"query name is not supported in short version of range query"
argument_list|)
decl_stmt|;
DECL|field|LTE_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|LTE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"lte"
argument_list|,
literal|"le"
argument_list|)
decl_stmt|;
DECL|field|GTE_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|GTE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"gte"
argument_list|,
literal|"ge"
argument_list|)
decl_stmt|;
DECL|field|FROM_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|FROM_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"from"
argument_list|)
decl_stmt|;
DECL|field|TO_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|TO_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"to"
argument_list|)
decl_stmt|;
DECL|field|INCLUDE_LOWER_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|INCLUDE_LOWER_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"include_lower"
argument_list|)
decl_stmt|;
DECL|field|INCLUDE_UPPER_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|INCLUDE_UPPER_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"include_upper"
argument_list|)
decl_stmt|;
DECL|field|GT_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|GT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"gt"
argument_list|)
decl_stmt|;
DECL|field|LT_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|LT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"lt"
argument_list|)
decl_stmt|;
DECL|field|TIME_ZONE_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|TIME_ZONE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"time_zone"
argument_list|)
decl_stmt|;
DECL|field|FORMAT_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|FORMAT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"format"
argument_list|)
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|from
specifier|private
name|Object
name|from
decl_stmt|;
DECL|field|to
specifier|private
name|Object
name|to
decl_stmt|;
DECL|field|timeZone
specifier|private
name|DateTimeZone
name|timeZone
decl_stmt|;
DECL|field|includeLower
specifier|private
name|boolean
name|includeLower
init|=
name|DEFAULT_INCLUDE_LOWER
decl_stmt|;
DECL|field|includeUpper
specifier|private
name|boolean
name|includeUpper
init|=
name|DEFAULT_INCLUDE_UPPER
decl_stmt|;
DECL|field|format
specifier|private
name|FormatDateTimeFormatter
name|format
decl_stmt|;
comment|/**      * A Query that matches documents within an range of terms.      *      * @param fieldName The field name      */
DECL|method|RangeQueryBuilder
specifier|public
name|RangeQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field name is null or empty"
argument_list|)
throw|;
block|}
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|RangeQueryBuilder
specifier|public
name|RangeQueryBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|from
operator|=
name|in
operator|.
name|readGenericValue
argument_list|()
expr_stmt|;
name|to
operator|=
name|in
operator|.
name|readGenericValue
argument_list|()
expr_stmt|;
name|includeLower
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|includeUpper
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|timeZone
operator|=
name|in
operator|.
name|readOptionalTimeZone
argument_list|()
expr_stmt|;
name|String
name|formatString
init|=
name|in
operator|.
name|readOptionalString
argument_list|()
decl_stmt|;
if|if
condition|(
name|formatString
operator|!=
literal|null
condition|)
block|{
name|format
operator|=
name|Joda
operator|.
name|forPattern
argument_list|(
name|formatString
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|this
operator|.
name|fieldName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeGenericValue
argument_list|(
name|this
operator|.
name|from
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeGenericValue
argument_list|(
name|this
operator|.
name|to
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|this
operator|.
name|includeLower
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|this
operator|.
name|includeUpper
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalTimeZone
argument_list|(
name|timeZone
argument_list|)
expr_stmt|;
name|String
name|formatString
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|format
operator|!=
literal|null
condition|)
block|{
name|formatString
operator|=
name|this
operator|.
name|format
operator|.
name|format
argument_list|()
expr_stmt|;
block|}
name|out
operator|.
name|writeOptionalString
argument_list|(
name|formatString
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the field name for this query.      */
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldName
return|;
block|}
comment|/**      * The from part of the range query. Null indicates unbounded.      * In case lower bound is assigned to a string, we internally convert it to a {@link BytesRef} because      * in {@link RangeQueryBuilder} field are later parsed as {@link BytesRef} and we need internal representation      * of query to be equal regardless of whether it was created from XContent or via Java API.      */
DECL|method|from
specifier|public
name|RangeQueryBuilder
name|from
parameter_list|(
name|Object
name|from
parameter_list|,
name|boolean
name|includeLower
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|convertToBytesRefIfString
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the range query. Null indicates unbounded.      */
DECL|method|from
specifier|public
name|RangeQueryBuilder
name|from
parameter_list|(
name|Object
name|from
parameter_list|)
block|{
return|return
name|from
argument_list|(
name|from
argument_list|,
name|this
operator|.
name|includeLower
argument_list|)
return|;
block|}
comment|/**      * Gets the lower range value for this query.      */
DECL|method|from
specifier|public
name|Object
name|from
parameter_list|()
block|{
return|return
name|convertToStringIfBytesRef
argument_list|(
name|this
operator|.
name|from
argument_list|)
return|;
block|}
comment|/**      * The from part of the range query. Null indicates unbounded.      */
DECL|method|gt
specifier|public
name|RangeQueryBuilder
name|gt
parameter_list|(
name|Object
name|from
parameter_list|)
block|{
return|return
name|from
argument_list|(
name|from
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * The from part of the range query. Null indicates unbounded.      */
DECL|method|gte
specifier|public
name|RangeQueryBuilder
name|gte
parameter_list|(
name|Object
name|from
parameter_list|)
block|{
return|return
name|from
argument_list|(
name|from
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * The to part of the range query. Null indicates unbounded.      */
DECL|method|to
specifier|public
name|RangeQueryBuilder
name|to
parameter_list|(
name|Object
name|to
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|convertToBytesRefIfString
argument_list|(
name|to
argument_list|)
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the range query. Null indicates unbounded.      */
DECL|method|to
specifier|public
name|RangeQueryBuilder
name|to
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
return|return
name|to
argument_list|(
name|to
argument_list|,
name|this
operator|.
name|includeUpper
argument_list|)
return|;
block|}
comment|/**      * Gets the upper range value for this query.      * In case upper bound is assigned to a string, we internally convert it to a {@link BytesRef} because      * in {@link RangeQueryBuilder} field are later parsed as {@link BytesRef} and we need internal representation      * of query to be equal regardless of whether it was created from XContent or via Java API.      */
DECL|method|to
specifier|public
name|Object
name|to
parameter_list|()
block|{
return|return
name|convertToStringIfBytesRef
argument_list|(
name|this
operator|.
name|to
argument_list|)
return|;
block|}
comment|/**      * The to part of the range query. Null indicates unbounded.      */
DECL|method|lt
specifier|public
name|RangeQueryBuilder
name|lt
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
return|return
name|to
argument_list|(
name|to
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * The to part of the range query. Null indicates unbounded.      */
DECL|method|lte
specifier|public
name|RangeQueryBuilder
name|lte
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
return|return
name|to
argument_list|(
name|to
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * Should the lower bound be included or not. Defaults to<tt>true</tt>.      */
DECL|method|includeLower
specifier|public
name|RangeQueryBuilder
name|includeLower
parameter_list|(
name|boolean
name|includeLower
parameter_list|)
block|{
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Gets the includeLower flag for this query.      */
DECL|method|includeLower
specifier|public
name|boolean
name|includeLower
parameter_list|()
block|{
return|return
name|this
operator|.
name|includeLower
return|;
block|}
comment|/**      * Should the upper bound be included or not. Defaults to<tt>true</tt>.      */
DECL|method|includeUpper
specifier|public
name|RangeQueryBuilder
name|includeUpper
parameter_list|(
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Gets the includeUpper flag for this query.      */
DECL|method|includeUpper
specifier|public
name|boolean
name|includeUpper
parameter_list|()
block|{
return|return
name|this
operator|.
name|includeUpper
return|;
block|}
comment|/**      * In case of date field, we can adjust the from/to fields using a timezone      */
DECL|method|timeZone
specifier|public
name|RangeQueryBuilder
name|timeZone
parameter_list|(
name|String
name|timeZone
parameter_list|)
block|{
if|if
condition|(
name|timeZone
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"timezone cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|timeZone
operator|=
name|DateTimeZone
operator|.
name|forID
argument_list|(
name|timeZone
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * In case of date field, gets the from/to fields timezone adjustment      */
DECL|method|timeZone
specifier|public
name|String
name|timeZone
parameter_list|()
block|{
return|return
name|this
operator|.
name|timeZone
operator|==
literal|null
condition|?
literal|null
else|:
name|this
operator|.
name|timeZone
operator|.
name|getID
argument_list|()
return|;
block|}
comment|/**      * In case of format field, we can parse the from/to fields using this time format      */
DECL|method|format
specifier|public
name|RangeQueryBuilder
name|format
parameter_list|(
name|String
name|format
parameter_list|)
block|{
if|if
condition|(
name|format
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"format cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|format
operator|=
name|Joda
operator|.
name|forPattern
argument_list|(
name|format
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Gets the format field to parse the from/to fields      */
DECL|method|format
specifier|public
name|String
name|format
parameter_list|()
block|{
return|return
name|this
operator|.
name|format
operator|==
literal|null
condition|?
literal|null
else|:
name|this
operator|.
name|format
operator|.
name|format
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|protected
name|void
name|doXContent
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
name|startObject
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|FROM_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|convertToStringIfBytesRef
argument_list|(
name|this
operator|.
name|from
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|TO_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|convertToStringIfBytesRef
argument_list|(
name|this
operator|.
name|to
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|INCLUDE_LOWER_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|includeLower
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|INCLUDE_UPPER_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|includeUpper
argument_list|)
expr_stmt|;
if|if
condition|(
name|timeZone
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|TIME_ZONE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|timeZone
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|format
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|FORMAT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|format
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|printBoostAndQueryName
argument_list|(
name|builder
argument_list|)
expr_stmt|;
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
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|RangeQueryBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|Object
name|from
init|=
literal|null
decl_stmt|;
name|Object
name|to
init|=
literal|null
decl_stmt|;
name|boolean
name|includeLower
init|=
name|RangeQueryBuilder
operator|.
name|DEFAULT_INCLUDE_LOWER
decl_stmt|;
name|boolean
name|includeUpper
init|=
name|RangeQueryBuilder
operator|.
name|DEFAULT_INCLUDE_UPPER
decl_stmt|;
name|String
name|timeZone
init|=
literal|null
decl_stmt|;
name|float
name|boost
init|=
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
name|String
name|format
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
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
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|isDeprecatedSetting
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// skip
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
name|fieldName
operator|=
name|currentFieldName
expr_stmt|;
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
block|}
else|else
block|{
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|FROM_FIELD
argument_list|)
condition|)
block|{
name|from
operator|=
name|parser
operator|.
name|objectBytes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|TO_FIELD
argument_list|)
condition|)
block|{
name|to
operator|=
name|parser
operator|.
name|objectBytes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|INCLUDE_LOWER_FIELD
argument_list|)
condition|)
block|{
name|includeLower
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|INCLUDE_UPPER_FIELD
argument_list|)
condition|)
block|{
name|includeUpper
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|AbstractQueryBuilder
operator|.
name|BOOST_FIELD
argument_list|)
condition|)
block|{
name|boost
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|GT_FIELD
argument_list|)
condition|)
block|{
name|from
operator|=
name|parser
operator|.
name|objectBytes
argument_list|()
expr_stmt|;
name|includeLower
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|GTE_FIELD
argument_list|)
condition|)
block|{
name|from
operator|=
name|parser
operator|.
name|objectBytes
argument_list|()
expr_stmt|;
name|includeLower
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|LT_FIELD
argument_list|)
condition|)
block|{
name|to
operator|=
name|parser
operator|.
name|objectBytes
argument_list|()
expr_stmt|;
name|includeUpper
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|LTE_FIELD
argument_list|)
condition|)
block|{
name|to
operator|=
name|parser
operator|.
name|objectBytes
argument_list|()
expr_stmt|;
name|includeUpper
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|TIME_ZONE_FIELD
argument_list|)
condition|)
block|{
name|timeZone
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|FORMAT_FIELD
argument_list|)
condition|)
block|{
name|format
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|AbstractQueryBuilder
operator|.
name|NAME_FIELD
argument_list|)
condition|)
block|{
name|queryName
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
literal|"[range] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
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
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|NAME_FIELD
argument_list|)
condition|)
block|{
name|queryName
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|FIELDDATA_FIELD
argument_list|)
condition|)
block|{
comment|// ignore
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
literal|"[range] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
name|RangeQueryBuilder
name|rangeQuery
init|=
operator|new
name|RangeQueryBuilder
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|rangeQuery
operator|.
name|from
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|rangeQuery
operator|.
name|to
argument_list|(
name|to
argument_list|)
expr_stmt|;
name|rangeQuery
operator|.
name|includeLower
argument_list|(
name|includeLower
argument_list|)
expr_stmt|;
name|rangeQuery
operator|.
name|includeUpper
argument_list|(
name|includeUpper
argument_list|)
expr_stmt|;
if|if
condition|(
name|timeZone
operator|!=
literal|null
condition|)
block|{
name|rangeQuery
operator|.
name|timeZone
argument_list|(
name|timeZone
argument_list|)
expr_stmt|;
block|}
name|rangeQuery
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|rangeQuery
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
if|if
condition|(
name|format
operator|!=
literal|null
condition|)
block|{
name|rangeQuery
operator|.
name|format
argument_list|(
name|format
argument_list|)
expr_stmt|;
block|}
return|return
name|rangeQuery
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
comment|// Overridable for testing only
DECL|method|getRelation
specifier|protected
name|MappedFieldType
operator|.
name|Relation
name|getRelation
parameter_list|(
name|QueryRewriteContext
name|queryRewriteContext
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
name|queryRewriteContext
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
comment|// If the reader is null we are not on the shard and cannot
comment|// rewrite so just pretend there is an intersection so that the rewrite is a noop
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
return|return
name|MappedFieldType
operator|.
name|Relation
operator|.
name|INTERSECTS
return|;
block|}
specifier|final
name|MapperService
name|mapperService
init|=
name|queryRewriteContext
operator|.
name|getMapperService
argument_list|()
decl_stmt|;
specifier|final
name|MappedFieldType
name|fieldType
init|=
name|mapperService
operator|.
name|fullName
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
comment|// no field means we have no values
return|return
name|MappedFieldType
operator|.
name|Relation
operator|.
name|DISJOINT
return|;
block|}
else|else
block|{
name|DateMathParser
name|dateMathParser
init|=
name|format
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|DateMathParser
argument_list|(
name|format
argument_list|)
decl_stmt|;
return|return
name|fieldType
operator|.
name|isFieldWithinQuery
argument_list|(
name|queryRewriteContext
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|,
name|timeZone
argument_list|,
name|dateMathParser
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|doRewrite
specifier|protected
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|doRewrite
parameter_list|(
name|QueryRewriteContext
name|queryRewriteContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|MappedFieldType
operator|.
name|Relation
name|relation
init|=
name|getRelation
argument_list|(
name|queryRewriteContext
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|relation
condition|)
block|{
case|case
name|DISJOINT
case|:
return|return
operator|new
name|MatchNoneQueryBuilder
argument_list|()
return|;
case|case
name|WITHIN
case|:
if|if
condition|(
name|from
operator|!=
literal|null
operator|||
name|to
operator|!=
literal|null
condition|)
block|{
name|RangeQueryBuilder
name|newRangeQuery
init|=
operator|new
name|RangeQueryBuilder
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|newRangeQuery
operator|.
name|from
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|newRangeQuery
operator|.
name|to
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|newRangeQuery
operator|.
name|format
operator|=
name|format
expr_stmt|;
name|newRangeQuery
operator|.
name|timeZone
operator|=
name|timeZone
expr_stmt|;
return|return
name|newRangeQuery
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
case|case
name|INTERSECTS
case|:
return|return
name|this
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|doToQuery
specifier|protected
name|Query
name|doToQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
literal|null
decl_stmt|;
name|MappedFieldType
name|mapper
init|=
name|context
operator|.
name|fieldMapper
argument_list|(
name|this
operator|.
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapper
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|mapper
operator|instanceof
name|DateFieldMapper
operator|.
name|DateFieldType
condition|)
block|{
name|DateMathParser
name|forcedDateParser
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|format
operator|!=
literal|null
condition|)
block|{
name|forcedDateParser
operator|=
operator|new
name|DateMathParser
argument_list|(
name|this
operator|.
name|format
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
operator|(
operator|(
name|DateFieldMapper
operator|.
name|DateFieldType
operator|)
name|mapper
operator|)
operator|.
name|rangeQuery
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|,
name|timeZone
argument_list|,
name|forcedDateParser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|timeZone
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|context
argument_list|,
literal|"[range] time_zone can not be applied to non date field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|//LUCENE 4 UPGRADE Mapper#rangeQuery should use bytesref as well?
name|query
operator|=
name|mapper
operator|.
name|rangeQuery
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|timeZone
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|context
argument_list|,
literal|"[range] time_zone can not be applied to non unmapped field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|TermRangeQuery
argument_list|(
name|this
operator|.
name|fieldName
argument_list|,
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|from
argument_list|)
argument_list|,
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|to
argument_list|)
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
name|String
name|timeZoneId
init|=
name|timeZone
operator|==
literal|null
condition|?
literal|null
else|:
name|timeZone
operator|.
name|getID
argument_list|()
decl_stmt|;
name|String
name|formatString
init|=
name|format
operator|==
literal|null
condition|?
literal|null
else|:
name|format
operator|.
name|format
argument_list|()
decl_stmt|;
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|fieldName
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|timeZoneId
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|,
name|formatString
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|RangeQueryBuilder
name|other
parameter_list|)
block|{
name|String
name|timeZoneId
init|=
name|timeZone
operator|==
literal|null
condition|?
literal|null
else|:
name|timeZone
operator|.
name|getID
argument_list|()
decl_stmt|;
name|String
name|formatString
init|=
name|format
operator|==
literal|null
condition|?
literal|null
else|:
name|format
operator|.
name|format
argument_list|()
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|,
name|other
operator|.
name|fieldName
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|from
argument_list|,
name|other
operator|.
name|from
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|to
argument_list|,
name|other
operator|.
name|to
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|timeZoneId
argument_list|,
name|other
operator|.
name|timeZone
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|includeLower
argument_list|,
name|other
operator|.
name|includeLower
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|includeUpper
argument_list|,
name|other
operator|.
name|includeUpper
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|formatString
argument_list|,
name|other
operator|.
name|format
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

