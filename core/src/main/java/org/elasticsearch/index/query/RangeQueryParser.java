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
name|inject
operator|.
name|Inject
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

begin_comment
comment|/**  * Parser for range query  */
end_comment

begin_class
DECL|class|RangeQueryParser
specifier|public
class|class
name|RangeQueryParser
extends|extends
name|BaseQueryParser
argument_list|<
name|RangeQueryBuilder
argument_list|>
block|{
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
annotation|@
name|Inject
DECL|method|RangeQueryParser
specifier|public
name|RangeQueryParser
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|names
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|RangeQueryBuilder
operator|.
name|NAME
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|RangeQueryBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
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
literal|"from"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"to"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"include_lower"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"includeLower"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"include_upper"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"includeUpper"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"gt"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"gte"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"ge"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"lt"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"lte"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"le"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"time_zone"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"timeZone"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"format"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
name|QueryParsingException
argument_list|(
name|parseContext
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
name|QueryParsingException
argument_list|(
name|parseContext
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
name|rangeQuery
operator|.
name|timeZone
argument_list|(
name|timeZone
argument_list|)
expr_stmt|;
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
name|rangeQuery
operator|.
name|format
argument_list|(
name|format
argument_list|)
expr_stmt|;
return|return
name|rangeQuery
return|;
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|RangeQueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|RangeQueryBuilder
operator|.
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit
