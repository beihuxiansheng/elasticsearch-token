begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.completion
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
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
name|ScoreDoc
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
name|suggest
operator|.
name|Lookup
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
name|lucene
operator|.
name|Lucene
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
name|text
operator|.
name|Text
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
name|search
operator|.
name|SearchHit
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
name|Suggest
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
name|Suggest
operator|.
name|Suggestion
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
name|Comparator
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
name|LinkedHashMap
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentParserUtils
operator|.
name|ensureExpectedToken
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|Suggest
operator|.
name|COMPARATOR
import|;
end_import

begin_comment
comment|/**  * Suggestion response for {@link CompletionSuggester} results  *  * Response format for each entry:  * {  *     "text" : STRING  *     "score" : FLOAT  *     "contexts" : CONTEXTS  * }  *  * CONTEXTS : {  *     "CONTEXT_NAME" : ARRAY,  *     ..  * }  *  */
end_comment

begin_class
DECL|class|CompletionSuggestion
specifier|public
specifier|final
class|class
name|CompletionSuggestion
extends|extends
name|Suggest
operator|.
name|Suggestion
argument_list|<
name|CompletionSuggestion
operator|.
name|Entry
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"completion"
decl_stmt|;
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|int
name|TYPE
init|=
literal|4
decl_stmt|;
DECL|method|CompletionSuggestion
specifier|public
name|CompletionSuggestion
parameter_list|()
block|{     }
DECL|method|CompletionSuggestion
specifier|public
name|CompletionSuggestion
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the result options for the suggestion      */
DECL|method|getOptions
specifier|public
name|List
argument_list|<
name|Entry
operator|.
name|Option
argument_list|>
name|getOptions
parameter_list|()
block|{
if|if
condition|(
name|entries
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
assert|assert
name|entries
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|:
literal|"CompletionSuggestion must have only one entry"
assert|;
return|return
name|entries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getOptions
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
comment|/**      * @return whether there is any hits for the suggestion      */
DECL|method|hasScoreDocs
specifier|public
name|boolean
name|hasScoreDocs
parameter_list|()
block|{
return|return
name|getOptions
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|CompletionSuggestion
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|CompletionSuggestion
name|suggestion
init|=
operator|new
name|CompletionSuggestion
argument_list|(
name|name
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|parseEntries
argument_list|(
name|parser
argument_list|,
name|suggestion
argument_list|,
name|CompletionSuggestion
operator|.
name|Entry
operator|::
name|fromXContent
argument_list|)
expr_stmt|;
return|return
name|suggestion
return|;
block|}
DECL|class|OptionPriorityQueue
specifier|private
specifier|static
specifier|final
class|class
name|OptionPriorityQueue
extends|extends
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|PriorityQueue
argument_list|<
name|Entry
operator|.
name|Option
argument_list|>
block|{
DECL|field|comparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
name|comparator
decl_stmt|;
DECL|method|OptionPriorityQueue
name|OptionPriorityQueue
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|Comparator
argument_list|<
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
name|comparator
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|Entry
operator|.
name|Option
name|a
parameter_list|,
name|Entry
operator|.
name|Option
name|b
parameter_list|)
block|{
name|int
name|cmp
init|=
name|comparator
operator|.
name|compare
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
operator|>
literal|0
return|;
block|}
return|return
name|Lookup
operator|.
name|CHARSEQUENCE_COMPARATOR
operator|.
name|compare
argument_list|(
name|a
operator|.
name|getText
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|b
operator|.
name|getText
argument_list|()
operator|.
name|string
argument_list|()
argument_list|)
operator|>
literal|0
return|;
block|}
DECL|method|get
name|Entry
operator|.
name|Option
index|[]
name|get
parameter_list|()
block|{
name|int
name|size
init|=
name|size
argument_list|()
decl_stmt|;
name|Entry
operator|.
name|Option
index|[]
name|results
init|=
operator|new
name|Entry
operator|.
name|Option
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|size
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|results
index|[
name|i
index|]
operator|=
name|pop
argument_list|()
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
block|}
comment|/**      * Reduces suggestions to a single suggestion containing at most      * top {@link CompletionSuggestion#getSize()} options across<code>toReduce</code>      */
DECL|method|reduceTo
specifier|public
specifier|static
name|CompletionSuggestion
name|reduceTo
parameter_list|(
name|List
argument_list|<
name|Suggest
operator|.
name|Suggestion
argument_list|<
name|Entry
argument_list|>
argument_list|>
name|toReduce
parameter_list|)
block|{
if|if
condition|(
name|toReduce
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
specifier|final
name|CompletionSuggestion
name|leader
init|=
operator|(
name|CompletionSuggestion
operator|)
name|toReduce
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Entry
name|leaderEntry
init|=
name|leader
operator|.
name|getEntries
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|leader
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|toReduce
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|leader
return|;
block|}
else|else
block|{
comment|// combine suggestion entries from participating shards on the coordinating node
comment|// the global top<code>size</code> entries are collected from the shard results
comment|// using a priority queue
name|OptionPriorityQueue
name|priorityQueue
init|=
operator|new
name|OptionPriorityQueue
argument_list|(
name|leader
operator|.
name|getSize
argument_list|()
argument_list|,
name|COMPARATOR
argument_list|)
decl_stmt|;
for|for
control|(
name|Suggest
operator|.
name|Suggestion
argument_list|<
name|Entry
argument_list|>
name|suggestion
range|:
name|toReduce
control|)
block|{
assert|assert
name|suggestion
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|:
literal|"name should be identical across all suggestions"
assert|;
for|for
control|(
name|Entry
operator|.
name|Option
name|option
range|:
operator|(
operator|(
name|CompletionSuggestion
operator|)
name|suggestion
operator|)
operator|.
name|getOptions
argument_list|()
control|)
block|{
if|if
condition|(
name|option
operator|==
name|priorityQueue
operator|.
name|insertWithOverflow
argument_list|(
name|option
argument_list|)
condition|)
block|{
comment|// if the current option has overflown from pq,
comment|// we can assume all of the successive options
comment|// from this shard result will be overflown as well
break|break;
block|}
block|}
block|}
specifier|final
name|CompletionSuggestion
name|suggestion
init|=
operator|new
name|CompletionSuggestion
argument_list|(
name|leader
operator|.
name|getName
argument_list|()
argument_list|,
name|leader
operator|.
name|getSize
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Entry
name|entry
init|=
operator|new
name|Entry
argument_list|(
name|leaderEntry
operator|.
name|getText
argument_list|()
argument_list|,
name|leaderEntry
operator|.
name|getOffset
argument_list|()
argument_list|,
name|leaderEntry
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|entry
operator|.
name|getOptions
argument_list|()
argument_list|,
name|priorityQueue
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|suggestion
operator|.
name|addTerm
argument_list|(
name|entry
argument_list|)
expr_stmt|;
return|return
name|suggestion
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|reduce
specifier|public
name|Suggest
operator|.
name|Suggestion
argument_list|<
name|Entry
argument_list|>
name|reduce
parameter_list|(
name|List
argument_list|<
name|Suggest
operator|.
name|Suggestion
argument_list|<
name|Entry
argument_list|>
argument_list|>
name|toReduce
parameter_list|)
block|{
return|return
name|reduceTo
argument_list|(
name|toReduce
argument_list|)
return|;
block|}
DECL|method|setShardIndex
specifier|public
name|void
name|setShardIndex
parameter_list|(
name|int
name|shardIndex
parameter_list|)
block|{
if|if
condition|(
name|entries
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
for|for
control|(
name|Entry
operator|.
name|Option
name|option
range|:
name|getOptions
argument_list|()
control|)
block|{
name|option
operator|.
name|setShardIndex
argument_list|(
name|shardIndex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getWriteableType
specifier|public
name|int
name|getWriteableType
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|protected
name|String
name|getType
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|newEntry
specifier|protected
name|Entry
name|newEntry
parameter_list|()
block|{
return|return
operator|new
name|Entry
argument_list|()
return|;
block|}
DECL|class|Entry
specifier|public
specifier|static
specifier|final
class|class
name|Entry
extends|extends
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
argument_list|<
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
block|{
DECL|method|Entry
specifier|public
name|Entry
parameter_list|(
name|Text
name|text
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|Entry
name|Entry
parameter_list|()
block|{         }
annotation|@
name|Override
DECL|method|newOption
specifier|protected
name|Option
name|newOption
parameter_list|()
block|{
return|return
operator|new
name|Option
argument_list|()
return|;
block|}
DECL|field|PARSER
specifier|private
specifier|static
name|ObjectParser
argument_list|<
name|Entry
argument_list|,
name|Void
argument_list|>
name|PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
literal|"CompletionSuggestionEntryParser"
argument_list|,
literal|true
argument_list|,
name|Entry
operator|::
operator|new
argument_list|)
decl_stmt|;
static|static
block|{
name|declareCommonFields
argument_list|(
name|PARSER
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareObjectArray
argument_list|(
name|Entry
operator|::
name|addOptions
argument_list|,
parameter_list|(
name|p
parameter_list|,
name|c
parameter_list|)
lambda|->
name|Option
operator|.
name|fromXContent
argument_list|(
name|p
argument_list|)
argument_list|,
operator|new
name|ParseField
argument_list|(
name|OPTIONS
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|Entry
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
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
return|;
block|}
DECL|class|Option
specifier|public
specifier|static
class|class
name|Option
extends|extends
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
block|{
DECL|field|contexts
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|CharSequence
argument_list|>
argument_list|>
name|contexts
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
DECL|field|doc
specifier|private
name|ScoreDoc
name|doc
decl_stmt|;
DECL|field|hit
specifier|private
name|SearchHit
name|hit
decl_stmt|;
DECL|field|CONTEXTS
specifier|public
specifier|static
specifier|final
name|ParseField
name|CONTEXTS
init|=
operator|new
name|ParseField
argument_list|(
literal|"contexts"
argument_list|)
decl_stmt|;
DECL|method|Option
specifier|public
name|Option
parameter_list|(
name|int
name|docID
parameter_list|,
name|Text
name|text
parameter_list|,
name|float
name|score
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|CharSequence
argument_list|>
argument_list|>
name|contexts
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|,
name|score
argument_list|)
expr_stmt|;
name|this
operator|.
name|doc
operator|=
operator|new
name|ScoreDoc
argument_list|(
name|docID
argument_list|,
name|score
argument_list|)
expr_stmt|;
name|this
operator|.
name|contexts
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|contexts
argument_list|,
literal|"context map cannot be null"
argument_list|)
expr_stmt|;
block|}
DECL|method|Option
specifier|protected
name|Option
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|mergeInto
specifier|protected
name|void
name|mergeInto
parameter_list|(
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
name|otherOption
parameter_list|)
block|{
comment|// Completion suggestions are reduced by
comment|// org.elasticsearch.search.suggest.completion.CompletionSuggestion.reduce()
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|getContexts
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|CharSequence
argument_list|>
argument_list|>
name|getContexts
parameter_list|()
block|{
return|return
name|contexts
return|;
block|}
DECL|method|getDoc
specifier|public
name|ScoreDoc
name|getDoc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|getHit
specifier|public
name|SearchHit
name|getHit
parameter_list|()
block|{
return|return
name|hit
return|;
block|}
DECL|method|setShardIndex
specifier|public
name|void
name|setShardIndex
parameter_list|(
name|int
name|shardIndex
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|.
name|shardIndex
operator|=
name|shardIndex
expr_stmt|;
block|}
DECL|method|setHit
specifier|public
name|void
name|setHit
parameter_list|(
name|SearchHit
name|hit
parameter_list|)
block|{
name|this
operator|.
name|hit
operator|=
name|hit
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|innerToXContent
specifier|protected
name|XContentBuilder
name|innerToXContent
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
name|field
argument_list|(
name|TEXT
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|getText
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|hit
operator|!=
literal|null
condition|)
block|{
name|hit
operator|.
name|toInnerXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
name|SCORE
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|getScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contexts
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|CONTEXTS
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|CharSequence
argument_list|>
argument_list|>
name|entry
range|:
name|contexts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|CharSequence
name|context
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|context
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|field|PARSER
specifier|private
specifier|static
name|ObjectParser
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|Void
argument_list|>
name|PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
literal|"CompletionOptionParser"
argument_list|,
literal|true
argument_list|,
name|HashMap
operator|::
operator|new
argument_list|)
decl_stmt|;
static|static
block|{
name|SearchHit
operator|.
name|declareInnerHitsParseFields
argument_list|(
name|PARSER
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareString
argument_list|(
parameter_list|(
name|map
parameter_list|,
name|value
parameter_list|)
lambda|->
name|map
operator|.
name|put
argument_list|(
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
operator|.
name|TEXT
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|value
argument_list|)
argument_list|,
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
operator|.
name|TEXT
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareFloat
argument_list|(
parameter_list|(
name|map
parameter_list|,
name|value
parameter_list|)
lambda|->
name|map
operator|.
name|put
argument_list|(
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
operator|.
name|SCORE
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|value
argument_list|)
argument_list|,
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
operator|.
name|SCORE
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareObject
argument_list|(
parameter_list|(
name|map
parameter_list|,
name|value
parameter_list|)
lambda|->
name|map
operator|.
name|put
argument_list|(
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
operator|.
name|CONTEXTS
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|value
argument_list|)
argument_list|,
parameter_list|(
name|p
parameter_list|,
name|c
parameter_list|)
lambda|->
name|parseContexts
argument_list|(
name|p
argument_list|)
argument_list|,
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
operator|.
name|CONTEXTS
argument_list|)
expr_stmt|;
block|}
DECL|method|parseContexts
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|CharSequence
argument_list|>
argument_list|>
name|parseContexts
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|CharSequence
argument_list|>
argument_list|>
name|contexts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
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
name|ensureExpectedToken
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|,
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|,
name|parser
operator|::
name|getTokenLocation
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|ensureExpectedToken
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|parser
operator|::
name|getTokenLocation
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|CharSequence
argument_list|>
name|values
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
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
name|END_ARRAY
condition|)
block|{
name|ensureExpectedToken
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
argument_list|,
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|,
name|parser
operator|::
name|getTokenLocation
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|contexts
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
return|return
name|contexts
return|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|Option
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
init|=
name|PARSER
operator|.
name|apply
argument_list|(
name|parser
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Text
name|text
init|=
operator|new
name|Text
argument_list|(
operator|(
name|String
operator|)
name|values
operator|.
name|get
argument_list|(
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
operator|.
name|TEXT
operator|.
name|getPreferredName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Float
name|score
init|=
operator|(
name|Float
operator|)
name|values
operator|.
name|get
argument_list|(
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
operator|.
name|SCORE
operator|.
name|getPreferredName
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|CharSequence
argument_list|>
argument_list|>
name|contexts
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|CharSequence
argument_list|>
argument_list|>
operator|)
name|values
operator|.
name|get
argument_list|(
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
operator|.
name|CONTEXTS
operator|.
name|getPreferredName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|contexts
operator|==
literal|null
condition|)
block|{
name|contexts
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
name|SearchHit
name|hit
init|=
literal|null
decl_stmt|;
comment|// the option either prints SCORE or inlines the search hit
if|if
condition|(
name|score
operator|==
literal|null
condition|)
block|{
name|hit
operator|=
name|SearchHit
operator|.
name|createFromMap
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|score
operator|=
name|hit
operator|.
name|getScore
argument_list|()
expr_stmt|;
block|}
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
name|option
init|=
operator|new
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|(
operator|-
literal|1
argument_list|,
name|text
argument_list|,
name|score
argument_list|,
name|contexts
argument_list|)
decl_stmt|;
name|option
operator|.
name|setHit
argument_list|(
name|hit
argument_list|)
expr_stmt|;
return|return
name|option
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|Lucene
operator|.
name|readScoreDoc
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|this
operator|.
name|hit
operator|=
name|SearchHit
operator|.
name|readSearchHit
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|int
name|contextSize
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|this
operator|.
name|contexts
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|contextSize
argument_list|)
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
name|contextSize
condition|;
name|i
operator|++
control|)
block|{
name|String
name|contextName
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|int
name|nContexts
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|CharSequence
argument_list|>
name|contexts
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|nContexts
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nContexts
condition|;
name|j
operator|++
control|)
block|{
name|contexts
operator|.
name|add
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|contexts
operator|.
name|put
argument_list|(
name|contextName
argument_list|,
name|contexts
argument_list|)
expr_stmt|;
block|}
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|Lucene
operator|.
name|writeScoreDoc
argument_list|(
name|out
argument_list|,
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|hit
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|hit
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeInt
argument_list|(
name|contexts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|CharSequence
argument_list|>
argument_list|>
name|entry
range|:
name|contexts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|CharSequence
name|ctx
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|ctx
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|stringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
literal|"text:"
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
literal|" score:"
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
name|getScore
argument_list|()
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
literal|" context:["
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|CharSequence
argument_list|>
argument_list|>
name|entry
range|:
name|contexts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|stringBuilder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|stringBuilder
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|stringBuilder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

