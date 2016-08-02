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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|anyOf
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
name|equalToIgnoringCase
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
comment|/**  * Represents an is_false assert section:  *  *   - is_false:  get.fields.bar  *  */
end_comment

begin_class
DECL|class|IsFalseAssertion
specifier|public
class|class
name|IsFalseAssertion
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
name|IsFalseAssertion
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|IsFalseAssertion
specifier|public
name|IsFalseAssertion
parameter_list|(
name|XContentLocation
name|location
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|location
argument_list|,
name|field
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|logger
operator|.
name|trace
argument_list|(
literal|"assert that [{}] doesn't have a true value (field: [{}])"
argument_list|,
name|actualValue
argument_list|,
name|getField
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|actualValue
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
name|actualString
init|=
name|actualValue
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|errorMessage
argument_list|()
argument_list|,
name|actualString
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
literal|""
argument_list|)
argument_list|,
name|equalToIgnoringCase
argument_list|(
name|Boolean
operator|.
name|FALSE
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|errorMessage
specifier|private
name|String
name|errorMessage
parameter_list|()
block|{
return|return
literal|"field ["
operator|+
name|getField
argument_list|()
operator|+
literal|"] has a true value but it shouldn't"
return|;
block|}
block|}
end_class

end_unit

