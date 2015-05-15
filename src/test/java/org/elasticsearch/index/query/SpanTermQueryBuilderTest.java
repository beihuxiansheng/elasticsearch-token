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
name|spans
operator|.
name|SpanTermQuery
import|;
end_import

begin_class
DECL|class|SpanTermQueryBuilderTest
specifier|public
class|class
name|SpanTermQueryBuilderTest
extends|extends
name|BaseTermQueryTestCase
argument_list|<
name|SpanTermQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createEmptyQueryBuilder
specifier|protected
name|SpanTermQueryBuilder
name|createEmptyQueryBuilder
parameter_list|()
block|{
return|return
operator|new
name|SpanTermQueryBuilder
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createQueryBuilder
specifier|protected
name|SpanTermQueryBuilder
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
name|SpanTermQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createLuceneTermQuery
specifier|protected
name|Query
name|createLuceneTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
return|return
operator|new
name|SpanTermQuery
argument_list|(
name|term
argument_list|)
return|;
block|}
block|}
end_class

end_unit

