begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|index
operator|.
name|fielddata
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FloatField
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
name|document
operator|.
name|StringField
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
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|FieldDataType
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|FloatFieldDataTests
specifier|public
class|class
name|FloatFieldDataTests
extends|extends
name|AbstractNumericFieldDataTests
block|{
annotation|@
name|Override
DECL|method|getFieldDataType
specifier|protected
name|FieldDataType
name|getFieldDataType
parameter_list|()
block|{
return|return
operator|new
name|FieldDataType
argument_list|(
literal|"float"
argument_list|)
return|;
block|}
DECL|method|one
specifier|protected
name|String
name|one
parameter_list|()
block|{
return|return
literal|"1.0"
return|;
block|}
DECL|method|two
specifier|protected
name|String
name|two
parameter_list|()
block|{
return|return
literal|"2.0"
return|;
block|}
DECL|method|three
specifier|protected
name|String
name|three
parameter_list|()
block|{
return|return
literal|"3.0"
return|;
block|}
DECL|method|four
specifier|protected
name|String
name|four
parameter_list|()
block|{
return|return
literal|"4.0"
return|;
block|}
DECL|method|add2SingleValuedDocumentsAndDeleteOneOfThem
specifier|protected
name|void
name|add2SingleValuedDocumentsAndDeleteOneOfThem
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|2.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|4.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"_id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fillSingleValueAllSet
specifier|protected
name|void
name|fillSingleValueAllSet
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|2.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|1.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"3"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|3.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fillSingleValueWithMissing
specifier|protected
name|void
name|fillSingleValueWithMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|2.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
comment|//d.add(new StringField("value", one(), Field.Store.NO)); // MISSING....
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"3"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|3.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fillMultiValueAllSet
specifier|protected
name|void
name|fillMultiValueAllSet
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|2.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|4.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|1.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"3"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|3.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fillMultiValueWithMissing
specifier|protected
name|void
name|fillMultiValueWithMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|2.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|4.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
comment|//d.add(new StringField("value", one(), Field.Store.NO)); // MISSING
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"3"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|3.0f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
DECL|method|fillExtendedMvSet
specifier|protected
name|void
name|fillExtendedMvSet
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|2
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|4
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"3"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|3
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"4"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|4
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|5
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|6
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"5"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|6
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|7
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|8
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"6"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"7"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|8
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|9
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
literal|10
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"_id"
argument_list|,
literal|"8"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
operator|-
literal|8
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
operator|-
literal|9
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"value"
argument_list|,
operator|-
literal|10
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

