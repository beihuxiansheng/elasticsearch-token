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
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|nullValue
import|;
end_import

begin_class
DECL|class|BooleansTests
specifier|public
class|class
name|BooleansTests
extends|extends
name|ESTestCase
block|{
DECL|method|testIsBoolean
specifier|public
name|void
name|testIsBoolean
parameter_list|()
block|{
name|String
index|[]
name|booleans
init|=
operator|new
name|String
index|[]
block|{
literal|"true"
block|,
literal|"false"
block|,
literal|"on"
block|,
literal|"off"
block|,
literal|"yes"
block|,
literal|"no"
block|,
literal|"0"
block|,
literal|"1"
block|}
decl_stmt|;
name|String
index|[]
name|notBooleans
init|=
operator|new
name|String
index|[]
block|{
literal|"11"
block|,
literal|"00"
block|,
literal|"sdfsdfsf"
block|,
literal|"F"
block|,
literal|"T"
block|}
decl_stmt|;
name|assertThat
argument_list|(
name|Booleans
operator|.
name|isBoolean
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|1
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
name|b
range|:
name|booleans
control|)
block|{
name|String
name|t
init|=
literal|"prefix"
operator|+
name|b
operator|+
literal|"suffix"
decl_stmt|;
name|assertThat
argument_list|(
literal|"failed to recognize ["
operator|+
name|b
operator|+
literal|"] as boolean"
argument_list|,
name|Booleans
operator|.
name|isBoolean
argument_list|(
name|t
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|"prefix"
operator|.
name|length
argument_list|()
argument_list|,
name|b
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|nb
range|:
name|notBooleans
control|)
block|{
name|String
name|t
init|=
literal|"prefix"
operator|+
name|nb
operator|+
literal|"suffix"
decl_stmt|;
name|assertThat
argument_list|(
literal|"recognized ["
operator|+
name|nb
operator|+
literal|"] as boolean"
argument_list|,
name|Booleans
operator|.
name|isBoolean
argument_list|(
name|t
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|"prefix"
operator|.
name|length
argument_list|()
argument_list|,
name|nb
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseBoolean
specifier|public
name|void
name|testParseBoolean
parameter_list|()
block|{
name|assertThat
argument_list|(
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|randomFrom
argument_list|(
literal|"true"
argument_list|,
literal|"on"
argument_list|,
literal|"yes"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
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
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|randomFrom
argument_list|(
literal|"false"
argument_list|,
literal|"off"
argument_list|,
literal|"no"
argument_list|,
literal|"0"
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
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
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|randomFrom
argument_list|(
literal|"true"
argument_list|,
literal|"on"
argument_list|,
literal|"yes"
argument_list|)
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
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
name|Booleans
operator|.
name|parseBoolean
argument_list|(
literal|null
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
name|assertThat
argument_list|(
name|Booleans
operator|.
name|parseBoolean
argument_list|(
literal|null
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
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|randomFrom
argument_list|(
literal|"true"
argument_list|,
literal|"on"
argument_list|,
literal|"yes"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|randomFrom
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|,
literal|null
argument_list|)
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
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|randomFrom
argument_list|(
literal|"false"
argument_list|,
literal|"off"
argument_list|,
literal|"no"
argument_list|,
literal|"0"
argument_list|)
argument_list|,
name|randomFrom
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|,
literal|null
argument_list|)
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
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|randomFrom
argument_list|(
literal|"true"
argument_list|,
literal|"on"
argument_list|,
literal|"yes"
argument_list|)
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|randomFrom
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|,
literal|null
argument_list|)
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
name|Booleans
operator|.
name|parseBoolean
argument_list|(
literal|null
argument_list|,
name|Boolean
operator|.
name|FALSE
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
name|Booleans
operator|.
name|parseBoolean
argument_list|(
literal|null
argument_list|,
name|Boolean
operator|.
name|TRUE
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
name|Booleans
operator|.
name|parseBoolean
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|char
index|[]
name|chars
init|=
name|randomFrom
argument_list|(
literal|"true"
argument_list|,
literal|"on"
argument_list|,
literal|"yes"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|chars
operator|=
name|randomFrom
argument_list|(
literal|"false"
argument_list|,
literal|"off"
argument_list|,
literal|"no"
argument_list|,
literal|"0"
argument_list|)
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|chars
operator|=
name|randomFrom
argument_list|(
literal|"true"
argument_list|,
literal|"on"
argument_list|,
literal|"yes"
argument_list|)
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseBooleanExact
specifier|public
name|void
name|testParseBooleanExact
parameter_list|()
block|{
name|assertThat
argument_list|(
name|Booleans
operator|.
name|parseBooleanExact
argument_list|(
name|randomFrom
argument_list|(
literal|"true"
argument_list|,
literal|"on"
argument_list|,
literal|"yes"
argument_list|,
literal|"1"
argument_list|)
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
name|Booleans
operator|.
name|parseBooleanExact
argument_list|(
name|randomFrom
argument_list|(
literal|"false"
argument_list|,
literal|"off"
argument_list|,
literal|"no"
argument_list|,
literal|"0"
argument_list|)
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
name|Booleans
operator|.
name|parseBooleanExact
argument_list|(
name|randomFrom
argument_list|(
literal|"fred"
argument_list|,
literal|"foo"
argument_list|,
literal|"barney"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception while parsing invalid boolean value "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ex
operator|instanceof
name|IllegalArgumentException
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIsExplicit
specifier|public
name|void
name|testIsExplicit
parameter_list|()
block|{
name|assertThat
argument_list|(
name|Booleans
operator|.
name|isExplicitFalse
argument_list|(
name|randomFrom
argument_list|(
literal|"true"
argument_list|,
literal|"on"
argument_list|,
literal|"yes"
argument_list|,
literal|"1"
argument_list|,
literal|"foo"
argument_list|,
literal|null
argument_list|)
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
name|Booleans
operator|.
name|isExplicitFalse
argument_list|(
name|randomFrom
argument_list|(
literal|"false"
argument_list|,
literal|"off"
argument_list|,
literal|"no"
argument_list|,
literal|"0"
argument_list|)
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
name|Booleans
operator|.
name|isExplicitTrue
argument_list|(
name|randomFrom
argument_list|(
literal|"true"
argument_list|,
literal|"on"
argument_list|,
literal|"yes"
argument_list|,
literal|"1"
argument_list|)
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
name|Booleans
operator|.
name|isExplicitTrue
argument_list|(
name|randomFrom
argument_list|(
literal|"false"
argument_list|,
literal|"off"
argument_list|,
literal|"no"
argument_list|,
literal|"0"
argument_list|,
literal|"foo"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

