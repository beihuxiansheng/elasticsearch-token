begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.ip
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|ip
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
name|net
operator|.
name|InetAddresses
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
name|NumericTokenStream
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
operator|.
name|NumericType
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
name|IndexOptions
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
name|NumericRangeQuery
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NumericUtils
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
name|Explicit
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
name|Nullable
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
name|Numbers
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
name|settings
operator|.
name|Settings
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
name|unit
operator|.
name|Fuzziness
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
name|analysis
operator|.
name|NamedAnalyzer
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
name|analysis
operator|.
name|NumericAnalyzer
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
name|analysis
operator|.
name|NumericTokenizer
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
name|FieldDataType
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
name|Mapper
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
name|MapperParsingException
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
name|MergeMappingException
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
name|MergeResult
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
name|core
operator|.
name|LongFieldMapper
operator|.
name|CustomLongNumericField
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
name|NumberFieldMapper
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
name|Iterator
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MapperBuilders
operator|.
name|ipField
import|;
end_import

begin_import
import|import static
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
name|TypeParsers
operator|.
name|parseNumberField
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|IpFieldMapper
specifier|public
class|class
name|IpFieldMapper
extends|extends
name|NumberFieldMapper
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"ip"
decl_stmt|;
DECL|method|longToIp
specifier|public
specifier|static
name|String
name|longToIp
parameter_list|(
name|long
name|longIp
parameter_list|)
block|{
name|int
name|octet3
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|longIp
operator|>>
literal|24
operator|)
operator|%
literal|256
argument_list|)
decl_stmt|;
name|int
name|octet2
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|longIp
operator|>>
literal|16
operator|)
operator|%
literal|256
argument_list|)
decl_stmt|;
name|int
name|octet1
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|longIp
operator|>>
literal|8
operator|)
operator|%
literal|256
argument_list|)
decl_stmt|;
name|int
name|octet0
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|longIp
operator|)
operator|%
literal|256
argument_list|)
decl_stmt|;
return|return
name|octet3
operator|+
literal|"."
operator|+
name|octet2
operator|+
literal|"."
operator|+
name|octet1
operator|+
literal|"."
operator|+
name|octet0
return|;
block|}
DECL|field|pattern
specifier|private
specifier|static
specifier|final
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
DECL|method|ipToLong
specifier|public
specifier|static
name|long
name|ipToLong
parameter_list|(
name|String
name|ip
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|InetAddresses
operator|.
name|isInetAddress
argument_list|(
name|ip
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"failed to parse ip ["
operator|+
name|ip
operator|+
literal|"], not a valid ip address"
argument_list|)
throw|;
block|}
name|String
index|[]
name|octets
init|=
name|pattern
operator|.
name|split
argument_list|(
name|ip
argument_list|)
decl_stmt|;
if|if
condition|(
name|octets
operator|.
name|length
operator|!=
literal|4
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"failed to parse ip ["
operator|+
name|ip
operator|+
literal|"], not a valid ipv4 address (4 dots)"
argument_list|)
throw|;
block|}
return|return
operator|(
name|Long
operator|.
name|parseLong
argument_list|(
name|octets
index|[
literal|0
index|]
argument_list|)
operator|<<
literal|24
operator|)
operator|+
operator|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|octets
index|[
literal|1
index|]
argument_list|)
operator|<<
literal|16
operator|)
operator|+
operator|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|octets
index|[
literal|2
index|]
argument_list|)
operator|<<
literal|8
operator|)
operator|+
name|Integer
operator|.
name|parseInt
argument_list|(
name|octets
index|[
literal|3
index|]
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|IllegalArgumentException
condition|)
block|{
throw|throw
operator|(
name|IllegalArgumentException
operator|)
name|e
throw|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"failed to parse ip ["
operator|+
name|ip
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|NumberFieldMapper
operator|.
name|Defaults
block|{
DECL|field|NULL_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|NULL_VALUE
init|=
literal|null
decl_stmt|;
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|MappedFieldType
name|FIELD_TYPE
init|=
operator|new
name|IpFieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|FIELD_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|NumberFieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|IpFieldMapper
argument_list|>
block|{
DECL|field|nullValue
specifier|protected
name|String
name|nullValue
init|=
name|Defaults
operator|.
name|NULL_VALUE
decl_stmt|;
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
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|,
name|Defaults
operator|.
name|PRECISION_STEP_64_BIT
argument_list|)
expr_stmt|;
name|builder
operator|=
name|this
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|IpFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|setupFieldType
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|IpFieldMapper
name|fieldMapper
init|=
operator|new
name|IpFieldMapper
argument_list|(
name|fieldType
argument_list|,
name|docValues
argument_list|,
name|ignoreMalformed
argument_list|(
name|context
argument_list|)
argument_list|,
name|coerce
argument_list|(
name|context
argument_list|)
argument_list|,
name|fieldDataSettings
argument_list|,
name|context
operator|.
name|indexSettings
argument_list|()
argument_list|,
name|multiFieldsBuilder
operator|.
name|build
argument_list|(
name|this
argument_list|,
name|context
argument_list|)
argument_list|,
name|copyTo
argument_list|)
decl_stmt|;
name|fieldMapper
operator|.
name|includeInAll
argument_list|(
name|includeInAll
argument_list|)
expr_stmt|;
return|return
name|fieldMapper
return|;
block|}
annotation|@
name|Override
DECL|method|makeNumberAnalyzer
specifier|protected
name|NamedAnalyzer
name|makeNumberAnalyzer
parameter_list|(
name|int
name|precisionStep
parameter_list|)
block|{
name|String
name|name
init|=
name|precisionStep
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|?
literal|"_ip/max"
else|:
operator|(
literal|"_ip/"
operator|+
name|precisionStep
operator|)
decl_stmt|;
return|return
operator|new
name|NamedAnalyzer
argument_list|(
name|name
argument_list|,
operator|new
name|NumericIpAnalyzer
argument_list|(
name|precisionStep
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|maxPrecisionStep
specifier|protected
name|int
name|maxPrecisionStep
parameter_list|()
block|{
return|return
literal|64
return|;
block|}
block|}
DECL|class|TypeParser
specifier|public
specifier|static
class|class
name|TypeParser
implements|implements
name|Mapper
operator|.
name|TypeParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Mapper
operator|.
name|Builder
name|parse
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
name|node
parameter_list|,
name|ParserContext
name|parserContext
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|IpFieldMapper
operator|.
name|Builder
name|builder
init|=
name|ipField
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|parseNumberField
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|node
argument_list|,
name|parserContext
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|iterator
init|=
name|node
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|propName
init|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|propNode
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
literal|"null_value"
argument_list|)
condition|)
block|{
if|if
condition|(
name|propNode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Property [null_value] cannot be null."
argument_list|)
throw|;
block|}
name|builder
operator|.
name|nullValue
argument_list|(
name|propNode
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|class|IpFieldType
specifier|public
specifier|static
specifier|final
class|class
name|IpFieldType
extends|extends
name|NumberFieldType
block|{
DECL|method|IpFieldType
specifier|public
name|IpFieldType
parameter_list|()
block|{
name|super
argument_list|(
name|NumericType
operator|.
name|LONG
argument_list|)
expr_stmt|;
block|}
DECL|method|IpFieldType
specifier|protected
name|IpFieldType
parameter_list|(
name|IpFieldType
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|NumberFieldType
name|clone
parameter_list|()
block|{
return|return
operator|new
name|IpFieldType
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|typeName
specifier|public
name|String
name|typeName
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Long
name|value
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|BytesRef
condition|)
block|{
return|return
name|Numbers
operator|.
name|bytesToLong
argument_list|(
operator|(
name|BytesRef
operator|)
name|value
argument_list|)
return|;
block|}
return|return
name|ipToLong
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**          * IPs should return as a string.          */
annotation|@
name|Override
DECL|method|valueForSearch
specifier|public
name|Object
name|valueForSearch
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|Long
name|val
init|=
name|value
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|longToIp
argument_list|(
name|val
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|indexedValueForSearch
specifier|public
name|BytesRef
name|indexedValueForSearch
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|BytesRefBuilder
name|bytesRef
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|parseValue
argument_list|(
name|value
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
comment|// 0 because of exact match
return|return
name|bytesRef
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|rangeQuery
specifier|public
name|Query
name|rangeQuery
parameter_list|(
name|Object
name|lowerTerm
parameter_list|,
name|Object
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
block|{
return|return
name|NumericRangeQuery
operator|.
name|newLongRange
argument_list|(
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|numericPrecisionStep
argument_list|()
argument_list|,
name|lowerTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|parseValue
argument_list|(
name|lowerTerm
argument_list|)
argument_list|,
name|upperTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|parseValue
argument_list|(
name|upperTerm
argument_list|)
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fuzzyQuery
specifier|public
name|Query
name|fuzzyQuery
parameter_list|(
name|String
name|value
parameter_list|,
name|Fuzziness
name|fuzziness
parameter_list|,
name|int
name|prefixLength
parameter_list|,
name|int
name|maxExpansions
parameter_list|,
name|boolean
name|transpositions
parameter_list|)
block|{
name|long
name|iValue
init|=
name|ipToLong
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|long
name|iSim
decl_stmt|;
try|try
block|{
name|iSim
operator|=
name|ipToLong
argument_list|(
name|fuzziness
operator|.
name|asString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|iSim
operator|=
name|fuzziness
operator|.
name|asLong
argument_list|()
expr_stmt|;
block|}
return|return
name|NumericRangeQuery
operator|.
name|newLongRange
argument_list|(
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|numericPrecisionStep
argument_list|()
argument_list|,
name|iValue
operator|-
name|iSim
argument_list|,
name|iValue
operator|+
name|iSim
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
DECL|method|IpFieldMapper
specifier|protected
name|IpFieldMapper
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|,
name|Boolean
name|docValues
parameter_list|,
name|Explicit
argument_list|<
name|Boolean
argument_list|>
name|ignoreMalformed
parameter_list|,
name|Explicit
argument_list|<
name|Boolean
argument_list|>
name|coerce
parameter_list|,
annotation|@
name|Nullable
name|Settings
name|fieldDataSettings
parameter_list|,
name|Settings
name|indexSettings
parameter_list|,
name|MultiFields
name|multiFields
parameter_list|,
name|CopyTo
name|copyTo
parameter_list|)
block|{
name|super
argument_list|(
name|fieldType
argument_list|,
name|docValues
argument_list|,
name|ignoreMalformed
argument_list|,
name|coerce
argument_list|,
name|fieldDataSettings
argument_list|,
name|indexSettings
argument_list|,
name|multiFields
argument_list|,
name|copyTo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|defaultFieldType
specifier|public
name|MappedFieldType
name|defaultFieldType
parameter_list|()
block|{
return|return
name|Defaults
operator|.
name|FIELD_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|defaultFieldDataType
specifier|public
name|FieldDataType
name|defaultFieldDataType
parameter_list|()
block|{
return|return
operator|new
name|FieldDataType
argument_list|(
literal|"long"
argument_list|)
return|;
block|}
DECL|method|parseValue
specifier|private
specifier|static
name|long
name|parseValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|BytesRef
condition|)
block|{
return|return
name|ipToLong
argument_list|(
operator|(
operator|(
name|BytesRef
operator|)
name|value
operator|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
return|;
block|}
return|return
name|ipToLong
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|innerParseCreateField
specifier|protected
name|void
name|innerParseCreateField
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|List
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|ipAsString
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|externalValueSet
argument_list|()
condition|)
block|{
name|ipAsString
operator|=
operator|(
name|String
operator|)
name|context
operator|.
name|externalValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|ipAsString
operator|==
literal|null
condition|)
block|{
name|ipAsString
operator|=
name|fieldType
argument_list|()
operator|.
name|nullValueAsString
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|context
operator|.
name|parser
argument_list|()
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NULL
condition|)
block|{
name|ipAsString
operator|=
name|fieldType
argument_list|()
operator|.
name|nullValueAsString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|ipAsString
operator|=
name|context
operator|.
name|parser
argument_list|()
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ipAsString
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|context
operator|.
name|includeInAll
argument_list|(
name|includeInAll
argument_list|,
name|this
argument_list|)
condition|)
block|{
name|context
operator|.
name|allEntries
argument_list|()
operator|.
name|addText
argument_list|(
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|,
name|ipAsString
argument_list|,
name|fieldType
argument_list|()
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|value
init|=
name|ipToLong
argument_list|(
name|ipAsString
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
operator|||
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
condition|)
block|{
name|CustomLongNumericField
name|field
init|=
operator|new
name|CustomLongNumericField
argument_list|(
name|value
argument_list|,
name|fieldType
argument_list|()
argument_list|)
decl_stmt|;
name|field
operator|.
name|setBoost
argument_list|(
name|fieldType
argument_list|()
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|addDocValue
argument_list|(
name|context
argument_list|,
name|fields
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|contentType
specifier|protected
name|String
name|contentType
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|protected
name|void
name|doXContentBody
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|boolean
name|includeDefaults
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|doXContentBody
argument_list|(
name|builder
argument_list|,
name|includeDefaults
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|numericPrecisionStep
argument_list|()
operator|!=
name|Defaults
operator|.
name|PRECISION_STEP_64_BIT
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"precision_step"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|numericPrecisionStep
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|nullValueAsString
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"null_value"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|nullValueAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeInAll
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"include_in_all"
argument_list|,
name|includeInAll
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeDefaults
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"include_in_all"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|NumericIpAnalyzer
specifier|public
specifier|static
class|class
name|NumericIpAnalyzer
extends|extends
name|NumericAnalyzer
argument_list|<
name|NumericIpTokenizer
argument_list|>
block|{
DECL|field|precisionStep
specifier|private
specifier|final
name|int
name|precisionStep
decl_stmt|;
DECL|method|NumericIpAnalyzer
specifier|public
name|NumericIpAnalyzer
parameter_list|(
name|int
name|precisionStep
parameter_list|)
block|{
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createNumericTokenizer
specifier|protected
name|NumericIpTokenizer
name|createNumericTokenizer
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|NumericIpTokenizer
argument_list|(
name|precisionStep
argument_list|,
name|buffer
argument_list|)
return|;
block|}
block|}
DECL|class|NumericIpTokenizer
specifier|public
specifier|static
class|class
name|NumericIpTokenizer
extends|extends
name|NumericTokenizer
block|{
DECL|method|NumericIpTokenizer
specifier|public
name|NumericIpTokenizer
parameter_list|(
name|int
name|precisionStep
parameter_list|,
name|char
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|NumericTokenStream
argument_list|(
name|precisionStep
argument_list|)
argument_list|,
name|buffer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setValue
specifier|protected
name|void
name|setValue
parameter_list|(
name|NumericTokenStream
name|tokenStream
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|tokenStream
operator|.
name|setLongValue
argument_list|(
name|ipToLong
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

