begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.yaml.section
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|yaml
operator|.
name|section
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
name|Nullable
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|XContentLocation
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
name|Locale
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|RegexMatcher
operator|.
name|matches
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
name|equalTo
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
name|instanceOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Represents a match assert section:  *  *   - match:   { get.fields._routing: "5" }  *  */
end_comment

begin_class
DECL|class|MatchAssertion
specifier|public
class|class
name|MatchAssertion
extends|extends
name|Assertion
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|MatchAssertion
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|MatchAssertion
specifier|public
name|MatchAssertion
parameter_list|(
name|XContentLocation
name|location
parameter_list|,
name|String
name|field
parameter_list|,
name|Object
name|expectedValue
parameter_list|)
block|{
name|super
argument_list|(
name|location
argument_list|,
name|field
argument_list|,
name|expectedValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doAssert
specifier|protected
name|void
name|doAssert
parameter_list|(
name|Object
name|actualValue
parameter_list|,
name|Object
name|expectedValue
parameter_list|)
block|{
comment|//if the value is wrapped into / it is a regexp (e.g. /s+d+/)
if|if
condition|(
name|expectedValue
operator|instanceof
name|String
condition|)
block|{
name|String
name|expValue
init|=
operator|(
operator|(
name|String
operator|)
name|expectedValue
operator|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|expValue
operator|.
name|length
argument_list|()
operator|>
literal|2
operator|&&
name|expValue
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
operator|&&
name|expValue
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
literal|"field ["
operator|+
name|getField
argument_list|()
operator|+
literal|"] was expected to be of type String but is an instanceof ["
operator|+
name|safeClass
argument_list|(
name|actualValue
argument_list|)
operator|+
literal|"]"
argument_list|,
name|actualValue
argument_list|,
name|instanceOf
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|stringValue
init|=
operator|(
name|String
operator|)
name|actualValue
decl_stmt|;
name|String
name|regex
init|=
name|expValue
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|expValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"assert that [{}] matches [{}]"
argument_list|,
name|stringValue
argument_list|,
name|regex
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"field ["
operator|+
name|getField
argument_list|()
operator|+
literal|"] was expected to match the provided regex but didn't"
argument_list|,
name|stringValue
argument_list|,
name|matches
argument_list|(
name|regex
argument_list|,
name|Pattern
operator|.
name|COMMENTS
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|assertNotNull
argument_list|(
literal|"field ["
operator|+
name|getField
argument_list|()
operator|+
literal|"] is null"
argument_list|,
name|actualValue
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"assert that [{}] matches [{}] (field [{}])"
argument_list|,
name|actualValue
argument_list|,
name|expectedValue
argument_list|,
name|getField
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|actualValue
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|safeClass
argument_list|(
name|expectedValue
argument_list|)
argument_list|)
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|actualValue
operator|instanceof
name|Number
operator|&&
name|expectedValue
operator|instanceof
name|Number
condition|)
block|{
comment|//Double 1.0 is equal to Integer 1
name|assertThat
argument_list|(
literal|"field ["
operator|+
name|getField
argument_list|()
operator|+
literal|"] doesn't match the expected value"
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|actualValue
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|expectedValue
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
name|expectedValue
operator|.
name|equals
argument_list|(
name|actualValue
argument_list|)
operator|==
literal|false
condition|)
block|{
name|FailureMessage
name|message
init|=
operator|new
name|FailureMessage
argument_list|(
name|getField
argument_list|()
argument_list|)
decl_stmt|;
name|message
operator|.
name|compare
argument_list|(
name|getField
argument_list|()
argument_list|,
name|actualValue
argument_list|,
name|expectedValue
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
name|message
operator|.
name|message
argument_list|)
throw|;
block|}
block|}
DECL|class|FailureMessage
specifier|private
specifier|static
class|class
name|FailureMessage
block|{
DECL|field|message
specifier|private
specifier|final
name|StringBuilder
name|message
decl_stmt|;
DECL|field|indent
specifier|private
name|int
name|indent
init|=
literal|0
decl_stmt|;
DECL|method|FailureMessage
specifier|private
name|FailureMessage
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
operator|new
name|StringBuilder
argument_list|(
name|field
operator|+
literal|" didn't match the expected value:\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|compareMaps
specifier|private
name|void
name|compareMaps
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|actual
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expected
parameter_list|)
block|{
name|actual
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|actual
argument_list|)
expr_stmt|;
name|expected
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|expected
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedEntry
range|:
name|expected
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|compare
argument_list|(
name|expectedEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|actual
operator|.
name|remove
argument_list|(
name|expectedEntry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|expectedEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|unmatchedEntry
range|:
name|actual
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|field
argument_list|(
name|unmatchedEntry
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"unexpected but found ["
operator|+
name|unmatchedEntry
operator|.
name|getValue
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|compareLists
specifier|private
name|void
name|compareLists
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|actual
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|expected
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|actual
operator|.
name|size
argument_list|()
operator|&&
name|i
operator|<
name|expected
operator|.
name|size
argument_list|()
condition|)
block|{
name|compare
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|actual
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|expected
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|actual
operator|.
name|size
argument_list|()
operator|==
name|expected
operator|.
name|size
argument_list|()
condition|)
block|{
return|return;
block|}
name|indent
argument_list|()
expr_stmt|;
if|if
condition|(
name|actual
operator|.
name|size
argument_list|()
operator|<
name|expected
operator|.
name|size
argument_list|()
condition|)
block|{
name|message
operator|.
name|append
argument_list|(
literal|"expected ["
argument_list|)
operator|.
name|append
argument_list|(
name|expected
operator|.
name|size
argument_list|()
operator|-
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|"] more entries\n"
argument_list|)
expr_stmt|;
return|return;
block|}
name|message
operator|.
name|append
argument_list|(
literal|"received ["
argument_list|)
operator|.
name|append
argument_list|(
name|actual
operator|.
name|size
argument_list|()
operator|-
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|"] more entries than expected\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|compare
specifier|private
name|void
name|compare
parameter_list|(
name|String
name|field
parameter_list|,
annotation|@
name|Nullable
name|Object
name|actual
parameter_list|,
name|Object
name|expected
parameter_list|)
block|{
if|if
condition|(
name|expected
operator|instanceof
name|Map
condition|)
block|{
if|if
condition|(
name|actual
operator|==
literal|null
condition|)
block|{
name|field
argument_list|(
name|field
argument_list|,
literal|"expected map but not found"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
literal|false
operator|==
name|actual
operator|instanceof
name|Map
condition|)
block|{
name|field
argument_list|(
name|field
argument_list|,
literal|"expected map but found ["
operator|+
name|actual
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|expected
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|actualMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|actual
decl_stmt|;
if|if
condition|(
name|expectedMap
operator|.
name|isEmpty
argument_list|()
operator|&&
name|actualMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|field
argument_list|(
name|field
argument_list|,
literal|"same [empty map]"
argument_list|)
expr_stmt|;
return|return;
block|}
name|field
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|indent
operator|+=
literal|1
expr_stmt|;
name|compareMaps
argument_list|(
name|actualMap
argument_list|,
name|expectedMap
argument_list|)
expr_stmt|;
name|indent
operator|-=
literal|1
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|expected
operator|instanceof
name|List
condition|)
block|{
if|if
condition|(
name|actual
operator|==
literal|null
condition|)
block|{
name|field
argument_list|(
name|field
argument_list|,
literal|"expected list but not found"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
literal|false
operator|==
name|actual
operator|instanceof
name|List
condition|)
block|{
name|field
argument_list|(
name|field
argument_list|,
literal|"expected list but found ["
operator|+
name|actual
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Object
argument_list|>
name|expectedList
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|expected
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Object
argument_list|>
name|actualList
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|actual
decl_stmt|;
if|if
condition|(
name|expectedList
operator|.
name|isEmpty
argument_list|()
operator|&&
name|actualList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|field
argument_list|(
name|field
argument_list|,
literal|"same [empty list]"
argument_list|)
expr_stmt|;
return|return;
block|}
name|field
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|indent
operator|+=
literal|1
expr_stmt|;
name|compareLists
argument_list|(
name|actualList
argument_list|,
name|expectedList
argument_list|)
expr_stmt|;
name|indent
operator|-=
literal|1
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|actual
operator|==
literal|null
condition|)
block|{
name|field
argument_list|(
name|field
argument_list|,
literal|"expected ["
operator|+
name|expected
operator|+
literal|"] but not found"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
condition|)
block|{
name|field
argument_list|(
name|field
argument_list|,
literal|"same ["
operator|+
name|expected
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return;
block|}
name|field
argument_list|(
name|field
argument_list|,
literal|"expected ["
operator|+
name|expected
operator|+
literal|"] but was ["
operator|+
name|actual
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
DECL|method|indent
specifier|private
name|void
name|indent
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indent
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|field
specifier|private
name|void
name|field
parameter_list|(
name|Object
name|name
parameter_list|,
name|String
name|info
parameter_list|)
block|{
name|indent
argument_list|()
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%30s: "
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|append
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|message
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

