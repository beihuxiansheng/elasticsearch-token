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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermQuery
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
name|lucene
operator|.
name|BytesRefs
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
name|MappedFieldType
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|is
import|;
end_import

begin_class
DECL|class|TermQueryBuilderTests
specifier|public
class|class
name|TermQueryBuilderTests
extends|extends
name|AbstractTermQueryTestCase
argument_list|<
name|TermQueryBuilder
argument_list|>
block|{
comment|/**      * @return a TermQuery with random field name and value, optional random boost and queryname      */
annotation|@
name|Override
DECL|method|createQueryBuilder
specifier|protected
name|TermQueryBuilder
name|createQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doAssertLuceneQuery
specifier|protected
name|void
name|doAssertLuceneQuery
parameter_list|(
name|TermQueryBuilder
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
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|TermQuery
name|termQuery
init|=
operator|(
name|TermQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|termQuery
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|fieldName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|MappedFieldType
name|mapper
init|=
name|context
operator|.
name|fieldMapper
argument_list|(
name|queryBuilder
operator|.
name|fieldName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapper
operator|!=
literal|null
condition|)
block|{
name|BytesRef
name|bytesRef
init|=
name|mapper
operator|.
name|indexedValueForSearch
argument_list|(
name|queryBuilder
operator|.
name|value
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|termQuery
operator|.
name|getTerm
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|bytesRef
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|termQuery
operator|.
name|getTerm
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|queryBuilder
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTermArray
specifier|public
name|void
name|testTermArray
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|queryAsString
init|=
literal|"{\n"
operator|+
literal|"    \"term\": {\n"
operator|+
literal|"        \"age\": [34, 35]\n"
operator|+
literal|"    }\n"
operator|+
literal|"}"
decl_stmt|;
try|try
block|{
name|parseQuery
argument_list|(
name|queryAsString
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected ParsingException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParsingException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"[term] query does not support array of values"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

