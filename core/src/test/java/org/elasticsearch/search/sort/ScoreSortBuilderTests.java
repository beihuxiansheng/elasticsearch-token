begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.sort
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
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
name|SortField
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
name|ParseFieldMatcher
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
name|XContentFactory
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
name|index
operator|.
name|query
operator|.
name|QueryParseContext
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
name|DocValueFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
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
DECL|class|ScoreSortBuilderTests
specifier|public
class|class
name|ScoreSortBuilderTests
extends|extends
name|AbstractSortTestCase
argument_list|<
name|ScoreSortBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createTestItem
specifier|protected
name|ScoreSortBuilder
name|createTestItem
parameter_list|()
block|{
return|return
name|randomScoreSortBuilder
argument_list|()
return|;
block|}
DECL|method|randomScoreSortBuilder
specifier|public
specifier|static
name|ScoreSortBuilder
name|randomScoreSortBuilder
parameter_list|()
block|{
return|return
operator|new
name|ScoreSortBuilder
argument_list|()
operator|.
name|order
argument_list|(
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASC
else|:
name|SortOrder
operator|.
name|DESC
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|mutate
specifier|protected
name|ScoreSortBuilder
name|mutate
parameter_list|(
name|ScoreSortBuilder
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|ScoreSortBuilder
name|result
init|=
operator|new
name|ScoreSortBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|order
argument_list|(
name|randomValueOtherThan
argument_list|(
name|original
operator|.
name|order
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|randomFrom
argument_list|(
name|SortOrder
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Rule
DECL|field|exceptionRule
specifier|public
name|ExpectedException
name|exceptionRule
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
comment|/**      * test passing null to {@link ScoreSortBuilder#order(SortOrder)} is illegal      */
DECL|method|testIllegalOrder
specifier|public
name|void
name|testIllegalOrder
parameter_list|()
block|{
name|exceptionRule
operator|.
name|expect
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exceptionRule
operator|.
name|expectMessage
argument_list|(
literal|"sort order cannot be null."
argument_list|)
expr_stmt|;
operator|new
name|ScoreSortBuilder
argument_list|()
operator|.
name|order
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * test parsing order parameter if specified as `order` field in the json      * instead of the `reverse` field that we render in toXContent      */
DECL|method|testParseOrder
specifier|public
name|void
name|testParseOrder
parameter_list|()
throws|throws
name|IOException
block|{
name|SortOrder
name|order
init|=
name|randomBoolean
argument_list|()
condition|?
name|SortOrder
operator|.
name|ASC
else|:
name|SortOrder
operator|.
name|DESC
decl_stmt|;
name|String
name|scoreSortString
init|=
literal|"{ \"_score\": { \"order\": \""
operator|+
name|order
operator|.
name|toString
argument_list|()
operator|+
literal|"\" }}"
decl_stmt|;
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|scoreSortString
argument_list|)
operator|.
name|createParser
argument_list|(
name|scoreSortString
argument_list|)
decl_stmt|;
comment|// need to skip until parser is located on second START_OBJECT
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|ScoreSortBuilder
name|scoreSort
init|=
name|ScoreSortBuilder
operator|.
name|fromXContent
argument_list|(
name|context
argument_list|,
literal|"_score"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|order
argument_list|,
name|scoreSort
operator|.
name|order
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testReverseOptionFails
specifier|public
name|void
name|testReverseOptionFails
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|json
init|=
literal|"{ \"_score\": { \"reverse\": true }}"
decl_stmt|;
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|json
argument_list|)
operator|.
name|createParser
argument_list|(
name|json
argument_list|)
decl_stmt|;
comment|// need to skip until parser is located on second START_OBJECT
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|indicesQueriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
try|try
block|{
name|ScoreSortBuilder
operator|.
name|fromXContent
argument_list|(
name|context
argument_list|,
literal|"_score"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"adding reverse sorting option should fail with an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// all good
block|}
block|}
annotation|@
name|Override
DECL|method|sortFieldAssertions
specifier|protected
name|void
name|sortFieldAssertions
parameter_list|(
name|ScoreSortBuilder
name|builder
parameter_list|,
name|SortField
name|sortField
parameter_list|,
name|DocValueFormat
name|format
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|,
name|sortField
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|builder
operator|.
name|order
argument_list|()
operator|==
name|SortOrder
operator|.
name|DESC
condition|?
literal|false
else|:
literal|true
argument_list|,
name|sortField
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|protected
name|ScoreSortBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|context
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ScoreSortBuilder
operator|.
name|fromXContent
argument_list|(
name|context
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

