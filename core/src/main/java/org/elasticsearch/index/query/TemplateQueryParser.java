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

begin_comment
comment|/**  * In the simplest case, parse template string and variables from the request,  * compile the template and execute the template against the given variables.  * */
end_comment

begin_class
DECL|class|TemplateQueryParser
specifier|public
class|class
name|TemplateQueryParser
extends|extends
name|BaseQueryParserTemp
block|{
comment|/** Name of query parameter containing the template string. */
DECL|field|QUERY
specifier|public
specifier|static
specifier|final
name|String
name|QUERY
init|=
literal|"query"
decl_stmt|;
DECL|field|scriptService
specifier|private
specifier|final
name|ScriptService
name|scriptService
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
name|INDEXED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Inject
DECL|method|TemplateQueryParser
specifier|public
name|TemplateQueryParser
parameter_list|(
name|ScriptService
name|scriptService
parameter_list|)
block|{
name|this
operator|.
name|scriptService
operator|=
name|scriptService
expr_stmt|;
block|}
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
name|TemplateQueryBuilder
operator|.
name|NAME
block|}
return|;
block|}
comment|/**      * Parses the template query replacing template parameters with provided      * values. Handles both submitting the template as part of the request as      * well as referencing only the template name.      *       * @param parseContext      *            parse context containing the templated query.      */
annotation|@
name|Override
annotation|@
name|Nullable
DECL|method|parse
specifier|public
name|Query
name|parse
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
name|parseFieldMatcher
argument_list|()
argument_list|)
decl_stmt|;
name|ExecutableScript
name|executable
init|=
name|this
operator|.
name|scriptService
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
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
name|parseContext
operator|.
name|indexQueryParserService
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|reset
argument_list|(
name|qSourceParser
argument_list|)
expr_stmt|;
return|return
name|context
operator|.
name|parseInnerQuery
argument_list|()
return|;
block|}
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
DECL|method|getBuilderPrototype
specifier|public
name|TemplateQueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|TemplateQueryBuilder
operator|.
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit

