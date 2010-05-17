begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|support
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
name|analysis
operator|.
name|Analyzer
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
name|index
operator|.
name|Term
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
name|queryParser
operator|.
name|ParseException
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
name|queryParser
operator|.
name|QueryParser
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
name|BooleanClause
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
name|MultiTermQuery
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
name|index
operator|.
name|cache
operator|.
name|IndexCache
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
name|mapper
operator|.
name|FieldMapper
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
name|mapper
operator|.
name|FieldMappers
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
name|mapper
operator|.
name|MapperService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|lucene
operator|.
name|Lucene
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|support
operator|.
name|QueryParsers
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|lucene
operator|.
name|search
operator|.
name|Queries
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A query parser that uses the {@link MapperService} in order to build smarter  * queries based on the mapping information.  *  *<p>Also breaks fields with [type].[name] into a boolean query that must include the type  * as well as the query on the name.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|MapperQueryParser
specifier|public
class|class
name|MapperQueryParser
extends|extends
name|QueryParser
block|{
DECL|field|mapperService
specifier|private
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|indexCache
specifier|private
specifier|final
name|IndexCache
name|indexCache
decl_stmt|;
DECL|field|currentMapper
specifier|private
name|FieldMapper
name|currentMapper
decl_stmt|;
DECL|method|MapperQueryParser
specifier|public
name|MapperQueryParser
parameter_list|(
name|String
name|defaultField
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
annotation|@
name|Nullable
name|MapperService
name|mapperService
parameter_list|,
annotation|@
name|Nullable
name|IndexCache
name|indexCache
parameter_list|)
block|{
name|super
argument_list|(
name|Lucene
operator|.
name|QUERYPARSER_VERSION
argument_list|,
name|defaultField
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|this
operator|.
name|mapperService
operator|=
name|mapperService
expr_stmt|;
name|this
operator|.
name|indexCache
operator|=
name|indexCache
expr_stmt|;
name|setMultiTermRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
argument_list|)
expr_stmt|;
block|}
DECL|method|newTermQuery
annotation|@
name|Override
specifier|protected
name|Query
name|newTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
if|if
condition|(
name|currentMapper
operator|!=
literal|null
condition|)
block|{
name|Query
name|termQuery
init|=
name|currentMapper
operator|.
name|queryStringTermQuery
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|termQuery
operator|!=
literal|null
condition|)
block|{
return|return
name|termQuery
return|;
block|}
block|}
return|return
name|super
operator|.
name|newTermQuery
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|getFieldQuery
annotation|@
name|Override
specifier|public
name|Query
name|getFieldQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|)
throws|throws
name|ParseException
block|{
name|currentMapper
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|mapperService
operator|!=
literal|null
condition|)
block|{
name|MapperService
operator|.
name|SmartNameFieldMappers
name|fieldMappers
init|=
name|mapperService
operator|.
name|smartName
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMappers
operator|!=
literal|null
condition|)
block|{
name|currentMapper
operator|=
name|fieldMappers
operator|.
name|fieldMappers
argument_list|()
operator|.
name|mapper
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentMapper
operator|!=
literal|null
condition|)
block|{
name|Query
name|query
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|currentMapper
operator|.
name|useFieldQueryWithQueryString
argument_list|()
condition|)
block|{
name|query
operator|=
name|currentMapper
operator|.
name|fieldQuery
argument_list|(
name|queryText
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
name|query
operator|=
name|super
operator|.
name|getFieldQuery
argument_list|(
name|currentMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|queryText
argument_list|)
expr_stmt|;
block|}
return|return
name|wrapSmartNameQuery
argument_list|(
name|query
argument_list|,
name|fieldMappers
argument_list|,
name|indexCache
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|super
operator|.
name|getFieldQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|)
return|;
block|}
DECL|method|getRangeQuery
annotation|@
name|Override
specifier|protected
name|Query
name|getRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
literal|"*"
operator|.
name|equals
argument_list|(
name|part1
argument_list|)
condition|)
block|{
name|part1
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
literal|"*"
operator|.
name|equals
argument_list|(
name|part2
argument_list|)
condition|)
block|{
name|part2
operator|=
literal|null
expr_stmt|;
block|}
name|currentMapper
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|mapperService
operator|!=
literal|null
condition|)
block|{
name|MapperService
operator|.
name|SmartNameFieldMappers
name|fieldMappers
init|=
name|mapperService
operator|.
name|smartName
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMappers
operator|!=
literal|null
condition|)
block|{
name|currentMapper
operator|=
name|fieldMappers
operator|.
name|fieldMappers
argument_list|()
operator|.
name|mapper
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentMapper
operator|!=
literal|null
condition|)
block|{
name|Query
name|rangeQuery
init|=
name|currentMapper
operator|.
name|rangeQuery
argument_list|(
name|part1
argument_list|,
name|part2
argument_list|,
name|inclusive
argument_list|,
name|inclusive
argument_list|)
decl_stmt|;
return|return
name|wrapSmartNameQuery
argument_list|(
name|rangeQuery
argument_list|,
name|fieldMappers
argument_list|,
name|indexCache
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|newRangeQuery
argument_list|(
name|field
argument_list|,
name|part1
argument_list|,
name|part2
argument_list|,
name|inclusive
argument_list|)
return|;
block|}
DECL|method|getPrefixQuery
annotation|@
name|Override
specifier|protected
name|Query
name|getPrefixQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|indexedNameField
init|=
name|field
decl_stmt|;
name|currentMapper
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|mapperService
operator|!=
literal|null
condition|)
block|{
name|MapperService
operator|.
name|SmartNameFieldMappers
name|fieldMappers
init|=
name|mapperService
operator|.
name|smartName
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMappers
operator|!=
literal|null
condition|)
block|{
name|currentMapper
operator|=
name|fieldMappers
operator|.
name|fieldMappers
argument_list|()
operator|.
name|mapper
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentMapper
operator|!=
literal|null
condition|)
block|{
name|indexedNameField
operator|=
name|currentMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
block|}
return|return
name|wrapSmartNameQuery
argument_list|(
name|super
operator|.
name|getPrefixQuery
argument_list|(
name|indexedNameField
argument_list|,
name|termStr
argument_list|)
argument_list|,
name|fieldMappers
argument_list|,
name|indexCache
argument_list|)
return|;
block|}
block|}
return|return
name|super
operator|.
name|getPrefixQuery
argument_list|(
name|indexedNameField
argument_list|,
name|termStr
argument_list|)
return|;
block|}
DECL|method|getFuzzyQuery
annotation|@
name|Override
specifier|protected
name|Query
name|getFuzzyQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|,
name|float
name|minSimilarity
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|indexedNameField
init|=
name|field
decl_stmt|;
name|currentMapper
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|mapperService
operator|!=
literal|null
condition|)
block|{
name|MapperService
operator|.
name|SmartNameFieldMappers
name|fieldMappers
init|=
name|mapperService
operator|.
name|smartName
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMappers
operator|!=
literal|null
condition|)
block|{
name|currentMapper
operator|=
name|fieldMappers
operator|.
name|fieldMappers
argument_list|()
operator|.
name|mapper
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentMapper
operator|!=
literal|null
condition|)
block|{
name|indexedNameField
operator|=
name|currentMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
block|}
return|return
name|wrapSmartNameQuery
argument_list|(
name|super
operator|.
name|getFuzzyQuery
argument_list|(
name|indexedNameField
argument_list|,
name|termStr
argument_list|,
name|minSimilarity
argument_list|)
argument_list|,
name|fieldMappers
argument_list|,
name|indexCache
argument_list|)
return|;
block|}
block|}
return|return
name|super
operator|.
name|getFuzzyQuery
argument_list|(
name|indexedNameField
argument_list|,
name|termStr
argument_list|,
name|minSimilarity
argument_list|)
return|;
block|}
DECL|method|getWildcardQuery
annotation|@
name|Override
specifier|protected
name|Query
name|getWildcardQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|indexedNameField
init|=
name|field
decl_stmt|;
name|currentMapper
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|mapperService
operator|!=
literal|null
condition|)
block|{
name|MapperService
operator|.
name|SmartNameFieldMappers
name|fieldMappers
init|=
name|mapperService
operator|.
name|smartName
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMappers
operator|!=
literal|null
condition|)
block|{
name|currentMapper
operator|=
name|fieldMappers
operator|.
name|fieldMappers
argument_list|()
operator|.
name|mapper
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentMapper
operator|!=
literal|null
condition|)
block|{
name|indexedNameField
operator|=
name|currentMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
block|}
return|return
name|wrapSmartNameQuery
argument_list|(
name|super
operator|.
name|getWildcardQuery
argument_list|(
name|indexedNameField
argument_list|,
name|termStr
argument_list|)
argument_list|,
name|fieldMappers
argument_list|,
name|indexCache
argument_list|)
return|;
block|}
block|}
return|return
name|super
operator|.
name|getWildcardQuery
argument_list|(
name|indexedNameField
argument_list|,
name|termStr
argument_list|)
return|;
block|}
DECL|method|getBooleanQuery
annotation|@
name|Override
specifier|protected
name|Query
name|getBooleanQuery
parameter_list|(
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
parameter_list|,
name|boolean
name|disableCoord
parameter_list|)
throws|throws
name|ParseException
block|{
name|Query
name|q
init|=
name|super
operator|.
name|getBooleanQuery
argument_list|(
name|clauses
argument_list|,
name|disableCoord
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|optimizeQuery
argument_list|(
name|fixNegativeQueryIfNeeded
argument_list|(
name|q
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fieldMapper
specifier|protected
name|FieldMapper
name|fieldMapper
parameter_list|(
name|String
name|smartName
parameter_list|)
block|{
if|if
condition|(
name|mapperService
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|FieldMappers
name|fieldMappers
init|=
name|mapperService
operator|.
name|smartNameFieldMappers
argument_list|(
name|smartName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMappers
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|fieldMappers
operator|.
name|mapper
argument_list|()
return|;
block|}
block|}
end_class

end_unit

