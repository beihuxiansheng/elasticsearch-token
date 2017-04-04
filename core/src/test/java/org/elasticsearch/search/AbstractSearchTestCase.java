begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchRequest
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
name|CheckedFunction
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
name|io
operator|.
name|stream
operator|.
name|NamedWriteableRegistry
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
name|settings
operator|.
name|Settings
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
name|indices
operator|.
name|IndicesModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|SearchPlugin
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|collapse
operator|.
name|CollapseBuilderTests
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
name|fetch
operator|.
name|subphase
operator|.
name|highlight
operator|.
name|HighlightBuilderTests
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
name|rescore
operator|.
name|QueryRescoreBuilderTests
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
name|suggest
operator|.
name|SuggestBuilderTests
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
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
name|ArrayList
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
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_class
DECL|class|AbstractSearchTestCase
specifier|public
specifier|abstract
class|class
name|AbstractSearchTestCase
extends|extends
name|ESTestCase
block|{
DECL|field|namedWriteableRegistry
specifier|protected
name|NamedWriteableRegistry
name|namedWriteableRegistry
decl_stmt|;
DECL|field|searchExtPlugin
specifier|private
name|TestSearchExtPlugin
name|searchExtPlugin
decl_stmt|;
DECL|field|xContentRegistry
specifier|private
name|NamedXContentRegistry
name|xContentRegistry
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|IndicesModule
name|indicesModule
init|=
operator|new
name|IndicesModule
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|searchExtPlugin
operator|=
operator|new
name|TestSearchExtPlugin
argument_list|()
expr_stmt|;
name|SearchModule
name|searchModule
init|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|false
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|searchExtPlugin
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NamedWriteableRegistry
operator|.
name|Entry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|entries
operator|.
name|addAll
argument_list|(
name|indicesModule
operator|.
name|getNamedWriteables
argument_list|()
argument_list|)
expr_stmt|;
name|entries
operator|.
name|addAll
argument_list|(
name|searchModule
operator|.
name|getNamedWriteables
argument_list|()
argument_list|)
expr_stmt|;
name|namedWriteableRegistry
operator|=
operator|new
name|NamedWriteableRegistry
argument_list|(
name|entries
argument_list|)
expr_stmt|;
name|xContentRegistry
operator|=
operator|new
name|NamedXContentRegistry
argument_list|(
name|searchModule
operator|.
name|getNamedXContents
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|xContentRegistry
specifier|protected
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|()
block|{
return|return
name|xContentRegistry
return|;
block|}
DECL|method|createSearchSourceBuilder
specifier|protected
name|SearchSourceBuilder
name|createSearchSourceBuilder
parameter_list|()
block|{
name|Supplier
argument_list|<
name|List
argument_list|<
name|SearchExtBuilder
argument_list|>
argument_list|>
name|randomExtBuilders
init|=
parameter_list|()
lambda|->
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|elementNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|searchExtPlugin
operator|.
name|getSupportedElements
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numSearchExts
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|elementNames
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|elementNames
operator|.
name|size
argument_list|()
operator|>
name|numSearchExts
condition|)
block|{
name|elementNames
operator|.
name|remove
argument_list|(
name|randomFrom
argument_list|(
name|elementNames
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|SearchExtBuilder
argument_list|>
name|searchExtBuilders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|elementName
range|:
name|elementNames
control|)
block|{
name|searchExtBuilders
operator|.
name|add
argument_list|(
name|searchExtPlugin
operator|.
name|getSupportedElements
argument_list|()
operator|.
name|get
argument_list|(
name|elementName
argument_list|)
operator|.
name|apply
argument_list|(
name|randomAlphaOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|searchExtBuilders
return|;
block|}
decl_stmt|;
return|return
name|RandomSearchRequestGenerator
operator|.
name|randomSearchSourceBuilder
argument_list|(
name|HighlightBuilderTests
operator|::
name|randomHighlighterBuilder
argument_list|,
name|SuggestBuilderTests
operator|::
name|randomSuggestBuilder
argument_list|,
name|QueryRescoreBuilderTests
operator|::
name|randomRescoreBuilder
argument_list|,
name|randomExtBuilders
argument_list|,
name|CollapseBuilderTests
operator|::
name|randomCollapseBuilder
argument_list|)
return|;
block|}
DECL|method|createSearchRequest
specifier|protected
name|SearchRequest
name|createSearchRequest
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|RandomSearchRequestGenerator
operator|.
name|randomSearchRequest
argument_list|(
name|this
operator|::
name|createSearchSourceBuilder
argument_list|)
return|;
block|}
DECL|class|TestSearchExtPlugin
specifier|private
specifier|static
class|class
name|TestSearchExtPlugin
extends|extends
name|Plugin
implements|implements
name|SearchPlugin
block|{
DECL|field|searchExtSpecs
specifier|private
specifier|final
name|List
argument_list|<
name|SearchExtSpec
argument_list|<
name|?
extends|extends
name|SearchExtBuilder
argument_list|>
argument_list|>
name|searchExtSpecs
decl_stmt|;
DECL|field|supportedElements
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Function
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SearchExtBuilder
argument_list|>
argument_list|>
name|supportedElements
decl_stmt|;
DECL|method|TestSearchExtPlugin
specifier|private
name|TestSearchExtPlugin
parameter_list|()
block|{
name|int
name|numSearchExts
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|this
operator|.
name|searchExtSpecs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numSearchExts
argument_list|)
expr_stmt|;
name|this
operator|.
name|supportedElements
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSearchExts
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
if|if
condition|(
name|this
operator|.
name|supportedElements
operator|.
name|put
argument_list|(
name|TestSearchExtBuilder1
operator|.
name|NAME
argument_list|,
name|TestSearchExtBuilder1
operator|::
operator|new
argument_list|)
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|searchExtSpecs
operator|.
name|add
argument_list|(
operator|new
name|SearchExtSpec
argument_list|<>
argument_list|(
name|TestSearchExtBuilder1
operator|.
name|NAME
argument_list|,
name|TestSearchExtBuilder1
operator|::
operator|new
argument_list|,
operator|new
name|TestSearchExtParser
argument_list|<>
argument_list|(
name|TestSearchExtBuilder1
operator|::
operator|new
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|1
case|:
if|if
condition|(
name|this
operator|.
name|supportedElements
operator|.
name|put
argument_list|(
name|TestSearchExtBuilder2
operator|.
name|NAME
argument_list|,
name|TestSearchExtBuilder2
operator|::
operator|new
argument_list|)
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|searchExtSpecs
operator|.
name|add
argument_list|(
operator|new
name|SearchExtSpec
argument_list|<>
argument_list|(
name|TestSearchExtBuilder2
operator|.
name|NAME
argument_list|,
name|TestSearchExtBuilder2
operator|::
operator|new
argument_list|,
operator|new
name|TestSearchExtParser
argument_list|<>
argument_list|(
name|TestSearchExtBuilder2
operator|::
operator|new
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|2
case|:
if|if
condition|(
name|this
operator|.
name|supportedElements
operator|.
name|put
argument_list|(
name|TestSearchExtBuilder3
operator|.
name|NAME
argument_list|,
name|TestSearchExtBuilder3
operator|::
operator|new
argument_list|)
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|searchExtSpecs
operator|.
name|add
argument_list|(
operator|new
name|SearchExtSpec
argument_list|<>
argument_list|(
name|TestSearchExtBuilder3
operator|.
name|NAME
argument_list|,
name|TestSearchExtBuilder3
operator|::
operator|new
argument_list|,
operator|new
name|TestSearchExtParser
argument_list|<>
argument_list|(
name|TestSearchExtBuilder3
operator|::
operator|new
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
DECL|method|getSupportedElements
name|Map
argument_list|<
name|String
argument_list|,
name|Function
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SearchExtBuilder
argument_list|>
argument_list|>
name|getSupportedElements
parameter_list|()
block|{
return|return
name|supportedElements
return|;
block|}
annotation|@
name|Override
DECL|method|getSearchExts
specifier|public
name|List
argument_list|<
name|SearchExtSpec
argument_list|<
name|?
argument_list|>
argument_list|>
name|getSearchExts
parameter_list|()
block|{
return|return
name|searchExtSpecs
return|;
block|}
block|}
DECL|class|TestSearchExtParser
specifier|private
specifier|static
class|class
name|TestSearchExtParser
parameter_list|<
name|T
extends|extends
name|SearchExtBuilder
parameter_list|>
implements|implements
name|CheckedFunction
argument_list|<
name|XContentParser
argument_list|,
name|T
argument_list|,
name|IOException
argument_list|>
block|{
DECL|field|searchExtBuilderFunction
specifier|private
specifier|final
name|Function
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|searchExtBuilderFunction
decl_stmt|;
DECL|method|TestSearchExtParser
name|TestSearchExtParser
parameter_list|(
name|Function
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|searchExtBuilderFunction
parameter_list|)
block|{
name|this
operator|.
name|searchExtBuilderFunction
operator|=
name|searchExtBuilderFunction
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply
specifier|public
name|T
name|apply
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|searchExtBuilderFunction
operator|.
name|apply
argument_list|(
name|parseField
argument_list|(
name|parser
argument_list|)
argument_list|)
return|;
block|}
DECL|method|parseField
name|String
name|parseField
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|!=
name|XContentParser
operator|.
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
literal|"start_object expected, found "
operator|+
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
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
literal|"field_name expected, found "
operator|+
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|field
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
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
literal|"start_object expected, found "
operator|+
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
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
literal|"end_object expected, found "
operator|+
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
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
literal|"end_object expected, found "
operator|+
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|field
return|;
block|}
block|}
comment|//Would be nice to have a single builder that gets its name as a parameter, but the name wouldn't get a value when the object
comment|//is created reading from the stream (constructor that takes a StreamInput) which is a problem as we check that after reading
comment|//a named writeable its name is the expected one. That's why we go for the following less dynamic approach.
DECL|class|TestSearchExtBuilder1
specifier|private
specifier|static
class|class
name|TestSearchExtBuilder1
extends|extends
name|TestSearchExtBuilder
block|{
DECL|field|NAME
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"name1"
decl_stmt|;
DECL|method|TestSearchExtBuilder1
name|TestSearchExtBuilder1
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|TestSearchExtBuilder1
name|TestSearchExtBuilder1
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestSearchExtBuilder2
specifier|private
specifier|static
class|class
name|TestSearchExtBuilder2
extends|extends
name|TestSearchExtBuilder
block|{
DECL|field|NAME
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"name2"
decl_stmt|;
DECL|method|TestSearchExtBuilder2
name|TestSearchExtBuilder2
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|TestSearchExtBuilder2
name|TestSearchExtBuilder2
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestSearchExtBuilder3
specifier|private
specifier|static
class|class
name|TestSearchExtBuilder3
extends|extends
name|TestSearchExtBuilder
block|{
DECL|field|NAME
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"name3"
decl_stmt|;
DECL|method|TestSearchExtBuilder3
name|TestSearchExtBuilder3
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|TestSearchExtBuilder3
name|TestSearchExtBuilder3
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestSearchExtBuilder
specifier|private
specifier|abstract
specifier|static
class|class
name|TestSearchExtBuilder
extends|extends
name|SearchExtBuilder
block|{
DECL|field|objectName
specifier|final
name|String
name|objectName
decl_stmt|;
DECL|field|name
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|TestSearchExtBuilder
name|TestSearchExtBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|objectName
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|objectName
operator|=
name|objectName
expr_stmt|;
block|}
DECL|method|TestSearchExtBuilder
name|TestSearchExtBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|objectName
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
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
name|objectName
argument_list|)
expr_stmt|;
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
block|{
return|return
literal|true
return|;
block|}
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
block|{
return|return
literal|false
return|;
block|}
name|TestSearchExtBuilder
name|that
init|=
operator|(
name|TestSearchExtBuilder
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|objectName
argument_list|,
name|that
operator|.
name|objectName
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|name
argument_list|,
name|that
operator|.
name|name
argument_list|)
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
name|objectName
argument_list|,
name|name
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
name|name
return|;
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
name|builder
operator|.
name|startObject
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|objectName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
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
block|}
block|}
end_class

end_unit

