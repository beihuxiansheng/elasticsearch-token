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
name|queryparser
operator|.
name|XSimpleQueryParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Flags for the XSimpleQueryString parser  */
end_comment

begin_enum
DECL|enum|SimpleQueryStringFlag
specifier|public
enum|enum
name|SimpleQueryStringFlag
block|{
DECL|enum constant|ALL
name|ALL
argument_list|(
operator|-
literal|1
argument_list|)
block|,
DECL|enum constant|NONE
name|NONE
argument_list|(
literal|0
argument_list|)
block|,
DECL|enum constant|AND
name|AND
parameter_list|(
name|XSimpleQueryParser
operator|.
name|AND_OPERATOR
parameter_list|)
operator|,
DECL|enum constant|NOT
constructor|NOT(XSimpleQueryParser.NOT_OPERATOR
block|)
enum|,
DECL|enum constant|OR
name|OR
argument_list|(
name|XSimpleQueryParser
operator|.
name|OR_OPERATOR
argument_list|)
operator|,
DECL|enum constant|PREFIX
name|PREFIX
argument_list|(
name|XSimpleQueryParser
operator|.
name|PREFIX_OPERATOR
argument_list|)
operator|,
DECL|enum constant|PHRASE
name|PHRASE
argument_list|(
name|XSimpleQueryParser
operator|.
name|PHRASE_OPERATOR
argument_list|)
operator|,
DECL|enum constant|PRECEDENCE
name|PRECEDENCE
argument_list|(
name|XSimpleQueryParser
operator|.
name|PRECEDENCE_OPERATORS
argument_list|)
operator|,
DECL|enum constant|ESCAPE
name|ESCAPE
argument_list|(
name|XSimpleQueryParser
operator|.
name|ESCAPE_OPERATOR
argument_list|)
operator|,
DECL|enum constant|WHITESPACE
name|WHITESPACE
argument_list|(
name|XSimpleQueryParser
operator|.
name|WHITESPACE_OPERATOR
argument_list|)
operator|,
DECL|enum constant|FUZZY
name|FUZZY
argument_list|(
name|XSimpleQueryParser
operator|.
name|FUZZY_OPERATOR
argument_list|)
operator|,
comment|// NEAR and SLOP are synonymous, since "slop" is a more familiar term than "near"
DECL|enum constant|NEAR
name|NEAR
argument_list|(
name|XSimpleQueryParser
operator|.
name|NEAR_OPERATOR
argument_list|)
operator|,
DECL|enum constant|SLOP
name|SLOP
argument_list|(
name|XSimpleQueryParser
operator|.
name|NEAR_OPERATOR
argument_list|)
enum|;
end_enum

begin_decl_stmt
DECL|field|value
specifier|final
name|int
name|value
decl_stmt|;
end_decl_stmt

begin_constructor
DECL|method|SimpleQueryStringFlag
specifier|private
name|SimpleQueryStringFlag
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
end_constructor

begin_function
DECL|method|value
specifier|public
name|int
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
end_function

begin_function
DECL|method|resolveFlags
specifier|static
name|int
name|resolveFlags
parameter_list|(
name|String
name|flags
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasLength
argument_list|(
name|flags
argument_list|)
condition|)
block|{
return|return
name|ALL
operator|.
name|value
argument_list|()
return|;
block|}
name|int
name|magic
init|=
name|NONE
operator|.
name|value
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|Strings
operator|.
name|delimitedListToStringArray
argument_list|(
name|flags
argument_list|,
literal|"|"
argument_list|)
control|)
block|{
if|if
condition|(
name|s
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|SimpleQueryStringFlag
name|flag
init|=
name|SimpleQueryStringFlag
operator|.
name|valueOf
argument_list|(
name|s
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|flag
condition|)
block|{
case|case
name|NONE
case|:
return|return
literal|0
return|;
case|case
name|ALL
case|:
return|return
operator|-
literal|1
return|;
default|default:
name|magic
operator||=
name|flag
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Unknown "
operator|+
name|SimpleQueryStringParser
operator|.
name|NAME
operator|+
literal|" flag ["
operator|+
name|s
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
return|return
name|magic
return|;
block|}
end_function

unit|}
end_unit

