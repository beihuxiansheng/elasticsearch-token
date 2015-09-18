begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.cache.query.terms
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|cache
operator|.
name|query
operator|.
name|terms
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
name|ToXContent
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Encapsulates the parameters needed to fetch terms.  */
end_comment

begin_class
DECL|class|TermsLookup
specifier|public
class|class
name|TermsLookup
implements|implements
name|Writeable
argument_list|<
name|TermsLookup
argument_list|>
implements|,
name|ToXContent
block|{
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|TermsLookup
name|PROTOTYPE
init|=
operator|new
name|TermsLookup
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
literal|"path"
argument_list|)
decl_stmt|;
DECL|field|index
specifier|private
name|String
name|index
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|routing
specifier|private
name|String
name|routing
decl_stmt|;
DECL|method|TermsLookup
specifier|public
name|TermsLookup
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[terms] query lookup element requires specifying the id."
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
literal|"[terms] query lookup element requires specifying the type."
argument_list|)
throw|;
block|}
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[terms] query lookup element requires specifying the path."
argument_list|)
throw|;
block|}
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|index
specifier|public
name|TermsLookup
name|index
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|id
specifier|public
name|String
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|path
specifier|public
name|String
name|path
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|routing
specifier|public
name|String
name|routing
parameter_list|()
block|{
return|return
name|routing
return|;
block|}
DECL|method|routing
specifier|public
name|TermsLookup
name|routing
parameter_list|(
name|String
name|routing
parameter_list|)
block|{
name|this
operator|.
name|routing
operator|=
name|routing
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|parseTermsLookup
specifier|public
specifier|static
name|TermsLookup
name|parseTermsLookup
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|index
init|=
literal|null
decl_stmt|;
name|String
name|type
init|=
literal|null
decl_stmt|;
name|String
name|id
init|=
literal|null
decl_stmt|;
name|String
name|path
init|=
literal|null
decl_stmt|;
name|String
name|routing
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|""
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
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|currentFieldName
condition|)
block|{
case|case
literal|"index"
case|:
name|index
operator|=
name|parser
operator|.
name|textOrNull
argument_list|()
expr_stmt|;
break|break;
case|case
literal|"type"
case|:
name|type
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
break|break;
case|case
literal|"id"
case|:
name|id
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
break|break;
case|case
literal|"routing"
case|:
name|routing
operator|=
name|parser
operator|.
name|textOrNull
argument_list|()
expr_stmt|;
break|break;
case|case
literal|"path"
case|:
name|path
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[terms] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"] within lookup element"
argument_list|)
throw|;
block|}
block|}
block|}
return|return
operator|new
name|TermsLookup
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|path
argument_list|)
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
return|;
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
name|index
operator|+
literal|"/"
operator|+
name|type
operator|+
literal|"/"
operator|+
name|id
operator|+
literal|"/"
operator|+
name|path
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|TermsLookup
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|type
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|id
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|index
init|=
name|in
operator|.
name|readOptionalString
argument_list|()
decl_stmt|;
name|TermsLookup
name|termsLookup
init|=
operator|new
name|TermsLookup
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|termsLookup
operator|.
name|routing
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
return|return
name|termsLookup
return|;
block|}
DECL|method|readTermsLookupFrom
specifier|public
specifier|static
name|TermsLookup
name|readTermsLookupFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|PROTOTYPE
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
return|;
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
name|out
operator|.
name|writeString
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|routing
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"path"
argument_list|,
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|routing
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"routing"
argument_list|,
name|routing
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
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
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|path
argument_list|,
name|routing
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
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TermsLookup
name|other
init|=
operator|(
name|TermsLookup
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|index
argument_list|,
name|other
operator|.
name|index
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
name|id
argument_list|,
name|other
operator|.
name|id
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|path
argument_list|,
name|other
operator|.
name|path
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|routing
argument_list|,
name|other
operator|.
name|routing
argument_list|)
return|;
block|}
block|}
end_class

end_unit

