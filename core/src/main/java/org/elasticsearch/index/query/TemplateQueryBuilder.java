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
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ExecutableScript
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
name|ScriptService
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
name|Template
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
comment|/**  * Facilitates creating template query requests.  * */
end_comment

begin_class
DECL|class|TemplateQueryBuilder
specifier|public
class|class
name|TemplateQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|TemplateQueryBuilder
argument_list|>
block|{
comment|/** Name to reference this type of query. */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"template"
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
DECL|field|parametersToTypes
specifier|private
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptService
operator|.
name|ScriptType
argument_list|>
name|parametersToTypes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|parametersToTypes
operator|.
name|put
argument_list|(
literal|"query"
argument_list|,
name|ScriptService
operator|.
name|ScriptType
operator|.
name|INLINE
argument_list|)
expr_stmt|;
name|parametersToTypes
operator|.
name|put
argument_list|(
literal|"file"
argument_list|,
name|ScriptService
operator|.
name|ScriptType
operator|.
name|FILE
argument_list|)
expr_stmt|;
name|parametersToTypes
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|ScriptService
operator|.
name|ScriptType
operator|.
name|STORED
argument_list|)
expr_stmt|;
block|}
comment|/** Template to fill. */
DECL|field|template
specifier|private
specifier|final
name|Template
name|template
decl_stmt|;
comment|/**      * @param template      *            the template to use for that query.      * */
DECL|method|TemplateQueryBuilder
specifier|public
name|TemplateQueryBuilder
parameter_list|(
name|Template
name|template
parameter_list|)
block|{
if|if
condition|(
name|template
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"query template cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|template
operator|=
name|template
expr_stmt|;
block|}
DECL|method|template
specifier|public
name|Template
name|template
parameter_list|()
block|{
return|return
name|template
return|;
block|}
comment|/**      * @param template      *            the template to use for that query.      * @param vars      *            the parameters to fill the template with.      * @deprecated Use {@link #TemplateQueryBuilder(Template)} instead.      * */
annotation|@
name|Deprecated
DECL|method|TemplateQueryBuilder
specifier|public
name|TemplateQueryBuilder
parameter_list|(
name|String
name|template
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Template
argument_list|(
name|template
argument_list|,
name|ScriptService
operator|.
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|vars
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param template      *            the template to use for that query.      * @param vars      *            the parameters to fill the template with.      * @param templateType      *            what kind of template (INLINE,FILE,ID)      * @deprecated Use {@link #TemplateQueryBuilder(Template)} instead.      * */
annotation|@
name|Deprecated
DECL|method|TemplateQueryBuilder
specifier|public
name|TemplateQueryBuilder
parameter_list|(
name|String
name|template
parameter_list|,
name|ScriptService
operator|.
name|ScriptType
name|templateType
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Template
argument_list|(
name|template
argument_list|,
name|templateType
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|vars
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|TemplateQueryBuilder
specifier|public
name|TemplateQueryBuilder
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
name|template
operator|=
operator|new
name|Template
argument_list|(
name|in
argument_list|)
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
name|template
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
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
name|builderParams
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
name|TemplateQueryBuilder
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|template
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|builderParams
argument_list|)
expr_stmt|;
block|}
comment|/**      * In the simplest case, parse template string and variables from the request,      * compile the template and execute the template against the given variables.      */
DECL|method|fromXContent
specifier|public
specifier|static
name|TemplateQueryBuilder
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
name|Template
name|template
init|=
name|parse
argument_list|(
name|parser
argument_list|,
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|TemplateQueryBuilder
argument_list|(
name|template
argument_list|)
return|;
block|}
DECL|method|parse
specifier|public
specifier|static
name|Template
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|,
name|String
modifier|...
name|parameters
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptService
operator|.
name|ScriptType
argument_list|>
name|parameterMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|parametersToTypes
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|parameter
range|:
name|parameters
control|)
block|{
name|parameterMap
operator|.
name|put
argument_list|(
name|parameter
argument_list|,
name|ScriptService
operator|.
name|ScriptType
operator|.
name|INLINE
argument_list|)
expr_stmt|;
block|}
return|return
name|parse
argument_list|(
name|parser
argument_list|,
name|parameterMap
argument_list|,
name|parseFieldMatcher
argument_list|)
return|;
block|}
DECL|method|parse
specifier|public
specifier|static
name|Template
name|parse
parameter_list|(
name|String
name|defaultLang
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|,
name|String
modifier|...
name|parameters
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptService
operator|.
name|ScriptType
argument_list|>
name|parameterMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|parametersToTypes
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|parameter
range|:
name|parameters
control|)
block|{
name|parameterMap
operator|.
name|put
argument_list|(
name|parameter
argument_list|,
name|ScriptService
operator|.
name|ScriptType
operator|.
name|INLINE
argument_list|)
expr_stmt|;
block|}
return|return
name|Template
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|parameterMap
argument_list|,
name|defaultLang
argument_list|,
name|parseFieldMatcher
argument_list|)
return|;
block|}
DECL|method|parse
specifier|public
specifier|static
name|Template
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|parse
argument_list|(
name|parser
argument_list|,
name|parametersToTypes
argument_list|,
name|parseFieldMatcher
argument_list|)
return|;
block|}
DECL|method|parse
specifier|public
specifier|static
name|Template
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ScriptService
operator|.
name|ScriptType
argument_list|>
name|parameterMap
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Template
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|parameterMap
argument_list|,
name|parseFieldMatcher
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
name|Objects
operator|.
name|hash
argument_list|(
name|template
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
name|TemplateQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|template
argument_list|,
name|other
operator|.
name|template
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doRewrite
specifier|protected
name|QueryBuilder
name|doRewrite
parameter_list|(
name|QueryRewriteContext
name|queryRewriteContext
parameter_list|)
throws|throws
name|IOException
block|{
name|ExecutableScript
name|executable
init|=
name|queryRewriteContext
operator|.
name|getScriptService
argument_list|()
operator|.
name|executable
argument_list|(
name|template
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
argument_list|,
name|queryRewriteContext
operator|.
name|getClusterState
argument_list|()
argument_list|)
decl_stmt|;
name|BytesReference
name|querySource
init|=
operator|(
name|BytesReference
operator|)
name|executable
operator|.
name|run
argument_list|()
decl_stmt|;
try|try
init|(
name|XContentParser
name|qSourceParser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|querySource
argument_list|)
operator|.
name|createParser
argument_list|(
name|querySource
argument_list|)
init|)
block|{
specifier|final
name|QueryParseContext
name|queryParseContext
init|=
name|queryRewriteContext
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
name|queryParseContext
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

