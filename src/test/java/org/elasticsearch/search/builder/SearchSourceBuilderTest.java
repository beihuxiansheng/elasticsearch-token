begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.builder
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|builder
package|;
end_package

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
name|json
operator|.
name|JsonXContent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|*
import|;
end_import

begin_class
DECL|class|SearchSourceBuilderTest
specifier|public
class|class
name|SearchSourceBuilderTest
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|builder
name|SearchSourceBuilder
name|builder
init|=
operator|new
name|SearchSourceBuilder
argument_list|()
decl_stmt|;
annotation|@
name|Test
comment|// issue #6632
DECL|method|testThatSearchSourceBuilderIncludesExcludesAreAppliedCorrectly
specifier|public
name|void
name|testThatSearchSourceBuilderIncludesExcludesAreAppliedCorrectly
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|fetchSource
argument_list|(
literal|"foo"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertIncludes
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertExcludes
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|fetchSource
argument_list|(
literal|null
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertIncludes
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertExcludes
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|fetchSource
argument_list|(
literal|null
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|}
argument_list|)
expr_stmt|;
name|assertIncludes
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertExcludes
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|fetchSource
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"foo"
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertIncludes
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertExcludes
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|fetchSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertIncludes
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertExcludes
argument_list|(
name|builder
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|fetchSource
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"foo"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"bar"
block|,
literal|"baz"
block|}
argument_list|)
expr_stmt|;
name|assertIncludes
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertExcludes
argument_list|(
name|builder
argument_list|,
literal|"bar"
argument_list|,
literal|"baz"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertIncludes
specifier|private
name|void
name|assertIncludes
parameter_list|(
name|SearchSourceBuilder
name|builder
parameter_list|,
name|String
modifier|...
name|elems
parameter_list|)
throws|throws
name|IOException
block|{
name|assertFieldValues
argument_list|(
name|builder
argument_list|,
literal|"includes"
argument_list|,
name|elems
argument_list|)
expr_stmt|;
block|}
DECL|method|assertExcludes
specifier|private
name|void
name|assertExcludes
parameter_list|(
name|SearchSourceBuilder
name|builder
parameter_list|,
name|String
modifier|...
name|elems
parameter_list|)
throws|throws
name|IOException
block|{
name|assertFieldValues
argument_list|(
name|builder
argument_list|,
literal|"excludes"
argument_list|,
name|elems
argument_list|)
expr_stmt|;
block|}
DECL|method|assertFieldValues
specifier|private
name|void
name|assertFieldValues
parameter_list|(
name|SearchSourceBuilder
name|builder
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|String
modifier|...
name|elems
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|getSourceMap
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|map
argument_list|,
name|hasKey
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
argument_list|,
name|is
argument_list|(
name|instanceOf
argument_list|(
name|List
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|castedList
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|map
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|castedList
argument_list|,
name|hasSize
argument_list|(
name|elems
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|castedList
argument_list|,
name|hasItems
argument_list|(
name|elems
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getSourceMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getSourceMap
parameter_list|(
name|SearchSourceBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
init|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|mapAndClose
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|data
argument_list|,
name|hasKey
argument_list|(
literal|"_source"
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|data
operator|.
name|get
argument_list|(
literal|"_source"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

