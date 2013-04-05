begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.terms.strings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|terms
operator|.
name|strings
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|BytesRef
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
name|BytesRefHash
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
name|CharsRef
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
name|UnicodeUtil
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
name|fielddata
operator|.
name|BytesValues
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_class
DECL|class|HashedScriptAggregator
specifier|public
specifier|final
class|class
name|HashedScriptAggregator
extends|extends
name|HashedAggregator
block|{
DECL|field|excluded
specifier|private
specifier|final
name|ImmutableSet
argument_list|<
name|BytesRef
argument_list|>
name|excluded
decl_stmt|;
DECL|field|matcher
specifier|private
specifier|final
name|Matcher
name|matcher
decl_stmt|;
DECL|field|script
specifier|private
specifier|final
name|SearchScript
name|script
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|CharsRef
name|spare
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
DECL|field|scriptSpare
specifier|private
specifier|final
name|BytesRef
name|scriptSpare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|convert
specifier|private
specifier|final
name|boolean
name|convert
decl_stmt|;
DECL|method|HashedScriptAggregator
specifier|public
name|HashedScriptAggregator
parameter_list|(
name|ImmutableSet
argument_list|<
name|BytesRef
argument_list|>
name|excluded
parameter_list|,
name|Pattern
name|pattern
parameter_list|,
name|SearchScript
name|script
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|BytesRefHash
argument_list|()
argument_list|,
name|excluded
argument_list|,
name|pattern
argument_list|,
name|script
argument_list|)
expr_stmt|;
block|}
DECL|method|HashedScriptAggregator
specifier|public
name|HashedScriptAggregator
parameter_list|(
name|BytesRefHash
name|hash
parameter_list|,
name|ImmutableSet
argument_list|<
name|BytesRef
argument_list|>
name|excluded
parameter_list|,
name|Pattern
name|pattern
parameter_list|,
name|SearchScript
name|script
parameter_list|)
block|{
name|super
argument_list|(
name|hash
argument_list|)
expr_stmt|;
name|this
operator|.
name|excluded
operator|=
name|excluded
expr_stmt|;
name|this
operator|.
name|matcher
operator|=
name|pattern
operator|!=
literal|null
condition|?
name|pattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|this
operator|.
name|convert
operator|=
name|script
operator|!=
literal|null
operator|||
name|matcher
operator|!=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onValue
specifier|protected
name|void
name|onValue
parameter_list|(
name|int
name|docId
parameter_list|,
name|BytesRef
name|value
parameter_list|,
name|int
name|hashCode
parameter_list|,
name|BytesValues
name|values
parameter_list|)
block|{
if|if
condition|(
name|excluded
operator|!=
literal|null
operator|&&
name|excluded
operator|.
name|contains
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|convert
condition|)
block|{
comment|// only convert if we need to and only once per doc...
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|value
argument_list|,
name|spare
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|!=
literal|null
condition|)
block|{
assert|assert
name|convert
operator|:
literal|"regexp: [convert == false] but should be true"
assert|;
assert|assert
name|value
operator|.
name|utf8ToString
argument_list|()
operator|.
name|equals
argument_list|(
name|spare
operator|.
name|toString
argument_list|()
argument_list|)
operator|:
literal|"not converted"
assert|;
if|if
condition|(
operator|!
name|matcher
operator|.
name|reset
argument_list|(
name|spare
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
block|{
assert|assert
name|convert
operator|:
literal|"script: [convert == false] but should be true"
assert|;
assert|assert
name|value
operator|.
name|utf8ToString
argument_list|()
operator|.
name|equals
argument_list|(
name|spare
operator|.
name|toString
argument_list|()
argument_list|)
operator|:
literal|"not converted"
assert|;
name|script
operator|.
name|setNextDocId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
comment|// LUCENE 4 UPGRADE: needs optimization -- maybe a CharSequence does the job here?
comment|// we only creat that string if we really need
name|script
operator|.
name|setNextVar
argument_list|(
literal|"term"
argument_list|,
name|spare
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|scriptValue
init|=
name|script
operator|.
name|run
argument_list|()
decl_stmt|;
if|if
condition|(
name|scriptValue
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|scriptValue
operator|instanceof
name|Boolean
condition|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|Boolean
operator|)
name|scriptValue
operator|)
condition|)
block|{
return|return;
block|}
block|}
else|else
block|{
comment|// LUCENE 4 UPGRADE: should be possible to convert directly to BR
name|scriptSpare
operator|.
name|copyChars
argument_list|(
name|scriptValue
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|hashCode
operator|=
name|scriptSpare
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|super
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
name|scriptSpare
argument_list|,
name|hashCode
argument_list|,
name|values
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
assert|assert
name|convert
operator|||
operator|(
name|matcher
operator|==
literal|null
operator|&&
name|script
operator|==
literal|null
operator|)
assert|;
name|super
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
name|value
argument_list|,
name|hashCode
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

