begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.path
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|path
package|;
end_package

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
name|ImmutableMap
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
name|collect
operator|.
name|MapBuilder
operator|.
name|newMapBuilder
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|PathTrie
specifier|public
class|class
name|PathTrie
parameter_list|<
name|T
parameter_list|>
block|{
DECL|interface|Decoder
specifier|public
specifier|static
interface|interface
name|Decoder
block|{
DECL|method|decode
name|String
name|decode
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
block|}
DECL|field|NO_DECODER
specifier|public
specifier|static
specifier|final
name|Decoder
name|NO_DECODER
init|=
operator|new
name|Decoder
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|decode
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
block|}
decl_stmt|;
DECL|field|decoder
specifier|private
specifier|final
name|Decoder
name|decoder
decl_stmt|;
DECL|field|root
specifier|private
specifier|final
name|TrieNode
argument_list|<
name|T
argument_list|>
name|root
decl_stmt|;
DECL|field|separator
specifier|private
specifier|final
name|char
name|separator
decl_stmt|;
DECL|field|rootValue
specifier|private
name|T
name|rootValue
decl_stmt|;
DECL|method|PathTrie
specifier|public
name|PathTrie
parameter_list|()
block|{
name|this
argument_list|(
literal|'/'
argument_list|,
literal|"*"
argument_list|,
name|NO_DECODER
argument_list|)
expr_stmt|;
block|}
DECL|method|PathTrie
specifier|public
name|PathTrie
parameter_list|(
name|Decoder
name|decoder
parameter_list|)
block|{
name|this
argument_list|(
literal|'/'
argument_list|,
literal|"*"
argument_list|,
name|decoder
argument_list|)
expr_stmt|;
block|}
DECL|method|PathTrie
specifier|public
name|PathTrie
parameter_list|(
name|char
name|separator
parameter_list|,
name|String
name|wildcard
parameter_list|,
name|Decoder
name|decoder
parameter_list|)
block|{
name|this
operator|.
name|decoder
operator|=
name|decoder
expr_stmt|;
name|this
operator|.
name|separator
operator|=
name|separator
expr_stmt|;
name|root
operator|=
operator|new
name|TrieNode
argument_list|<
name|T
argument_list|>
argument_list|(
operator|new
name|String
argument_list|(
operator|new
name|char
index|[]
block|{
name|separator
block|}
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|wildcard
argument_list|)
expr_stmt|;
block|}
DECL|class|TrieNode
specifier|public
class|class
name|TrieNode
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|key
specifier|private
specifier|transient
name|String
name|key
decl_stmt|;
DECL|field|value
specifier|private
specifier|transient
name|T
name|value
decl_stmt|;
DECL|field|isWildcard
specifier|private
name|boolean
name|isWildcard
decl_stmt|;
DECL|field|wildcard
specifier|private
specifier|final
name|String
name|wildcard
decl_stmt|;
DECL|field|namedWildcard
specifier|private
specifier|transient
name|String
name|namedWildcard
decl_stmt|;
DECL|field|children
specifier|private
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|TrieNode
argument_list|<
name|T
argument_list|>
argument_list|>
name|children
decl_stmt|;
DECL|field|parent
specifier|private
specifier|final
name|TrieNode
name|parent
decl_stmt|;
DECL|method|TrieNode
specifier|public
name|TrieNode
parameter_list|(
name|String
name|key
parameter_list|,
name|T
name|value
parameter_list|,
name|TrieNode
name|parent
parameter_list|,
name|String
name|wildcard
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|wildcard
operator|=
name|wildcard
expr_stmt|;
name|this
operator|.
name|isWildcard
operator|=
operator|(
name|key
operator|.
name|equals
argument_list|(
name|wildcard
argument_list|)
operator|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|ImmutableMap
operator|.
name|of
argument_list|()
expr_stmt|;
if|if
condition|(
name|isNamedWildcard
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|namedWildcard
operator|=
name|key
operator|.
name|substring
argument_list|(
name|key
operator|.
name|indexOf
argument_list|(
literal|'{'
argument_list|)
operator|+
literal|1
argument_list|,
name|key
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|namedWildcard
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|updateKeyWithNamedWildcard
specifier|public
name|void
name|updateKeyWithNamedWildcard
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|namedWildcard
operator|=
name|key
operator|.
name|substring
argument_list|(
name|key
operator|.
name|indexOf
argument_list|(
literal|'{'
argument_list|)
operator|+
literal|1
argument_list|,
name|key
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|isWildcard
specifier|public
name|boolean
name|isWildcard
parameter_list|()
block|{
return|return
name|isWildcard
return|;
block|}
DECL|method|addChild
specifier|public
specifier|synchronized
name|void
name|addChild
parameter_list|(
name|TrieNode
argument_list|<
name|T
argument_list|>
name|child
parameter_list|)
block|{
name|children
operator|=
name|newMapBuilder
argument_list|(
name|children
argument_list|)
operator|.
name|put
argument_list|(
name|child
operator|.
name|key
argument_list|,
name|child
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
DECL|method|getChild
specifier|public
name|TrieNode
name|getChild
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|children
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|insert
specifier|public
specifier|synchronized
name|void
name|insert
parameter_list|(
name|String
index|[]
name|path
parameter_list|,
name|int
name|index
parameter_list|,
name|T
name|value
parameter_list|)
block|{
if|if
condition|(
name|index
operator|>=
name|path
operator|.
name|length
condition|)
return|return;
name|String
name|token
init|=
name|path
index|[
name|index
index|]
decl_stmt|;
name|String
name|key
init|=
name|token
decl_stmt|;
if|if
condition|(
name|isNamedWildcard
argument_list|(
name|token
argument_list|)
condition|)
block|{
name|key
operator|=
name|wildcard
expr_stmt|;
block|}
name|TrieNode
argument_list|<
name|T
argument_list|>
name|node
init|=
name|children
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|index
operator|==
operator|(
name|path
operator|.
name|length
operator|-
literal|1
operator|)
condition|)
block|{
name|node
operator|=
operator|new
name|TrieNode
argument_list|<
name|T
argument_list|>
argument_list|(
name|token
argument_list|,
name|value
argument_list|,
name|this
argument_list|,
name|wildcard
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|=
operator|new
name|TrieNode
argument_list|<
name|T
argument_list|>
argument_list|(
name|token
argument_list|,
literal|null
argument_list|,
name|this
argument_list|,
name|wildcard
argument_list|)
expr_stmt|;
block|}
name|children
operator|=
name|newMapBuilder
argument_list|(
name|children
argument_list|)
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|node
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|isNamedWildcard
argument_list|(
name|token
argument_list|)
condition|)
block|{
name|node
operator|.
name|updateKeyWithNamedWildcard
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
comment|// in case the target(last) node already exist but without a value
comment|// than the value should be updated.
if|if
condition|(
name|index
operator|==
operator|(
name|path
operator|.
name|length
operator|-
literal|1
operator|)
condition|)
block|{
assert|assert
operator|(
name|node
operator|.
name|value
operator|==
literal|null
operator|||
name|node
operator|.
name|value
operator|==
name|value
operator|)
assert|;
if|if
condition|(
name|node
operator|.
name|value
operator|==
literal|null
condition|)
block|{
name|node
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
name|node
operator|.
name|insert
argument_list|(
name|path
argument_list|,
name|index
operator|+
literal|1
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|isNamedWildcard
specifier|private
name|boolean
name|isNamedWildcard
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|key
operator|.
name|indexOf
argument_list|(
literal|'{'
argument_list|)
operator|!=
operator|-
literal|1
operator|&&
name|key
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|)
operator|!=
operator|-
literal|1
return|;
block|}
DECL|method|namedWildcard
specifier|private
name|String
name|namedWildcard
parameter_list|()
block|{
return|return
name|namedWildcard
return|;
block|}
DECL|method|isNamedWildcard
specifier|private
name|boolean
name|isNamedWildcard
parameter_list|()
block|{
return|return
name|namedWildcard
operator|!=
literal|null
return|;
block|}
DECL|method|retrieve
specifier|public
name|T
name|retrieve
parameter_list|(
name|String
index|[]
name|path
parameter_list|,
name|int
name|index
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
if|if
condition|(
name|index
operator|>=
name|path
operator|.
name|length
condition|)
return|return
literal|null
return|;
name|String
name|token
init|=
name|path
index|[
name|index
index|]
decl_stmt|;
name|TrieNode
argument_list|<
name|T
argument_list|>
name|node
init|=
name|children
operator|.
name|get
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|boolean
name|usedWildcard
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|node
operator|=
name|children
operator|.
name|get
argument_list|(
name|wildcard
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|usedWildcard
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
operator|&&
name|node
operator|.
name|isNamedWildcard
argument_list|()
condition|)
block|{
name|put
argument_list|(
name|params
argument_list|,
name|node
operator|.
name|namedWildcard
argument_list|()
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|index
operator|==
operator|(
name|path
operator|.
name|length
operator|-
literal|1
operator|)
condition|)
block|{
return|return
name|node
operator|.
name|value
return|;
block|}
name|T
name|res
init|=
name|node
operator|.
name|retrieve
argument_list|(
name|path
argument_list|,
name|index
operator|+
literal|1
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|null
operator|&&
operator|!
name|usedWildcard
condition|)
block|{
name|node
operator|=
name|children
operator|.
name|get
argument_list|(
name|wildcard
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|params
operator|!=
literal|null
operator|&&
name|node
operator|.
name|isNamedWildcard
argument_list|()
condition|)
block|{
name|put
argument_list|(
name|params
argument_list|,
name|node
operator|.
name|namedWildcard
argument_list|()
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
name|res
operator|=
name|node
operator|.
name|retrieve
argument_list|(
name|path
argument_list|,
name|index
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
DECL|method|put
specifier|private
name|void
name|put
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|params
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|decoder
operator|.
name|decode
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|insert
specifier|public
name|void
name|insert
parameter_list|(
name|String
name|path
parameter_list|,
name|T
name|value
parameter_list|)
block|{
name|String
index|[]
name|strings
init|=
name|Strings
operator|.
name|splitStringToArray
argument_list|(
name|path
argument_list|,
name|separator
argument_list|)
decl_stmt|;
if|if
condition|(
name|strings
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|rootValue
operator|=
name|value
expr_stmt|;
return|return;
block|}
name|int
name|index
init|=
literal|0
decl_stmt|;
comment|// supports initial delimiter.
if|if
condition|(
name|strings
operator|.
name|length
operator|>
literal|0
operator|&&
name|strings
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|index
operator|=
literal|1
expr_stmt|;
block|}
name|root
operator|.
name|insert
argument_list|(
name|strings
argument_list|,
name|index
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|retrieve
specifier|public
name|T
name|retrieve
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|retrieve
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|retrieve
specifier|public
name|T
name|retrieve
parameter_list|(
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|rootValue
return|;
block|}
name|String
index|[]
name|strings
init|=
name|Strings
operator|.
name|splitStringToArray
argument_list|(
name|path
argument_list|,
name|separator
argument_list|)
decl_stmt|;
if|if
condition|(
name|strings
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|rootValue
return|;
block|}
name|int
name|index
init|=
literal|0
decl_stmt|;
comment|// supports initial delimiter.
if|if
condition|(
name|strings
operator|.
name|length
operator|>
literal|0
operator|&&
name|strings
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|index
operator|=
literal|1
expr_stmt|;
block|}
return|return
name|root
operator|.
name|retrieve
argument_list|(
name|strings
argument_list|,
name|index
argument_list|,
name|params
argument_list|)
return|;
block|}
block|}
end_class

end_unit

