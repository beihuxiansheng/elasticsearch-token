begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|IndexReader
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
name|Explanation
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
name|search
operator|.
name|function
operator|.
name|FunctionScoreQuery
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
name|search
operator|.
name|function
operator|.
name|ScoreFunction
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
name|SearchScript
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|CustomScoreQueryParser
specifier|public
class|class
name|CustomScoreQueryParser
implements|implements
name|QueryParser
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"custom_score"
decl_stmt|;
annotation|@
name|Inject
DECL|method|CustomScoreQueryParser
specifier|public
name|CustomScoreQueryParser
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
block|,
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|NAME
argument_list|)
block|}
return|;
block|}
annotation|@
name|Override
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
name|Query
name|query
init|=
literal|null
decl_stmt|;
name|boolean
name|queryFound
init|=
literal|false
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|String
name|script
init|=
literal|null
decl_stmt|;
name|String
name|scriptLang
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
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
literal|"query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|query
operator|=
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
expr_stmt|;
name|queryFound
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
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
name|vars
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
literal|"[custom_score] query does not support ["
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
literal|"script"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|script
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
literal|"lang"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|scriptLang
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
literal|"boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|boost
operator|=
name|parser
operator|.
name|floatValue
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
literal|"[custom_score] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|queryFound
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
literal|"[custom_score] requires 'query' field"
argument_list|)
throw|;
block|}
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
literal|"[custom_score] requires 'script' field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SearchScript
name|searchScript
decl_stmt|;
try|try
block|{
name|searchScript
operator|=
name|parseContext
operator|.
name|scriptService
argument_list|()
operator|.
name|search
argument_list|(
name|parseContext
operator|.
name|lookup
argument_list|()
argument_list|,
name|scriptLang
argument_list|,
name|script
argument_list|,
name|vars
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
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
literal|"[custom_score] the script could not be loaded"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|FunctionScoreQuery
name|functionScoreQuery
init|=
operator|new
name|FunctionScoreQuery
argument_list|(
name|query
argument_list|,
operator|new
name|ScriptScoreFunction
argument_list|(
name|script
argument_list|,
name|vars
argument_list|,
name|searchScript
argument_list|)
argument_list|)
decl_stmt|;
name|functionScoreQuery
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|functionScoreQuery
return|;
block|}
DECL|class|ScriptScoreFunction
specifier|public
specifier|static
class|class
name|ScriptScoreFunction
implements|implements
name|ScoreFunction
block|{
DECL|field|sScript
specifier|private
specifier|final
name|String
name|sScript
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
DECL|field|script
specifier|private
specifier|final
name|SearchScript
name|script
decl_stmt|;
DECL|method|ScriptScoreFunction
specifier|public
name|ScriptScoreFunction
parameter_list|(
name|String
name|sScript
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|,
name|SearchScript
name|script
parameter_list|)
block|{
name|this
operator|.
name|sScript
operator|=
name|sScript
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|script
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|(
name|int
name|docId
parameter_list|,
name|float
name|subQueryScore
parameter_list|)
block|{
name|script
operator|.
name|setNextDocId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|script
operator|.
name|setNextScore
argument_list|(
name|subQueryScore
argument_list|)
expr_stmt|;
return|return
name|script
operator|.
name|runAsFloat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|factor
specifier|public
name|float
name|factor
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
comment|// just the factor, so don't provide _score
name|script
operator|.
name|setNextDocId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return
name|script
operator|.
name|runAsFloat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|explainScore
specifier|public
name|Explanation
name|explainScore
parameter_list|(
name|int
name|docId
parameter_list|,
name|Explanation
name|subQueryExpl
parameter_list|)
block|{
name|float
name|score
init|=
name|score
argument_list|(
name|docId
argument_list|,
name|subQueryExpl
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|Explanation
name|exp
init|=
operator|new
name|Explanation
argument_list|(
name|score
argument_list|,
literal|"script score function: product of:"
argument_list|)
decl_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
name|subQueryExpl
argument_list|)
expr_stmt|;
return|return
name|exp
return|;
block|}
annotation|@
name|Override
DECL|method|explainFactor
specifier|public
name|Explanation
name|explainFactor
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|new
name|Explanation
argument_list|(
name|factor
argument_list|(
name|docId
argument_list|)
argument_list|,
literal|"scriptFactor"
argument_list|)
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
literal|"script["
operator|+
name|sScript
operator|+
literal|"], params ["
operator|+
name|params
operator|+
literal|"]"
return|;
block|}
block|}
block|}
end_class

end_unit

