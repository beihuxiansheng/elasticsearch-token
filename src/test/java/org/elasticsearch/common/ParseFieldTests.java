begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|ArrayUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
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
name|util
operator|.
name|EnumSet
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
name|*
import|;
end_import

begin_class
DECL|class|ParseFieldTests
specifier|public
class|class
name|ParseFieldTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testParse
specifier|public
name|void
name|testParse
parameter_list|()
block|{
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[]
block|{
literal|"foo_bar"
block|,
literal|"fooBar"
block|}
decl_stmt|;
name|ParseField
name|field
init|=
operator|new
name|ParseField
argument_list|(
name|randomFrom
argument_list|(
name|values
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|deprecated
init|=
operator|new
name|String
index|[]
block|{
literal|"barFoo"
block|,
literal|"bar_foo"
block|}
decl_stmt|;
name|ParseField
name|withDeprecations
init|=
name|field
operator|.
name|withDeprecation
argument_list|(
literal|"Foobar"
argument_list|,
name|randomFrom
argument_list|(
name|deprecated
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|field
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|withDeprecations
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
name|randomFrom
argument_list|(
name|values
argument_list|)
argument_list|,
name|ParseField
operator|.
name|EMPTY_FLAGS
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
literal|"foo bar"
argument_list|,
name|ParseField
operator|.
name|EMPTY_FLAGS
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
name|randomFrom
argument_list|(
name|deprecated
argument_list|)
argument_list|,
name|ParseField
operator|.
name|EMPTY_FLAGS
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
literal|"barFoo"
argument_list|,
name|ParseField
operator|.
name|EMPTY_FLAGS
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|withDeprecations
operator|.
name|match
argument_list|(
name|randomFrom
argument_list|(
name|values
argument_list|)
argument_list|,
name|ParseField
operator|.
name|EMPTY_FLAGS
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|withDeprecations
operator|.
name|match
argument_list|(
literal|"foo bar"
argument_list|,
name|ParseField
operator|.
name|EMPTY_FLAGS
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|withDeprecations
operator|.
name|match
argument_list|(
name|randomFrom
argument_list|(
name|deprecated
argument_list|)
argument_list|,
name|ParseField
operator|.
name|EMPTY_FLAGS
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|withDeprecations
operator|.
name|match
argument_list|(
literal|"barFoo"
argument_list|,
name|ParseField
operator|.
name|EMPTY_FLAGS
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// now with strict mode
name|EnumSet
argument_list|<
name|ParseField
operator|.
name|Flag
argument_list|>
name|flags
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|ParseField
operator|.
name|Flag
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
name|randomFrom
argument_list|(
name|values
argument_list|)
argument_list|,
name|flags
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
literal|"foo bar"
argument_list|,
name|flags
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
name|randomFrom
argument_list|(
name|deprecated
argument_list|)
argument_list|,
name|flags
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
literal|"barFoo"
argument_list|,
name|flags
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|withDeprecations
operator|.
name|match
argument_list|(
name|randomFrom
argument_list|(
name|values
argument_list|)
argument_list|,
name|flags
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|withDeprecations
operator|.
name|match
argument_list|(
literal|"foo bar"
argument_list|,
name|flags
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|withDeprecations
operator|.
name|match
argument_list|(
name|randomFrom
argument_list|(
name|deprecated
argument_list|)
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{          }
try|try
block|{
name|withDeprecations
operator|.
name|match
argument_list|(
literal|"barFoo"
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{          }
block|}
annotation|@
name|Test
DECL|method|testAllDeprecated
specifier|public
name|void
name|testAllDeprecated
parameter_list|()
block|{
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[]
block|{
literal|"like_text"
block|,
literal|"likeText"
block|}
decl_stmt|;
name|boolean
name|withDeprecatedNames
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|String
index|[]
name|deprecated
init|=
operator|new
name|String
index|[]
block|{
literal|"text"
block|,
literal|"same_as_text"
block|}
decl_stmt|;
name|String
index|[]
name|allValues
init|=
name|values
decl_stmt|;
if|if
condition|(
name|withDeprecatedNames
condition|)
block|{
name|allValues
operator|=
name|ArrayUtils
operator|.
name|addAll
argument_list|(
name|values
argument_list|,
name|deprecated
argument_list|)
expr_stmt|;
block|}
name|ParseField
name|field
init|=
operator|new
name|ParseField
argument_list|(
name|randomFrom
argument_list|(
name|values
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|withDeprecatedNames
condition|)
block|{
name|field
operator|=
name|field
operator|.
name|withDeprecation
argument_list|(
name|deprecated
argument_list|)
expr_stmt|;
block|}
name|field
operator|=
name|field
operator|.
name|withAllDeprecated
argument_list|(
literal|"like"
argument_list|)
expr_stmt|;
comment|// strict mode off
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
name|randomFrom
argument_list|(
name|allValues
argument_list|)
argument_list|,
name|ParseField
operator|.
name|EMPTY_FLAGS
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
literal|"not a field name"
argument_list|,
name|ParseField
operator|.
name|EMPTY_FLAGS
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// now with strict mode
name|EnumSet
argument_list|<
name|ParseField
operator|.
name|Flag
argument_list|>
name|flags
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|ParseField
operator|.
name|Flag
operator|.
name|STRICT
argument_list|)
decl_stmt|;
try|try
block|{
name|field
operator|.
name|match
argument_list|(
name|randomFrom
argument_list|(
name|allValues
argument_list|)
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{         }
block|}
block|}
end_class

end_unit

