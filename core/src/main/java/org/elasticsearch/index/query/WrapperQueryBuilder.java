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
name|search
operator|.
name|Query
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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

begin_comment
comment|/**  * A Query builder which allows building a query given JSON string or binary data provided as input. This is useful when you want  * to use the Java Builder API but still have JSON query strings at hand that you want to combine with other  * query builders.  *<p>  * Example usage in a boolean query :  *<pre>  *<code>  *      BoolQueryBuilder bool = new BoolQueryBuilder();  *      bool.must(new WrapperQueryBuilder("{\"term\": {\"field\":\"value\"}}");  *      bool.must(new TermQueryBuilder("field2","value2");  *</code>  *</pre>  */
end_comment

begin_class
DECL|class|WrapperQueryBuilder
specifier|public
class|class
name|WrapperQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|WrapperQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"wrapper"
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
DECL|field|QUERY_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|QUERY_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"query"
argument_list|)
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|byte
index|[]
name|source
decl_stmt|;
comment|/**      * Creates a query builder given a query provided as a bytes array      */
DECL|method|WrapperQueryBuilder
specifier|public
name|WrapperQueryBuilder
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
operator|||
name|source
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"query source text cannot be null or empty"
argument_list|)
throw|;
block|}
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
comment|/**      * Creates a query builder given a query provided as a string      */
DECL|method|WrapperQueryBuilder
specifier|public
name|WrapperQueryBuilder
parameter_list|(
name|String
name|source
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|source
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"query source string cannot be null or empty"
argument_list|)
throw|;
block|}
name|this
operator|.
name|source
operator|=
name|source
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a query builder given a query provided as a {@link BytesReference}      */
DECL|method|WrapperQueryBuilder
specifier|public
name|WrapperQueryBuilder
parameter_list|(
name|BytesReference
name|source
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
operator|||
name|source
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"query source text cannot be null or empty"
argument_list|)
throw|;
block|}
name|this
operator|.
name|source
operator|=
name|source
operator|.
name|array
argument_list|()
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|WrapperQueryBuilder
specifier|public
name|WrapperQueryBuilder
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
name|source
operator|=
name|in
operator|.
name|readByteArray
argument_list|()
expr_stmt|;
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
name|writeByteArray
argument_list|(
name|this
operator|.
name|source
argument_list|)
expr_stmt|;
block|}
DECL|method|source
specifier|public
name|byte
index|[]
name|source
parameter_list|()
block|{
return|return
name|this
operator|.
name|source
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAME
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
name|field
argument_list|(
name|QUERY_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|source
argument_list|)
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
name|WrapperQueryBuilder
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
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
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
literal|"[wrapper] query malformed"
argument_list|)
throw|;
block|}
name|String
name|fieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|fieldName
argument_list|,
name|QUERY_FIELD
argument_list|)
condition|)
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
literal|"[wrapper] query malformed, expected `query` but was"
operator|+
name|fieldName
argument_list|)
throw|;
block|}
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|byte
index|[]
name|source
init|=
name|parser
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
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
literal|"wrapper query has no [query] specified"
argument_list|)
throw|;
block|}
return|return
operator|new
name|WrapperQueryBuilder
argument_list|(
name|source
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this query must be rewritten first"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|hashCode
argument_list|(
name|source
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
name|WrapperQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|source
argument_list|,
name|other
operator|.
name|source
argument_list|)
return|;
comment|// otherwise we compare pointers
block|}
annotation|@
name|Override
DECL|method|doRewrite
specifier|protected
name|QueryBuilder
name|doRewrite
parameter_list|(
name|QueryRewriteContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|XContentParser
name|qSourceParser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|source
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
init|)
block|{
name|QueryParseContext
name|parseContext
init|=
name|context
operator|.
name|newParseContext
argument_list|(
name|qSourceParser
argument_list|)
decl_stmt|;
specifier|final
name|QueryBuilder
name|queryBuilder
init|=
name|parseContext
operator|.
name|parseInnerQueryBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|boost
argument_list|()
operator|!=
name|DEFAULT_BOOST
operator|||
name|queryName
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|BoolQueryBuilder
name|boolQueryBuilder
init|=
operator|new
name|BoolQueryBuilder
argument_list|()
decl_stmt|;
name|boolQueryBuilder
operator|.
name|must
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|boolQueryBuilder
return|;
block|}
return|return
name|queryBuilder
return|;
block|}
block|}
block|}
end_class

end_unit

