begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|support
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
name|search
operator|.
name|Filter
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
name|join
operator|.
name|BitSetProducer
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
name|bytes
operator|.
name|BytesReference
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
name|search
operator|.
name|Queries
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
name|XContentFactory
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
name|XContentHelper
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
name|object
operator|.
name|ObjectMapper
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
name|QueryParsingException
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A helper that helps with parsing inner queries of the nested query.  * 1) Takes into account that type nested path can appear before or after the inner query  * 2) Updates the {@link NestedScope} when parsing the inner query.  */
end_comment

begin_class
DECL|class|NestedInnerQueryParseSupport
specifier|public
class|class
name|NestedInnerQueryParseSupport
block|{
DECL|field|parseContext
specifier|protected
specifier|final
name|QueryParseContext
name|parseContext
decl_stmt|;
DECL|field|source
specifier|private
name|BytesReference
name|source
decl_stmt|;
DECL|field|innerQuery
specifier|private
name|Query
name|innerQuery
decl_stmt|;
DECL|field|innerFilter
specifier|private
name|Query
name|innerFilter
decl_stmt|;
DECL|field|path
specifier|protected
name|String
name|path
decl_stmt|;
DECL|field|filterParsed
specifier|private
name|boolean
name|filterParsed
init|=
literal|false
decl_stmt|;
DECL|field|queryParsed
specifier|private
name|boolean
name|queryParsed
init|=
literal|false
decl_stmt|;
DECL|field|queryFound
specifier|protected
name|boolean
name|queryFound
init|=
literal|false
decl_stmt|;
DECL|field|filterFound
specifier|protected
name|boolean
name|filterFound
init|=
literal|false
decl_stmt|;
DECL|field|parentFilter
specifier|protected
name|BitSetProducer
name|parentFilter
decl_stmt|;
DECL|field|childFilter
specifier|protected
name|Filter
name|childFilter
decl_stmt|;
DECL|field|nestedObjectMapper
specifier|protected
name|ObjectMapper
name|nestedObjectMapper
decl_stmt|;
DECL|field|parentObjectMapper
specifier|private
name|ObjectMapper
name|parentObjectMapper
decl_stmt|;
DECL|method|NestedInnerQueryParseSupport
specifier|public
name|NestedInnerQueryParseSupport
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|searchContext
parameter_list|)
block|{
name|parseContext
operator|=
name|searchContext
operator|.
name|queryParserService
argument_list|()
operator|.
name|getParseContext
argument_list|()
expr_stmt|;
name|parseContext
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
DECL|method|NestedInnerQueryParseSupport
specifier|public
name|NestedInnerQueryParseSupport
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
block|{
name|this
operator|.
name|parseContext
operator|=
name|parseContext
expr_stmt|;
block|}
DECL|method|query
specifier|public
name|void
name|query
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|setPathLevel
argument_list|()
expr_stmt|;
try|try
block|{
name|innerQuery
operator|=
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|resetPathLevel
argument_list|()
expr_stmt|;
block|}
name|queryParsed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|source
operator|=
name|XContentFactory
operator|.
name|smileBuilder
argument_list|()
operator|.
name|copyCurrentStructure
argument_list|(
name|parseContext
operator|.
name|parser
argument_list|()
argument_list|)
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
name|queryFound
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|filter
specifier|public
name|void
name|filter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|setPathLevel
argument_list|()
expr_stmt|;
try|try
block|{
name|innerFilter
operator|=
name|parseContext
operator|.
name|parseInnerFilter
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|resetPathLevel
argument_list|()
expr_stmt|;
block|}
name|filterParsed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|source
operator|=
name|XContentFactory
operator|.
name|smileBuilder
argument_list|()
operator|.
name|copyCurrentStructure
argument_list|(
name|parseContext
operator|.
name|parser
argument_list|()
argument_list|)
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
name|filterFound
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getInnerQuery
specifier|public
name|Query
name|getInnerQuery
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|queryParsed
condition|)
block|{
return|return
name|innerQuery
return|;
block|}
else|else
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[nested] requires 'path' field"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|queryFound
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[nested] requires either 'query' or 'filter' field"
argument_list|)
throw|;
block|}
name|XContentParser
name|old
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
try|try
block|{
name|XContentParser
name|innerParser
init|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|parseContext
operator|.
name|parser
argument_list|(
name|innerParser
argument_list|)
expr_stmt|;
name|setPathLevel
argument_list|()
expr_stmt|;
try|try
block|{
name|innerQuery
operator|=
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|resetPathLevel
argument_list|()
expr_stmt|;
block|}
name|queryParsed
operator|=
literal|true
expr_stmt|;
return|return
name|innerQuery
return|;
block|}
finally|finally
block|{
name|parseContext
operator|.
name|parser
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getInnerFilter
specifier|public
name|Query
name|getInnerFilter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|filterParsed
condition|)
block|{
return|return
name|innerFilter
return|;
block|}
else|else
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[nested] requires 'path' field"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|filterFound
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[nested] requires either 'query' or 'filter' field"
argument_list|)
throw|;
block|}
name|setPathLevel
argument_list|()
expr_stmt|;
name|XContentParser
name|old
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
try|try
block|{
name|XContentParser
name|innerParser
init|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|parseContext
operator|.
name|parser
argument_list|(
name|innerParser
argument_list|)
expr_stmt|;
name|innerFilter
operator|=
name|parseContext
operator|.
name|parseInnerFilter
argument_list|()
expr_stmt|;
name|filterParsed
operator|=
literal|true
expr_stmt|;
return|return
name|innerFilter
return|;
block|}
finally|finally
block|{
name|resetPathLevel
argument_list|()
expr_stmt|;
name|parseContext
operator|.
name|parser
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setPath
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|nestedObjectMapper
operator|=
name|parseContext
operator|.
name|getObjectMapper
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|nestedObjectMapper
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[nested] failed to find nested object under path ["
operator|+
name|path
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|nestedObjectMapper
operator|.
name|nested
argument_list|()
operator|.
name|isNested
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[nested] nested object under path ["
operator|+
name|path
operator|+
literal|"] is not of nested type"
argument_list|)
throw|;
block|}
block|}
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|getNestedObjectMapper
specifier|public
name|ObjectMapper
name|getNestedObjectMapper
parameter_list|()
block|{
return|return
name|nestedObjectMapper
return|;
block|}
DECL|method|queryFound
specifier|public
name|boolean
name|queryFound
parameter_list|()
block|{
return|return
name|queryFound
return|;
block|}
DECL|method|filterFound
specifier|public
name|boolean
name|filterFound
parameter_list|()
block|{
return|return
name|filterFound
return|;
block|}
DECL|method|getParentObjectMapper
specifier|public
name|ObjectMapper
name|getParentObjectMapper
parameter_list|()
block|{
return|return
name|parentObjectMapper
return|;
block|}
DECL|method|setPathLevel
specifier|private
name|void
name|setPathLevel
parameter_list|()
block|{
name|ObjectMapper
name|objectMapper
init|=
name|parseContext
operator|.
name|nestedScope
argument_list|()
operator|.
name|getObjectMapper
argument_list|()
decl_stmt|;
if|if
condition|(
name|objectMapper
operator|==
literal|null
condition|)
block|{
name|parentFilter
operator|=
name|parseContext
operator|.
name|bitsetFilter
argument_list|(
name|Queries
operator|.
name|newNonNestedFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parentFilter
operator|=
name|parseContext
operator|.
name|bitsetFilter
argument_list|(
name|objectMapper
operator|.
name|nestedTypeFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|childFilter
operator|=
name|nestedObjectMapper
operator|.
name|nestedTypeFilter
argument_list|()
expr_stmt|;
name|parentObjectMapper
operator|=
name|parseContext
operator|.
name|nestedScope
argument_list|()
operator|.
name|nextLevel
argument_list|(
name|nestedObjectMapper
argument_list|)
expr_stmt|;
block|}
DECL|method|resetPathLevel
specifier|private
name|void
name|resetPathLevel
parameter_list|()
block|{
name|parseContext
operator|.
name|nestedScope
argument_list|()
operator|.
name|previousLevel
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

