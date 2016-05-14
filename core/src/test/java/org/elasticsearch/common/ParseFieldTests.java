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
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
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
name|containsString
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
name|is
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
name|not
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
name|sameInstance
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|collection
operator|.
name|IsArrayContainingInAnyOrder
operator|.
name|arrayContainingInAnyOrder
import|;
end_import

begin_class
DECL|class|ParseFieldTests
specifier|public
class|class
name|ParseFieldTests
extends|extends
name|ESTestCase
block|{
DECL|method|testParse
specifier|public
name|void
name|testParse
parameter_list|()
block|{
name|String
name|name
init|=
literal|"foo_bar"
decl_stmt|;
name|ParseField
name|field
init|=
operator|new
name|ParseField
argument_list|(
name|name
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
block|,
literal|"Foobar"
block|}
decl_stmt|;
name|ParseField
name|withDeprecations
init|=
name|field
operator|.
name|withDeprecation
argument_list|(
name|deprecated
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
name|name
argument_list|,
literal|false
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
literal|false
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|deprecatedName
range|:
name|deprecated
control|)
block|{
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
name|deprecatedName
argument_list|,
literal|false
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|withDeprecations
operator|.
name|match
argument_list|(
name|name
argument_list|,
literal|false
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
literal|false
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|deprecatedName
range|:
name|deprecated
control|)
block|{
name|assertThat
argument_list|(
name|withDeprecations
operator|.
name|match
argument_list|(
name|deprecatedName
argument_list|,
literal|false
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// now with strict mode
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
name|name
argument_list|,
literal|true
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
literal|true
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|deprecatedName
range|:
name|deprecated
control|)
block|{
name|assertThat
argument_list|(
name|field
operator|.
name|match
argument_list|(
name|deprecatedName
argument_list|,
literal|true
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|withDeprecations
operator|.
name|match
argument_list|(
name|name
argument_list|,
literal|true
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
literal|true
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|deprecatedName
range|:
name|deprecated
control|)
block|{
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|withDeprecations
operator|.
name|match
argument_list|(
name|deprecatedName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"used, expected [foo_bar] instead"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAllDeprecated
specifier|public
name|void
name|testAllDeprecated
parameter_list|()
block|{
name|String
name|name
init|=
literal|"like_text"
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
decl_stmt|;
if|if
condition|(
name|withDeprecatedNames
condition|)
block|{
name|String
index|[]
name|newArray
init|=
operator|new
name|String
index|[
literal|1
operator|+
name|deprecated
operator|.
name|length
index|]
decl_stmt|;
name|newArray
index|[
literal|0
index|]
operator|=
name|name
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|deprecated
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|1
argument_list|,
name|deprecated
operator|.
name|length
argument_list|)
expr_stmt|;
name|allValues
operator|=
name|newArray
expr_stmt|;
block|}
else|else
block|{
name|allValues
operator|=
operator|new
name|String
index|[]
block|{
name|name
block|}
expr_stmt|;
block|}
name|ParseField
name|field
decl_stmt|;
if|if
condition|(
name|withDeprecatedNames
condition|)
block|{
name|field
operator|=
operator|new
name|ParseField
argument_list|(
name|name
argument_list|)
operator|.
name|withDeprecation
argument_list|(
name|deprecated
argument_list|)
operator|.
name|withAllDeprecated
argument_list|(
literal|"like"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|field
operator|=
operator|new
name|ParseField
argument_list|(
name|name
argument_list|)
operator|.
name|withAllDeprecated
argument_list|(
literal|"like"
argument_list|)
expr_stmt|;
block|}
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
literal|false
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
literal|false
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// now with strict mode
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|field
operator|.
name|match
argument_list|(
name|randomFrom
argument_list|(
name|allValues
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|" used, replaced by [like]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetAllNamesIncludedDeprecated
specifier|public
name|void
name|testGetAllNamesIncludedDeprecated
parameter_list|()
block|{
name|ParseField
name|parseField
init|=
operator|new
name|ParseField
argument_list|(
literal|"terms"
argument_list|,
literal|"in"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parseField
operator|.
name|getAllNamesIncludedDeprecated
argument_list|()
argument_list|,
name|arrayContainingInAnyOrder
argument_list|(
literal|"terms"
argument_list|,
literal|"in"
argument_list|)
argument_list|)
expr_stmt|;
name|parseField
operator|=
operator|new
name|ParseField
argument_list|(
literal|"more_like_this"
argument_list|,
literal|"mlt"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parseField
operator|.
name|getAllNamesIncludedDeprecated
argument_list|()
argument_list|,
name|arrayContainingInAnyOrder
argument_list|(
literal|"more_like_this"
argument_list|,
literal|"mlt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

