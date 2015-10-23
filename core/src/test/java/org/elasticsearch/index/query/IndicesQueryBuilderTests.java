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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|IndicesQueryBuilderTests
specifier|public
class|class
name|IndicesQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|IndicesQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|IndicesQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|String
index|[]
name|indices
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|indices
operator|=
operator|new
name|String
index|[]
block|{
name|getIndex
argument_list|()
operator|.
name|getName
argument_list|()
block|}
expr_stmt|;
block|}
else|else
block|{
name|indices
operator|=
name|generateRandomStringArray
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|IndicesQueryBuilder
name|query
init|=
operator|new
name|IndicesQueryBuilder
argument_list|(
name|RandomQueryBuilder
operator|.
name|createQuery
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|indices
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|randomInt
argument_list|(
literal|2
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|query
operator|.
name|noMatchQuery
argument_list|(
name|RandomQueryBuilder
operator|.
name|createQuery
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|query
operator|.
name|noMatchQuery
argument_list|(
name|randomFrom
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|,
operator|new
name|MatchNoneQueryBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// do not set noMatchQuery
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|doAssertLuceneQuery
specifier|protected
name|void
name|doAssertLuceneQuery
parameter_list|(
name|IndicesQueryBuilder
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|expected
decl_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|indices
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|&&
name|getIndex
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|queryBuilder
operator|.
name|indices
argument_list|()
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|expected
operator|=
name|queryBuilder
operator|.
name|innerQuery
argument_list|()
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expected
operator|=
name|queryBuilder
operator|.
name|noMatchQuery
argument_list|()
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|testIllegalArguments
specifier|public
name|void
name|testIllegalArguments
parameter_list|()
block|{
try|try
block|{
operator|new
name|IndicesQueryBuilder
argument_list|(
literal|null
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"cannot be null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
operator|new
name|IndicesQueryBuilder
argument_list|(
name|EmptyQueryBuilder
operator|.
name|PROTOTYPE
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"cannot be null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
operator|new
name|IndicesQueryBuilder
argument_list|(
name|EmptyQueryBuilder
operator|.
name|PROTOTYPE
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"cannot be empty"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|IndicesQueryBuilder
name|indicesQueryBuilder
init|=
operator|new
name|IndicesQueryBuilder
argument_list|(
name|EmptyQueryBuilder
operator|.
name|PROTOTYPE
argument_list|,
literal|"index"
argument_list|)
decl_stmt|;
try|try
block|{
name|indicesQueryBuilder
operator|.
name|noMatchQuery
argument_list|(
operator|(
name|QueryBuilder
operator|)
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"cannot be null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|indicesQueryBuilder
operator|.
name|noMatchQuery
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"cannot be null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class

end_unit

