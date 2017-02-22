begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|storedscripts
operator|.
name|GetStoredScriptResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|AbstractDiffable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|Diff
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
name|bytes
operator|.
name|BytesArray
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
name|NamedXContentRegistry
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
name|ObjectParser
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
name|ObjectParser
operator|.
name|ValueType
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
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
name|XContentType
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
name|io
operator|.
name|UncheckedIOException
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
comment|/**  * {@link StoredScriptSource} represents user-defined parameters for a script  * saved in the {@link ClusterState}.  */
end_comment

begin_class
DECL|class|StoredScriptSource
specifier|public
class|class
name|StoredScriptSource
extends|extends
name|AbstractDiffable
argument_list|<
name|StoredScriptSource
argument_list|>
implements|implements
name|Writeable
implements|,
name|ToXContent
block|{
comment|/**      * Standard {@link ParseField} for outer level of stored script source.      */
DECL|field|SCRIPT_PARSE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|SCRIPT_PARSE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"script"
argument_list|)
decl_stmt|;
comment|/**      * Standard {@link ParseField} for outer level of stored script source.      */
DECL|field|TEMPLATE_PARSE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|TEMPLATE_PARSE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"template"
argument_list|)
decl_stmt|;
comment|/**      * Standard {@link ParseField} for lang on the inner level.      */
DECL|field|LANG_PARSE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|LANG_PARSE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"lang"
argument_list|)
decl_stmt|;
comment|/**      * Standard {@link ParseField} for code on the inner level.      */
DECL|field|CODE_PARSE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|CODE_PARSE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"code"
argument_list|)
decl_stmt|;
comment|/**      * Standard {@link ParseField} for options on the inner level.      */
DECL|field|OPTIONS_PARSE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|OPTIONS_PARSE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"options"
argument_list|)
decl_stmt|;
comment|/**      * Helper class used by {@link ObjectParser} to store mutable {@link StoredScriptSource} variables and then      * construct an immutable {@link StoredScriptSource} object based on parsed XContent.      */
DECL|class|Builder
specifier|private
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|lang
specifier|private
name|String
name|lang
decl_stmt|;
DECL|field|code
specifier|private
name|String
name|code
decl_stmt|;
DECL|field|options
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
decl_stmt|;
DECL|method|Builder
specifier|private
name|Builder
parameter_list|()
block|{
comment|// This cannot default to an empty map because options are potentially added at multiple points.
name|this
operator|.
name|options
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|setLang
specifier|private
name|void
name|setLang
parameter_list|(
name|String
name|lang
parameter_list|)
block|{
name|this
operator|.
name|lang
operator|=
name|lang
expr_stmt|;
block|}
comment|/**          * Since stored scripts can accept templates rather than just scripts, they must also be able          * to handle template parsing, hence the need for custom parsing code.  Templates can          * consist of either an {@link String} or a JSON object.  If a JSON object is discovered          * then the content type option must also be saved as a compiler option.          */
DECL|method|setCode
specifier|private
name|void
name|setCode
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
comment|//this is really for search templates, that need to be converted to json format
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|code
operator|=
name|builder
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
operator|.
name|string
argument_list|()
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|Script
operator|.
name|CONTENT_TYPE_OPTION
argument_list|,
name|XContentType
operator|.
name|JSON
operator|.
name|mediaType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|code
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|exception
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|exception
argument_list|)
throw|;
block|}
block|}
comment|/**          * Options may have already been added if a template was specified.          * Appends the user-defined compiler options with the internal compiler options.          */
DECL|method|setOptions
specifier|private
name|void
name|setOptions
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
parameter_list|)
block|{
if|if
condition|(
name|options
operator|.
name|containsKey
argument_list|(
name|Script
operator|.
name|CONTENT_TYPE_OPTION
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|Script
operator|.
name|CONTENT_TYPE_OPTION
operator|+
literal|" cannot be user-specified"
argument_list|)
throw|;
block|}
name|this
operator|.
name|options
operator|.
name|putAll
argument_list|(
name|options
argument_list|)
expr_stmt|;
block|}
comment|/**          * Validates the parameters and creates an {@link StoredScriptSource}.          */
DECL|method|build
specifier|private
name|StoredScriptSource
name|build
parameter_list|()
block|{
if|if
condition|(
name|lang
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"must specify lang for stored script"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|lang
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lang cannot be empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|code
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"must specify code for stored script"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|code
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"code cannot be empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|options
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|||
name|options
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|options
operator|.
name|get
argument_list|(
name|Script
operator|.
name|CONTENT_TYPE_OPTION
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal compiler options ["
operator|+
name|options
operator|+
literal|"] specified"
argument_list|)
throw|;
block|}
return|return
operator|new
name|StoredScriptSource
argument_list|(
name|lang
argument_list|,
name|code
argument_list|,
name|options
argument_list|)
return|;
block|}
block|}
DECL|field|PARSER
specifier|private
specifier|static
specifier|final
name|ObjectParser
argument_list|<
name|Builder
argument_list|,
name|Void
argument_list|>
name|PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
literal|"stored script source"
argument_list|,
name|Builder
operator|::
operator|new
argument_list|)
decl_stmt|;
static|static
block|{
comment|// Defines the fields necessary to parse a Script as XContent using an ObjectParser.
name|PARSER
operator|.
name|declareString
argument_list|(
name|Builder
operator|::
name|setLang
argument_list|,
name|LANG_PARSE_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareField
argument_list|(
name|Builder
operator|::
name|setCode
argument_list|,
name|parser
lambda|->
name|parser
argument_list|,
name|CODE_PARSE_FIELD
argument_list|,
name|ValueType
operator|.
name|OBJECT_OR_STRING
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareField
argument_list|(
name|Builder
operator|::
name|setOptions
argument_list|,
name|XContentParser
operator|::
name|mapStrings
argument_list|,
name|OPTIONS_PARSE_FIELD
argument_list|,
name|ValueType
operator|.
name|OBJECT
argument_list|)
expr_stmt|;
block|}
comment|/**      * This will parse XContent into a {@link StoredScriptSource}.  The following formats can be parsed:      *      * The simple script format with no compiler options or user-defined params:      *      * Example:      * {@code      * {"script": "return Math.log(doc.popularity) * 100;"}      * }      *      * The above format requires the lang to be specified using the deprecated stored script namespace      * (as a url parameter during a put request).  See {@link ScriptMetaData} for more information about      * the stored script namespaces.      *      * The complex script format using the new stored script namespace      * where lang and code are required but options is optional:      *      * {@code      * {      *     "script" : {      *         "lang" : "<lang>",      *         "code" : "<code>",      *         "options" : {      *             "option0" : "<option0>",      *             "option1" : "<option1>",      *             ...      *         }      *     }      * }      * }      *      * Example:      * {@code      * {      *     "script": {      *         "lang" : "painless",      *         "code" : "return Math.log(doc.popularity) * params.multiplier"      *     }      * }      * }      *      * The simple template format:      *      * {@code      * {      *     "query" : ...      * }      * }      *      * The complex template format:      *      * {@code      * {      *     "template": {      *         "query" : ...      *     }      * }      * }      *      * Note that templates can be handled as both strings and complex JSON objects.      * Also templates may be part of the 'code' parameter in a script.  The Parser      * can handle this case as well.      *      * @param lang    An optional parameter to allow for use of the deprecated stored      *                script namespace.  This will be used to specify the language      *                coming in as a url parameter from a request or for stored templates.      * @param content The content from the request to be parsed as described above.      * @return        The parsed {@link StoredScriptSource}.      */
DECL|method|parse
specifier|public
specifier|static
name|StoredScriptSource
name|parse
parameter_list|(
name|String
name|lang
parameter_list|,
name|BytesReference
name|content
parameter_list|,
name|XContentType
name|xContentType
parameter_list|)
block|{
try|try
init|(
name|XContentParser
name|parser
init|=
name|xContentType
operator|.
name|xContent
argument_list|()
operator|.
name|createParser
argument_list|(
name|NamedXContentRegistry
operator|.
name|EMPTY
argument_list|,
name|content
argument_list|)
init|)
block|{
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
name|Token
operator|.
name|START_OBJECT
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
literal|"unexpected token ["
operator|+
name|token
operator|+
literal|"], expected [{]"
argument_list|)
throw|;
block|}
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|!=
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
literal|"unexpected token ["
operator|+
name|token
operator|+
literal|", expected ["
operator|+
name|SCRIPT_PARSE_FIELD
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|", "
operator|+
name|TEMPLATE_PARSE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|name
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
if|if
condition|(
name|SCRIPT_PARSE_FIELD
operator|.
name|getPreferredName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
if|if
condition|(
name|lang
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"must specify lang as a url parameter when using the deprecated stored script namespace"
argument_list|)
throw|;
block|}
return|return
operator|new
name|StoredScriptSource
argument_list|(
name|lang
argument_list|,
name|parser
operator|.
name|text
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
name|lang
operator|==
literal|null
condition|)
block|{
return|return
name|PARSER
operator|.
name|apply
argument_list|(
name|parser
argument_list|,
literal|null
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
else|else
block|{
comment|//this is really for search templates, that need to be converted to json format
try|try
init|(
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
init|)
block|{
name|builder
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
return|return
operator|new
name|StoredScriptSource
argument_list|(
name|lang
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|Script
operator|.
name|CONTENT_TYPE_OPTION
argument_list|,
name|XContentType
operator|.
name|JSON
operator|.
name|mediaType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
else|else
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
literal|"unexpected token ["
operator|+
name|token
operator|+
literal|"], expected [{,<code>]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|lang
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unexpected stored script format"
argument_list|)
throw|;
block|}
if|if
condition|(
name|TEMPLATE_PARSE_FIELD
operator|.
name|getPreferredName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
return|return
operator|new
name|StoredScriptSource
argument_list|(
name|lang
argument_list|,
name|parser
operator|.
name|text
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|Script
operator|.
name|CONTENT_TYPE_OPTION
argument_list|,
name|XContentType
operator|.
name|JSON
operator|.
name|mediaType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
try|try
init|(
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
init|)
block|{
if|if
condition|(
name|token
operator|!=
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|StoredScriptSource
argument_list|(
name|lang
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|Script
operator|.
name|CONTENT_TYPE_OPTION
argument_list|,
name|XContentType
operator|.
name|JSON
operator|.
name|mediaType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
comment|/**      * This will parse XContent into a {@link StoredScriptSource}. The following format is what will be parsed:      *      * {@code      * {      *     "script" : {      *         "lang" : "<lang>",      *         "code" : "<code>",      *         "options" : {      *             "option0" : "<option0>",      *             "option1" : "<option1>",      *             ...      *         }      *     }      * }      * }      *      * Note that the "code" parameter can also handle template parsing including from      * a complex JSON object.      */
DECL|method|fromXContent
specifier|public
specifier|static
name|StoredScriptSource
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|PARSER
operator|.
name|apply
argument_list|(
name|parser
argument_list|,
literal|null
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**      * Required for {@link ScriptMetaData.ScriptMetadataDiff}.  Uses      * the {@link StoredScriptSource#StoredScriptSource(StreamInput)}      * constructor.      */
DECL|method|readDiffFrom
specifier|public
specifier|static
name|Diff
argument_list|<
name|StoredScriptSource
argument_list|>
name|readDiffFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readDiffFrom
argument_list|(
name|StoredScriptSource
operator|::
operator|new
argument_list|,
name|in
argument_list|)
return|;
block|}
DECL|field|lang
specifier|private
specifier|final
name|String
name|lang
decl_stmt|;
DECL|field|code
specifier|private
specifier|final
name|String
name|code
decl_stmt|;
DECL|field|options
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
decl_stmt|;
comment|/**      * Constructor for use with {@link GetStoredScriptResponse}      * to support the deprecated stored script namespace.      */
DECL|method|StoredScriptSource
specifier|public
name|StoredScriptSource
parameter_list|(
name|String
name|code
parameter_list|)
block|{
name|this
operator|.
name|lang
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|code
argument_list|)
expr_stmt|;
name|this
operator|.
name|options
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Standard StoredScriptSource constructor.      * @param lang    The language to compile the script with.  Must not be {@code null}.      * @param code    The source code to compile with.  Must not be {@code null}.      * @param options Compiler options to be compiled with.  Must not be {@code null},      *                use an empty {@link Map} to represent no options.      */
DECL|method|StoredScriptSource
specifier|public
name|StoredScriptSource
parameter_list|(
name|String
name|lang
parameter_list|,
name|String
name|code
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
parameter_list|)
block|{
name|this
operator|.
name|lang
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|lang
argument_list|)
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|code
argument_list|)
expr_stmt|;
name|this
operator|.
name|options
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Reads a {@link StoredScriptSource} from a stream.  Version 5.3+ will read      * all of the lang, code, and options parameters.  For versions prior to 5.3,      * only the code parameter will be read in as a bytes reference.      */
DECL|method|StoredScriptSource
specifier|public
name|StoredScriptSource
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_5_3_0_UNRELEASED
argument_list|)
condition|)
block|{
name|this
operator|.
name|lang
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
init|=
call|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
call|)
argument_list|(
name|Map
argument_list|)
name|in
operator|.
name|readMap
argument_list|()
decl_stmt|;
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|lang
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|in
operator|.
name|readBytesReference
argument_list|()
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
name|this
operator|.
name|options
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      * Writes a {@link StoredScriptSource} to a stream.  Version 5.3+ will write      * all of the lang, code, and options parameters.  For versions prior to 5.3,      * only the code parameter will be read in as a bytes reference.      */
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
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_5_3_0_UNRELEASED
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|lang
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|code
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|options
init|=
call|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
call|)
argument_list|(
name|Map
argument_list|)
name|this
operator|.
name|options
decl_stmt|;
name|out
operator|.
name|writeMap
argument_list|(
name|options
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBytesReference
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|code
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This will write XContent from a {@link StoredScriptSource}. The following format will be written:      *      * {@code      * {      *     "script" : {      *         "lang" : "<lang>",      *         "code" : "<code>",      *         "options" : {      *             "option0" : "<option0>",      *             "option1" : "<option1>",      *             ...      *         }      *     }      * }      * }      *      * Note that the 'code' parameter can also handle templates written as complex JSON.      */
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
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|LANG_PARSE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|lang
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|CODE_PARSE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|code
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|OPTIONS_PARSE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|isFragment
specifier|public
name|boolean
name|isFragment
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @return The language used for compiling this script.      */
DECL|method|getLang
specifier|public
name|String
name|getLang
parameter_list|()
block|{
return|return
name|lang
return|;
block|}
comment|/**      * @return The code used for compiling this script.      */
DECL|method|getCode
specifier|public
name|String
name|getCode
parameter_list|()
block|{
return|return
name|code
return|;
block|}
comment|/**      * @return The compiler options used for this script.      */
DECL|method|getOptions
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getOptions
parameter_list|()
block|{
return|return
name|options
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|StoredScriptSource
name|that
init|=
operator|(
name|StoredScriptSource
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|lang
operator|!=
literal|null
condition|?
operator|!
name|lang
operator|.
name|equals
argument_list|(
name|that
operator|.
name|lang
argument_list|)
else|:
name|that
operator|.
name|lang
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|code
operator|!=
literal|null
condition|?
operator|!
name|code
operator|.
name|equals
argument_list|(
name|that
operator|.
name|code
argument_list|)
else|:
name|that
operator|.
name|code
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|options
operator|!=
literal|null
condition|?
name|options
operator|.
name|equals
argument_list|(
name|that
operator|.
name|options
argument_list|)
else|:
name|that
operator|.
name|options
operator|==
literal|null
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
name|int
name|result
init|=
name|lang
operator|!=
literal|null
condition|?
name|lang
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|code
operator|!=
literal|null
condition|?
name|code
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|options
operator|!=
literal|null
condition|?
name|options
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
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
literal|"StoredScriptSource{"
operator|+
literal|"lang='"
operator|+
name|lang
operator|+
literal|'\''
operator|+
literal|", code='"
operator|+
name|code
operator|+
literal|'\''
operator|+
literal|", options="
operator|+
name|options
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

