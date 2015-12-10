begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|IngestDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|ValueSource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
DECL|class|ValueSourceMustacheIT
specifier|public
class|class
name|ValueSourceMustacheIT
extends|extends
name|AbstractMustacheTests
block|{
DECL|method|testValueSourceWithTemplates
specifier|public
name|void
name|testValueSourceWithTemplates
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|model
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|model
operator|.
name|put
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|model
operator|.
name|put
argument_list|(
literal|"field2"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"field3"
argument_list|,
literal|"value3"
argument_list|)
argument_list|)
expr_stmt|;
name|ValueSource
name|valueSource
init|=
name|ValueSource
operator|.
name|wrap
argument_list|(
literal|"{{field1}}/{{field2}}/{{field2.field3}}"
argument_list|,
name|templateService
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|valueSource
argument_list|,
name|instanceOf
argument_list|(
name|ValueSource
operator|.
name|TemplatedValue
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|valueSource
operator|.
name|copyAndResolve
argument_list|(
name|model
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1/{field3=value3}/value3"
argument_list|)
argument_list|)
expr_stmt|;
name|valueSource
operator|=
name|ValueSource
operator|.
name|wrap
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"_value"
argument_list|,
literal|"{{field1}}"
argument_list|)
argument_list|,
name|templateService
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|valueSource
argument_list|,
name|instanceOf
argument_list|(
name|ValueSource
operator|.
name|ListValue
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|valueSource
operator|.
name|copyAndResolve
argument_list|(
name|model
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"_value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"field1"
argument_list|,
literal|"{{field1}}"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"field2"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"field3"
argument_list|,
literal|"{{field2.field3}}"
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"field4"
argument_list|,
literal|"_value"
argument_list|)
expr_stmt|;
name|valueSource
operator|=
name|ValueSource
operator|.
name|wrap
argument_list|(
name|map
argument_list|,
name|templateService
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|valueSource
argument_list|,
name|instanceOf
argument_list|(
name|ValueSource
operator|.
name|MapValue
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|resultMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|valueSource
operator|.
name|copyAndResolve
argument_list|(
name|model
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultMap
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultMap
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|resultMap
operator|.
name|get
argument_list|(
literal|"field2"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|resultMap
operator|.
name|get
argument_list|(
literal|"field2"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"field3"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultMap
operator|.
name|get
argument_list|(
literal|"field4"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"_value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAccessSourceViaTemplate
specifier|public
name|void
name|testAccessSourceViaTemplate
parameter_list|()
block|{
name|IngestDocument
name|ingestDocument
init|=
operator|new
name|IngestDocument
argument_list|(
literal|"marvel"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|hasField
argument_list|(
literal|"marvel"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|ingestDocument
operator|.
name|setFieldValue
argument_list|(
name|templateService
operator|.
name|compile
argument_list|(
literal|"{{_index}}"
argument_list|)
argument_list|,
name|ValueSource
operator|.
name|wrap
argument_list|(
literal|"{{_index}}"
argument_list|,
name|templateService
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
literal|"marvel"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"marvel"
argument_list|)
argument_list|)
expr_stmt|;
name|ingestDocument
operator|.
name|removeField
argument_list|(
name|templateService
operator|.
name|compile
argument_list|(
literal|"{{marvel}}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|hasField
argument_list|(
literal|"index"
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

