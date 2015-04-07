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
name|index
operator|.
name|LeafReaderContext
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
name|BitsFilteredDocIdSet
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
name|DocIdSet
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
name|DocValuesDocIdSet
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
name|FilterCachingPolicy
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
name|util
operator|.
name|Bits
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
name|lucene
operator|.
name|HashedBytesRef
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
name|ScriptParameterParser
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
name|ScriptParameterParser
operator|.
name|ScriptParameterValue
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
name|SearchScript
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
name|lookup
operator|.
name|SearchLookup
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
name|Map
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ScriptFilterParser
specifier|public
class|class
name|ScriptFilterParser
implements|implements
name|FilterParser
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"script"
decl_stmt|;
annotation|@
name|Inject
DECL|method|ScriptFilterParser
specifier|public
name|ScriptFilterParser
parameter_list|()
block|{     }
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
name|NAME
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Filter
name|parse
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|ScriptParameterParser
name|scriptParameterParser
init|=
operator|new
name|ScriptParameterParser
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|FilterCachingPolicy
name|cache
init|=
name|parseContext
operator|.
name|autoFilterCachePolicy
argument_list|()
decl_stmt|;
name|HashedBytesRef
name|cacheKey
init|=
literal|null
decl_stmt|;
comment|// also, when caching, since its isCacheable is false, will result in loading all bit set...
name|String
name|script
init|=
literal|null
decl_stmt|;
name|String
name|scriptLang
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
literal|null
decl_stmt|;
name|String
name|filterName
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|ScriptService
operator|.
name|ScriptType
name|scriptType
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
if|if
condition|(
literal|"params"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|params
operator|=
name|parser
operator|.
name|map
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[script] filter does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
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
if|if
condition|(
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|filterName
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_cache"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|cache
operator|=
name|parseContext
operator|.
name|parseFilterCachePolicy
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_cache_key"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"_cacheKey"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|cacheKey
operator|=
operator|new
name|HashedBytesRef
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|scriptParameterParser
operator|.
name|token
argument_list|(
name|currentFieldName
argument_list|,
name|token
argument_list|,
name|parser
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[script] filter does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
name|ScriptParameterValue
name|scriptValue
init|=
name|scriptParameterParser
operator|.
name|getDefaultScriptParameterValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|scriptValue
operator|!=
literal|null
condition|)
block|{
name|script
operator|=
name|scriptValue
operator|.
name|script
argument_list|()
expr_stmt|;
name|scriptType
operator|=
name|scriptValue
operator|.
name|scriptType
argument_list|()
expr_stmt|;
block|}
name|scriptLang
operator|=
name|scriptParameterParser
operator|.
name|lang
argument_list|()
expr_stmt|;
if|if
condition|(
name|script
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"script must be provided with a [script] filter"
argument_list|)
throw|;
block|}
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|params
operator|=
name|newHashMap
argument_list|()
expr_stmt|;
block|}
name|Filter
name|filter
init|=
operator|new
name|ScriptFilter
argument_list|(
name|scriptLang
argument_list|,
name|script
argument_list|,
name|scriptType
argument_list|,
name|params
argument_list|,
name|parseContext
operator|.
name|scriptService
argument_list|()
argument_list|,
name|parseContext
operator|.
name|lookup
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|filter
operator|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|filter
argument_list|,
name|cacheKey
argument_list|,
name|cache
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filterName
operator|!=
literal|null
condition|)
block|{
name|parseContext
operator|.
name|addNamedFilter
argument_list|(
name|filterName
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
return|return
name|filter
return|;
block|}
DECL|class|ScriptFilter
specifier|public
specifier|static
class|class
name|ScriptFilter
extends|extends
name|Filter
block|{
DECL|field|script
specifier|private
specifier|final
name|String
name|script
decl_stmt|;
DECL|field|params
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
decl_stmt|;
DECL|field|searchScript
specifier|private
specifier|final
name|SearchScript
name|searchScript
decl_stmt|;
DECL|method|ScriptFilter
specifier|public
name|ScriptFilter
parameter_list|(
name|String
name|scriptLang
parameter_list|,
name|String
name|script
parameter_list|,
name|ScriptService
operator|.
name|ScriptType
name|scriptType
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|SearchLookup
name|searchLookup
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|searchScript
operator|=
name|scriptService
operator|.
name|search
argument_list|(
name|searchLookup
argument_list|,
name|scriptLang
argument_list|,
name|script
argument_list|,
name|scriptType
argument_list|,
name|ScriptContext
operator|.
name|SEARCH
argument_list|,
name|newHashMap
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"ScriptFilter("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|script
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
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
name|ScriptFilter
name|that
init|=
operator|(
name|ScriptFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|?
operator|!
name|params
operator|.
name|equals
argument_list|(
name|that
operator|.
name|params
argument_list|)
else|:
name|that
operator|.
name|params
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|script
operator|!=
literal|null
condition|?
operator|!
name|script
operator|.
name|equals
argument_list|(
name|that
operator|.
name|script
argument_list|)
else|:
name|that
operator|.
name|script
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
name|script
operator|!=
literal|null
condition|?
name|script
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
name|params
operator|!=
literal|null
condition|?
name|params
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
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafSearchScript
name|leafScript
init|=
name|searchScript
operator|.
name|getLeafSearchScript
argument_list|(
name|context
argument_list|)
decl_stmt|;
comment|// LUCENE 4 UPGRADE: we can simply wrap this here since it is not cacheable and if we are not top level we will get a null passed anyway
return|return
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
operator|new
name|ScriptDocSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|acceptDocs
argument_list|,
name|leafScript
argument_list|)
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
DECL|class|ScriptDocSet
specifier|static
class|class
name|ScriptDocSet
extends|extends
name|DocValuesDocIdSet
block|{
DECL|field|searchScript
specifier|private
specifier|final
name|LeafSearchScript
name|searchScript
decl_stmt|;
DECL|method|ScriptDocSet
specifier|public
name|ScriptDocSet
parameter_list|(
name|int
name|maxDoc
parameter_list|,
annotation|@
name|Nullable
name|Bits
name|acceptDocs
parameter_list|,
name|LeafSearchScript
name|searchScript
parameter_list|)
block|{
name|super
argument_list|(
name|maxDoc
argument_list|,
name|acceptDocs
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchScript
operator|=
name|searchScript
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matchDoc
specifier|protected
name|boolean
name|matchDoc
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|searchScript
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Object
name|val
init|=
name|searchScript
operator|.
name|run
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|val
operator|instanceof
name|Boolean
condition|)
block|{
return|return
operator|(
name|Boolean
operator|)
name|val
return|;
block|}
if|if
condition|(
name|val
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|val
operator|)
operator|.
name|longValue
argument_list|()
operator|!=
literal|0
return|;
block|}
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Can't handle type ["
operator|+
name|val
operator|+
literal|"] in script filter"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

