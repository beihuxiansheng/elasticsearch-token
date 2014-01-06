begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch.source
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|source
package|;
end_package

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
name|search
operator|.
name|SearchParseElement
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
name|internal
operator|.
name|SearchContext
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

begin_comment
comment|/**  *<pre>  * "source" : true/false  * "source" : "field"  * "source" : [ "include", "include" ]  * "source" : {  *     "include" : ["obj"]  *     "exclude" : ["obj"]  * }  *</pre>  */
end_comment

begin_class
DECL|class|FetchSourceParseElement
specifier|public
class|class
name|FetchSourceParseElement
implements|implements
name|SearchParseElement
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|includes
init|=
literal|null
decl_stmt|,
name|excludes
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|token
operator|=
name|parser
operator|.
name|currentToken
argument_list|()
expr_stmt|;
comment|// we get it on the value
if|if
condition|(
name|parser
operator|.
name|isBooleanValue
argument_list|()
condition|)
block|{
name|context
operator|.
name|fetchSourceContext
argument_list|(
operator|new
name|FetchSourceContext
argument_list|(
name|parser
operator|.
name|booleanValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
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
name|VALUE_STRING
condition|)
block|{
name|context
operator|.
name|fetchSourceContext
argument_list|(
operator|new
name|FetchSourceContext
argument_list|(
operator|new
name|String
index|[]
block|{
name|parser
operator|.
name|text
argument_list|()
block|}
argument_list|)
argument_list|)
expr_stmt|;
return|return;
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
name|START_ARRAY
condition|)
block|{
name|includes
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
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
name|END_ARRAY
condition|)
block|{
name|includes
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
name|List
argument_list|<
name|String
argument_list|>
name|currentList
init|=
literal|null
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
if|if
condition|(
literal|"includes"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"include"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|currentList
operator|=
name|includes
operator|!=
literal|null
condition|?
name|includes
else|:
operator|(
name|includes
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|2
argument_list|)
operator|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"excludes"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"exclude"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|currentList
operator|=
name|excludes
operator|!=
literal|null
condition|?
name|excludes
else|:
operator|(
name|excludes
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|2
argument_list|)
operator|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Source definition may not contain "
operator|+
name|parser
operator|.
name|text
argument_list|()
argument_list|)
throw|;
block|}
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
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|currentList
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
name|currentList
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
literal|"unexpected token while parsing source settings"
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
literal|"source element value can be of type "
operator|+
name|token
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
name|context
operator|.
name|fetchSourceContext
argument_list|(
operator|new
name|FetchSourceContext
argument_list|(
name|includes
operator|==
literal|null
condition|?
name|Strings
operator|.
name|EMPTY_ARRAY
else|:
name|includes
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|includes
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|excludes
operator|==
literal|null
condition|?
name|Strings
operator|.
name|EMPTY_ARRAY
else|:
name|excludes
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|excludes
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

