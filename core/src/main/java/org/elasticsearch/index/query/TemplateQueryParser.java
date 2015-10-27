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
name|HasContextAndHeaders
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
name|lease
operator|.
name|Releasables
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
name|XContent
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
name|*
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
name|mustache
operator|.
name|MustacheScriptEngineService
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
name|builder
operator|.
name|SearchSourceBuilder
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Strings
operator|.
name|hasLength
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
implements|implements
name|QueryParser
argument_list|<
name|TemplateQueryBuilder
argument_list|>
block|{
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
comment|/**      * Parses the template query replacing template parameters with provided      * values. Handles both submitting the template as part of the request as      * well as referencing only the template name.      *      * @param parseContext parse context containing the templated query.      */
annotation|@
name|Override
annotation|@
name|Nullable
DECL|method|fromXContent
specifier|public
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
name|parseFieldMatcher
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

