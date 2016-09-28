begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|IndexOptions
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
name|TermQuery
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
name|BooleanFieldMapper
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
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_class
DECL|class|BooleanFieldTypeTests
specifier|public
class|class
name|BooleanFieldTypeTests
extends|extends
name|FieldTypeTestCase
block|{
annotation|@
name|Override
DECL|method|createDefaultFieldType
specifier|protected
name|MappedFieldType
name|createDefaultFieldType
parameter_list|()
block|{
return|return
operator|new
name|BooleanFieldMapper
operator|.
name|BooleanFieldType
argument_list|()
return|;
block|}
annotation|@
name|Before
DECL|method|setupProperties
specifier|public
name|void
name|setupProperties
parameter_list|()
block|{
name|setDummyNullValue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testValueFormat
specifier|public
name|void
name|testValueFormat
parameter_list|()
block|{
name|MappedFieldType
name|ft
init|=
name|createDefaultFieldType
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|ft
operator|.
name|docValueFormat
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|format
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|ft
operator|.
name|docValueFormat
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|format
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testValueForSearch
specifier|public
name|void
name|testValueForSearch
parameter_list|()
block|{
name|MappedFieldType
name|ft
init|=
name|createDefaultFieldType
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|ft
operator|.
name|valueForSearch
argument_list|(
literal|"T"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|ft
operator|.
name|valueForSearch
argument_list|(
literal|"F"
argument_list|)
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|ft
operator|.
name|valueForSearch
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|ft
operator|.
name|valueForSearch
argument_list|(
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|ft
operator|.
name|valueForSearch
argument_list|(
literal|"G"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermQuery
specifier|public
name|void
name|testTermQuery
parameter_list|()
block|{
name|MappedFieldType
name|ft
init|=
name|createDefaultFieldType
argument_list|()
decl_stmt|;
name|ft
operator|.
name|setName
argument_list|(
literal|"field"
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"T"
argument_list|)
argument_list|)
argument_list|,
name|ft
operator|.
name|termQuery
argument_list|(
literal|"true"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"F"
argument_list|)
argument_list|)
argument_list|,
name|ft
operator|.
name|termQuery
argument_list|(
literal|"false"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|NONE
argument_list|)
expr_stmt|;
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
name|ft
operator|.
name|termQuery
argument_list|(
literal|"true"
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Cannot search on field [field] since it is not indexed."
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
